package net.torocraft.toroquest.generation.village.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockLog.EnumAxis;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockQuartz;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockSlab.EnumBlockHalf;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockStairs.EnumHalf;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockStoneSlab;
import net.minecraft.block.BlockStoneSlab.EnumType;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.BlockTrapDoor.DoorHalf;
import net.minecraft.block.BlockWoodSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.BossInfo.Color;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.torocraft.toroquest.block.BlockSmartBanner;
import net.torocraft.toroquest.block.BlockToroSpawner;
import net.torocraft.toroquest.block.TileEntityToroSpawner;
import net.torocraft.toroquest.civilization.CivilizationType;
import net.torocraft.toroquest.civilization.CivilizationUtil;
import net.torocraft.toroquest.config.ToroQuestConfiguration;

public abstract class VillagePieceBlockMap extends StructureVillagePieces.Village
{
	static Random rand = new Random();
	public static final Map<String, IBlockState> DEFAULT_PALLETTE;
	
	static 
	{
		DEFAULT_PALLETTE = new HashMap<String, IBlockState>();
		DEFAULT_PALLETTE.put("--", Blocks.AIR.getDefaultState());
		DEFAULT_PALLETTE.put("Cs", Blocks.COBBLESTONE.getDefaultState());
		DEFAULT_PALLETTE.put("Pw", Blocks.PLANKS.getDefaultState());
		DEFAULT_PALLETTE.put("Sb", Blocks.STONEBRICK.getDefaultState());
		// DEFAULT_PALLETTE.put("Wa", Blocks.WATER.getDefaultState());
		DEFAULT_PALLETTE.put("Dr", Blocks.DIRT.getDefaultState());
		DEFAULT_PALLETTE.put("Gr", Blocks.GRASS.getDefaultState());
		DEFAULT_PALLETTE.put("GP", Blocks.GRASS_PATH.getDefaultState());
		DEFAULT_PALLETTE.put("So", Blocks.STONE.getDefaultState());
		
		DEFAULT_PALLETTE.put("sp", Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.SPRUCE));
		DEFAULT_PALLETTE.put("st", Blocks.WOODEN_SLAB.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.SPRUCE).withProperty(BlockSlab.HALF, EnumBlockHalf.TOP));
		DEFAULT_PALLETTE.put("sb", Blocks.WOODEN_SLAB.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.SPRUCE));

		DEFAULT_PALLETTE.put("Fc", Blocks.COBBLESTONE_WALL.getDefaultState());
		DEFAULT_PALLETTE.put("Sw", Blocks.WOODEN_SLAB.getDefaultState());
		DEFAULT_PALLETTE.put("td", Blocks.TRAPDOOR.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH));
		DEFAULT_PALLETTE.put("ti", Blocks.IRON_TRAPDOOR.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH));
		DEFAULT_PALLETTE.put("tt", Blocks.IRON_TRAPDOOR.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH).withProperty(BlockTrapDoor.HALF, DoorHalf.TOP));
		DEFAULT_PALLETTE.put("Lg", Blocks.LOG.getDefaultState());
		
		DEFAULT_PALLETTE.put("Tv", Blocks.TRAPDOOR.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.SOUTH).withProperty(BlockTrapDoor.OPEN, true));
		DEFAULT_PALLETTE.put("T^", Blocks.TRAPDOOR.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH).withProperty(BlockTrapDoor.OPEN, true));
		DEFAULT_PALLETTE.put("T>", Blocks.TRAPDOOR.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.WEST).withProperty(BlockTrapDoor.OPEN, true));
		DEFAULT_PALLETTE.put("T<", Blocks.TRAPDOOR.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.EAST).withProperty(BlockTrapDoor.OPEN, true));
		
		DEFAULT_PALLETTE.put("Lx", Blocks.LOG.getDefaultState().withProperty(BlockLog.LOG_AXIS, EnumAxis.X));
		DEFAULT_PALLETTE.put("Lz", Blocks.LOG.getDefaultState().withProperty(BlockLog.LOG_AXIS, EnumAxis.Z));
		
		DEFAULT_PALLETTE.put("vv", Blocks.OAK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.SOUTH).withProperty(BlockStairs.HALF, EnumHalf.TOP));
		DEFAULT_PALLETTE.put("^^", Blocks.OAK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH).withProperty(BlockStairs.HALF, EnumHalf.TOP));
		DEFAULT_PALLETTE.put(">>", Blocks.OAK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.WEST).withProperty(BlockStairs.HALF, EnumHalf.TOP));
		DEFAULT_PALLETTE.put("<<", Blocks.OAK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.EAST).withProperty(BlockStairs.HALF, EnumHalf.TOP));

		DEFAULT_PALLETTE.put("Ss", Blocks.STONE_SLAB.getDefaultState());
		DEFAULT_PALLETTE.put("DS", Blocks.DOUBLE_STONE_SLAB.getDefaultState());
		DEFAULT_PALLETTE.put("Gp", Blocks.GLASS_PANE.getDefaultState());
		DEFAULT_PALLETTE.put("Gs", Blocks.GLOWSTONE.getDefaultState());
		DEFAULT_PALLETTE.put("pT", Blocks.WOODEN_SLAB.getDefaultState().withProperty(BlockSlab.HALF, EnumBlockHalf.TOP));
		DEFAULT_PALLETTE.put("cT", Blocks.STONE_SLAB.getDefaultState().withProperty(BlockSlab.HALF, EnumBlockHalf.TOP).withProperty(BlockStoneSlab.VARIANT, EnumType.COBBLESTONE));
		DEFAULT_PALLETTE.put("Sc", Blocks.STONE_SLAB.getDefaultState().withProperty(BlockStoneSlab.VARIANT, EnumType.COBBLESTONE));
		DEFAULT_PALLETTE.put("Sv", Blocks.OAK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH));
		DEFAULT_PALLETTE.put("S^", Blocks.OAK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.SOUTH));
		DEFAULT_PALLETTE.put("S>", Blocks.OAK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.EAST));
		DEFAULT_PALLETTE.put("S<", Blocks.OAK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.WEST));
		DEFAULT_PALLETTE.put("Gb", Blocks.GOLD_BLOCK.getDefaultState());
		DEFAULT_PALLETTE.put("Qs", Blocks.STONE_SLAB.getDefaultState());
		DEFAULT_PALLETTE.put("Qc", Blocks.QUARTZ_BLOCK.getDefaultState().withProperty(BlockQuartz.VARIANT, BlockQuartz.EnumType.CHISELED));
		DEFAULT_PALLETTE.put("Qx", Blocks.QUARTZ_BLOCK.getDefaultState().withProperty(BlockQuartz.VARIANT, BlockQuartz.EnumType.LINES_Z));
		DEFAULT_PALLETTE.put("Qy", Blocks.QUARTZ_BLOCK.getDefaultState().withProperty(BlockQuartz.VARIANT, BlockQuartz.EnumType.LINES_Y));
		DEFAULT_PALLETTE.put("An", Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.ANDESITE_SMOOTH));
		DEFAULT_PALLETTE.put("wR", Blocks.STONE.getDefaultState());
		DEFAULT_PALLETTE.put("FF", Blocks.FIRE.getDefaultState());
		DEFAULT_PALLETTE.put("BS", Blocks.BOOKSHELF.getDefaultState());
		
		// TABLE
		DEFAULT_PALLETTE.put("Wp", Blocks.WOODEN_PRESSURE_PLATE.getDefaultState());
		DEFAULT_PALLETTE.put("Fw", Blocks.OAK_FENCE.getDefaultState());
		DEFAULT_PALLETTE.put("F<", Blocks.FURNACE.getDefaultState().withProperty(BlockFurnace.FACING, EnumFacing.WEST));
		
		DEFAULT_PALLETTE.put("CT", Block.getBlockFromName(ToroQuestConfiguration.craftingTableResourceName).getDefaultState());
		DEFAULT_PALLETTE.put("CA", Blocks.CAULDRON.getDefaultState());
		
		IBlockState LA = Blocks.AIR.getDefaultState();
		try {LA = Block.getBlockFromName(ToroQuestConfiguration.lanternResourceName).getDefaultState();}catch(Exception e){}
		DEFAULT_PALLETTE.put("LA", LA);
		IBlockState CH = Blocks.AIR.getDefaultState();
		try {CH = Block.getBlockFromName(ToroQuestConfiguration.chainResourceName).getDefaultState();}catch(Exception e){}
		DEFAULT_PALLETTE.put("CH", CH);

		// l
		DEFAULT_PALLETTE.put("l^", Blocks.LADDER.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH));
		DEFAULT_PALLETTE.put("lv", Blocks.LADDER.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.SOUTH));
		DEFAULT_PALLETTE.put("l<", Blocks.LADDER.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.EAST));
		DEFAULT_PALLETTE.put("l>", Blocks.LADDER.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.WEST));
		
		// Q
		DEFAULT_PALLETTE.put("Qv", Blocks.QUARTZ_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.SOUTH));
		DEFAULT_PALLETTE.put("Q^", Blocks.QUARTZ_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH));
		DEFAULT_PALLETTE.put("Q>", Blocks.QUARTZ_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.EAST));
		DEFAULT_PALLETTE.put("Q<", Blocks.QUARTZ_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.WEST));
		
		// C
		DEFAULT_PALLETTE.put("Cv", Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH));
		DEFAULT_PALLETTE.put("C^", Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.SOUTH));
		DEFAULT_PALLETTE.put("C>", Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.EAST));
		DEFAULT_PALLETTE.put("C<", Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.WEST));
		
		// d
		DEFAULT_PALLETTE.put("dv", Blocks.OAK_DOOR.getDefaultState().withProperty(BlockBed.FACING, EnumFacing.NORTH));
		DEFAULT_PALLETTE.put("d^", Blocks.OAK_DOOR.getDefaultState().withProperty(BlockBed.FACING, EnumFacing.SOUTH));
		DEFAULT_PALLETTE.put("d>", Blocks.OAK_DOOR.getDefaultState().withProperty(BlockBed.FACING, EnumFacing.EAST));
		DEFAULT_PALLETTE.put("d<", Blocks.OAK_DOOR.getDefaultState().withProperty(BlockBed.FACING, EnumFacing.WEST));
		
		// c
		DEFAULT_PALLETTE.put("cv", Blocks.TRAPPED_CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.NORTH));
		DEFAULT_PALLETTE.put("c^", Blocks.TRAPPED_CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.SOUTH));
		DEFAULT_PALLETTE.put("c>", Blocks.TRAPPED_CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.EAST));
		DEFAULT_PALLETTE.put("c<", Blocks.TRAPPED_CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.WEST));

		// t
		DEFAULT_PALLETTE.put("t^", Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.NORTH));
		DEFAULT_PALLETTE.put("tv", Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.SOUTH));
		DEFAULT_PALLETTE.put("t<", Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.EAST));
		DEFAULT_PALLETTE.put("t>", Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.WEST));
		DEFAULT_PALLETTE.put("t.", Blocks.TORCH.getDefaultState());
		
		// COLORED BEDS = $v
		//DEFAULT_PALLETTE.put("aa", Block.getBlockFromItem( new ItemStack(Items.BED, 1, 3).getItem() ).getDefaultState());

		DEFAULT_PALLETTE.put("bv", Blocks.BED.getDefaultState().withProperty(BlockBed.FACING, EnumFacing.NORTH));
		DEFAULT_PALLETTE.put("b^", Blocks.BED.getDefaultState().withProperty(BlockBed.FACING, EnumFacing.SOUTH));
		DEFAULT_PALLETTE.put("b>", Blocks.BED.getDefaultState().withProperty(BlockBed.FACING, EnumFacing.EAST));
		DEFAULT_PALLETTE.put("b<", Blocks.BED.getDefaultState().withProperty(BlockBed.FACING, EnumFacing.WEST));
