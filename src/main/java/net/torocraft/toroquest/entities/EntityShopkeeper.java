package net.torocraft.toroquest.entities;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.ai.EntityAILookAtTradePlayer;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITradePlayer;
import net.minecraft.entity.ai.EntityAIVillagerInteract;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.torocraft.toroquest.ToroQuest;
import net.torocraft.toroquest.civilization.CivilizationUtil;
import net.torocraft.toroquest.civilization.Province;
import net.torocraft.toroquest.civilization.player.PlayerCivilizationCapabilityImpl;
import net.torocraft.toroquest.config.ToroQuestConfiguration;
import net.torocraft.toroquest.entities.trades.ToroVillagerTrades;
import net.torocraft.toroquest.item.ItemScrollEarth;
import net.torocraft.toroquest.item.ItemScrollFire;
import net.torocraft.toroquest.item.ItemScrollMoon;
import net.torocraft.toroquest.item.ItemScrollSun;
import net.torocraft.toroquest.item.ItemScrollWater;
import net.torocraft.toroquest.item.ItemScrollWind;

public class EntityShopkeeper extends EntityToroVillager implements IMerchant
{

	public static String NAME = "shopkeeper";

	static
	{
		if (ToroQuestConfiguration.specificEntityNames)
		{
			NAME = ToroQuestEntities.ENTITY_PREFIX + NAME;
		}
	}
	
	@Override
	public String getName()
    {
		return "Shopkeeper";
    }
	
	@Override
	public ITextComponent getDisplayName()
    {
		return new TextComponentString("Shopkeeper");
    }

	public static void init(int entityId) {
		EntityRegistry.registerModEntity(new ResourceLocation(ToroQuest.MODID, NAME), EntityShopkeeper.class, NAME, entityId, ToroQuest.INSTANCE, 80,
				3, true, 0x000000, 0xe0d6b9);
	}

	public EntityShopkeeper(World worldIn)
	{
		super(worldIn, 0);
	}

	@Override
	public IEntityLivingData finalizeMobSpawn(DifficultyInstance p_190672_1_, @Nullable IEntityLivingData p_190672_2_, boolean p_190672_3_) {
		return p_190672_2_;
	}
	
	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand)
	{
		ItemStack itemstack = player.getHeldItem(hand);
		Item item = itemstack.getItem();
		if ( item.equals(Item.getByNameOrId("toroquest:recruitment_papers")) )
		{
			return true;
		}
		return super.processInteract(player, hand);
	}
