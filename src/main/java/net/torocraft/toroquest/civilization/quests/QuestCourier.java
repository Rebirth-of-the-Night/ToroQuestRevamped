package net.torocraft.toroquest.civilization.quests;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.torocraft.toroquest.civilization.CivilizationHandlers;
import net.torocraft.toroquest.civilization.Province;
import net.torocraft.toroquest.civilization.player.PlayerCivilizationCapabilityImpl;
import net.torocraft.toroquest.civilization.quests.util.Quest;
import net.torocraft.toroquest.civilization.quests.util.QuestData;
import net.torocraft.toroquest.civilization.quests.util.Quests;

public class QuestCourier extends QuestBase implements Quest
{
	private static final ITooltipFlag ITooltipFlag = null;

	public static QuestCourier INSTANCE;

	public static int ID;

	public static void init(int id)
	{
		INSTANCE = new QuestCourier();
		Quests.registerQuest(id, INSTANCE);
		MinecraftForge.EVENT_BUS.register(INSTANCE);
		ID = id;
	}

	@Override
	public List<ItemStack> complete(QuestData data, List<ItemStack> in)
	{
		Province province = loadProvince(data.getPlayer().world, data.getPlayer().getPosition());

		if ( province == null || province.id == null || !province.id.equals(data.getProvinceId()) )
		{
			return null;
		}
		
		ItemStack note = null;
		boolean flag = false;
		
		for ( ItemStack item : in )
		{
			if ( !item.hasTagCompound() )
			{
				continue;
			}

			String noteQuestId = item.getTagCompound().getString("questId");
			Boolean isReply = item.getTagCompound().getBoolean("reply");

			if ( noteQuestId != null && isReply )
			{
				if ( noteQuestId.equals(data.getQuestId().toString()) )
				{
					note = item;
					break;
				}
				else
				{
					flag = true;
				}
			}
		}
		
		Province deliverToProvince = getDeliverToProvince(data);

		if ( note == null )
		{
			if ( flag )
			{
				data.setChatStack("courier.wrong", data.getPlayer(), deliverToProvince.name);
			}
			else
			{
				data.setChatStack("courier.incomlete", data.getPlayer(), deliverToProvince.name);
			}
			this.setData(data);
			return null;
		}

		note.setCount(0);
		in.remove(note);

		List<ItemStack> rewards = getRewardItems(data);
		
		CivilizationHandlers.adjustPlayerRep(data.getPlayer(), data.getCiv(), getRewardRep(data));

		if ( PlayerCivilizationCapabilityImpl.get(data.getPlayer()).getReputation(province.civilization) >= 3000 )
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
		
		if (rewards != null)
		{
			in.addAll(rewards);
		}
		data.setChatStack("courier.comlete", data.getPlayer(), deliverToProvince.name);
		this.setData(data);
		return in;
	}

	@Override
	public List<ItemStack> reject(QuestData data, List<ItemStack> in)
	{
		if ( data.getCompleted() )
		{
			return null;
		}
		Province deliverToProvince = getDeliverToProvince(data);
		data.setChatStack("courier.reject", data.getPlayer(), deliverToProvince.name);
		this.setData(data);
		data.getPlayer().closeScreen();
		return in;
	}

	@Override
	public List<ItemStack> accept(QuestData data, List<ItemStack> in)
	{
		Province deliverToProvince = getDeliverToProvince(data);
		
		Item paper = Items.PAPER;
		
		ItemStack note = new ItemStack(paper);
		
		ArrayList<String> list = new ArrayList<String>();
		
		list.add(I18n.format("quests.courier.notetooltip"));
		
		paper.addInformation( note, data.getPlayer().world, list, ITooltipFlag );
		
		BlockPos pos = new BlockPos(deliverToProvince.chunkX * 16, 0, deliverToProvince.chunkZ * 16);
		note.setStackDisplayName( (I18n.format("quests.courier.notename")) + deliverToProvince.name + " at " + getDirections( pos ) );
		
		note.setTagInfo("toProvince", new NBTTagString(deliverToProvince.id.toString()));
		note.setTagInfo("questId", new NBTTagString(data.getQuestId().toString()));
		
		in.add(note);
		data.setChatStack("courier.accept", data.getPlayer(), deliverToProvince.name);
		this.setData(data);
		return in;
	}

	@Override
	public String getTitle(QuestData data)
	{
		return "quests.courier.title";
	}

	@Override
	public String getDescription(QuestData data)
	{
		if (data == null)
		{
			return "";
		}
		Province deliverToProvince = getDeliverToProvince(data);
		BlockPos from = data.getPlayer().getPosition();
		BlockPos to = new BlockPos(deliverToProvince.chunkX * 16, from.getY(), deliverToProvince.chunkZ * 16);

		StringBuilder s = new StringBuilder();
		s.append("quests.courier.description");
		s.append("|").append(deliverToProvince.name);
		s.append("|").append( " at §lLocation:§r [" + getDirections(from, to) + "]" );
		s.append("|").append( "\n\n" );
		s.append("|").append(listItems(getRewardItems(data))  + ",\n" );
		s.append("|").append(getRewardRep(data));
		return s.toString();
	}

	@Override
	public QuestData generateQuestFor(EntityPlayer player, Province questProvince)
	{
		Province deliverToProvince = chooseRandomProvince(questProvince, player.world, true);
		if (deliverToProvince == null)
		{
			return null;
		}
		QuestData data = new QuestData();
		data.setCiv(questProvince.civilization);
		data.setPlayer(player);
		data.setProvinceId(questProvince.id);
		data.setQuestId(UUID.randomUUID());
		data.setQuestType(ID);
		data.setCompleted(false);
		setDeliverToProvinceId(data, deliverToProvince.id);
		setDistance(data, (int) Math.round(player.getPosition().getDistance(deliverToProvince.chunkX * 16, (int) player.posY, deliverToProvince.chunkZ * 16)));
		
		int roll = Math.round(getDistance(data)/60)+4;
		int em = MathHelper.clamp(roll, 8, 32);
		int rep = em*2;
		if ( PlayerCivilizationCapabilityImpl.get(player).getReputation(questProvince.civilization) >= 2000 )
		{
			em *= 2;
		}
		
		setRewardRep(data, rep);

		List<ItemStack> rewards = new ArrayList<ItemStack>(1);
		ItemStack emeralds = new ItemStack(Items.EMERALD, em);
		rewards.add(emeralds);
		setRewardItems(data, rewards);
		this.setData(data);
		return data;
	}


	private void setDeliverToProvinceId(QuestData data, UUID id) {
		data.getsData().put("deliverTo", id.toString());
	}

	private UUID getDeliverToProvinceId(QuestData data) {
		return UUID.fromString(data.getsData().get("deliverTo"));
	}

	private Province getDeliverToProvince(QuestData data) {
		Province p = getProvinceById(data.getPlayer().world, data.getsData().get("deliverTo"));
		if (p == null) {
			throw new UnsupportedOperationException("Deliever to provice ID[" + data.getsData().get("deliverTo") + "] was not found");
		}
		return p;
	}

	public static Integer getDistance(QuestData data) {
		return i(data.getiData().get("distance"));
	}

	public static void setDistance(QuestData data, Integer distance) {
		data.getiData().put("distance", distance);
	}

}
