package net.torocraft.toroquest.civilization.quests;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import net.torocraft.toroquest.civilization.CivilizationHandlers;
import net.torocraft.toroquest.civilization.Province;
import net.torocraft.toroquest.civilization.player.PlayerCivilizationCapability;
import net.torocraft.toroquest.civilization.player.PlayerCivilizationCapabilityImpl;
import net.torocraft.toroquest.civilization.quests.QuestCaptureEntity.DataWrapper;
import net.torocraft.toroquest.civilization.quests.util.Quest;
import net.torocraft.toroquest.civilization.quests.util.QuestData;
import net.torocraft.toroquest.civilization.quests.util.Quests;

public class QuestRecruit extends QuestBase implements Quest
{
	public static QuestRecruit INSTANCE;
	public static int ID;

	public static void init(int id)
	{
		INSTANCE = new QuestRecruit();
		Quests.registerQuest(id, INSTANCE);
		MinecraftForge.EVENT_BUS.register(INSTANCE);
		ID = id;
	}

	public void onRecruit(EntityPlayer player)
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

		handleRecruitment(player, province);
	}

	private void handleRecruitment(EntityPlayer player, Province province)
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

		if ( !quest.data.getCompleted() )
		{
			quest.setCurrentAmount(quest.getCurrentAmount() + 1);
			quest.data.getPlayer().sendStatusMessage( new TextComponentString(MathHelper.clamp(quest.getCurrentAmount(), 0, quest.getTargetAmount())+"/"+quest.getTargetAmount()), true);
			
			if (quest.getCurrentAmount() >= quest.getTargetAmount())
			{
				quest.data.setCompleted(true);
				chatCompletedQuest(quest.data);
			}
			return true;
		}
		return false;
	}
	
	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

	@Override
	public List<ItemStack> complete(QuestData quest, List<ItemStack> items)
	{
		if ( !data.getCompleted() )
		{
			if ( data.getChatStack().equals("") )
			{
				data.setChatStack( "You haven't recruited enough guards! Speak to a shopkeeper to purchase recruitment papers." );
				this.setData(data);
			}
			// data.getPlayer().closeScreen();
			return null;
		}

		Province province = loadProvince(quest.getPlayer().world, quest.getPlayer().getPosition());

		if ( province == null || province.id == null || !province.id.equals(quest.getProvinceId()) )
		{
			return null;
		}

//		PlayerCivilizationCapability playerCiv = PlayerCivilizationCapabilityImpl.get(quest.getPlayer());
//		int amount = new DataWrapper().setData(quest).getRewardRep();
//		CivilizationHandlers.adjustPlayerRep(quest.getPlayer(), playerCiv.getInCivilization().civilization, amount);
		
		CivilizationHandlers.adjustPlayerRep(data.getPlayer(), data.getCiv(), getRewardRep(data));

		List<ItemStack> rewards = getRewardItems(quest); // TODO
		if (rewards != null)
		{
			items.addAll(rewards);
		}
		// data.setChatStack( "You have my gratitude, " + data.getPlayer().getName() + "." );
		this.setData(data);
		return items;
	}
	
	@Override
	public List<ItemStack> accept(QuestData data, List<ItemStack> in)
	{
		// if (!data.getPlayer().world.isRemote)
		{
			switch (data.getPlayer().world.rand.nextInt(4))
			{
				case 0:{data.setChatStack( "I am not sure how many more nights the village can survive with our current defenses. " + data.getPlayer().getName() + ", I need you to recruit and arm several of the villagers.");break;}
				case 1:{data.setChatStack( "The villagers are fearful of the nights to come. Recruit several guards to better bolster our defenses.");break;}
				case 2:{data.setChatStack( "We have lost good men in the last raid. Recruit more guards to help protect the village.");break;}
				case 3:{data.setChatStack( "I need you to recruit more guards. The people respect you, " + data.getPlayer().getName() + ", and they are willing to fight for our province.");break;}
			}
		}
		this.setData(data);
		return in;
	}

	@Override
	public List<ItemStack> reject(QuestData data, List<ItemStack> in)
	{
		data.setChatStack( "I can only hope we survive the harsh nights to come..." );
		this.setData(data);
		data.getPlayer().closeScreen();
		return in;
	}
	
	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	
	@Override
	public String getTitle(QuestData data)
	{
		return "quests.recruit_guards.title";
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
		s.append("quests.recruit_guards.description");
		s.append("|").append(q.getTargetAmount());
		s.append("|").append("guards");
		s.append("|").append(getProvinceName(data.getPlayer(), data.getProvinceId()));
		s.append("|").append(q.getCurrentAmount()  + "\n\n" );
		s.append("|").append(listItems(getRewardItems(q.data))  + ",\n" );
		s.append("|").append(getRewardRep(data));
		return s.toString();
	}

	@Override
	public QuestData generateQuestFor(EntityPlayer player, Province province)
	{
		Random rand = new Random();
		DataWrapper q = new DataWrapper();
		q.data.setCiv(province.civilization);
		q.data.setPlayer(player);
		q.data.setProvinceId(province.id);
		q.data.setQuestId(UUID.randomUUID());
		q.data.setQuestType(ID);
		q.data.setCompleted(false);
		int roll = rand.nextInt(5)+3;
		int em = roll*2+2;
		q.setRewardRep(em*2);
		if ( PlayerCivilizationCapabilityImpl.get(player).getReputation(province.civilization) >= 3000 )
		{
			em *= 2;
		}
		q.setCurrentAmount(0);
		q.setTargetAmount(roll);
		ItemStack emeralds = new ItemStack(Items.EMERALD, em);
		List<ItemStack> rewardItems = new ArrayList<ItemStack>();
		rewardItems.add(emeralds);
		setRewardItems(q.data, rewardItems);
		this.setData(q.data);
		return q.data;
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
