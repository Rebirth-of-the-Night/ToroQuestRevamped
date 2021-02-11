package net.torocraft.toroquest.civilization.quests;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemLead;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;
import net.minecraftforge.common.MinecraftForge;
import net.torocraft.toroquest.block.BlockToroSpawner;
import net.torocraft.toroquest.block.TileEntityToroSpawner;
import net.torocraft.toroquest.civilization.CivilizationHandlers;
import net.torocraft.toroquest.civilization.Province;
import net.torocraft.toroquest.civilization.player.PlayerCivilizationCapability;
import net.torocraft.toroquest.civilization.player.PlayerCivilizationCapabilityImpl;
import net.torocraft.toroquest.civilization.quests.QuestCaptureFugitives.DataWrapper;
import net.torocraft.toroquest.civilization.quests.util.ItemMapCentered;
import net.torocraft.toroquest.civilization.quests.util.Quest;
import net.torocraft.toroquest.civilization.quests.util.QuestData;
import net.torocraft.toroquest.civilization.quests.util.Quests;

public class QuestCaptureEntity extends QuestBase implements Quest {
	public static int ID;
	public static QuestCaptureEntity INSTANCE;


	public static void init(int id) {
		INSTANCE = new QuestCaptureEntity();
		Quests.registerQuest(id, INSTANCE);
		MinecraftForge.EVENT_BUS.register(INSTANCE);
		ID = id;
	}


