package net.torocraft.toroquest.civilization.quests.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.torocraft.toroquest.civilization.CivilizationType;
import net.torocraft.toroquest.civilization.quests.QuestBase;
import net.torocraft.toroquest.civilization.quests.QuestKillBossZombiePig;

public class QuestData
{	
	private UUID questId;
	private Integer questType;
	private UUID provinceId;
	private String provinceName;
	private CivilizationType civ;
	private transient EntityPlayer player;
	private Boolean completed = false;
	private Map<String, Integer> iData = new HashMap<String, Integer>();
	private Map<String, String> sData = new HashMap<String, String>();
	private NBTBase custom = new NBTTagCompound();
	private String chatStack = "";

//	@SubscribeEvent
//	protected void closeUI(GuiOpenEvent event)
//	{
//		if ( event.getGui() == null )
//		{
//			System.out.println("m");
//
//			if ( this.getChatStack() != "" )
//			{
//				QuestBase.chat( this, this.getChatStack() );
//	            EntityPlayer player = this.getPlayer();
//	            if ( player == null ) 
//	            {
//	            	return;
//	            }
//	        	//QuestKillBossZombiePig.INSTANCE.chatCompletedQuest(data);
//				if ( !player.world.isRemote ) player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.E_PARROT_IM_ZOMBIE_VILLAGER, SoundCategory.VOICE, 0.9F, 1.2F);					
//			}
//			this.setChatStack("");
//		}
//	}
	
	@Override
	public String toString()
	{
		StringBuilder s = new StringBuilder();
		s.append("id[").append(questId).append("]");
		s.append(" questType[").append(questType).append("]");
		s.append(" provinceId[").append(provinceId).append("]");
		for (Entry<String, Integer> e : iData.entrySet()) {
			s.append(" idata_" + e.getKey() + "[").append(e.getValue()).append("]");
		}
		for (Entry<String, Integer> e : iData.entrySet()) {
			s.append(" sdata_" + e.getKey() + "[").append(e.getValue()).append("]");
		}
		return s.toString();
	}

	public boolean isValid()
	{
		return questId != null && questType != null && Quests.getQuestForId(questType) != null && provinceId != null;
	}

	public void readNBT(NBTTagCompound c, EntityPlayer player) {
		this.player = player;
		questId = UUID.fromString(c.getString("id"));
		questType = c.getInteger("type");
		provinceId = UUID.fromString(c.getString("provinceId"));
		civ = e(c.getString("civ"));
		iData = readIMap(c.getCompoundTag("idata"));
		sData = readSMap(c.getCompoundTag("sdata"));
		completed = c.getBoolean("completed");
		chatStack = c.getString("chatStack");
		custom = c.getTag("custom");

	}

	private Map<String, Integer> readIMap(NBTTagCompound c) {
		Map<String, Integer> m = new HashMap<String, Integer>();
		for (String key : c.getKeySet()) {
			m.put(key, c.getInteger(key));
		}
		return m;
	}

	private NBTTagCompound writeIMap(Map<String, Integer> m) {
		NBTTagCompound c = new NBTTagCompound();
		for (Entry<String, Integer> e : m.entrySet()) {
			c.setInteger(e.getKey(), e.getValue());
		}
		return c;
	}

	private Map<String, String> readSMap(NBTTagCompound c) {
		Map<String, String> m = new HashMap<String, String>();
		for (String key : c.getKeySet()) {
			m.put(key, c.getString(key));
		}
		return m;
	}

	private NBTTagCompound writeSMap(Map<String, String> m) {
		NBTTagCompound c = new NBTTagCompound();
		for (Entry<String, String> e : m.entrySet()) {
			c.setString(e.getKey(), e.getValue());
		}
		return c;
	}

	public NBTTagCompound writeNBT() {
		NBTTagCompound c = new NBTTagCompound();
		c.setString("id", questId.toString());
		c.setInteger("type", questType);
		c.setString("provinceId", provinceId.toString());
		c.setString("civ", s(civ));
		c.setTag("idata", writeIMap(iData));
		c.setTag("sdata", writeSMap(sData));
		c.setBoolean("completed", completed);
		c.setString("chatStack", chatStack);
		if (custom == null) {
			System.out.println("**** quest custom data was null, boo.");
			custom = new NBTTagCompound();
		}

		c.setTag("custom", custom);
		return c;
	}

	private String s(CivilizationType civ) {
		if (civ == null) {
			return "";
		}
		return civ.toString();
	}

	private CivilizationType e(String s) {
		try {
			return CivilizationType.valueOf(s);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((questId == null) ? 0 : questId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		QuestData other = (QuestData) obj;
		if (questId == null) {
			if (other.questId != null)
				return false;
		} else if (!questId.equals(other.questId))
			return false;
		return true;
	}

	public UUID getQuestId() {
		return questId;
	}

	public void setQuestId(UUID questId) {
		this.questId = questId;
	}

	public Integer getQuestType() {
		return questType;
	}

	public void setQuestType(Integer questType) {
		this.questType = questType;
	}

	public UUID getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(UUID provinceId) {
		this.provinceId = provinceId;
	}

	public CivilizationType getCiv() {
		return civ;
	}

	public void setCiv(CivilizationType civ) {
		this.civ = civ;
	}
	
	public void setProvinceName(String province) {
		this.provinceName = province;
	}
	
	public String getProvinceName() {
		return provinceName;
	}

	public EntityPlayer getPlayer() {
		return player;
	}

	public void setPlayer(EntityPlayer player) {
		this.player = player;
	}

	public Map<String, Integer> getiData() {
		return iData;
	}

	public void setiData(Map<String, Integer> iData) {
		this.iData = iData;
	}

	public Boolean getCompleted()
	{
		return completed;
	}
	
	public String getChatStack()
	{
		return chatStack;
	}
	
	public void setCompleted(Boolean completed)
	{
		this.completed = completed;
	}
	
	public void setChatStack(String chatStack)
	{
		this.chatStack = chatStack;
	}

	public Map<String, String> getsData() {
		return sData;
	}

	public void setsData(Map<String, String> sData) {
		this.sData = sData;
	}

	public NBTBase getCustom() {
		return custom;
	}

	public void setCustom(NBTBase custom) {
		this.custom = custom;
	}

}
