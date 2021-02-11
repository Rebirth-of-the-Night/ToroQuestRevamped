package net.torocraft.toroquest.material;

import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.util.EnumHelper;
import net.torocraft.toroquest.ToroQuest;

public class ArmorMaterials
{
	private static final String MODID = ToroQuest.MODID;
	
	// (String name, String textureName, int durability, int[] reductionAmounts, int enchantability, SoundEvent soundOnEquip, float toughness
	
	public static ArmorMaterial ROYAL = EnumHelper.addArmorMaterial("ROYAL",
			MODID + ":royal_armor", 36, new int[] { 1, 3, 5, 2 }, 30, SoundEvents.ITEM_ARMOR_EQUIP_GOLD, 0);
	// public static ArmorMaterial REINFORCED_DIAMOND = EnumHelper.addArmorMaterial("REINFORCED_DIAMOND", MODID + ":reinforced_diamond_armor", 30, new int[] { 3, 6, 8, 3 }, 5, null, 2);
	
	public static ArmorMaterial BANDIT = EnumHelper.addArmorMaterial("TORO",
			MODID + ":bandit_armor", 10, new int[] { 0, 0, 0, 0 }, 30, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0);
	
	public static ArmorMaterial LEGENDARY_BANDIT = EnumHelper.addArmorMaterial("TORO",
			MODID + ":legendary_bandit_armor", 10, new int[] { 4, 4, 4, 4 }, 35, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 4.0F);
	
	public static ArmorMaterial TORO = EnumHelper.addArmorMaterial("TORO",
			MODID + ":toro_armor", 10, new int[] { 2, 4, 5, 2 }, 15, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0);
	
	public static ArmorMaterial SAMURAI = EnumHelper.addArmorMaterial("SAMURAI",
			MODID + ":samurai_armor", 10, new int[] { 1, 3, 5, 2 }, 25, SoundEvents.ITEM_ARMOR_EQUIP_GOLD, 0);
}
