package net.torocraft.toroquest.generation.village;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
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
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry.IVillageCreationHandler;
import net.torocraft.toroquest.ToroQuest;
import net.torocraft.toroquest.config.ToroQuestConfiguration;
import net.torocraft.toroquest.entities.EntityGuard;
import net.torocraft.toroquest.entities.EntitySentry;
import net.torocraft.toroquest.entities.EntityShopkeeper;
import net.torocraft.toroquest.generation.village.util.BlockMapMeasurer;
import net.torocraft.toroquest.generation.village.util.VillagePieceBlockMap;

public class VillageHandlerShop implements IVillageCreationHandler {

	protected static final String NAME = "shop";

	public static void init()
	{
		MapGenStructureIO.registerStructureComponent(VillagePieceShop.class, NAME);
		//MapGenStructureIO.registerStructureComponent(VillagePieceShop.class, NAME + "_destroyed");
		VillagerRegistry.instance().registerVillageCreationHandler(new VillageHandlerShop());
	}

	@Override
	public PieceWeight getVillagePieceWeight(Random random, int i) {
		return new PieceWeight(VillagePieceShop.class, 30, 2);
	}

	@Override
	public Class<?> getComponentClass() {
		return VillagePieceShop.class;
	}

	@Override
	public Village buildComponent(PieceWeight villagePiece, Start startPiece, List<StructureComponent> pieces, Random random, int p1, int p2, int p3, EnumFacing facing, int p5)
	{
		return VillagePieceShop.createPiece(startPiece, pieces, random, p1, p2, p3, facing, p5);
	}

	public static class VillagePieceShop extends VillagePieceBlockMap
	{

		@Override
		protected int getYOffset()
		{
			return -2;
		}

		public static VillagePieceShop createPiece(StructureVillagePieces.Start start, List<StructureComponent> structures, Random rand, int x, int y, int z, EnumFacing facing, int p_175850_7_)
		{
			if ( ToroQuestConfiguration.disableShop ) return null;
			int i = ToroQuestConfiguration.destroyedVillagesNearSpawnDistance;
			String nameType = NAME;
			if ( i > 0 && Math.abs(x) < i && Math.abs(z) < i )
			{
				 nameType += "_destroyed";
			}
			
			BlockPos size = new BlockMapMeasurer(nameType).measure();
			StructureBoundingBox bounds = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, size.getX(), size.getY(), size.getZ(), facing);
			return canVillageGoDeeper(bounds) && StructureComponent.findIntersecting(structures, bounds) == null ? new VillagePieceShop( nameType, start, p_175850_7_, rand, bounds, facing ) : null;
		}

		public VillagePieceShop( String name, Start start, int type, Random rand, StructureBoundingBox bounds, EnumFacing facing )
		{
			super(name, start, type, rand, bounds, facing);
		}
		
		public VillagePieceShop()
		{
			super();
		}

