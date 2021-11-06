package net.torocraft.toroquest.civilization.quests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.server.command.TextComponentHelper;
import net.torocraft.toroquest.civilization.CivilizationHandlers;
import net.torocraft.toroquest.civilization.CivilizationType;
import net.torocraft.toroquest.civilization.Province;
import net.torocraft.toroquest.civilization.player.PlayerCivilizationCapabilityImpl;
import net.torocraft.toroquest.civilization.quests.util.Quest;
import net.torocraft.toroquest.civilization.quests.util.QuestData;
import net.torocraft.toroquest.civilization.quests.util.Quests;
import net.torocraft.toroquest.config.ToroQuestConfiguration;
import net.torocraft.toroquest.config.ToroQuestConfiguration.KillMob;

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
		DamageSource source = event.getSource();

		if ( source == null || source.getTrueSource() == null )
		{
			return;
		}
		
		EntityPlayer player = null;
		
		if ( source.getTrueSource() instanceof EntityPlayer )
		{
			player = (EntityPlayer) source.getTrueSource();
		}
		else
		{
			return;
		}
		
		if ( !(event.getEntity() instanceof EntityLivingBase) )
		{
			return;
		}
		
		EntityLivingBase victim = (EntityLivingBase) event.getEntity();
		
		Set<QuestData> quests = PlayerCivilizationCapabilityImpl.get(player).getCurrentQuests();

		DataWrapper quest = new DataWrapper();
		
		for (QuestData data : quests)
		{
			try
			{				
				quest.setData(data);
				
				if ( data.getsData().get("mobString") == null )
				{
					if ( victim instanceof EntityMob || victim instanceof IMob )
					{
						perform(quest);
					}
				}
				else
				{
					String km = data.getsData().get("mobString");
					
					if ( victim.getClass().getSimpleName().equals(km) )
					{
						perform(quest);
					}
					else if ( victim.getClass().getSuperclass().getSimpleName().equals(km) )
					{
						perform(quest);
					}
					else if ( victim.getClass().getSuperclass().getSuperclass().getSimpleName().equals(km) )
					{
						perform(quest);
					}
				}
			}
			catch (Exception e)
			{
				
			}
		}
	}

	private boolean perform(DataWrapper quest)
	{
		if (quest.getData().getPlayer().world.isRemote)
		{
			return false;
		}

		if ( !(quest.data.getQuestType() == ID) )
		{
			return false;
		}
		
		quest.setCurrentAmount(quest.getCurrentAmount()+1);
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
		Province province = loadProvince(quest.getPlayer().world, quest.getPlayer().getPosition());

		if ( province == null || province.id == null || !province.id.equals(quest.getProvinceId()) )
		{
			return null;
		}
		
		if ( !quest.getCompleted() )
		{
			if ( quest.getChatStack().equals("") )
			{
				String s = TextComponentHelper.createComponentTranslation(quest.getPlayer(), "quests.killmobs.monsters", new Object[0]).getFormattedText();
				if ( quest.getsData().get("mobString") != null )
				{
					s = quest.getsData().get("mobDisplay")+"s";
				}
				quest.setChatStack("killmobs.incomplete", quest.getPlayer(), s);
				this.setData(quest);
			}
			return null;
		}

		List<ItemStack> rewards = getRewardItems(quest);
		
		if ( rewards != null )
		{
			items.addAll(rewards);
		}
		
		String s = TextComponentHelper.createComponentTranslation(quest.getPlayer(), "quests.killmobs.monsters", new Object[0]).getFormattedText();
		
		if ( quest.getsData().get("mobString") != null )
		{
			s = quest.getsData().get("mobDisplay")+"s";
		}
		
		quest.setChatStack("killmobs.complete", quest.getPlayer(), s);
		
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
		
		this.setData(quest);
		return items;
	}

	@Override
	public List<ItemStack> reject(QuestData data, List<ItemStack> in)
	{
		if ( data.getCompleted() )
		{
			return null;
		}
		
		if ( data.getsData().get("mobString") == null )
		{
			data.setChatStack("killmobs.reject", data.getPlayer(), TextComponentHelper.createComponentTranslation(data.getPlayer(), "quests.killmobs.monsters", new Object[0]).getFormattedText());
		}
		else
		{
			data.setChatStack("killmobs.reject", data.getPlayer(), data.getsData().get("mobDisplay")+"s");
		}
		data.getPlayer().closeScreen();
		this.setData(data);
		return in;
	}

	@Override
	public List<ItemStack> accept(QuestData data, List<ItemStack> in)
	{
		if ( data.getsData().get("mobString") == null )
		{
			data.setChatStack("killmobs.accept", data.getPlayer(), null);
		}
		else
		{
			data.setChatStackString(data.getsData().get("mobAccept"), data.getPlayer(), data.getsData().get("mobString"));
		}
		this.setData(data);
		return in;
	}

	@Override
	public String getTitle(QuestData data)
	{
		if ( data.getsData().get("mobString") == null )
		{
			return "quests.killmobs.title";
		}
		else
		{
			return data.getsData().get("mobTitle");
		}
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
		s.append("quests.killmobs.description");
		s.append("|").append(q.getTargetAmount());
		
		if ( data.getsData().get("mobString") == null )
		{
			s.append("|").append("monsters");
		}
		else
		{
			String km = data.getsData().get("mobDisplay");
			if ( q.getTargetAmount() == 1 )
			{
				s.append("|").append(km);
			}
			else
			{
				s.append("|").append(km+"s");
			}	
		}

		s.append("|").append(q.getCurrentAmount() + "\n\n" );
		s.append("|").append(listKillMobItems(getRewardItems(q.data)) + ",\n" );
		s.append("|").append(getRewardRep(data));
		
		return s.toString();
	}

