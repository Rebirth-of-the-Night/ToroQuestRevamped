package net.torocraft.toroquest.civilization.quests;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.village.Village;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.torocraft.toroquest.civilization.CivilizationHandlers;
import net.torocraft.toroquest.civilization.CivilizationType;
import net.torocraft.toroquest.civilization.CivilizationUtil;
import net.torocraft.toroquest.civilization.Province;
import net.torocraft.toroquest.civilization.player.PlayerCivilizationCapabilityImpl;
import net.torocraft.toroquest.civilization.quests.util.ItemMapCentered;
import net.torocraft.toroquest.civilization.quests.util.Quest;
import net.torocraft.toroquest.civilization.quests.util.QuestData;
import net.torocraft.toroquest.civilization.quests.util.Quests;
import net.torocraft.toroquest.entities.EntityBanditLord;
import net.torocraft.toroquest.entities.EntityPigLord;
import net.torocraft.toroquest.generation.WorldGenPlacer;

public class QuestKillBossZombiePig extends QuestBase implements Quest
{
	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	public static int ID;
	public static QuestKillBossZombiePig INSTANCE;
	
	protected final String entityName = "toroquest:pig_lord";
	protected final String tag = "pig_lord";
	protected final String location = "The Corrupted Pig-Demon";
	protected final int emeraldAmount = 5;
	

	public static void init(int id)
	{
		INSTANCE = new QuestKillBossZombiePig();
		Quests.registerQuest(id, INSTANCE);
		MinecraftForge.EVENT_BUS.register(INSTANCE);
		ID = id;
	}
	
	@Override
	public List<ItemStack> complete(QuestData data, List<ItemStack> in)
	{
		if ( !data.getCompleted() )
		{
			if ( data.getChatStack().equals("") )
			{
				data.setChatStack( "My scouts report the demon still lives!" );
				this.setData(data);
			}
			// data.getPlayer().closeScreen();
			return null;
		}
		CivilizationHandlers.adjustPlayerRep(data.getPlayer(), data.getCiv(), getRewardRep(data));
		data.setChatStack( "You are truly a hero, " + data.getPlayer().getName() + "! You have slain the demon! Our people are forever in your debt." );
		this.setData(data);
		in.addAll(getRewardItems(data));
		return in;
	}

	@Override
	public List<ItemStack> reject(QuestData data, List<ItemStack> in)
	{
		data.setChatStack( "I understand your unease. I can only hope this demon will not catch scent of the village." );
		this.setData(data);
		data.getPlayer().closeScreen();
		return in;
	}

	@Override
	public List<ItemStack> accept(QuestData data, List<ItemStack> in)
	{
		try
		{
			BlockPos pos = searchForSuitableLocation(data, 1100, 30);
			if ( pos == null )
			{
				reject(data,in);
				return in;
			}
			this.setSpawnPosition(data, pos);
			// =-=-=-=-=-=-=-=-=-=-=-=-=-=-= World Generation =-=-=-=-=-=-=-=-=-=-=-=-=-=-=
			WorldGenPlacer.genPigPortal( data.getPlayer().world, pos.getX(), pos.getZ() );
			// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-==-=-=-=-=-=-=-=-=
			ItemStack itemstack = ItemMapCentered.setupNewMap(data.getPlayer().world, (double)pos.getX(), (double)pos.getZ(), (byte)4, true, true);
			ItemMapCentered.renderBiomePreviewMap(data.getPlayer().world, itemstack);
			MapData.addTargetDecoration(itemstack, pos, "+", MapDecoration.Type.TARGET_POINT);
			itemstack.setTranslatableName("§lMap to " + location + "§r");
			itemstack.setStackDisplayName("§lMap to " + location + "§r");
			in.add(itemstack);
			data.setChatStack( "A massive demon-pig from the underworld has found its way to the surface! It has not found us yet, but it will feast on the bodies of the villagers if does. " + data.getPlayer().getName() + ", I can only trust your expertise to slay this beast." );
			this.setData(data);
		}
		catch (Exception e)
		{
			reject(data,in);
		}
		return in;
	}
	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

	@Override
	public String getTitle(QuestData data)
	{
		if (data == null)
		{
			return "";
		}
		return "quests." + tag + ".title";
	}

	@Override
	public String getDescription(QuestData data)
	{
		if (data == null)
		{
			return "";
		}
		StringBuilder s = new StringBuilder();
		s.append("quests." + tag + ".description");
		if (getSpawnPosition(data) != null)
		{
			s.append("|").append( " at §lLocation:§r [" + getDirections(getProvincePosition(getQuestProvince(data)), getSpawnPosition(data)) + "]\n\n" );
		}
		else
		{
			s.append("|").append("\n\n");
		}
		s.append("|").append(listItemsBlocks(getRewardItems(data))  + "\n" );
		s.append("|").append(getRewardRep(data));
		return s.toString();
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
		setRewardRep(data, emeraldAmount*18);
		int em = emeraldAmount;
		if ( PlayerCivilizationCapabilityImpl.get(player).getReputation(province.civilization) >= 3000 )
		{
			em *= 2;
		}
		List<ItemStack> reward = new ArrayList<ItemStack>(1);
		reward.add(new ItemStack(Blocks.EMERALD_BLOCK, em));
		setRewardItems(data, reward);
		this.setData(data);
		return data;
	}

	@SubscribeEvent
	public void checkkills(LivingDeathEvent event)
	{

		Entity victim = event.getEntity();
		
		if ( victim == null || !(victim instanceof EntityPigLord) )
		{
			return;
		}

		DamageSource source = event.getSource();
		
		if ( source == null || source.getTrueSource() == null )
		{
			return;
		}

		List<EntityPlayer> playerList = victim.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(victim.getPosition()).grow(512, 128, 512), new Predicate<EntityPlayer>()
		{
			public boolean apply(@Nullable EntityPlayer entity)
			{
				return true;
			}
		});
		
		if ( playerList.size() < 1 )
		{
			if (source.getTrueSource() instanceof EntityPlayer)
			{
				playerList.add((EntityPlayer) source.getTrueSource());
			}
		}
		
		CivilizationType civ = null;
		for ( EntityPlayer player : playerList )
		{
			Set<QuestData> quests = PlayerCivilizationCapabilityImpl.get(player).getCurrentQuests();
			for (QuestData data : quests)
			{
				if (ID == data.getQuestType())
				{
					data.setCompleted(true);
					chatCompletedQuest(data);
					civ = data.getCiv();
					//this.setData(data);
				}
			}
			if ( !player.world.isRemote ) player.sendMessage(new TextComponentString( "§lThe Pig-Demon has been slain!§r"));
		}
	}
}
