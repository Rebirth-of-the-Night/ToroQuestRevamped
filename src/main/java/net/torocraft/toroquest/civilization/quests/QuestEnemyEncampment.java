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
import net.torocraft.toroquest.entities.EntitySentry;
import net.torocraft.toroquest.entities.EntityToroMob;

public class QuestEnemyEncampment extends QuestBase implements Quest {
	public static int ID;
	public static QuestEnemyEncampment INSTANCE;

	private final static int hutHalfWidth = 4;

	private final static int mobCount = 9;
	private final static int minKillsRequired = 6;

	public static void init(int id)
	{
		INSTANCE = new QuestEnemyEncampment();
		Quests.registerQuest(id, INSTANCE);
		MinecraftForge.EVENT_BUS.register(INSTANCE);
		ID = id;
	}

	@Override
	public List<ItemStack> complete(QuestData data, List<ItemStack> in)
	{
		
		if ( !data.getCompleted() )
		{
			int kills = getKills(data);
			
			if ( kills >= minKillsRequired )
			{
				data.setCompleted(true);
				CivilizationHandlers.adjustPlayerRep(data.getPlayer(), data.getCiv(), getRewardRep(data));

				// data.setChatStack( "I knew I could count on you, " + data.getPlayer().getName() + "." );
				this.setData(data);
				
				in.addAll(getRewardItems(data));
				return in;
			}
			
			int count = countEntities(data);
			
			/*
			
			quests.enemy_encampment.wasnt_you=I hired mercenaries to do the job, the task could not wait.
			quests.enemy_encampment.one_left=My scouts report there is still a bandit remaining!
			quests.enemy_encampment.some_left=My scouts report there are %i bandits remaining!
			
			
			*/
			if ( kills > 1 && count > 0 && count + kills < minKillsRequired )
			{
				data.setChatStack("Well done. It seems you had help, however?");
				this.setData(data);
				in.addAll(getRewardItems(data));
				return in;
			}
			
			if ( kills < 1)
			{
				data.setChatStack( "Do you take me for a fool? You haven't killed any bandits yet!" );
				this.setData(data);
				// data.getPlayer().closeScreen();
				return null;
			}
			
			if ( kills > 0 )
			{
				data.setChatStack( "My scouts report the bandits have not yet been done away with!" );
				this.setData(data);
				// data.getPlayer().closeScreen();
				return null;
			}
			
			return null;
		}
		
		CivilizationHandlers.adjustPlayerRep(data.getPlayer(), data.getCiv(), getRewardRep(data));

		data.setChatStack( "I knew I could count on you, " + data.getPlayer().getName() + "." );
		this.setData(data);
		in.addAll(getRewardItems(data));
		return in;
	}
	
	private int countEntities(final QuestData data)
	{
		Predicate<EntitySentry> filter = new Predicate<EntitySentry>()
		{
			@Override
			public boolean apply( EntitySentry entity )
			{
				return entity.getTags().contains("encampment_quest") && entity.getTags().contains(data.getQuestId().toString());
			}
		};
		return data.getPlayer().world.getEntitiesWithinAABB(EntitySentry.class, new AxisAlignedBB(getSpawnPosition(data)).expand(96, 64, 96), filter).size();
	}

	@Override
	public List<ItemStack> reject(QuestData data, List<ItemStack> in)
	{
		data.setChatStack( "My guards lack your expertise in combat, " + data.getPlayer().getName() + ". But, I understand you have other matters to attend to.");
		this.setData(data);
		data.getPlayer().closeScreen();
		return in;
	}

