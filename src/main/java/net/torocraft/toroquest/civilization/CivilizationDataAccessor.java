package net.torocraft.toroquest.civilization;

import java.util.List;
import java.util.UUID;

public interface CivilizationDataAccessor
{
	Province atLocation(int chunkX, int chunkZ);

	Province register(int chunkX, int chunkZ);

	List<Province> getProvinces();

	boolean canGenStructure(String type, int chunkX, int chunkZ);

	void setProvinceHasLord(UUID provinceId, boolean hasLord);

	boolean provinceHasLord(UUID provinceId);
	
	void setName(UUID provinceId, String name);
	
	// CivilizationsWorldSaveData
	void setTrophyPig(UUID provinceId, boolean hasLord);
	boolean hasTrophyPig(UUID provinceId);
	
	void setTrophyMage(UUID provinceId, boolean hasLord);
	boolean hasTrophyMage(UUID provinceId);
	
	void setTrophyBandit(UUID provinceId, boolean hasLord);
	boolean hasTrophyBandit(UUID provinceId);
	
	void setTrophySkeleton(UUID provinceId, boolean hasLord);
	boolean hasTrophySkeleton(UUID provinceId);
	
	void setTrophySpider(UUID provinceId, boolean hasLord);
	boolean hasTrophySpider(UUID provinceId);
	
	void setTrophyTitan(UUID provinceId, boolean hasLord);
	boolean hasTrophyTitan(UUID provinceId);
	
	void setTrophyBeholder(UUID provinceId, boolean hasLord);
	boolean hasTrophyBeholder(UUID provinceId);
	
	void setTrophyLord(UUID provinceId, boolean hasLord);
	boolean hasTrophyLord(UUID provinceId);


}