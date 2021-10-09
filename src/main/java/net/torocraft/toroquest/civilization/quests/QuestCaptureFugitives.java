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
import net.minecraft.item.ItemLead;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.torocraft.toroquest.block.BlockToroSpawner;
import net.torocraft.toroquest.block.TileEntityToroSpawner;
import net.torocraft.toroquest.civilization.CivilizationHandlers;
import net.torocraft.toroquest.civilization.CivilizationUtil;
import net.torocraft.toroquest.civilization.Province;
import net.torocraft.toroquest.civilization.player.PlayerCivilizationCapabilityImpl;
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
		Province province = loadProvince(quest.getPlayer().world, quest.getPlayer().getPosition());

		if (province == null || province.id == null || !province.id.equals(quest.getProvinceId()))
		{
			return null;
		}
		
		if (!quest.getCompleted())
		{
			if ( quest.getChatStack() == "" )
			{
				quest.setChatStack( "capture_fugitives.incomplete", quest.getPlayer(), quest.getCiv().getDisplayName(data.getPlayer()));
				this.setData(quest);
			}
			// quest.getPlayer().closeScreen();
			return null;
		}

		//PlayerCivilizationCapability playerCiv = PlayerCivilizationCapabilityImpl.get(quest.getPlayer());

		//int amount = new DataWrapper().setData(quest).getRewardRep();
		//CivilizationHandlers.adjustPlayerRep(quest.getPlayer(), playerCiv.getInCivilization().civilization, amount);

		//playerCiv.adjustReputation(quest.getCiv(), new DataWrapper().setData(quest).getRewardRep());

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
		
		quest.setChatStack( "capture_fugitives.complete", quest.getPlayer(), quest.getCiv().getDisplayName(data.getPlayer()));
		this.setData(quest);
		return items;
	}

	@Override
	public List<ItemStack> reject(QuestData quest, List<ItemStack> items)
	{
		if ( quest.getCompleted() )
		{
			return null;
		}

		boolean toolIncluded = false;
		int emeraldRemainingCount = 5;

		for (ItemStack item : items)
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
			for (ItemStack item : items)
			{
				if (!item.isEmpty() && item.getItem() == Items.EMERALD && item.getCount() >= 5)
				{
					int decBy = Math.min(item.getCount(), emeraldRemainingCount);
					emeraldRemainingCount -= decBy;
					item.shrink(decBy);
				}
			}
		}

		if ( !toolIncluded && emeraldRemainingCount > 0 )
		{
			quest.setChatStack( "rejectreturnitem", quest.getPlayer(), quest.getCiv().getDisplayName(data.getPlayer()));
			this.setData(quest);
			return null;
		}
		
		quest.setChatStack( "capture_fugitives.reject", quest.getPlayer(), quest.getCiv().getDisplayName(data.getPlayer()));
		this.setData(quest);
		quest.getPlayer().closeScreen();
		return items;
	}
	
	@Override
	public List<ItemStack> accept(QuestData data, List<ItemStack> in)
	{
		try
		{
			BlockPos pos = searchForSuitableLocation(data, 88, 0);
			setSpawnPosition(data, pos);
			addToroSpawner(data, data.getPlayer().getEntityWorld(), getSpawnPosition(data), getDefaultEnemies(data));
		}
		catch (Exception e)
		{
			
		}
		ItemStack itemstack = new ItemStack(Items.LEAD, 1);
		itemstack.setStackDisplayName( I18n.format("item.fugitive_bindings.name") );
		in.add(itemstack);
		data.setChatStack( "capture_fugitives.accept", data.getPlayer(), data.getCiv().getDisplayName(data.getPlayer()));
		this.setData(data);
		return in;
	}
	
	public BlockPos searchForSuitableLocation( QuestData data, int range, int occupiedRange )
	{
		BlockPos pos = null;
		for (int i = 0; i < 100; i++)
		{
			pos = randomLocation(data, rand, range, false, occupiedRange);
			if (pos != null)
			{
				break;
			}
		}
		if (pos == null)
		{
			for (int i = 0; i < 10; i++)
			{
				pos = randomLocation(data, rand, range*2, true, occupiedRange);
				if (pos != null)
				{
					break;
				}
			}
		}
		return pos;
	}

	public BlockPos randomLocation(QuestData data, Random random, int range, boolean force, int occupiedRange)
	{
		Province province = getQuestProvince(data);
		EntityPlayer player = data.getPlayer();

		if ( province == null || player == null )
		{
			return null;
		}
		
		int x = (random.nextInt(range));
		int z = (random.nextInt(range));
		
		while ( x + z < range/2 )
		{
			x = (random.nextInt(range));
			z = (random.nextInt(range));
		}
		
		x *= (random.nextInt(2)*2-1);
		z *= (random.nextInt(2)*2-1);
		
		x += province.getCenterX();
		z += province.getCenterZ();
		
		BlockPos pos = findSurface(player.world, x, z, force);

		if ( force )
		{
			return pos;
		}
		
		if (pos == null)
		{
			return null;
		}
		
	    province = CivilizationUtil.getProvinceAt(player.world, x/16, z/16);
	
		if ( province == null)
		{
			return null;
		}
		
		if (data.getPlayer().world.isAnyPlayerWithinRangeAt((double)pos.getX(),(double)pos.getY(),(double)pos.getZ(),16))
		{
			return null;
		}

		return pos;
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
		
		int roll = rand.nextInt(3)+2;
		int em = roll*4+4;
		q.setRewardRep(em*2);
		if ( PlayerCivilizationCapabilityImpl.get(player).getReputation(province.civilization) >= 2000 )
		{
			em *= 2;
		}
		q.setCurrentAmount(0);
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
