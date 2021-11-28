package net.torocraft.toroquest.civilization.quests;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.server.command.TextComponentHelper;
import net.torocraft.toroquest.block.BlockToroSpawner;
import net.torocraft.toroquest.block.TileEntityToroSpawner;
import net.torocraft.toroquest.civilization.CivilizationHandlers;
import net.torocraft.toroquest.civilization.Province;
import net.torocraft.toroquest.civilization.player.PlayerCivilizationCapabilityImpl;
import net.torocraft.toroquest.civilization.quests.util.ItemMapCentered;
import net.torocraft.toroquest.civilization.quests.util.Quest;
import net.torocraft.toroquest.civilization.quests.util.QuestData;
import net.torocraft.toroquest.civilization.quests.util.Quests;
import net.torocraft.toroquest.entities.EntityConstructQuest;

public class QuestEnemyGolem extends QuestBase implements Quest
{
	public static int ID;
	public static QuestEnemyGolem INSTANCE;
	
	Random rand = new Random();

	public static void init(int id)
	{
		INSTANCE = new QuestEnemyGolem();
		Quests.registerQuest(id, INSTANCE);
		MinecraftForge.EVENT_BUS.register(INSTANCE);
		ID = id;
	}

	@Override
	public List<ItemStack> complete(QuestData data, List<ItemStack> in)
	{
		Province province = loadProvince(data.getPlayer().world, data.getPlayer().getPosition());

		if (province == null || province.id == null || !province.id.equals(data.getProvinceId()))
		{
			return null;
		}
		
		if ( !data.getCompleted() )
		{
			if ( data.getChatStack() == "" )
			{
				data.setChatStack( "steam_golem.incomplete", data.getPlayer(), null );
				this.setData(data);
			}
			return null;
		}
		
//		int i = 0;
//		for ( ItemStack item: in )
//		{
//			try
//			{
//				if ( item.getItem() == Item.getByNameOrId("toroquest:dwarven_artifact") )
//				{
//					data.setCompleted(true);
//					in.remove(i);
//					CivilizationHandlers.adjustPlayerRep(data.getPlayer(), data.getCiv(), getRewardRep(data));
//					
//					if ( PlayerCivilizationCapabilityImpl.get(data.getPlayer()).getReputation(data.getCiv()) >= 3000 )
//					{
//						if (!data.getPlayer().world.isRemote)
//				        {
//				            int ii = getRewardRep(data)*2;
//
//				            while (ii > 0)
//				            {
//				                int j = EntityXPOrb.getXPSplit(ii);
//				                ii -= j;
//				                data.getPlayer().world.spawnEntity(new EntityXPOrb(data.getPlayer().world, data.getPlayer().posX+((rand.nextInt(2)*2-1)*2), data.getPlayer().posY, data.getPlayer().posZ+((rand.nextInt(2)*2-1)*2), j));
//				            }
//				        }
//					}
//					data.setChatStack( "steam_golem.complete", data.getPlayer(), null );
//					this.setData(data);
//					in.addAll(getRewardItems(data));
//					return in;
//				}
//			}
//			catch ( Exception e )
//			{
//
//			}
//			i++;
//		}
		
		CivilizationHandlers.adjustPlayerRep(data.getPlayer(), data.getCiv(), getRewardRep(data));
		
		if ( PlayerCivilizationCapabilityImpl.get(data.getPlayer()).getReputation(data.getCiv()) >= 3000 )
		{
			if (!data.getPlayer().world.isRemote)
	        {
	            int ii = getRewardRep(data)*2;

	            while (ii > 0)
	            {
	                int j = EntityXPOrb.getXPSplit(ii);
	                ii -= j;
	                data.getPlayer().world.spawnEntity(new EntityXPOrb(data.getPlayer().world, data.getPlayer().posX+((rand.nextInt(2)*2-1)*2), data.getPlayer().posY, data.getPlayer().posZ+((rand.nextInt(2)*2-1)*2), j));
	            }
	        }
		}
		
		data.setChatStack( "steam_golem.complete", data.getPlayer(), null );
		in.addAll(getRewardItems(data));
		this.setData(data);
		return in;
		
	}
	
//	@Override
//	public List<ItemStack> complete(QuestData data, List<ItemStack> in)
//	{
//		
//		Province province = loadProvince(data.getPlayer().world, data.getPlayer().getPosition());
//
//		if ( province == null || province.id == null || !province.id.equals(data.getProvinceId()) )
//		{
//			return null;
//		}
//		
//		if ( !data.getCompleted() )
//		{
//			if ( data.getChatStack().equals("") )
//			{
//				data.setChatStack( "My scouts report the XXX still lives!" );
//				this.setData(data);
//			}
//			// data.getPlayer().closeScreen();
//			return null;
//		}
//		CivilizationHandlers.adjustPlayerRep(data.getPlayer(), data.getCiv(), getRewardRep(data));
//		
//		if ( PlayerCivilizationCapabilityImpl.get(data.getPlayer()).getReputation(data.getCiv()) >= 3000 )
//		{
//			if (!data.getPlayer().world.isRemote)
//	        {
//	            int i = getRewardRep(data)*2;
//
//	            while (i > 0)
//	            {
//	                int j = EntityXPOrb.getXPSplit(i);
//	                i -= j;
//	                data.getPlayer().world.spawnEntity(new EntityXPOrb(data.getPlayer().world, data.getPlayer().posX+((rand.nextInt(2)*2-1)*2), data.getPlayer().posY, data.getPlayer().posZ+((rand.nextInt(2)*2-1)*2), j));
//	            }
//	        }
//		}
//		
//		data.setChatStack( "You are a hero, " + data.getPlayer().getName() + "! You have slain the beholder! I am truly XXX for your bravery." );
//		this.setData(data);
//		in.addAll(getRewardItems(data));
//		return in;
//	}