		@Override
		protected boolean specialBlockHandling(World world, String c, int x, int y, int z)
		{
			if ( c.equals("xx") )
			{
				List<String> entities = new ArrayList<String>();
				entities.add(ToroQuest.MODID + ":" + EntityShopkeeper.NAME);
				return specialHandlingForSpawner(world, "xx", c, x, y, z, entities);
			}
			if ( c.equals("XX") )
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
			else if ( c.equals("cv") )
			{
				setChestBlockState(world, Blocks.TRAPPED_CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.NORTH), x, y, z, boundingBox);
				// world.setBlockState( new BlockPos( x, y, z ), Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.WEST));
				return true;
			}
			else if ( c.equals("c^") )
			{
				setChestBlockState(world, Blocks.TRAPPED_CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.SOUTH), x, y, z, boundingBox);
				// world.setBlockState( new BlockPos( x, y, z ), Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.WEST));
				return true;
			}
			else if ( c.equals("c>") )
			{
				setChestBlockState(world, Blocks.TRAPPED_CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.EAST), x, y, z, boundingBox);
				// world.setBlockState( new BlockPos( x, y, z ), Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.WEST));
				return true;
			}
			else if ( c.equals("c<") )
			{
				setChestBlockState(world, Blocks.TRAPPED_CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.WEST), x, y, z, boundingBox);
				// world.setBlockState( new BlockPos( x, y, z ), Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.WEST));
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
	        	setBlockState( worldIn, blockstateIn, x, y, z, boundingBox );
				TileEntity tileentity = worldIn.getTileEntity( blockpos );
				if ( tileentity instanceof TileEntityChest )
				{
					//if ( !worldIn.isRemote )
					{
						//((TileEntityChest) tileentity).setLootTable(LootTableList.CHESTS_SIMPLE_DUNGEON, worldIn.rand.nextLong());
						TileEntityChest t = (TileEntityChest) tileentity;
						for ( int i = worldIn.rand.nextInt(5)+3; i > 0; i-- )
						{
							setSlot(t, worldIn.rand.nextInt(27), new ItemStack(Items.PAPER, worldIn.rand.nextInt(5)+1) );
						}
						for ( int i = worldIn.rand.nextInt(5)+1; i > 0; i-- )
						{
//							if ( worldIn.rand.nextInt(3) == 0 )
//							{
//								setSlot(t, worldIn.rand.nextInt(27), new ItemStack(Items.EMERALD, 5));
//							}
//							else
							{
								setSlot(t, worldIn.rand.nextInt(27), new ItemStack(Items.EMERALD, 1));
							}
						}
						for ( int i = worldIn.rand.nextInt(3); i > 0; i-- )
						{
							setSlot(t, worldIn.rand.nextInt(27), new ItemStack(Items.BOOK, 1 ));
							setSlot(t, worldIn.rand.nextInt(27), new ItemStack(Items.FEATHER, 1 ));
						}
						for ( int i = worldIn.rand.nextInt(2); i > 0; i-- )
						{
							setSlot(t, worldIn.rand.nextInt(27), new ItemStack(Items.WRITABLE_BOOK, 1 ));
						}
						for ( int i = worldIn.rand.nextInt(3)+3; i > 0; i-- )
						{
							if ( worldIn.rand.nextInt(3) == 0 )
							{
								setSlot(t, worldIn.rand.nextInt(27), new ItemStack(Items.EXPERIENCE_BOTTLE, 5));
							}
							else
							{
								setSlot(t, worldIn.rand.nextInt(27), new ItemStack(Items.EXPERIENCE_BOTTLE, 1));
							}
						}
						for ( int i = worldIn.rand.nextInt(2); i > 0; i-- )
						{
							setSlot(t, worldIn.rand.nextInt(27), new ItemStack( (Items.GOLD_INGOT), 1 ));
						}
						for ( int i = worldIn.rand.nextInt(4); i > 0; i-- )
						{
							setSlot(t, worldIn.rand.nextInt(27), new ItemStack( (Items.GOLD_NUGGET), worldIn.rand.nextInt(5)+1 ));
						}
						
//						Province province = CivilizationUtil.getProvinceAt( worldIn, x/16, z/16);
//						
//						if ( province != null && province.civilization != null )
//						{
//							switch ( province.civilization )
//							{
//								case EARTH:
//								{
//									ItemScrollEarth scroll = (ItemScrollEarth)Item.getByNameOrId("toroquest:scroll_earth");
//									ItemStack itemstack = new ItemStack(scroll,worldIn.rand.nextInt(3)+1);
//									itemstack.setTagInfo("province", new NBTTagString(province.id.toString()));
//									itemstack.setTagInfo("province_name", new NBTTagString(province.name.toString()));
//									itemstack.setStackDisplayName("Teleport scroll:  " + province.name);
//									setSlot(t, worldIn.rand.nextInt(27), itemstack);
//	
//									ItemStack banner = VillagePieceBlockMap.getGreenBanner();
//									banner.setStackDisplayName(ToroQuestConfiguration.greenName + " Banner");
//									setSlot(t, worldIn.rand.nextInt(27), itemstack);
//	
//									break;
//								}
//								case FIRE:
//								{
//									ItemScrollFire scroll = (ItemScrollFire)Item.getByNameOrId("toroquest:scroll_fire");
//									ItemStack itemstack = new ItemStack(scroll,worldIn.rand.nextInt(3)+1);
//									itemstack.setTagInfo("province", new NBTTagString(province.id.toString()));
//									itemstack.setTagInfo("province_name", new NBTTagString(province.name.toString()));
//									itemstack.setStackDisplayName("Teleport scroll:  " + province.name);
//									setSlot(t, worldIn.rand.nextInt(27), itemstack);
//	
//									ItemStack banner = VillagePieceBlockMap.getGreenBanner();
//									banner.setStackDisplayName(ToroQuestConfiguration.redName + " Banner");
//									setSlot(t, worldIn.rand.nextInt(27), itemstack);
//									
//									break;
//								}
//								case SUN:
//								{
//									ItemScrollSun scroll = (ItemScrollSun)Item.getByNameOrId("toroquest:scroll_sun");
//									ItemStack itemstack = new ItemStack(scroll,worldIn.rand.nextInt(3)+1);
//									itemstack.setTagInfo("province", new NBTTagString(province.id.toString()));
//									itemstack.setTagInfo("province_name", new NBTTagString(province.name.toString()));
//									itemstack.setStackDisplayName("Teleport scroll:  " + province.name);
//									setSlot(t, worldIn.rand.nextInt(27), itemstack);
//	
//									ItemStack banner = VillagePieceBlockMap.getGreenBanner();
//									banner.setStackDisplayName(ToroQuestConfiguration.yellowName + " Banner");
//									setSlot(t, worldIn.rand.nextInt(27), itemstack);
//									
//									break;
//								}
//								case WATER:
//								{
//									ItemScrollWater scroll = (ItemScrollWater)Item.getByNameOrId("toroquest:scroll_water");
//									ItemStack itemstack = new ItemStack(scroll,worldIn.rand.nextInt(3)+1);
//									itemstack.setTagInfo("province", new NBTTagString(province.id.toString()));
//									itemstack.setTagInfo("province_name", new NBTTagString(province.name.toString()));
//									itemstack.setStackDisplayName("Teleport scroll:  " + province.name);
//									setSlot(t, worldIn.rand.nextInt(27), itemstack);
//	
//									ItemStack banner = VillagePieceBlockMap.getGreenBanner();
//									banner.setStackDisplayName(ToroQuestConfiguration.blueName + " Banner");
//									setSlot(t, worldIn.rand.nextInt(27), itemstack);
//									
//									break;
//								}
//								case MOON:
//								{
//									ItemScrollMoon scroll = (ItemScrollMoon)Item.getByNameOrId("toroquest:scroll_moon");
//									ItemStack itemstack = new ItemStack(scroll,worldIn.rand.nextInt(3)+1);
//									itemstack.setTagInfo("province", new NBTTagString(province.id.toString()));
//									itemstack.setTagInfo("province_name", new NBTTagString(province.name.toString()));
//									itemstack.setStackDisplayName("Teleport scroll:  " + province.name);
//									setSlot(t, worldIn.rand.nextInt(27), itemstack);
//	
//									ItemStack banner = VillagePieceBlockMap.getGreenBanner();
//									banner.setStackDisplayName(ToroQuestConfiguration.blackName + " Banner");
//									setSlot(t, worldIn.rand.nextInt(27), itemstack);
//									
//									break;
//								}
//								case WIND:
//								{
//									ItemScrollWind scroll = (ItemScrollWind)Item.getByNameOrId("toroquest:scroll_wind");
//									ItemStack itemstack = new ItemStack(scroll,worldIn.rand.nextInt(3)+1);
//									itemstack.setTagInfo("province", new NBTTagString(province.id.toString()));
//									itemstack.setTagInfo("province_name", new NBTTagString(province.name.toString()));
//									itemstack.setStackDisplayName("Teleport scroll:  " + province.name);
//									setSlot(t, worldIn.rand.nextInt(27), itemstack);
//	
//									ItemStack banner = VillagePieceBlockMap.getGreenBanner();
//									banner.setStackDisplayName(ToroQuestConfiguration.brownName + " Banner");
//									setSlot(t, worldIn.rand.nextInt(27), itemstack);
//									
//									break;
//								}
//								default:
//								{
//									break;
//								}
//							}
//						}
					}
				}
	        }
	    }

