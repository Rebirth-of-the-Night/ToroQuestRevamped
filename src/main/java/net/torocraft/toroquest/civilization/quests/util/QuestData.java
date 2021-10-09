package net.torocraft.toroquest.civilization.quests.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.server.command.TextComponentHelper;
import net.torocraft.toroquest.civilization.CivilizationType;
import net.torocraft.toroquest.civilization.quests.QuestBase;

public class QuestData
{	
	private UUID questId;
	private Integer questType;
	private UUID provinceId;
	private String provinceName;
	private CivilizationType civ;
	private transient EntityPlayer player;
	private boolean completed = false;
	private Map<String, Integer> iData = new HashMap<String, Integer>();
	private Map<String, String> sData = new HashMap<String, String>();
	private NBTBase custom = new NBTTagCompound();

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
		s.append("id[").append(this.questId).append("]");
		s.append(" questType[").append(this.questType).append("]");
		s.append(" provinceId[").append(this.provinceId).append("]");
		for (Entry<String, Integer> e : this.iData.entrySet()) {
			s.append(" idata_" + e.getKey() + "[").append(e.getValue()).append("]");
		}
		for (Entry<String, Integer> e : this.iData.entrySet()) {
			s.append(" sdata_" + e.getKey() + "[").append(e.getValue()).append("]");
		}
		return s.toString();
	}

	public boolean isValid()
	{
		return this.questId != null && this.questType != null && Quests.getQuestForId(this.questType) != null && this.provinceId != null;
	}

	public void readNBT(NBTTagCompound c, EntityPlayer player) {
		this.player = player;
		this.questId = UUID.fromString(c.getString("id"));
		this.questType = c.getInteger("type");
		this.provinceId = UUID.fromString(c.getString("provinceId"));
		this.civ = e(c.getString("civ"));
		this.iData = readIMap(c.getCompoundTag("idata"));
		this.sData = readSMap(c.getCompoundTag("sdata"));
		this.completed = c.getBoolean("completed");
		this.chatStack = c.getString("chatStack");
		this.custom = c.getTag("custom");

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
		c.setString("id", this.questId.toString());
		c.setInteger("type", this.questType);
		c.setString("provinceId", this.provinceId.toString());
		c.setString("civ", s(civ));
		c.setTag("idata", writeIMap(this.iData));
		c.setTag("sdata", writeSMap(this.sData));
		c.setBoolean("completed", this.completed);
		c.setString("chatStack", this.chatStack);
		if (this.custom == null) {
			System.out.println("**** quest custom data was null ***");
			this.custom = new NBTTagCompound();
		}

		c.setTag("custom", this.custom);
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
		result = prime * result + ((this.questId == null) ? 0 : this.questId.hashCode());
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
		if (this.questId == null) {
			if (other.questId != null)
				return false;
		} else if (!this.questId.equals(other.questId))
			return false;
		return true;
	}

	public UUID getQuestId() {
		return this.questId;
	}

	public void setQuestId(UUID questId) {
		this.questId = questId;
	}

	public Integer getQuestType() {
		return this.questType;
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
		return this.civ;
	}

	public void setCiv(CivilizationType civ) {
		this.civ = civ;
	}
	
	public void setProvinceName(String province) {
		this.provinceName = province;
	}
	
	public String getProvinceName() {
		return this.provinceName;
	}

	public EntityPlayer getPlayer() {
		return this.player;
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
		return this.completed;
	}
	
			//	public String getChatStack() CSTA
			//	{
			//		return this.chatStack;
			//	}
	
	public String getChatStack()
	{
		return "";
	}
	
	public void setCompleted(Boolean completed)
	{
		this.completed = completed;
	}
	
						//	public void setChatStackString(String chatStack, EntityPlayer player, @Nullable String extra) CSTA
						//	{
						//		chatStack = chatStack.replace("@p", player.getDisplayName().toString());
						//		if ( extra != null )
						//		{
						//			chatStack = chatStack.replace("@e", extra);
						//		}
						//		this.chatStack = chatStack;
						//	}
	
	public void setChatStackString(String chatStack, EntityPlayer player, @Nullable String extra)
	{
		chatStack = chatStack.replace("@p", player.getDisplayNameString());
		if ( extra != null )
		{
			chatStack = chatStack.replace("@e", extra);
		}
		QuestBase.chat( player, this.getProvinceName(), chatStack );
	}
	
	
	
	
	
	
	public void setChatStack(String chatStack, EntityPlayer player, @Nullable String extra)
	{
		String s = "";
		chatStack = "quests."+chatStack;
		
		try
		{
			s = TextComponentHelper.createComponentTranslation(player, chatStack+player.world.rand.nextInt(  Integer.parseInt(TextComponentHelper.createComponentTranslation(player,chatStack,new Object[0]).getUnformattedText())  ), new Object[0]).getFormattedText();
			// s = I18n.format(I18n.format(chatStack+player.world.rand.nextInt(Integer.parseInt(I18n.format(chatStack)))));
		}
		catch ( Exception e )
		{
			s = TextComponentHelper.createComponentTranslation(player, chatStack, new Object[0]).getFormattedText();
		}
		
		s = s.replace("@p", player.getDisplayNameString());

		if ( extra != null )
		{
			s = s.replace("@e", extra);
		}
		
		QuestBase.chat( player, this.getProvinceName(), s );
	}
	
							//	public void setChatStack(String chatStack, EntityPlayer player, @Nullable String extra) CSTA
							//	{
							//		String s = "";
							//		chatStack = "quests."+chatStack;
							//		
							//		try
							//		{
							//			s = TextComponentHelper.createComponentTranslation(player, chatStack+player.world.rand.nextInt(  Integer.parseInt(TextComponentHelper.createComponentTranslation(player,chatStack,new Object[0]).getUnformattedText())  ), new Object[0]).getFormattedText();
							//			// s = I18n.format(I18n.format(chatStack+player.world.rand.nextInt(Integer.parseInt(I18n.format(chatStack)))));
							//		}
							//		catch ( Exception e )
							//		{
							//			s = TextComponentHelper.createComponentTranslation(player, chatStack, new Object[0]).getFormattedText();
							//		}
							//		
							//		s = s.replace("@p", player.getDisplayNameString());
							//
							//		if ( extra != null )
							//		{
							//			s = s.replace("@e", extra);
							//		}
							//		
							//		this.chatStack = s;
							//	}
	
	private String chatStack = "";

	public void clearChatStack()
	{
		this.chatStack = "";
	}

	public Map<String, String> getsData() {
		return this.sData;
	}

	public void setsData(Map<String, String> sData) {
		this.sData = sData;
	}

	public NBTBase getCustom() {
		return this.custom;
	}

	public void setCustom(NBTBase custom) {
		this.custom = custom;
	}

}
