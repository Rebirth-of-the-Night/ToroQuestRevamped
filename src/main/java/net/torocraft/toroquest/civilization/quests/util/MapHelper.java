package net.torocraft.toroquest.civilization.quests.util;
import net.minecraft.world.storage.MapData;

public class MapHelper extends MapData
{

	public MapHelper(String mapname)
	{
		super(mapname);
	}
	
	@Override
	public void calculateMapCenter(double x, double z, int mapScale)
    {
        int i = 128 * (1 << mapScale);
        this.xCenter = i - 64;
        this.zCenter = i - 64;
    }
	
}