	@Override
	public List<ItemStack> reject(QuestData data, List<ItemStack> in)
	{
		if ( data.getCompleted() )
		{
			return null;
		}
		
		data.setChatStack( "steam_golem.reject", data.getPlayer(), null );
		this.setData(data);
		data.getPlayer().closeScreen();
		return in;
	}

	@Override
	public List<ItemStack> accept(QuestData data, List<ItemStack> in)
	{
		try
		{
			BlockPos pos = searchForSuitableLocation(data, 600, -30);
			
			int tries = 3;
			
			while ( tries > 0 && pos == null )
			{
				tries--;
				pos = searchForSuitableLocation(data, 600, -tries*10);
			}
			
			placeDungeonRoom(data.getPlayer().world, data, pos.getX(), pos.getZ());
			setSpawnPosition(data, pos);

			ItemStack itemstack = ItemMapCentered.setupNewMap(data.getPlayer().world, (double)pos.getX(), (double)pos.getZ(), (byte)3, true, true);
			ItemMapCentered.renderBiomePreviewMap(data.getPlayer().world, itemstack);
			MapData.addTargetDecoration(itemstack, pos, "+", MapDecoration.Type.TARGET_POINT);
			//itemstack.setTranslatableName("§lMap to " + TextComponentHelper.createComponentTranslation(data.getPlayer(), "quests.steam_golem.map", new Object[0]).getFormattedText() + "§r");
			itemstack.setStackDisplayName(TextComponentHelper.createComponentTranslation(data.getPlayer(), "quests.steam_golem.map", new Object[0]).getFormattedText() + "§r");
			in.add(itemstack);
			data.setChatStack( "steam_golem.accept", data.getPlayer(), null );
			this.setData(data);
		}
		catch (Exception e)
		{
			reject(data,in);
		}
		return in;
	}
	
