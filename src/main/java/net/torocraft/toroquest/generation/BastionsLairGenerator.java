package net.torocraft.toroquest.generation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockClay;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockGravel;
import net.minecraft.block.BlockMycelium;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockSandStone;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockSlab.EnumBlockHalf;
import net.minecraft.block.BlockStone.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.storage.loot.LootTableList;
import net.torocraft.toroquest.block.BlockToroSpawner;
import net.torocraft.toroquest.block.TileEntityToroSpawner;
import net.torocraft.toroquest.entities.EntityBas;

public class BastionsLairGenerator extends WorldGenerator {

	private final int width = 40;
	private final int height = 25;

	private BlockPos origin;
	private int x, y, z;
	private World world;
	private Random rand;
	private IBlockState block;
	
	IBlockState blockMossy = Blocks.GRAVEL.getDefaultState();
	IBlockState blockSlab = Blocks.STONE_SLAB.getDefaultState().withProperty(BlockSlab.HALF, EnumBlockHalf.BOTTOM);
	IBlockState blockSlab2 = Blocks.DOUBLE_STONE_SLAB.getDefaultState();
	IBlockState blockFire = Blocks.FIRE.getDefaultState();

	@Override
	public boolean generate(World world, Random rand, BlockPos pos) {
		this.world = world;
		this.rand = rand;

		findOrigin(pos);

		if (origin == null) {
			return false;
		}

		genMainChamber();
		genEntrance(width + 2, 0, EnumFacing.WEST);
		genEntrance(0, -width - 2, EnumFacing.NORTH);
		genEntrance(0, width + 2, EnumFacing.SOUTH);
		genEntrance(-width - 2, 0, EnumFacing.EAST);
		this.spawnBas(world, origin.add(0, walkwayHeight + 2, 0));
		return true;
	}

	protected void findOrigin(BlockPos pos)
	{
		origin = null;
		BlockPos a = getSurfacePosition(pos.add(width, 0, 0));
		BlockPos b = getSurfacePosition(pos.add(0, 0, -width));
		BlockPos c = getSurfacePosition(pos.add(0, 0, width));
		BlockPos d = getSurfacePosition(pos.add(-width, 0, 0));

		if (a == null || b == null || c == null || d == null) {
			return;
		}

		origin = a;

		if (b.getY() < origin.getY()) {
			origin = b;
		}

		if (c.getY() < origin.getY()) {
			origin = c;
		}

		if (d.getY() < origin.getY()) {
			origin = d;
		}

		origin = origin.down(50);

		if (origin.getY() < 5) {
			origin = new BlockPos(origin.getX(), 5, origin.getZ());
		}
	}

	protected void genEntrance(int x, int z, EnumFacing facing) {
		BastionsLairEntranceGenerator g = new BastionsLairEntranceGenerator();
		g.setEntrance(facing);
		g.generate(world, rand, new BlockPos(x, walkwayHeight, z).add(origin));
	}

	private BlockPos getSurfacePosition(BlockPos start) {
		IBlockState blockState;
		BlockPos search = new BlockPos(start.getX(), world.getActualHeight()/2, start.getZ());
		while (search.getY() > 0) {
			search = search.down();
			blockState = world.getBlockState(search);
			if (isLiquid(blockState)) {
				return null;
			}
			if (isGroundBlock(blockState)) {
				break;
			}
		}
		return search;
	}

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

	private void genMainChamber() {
		for (y = 0; y <= height; y++) {
			for (x = -width; x <= width; x++) {
				for (z = -width; z <= width; z++) {
					placeTombBlock();
				}
			}
		}
	}

	private int radiusSq;
	private final int walkwayHeight = height - 10;

