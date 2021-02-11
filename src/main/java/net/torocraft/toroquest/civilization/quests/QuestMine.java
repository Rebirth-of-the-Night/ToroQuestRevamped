package net.torocraft.toroquest.civilization.quests;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockGravel;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockOre;
import net.minecraft.block.BlockStone;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.torocraft.toroquest.civilization.CivilizationHandlers;
import net.torocraft.toroquest.civilization.Province;
import net.torocraft.toroquest.civilization.player.PlayerCivilizationCapabilityImpl;
import net.torocraft.toroquest.civilization.quests.util.Quest;
import net.torocraft.toroquest.civilization.quests.util.QuestData;
import net.torocraft.toroquest.civilization.quests.util.Quests;

public class QuestMine extends QuestBase implements Quest
{
	public static QuestMine INSTANCE;
	
	private static final Block[] BLOCK_TYPES =
	{
		Blocks.DIRT,
		Blocks.STONE,
		Blocks.GRAVEL,
		Blocks.COAL_ORE,
		Blocks.REDSTONE_ORE,
		Blocks.LOG
//		Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.ANDESITE).getBlock(),
//		Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.DIORITE).getBlock(),
//		Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.GRANITE).getBlock()
	};

	public static int ID;

	public static void init(int id)
	{
		INSTANCE = new QuestMine();
		Quests.registerQuest(id, INSTANCE);
		MinecraftForge.EVENT_BUS.register(INSTANCE);
		ID = id;
	}

	@SubscribeEvent
	public void onMine(HarvestDropsEvent event)
	{
		EntityPlayer player = event.getHarvester();
		
		if ( player == null )
		{
			return;
		}

		ItemStack tool = player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);

		if ( tool == null )
		{
			return;
		}
		
		if (!tool.hasTagCompound())
		{
			return;
		}

		if ( !tool.getTagCompound().hasKey("mine_quest") )
		{
			return;
		}
		String questId = tool.getTagCompound().getString("mine_quest");
		if ( !tool.getTagCompound().hasKey("provinceID") )
		{
			return;
		}
		String provinceId = tool.getTagCompound().getString("provinceID");
		if ( !tool.getTagCompound().hasKey("provinceName") )
		{
			return;
		}
		String provinceName = tool.getTagCompound().getString("provinceName");

		if ( questId == null || provinceId == null || provinceName == null ) // || !provinceId.equals(inProvince.id.toString()))
		{
			return;
		}

		QuestData data = getQuestById(player, questId);

		if ( data == null )
		{
			return;
		}
		
		DataWrapper q = new DataWrapper().setData(data);
		
		Block eventBlock = event.getState().getBlock();
		Block allowedBlock = BLOCK_TYPES[q.getBlockType()];
		
		if ( allowedBlock == null || eventBlock == null )
		{
			return;
		}
		
		for ( ItemStack itemstack: event.getDrops() )
		{
			String blockName = "blocks";
					
			if ( allowedBlock == Blocks.STONE )
			{
				if ( eventBlock instanceof BlockStone )
				{
					blockName = "Stone";
					itemstack.setCount(1);
					// itemstack = new ItemStack(Blocks.STONE, 1);
				}
				else return;
			}
			else if ( allowedBlock == Blocks.LOG )
			{
				if ( eventBlock instanceof BlockLog )
				{
					blockName = "Logs";
					itemstack.setCount(1);
					// itemstack = new ItemStack(Blocks.LOG, 1);
				}
				else return;
			}
			else if ( allowedBlock == Blocks.REDSTONE_ORE )
			{
				if ( eventBlock == Blocks.REDSTONE_ORE || itemstack.getItem() == Items.REDSTONE )
				{
					blockName = "Redstone";
					itemstack = new ItemStack(Items.REDSTONE, rand.nextInt(2)+4);
				}
				else return;
			}
			else if ( allowedBlock == Blocks.COAL_ORE )
			{
				if ( eventBlock == Blocks.COAL_ORE || itemstack.getItem() == Items.COAL )
				{
					blockName = "Coal";
					itemstack = new ItemStack(Items.COAL, 1);
				}
				else return;
			}
			else if ( allowedBlock == Blocks.GRAVEL )
			{
				if ( eventBlock instanceof BlockGravel || itemstack.getItem() == Items.FLINT )
				{
					blockName = "Flint";
					itemstack = new ItemStack(Items.FLINT, 1);
				}
				else return;
			}
			else if ( allowedBlock == Blocks.DIRT )
			{
				if ( eventBlock instanceof BlockDirt )
				{
					blockName = "Dirt";
					itemstack.setCount(1);
				}
				else return;
			}
			else
			{
				return;
			}
			
			event.setDropChance(0.0F);
			
			itemstack.setTagInfo("mine_quest", new NBTTagString(questId));
			itemstack.setTagInfo("provinceID", new NBTTagString(provinceId));
			itemstack.setTagInfo("provinceName", new NBTTagString(provinceName));
			itemstack.setStackDisplayName(blockName + " for " + provinceName);
			
			if ( player.addItemStackToInventory(itemstack) )
			{
				player.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);
			}
			else
			{
				EntityItem item = new EntityItem(event.getWorld(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), itemstack.copy() );
		    	item.setNoPickupDelay();
		    	event.getWorld().spawnEntity(item);
		    }
			
