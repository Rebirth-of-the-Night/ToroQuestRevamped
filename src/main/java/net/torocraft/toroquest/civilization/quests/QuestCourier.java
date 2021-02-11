package net.torocraft.toroquest.civilization.quests;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
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
		ItemStack note = getNoteFromItems(data, in);

		if (note == null)
		{
			if ( data.getChatStack() == "" )
			{
				data.setChatStack( "I need the reply note, " + data.getPlayer().getName() + "." );
				this.setData(data);
			}
			// data.getPlayer().closeScreen();
			return null;
		}
		
		Province province = loadProvince(data.getPlayer().world, data.getPlayer().getPosition());

		if (province == null || province.id == null || !province.id.equals(data.getProvinceId())) 
		{
			return null;
		}

		note.setCount(0);
		in.remove(note);

		List<ItemStack> rewards = getRewardItems(data);
		
		CivilizationHandlers.adjustPlayerRep(data.getPlayer(), data.getCiv(), getRewardRep(data));

		// data.setChatStack( "I knew I could count on you, " + data.getPlayer().getName() + "." );
		this.setData(data);
		
		if (rewards != null)
		{
			in.addAll(rewards);
		}
		return in;
	}

	protected ItemStack getNoteFromItems(QuestData data, List<ItemStack> in)
	{
		for (ItemStack s : in)
		{
			if (isReplyNoteForQuest(data, s))
			{
				return s;
			}
		}
		return null;
	}

	protected boolean isReplyNoteForQuest(QuestData data, ItemStack item)
	{
		if (!item.hasTagCompound() || !item.hasTagCompound())
		{
			return false;
		}

		String noteQuestId = item.getTagCompound().getString("questId");
		Boolean isReply = item.getTagCompound().getBoolean("reply");

		if (noteQuestId == null)
		{
			return false;
		}

		if (!isReply)
		{
			data.getPlayer().sendStatusMessage( new TextComponentString("This reply is not for your current quest!"), true);
			// data.getPlayer().closeScreen();
			return false;
		}

		/*
		 * quest ID must match
		 */
		if (!noteQuestId.equals(data.getQuestId().toString()))
		{
			data.getPlayer().sendStatusMessage( new TextComponentString("This reply is not for your current quest!"), true);
			// data.getPlayer().closeScreen();
			return false;
		}

		return true;
	}

	@Override
	public List<ItemStack> reject(QuestData data, List<ItemStack> in)
	{
		data.setChatStack( "Ah, I understand. A task too difficult for you then?" );
		this.setData(data);
		data.getPlayer().closeScreen();
		return in;
	}

	@Override
	public List<ItemStack> accept(QuestData data, List<ItemStack> in)
	{
		Province deliverToProvince = getDeliverToProvince(data);
		ItemStack note = new ItemStack(Items.PAPER);
		BlockPos pos = new BlockPos(deliverToProvince.chunkX * 16, 0, deliverToProvince.chunkZ * 16);
		note.setStackDisplayName( "Deliver to the Lord of " + deliverToProvince.name + " at " + getDirections( pos ) );
		note.setTagInfo("toProvince", new NBTTagString(deliverToProvince.id.toString()));
		note.setTagInfo("questId", new NBTTagString(data.getQuestId().toString()));
		in.add(note);
		// if (!data.getPlayer().world.isRemote)
		{
			switch (data.getPlayer().world.rand.nextInt(2))
			{
				case 0:
				{
					data.setChatStack( "The lord of " + deliverToProvince.name + " and I have much to discuss. Make sure this letter gets to him.");
					break;
				}
				case 1:
				{
					data.setChatStack( "I need this letter sent to the lord of " + deliverToProvince.name + " immediately. Be swift, " + data.getPlayer().getName() + "." );
					break;
				}
			}
		}
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
		
		int roll = (getDistance(data) / 160);
		int em = MathHelper.clamp(roll, 4, 32);
		int rep = em*2;
		if ( PlayerCivilizationCapabilityImpl.get(player).getReputation(questProvince.civilization) >= 3000 )
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
