package net.torocraft.toroquest.generation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockClay;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockGravel;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockMycelium;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockSandStone;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.torocraft.toroquest.block.BlockToroSpawner;
import net.torocraft.toroquest.block.TileEntityToroSpawner;
import net.torocraft.toroquest.entities.EntitySpiderLord;

public class SpiderLairGenerator extends WorldGenerator
{


	@Override
	public boolean generate(World world, Random rand, BlockPos pos)
	{
//		this.world = world;
//		this.rand = rand;
//
//		this.origin = pos;
		if ( pos == null )
		{
			return false;
		}
		this.createWebs(world, rand, pos);
		//this.addToroSpawner( world, pos, getDefaultEnemies() );
		this.spawnSpiderLord(world, rand, pos);
		return true;
	}
	
	protected void createWebPatch(World world, BlockPos start)
	{
		int radius = 17;
		int x = start.getX();
		int z = start.getZ();
		for ( int xx = -radius/2; xx < radius/2; xx++ )
		{
				for ( int zz = -radius/2; zz < radius/2; zz++ )
				{
					// EQUATION: ( Math.sqrt(Math.pow(Math.abs(xx)+(radius-1)/4, 2) + Math.pow(Math.abs(zz)+(radius-1)/4, 2)) <= (Math.pow((radius-1),2)/Math.sqrt(2)) )
					int distFromCenter = (int)(Math.pow(Math.abs(xx)+4, 2) + Math.pow(Math.abs(zz)+4, 2));
					if ( world.rand.nextInt(distFromCenter) < 64 )
					{
						if ( distFromCenter <= 181 )
						{
							BlockPos pos = new BlockPos(x+xx,0,z+zz);
							pos = this.getSurfacePosition(world, pos);
							if ( pos == null )
							{
								break;
							}
							pos = pos.up();
							world.setBlockState(pos, Blocks.WEB.getDefaultState());
						}
					}
					/*
					if ( distFromCenter <= 181 )
					{
						BlockPos pos = new BlockPos(x+xx,0,z+zz);
						pos = this.getSurfacePositionClearLeaves(world, pos);
						if ( pos == null )
						{
							break;
						}
						if ( world.rand.nextInt(distFromCenter) < 64 )
						{
							pos = pos.up();
							world.setBlockState(pos, Blocks.WEB.getDefaultState());
						}
					}
					*/
				}
		}
	}
	
	protected void createWebs(World world, Random rand, BlockPos origin)
	{
		double x = origin.getX();
    	double z = origin.getZ();
    	
    	int radius = 64;
    	
		for ( int webs = 0; webs < 64; webs++ )
		{
			int xx = (rand.nextInt(radius))*(rand.nextInt(2)*2-1);
			int zz = (rand.nextInt(radius))*(rand.nextInt(2)*2-1);
			BlockPos pos = new BlockPos(new BlockPos(x+xx,0,z+zz));
			this.createWebPatch(world, pos);
		}
	}
	

	private BlockPos getSurfacePosition(World world, BlockPos start)
	{
		IBlockState blockState;
		BlockPos search = new BlockPos(start.getX(), world.getActualHeight()/2, start.getZ());
		while (search.getY() > 0)
		{
			search = search.down();
			blockState = world.getBlockState(search);
			if (isLiquid(blockState))
			{
				break;
			}
			if ((blockState).isOpaqueCube())
			{
				break;
			}
		}
		return search;
	}
	
	/*
	private BlockPos getSurfacePositionClearLeaves(World world, BlockPos start)
	{
		IBlockState blockState;
		BlockPos search = new BlockPos(start.getX(), world.getActualHeight()/2, start.getZ());
		while (search.getY() > 0)
		{
			search = search.down();
			blockState = world.getBlockState(search);
			if (blockState.getBlock() instanceof BlockLeaves )
			{
				world.setBlockState(search, Blocks.AIR.getDefaultState());
			}
			else
			{
				if (isLiquid(blockState))
				{
					break;
				}
				if ((blockState).isOpaqueCube())
				{
					break;
				}
			}
		}
		return search;
	}
	*/

	private boolean isLiquid(IBlockState blockState) {
		return blockState.getBlock() == Blocks.WATER || blockState.getBlock() == Blocks.LAVA;
	}

	private boolean isGroundBlock(IBlockState blockState) {
		Block b = blockState.getBlock();
		if ( b == null )
		{
			return false;
		}
		if ( b instanceof BlockGrass ||  b instanceof BlockDirt ||  b == Blocks.STONE ||  b instanceof BlockSand || b instanceof BlockSnow || b instanceof BlockClay || b instanceof BlockGravel || b instanceof BlockMycelium || b instanceof BlockSand || b instanceof BlockSandStone )
		{
			return blockState.isOpaqueCube();
		}
		return false;
	}


//	private IBlockState randomChest()
//	{
//		int roll = rand.nextInt(4);
//		switch (roll) {
//		case 1:
//			return Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.NORTH);
//		case 2:
//			return Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.SOUTH);
//		case 3:
//			return Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.EAST);
//		default:
//			return Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.WEST);
//		}
//	}

//	protected void addLootToChest()
//	{
//		if (block == null)
//		{
//			return;
//		}
//		if (block.getBlock() == Blocks.CHEST)
//		{
//			TileEntity tileentity = world.getTileEntity(origin.add(x, y, z));
//			if (tileentity instanceof TileEntityChest)
//			{
//				((TileEntityChest) tileentity).setLootTable(LootTableList.CHESTS_END_CITY_TREASURE, world.rand.nextLong());
//			}
//		}
//	}
//
//	private boolean isLootChest() {
//		return y == 1 && rand.nextInt(400) == 0;
//	}


//	protected void placeBlock()
//	{
//		if (block == null)
//		{
//			return;
//		}
//		setBlockAndNotifyAdequately(world, origin.add(x, y, z), block);
//	}

	private void spawnSpiderLord(World world, Random rand, BlockPos origin)
	{
		EntitySpiderLord e = new EntitySpiderLord(world);
		e.setPosition(origin.getX() + 0.5, origin.getY() + 1, origin.getZ() + 0.5);
		e.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(e)), (IEntityLivingData) null);
		e.setRaidLocation(origin.getX(), origin.getZ(), origin.getY());
		world.spawnEntity(e);
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
			spawner.setSpawnRadius(16);
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
		entity.add("toroquest:toroquest_spider_lord");
		return entity;
	}

}