//	@Override
//	public boolean processInteract(EntityPlayer player, EnumHand hand)
//	{
//		ItemStack itemstack = player.getHeldItem(hand);
//		Item item = itemstack.getItem();
//		if ( item.equals(Item.getByNameOrId("toroquest:recruitment_papers")) )
//		{
//			return true;
//		}
//		return super.processInitialInteract(player, hand);
//	}
	
	
	@Override
	protected void initEntityAI()
    {              
		this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAITradePlayer(this));
        this.tasks.addTask(1, new EntityAILookAtTradePlayer(this));
        this.tasks.addTask(9, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
        this.tasks.addTask(9, new EntityAIVillagerInteract(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
    }
	
//
//	@Override
//	public boolean processInteract(EntityPlayer player, EnumHand hand)
//	{
//		//
//		ItemStack itemstack = player.getHeldItem(hand);
//        boolean flag = itemstack.getItem() == Items.NAME_TAG;
//
//        if (flag)
//        {
//            itemstack.interactWithEntity(player, this, hand);
//            return true;
//        }
//        
//        
//		if (!flag && isEntityAlive() && !isTrading() && !isChild() && !player.isSneaking())
//		{
//
//			if (!this.world.isRemote)
//			{
//
//				RepData rep = getReputation(player);
//
//				if (rep.rep.equals(ReputationLevel.OUTCAST) || rep.rep.equals(ReputationLevel.ENEMY) || rep.rep.equals(ReputationLevel.VILLAIN))
//				{
//					chat( player, "I will not trade with a filthy " + rep.rep + " like you!" );
//				}
//				else
//				{
//					this.setCustomer(player);
//					player.displayVillagerTradeGui(this);
//				}
//
//			}
//
//			player.addStat(StatList.TALKED_TO_VILLAGER);
//			return true;
//		}
//		else
//		{
//			return super.processInteract(player, hand);
//		}
//	}
//
//	public void setCustomer(EntityPlayer player)
//	{
//		super.setCustomer(player);
//	}
//
//	public EntityPlayer getCustomer() {
//		return super.getCustomer();
//	}
//
//
	
	@Override
	public MerchantRecipeList getRecipes(EntityPlayer player)
	{
		return this.createTradesBaseOnRep(player);
	}
	
	@Override
	protected MerchantRecipeList createTradesBaseOnRep(EntityPlayer player)
	{
		MerchantRecipeList recipeList = new MerchantRecipeList();
		try
		{
			Province province = CivilizationUtil.getProvinceAt( player.world, player.chunkCoordX, player.chunkCoordZ);
			int rep = PlayerCivilizationCapabilityImpl.get(player).getReputation( province.civilization );
			
			if ( province == null || province.civilization == null )
			{
				this.playSound(SoundEvents.ENTITY_VILLAGER_AMBIENT, 1.25F, 1.1F);
				this.canTalk = 3;
				return recipeList;
			}
			else if ( rep <= -50 )
			{
				if ( this.canTalk <= 0 )
				{
					this.reportToGuards(player);
					this.playSound(SoundEvents.ENTITY_VILLAGER_AMBIENT, 1.25F, 1.1F);
					this.canTalk = 1;
				}
				return recipeList;
			}
			
			recipeList = ToroVillagerTrades.trades(player, rep, province.civilization, "shopkeeper", "x" );

			Item item = Item.getByNameOrId(ToroQuestConfiguration.scrollTradeItem);
			int amount = ToroQuestConfiguration.scrollTradeAmount;
			
			if ( item == null || amount < 1 )
			{
				return recipeList;
			}
			
			switch ( province.civilization )
			{
				
				case EARTH:
				{
					ItemScrollEarth scroll = (ItemScrollEarth)Item.getByNameOrId("toroquest:scroll_earth");
					ItemStack itemstack = new ItemStack(scroll,1);
					itemstack.setTagInfo("province", new NBTTagString(province.id.toString()));
					itemstack.setTagInfo("province_name", new NBTTagString(province.name.toString()));
					itemstack.setStackDisplayName("Teleport scroll: " + CivilizationUtil.chatColor(province.civilization) + province.name);
					recipeList.add(new MerchantRecipe(new ItemStack(item ,ToroVillagerTrades.getSellPrice(amount, rep) ),ItemStack.EMPTY,itemstack,0,99999));
					break;
				}
				case FIRE:
				{
					ItemScrollFire scroll = (ItemScrollFire)Item.getByNameOrId("toroquest:scroll_fire");
					ItemStack itemstack = new ItemStack(scroll,1);
					itemstack.setTagInfo("province", new NBTTagString(province.id.toString()));
					itemstack.setTagInfo("province_name", new NBTTagString(province.name.toString()));
					itemstack.setStackDisplayName("Teleport scroll: " + CivilizationUtil.chatColor(province.civilization) + province.name);
					recipeList.add(new MerchantRecipe(new ItemStack(item ,ToroVillagerTrades.getSellPrice(amount, rep) ),ItemStack.EMPTY,itemstack,0,99999));
					break;
				}
				case SUN:
				{
					ItemScrollSun scroll = (ItemScrollSun)Item.getByNameOrId("toroquest:scroll_sun");
					ItemStack itemstack = new ItemStack(scroll,1);
					itemstack.setTagInfo("province", new NBTTagString(province.id.toString()));
					itemstack.setTagInfo("province_name", new NBTTagString(province.name.toString()));
					itemstack.setStackDisplayName("Teleport scroll: " + CivilizationUtil.chatColor(province.civilization) + province.name);
					recipeList.add(new MerchantRecipe(new ItemStack(item ,ToroVillagerTrades.getSellPrice(amount, rep) ),ItemStack.EMPTY,itemstack,0,99999));
					break;
				}
				case WATER:
				{
					ItemScrollWater scroll = (ItemScrollWater)Item.getByNameOrId("toroquest:scroll_water");
					ItemStack itemstack = new ItemStack(scroll,1);
					itemstack.setTagInfo("province", new NBTTagString(province.id.toString()));
					itemstack.setTagInfo("province_name", new NBTTagString(province.name.toString()));
					itemstack.setStackDisplayName("Teleport scroll: " + CivilizationUtil.chatColor(province.civilization) + province.name);
					recipeList.add(new MerchantRecipe(new ItemStack(item ,ToroVillagerTrades.getSellPrice(amount, rep) ),ItemStack.EMPTY,itemstack,0,99999));
					break;
				}
				case MOON:
				{
					ItemScrollMoon scroll = (ItemScrollMoon)Item.getByNameOrId("toroquest:scroll_moon");
					ItemStack itemstack = new ItemStack(scroll,1);
					itemstack.setTagInfo("province", new NBTTagString(province.id.toString()));
					itemstack.setTagInfo("province_name", new NBTTagString(province.name.toString()));
					itemstack.setStackDisplayName("Teleport scroll: " + CivilizationUtil.chatColor(province.civilization) + province.name);
					recipeList.add(new MerchantRecipe(new ItemStack(item ,ToroVillagerTrades.getSellPrice(amount, rep) ),ItemStack.EMPTY,itemstack,0,99999));
					break;
				}
				case WIND:
				{
					ItemScrollWind scroll = (ItemScrollWind)Item.getByNameOrId("toroquest:scroll_wind");
					ItemStack itemstack = new ItemStack(scroll,1);
					itemstack.setTagInfo("province", new NBTTagString(province.id.toString()));
					itemstack.setTagInfo("province_name", new NBTTagString(province.name.toString()));
					itemstack.setStackDisplayName("Teleport scroll:" + CivilizationUtil.chatColor(province.civilization) + province.name);
					recipeList.add(new MerchantRecipe(new ItemStack(item ,ToroVillagerTrades.getSellPrice(amount, rep) ),ItemStack.EMPTY,itemstack,0,99999));
					break;
				}
				default:
				{
					return recipeList;
				}
			}
			return recipeList;
		}
		catch ( Exception e )
		{
			return recipeList;
		}
//		RepData repData = getReputation(player);
//		MerchantRecipeList recipeListDefault = ToroVillagerTrades.trades(player, repData.rep, repData.civ, "shopkeeper", "x" );
//		recipeList.addAll(recipeListDefault);
//		return recipeList;
	};
//
//	/**
//	 * Get the formatted ChatComponent that will be used for the sender's
//	 * username in chat
//	 */
//	public ITextComponent getDisplayName() {
//		TextComponentTranslation textcomponenttranslation = new TextComponentTranslation("entity.toroquest.shopkeeper.name", new Object[0]);
//		textcomponenttranslation.getStyle().setHoverEvent(this.getHoverEvent());
//		textcomponenttranslation.getStyle().setInsertion(this.getCachedUniqueIdString());
//		return textcomponenttranslation;
//	};
//
//	private void chat(EntityPlayer player, String message) {
//		player.sendMessage(new TextComponentString(message));
//	}
//
//	private static class RepData {
//		CivilizationType civ = CivilizationType.EARTH;
//		ReputationLevel rep = ReputationLevel.DRIFTER;
//	}
//
//	private RepData getReputation(EntityPlayer player) {
//		RepData rep = new RepData();
//
//		if (player == null) {
//			return rep;
//		}
//
//		Province province = CivilizationUtil.getProvinceAt(world, chunkCoordX, chunkCoordZ);
//
//		if (province == null) {
//			return rep;
//		}
//
//		rep.civ = province.civilization;
//
//		if (rep.civ == null) {
//			return rep;
//		}
//		rep.rep = ReputationLevel.fromReputation(PlayerCivilizationCapabilityImpl.get(player).getReputation(rep.civ));
//		return rep;
//	}

}