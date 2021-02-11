// ----------------------- important class --------------------------

package net.torocraft.toroquest.network.message;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.ItemHandlerHelper;
import net.torocraft.toroquest.civilization.CivilizationHandlers;
import net.torocraft.toroquest.civilization.CivilizationUtil;
import net.torocraft.toroquest.civilization.Province;
import net.torocraft.toroquest.civilization.player.PlayerCivilizationCapabilityImpl;
import net.torocraft.toroquest.civilization.quests.util.QuestData;
import net.torocraft.toroquest.config.ToroQuestConfiguration;
import net.torocraft.toroquest.entities.EntityVillageLord;
import net.torocraft.toroquest.inventory.IVillageLordInventory;
//import net.torocraft.toroquest.item.ItemFireSword;
//import net.torocraft.toroquest.item.ItemObsidianSword;
import net.torocraft.toroquest.network.ToroQuestPacketHandler;

public class MessageQuestUpdate implements IMessage {

	public static enum Action {
		ACCEPT, REJECT, COMPLETE, DONATE
	}

	public Action action;
	public int lordEntityId;

	@Override
	public void fromBytes(ByteBuf buf) {
		action = Action.values()[buf.readInt()];
		lordEntityId = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(action.ordinal());
		buf.writeInt(lordEntityId);
	}

	// UI SOUNDS //
	public static class Worker
	{

		private final Action action;

		public Worker(Action action)
		{
			this.action = action;
		}

		void work(MessageQuestUpdate message, EntityPlayer player)
		{
			if ( player == null )
			{
				return;
			}
			
			Province province = CivilizationUtil.getProvinceAt(player.getEntityWorld(), player.chunkCoordX, player.chunkCoordZ);

			if ( province == null )
			{
				player.closeScreen();
				return;
			}
			
			EntityVillageLord lord = (EntityVillageLord) player.world.getEntityByID(message.lordEntityId);
			
			if ( lord == null )
			{
				player.closeScreen();
				return;
			}
			
			IVillageLordInventory inventory = lord.getInventory(player.getUniqueID());

			if ( inventory == null )
			{
				player.closeScreen();
				return;
			}
			
            if ( !player.world.isRemote ) player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.UI_BUTTON_CLICK, SoundCategory.PLAYERS, 1.0F, 1.0F);

			switch (action) // TODO
			{
				case ACCEPT:
				{
					//this.handleStatusUpdate(player,12);
					if ( !player.world.isRemote )
		            {
		            	player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.BLOCK_NOTE_CHIME, SoundCategory.AMBIENT, 1.0F, 1.0F);					
		            }
					processAccept(player, province, inventory);
					break;
				}
				case COMPLETE:
				{
		            //this.spawnParticles(player, EnumParticleTypes.VILLAGER_HAPPY);
					processComplete(player, province, inventory);
		            if ( !player.world.isRemote )
		            {
		            	player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.AMBIENT, 1.0F, 1.0F);					
		            }
					break;
				}
				case REJECT:
				{
		            //this.spawnParticles(player, EnumParticleTypes.VILLAGER_ANGRY);
					processReject(player, province, inventory);
					if ( !player.world.isRemote )
		            {
		            	player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.BLOCK_NOTE_BASS, SoundCategory.AMBIENT, 0.9F, 0.9F);					
		            }					
					 break;
				}
				case DONATE:
				{
		            //this.spawnParticles(player, EnumParticleTypes.VILLAGER_HAPPY);
					processDonate(player, province, inventory);
					if ( !player.world.isRemote )
		            {
		            	//player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.VINDICATION_ILLAGER_AMBIENT, SoundCategory.VOICE, 0.8F, 1.25F);					
		            	player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.AMBIENT, 1.0F, 1.0F);					
		            }
					break;
				}
				default:
				{
					throw new IllegalArgumentException("invalid quest action [" + action + "]");
				}
			}
		}

	    // @SideOnly(Side.CLIENT)
