package net.torocraft.toroquest.civilization.quests;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.MinecraftForge;
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

public class QuestEnemySpiderPit extends QuestBase implements Quest
{
	public static int ID;
	public static QuestEnemySpiderPit INSTANCE;
	Random rand = new Random();

	public static void init(int id)
	{
		INSTANCE = new QuestEnemySpiderPit();
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
		
		int i = 0;
		
		for ( ItemStack item: in )
		{
			try
			{
				if ( item.getTagCompound().getString("provinceHeirloom").equals(data.getProvinceId().toString()) )
				{
					data.setCompleted(true);
					in.remove(i);
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
					data.setChatStack( "spider_pit.complete", data.getPlayer(), null );
					this.setData(data);
					in.addAll(getRewardItems(data));
					return in;
				}
			}
			catch ( Exception e )
			{

			}
			i++;
		}
		if ( data.getChatStack() == "" )
		{
			data.setChatStack( "spider_pit.incomplete", data.getPlayer(), null );
			this.setData(data);
		}
		return null;
		
	}

	@Override
	public List<ItemStack> reject(QuestData data, List<ItemStack> in)
	{
		if ( data.getCompleted() )
		{
			return null;
		}
		
		data.setChatStack( "spider_pit.reject", data.getPlayer(), null );
		this.setData(data);
		data.getPlayer().closeScreen();
		return in;
	}

	@Override
	public List<ItemStack> accept(QuestData data, List<ItemStack> in)
	{
			BlockPos pos = searchForSuitableLocation(data, 400, -20);
			try
			{
				placeDungeonRoom(data.getPlayer().world, data, pos.getX(), pos.getZ());
			}
			catch (Exception e)
			{
				try
				{
					int tries = 3;
					while ( tries > 0 && pos == null )
					{
						tries--;
						pos = searchForSuitableLocation(data, 400, 0);
					}
					placeDungeonRoom(data.getPlayer().world, data, pos.getX(), pos.getZ());
				}
				catch (Exception ee)
				{
					reject(data,in);
				}
	
			}
			setSpawnPosition(data, pos);

			ItemStack itemstack = ItemMapCentered.setupNewMap(data.getPlayer().world, (double)pos.getX(), (double)pos.getZ(), (byte)3, true, true);
			ItemMapCentered.renderBiomePreviewMap(data.getPlayer().world, itemstack);
			MapData.addTargetDecoration(itemstack, pos, "+", MapDecoration.Type.TARGET_POINT);
			//itemstack.setTranslatableName("§lMap to Heirloom§r");
			itemstack.setStackDisplayName(TextComponentHelper.createComponentTranslation(data.getPlayer(), "quests.spider_pit.map", new Object[0]).getFormattedText() + "§r");
			in.add(itemstack);
			data.setChatStack( "spider_pit.accept", data.getPlayer(), null );
			this.setData(data);
			return in;
	}
	
