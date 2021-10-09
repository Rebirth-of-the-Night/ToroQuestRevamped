package net.torocraft.toroquest.block;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.toroquest.ToroQuest;

@Mod.EventBusSubscriber
public class BlockToroSpawner extends BlockContainer
{

	public static final String NAME = "toroSpawnerBlock";
	public static BlockToroSpawner INSTANCE;
	//public static Item ITEM_INSTANCE;
	private static ResourceLocation REGISTRY_NAME = new ResourceLocation(ToroQuest.MODID, NAME);

	@SuppressWarnings("deprecation")
	@SubscribeEvent
	public static void initBlock(final RegistryEvent.Register<Block> event)
	{
		GameRegistry.registerTileEntity(TileEntityToroSpawner.class, ToroQuest.MODID + ":" + NAME);
		INSTANCE = (BlockToroSpawner) new BlockToroSpawner().setUnlocalizedName(NAME);
		INSTANCE.setRegistryName(REGISTRY_NAME);
		event.getRegistry().register(INSTANCE);
		//ITEM_INSTANCE = Item.REGISTRY.getObject(new ResourceLocation(ToroQuest.MODID, NAME));
	}

	public static void registerRenders() {
		//ModelResourceLocation model = new ModelResourceLocation(ToroQuest.MODID + ":" + NAME, "inventory");
		//Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(ITEM_INSTANCE, 0, model);
	}

	protected BlockToroSpawner() {
		super(Material.AIR);
		setCreativeTab(CreativeTabs.MISC);
	}

	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.INVISIBLE;
	}

	@Nullable
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos) {
		return NULL_AABB;
	}

	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid) {
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
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityToroSpawner();
	}

	public boolean isFullCube(IBlockState state) {
		return false;
	}

	/**
	 * Get the Item that this Block should drop when harvested.
	 */
	@Nullable
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return null;
	}

	/**
	 * Returns the quantity of items to drop on block destruction.
	 */
	public int quantityDropped(Random random) {
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
		return BlockRenderLayer.CUTOUT;
	}
}
