package net.torocraft.toroquest.civilization;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockBrewingStand;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockDirt.DirtType;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockGrassPath;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockStone;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBed;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.torocraft.toroquest.config.ToroQuestConfiguration;

public class CivilizationGeneratorHandlers
{

	@SubscribeEvent
	public void registerNewCiviliationBorder(PopulateChunkEvent.Post event)
	{
		registerCiv(event);
	}

	@SubscribeEvent
	public void registerNewCiviliationBorder(PopulateChunkEvent.Pre event)
	{
		registerCiv(event);
	}

	protected void registerCiv(PopulateChunkEvent event)
	{
		if ( !event.isHasVillageGenerated() )
		{
			return;
		}
		
		CivilizationUtil.registerNewCivilization(event.getWorld(), event.getChunkX(), event.getChunkZ());
		
//		BlockPos block = new BlockPos(event.getChunkX()*16, 0, event.getChunkZ()*16);
//		
//		int x = block.getX();
//		int y = 64;
//		int z = block.getZ();
//		int range = 16;
//		for ( int xx = -range/2; xx < range/2; xx++ ) // /2 range
//		{
//			for ( int yy = -range/4; yy < range/4; yy++ )
//			{
//				for ( int zz = -range/2; zz < range/2; zz++ ) // /2
//				{
//					//if ( Math.pow(Math.abs(xx)+7, 2) + Math.pow(Math.abs(zz)+7, 2) <= 554 )
//					{
//						BlockPos pos = new BlockPos(new BlockPos(x+xx,y+yy,z+zz));
//						IBlockState b = event.getWorld().getBlockState(pos);
////						if ( b != Blocks.AIR && ( event.getWorld().rand.nextInt(10) == 0 && (b instanceof BlockStairs || b instanceof BlockStone || b instanceof BlockGlass) ) )
////						{
////							event.getWorld().setBlockState(pos, Blocks.AIR.getDefaultState());
////						}
//						if ( xx == range/2 || xx == -range/2 || zz == -range/2 || zz == range/2 ) event.getWorld().setBlockState(pos, Blocks.STONE.getDefaultState());
////						System.out.println(pos);
//
//					}
//				}
//			}
//		}
	}

