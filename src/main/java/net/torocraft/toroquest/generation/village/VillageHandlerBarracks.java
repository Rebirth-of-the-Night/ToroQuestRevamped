package net.torocraft.toroquest.generation.village;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
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
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry.IVillageCreationHandler;
import net.torocraft.toroquest.ToroQuest;
import net.torocraft.toroquest.config.ToroQuestConfiguration;
import net.torocraft.toroquest.entities.EntityGuard;
import net.torocraft.toroquest.entities.EntitySentry;
import net.torocraft.toroquest.generation.village.util.BlockMapMeasurer;
import net.torocraft.toroquest.generation.village.util.VillagePieceBlockMap;

public class VillageHandlerBarracks implements IVillageCreationHandler
{

	protected static final String NAME = "barracks";

	public static void init()
	{
		MapGenStructureIO.registerStructureComponent(VillagePieceBarracks.class, NAME);
		//MapGenStructureIO.registerStructureComponent(VillagePieceBarracks.class, NAME + "_destroyed");
		VillagerRegistry.instance().registerVillageCreationHandler(new VillageHandlerBarracks());
	}
	
	@Override
	public PieceWeight getVillagePieceWeight(Random random, int i) {
		return new PieceWeight(VillagePieceBarracks.class, 30, 1);
	}

	@Override
	public Class<?> getComponentClass() {
		return VillagePieceBarracks.class;
	}

	@Override
	public Village buildComponent(PieceWeight villagePiece, Start startPiece, List<StructureComponent> pieces, Random random, int p1, int p2, int p3, EnumFacing facing, int p5) {
		return VillagePieceBarracks.createPiece(startPiece, pieces, random, p1, p2, p3, facing, p5);

	}

	public static class VillagePieceBarracks extends VillagePieceBlockMap
	{

		@Override
		protected int getYOffset() {
			return -1;
		}
		
		public static VillagePieceBarracks createPiece(StructureVillagePieces.Start start, List<StructureComponent> structures, Random rand, int x, int y, int z, EnumFacing facing, int p_175850_7_)
		{
			if ( ToroQuestConfiguration.disableBarracks ) return null;
			int i = ToroQuestConfiguration.destroyedVillagesNearSpawnDistance;
			String nameType = NAME;
			if ( i > 0 && Math.abs(x) < i && Math.abs(z) < i )
			{
				 nameType += "_destroyed";
			}
			
			BlockPos size = new BlockMapMeasurer(nameType).measure();
			
			StructureBoundingBox bounds = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, -2, size.getX(), size.getY(), size.getZ(), EnumFacing.NORTH);
			return canVillageGoDeeper(bounds) && StructureComponent.findIntersecting(structures, bounds) == null ? new VillagePieceBarracks(nameType, start, p_175850_7_, rand, bounds, EnumFacing.NORTH) : null;
		}

		public VillagePieceBarracks( String name, Start start, int type, Random rand, StructureBoundingBox bounds, EnumFacing facing )
		{
			super(name, start, type, rand, bounds, EnumFacing.NORTH);
		}
		
		public VillagePieceBarracks()
		{
			super();
		}

