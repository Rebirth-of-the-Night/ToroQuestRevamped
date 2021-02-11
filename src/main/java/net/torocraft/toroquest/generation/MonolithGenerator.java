package net.torocraft.toroquest.generation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.storage.loot.LootTableList;
import net.torocraft.toroquest.block.BlockToroSpawner;
import net.torocraft.toroquest.block.TileEntityToroSpawner;

public class MonolithGenerator extends WorldGenerator
{

	protected IBlockState getObsidianBlock()
	{
		return Blocks.OBSIDIAN.getDefaultState();
	}

	protected IBlockState getEyeBlock()
	{
		return Blocks.SEA_LANTERN.getDefaultState();
	}
	
	

	@Override
	public boolean generate(World world, Random rand, BlockPos pos)
	{
		if (pos == null)
		{
			return false;
		}
		placeMonolith(world, rand, pos);
		return true;
	}

	private void placeDungeonRoom(World world, int xCenter, int zCenter)
	{
		int halfX = 6;
		int halfZ = 6;
		int height = 6;

		int xMin = xCenter - halfX;
		int zMin = zCenter - halfZ;
		int yMin = 10;

		int xMax = xCenter + halfX;
		int zMax = zCenter + halfZ;
		int yMax = yMin + height;

		IBlockState block;
		BlockPos pos;
		Random rand = new Random();

		for (int y = yMin; y <= yMax; y++) {
			for (int x = xMin; x <= xMax; x++) {
				for (int z = zMin; z <= zMax; z++) {
					if (y == yMax) {
						block = Blocks.LAVA.getDefaultState();
					} else if (x == xMin || x == xMax || z == zMax || z == zMin || y == yMax - 1 || y == yMin) {
						block = Blocks.MOSSY_COBBLESTONE.getDefaultState();
					} else if (y == yMin + 1) {
						block = Blocks.LAVA.getDefaultState();
					} else {
						block = Blocks.AIR.getDefaultState();
					}
					pos = new BlockPos(x, y, z);
					setBlockAndNotifyAdequately(world, pos, block);
				}
			}
		}

		for (int x = xMin + 1; x < xMax; x++) {
			for (int z = zMin + 1; z < zMax; z++) {
				if (rand.nextInt(100) < 3) {
					pos = new BlockPos(x, yMin + 1, z);
					placeChest(world, pos);
				}
			}
		}

		for (int x = xMin + 1; x < xMax; x++) {
			for (int z = zMin + 1; z < zMax; z++) {
				if (rand.nextInt(100) < 3) {
					pos = new BlockPos(x, yMax - 1, z);
					placeSpawner(world, pos);
				}
			}
		}

	}
	protected void placeChest(World world, BlockPos placementPos)
	{
		setBlockAndNotifyAdequately(world, placementPos, Blocks.CHEST.getDefaultState());
		TileEntity tileentity = world.getTileEntity(placementPos);
		if (tileentity instanceof TileEntityChest)
		{
			((TileEntityChest) tileentity).setLootTable(LootTableList.CHESTS_END_CITY_TREASURE, world.rand.nextLong());
		}
	}
	
	

	public void placeSpawner(World world, BlockPos pos)
	{
		setBlockAndNotifyAdequately(world, pos, Blocks.MOB_SPAWNER.getDefaultState());
		TileEntityMobSpawner theSpawner = (TileEntityMobSpawner) world.getTileEntity(pos);
		MobSpawnerBaseLogic logic = theSpawner.getSpawnerBaseLogic();
		logic.setEntityId(new ResourceLocation("magma_cube"));
	}

	private void spawnMonolithEye(World world, BlockPos pos)
	{
		
		addToroSpawner( world, pos, getDefaultEnemies() );
//		EntityMonolithEye e = new EntityMonolithEye(world);
//		e.setRaidLocation(pos.getX(), pos.getY(), pos.getZ());
//		e.setPosition(pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5);
//		world.spawnEntity(e);
	}
	
	private void addToroSpawner( World world, BlockPos blockpos, List<String> entities)
	{
		world.setBlockState(blockpos, BlockToroSpawner.INSTANCE.getDefaultState());
		TileEntity tileentity = world.getTileEntity(blockpos);
		if (tileentity instanceof TileEntityToroSpawner)
		{
			TileEntityToroSpawner spawner = (TileEntityToroSpawner) tileentity;
			spawner.setTriggerDistance(80);
			spawner.setEntityIds(entities);
			spawner.setSpawnRadius(0);
			// spawner.addEntityTag(data.getQuestId().toString());
			// spawner.addEntityTag("capture_fugitives");
		}
		else
		{
			System.out.println("tile entity is missing");
		}
	}

