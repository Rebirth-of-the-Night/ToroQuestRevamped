package net.torocraft.toroquest.generation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockCarpet;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockNetherBrick;
import net.minecraft.block.BlockQuartz;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockSlab.EnumBlockHalf;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockStairs.EnumHalf;
import net.minecraft.block.BlockStone.EnumType;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.storage.loot.LootTableList;
import net.torocraft.toroquest.block.BlockToroSpawner;
import net.torocraft.toroquest.block.TileEntityToroSpawner;
import net.torocraft.toroquest.entities.EntityBanditLord;
import net.torocraft.toroquest.entities.EntityGraveTitan;

public class ThroneRoomGenerator extends WorldGenerator {	

	protected final IBlockState stone = Blocks.STONE.getDefaultState();
	protected final IBlockState andesite = Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, EnumType.DIORITE_SMOOTH);
	protected final IBlockState diorite = Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, EnumType.ANDESITE_SMOOTH);
	protected final IBlockState ironBars = Blocks.IRON_BARS.getDefaultState();

	//protected final IBlockState stoneBrickSlabs = Blocks.STONE_SLAB.getDefaultState().withProperty(BlockStoneSlab.VARIANT, EnumType.SMOOTHBRICK);
	protected final IBlockState stoneBrick = Blocks.STONEBRICK.getDefaultState();
	protected final IBlockState stoneBrickChiseled = Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CHISELED);

	protected final IBlockState air = Blocks.AIR.getDefaultState();
	protected final IBlockState goldBlock = Blocks.QUARTZ_BLOCK.getDefaultState();
	protected final IBlockState redstoneBlock = Blocks.QUARTZ_BLOCK.getDefaultState().withProperty(BlockQuartz.VARIANT, BlockQuartz.EnumType.LINES_Y);
	protected final IBlockState lava = Blocks.FLOWING_LAVA.getDefaultState();
	protected final IBlockState redCarpet = Blocks.CARPET.getDefaultState().withProperty(BlockCarpet.COLOR, EnumDyeColor.RED);
	protected final IBlockState blackCarpet = Blocks.CARPET.getDefaultState().withProperty(BlockCarpet.COLOR, EnumDyeColor.BLACK);
	protected final IBlockState stoneBrickStairsN = Blocks.STONE_BRICK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH);
	protected final IBlockState stoneBrickStairsS = Blocks.STONE_BRICK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.SOUTH);
	protected final IBlockState stoneBrickStairsE = Blocks.STONE_BRICK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.EAST);
	protected final IBlockState stoneBrickStairsW = Blocks.STONE_BRICK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.WEST);
	protected final IBlockState doubleSlab = Blocks.DOUBLE_STONE_SLAB.getDefaultState();
	private int width = 30;
	private int length = 64;
	private int height = 14;

	@Override
	public boolean generate(World worldIn, Random rand, BlockPos origin)
	{
		this.buildThroneRoom(worldIn, rand, origin);
		this.addToroSpawner(worldIn, origin.add(15, 0, 75), getDefaultEnemies());
		return true;
	}

	private void buildThroneRoom(World world, Random rand, BlockPos startPos)
	{
		for (int y = 0; y <= height; y++)
		{
			for (int x = 0; x <= width; x++)
			{
				for (int z = 0; z <= length; z++)
				{
					IBlockState currentBlock = air;
					if (isThroneSideLavaDitch(y, x, z))
					{
						if ( x == 4 || x == width - 4 )
						{
							world.setBlockState(startPos.add(x, y, z), Blocks.DOUBLE_STONE_SLAB.getDefaultState());
						}
						else
						{
							world.setBlockState(startPos.add(x, y, z), lava);
						}
						continue;
					}
					else if (isFloor(y, x, z))
					{
						if ( (x + z) % 2 == 0 )
						{
							currentBlock = andesite;
						}
						else
						{
							currentBlock = diorite;
						}
						world.setBlockState(startPos.add(x, y, z), currentBlock);
						continue;
					}
					else if (isWall(y, x, z))
					{
						if ( y % 4 == 0 )
						{
							if ( y == 12 && ( ( x == 0 || x == width ) && z % 4 == 0 ) )
							{
								currentBlock = lava;world.setBlockState(startPos.add(x, y, z), currentBlock);
							}
							else
							{
								currentBlock = ironBars;world.setBlockState(startPos.add(x, y, z), currentBlock);
							}
							continue;
						}
						else
						{
							currentBlock = stoneBrick;world.setBlockState(startPos.add(x, y, z), currentBlock);
							continue;
						}
					}
					else if ( isThroneCarpet(y, x, z) )
					{
						currentBlock = redCarpet;world.setBlockState(startPos.add(x, y, z), currentBlock);
						continue;
					}
					else if (isEntranceStoneBrick(y, x, z))
					{
						currentBlock = stoneBrick;world.setBlockState(startPos.add(x, y, z), currentBlock);
						continue;
					}
					else if (isEntranceOpening(y, x, z)) {
						currentBlock = air;world.setBlockState(startPos.add(x, y, z), currentBlock);
						continue;
					}
					
					else if (z == 0 && y == 7 && x == 13) {
						currentBlock = stoneBrickStairsW.withProperty(BlockStairs.HALF, EnumHalf.TOP);world.setBlockState(startPos.add(x, y, z), currentBlock);
						continue;
					}
					
					else if (z == 0 && y == 7 && x == 17)
					{
						currentBlock = stoneBrickStairsE.withProperty(BlockStairs.HALF, EnumHalf.TOP);world.setBlockState(startPos.add(x, y, z), currentBlock);
						continue;
					}
					else if ( isEntranceCarpet(y, x, z) )
					{
						if ( x == 15 && y == 4 )
						{
							currentBlock = Blocks.TRAPPED_CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.SOUTH);world.setBlockState(startPos.add(x, y, z), currentBlock);
							TileEntity tileentity = world.getTileEntity( new BlockPos(x, y, z) );
							if (tileentity instanceof TileEntityChest)
							{
								if ( rand.nextBoolean() )
								{
									((TileEntityChest) tileentity).setLootTable(LootTableList.CHESTS_STRONGHOLD_CORRIDOR, world.rand.nextLong());
								}
								else
								{
									((TileEntityChest) tileentity).setLootTable(LootTableList.CHESTS_STRONGHOLD_CROSSING, world.rand.nextLong());
								}
								if ( rand.nextBoolean() ) ((TileEntityChest) tileentity).setInventorySlotContents(world.rand.nextInt(20)+2, new ItemStack(Items.GOLD_INGOT, 5));
								if ( rand.nextBoolean() ) ((TileEntityChest) tileentity).setInventorySlotContents(world.rand.nextInt(20)+2, new ItemStack(Items.GOLD_INGOT, 5));
							}
							continue;
						}
						currentBlock = redCarpet;world.setBlockState(startPos.add(x, y, z), currentBlock);
						continue;
					}
					else if (isEntrancePlatform(y, x, z))
					{
						if ( x >= 13 && x <= 15 )
						{
							if ( y == 3 )
							{
								currentBlock = Blocks.TNT.getDefaultState();world.setBlockState(startPos.add(x, y, z), currentBlock);
								continue;
							}
							else if ( y == 2 )
							{
								currentBlock = Blocks.TNT.getDefaultState();world.setBlockState(startPos.add(x, y, z), currentBlock);
								continue;
							}
							else if ( y == 1 )
							{
								currentBlock = Blocks.TNT.getDefaultState();world.setBlockState(startPos.add(x, y, z), currentBlock);
								continue;
							}
						}
						currentBlock = stone;world.setBlockState(startPos.add(x, y, z), currentBlock);
						continue;
					}
					else if (isEntranceStairsEast(y, x, z))
					{
						currentBlock = stoneBrickStairsE;world.setBlockState(startPos.add(x, y, z), currentBlock);
						continue;
					}
					else if (isEntranceStairsWest(y, x, z))
					{
						currentBlock = stoneBrickStairsW;world.setBlockState(startPos.add(x, y, z), currentBlock);
						continue;
					}
					else if (isEntranceStairsNorth(y, x, z))
					{
						currentBlock = stoneBrickStairsN;world.setBlockState(startPos.add(x, y, z), currentBlock);
						continue;
					}
					
					else if (isThronePlatform(y, x, z)) {
						currentBlock = stone;world.setBlockState(startPos.add(x, y, z), currentBlock);
						continue;
					}
					else if ( isAisleCarpet(y, x, z) )
					{
						if ( x == 13 || x == 17 )
						{
							currentBlock = blackCarpet;world.setBlockState(startPos.add(x, y, z), currentBlock);
						}
						else
						{
							currentBlock = redCarpet;world.setBlockState(startPos.add(x, y, z), currentBlock);
						}
						continue;
					}
					else if (isThroneArms(y, x, z)) {
						currentBlock = goldBlock;world.setBlockState(startPos.add(x, y, z), currentBlock);
						continue;
					}
					
					else if (isBackOfThrone(y, x, z))
					{
						if ( y == 10 )
						{
							if ( x == 15 )
							{
								currentBlock = Blocks.SEA_LANTERN.getDefaultState();world.setBlockState(startPos.add(x, y, z), currentBlock);
							}
							else if ( x == 14 )
							{
								currentBlock = Blocks.QUARTZ_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.EAST);world.setBlockState(startPos.add(x, y, z), currentBlock);
							}
							else if ( x == 16 )
							{
								currentBlock = Blocks.QUARTZ_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.WEST);world.setBlockState(startPos.add(x, y, z), currentBlock);
							}
						}
						else
						{
							currentBlock = redstoneBlock;world.setBlockState(startPos.add(x, y, z), currentBlock);
						}
						continue;
					}
					
					else if (isThroneStairsNorth(y, x, z)) {
						currentBlock = stoneBrickStairsN;world.setBlockState(startPos.add(x, y, z), currentBlock);
						continue;
					}
					
					else if (isThroneStairsEast(y, x, z)) {
						currentBlock = stoneBrickStairsE;world.setBlockState(startPos.add(x, y, z), currentBlock);
						continue;
					}
					
					else if (isThroneStairsWest(y, x, z)) {
						currentBlock = stoneBrickStairsW;world.setBlockState(startPos.add(x, y, z), currentBlock);
						continue;
					}
					
					else if (isThroneStairsSouth(y, x, z))
					{
						currentBlock = stoneBrickStairsS;world.setBlockState(startPos.add(x, y, z), currentBlock);
						continue;
					}
					else if (isInTorchGrid(y, x, z))
					{
						if ( ((z - 2) % 12 == 0) )
						{
							currentBlock = doubleSlab;
							for ( int yy = 0; yy < height; yy++ )
							{
								world.setBlockState(startPos.add(x, y+yy, z), currentBlock);
								world.setBlockState(startPos.add(x-1, y+yy, z-1), currentBlock);
								world.setBlockState(startPos.add(x-1, y+yy, z), currentBlock);
								world.setBlockState(startPos.add(x-1, y+yy, z+1), currentBlock);
								world.setBlockState(startPos.add(x, y+yy, z+1), currentBlock);
								world.setBlockState(startPos.add(x, y+yy, z-1), currentBlock);
								world.setBlockState(startPos.add(x+1, y+yy, z-1), currentBlock);
								world.setBlockState(startPos.add(x+1, y+yy, z), currentBlock);
								world.setBlockState(startPos.add(x+1, y+yy, z+1), currentBlock);
							}
						}
						else
						{
							world.setBlockState(startPos.add(x, y-1, z), Blocks.MAGMA.getDefaultState());
							world.setBlockState(startPos.add(x-1, y, z-1), blockSlab);
							world.setBlockState(startPos.add(x-1, y, z), blockSlab);
							world.setBlockState(startPos.add(x-1, y, z+1), blockSlab);
							world.setBlockState(startPos.add(x, y, z+1), blockSlab);
							world.setBlockState(startPos.add(x, y, z-1), blockSlab);
							world.setBlockState(startPos.add(x+1, y, z-1), blockSlab);
							world.setBlockState(startPos.add(x+1, y, z), blockSlab);
							world.setBlockState(startPos.add(x+1, y, z+1), blockSlab);
							world.setBlockState(startPos.add(x, y, z), blockFire);
						}
						continue;
					}
					
					if ( world.getBlockState(startPos.add(x, y, z)) == doubleSlab || world.getBlockState(startPos.add(x, y, z)) == Blocks.STONE_SLAB.getDefaultState() || world.getBlockState(startPos.add(x, y, z)) == blockFire ) { continue; }

					world.setBlockState(startPos.add(x, y, z), currentBlock);
				}
			}
		}
	}

	IBlockState blockSlab = Blocks.STONE_SLAB.getDefaultState().withProperty(BlockSlab.HALF, EnumBlockHalf.BOTTOM);
	IBlockState blockFire = Blocks.FIRE.getDefaultState();

	private boolean isAisleCarpet(int y, int x, int z)
	{
		return y == 1 && (x >= 13 && x <= 17) && z > 6 && z < length - 10;
	}

	private boolean isInTorchGrid(int y, int x, int z) {
		return y == 1 && ( x == 8 || x == width - 8 ) && ((z - 2) % 6 == 0);
	}

	private boolean isFloor(int y, int x, int z) {
		return (y == 0 || y == height);
	}
	
	private boolean isWall(int y, int x, int z) {
		return (x == 0 || x == width) || (z == 0 || z == length);
	}

	private boolean isEntranceStoneBrick(int y, int x, int z) {
		return (z == 0 && (y == 3 || y == 8) & x >= 12 && x <= 18) || (z == 0 && (x == 12 || x == 18) && y >= 4 && y <= 7) || (z == 0 && (x == 13 || x == 17) && y == 4);
	}

	private boolean isEntranceOpening(int y, int x, int z) {
		return z == 0 && y >= 4 && y <= 7 && x >= 14 && x <= 16;
	}

	private boolean isThroneStairsSouth(int y, int x, int z) {
		return (y == 1 && z == length - 10 && x >= 12 && x <= 18) || (y == 2 && z == length - 9 && x >= 13 && x <= 17) || (y == 3 && z == length - 8 && x >= 14 && x <= 16) || (y == 4 && z == length - 5 && x >= 14 && x <= 16) || (y == 5 && z == length - 4 && x >= 14 && x <= 16);
	}

	private boolean isThroneStairsWest(int y, int x, int z) {
		return (y == 1 && x == 19 && z <= length - 6 && z >= length - 10) || (y == 2 && x == 18 && z <= length - 7 && z >= length - 9) || (y == 3 && x == 17 && z <= length - 7 && z >= length - 8);
	}

	private boolean isThroneStairsEast(int y, int x, int z) {
		return (y == 1 && x == 11 && z <= length - 6 && z >= length - 10) || (y == 2 && x == 12 && z <= length - 7 && z >= length - 9) || (y == 3 && x == 13 && z <= length - 7 && z >= length - 8);
	}

	private boolean isThroneStairsNorth(int y, int x, int z) {
		return (y == 1 && (x == 11 || x == 12 || x == 18 || x == 19) && z == length - 5) || (y == 2 && (x == 12 || x == 18) && z == length - 6);
	}

	private boolean isThroneSideLavaDitch(int y, int x, int z)
	{
		return y == 0 && (x < 5 || x > width - 5);
	}
	
	private boolean isBackOfThrone(int y, int x, int z) {
		return (y >= 6 && y <= 10) && x > 13 && x < 17 && z == length - 1;
	}

	private boolean isEntranceCarpet(int y, int x, int z) {
		return (y == 2 && x > 13 && x < 17 && z == 5) || (y == 3 && z == 3 && x > 13 && x < 17) || (y == 4 && z == 1 && x > 13 && x < 17);
	}

	private boolean isThroneCarpet(int y, int x, int z) {
		return (y == 6 && x > 13 && x < 17 && (z == length - 3 || z == length - 2)) || (y == 4 && x > 13 && x < 17 && (z == length - 6 || z == length - 7));
	}

	private boolean isThroneArms(int y, int x, int z) {
		return (y == 3 && (x == 13 || x == 17) && z == length - 6) || (y == 4 && (x == 13 || x == 17) && z == length - 5) || (y == 5 && (x == 13 || x == 17) && z == length -4) || 
				(y == 6 && (x == 13 || x == 17) && (z == length - 3 || z == length - 2)) ||	(y == 7 && (x == 13 || x == 17) && z == length - 1);
	}

	private boolean isThronePlatform(int y, int x, int z) {
		return (y == 1 && (x >= 13 && x <= 17) && z >= length - 9) || (y == 1 && (x == 12 || x == 18) && z >= length -9 && z <= length - 6) || (y == 2 && (x >= 13 && x <= 17) && z >= length -8) || (y == 3 && x >= 13 && x <= 17 && z >= length - 5)
				|| (y == 3 && x >= 14 && x <= 16 && z >= length - 7) || (y == 4 && x >= 13 && x <= 17 && z >= length - 4) || (y == 5 && x >= 13 && x <= 17 && z >= length - 3) || (y == 6 && (x == 13 || x == 17) && z == length - 1);
	}

	private boolean isEntranceStairsNorth(int y, int x, int z) {
		return (y == 1 && z == 6 && x >= 10 && x <= width - 10) || (y == 2 && z == 4 && x >= 12 && x <= width - 12) || (y == 3 && z == 2 && x >= 14 && x <= width - 14);
	}

	private boolean isEntranceStairsWest(int y, int x, int z) {
		return (y == 1 && x == width - 9 && z > 0 && z <= 6) || (y == 2 && x == width - 11 && z > 0 && z <= 4) || (y == 3 && x == width - 13 && z > 0 && z <= 2);
	}

	private boolean isEntranceStairsEast(int y, int x, int z) {
		return (y == 1 && x == 9 && z > 0 && z <= 6) || (y == 2 && x == 11 && z > 0 && z <= 4) || (y == 3 && x == 13 && z > 0 && z <= 2);
	}

	private boolean isEntrancePlatform(int y, int x, int z) {
		return (y == 1 && (x >= 10 && x <= width - 10) && z <= 5) || (y == 2 && (x >= 12 && x <= width - 12) && z <= 3) || (y == 3 && (x >= 14 && x <= width - 14) && z == 1);
	}

	private void addToroSpawner( World world, BlockPos blockpos, List<String> entities)
	{
		blockpos = blockpos.up(2);
		world.setBlockState(blockpos, BlockToroSpawner.INSTANCE.getDefaultState());
		TileEntity tileentity = world.getTileEntity(blockpos);
		if (tileentity instanceof TileEntityToroSpawner)
		{
			TileEntityToroSpawner spawner = (TileEntityToroSpawner) tileentity;
			spawner.setTriggerDistance(80);
			spawner.setEntityIds(entities);
			spawner.setSpawnRadius(16);
		}
		else
		{
			System.out.println("tile entity is missing");
			EntityBanditLord e = new EntityBanditLord(world);
			e.setPositionAndUpdate(blockpos.getX()+0.5,blockpos.getY()+0.5,blockpos.getZ()+0.5);
			world.spawnEntity(e);
		}
	}

	private List<String> getDefaultEnemies()
	{
		List<String> entity = new ArrayList<String>();
		entity.add("toroquest:toroquest_bandit_lord");
		for ( int i = 8; i > 0; i-- )
		{
			entity.add("toroquest:toroquest_sentry");
		}
		return entity;
	}

}