	@SubscribeEvent (priority = EventPriority.LOWEST)
	public void registerNewCiviliationBorder2(PopulateChunkEvent.Post event)
	{

		if ( !event.isHasVillageGenerated() )
		{
			return;
		}
		
		int x = (event.getChunkX())*16;
		int y = CivilizationHandlers.MIN_SPAWN_HEIGHT; // BASE
		int z = (event.getChunkZ())*16;
		
		boolean destroyedVillage = ( ToroQuestConfiguration.destroyedVillagesNearSpawnDistance > 0 && Math.abs(x) < ToroQuestConfiguration.destroyedVillagesNearSpawnDistance && Math.abs(z) < ToroQuestConfiguration.destroyedVillagesNearSpawnDistance );
		
		boolean hasVillagerChunk = false;
		
		if ( destroyedVillage )
		{
			List<EntityVillager> villagers = event.getWorld().getEntitiesWithinAABB(EntityVillager.class, new AxisAlignedBB(x-16,y,z-16,x+16,CivilizationHandlers.MAX_SPAWN_HEIGHT,z+16));
			
			for ( EntityVillager villager : villagers )
			{
				villager.setHealth(0.0F);
				villager.setDead();
				hasVillagerChunk = true;
			}
		}
		
		BlockPos pos;
		Block b;

		for ( int xx = 0; xx < 16; xx++ )
		{
			for ( int zz = 0; zz < 16; zz++ )
			{
				for ( int yy = CivilizationHandlers.MAX_SPAWN_HEIGHT; yy >= y; yy-- )
				{
					pos = new BlockPos(new BlockPos(x+xx,yy,z+zz));
					b = event.getWorld().getBlockState(pos).getBlock();
					
					if ( destroyedVillage )
					{
						if ( b == Blocks.AIR )
						{
							continue;
						}
						else if ( b == Blocks.STONE || b instanceof BlockStone || b instanceof BlockDirt ) // GROUND, BREAK LOOP
						{
							break;
						}
						else if ( b instanceof BlockGrass || b instanceof BlockSand ) // GROUND, BREAK LOOP
						{
							if ( hasVillagerChunk && event.getRand().nextInt(128) == 0 )
							{
								if ( event.getWorld().getBlockState(pos.add(1, 1, 0)).getBlock() == Blocks.AIR && event.getWorld().getBlockState(pos.add(0, 1, 1)).getBlock() == Blocks.AIR && event.getWorld().getBlockState(pos.add(-1, 1, 0)).getBlock() == Blocks.AIR && event.getWorld().getBlockState(pos.add(0, 1, -1)).getBlock() == Blocks.AIR)
								{
									event.getWorld().setBlockState(pos.up(2), Blocks.SKULL.getDefaultState().withProperty(BlockSkull.FACING, EnumFacing.UP));
									if ( ToroQuestConfiguration.useIronBarsForHeadSpike ) event.getWorld().setBlockState(pos.up(1), Blocks.IRON_BARS.getDefaultState());
									else event.getWorld().setBlockState(pos.up(1), Blocks.OAK_FENCE.getDefaultState());
									event.getWorld().setBlockState(pos, Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, DirtType.COARSE_DIRT));
								}
							}
							break;
						}
						else if ( b instanceof BlockChest )
						{
							TileEntity tileentity = event.getWorld().getTileEntity(pos);
							if ( tileentity instanceof TileEntityChest )
							{
								((TileEntityChest) tileentity).clear();
								((TileEntityChest) tileentity).markDirty();
							}
							continue;
						}
						else if ( b instanceof BlockBed || b instanceof BlockDoor || b instanceof BlockFurnace || b instanceof BlockAnvil || b instanceof BlockBrewingStand )
						{
							event.getWorld().setBlockState(pos, Blocks.AIR.getDefaultState());
							continue;
						}
						else if ( b instanceof BlockPane )
						{
							if ( event.getWorld().rand.nextBoolean() )
							{
								event.getWorld().setBlockState(pos, Blocks.AIR.getDefaultState());
							}
							continue;
						}
						else if ( event.getWorld().rand.nextInt(5) == 0 )
						{
							if ( b instanceof BlockGrassPath ) // GROUND, BREAK LOOP
							{
								event.getWorld().setBlockState(pos, Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, DirtType.COARSE_DIRT));
								break;
							}
							else if ( b == Blocks.COBBLESTONE )
							{
								if ( event.getWorld().rand.nextBoolean() ) event.getWorld().setBlockState(pos, Blocks.STONE_STAIRS.getDefaultState());
								else if ( event.getWorld().rand.nextBoolean() ) event.getWorld().setBlockState(pos, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.EAST));
								else event.getWorld().setBlockState(pos, Blocks.AIR.getDefaultState());
							}
							else if ( b instanceof BlockStairs && event.getWorld().rand.nextInt(7) == 0 )
							{
								event.getWorld().setBlockState(pos, Blocks.FIRE.getDefaultState());
							}
							else
							{
								event.getWorld().setBlockState(pos, Blocks.AIR.getDefaultState());
							}
							continue;
						}
						continue;
					}
					else // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
					{
						if ( b instanceof BlockChest && ToroQuestConfiguration.replaceVillageChestsWithTrappedChests )
						{
							try
							{
								BlockChest chest = (BlockChest) b;
								if ( chest.chestType != BlockChest.Type.TRAP )
								{
									TileEntity tile = event.getWorld().getTileEntity(pos);
									ResourceLocation lootTable = null;
									ArrayList<ItemStack> lootItems = new ArrayList<ItemStack>();
									
									if ( tile instanceof TileEntityChest )
									{
										lootTable = ((TileEntityChest) tile).getLootTable();
										
										if ( lootTable == null )
										{
											try
											{
												for (int i = 0; i < ((TileEntityChest) tile).getSizeInventory(); ++i)
											    {
											        ItemStack itemstack = ((TileEntityChest) tile).removeStackFromSlot(i);
	
											        if ( itemstack != null )
											        {
											            lootItems.add(itemstack);
											        }
											    }
											}
											catch ( Exception e ) {}
										}
										
										((TileEntityChest) tile).clear();
										((TileEntityChest) tile).markDirty();
									}
									
									IBlockState ib = event.getWorld().getBlockState(pos);
									IProperty<EnumFacing> FACING = BlockHorizontal.FACING;
						            EnumFacing enumfacing = (EnumFacing)ib.getValue(FACING);
						            // EnumTrapped a = (EnumTrapped)ib.getValue(TRAPPED);
									event.getWorld().setBlockState(pos, Blocks.TRAPPED_CHEST.getDefaultState().withProperty(BlockChest.FACING, enumfacing));
									tile = event.getWorld().getTileEntity(pos);
									if ( tile instanceof TileEntityChest )
									{
										if ( lootTable != null )
										{
											((TileEntityChest) tile).setLootTable(lootTable, new Random().nextLong());
											((TileEntityChest) tile).markDirty();
										}
										else if ( lootItems != null && !lootItems.isEmpty() )
										{
											try
											{
												int i = 0;
												for ( ItemStack itemstack : lootItems )
												{
										            ((TileEntityChest) tile).setInventorySlotContents(i, itemstack);
										            i++;
												}
											}
											catch ( Exception e ) {}
											((TileEntityChest) tile).markDirty();
										}
									}
								}
							}
							catch ( Exception e )
							{
								
							}
							continue;
						}
						// ================================ FLOWER POT ================================
						else if ( b instanceof BlockFlowerPot )
						{
							try
							{
								CivilizationType civ = CivilizationUtil.getProvinceAt(event.getWorld(), x/16, z/16).civilization;
						        TileEntityFlowerPot tileentityflowerpot = this.getTileEntity(event.getWorld(), pos);
						        if ( civ != null && tileentityflowerpot != null )
						        {
							        switch ( civ )
									{
										case FIRE:
										{
											Item item = Item.getItemFromBlock(Blocks.RED_FLOWER);
											ItemStack istack = new ItemStack(item);
											//istack.setItemDamage(0);
											tileentityflowerpot.setItemStack(istack);
											break;
										}
										case EARTH:
										{
											Item item = Item.getItemFromBlock(Blocks.TALLGRASS);
											ItemStack istack = new ItemStack(item);
											istack.setItemDamage(2);
											tileentityflowerpot.setItemStack(istack);
											break;
										}
										case WATER:
										{
											Item item = Item.getItemFromBlock(Blocks.RED_FLOWER);
											ItemStack istack = new ItemStack(item);
											istack.setItemDamage(1);
											tileentityflowerpot.setItemStack(istack);
											break;
										}
										case MOON:
										{
											Item item = Item.getItemFromBlock(Blocks.BROWN_MUSHROOM);
											ItemStack istack = new ItemStack(item);
											//istack.setItemDamage(0);
											tileentityflowerpot.setItemStack(istack);
											break;
										}
										case WIND:
										{
											Item item = Item.getItemFromBlock(Blocks.DEADBUSH);
											ItemStack istack = new ItemStack(item);
											//istack.setItemDamage(0);
											tileentityflowerpot.setItemStack(istack);
											break;
										}
										case SUN:
										{
											Item item = Item.getItemFromBlock(Blocks.CACTUS);
											ItemStack istack = new ItemStack(item);
											//istack.setItemDamage(0);
											tileentityflowerpot.setItemStack(istack);
											break;
										}
									}
									tileentityflowerpot.markDirty();
							        event.getWorld().notifyBlockUpdate(pos, b.getDefaultState(), b.getDefaultState(), 3);
						        }
							}
							catch ( Exception e )
							{
								
							}
							continue;
						}
						// ================================ BED ================================
						else if ( b instanceof BlockBed )
						{
							try
							{
								CivilizationType civ = CivilizationUtil.getProvinceAt(event.getWorld(), x/16, z/16).civilization;
						        TileEntityBed bed = this.getBed(event.getWorld(), pos);
						        if ( civ != null && bed != null )
						        {
							        switch ( civ )
									{
										case FIRE:
										{
											bed.setColor(EnumDyeColor.RED);
											break;
										}
										case EARTH:
										{
											bed.setColor(EnumDyeColor.GREEN);
											break;
										}
										case WATER:
										{
											bed.setColor(EnumDyeColor.BLUE);
											break;
										}
										case MOON:
										{
											bed.setColor(EnumDyeColor.BLACK);
											break;
										}
										case WIND:
										{
											bed.setColor(EnumDyeColor.BROWN);
											break;
										}
										case SUN:
										{
											bed.setColor(EnumDyeColor.YELLOW);
											break;
										}
									}
									bed.markDirty();
							        event.getWorld().notifyBlockUpdate(pos, b.getDefaultState(), b.getDefaultState(), 3);
						        }
							}
							catch ( Exception e )
							{
								
							}
							continue;
						}
					}
				}
			}
		}
	}
	
	/*
	 * @Method(modid = "dldungeonsjdg")
	 * 
	 * @SubscribeEvent public void spawnDungeon(DLDEvent.PlaceDungeonBegin
	 * event) {
	 * 
	 * }
	 * 
	 * @Method(modid = "dldungeonsjdg")
	 * 
	 * @SubscribeEvent public void spawnDungeon(DLDEvent.PlaceDungeonFinish
	 * event) {
	 * 
	 * }
	 * 
	 * @Method(modid = "dldungeonsjdg")
	 * 
	 * @SubscribeEvent public void spawnDungeon(DLDEvent.AfterChestTileEntity
	 * event) { addArmorToSlot2(event); }
	 * 
	 * protected void addArmorToSlot2(DLDEvent.AfterChestTileEntity event) {
	 * event.getContents().setInventorySlotContents(2, new
	 * ItemStack(getRandomArmor(event.getRandom()))); }
	 * 
	 * private Item getRandomArmor(Random rand) { int i = rand.nextInt(12);
	 * 
	 * switch(i) { case 0: return ItemRoyalArmor.chestplateItem; case 1: return
	 * ItemRoyalArmor.leggingsItem; case 2: return ItemRoyalArmor.helmetItem;
	 * case 3: return ItemRoyalArmor.bootsItem;
	 * 
	 * case 4: return ItemSamuraiArmor.chestplateItem; case 5: return
	 * ItemSamuraiArmor.leggingsItem; case 6: return
	 * ItemSamuraiArmor.helmetItem; case 7: return ItemSamuraiArmor.bootsItem;
	 * 
	 * case 8: return ItemReinforcedDiamondArmor.chestplateItem; case 9: return
	 * ItemReinforcedDiamondArmor.leggingsItem; case 10: return
	 * ItemReinforcedDiamondArmor.helmetItem; default: return
	 * ItemReinforcedDiamondArmor.bootsItem; }
	 * 
	 * }
	 * 
	 * @Method(modid = "dldungeonsjdg")
	 * 
	 * @SubscribeEvent public void spawnDungeon(DLDEvent.BeforeBuild event) {
	 * 
	 * }
	 * 
	 * @Method(modid = "dldungeonsjdg")
	 * 
	 * @SubscribeEvent public void handle(DLDEvent.BeforePlaceSpawner event) {
	 * 
	 * if (event.getWorld().rand.nextInt(100) > 50) { return; }
	 * 
	 * event.setCanceled(true); event.getWorld().setBlockState(event.getPos(),
	 * BlockToroSpawner.INSTANCE.getDefaultState()); TileEntityToroSpawner
	 * spawner = (TileEntityToroSpawner)
	 * event.getWorld().getTileEntity(event.getPos());
	 * spawner.setTriggerDistance(8); spawner.setSpawnRadius(10); int count =
	 * 30; List<String> entities = new ArrayList<String>(count); for (int i = 0;
	 * i < count; i++) { entities.add("Zombie"); }
	 * spawner.setEntityIds(entities); }
	 * 
	 * @Method(modid = "dldungeonsjdg")
	 * 
	 * @SubscribeEvent public void
	 * placeDungeonBlock(DLDEvent.AddTileEntitiesToRoom event) {
	 * 
	 * }
	 * 
	 * @Method(modid = "dldungeonsjdg")
	 * 
	 * @SubscribeEvent public void
	 * placeDungeonBlock(DLDEvent.AddChestBlocksToRoom event) {
	 * 
	 * }
	 * 
	 * @Method(modid = "dldungeonsjdg")
	 * 
	 * @SubscribeEvent public void placeDungeonBlock(DLDEvent.AddEntrance event)
	 * {
	 * 
	 * }
	 * 
	 * @Method(modid = "dldungeonsjdg")
	 * 
	 * @SubscribeEvent public void placeDungeonBlock(DLDEvent.PlaceBlock event)
	 * {
	 * 
	 * }
	 * 
	 * protected void replaceSpawnersWithDiamond(DLDEvent.PlaceBlock event) { if
	 * (event.getBlock().getUnlocalizedName().equals("tile.mobSpawner")) {
	 * event.getWorld().setBlockState(event.getPos(),
	 * Blocks.DIAMOND_BLOCK.getDefaultState()); event.setCanceled(true); } }
	 * 
	 * protected void printFloorPlan(DLDEvent.BeforeBuild event) { int[][] room
	 * = event.getMapMatrix().room; StringBuilder s = new StringBuilder();
	 * s.append("\n"); for (int i = 0; i < room.length; i++) { for (int j = 0; j
	 * < room.length; j++) { s.append(pad(room[i][j])); } s.append("\n"); }
	 * System.out.println(s.toString()); }
	 */

//	private Object pad(int i) {
//		if (i < 10) {
//			return "0" + i;
//		}
//		return i + "";
//	}
	
    private TileEntityFlowerPot getTileEntity(World worldIn, BlockPos pos)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity instanceof TileEntityFlowerPot ? (TileEntityFlowerPot)tileentity : null;
    }
    
    private TileEntityBed getBed(World worldIn, BlockPos pos)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity instanceof TileEntityBed ? (TileEntityBed)tileentity : null;
    }

}
