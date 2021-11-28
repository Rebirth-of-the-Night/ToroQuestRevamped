package net.torocraft.toroquest.civilization.quests;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import net.minecraft.client.resources.I18n;
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

public class QuestTradeWithVillagers extends QuestBase implements Quest
{
	public static QuestTradeWithVillagers INSTANCE;
	public static int ID;

	public static void init(int id)
	{
		INSTANCE = new QuestTradeWithVillagers();
		Quests.registerQuest(id, INSTANCE);
		MinecraftForge.EVENT_BUS.register(INSTANCE);
		ID = id;
	}

	public void onTrade(EntityPlayer player)
	{
		if (player == null)
		{
			return;
		}

		Province province = PlayerCivilizationCapabilityImpl.get(player).getInCivilization();

		if (province == null || province.civilization == null)
		{
			return;
		}

		handleTrade(player, province);
	}

	private void handleTrade(EntityPlayer player, Province province)
	{
		Set<QuestData> quests = PlayerCivilizationCapabilityImpl.get(player).getCurrentQuests();

		DataWrapper quest = new DataWrapper();
		for (QuestData data : quests)
		{
			try
			{
				quest.setData(data);
				quest.province = province;
				
				if (perform(quest))
				{
					return;
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= QUEST STATUS =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	
	private boolean perform(DataWrapper quest)
	{
		if (quest.getData().getPlayer().world.isRemote)
		{
			return false;
		}

		if (!quest.isApplicable())
		{
			return false;
		}

		if ( !quest.getData().getCompleted() )
		{
			quest.setCurrentAmount(quest.getCurrentAmount() + 1);
			quest.getData().getPlayer().sendStatusMessage( new TextComponentString(MathHelper.clamp(quest.getCurrentAmount(), 0, quest.getTargetAmount())+"/"+quest.getTargetAmount()), true);
	
			if (quest.getCurrentAmount() >= quest.getTargetAmount())
			{
				quest.getData().setCompleted(true);
				this.chatCompletedQuest(quest.getData());
			}
			return true;
		}
		return false;
	}
	
	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

	@Override
	public List<ItemStack> complete(QuestData quest, List<ItemStack> items)
	{
		Province province = loadProvince(quest.getPlayer().world, quest.getPlayer().getPosition());

		if ( province == null || province.id == null || !province.id.equals(quest.getProvinceId()) )
		{
			return null;
		}
		
		if ( !quest.getCompleted() )
		{
			if ( quest.getChatStack().equals("") )
			{
				quest.setChatStack("trade.incomplete", quest.getPlayer(), null);
				this.setData(quest);
			}
			// data.getPlayer().closeScreen();
			return null;
		}

//		PlayerCivilizationCapability playerCiv = PlayerCivilizationCapabilityImpl.get(quest.getPlayer());
//		int amount = new DataWrapper().setData(quest).getRewardRep();
//		CivilizationHandlers.adjustPlayerRep(quest.getPlayer(), playerCiv.getInCivilization().civilization, amount);

		CivilizationHandlers.adjustPlayerRep(quest.getPlayer(), quest.getCiv(), getRewardRep(quest));
		
		if ( PlayerCivilizationCapabilityImpl.get(quest.getPlayer()).getReputation(quest.getCiv()) >= 3000 )
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

		List<ItemStack> rewards = getRewardItems(quest); // TODO
		if (rewards != null)
		{
			items.addAll(rewards);
		}
		quest.setChatStack("trade.complete", quest.getPlayer(), null);
		this.setData(quest);
		return items;
	}
	
	@Override
	public List<ItemStack> accept(QuestData data, List<ItemStack> in)
	{
		data.setChatStack("trade.accept", data.getPlayer(), null);
		this.setData(data);
		return in;
	}

	@Override
	public List<ItemStack> reject(QuestData data, List<ItemStack> in)
	{
		if ( data.getCompleted() )
		{
			return null;
		}
		
		data.setChatStack("trade.reject", data.getPlayer(), null);
		this.setData(data);
		data.getPlayer().closeScreen();
		return in;
	}
	
	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	
	@Override
	public String getTitle(QuestData data)
	{
		return "quests.trade_villagers.title";
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
		s.append("quests.trade_villagers.description");
		s.append("|").append(q.getTargetAmount());
		s.append("|").append(getProvinceName(data.getPlayer(), data.getProvinceId()));
		s.append("|").append(q.getCurrentAmount());
		s.append("|").append("\n\n");
		s.append("|").append(listItems(getRewardItems(q.getData())) + ",\n" );
		s.append("|").append(getRewardRep(data));
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
		int roll = rand.nextInt(5)*2+8;
		int em = roll;
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

	public static class DataWrapper
	{
		private QuestData data = new QuestData();
		private Province province;

		public QuestData getData()
		{
			return data;
		}

		public DataWrapper setData(QuestData data)
		{
			this.data = data;
			return this;
		}

		public Province getProvinceHuntedIn()
		{
			return province;
		}

		public void setProvinceHuntedIn(Province provinceHuntedIn)
		{
			this.province = provinceHuntedIn;
		}
		

		public Integer getTargetAmount()
		{
			return i(data.getiData().get("target"));
		}

		public void setTargetAmount(Integer targetAmount)
		{
			data.getiData().put("target", targetAmount);
		}

		public Integer getCurrentAmount()
		{
			return i(data.getiData().get("amount"));
		}

		public void setCurrentAmount(Integer currentAmount)
		{
			data.getiData().put("amount", currentAmount);
		}

		public Integer getRewardRep()
		{
			return i(data.getiData().get("rep"));
		}

		public void setRewardRep(Integer rewardRep)
		{
			data.getiData().put("rep", rewardRep);
		}

		private Integer i(Object o)
		{
			try
			{
				return (Integer) o;
			}
			catch (Exception e)
			{
				return 0;
			}
		}

		private boolean isApplicable()
		{
			return isCorrectQuest() && isInCorrectProvince();
		}

		private boolean isCorrectQuest()
		{
			return data.getQuestType() == ID;
		}

		private boolean isInCorrectProvince()
		{
			return data.getProvinceId().equals(getProvinceHuntedIn().id);
		}

	}
}