//		DEFAULT_PALLETTE.put("rv", Block.getBlockFromName("minecraft:bed").getDefaultState().withProperty(BlockBed.FACING, EnumFacing.NORTH).withProperty(BlockColored.COLOR, EnumDyeColor.RED));
//		DEFAULT_PALLETTE.put("r^", Block.getBlockFromName("minecraft:bed").getDefaultState().withProperty(BlockBed.FACING, EnumFacing.SOUTH).withProperty(BlockColored.COLOR, EnumDyeColor.RED));
//		DEFAULT_PALLETTE.put("r>", Block.getBlockFromName("minecraft:bed").getDefaultState().withProperty(BlockBed.FACING, EnumFacing.EAST).withProperty(BlockColored.COLOR, EnumDyeColor.RED));
//		DEFAULT_PALLETTE.put("r<", Block.getBlockFromName("minecraft:bed").getDefaultState().withProperty(BlockBed.FACING, EnumFacing.WEST).withProperty(BlockColored.COLOR, EnumDyeColor.RED));
//		DEFAULT_PALLETTE.put("gv", Blocks.BED.getDefaultState().withProperty(BlockBed.FACING, EnumFacing.NORTH).withProperty(BlockColored.COLOR, EnumDyeColor.RED));
//		DEFAULT_PALLETTE.put("g^", Blocks.BED.getDefaultState().withProperty(BlockBed.FACING, EnumFacing.SOUTH).withProperty(BlockColored.COLOR, EnumDyeColor.RED));
//		DEFAULT_PALLETTE.put("g>", Blocks.BED.getDefaultState().withProperty(BlockBed.FACING, EnumFacing.EAST).withProperty(BlockColored.COLOR, EnumDyeColor.RED));
//		DEFAULT_PALLETTE.put("g<", Blocks.BED.getDefaultState().withProperty(BlockBed.FACING, EnumFacing.WEST).withProperty(BlockColored.COLOR, EnumDyeColor.RED));
//		DEFAULT_PALLETTE.put("uv", Blocks.BED.getDefaultState().withProperty(BlockBed.FACING, EnumFacing.NORTH).withProperty(BlockColored.COLOR, EnumDyeColor.RED));
//		DEFAULT_PALLETTE.put("u^", Blocks.BED.getDefaultState().withProperty(BlockBed.FACING, EnumFacing.SOUTH).withProperty(BlockColored.COLOR, EnumDyeColor.RED));
//		DEFAULT_PALLETTE.put("u>", Blocks.BED.getDefaultState().withProperty(BlockBed.FACING, EnumFacing.EAST).withProperty(BlockColored.COLOR, EnumDyeColor.RED));
//		DEFAULT_PALLETTE.put("u<", Blocks.BED.getDefaultState().withProperty(BlockBed.FACING, EnumFacing.WEST).withProperty(BlockColored.COLOR, EnumDyeColor.RED));
//		DEFAULT_PALLETTE.put("bv", Blocks.BED.getDefaultState().withProperty(BlockBed.FACING, EnumFacing.NORTH).withProperty(BlockColored.COLOR, EnumDyeColor.RED));
//		DEFAULT_PALLETTE.put("b^", Blocks.BED.getDefaultState().withProperty(BlockBed.FACING, EnumFacing.SOUTH).withProperty(BlockColored.COLOR, EnumDyeColor.RED));
//		DEFAULT_PALLETTE.put("b>", Blocks.BED.getDefaultState().withProperty(BlockBed.FACING, EnumFacing.EAST).withProperty(BlockColored.COLOR, EnumDyeColor.RED));
//		DEFAULT_PALLETTE.put("b<", Blocks.BED.getDefaultState().withProperty(BlockBed.FACING, EnumFacing.WEST).withProperty(BlockColored.COLOR, EnumDyeColor.RED));
//		DEFAULT_PALLETTE.put("yv", Blocks.BED.getDefaultState().withProperty(BlockBed.FACING, EnumFacing.NORTH).withProperty(BlockColored.COLOR, EnumDyeColor.RED));
//		DEFAULT_PALLETTE.put("y^", Blocks.BED.getDefaultState().withProperty(BlockBed.FACING, EnumFacing.SOUTH).withProperty(BlockColored.COLOR, EnumDyeColor.RED));
//		DEFAULT_PALLETTE.put("y>", Blocks.BED.getDefaultState().withProperty(BlockBed.FACING, EnumFacing.EAST).withProperty(BlockColored.COLOR, EnumDyeColor.RED));
//		DEFAULT_PALLETTE.put("y<", Blocks.BED.getDefaultState().withProperty(BlockBed.FACING, EnumFacing.WEST).withProperty(BlockColored.COLOR, EnumDyeColor.RED));
//		DEFAULT_PALLETTE.put("wv", Blocks.BED.getDefaultState().withProperty(BlockBed.FACING, EnumFacing.NORTH).withProperty(BlockColored.COLOR, EnumDyeColor.RED));
//		DEFAULT_PALLETTE.put("w^", Blocks.BED.getDefaultState().withProperty(BlockBed.FACING, EnumFacing.SOUTH).withProperty(BlockColored.COLOR, EnumDyeColor.RED));
//		DEFAULT_PALLETTE.put("w>", Blocks.BED.getDefaultState().withProperty(BlockBed.FACING, EnumFacing.EAST).withProperty(BlockColored.COLOR, EnumDyeColor.RED));
//		DEFAULT_PALLETTE.put("w<", Blocks.BED.getDefaultState().withProperty(BlockBed.FACING, EnumFacing.WEST).withProperty(BlockColored.COLOR, EnumDyeColor.RED));
		IBlockState ET = Blocks.AIR.getDefaultState();
		try {ET = Block.getBlockFromName(ToroQuestConfiguration.enchantingTableResourceName).getDefaultState();}catch(Exception e){}
		DEFAULT_PALLETTE.put("ET", ET);
		DEFAULT_PALLETTE.put("IP", Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE.getDefaultState());
		DEFAULT_PALLETTE.put("LP", Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE.getDefaultState());
		DEFAULT_PALLETTE.put("JJ", Blocks.JUKEBOX.getDefaultState());
		DEFAULT_PALLETTE.put("FL", Blocks.FLOWER_POT.getDefaultState());
		DEFAULT_PALLETTE.put("EM", Blocks.EMERALD_BLOCK.getDefaultState());

		//		DEFAULT_PALLETTE.put("FR", Blocks.FLOWER_POT.getDefaultState().withProperty(BlockFlowerPot.CONTENTS, EnumFlowerType.POPPY));
