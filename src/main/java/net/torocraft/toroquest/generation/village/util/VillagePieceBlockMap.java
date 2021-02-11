package net.torocraft.toroquest.generation.village.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.BlockQuartz;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.torocraft.toroquest.block.BlockToroSpawner;
import net.torocraft.toroquest.block.TileEntityToroSpawner;
import net.torocraft.toroquest.civilization.CivilizationType;
import net.torocraft.toroquest.civilization.CivilizationUtil;

public abstract class VillagePieceBlockMap extends StructureVillagePieces.Village
{
	static Random rand = new Random();
	public static final Map<String, IBlockState> DEFAULT_PALLETTE;
	
	static 
	{
		DEFAULT_PALLETTE = new HashMap<String, IBlockState>();
		DEFAULT_PALLETTE.put("--", Blocks.AIR.getDefaultState());
		DEFAULT_PALLETTE.put("Cs", Blocks.COBBLESTONE.getDefaultState());
		DEFAULT_PALLETTE.put("Wa", Blocks.WATER.getDefaultState());
		DEFAULT_PALLETTE.put("Dr", Blocks.DIRT.getDefaultState());
		DEFAULT_PALLETTE.put("Gr", Blocks.GRASS.getDefaultState());
		DEFAULT_PALLETTE.put("GP", Blocks.GRASS_PATH.getDefaultState());
		DEFAULT_PALLETTE.put("Sb", Blocks.STONEBRICK.getDefaultState());
		DEFAULT_PALLETTE.put("So", Blocks.STONE.getDefaultState());
		DEFAULT_PALLETTE.put("Pw", Blocks.PLANKS.getDefaultState());
		DEFAULT_PALLETTE.put("Fw", Blocks.OAK_FENCE.getDefaultState());
		DEFAULT_PALLETTE.put("Fc", Blocks.COBBLESTONE_WALL.getDefaultState());
		DEFAULT_PALLETTE.put("Sw", Blocks.WOODEN_SLAB.getDefaultState());
		DEFAULT_PALLETTE.put("td", Blocks.TRAPDOOR.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH));
		DEFAULT_PALLETTE.put("Lg", Blocks.LOG.getDefaultState());
		DEFAULT_PALLETTE.put("l^", Blocks.LADDER.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH));
		DEFAULT_PALLETTE.put("lv", Blocks.LADDER.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.SOUTH));
		DEFAULT_PALLETTE.put("l<", Blocks.LADDER.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.EAST));
		DEFAULT_PALLETTE.put("l>", Blocks.LADDER.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.WEST));
		DEFAULT_PALLETTE.put("Ss", Blocks.STONE_SLAB.getDefaultState());
		DEFAULT_PALLETTE.put("Gp", Blocks.GLASS_PANE.getDefaultState());
		DEFAULT_PALLETTE.put("Gs", Blocks.GLOWSTONE.getDefaultState());
		DEFAULT_PALLETTE.put("Sv", Blocks.OAK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH));
		DEFAULT_PALLETTE.put("S^", Blocks.OAK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.SOUTH));
		DEFAULT_PALLETTE.put("S>", Blocks.OAK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.EAST));
		DEFAULT_PALLETTE.put("S<", Blocks.OAK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.WEST));
		DEFAULT_PALLETTE.put("ww", Blocks.WOOL.getDefaultState());
		DEFAULT_PALLETTE.put("Wp", Blocks.WOODEN_PRESSURE_PLATE.getDefaultState());
		DEFAULT_PALLETTE.put("Gb", Blocks.GOLD_BLOCK.getDefaultState());
		DEFAULT_PALLETTE.put("Qs", Blocks.STONE_SLAB.getDefaultState());
		DEFAULT_PALLETTE.put("Qc", Blocks.QUARTZ_BLOCK.getDefaultState().withProperty(BlockQuartz.VARIANT, BlockQuartz.EnumType.CHISELED));
		DEFAULT_PALLETTE.put("Qx", Blocks.QUARTZ_BLOCK.getDefaultState().withProperty(BlockQuartz.VARIANT, BlockQuartz.EnumType.LINES_Z));
		DEFAULT_PALLETTE.put("Qy", Blocks.QUARTZ_BLOCK.getDefaultState().withProperty(BlockQuartz.VARIANT, BlockQuartz.EnumType.LINES_Y));
		DEFAULT_PALLETTE.put("An", Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.ANDESITE_SMOOTH));
		DEFAULT_PALLETTE.put("Qv", Blocks.QUARTZ_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH));
		DEFAULT_PALLETTE.put("Q^", Blocks.QUARTZ_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.SOUTH));
		DEFAULT_PALLETTE.put("Q>", Blocks.QUARTZ_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.EAST));
		DEFAULT_PALLETTE.put("Q<", Blocks.QUARTZ_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.WEST));
		DEFAULT_PALLETTE.put("Cv", Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH));
		DEFAULT_PALLETTE.put("C^", Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.SOUTH));
		DEFAULT_PALLETTE.put("C>", Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.EAST));
		DEFAULT_PALLETTE.put("C<", Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.WEST));
		DEFAULT_PALLETTE.put("bv", Blocks.BED.getDefaultState().withProperty(BlockBed.FACING, EnumFacing.NORTH));
		DEFAULT_PALLETTE.put("b^", Blocks.BED.getDefaultState().withProperty(BlockBed.FACING, EnumFacing.SOUTH));
		DEFAULT_PALLETTE.put("b>", Blocks.BED.getDefaultState().withProperty(BlockBed.FACING, EnumFacing.EAST));
		DEFAULT_PALLETTE.put("b<", Blocks.BED.getDefaultState().withProperty(BlockBed.FACING, EnumFacing.WEST));
		
		DEFAULT_PALLETTE.put("FF", Blocks.FIRE.getDefaultState());
		
		DEFAULT_PALLETTE.put("dv", Blocks.OAK_DOOR.getDefaultState().withProperty(BlockBed.FACING, EnumFacing.NORTH));
		DEFAULT_PALLETTE.put("d^", Blocks.OAK_DOOR.getDefaultState().withProperty(BlockBed.FACING, EnumFacing.SOUTH));
		DEFAULT_PALLETTE.put("d>", Blocks.OAK_DOOR.getDefaultState().withProperty(BlockBed.FACING, EnumFacing.EAST));
		DEFAULT_PALLETTE.put("d<", Blocks.OAK_DOOR.getDefaultState().withProperty(BlockBed.FACING, EnumFacing.WEST));

		//DEFAULT_PALLETTE.put("sv", Blocks.SPRUCE_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.NORTH));
		//DEFAULT_PALLETTE.put("s^", Blocks.SPRUCE_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.SOUTH));
		//DEFAULT_PALLETTE.put("s>", Blocks.SPRUCE_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.EAST));
		//DEFAULT_PALLETTE.put("s<", Blocks.SPRUCE_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.WEST));
		
		DEFAULT_PALLETTE.put("cv", Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.NORTH));
		DEFAULT_PALLETTE.put("c^", Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.SOUTH));
		DEFAULT_PALLETTE.put("c>", Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.EAST));
		DEFAULT_PALLETTE.put("c<", Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.WEST));

		DEFAULT_PALLETTE.put("t^", Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.NORTH));
		DEFAULT_PALLETTE.put("tv", Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.SOUTH));
		DEFAULT_PALLETTE.put("t<", Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.EAST));
		DEFAULT_PALLETTE.put("t>", Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.WEST));
		DEFAULT_PALLETTE.put("t.", Blocks.TORCH.getDefaultState());
		
		// COLOR SPECFIC																								
		DEFAULT_PALLETTE.put("pR", Blocks.FLOWER_POT.getDefaultState().withProperty(BlockFlowerPot.CONTENTS, BlockFlowerPot.EnumFlowerType.RED_TULIP));
		//DEFAULT_PALLETTE.put("bR", Blocks.WALL_BANNER.getDefaultState().withProperty(BlockBanner.ROTATION, 0).withProperty(BlockColored.COLOR, EnumDyeColor.RED));
		//DEFAULT_PALLETTE.put("bR", Blocks.STANDING_BANNER.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.RED));
		DEFAULT_PALLETTE.put("wR", Blocks.STONE.getDefaultState());
		
		DEFAULT_PALLETTE.put("cR", Blocks.CARPET.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.WHITE));
		
		DEFAULT_PALLETTE.put("*r", Blocks.CARPET.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.RED));
		DEFAULT_PALLETTE.put("*g", Blocks.CARPET.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.GREEN));
		DEFAULT_PALLETTE.put("*u", Blocks.CARPET.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.CYAN));
		DEFAULT_PALLETTE.put("*b", Blocks.CARPET.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.BLACK));
		DEFAULT_PALLETTE.put("*w", Blocks.CARPET.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.BROWN));
		DEFAULT_PALLETTE.put("*y", Blocks.CARPET.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.YELLOW));

		DEFAULT_PALLETTE.put("*R", Blocks.CONCRETE.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.RED));
		DEFAULT_PALLETTE.put("*G", Blocks.CONCRETE.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.GREEN));
		DEFAULT_PALLETTE.put("*U", Blocks.CONCRETE.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.CYAN));
		DEFAULT_PALLETTE.put("*B", Blocks.CONCRETE.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.BLACK));
		DEFAULT_PALLETTE.put("*W", Blocks.CONCRETE.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.BROWN));
		DEFAULT_PALLETTE.put("*Y", Blocks.CONCRETE.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.YELLOW));
		
		DEFAULT_PALLETTE.put("**", Blocks.CONCRETE.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.WHITE));		
	}

	protected String name;

	public VillagePieceBlockMap(String name, StructureVillagePieces.Start start, int type, Random rand, StructureBoundingBox bounds, EnumFacing facing) {
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
						case EARTH:
						{
							return "*g";
						}
						case WATER:
						{
							return "*u";
						}
						case MOON:
						{
							return "*b";
						}
						case WIND:
						{
							return "*w";
						}
						case SUN:
						{
							return "*y";
						}
					}
				}
				catch (Exception e)
				{
					
				}
				return "cR";
			}
			
			
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

}