//	    private void spawnParticles(EntityPlayer player, EnumParticleTypes particleType)
//	    {
//	    	Random rand = new Random();
//	    	double x = player.posX;
//	    	double y = player.posY;
//	    	double z = player.posZ;
//	    	for (int i = 0; i < 16; i++)
//	    	{
//	    		player.world.spawnParticle(particleType, x+rand.nextDouble()*2-1, y+rand.nextDouble()+1, z+rand.nextDouble()*2-1, rand.nextDouble()-0.5, +rand.nextDouble()-0.5, +rand.nextDouble()-0.5, 0);
//	    	}
//	    }

		private void processDonate(EntityPlayer player, Province province, IVillageLordInventory inventory)
		{
			ItemStack donation = inventory.getDonationItem();

			if (MessageSetItemReputationAmount.isNoteForLord(province, donation))
			{
				writeReplyNote(inventory, donation, player);
				return;
			}

			if (MessageSetItemReputationAmount.isStolenItemForProvince(province, donation))
			{
				handleReturnStolenItem(player, province, inventory, donation);
				return;
			}
			
			if (MessageSetItemReputationAmount.isTrophy(province, donation))
			{
				handleDonateTrophy(player, province, inventory, donation);
				return;
			}

			DonationReward reward = getRepForDonation(donation);
			
			if (reward != null)
			{
				CivilizationHandlers.adjustPlayerRep(player, province.civilization, reward.rep);
				//if ( player.world.isRemote )
				{
					player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
				}
				inventory.setDonationItem(ItemStack.EMPTY);
				try{player.addItemStackToInventory(inventory.getReturnItems().get(0));} catch ( Exception e ) {}
				inventory.setReturnItems(new ItemStack(reward.item));
			}
		}

