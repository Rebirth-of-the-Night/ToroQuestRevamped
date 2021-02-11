package net.torocraft.toroquest.civilization.quests;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockSlab.EnumBlockHalf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.village.Village;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.torocraft.toroquest.block.BlockToroSpawner;
import net.torocraft.toroquest.block.TileEntityToroSpawner;
import net.torocraft.toroquest.civilization.CivilizationHandlers;
import net.torocraft.toroquest.civilization.CivilizationUtil;
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
					// data.setChatStack( "Ah! Magnificent! This is just what I was looking for, " + data.getPlayer().getName() + "." );
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
			data.setChatStack( "Have you found the artifact, " + data.getPlayer().getName() + "?" );
			this.setData(data);
		}
		return null;
		
	}

	@Override
	public List<ItemStack> reject(QuestData data, List<ItemStack> in)
	{
		data.setChatStack( "Ah, I understand. A task too difficult for you then?" );
		this.setData(data);
		data.getPlayer().closeScreen();
		return in;
	}

	@Override
	public List<ItemStack> accept(QuestData data, List<ItemStack> in)
	{
			BlockPos pos = searchForSuitableLocation(data, 400, 30);
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
						pos = searchForSuitableLocation(data, 400, 40);
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
			itemstack.setTranslatableName("§lMap to Artifact§r");
			itemstack.setStackDisplayName("§lMap to Artifact§r");

			in.add(itemstack);
			data.setChatStack("I wish to recover an ancient arifact to learn more about the history of our world. This artifact is from a long lost civilization, buried deep beneath the surface. I have marked the location on a map for you.");
			this.setData(data);
			return in;
	}
	
	private void placeDungeonRoom(World world, QuestData data, int xCenter, int zCenter)
	{
		int halfX = rand.nextInt(7)+5;
		int halfZ = rand.nextInt(7)+5;
		int height = rand.nextInt(7)+5;

		int xMin = xCenter - halfX;
		int zMin = zCenter - halfZ;
		int yMin = 16 + rand.nextInt(32);

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
						else if ( !lava && rand.nextInt(160) == 0 )
						{
							lava = true;
							block = Blocks.LAVA.getDefaultState();
						}
						else
						{
							if ( rand.nextInt(4) == 0 )
							{
								block = Blocks.STONEBRICK.getDefaultState();
							}
							else
							{
								block = Blocks.STONE.getDefaultState();
							}
						}
					}
					else
					{
						block = Blocks.AIR.getDefaultState();
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
				if ( rand.nextInt(MathHelper.abs(z-zMin)*4+4) == 0 )
				{
					pos = new BlockPos(x, yMax - 2 + rand.nextInt(3), z);
					world.setBlockState(pos, Blocks.WEB.getDefaultState());
				}
				if ( rand.nextInt(MathHelper.abs(x-xMin)*16+8) == 0 )
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
				if ( rand.nextInt(100) == 0 )
				{
					pos = new BlockPos(x, yMin, z);
					placeChest(world, pos);
				}
			}
		}
		
		addToroSpawner(data, data.getPlayer().getEntityWorld(), new BlockPos( xCenter, yMin + 1, zCenter ), getDefaultEnemies(data));
		placeQuestChest(world, data, new BlockPos(xCenter, yMin, zCenter));

	}
	
	protected void placeChest(World world, BlockPos placementPos)
	{
		world.setBlockState(placementPos, Blocks.CHEST.getDefaultState());
		TileEntity tileentity = world.getTileEntity(placementPos);
		if (tileentity instanceof TileEntityChest)
		{
			((TileEntityChest) tileentity).setLootTable(LootTableList.CHESTS_SIMPLE_DUNGEON, world.rand.nextLong());
		}
		world.setBlockState(placementPos.down(), Blocks.STONE.getDefaultState());
	}
	protected void placeQuestChest(World world, QuestData data, BlockPos placementPos)
	{
		world.setBlockState(placementPos, Blocks.CHEST.getDefaultState());
		TileEntity tileentity = world.getTileEntity(placementPos);
		if (tileentity instanceof TileEntityChest)
		{
			for ( int i = rand.nextInt(6) + 6; i > 0; i-- )
			{
				((TileEntityChest) tileentity).setInventorySlotContents( rand.nextInt(26), new ItemStack(Items.GOLD_NUGGET,rand.nextInt(3)));		System.out.println( "arr" );

				if ( rand.nextBoolean() )((TileEntityChest) tileentity).setInventorySlotContents( rand.nextInt(26), new ItemStack(Items.GOLD_INGOT, 1));
			}
			Item item = Items.CLOCK;
			ItemStack itemstack = new ItemStack(item,1);
			itemstack.setTagInfo("provinceHeirloom", new NBTTagString(data.getProvinceId().toString()));
			itemstack.setStackDisplayName("§6Ancient Artifact from " + data.getProvinceName() + "§r");
			try{itemstack.addEnchantment(null, 0);}catch(Exception e){}
			((TileEntityChest) tileentity).setInventorySlotContents(rand.nextInt(26), itemstack);
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
			spawner.setTriggerDistance(32);
			spawner.setEntityIds(entities);
			spawner.setSpawnRadius(5);
			spawner.addEntityTag(data.getQuestId().toString());
			spawner.addEntityTag("spider_quest");
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
		s.append("|").append(listItems(getRewardItems(data)) + ",\n" );
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
		
		int em = 10;
		int rep = em*2;
		if ( PlayerCivilizationCapabilityImpl.get(player).getReputation(province.civilization) >= 3000 )
		{
			em *= 2;
		}
		List<ItemStack> reward = new ArrayList<ItemStack>(1);
		reward.add(new ItemStack(Items.EMERALD, em));
		setRewardRep(data, rep);
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
