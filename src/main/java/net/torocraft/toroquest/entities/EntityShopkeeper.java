package net.torocraft.toroquest.entities;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookAtTradePlayer;
import net.minecraft.entity.ai.EntityAIMoveIndoors;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITradePlayer;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
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
import net.torocraft.toroquest.entities.ai.EntityAIAvoidEnemies;
import net.torocraft.toroquest.entities.ai.EntityAISmartTempt;
import net.torocraft.toroquest.entities.ai.EntityAIToroVillagerMate;
import net.torocraft.toroquest.entities.trades.ToroVillagerTrades;
import net.torocraft.toroquest.generation.village.util.VillagePieceBlockMap;
import net.torocraft.toroquest.item.ItemScrollEarth;
import net.torocraft.toroquest.item.ItemScrollFire;
import net.torocraft.toroquest.item.ItemScrollMoon;
import net.torocraft.toroquest.item.ItemScrollSun;
import net.torocraft.toroquest.item.ItemScrollWater;
import net.torocraft.toroquest.item.ItemScrollWind;

public class EntityShopkeeper extends EntityToroVillager implements IMerchant
{
	private boolean hasEmeraldBlock = false;
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
	
	
	@Override
	public void onLivingUpdate()
	{
		super.onLivingUpdate();

		if ( this.world.isRemote )
		{
			return;
		}
		
		if ( this.ticksExisted % 100 == 10 )
		{
	        BlockPos pos = this.getPosition().down();
	        
	        if ( pos == null )
	        {
	        	return;
	        }
	        
	        Block b;
	        
	        try
	        {
	        	b = this.getWorld().getBlockState(pos).getBlock();
	        }
	        catch ( Exception e )
	        {
	        	return;
	        }
	        
			if ( b == Blocks.EMERALD_BLOCK )
			{
				if ( this.noShopKeepersNear(pos) )
		        {
		        	this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
		        	this.hasEmeraldBlock = true;
		        	return;
		        }
			}
			else
			{
		        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
		        
		        int x = pos.getX();
		        int y = pos.getY();
		        int z = pos.getZ();

		        for ( int xx = -8; xx <= 8; xx++ )
				{
					for ( int zz = -8; zz <= 8; zz++ )
					{
						for ( int yy = -3; yy <= 3; yy++ )
						{
							pos = new BlockPos(new BlockPos(x+xx,y+yy,z+zz));
							
							if ( b == Blocks.EMERALD_BLOCK )
							{
								if ( this.noShopKeepersNear(pos) )
						        {
									this.getNavigator().tryMoveToXYZ(x+xx+0.5D,y+yy+0.5D,z+zz+0.5D, 0.6D);
									this.hasEmeraldBlock = true;
									return;
								}
							}
						}
					}
				}
			}
			this.hasEmeraldBlock = false;
		}
	}
	
	private boolean noShopKeepersNear( BlockPos pos )
	{
		return this.getEntityWorld().getEntitiesWithinAABB(EntityShopkeeper.class, new AxisAlignedBB(pos).grow(2, 2, 2), new Predicate<EntityShopkeeper>()
		{
			public boolean apply(@Nullable EntityShopkeeper entity)
			{
				return entity != EntityShopkeeper.this;
			}
		}).isEmpty();
	}
	
	public boolean hasEmeraldBlock()
	{
		return this.hasEmeraldBlock;
	}

