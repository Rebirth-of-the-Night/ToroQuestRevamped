// civ radius

package net.torocraft.toroquest.civilization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.torocraft.toroquest.ToroQuest;
import net.torocraft.toroquest.config.ToroQuestConfiguration;

public class CivilizationsWorldSaveData extends WorldSavedData implements CivilizationDataAccessor
{

	private static final String DATA_NAME = ToroQuest.MODID + "_civilizations";
	public static final int RADIUS = 6;
	public World world;

	private TreeMap<Integer, TreeMap<Integer, Province>> provincesTreeMap = new TreeMap<Integer, TreeMap<Integer, Province>>();
	private List<Province> provinces = new ArrayList<Province>();
	private List<Structure> structures = new ArrayList<Structure>();

	public CivilizationsWorldSaveData() {
		super(DATA_NAME);
	}

	public CivilizationsWorldSaveData(String name) {
		super(name);
	}

	@Override
	public synchronized boolean canGenStructure(String type, int chunkX, int chunkZ)
	{

//		if (chunkX < 70 && chunkZ < 70) {
//			return false;
//		}
//
//		for (Structure s : structures) {
//			if (s.distanceSqFrom(chunkX, chunkZ) < ToroQuestConfiguration.structureMinDistance) {
//				return false;
//			}
//			if (type.equals(s.type) && s.distanceSqFrom(chunkX, chunkZ) < ToroQuestConfiguration.structureMinDistanceBetweenSame) {
//				return false;
//			}
//		}
//
//		Structure newStructure = new Structure();
//		newStructure.type = type;
//		newStructure.chunkX = chunkX;
//		newStructure.chunkZ = chunkZ;
//		structures.add(newStructure);

		return true;
	}
	
	public synchronized boolean canGenStructureForce(String type, int chunkX, int chunkZ)
	{
//		Structure newStructure = new Structure();
//		newStructure.type = type;
//		newStructure.chunkX = chunkX;
//		newStructure.chunkZ = chunkZ;
//		structures.add(newStructure);

		return true;
	}
	
	@Override
	public Province atLocation(final int chunkX, final int chunkZ)
	{

		Collection<Province> provinces = scan(chunkX, chunkZ);

		if (provinces == null || provinces.size() < 1)
		{
			return null;
		}

		if (provinces.size() == 1)
		{
			for (Province border : provinces)
			{
				return border;
			}
		}

		List<Province> list = new ArrayList<Province>(provinces);

		Collections.sort(list, new Comparator<Province>()
		{
			@Override
			public int compare(Province a, Province b)
			{
				double d0 = chunkDistanceSq(chunkX, chunkZ, a.chunkX, a.chunkZ);
				double d1 = chunkDistanceSq(chunkX, chunkZ, b.chunkX, b.chunkZ);
				return d0 < d1 ? -1 : (d0 > d1 ? 1 : 0);
			}
		});

		return list.get(0);
	}

	public int chunkDistanceSq(int aX, int aZ, int bX, int bZ) {
		int x = aX - bX;
		int z = aZ - bZ;
		return x * x + z * z;
	}

	private Collection<Province> scan(int chunkX, int chunkZ) {
		return sequentialScan(chunkX, chunkZ);
	}

	private Collection<Province> sequentialScan(int chunkX, int chunkZ) {

		int lowerX = chunkX - RADIUS;
		int upperX = chunkX + RADIUS;
		int lowerZ = chunkZ - RADIUS;
		int upperZ = chunkZ + RADIUS;

		List<Province> subset = new ArrayList<Province>();

		for (Province p : provinces)
		{
			if (p.chunkX >= lowerX && p.chunkX <= upperX && p.chunkZ >= lowerZ && p.chunkZ <= upperZ)
			{
				subset.add(p);
			}
		}

		return subset;
	}