//	private String mobName()
//	{
//		Entity mob = EntityList.createEntityByIDFromName(  new ResourceLocation(  ToroQuestConfiguration.mobs.get( rand.nextInt(ToroQuestConfiguration.mobs.size()) ).mobName  ), player.world ); // MOB_TYPES[mobType]
//		System.out.println(mob); // XXX
//		mob.getClass();
//		return mob.getName();
//	Entity mob = EntityList.createEntityByIDFromName( new ResourceLocation(), player.world ); // MOB_TYPES[mobType]
//	System.out.println(mob); // XXX
//	mob.getClass();
//	return mob.getName();
	
//	Entity mob = EntityList.createEntityByIDFromName(  new ResourceLocation(  ToroQuestConfiguration.mobs.get( rand.nextInt(ToroQuestConfiguration.mobs.size()) ).mobName  ), player.world   ); // MOB_TYPES[mobType]
	
	//else if ( quest.getHuntedMob() != victim.toString() )
	//e.printStackTrace();
	// quest.huntedMob = EntityList.getKey(victim).getResourcePath(); EntityZombie > minecraft:zombie
	//quest.provinceHuntedIn = provinceHuntedIn;
	
	//
	// q.setHuntedMob("");
	// q.setMobType(rand.nextInt(MOB_TYPES.size()));
	// EntityList.getClassFromName("");
	//
	
	// EntitySkeleton,5,8,0.5,kill x beasts
	
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
		
		int roll = rand.nextInt(8)*4+12;
		int em = (int)Math.round((double)roll/3)+4;
		
		q.setMobString(null);
		q.setMobDisplay(null);
		q.setMobAccept(null);
		q.setMobTitle(null);
		
		int custom = ToroQuestConfiguration.mobs.isEmpty() ? -1 : rand.nextInt(ToroQuestConfiguration.mobs.size());
		
		if ( custom >= 0 )
		{
			KillMob km = ToroQuestConfiguration.mobs.get(custom);
			
			if ( km.minRepRequired <= PlayerCivilizationCapabilityImpl.get(player).getReputation(province.civilization) && ( km.provinceAllowed.equals("x") || km.provinceAllowed.equals(CivilizationType.biomeName(province.toString())) ) )
			{
				roll = km.minKills+rand.nextInt(1+km.maxKills-km.minKills);
				if ( roll % 2 == 1 && roll != km.maxKills && roll != km.minKills )
				{
					roll++;
				}
				em = (int)Math.round(km.emeraldsPerKill*roll);
				q.setMobString(km.mobName);
				q.setMobDisplay(km.mobDisplayName);
				q.setMobAccept(km.acceptChat);
				q.setMobTitle(km.title);
			}
			else
			{
				ArrayList<KillMob> list = new ArrayList<KillMob>(ToroQuestConfiguration.mobs);
				Collections.shuffle(list);
								
				for ( KillMob c : list )
				{
					if ( c.minRepRequired <= PlayerCivilizationCapabilityImpl.get(player).getReputation(province.civilization) && ( c.provinceAllowed.equals("x") || c.provinceAllowed.equals(CivilizationType.biomeName(province.toString())) ) )
					{
						roll = c.minKills+rand.nextInt(1+c.maxKills-c.minKills);
						if ( roll % 2 == 1 && roll != c.maxKills && roll != c.minKills )
						{
							roll++;
						}
						em = (int)Math.round(c.emeraldsPerKill*roll);
						q.setMobString(c.mobName);
						q.setMobDisplay(c.mobDisplayName);
						q.setMobAccept(c.acceptChat);
						q.setMobTitle(c.title);
						break;
					}
				}
			}
		}
		
		q.setCurrentAmount(0);
		q.setRewardRep(em*2);
		if ( PlayerCivilizationCapabilityImpl.get(player).getReputation(province.civilization) >= 2000 )
		{
			em *= 2;
		}
		q.setTargetAmount(roll);
		
		if ( em > 64 )
		{
			em = em/9;
			List<ItemStack> rewardItems = new ArrayList<ItemStack>(1);
			rewardItems.add(new ItemStack(Blocks.EMERALD_BLOCK, em));
			setRewardItems(q.data, rewardItems);
		}
		else
		{
			ItemStack emeralds = new ItemStack(Items.EMERALD, em); // emerald reward
			List<ItemStack> rewardItems = new ArrayList<ItemStack>();
			rewardItems.add(emeralds);
			setRewardItems(q.data, rewardItems);
		}
		this.setData(q.data);
		return q.data;
	}

	public static class DataWrapper
	{
		private QuestData data = new QuestData();
		// private Province provinceHuntedIn;
		// private String huntedMob = null;
		// private boolean custom;

		public QuestData getData()
		{
			return data;
		}

		public DataWrapper setData(QuestData data)
		{
			this.data = data;
			return this;
		}

//		public Province getProvinceHuntedIn()
//		{
//			return provinceHuntedIn;
//		}
//
//		public void setProvinceHuntedIn(Province provinceHuntedIn)
//		{
//			this.provinceHuntedIn = provinceHuntedIn;
//		}

//		public boolean getCustom()
//		{
//			return custom;
//		}
//		
//		public void setCustom( boolean b )
//		{
//			this.custom = b;
//		}
		
//		public String getHuntedMob()
//		{
//			return this.huntedMob;
//		}
//		
//		public void setHuntedMob( @Nullable String m ) 
//		{
//			this.huntedMob = m;
//		}
		
//		public KillMob killMobData()
//		{
//			return i(data.getiData().get("type"));
//		}

		// mobAccept
		
		public String getMobAccept()
		{
			return data.getsData().get("mobAccept");
		}

		public void setMobAccept(String c)
		{
			data.getsData().put("mobAccept", c);
		}
		
		public String getMobTitle()
		{
			return data.getsData().get("mobTitle");
		}

		public void setMobTitle(String c)
		{
			data.getsData().put("mobTitle", c);
		}
		
		public String getMobString()
		{
			return data.getsData().get("mobString");
		}

		public void setMobString(String c)
		{
			data.getsData().put("mobString", c);
		}
		
		public String getMobDisplay()
		{
			return data.getsData().get("mobDisplay");
		}

		public void setMobDisplay(String c)
		{
			data.getsData().put("mobDisplay", c);
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

//		private boolean isInCorrectProvince()
//		{
//			return data.getProvinceId().equals(getProvinceHuntedIn().id);
//		}

	}
}