	private void placeDungeonRoom(World world, QuestData data, int xCenter, int zCenter)
	{
		int halfX = rand.nextInt(6)+6;
		int halfZ = rand.nextInt(6)+6;
		int height = rand.nextInt(3)+8;

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
						else if ( !lava && rand.nextInt(50) == 0 )
						{
							lava = true;
							block = Blocks.LAVA.getDefaultState();
						}
						else
						{
							if ( rand.nextInt(4) == 0 )
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
					}
					else
					{
						if ( (y == yMin) )
						{
							if ( rand.nextInt(3) == 0 )
							{
								block = Blocks.STONE_SLAB.getDefaultState();
							}
							else
							{
								block = Blocks.AIR.getDefaultState();
								world.setBlockState(new BlockPos(x, y-1, z), Blocks.WEB.getDefaultState());
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
				if ( rand.nextInt(MathHelper.abs(z-zMin)*4+1) == 0 )
				{
					pos = new BlockPos(x, yMax - 2 + rand.nextInt(3), z);
					world.setBlockState(pos, Blocks.WEB.getDefaultState());
				}
				if ( rand.nextInt(MathHelper.abs(x-xMin)*16+1) == 0 )
				{
					pos = new BlockPos(x, yMin + 1 - rand.nextInt(2), z);
					world.setBlockState(pos, Blocks.WEB.getDefaultState());
				}
			}
		}
		
		for (int x = xMin; x < xMax; x++)
		{
			for (int z = zMin; z < zMax; z++)
			{
				if ( rand.nextInt(75) == 0 )
				{
					pos = new BlockPos(x, yMin, z);
					placeChest(world, pos);
				}
			}
		}
		
		for ( int i = 0; height > i; i++ )
		{
			int y = yMin+i;
			if ( rand.nextInt(16) != 0 ) world.setBlockState(new BlockPos(xCenter, y, zCenter), Blocks.STONEBRICK.getDefaultState());
			if ( rand.nextInt(16) != 0 ) world.setBlockState(new BlockPos(xCenter-1, y, zCenter), Blocks.STONEBRICK.getDefaultState());
			else world.setBlockState(new BlockPos(xCenter-1, y, zCenter), Blocks.STONE_BRICK_STAIRS.getDefaultState());
			if ( rand.nextInt(16) != 0 ) world.setBlockState(new BlockPos(xCenter, y, zCenter-1), Blocks.STONEBRICK.getDefaultState());
			else world.setBlockState(new BlockPos(xCenter, y, zCenter-1), Blocks.STONE_BRICK_STAIRS.getDefaultState());
			if ( rand.nextInt(16) != 0 ) world.setBlockState(new BlockPos(xCenter-1, y, zCenter-1), Blocks.STONEBRICK.getDefaultState());
		}
		addToroSpawner(data, data.getPlayer().getEntityWorld(), new BlockPos( xCenter+1, yMin + 1, zCenter ), getDefaultEnemies(data));
		placeQuestChest(world, data, new BlockPos(xCenter+1, yMin, zCenter));

	}
	
	protected void placeChest(World world, BlockPos placementPos)
	{
		world.setBlockState(placementPos, Blocks.TRAPPED_CHEST.getDefaultState());
		TileEntity tileentity = world.getTileEntity(placementPos);
		if (tileentity instanceof TileEntityChest)
		{
			((TileEntityChest) tileentity).setLootTable(LootTableList.CHESTS_SIMPLE_DUNGEON, world.rand.nextLong());
		}
		world.setBlockState(placementPos.down(), Blocks.STONE.getDefaultState());
		if ( rand.nextBoolean() )
		{
			world.setBlockState(placementPos.down(2), Blocks.TNT.getDefaultState());
		}
		world.setBlockState(placementPos.down(3), Blocks.STONE.getDefaultState());
	}
	protected void placeQuestChest(World world, QuestData data, BlockPos placementPos)
	{
		world.setBlockState(placementPos, Blocks.CHEST.getDefaultState());
		TileEntity tileentity = world.getTileEntity(placementPos);
		if (tileentity instanceof TileEntityChest)
		{
			for ( int i = rand.nextInt(6) + 6; i > 0; i-- )
			{
				((TileEntityChest) tileentity).setInventorySlotContents( rand.nextInt(27), new ItemStack(Items.GOLD_NUGGET,rand.nextInt(3)+1));

				if ( rand.nextInt(3) == 0 )
				{
					((TileEntityChest) tileentity).setInventorySlotContents( rand.nextInt(27), new ItemStack(Items.EMERALD, 1));
				}
				else if ( rand.nextInt(3) == 0 )
				{
					((TileEntityChest) tileentity).setInventorySlotContents( rand.nextInt(27), new ItemStack(Blocks.WEB, 1));
				}
				else
				{
					((TileEntityChest) tileentity).setInventorySlotContents( rand.nextInt(27), new ItemStack(Items.EMERALD, 5));
				}
			}
			Item item = Items.CLOCK;
			ItemStack itemstack = new ItemStack(item,1);
			itemstack.setTagInfo("provinceHeirloom", new NBTTagString(data.getProvinceId().toString()));
			itemstack.setStackDisplayName("§eAncient Heirloom§r");
			try{itemstack.addEnchantment(null, 0);}catch(Exception e){}
			((TileEntityChest) tileentity).setInventorySlotContents(15, itemstack);
		}
		world.setBlockState(placementPos.down(), Blocks.STONE.getDefaultState());
	}

	private void addToroSpawner(QuestData data, World world, BlockPos blockpos, List<String> entities)
	{
		world.setBlockState(blockpos, BlockToroSpawner.INSTANCE.getDefaultState());
		TileEntity tileentity = world.getTileEntity(new BlockPos( blockpos.getX(), blockpos.getY(), blockpos.getZ()));
		if ( tileentity instanceof TileEntityToroSpawner )
		{
			TileEntityToroSpawner spawner = (TileEntityToroSpawner) tileentity;
			spawner.setTriggerDistance(16);
			spawner.setEntityIds(entities);
			spawner.setSpawnRadius(5);
			//spawner.addEntityTag(data.getQuestId().toString());
			//spawner.addEntityTag("spider_quest");
		}
		else
		{
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
		return "quests.spider_pit.title";
	}

	@Override
	public String getDescription(QuestData data) 
	{
		if (data == null)
		{
			return "";
		}
		StringBuilder s = new StringBuilder();
		s.append("quests.spider_pit.description");
		if (getSpawnPosition(data) != null)
		{
			s.append("|").append( " at §lLocation:§r [" +  getDirections(getProvincePosition(getQuestProvince(data)), getSpawnPosition(data)) + "]\n\n" );
		}
		else
		{
			s.append("|").append("\n\n");
		}
		s.append("|").append(listItemsBlocks(getRewardItems(data)) + ",\n" );
		s.append("|").append(getRewardRep(data));
		return s.toString();
	}

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
		int em = 20;
		setRewardRep(data, em*2);
		if ( PlayerCivilizationCapabilityImpl.get(player).getReputation(province.civilization) >= 2000 )
		{
			em *= 2;
		}
		List<ItemStack> reward = new ArrayList<ItemStack>(1);
		reward.add(new ItemStack(Items.EMERALD, em));
		setRewardItems(data, reward);
		this.setData(data);
		return data;
	}

	private List<String> getDefaultEnemies(QuestData data)
	{
		List<String> entity = new ArrayList<String>();
		for (int i = 0; i < 6; i++)
		{
			entity.add("minecraft:spider");
		}
		return entity;
	}
	
}
