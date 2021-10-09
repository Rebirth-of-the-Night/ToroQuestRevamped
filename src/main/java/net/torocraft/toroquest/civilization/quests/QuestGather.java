package net.torocraft.toroquest.civilization.quests;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.torocraft.toroquest.civilization.CivilizationHandlers;
import net.torocraft.toroquest.civilization.Province;
import net.torocraft.toroquest.civilization.player.PlayerCivilizationCapabilityImpl;
import net.torocraft.toroquest.civilization.quests.QuestRecruit.DataWrapper;
import net.torocraft.toroquest.civilization.quests.util.Quest;
import net.torocraft.toroquest.civilization.quests.util.QuestData;
import net.torocraft.toroquest.civilization.quests.util.Quests;

public class QuestGather extends QuestBase implements Quest
{

	public static QuestGather INSTANCE;

	public static int ID;

	public static void init(int id)
	{
		INSTANCE = new QuestGather();
		Quests.registerQuest(id, INSTANCE);
		MinecraftForge.EVENT_BUS.register(INSTANCE);
		ID = id;
	}

	@Override
	public QuestData generateQuestFor(EntityPlayer player, Province province)
	{
		QuestData data = new QuestData();
		data.setCiv(province.civilization);
		data.setPlayer(player);
		data.setProvinceId(province.id);
		data.setQuestId(UUID.randomUUID());
		data.setQuestType(ID);
		data.setCompleted(false);
		List<ItemStack> reward = new ArrayList<ItemStack>();
		int em = 12;
		setRewardRep(data, em*2);
		if ( PlayerCivilizationCapabilityImpl.get(player).getReputation(province.civilization) >= 2000 )
		{
			em *= 2;
		}
		reward.add(new ItemStack(Items.EMERALD, em));
		QuestGather.setRewardItems(data, reward);
		this.setData(data);
		return data;
	}

	@Override
	public String getTitle(QuestData data)
	{
		return "quests.gather.title";
	}

	@Override
	public String getDescription(QuestData data)
	{
		if (data == null)
		{
			return "";
		}
		StringBuilder s = new StringBuilder();
		s.append("quests.gather.description");
		s.append("|").append("\n\n");
		s.append("|").append(listItems(getRewardItems(data)) + ",\n" );
		s.append("|").append(getRewardRep(data));
		return s.toString();
	}

	@Override
	public List<ItemStack> reject(QuestData data, List<ItemStack> in)
	{
		data.setChatStack( "gather.reject", data.getPlayer(), null );
		this.setData(data);
		data.getPlayer().closeScreen();
		return in;
	}

