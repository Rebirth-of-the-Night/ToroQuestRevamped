package net.torocraft.toroquest.generation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.storage.loot.LootTableList;
import net.torocraft.toroquest.block.BlockToroSpawner;
import net.torocraft.toroquest.block.TileEntityToroSpawner;
import net.torocraft.toroquest.civilization.CivilizationHandlers;

public class BanditFortressGenerator extends WorldGenerator
{
	

	@Override
	public boolean generate(World world, Random rand, BlockPos pos)
	{
		if (pos == null)
		{
			return false;
		}
		
		clearTrees( world, pos, 32 );
		
		
		
		return true;
	}

	protected void placeChest(World world, BlockPos placementPos)
	{
		setBlockAndNotifyAdequately(world, placementPos, Blocks.CHEST.getDefaultState());
		TileEntity tileentity = world.getTileEntity(placementPos);
		if (tileentity instanceof TileEntityChest)
		{
			if ( rand.nextBoolean() ) ((TileEntityChest) tileentity).setLootTable(LootTableList.CHESTS_STRONGHOLD_CORRIDOR, world.rand.nextLong());
			else ((TileEntityChest) tileentity).setLootTable(LootTableList.CHESTS_STRONGHOLD_CROSSING, world.rand.nextLong());

		}
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
		entity.add("toroquest:toroquest_sentry");
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
	
	public boolean isAir(IBlockState blockState)
	{
		return blockState.getBlock() == Blocks.AIR;
	}

	private boolean isLiquid(IBlockState blockState) {
		return blockState.getBlock() == Blocks.WATER || blockState.getBlock() == Blocks.LAVA;
	}

	
	
	// walls
	public void clearTrees( World world, BlockPos start, int radius )
	{
		double x = start.getX();
		double z = start.getZ();
		for ( int i = 3; i > 0; i-- )
		{
			for ( int xx = -radius; xx < radius; xx++ )
			{
				for ( int zz = -radius; zz < radius; zz++ )
				{
					int distFromCenter = (int)(MathHelper.sqrt((xx*xx+zz*zz)));
					if ( radius == (distFromCenter) + 1 )
					{
						BlockPos pos = findSurface(world,(int)(x+xx),(int)(z+zz),true);
						if ( pos != null )
						{
							if ( i == 2 || rand.nextInt(3) != 0 )
							{
								placePillar( world, pos );
							}
						}
					}
				}
			}
			radius++;
		}
	}
	
	public BlockPos findSurface(World world, int x, int z, boolean force)
	{
		BlockPos pos = new BlockPos(x, world.getActualHeight(), z);
		IBlockState blockState;
		while (pos.getY() > 0)
		{
			blockState = world.getBlockState(pos);
			if (!force && ( isLiquid(blockState) || CivilizationHandlers.isStructureBlock(blockState) ))
			{
				return null;
			}
			else if ( blockState.getBlock() instanceof BlockLog )
			{
				
			}
			else if (isGroundBlock(blockState))
			{
				break;
			}
			pos = pos.down();
		}
		return pos.up();
	}
	
	protected boolean isGroundBlock(IBlockState blockState)
	{
		if (blockState.getBlock() instanceof BlockLeaves || blockState.getBlock() instanceof BlockLog || blockState.getBlock() instanceof BlockBush || blockState.getBlock() instanceof BlockSlab )
		{
			return false;
		}
		return blockState.isOpaqueCube();
	}
	Random rand = new Random();
	private void placePillar(World world, BlockPos pos)
	{
		int height = rand.nextInt(2)+rand.nextInt(2)+rand.nextInt(2)+rand.nextInt(2)+rand.nextInt(2)+rand.nextInt(2)+2;
		pos = pos.up(height);
		for ( int i = 0; i < 16+height; i++ )
		{
			pos = pos.down();
			setBlockAndNotifyAdequately(world, pos, Blocks.LOG.getDefaultState());
		}
	}

	public void placeBlock(World world, BlockPos pos, net.minecraft.block.Block block)
	{
		world.setBlockState(pos, block.getDefaultState());
	}

}
