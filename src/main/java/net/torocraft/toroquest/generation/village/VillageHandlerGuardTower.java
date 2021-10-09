package net.torocraft.toroquest.generation.village;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.StructureVillagePieces.PieceWeight;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;
import net.minecraft.world.gen.structure.StructureVillagePieces.Village;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry.IVillageCreationHandler;
import net.torocraft.toroquest.ToroQuest;
import net.torocraft.toroquest.config.ToroQuestConfiguration;
import net.torocraft.toroquest.entities.EntityGuard;
import net.torocraft.toroquest.entities.EntitySentry;
import net.torocraft.toroquest.generation.village.util.BlockMapMeasurer;
import net.torocraft.toroquest.generation.village.util.VillagePieceBlockMap;

public class VillageHandlerGuardTower implements IVillageCreationHandler
{

	protected static final String NAME = "guardTower";

	public static void init()
	{
		MapGenStructureIO.registerStructureComponent(VillagePieceGuardTower.class, NAME);
		//MapGenStructureIO.registerStructureComponent(VillagePieceGuardTower.class, NAME + "_destroyed");
		VillagerRegistry.instance().registerVillageCreationHandler(new VillageHandlerGuardTower());
	}

	@Override
	public PieceWeight getVillagePieceWeight(Random random, int i)
	{
		return new PieceWeight(VillagePieceGuardTower.class, 15, 3);
	}

	@Override
	public Class<?> getComponentClass()
	{
		return VillagePieceGuardTower.class;
	}

	@Override
	public Village buildComponent(PieceWeight villagePiece, Start startPiece, List<StructureComponent> pieces, Random random, int p1, int p2, int p3, EnumFacing facing, int p5)
	{
		return VillagePieceGuardTower.createPiece(startPiece, pieces, random, p1, p2, p3, facing, p5);
	}

	public static class VillagePieceGuardTower extends VillagePieceBlockMap
	{

		@Override
		protected int getYOffset()
		{
			return -1;
		}
		
		public static VillagePieceGuardTower createPiece(StructureVillagePieces.Start start, List<StructureComponent> structures, Random rand, int x, int y, int z, EnumFacing facing, int p_175850_7_)
		{
			int i = ToroQuestConfiguration.destroyedVillagesNearSpawnDistance;
			String nameType = NAME;
			if ( i > 0 && Math.abs(x) < i && Math.abs(z) < i )
			{
				 nameType += "_destroyed";
			}
			
			BlockPos size = new BlockMapMeasurer(nameType).measure();
			StructureBoundingBox bounds = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, 1, 0, 1, size.getX(), size.getY(), size.getZ(), facing);
			return canVillageGoDeeper(bounds) && StructureComponent.findIntersecting(structures, bounds) == null ? new VillagePieceGuardTower( nameType, start, p_175850_7_, rand, bounds, facing) : null;
		}

		public VillagePieceGuardTower(String name, Start start, int type, Random rand, StructureBoundingBox bounds, EnumFacing facing)
		{
			super(name, start, type, rand, bounds, EnumFacing.NORTH);
		}

		public VillagePieceGuardTower()
		{
			super();
		}

		@Override
		protected boolean specialBlockHandling(World world, String c, int x, int y, int z)
		{
			if ( c.equals("xx") )
			{
				List<String> entities = new ArrayList<String>();
				entities.add(ToroQuest.MODID + ":" + EntityGuard.NAME);
				return specialHandlingForSpawner(world, "xx", c, x, y, z, entities);
			}
			else if ( c.equals("BB") )
			{
				List<String> bandit = new ArrayList<String>();
				bandit.add(ToroQuest.MODID + ":" + EntitySentry.NAME);
				return specialHandlingForSpawner(world, "BB", c, x, y, z, bandit);
			}
			return false;
		}

		@Override
		protected void alterPalette(Map<String, IBlockState> palette) {
			// TODO Auto-generated method stub
			
		}

//		@Override
//		protected void alterPalette(Map<String, IBlockState> palette) {
//			palette.put("Td", Blocks.TRAPDOOR.getDefaultState());
//		}

	}

}