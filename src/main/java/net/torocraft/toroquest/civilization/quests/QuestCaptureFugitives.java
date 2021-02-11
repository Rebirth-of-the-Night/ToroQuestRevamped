package net.torocraft.toroquest.civilization.quests;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemLead;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.village.Village;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;
import net.minecraftforge.client.event.GuiContainerEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.ItemHandlerHelper;
import net.torocraft.toroquest.block.BlockToroSpawner;
import net.torocraft.toroquest.block.TileEntityToroSpawner;
import net.torocraft.toroquest.civilization.CivilizationEvent;
import net.torocraft.toroquest.civilization.CivilizationHandlers;
import net.torocraft.toroquest.civilization.CivilizationUtil;
import net.torocraft.toroquest.civilization.Province;
import net.torocraft.toroquest.civilization.player.PlayerCivilizationCapability;
import net.torocraft.toroquest.civilization.player.PlayerCivilizationCapabilityImpl;
import net.torocraft.toroquest.civilization.quests.QuestCaptureEntity.DataWrapper;
import net.torocraft.toroquest.civilization.quests.util.ItemMapCentered;
import net.torocraft.toroquest.civilization.quests.util.Quest;
import net.torocraft.toroquest.civilization.quests.util.QuestData;
import net.torocraft.toroquest.civilization.quests.util.Quests;

public class QuestCaptureFugitives extends QuestBase implements Quest
{

	public static QuestCaptureFugitives INSTANCE;

	public static int ID;

	public static void init(int id)
	{
		INSTANCE = new QuestCaptureFugitives();
		Quests.registerQuest(id, INSTANCE);
		MinecraftForge.EVENT_BUS.register(INSTANCE);
		ID = id;
	}

	public void onReturn(EntityPlayer player)
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

		handleReturn(player, province);
	}

	private void handleReturn(EntityPlayer player, Province province)
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
			
			if ( quest.getCurrentAmount() >= quest.getTargetAmount() )
			{
				quest.data.setCompleted(true);
				chatCompletedQuest(quest.data);
			}
			return true;
		}
		return false;
	}

	@Override
	public List<ItemStack> complete(QuestData quest, List<ItemStack> items)
	{
		if (!quest.getCompleted())
		{
			if ( quest.getChatStack() == "" )
			{
				quest.setChatStack( "You haven't captured all the fugitives yet!" );
				this.setData(quest);
			}
			// quest.getPlayer().closeScreen();
			return null;
		}

		Province province = loadProvince(quest.getPlayer().world, quest.getPlayer().getPosition());

		if (province == null || province.id == null || !province.id.equals(quest.getProvinceId())) {
			return null;
		}

		//PlayerCivilizationCapability playerCiv = PlayerCivilizationCapabilityImpl.get(quest.getPlayer());

		//int amount = new DataWrapper().setData(quest).getRewardRep();
		//CivilizationHandlers.adjustPlayerRep(quest.getPlayer(), playerCiv.getInCivilization().civilization, amount);

		//playerCiv.adjustReputation(quest.getCiv(), new DataWrapper().setData(quest).getRewardRep());

		CivilizationHandlers.adjustPlayerRep(quest.getPlayer(), quest.getCiv(), getRewardRep(quest));

		// data.setChatStack( "I knew I could count on you, " + data.getPlayer().getName() + "." );
		this.setData(quest);
		
		List<ItemStack> rewards = getRewardItems(quest);
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
				data.getPlayer().closeScreen();
			}
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
			// data.getPlayer().closeScreen();
			return null;
		}
		data.setCompleted(false);
		data.setChatStack( "Very well. Crime will continue to run rampant in our province, then." );
		this.setData(data);
		data.getPlayer().closeScreen();
		return givenItems;
	}
	
	@Override
	public List<ItemStack> accept(QuestData data, List<ItemStack> in)
	{
		try
		{
			BlockPos pos = searchForSuitableLocation(data, 96, 0);
			setSpawnPosition(data, pos);
			addToroSpawner(data, data.getPlayer().getEntityWorld(), getSpawnPosition(data), getDefaultEnemies(data));

			//spawnFugitives( data.getPlayer().world, data.getPlayer(), getSpawnPosition(data) );
			
			ItemStack itemstack = new ItemStack(Items.LEAD, 1);
			itemstack.setStackDisplayName( "Lasso" );
			in.add(itemstack);
						
			//if (!data.getPlayer().world.isRemote)
			{
				switch (data.getPlayer().world.rand.nextInt(7))
				{
					case 0:
					{
						data.setChatStack( "Capture the band of fugitives dwelling amongst us, and seize them to my guards." );
						break;
					}
					case 1:
					{
						data.setChatStack( "Hunt down the band of fugitives dwelling amongst us, and bring them to justice!" );
						break;
					}
					case 2:
					{
						data.setChatStack( "Find and bring the band of fugitives to justice, here in " + getProvinceName(data.getPlayer(), data.getProvinceId() ) + "." );                                                                  
						break;
					}
					case 3:
					{
						data.setChatStack( "Investigate the streets of " + getProvinceName(data.getPlayer(), data.getProvinceId()) + " and sieze the band of fugitives." );
						break;
					}
					case 4:
					{
						data.setChatStack( "Several fugitives have managed to break free from their dungeon cells. Find and turn them over to my guards." );
						break;
					}
					case 5:
					{
						data.setChatStack( "Crime is running rampant on my streets... show the citizens of " + getProvinceName(data.getPlayer(), data.getProvinceId()) + " what happens to those who break the law!" );
						break;
					}
					case 6:
					{
						data.setChatStack( "My guards have failed to keep crime under control. Do them a service, " + data.getPlayer().getName() + " and show them how it's done." );
						break;
					}
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("ERROR: " + e + " - Capture fugitives accept method" );
			reject(data,in);
		}
		this.setData(data);
		return in;
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
			spawner.addEntityTag("capture_fugitives");
		}
		else
		{
			System.out.println("tile entity is missing");
		}
	}

	private List<String> getDefaultEnemies(QuestData data)
	{
		List<String> entity = new ArrayList<String>();
		
		for ( int i = data.getiData().get("target") + 1; i > 0; i-- )
		{
			entity.add("toroquest:toroquest_fugitive");
		}
		return entity;
	}
	
	@Override
	public String getTitle(QuestData data)
	{
		return "quests.capture_fugitives.title";
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
		s.append("quests.capture_fugitives.description");
		s.append("|").append(q.getTargetAmount());
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
		
		int roll = 2+rand.nextInt(3);
 
		setRewardRep(q.data, roll*2);
		int em = roll;
		if ( PlayerCivilizationCapabilityImpl.get(player).getReputation(province.civilization) >= 3000 )
		{
			em *= 2;
		}
		q.setCurrentAmount(0);
		q.setRewardRep(em);
		q.setTargetAmount(roll);

		ItemStack emeralds = new ItemStack(Items.EMERALD, em); // emerald reward
		List<ItemStack> rewardItems = new ArrayList<ItemStack>();
		rewardItems.add(emeralds);
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
	
}
