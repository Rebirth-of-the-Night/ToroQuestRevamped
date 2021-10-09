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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.toroquest.ToroQuest;

@Mod.EventBusSubscriber
public class ItemArtifactBlue extends Item
{

	public static ItemArtifactBlue INSTANCE;
	public static final String NAME = "artifact_blue";
	private static ResourceLocation REGISTRY_NAME = new ResourceLocation(ToroQuest.MODID, NAME);

	@SubscribeEvent
	public static void init(final RegistryEvent.Register<Item> event)
	{
		INSTANCE = new ItemArtifactBlue();
		INSTANCE.setRegistryName(REGISTRY_NAME);
		event.getRegistry().register(INSTANCE);
	}

	public static void registerRenders() {
		ModelResourceLocation model = new ModelResourceLocation(ToroQuest.MODID + ":" + NAME, "inventory");
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(INSTANCE, 0, model);
		RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
		renderItem.getItemModelMesher().register(INSTANCE, 0, new ModelResourceLocation(ToroQuest.MODID + ":" + NAME, "inventory"));
	}

	public ItemArtifactBlue()
	{
		setUnlocalizedName(NAME);
		this.maxStackSize = 1;
		this.setCreativeTab(CreativeTabs.MATERIALS);
	}
	
	@Override
    @SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
		tooltip.add("Give this artifact to it's respective Village Lord for a reward §7[put the item in donate box]§r.");
    }

}
