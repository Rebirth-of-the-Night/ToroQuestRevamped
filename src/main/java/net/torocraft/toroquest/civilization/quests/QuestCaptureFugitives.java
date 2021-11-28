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
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.server.command.TextComponentHelper;
import net.torocraft.toroquest.civilization.CivilizationHandlers;
import net.torocraft.toroquest.civilization.CivilizationUtil;
import net.torocraft.toroquest.civilization.Province;
import net.torocraft.toroquest.civilization.player.PlayerCivilizationCapabilityImpl;
import net.torocraft.toroquest.civilization.quests.util.Quest;
import net.torocraft.toroquest.civilization.quests.util.QuestData;
import net.torocraft.toroquest.civilization.quests.util.Quests;
import net.torocraft.toroquest.entities.EntityFugitive;

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

		if ( !quest.getData().getCompleted() )
		{
			quest.setCurrentAmount(quest.getCurrentAmount() + 1);
			quest.getData().getPlayer().sendStatusMessage( new TextComponentString(MathHelper.clamp(quest.getCurrentAmount(), 0, quest.getTargetAmount())+"/"+quest.getTargetAmount()), true);
			
			if ( quest.getCurrentAmount() >= quest.getTargetAmount() )
			{
				quest.getData().setCompleted(true);
				chatCompletedQuest(quest.getData());
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
				quest.setChatStack( "capture_fugitives.incomplete", quest.getPlayer(), quest.getCiv().getDisplayName(quest.getPlayer()));
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
		
		List<ItemStack> rewards = getRewardItems(quest);
		
		if (rewards != null)
		{
			items.addAll(rewards);
		}
		
		quest.setChatStack( "capture_fugitives.complete", quest.getPlayer(), quest.getCiv().getDisplayName(quest.getPlayer()));
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
			quest.setChatStack( "rejectreturnitem", quest.getPlayer(), quest.getCiv().getDisplayName(quest.getPlayer()));
			this.setData(quest);
			return null;
		}
		
		quest.setChatStack( "capture_fugitives.reject", quest.getPlayer(), quest.getCiv().getDisplayName(quest.getPlayer()));
		this.setData(quest);
		quest.getPlayer().closeScreen();
		return items;
	}
	
	@Override
	public List<ItemStack> accept(QuestData data, List<ItemStack> in)
	{
//		try
//		{
//			BlockPos pos = searchForSuitableLocation(data);
//			//addToroSpawner(data, data.getPlayer().getEntityWorld(), pos, getDefaultEnemies(data));
//		}
//		catch (Exception e)
//		{
//			
//		}
		this.spawnFugitives(data.getPlayer().getEntityWorld(), data.getPlayer());
		
		ItemStack itemstack = new ItemStack(Items.LEAD, 1);
		itemstack.setStackDisplayName(TextComponentHelper.createComponentTranslation(data.getPlayer(), "item.fugitive_bindings.name", new Object[0]).getFormattedText() + "§r");
		in.add(itemstack);
		data.setChatStack( "capture_fugitives.accept", data.getPlayer(), data.getCiv().getDisplayName(data.getPlayer()));
		this.setData(data);
		return in;
	}
	

	private void spawnFugitives(World world, EntityPlayer player)
	{	
		if ( world.isRemote )
		{
			return;
		}
		
		try
		{
			int tries = 3;
			while ( tries > 0 )
			{
				tries--;
				
				{
					if ( world.provider.getDimension() != 0 )
					{
						continue;
					}
					
					Province province = CivilizationUtil.getProvinceAt(world, player.chunkCoordX, player.chunkCoordZ);
										
					if ( province == null )
					{
						continue;
					}
					
					int villageCenterX = province.getCenterX();
					int villageCenterZ = province.getCenterZ();
					
					double angle = rand.nextDouble()*Math.PI*2.0D;

					int range = 20+rand.nextInt(32);

					int x = (int) (Math.cos(angle)*range);
					int z = (int) (Math.sin(angle)*range);
					
					x += villageCenterX;
					z += villageCenterZ;
					
					BlockPos loc = new BlockPos(x,CivilizationHandlers.MAX_SPAWN_HEIGHT,z);
					BlockPos spawnPos = CivilizationHandlers.findSpawnLocationFrom(world, loc);
					
					if ( spawnPos == null )
					{
						continue;
					}
					
					if ( !(world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(spawnPos).grow(16, 10, 16))).isEmpty() )
					{
						continue;
					}
					
					int localFugitiveCount = world.getEntitiesWithinAABB(EntityFugitive.class, new AxisAlignedBB(spawnPos).grow(90, 45, 90)).size();
	
					if ( localFugitiveCount > 4 )
					{
						continue;
					}
					
					if ( localFugitiveCount == 4 )
					{
						this.spawnFugitive(world, spawnPos, player);
						continue;
					}
					else if ( localFugitiveCount < 2 )
					{
						this.spawnFugitive(world, spawnPos, player);
						this.spawnFugitive(world, spawnPos, player);
					}
					
					this.spawnFugitive(world, spawnPos, player);
					this.spawnFugitive(world, spawnPos, player);
					
					return;
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("ERROR SPAWNING EntityFugitive: " + e);
			return;
		}
	}
	
	public void spawnFugitive(World world, BlockPos spawnPos, EntityPlayer player)
	{
		EntityFugitive e = new EntityFugitive(world);
		e.setPosition(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
		world.spawnEntity(e);
		e.setAttackTarget(player);
	}
	
	public BlockPos searchForSuitableLocation( QuestData data )
	{
		BlockPos pos = null;
		for (int i = 0; i < 100; i++)
		{
			pos = randomLocation(data, rand, rand.nextInt(16)+16+i, false);
			if (pos != null)
			{
				break;
			}
		}
		if (pos == null)
		{
			for (int i = 0; i < 10; i++)
			{
				pos = randomLocation(data, rand, rand.nextInt(64)+16, true);
				if (pos != null)
				{
					break;
				}
			}
		}
		return pos;
	}

	public BlockPos randomLocation(QuestData data, Random random, int range, boolean force)
	{
		Province province = getQuestProvince(data);
		EntityPlayer player = data.getPlayer();

		if ( province == null || player == null )
		{
			return null;
		}
		
		double angle = rand.nextDouble()*Math.PI*2.0D;
		int x = (int) (Math.cos(angle)*range);
		int z = (int) (Math.sin(angle)*range);
		
		x += player.posX;
		z += player.posZ;
		
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
	
//	private void addToroSpawner(QuestData data, World world, BlockPos blockpos, List<String> entities)
//	{
//		world.setBlockState(blockpos, BlockToroSpawner.INSTANCE.getDefaultState());
//		TileEntity tileentity = world.getTileEntity(blockpos);
//		if (tileentity instanceof TileEntityToroSpawner)
//		{
//			TileEntityToroSpawner spawner = (TileEntityToroSpawner) tileentity;
//			spawner.setTriggerDistance(28);
//			spawner.setEntityIds(entities);
//			spawner.setSpawnRadius(8);
//			spawner.addEntityTag(data.getQuestId().toString());
//			spawner.addEntityTag("capture_fugitives");
//		}
//		else
//		{
//			System.out.println("tile entity is missing");
//		}
//	}
//
//	private List<String> getDefaultEnemies(QuestData data)
//	{
//		List<String> entity = new ArrayList<String>();
//		
//		for ( int i = data.getiData().get("target") + 1; i > 0; i-- )
//		{
//			entity.add("toroquest:toroquest_fugitive");
//		}
//		return entity;
//	}
	
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
		s.append("|").append(listItems(getRewardItems(q.getData()))  + ",\n" );
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
		
		int roll = rand.nextInt(3)+2;
		int em = roll*3+4;
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