	@Override
	public List<ItemStack> complete(QuestData quest, List<ItemStack> items)
	{
		if (!quest.getCompleted())
		{
			if ( quest.getChatStack() == "" )
			{
				quest.setChatStack("You haven't returned the toro yet!");
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
	
	@Override
	public List<ItemStack> reject(QuestData data, List<ItemStack> in)
	{
		if ( in == null )
		{
			if ( data.getChatStack() == "" )
			{
				data.setChatStack( "Return the tool that was provided, or 5 emeralds to pay for it." );
				this.setData(data);
			}
			data.getPlayer().closeScreen();
			return null;
		}

		List<ItemStack> givenItems = copyItems(in);

		boolean toolIncluded = false;
		int emeraldRemainingCount = 5;

		for (ItemStack item : givenItems)
		{
			{
				if (item.getItem() instanceof ItemLead)
				{
					toolIncluded = true;
					item.shrink(1);
				}
			}
		}

		if (!toolIncluded)
		{
			for (ItemStack item : givenItems)
			{
				if (!item.isEmpty() && item.getItem() == Items.EMERALD && item.getCount() >= 5)
				{
					int decBy = Math.min(item.getCount(), emeraldRemainingCount);
					emeraldRemainingCount -= decBy;
					item.shrink(decBy);
				}
			}
		}

		if (!toolIncluded && emeraldRemainingCount > 0)
		{
			data.setChatStack( "Return the tool that was provided, or 5 emeralds to pay for it." );
			this.setData(data);
			data.getPlayer().closeScreen();
			return null;
		}
		data.setChatStack( "B-but... my beautiful toro! Oh, how I long for his company..." );
		this.setData(data);
		data.getPlayer().closeScreen();
		return givenItems;
	}

	@Override
	public List<ItemStack> accept(QuestData data, List<ItemStack> in)
	{
		try
		{
			BlockPos pos = searchForSuitableLocation(data, 320, 0);
			setSpawnPosition(data, pos);
			addToroSpawner(data, data.getPlayer().getEntityWorld(), getSpawnPosition(data), getDefaultEnemies(data));
			// if (data.getPlayer().world.isRemote)
			{	try
				{
					if ( getRewardItems(data).get(0).getCount() == 0 )
					{
						data.setChatStack("Bandits have stolen my prized toro! Bring him back, please — I will reward you!");
					}
					else
					{
						data.setChatStack( "Please, " + data.getPlayer().getName() + ". My prized toro has broken loose. Find and return him as soon as possible!");
					}
				}
				catch(Exception e)
				{
					
				}
			}
			this.setData(data);
			setLocationBasedRewards(data);
			ItemStack itemstack = new ItemStack(Items.LEAD, 1);
			itemstack.setStackDisplayName( "Lasso" );
			in.add(itemstack);
		}
		catch (Exception e)
		{
			System.out.println("error");
			reject(data,in);
		}
		return in;
	}
	
	private List<String> getDefaultEnemies(QuestData data)
	{
		List<String> entity = new ArrayList<String>();
		entity.add("toroquest:toroquest_quest_toro");
		try
		{
			if ( getRewardItems(data).get(0).getCount() == 0 )
			{
				entity.add("toroquest:toroquest_sentry");
				entity.add("toroquest:toroquest_sentry");
				entity.add("toroquest:toroquest_sentry");
			}
		}
		catch(Exception e)
		{
			
		}
		return entity;
	}
	
	private void setLocationBasedRewards(QuestData data)
	{
		int roll = 10 + (int)Math.round(((getSpawnPosition(data).getDistance((data.getPlayer().getPosition()).getX(), (data.getPlayer().getPosition()).getY(), (data.getPlayer().getPosition()).getZ()) / 64)));
		roll = MathHelper.clamp(roll, 8, 12);
		setRewardRep(data, roll*2);
		if ( PlayerCivilizationCapabilityImpl.get(data.getPlayer()).getReputation(data.getCiv()) >= 3000 )
		{
			roll *= 2;
		}
		List<ItemStack> reward = new ArrayList<ItemStack>(1);
		reward.add(new ItemStack(Items.EMERALD, roll));
		setRewardItems(data, reward);
	}

	public boolean onReturn(EntityPlayer player)
	{

		if (player == null)
		{
			return false;
		}

		Province province = PlayerCivilizationCapabilityImpl.get(player).getInCivilization();

		if (province == null || province.civilization == null)
		{
			return false;
		}

		return handleReturn(player, province);
	}

	private boolean handleReturn(EntityPlayer player, Province province)
	{
		Set<QuestData> quests = PlayerCivilizationCapabilityImpl.get(player).getCurrentQuests();

		DataWrapper quest = new DataWrapper();
		for (QuestData data : quests)
		{
			try
			{
				if ( data == null )
				{
					continue;
				}
				
				quest.setData(data);
				quest.province = province;
				
				return (perform(quest));
				
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return false;
	}

	private boolean perform(DataWrapper quest)
	{
		if (quest.getData().getPlayer().world.isRemote)
		{
			return false;
		}

//		if (!quest.isApplicable())
//		{
//			return false;
//		}

		if ( !quest.data.getCompleted() )
		{
			//quest.setCurrentAmount(quest.getCurrentAmount() + 1);
			//quest.data.getPlayer().sendStatusMessage( new TextComponentString(MathHelper.clamp(quest.getCurrentAmount(), 0, quest.getTargetAmount())+"/"+quest.getTargetAmount()), true);
			
			//if (quest.getCurrentAmount() >= quest.getTargetAmount())
			//{
				quest.data.setCompleted(true);
				quest.getData().setCompleted(true);
				chatCompletedQuest(quest.data);
			//}
			return true;
		}
		return false;
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

		q.setCurrentAmount(0);
		q.setTargetAmount(1);

		int roll = rand.nextInt(2);
		int em = roll;
		int rep = em*2;
		if ( PlayerCivilizationCapabilityImpl.get(player).getReputation(province.civilization) >= 3000 )
		{
			em *= 2;
		}

		ItemStack emeralds = new ItemStack(Items.EMERALD, em); // emerald reward
		List<ItemStack> rewardItems = new ArrayList<ItemStack>();
		rewardItems.add(emeralds);
		
		// temporary //
		q.setRewardRep(rep);
		setRewardItems(q.data, rewardItems);

		//setRewardRep(q.data, getRewardRep(q.data));
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
	
	private void addToroSpawner(QuestData data, World world, BlockPos blockpos, List<String> entities)
	{
		world.setBlockState(blockpos, BlockToroSpawner.INSTANCE.getDefaultState());
		TileEntity tileentity = world.getTileEntity(blockpos);
		if (tileentity instanceof TileEntityToroSpawner)
		{
			TileEntityToroSpawner spawner = (TileEntityToroSpawner) tileentity;
			spawner.setTriggerDistance(64);
			spawner.setEntityIds(entities);
			spawner.setSpawnRadius(16);
			spawner.addEntityTag(data.getQuestId().toString());
			spawner.addEntityTag("capture_quest");
		}
		else
		{
			System.out.println("tile entity is missing");
		}
	}

	@Override
	public String getTitle(QuestData data)
	{
		if (data == null)
		{
			return "";
		}
		return "quests.capture_entity.title";
	}

	@Override
	public String getDescription(QuestData data)
	{
		if (data == null)
		{
			return "";
		}
		
		StringBuilder s = new StringBuilder();
		
		if ( getSpawnPosition(data) == null )
		{
			s.append("quests.capture_entity.description_null");
		}
		else
		{
			s.append("quests.capture_entity.description");
			s.append("|").append( "at §lLocation:§r [" + getDirections(getProvincePosition(getQuestProvince(data)), getSpawnPosition(data)) + "]" );
			s.append("|").append("\n\n");
			s.append("|").append(listItems(getRewardItems(data)) + ",\n" );
			s.append("|").append(getRewardRep(data));
		}
		
		return s.toString();
	}
}
