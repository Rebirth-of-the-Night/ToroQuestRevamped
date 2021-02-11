package net.torocraft.toroquest.civilization.quests;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.torocraft.toroquest.civilization.CivilizationHandlers;
import net.torocraft.toroquest.civilization.Province;
import net.torocraft.toroquest.civilization.player.PlayerCivilizationCapabilityImpl;
import net.torocraft.toroquest.civilization.quests.util.Quest;
import net.torocraft.toroquest.civilization.quests.util.QuestData;
import net.torocraft.toroquest.civilization.quests.util.Quests;
import net.torocraft.toroquest.entities.EntitySentry;
import net.torocraft.toroquest.entities.EntityWolfRaider;

public class QuestKillMobs extends QuestBase implements Quest
{
	public static QuestKillMobs INSTANCE;

	public static int ID;

	public static void init(int id)
	{
		INSTANCE = new QuestKillMobs();
		Quests.registerQuest(id, INSTANCE);
		MinecraftForge.EVENT_BUS.register(INSTANCE);
		ID = id;
	}

	@SubscribeEvent
	public void onKill(LivingDeathEvent event)
	{
		EntityPlayer player = null;
		EntityLivingBase victim = (EntityLivingBase) event.getEntity();
		DamageSource source = event.getSource();

		if ( source == null || source.getTrueSource() == null )
		{
			return;
		}
		
		if ( source.getTrueSource() instanceof EntityPlayer )
		{
			player = (EntityPlayer) source.getTrueSource();
		}

		if ( player == null )
		{
			return;
		}

		handleKillMobsQuest(player, victim);
	}

	private void handleKillMobsQuest(EntityPlayer player, EntityLivingBase victim)
	{
		if (victim == null)
		{
			return;
		}
		
		if ( !(victim instanceof EntityMob) && !(victim instanceof IMob) )
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
				quest.huntedMob = EntityList.getKey(victim).getResourcePath();
				//quest.provinceHuntedIn = provinceHuntedIn;
				if ( perform(quest) )
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

		quest.setCurrentAmount(quest.getCurrentAmount() + 1);
		quest.data.getPlayer().sendStatusMessage( new TextComponentString(MathHelper.clamp(quest.getCurrentAmount(), 0, quest.getTargetAmount())+"/"+quest.getTargetAmount()), true);

		if ( !quest.data.getCompleted() && quest.getCurrentAmount() >= quest.getTargetAmount() )
		{
			quest.data.setCompleted(true);
			chatCompletedQuest(quest.data);
		}

		return true;
	}

	@Override
	public List<ItemStack> complete(QuestData quest, List<ItemStack> items)
	{
		if ( !quest.getCompleted() )
		{
			if ( !quest.getChatStack().equals("") )
			{
				quest.setChatStack("You haven't slain enough monsters!");
				this.setData(quest);
			}
			// quest.getPlayer().closeScreen();
			return null;
		}

		Province province = loadProvince(quest.getPlayer().world, quest.getPlayer().getPosition());

		if (province == null || province.id == null || !province.id.equals(quest.getProvinceId())) {
			return null;
		}

		// PlayerCivilizationCapability playerCiv = PlayerCivilizationCapabilityImpl.get(quest.getPlayer());

		// int amount = new DataWrapper().setData(quest).getRewardRep();
		// CivilizationHandlers.adjustPlayerRep(quest.getPlayer(), playerCiv.getInCivilization().civilization, amount);
		//playerCiv.adjustReputation(quest.getCiv(), new DataWrapper().setData(quest).getRewardRep());

		CivilizationHandlers.adjustPlayerRep(data.getPlayer(), data.getCiv(), getRewardRep(data));

		List<ItemStack> rewards = getRewardItems(quest);
		if (rewards != null)
		{
			items.addAll(rewards);
		}
		// quest.setChatStack( "You have my gratitude, " + quest.getPlayer().getName() + "." );
		this.setData(quest);
		return items;
	}

	@Override
	public List<ItemStack> reject(QuestData data, List<ItemStack> in)
	{
		data.setChatStack( "Very well. I can only hope we survive the harsh nights to come..." );
		this.setData(data);
		data.getPlayer().closeScreen();
		return in;
	}