			q.setCurrentAmount( q.getCurrentAmount() + 1 );
			player.sendStatusMessage( new TextComponentString( q.getCurrentAmount() + "/" + q.getTargetAmount() + " " + blockName ), true);
			
	    	return;
		}
	}






	/*
	 
	 
	 ItemStack itemstack;
		
		String blockName = "blocks";
				
		if ( allowedBlock == Blocks.STONE )
		{
			if ( eventBlock instanceof BlockStone )
			{
				blockName = "Stone";
				itemstack = new ItemStack(Blocks.STONE, 1);
			}
			else return;
		}
		else if ( allowedBlock == Blocks.LOG )
		{
			if ( eventBlock instanceof BlockLog )
			{
				blockName = "Logs";
				itemstack = new ItemStack(Blocks.LOG, 1);
			}
			else return;
		}
		else if ( allowedBlock == Blocks.REDSTONE_ORE )
		{
			blockName = "Redstone";
			itemstack = new ItemStack(Items.REDSTONE, rand.nextInt(2)+4);
		}
		else if ( allowedBlock == Blocks.COAL_ORE )
		{
			blockName = "Coal";
			itemstack = new ItemStack(Items.COAL, 1);
		}
		else if ( allowedBlock == Blocks.GRAVEL )
		{
			blockName = "Flint";
			itemstack = new ItemStack(Items.FLINT, 1);
		}
		else if ( allowedBlock == Blocks.DIRT )
		{
			if ( eventBlock instanceof BlockDirt )
			{
				blockName = "Dirt";
				itemstack = new ItemStack(Blocks.DIRT, 1);
			}
			else return;
		}
		else
		{
			return;
		}
		
		event.setDropChance(0.0F);

		itemstack.setTagInfo("mine_quest", new NBTTagString(questId));
		itemstack.setTagInfo("province", new NBTTagString(provinceId));
		itemstack.setTagInfo("provinceName", new NBTTagString(provinceName));
		itemstack.setStackDisplayName(blockName + " for " + provinceName);
    	EntityItem item = new EntityItem(event.getWorld(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), itemstack.copy() );
    	item.setNoPickupDelay();
    	event.getWorld().spawnEntity(item);
	 
	 
	 
	 
	 */
	