//		protected void setChest( World world, int x, int y, int z )
//		{
//			// if ( world.isRemote ) return;
//			// world.setTileEntity( new BlockPos( x, y+1, z ), new TileEntityChest() );
//			TileEntity tileentity = world.getTileEntity( new BlockPos( x, y, z ) );
//			System.out.println(tileentity);
//			if (tileentity instanceof TileEntityChest)
//			{
//				System.out.println("isinst");
//
//				//((TileEntityChest) tileentity).setLootTable(LootTableList.CHESTS_STRONGHOLD_LIBRARY, world.rand.nextLong());
//				 // ((TileEntityChest) tileentity).setLootTable(LootTableList.CHESTS_END_CITY_TREASURE, world.rand.nextLong());
//				 
//			}
//			
//			
//			world.setBlockState(new BlockPos( x, y+2, z ), Blocks.CHEST.getDefaultState());
//			TileEntity tileentity1 = world.getTileEntity(new BlockPos( x, y, z ));
//			if (tileentity instanceof TileEntityChest)
//			{
//				((TileEntityChest) tileentity1).setLootTable(LootTableList.CHESTS_SIMPLE_DUNGEON, world.rand.nextLong());
//			}
//			for (int i = 0; i < 17; i++)
//			world.setBlockState(new BlockPos( x, y+8+i, z ), Blocks.STONE.getDefaultState(), i);
//			
//		}

		@Override
		protected void alterPalette(Map<String, IBlockState> palette)
		{
			//palette.put("Bs", Blocks.BOOKSHELF.getDefaultState());
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