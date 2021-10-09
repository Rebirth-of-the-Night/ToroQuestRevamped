package net.torocraft.toroquest.generation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.torocraft.toroquest.block.BlockToroSpawner;
import net.torocraft.toroquest.block.TileEntityToroSpawner;
import net.torocraft.toroquest.civilization.CivilizationsWorldSaveData;
import net.torocraft.toroquest.config.ToroQuestConfiguration;

public class WorldGenPlacer implements IWorldGenerator {

	public static void init()
	{
		GameRegistry.registerWorldGenerator(new WorldGenPlacer(), 2);
	}

	public static final Random random = new Random();

	@Override
	public void generate(Random random, int x, int z, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider)
	{
		if ( world.provider.getDimension() != 0 )
		{
			return;
		}
//		if ( random.nextInt(ToroQuestConfiguration.structureSpawnChance) != 0 || world.provider.getDimension() != 0 )
//		{
//			return;
//		}
//
//		int roll = random.nextInt(5);
//		if ( roll == 0 ) genGraveyard(world, random, x, z);


//		switch (roll) {
//		case 0:
//			//genMonolith(world, random, x, z);
//			break;
//		case 1:
//			//genBastionsLair(world, random, x, z);
//			break;
//		case 2:
//			//genMageTower(world, random, x, z);
//			break;
//		case 3:
//			genBandits(world, random, x, z);
//			break;
//		case 4:
//			genGraveyard(world, random, x, z);
//			break;
//		}
	}
	
	public static void clearTrees( World world, BlockPos start, int radius )
	{
		double x = start.getX();
		double y = start.getY()-32;
		double z = start.getZ();
		for ( int xx = -radius; xx < radius; xx++ )
		{
			for ( int zz = -radius; zz < radius; zz++ )
			{
				int distFromCenter = (int)(MathHelper.sqrt((xx*xx+zz*zz)));
				if ( radius >= distFromCenter )
				{
					for ( int yy = 0; 64 > yy; yy++ )
					{
						BlockPos pos = new BlockPos(x+xx,y+yy,z+zz);
						Block block = world.getBlockState(pos).getBlock();
						if ( block instanceof BlockLeaves || block instanceof BlockLog )
						{
							world.setBlockState(pos, Blocks.AIR.getDefaultState());
						}
					}
				}
			}
		}
	}

	public static void genMageTower(World world, int x, int z)
	{
		if (!CivilizationsWorldSaveData.get(world).canGenStructure("mage_tower", x, z))
		{
			// return;
		}
		BlockPos pos = new BlockPos( x, world.getActualHeight(), z );
		if ( new MageTowerGenerator().generate(world, random, pos) )
		{
			//System.out.println("ToroQuest Gen Placer: Mage Tower " + pos);
		}
	}

	public static void genBastionsLair(World world, int x, int z)
	{
		if (!CivilizationsWorldSaveData.get(world).canGenStructure("bastions_lair", x, z))
		{
			// return;
		}
		BlockPos pos = new BlockPos( x, world.getActualHeight(), z );
		if (new BastionsLairGenerator().generate(world, random, pos))
		{
			//System.out.println("ToroQuest Gen Placer: Bastion's Lair " + pos);
		}
	}
	
	public static void genSpiderLair(World world, int x, int z)
	{
		if (!CivilizationsWorldSaveData.get(world).canGenStructure("spider_lair", x, z))
		{
			// return;
		}
		BlockPos pos = new BlockPos( x, world.getActualHeight(), z );
		if (new SpiderLairGenerator().generate(world, random, pos))
		{
			//System.out.println("ToroQuest Gen Placer: Spider's Lair" + pos);
		}
	}

	public static void genPigPortal(World world, int x, int z)
	{
		if (!CivilizationsWorldSaveData.get(world).canGenStructure("pig_portal", x, z))
		{
			// return;
		}
		BlockPos pos = new BlockPos( x, world.getActualHeight(), z );
		if ( new PigPortalGenerator().generate(world, random, pos) )
		{
			//System.out.println("ToroQuest Gen Placer: Pig Portal" + pos);
		}
		else
		{
			//System.out.println("ERROR SPAWNING: PORTAL");
		}

	}
	
	public static void genMonolith(World world, int x, int z)
	{
		if (!CivilizationsWorldSaveData.get(world).canGenStructure("monolith", x, z))
		{
			// return;
		}
		BlockPos pos = new BlockPos( x, world.getActualHeight(), z );
		if ( new MonolithGenerator().generate( world, random, pos ) )
		{
			//System.out.println("ToroQuest Gen Placer: Monolith " + pos);
		}
	}
	
//	public static void genHouse(World world, int x, int z)
//	{
//		BlockPos pos = new BlockPos( x, world.getActualHeight(), z );
//		if ( new HouseGenerator().generate( world, random, pos ) )
//		{
//			//System.out.println("ToroQuest Gen Placer: Bandits " + pos);
//		}
//	}
	
	public static void genThroneRoom(World world, int x, int z)
	{
		if (!CivilizationsWorldSaveData.get(world).canGenStructure("throne_room", x, z))
		{
			// return;
		}
		BlockPos pos = new BlockPos( x, 40, z );
		if ( new ThroneRoomGenerator().generate(world, random, pos) )
		{
			//System.out.println("ToroQuest Gen Placer: Throne Room " + pos);
		}
	}
	
	public static void genGraveyardTitan(World world, int x, int z)
	{
		if (!CivilizationsWorldSaveData.get(world).canGenStructure("grave_titan", x, z))
		{
			// return;
		}
		BlockPos pos = new BlockPos( x, world.getActualHeight(), z );
		if ( new GraveyardGenerator().generate(world, random, pos) )
		{
			//System.out.println("ToroQuest Gen Placer: GraveyardTitan " + pos);
		}
	}
}