//		DEFAULT_PALLETTE.put("FU", Blocks.FLOWER_POT.getDefaultState().withProperty(BlockFlowerPot.CONTENTS, EnumFlowerType.BLUE_ORCHID));
//		DEFAULT_PALLETTE.put("FG", Blocks.FLOWER_POT.getDefaultState().withProperty(BlockFlowerPot.CONTENTS, EnumFlowerType.OAK_SAPLING));
//		DEFAULT_PALLETTE.put("FB", Blocks.FLOWER_POT.getDefaultState().withProperty(BlockFlowerPot.CONTENTS, EnumFlowerType.MUSHROOM_BROWN));
//		DEFAULT_PALLETTE.put("FY", Blocks.FLOWER_POT.getDefaultState().withProperty(BlockFlowerPot.CONTENTS, EnumFlowerType.CACTUS));
//		DEFAULT_PALLETTE.put("FW", Blocks.FLOWER_POT.getDefaultState().withProperty(BlockFlowerPot.CONTENTS, EnumFlowerType.FERN));

		// CARPET = *c
		DEFAULT_PALLETTE.put("*r", Blocks.CARPET.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.RED));
		DEFAULT_PALLETTE.put("*g", Blocks.CARPET.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.GREEN));
		DEFAULT_PALLETTE.put("*u", Blocks.CARPET.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.CYAN));
		DEFAULT_PALLETTE.put("*b", Blocks.CARPET.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.BLACK));
		DEFAULT_PALLETTE.put("*w", Blocks.CARPET.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.BROWN));
		DEFAULT_PALLETTE.put("*y", Blocks.CARPET.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.YELLOW));
		DEFAULT_PALLETTE.put("*c", Blocks.CARPET.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.WHITE));
		
		DEFAULT_PALLETTE.put("cc", Blocks.CARPET.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.WHITE));
		DEFAULT_PALLETTE.put("co", Blocks.CARPET.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.ORANGE));
		DEFAULT_PALLETTE.put("cp", Blocks.CARPET.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.PURPLE));

		// WOOL = wc
		DEFAULT_PALLETTE.put("wr", Blocks.WOOL.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.RED));
		DEFAULT_PALLETTE.put("wg", Blocks.WOOL.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.GREEN));
		DEFAULT_PALLETTE.put("wu", Blocks.WOOL.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.CYAN));
		DEFAULT_PALLETTE.put("wb", Blocks.WOOL.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.BLACK));
		DEFAULT_PALLETTE.put("ww", Blocks.WOOL.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.BROWN));
		DEFAULT_PALLETTE.put("wy", Blocks.WOOL.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.YELLOW));
		DEFAULT_PALLETTE.put("wc", Blocks.WOOL.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.WHITE));
		
		DEFAULT_PALLETTE.put("WW", Blocks.WOOL.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.WHITE));
		DEFAULT_PALLETTE.put("WO", Blocks.WOOL.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.ORANGE));
		DEFAULT_PALLETTE.put("WP", Blocks.WOOL.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.PURPLE));
		
		// CONCRETE == *C
		DEFAULT_PALLETTE.put("*R", Blocks.CONCRETE.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.RED));
		DEFAULT_PALLETTE.put("*G", Blocks.CONCRETE.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.GREEN));
		DEFAULT_PALLETTE.put("*U", Blocks.CONCRETE.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.CYAN));
		DEFAULT_PALLETTE.put("*B", Blocks.CONCRETE.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.BLACK));
		DEFAULT_PALLETTE.put("*W", Blocks.CONCRETE.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.BROWN));
		DEFAULT_PALLETTE.put("*Y", Blocks.CONCRETE.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.YELLOW));
		DEFAULT_PALLETTE.put("*C", Blocks.CONCRETE.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.WHITE));
															DEFAULT_PALLETTE.put("CC", Blocks.CONCRETE.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.WHITE));
        //new BlockSmartBanner()
		BlockSmartBanner banner = new BlockSmartBanner();
		banner.setFacing(EnumFacing.NORTH);
        DEFAULT_PALLETTE.put("@v", banner.getDefaultState());
        banner = new BlockSmartBanner();
		banner.setFacing(EnumFacing.SOUTH);
        DEFAULT_PALLETTE.put("@^", banner.getDefaultState());
        banner = new BlockSmartBanner();
		banner.setFacing(EnumFacing.EAST);
        DEFAULT_PALLETTE.put("@>", banner.getDefaultState());
        banner = new BlockSmartBanner();
		banner.setFacing(EnumFacing.WEST);
        DEFAULT_PALLETTE.put("@<", banner.getDefaultState());
        
		DEFAULT_PALLETTE.put("tA", Blocks.TRAPDOOR.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.WEST));
															
		//
