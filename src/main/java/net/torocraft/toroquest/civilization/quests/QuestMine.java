package net.torocraft.toroquest.civilization.quests;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockStone;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.server.command.TextComponentHelper;
import net.torocraft.toroquest.civilization.CivilizationHandlers;
import net.torocraft.toroquest.civilization.CivilizationType;
import net.torocraft.toroquest.civilization.Province;
import net.torocraft.toroquest.civilization.player.PlayerCivilizationCapabilityImpl;
import net.torocraft.toroquest.civilization.quests.util.Quest;
import net.torocraft.toroquest.civilization.quests.util.QuestData;
import net.torocraft.toroquest.civilization.quests.util.Quests;
import net.torocraft.toroquest.config.ToroQuestConfiguration;

public class QuestMine extends QuestBase implements Quest
{
	public static QuestMine INSTANCE;
	public static int ID;

	public static void init(int id)
	{
		INSTANCE = new QuestMine();
		Quests.registerQuest(id, INSTANCE);
		MinecraftForge.EVENT_BUS.register(INSTANCE);
		ID = id;
	}

//	@SubscribeEvent
//	public void onMine(HarvestDropsEvent event)
//	{
//		EntityPlayer player = event.getHarvester();
//		
//		if ( player == null )
//		{
//			return;
//		}
//		
////		ItemStack tool = player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
////
////		if ( tool == null )
////		{
////			return;
////		}
////		
////		if (!tool.hasTagCompound())
////		{
////			return;
////		}
////
////		if ( !tool.getTagCompound().hasKey("mine_quest") )
////		{
////			return;
////		}
////		String questId = tool.getTagCompound().getString("mine_quest");
////		if ( !tool.getTagCompound().hasKey("provinceID") )
////		{
////			return;
////		}
////		String provinceId = tool.getTagCompound().getString("provinceID");
////		if ( !tool.getTagCompound().hasKey("provinceName") )
////		{
////			return;
////		}
////		String provinceName = tool.getTagCompound().getString("provinceName");
////
////		if ( questId == null || provinceId == null || provinceName == null ) // || !provinceId.equals(inProvince.id.toString()))
////		{
////			return;
////		}
//
//		Set<QuestData> quests = PlayerCivilizationCapabilityImpl.get(player).getCurrentQuests();
//
//		DataWrapper quest = new DataWrapper();
//		
//		System.out.println("mine");
//
//		for (QuestData data : quests)
//		{
//			System.out.println("quest");
//
//			try
//			{
//				DataWrapper q = new DataWrapper().setData(data);
//				
//				int bt = q.getBlockType();
//				
//				System.out.println(bt);
//				
//				for ( ItemStack drop : event.getDrops() )
//				{
//					System.out.println(drop);
//					if ( this.isCorrectBlock(drop.getItem(), bt) )
//					{
//						System.out.println("!!!");
//
//						this.perform(quest, drop.getCount());
//					}
//				}
//			}
//			catch (Exception e)
//			{
//				
//			}
//		}
		
		
//		Block eventBlock = event.getState().getBlock();
//		Block allowedBlock = BLOCK_TYPES[q.getBlockType()];
//		
//		if ( allowedBlock == null || eventBlock == null )
//		{
//			return;
//		}
//		
//		for ( ItemStack itemstack: event.getDrops() )
//		{
//			String blockName = "blocks";
//					
//			if ( allowedBlock == Blocks.STONE )
//			{
//				if ( eventBlock instanceof BlockStone )
//				{
//					blockName = "Stone";
//					itemstack.setCount(1);
//					// itemstack = new ItemStack(Blocks.STONE, 1);
//				}
//				else return;
//			}
//			else if ( allowedBlock == Blocks.LOG )
//			{
//				if ( eventBlock instanceof BlockLog )
//				{
//					blockName = "Logs";
//					itemstack.setCount(1);
//					// itemstack = new ItemStack(Blocks.LOG, 1);
//				}
//				else return;
//			}
//			else if ( allowedBlock == Blocks.REDSTONE_ORE )
//			{
//				if ( eventBlock == Blocks.REDSTONE_ORE || itemstack.getItem() == Items.REDSTONE )
//				{
//					blockName = "Redstone";
//					itemstack = new ItemStack(Items.REDSTONE, rand.nextInt(2)+4);
//				}
//				else return;
//			}
//			else if ( allowedBlock == Blocks.COAL_ORE )
//			{
//				if ( eventBlock == Blocks.COAL_ORE || itemstack.getItem() == Items.COAL )
//				{
//					blockName = "Coal";
//					itemstack = new ItemStack(Items.COAL, 1);
//				}
//				else return;
//			}
//			else if ( allowedBlock == Blocks.GRAVEL )
//			{
//				if ( eventBlock instanceof BlockGravel || itemstack.getItem() == Items.FLINT )
//				{
//					blockName = "Flint";
//					itemstack = new ItemStack(Items.FLINT, 1);
//				}
//				else return;
//			}
//			else if ( allowedBlock == Blocks.DIRT )
//			{
//				if ( eventBlock instanceof BlockDirt )
//				{
//					blockName = "Dirt";
//					itemstack.setCount(1);
//				}
//				else return;
//			}
//			else
//			{
//				return;
//			}
//			
//			event.setDropChance(0.0F);
//			
//			itemstack.setTagInfo("mine_quest", new NBTTagString(questId));
//			itemstack.setTagInfo("provinceID", new NBTTagString(provinceId));
//			itemstack.setTagInfo("provinceName", new NBTTagString(provinceName));
//			itemstack.setStackDisplayName(blockName + " for " + provinceName);
//			try{itemstack.addEnchantment(null, 0);}catch(Exception e){}
//			
//			if ( player.addItemStackToInventory(itemstack) )
//			{
//				player.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);
//			}
//			
//			else
//			{
//				EntityItem item = new EntityItem(event.getWorld(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), itemstack.copy() );
//		    	item.setNoPickupDelay();
//		    	event.getWorld().spawnEntity(item);
//		    }
			
//			String blockName = "blocks";
//
//			q.setCurrentAmount( q.getCurrentAmount() + 1 );
//			if ( q.getCurrentAmount() == q.getTargetAmount() )
//			{
//				data.setCompleted(true);
//				if ( !player.world.isRemote )
//				{
//					player.sendStatusMessage( new TextComponentString( I18n.format("quest.quest_complete_message") ), ToroQuestConfiguration.showQuestCompletionAboveActionBar );
//				}
//				player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1.1F, 1.1F);
//			}
//			player.sendStatusMessage( new TextComponentString( q.getCurrentAmount() + "/" + q.getTargetAmount() + " " + blockName ), true);
//			
//	    	return;
//	}

