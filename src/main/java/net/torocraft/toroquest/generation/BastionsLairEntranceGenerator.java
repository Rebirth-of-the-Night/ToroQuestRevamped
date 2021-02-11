package net.torocraft.toroquest.generation;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockClay;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockGravel;
import net.minecraft.block.BlockMycelium;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockSandStone;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BastionsLairEntranceGenerator extends WorldGenerator {

	private final int boxRadius = 6;

	private final int radius = 5;
	private final int radiusSq = 5 * 5;
	private final int halfRadius = radius / 2;
	private final int innerRadiusSquared = (radius - 1) * radius;
	private int bottom;
	private BlockPos entrancePosition;
	private BlockPos origin;
	private int magSq;

	private int surface;
	private int x, y, z;
	private World world;
	private Random rand;
	private IBlockState block;
	private EnumFacing entranceFacing;

	@Override
	public boolean generate(World world, Random rand, BlockPos entrancePosition) {
		this.world = world;
		this.rand = rand;
		this.entrancePosition = entrancePosition;
		origin = entrancePosition;

		findSurface(entrancePosition);
		genEntrance();
		return true;
	}

	public void setEntrance(EnumFacing facing) {
		this.entranceFacing = facing;
	}

	private void findSurface(BlockPos start) {
		int surfaceGlobal = world.getActualHeight();
		IBlockState blockState;
		while (surfaceGlobal > 0) {
			surfaceGlobal--;
			blockState = world.getBlockState(new BlockPos(start.getX(), surfaceGlobal, start.getZ()));
			if (isLiquid(blockState) || isGroundBlock(blockState)) {
				break;
			}
		}
		surface = surfaceGlobal - origin.getY();

		if (surface < 5) {
			surface = 5;
		}
	}

	private boolean isLiquid(IBlockState blockState) {
		return blockState.getBlock() == Blocks.WATER || blockState.getBlock() == Blocks.LAVA;
	}

	private boolean isGroundBlock(IBlockState blockState)
	{
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

	private void genEntrance() {
		for (y = -20; y <= surface + 5; y++) {
			for (x = -boxRadius; x <= boxRadius; x++) {
				for (z = -boxRadius; z <= boxRadius; z++) {
					magSq = (x * x) + (z * z);
					chooseAndPlaceBlock();
				}
			}
		}
	}

	protected void chooseAndPlaceBlock() {
		if (isOutside()) {
			block = null;

		} else if (isStairs()) {
			block = getStairBlock();

		} else if (isWall() && !isEntrance()) {
			block = Blocks.STONEBRICK.getDefaultState();

		} else if (isFloor()) {
			block = Blocks.STONE.getDefaultState();

		} else {
			block = Blocks.AIR.getDefaultState();
		}
		placeBlock();
	}

	private boolean isFloor() {
		return (y < surface && y % 8 == 0) || y == surface;
	}

	private boolean isStairs() {
		return Math.abs(x) < 2 && Math.abs(z) < 2;
	}

	private IBlockState getStairBlock() {

		BlockStairs stairsBlock = (BlockStairs) Blocks.STONE_BRICK_STAIRS;

		if (y > surface) {
			return Blocks.AIR.getDefaultState();
		}

		if (y < 1) {
			return Blocks.STONE.getDefaultState();
		}

		if (x == 0 && z == 0) {
			return Blocks.STONE.getDefaultState();
		}

		int yAdj = (y + 1) % 4;

		switch (yAdj) {
		case 0:
			if (x == 1 && z == 0) {
				return stairsBlock.getStateFromMeta(2);
			}
			if (x == 1 && z == 1) {
				return Blocks.STONEBRICK.getDefaultState();
			}
			break;
		case 1:
			if (x == 0 && z == 1) {
				return stairsBlock.getStateFromMeta(1);
			}
			if (x == -1 && z == 1) {
				return Blocks.STONEBRICK.getDefaultState();
			}
			break;
		case 2:
			if (x == -1 && z == 0) {
				return stairsBlock.getStateFromMeta(3);
			}
			if (x == -1 && z == -1) {
				return Blocks.STONEBRICK.getDefaultState();
			}
			break;
		case 3:
			if (x == 0 && z == -1) {
				return stairsBlock.getStateFromMeta(0);
			}
			if (x == 1 && z == -1) {
				return Blocks.STONEBRICK.getDefaultState();
			}
			break;
		default:
			break;
		}
		return Blocks.AIR.getDefaultState();
	}

	private boolean isEntrance() {
		if (y > 5 || y < 1) {
			return false;
		}
		switch (entranceFacing) {
		case EAST:
			return x > halfRadius;
		case NORTH:
			return z > halfRadius;
		case SOUTH:
			return z < -halfRadius;
		case WEST:
			return x < -halfRadius;
		default:
			return false;
		}
	}

	private boolean isWall() {
		return magSq >= innerRadiusSquared && y <= surface + 1;
	}

	private boolean isOutside() {
		return magSq > radiusSq + 5;
	}

	protected void placeBlock() {
		if (block == null) {
			return;
		}
		setBlockAndNotifyAdequately(world, origin.add(x, y, z), block);
	}
}
