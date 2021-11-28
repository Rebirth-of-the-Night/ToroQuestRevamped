package net.torocraft.toroquest.civilization.quests;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.torocraft.toroquest.civilization.CivilizationHandlers;
import net.torocraft.toroquest.civilization.Province;
import net.torocraft.toroquest.civilization.player.PlayerCivilizationCapabilityImpl;
import net.torocraft.toroquest.civilization.quests.util.Quest;
import net.torocraft.toroquest.civilization.quests.util.QuestData;
import net.torocraft.toroquest.civilization.quests.util.Quests;

public class QuestFarm extends QuestBase implements Quest
{
	public static QuestFarm INSTANCE;
	public static int ID;

	public static void init(int id)
	{
		INSTANCE = new QuestFarm();
		Quests.registerQuest(id, INSTANCE);
		MinecraftForge.EVENT_BUS.register(INSTANCE);
		ID = id;
	}
	
	public void onFarm( EntityPlayer player )
	{
		if ( player == null ) // || player.world.isRemote )
		{
			return;
		}
			
		Province province = PlayerCivilizationCapabilityImpl.get(player).getInCivilization();

		if (province == null || province.civilization == null)
		{
			return;
		}
		
		Set<QuestData> quests = PlayerCivilizationCapabilityImpl.get(player).getCurrentQuests();

		DataWrapper quest = new DataWrapper();
		
		for (QuestData data : quests)
		{
			try
			{
				quest.setData(data);
				
				if ( !quest.isFarmQuest() )
				{
					continue;
				}
				
				if ( !quest.getData().getCompleted() )
				{
					quest.setCurrentAmount(quest.getCurrentAmount() + 1);
					
					quest.getData().getPlayer().sendStatusMessage( new TextComponentString(MathHelper.clamp(quest.getCurrentAmount(), 0, quest.getTargetAmount())+"/"+quest.getTargetAmount()), true);
					if ( quest.getCurrentAmount() >= quest.getTargetAmount() )
					{
						quest.getData().setCompleted(true);
						chatCompletedQuest(quest.getData());
					}
				}
			}
			catch (Exception e)
			{

			}
		}
	}
	
	public void destroyedCrop( EntityPlayer player )
	{
		if ( player == null ) // || player.world.isRemote )
		{
			return;
		}
		
		Province province = PlayerCivilizationCapabilityImpl.get(player).getInCivilization();

		if (province == null || province.civilization == null)
		{
			return;
		}
		
		Set<QuestData> quests = PlayerCivilizationCapabilityImpl.get(player).getCurrentQuests();

		DataWrapper quest = new DataWrapper();
		
		for (QuestData data : quests)
		{
			try
			{
				quest.setData(data);
				
				if ( !quest.isFarmQuest() )
				{
					continue;
				}
				
				if ( !quest.getData().getCompleted() )
				{
					quest.setCurrentAmount(quest.getCurrentAmount() - 1);
					if ( quest.getCurrentAmount() < 0 )
					{
						quest.setCurrentAmount(0);
					}
					quest.getData().getPlayer().sendStatusMessage( new TextComponentString(MathHelper.clamp(quest.getCurrentAmount(), 0, quest.getTargetAmount())+"/"+quest.getTargetAmount()), true);
				}
			}
			catch (Exception e)
			{

			}
		}
	}
	
	public void perform(DataWrapper quest, boolean plant)
	{
		if ( !quest.getData().getCompleted() )
		{
			if (quest.getData().getPlayer().world.isRemote)
			{
				return;
			}
			
			if ( !(quest.getData().getQuestType() == ID) )
			{
				return;
			}
			
			if (plant)
			{
				quest.setCurrentAmount(quest.getCurrentAmount() + 1);
			}
			else
			{
				quest.setCurrentAmount(quest.getCurrentAmount() - 1);
				if ( quest.getCurrentAmount() < 0 )
				{
					quest.setCurrentAmount(0);
				}
			}
			
			quest.getData().getPlayer().sendStatusMessage( new TextComponentString(MathHelper.clamp(quest.getCurrentAmount(), 0, quest.getTargetAmount())+"/"+quest.getTargetAmount()), true);
			
			if ( quest.getCurrentAmount() >= quest.getTargetAmount() )
			{
				quest.setCurrentAmount(quest.getTargetAmount());
				quest.getData().setCompleted(true);
				chatCompletedQuest(quest.getData());
			}
			return;
		}
		return;
	}

	@Override
	public String getTitle(QuestData data)
	{
		return "quests.farm.title";
	}

	@Override
	public String getDescription(QuestData data)
	{
		if (data == null)
		{
			return "";
		}
		DataWrapper q = new DataWrapper().setData(data);
		StringBuilder s = new StringBuilder();
		s.append("quests.farm.description");
		s.append("|").append(q.getTargetAmount());
		s.append("|").append(getProvinceName(data.getPlayer(), data.getProvinceId()));
		s.append("|").append(q.getCurrentAmount() + "\n\n" );
		s.append("|").append(listItems(getRewardItems(data)) + ",\n");
		s.append("|").append(q.getRewardRep());
		return s.toString();
	}