	protected void placeTombBlock()
	{
		block = null;

		radiusSq = (x * x) + (z * z);

		if (isOutside()) {
			return;

		}
		else if (isWall() || isRoof())
		{
			if ( rand.nextBoolean() )
			{
				block = Blocks.STONE.getDefaultState();
			}
//			else if ( rand.nextInt(66) == 0 )
//			{
//				block = Blocks.LAVA.getDefaultState();
//			}
			else
			{
				block = Blocks.STONEBRICK.getDefaultState();
			}
		}
		else if (isFloor())
		{
//			if ( rand.nextInt(33) == 0 )
//			{
//				block = Blocks.MAGMA.getDefaultState();
//			}
			// else
			{
				block = Blocks.GRAVEL.getDefaultState();
			}
		}
		else if (isPlatform())
		{
			{
				block = Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, EnumType.ANDESITE);
			}
		}
		else if ( isWalkway() )
		{
			if ( isWalkwayEdge() )
			{
				setBlockAndNotifyAdequately(world, origin.add(x, y+1, z), blockSlab);
				block = Blocks.STONE.getDefaultState();
			}
			else
			{
				block = Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, EnumType.ANDESITE);
			}
		}
		else if ( isWalkwayTorch() )
		{
			block = blockFire;
			BlockPos pos = origin.add(x, y, z);
			setBlockAndNotifyAdequately(world, pos.add(0, -1, 0), Blocks.MAGMA.getDefaultState());
			
			setBlockAndNotifyAdequately(world, pos.add(-1, 0, -1), blockSlab);
			setBlockAndNotifyAdequately(world, pos.add(-1, 0, 0), blockSlab);
			setBlockAndNotifyAdequately(world, pos.add(-1, 0, 1), blockSlab);
			setBlockAndNotifyAdequately(world, pos.add(0, 0, 1), blockSlab);
			setBlockAndNotifyAdequately(world, pos.add(0, 0, -1), blockSlab);
			setBlockAndNotifyAdequately(world, pos.add(1, 0, 1), blockSlab);
			setBlockAndNotifyAdequately(world, pos.add(1, 0, -1), blockSlab);
			setBlockAndNotifyAdequately(world, pos.add(1, 0, 0), blockSlab);
			
			setBlockAndNotifyAdequately(world, pos.add(-1, -1, -1), blockSlab2);
			setBlockAndNotifyAdequately(world, pos.add(-1, -1, 0), blockSlab2);
			setBlockAndNotifyAdequately(world, pos.add(-1, -1, 1), blockSlab2);
			setBlockAndNotifyAdequately(world, pos.add(0, -1, 1), blockSlab2);
			setBlockAndNotifyAdequately(world, pos.add(0, -1, -1), blockSlab2);
			setBlockAndNotifyAdequately(world, pos.add(1, -1, 1), blockSlab2);
			setBlockAndNotifyAdequately(world, pos.add(1, -1, -1), blockSlab2);
			setBlockAndNotifyAdequately(world, pos.add(1, -1, 0), blockSlab2);
			
			setBlockAndNotifyAdequately(world, pos, blockFire);

		}
		else if (isWalkwaySubstructure())
		{
			block = Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, EnumType.ANDESITE);
		}
		else if (isLootChest())
		{
			//placeBlock();
			
			BlockPos pos = origin.add(x, y, z);
			
			block = randomChest();
			setBlockAndNotifyAdequately(world, pos.add(0, 0, 0), block);
			addLootToChest();

			if ( rand.nextBoolean() ) setBlockAndNotifyAdequately(world, pos.add(1, 0, 1), blockMossy);
			setBlockAndNotifyAdequately(world, pos.add(1, 0, 0), blockMossy);
			if ( rand.nextBoolean() ) setBlockAndNotifyAdequately(world, pos.add(1, 0, 1), blockMossy);
			if ( rand.nextBoolean() ) setBlockAndNotifyAdequately(world, pos.add(0, 0, 1), blockMossy);
			setBlockAndNotifyAdequately(world, pos.add(0, 0, 1), blockMossy);
			setBlockAndNotifyAdequately(world, pos.add(1, 0, 1), blockMossy);
			setBlockAndNotifyAdequately(world, pos.add(1, 0, 2), blockMossy);
			if ( rand.nextBoolean() ) setBlockAndNotifyAdequately(world, pos.add(1, 0, 3), blockMossy);
			setBlockAndNotifyAdequately(world, pos.add(1, 0, 0), blockMossy);
			setBlockAndNotifyAdequately(world, pos.add(2, 0, 0), blockMossy);
			setBlockAndNotifyAdequately(world, pos.add(1, 0, 1), blockMossy);
			if ( rand.nextBoolean() ) setBlockAndNotifyAdequately(world, pos.add(1, 0, 2), blockMossy);
			if ( rand.nextBoolean() ) setBlockAndNotifyAdequately(world, pos.add(2, 0, 1), blockMossy);
			setBlockAndNotifyAdequately(world, pos.add(1, 1, 1), blockMossy);
			if ( rand.nextBoolean() ) setBlockAndNotifyAdequately(world, pos.add(1, 2, 1), blockMossy);

			return;
		}
		else
		{
			block = Blocks.AIR.getDefaultState();
		}

		placeBlock();
		return;
	}

	protected boolean isHiddenUnderPlatformChest() {
		return y == 1 && x == 0 && z == 0;
	}

	private IBlockState randomChest() {
		int roll = rand.nextInt(4);
		switch (roll) {
		case 1:
			return Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.NORTH);
		case 2:
			return Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.SOUTH);
		case 3:
			return Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.EAST);
		default:
			return Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.WEST);
		}
	}

	protected void addLootToChest()
	{
		if (block == null)
		{
			return;
		}
		if (block.getBlock() == Blocks.CHEST)
		{
			TileEntity tileentity = world.getTileEntity(origin.add(x, y, z));
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
				if ( rand.nextBoolean() ) ((TileEntityChest) tileentity).setInventorySlotContents(this.rand.nextInt(20)+2, new ItemStack(Items.ENDER_PEARL));
				if ( rand.nextBoolean() ) ((TileEntityChest) tileentity).setInventorySlotContents(this.rand.nextInt(20)+2, new ItemStack(Items.ENDER_PEARL));
				ItemStack stack = PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), PotionTypes.NIGHT_VISION);
				if ( rand.nextBoolean() ) ((TileEntityChest) tileentity).setInventorySlotContents(this.rand.nextInt(20)+2, stack );
				if ( rand.nextBoolean() ) ((TileEntityChest) tileentity).setInventorySlotContents(this.rand.nextInt(20)+2, stack );
			}
		}
	}

	private boolean isLootChest()
	{
		return y == 1 && rand.nextInt(400) == 0;
	}

