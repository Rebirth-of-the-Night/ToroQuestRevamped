package net.torocraft.toroquest.item.armor;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.toroquest.ToroQuest;
import net.torocraft.toroquest.material.ArmorMaterials;

@Mod.EventBusSubscriber
public class ItemBanditArmor extends ItemArmor {

	public static final String NAME = "bandit";

	public static ItemBanditArmor helmetItem;

	@SubscribeEvent
	public static void init(final RegistryEvent.Register<Item> event)
	{
		helmetItem = new ItemBanditArmor(NAME + "_helmet", 1, EntityEquipmentSlot.HEAD);
		
		helmetItem.setRegistryName(new ResourceLocation(ToroQuest.MODID, NAME + "_helmet"));
		event.getRegistry().register(helmetItem);
	}
	
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
    	{
    		tooltip.add("Donate this item to a village lord for reputation.");
    	}
    }

	public static void registerRenders()
	{
		registerRendersHelmet();
	}

	private static void registerRendersHelmet()
	{
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(helmetItem, 0, model("helmet"));
	}

	private static ModelResourceLocation model(String model)
	{
		return new ModelResourceLocation(ToroQuest.MODID + ":" + NAME + "_" + model, "inventory");
	}

	public ItemBanditArmor(String unlocalizedName, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn)
	{
		super(ArmorMaterials.BANDIT, renderIndexIn, equipmentSlotIn);
		this.setUnlocalizedName(unlocalizedName);
		this.maxStackSize = 16;
		setMaxDamage(0);
	}
	
	@Override
	public boolean isDamageable()
    {
        return false;
    }
	
	@Override
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair)
    {
		return false;
//        ItemStack mat = new ItemStack( Item.getByNameOrId("toroquest:bandit_helmet"), 1 );
//        if (!mat.isEmpty() && net.minecraftforge.oredict.OreDictionary.itemMatches(mat,repair,false)) return true;
//        return super.getIsRepairable(toRepair, repair);
    }

}