//		BlockSmartBanner banner = new BlockSmartBanner();
//		banner.facing = EnumFacing.NORTH;
//        DEFAULT_PALLETTE.put("@^", banner.getDefaultState());
//		banner.facing = EnumFacing.SOUTH;
//        DEFAULT_PALLETTE.put("@v", banner.getDefaultState());
//        banner.facing = EnumFacing.EAST;
//        DEFAULT_PALLETTE.put("@>", banner.getDefaultState());
//		banner.facing = EnumFacing.WEST;
	}

	protected String name;

	public VillagePieceBlockMap(String name, StructureVillagePieces.Start start, int type, Random rand, StructureBoundingBox bounds, EnumFacing facing)
	{
		super(start, type);
		this.name = name;
		this.setCoordBaseMode(facing);
		this.boundingBox = bounds;
	}

	public VillagePieceBlockMap()
	{

	}

	protected boolean specialBlockHandling(World world, String c, int x, int y, int z)
	{
		return false;
	}
	
	protected int getYOffset()
	{
		return 0;
	}

	protected boolean specialHandlingForSpawner(World world, String entityBlockCode, String c, int x, int y, int z, List<String> entities)
	{
		if (!c.equals(entityBlockCode))
		{
			return false;
		}

		BlockPos blockpos = new BlockPos(this.getXWithOffset(x, z), this.getYWithOffset(y), this.getZWithOffset(x, z));
		if (boundingBox.isVecInside(blockpos))
		{
			addToroSpawner(world, blockpos, entities);
		}
		return true;
	}

	protected void addToroSpawner(World world, BlockPos blockpos, List<String> entities)
	{
		try
		{
			world.setBlockState(blockpos, BlockToroSpawner.INSTANCE.getDefaultState());
			TileEntity tileentity = world.getTileEntity(blockpos);
			if (tileentity instanceof TileEntityToroSpawner)
			{
				((TileEntityToroSpawner) tileentity).setTriggerDistance(80);
				((TileEntityToroSpawner) tileentity).setEntityIds(entities);
			}
		}
		catch(Exception e)
		{
			System.out.println("***error spawning ToroQuest entity***");
		}
	}
	
