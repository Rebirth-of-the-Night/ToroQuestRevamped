package net.torocraft.toroquest.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.toroquest.ToroQuest;

@Mod.EventBusSubscriber
public class ItemTrophyPig extends ItemTrophy
{

	public static ItemTrophyPig INSTANCE;
	public static final String NAME = "trophy_pig";
	private static ResourceLocation REGISTRY_NAME = new ResourceLocation(ToroQuest.MODID, NAME);

	@SubscribeEvent
	public static void init(final RegistryEvent.Register<Item> event)
	{
		INSTANCE = new ItemTrophyPig();
		INSTANCE.setRegistryName(REGISTRY_NAME);
		event.getRegistry().register(INSTANCE);
	}

	public static void registerRenders() {
		ModelResourceLocation model = new ModelResourceLocation(ToroQuest.MODID + ":" + NAME, "inventory");
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(INSTANCE, 0, model);
		RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
		renderItem.getItemModelMesher().register(INSTANCE, 0, new ModelResourceLocation(ToroQuest.MODID + ":" + NAME, "inventory"));
	}

	public ItemTrophyPig()
	{
		setUnlocalizedName(NAME);
		this.maxStackSize = 1;
		this.setCreativeTab(CreativeTabs.MATERIALS);
	}
	
	@Override
    @SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
		tooltip.add("Give this trophy to a Village Lord: The corrupted heart of the Demon-Pig slowly replenishes your hunger while in that province.\n\n§oWho needs food when you have demon magic?");
    }

}