//		private <T> List<T> l(T... items)
//		{
//			List<T> l = new ArrayList<T>();
//			for (T item : items)
//			{
//				l.add(item);
//			}
//			return l;
//		}

		// TODO -==-=-=-=-=-=-=--=-=-=--1pofjqwphfvhisuidbcuoiasoiba
		
		private void handleDonateTrophy(EntityPlayer player, Province province, IVillageLordInventory inventory, ItemStack stack)
		{
			if ( inventory.addTrophy(stack.getItem()) )
			{
				inventory.setDonationItem(ItemStack.EMPTY);
				CivilizationHandlers.adjustPlayerRep(player, province.civilization, ToroQuestConfiguration.donateTrophyRepGain);
			}
			return;
		}
		
		private void handleReturnStolenItem(EntityPlayer player, Province province, IVillageLordInventory inventory, ItemStack stack)
		{
			inventory.setDonationItem(ItemStack.EMPTY);
			int num = 25; // player.world.rand.nextInt(8) + 16;
			ItemStack emeralds = new ItemStack(Items.EMERALD, num);
			List<ItemStack> list = new ArrayList<ItemStack>(1);
			list.add(emeralds);
			try{player.addItemStackToInventory(inventory.getReturnItems().get(0));} catch ( Exception e ) {}
			inventory.setReturnItems(list);
			CivilizationHandlers.adjustPlayerRep( player, province.civilization, ToroQuestConfiguration.donateArtifactRepGain );
		}

		private void writeReplyNote(IVillageLordInventory inventory, ItemStack donation, EntityPlayer player)
		{
			String sToProvinceId = donation.getTagCompound().getString("toProvince");
			String sQuestId = donation.getTagCompound().getString("questId");

			if (isEmpty(sToProvinceId) || isEmpty(sQuestId))
			{
				return;
			}

			inventory.setDonationItem(ItemStack.EMPTY);
			donation.setStackDisplayName("Reply Note");
			donation.getTagCompound().setBoolean("reply", true);

			List<ItemStack> list = new ArrayList<ItemStack>(1);
			list.add(donation);
			try{player.addItemStackToInventory(inventory.getReturnItems().get(0));} catch ( Exception e ) {}
			inventory.setReturnItems(list);
		}

		protected void processAccept(EntityPlayer player, Province province, IVillageLordInventory inventory) {

			if ( player.world.isRemote )
			{
				return;
			}
			
			List<ItemStack> inputItems = inventory.getGivenItems(); // left items
			List<ItemStack> outputItems = PlayerCivilizationCapabilityImpl.get(player).acceptQuest(inputItems); // after items left
			List<ItemStack> returnItems = inventory.getReturnItems(); // right item

			for ( ItemStack item : returnItems )
			{
				ItemHandlerHelper.giveItemToPlayer(player, item);
			}
			
			if ( outputItems == null && inputItems != null)
			{
				for ( ItemStack item : inputItems )
				{
					ItemHandlerHelper.giveItemToPlayer(player, item);
				}
				//return; =-=-= ???
			}
			
			if ( outputItems != null )
			{
				// this works v
//				int outputItemsSize = 0;
//				
//				for ( ItemStack item : outputItems )
//				{
//					if ( !(item.getItem().equals(Items.AIR)) )
//					{
//						outputItemsSize++;
//					}
//				}
//				if ( outputItemsSize > 1 || returnItems != null )
//				{
//					for ( ItemStack item : outputItems )
//					{
//						ItemHandlerHelper.giveItemToPlayer(player, item);
//					}
//				}
//				else
//				{
//					inventory.setReturnItems(outputItems);
//				}
				
				int outputItemsSize = outputItems.size();
				int i = 0;
				
				for ( ItemStack item : outputItems )
				{
					if ( i == outputItemsSize-1 )
					{
						List<ItemStack> stack = new ArrayList<ItemStack>();
						stack.add(outputItems.get(i));
						inventory.setReturnItems(stack);
					}
					else
					{
						ItemHandlerHelper.giveItemToPlayer(player, item);
					}
					i++;
				}
			}
			
				//return;
			
			//inventory.setReturnItems(outputItems);
			
			
			
//			for ( ItemStack item : inputItems )
//			{
//				ItemHandlerHelper.giveItemToPlayer(player, item);
//			}
					
			QuestData currentQuest = PlayerCivilizationCapabilityImpl.get(player).getCurrentQuestFor(province);
			ToroQuestPacketHandler.INSTANCE.sendTo(new MessageSetQuestInfo(province, currentQuest, null), (EntityPlayerMP) player);
		}

		protected void processReject(EntityPlayer player, Province province, IVillageLordInventory inventory)
		{	
			List<ItemStack> inputItems = inventory.getGivenItems(); // left items
			List<ItemStack> outputItems = PlayerCivilizationCapabilityImpl.get(player).rejectQuest(inputItems); // after items left
			List<ItemStack> returnItems = inventory.getReturnItems(); // right item

			// List<ItemStack> other = PlayerCivilizationCapabilityImpl.get(player).rejectQuest(returnItems); // after item right


			if ( outputItems != null )
			{
				for ( ItemStack item : outputItems )
				{
					ItemHandlerHelper.giveItemToPlayer(player, item);
				}
			}
			else
			{
				for ( ItemStack item : inputItems )
				{
					ItemHandlerHelper.giveItemToPlayer(player, item);
				}
				inventory.setGivenItems(returnItems);
				return;  
			}
			
//			else
//			{
//				for ( ItemStack item : outputItems )
//				{
//					ItemHandlerHelper.giveItemToPlayer(player, item);
//				}
//			}
			
//			boolean found = false;
//			int i = 0;
//			if ( returnItems != null )
//			{		
//				for ( ItemStack item : returnItems )
//				{
//					if ( !found && other != null && other.get(i).getCount() == 0 )
//					{
//						item.setCount(item.getCount() - 1);
//						found = true;
//					}
//					else
//					{
//						ItemHandlerHelper.giveItemToPlayer(player, item);
//					}
//					i++;
//				}
//			}
//			i = 0;
//			
//			if ( inputItems != null )
//			{
//				for ( ItemStack item : inputItems )
//				{
//					if ( !found && outputItems != null && outputItems.get(i).getCount() == 0 ) //&& outputItems.get(j).getCount() == 1 )
//					{
//						item.setCount(item.getCount() - 1);
//						found = true;
//					}
//					else
//					{
//						ItemHandlerHelper.giveItemToPlayer(player, item);
//					}
//					i++;
//				}
//			}
			
			QuestData nextQuest = PlayerCivilizationCapabilityImpl.get(player).getNextQuestFor(province);
			ToroQuestPacketHandler.INSTANCE.sendTo(new MessageSetQuestInfo(province, null, nextQuest), (EntityPlayerMP) player);
		
		}

		protected void processComplete(EntityPlayer player, Province province, IVillageLordInventory inventory) {

			if ( player.world.isRemote )
			{
				return;
			}
			
			List<ItemStack> inputItems = inventory.getGivenItems(); // left items
			List<ItemStack> outputItems = PlayerCivilizationCapabilityImpl.get(player).completeQuest(inputItems); // after items
			//List<ItemStack> returnItems = inventory.getReturnItems(); // right items
			

			//List<ItemStack> other = PlayerCivilizationCapabilityImpl.get(player).rejectQuest(returnItems); // after items right

			if (outputItems == null)
			{
				for ( ItemStack itemstack : inputItems )
				{
					ItemHandlerHelper.giveItemToPlayer(player, itemstack);
				}
				//inventory.setGivenItems(inputItems);
				return;
			}

			//if (outputItems != null)
			
			boolean flag = false;
			for ( ItemStack itemstack : outputItems )
			{
				if ( itemstack.getItem() == Items.EMERALD && !flag )
				{
					flag = true;
					inventory.setReturnItems(itemstack);
				}
				else
				{
					ItemHandlerHelper.giveItemToPlayer(player, itemstack);
				}
			}
			
			//inventory.setReturnItems(outputItems);
//								for ( ItemStack item : outputItems )
//								{
//									ItemHandlerHelper.giveItemToPlayer(player, item);
//								}
			
//			for ( ItemStack item : returnItems )
//			{
//				ItemHandlerHelper.giveItemToPlayer(player, item);
//			}
			
			//if (outputItems == null) return;

			QuestData nextQuest = PlayerCivilizationCapabilityImpl.get(player).getNextQuestFor(province);
			ToroQuestPacketHandler.INSTANCE.sendTo(new MessageSetQuestInfo(province, null, nextQuest), (EntityPlayerMP) player);
		}
	}

	public static class Handler implements IMessageHandler<MessageQuestUpdate, IMessage> {

		@Override
		public IMessage onMessage(final MessageQuestUpdate message, MessageContext ctx) {
			if (ctx.side != Side.SERVER) {
				return null;
			}

			final EntityPlayerMP player = ctx.getServerHandler().player;

			if (player == null) {
				return null;
			}

			final WorldServer worldServer = player.getServerWorld();

			worldServer.addScheduledTask(new Runnable() {
				@Override
				public void run() {
					new Worker(message.action).work(message, player);
				}
			});

			return null;
		}
	}

	private static boolean isSet(String s)
	{
		return s != null && s.trim().length() > 0;
	}

	private static boolean isEmpty(String s)
	{
		return !isSet(s);
	}

	public static class DonationReward
	{
		public DonationReward()
		{

		}

		public DonationReward(int rep, Item item)
		{
			this.rep = rep;
			this.item = item;
		}

		public Item item;
		public int rep;
	}

	public static DonationReward getRepForDonation(ItemStack item)
	{

		if ( item.isEmpty() )
		{
			return null;
		}
		
		if ( item.getItem() == Items.EMERALD )
		{
			return new DonationReward(ToroQuestConfiguration.donateEmeraldRepGain * item.getCount(), null);
		}
		
		if ( item.getItem() == Item.getByNameOrId( "toroquest:bandit_helmet" ) )
		{
			return new DonationReward(ToroQuestConfiguration.donateBanditMaskRepGain * item.getCount(), null);
		}

		if ( item.getItem() instanceof ItemBlock )
		{
			Block block = ((ItemBlock) item.getItem()).getBlock();
			if (Blocks.EMERALD_BLOCK == block)
			{
				return new DonationReward(ToroQuestConfiguration.donateEmeraldRepGain * 9 * item.getCount(), null);
			}
		}
		
		return null;
		
		/*
		if (item.getItem() instanceof ItemTool)
		{
			String material = ((ItemTool) item.getItem()).getToolMaterialName();
			switch (material) {
			case "DIAMOND":
				return new DonationReward(4, null);
			case "GOLD":
				return new DonationReward(2, null);
			case "IRON":
				return new DonationReward(1, null);
			default:
				return null;
			}
		}

		if (item.getItem() instanceof ItemSword)
		{
			String material = ((ItemSword) item.getItem()).getToolMaterialName();
			
			if ("DIAMOND".equals(material))
			{
				return new DonationReward(4, null);
			}
			else if ("GOLD".equals(material))
			{
				return new DonationReward(2, null);
			}
			else if ("IRON".equals(material))
			{
				return new DonationReward(1, null);
			}
			else
			{
				return null;
			}
		}

		if (item.getItem() == Items.DIAMOND)
		{
			return new DonationReward(4 * item.getCount(), null);
		}
		else if (item.getItem() == Items.EMERALD)
		{
			return new DonationReward(2 * item.getCount(), null);
		}
		else if (item.getItem() == Items.GOLD_INGOT)
		{
			return new DonationReward(2 * item.getCount(), null);
		}
		else if (item.getItem() == Items.IRON_INGOT)
		{
			return new DonationReward(1 * item.getCount(), null);
		}

		if (item.getItem() instanceof ItemBlock)
		{
			Block block = ((ItemBlock) item.getItem()).getBlock();
			if (Blocks.DIAMOND_BLOCK == block)
			{
				return new DonationReward(36 * item.getCount(), null);
			}

			else if (Blocks.EMERALD_BLOCK == block)
			{
				return new DonationReward(18 * item.getCount(), null);
			}
			
			else if (Blocks.GOLD_BLOCK == block)
			{
				return new DonationReward(18 * item.getCount(), null);
			}
			
			else if (Blocks.IRON_BLOCK == block)
			{
				return new DonationReward(9 * item.getCount(), null);
			}
			
			else if (Blocks.MELON_BLOCK == block)
			{
				return new DonationReward(1 * MathHelper.floor(item.getCount() / 4), null);
			}
			
			else if (Blocks.PUMPKIN == block)
			{
				return new DonationReward(1 * MathHelper.floor(item.getCount() / 4), null);
			}
		}

		DonationReward reward = new DonationReward();

		if (is(item, Items.APPLE, Items.POTATO))
		{
			reward.rep = 1 * MathHelper.floor(item.getCount() / 4);
		}
		else if (is(item, Items.CARROT, Items.BEETROOT, Items.MELON))
		{
			reward.rep = 1 * MathHelper.floor(item.getCount() / 16);
		}
		else if (is(item, Items.WHEAT))
		{
			reward.rep = 1 * MathHelper.floor(item.getCount() / 8);
		}
		else if (is(item, Items.BREAD,  Items.BAKED_POTATO, Items.CHICKEN, Items.COOKED_CHICKEN, Items.PORKCHOP, Items.COOKED_PORKCHOP, Items.MUTTON, Items.COOKED_MUTTON, Items.RABBIT, Items.COOKED_RABBIT, Items.FISH, Items.COOKED_FISH, Items.BEEF, Items.COOKED_BEEF, Items.PUMPKIN_PIE))
		{
			reward.rep = 3 * MathHelper.floor(item.getCount() / 8);
		}
		else if (is(item, Items.MILK_BUCKET))
		{
			reward.rep = 6 * item.getCount();
		}
		else if ( item.getItem() == (new ItemStack(Blocks.MELON_BLOCK, 1 )).getItem() || item.getItem() == (new ItemStack(Blocks.PUMPKIN, 1 )).getItem() )
		{
			reward.rep = 3 * MathHelper.floor(item.getCount() / 8);
		}
		else if ( item.getItem() instanceof ItemFood )
		{
			reward.rep = 1 * MathHelper.floor(item.getCount() / 4);
		}
		
		if (reward.rep == 0)
		{
			return null;
		}
		return reward;
	*/
	}

	private static boolean is(ItemStack stack, Item... items)
	{
		for (Item item : items)
		{
			if (stack.getItem() == item)
			{
				return true;
			}
		}
		return false;
	}

}