	@Override
	public List<ItemStack> accept(QuestData data, List<ItemStack> in)
	{
		try
		{
			BlockPos pos = searchForSuitableLocation(data, 400, 20);
			int tries = 3;
			while ( tries > 0 && !buildHut(data, pos) )
			{
				tries--;
				pos = searchForSuitableLocation(data, 400, 20);
			}
			setSpawnPosition(data, pos);
			ItemStack itemstack = ItemMapCentered.setupNewMap(data.getPlayer().world, (double)pos.getX(), (double)pos.getZ(), (byte)3, true, true);
			ItemMapCentered.renderBiomePreviewMap(data.getPlayer().world, itemstack);
			MapData.addTargetDecoration(itemstack, pos, "+", MapDecoration.Type.TARGET_POINT);
			itemstack.setTranslatableName("§lMap to Bandit encampment§r");
			itemstack.setStackDisplayName("§lMap to Bandit encampment§r");
			addToroSpawner(data, data.getPlayer().getEntityWorld(), getSpawnPosition(data), getEnemyType(data));
			in.add(itemstack);
			//if (!data.getPlayer().world.isRemote)
			{
				switch (data.getPlayer().world.rand.nextInt(3))
				{
					case 0:{data.setChatStack( "Bandits have established a camp not far from here. Wipe them out before they prepare their raid on our village, " + data.getPlayer().getName() + ".");break;}
					case 1:{data.setChatStack( "My scouts have discovered a bandit encampment nearby. Put an end to them before they reach us!");break;}
					case 2:{data.setChatStack( "Bandits have set up an outpost and are attacking one of our trade routes. I can trust you to take care of this matter, " + data.getPlayer().getName() + ".");break;}
				}
				this.setData(data);
			}
		}
		catch (Exception e)
		{
			reject(data,in);
		}
		return in;
	}
	
	@Override
	public BlockPos randomLocation(QuestData data, Random rand, int range, boolean force, int occupiedRange)
	{
		BlockPos pos = super.randomLocation(data, rand, range, force, occupiedRange);
		if (force)
		{
			return pos;
		}
		if ( pos == null )
		{
			return null;
		}
		if ( getLocalMinimum(data, pos) )
		{
			return pos;
		}
		else
		{
			return null;
		}
	}

	public boolean getLocalMinimum(QuestData data, BlockPos pos)
	{
		int w = 10;
		int max = 0, min = 256;
		int y;
		for (int x = -w; x <= w; x++)
		{
			for (int z = -w; z <= w; z++)
			{
				y = findSurface(data.getPlayer().world, pos.getX() + x, pos.getZ() + z, true).getY();
				max = Math.max(y, max);
				min = Math.min(y, min);
			}
		}
		if (max - min > 6)
		{
			return false;
		}
		return true;
	}
	
