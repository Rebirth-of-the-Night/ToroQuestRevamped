package net.torocraft.toroquest.civilization;

import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;

public class Province
{

	public UUID id;
	public String name;
	public boolean hasLord;
	public int chunkX;
	public int chunkZ;

	public int lowerVillageBoundX;
	public int upperVillageBoundX;
	public int lowerVillageBoundZ;
	public int upperVillageBoundZ;

	public int xLength;
	public int zLength;
	public int area;

	public CivilizationType civilization;
	
	// =-=-=-=-=-=-= Trophy =-=-=-=-=-=-=-=-=
	public boolean zombiepigTrophy;
	public boolean skeletonTrophy;
	public boolean banditTrophy;
	public boolean spiderTrophy;
	public boolean mageTrophy;
	public boolean titanTrophy;
	public boolean lordTrophy;
	public boolean beholderTrophy;
	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	
	public boolean hasMageTrophy()
	{
		return this.mageTrophy;
	}
	public boolean hasSpiderTrophy()
	{
		return this.spiderTrophy;
	}
	public boolean hasBanditTrophy()
	{
		return this.banditTrophy;
	}
	public boolean hasSkeletonTrophy()
	{
		return this.skeletonTrophy;
	}
	public boolean hasPigTrophy()
	{
		return this.zombiepigTrophy;
	}
	public boolean hasBeholderTrophy()
	{
		return this.zombiepigTrophy;
	}
	public boolean hasTitanTrophy()
	{
		return this.zombiepigTrophy;
	}
	public boolean hasLordTrophy()
	{
		return this.zombiepigTrophy;
	}


//	public void setPigTrophy(boolean bool)
//	{
//		this.zombiepigTrophy = bool;
//		this.writeNBT();
//	}
	


	public void readNBT(NBTTagCompound c)
	{
		this.id = uuid(c.getString("id"));
		this.chunkX = c.getInteger("chunkX");
		this.chunkZ = c.getInteger("chunkZ");
		this.name = c.getString("name");
		this.lowerVillageBoundX = c.getInteger("lX");
		this.upperVillageBoundX = c.getInteger("uX");
		this.lowerVillageBoundZ = c.getInteger("lZ");
		this.upperVillageBoundZ = c.getInteger("uZ");
		this.civilization = e(c.getString("civilization"));
		this.hasLord = c.getBoolean("hasLord");
		
		// =-=-=-=-=-=-= Trophy =-=-=-=-=-=-=-=-=		
		this.zombiepigTrophy = c.getBoolean("zombiepigTrophy");
		this.skeletonTrophy = c.getBoolean("skeletonTrophy");
		this.banditTrophy = c.getBoolean("banditTrophy");
		this.spiderTrophy = c.getBoolean("spiderTrophy");
		this.mageTrophy = c.getBoolean("mageTrophy");
		this.titanTrophy = c.getBoolean("titanTrophy");
		this.lordTrophy = c.getBoolean("lordTrophy");
		this.beholderTrophy = c.getBoolean("beholderTrophy");
		// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
		
		computeSize();
	}

	private UUID uuid(String s)
	{
		try
		{
			return UUID.fromString(s);
		}
		catch (Exception e)
		{
			return null;
		}
	}

	private CivilizationType e(String s) {
		try {
			return CivilizationType.valueOf(s);
		} catch (Exception e) {
			return null;
		}
	}
	
	public int getCenterX()
	{
		return (this.lowerVillageBoundX+this.upperVillageBoundX)*8;
	}
	
	public int getCenterZ()
	{ 
		return (this.lowerVillageBoundZ+this.upperVillageBoundZ)*8;
	}

	public void addToBoundsAndRecenter(int newChunkX, int newChunkZ)
	{
		lowerVillageBoundX = Math.min(lowerVillageBoundX, newChunkX);
		upperVillageBoundX = Math.max(upperVillageBoundX, newChunkX);
		lowerVillageBoundZ = Math.min(lowerVillageBoundZ, newChunkZ);
		upperVillageBoundZ = Math.max(upperVillageBoundZ, newChunkZ);
		computeSize();
		recenter();
	}

	private void recenter()
	{
		chunkX = (lowerVillageBoundX + xLength / 2);
		chunkZ = (lowerVillageBoundZ + zLength / 2);
	}

	public void computeSize()
	{
		xLength = Math.abs(upperVillageBoundX - lowerVillageBoundX) + 1;
		zLength = Math.abs(upperVillageBoundZ - lowerVillageBoundZ) + 1;
		area = xLength * zLength;
	}

	public NBTTagCompound writeNBT()
	{
		NBTTagCompound c = new NBTTagCompound();
		c.setString("id", s(this.id));
		c.setString("civilization", s(this.civilization));
		c.setInteger("chunkX", this.chunkX);
		c.setInteger("chunkZ", this.chunkZ);
		c.setInteger("lX", this.lowerVillageBoundX);
		c.setInteger("uX", this.upperVillageBoundX);
		c.setInteger("lZ", this.lowerVillageBoundZ);
		c.setInteger("uZ", this.upperVillageBoundZ);
		c.setBoolean("hasLord", this.hasLord);
		
		// =-=-=-=-=-=-= Trophy =-=-=-=-=-=-=-=-=
		c.setBoolean("zombiepigTrophy", this.zombiepigTrophy);
		c.setBoolean("skeletonTrophy", this.skeletonTrophy);
		c.setBoolean("banditTrophy", this.banditTrophy);
		c.setBoolean("spiderTrophy", this.spiderTrophy);
		c.setBoolean("mageTrophy", this.mageTrophy);
		c.setBoolean("titanTrophy", this.titanTrophy);
		c.setBoolean("lordTrophy", this.lordTrophy);
		c.setBoolean("beholderTrophy", this.beholderTrophy);
		// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
		
		if (this.name != null && this.name.trim().length() > 0)
		{
			c.setString("name", this.name);
		}
		return c;
	}
	
	public void setName( String newName )
	{
		this.name = newName;
	}

	private String s(UUID s)
	{
		try
		{
			return s.toString();
		}
		catch (Exception e)
		{
			return "";
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name).append(" of ").append(s(civilization).toLowerCase());
		sb.append(" at [").append(chunkX * 16).append(",").append(chunkZ * 16).append("]");
		if (hasLord) {
			sb.append(" has lord");
		} else {
			sb.append(" no lord");
		}
		return sb.toString();
	}

	private String s(CivilizationType civ) {
		if (civ == null) {
			return "";
		}
		return civ.toString();
	}

	public double chunkDistanceSq(int toChunkX, int toChunkZ) {
		double dx = (double) chunkX - toChunkX;
		double dz = (double) chunkZ - toChunkZ;
		return dx * dx + dz * dz;
	}
}