//	private boolean isPlatformUnderstructure()
//	{
//		return y < walkwayHeight;
//	}

	protected boolean isPlatform() {
		return y <= walkwayHeight && radiusSq < 55;
	}

//	protected boolean isPlatformEdge() {
//		return y == walkwayHeight && radiusSq > 45;
//	}

	private boolean isWalkwayEdge() {
		if (Math.abs(x) <= 3 && Math.abs(z) <= 3) {
			return false;
		}
		return Math.abs(x) == 3 || Math.abs(z) == 3;
	}

	private boolean isWalkwaySubstructure() {

		if (y >= walkwayHeight || y < walkwayHeight - 5) {
			return false;
		}

		if (!(Math.abs(x) < 4 || Math.abs(z) < 4)) {
			return false;
		}

		int distanceUnderWalkway = walkwayHeight - y;

		if (distanceUnderWalkway > 5 || distanceUnderWalkway < 1) {
			return false;
		}

		int distanceFromWalkwayMiddle = Math.min(Math.abs(z), Math.abs(x));

		return distanceFromWalkwayMiddle + distanceUnderWalkway < 5;
	}

	private boolean isWalkway() {
		return y == walkwayHeight && (Math.abs(x) < 4 || Math.abs(z) < 4);
	}

	private boolean isWalkwayTorch() {
		return y == walkwayHeight + 1 && isWalkwayEdge() && (x % 6 == 0 || z % 6 == 0) && (Math.abs(x) > 4 || Math.abs(z) > 4);
	}

	private boolean isFloor() {
		return y == 0;
	}

	private boolean isRoof() {
		return y == height;
	}

	protected void placeBlock()
	{
		if ( block == null )
		{
			return;
		}
		
		if ( world.getBlockState(origin.add(x, y, z)) == blockSlab || world.getBlockState(origin.add(x, y, z)) == blockFire || world.getBlockState(origin.add(x, y, z)) == blockSlab2 || world.getBlockState(origin.add(x, y, z)) == blockMossy ) { return; }
		
		setBlockAndNotifyAdequately(world, origin.add(x, y, z), block);
	}

	private boolean isWall() {
		return Math.abs(x) + Math.abs(z) == width + 4;
	}

	protected boolean isOutside() {
		return Math.abs(x) + Math.abs(z) > width + 4;
	}
	
	private void spawnBas(World world, BlockPos origin)
	{
		this.addToroSpawner(world, origin);
	}
	
	private void addToroSpawner( World world, BlockPos blockpos)
	{
		world.setBlockState(blockpos, BlockToroSpawner.INSTANCE.getDefaultState());
		TileEntity tileentity = world.getTileEntity(blockpos);
		if (tileentity instanceof TileEntityToroSpawner)
		{
			TileEntityToroSpawner spawner = (TileEntityToroSpawner) tileentity;
			spawner.setTriggerDistance(80);
			spawner.setEntityIds(getDefaultEnemies());
			spawner.setSpawnRadius(4);
		}
		else
		{
			EntityBas e = new EntityBas(world);
			e.setPosition(origin.getX() + 0.5, origin.getY() + walkwayHeight + 1, origin.getZ() + 0.5);
			e.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(e)), (IEntityLivingData) null);
			world.spawnEntity(e);
			System.out.println("tile entity is missing");
		}
	}

	private List<String> getDefaultEnemies()
	{
		List<String> entity = new ArrayList<String>();
		entity.add("toroquest:toroquest_bas");
		return entity;
	}

}
