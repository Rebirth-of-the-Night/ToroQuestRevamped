package net.torocraft.toroquest.civilization.quests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemLead;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.Village;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.server.command.TextComponentHelper;
import net.torocraft.toroquest.block.BlockToroSpawner;
import net.torocraft.toroquest.block.TileEntityToroSpawner;
import net.torocraft.toroquest.civilization.CivilizationHandlers;
import net.torocraft.toroquest.civilization.CivilizationUtil;
import net.torocraft.toroquest.civilization.Province;
import net.torocraft.toroquest.civilization.player.PlayerCivilizationCapabilityImpl;
import net.torocraft.toroquest.civilization.quests.util.Quest;
import net.torocraft.toroquest.civilization.quests.util.QuestData;
import net.torocraft.toroquest.civilization.quests.util.Quests;
import net.torocraft.toroquest.entities.EntityFugitive;

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
		
		Province province = loadProvince(quest.getPlayer().world, quest.getPlayer().getPosition());

		if (province == null || province.id == null || !province.id.equals(quest.getProvinceId()))
		{
			return null;
		}
		
		if (!quest.getCompleted())
		{
			if ( quest.getChatStack() == "" )
			{
				quest.setChatStack( "capture_entity.incomplete", quest.getPlayer(), null );
				this.setData(quest);
			}
			// quest.getPlayer().closeScreen();
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
		
		quest.setChatStack( "capture_entity.complete", quest.getPlayer(), null );

		List<ItemStack> rewards = getRewardItems(quest);
		
		if (rewards != null)
		{
			items.addAll(rewards);
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
		
		if ( in == null )
		{
			if ( data.getChatStack() == "" )
			{
				data.setChatStack( "rejectreturnitem", data.getPlayer(), null);
				this.setData(data);
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
			data.setChatStack( "rejectreturnitem", data.getPlayer(), null);
			this.setData(data);
			//data.getPlayer().closeScreen();
			return null;
		}
		
		data.setChatStack( "capture_entity.reject", data.getPlayer(), null);
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
			Boolean spawnBandits = rand.nextBoolean();
			addToroSpawner(data, data.getPlayer().getEntityWorld(), getSpawnPosition(data), getDefaultEnemies(data, spawnBandits));
			// if (data.getPlayer().world.isRemote)
			{	try
				{
					if ( spawnBandits )
					{
						data.setChatStack( "capture_entity.acceptbandits", data.getPlayer(), null);
					}
					else
					{
						data.setChatStack( "capture_entity.acceptnobandits", data.getPlayer(), null);
					}
				}
				catch(Exception e)
				{
					
				}
			}
			this.setData(data);
			ItemStack itemstack = new ItemStack(Items.LEAD, 1);
			itemstack.setStackDisplayName(TextComponentHelper.createComponentTranslation(data.getPlayer(), "item.sheep_bindings.name", new Object[0]).getFormattedText() + "§r");
			in.add(itemstack);
		}
		catch (Exception e)
		{
			reject(data,in);
		}
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
		//random = new Random( rand.nextInt() + data.hashCode() + data.getQuestId().hashCode() );

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
		
		Village village = player.world.getVillageCollection().getNearestVillage(pos, 16);

		if ( village != null )
		{
			return null;
		}
		
	    province = CivilizationUtil.getProvinceAt(player.world, x/16, z/16);
	    
		if ( province == null ) {province = CivilizationUtil.getProvinceAt(player.world, x/16+1, z/16+1);}
		if ( province == null ) {province = CivilizationUtil.getProvinceAt(player.world, x/16+1, z/16-1);}
		if ( province == null ) {province = CivilizationUtil.getProvinceAt(player.world, x/16-1, z/16+1);}
		if ( province == null ) {province = CivilizationUtil.getProvinceAt(player.world, x/16-1, z/16-1);}
	
		if ( province != null)
		{
			return null;
		}
		
		if (data.getPlayer().world.isAnyPlayerWithinRangeAt((double)pos.getX(),(double)pos.getY(),(double)pos.getZ(),32))
		{
			return null;
		}

		return pos;
	}
	
	private List<String> getDefaultEnemies(QuestData data, Boolean spawnBandits)
	{
		List<String> entity = new ArrayList<String>();
		entity.add("minecraft:sheep");
		try
		{
			if ( spawnBandits )
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

		if ( !quest.getData().getCompleted() )
		{
			//quest.setCurrentAmount(quest.getCurrentAmount() + 1);
			//quest.getData().getPlayer().sendStatusMessage( new TextComponentString(MathHelper.clamp(quest.getCurrentAmount(), 0, quest.getTargetAmount())+"/"+quest.getTargetAmount()), true);
			
			//if (quest.getCurrentAmount() >= quest.getTargetAmount())
			//{
				quest.getData().setCompleted(true);
				quest.getData().setCompleted(true);
				chatCompletedQuest(quest.getData());
			//}
			return true;
		}
		return false;
	}
	

	@Override
	public QuestData generateQuestFor(EntityPlayer player, Province province)
	{
		DataWrapper q = new DataWrapper();
		q.getData().setCiv(province.civilization);
		q.getData().setPlayer(player);
		q.getData().setProvinceId(province.id);
		q.getData().setQuestId(UUID.randomUUID());
		q.getData().setQuestType(ID);
		q.getData().setCompleted(false);

		q.setCurrentAmount(0);
		q.setTargetAmount(1);

		int em = 22;
		q.setRewardRep(em*2);
		if ( PlayerCivilizationCapabilityImpl.get(player).getReputation(province.civilization) >= 2000 )
		{
			em *= 2;
		}
		q.setCurrentAmount(0);
		ItemStack emeralds = new ItemStack(Items.EMERALD, em); // emerald reward
		List<ItemStack> rewardItems = new ArrayList<ItemStack>();
		rewardItems.add(emeralds);
		setRewardItems(q.getData(), rewardItems);
		//setRewardRep(q.getData(), getRewardRep(q.getData()));
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

//		private boolean isApplicable()
//		{
//			return isCorrectQuest() && isInCorrectProvince();
//		}
//
//		private boolean isCorrectQuest()
//		{
//			return data.getQuestType() == ID;
//		}
//
//		private boolean isInCorrectProvince()
//		{
//			return data.getProvinceId().equals(getProvinceHuntedIn().id);
//		}

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
			int color = 0;
			switch ( data.getCiv() )
			{
				case FIRE:
				{
					color = 1;
					break;
				}
				case EARTH:
				{
					color = 2;
					break;
				}
				case WIND:
				{
					color = 3;
					break;
				}
				case WATER:
				{
					color = 4;
					break;
				}
				case MOON:
				{
					color = 5;
					break;
				}
				case SUN:
				{
					color = 6;
					break;
				}
				default:
				{
					color = 0;
					break;
				}
			}
			spawner.setColor(color);
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
