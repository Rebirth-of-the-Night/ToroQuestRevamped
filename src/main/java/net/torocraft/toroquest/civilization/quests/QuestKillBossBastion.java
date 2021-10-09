package net.torocraft.toroquest.civilization.quests;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.server.command.TextComponentHelper;
import net.torocraft.toroquest.civilization.CivilizationHandlers;
import net.torocraft.toroquest.civilization.Province;
import net.torocraft.toroquest.civilization.player.PlayerCivilizationCapabilityImpl;
import net.torocraft.toroquest.civilization.quests.util.ItemMapCentered;
import net.torocraft.toroquest.civilization.quests.util.Quest;
import net.torocraft.toroquest.civilization.quests.util.QuestData;
import net.torocraft.toroquest.civilization.quests.util.Quests;
import net.torocraft.toroquest.entities.EntityBas;
import net.torocraft.toroquest.generation.WorldGenPlacer;

public class QuestKillBossBastion extends QuestBase implements Quest
{
	public static int ID;
	public static QuestKillBossBastion INSTANCE;
	
	protected final String entityName = "toroquest:toroquest_bas";
	protected final String tag = "legendary_skeleton";
	protected final int emeraldAmount = 5;
	


	public static void init(int id)
	{
		INSTANCE = new QuestKillBossBastion();
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
		
		if ( !data.getCompleted() )
		{
			if ( data.getChatStack().equals("") )
			{
				data.setChatStack( "legendary_skeleton.incomplete", data.getPlayer(), null );

				this.setData(data);
			}
			// data.getPlayer().closeScreen();
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
		
		data.setChatStack( "legendary_skeleton.complete", data.getPlayer(), null );

		in.addAll(getRewardItems(data));
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
		
		data.setChatStack( "legendary_skeleton.reject", data.getPlayer(), null );
		data.getPlayer().closeScreen();this.setData(data);
		return in;
	}

	@Override
	public List<ItemStack> accept(QuestData data, List<ItemStack> in)
	{
		try
		{
			BlockPos pos = searchForSuitableLocation(data, 1000, 60);
			if ( pos == null )
			{
				reject(data,in);
				return in;
			}
			this.setSpawnPosition(data, pos);
			// =-=-=-=-=-=-=-=-=-=-=-=-=-=-= World Generation =-=-=-=-=-=-=-=-=-=-=-=-=-=-=
			WorldGenPlacer.genBastionsLair(data.getPlayer().world, pos.getX(), pos.getZ());
			// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-==-=-=-=-=-=-=-=-=
			ItemStack itemstack = ItemMapCentered.setupNewMap(data.getPlayer().world, (double)pos.getX(), (double)pos.getZ(), (byte)4, true, true);
			ItemMapCentered.renderBiomePreviewMap(data.getPlayer().world, itemstack);
			MapData.addTargetDecoration(itemstack, pos, "+", MapDecoration.Type.TARGET_POINT);
			itemstack.setTranslatableName("§lMap to " + TextComponentHelper.createComponentTranslation(data.getPlayer(), "quests.legendary_skeleton.map", new Object[0]).getFormattedText() + "§r");
			itemstack.setStackDisplayName("§lMap to " + TextComponentHelper.createComponentTranslation(data.getPlayer(), "quests.legendary_skeleton.map", new Object[0]).getFormattedText() + "§r");
			in.add(itemstack);
			data.setChatStack( "legendary_skeleton.accept", data.getPlayer(), null );

			this.setData(data);
		}
		catch (Exception e)
		{
			reject(data,in);
		}
		return in;
	}
	
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
		if ( PlayerCivilizationCapabilityImpl.get(player).getReputation(province.civilization) >= 2000 )
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
		
		if (!(victim instanceof EntityBas) )
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
		
		//CivilizationType civ = null;
		for ( EntityPlayer player : playerList )
		{
			Set<QuestData> quests = PlayerCivilizationCapabilityImpl.get(player).getCurrentQuests();
			for (QuestData data : quests)
			{
				if (ID == data.getQuestType())
				{
					data.setCompleted(true);
					chatCompletedQuest(data);
					//civ = data.getCiv();
					//this.setData(data);
				}
			}
			player.sendMessage(new TextComponentString(TextComponentHelper.createComponentTranslation(player, "quests.legendary_skeleton.slain", new Object[0]).getFormattedText()));
		}
	}
}