	private List<String> getDefaultEnemies()
	{
		List<String> entity = new ArrayList<String>();
		entity.add("toroquest:toroquest_monolitheye");
		return entity;
	}
	
	private void spawnCrystal(World world, BlockPos pos)
	{
		EntityEnderCrystal e = new EntityEnderCrystal(world);
		e.setPosition(pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5);
		world.spawnEntity(e);
	}
	
	private BlockPos findPillarSurface(World world, BlockPos start)
	{
		IBlockState blockState;
		BlockPos search = new BlockPos(start.getX(), world.getActualHeight(), start.getZ());
		while ( search.getY() > 40 )
		{
			search = search.down();
			blockState = world.getBlockState(search);
			if ( isLiquid(blockState) )
			{
				break;
			}
			if ( (blockState).isOpaqueCube() && !( blockState.getBlock() instanceof BlockLog ) )
			{
				break;
			}
		}
		return search;
	}
	
	public static boolean isAir(IBlockState blockState)
	{
		return blockState.getBlock() == Blocks.AIR;
	}

	private boolean isLiquid(IBlockState blockState) {
		return blockState.getBlock() == Blocks.WATER || blockState.getBlock() == Blocks.LAVA;
	}

	private void placeMonolith(World world, Random rand, BlockPos pos)
	{
		pos = findPillarSurface( world, pos );
		
		placePillar(world, rand, new BlockPos(pos.getX()+38,pos.getY(),pos.getZ()+10));
		placePillar(world, rand, new BlockPos(pos.getX()+30,pos.getY(),pos.getZ()+30));
		placePillar(world, rand, new BlockPos(pos.getX()+10,pos.getY(),pos.getZ()+38));
		
		placePillar(world, rand, new BlockPos(pos.getX()-10,pos.getY(),pos.getZ()+38));
		placePillar(world, rand, new BlockPos(pos.getX()-32,pos.getY(),pos.getZ()+32));
		placePillar(world, rand, new BlockPos(pos.getX()-38,pos.getY(),pos.getZ()+10));
		
		placePillar(world, rand, new BlockPos(pos.getX()-38,pos.getY(),pos.getZ()-10));
		placePillar(world, rand, new BlockPos(pos.getX()-30,pos.getY(),pos.getZ()-30));
		placePillar(world, rand, new BlockPos(pos.getX()-10,pos.getY(),pos.getZ()-38));
		
		placePillar(world, rand, new BlockPos(pos.getX()+10,pos.getY(),pos.getZ()-38));
		placePillar(world, rand, new BlockPos(pos.getX()+30,pos.getY(),pos.getZ()-30));
		placePillar(world, rand, new BlockPos(pos.getX()+38,pos.getY(),pos.getZ()-10));
		
//		int height = 16;
//		pos = pos.up(height);
		spawnMonolithEye(world, pos);
//		for ( int i = 0; i < 16+height; i++ )
//		{
//			pos = pos.down();
//			setBlockAndNotifyAdequately(world, pos, Blocks.OBSIDIAN.getDefaultState());
//			setBlockAndNotifyAdequately(world, pos.add(-1,0,-1), Blocks.OBSIDIAN.getDefaultState());
//			setBlockAndNotifyAdequately(world, pos.add(-1,0,0), Blocks.OBSIDIAN.getDefaultState());
//			setBlockAndNotifyAdequately(world, pos.add(-1,0,1), Blocks.OBSIDIAN.getDefaultState());
//			setBlockAndNotifyAdequately(world, pos.add(0,0,1), Blocks.OBSIDIAN.getDefaultState());
//			setBlockAndNotifyAdequately(world, pos.add(0,0,-1), Blocks.OBSIDIAN.getDefaultState());
//			setBlockAndNotifyAdequately(world, pos.add(1,0,-1), Blocks.OBSIDIAN.getDefaultState());
//			setBlockAndNotifyAdequately(world, pos.add(1,0,0), Blocks.OBSIDIAN.getDefaultState());
//			setBlockAndNotifyAdequately(world, pos.add(1,0,1), Blocks.OBSIDIAN.getDefaultState());
//		}
	}
		
	private void placePillar(World world, Random rand, BlockPos pos)
	{
		pos = findPillarSurface( world, pos );
		int height = rand.nextInt(8)+4;
		pos = pos.up(height);
		if ( !world.isRemote )
		{
			spawnCrystal(world, pos);
		}
		for ( int i = 0; i < 16+height; i++ )
		{
			pos = pos.down();
			setBlockAndNotifyAdequately(world, pos, Blocks.OBSIDIAN.getDefaultState());
		}
	}

	public static void placeBlock(World world, BlockPos pos, net.minecraft.block.Block block)
	{
		world.setBlockState(pos, block.getDefaultState());
	}

}
