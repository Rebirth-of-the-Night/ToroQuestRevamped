package net.torocraft.toroquest.civilization;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.server.command.TextComponentHelper;
import net.torocraft.toroquest.config.ToroQuestConfiguration;

public enum CivilizationType
{
	EARTH, WIND, FIRE, WATER, MOON, SUN;
	
	public String getUnlocalizedName()
	{
		return "civilization." + this.toString().toLowerCase() + ".name";
	}
	
//	public String getCivName()
//	{
//		return this.toString().toLowerCase();
//	}

	public String getLocalizedName()
	{
		return I18n.format(getUnlocalizedName(), new Object[0]);
	}
	
	public String getDisplayName( EntityPlayer player )
	{
		return TextComponentHelper.createComponentTranslation(player, this.getUnlocalizedName(), new Object[0]).getFormattedText();//I18n.format(getUnlocalizedName());
	}
	
	public static String biomeName(String s)
	{
		switch ( s )
		{
			case "EARTH":
			{
				return "GREEN";
			}
			case "WIND":
			{
				return "BROWN";
			}
			case "FIRE":
			{
				return "RED";
			}
			case "WATER":
			{
				return "BLUE";
			}
			case "SUN":
			{
				return "YELLOW";
			}
			case "MOON":
			{
				return "BLACK";
			}
		}
		return "";
	}
	
	public static String configHouseName(CivilizationType civ)
	{
		switch ( civ )
		{
			case EARTH:
			{
				return ToroQuestConfiguration.greenName;
			}
			case WIND:
			{
				return ToroQuestConfiguration.brownName;
			}
			case FIRE:
			{
				return ToroQuestConfiguration.redName;
			}
			case WATER:
			{
				return ToroQuestConfiguration.blueName;
			}
			case SUN:
			{
				return ToroQuestConfiguration.yellowName;
			}
			case MOON:
			{
				return ToroQuestConfiguration.blackName;
			}
		}
		return "";
	}
	
//	public static String civServerName(EntityPlayer player, String s)
//	{
//		return TextComponentHelper.createComponentTranslation(player, "civilization."+s.toLowerCase()+".name", new Object[0]).getFormattedText();
//		// return I18n.format("civilization."+s.toLowerCase()+".name");
//	}
	
	// @SideOnly(Side.CLIENT)
	public String getFriendlyEnteringMessage(Province province) {
		return I18n.format("civilization.entering.friendly", province.name, getLocalizedName());
	}

	// @SideOnly(Side.CLIENT)
	public String getNeutralEnteringMessage(Province province) {
		return I18n.format("civilization.entering.neutral", province.name, getLocalizedName());
	}

	// @SideOnly(Side.CLIENT)
	public String getHostileEnteringMessage(Province province) {
		return I18n.format("civilization.entering.hostile", province.name, getLocalizedName());
	}

	// @SideOnly(Side.CLIENT)
	public String getFriendlyLeavingMessage(Province province) {
		return I18n.format("civilization.leaving.friendly", province.name, getLocalizedName());
	}

	// @SideOnly(Side.CLIENT)
	public String getNeutralLeavingMessage(Province province) {
		return I18n.format("civilization.leaving.neutral", province.name, getLocalizedName());
	}

	// @SideOnly(Side.CLIENT)
	public String getHostileLeavingMessage(Province province) {
		return I18n.format("civilization.leaving.hostile", province.name, getLocalizedName());
	}
}