	public void perform(QuestData quest, int count)
	{
		if ( !quest.getCompleted() )
		{
			if (quest.getPlayer().world.isRemote)
			{
				return;
			}
			
			if ( !(quest.getQuestType() == ID) )
			{
				return;
			}
			
			quest.getiData().put("amount", quest.getiData().get("amount")+count);
	
			quest.getPlayer().sendStatusMessage( new TextComponentString(MathHelper.clamp(quest.getiData().get("amount"), 0, quest.getiData().get("target"))+"/"+quest.getiData().get("target")), true);
	
			if ( quest.getiData().get("amount") >= quest.getiData().get("target") )
			{
				quest.setCompleted(true);
				chatCompletedQuest(quest);
			}
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
		Province province = loadProvince(data.getPlayer().world, data.getPlayer().getPosition());

		if ( province == null || province.id == null || !province.id.equals(data.getProvinceId()) )
		{
			return null;
		}
		
		if ( in == null || data == null )
		{
			return null;
		}
		
		List<ItemStack> givenItems = copyItems(in);
		DataWrapper q = new DataWrapper().setData(data);
		
		int target = q.getTargetAmount();
		int total = 0;

		for (ItemStack item : givenItems)
		{
			if ( this.isCorrectBlock(data.getPlayer(), item.getItem(), q.getBlockType()) )
			{
				total += item.getCount();
				item.setCount(MathHelper.clamp(total-target, 0, item.getCount()));
			}
		}
		
		if ( total < target )
		{
			if ( data.getChatStack().equals("") )
			{
				int requiredLeft = target - total;

				switch ( q.getBlockType() )
				{
					case 0:
					{
						data.setChatStack( "mine.logresource.incomplete", data.getPlayer(), ""+requiredLeft);
						break;
					}
					case 1:
					{
						data.setChatStack( "mine.dirtresource.incomplete", data.getPlayer(), ""+requiredLeft);
						break;
					}
					case 2:
					{
						data.setChatStack( "mine.stoneresource.incomplete", data.getPlayer(), ""+requiredLeft);
						break;
					}
					case 3:
					{
						data.setChatStack( "mine.coalresource.incomplete", data.getPlayer(), ""+requiredLeft);
						break;
					}
					case 4:
					{
						data.setChatStack( "mine.redstoneresource.incomplete", data.getPlayer(), ""+requiredLeft);
						break;
					}
					case 5:
					{
						data.setChatStack( "mine.obsidianresource.incomplete", data.getPlayer(), ""+requiredLeft);
						break;
					}
					case 6:
					{
						data.setChatStack( "mine.glowstoneresource.incomplete", data.getPlayer(), ""+requiredLeft);
						break;
					}
					case 7:
					{
						data.setChatStack( "mine.lapisresource.incomplete", data.getPlayer(), ""+requiredLeft);
						break;
					}
					case 8:
					{
						data.setChatStack( "mine.diamondresource.incomplete", data.getPlayer(), ""+requiredLeft);
						break;
					}
				}
				
			}
			this.setData(data);
			return null;
		}
		
		if ( q.getCurrentAmount() < q.getTargetAmount() )
		{
			if ( data.getChatStack().equals("") )
			{
				int requiredLeft = q.getTargetAmount() - q.getCurrentAmount();

				switch ( q.getBlockType() )
				{
					case 0:
					{
						data.setChatStack( "mine.loggathered.incomplete", data.getPlayer(), ""+requiredLeft);
						break;
					}
					case 1:
					{
						data.setChatStack( "mine.dirtgathered.incomplete", data.getPlayer(), ""+requiredLeft);
						break;
					}
					case 2:
					{
						data.setChatStack( "mine.stonegathered.incomplete", data.getPlayer(), ""+requiredLeft);
						break;
					}
					case 3:
					{
						data.setChatStack( "mine.coalgathered.incomplete", data.getPlayer(), ""+requiredLeft);
						break;
					}
					case 4:
					{
						data.setChatStack( "mine.redstonegathered.incomplete", data.getPlayer(), ""+requiredLeft);
						break;
					}
					case 5:
					{
						data.setChatStack( "mine.obsidiangathered.incomplete", data.getPlayer(), ""+requiredLeft);
						break;
					}
					case 6:
					{
						data.setChatStack( "mine.glowstonegathered.incomplete", data.getPlayer(), ""+requiredLeft);
						break;
					}
					case 7:
					{
						data.setChatStack( "mine.lapisgathered.incomplete", data.getPlayer(), ""+requiredLeft);
						break;
					}
					case 8:
					{
						data.setChatStack( "mine.diamondgathered.incomplete", data.getPlayer(), ""+requiredLeft);
						break;
					}
				}
			}
			this.setData(data);
			return null;
		}
		
		//boolean toolIncluded = false;

//		for (ItemStack item : givenItems)
//		{
//				if (item.getItem() instanceof ItemTool)
//				{
//					item.setCount(0);
//				}
//		}

//		if ( requiredLeft > 0 )
//		{
//			if ( data.getChatStack().equals("") )
//			{
//				Block block = BLOCK_TYPES[q.getBlockType()];
//				
//				if ( block == Blocks.STONE || block == Blocks.LOG )
//				{
//					data.setChatStack( "mine.stone.incomplete", data.getPlayer(), ""+requiredLeft);
//				}
//				else if ( block == Blocks.REDSTONE_ORE )
//				{
//					data.setChatStack( "mine.redstone.incomplete", data.getPlayer(), ""+requiredLeft);
//				}
//				else if ( block == Blocks.COAL_ORE )
//				{
//					data.setChatStack( "mine.coal.incomplete", data.getPlayer(), ""+requiredLeft);
//				}
//				else if ( block == Blocks.GRAVEL )
//				{
//					data.setChatStack( "mine.gravel.incomplete", data.getPlayer(), ""+requiredLeft);
//				}
//				else if ( block == Blocks.DIRT )
//				{
//					data.setChatStack( "mine.dirt.incomplete", data.getPlayer(), ""+requiredLeft);
//				}
//				
//			}
//			this.setData(data);
//			return null;
//		}

		//int emeraldRemainingCount = 5;

//		if (!toolIncluded)
//		{
//			for (ItemStack item : givenItems)
//			{
//				if (!item.isEmpty() && item.getItem() == Items.EMERALD)
//				{
//					int decBy = Math.min(item.getCount(), emeraldRemainingCount);
//					emeraldRemainingCount -= decBy;
//					item.shrink(decBy);
//				}
//			}
//		}
//
//		if ( !toolIncluded && emeraldRemainingCount > 0 )
//		{
//			data.setChatStack( "completereturnitem", data.getPlayer(), null );
//			this.setData(data);
//			return null;
//		}

		givenItems = removeEmptyItemStacks(givenItems);
		
		addRewardItems(data, givenItems);
		
		CivilizationHandlers.adjustPlayerRep(data.getPlayer(), data.getCiv(), getRewardRep(data));
		
		if ( PlayerCivilizationCapabilityImpl.get(data.getPlayer()).getReputation(data.getCiv()) >= 3000 )
		{
			if (!data.getPlayer().world.isRemote)
	        {
	            int i = getRewardRep(data)*2;

	            while (i > 0)
	            {
	                int j = EntityXPOrb.getXPSplit(i);
	                i -= j;
	                data.getPlayer().world.spawnEntity(new EntityXPOrb(data.getPlayer().world, data.getPlayer().posX+((rand.nextInt(2)*2-1)*2), data.getPlayer().posY, data.getPlayer().posZ+((rand.nextInt(2)*2-1)*2), j));
	            }
	        }
		}

		data.setChatStack( "mine.complete", data.getPlayer(), null );
		data.setCompleted(true);
		this.setData(data);
		return givenItems;
	}
	
//	protected boolean isForThisQuest(QuestData data, ItemStack item)
//	{
//		if (item.isEmpty())
//		{
//			return false;
//		}
//		
//		switch ( data.getiData().get("block_type") )
//		{
//			case 0: // DIRT
//			{
//				return item.getItem() == Item.getItemFromBlock(Blocks.DIRT);
//			}
//			case 1: // STONE
//			{
//				return item.getItem() == Item.getItemFromBlock(Blocks.COBBLESTONE);
//			}
//			case 2: // GRAVEL
//			{
//				return item.getItem() == Item.getItemFromBlock(Blocks.GRAVEL);
//			}
//			case 3: // COAL
//			{
//				return item.getItem() == Items.COAL;
//			}
//			case 4: // REDSTONE
//			{
//				return item.getItem() == Items.REDSTONE;
//			}
//			case 5: // LOG
//			{
//				return item.getItem() == Item.getItemFromBlock(Blocks.LOG);
//			}
//		}
//		return true;
//	}

//	protected boolean isForThisQuest(QuestData data, ItemStack item)
//	{
//		if (!item.hasTagCompound() || item.isEmpty())
//		{
//			return false;
//		}
//		String wasMinedForQuestId = item.getTagCompound().getString("mine_quest");
//		return data.getQuestId().toString().equals(wasMinedForQuestId);
//	}

	@Override
	public List<ItemStack> reject(QuestData data, List<ItemStack> in)
	{
		
		if (in == null)
		{
			data.setChatStack( "mine.reject", data.getPlayer(), null );

			// data.setChatStack( "rejectreturnitem", data.getPlayer(), null );
			this.setData(data);
			//data.getPlayer().closeScreen();
			return null;
		}

//		List<ItemStack> givenItems = copyItems(in);
//
//		for (ItemStack item : givenItems)
//		{
//			if (isForThisQuest(data, item))
//			{
//				//q.setBlockType(blockType);
//				//q.setTargetAmount((int)roll);
//				item.setCount(0);
//			}
//		}
		
//		List<ItemStack> givenItems = copyItems(in);
//
//		boolean toolIncluded = false;
//		int emeraldRemainingCount = 5;
//
//		for (ItemStack item : givenItems)
//		{
//			if (isForThisQuest(data, item))
//			{
//				if (item.getItem() instanceof ItemTool)
//				{
//					toolIncluded = true;
//					item.setCount(0);
//				}
//			}
//		}
//
//		if ( !toolIncluded )
//		{
//			for (ItemStack item : givenItems)
//			{
//				if (!item.isEmpty() && item.getItem() == Items.EMERALD && item.getCount() >= 5)
//				{
//					int decBy = Math.min(item.getCount(), emeraldRemainingCount);
//					emeraldRemainingCount -= decBy;
//					item.shrink(decBy);
//				}
//			}
//		}
//
//		if ( !toolIncluded && emeraldRemainingCount > 0 )
//		{
//			data.setChatStack( "rejectreturnitem", data.getPlayer(), null );
//			this.setData(data);
//			//data.getPlayer().closeScreen();
//			return null;
//		}
		
		data.setChatStack( "mine.reject", data.getPlayer(), null );
		this.setData(data);
		data.getPlayer().closeScreen();
		return in;
	}

	@Override
	public List<ItemStack> accept(QuestData data, List<ItemStack> in)
	{
//			DataWrapper q = new DataWrapper().setData(data);
			
//			Province province = getQuestProvince(q.getData());
			
//			if ( type == null || province == null )
//			{
//				return null;
//			}
//	
//			// ItemStack tool;
//	
//			if ( type == Blocks.GRAVEL )
//			{
//				//tool = new ItemStack(Items.IRON_SHOVEL);
//				{
//					data.setChatStack( "mine.gravel.accept", data.getPlayer(), null );
//				}
//			}
//			else if ( type == Blocks.DIRT )
//			{
//				//tool = new ItemStack(Items.IRON_SHOVEL);
//				{
//					data.setChatStack( "mine.dirt.accept", data.getPlayer(), null );
//				}
//			}
//			else if ( type == Blocks.LOG )
//			{
//				//tool = new ItemStack(Items.IRON_AXE);
//				{
//					data.setChatStack( "mine.wood.accept", data.getPlayer(), null );
//				}
//			}
//			else
//			{
//				//tool = new ItemStack(Items.IRON_PICKAXE);
//				
//				if ( type == Blocks.REDSTONE_ORE )
//				{
//					data.setChatStack( "mine.redstone.accept", data.getPlayer(), null );
//				}
//				else if ( type == Blocks.COAL_ORE )
//				{					
//					data.setChatStack( "mine.coal.accept", data.getPlayer(), null );
//
//				}
//				else if ( type == Blocks.STONE )
//				{					
//					data.setChatStack( "mine.stone.accept", data.getPlayer(), null );
//				}
//			}
	
//			tool.setStackDisplayName(tool.getDisplayName() + " of " + province.name.toString() );
//			tool.addEnchantment(Enchantment.getEnchantmentByLocation("minecraft:unbreaking"), 1);
//			tool.setTagInfo("mine_quest", new NBTTagString(q.getData().getQuestId().toString()));
//			tool.setTagInfo("provinceID", new NBTTagString(province.id.toString()));
//			tool.setTagInfo("provinceName", new NBTTagString(province.name.toString()));
	
//			in.add(tool);
		
		switch ( data.getiData().get("block_type") )
		{
			case 0: // LOG
			{
				data.setChatStack( "mine.log.accept", data.getPlayer(), null );
				break;
			}
			case 1: // DIRT
			{
				data.setChatStack( "mine.dirt.accept", data.getPlayer(), null );
				break;
			}
			case 2: // STONE
			{
				data.setChatStack( "mine.stone.accept", data.getPlayer(), null );
				break;
			}
			case 3: // COAL
			{
				data.setChatStack( "mine.coal.accept", data.getPlayer(), null );
				break;
			}
			case 4: // REDSTONE
			{
				data.setChatStack( "mine.redstone.accept", data.getPlayer(), null );
				break;
			}
			case 5: // OBSIDIAN
			{
				data.setChatStack( "mine.obsidian.accept", data.getPlayer(), null );
				break;
			}
			case 6: // GLOWSTONE
			{
				data.setChatStack( "mine.glowstone.accept", data.getPlayer(), null );
				break;
			}
			case 7: // LAPIS
			{
				data.setChatStack( "mine.lapis.accept", data.getPlayer(), null );
				break;
			}
			case 8: // DIAMOND
			{
				data.setChatStack( "mine.diamond.accept", data.getPlayer(), null );
				break;
			}
		}
		this.setData(data);
		return in;
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


		if ( type == 0 )
		{
			return "quests.chop.title";
		}
		if ( type == 1 )
		{
			return "quests.dig.title";
		}
		return "quests.mine.title";
	}

	@Override
	public String getDescription(QuestData data)
	{
		if (data == null)
		{
			return "";
		}
		
		DataWrapper q = new DataWrapper().setData(data);
		int block = q.getBlockType();

		String resource = "";
		
		switch ( block )
		{
			case 0: // LOG
			{
				resource = TextComponentHelper.createComponentTranslation(data.getPlayer(), "quests.log.itemname", new Object[0]).getFormattedText();
				break;
			}
			case 1: // DIRT
			{
				resource = TextComponentHelper.createComponentTranslation(data.getPlayer(), "quests.dirt.itemname", new Object[0]).getFormattedText();
				break;
			}
			case 2: // STONE
			{
				resource = TextComponentHelper.createComponentTranslation(data.getPlayer(), "quests.stone.itemname", new Object[0]).getFormattedText();
				break;
			}
			case 3: // COAL
			{
				resource = TextComponentHelper.createComponentTranslation(data.getPlayer(), "quests.coal.itemname", new Object[0]).getFormattedText();
				break;
			}
			case 4: // REDSTONE
			{
				resource = TextComponentHelper.createComponentTranslation(data.getPlayer(), "quests.redstone.itemname", new Object[0]).getFormattedText();
				break;
			}
			case 5: // OBSIDIAN
			{
				resource = TextComponentHelper.createComponentTranslation(data.getPlayer(), "quests.obsidian.itemname", new Object[0]).getFormattedText();
				break;
			}
			case 6: // GLOWSTONE
			{
				resource = TextComponentHelper.createComponentTranslation(data.getPlayer(), "quests.glowstone.itemname", new Object[0]).getFormattedText();
				break;
			}
			case 7: // LAPIS
			{
				resource = TextComponentHelper.createComponentTranslation(data.getPlayer(), "quests.lapis.itemname", new Object[0]).getFormattedText();
				break;
			}
			case 8: // DIAMOND
			{
				resource = TextComponentHelper.createComponentTranslation(data.getPlayer(), "quests.diamond.itemname", new Object[0]).getFormattedText();
				break;
			}
		}
		
		StringBuilder s = new StringBuilder();
				
		if ( block == 0 )
		{
			s.append("quests.chop.description");
		}
		else if ( block == 1 )
		{
			s.append("quests.dig.description");
		}
		else
		{
			s.append("quests.mine.description");
		}
		s.append("|").append(q.getTargetAmount());
		s.append("|").append( "§l" + resource + "§r" );
		s.append("|").append( "\n\n" );
		s.append("|").append( listItems(getRewardItems(q.getData())) + ",\n" );
		s.append("|").append(getRewardRep(q.getData()));
		return s.toString();
	}
	
	// ========================================================================================
	// ========================================================================================

	// public static final List<ItemStack> blockStone = OreDictionary.getOres("stone");
	
	// public static final List<ItemStack> blockStone = OreDictionary.getOres("stone");

	//public static final List<ItemStack> blockStoneBrick = OreDictionary.getOres("stonebrick");

	// public static final List<ItemStack> blockLog = OreDictionary.getOres("log");

	// @SuppressWarnings("deprecation")
	public boolean isCorrectBlock(EntityPlayer player, Item item, int i)
	{
		switch ( i )
		{
			case 0: // LOG
			{
				Block b = Block.getBlockFromItem(item);
				
				if ( b instanceof BlockLog )
				{
					return true;
				}
				
//				for ( ItemStack block : blockLog )
//				{
//					if ( block.getItem().getUnlocalizedName() == item.getUnlocalizedName() )
//					{
//						return true;
//					}
//				}
				
				return false;
			}
			case 1: // DIRT
			{
				Block b = Block.getBlockFromItem(item);
				return ( b instanceof BlockDirt || b instanceof BlockGrass );
			}
			case 2: // STONE
			{
				Block b = Block.getBlockFromItem(item);

//				if ( ToroQuestConfiguration.useOreDicForMineQuest )
//				{
//					if ( b.getUnlocalizedName().equals("tile.stonebrick") )
//					{
//						return true;
//					}
//					
//					if ( b.getUnlocalizedName().equals("tile.cobblestone") )
//					{
//						return true;
//					}
//					
//					if ( b.getUnlocalizedName().equals("tile.stone") )
//					{
//						return true;
//					}xx
//					
////					for ( ItemStack block : blockStone )
////					{
////						if ( block.getItem().getUnlocalizedName().equals(b.getUnlocalizedName()) )
////						{
////							return true;
////						}
////					}
//				}
				if ( b.getDefaultState().getMaterial() == Material.ROCK && b.getDefaultState().isFullCube() )
				{
					return true;
				}
				
				return false;
			}
			case 3: // COAL
			{
				return item == Items.COAL;
			}
			case 4: // REDSTONE
			{
//				if ( player.posY > 60 )
//				{
//					return false;
//				}
				return item == Items.REDSTONE;
			}
			case 5: // OBSIDIAN
			{
				Block b = Block.getBlockFromItem(item);
				return ( b instanceof BlockObsidian ); // || b instanceof BlockMushroom );
			}
			case 6: // GLOWSTONE
			{
				if ( player.world.getWorldType().getId() == 0 )
				{
					return false;
				}
				return item == Items.GLOWSTONE_DUST;
			}
			case 7: // LAPIS
			{
				if ( player.posY > 60 )
				{
					return false;
				}
				return item == Item.getByNameOrId("minecraft:dye:4");
			}
			case 8: // DIAMOND
			{
//				if ( player.posY > 60 )
//				{
//					return false;
//				}
				return item == Items.DIAMOND;
			}
		}
		return false;
	}
	
	// ========================================================================================
	// ========================================================================================

	@Override
	public QuestData generateQuestFor(EntityPlayer player, Province questProvince)
	{
		Random rand = new Random();
		
		DataWrapper q = new DataWrapper();

		q.getData().setCiv(questProvince.civilization);
		q.getData().setPlayer(player);
		q.getData().setProvinceId(questProvince.id);
		q.getData().setQuestId(UUID.randomUUID());
		q.getData().setQuestType(ID);
		q.getData().setCompleted(false);
		
//		Blocks.DIRT,
//		Blocks.STONE,
//		Blocks.COAL_ORE,
//		Blocks.REDSTONE_ORE
//		OBSIDIAN
//		GLOW
		
		int rep = PlayerCivilizationCapabilityImpl.get(player).getReputation(questProvince.civilization);
		
		int blockType = rand.nextInt(4);

		if ( rep >= 750 )
		{
			blockType = rand.nextInt(9);
		}
		else if ( rep >= 500 )
		{
			blockType = rand.nextInt(7);
		}
		else if ( rep >= 250 )
		{
			blockType = rand.nextInt(5);
		}
		
		if ( questProvince.civilization == CivilizationType.SUN )
		{
			if ( blockType < 2 ) blockType = rand.nextInt(2)+2;
		}
		else if ( blockType == 0 && ( ToroQuestConfiguration.disableTreeChoppingQuest ) )
		{
			blockType = rand.nextInt(3)+1;
		}
		
		q.setBlockType(blockType);
				
		double roll = 0;
		int em = 0;
		
		switch ( blockType )
		{
			case 0: // LOG
			{
				roll = (rand.nextInt(2)+2)*64;
				em = (int)Math.round(roll/8);
				break;
			}
			case 1: // DIRT
			{
				roll = (rand.nextInt(3)+2)*64;
				em = (int)Math.round(roll/16);
				break;
			}
			case 2: // STONE
			{
				roll = (rand.nextInt(3)+2)*64;
				em = (int)Math.round(roll/16);
				break;
			}
			case 3: // COAL
			{
				roll = (rand.nextInt(3)+2)*8;
				em = (int)Math.round(roll/3)+4;
				break;
			}
			case 4: // REDSTONE
			{
				roll = (rand.nextInt(4)+3)*8;
				em = (int)Math.round(roll/3)+8;
				break;
			}
			case 5: // OBSIDIAN
			{
				roll = (rand.nextInt(4)+2)*8;
				em = (int)Math.round(roll)+2;
				break;
			}
			case 6: // GLOWSTONE
			{
				roll = (rand.nextInt(5)+4)*8;
				em = (int)Math.round(roll/4)+8;
				break;
			}
			case 7: // LAPIS
			{
				roll = (rand.nextInt(5)+6)*2;
				em = (int)Math.round(roll)+12;
				break;
			}
			case 8: // DIAMOND
			{
				roll = 1;
				em = 24;
				break;
			}
		}
		q.setRewardRep(em*2);
		if ( rep >= 2000 )
		{
			em *= 2;
		}

		q.setCurrentAmount(0);
		q.setTargetAmount((int)roll);

		List<ItemStack> rewards = new ArrayList<ItemStack>(1);
		ItemStack emeralds = new ItemStack(Items.EMERALD, em);
		rewards.add(emeralds);
		setRewardItems(q.getData(), rewards);
		this.setData(q.getData());
		return q.getData();
	}
	
	
	public static class DataWrapper
	{
		private QuestData data = new QuestData();

//		public int getBlockType()
//		{
//			return coalesce(i(data.getiData().get("block_type")), 0);
//		}

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
		
		public Integer getBlockType()
		{
			return i(data.getiData().get("block_type"));
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