	@Override
	public List<ItemStack> accept(QuestData data, List<ItemStack> in) 
	{
		data.setChatStack( "gather.accept", data.getPlayer(), null );
		data.setCompleted(true);
		this.setData(data);
		return in;
	}
	
//	public void a(EntityPlayer player)
//	{
//		if (player == null)
//		{
//			return;
//		}
//
//		Province province = PlayerCivilizationCapabilityImpl.get(player).getInCivilization();
//
//		if (province == null || province.civilization == null)
//		{
//			return;
//		}
//
//		aa(player, province);
//	}
//
//	private void aa(EntityPlayer player, Province province)
//	{
//		Set<QuestData> quests = PlayerCivilizationCapabilityImpl.get(player).getCurrentQuests();
//
//		for (QuestData data : quests)
//		{
//			try
//			{
//				data.setCompleted(true);
//				System.out.println("ooooooooo");
//			}
//			catch (Exception e)
//			{
//				e.printStackTrace();
//			}
//		}
//	}
//	
//	
//	
//	public boolean checkCompleted( EntityPlayer player, List<ItemStack> items )
//	{
//		System.out.println("!!!");     	//QuestGather.INSTANCE.checkCompleted(inventory.getGivenItems());
//		this.a(player);
//
//		for ( ItemStack item: items )
//		{
//			System.out.println(item);     	//QuestGather.INSTANCE.checkCompleted(inventory.getGivenItems());
//
//			if ( item.isItemEnchanted() && !item.isStackable() )
//			{
//				this.a(player);
//				return true;
//			}
//		}
//		return false;
		
//		Province province = loadProvince(data.getPlayer().world, data.getPlayer().getPosition());
//
//		if (province == null || !province.id.equals(data.getProvinceId()))
//		{
//			return false;
//		}
//		
//		if ( items == null || items.isEmpty() )
//		{
//			if ( data.getChatStack() == "" )
//			{
//				data.setChatStack( "gather.incomplete", data.getPlayer(), null );
//				this.setData(data);
//			}
//			return false;
//		}
//
//		int amount = 0;
//		for ( ItemStack item: items )
//		{
//			if ( item.isItemEnchanted() && !item.isStackable() )
//			{
//				amount++;
//			}
//		}
//		
//		if ( amount < 4 )
//		{
//			if ( data.getChatStack() == "" )
//			{
//				data.setChatStack( "gather.incomplete", data.getPlayer(), null );
//				this.setData(data);
//			}
//			return false;
//		}
//
////		for ( ItemStack item: items )
////		{
////			items.remove(item);
////		}
//		
//		CivilizationHandlers.adjustPlayerRep(data.getPlayer(), data.getCiv(), getRewardRep(data));
//		
//		if ( PlayerCivilizationCapabilityImpl.get(data.getPlayer()).getReputation(data.getCiv()) >= 3000 )
//		{
//			if (!data.getPlayer().world.isRemote)
//	        {
//	            int i = getRewardRep(data)*2;
//
//	            while (i > 0)
//	            {
//	                int j = EntityXPOrb.getXPSplit(i);
//	                i -= j;
//	                data.getPlayer().world.spawnEntity(new EntityXPOrb(data.getPlayer().world, data.getPlayer().posX+((rand.nextInt(2)*2-1)*2), data.getPlayer().posY, data.getPlayer().posZ+((rand.nextInt(2)*2-1)*2), j));
//	            }
//	        }
//		}
//		data.setCompleted(true);
//		data.setChatStack( "gather.complete", data.getPlayer(), null );
//		this.setData(data);
//		return true;
//	}
	
	@Override
	public List<ItemStack> complete(QuestData data, List<ItemStack> items)
	{
//		if ( data.getCompleted() )
//		{
//			data.setChatStack( "gather.complete", data.getPlayer(), null );
//			this.setData(data);
//			return getRewardItems(data);
//		}
//		else
//		{
//			data.setChatStack( "gather.incomplete", data.getPlayer(), null );
//			this.setData(data);
//			return null;
//		}
		Province province = loadProvince(data.getPlayer().world, data.getPlayer().getPosition());

		if ( province == null || !province.id.equals(data.getProvinceId()) )
		{
			return null;
		}
		
		if ( items == null || items.isEmpty() )
		{
			if ( data.getChatStack() == "" )
			{
				data.setChatStack( "gather.incomplete", data.getPlayer(), null );
				this.setData(data);
			}
			return null;
		}

		int amount = 0;
		for ( ItemStack item: items )
		{
			if ( item.isItemEnchanted() && !item.isStackable() )
			{
				amount++;
			}
		}
		
		if ( amount < 4 )
		{
			if ( data.getChatStack() == "" )
			{
				data.setChatStack( "gather.incomplete", data.getPlayer(), null );
				this.setData(data);
			}
			return null;
		}
		
		CivilizationHandlers.adjustPlayerRep(data.getPlayer(), data.getCiv(), getRewardRep(data));
		
		if ( PlayerCivilizationCapabilityImpl.get(data.getPlayer()).getReputation(data.getCiv()) >= 3000 )
		{
			if (!data.getPlayer().world.isRemote)
	        {
	            int i = getRewardRep(data)*2;

	            while (i > 0)
	            {
	                int j = EntityXPOrb.getXPSplit(i);
	                i -= j;
	                data.getPlayer().world.spawnEntity(new EntityXPOrb(data.getPlayer().world, data.getPlayer().posX+((rand.nextInt(2)*2-1)*2), data.getPlayer().posY, data.getPlayer().posZ+((rand.nextInt(2)*2-1)*2), j));
	            }
	        }
		}

		data.setChatStack( "gather.complete", data.getPlayer(), null );
		data.setCompleted(true);
		this.setData(data);
		return getRewardItems(data);
	}
}