//	// helmet
//	protected boolean specialHandlingForSpawnerH(World world, String entityBlockCode, String c, int x, int y, int z, List<String> entities)
//	{
//		if (!c.equals(entityBlockCode))
//		{
//			return false;
//		}
//
//		BlockPos blockpos = new BlockPos(this.getXWithOffset(x, z), this.getYWithOffset(y), this.getZWithOffset(x, z));
//		if (boundingBox.isVecInside(blockpos))
//		{
//			addToroSpawner(world, blockpos, entities);
//		}
//		return true;
//	}
//	protected void addToroSpawnerH(World world, BlockPos blockpos, List<String> entities)
//	{
//		try
//		{
//			world.setBlockState(blockpos, BlockToroSpawner.INSTANCE.getDefaultState());
//			TileEntity tileentity = world.getTileEntity(blockpos);
//			if (tileentity instanceof TileEntityToroSpawner)
//			{
//				((TileEntityToroSpawner) tileentity).setTriggerDistance(80);
//				((TileEntityToroSpawner) tileentity).setEntityIds(entities);
//				((TileEntityToroSpawner) tileentity).setHelmet(new ItemStack(Item.getByNameOrId("toroquest:bandit_helmet")));
//			}
//		}
//		catch(Exception e)
//		{
//			System.out.println("***error spawning ToroQuest entity***");
//		}
//	}
//	//

	public boolean addComponentParts(final World world, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
		if (averageGroundLvl < 0) {
			averageGroundLvl = this.getAverageGroundLevel(world, structureBoundingBoxIn);

			if (averageGroundLvl < 0) {
				return true;
			}
			boundingBox.offset(0, averageGroundLvl - boundingBox.maxY + boundingBox.getYSize() - 1 + getYOffset(), 0);
		}

		Map<String, IBlockState> palette = getBiomeSpecificPalette();

		new BlockMapBuilder(name)
		{
			@Override
			protected void setBlockState(IBlockState block, int x, int y, int z)
			{
				VillagePieceBlockMap.this.setBlockState(world, block, x, y, z, boundingBox);
			}
		    /**
		     * Flag 1 will cause a block update. Flag 2 will send the change to clients. Flag 4 will prevent the block from
		     * being re-rendered, if this is a client world. Flag 8 will force any re-renders to run on the main thread instead
		     * of the worker pool, if this is a client world and flag 4 is clear. Flag 16 will prevent observers from seeing
		     * this change. Flags can be OR-ed
		     */

			@Override
			protected void replaceAirAndLiquidDownwards(IBlockState block, int x, int y, int z) {
				VillagePieceBlockMap.this.replaceAirAndLiquidDownwards(world, block, x, y, z, boundingBox);
			}

			@Override
			protected boolean specialBlockHandling(String c, int x, int y, int z)
			{
				return VillagePieceBlockMap.this.specialBlockHandling(world, c, x, y, z);
			}
			
			@Override
			public String colorChange()
			{
				try
				{
					CivilizationType civ = CivilizationUtil.getProvinceAt(world, ((structureBoundingBoxIn.minX+structureBoundingBoxIn.maxX)/32), ((structureBoundingBoxIn.minZ+structureBoundingBoxIn.maxZ)/32)).civilization;
					//System.out.println(civ);
					switch ( civ )
					{
						case FIRE:
						{
							return "*R";
						}
						case EARTH:
						{
							return "*G";
						}
						case WATER:
						{
							return "*U";
						}
						case MOON:
						{
							return "*B";
						}
						case WIND:
						{
							return "*W";
						}
						case SUN:
						{
							return "*Y";
						}
					}
				}
				catch (Exception e)
				{
					
				}
				return "**";
			}
			
			@Override
			public String bedChange(String s)
			{
				try
				{
					CivilizationType civ = CivilizationUtil.getProvinceAt(world, ((structureBoundingBoxIn.minX+structureBoundingBoxIn.maxX)/32), ((structureBoundingBoxIn.minZ+structureBoundingBoxIn.maxZ)/32)).civilization;
					
					switch ( civ )
					{
						case FIRE:
						{
							return s.replace("$", "r");
						}
						case WATER:
						{
							return s.replace("$", "u");
						}
						case EARTH:
						{
							return s.replace("$", "g");
						}
						case MOON:
						{
							return s.replace("$", "b");
						}
						case WIND:
						{
							return s.replace("$", "y");
						}
						case SUN:
						{
							return s.replace("$", "w");
						}
					}
				}
				catch (Exception e)
				{
					
				}
				return super.bedChange(s);
			}
			
//			@Override
//			public void bannerChange(String s) // TODO
//			{
//				try
//				{
//					CivilizationType civ = CivilizationUtil.getProvinceAt(world, ((structureBoundingBoxIn.minX+structureBoundingBoxIn.maxX)/32), ((structureBoundingBoxIn.minZ+structureBoundingBoxIn.maxZ)/32)).civilization;
//					//System.out.println(civ);
//					
//					EnumFacing facing = EnumFacing.NORTH;
//					
//					switch ( s.substring(1) )
//					{
//						case "v":
//						{
//							facing = EnumFacing.NORTH;
//							break;
//						}
//						case "<":
//						{
//							facing = EnumFacing.WEST;
//							break;
//						}
//						case ">":
//						{
//							facing = EnumFacing.EAST;
//							break;
//						}
//						case "^":
//						{
//							facing = EnumFacing.SOUTH;
//							break;
//						}
//					}
//					
//					switch ( civ )
//					{
//						case FIRE:
//						{
//							setRedBanner(world, new BlockPos(x,y,z), facing);
//							return;
//						}
//						case EARTH:
//						{
//							setGreenBanner(world, new BlockPos(x,y,z), facing);
//							return;
//						}
//						case WATER:
//						{
//							setBlueBanner(world, new BlockPos(x,y,z), facing);
//							return;
//						}
//						case MOON:
//						{
//							setBlackBanner(world, new BlockPos(x,y,z), facing);
//							return;
//						}
//						case WIND:
//						{
//							setBrownBanner(world, new BlockPos(x,y,z), facing);
//							return;
//						}
//						case SUN:
//						{
//							setYellowBanner(world, new BlockPos(x,y,z), facing);
//							return;
//						}
//					}
//				}
//				catch (Exception e)
//				{
//					
//				}
//			}
			
		
			@Override
			public String carpetChange()
			{
				try
				{
					CivilizationType civ = CivilizationUtil.getProvinceAt(world, ((structureBoundingBoxIn.minX+structureBoundingBoxIn.maxX)/32), ((structureBoundingBoxIn.minZ+structureBoundingBoxIn.maxZ)/32)).civilization;
					//System.out.println(civ);
					switch ( civ )
					{
						case FIRE:
						{
							return "*r";
						}
						case WATER:
						{
							return "*u";
						}
						case EARTH:
						{
							return "*g";
						}
						case MOON:
						{
							return "*b";
						}
						case SUN:
						{
							return "*y";
						}
						case WIND:
						{
							return "*w";
						}
					}
				}
				catch (Exception e)
				{
					
				}
				return super.carpetChange();
			}
			
			@Override
			public String woolChange()
			{
				try
				{
					CivilizationType civ = CivilizationUtil.getProvinceAt(world, ((structureBoundingBoxIn.minX+structureBoundingBoxIn.maxX)/32), ((structureBoundingBoxIn.minZ+structureBoundingBoxIn.maxZ)/32)).civilization;
					//System.out.println(civ);
					switch ( civ )
					{
						case FIRE:
						{
							return "wr";
						}
						case EARTH:
						{
							return "wg";
						}
						case WATER:
						{
							return "wu";
						}
						case MOON:
						{
							return "wb";
						}
						case WIND:
						{
							return "ww";
						}
						case SUN:
						{
							return "wy";
						}
					}
				}
				catch (Exception e)
				{
					
				}
				return super.woolChange();
			}
			
//			@Override
//			public String flower  fChange()
//			{
//				try
//				{
//					CivilizationType civ = CivilizationUtil.getProvinceAt(world, ((structureBoundingBoxIn.minX+structureBoundingBoxIn.maxX)/32), ((structureBoundingBoxIn.minZ+structureBoundingBoxIn.maxZ)/32)).civilization;
//					//System.out.println(civ);
//					switch ( civ )
//					{
//						case FIRE:
//						{
//							return "FR";
//						}
//						case EARTH:
//						{
//							return "FG";
//						}
//						case WATER:
//						{
//							return "FU";
//						}
//						case MOON:
//						{
//							return "FB";
//						}
//						case WIND:
//						{
//							return "FW";
//						}
//						case SUN:
//						{
//							return "FY";
//						}
//					}
//				}
//				catch (Exception e)
//				{
//					
//				}
//				return super.flowerChange();
//			}
			
			
		}.build(palette);
		
		return true;
	}

	private Map<String, IBlockState> getBiomeSpecificPalette()
	{		
		Map<String, IBlockState> palette = new HashMap<String, IBlockState>();
		palette.putAll(DEFAULT_PALLETTE);
		alterPalette(palette);

		for (Entry<String, IBlockState> e : palette.entrySet()) {
			e.setValue(getBiomeSpecificBlockState(e.getValue()));
		}

		return palette;
	}

	protected IBlockState getBiomeSpecificBlockState(IBlockState in)
	{
		in = super.getBiomeSpecificBlockState(in);
		if (in.getBlock() instanceof BlockDoor) {
			in = biomeSpecificDoor(in);
		}
		return in;
	}

	protected IBlockState biomeSpecificDoor(IBlockState in)
	{
		BlockDoor newBlock;
		switch (this.structureType)
		{
		case 2:
			newBlock = Blocks.ACACIA_DOOR;
			break;
		case 3:
			newBlock = Blocks.SPRUCE_DOOR;
			break;
		default:
			newBlock = Blocks.OAK_DOOR;
			break;
		}
		return newBlock.getDefaultState().withProperty(BlockBed.FACING, in.getValue(BlockDoor.FACING));
	}

	protected abstract void alterPalette(Map<String, IBlockState> palette);

	protected int chooseProfession(int villagersSpawnedIn, int currentVillagerProfession)
	{
		return 1;
	}

	
	
	// ======================================== BANNER ========================================
	// ========================================================================================
	
	