	@Override
	public List<ItemStack> accept(QuestData data, List<ItemStack> in)
	{
		// if (!data.getPlayer().world.isRemote)
		{
			switch (data.getPlayer().world.rand.nextInt(7))
			{
				case 0:
				{
					data.setChatStack( "The city has been rampaged enough by creatures of the night for too long! Kill them, " + data.getPlayer().getName() + ". Kill them all!" );
					break;
				}
				case 1:
				{
					data.setChatStack( "We cannot afford another raid on the city, " + data.getPlayer().getName() + ". You must help us. Kill the vile creatures of night which fighten our people!");
					break;
				}
				case 2:
				{
					data.setChatStack( "Looking to wet your blade, " + data.getPlayer().getName() + "? Slay these vile creatures that attack our city and I will reward you handsomly." );                                                                  
					break;
				}
				case 3:
				{
					data.setChatStack( "Put an end to the attacks on the city. Slay as many of those vicious creatures as you can... you will be rewarded, of course." );
					break;
				}
				case 4:
				{
					data.setChatStack( "The guard have been struggling to deal with raids on the city. " + data.getPlayer().getName() + ", come to their aid and help them fight off these attackers." );
					break;
				}
				case 5:
				{
					data.setChatStack( "The villagers are fearful. Bandits... wolves... undead... it just wont stop. We need your expertise in combat, " + data.getPlayer().getName() + ". Help protect the city and it's people!" );
					break;
				}
				case 6:
				{
					data.setChatStack( "We have lost too many villagers to these creatures of the night. I need you to put a stop to their rampage, and kill these monsters." );
					break;
				}
			}
		}
		this.setData(data);
		return in;
	}

	@Override
	public String getTitle(QuestData data) {
		return "quests.kill_mobs.title";
		// return "Kill " + q.getTargetAmount() + " " + mobName(q.getMobType(),
		// data.getPlayer()) + " in " + getProvinceName(data.getPlayer(),
		// data.getProvinceId());
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
		s.append("quests.kill_mobs.description");
		s.append("|").append(q.getTargetAmount());
		// s.append("|").append("of these vile");
		s.append("|").append(getProvinceName(data.getPlayer(), data.getProvinceId()));
		s.append("|").append(q.getCurrentAmount()  + "\n\n" );
		s.append("|").append(listItems(getRewardItems(q.data))  + ",\n" );
		s.append("|").append(getRewardRep(data));
		return s.toString();
	}

//	private String mobName(Integer mobType, EntityPlayer player) {
//		Entity mob = EntityList.createEntityByIDFromName(new ResourceLocation(MOB_TYPES[mobType]), player.world);
//		return mob.getName();
//	}

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


		//q.setMobType(rand.nextInt(MOB_TYPES.length));
		q.setCurrentAmount(0);
		
		int roll = rand.nextInt(7)*4+12;
		int em = (int)Math.round((double)roll/3)+2;
		int rep = em*2;
		if ( PlayerCivilizationCapabilityImpl.get(player).getReputation(province.civilization) >= 3000 )
		{
			em *= 2;
		}
		q.setRewardRep(rep);
		q.setTargetAmount(roll);
		ItemStack emeralds = new ItemStack(Items.EMERALD, em); // emerald reward
		List<ItemStack> rewardItems = new ArrayList<ItemStack>();
		rewardItems.add(emeralds);
		setRewardItems(q.data, rewardItems);
		this.setData(q.data);
		return q.data;
	}

	public static class DataWrapper
	{
		private QuestData data = new QuestData();
		private Province provinceHuntedIn;
		private String huntedMob;

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
			return provinceHuntedIn;
		}

		public void setProvinceHuntedIn(Province provinceHuntedIn)
		{
			this.provinceHuntedIn = provinceHuntedIn;
		}

		public String getHuntedMob()
		{
			return huntedMob;
		}

		public void setHuntedMob(String huntedMob) 
		{
			this.huntedMob = huntedMob;
		}

		public Integer getMobType()
		{
			return i(data.getiData().get("type"));
		}

		public void setMobType(Integer mobType)
		{
			data.getiData().put("type", mobType);
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
			return isKillMobsQuest(); // && isInCorrectProvince();
		}

		private boolean isKillMobsQuest()
		{
			return data.getQuestType() == ID;
		}

//		private boolean isInCorrectProvince()
//		{
//			return data.getProvinceId().equals(getProvinceHuntedIn().id);
//		}

	}
}
