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
import net.torocraft.toroquest.civilization.Province;

@Mod.EventBusSubscriber
public class ItemScrollEarth extends ItemTownScroll
{
	public static final String SCROLL_NAME = "earth";
	public static ItemScrollEarth INSTANCE;

	public ItemScrollEarth(Province province)
	{
		super(province);
		this.setHasSubtypes(true);
        this.setMaxStackSize(16);
        this.setCreativeTab(CreativeTabs.MISC);
	}
	
	@SubscribeEvent
	public static void init(final RegistryEvent.Register<Item> event)
	{
    	INSTANCE = new ItemScrollEarth(null);
		INSTANCE.setRegistryName(new ResourceLocation( ToroQuest.MODID, NAME + SCROLL_NAME ));
		event.getRegistry().register(INSTANCE);
	}
	
    public static void registerRenders()
    {
    	ModelResourceLocation model = new ModelResourceLocation(ToroQuest.MODID + ":" + NAME + SCROLL_NAME, "inventory");
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(INSTANCE, 0, model);
		RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
		renderItem.getItemModelMesher().register(INSTANCE, 0, new ModelResourceLocation(ToroQuest.MODID + ":" + NAME + SCROLL_NAME, "inventory"));
	}
    
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
    	if ( stack.hasTagCompound() && !(stack.isEmpty()) )
		{
    		String name = stack.getTagCompound().getString("province_name");
    		if ( !(name == null) && !(name == "") )
        	{
            	tooltip.add("Teleport scroll bound to " + name);
        		return;
        	}
		}
    	tooltip.add("Teleport scroll not bound.");
    }
}