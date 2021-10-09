package net.torocraft.toroquest.item.armor;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
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
public class ItemLegendaryBanditArmor extends ItemArmor {

	public static final String NAME = "legendary_bandit";

	public static ItemLegendaryBanditArmor helmetItem;

	@SubscribeEvent
	public static void init(final RegistryEvent.Register<Item> event)
	{
		helmetItem = new ItemLegendaryBanditArmor(NAME + "_helmet", 1, EntityEquipmentSlot.HEAD);
		
		helmetItem.setRegistryName(new ResourceLocation(ToroQuest.MODID, NAME + "_helmet"));
		event.getRegistry().register(helmetItem);
	}
	
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
		tooltip.add("Give this trophy to a Village Lord: You gain the power of the Bandit Lord; vastly increasing strength while in that province.\n\n§oYou can sense the powerful, yet dark energy resonating from the mask. It calls out and begs for a suitable host... you feel compelled to put the mask on...\n\n");
    	// tooltip.add("Donate this trophy to a village lord: You gain the strength of the bandit lord while in your province. OR you could look hella fresh and wear it. Go ahead, put on the mask...");
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

	public ItemLegendaryBanditArmor(String unlocalizedName, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn)
	{
		super(ArmorMaterials.LEGENDARY_BANDIT, renderIndexIn, equipmentSlotIn);
		this.setUnlocalizedName(unlocalizedName);
		this.maxStackSize = 1;
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