//	0, // Black
//	1, // Red
//	2, // Green
//	3, // Brown
//	4, // Blue
//	5, // Purple
//	6, // Cyan
//	7, // Light Gray
//	8, // Gray
//	9, // Pink
//	10, // Lime
//	11, // Yellow
//	12, // Light Blue
//	13, // Magenta
//	14, // Orange
//	15 // White
	
	/**
	 * For reference: the different banner emblazon patterns
	 * 
	 * "bs"		Base (bottom third colored)
	 * "ts" 	Chief (top third colored)
	 * "ls" 	pale dexter (left third colored)
	 * "rs" 	pale sinister (right third colored)
	 * "cs" 	pale (center vertical line)
	 * "ms" 	fess (center horizontal line)
	 * "drs" 	bend (line from upper-left to lower-right)
	 * "dls" 	bend sinister (line from upper-right to lower-left)
	 * "ss" 	paly (vertical pinstripes)
	 * "cr" 	saltire ('X' shape)
	 * "sc" 	+
	 * "ld"		per bend sinister (upper-left half colored)
	 * "rud"	per bend (upper-right half colored)
	 * "lud"	per bend inverted (lower-left half colored)
	 * "rd"		per bend sinister inverted (lower-right half colored)
	 * "vh"		per pale (left half colored)
	 * "vhr"	per pale inverted (right half colored)
	 * "hh"		per fess (top half colored)
	 * "hhb"	per fess inverted (bottom half colored)
	 * "bl" 	base dexter canton (square in lower-left corner)
	 * "br" 	base sinister canton (square in lower-right corner)
	 * "tl" 	chief dexter canton (square in upper-left corner)
	 * "tr" 	chief sinister canton (square in upper-right corner)
	 * "bt" 	^
	 * "tt" 	V
	 * "bts" 	base indented (scallop shapes at bottom)
	 * "tts" 	chief indented (scallop shapes at top)
	 * "mc"		O
	 * "mr"		lozenge (rhombus in center)
	 * "bo"		bordure (border)
	 * "cbo"	dyed bordure indented (fancy border)
	 * "bri"	BRICKS
	 * "gra"	gradient (color at top)
	 * "gru"	base gradient (color at bottom)
	 * "cre"	:(
	 * "sku"	*X
	 * "flo"	*
	 * "moj"	dyed Thing (Mojang logo)
	 */
	
	// ================ BLACK ================
	public static ItemStack getBlackBanner()
	{
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		nbttagcompound.setInteger("Base", 2);
		
    	NBTTagCompound patterntag = new NBTTagCompound();
        NBTTagList nbttaglist = new NBTTagList();

        // BLACK DIAMOND
        patterntag.setString("Pattern", "mr");
        patterntag.setInteger("Color", 0);
        nbttaglist.appendTag(patterntag);
//        // DARK GREEN BARS
//        patterntag.setString("Pattern", "ss");
//        patterntag.setInteger("Color", 2);
//        nbttaglist.appendTag(patterntag);
        // DARK GREEN GRADIENT
//        patterntag = new NBTTagCompound();
//        patterntag.setString("Pattern", "gra");
//        patterntag.setInteger("Color", 2);
//        nbttaglist.appendTag(patterntag);
        // WHITE BORDER
        patterntag = new NBTTagCompound();
        patterntag.setString("Pattern", "cbo");
        patterntag.setInteger("Color", 15);
        nbttaglist.appendTag(patterntag);
        // BLACK TOP BOX
        patterntag = new NBTTagCompound();
        patterntag.setString("Pattern", "ts");
        patterntag.setInteger("Color", 0);
        nbttaglist.appendTag(patterntag);
        // BLACK DIAMOND
        patterntag = new NBTTagCompound();
        patterntag.setString("Pattern", "mr");
        patterntag.setInteger("Color", 0);
        nbttaglist.appendTag(patterntag);
        // GRAY CREEPER
        patterntag = new NBTTagCompound();
        patterntag.setString("Pattern", "cre");
        patterntag.setInteger("Color", 7);
        nbttaglist.appendTag(patterntag);
        // WHITE POLE
        patterntag = new NBTTagCompound();
        patterntag.setString("Pattern", "cs");
        patterntag.setInteger("Color", 15);
        nbttaglist.appendTag(patterntag);
        // BLACK BASE
        patterntag = new NBTTagCompound();
        patterntag.setString("Pattern", "bs");
        patterntag.setInteger("Color", 0);
        nbttaglist.appendTag(patterntag);
        // WHITE CREEPER
        patterntag = new NBTTagCompound();
        patterntag.setString("Pattern", "cre");
        patterntag.setInteger("Color", 15);
        nbttaglist.appendTag(patterntag);
        // WHITE TRI
        patterntag = new NBTTagCompound();
        patterntag.setString("Pattern", "tt");
        patterntag.setInteger("Color", 15);
        nbttaglist.appendTag(patterntag);
        // BLACK TLEFT
        patterntag = new NBTTagCompound();
        patterntag.setString("Pattern", "tl");
        patterntag.setInteger("Color", 0);
        nbttaglist.appendTag(patterntag);
        // BLACK TRIGHT
        patterntag = new NBTTagCompound();
        patterntag.setString("Pattern", "tr");
        patterntag.setInteger("Color", 0);
        nbttaglist.appendTag(patterntag);
        // DARK GREEN WAVES
        patterntag = new NBTTagCompound();
        patterntag.setString("Pattern", "tts");
        patterntag.setInteger("Color", 2);
        nbttaglist.appendTag(patterntag);

        nbttagcompound.setTag("Patterns", nbttaglist);
        
        ItemStack banner = ItemBanner.makeBanner(EnumDyeColor.GREEN, nbttaglist);
        banner.setTagInfo("BlockEntityTag", nbttagcompound);
        return banner;
    }
	
	public static void setBlackBanner(World world, BlockPos pos, EnumFacing facing)
	{
    	ItemBanner ba = new ItemBanner();
        ba.placeBlockAt(getBlackBanner(), null, world, pos, facing, 0.0F, 0.0F, 0.0F, Blocks.STANDING_BANNER.getDefaultState());
	}
	
	// ================ GREEN ================
	public static ItemStack getGreenBanner()
	{
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		nbttagcompound.setInteger("Base", 2);
		
    	NBTTagCompound patterntag = new NBTTagCompound();
        NBTTagList nbttaglist = new NBTTagList();

        // BLACK BASE
        patterntag.setString("Pattern", "bs");
        patterntag.setInteger("Color", 0);
        nbttaglist.appendTag(patterntag);
        // BROWN POLE
        patterntag = new NBTTagCompound();
        patterntag.setString("Pattern", "ms");
        patterntag.setInteger("Color", 3);
        nbttaglist.appendTag(patterntag);
        // DARK GREEN TOP
        patterntag = new NBTTagCompound();
        patterntag.setString("Pattern", "hh");
        patterntag.setInteger("Color", 2);
        nbttaglist.appendTag(patterntag);
        // DARK GREEN CREEPER
        patterntag = new NBTTagCompound();
        patterntag.setString("Pattern", "cre");
        patterntag.setInteger("Color", 2);
        nbttaglist.appendTag(patterntag);
        // DARK GREEN SKULL
        patterntag = new NBTTagCompound();
        patterntag.setString("Pattern", "cre");
        patterntag.setInteger("Color", 2);
        nbttaglist.appendTag(patterntag);
        // GREEN FLOWER
        patterntag = new NBTTagCompound();
        patterntag.setString("Pattern", "flo");
        patterntag.setInteger("Color", 10);
        nbttaglist.appendTag(patterntag);
        // GREEN DIAMOND
        patterntag = new NBTTagCompound();
        patterntag.setString("Pattern", "mr");
        patterntag.setInteger("Color", 10);
        nbttaglist.appendTag(patterntag);
        
        nbttagcompound.setTag("Patterns", nbttaglist);
        
        ItemStack banner = ItemBanner.makeBanner(EnumDyeColor.GREEN, nbttaglist);
        banner.setTagInfo("BlockEntityTag", nbttagcompound);
        return banner;        
	}
	
	public static void setGreenBanner( World world, BlockPos pos, EnumFacing facing )
	{
        ItemBanner ba = new ItemBanner();
        ba.placeBlockAt(getGreenBanner(), null, world, pos, EnumFacing.EAST, 0.0F, 0.0F, 0.0F, Blocks.STANDING_BANNER.getDefaultState());
	}
	
	// ================ BLUE ================
	public static ItemStack getBlueBanner()
	{
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		nbttagcompound.setInteger("Base", 4);
		
    	NBTTagCompound patterntag = new NBTTagCompound();
        NBTTagList nbttaglist = new NBTTagList();

        //
        patterntag.setString("Pattern", "cbo");
        patterntag.setInteger("Color", 4);
        nbttaglist.appendTag(patterntag);
        //
        patterntag = new NBTTagCompound();
        patterntag.setString("Pattern", "bo");
        patterntag.setInteger("Color", 4);
        nbttaglist.appendTag(patterntag);
        //
        patterntag = new NBTTagCompound();
        patterntag.setString("Pattern", "rud");
        patterntag.setInteger("Color", 12);
        nbttaglist.appendTag(patterntag);
        //
        patterntag = new NBTTagCompound();
        patterntag.setString("Pattern", "moj");
        patterntag.setInteger("Color", 4);
        nbttaglist.appendTag(patterntag);
        //
        patterntag = new NBTTagCompound();
        patterntag.setString("Pattern", "bs");
        patterntag.setInteger("Color", 4);
        nbttaglist.appendTag(patterntag);
        //
        patterntag = new NBTTagCompound();
        patterntag.setString("Pattern", "ts");
        patterntag.setInteger("Color", 12);
        nbttaglist.appendTag(patterntag);
        // GRADIENT WHITE
//        patterntag = new NBTTagCompound();
//        patterntag.setString("Pattern", "gra");
//        patterntag.setInteger("Color", 15);
//        nbttaglist.appendTag(patterntag);

        nbttagcompound.setTag("Patterns", nbttaglist);
                
        ItemStack banner = ItemBanner.makeBanner(EnumDyeColor.BLUE, nbttaglist);
        banner.setTagInfo("BlockEntityTag", nbttagcompound);
        return banner;
    }
	
	public static void setBlueBanner( World world, BlockPos pos, EnumFacing facing )
	{
        ItemBanner ba = new ItemBanner();
        ba.placeBlockAt(getBlueBanner(), null, world, pos, EnumFacing.EAST, 0.0F, 0.0F, 0.0F, Blocks.STANDING_BANNER.getDefaultState());
	}
	
	// ================ BROWN ================
	public static ItemStack getBrownBanner()
	{
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		nbttagcompound.setInteger("Base", 3);
		
    	NBTTagCompound patterntag = new NBTTagCompound();
        NBTTagList nbttaglist = new NBTTagList();

        // GRADIENT BLUE
        patterntag = new NBTTagCompound();
        patterntag.setString("Pattern", "gru");
        patterntag.setInteger("Color", 15);
        nbttaglist.appendTag(patterntag);
        // CIRCLE
        patterntag = new NBTTagCompound();
        patterntag.setString("Pattern", "mc");
        patterntag.setInteger("Color", 15);
        nbttaglist.appendTag(patterntag);
        // TRIANGLE BROWN
        patterntag = new NBTTagCompound();
        patterntag.setString("Pattern", "bt");
        patterntag.setInteger("Color", 3);
        nbttaglist.appendTag(patterntag);
        // SPIKES WHITE
        patterntag = new NBTTagCompound();
        patterntag.setString("Pattern", "bts");
        patterntag.setInteger("Color", 15);
        nbttaglist.appendTag(patterntag);
        // SPIKES BROWN
        patterntag = new NBTTagCompound();
        patterntag.setString("Pattern", "bts");
        patterntag.setInteger("Color", 3);
        nbttaglist.appendTag(patterntag);
        
        nbttagcompound.setTag("Patterns", nbttaglist);
        
        ItemStack banner = ItemBanner.makeBanner(EnumDyeColor.BROWN, nbttaglist);
        banner.setTagInfo("BlockEntityTag", nbttagcompound);
        return banner;
	}
	
	public static void setBrownBanner( World world, BlockPos pos, EnumFacing facing )
	{
        ItemBanner ba = new ItemBanner();
	    ba.placeBlockAt(getBrownBanner(), null, world, pos, facing, 0.0F, 0.0F, 0.0F, Blocks.STANDING_BANNER.getDefaultState());
	}

	// ================ RED ================
	
	public static ItemStack getRedBanner()
	{
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		nbttagcompound.setInteger("Base", 1);
		
    	NBTTagCompound patterntag = new NBTTagCompound();
        NBTTagList nbttaglist = new NBTTagList();

        //
        patterntag.setString("Pattern", "flo");
        patterntag.setInteger("Color", 2);
        nbttaglist.appendTag(patterntag);
        //
        patterntag = new NBTTagCompound();
        patterntag.setString("Pattern", "moj");
        patterntag.setInteger("Color", 10);
        nbttaglist.appendTag(patterntag);
        //
        patterntag = new NBTTagCompound();
        patterntag.setString("Pattern", "lud");
        patterntag.setInteger("Color", 1);
        nbttaglist.appendTag(patterntag);
        //
        patterntag = new NBTTagCompound();
        patterntag.setString("Pattern", "tl");
        patterntag.setInteger("Color", 9);
        nbttaglist.appendTag(patterntag);
        //
        patterntag = new NBTTagCompound();
        patterntag.setString("Pattern", "tts");
        patterntag.setInteger("Color", 1);
        nbttaglist.appendTag(patterntag);
        //
        patterntag = new NBTTagCompound();
        patterntag.setString("Pattern", "cbo");
        patterntag.setInteger("Color", 1);
        nbttaglist.appendTag(patterntag);
        //

        nbttagcompound.setTag("Patterns", nbttaglist);
        
        ItemStack banner = ItemBanner.makeBanner(EnumDyeColor.RED, nbttaglist);
        banner.setTagInfo("BlockEntityTag", nbttagcompound);
        return banner;
	}
	
