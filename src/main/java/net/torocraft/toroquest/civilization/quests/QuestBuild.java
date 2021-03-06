package net.torocraft.toroquest.civilization.quests;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockStone;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.torocraft.toroquest.civilization.CivilizationHandlers;
import net.torocraft.toroquest.civilization.Province;
import net.torocraft.toroquest.civilization.player.PlayerCivilizationCapabilityImpl;
import net.torocraft.toroquest.civilization.quests.util.Quest;
import net.torocraft.toroquest.civilization.quests.util.QuestData;
import net.torocraft.toroquest.civilization.quests.util.Quests;

public class QuestBuild extends QuestBase implements Quest
{

	//private static final Block[] CROP_TYPES = { Blocks.CARROTS, Blocks.POTATOES, Blocks.WHEAT, Blocks.MELON_STEM, Blocks.PUMPKIN_STEM, Blocks.BEETROOTS };

	public static QuestBuild INSTANCE;

	public static int ID;

	public static void init(int id)
	{
		INSTANCE = new QuestBuild();
		Quests.registerQuest(id, INSTANCE);
		MinecraftForge.EVENT_BUS.register(INSTANCE);
		ID = id;
	}

	@SubscribeEvent
	public void onFarm(PlaceEvent event)
	{
		if (event.getPlayer() == null)
		{
			return;
		}

		Province provinceFarmedIn = loadProvince(event.getPlayer().world, event.getBlockSnapshot().getPos());

		if (provinceFarmedIn == null || provinceFarmedIn.civilization == null)
		{
			return;
		}

		handleFarmQuest(event.getPlayer(), provinceFarmedIn, event.getPlacedBlock().getBlock(), true);
	}

	@SubscribeEvent
	public void onHarvest(BreakEvent event)
	{
		if (event.getPlayer() == null)
		{
			return;
		}

		Province provinceFarmedIn = loadProvince(event.getPlayer().world, event.getPos());

		if (provinceFarmedIn == null || provinceFarmedIn.civilization == null)
		{
			return;
		}

		handleFarmQuest(event.getPlayer(), provinceFarmedIn, event.getState().getBlock(), false);
	}

	private void handleFarmQuest(EntityPlayer player, Province provinceFarmedIn, Block crop, boolean plant)
	{
		Set<QuestData> quests = PlayerCivilizationCapabilityImpl.get(player).getCurrentQuests();
		DataWrapper quest = new DataWrapper();
		for (QuestData data : quests)
		{
			quest.setData(data);
			quest.farmedCrop = crop;
			quest.provinceFarmedIn = provinceFarmedIn;
			if (perform(quest, plant, crop))
			{
				return;
			}
		}
	}
	
	public boolean perform(DataWrapper quest, boolean plant, Block crop)
	{
		if (quest.getData().getPlayer().world.isRemote)
		{
			return false;
		}
		
		if (!quest.isApplicable(crop))
		{
			return false;
		}

		if ( !quest.data.getCompleted() )
		{
			if (plant)
			{
				quest.setCurrentAmount(quest.getCurrentAmount() + 1);
			}
//			else if ( !(crop instanceof BlockStone) )
//			{
//				quest.setCurrentAmount(quest.getCurrentAmount() - 1);
//				if ( quest.getCurrentAmount() < 0 )
//				{
//					quest.setCurrentAmount(0);
//				}
//			}
			
			quest.data.getPlayer().sendStatusMessage( new TextComponentString(MathHelper.clamp(quest.getCurrentAmount(), 0, quest.getTargetAmount())+"/"+quest.getTargetAmount()), true);
			
			if ( quest.getCurrentAmount() >= quest.getTargetAmount() )
			{
				quest.setCurrentAmount(quest.getTargetAmount());
				quest.data.setCompleted(true);
				chatCompletedQuest(quest.data);
			}
		}
		return true;
	}

	@Override
	public String getTitle(QuestData data)
	{
		return "quests.build.title";
	}

//	private String cropName(Integer i) {
//		if (i == null) {
//			return "Crop";
//		}
//		Block crop = CROP_TYPES[i];
//		if (crop == null) {
//			System.out.println("invalid crop ID [" + i + "]");
//			return "Crop";
//		}
//		return crop.getLocalizedName();
//	}

	@Override
	public String getDescription(QuestData data)
	{
		if (data == null)
		{
			return "";
		}
		DataWrapper q = new DataWrapper().setData(data);
		StringBuilder s = new StringBuilder();
		s.append("quests.build.description");
		s.append("|").append(getProvinceName(data.getPlayer(), data.getProvinceId()));
		s.append("|").append(q.getCurrentAmount() );
		s.append("|").append(q.getTargetAmount() + "\n\n");
		s.append("|").append(listItems(getRewardItems(data)) + ",\n");
		s.append("|").append(q.getRewardRep());
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

		int roll = rand.nextInt(3)*64+128;
		int em = (int)Math.round((double)roll/32+4);
		int rep = em*2;
		if ( PlayerCivilizationCapabilityImpl.get(player).getReputation(province.civilization) >= 3000 )
		{
			em *= 2;
		}
		//q.setCropType(rand.nextInt(CROP_TYPES.length));
		q.setCurrentAmount(0);
		
		q.setTargetAmount(roll);
		ItemStack emeralds = new ItemStack(Items.EMERALD, em);
		q.setRewardRep(rep);
		List<ItemStack> rewardItems = new ArrayList<ItemStack>();
		rewardItems.add(emeralds);
		setRewardItems(q.data, rewardItems);
		this.setData(q.data);
		return q.data;
	}