	protected Collection<Province> rangeQueryOnProvinces(int chunkX, int chunkZ) {
		try {
			for (TreeMap<Integer, Province> zValues : provincesTreeMap.subMap(chunkX - RADIUS, chunkX + RADIUS).values()) {
				return zValues.subMap(chunkZ - RADIUS, chunkZ + RADIUS).values();
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public synchronized Province register(int chunkX, int chunkZ)
	{
		Province province = atLocation(chunkX, chunkZ);

		if (province != null)
		{
			updateExistingProvince(province, chunkX, chunkZ);
		}
		else
		{
			province = buildNewProvince(chunkX, chunkZ);
		}
		markDirty();
		return province;
	}
	
	
	
	
	
	
	
	
	
	
	// CROWN
	
	public synchronized Province registerCrown(int chunkX, int chunkZ)
	{
		Province province = atLocation(chunkX, chunkZ);

		if (province != null)
		{
			updateExistingProvince(province, chunkX, chunkZ);
		}
		else
		{
			province = buildNewProvince(chunkX, chunkZ);
		}
		markDirty();
		return province;
	}
	
	protected Province buildNewProvinceCrown(int chunkX, int chunkZ)
	{
//		if ( this.world.isRemote )
//		{
//			return null;
//		}
		
		if ( this.world.getBiomeProvider() == null )
		{
			return null;
		}
		
		BlockPos block = new BlockPos(chunkX*16, 0, chunkZ*16);

		
//		if ( ToroQuestConfiguration.unregisterDestroyedVillages )
//		{
//			int i = ToroQuestConfiguration.destroyedVillagesNearSpawnDistance;
//			
//			if ( i > 0 && Math.abs(block.getX()) < i && Math.abs(block.getZ()) < i )
//			{
//				return null;
//			}
//		}
		
		Province province;
		province = new Province();
		province.id = UUID.randomUUID();
		
		if ( ToroQuestConfiguration.useBiomeSpecificProvinces ) // 
		{
//			Chunk chunk = new Chunk(this.world, chunkX, chunkZ);
//			Biome biome = chunk.getBiome(block, this.world.getBiomeProvider());
			Biome biome = this.world.getBiome(block);
			Set<Type> biomeType = BiomeDictionary.getTypes(biome);
//			List<EntityPlayer> players = world.playerEntities;
//			for ( EntityPlayer player : players )
//			{
//				player.sendMessage( new TextComponentString(biome.getBiomeName()) );
//				player.sendMessage( new TextComponentString(biome.getRegistryName().toString()) );
//				player.sendMessage( new TextComponentString(BiomeDictionary.getTypes(biome).toString()) );
//			}
			if ( biomeType.contains(Type.SNOWY) || biome.isSnowyBiome() )
			{
				province.civilization=CivilizationType.WATER;
			}
			else if ( biomeType.contains(Type.JUNGLE) )
			{
				if ( biomeType.contains(Type.DENSE) )
				{
					province.civilization=CivilizationType.EARTH;
				}
				else
				{
					province.civilization=CivilizationType.SUN;
				}
			}
			else if ( biomeType.contains(Type.MOUNTAIN) )
			{
				province.civilization=CivilizationType.WIND;
			}
			else if ( biomeType.contains(Type.SWAMP) || biome.decorator.waterlilyPerChunk > 0 || biomeType.contains(Type.MUSHROOM) || biome.decorator.bigMushroomsPerChunk > 0 || biomeType.contains(Type.DEAD) || biomeType.contains(Type.WASTELAND) || biomeType.contains(Type.SPOOKY) || (biomeType.contains(Type.WET) && biomeType.contains(Type.LUSH)) )
			{
				province.civilization=CivilizationType.MOON;
			}
			else if ( (biomeType.contains(Type.MESA) || biomeType.contains(Type.PLAINS) || biomeType.contains(Type.BEACH) || biomeType.contains(Type.SANDY) || biomeType.contains(Type.SAVANNA)) && biomeType.contains(Type.HOT) )
			{
				province.civilization=CivilizationType.SUN;
			}
			else if ( biomeType.contains(Type.FOREST) || biomeType.contains(Type.DENSE) )
			{
				province.civilization=CivilizationType.EARTH;
			}
			else if ( biomeType.contains(Type.HOT) || biomeType.contains(Type.MESA) )
			{
				province.civilization=CivilizationType.SUN;
			}
			else if ( biome.getDefaultTemperature() < 0.4F && ( biomeType.contains(Type.BEACH) || biomeType.contains(Type.OCEAN) || biomeType.contains(Type.RIVER) || biomeType.contains(Type.WATER) ) )
			{
				province.civilization=CivilizationType.WATER;
			}
			else if ( biomeType.contains(Type.HILLS) ) //( biome.getHeightVariation() >= 0.3F || biome.getBaseHeight() >= 0.3F )
			{
				province.civilization=CivilizationType.WIND;
			}
			else if ( biomeType.contains(Type.PLAINS) )
			{
				province.civilization=CivilizationType.FIRE;
			}
			else if ( biome.getDefaultTemperature() > 0.7F )
			{
				province.civilization=CivilizationType.SUN;
			}
			else if ( biome.getDefaultTemperature() < 0.4F  )
			{
				province.civilization=CivilizationType.WATER;
			}
			else
			{
				province.civilization=CivilizationType.MOON;
			}
		}
		// ============================== BACKUP =======================================
		if ( province.civilization == null )
		{
			province.civilization = randomCivilizationType();
		}
		
		province.chunkX = chunkX;
		province.chunkZ = chunkZ;
		province.name = ProvinceNames.random(new Random(), province.civilization);
		province.hasLord = false;
		
		province.lowerVillageBoundX = chunkX - RADIUS/2;
		province.upperVillageBoundX = chunkX + RADIUS/2;
		province.lowerVillageBoundZ = chunkZ - RADIUS/2;
		province.upperVillageBoundZ = chunkZ + RADIUS/2;
		
		province.computeSize();

		addProvinceToSaveData(province);

		return province;
	}
	
	
	
	
	
	
	
	
	

	@Override
	public void setProvinceHasLord(UUID provinceId, boolean hasLord)
	{
		for (Province p : provinces)
		{
			if (provinceId.equals(p.id))
			{
				p.hasLord = hasLord;
				markDirty();
				p.writeNBT();
				return;
			}
		}
	}
	
	@Override
	public boolean provinceHasLord(UUID provinceId)
	{
		for (Province p : provinces)
		{
			if (provinceId.equals(p.id))
			{
				return p.hasLord;
			}
		}
		return false;
	}
	
	@Override
	public void setTrophyPig(UUID provinceId, boolean hasLord)
	{
		for (Province p : provinces)
		{
			if (provinceId.equals(p.id))
			{
				p.zombiepigTrophy = hasLord;
				markDirty();
				p.writeNBT();
				return;
			}
		}
	}

	@Override
	public boolean hasTrophyPig(UUID provinceId)
	{
		for (Province p : provinces)
		{
			if (provinceId.equals(p.id))
			{
				return p.zombiepigTrophy;
			}
		}
		return false;
	}
	
	@Override
	public void setTrophyMage(UUID provinceId, boolean hasLord)
	{
		for (Province p : provinces)
		{
			if (provinceId.equals(p.id))
			{
				p.mageTrophy = hasLord;
				markDirty();
				p.writeNBT();
				return;
			}
		}
	}

	@Override
	public boolean hasTrophyMage(UUID provinceId)
	{
		for (Province p : provinces)
		{
			if (provinceId.equals(p.id))
			{
				return p.mageTrophy;
			}
		}
		return false;
	}
	
	@Override
	public void setTrophyBandit(UUID provinceId, boolean hasLord)
	{
		for (Province p : provinces)
		{
			if (provinceId.equals(p.id))
			{
				p.banditTrophy = hasLord;
				markDirty();
				p.writeNBT();
				return;
			}
		}
	}

	@Override
	public boolean hasTrophyBandit(UUID provinceId)
	{
		for (Province p : provinces)
		{
			if (provinceId.equals(p.id))
			{
				return p.banditTrophy;
			}
		}
		return false;
	}
	
	@Override
	public void setTrophySpider(UUID provinceId, boolean hasLord)
	{
		for (Province p : provinces)
		{
			if (provinceId.equals(p.id))
			{
				p.spiderTrophy = hasLord;
				markDirty();
				p.writeNBT();
				return;
			}
		}
	}

	@Override
	public boolean hasTrophySpider(UUID provinceId)
	{
		for (Province p : provinces)
		{
			if (provinceId.equals(p.id))
			{
				return p.spiderTrophy;
			}
		}
		return false;
	}
	
	@Override
	public void setTrophySkeleton(UUID provinceId, boolean hasLord)
	{
		for (Province p : provinces)
		{
			if (provinceId.equals(p.id))
			{
				p.skeletonTrophy = hasLord;
				markDirty();
				p.writeNBT();
				return;
			}
		}
	}

	@Override
	public boolean hasTrophySkeleton(UUID provinceId)
	{
		for (Province p : provinces)
		{
			if (provinceId.equals(p.id))
			{
				return p.skeletonTrophy;
			}
		}
		return false;
	}
	
	@Override
	public void setTrophyTitan(UUID provinceId, boolean hasLord)
	{
		for (Province p : provinces)
		{
			if (provinceId.equals(p.id))
			{
				p.titanTrophy = hasLord;
				markDirty();
				p.writeNBT();
				return;
			}
		}
		
	}

	@Override
	public boolean hasTrophyTitan(UUID provinceId) {
		for (Province p : provinces)
		{
			if (provinceId.equals(p.id))
			{
				return p.titanTrophy;
			}
		}
		return false;
	}

	@Override
	public void setTrophyBeholder(UUID provinceId, boolean hasLord) {
		for (Province p : provinces)
		{
			if (provinceId.equals(p.id))
			{
				p.beholderTrophy = hasLord;
				markDirty();
				p.writeNBT();
				return;
			}
		}
		
	}

	@Override
	public boolean hasTrophyBeholder(UUID provinceId)
	{
		for (Province p : provinces)
		{
			if (provinceId.equals(p.id))
			{
				return p.beholderTrophy;
			}
		}
		return false;
	}

	@Override
	public void setTrophyLord(UUID provinceId, boolean hasLord)
	{
		for (Province p : provinces)
		{
			if (provinceId.equals(p.id))
			{
				p.lordTrophy = hasLord;
				markDirty();
				p.writeNBT();
				return;
			}
		}
		
	}

	@Override
	public boolean hasTrophyLord(UUID provinceId)
	{
		for (Province p : provinces)
		{
			if (provinceId.equals(p.id))
			{
				return p.lordTrophy;
			}
		}
		return false;
	}

	protected Province buildNewProvince(int chunkX, int chunkZ)
	{
//		if ( this.world.isRemote )
//		{
//			return null;
//		}
		
		if ( this.world.getBiomeProvider() == null )
		{
			return null;
		}
		
		BlockPos block = new BlockPos(chunkX*16, 0, chunkZ*16);

		
		if ( ToroQuestConfiguration.unregisterDestroyedVillages )
		{
			int i = ToroQuestConfiguration.destroyedVillagesNearSpawnDistance;
			
			if ( i > 0 && Math.abs(block.getX()) < i && Math.abs(block.getZ()) < i )
			{
				return null;
			}
		}
		
		Province province;
		province = new Province();
		province.id = UUID.randomUUID();
		
		if ( ToroQuestConfiguration.useBiomeSpecificProvinces ) // 
		{
//			Chunk chunk = new Chunk(this.world, chunkX, chunkZ);
//			Biome biome = chunk.getBiome(block, this.world.getBiomeProvider());
			Biome biome = this.world.getBiome(block);
			Set<Type> biomeType = BiomeDictionary.getTypes(biome);
//			List<EntityPlayer> players = world.playerEntities;
//			for ( EntityPlayer player : players )
//			{
//				player.sendMessage( new TextComponentString(biome.getBiomeName()) );
//				player.sendMessage( new TextComponentString(biome.getRegistryName().toString()) );
//				player.sendMessage( new TextComponentString(BiomeDictionary.getTypes(biome).toString()) );
//			}
			if ( biomeType.contains(Type.SNOWY) || biome.isSnowyBiome() )
			{
				province.civilization=CivilizationType.WATER;
			}
			else if ( biomeType.contains(Type.JUNGLE) )
			{
				if ( biomeType.contains(Type.DENSE) )
				{
					province.civilization=CivilizationType.EARTH;
				}
				else
				{
					province.civilization=CivilizationType.SUN;
				}
			}
			else if ( biomeType.contains(Type.MOUNTAIN) )
			{
				province.civilization=CivilizationType.WIND;
			}
			else if ( biomeType.contains(Type.SWAMP) || biome.decorator.waterlilyPerChunk > 0 || biomeType.contains(Type.MUSHROOM) || biome.decorator.bigMushroomsPerChunk > 0 || biomeType.contains(Type.DEAD) || biomeType.contains(Type.WASTELAND) || biomeType.contains(Type.SPOOKY) || (biomeType.contains(Type.WET) && biomeType.contains(Type.LUSH)) )
			{
				province.civilization=CivilizationType.MOON;
			}
			else if ( (biomeType.contains(Type.MESA) || biomeType.contains(Type.PLAINS) || biomeType.contains(Type.BEACH) || biomeType.contains(Type.SANDY) || biomeType.contains(Type.SAVANNA)) && biomeType.contains(Type.HOT) )
			{
				province.civilization=CivilizationType.SUN;
			}
			else if ( biomeType.contains(Type.FOREST) || biomeType.contains(Type.DENSE) )
			{
				province.civilization=CivilizationType.EARTH;
			}
			else if ( biomeType.contains(Type.HOT) || biomeType.contains(Type.MESA) )
			{
				province.civilization=CivilizationType.SUN;
			}
			else if ( biome.getDefaultTemperature() < 0.4F && ( biomeType.contains(Type.BEACH) || biomeType.contains(Type.OCEAN) || biomeType.contains(Type.RIVER) || biomeType.contains(Type.WATER) ) )
			{
				province.civilization=CivilizationType.WATER;
			}
			else if ( biomeType.contains(Type.HILLS) ) //( biome.getHeightVariation() >= 0.3F || biome.getBaseHeight() >= 0.3F )
			{
				province.civilization=CivilizationType.WIND;
			}
			else if ( biomeType.contains(Type.PLAINS) )
			{
				province.civilization=CivilizationType.FIRE;
			}
			else if ( biome.getDefaultTemperature() > 0.7F )
			{
				province.civilization=CivilizationType.SUN;
			}
			else if ( biome.getDefaultTemperature() < 0.4F  )
			{
				province.civilization=CivilizationType.WATER;
			}
			else
			{
				province.civilization=CivilizationType.MOON;
			}
		}
		// ============================== BACKUP =======================================
		if ( province.civilization == null )
		{
			province.civilization = randomCivilizationType();
		}
		
		province.chunkX = chunkX;
		province.chunkZ = chunkZ;
		province.name = ProvinceNames.random(new Random(), province.civilization);
		province.hasLord = false;
		
		province.lowerVillageBoundX = chunkX - RADIUS/2;
		province.upperVillageBoundX = chunkX + RADIUS/2;
		province.lowerVillageBoundZ = chunkZ - RADIUS/2;
		province.upperVillageBoundZ = chunkZ + RADIUS/2;
		
		province.computeSize();

		addProvinceToSaveData(province);

		return province;
	}

	protected CivilizationType randomCivilizationType()
	{
		Random rand = world.rand;
		return CivilizationType.values()[rand.nextInt(CivilizationType.values().length)];
	}

	private synchronized void updateExistingProvince(Province province, int chunkX, int chunkZ)
	{
		province.addToBoundsAndRecenter(chunkX, chunkZ);
	}

	private synchronized void addProvinceToSaveData(Province province)
	{
		provinces.add(province);
		addProvinceToTreeMap(province);
	}

	protected void addProvinceToTreeMap(Province border)
	{
		if (provincesTreeMap.get(border.chunkX) == null)
		{
			provincesTreeMap.put(border.chunkX, new TreeMap<Integer, Province>());
		}
		provincesTreeMap.get(border.chunkX).put(border.chunkZ, border);
	}

	@Override
	public void readFromNBT(NBTTagCompound t)
	{
		NBTTagList list;
		try
		{
			list = (NBTTagList) t.getTag("provinces");
		}
		catch (Exception e)
		{
			list = new NBTTagList();
		}
		for (int i = 0; i < list.tagCount(); i++)
		{
			Province province = new Province();
			province.readNBT(list.getCompoundTagAt(i));
			if (province.id == null)
			{
				province.id = UUID.randomUUID();
				System.out.println("----- adding missing province ID");
				markDirty();
			}
			if (province.name == null || province.name.trim().length() == 0)
			{
				province.name = ProvinceNames.random(new Random(), province.civilization);
				System.out.println("----- adding missing province name");
				markDirty();
			}
			addProvinceToSaveData(province);
		}

		NBTTagList slist = null;
		try {
			slist = (NBTTagList) t.getTag("structures");
		} catch (Exception e) {

		}
		if (slist == null) {
			slist = new NBTTagList();
		}
		for (int i = 0; i < slist.tagCount(); i++) {
			structures.clear();
			Structure s = new Structure();
			s.readNBT(slist.getCompoundTagAt(i));
			structures.add(s);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound t) {
		NBTTagList list = new NBTTagList();
		for (Province p : provinces) {
			list.appendTag(p.writeNBT());
		}
		NBTTagList slist = new NBTTagList();
		for (Structure s : structures) {
			list.appendTag(s.writeNBT());
		}
		t.setTag("provinces", list);
		t.setTag("structures", slist);
		return t;
	}

	public static CivilizationDataAccessor get(World world) {
		MapStorage storage = world.getMapStorage();
		CivilizationsWorldSaveData instance = (CivilizationsWorldSaveData) storage.getOrLoadData(CivilizationsWorldSaveData.class, DATA_NAME);
		if (instance == null) {
			instance = new CivilizationsWorldSaveData();
			storage.setData(DATA_NAME, instance);
		}
		instance.world = world;
		return instance;
	}

	@Override
	public List<Province> getProvinces() {
		List<Province> copy = new ArrayList<Province>();
		copy.addAll(provinces);
		return copy;
	}

	@Override
	public void setName(UUID provinceId, String name)
	{
		for (Province p : provinces)
		{
			if (provinceId.equals(p.id))
			{
				p.name = name;
				markDirty();
				p.writeNBT();
				return;
			}
		}
	}

	

}
