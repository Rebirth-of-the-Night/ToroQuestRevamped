package net.torocraft.toroquest.civilization.quests;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStone;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
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

	@SuppressWarnings("deprecation")
	@SubscribeEvent
	public void onFarm(PlaceEvent event)
	{
		if ( event.getPlayer() == null || event.getPlayer().world.isRemote )
		{
			return;
		}
		
        //super(Material.ROCK);
//		System.out.println(event.getBlockSnapshot().getCurrentBlock().getBlock());
//		System.out.println(event.getBlockSnapshot().getCurrentBlock().getBlock().getDefaultState());
//		System.out.println(event.getBlockSnapshot().getCurrentBlock().getBlock().getDefaultState().getMaterial());
//		System.out.println(event.getPlacedBlock());
//		System.out.println(event.getPlacedBlock().getBlock());
//		System.out.println(event.getPlacedBlock().getMaterial());
//		System.out.println(event.getPlacedBlock().getBlock().getMaterial(event.getPlacedBlock().getBlock().getDefaultState()));

		if ( event.getPlacedBlock().getMaterial() != Material.ROCK ) // || event.getPlacedBlock() instanceof BlockStone )
		{
			return;
		}
		
		Set<QuestData> quests = PlayerCivilizationCapabilityImpl.get(event.getPlayer()).getCurrentQuests();
		
		DataWrapper quest = new DataWrapper();
		
		for ( QuestData data : quests )
		{
			quest.setData(data);
			
			if ( quest.isBuildQuest() )
			{
				Province province = PlayerCivilizationCapabilityImpl.get(event.getPlayer()).getInCivilization();

				if ( province != null && province.civilization != null && quest.isInCorrectProvince(province) )
				{
					if ( perform(quest) )
					{
						return;
					}
				}
			}
		}
	}
	
	public boolean perform(DataWrapper quest)
	{
		if ( !quest.data.getCompleted() )
		{
			quest.setCurrentAmount(quest.getCurrentAmount() + 1);
			
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
		int em = (int)Math.round((double)roll/32)+6;
		q.setRewardRep(em*2);
		if ( PlayerCivilizationCapabilityImpl.get(player).getReputation(province.civilization) >= 2000 )
		{
			em *= 2;
		}
		//q.setCropType(rand.nextInt(CROP_TYPES.length));
		q.setCurrentAmount(0);
		
		q.setTargetAmount(roll);
		ItemStack emeralds = new ItemStack(Items.EMERALD, em);
		List<ItemStack> rewardItems = new ArrayList<ItemStack>();
		rewardItems.add(emeralds);
		setRewardItems(q.getData(), rewardItems);
		this.setData(q.getData());
		return q.getData();
	}

	@Override
	public List<ItemStack> reject(QuestData data, List<ItemStack> in)
	{
		if ( data.getCompleted() )
		{
			return null;
		}
		data.setChatStack( "build.reject", data.getPlayer(), null );
		this.setData(data);
		data.getPlayer().closeScreen();
		return in;
	}

	@Override
	public List<ItemStack> accept(QuestData data, List<ItemStack> in)
	{
		data.setChatStack( "build.accept", data.getPlayer(), null );
		this.setData(data);
		return in;
	}

	@Override
	public List<ItemStack> complete(QuestData quest, List<ItemStack> items)
	{
		
		Province province = loadProvince(quest.getPlayer().world, quest.getPlayer().getPosition());

		if (province == null || province.id == null || !province.id.equals(quest.getProvinceId()))
		{
			return null;
		}
		
		if ( !quest.getCompleted() )
		{
			if ( quest.getChatStack().equals("")  )
			{
				quest.setChatStack( "build.incomplete", quest.getPlayer(), null );
			    this.setData(quest);
			}
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

		quest.setChatStack( "build.complete", quest.getPlayer(), null );
		this.setData(quest);
		return items;
	}

	public static class DataWrapper
	{
		QuestData data = new QuestData();
		//private Province provinceFarmedIn;
		//private Block farmedCrop;
		
		public QuestData getData()
		{
			return data;
		}

		public DataWrapper setData(QuestData data) {
			this.data = data;
			return this;
		}

		public Integer getProvinceFarmedIn()
		{
			return i(data.getiData().get("province"));
			// return provinceFarmedIn;
		}

		public void setProvinceFarmedIn(Integer provinceFarmedIn)
		{
			data.getiData().put("province", provinceFarmedIn);
			// this.provinceFarmedIn = provinceFarmedIn;
		}

//		public Block getFarmedCrop() {
//			return farmedCrop;
//		}
//
//		public void setFarmedCrop(Block farmedCrop) {
//			this.farmedCrop = farmedCrop;
//		}

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

		private boolean isBuildQuest()
		{
			return data.getQuestType() == ID;
		}

		private boolean isInCorrectProvince(Province provinceFarmedIn)
		{
			return data.getProvinceId().equals( provinceFarmedIn.id );
		}

	}
}