	private boolean buildHut(QuestData data, BlockPos pos)
	{
		World world = data.getPlayer().getEntityWorld();
		if (pos == null)
		{
			return false;
		}
		int w = hutHalfWidth;

		BlockPos pointer;
		IBlockState block;

		for (int x = -w; x <= w; x++)
		{
			for (int y = -3; y <= w; y++)
			{
				for (int z = -w; z <= w; z++)
				{
					pointer = pos.add(x, y, z);

					block = world.getBlockState(pointer);

					if (isGroundBlock(block))
					{
						continue;
					}
					
					if ( y < 0 )
					{
						world.setBlockState(pointer, Blocks.DIRT.getDefaultState());
					}
					
					if (y + Math.abs(z) == w)
					{
						if (x % 2 == 0)
						{
							world.setBlockState(pointer, Blocks.WOOL.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.RED));
						}
						else if (x % 4 == 0)
						{
							world.setBlockState(pointer, Blocks.WOOL.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.YELLOW));
						}
						else
						{
							world.setBlockState(pointer, Blocks.WOOL.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.BLACK));
						}
					}
					else if (z == 0 && (x == w || x == -w))
					{
						world.setBlockState(pointer, Blocks.DARK_OAK_FENCE.getDefaultState());
					}
					else if (x == 0 && y == 0 && z == 0)
					{
						// chest
						pointer = pos.add(x+w/2, y, z-1);
						world.setBlockState(pointer, Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.EAST));
						TileEntity chest = world.getTileEntity(pointer);
						((TileEntityChest) chest).setLootTable(LootTableList.CHESTS_VILLAGE_BLACKSMITH, world.rand.nextLong());

						// fire
						pointer = pos.add(x+w*2, 0, z);
						pointer = findSurface(world,pointer.getX(),pointer.getZ(),true);
						
						world.setBlockState(pointer.down(2), Blocks.MAGMA.getDefaultState());
						world.setBlockState(pointer.down(), Blocks.MAGMA.getDefaultState());
						world.setBlockState(pointer, Blocks.FIRE.getDefaultState());
						
						// fireplace
						
						world.setBlockState(pointer.add(-1,0,0), Blocks.STONE_SLAB.getDefaultState().withProperty(BlockSlab.HALF, EnumBlockHalf.BOTTOM));
						world.setBlockState(pointer.add(1,0,0), Blocks.STONE_SLAB.getDefaultState().withProperty(BlockSlab.HALF, EnumBlockHalf.BOTTOM));
						world.setBlockState(pointer.add(0,0,-1), Blocks.STONE_SLAB.getDefaultState().withProperty(BlockSlab.HALF, EnumBlockHalf.BOTTOM));
						world.setBlockState(pointer.add(0,0,1), Blocks.STONE_SLAB.getDefaultState().withProperty(BlockSlab.HALF, EnumBlockHalf.BOTTOM));
						world.setBlockState(pointer.add(-1,-1,0), Blocks.COBBLESTONE.getDefaultState());
						world.setBlockState(pointer.add(1,-1,0), Blocks.COBBLESTONE.getDefaultState());
						world.setBlockState(pointer.add(0,-1,-1), Blocks.COBBLESTONE.getDefaultState());
						world.setBlockState(pointer.add(0,-1,1), Blocks.COBBLESTONE.getDefaultState());
						
						// log
						pointer = pos.add(x+w*2+3, y, z+3);
						pointer = findSurface(world,pointer.getX(),pointer.getZ(),true);
						world.setBlockState(pointer, Blocks.LOG.getDefaultState());
						// world.setBlockState(pointer.down(), Blocks.LOG.getDefaultState());
						
						// log
						pointer = pos.add(x+w*2+2, y, z-3);
						pointer = findSurface(world,pointer.getX(),pointer.getZ(),true);
						world.setBlockState(pointer, Blocks.LOG.getDefaultState());
						// world.setBlockState(pointer.down(), Blocks.LOG.getDefaultState());
						
						// log
						pointer = pos.add(x+w*2-3, y, z+2);
						pointer = findSurface(world,pointer.getX(),pointer.getZ(),true);
						world.setBlockState(pointer, Blocks.LOG.getDefaultState());
						// world.setBlockState(pointer.down(), Blocks.LOG.getDefaultState());
					}
				}
			}
		}
		return true;
	}

	private void addToroSpawner(QuestData data, World world, BlockPos blockpos, List<String> entities)
	{
		world.setBlockState(blockpos, BlockToroSpawner.INSTANCE.getDefaultState());
		TileEntity tileentity = world.getTileEntity(blockpos);
		if (tileentity instanceof TileEntityToroSpawner)
		{
			TileEntityToroSpawner spawner = (TileEntityToroSpawner) tileentity;
			spawner.setTriggerDistance(64);
			spawner.setEntityIds(entities);
			spawner.setSpawnRadius(16);
//			spawner.setHelmet(new ItemStack(Items.DIAMOND_HELMET));
//			spawner.setBoots(new ItemStack(Items.DIAMOND_BOOTS));
//			spawner.setLeggings(new ItemStack(Items.DIAMOND_LEGGINGS));
//			spawner.setChestplate(new ItemStack(Items.DIAMOND_CHESTPLATE));
			spawner.addEntityTag(data.getQuestId().toString());
			spawner.addEntityTag("encampment_quest");
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
		return "quests.enemy_encampment.title";
	}

	@Override
	public String getDescription(QuestData data) {
		if (data == null) {
			return "";
		}
		StringBuilder s = new StringBuilder();
		s.append("quests.enemy_encampment.description");
		s.append("|").append(getEnemyNames(data));
		if (getSpawnPosition(data) != null)
		{
			s.append("|").append( " at §lLocation:§r [" + getDirections(getProvincePosition(getQuestProvince(data)), getSpawnPosition(data)) + "]\n\n" );
		}
		else
		{
			s.append("|").append("\n\n");
		}
		s.append("|").append(listItems(getRewardItems(data))  + "\n" );
		s.append("|").append(getRewardRep(data));
		return s.toString();
	}

	private String getEnemyNames(QuestData data) {
		String name = getEnemyType(data).get(0);
		try {
			Entity entity = TileEntityToroSpawner.getEntityForId(data.getPlayer().world, name);
			return entity.getName();
		} catch (Exception e) {
			System.out.println("failed to get name of entity [" + name + "] : " + e.getMessage());
			return "unknown enemy";
		}
	}


	@Override
	public QuestData generateQuestFor(EntityPlayer player, Province province)
	{
		QuestData data = new QuestData();
		data.setCiv(province.civilization);
		data.setPlayer(player);
		data.setProvinceId(province.id);
		data.setQuestId(UUID.randomUUID());
		data.setQuestType(ID);
		data.setCompleted(false);
		chooseEnemyType(data);
		
		int em = 9;
		if ( PlayerCivilizationCapabilityImpl.get(player).getReputation(province.civilization) >= 3000 )
		{
			em *= 2;
		}
		int rep = em*2;
		
		List<ItemStack> reward = new ArrayList<ItemStack>(1);
		reward.add(new ItemStack(Items.EMERALD, em));
		setRewardRep(data, rep);
		setRewardItems(data, reward);
		this.setData(data);
		return data;
	}

	

	public static Integer getKills(QuestData data)
	{
		return coalesce(i(data.getiData().get("kills")), 0);
	}

	private static Integer coalesce(Integer i, int j) {
		if (i == null) {
			return j;
		}
		return i;
	}

	public static void incrementKills(QuestData data)
	{
		data.getiData().put("kills", getKills(data) + 1);
		data.getPlayer().sendStatusMessage( new TextComponentString(MathHelper.clamp(getKills(data), 0, minKillsRequired)+"/"+minKillsRequired), true);
	}

	@SubscribeEvent
	public void checkkills(LivingDeathEvent event)
	{
		EntityLivingBase victum = (EntityLivingBase) event.getEntity();

		if (!victum.getTags().contains("encampment_quest"))
		{
			return;
		}
		
		EntityPlayer player = null;
		DamageSource source = event.getSource();
		
		if (source.getTrueSource() instanceof EntityPlayer)
		{
			player = (EntityPlayer) source.getTrueSource();
		}
		else
		{
			return;
		}

		Set<QuestData> quests = PlayerCivilizationCapabilityImpl.get(player).getCurrentQuests();
		for (QuestData data : quests)
		{
			if (!data.getCompleted() && ID == data.getQuestType() && victum.getTags().contains(data.getQuestId().toString()))
			{
				incrementKills(data);
				if (getKills(data) >= minKillsRequired )
				{
					data.setCompleted(true);
					chatCompletedQuest(data);
				}
				return;
			}
		}
	}
	
	
	
	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	
	private void chooseEnemyType(QuestData data)
	{
		List<String> enemies = new ArrayList<String>();
		if ( rand.nextBoolean() )
		{
			for (int i = 0; i < mobCount; i++)
			{
				enemies.add("toroquest:toroquest_sentry");
			}
		}
		else
		{
			for (int i = 0; i < mobCount; i++)
			{
				enemies.add("toroquest:toroquest_orc");
			}
		}
		setEnemyType(data, enemies);
	}

	private List<String> getEnemyType(QuestData data)
	{
		List<String> enemies = new ArrayList<String>();
		NBTTagCompound c = getCustomNbtTag(data);
		try
		{
			NBTTagList list = (NBTTagList) c.getTag("encampment_quest");
			for (int i = 0; i < list.tagCount(); i++)
			{
				enemies.add(list.getStringTagAt(i));
			}
			return enemies;
		}
		catch (Exception e)
		{
			System.out.println("Failed to load enemy types: " + e.getMessage());
			return getDefaultEnemies(data);
		}
	}

	private List<String> getDefaultEnemies(QuestData data)
	{
		List<String> entity = new ArrayList<String>();
		if ( rand.nextBoolean() )
		{
			for (int i = 0; i < 9; i++)
			{
				entity.add("toroquest:toroquest_sentry");
			}
		}
		else
		{
			for (int i = 0; i < 9; i++)
			{
				entity.add("toroquest:toroquest_orc");
			}
		}
		return entity;
	}

	private void setEnemyType(QuestData data, List<String> enemies)
	{
		NBTTagCompound c = getCustomNbtTag(data);
		NBTTagList list = new NBTTagList();
		for (String enemy : enemies)
		{
			list.appendTag(new NBTTagString(enemy));
		}
		c.setTag("encampment_quest", list);
	}
	
}