	@Override
	protected void initEntityAI()
	{              
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIAvoidEnemies(this, 0.5D, 0.65D));
		this.tasks.addTask(1, new EntityAIPanic(this, 0.65D)
		{
			@Override
			public boolean shouldExecute()
		    {
		        if ( isUnderAttack() && this.creature.canEntityBeSeen(underAttack) || isBurning() )
		        {
		            return this.findRandomPosition();
		        }
		        return false;
		    }
			
			@Override
			protected boolean findRandomPosition()
		    {
		        Vec3d vec3d = null;
	
				if ( isUnderAttack() && underAttack.getPositionVector() != null )
				{
			        vec3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.creature, 16, 8, underAttack.getPositionVector());
				}
				else
				{
			        vec3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.creature, 16, 8, this.creature.getPositionVector());
				}
				
		        if ( vec3d == null )
		        {
		            return false;
		        }
		        else
		        {
		            this.randPosX = vec3d.x;
		            this.randPosY = vec3d.y;
		            this.randPosZ = vec3d.z;
		            return true;
		        }
		    }
		});
	    this.tasks.addTask(4, new EntityAITradePlayer(this));
	    this.tasks.addTask(5, new EntityAILookAtTradePlayer(this));
	    this.tasks.addTask(6, new EntityAISmartTempt(this, 0.4D, Items.EMERALD)
	    {
	    	@Override
			public boolean shouldExecute()
		    {
	    		super.shouldExecute();
		        if ( hasEmeraldBlock() || isUnderAttack() || !canTrade() || isBurning() || isMating() )
		        {
			        return false;
		        }
		        return super.shouldExecute();
		    }
		});
	    this.tasks.addTask(7, new EntityAIMoveIndoors(this));
	    this.tasks.addTask(8, new EntityAIRestrictOpenDoor(this));
	    this.tasks.addTask(9, new EntityAIOpenDoor(this, true));
	    this.tasks.addTask(10, new EntityAIMoveTowardsRestriction(this, 0.6D));
	    this.tasks.addTask(11, new EntityAIWanderAvoidWater(this, 0.5D)
	    {
	    	@Override
	    	public boolean shouldExecute()
	        {
	    		if ( hasEmeraldBlock() || isMating() || isTrading() || isUnderAttack() )
	    		{
	    			return false;
	    		}
	    		
	            if ( !this.mustUpdate )
	            {
	                if ( this.entity.getIdleTime() >= 100 )
	                {
	                    return false;
	                }
	
	                if ( !this.entity.isInWater() && !this.entity.isInLava() && this.entity.getRNG().nextInt(this.executionChance) != 0 )
	                {
	                    return false;
	                }
	            }
	
	            Vec3d vec3d = this.getPosition();
	
	            if ( vec3d == null )
	            {
	                return false;
	            }
	            else
	            {
	                this.x = vec3d.x;
	                this.y = vec3d.y;
	                this.z = vec3d.z;
	                this.mustUpdate = false;
	                return true;
	            }
	        }
	    	
	    	@Override
	        protected Vec3d getPosition()
	        {
	    		if ( !this.entity.hasPath() && ( this.entity.isInWater() || this.entity.isInLava() ) )
	            {
	                Vec3d vec3d = RandomPositionGenerator.getLandPos(this.entity, 16, 8);
	                return vec3d == null ? super.getPosition() : vec3d;
	            }
	            else
	            {
	                return this.entity.getRNG().nextFloat() >= this.probability ? RandomPositionGenerator.getLandPos(this.entity, 12, 6) : super.getPosition();
	            }
	        }
	    });
	    this.tasks.addTask(12, new EntityAIWatchClosest(this, EntityLivingBase.class, 8.0F)
	    {
	    	@Override
	        public boolean shouldExecute()
	        {
		        if ( isUnderAttack() || !canTrade() || isBurning() || isMating() || EntityShopkeeper.this.hasPath() )
	    		{
	        		this.closestEntity = null;
	    			return false;
	    		}
	    		if ( getCustomer() != null )
	    		{
	        		this.closestEntity = getCustomer();
	        		return true;
	    		}
	    		this.closestEntity = null;
	    		return false;
	        }
	    });
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
				this.playSound(SoundEvents.ENTITY_VILLAGER_AMBIENT, 1.0F, 1.0F);
				this.canTalk = 2;
				return recipeList;
			}
			else if ( rep <= -50 )
			{
				if ( this.canTalk <= 0 )
				{
					this.reportToGuards(player);
					this.playSound(SoundEvents.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
					this.canTalk = 1;
				}
				return recipeList;
			}
			
			Item item = Item.getByNameOrId(ToroQuestConfiguration.scrollTradeItem);
			
			int amount = ToroQuestConfiguration.scrollTradeAmount;
			
			if ( item != null && amount > 0 )
			{
				switch ( province.civilization )
				{
					case EARTH:
					{
						ItemScrollEarth scroll = (ItemScrollEarth)Item.getByNameOrId("toroquest:scroll_earth");
						ItemStack itemstack = new ItemStack(scroll,1);
						itemstack.setTagInfo("province", new NBTTagString(province.id.toString()));
						itemstack.setTagInfo("province_name", new NBTTagString(province.name.toString()));
						itemstack.setStackDisplayName("Teleport scroll:  " + province.name);
						recipeList.add(new MerchantRecipe(new ItemStack(item ,ToroVillagerTrades.getSellPrice(amount, rep) ),ItemStack.EMPTY,itemstack,0,99999));
						
						if ( ToroQuestConfiguration.bannerTradeAmount > 0 )
						{
							ItemStack banner = VillagePieceBlockMap.getGreenBanner();
							banner.setStackDisplayName(province.civilization.getDisplayName(player) + " Banner");
							recipeList.add(new MerchantRecipe(new ItemStack(Items.EMERALD ,ToroVillagerTrades.getSellPrice(ToroQuestConfiguration.bannerTradeAmount, rep) ),ItemStack.EMPTY,banner,0,99999));
						}
						break;
					}
					case FIRE:
					{
						ItemScrollFire scroll = (ItemScrollFire)Item.getByNameOrId("toroquest:scroll_fire");
						ItemStack itemstack = new ItemStack(scroll,1);
						itemstack.setTagInfo("province", new NBTTagString(province.id.toString()));
						itemstack.setTagInfo("province_name", new NBTTagString(province.name.toString()));
						itemstack.setStackDisplayName("Teleport scroll:  " + province.name);
						recipeList.add(new MerchantRecipe(new ItemStack(item ,ToroVillagerTrades.getSellPrice(amount, rep) ),ItemStack.EMPTY,itemstack,0,99999));
						
						if ( ToroQuestConfiguration.bannerTradeAmount > 0 )
						{
							ItemStack banner = VillagePieceBlockMap.getRedBanner();
							banner.setStackDisplayName(province.civilization.getDisplayName(player) + " Banner");
							recipeList.add(new MerchantRecipe(new ItemStack(Items.EMERALD ,ToroVillagerTrades.getSellPrice(ToroQuestConfiguration.bannerTradeAmount, rep) ),ItemStack.EMPTY,banner,0,99999));
						}
						break;
					}
					case SUN:
					{
						ItemScrollSun scroll = (ItemScrollSun)Item.getByNameOrId("toroquest:scroll_sun");
						ItemStack itemstack = new ItemStack(scroll,1);
						itemstack.setTagInfo("province", new NBTTagString(province.id.toString()));
						itemstack.setTagInfo("province_name", new NBTTagString(province.name.toString()));
						itemstack.setStackDisplayName("Teleport scroll:  " + province.name);
						recipeList.add(new MerchantRecipe(new ItemStack(item ,ToroVillagerTrades.getSellPrice(amount, rep) ),ItemStack.EMPTY,itemstack,0,99999));
						
						if ( ToroQuestConfiguration.bannerTradeAmount > 0 )
						{
							ItemStack banner = VillagePieceBlockMap.getYellowBanner();
							banner.setStackDisplayName(province.civilization.getDisplayName(player) + " Banner");
							recipeList.add(new MerchantRecipe(new ItemStack(Items.EMERALD ,ToroVillagerTrades.getSellPrice(ToroQuestConfiguration.bannerTradeAmount, rep) ),ItemStack.EMPTY,banner,0,99999));
						}
						break;
					}
					case WATER:
					{
						ItemScrollWater scroll = (ItemScrollWater)Item.getByNameOrId("toroquest:scroll_water");
						ItemStack itemstack = new ItemStack(scroll,1);
						itemstack.setTagInfo("province", new NBTTagString(province.id.toString()));
						itemstack.setTagInfo("province_name", new NBTTagString(province.name.toString()));
						itemstack.setStackDisplayName("Teleport scroll:  " + province.name);
						recipeList.add(new MerchantRecipe(new ItemStack(item ,ToroVillagerTrades.getSellPrice(amount, rep) ),ItemStack.EMPTY,itemstack,0,99999));
						
						if ( ToroQuestConfiguration.bannerTradeAmount > 0 )
						{
							ItemStack banner = VillagePieceBlockMap.getBlueBanner();
							banner.setStackDisplayName(province.civilization.getDisplayName(player) + " Banner");
							recipeList.add(new MerchantRecipe(new ItemStack(Items.EMERALD ,ToroVillagerTrades.getSellPrice(ToroQuestConfiguration.bannerTradeAmount, rep) ),ItemStack.EMPTY,banner,0,99999));
						}
						break;
					}
					case MOON:
					{
						ItemScrollMoon scroll = (ItemScrollMoon)Item.getByNameOrId("toroquest:scroll_moon");
						ItemStack itemstack = new ItemStack(scroll,1);
						itemstack.setTagInfo("province", new NBTTagString(province.id.toString()));
						itemstack.setTagInfo("province_name", new NBTTagString(province.name.toString()));
						itemstack.setStackDisplayName("Teleport scroll:  " + province.name);
						recipeList.add(new MerchantRecipe(new ItemStack(item ,ToroVillagerTrades.getSellPrice(amount, rep) ),ItemStack.EMPTY,itemstack,0,99999));
						
						if ( ToroQuestConfiguration.bannerTradeAmount > 0 )
						{
							ItemStack banner = VillagePieceBlockMap.getBlackBanner();
							banner.setStackDisplayName(province.civilization.getDisplayName(player) + " Banner");
							recipeList.add(new MerchantRecipe(new ItemStack(Items.EMERALD ,ToroVillagerTrades.getSellPrice(ToroQuestConfiguration.bannerTradeAmount, rep) ),ItemStack.EMPTY,banner,0,99999));
						}
						break;
					}
					case WIND:
					{
						ItemScrollWind scroll = (ItemScrollWind)Item.getByNameOrId("toroquest:scroll_wind");
						ItemStack itemstack = new ItemStack(scroll,1);
						itemstack.setTagInfo("province", new NBTTagString(province.id.toString()));
						itemstack.setTagInfo("province_name", new NBTTagString(province.name.toString()));
						itemstack.setStackDisplayName("Teleport scroll: " + province.name);
						recipeList.add(new MerchantRecipe(new ItemStack(item ,ToroVillagerTrades.getSellPrice(amount, rep) ),ItemStack.EMPTY,itemstack,0,99999));
						
						if ( ToroQuestConfiguration.bannerTradeAmount > 0 )
						{
							ItemStack banner = VillagePieceBlockMap.getBrownBanner();
							banner.setStackDisplayName(province.civilization.getDisplayName(player) + " Banner");
							recipeList.add(new MerchantRecipe(new ItemStack(Items.EMERALD ,ToroVillagerTrades.getSellPrice(ToroQuestConfiguration.bannerTradeAmount, rep) ),ItemStack.EMPTY,banner,0,99999));
						}
						break;
					}
					default:
					{
						break;
					}
				}
			}
			MerchantRecipeList t = ToroVillagerTrades.trades(this, player, rep, province.civilization, "shopkeeper", "x" );
			for ( MerchantRecipe tt : t)
			{
				recipeList.add(tt);
			}
			return recipeList;
		}
		catch ( Exception e )
		{
			return recipeList;
		}
	}
}