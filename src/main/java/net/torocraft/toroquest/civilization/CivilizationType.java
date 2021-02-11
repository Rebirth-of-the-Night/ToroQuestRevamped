package net.torocraft.toroquest.civilization;

import net.minecraft.client.resources.I18n;

public enum CivilizationType
{
	EARTH, WIND, FIRE, WATER, MOON, SUN;
	
	public String getUnlocalizedName()
	{
		return "civilization." + this.toString().toLowerCase() + ".name";
	}
	
	public String getCivName()
	{
		return this.toString().toLowerCase();
	}

	// @SideOnly(Side.CLIENT)
	public String getLocalizedName()
	{
		return I18n.format(getUnlocalizedName(), new Object[0]);
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
	
	public static String civServerName(String s)
	{
		/*
		civilization.earth.name=Wild
		civilization.wind.name=Mithril
		civilization.fire.name=Briar
		civilization.moon.name=Moor
		civilization.sun.name=Dawn
		civilization.water.name=Glacier
		*/
		
		switch ( s )
		{
			case "earth":
			{
				return "Wild";
			}
			case "wind":
			{
				return "Mithril";
			}
			case "fire":
			{
				return "Briar";
			}
			case "water":
			{
				return "Glacier";
			}
			case "sun":
			{
				return "Dawn";
			}
			case "moon":
			{
				return "Moor";
			}
		}
		return "";
	}
	
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