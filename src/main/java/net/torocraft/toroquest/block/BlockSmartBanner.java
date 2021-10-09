package net.torocraft.toroquest.block;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.toroquest.ToroQuest;
import net.torocraft.toroquest.item.ItemEnderIdol;

@Mod.EventBusSubscriber
public class BlockSmartBanner extends BlockContainer
{
    public static BlockSmartBanner INSTANCE;
	public static Item ITEM_INSTANCE;
    
    public EnumFacing facing = EnumFacing.NORTH;
    
    public void setFacing( EnumFacing f )
    {
    	this.facing = f;
    }
    
    public EnumFacing getFacing()
    {
    	return this.facing;
    }

	public static final String NAME = "smartBanner";
	//public static BlockVillageSpawner INSTANCE;
	// public static Item ITEM_INSTANCE;
	public static ResourceLocation REGISTRY_NAME = new ResourceLocation(ToroQuest.MODID, NAME);
	//@GameRegistry.ObjectHolder(ToroQuest.MODID + "." + NAME) public static BlockVillageSpawner firstBlock;

	@SuppressWarnings("deprecation")
	@SubscribeEvent
	public static void initBlock(final RegistryEvent.Register<Block> event)
	{
		GameRegistry.registerTileEntity(TileEntityBannerSpawner.class, ToroQuest.MODID + ":" + NAME);
		//INSTANCE = (BlockSmartBanner) new BlockToroSpawner().setUnlocalizedName(NAME);
		//INSTANCE.setRegistryName(REGISTRY_NAME);
		//event.getRegistry().register(INSTANCE);
	}

//	@SuppressWarnings("deprecation")
//	@SubscribeEvent
//	public static void initBlock(final RegistryEvent.Register<Block> event)
//	{
//		// GameRegistry.registerTileEntity(BlockVillageSpawner.class, new ResourceLocation(ToroQuest.MODID , "tileEntityApiary"));
//
//		GameRegistry.registerTileEntity(TileEntityBannerSpawner.class, ToroQuest.MODID + ":" + NAME);
//		INSTANCE = (BlockVillageSpawner) new BlockVillageSpawner().setUnlocalizedName(NAME);
//		INSTANCE.setRegistryName(REGISTRY_NAME);
//		event.getRegistry().register(INSTANCE);
//		ITEM_INSTANCE = Item.REGISTRY.getObject(new ResourceLocation(ToroQuest.MODID, NAME));
//		INSTANCE.setUnlocalizedName(INSTANCE.getRegistryName().toString());
//	}
	
//	public static void registerRenders()
//	{
//		ModelResourceLocation model = new ModelResourceLocation(ToroQuest.MODID + ":" + NAME, "inventory");
//		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(ITEM_INSTANCE, 0, model);
//		RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
//		renderItem.getItemModelMesher().register(ITEM_INSTANCE, 0, new ModelResourceLocation(ToroQuest.MODID + ":" + NAME, "inventory"));
//	}

	
	@SubscribeEvent
	public static void registerRenders(ModelRegistryEvent event)
	{
		//registerRender(Item.getItemFromBlock(INSTANCE));
	}
	
	public static void registerRender(Item item)
	{
		//ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation( item.getRegistryName(), "inventory"));
	}
	
	public BlockSmartBanner()
	{
		super(Material.AIR);
		setUnlocalizedName(ToroQuest.MODID + "." + NAME);
		setRegistryName(NAME);
		//setCreativeTab(CreativeTabs.MISC);
	}
	
//	public void registerModels()
//	{
//		ToroQuest.proxy.re
//	}
	
//	Block myBlock = new CustomBlock();
//	string registryname = "my_block";
//	block.setRegistryName(registryname);
//	block.setUnlocalizedName(block.getRegistryName().toString());
//	GameRegistry.register(block);

//	@SubscribeEvent
//	public static void init(final RegistryEvent.Register<Item> event)
//	{
//		INSTANCE = new BlockVillageSpawner();
//	}
	
	
	/*
	
	public static ItemEnderIdol INSTANCE;
	public static final String NAME = "ender_idol";
	private static ResourceLocation REGISTRY_NAME = new ResourceLocation(ToroQuest.MODID, NAME);

	@SubscribeEvent
	public static void init(final RegistryEvent.Register<Item> event)
	{
		INSTANCE = new ItemEnderIdol();
		INSTANCE.setRegistryName(REGISTRY_NAME);
		event.getRegistry().register(INSTANCE);
	}

	public static void registerRenders()
	{
		ModelResourceLocation model = new ModelResourceLocation(ToroQuest.MODID + ":" + NAME, "inventory");
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(INSTANCE, 0, model);
		RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
		renderItem.getItemModelMesher().register(INSTANCE, 0, new ModelResourceLocation(ToroQuest.MODID + ":" + NAME, "inventory"));
	}

	public ItemEnderIdol() {
		setUnlocalizedName(NAME);
		this.maxStackSize = 1;
		this.setCreativeTab(CreativeTabs.MATERIALS);
	}
	
	
	*/
//	public EnumBlockRenderType getRenderType(IBlockState state)
//	{
//		return super.getRenderType(state)
//	}

	@Nullable
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos)
	{
		return NULL_AABB;
	}

	public boolean isOpaqueCube(IBlockState state) {
		return true;
	}

	public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid)
	{
		return false;
	}

	/**
	 * Spawns this Block's drops into the World as EntityItems.
	 */
	public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune)
	{
	}

	/**
	 * Returns a new instance of a block's tile entity class. Called on placing
	 * the block.
	 */
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		return new TileEntityBannerSpawner();
	}

	public boolean isFullCube(IBlockState state)
	{
		return true;
	}

	/**
	 * Get the Item that this Block should drop when harvested.
	 */
	@Nullable
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		return null;
	}

	/**
	 * Returns the quantity of items to drop on block destruction.
	 */
	public int quantityDropped(Random random)
	{
		return 0;
	}

	@Override
	public int getExpDrop(IBlockState state, net.minecraft.world.IBlockAccess world, BlockPos pos, int fortune)
	{
		return 0;
	}

	@Nullable
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		return null;
	}

	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer()
	{
		return BlockRenderLayer.TRANSLUCENT;
	}
}
