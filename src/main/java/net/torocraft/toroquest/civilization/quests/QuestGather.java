package net.torocraft.toroquest.civilization.quests;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.items.ItemHandlerHelper;
import net.torocraft.toroquest.civilization.CivilizationHandlers;
import net.torocraft.toroquest.civilization.Province;
import net.torocraft.toroquest.civilization.player.PlayerCivilizationCapabilityImpl;
import net.torocraft.toroquest.civilization.quests.QuestFarm.DataWrapper;
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
		
		int em = 6;
		setRewardRep(data, em*2);
		if ( PlayerCivilizationCapabilityImpl.get(player).getReputation(province.civilization) >= 3000 )
		{
			em *= 2;
		}
		reward.add(new ItemStack(Items.EMERALD, em));
		QuestGather.setRewardItems(data, reward);
		this.setData(data);
		return data;
	}

	@Override
	public String getTitle(QuestData data) {
		return "quests.gather.title";
	}

	@Override
	public String getDescription(QuestData data) {
		if (data == null) {
			return "";
		}
		StringBuilder s = new StringBuilder();
		s.append("quests.gather.description");
		s.append("|").append("4 enchanted items and gift them to me"  + "\n\n" );
		s.append("|").append(listItems(getRewardItems(data)) + ",\n" );
		s.append("|").append(getRewardRep(data));
		return s.toString();
	}

	@Override
	public List<ItemStack> reject(QuestData data, List<ItemStack> in)
	{
		data.setChatStack( "Ah, I understand. You'd rather keep them for yourself?" );
		this.setData(data);
		data.getPlayer().closeScreen();
		return in;
	}

	@Override
	public List<ItemStack> accept(QuestData data, List<ItemStack> in) 
	{
		//if (! data.getPlayer().world.isRemote )
		{
			switch ( data.getPlayer().world.rand.nextInt(2) )
			{
				case 0:{data.setChatStack( "My enchanters are in need of magical items to disenchant. Do them a favor and bring me several enchanted items." );break;}
				case 1:{data.setChatStack( "My enchanters wish to research more about the magical propeties of powerful enchantments. Do them a favor and bring me several enchanted items." );break;}
			}
		}
		this.setData(data);
		return in;
	}

	@Override
	public List<ItemStack> complete(QuestData data, List<ItemStack> items)
	{
		Province province = loadProvince(data.getPlayer().world, data.getPlayer().getPosition());

		if (province == null || !province.id.equals(data.getProvinceId()))
		{
			return null;
		}
		
		if ( items == null || items.isEmpty() )
		{
			return null;
		}

		int amount = 0;
		for ( ItemStack item: items )
		{
			if ( item.isItemEnchanted() )
			{
				amount++;
			}
		}
		
		if ( amount < 4 )
		{
			if ( data.getChatStack() == "" )
			{
				data.setChatStack( "You don't have enough enchanted items." );
				this.setData(data);
			}
			return null;
		}

//		for ( ItemStack item: items )
//		{
//			items.remove(item);
//		}
		
		CivilizationHandlers.adjustPlayerRep(data.getPlayer(), data.getCiv(), getRewardRep(data));
		
		// data.setChatStack( "You have my gratitude, " + data.getPlayer().getName() + "." );
		data.setCompleted(true);
		this.setData(data);
		return getRewardItems(data);
	}
}