		@Override
		protected boolean specialBlockHandling(World world, String c, int x, int y, int z)
		{
			if ( c.equals("xx") || c.equals("XX") )
			{
			
			
				setBlockState(world, Blocks.AIR.getDefaultState(), x, y, z, boundingBox);
	
				int j = this.getXWithOffset(x, z);
				int k = this.getYWithOffset(y);
				int l = this.getZWithOffset(x, z);
	
				/*
				 * if (!structurebb.isVecInside(new BlockPos(j, k, l))) { return; }
				 */
				if ( c.equals("XX") )
				{
					EntityArmorStand stand = new EntityArmorStand(world);
					stand.setLocationAndAngles((double) j + 0.5D, (double) k, (double) l + 0.5D, 90F, 0.0F);
					world.spawnEntity(stand);
				}
				List<String> entities = new ArrayList<String>();
				entities.add(ToroQuest.MODID + ":" + EntityGuard.NAME);
				specialHandlingForSpawner(world, "xx", c, x, y, z, entities);
				return true;
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
		
		protected void setChestBlockState(World worldIn, IBlockState blockstateIn, int x, int y, int z, StructureBoundingBox boundingboxIn)
	    {
			if ( worldIn.isRemote ) return;

	        BlockPos blockpos = new BlockPos(this.getXWithOffset(x, z), this.getYWithOffset(y), this.getZWithOffset(x, z));

	        if (boundingboxIn.isVecInside(blockpos))
	        {
	            // worldIn.setBlockState(blockpos, blockstateIn, 2);
	        	
	            setBlockState( worldIn, blockstateIn, x, y, z, boundingBox );
				TileEntity tileentity = worldIn.getTileEntity( blockpos );
				//((TileEntityChest) tileentity).setLootTable(LootTableList.CHESTS_VILLAGE_BLACKSMITH, worldIn.rand.nextLong());
				if ( tileentity instanceof TileEntityChest )
				{
					TileEntityChest t = (TileEntityChest) tileentity;
					//if ( !worldIn.isRemote )
					{
						
						if ( worldIn.rand.nextBoolean() )
						{
//							if ( worldIn.rand.nextInt(3) == 0 )
//							{
//								setSlot(t, worldIn.rand.nextInt(27), new ItemStack(Items.BOWL, 1));
//							}
//							if ( worldIn.rand.nextInt(3) == 0 )
//							{
//								setSlot(t, worldIn.rand.nextInt(27), new ItemStack(Items.MUSHROOM_STEW, 1));
//							}
							if ( worldIn.rand.nextInt(4) == 0 )
							{
								setSlot(t, worldIn.rand.nextInt(27), new ItemStack(Items.BREAD, 1));
							}
							for ( int i = worldIn.rand.nextInt(3); i > 0; i-- )
							{
								setSlot(t, worldIn.rand.nextInt(27), new ItemStack(Items.APPLE, worldIn.rand.nextInt(3)+1));
							}
							for ( int i = worldIn.rand.nextInt(3); i > 0; i-- )
							{
								setSlot(t, worldIn.rand.nextInt(27), new ItemStack(Items.BREAD, worldIn.rand.nextInt(3)+1));
							}
						}
						else
						{
							for ( int i = worldIn.rand.nextInt(3)+1; i > 0; i-- )
							{
								setSlot(t, worldIn.rand.nextInt(27), new ItemStack(Items.ARROW, 8));
							}
							for ( int i = worldIn.rand.nextInt(2); i > 0; i-- )
							{
								setSlot(t, worldIn.rand.nextInt(27), new ItemStack(Items.BOW, 1));
							}
							for ( int i = worldIn.rand.nextInt(3); i > 0; i-- )
							{
								setSlot(t, worldIn.rand.nextInt(27), new ItemStack(Items.IRON_SWORD, 1));
							}
							for ( int i = worldIn.rand.nextInt(2); i > 0; i-- )
							{
								Item shield = Item.getByNameOrId("spartanshields:shield_basic_wood");
								if ( shield == null )
								{
									shield = Items.SHIELD;
								}
								setSlot(t, worldIn.rand.nextInt(27), new ItemStack(shield, 1));
							}
							if ( worldIn.rand.nextInt(3) == 0 )
							{
								setSlot(t, worldIn.rand.nextInt(27), new ItemStack(Items.LEATHER_BOOTS, 1));
							}
							if ( worldIn.rand.nextInt(3) == 0 )
							{
								setSlot(t, worldIn.rand.nextInt(27), new ItemStack(Items.CHAINMAIL_CHESTPLATE, 1));
							}
							if ( worldIn.rand.nextInt(3) == 0 )
							{
								setSlot(t, worldIn.rand.nextInt(27), new ItemStack(Items.BOOK, 1));
								setSlot(t, worldIn.rand.nextInt(27), new ItemStack(Items.PAPER, 1));
								setSlot(t, worldIn.rand.nextInt(27), new ItemStack(Items.FEATHER, 1));
							}
						}
					}
				}
	        }
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