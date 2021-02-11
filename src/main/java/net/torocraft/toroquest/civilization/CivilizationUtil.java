package net.torocraft.toroquest.civilization;

import net.minecraft.world.World;

public class CivilizationUtil
{

	public static Province getProvinceAt(World world, int chunkX, int chunkZ)
	{
		return CivilizationsWorldSaveData.get(world).atLocation(chunkX, chunkZ);
	}
	
	public static Province registerNewCivilization(World world, int chunkX, int chunkZ)
	{
		return CivilizationsWorldSaveData.get(world).register(chunkX, chunkZ);
	}

	public static String chatColor( CivilizationType civ )
	{
		return "§l";
	}
//
//		if ( civ != null )
//		{
//			switch ( civ )
//			{
//				case FIRE:
//				{
//					return "§c";
//				}
//				case EARTH:
//				{
//					return "§a";
//				}
//				case WATER:
//				{
//					return "§b";
//				}
//				case MOON:
//				{
//					return "§0";
//				}
//				case WIND:
//				{
//					return "§f";
//				}
//				case SUN:
//				{
//					return "§e";
//				}
//			}
//		}
//		return "";
//	}
}