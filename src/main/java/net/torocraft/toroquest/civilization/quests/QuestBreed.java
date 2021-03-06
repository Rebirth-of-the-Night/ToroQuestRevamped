package net.torocraft.toroquest.civilization.quests;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.torocraft.toroquest.civilization.CivilizationHandlers;
import net.torocraft.toroquest.civilization.Province;
import net.torocraft.toroquest.civilization.player.PlayerCivilizationCapability;
import net.torocraft.toroquest.civilization.player.PlayerCivilizationCapabilityImpl;
import net.torocraft.toroquest.civilization.quests.QuestCaptureEntity.DataWrapper;
import net.torocraft.toroquest.civilization.quests.util.Quest;
import net.torocraft.toroquest.civilization.quests.util.QuestData;
import net.torocraft.toroquest.civilization.quests.util.Quests;

public class QuestBreed extends QuestBase implements Quest
{
	public static QuestBreed INSTANCE;
	public static int ID;

	public static void init(int id)
	{
		INSTANCE = new QuestBreed();
		Quests.registerQuest(id, INSTANCE);
		MinecraftForge.EVENT_BUS.register(INSTANCE);
		ID = id;
	}
	
	public void onBreed(EntityPlayer player, EntityAnimal animal)
	{
		if ( player == null || animal == null )
		{
			return;
		}
		
		Province province = PlayerCivilizationCapabilityImpl.get(player).getInCivilization();

		if (province == null || province.civilization == null)
		{
			return;
		}

		handleBreed(player, province, animal);
	}

	private void handleBreed(EntityPlayer player, Province province, EntityAnimal animal)
	{
		if ( player == null || province == null || animal == null ) return;
		
		Set<QuestData> quests = PlayerCivilizationCapabilityImpl.get(player).getCurrentQuests();

		DataWrapper quest = new DataWrapper();
		for (QuestData data : quests)
		{
			try
			{
				quest.setData(data);
				quest.province = province;
				
				int animalType = 0;
				if ( animal instanceof EntityChicken ) animalType = 1;
				else if ( animal instanceof EntityPig ) animalType = 2;
				else if ( animal instanceof EntitySheep ) animalType = 3;
				else if ( animal instanceof EntityCow ) animalType = 4;
				
				if ( quest.getAnimalType() == 0 || quest.getAnimalType() == animalType )
				{
					if ( perform(quest) )
					{
						return;
					}
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
		if (!quest.getCompleted())
		{
			if ( quest.getChatStack() == "" )
			{
				quest.setChatStack( "You haven't bred enough animals!" );
				this.setData(quest);
			}
			// quest.getPlayer().closeScreen();
			return null;
		}

		Province province = loadProvince(quest.getPlayer().world, quest.getPlayer().getPosition());

		if ( province == null || province.id == null || !province.id.equals(quest.getProvinceId()) )
		{
			return null;
		}

		CivilizationHandlers.adjustPlayerRep(quest.getPlayer(), quest.getCiv(), getRewardRep(quest));

		// quest.setChatStack( "I knew I could count on you, " + quest.getPlayer().getName() + "." );
		this.setData(quest);
		
		List<ItemStack> rewards = getRewardItems(quest); // TODO
		if (rewards != null)
		{
			items.addAll(rewards);
		}
		
		return items;
	}
	Random rand = new Random();
	@Override
	public List<ItemStack> accept(QuestData data, List<ItemStack> in)
	{
		//if (data.getPlayer().world.isRemote)
		{
			switch ( i(data.getiData().get("animalType")) )
			{
				case 0:
				{
					if ( rand.nextBoolean() ) data.setChatStack("We had to cull some of our livestock in preparation for a great feast! I will need you to breed several animals to strengthen their numbers.");
					else 					  data.setChatStack("A terrible disease has wiped out much of our livestock. I will need you to breed several animals to strengthen their numbers.");
					break;
				}
				case 1:
				{
					if ( rand.nextBoolean() ) data.setChatStack("A racoon broke into the coop and killed most of our chickens, bloody mess it was! I will need you to breed more chickens because a lot of them died.");
					else 					  data.setChatStack("A §ofowl§r disease has wiped out most of our chickens. Get it? A §oFOWL§r disease? Haha... ha... anyways... I will need you to breed more chickens because most of them are dead.");
					break;
				}
				case 2:
				{
					data.setChatStack("As you know, the villagers love having bacon for breakfast. I'll need you to breed more pigs because we've been going through bacon like crazy.");
					break;
				}
				case 3:
				{
					data.setChatStack("Wolves wiped out a flock of sheep a few days ago. I'll need you to breed more sheep.");
					break;
				}
				case 4:
				{
					if ( rand.nextBoolean() ) data.setChatStack("The bandits in the last raid stole a herd of cattle from one of the cowherds. Help them out and breed a few cows.");
					else 					  data.setChatStack("I will need you to breed more cows to strengthen their numbers.");
					break;
				}
			}
		}
		this.setData(data);
		return in;
	}

	@Override
	public List<ItemStack> reject(QuestData data, List<ItemStack> in)
	{
		data.setChatStack( "Ah, I understand. Does causing animals to love eachother make you uncomfortable, then?" );
		this.setData(data);
		data.getPlayer().closeScreen();
		return in;
	}
	
	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	
	@Override
	public String getTitle(QuestData data)
	{
		return "quests.breed.title";
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
		String animalType = "livestock";
		switch ( i(data.getiData().get("animalType")) )
		{
			case 0:{animalType = "livestock";break;}
			case 1:{animalType = "chickens";break;}
			case 2:{animalType = "pigs";break;}
			case 3:{animalType = "sheep";break;}
			case 4:{animalType = "cows";break;}
			default:{animalType = "livestock";break;}
		}
		s.append("quests.breed.description");
		s.append("|").append(q.getTargetAmount());
		s.append("|").append(animalType);
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
		
		
		// Quest changes based on reputation
		int rep = PlayerCivilizationCapabilityImpl.get(player).getReputation(province.civilization);
		if ( rep < 0 )
		{
			rep = 0;
		}
		if ( rep < 200 )
		{
			q.setAnimalType(0);
		}
		else
		{
			q.setAnimalType(rand.nextInt(5));
			// 0 = any
			// 1 = chicken
			// 2 = pig
			// 3 = sheep
			// 4 = cow
		}
		// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
		
		int roll = rand.nextInt(5)*2+8+((Math.round(rep/200))*2);
		int em = Math.round(roll/2);
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
		
		public Integer getAnimalType()
		{
			return i(data.getiData().get("animalType"));
		}

		public void setAnimalType(Integer type)
		{
			data.getiData().put("animalType", type);
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