	private void placeDungeonRoom(World world, QuestData data, int xCenter, int zCenter)
	{
		int halfX = rand.nextInt(5)+10;
		int halfZ = rand.nextInt(5)+15;
		int height = rand.nextInt(3)+9;

		int xMin = xCenter - halfX;
		int zMin = zCenter - halfZ;
		int yMin = 40;

		int xMax = xCenter + halfX;
		int zMax = zCenter + halfZ;
		int yMax = yMin + height;
		
		boolean lava = false;

		IBlockState block;
		BlockPos pos;
		Random rand = new Random();

		for (int y = yMin; y <= yMax; y++)
		{
			lava = false;
			for (int x = xMin; x <= xMax; x++)
			{
				for (int z = zMin; z <= zMax; z++)
				{
					if ( rand.nextInt(3) == 0 && ( x == xMin || x == xMax || z == zMax || z == zMin || y == yMax || y == yMin ) )
					{
						if ( rand.nextInt(50) == 0 ) 
						{
							block = Blocks.EMERALD_ORE.getDefaultState();
						}
						else if ( rand.nextInt(50) == 0 ) 
						{
							block = Blocks.GOLD_ORE.getDefaultState();
						}
						else if ( !lava && rand.nextInt(150) == 0 )
						{
							lava = true;
							block = Blocks.LAVA.getDefaultState();
						}
						else
						{
							if ( rand.nextInt(4) == 0 )
							{
								if ( rand.nextInt(8) == 0 )
								{
									block = Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CHISELED);
								}
								else if ( rand.nextInt(4) == 0 )
								{
									block = Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CRACKED);
								}
								else if ( rand.nextInt(4) == 0 )
								{
									block = Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.MOSSY);
								}
								else
								{
									block = Blocks.STONEBRICK.getDefaultState();
								}
							}
							else
							{
								block = Blocks.STONE.getDefaultState();
							}
						}
					}
					else
					{
						if ( (x == xMin || x == xMax) && y == yMin+height/2 )
						{
							block = Blocks.IRON_BARS.getDefaultState();
						}
						else if ( (z == zMin || z == zMax) && y == yMin+height/2 )
						{
							block = Blocks.IRON_BARS.getDefaultState();
						}
						else if ( (y == yMin) )
						{
							if ( rand.nextInt(8) == 0 )
							{
								if ( rand.nextInt(4) == 0 ) block = Blocks.STONE_BRICK_STAIRS.getDefaultState().withRotation(Rotation.CLOCKWISE_90);
								else if ( rand.nextInt(4) == 0 ) block = Blocks.STONE_BRICK_STAIRS.getDefaultState().withRotation(Rotation.COUNTERCLOCKWISE_90);
								else if ( rand.nextInt(4) == 0 ) block = Blocks.STONE_BRICK_STAIRS.getDefaultState().withRotation(Rotation.CLOCKWISE_180);
								else block = Blocks.STONE_BRICK_STAIRS.getDefaultState().withRotation(Rotation.NONE);
							}
							else if ( rand.nextInt(4) == 0 )
							{
								block = Blocks.STONEBRICK.getDefaultState();
							}
							else if ( rand.nextInt(4) == 0 )
							{
								block = Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.MOSSY);
							}
							else if ( rand.nextInt(4) == 0 )
							{
								block = Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CRACKED);
							}
							else
							{
								block = Blocks.AIR.getDefaultState();
								world.setBlockState(new BlockPos(x, y-1, z), Blocks.MAGMA.getDefaultState());
							}
						}
						else
						{
							block = Blocks.AIR.getDefaultState();
						}
					}
					pos = new BlockPos(x, y, z);
					world.setBlockState(pos, block);
				}
			}
		}

		for (int x = xMin; x < xMax; x++)
		{
			for (int z = zMin; z < zMax; z++)
			{
				if ( rand.nextBoolean() && z % 4 == 0 )
				{
					pos = new BlockPos(x, yMax, z);
					world.setBlockState(pos.down(), Blocks.STONE.getDefaultState());
					world.setBlockState(pos, Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE.getDefaultState(), 1);
				}
			}
		}
		
		for (int x = xMin; x < xMax; x++)
		{
			for (int z = zMin; z < zMax; z++)
			{
				if ( rand.nextInt(100) == 0 )
				{
					pos = new BlockPos(x, yMin, z);
					this.placeChest(world, pos);
				}
			}
		}
		
		this.placePillar(yMin,height,xCenter,zCenter,world);
		this.placePillar(yMin,height,xMin+1,zCenter-4,world);
		this.placePillar(yMin,height,xMin+5,zCenter+2,world);
		this.placePillar(yMin,height,xCenter-2,zMax-5,world);
		this.placePillar(yMin,height,xCenter+2,zMax-5,world);
		this.placePillar(yMin,height,xCenter-5,zCenter+5,world);
		
		this.addToroSpawner(data, data.getPlayer().getEntityWorld(), new BlockPos( xCenter+1, yMin + 1, zCenter ), getDefaultEnemies(data));
		//this.placeQuestChest(world, data, new BlockPos(xCenter+1, yMin, zCenter));
	}
	
	public void placePillar(int yMin, int height, int xCenter, int zCenter, World world)
	{
		for ( int i = 0; height > i; i++ )
		{
			int y = yMin+i;
			world.setBlockState(new BlockPos(xCenter, y, zCenter), clay);
			world.setBlockState(new BlockPos(xCenter-1, y, zCenter), clay);
			world.setBlockState(new BlockPos(xCenter, y, zCenter-1), clay);
			world.setBlockState(new BlockPos(xCenter-1, y, zCenter-1), clay);
		}
	}
	
	IBlockState clay = Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.YELLOW);
	
	protected void placeChest(World world, BlockPos placementPos)
	{
		world.setBlockState(placementPos, Blocks.TRAPPED_CHEST.getDefaultState());
		TileEntity tileentity = world.getTileEntity(placementPos);
		if (tileentity instanceof TileEntityChest)
		{
			if ( rand.nextInt(3) == 0 )
			{
				((TileEntityChest) tileentity).setLootTable(LootTableList.CHESTS_STRONGHOLD_LIBRARY, world.rand.nextLong());
			}
			else
			{
				((TileEntityChest) tileentity).setLootTable(LootTableList.CHESTS_VILLAGE_BLACKSMITH, world.rand.nextLong());
			}
		}
		world.setBlockState(placementPos.down(), Blocks.STONE.getDefaultState());
		if ( rand.nextInt(3) == 0 )
		{
			world.setBlockState(placementPos.down(2), Blocks.TNT.getDefaultState());
		}
		world.setBlockState(placementPos.down(3), Blocks.STONE.getDefaultState());
	}