//	public static void setBanner( World world, BlockPos pos, EnumFacing facing )
//	{
//		try
//		{
//			CivilizationType civ = CivilizationUtil.getProvinceAt(world, pos.getX()/16, pos.getZ()/16).civilization;
//			switch ( civ )
//			{
//				case FIRE:
//				{
//					setRedBanner(world, pos, facing);
//					break;
//				}
//				case EARTH:
//				{
//					setGreenBanner(world, pos, facing);
//					break;
//				}
//				case WATER:
//				{
//					setBlueBanner(world, pos, facing);
//					break;
//				}
//				case MOON:
//				{
//					setBlackBanner(world, pos, facing);
//					break;
//				}
//				case WIND:
//				{
//					setBrownBanner(world, pos, facing);
//					break;
//				}
//				case SUN:
//				{
//					setYellowBanner(world, pos, facing);
//					break;
//				}
//				default:
//				{
//					world.setBlockState(pos, Blocks.AIR.getDefaultState());
//					break;
//				}
//			}
//		}
//		catch (Exception e)
//		{
//			
//		}
//	}
	
	public static void setBannerRotation( World world, BlockPos pos, EnumFacing facing )
	{
		ItemBanner ba = new ItemBanner();
		
		Rotation rotation = Rotation.NONE;
		
		switch ( facing )
		{
			case NORTH:
			{
				rotation = Rotation.CLOCKWISE_180;
				break;
			}
			case EAST:
			{
				rotation = Rotation.COUNTERCLOCKWISE_90;
				break;
			}
			case SOUTH:
			{
				rotation = Rotation.NONE;
				break;
			}
			case WEST:
			{
				rotation = Rotation.CLOCKWISE_90;
				break;
			}
			default:
			{
				rotation = Rotation.NONE;
				break;
			}
		}
		
		ItemStack banner = ItemStack.EMPTY;
		
		try
		{
			CivilizationType civ = CivilizationUtil.getProvinceAt(world, pos.getX()/16, pos.getZ()/16).civilization;
			switch ( civ )
			{
				case FIRE:
				{
				    banner = getRedBanner();
				    break;
				}
				case EARTH:
				{
					banner = getGreenBanner();
					break;
				}
				case WATER:
				{
					banner = getBlueBanner();
					break;
				}
				case MOON:
				{
					banner = getBlackBanner();
					break;
				}
				case WIND:
				{
					banner = getBrownBanner();
					break;
				}
				case SUN:
				{
					banner = getYellowBanner();
					break;
				}
				default:
				{
					world.setBlockState(pos, Blocks.AIR.getDefaultState());
					return;
				}
			}
		}
		catch (Exception e)
		{
			
		}
	    ba.placeBlockAt(banner, null, world, pos, facing, 0.0F, 0.0F, 0.0F, Blocks.WALL_BANNER.getDefaultState().withRotation(rotation));
	    ItemBanner.setTileEntityNBT(world, null, pos, banner);
	}

	public static boolean setb(World worldIn, @Nullable EntityPlayer player, BlockPos pos, ItemStack stackIn)
    {
        
        {
            NBTTagCompound nbttagcompound = stackIn.getSubCompound("BlockEntityTag");

            if (nbttagcompound != null)
            {
                TileEntity tileentity = worldIn.getTileEntity(pos);

                if (tileentity != null)
                {
                    

                    NBTTagCompound nbttagcompound1 = tileentity.writeToNBT(new NBTTagCompound());
                    NBTTagCompound nbttagcompound2 = nbttagcompound1.copy();
                    nbttagcompound1.merge(nbttagcompound);
                    nbttagcompound1.setInteger("x", pos.getX());
                    nbttagcompound1.setInteger("y", pos.getY());
                    nbttagcompound1.setInteger("z", pos.getZ());

                    if (!nbttagcompound1.equals(nbttagcompound2))
                    {
                        tileentity.readFromNBT(nbttagcompound1);
                        tileentity.markDirty();
                        return true;
                    }
                }
            }

            return false;
        }
    }
	
	// ================ Yellow ================
	public static ItemStack getYellowBanner()
	{
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		nbttagcompound.setInteger("Base", 14);
		
    	NBTTagCompound patterntag = new NBTTagCompound();
        NBTTagList nbttaglist = new NBTTagList();

        // RED CROSS
        patterntag.setString("Pattern", "sc");
        patterntag.setInteger("Color", 1);
        nbttaglist.appendTag(patterntag);
        // ORANGE BORDER
        patterntag.setString("Pattern", "cbo");
        patterntag.setInteger("Color", 14);
        nbttaglist.appendTag(patterntag);
        // YELLOW BRICKS
        patterntag.setString("Pattern", "bri");
        patterntag.setInteger("Color", 11);
        nbttaglist.appendTag(patterntag);
        // RED FLOWER
        patterntag.setString("Pattern", "flo");
        patterntag.setInteger("Color", 1);
        nbttaglist.appendTag(patterntag);
        // ORANGE GRADIENT
        patterntag.setString("Pattern", "gra");
        patterntag.setInteger("Color", 14);
        nbttaglist.appendTag(patterntag);
        // YELLOW CIRCLE
        patterntag.setString("Pattern", "mc");
        patterntag.setInteger("Color", 11);
        nbttaglist.appendTag(patterntag);

        nbttagcompound.setTag("Patterns", nbttaglist);
        
        ItemStack banner = ItemBanner.makeBanner(EnumDyeColor.ORANGE, nbttaglist);
        banner.setTagInfo("BlockEntityTag", nbttagcompound);
        return banner;
    }
	
	public static void setYellowBanner( World world, BlockPos pos, EnumFacing facing )
	{
		ItemBanner ba = new ItemBanner();        
        ba.placeBlockAt(getRedBanner(), null, world, pos, facing, 0.0F, 0.0F, 0.0F, Blocks.STANDING_BANNER.getDefaultState());
	}
	
	
	
	
	
	
	
	
	
	
	
	
	// BANDIT
	public static void setBanditBanner( World world, BlockPos pos )
	{
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		nbttagcompound.setInteger("Base", 0);
		
    	NBTTagCompound patterntag = new NBTTagCompound();
        NBTTagList nbttaglist = new NBTTagList();

        //
        patterntag.setString("Pattern", "flo");
        patterntag.setInteger("Color", 2);
        nbttaglist.appendTag(patterntag);
        //
        patterntag = new NBTTagCompound();
        patterntag.setString("Pattern", "moj");
        patterntag.setInteger("Color", 10);
        nbttaglist.appendTag(patterntag);
        //
        patterntag = new NBTTagCompound();
        patterntag.setString("Pattern", "lud");
        patterntag.setInteger("Color", 15);
        nbttaglist.appendTag(patterntag);
        //
        patterntag = new NBTTagCompound();
        patterntag.setString("Pattern", "tl");
        patterntag.setInteger("Color", 1);
        nbttaglist.appendTag(patterntag);
        //
        patterntag = new NBTTagCompound();
        patterntag.setString("Pattern", "tts");
        patterntag.setInteger("Color", 15);
        nbttaglist.appendTag(patterntag);
        //
        patterntag = new NBTTagCompound();
        patterntag.setString("Pattern", "cbo");
        patterntag.setInteger("Color", 15);
        nbttaglist.appendTag(patterntag);
        //

        nbttagcompound.setTag("Patterns", nbttaglist);
        
        //banner.setTagInfo("BlockEntityTag", nbttagcompound);
        
        ItemBanner ba = new ItemBanner();
        ItemStack banner = ItemBanner.makeBanner(EnumDyeColor.WHITE, nbttaglist);
        banner.setTagInfo("BlockEntityTag", nbttagcompound);
        
        ba.placeBlockAt(banner, null, world, pos, EnumFacing.EAST, 0.0F, 0.0F, 0.0F, Blocks.STANDING_BANNER.getDefaultState());
	}
	
	
	
	
}