	@Override
	public List<ItemStack> reject(QuestData data, List<ItemStack> in)
	{
		data.setChatStack("How hard is it for you to place just a LITTLE cobblestone around the village?! Unbelievable! I bet you have chests full of that s*** just laying around.");
		this.setData(data);
		data.getPlayer().closeScreen();
		return in;
	}
	
	

	@Override
	public List<ItemStack> accept(QuestData data, List<ItemStack> in)
	{
		//if (!data.getPlayer().world.isRemote)
		{
			switch (data.getPlayer().world.rand.nextInt(4))
			{
				case 0:{data.setChatStack("The guards are having trouble securing certain areas around the village. I'll need you to set up walls to bolster the province's defenses.");break;}
				case 1:{data.setChatStack("Our province's defences are lacking! We have lost several villagers because of it. " + data.getPlayer().getName() + ", I need you to build more walls around the province to keep them safe.");break;}
				case 2:{data.setChatStack("" + data.getPlayer().getName() + ", the province is in desperate need of walls to keep our villagers protected.");break;}
				case 3:{data.setChatStack("This province is becoming overwhelmed by bandits and creatures of the night. We need you to start building walls around the province to keep them out and our villagers safe.");break;}
			}
		}
		this.setData(data);
		return in;
	}

	@Override
	public List<ItemStack> complete(QuestData quest, List<ItemStack> items)
	{
		if ( !quest.getCompleted() )
		{
			if ( quest.getChatStack().equals("")  )
			{
			    quest.setChatStack( "You haven't built enough of the wall, " + quest.getPlayer().getName() + "." );
			    this.setData(quest);
			}
		    // // quest.getPlayer().closeScreen();
			return null;
		}

		Province province = loadProvince(quest.getPlayer().world, quest.getPlayer().getPosition());

		if (province == null || province.id == null || !province.id.equals(quest.getProvinceId()))
		{
			return null;
		}

		CivilizationHandlers.adjustPlayerRep(quest.getPlayer(), quest.getCiv(), getRewardRep(quest));

		List<ItemStack> rewards = getRewardItems(quest);
		if (rewards != null)
		{
			items.addAll(rewards);
		}

		// data.setChatStack( "You have my gratitude, " + data.getPlayer().getName() + "." );
		this.setData(quest);
		
		return items;
	}

	public static class DataWrapper
	{
		QuestData data = new QuestData();
		private Province provinceFarmedIn;
		private Block farmedCrop;
		
		public QuestData getData() {
			return data;
		}

		public DataWrapper setData(QuestData data) {
			this.data = data;
			return this;
		}

		public Province getProvinceFarmedIn() {
			return provinceFarmedIn;
		}

		public void setProvinceFarmedIn(Province provinceFarmedIn) {
			this.provinceFarmedIn = provinceFarmedIn;
		}

		public Block getFarmedCrop() {
			return farmedCrop;
		}

		public void setFarmedCrop(Block farmedCrop) {
			this.farmedCrop = farmedCrop;
		}

//		public Integer getCropType() {
//			return i(data.getiData().get("type"));
//		}
//
//		public void setCropType(Integer cropType) {
//			data.getiData().put("type", cropType);
//		}

		public Integer getTargetAmount() {
			return i(data.getiData().get("target"));
		}

		public void setTargetAmount(Integer targetAmount) {
			data.getiData().put("target", targetAmount);
		}

		public Integer getCurrentAmount() {
			return i(data.getiData().get("amount"));
		}

		public void setCurrentAmount(Integer currentAmount) {
			data.getiData().put("amount", currentAmount);
		}

		public Integer getRewardRep() {
			return i(data.getiData().get("rep"));
		}

		public void setRewardRep(Integer rewardRep) {
			data.getiData().put("rep", rewardRep);
		}

		private Integer i(Object o) {
			try {
				return (Integer) o;
			} catch (Exception e) {
				return 0;
			}
		}

		private boolean isApplicable(Block crop)
		{
			return isFarmQuest() && isInCorrectProvince() && isCorrectCrop(crop);
		}

		private boolean isFarmQuest()
		{
			return data.getQuestType() == ID;
		}

		private boolean isInCorrectProvince()
		{
			return data.getProvinceId().equals(getProvinceFarmedIn().id);
		}

		private boolean isCorrectCrop(Block crop)
		{
			return ( (crop.getMaterial(crop.getDefaultState()) == Material.ROCK || crop.getSoundType() == SoundType.STONE ) && (crop.getDefaultState().isFullBlock() || crop.getDefaultState() instanceof BlockStairs || crop.getDefaultState() instanceof BlockSlab ) );
		}

	}
}