//	protected void placeQuestChest(World world, QuestData data, BlockPos placementPos)
//	{
//		world.setBlockState(placementPos, Blocks.CHEST.getDefaultState());
//		TileEntity tileentity = world.getTileEntity(placementPos);
//		if (tileentity instanceof TileEntityChest)
//		{
//			for ( int i = rand.nextInt(6) + 6; i > 0; i-- )
//			{
//				((TileEntityChest) tileentity).setInventorySlotContents( rand.nextInt(27), new ItemStack(Items.GOLD_NUGGET,rand.nextInt(3)+1));
//
//				if ( rand.nextInt(3) == 0 )
//				{
//					((TileEntityChest) tileentity).setInventorySlotContents( rand.nextInt(27), new ItemStack(Items.EMERALD, 1));
//				}
//				else
//				{
//					((TileEntityChest) tileentity).setInventorySlotContents( rand.nextInt(27), new ItemStack(Items.EMERALD, 5));
//				}
//			}
//			Item item = Items.CLOCK;
//			ItemStack itemstack = new ItemStack(item,1);
//			itemstack.setTagInfo("provinceHeirloom", new NBTTagString(data.getProvinceId().toString()));
//			itemstack.setStackDisplayName("§6Ancient Heirloom§r");
//			try{itemstack.addEnchantment(null, 0);}catch(Exception e){}
//			((TileEntityChest) tileentity).setInventorySlotContents(15, itemstack);
//		}
//		world.setBlockState(placementPos.down(), Blocks.STONE.getDefaultState());
//	}

	private void addToroSpawner(QuestData data, World world, BlockPos blockpos, List<String> entities)
	{
		world.setBlockState(blockpos, BlockToroSpawner.INSTANCE.getDefaultState());
		TileEntity tileentity = world.getTileEntity(new BlockPos( blockpos.getX(), blockpos.getY(), blockpos.getZ()));
		if ( tileentity instanceof TileEntityToroSpawner )
		{
			TileEntityToroSpawner spawner = (TileEntityToroSpawner) tileentity;
			spawner.setTriggerDistance(40);
			spawner.setEntityIds(entities);
			spawner.setSpawnRadius(5);
			spawner.addEntityTag(data.getQuestId().toString());
			spawner.addEntityTag("golem_quest");
		}
		else
		{
			EntityConstructQuest e = new EntityConstructQuest(world);
			e.setPosition(blockpos.getX() + 0.5, blockpos.getY() + 1, blockpos.getZ() + 0.5);
			e.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(e)), (IEntityLivingData) null);
			world.spawnEntity(e);
			System.out.println("tile entity is missing");
		}
	}

	@Override
	public String getTitle(QuestData data)
	{
		if (data == null)
		{
			return "";
		}
		return "quests.steam_golem.title";
	}

	@Override
	public String getDescription(QuestData data) 
	{
		if (data == null)
		{
			return "";
		}
		StringBuilder s = new StringBuilder();
		s.append("quests.steam_golem.description");
		if (getSpawnPosition(data) != null)
		{
			s.append("|").append( " at §lLocation:§r [" +  getDirections(getProvincePosition(getQuestProvince(data)), getSpawnPosition(data)) + "]\n\n" );
		}
		else
		{
			s.append("|").append("\n\n");
		}
		s.append("|").append(listItems(getRewardItems(data)) + ",\n" );
		s.append("|").append(getRewardRep(data));
		return s.toString();
	}
	
	protected final int emeraldAmount = 5;

	@Override
	public QuestData generateQuestFor(EntityPlayer player, Province province)
	{
		QuestData data = new QuestData();
		data.setCiv(province.civilization);
		data.setProvinceName(province.name);
		data.setPlayer(player);
		data.setProvinceId(province.id);
		data.setQuestId(UUID.randomUUID());
		data.setQuestType(ID);
		data.setCompleted(false);
		setRewardRep(data, emeraldAmount*18);
		int em = emeraldAmount;
		if ( PlayerCivilizationCapabilityImpl.get(player).getReputation(province.civilization) >= 2000 )
		{
			em *= 2;
		}
		List<ItemStack> reward = new ArrayList<ItemStack>(1);
		reward.add(new ItemStack(Blocks.EMERALD_BLOCK, em));
		setRewardItems(data, reward);
		this.setData(data);
		return data;
	}

	private List<String> getDefaultEnemies(QuestData data)
	{
		List<String> entity = new ArrayList<String>();
		{
			entity.add("toroquest:toroquest_construct_quest");
		}
		return entity;
	}
	
	@SubscribeEvent
	public void checkkills(LivingDeathEvent event)
	{
		Entity victim = event.getEntity();
		
		if (!(victim instanceof EntityConstructQuest) )
		{
			return;
		}

		DamageSource source = event.getSource();
		
		if ( source == null || source.getTrueSource() == null )
		{
			return;
		}

		List<EntityPlayer> playerList = victim.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(victim.getPosition()).grow(512, 128, 512), new Predicate<EntityPlayer>()
		{
			public boolean apply(@Nullable EntityPlayer entity)
			{
				return true;
			}
		});
		
		if ( playerList.size() < 1 )
		{
			if (source.getTrueSource() instanceof EntityPlayer)
			{
				playerList.add((EntityPlayer) source.getTrueSource());
			}
		}
		
		//CivilizationType civ = null;
		for ( EntityPlayer player : playerList )
		{
			Set<QuestData> quests = PlayerCivilizationCapabilityImpl.get(player).getCurrentQuests();
			for (QuestData data : quests)
			{
				if (ID == data.getQuestType())
				{
					data.setCompleted(true);
					chatCompletedQuest(data);
					//civ = data.getCiv();
					//this.setData(data);
				}
			}
			player.sendMessage(new TextComponentString(TextComponentHelper.createComponentTranslation(player, "quests.steam_golem.slain", new Object[0]).getFormattedText()));
		}
	}
}