//	@SubscribeEvent
//	public void onPickup(ItemPickupEvent event)
//	{
//		if ( event.player == null )
//		{
//			return;
//		}
//
//		EntityPlayer player = event.player;
//		Province inProvince = loadProvince(event.player.world, player.getPosition());
//
//		if (inProvince == null || inProvince.civilization == null)
//		{
//			return;
//		}
//
//		QuestData data = getQuestById(player, inProvince.id);
//		System.out.println( "000mine1" );
//		if (data == null)
//		{
//			return;
//		}
//		// event.getStack().isEmpty()
//		System.out.println( "11111mine11111" );
//        if ( !isForThisQuest(data, event.getStack()) )
//        {
//        	return;
//        }
//        System.out.println( "22222211mine11111" );
//
//		// QUEST COMPLETE =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
//		
//		int requiredAmount = getTargetAmount(data);
//		int pickupAmount = event.getStack().getCount();
//		int inventoryAmount = 0;
//		int afterAmount = 0;
//		
//	    List<NonNullList<ItemStack>> allInventories = Arrays.<NonNullList<ItemStack>>asList(player.inventory.mainInventory, player.inventory.offHandInventory);
//		
//		for (List<ItemStack> inv : allInventories)
//        {
//			for ( ItemStack itemstack : inv )
//			{
//	            if (!itemstack.isEmpty() && isForThisQuest(data, itemstack))
//	            {
//	            	inventoryAmount += itemstack.getCount();
//	            }
//			}
//        }
//		
//    	afterAmount = inventoryAmount + pickupAmount;
//		
//		if ( afterAmount < 1 )
//		{
//			return;
//		}
//				
//		if ( inventoryAmount >= requiredAmount )
//		{
//			return;
//		}
//		
//		player.sendStatusMessage( new TextComponentString(MathHelper.clamp(afterAmount, 0, requiredAmount)+"/"+requiredAmount), true);
//		
//		if ( afterAmount >= requiredAmount )
//		{
//			event.getStack().setCount(requiredAmount);
//			this.chatCompletedQuest(data);
//		}
//	}
	
	@Override
	public List<ItemStack> complete(QuestData data, List<ItemStack> in)
	{
		
		if ( in == null || data == null )
		{
			return null;
		}
		
		List<ItemStack> givenItems = copyItems(in);
		DataWrapper q = new DataWrapper().setData(data);
		int requiredLeft = q.getTargetAmount();
		boolean toolIncluded = false;

		for (ItemStack item : givenItems)
		{
			if (isForThisQuest(q.data, item))
			{
				if (item.getItem() instanceof ItemTool)
				{
					toolIncluded = true;
					item.setCount(0);
				}
				else
				{
					requiredLeft -= item.getCount();
					item.setCount(0);
				}
			}
		}

		if ( requiredLeft > 0 )
		{
			if ( data.getChatStack().equals("") )
			{
				Block block = BLOCK_TYPES[q.getBlockType()];
				
				if ( block == Blocks.STONE || block == Blocks.LOG )
				{
					data.setChatStack( "My builders need " + requiredLeft + " more of the resource requested.");
				}
				else if ( block == Blocks.REDSTONE_ORE )
				{
					data.setChatStack( "My engineers need " + requiredLeft + " more of the resource requested.");
				}
				else if ( block == Blocks.COAL_ORE )
				{
					data.setChatStack( "My blacksmiths need " + requiredLeft + " more of the resource requested.");
				}
				else if ( block == Blocks.GRAVEL )
				{
					data.setChatStack( "My guards need " + requiredLeft + " more of the resource requested.");
				}
				else if ( block == Blocks.DIRT )
				{
					data.setChatStack( "My farmers need " + requiredLeft + " more of the resource requested.");
				}
				
			}
			this.setData(data);
			return null;
		}

		int emeraldRemainingCount = 5;

		if (!toolIncluded)
		{
			for (ItemStack item : givenItems)
			{
				if (!item.isEmpty() && item.getItem() == Items.EMERALD)
				{
					int decBy = Math.min(item.getCount(), emeraldRemainingCount);
					emeraldRemainingCount -= decBy;
					item.shrink(decBy);
				}
			}
		}

		if ( !toolIncluded && emeraldRemainingCount > 0 )
		{
			data.setChatStack( "Could you also return the tool I gave you? Or 5 emeralds to pay for it." );
			this.setData(data);
			return null;
		}

		givenItems = removeEmptyItemStacks(givenItems);
		addRewardItems(data, givenItems);
		
		CivilizationHandlers.adjustPlayerRep(data.getPlayer(), data.getCiv(), getRewardRep(data));

		// data.setChatStack( "You have my gratitude, " + data.getPlayer().getName() + "." );
		data.setCompleted(true);
		this.setData(data);
		return givenItems;
	}

	protected boolean isForThisQuest(QuestData data, ItemStack item)
	{
		if (!item.hasTagCompound() || item.isEmpty())
		{
			return false;
		}
		String wasMinedForQuestId = item.getTagCompound().getString("mine_quest");
		return data.getQuestId().toString().equals(wasMinedForQuestId);
	}

	@Override
	public List<ItemStack> reject(QuestData data, List<ItemStack> in)
	{
		if (in == null)
		{
			data.setChatStack( "Return the tool I gave you, or 5 emeralds to pay for it." );
			this.setData(data);
			data.getPlayer().closeScreen();
			return null;
		}

		List<ItemStack> givenItems = copyItems(in);

		boolean toolIncluded = false;
		int emeraldRemainingCount = 5;

		for (ItemStack item : givenItems)
		{
			if (isForThisQuest(data, item))
			{
				if (item.getItem() instanceof ItemTool)
				{
					toolIncluded = true;
					item.setCount(0);
				}
			}
		}

		if ( !toolIncluded )
		{
			for (ItemStack item : givenItems)
			{
				if (!item.isEmpty() && item.getItem() == Items.EMERALD && item.getCount() >= 5)
				{
					int decBy = Math.min(item.getCount(), emeraldRemainingCount);
					emeraldRemainingCount -= decBy;
					item.shrink(decBy);
				}
			}
		}

		if ( !toolIncluded && emeraldRemainingCount > 0 )
		{
			data.setChatStack( "Return the tool I gave you, or 5 emeralds to pay for it." );
			this.setData(data);
			data.getPlayer().closeScreen();
			return null;
		}
		
		data.setChatStack( "You have brought me back nothing but dissapointment!" );
		this.setData(data);
		data.getPlayer().closeScreen();
		return givenItems;
	}

	@Override
	public List<ItemStack> accept(QuestData data, List<ItemStack> in)
	{
		try
		{
			DataWrapper q = new DataWrapper().setData(data);
			Block type = BLOCK_TYPES[q.getBlockType()];
			
			Province province = getQuestProvince(q.data);
			
			if ( type == null || province == null )
			{
				return null;
			}
	
			ItemStack tool;
	
			if ( type == Blocks.GRAVEL )
			{
				tool = new ItemStack(Items.IRON_SHOVEL);
				{
					data.setChatStack( "My guards are firing through their arrows faster than a skeleton archer. Gather some flint to use as arrowheads and I will reward you." );
				}
			}
			else if ( type == Blocks.DIRT )
			{
				tool = new ItemStack(Items.IRON_SHOVEL);
				{
					data.setChatStack( "My farmers need fertile soil for their crops. Gather some fresh dirt and return the supplies to me." );
				}
			}
			else if ( type == Blocks.LOG )
			{
				tool = new ItemStack(Items.IRON_AXE);
				{
					data.setChatStack( "My builders need wood to repair several houses after the last storm. Take to the forest and bring back the supplies." );
				}
			}
			else
			{
				tool = new ItemStack(Items.IRON_PICKAXE);
				
				if ( type == Blocks.REDSTONE_ORE )
				{
					data.setChatStack( "My engineers need redstone for their latest invention. If you could do them a favor and mine some, I will certainly reward you." );
				}
				else if ( type == Blocks.COAL_ORE )
				{
					data.setChatStack( "The blacksmiths are low on coal for their furnaces. I'll need you to mine some and return the supplies." );
				}
				else if ( type == Blocks.STONE )
				{
					data.setChatStack( "My builders need cobblestone to repair several houses after the last storm. Take to the mines and bring back the supplies." );
				}
			}
	
			tool.setStackDisplayName(tool.getDisplayName() + " of " + province.name.toString() );
			tool.addEnchantment(Enchantment.getEnchantmentByLocation("minecraft:unbreaking"), 1);
			tool.setTagInfo("mine_quest", new NBTTagString(q.data.getQuestId().toString()));
			tool.setTagInfo("provinceID", new NBTTagString(province.id.toString()));
			tool.setTagInfo("provinceName", new NBTTagString(province.name.toString()));
	
			in.add(tool);
			this.setData(data);
			return in;
		}
		catch ( Exception e )
		{
			return null;
		}
	}

	@Override
	public String getTitle(QuestData data)
	{
		if ( data == null )
		{
			return "";
		}
		DataWrapper q = new DataWrapper().setData(data);
		int type = q.getBlockType();

		if ( type == 0 || type == 2 )
		{
			return "quests.dig.title";
		}
		else if ( type == 5 )
		{
			return "quests.chop.title";
		}
		else
		{
			return "quests.mine.title";
		}
	}

	@Override
	public String getDescription(QuestData data)
	{
		if (data == null)
		{
			return "";
		}
		
		// BLOCK_TYPES[getBlockType(data)].getLocalizedName();
		DataWrapper q = new DataWrapper().setData(data);
		Block block = BLOCK_TYPES[q.getBlockType()];
		String resource = "";
		
		if ( block == Blocks.STONE )
		{
			resource = "Stone";
		}
		else if ( block == Blocks.REDSTONE_ORE )
		{
			resource = "Redstone";
		}
		else if ( block == Blocks.COAL_ORE )
		{
			Block m = new BlockOre();
			resource = "Coal";
		}
		else if ( block == Blocks.GRAVEL )
		{
			resource = "Flint";
		}
		else if ( block == Blocks.DIRT )
		{
			resource = "Dirt";
		}
		else
		{
			resource = "Logs";
		}
		StringBuilder s = new StringBuilder();
		int type = q.getBlockType();
		if ( type == 0 || type == 2 )
		{
			s.append("quests.dig.description");
		}
		else if ( type == 5 )
		{
			s.append("quests.chop.description");
		}
		else
		{
			s.append("quests.mine.description");
		}
		s.append("|").append(q.getTargetAmount());
		s.append("|").append( "§l" + resource + "§r" );
		s.append("|").append( "\n\n" );
		s.append("|").append( listItems(getRewardItems(q.data)) + ",\n" );
		s.append("|").append(getRewardRep(q.data));
		return s.toString();
	}

	@Override
	public QuestData generateQuestFor(EntityPlayer player, Province questProvince)
	{
		Random rand = new Random();
		
		DataWrapper q = new DataWrapper();

		q.data.setCiv(questProvince.civilization);
		q.data.setPlayer(player);
		q.data.setProvinceId(questProvince.id);
		q.data.setQuestId(UUID.randomUUID());
		q.data.setQuestType(ID);
		q.data.setCompleted(false);
		
//		Blocks.DIRT,
//		Blocks.STONE,
//		Blocks.GRAVEL,
//		Blocks.COAL_ORE,
//		Blocks.REDSTONE_ORE
		
		int blockType = rand.nextInt(6);
		q.setBlockType(blockType);
		
		double roll = 0;
		int em = 0;
		
		switch ( q.getBlockType() )
		{
			case 0: // DIRT
			{
				roll = (rand.nextInt(4)+3)*32;
				em = (int)Math.round(roll/16)+2;
				break;
			}
			case 1: // STONE
			{
				roll = (rand.nextInt(4)+3)*32;
				em = (int)Math.round(roll/16)+2;
				break;
			}
			case 2: // GRAVEL
			{
				roll = (rand.nextInt(3)+2)*8;
				em = (int)Math.round(roll/4)+2;
				break;
			}
			case 3: // COAL
			{
				roll = (rand.nextInt(3)+2)*8;
				em = (int)Math.round(roll/4)+3;
				break;
			}
			case 4: // REDSTONE
			{
				roll = (rand.nextInt(3)+2)*8;
				em = (int)Math.round(roll/4)+3;
				break;
			}
			case 5: // LOG
			{
				roll = (rand.nextInt(5)+2)*32;
				em = (int)Math.round(roll/8)+3;
				break;
			}
		}
		q.setRewardRep(em*2);
		if ( PlayerCivilizationCapabilityImpl.get(player).getReputation(questProvince.civilization) >= 3000 )
		{
			em *= 2;
		}

		q.setCurrentAmount(0);
		q.setTargetAmount((int)roll);

		List<ItemStack> rewards = new ArrayList<ItemStack>(1);
		ItemStack emeralds = new ItemStack(Items.EMERALD, em);
		rewards.add(emeralds);
		setRewardItems(q.data, rewards);
		this.setData(q.data);
		return q.data;
	}
	
	
	public static class DataWrapper
	{
		private QuestData data = new QuestData();

		public int getBlockType()
		{
			return coalesce(i(data.getiData().get("block_type")), 0);
		}

		public int coalesce(Integer integer, int i)
		{
			if (integer == null)
			{
				return i;
			}
			return integer;
		}

		public void setBlockType( int blockType )
		{
			data.getiData().put("block_type", blockType);
		}
		
		public QuestData getData()
		{
			return data;
		}

		public DataWrapper setData(QuestData data)
		{
			this.data = data;
			return this;
		}

		public Integer getTargetAmount()
		{
			return i(data.getiData().get("target"));
		}

		public void setTargetAmount(Integer targetAmount)
		{
			data.getiData().put("target", targetAmount);
		}

		public Integer getCurrentAmount()
		{
			return i(data.getiData().get("amount"));
		}

		public void setCurrentAmount(Integer currentAmount)
		{
			data.getiData().put("amount", currentAmount);
		}

		public Integer getRewardRep()
		{
			return i(data.getiData().get("rep"));
		}

		public void setRewardRep(Integer rewardRep)
		{
			data.getiData().put("rep", rewardRep);
		}

		private Integer i(Object o)
		{
			try
			{
				return (Integer) o;
			}
			catch (Exception e)
			{
				return 0;
			}
		}
	}
	
	
	
	
	

//	public Integer getCurrentAmount( QuestData data )
//	{
//		return data.getiData().get("current_amount");
//	}
//
//	public void setCurrentAmount(QuestData data, Integer currentAmount)
//	{
//		data.getiData().put("current_amount", currentAmount);
//	}
//	
//	private int getTargetAmount(QuestData data)
//	{
//		return data.getiData().get("target_amount");
//	}
//
//	private void setTargetAmount(QuestData data, int amount)
//	{
//		data.getiData().put("target_amount", amount);
//	}


}
