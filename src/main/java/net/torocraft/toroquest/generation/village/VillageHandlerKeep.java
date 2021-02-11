package net.torocraft.toroquest.generation.village;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nullable;

import ibxm.Player;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockColored;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
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
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry.IVillageCreationHandler;
import net.torocraft.toroquest.ToroQuest;
import net.torocraft.toroquest.civilization.CivilizationType;
import net.torocraft.toroquest.civilization.CivilizationUtil;
import net.torocraft.toroquest.civilization.CivilizationsWorldSaveData;
import net.torocraft.toroquest.civilization.Province;
import net.torocraft.toroquest.config.ToroQuestConfiguration;
import net.torocraft.toroquest.entities.EntityGuard;
import net.torocraft.toroquest.entities.EntitySentry;
import net.torocraft.toroquest.entities.EntityVillageLord;
import net.torocraft.toroquest.generation.village.util.BlockMapMeasurer;
import net.torocraft.toroquest.generation.village.util.VillagePieceBlockMap;

public class VillageHandlerKeep implements IVillageCreationHandler
{

	protected static final String NAME = "keep";

	public static void init()
	{
		MapGenStructureIO.registerStructureComponent(VillagePieceKeep.class, NAME);
		//MapGenStructureIO.registerStructureComponent(VillagePieceKeep.class, NAME + "_destroyed");
		VillagerRegistry.instance().registerVillageCreationHandler(new VillageHandlerKeep());
	}

	@Override
	public PieceWeight getVillagePieceWeight(Random random, int i)
	{
		return new PieceWeight(VillagePieceKeep.class, 30, 1);
	}

	@Override
	public Class<?> getComponentClass()
	{
		return VillagePieceKeep.class;
	}

	@Override
	public Village buildComponent(PieceWeight villagePiece, Start startPiece, List<StructureComponent> pieces, Random random, int p1, int p2, int p3, EnumFacing facing, int p5)
	{
		return VillagePieceKeep.createPiece(startPiece, pieces, random, p1, p2, p3, facing, p5);
	}

	public static class VillagePieceKeep extends VillagePieceBlockMap
	{

		@Override
		protected int getYOffset()
		{
			return -1;
		}
		
		public static VillagePieceKeep createPiece(StructureVillagePieces.Start start, List<StructureComponent> structures, Random rand, int x, int y, int z, EnumFacing facing, int p_175850_7_)
		{
			int i = ToroQuestConfiguration.destroyedVillagesNearSpawnDistance;
			String nameType = NAME;
			if ( i >= 0 && Math.abs(x) < i && Math.abs(z) < i )
			{
				 nameType += "_destroyed";
			}
			
			BlockPos size = new BlockMapMeasurer(nameType).measure();
			StructureBoundingBox bounds = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, -6, size.getX(), size.getY(), size.getZ(), EnumFacing.NORTH);
			return canVillageGoDeeper(bounds) && StructureComponent.findIntersecting(structures, bounds) == null ? new VillagePieceKeep(nameType, start, p_175850_7_, rand, bounds, EnumFacing.NORTH) : null;
		}

		public VillagePieceKeep(String name, Start start, int type, Random rand, StructureBoundingBox bounds, EnumFacing facing)
		{
			super(name, start, type, rand, bounds, EnumFacing.NORTH);
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
			else if ( c.equals("xl") )
			{
				List<String> villageLord = new ArrayList<String>();
				villageLord.add(ToroQuest.MODID + ":" + EntityVillageLord.NAME);
				return specialHandlingForSpawner(world, "xl", c, x, y, z, villageLord);
			}
			else if ( c.equals("BB") )
			{
				List<String> bandit = new ArrayList<String>();
				bandit.add(ToroQuest.MODID + ":" + EntitySentry.NAME);
				return specialHandlingForSpawner(world, "BB", c, x, y, z, bandit);
			}
			else if ( c.equals("cv") )
			{
				setChestBlockState(world, Blocks.TRAPPED_CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.NORTH), x, y, z, boundingBox);
				return true;
			}
			else if ( c.equals("c^") )
			{
				setChestBlockState(world, Blocks.TRAPPED_CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.SOUTH), x, y, z, boundingBox);
				return true;
			}
			else if ( c.equals("c>") )
			{
				setChestBlockState(world, Blocks.TRAPPED_CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.EAST), x, y, z, boundingBox);
				return true;
			}
			else if ( c.equals("c<") )
			{
				setChestBlockState(world, Blocks.TRAPPED_CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.WEST), x, y, z, boundingBox);
				return true;
			}
			return false;
		}
		
	    // protected void setBlockState(World worldIn, IBlockState blockstateIn, int x, int y, int z, StructureBoundingBox boundingboxIn)
		
		protected void setChestBlockState(World worldIn, IBlockState blockstateIn, int x, int y, int z, StructureBoundingBox boundingboxIn)
	    {
	        BlockPos blockpos = new BlockPos(this.getXWithOffset(x, z), this.getYWithOffset(y), this.getZWithOffset(x, z));

	        if (boundingboxIn.isVecInside(blockpos))
	        {
	            // worldIn.setBlockState(blockpos, blockstateIn, 2);
	        	
	            setBlockState( worldIn, blockstateIn, x, y, z, boundingBox );
				TileEntity tileentity = worldIn.getTileEntity( blockpos );
				// ((TileEntityChest) tileentity).setLootTable(LootTableList.CHESTS_VILLAGE_BLACKSMITH, worldIn.rand.nextLong());
				if ( tileentity instanceof TileEntityChest )
				{
					TileEntityChest t = (TileEntityChest) tileentity;
					if ( !worldIn.isRemote )
					{
						for ( int i = worldIn.rand.nextInt(5); i > 0; i-- )
						{
							setSlot(t, worldIn.rand.nextInt(27), new ItemStack(Items.PAPER, 1) );
						}
						setSlot(t, worldIn.rand.nextInt(27), new ItemStack( (Items.EMERALD), worldIn.rand.nextInt(5) ));
						if ( worldIn.rand.nextBoolean() ) setSlot(t, worldIn.rand.nextInt(27), new ItemStack( (Items.EMERALD), worldIn.rand.nextInt(5) ));
						if ( worldIn.rand.nextBoolean() ) setSlot(t, worldIn.rand.nextInt(27), new ItemStack( (Items.EMERALD), worldIn.rand.nextInt(5) ));
						if ( worldIn.rand.nextBoolean() ) setSlot(t, worldIn.rand.nextInt(27), new ItemStack( (Item.getByNameOrId("toroquest:recruitment_papers")), 1 ));
						if ( worldIn.rand.nextBoolean() ) setSlot(t, worldIn.rand.nextInt(27), new ItemStack( (Items.IRON_SWORD), 1 ));
						if ( worldIn.rand.nextBoolean() ) setSlot(t, worldIn.rand.nextInt(27), new ItemStack( (Items.SHIELD), 1 ));
					}
				}
	        }
	    }

		public VillagePieceKeep()
		{
			super();
		}

		@Override
		protected void alterPalette(Map<String, IBlockState> palette)
		{
			
		}

	}
	
	public static void setSlot(TileEntityChest chest, int index, @Nullable ItemStack stack)
    {
		if ( chest.getStackInSlot(index).isEmpty() )
		{
			chest.setInventorySlotContents(index, stack);
		}
    }

}