	@Override
	public QuestData generateQuestFor(EntityPlayer player, Province province)
	{
		Random rand = new Random();
		DataWrapper q = new DataWrapper();
		q.getData().setCiv(province.civilization);
		q.getData().setPlayer(player);
		q.getData().setProvinceId(province.id);
		q.getData().setQuestId(UUID.randomUUID());
		q.getData().setQuestType(ID);
		q.getData().setCompleted(false);

		int roll = rand.nextInt(5)*16+32;
		int em = (int)Math.round((double)roll/16)+2;

		q.setRewardRep(em*2);
		
		if ( PlayerCivilizationCapabilityImpl.get(player).getReputation(province.civilization) >= 2000 )
		{
			em *= 2;
		}

		q.setCurrentAmount(0);
		
		q.setTargetAmount(roll);
		ItemStack emeralds = new ItemStack(Items.EMERALD, em);
		List<ItemStack> rewardItems = new ArrayList<ItemStack>();
		rewardItems.add(emeralds);
		setRewardItems(q.getData(), rewardItems);
		this.setData(q.getData());
		return q.getData();
	}

	@Override
	public List<ItemStack> reject(QuestData data, List<ItemStack> in)
	{
		if ( data.getCompleted() )
		{
			return null;
		}
		
		data.setChatStack( "farm.reject", data.getPlayer(), null );
		this.setData(data);
		data.getPlayer().closeScreen();
		return in;
	}
	
	@Override
	public List<ItemStack> accept(QuestData data, List<ItemStack> in)
	{
		data.setChatStack( "farm.accept", data.getPlayer(), ""+(data.getiData().get("target")) );
		this.setData(data);
		return in;
	}

	@Override
	public List<ItemStack> complete(QuestData quest, List<ItemStack> items)
	{
		Province province = loadProvince(quest.getPlayer().world, quest.getPlayer().getPosition());

		if (province == null || province.id == null || !province.id.equals(quest.getProvinceId()))
		{
			return null;
		}
		
		if ( !quest.getCompleted() )
		{
			if ( quest.getChatStack().equals("")  )
			{
				quest.setChatStack( "farm.incomplete", quest.getPlayer(), ""+(quest.getiData().get("target")-quest.getiData().get("amount")) );
			    this.setData(quest);
			}
			return null;
		}

		CivilizationHandlers.adjustPlayerRep(quest.getPlayer(), quest.getCiv(), getRewardRep(quest));

		if ( PlayerCivilizationCapabilityImpl.get(quest.getPlayer()).getReputation(province.civilization) >= 3000 )
		{
			if (!quest.getPlayer().world.isRemote)
	        {
	            int i = getRewardRep(quest)*2;

	            while (i > 0)
	            {
	                int j = EntityXPOrb.getXPSplit(i);
	                i -= j;
	                quest.getPlayer().world.spawnEntity(new EntityXPOrb(quest.getPlayer().world, quest.getPlayer().posX+((rand.nextInt(2)*2-1)*2), quest.getPlayer().posY, quest.getPlayer().posZ+((rand.nextInt(2)*2-1)*2), j));
	            }
	        }
		}
		
		List<ItemStack> rewards = getRewardItems(quest);
		if (rewards != null)
		{
			items.addAll(rewards);
		}

		quest.setChatStack( "farm.complete", quest.getPlayer(), null );
		this.setData(quest);
		
		return items;
	}

	public static class DataWrapper
	{
		QuestData data = new QuestData();
		private Province provinceFarmedIn;
		private Block farmedCrop;
		
		public QuestData getData() {
			return data;
		}

		public DataWrapper setData(QuestData data) {
			this.data = data;
			return this;
		}

		public Province getProvinceFarmedIn() {
			return provinceFarmedIn;
		}

		public void setProvinceFarmedIn(Province provinceFarmedIn) {
			this.provinceFarmedIn = provinceFarmedIn;
		}

		public Block getFarmedCrop() {
			return farmedCrop;
		}

		public void setFarmedCrop(Block farmedCrop) {
			this.farmedCrop = farmedCrop;
		}

//		public Integer getCropType() {
//			return i(data.getiData().get("type"));
//		}
//
//		public void setCropType(Integer cropType) {
//			data.getiData().put("type", cropType);
//		}

		public Integer getTargetAmount() {
			return i(data.getiData().get("target"));
		}

		public void setTargetAmount(Integer targetAmount) {
			data.getiData().put("target", targetAmount);
		}

		public Integer getCurrentAmount() {
			return i(data.getiData().get("amount"));
		}

		public void setCurrentAmount(Integer currentAmount) {
			data.getiData().put("amount", currentAmount);
		}

		public Integer getRewardRep() {
			return i(data.getiData().get("rep"));
		}

		public void setRewardRep(Integer rewardRep) {
			data.getiData().put("rep", rewardRep);
		}

		private Integer i(Object o) {
			try {
				return (Integer) o;
			} catch (Exception e) {
				return 0;
			}
		}

		private boolean isFarmQuest()
		{
			return data.getQuestType() == ID;
		}

		private boolean isInCorrectProvince( Province province)
		{
			return data.getProvinceId().equals(province.id);
		}
	}
}
