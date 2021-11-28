package net.torocraft.toroquest.entities;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.INpc;
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
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.torocraft.toroquest.ToroQuest;
import net.torocraft.toroquest.civilization.CivilizationHandlers;
import net.torocraft.toroquest.civilization.CivilizationType;
import net.torocraft.toroquest.civilization.CivilizationUtil;
import net.torocraft.toroquest.civilization.Province;
import net.torocraft.toroquest.civilization.player.PlayerCivilizationCapabilityImpl;
import net.torocraft.toroquest.civilization.quests.QuestTradeWithVillagers;
import net.torocraft.toroquest.config.ToroQuestConfiguration;
import net.torocraft.toroquest.entities.ai.EntityAIAvoidEnemies;
import net.torocraft.toroquest.entities.ai.EntityAISmartTempt;
import net.torocraft.toroquest.entities.ai.EntityAIToroVillagerMate;
import net.torocraft.toroquest.entities.trades.ToroVillagerTrades;

@SuppressWarnings("deprecation")
public class EntityToroVillager extends EntityVillager implements INpc, IMerchant
{
	// -------------------------------------------------------------------
	public int canTalk;
	public boolean uiClick = false;
	public static String NAME = "toro_villager";
	public int blockedTrade = 0;
	public EntityLivingBase underAttack = null;
    public BlockPos bedLocation = null;
	public ItemStack treasureMap = null;
	// public Integer maxTrades = null;
	public Integer varient = null;
	public Integer job = null;
	public String jobName = null;
	// -------------------------------------------------------------------
	
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
		        if ( (isUnderAttack() && this.creature.canEntityBeSeen(underAttack)) || isBurning() )
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
        this.tasks.addTask(3, new EntityAIToroVillagerMate(this));
        this.tasks.addTask(4, new EntityAITradePlayer(this));
        this.tasks.addTask(5, new EntityAILookAtTradePlayer(this));
        this.tasks.addTask(6, new EntityAISmartTempt(this, 0.4D, Items.EMERALD)
        {
        	@Override
			public boolean shouldExecute()
		    {
        		super.shouldExecute();
		        if ( isUnderAttack() || !canTrade() || isTrading() || isBurning() || isMating() )
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
        		if ( isMating() || isTrading() || isUnderAttack() )
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
		        if ( isUnderAttack() || !canTrade() || isBurning() || isMating() || EntityToroVillager.this.hasPath() )
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
	public ITextComponent getDisplayName()
    {
		return super.getDisplayName();
    }
	
	@SuppressWarnings("deprecation")
	@Override
	public String getName()
    {
        String s = EntityList.getEntityString(this);

        if (s == null)
        {
            s = "generic";
        }

        return I18n.translateToLocal("entity." + s + ".name");
    }

	@Override
	public boolean processInteract( EntityPlayer player, EnumHand hand )
	{		
		if ( world == null || world.isRemote || player == null || hand == null || !this.isEntityAlive() || this.isTrading() || this.isChild() || this.isMating() || this.isBurning() )
    	{
			return false;
    	}
				
		for ( ItemStack itemStack : player.getArmorInventoryList() )
		{
			if ( itemStack.getItem().equals(Item.getByNameOrId("toroquest:bandit_helmet") ) || itemStack.getItem().equals(Item.getByNameOrId("toroquest:legendary_bandit_helmet") ) )
			{
				if ( this.canTalk < 1 )
				{
					this.playSound(SoundEvents.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
					this.canTalk = 2;
				}
				this.setUnderAttack(player);
				return true;
			}
		}
		
		RepData repData = getReputation(player);
		
		if ( repData == null || repData.civ == null || repData.rep == null )
		{
			if ( this.canTalk < 1 )
			{
				this.playSound(SoundEvents.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
				this.canTalk = 2;
			}
			return true;
		}
		
		// Cheeky way of saving NBT data, career types will not work without this code block!
		if ( this.job == 0 )
		{
			super.getRecipes(player);
	        this.setCustomer(null);
			NBTTagCompound compound = new NBTTagCompound();
	        this.writeEntityToNBT(compound);
		}
		
		ItemStack itemstack = player.getHeldItem(hand);
		Item item = itemstack.getItem();
		
		if ( (this.underAttack == player) || ( repData.rep != null && repData.rep < -50 ) )
		{
			if ( this.canTalk < 1 )
			{
	    		this.callForHelp(player, true);
				this.playSound(SoundEvents.ENTITY_VILLAGER_NO, 1.2F, 0.9F);
				this.canTalk = 2;
			}
			return true;
		}

        if ( ToroQuestConfiguration.recruitVillagers && player.isSneaking() && item.equals(Item.getByNameOrId("toroquest:recruitment_papers")) )
        {
        	if ( repData.rep != null && repData.rep >= 0 && this.canTrade() && !this.isUnderAttack() )
    		{
	        	playSound(SoundEvents.ENTITY_ILLAGER_CAST_SPELL, 1.2F, 1.2F);
	        	playSound(SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, 1.0F, 1.0F);
	        	playSound(SoundEvents.BLOCK_ANVIL_USE, 0.6F, 0.8F);
	        	
	        	player.setHeldItem(hand, new ItemStack(item, itemstack.getCount()-1 ));
	        	
				EntityGuard newEntity = new EntityGuard(world);
				newEntity.setPosition(this.posX, this.posY, this.posZ);
				newEntity.onInitialSpawn(this.world.getDifficultyForLocation(new BlockPos(this.getPosition())), (IEntityLivingData) null);
				newEntity.actionTimer = 1;
				
				this.setDead();
				world.spawnEntity(newEntity);
				
				newEntity.recruitGuard(player, repData.prov, "civvillagerrecruit");
    		}
        	else if ( this.canTalk <= 0 )
			{
    			this.playTameEffect(false);
    			this.playSound(SoundEvents.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
    			this.canTalk = 2;
			}
        	return true;
        }
        
    	
    	if ( this.blockedTrade > 0 )
		{
    		if ( this.canTalk <= 0 )
			{
				this.playSound(SoundEvents.ENTITY_VILLAGER_NO, 1.2F, 0.9F);
				this.canTalk = 1;
			}
    		return true;
		}
		else
		{
			this.getRecipes(player);
			
			if ( this.buyingList == null || this.buyingList.isEmpty() )
			{
				if ( this.canTalk <= 0 )
    			{
    				this.playSound(SoundEvents.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
    				this.canTalk = 1;
    			}
                return true;
			}
			else
            {
                this.setCustomer(player);
                player.displayVillagerTradeGui(this);

    			if ( this.canTalk <= 0 )
    			{
    				this.playSound(SoundEvents.ENTITY_VILLAGER_TRADING, 1.0F, 1.0F);
    				this.canTalk = 1;
    			}
    			return true;
            }
		}
	}
	
	protected void playTameEffect(boolean play)
    {
        EnumParticleTypes enumparticletypes = EnumParticleTypes.HEART;
        
        if (!play)
        {
            enumparticletypes = EnumParticleTypes.SMOKE_NORMAL;
        }

        for (int i = 0; i < 7; ++i)
        {
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;
            this.world.spawnParticle(enumparticletypes, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2);
        }
    }
	
	public void callForHelp( EntityLivingBase attacker, boolean attackThem )
	{
//		if ( this.world.isRemote )
//		{
//			return;
//		}
		
		this.setUnderAttack(attacker);
		
		List<EntityToroVillager> villagers = world.getEntitiesWithinAABB(EntityToroVillager.class, new AxisAlignedBB(getPosition()).grow(12, 8, 12), new Predicate<EntityToroVillager>()
		{
			public boolean apply(@Nullable EntityToroVillager entity)
			{
				return true;
			}
		});

		for (EntityToroVillager villager : villagers)
		{
			if ( villager.canEntityBeSeen(attacker) )
			{
				villager.setUnderAttack(attacker);
			}
		}
		
		List<EntityGuard> guards = world.getEntitiesWithinAABB(EntityGuard.class, new AxisAlignedBB(getPosition()).grow(16, 12, 16), new Predicate<EntityGuard>()
		{
			public boolean apply(@Nullable EntityGuard entity)
			{
				return true;
			}
		});
		
		boolean flag = false;
		
		for (EntityGuard guard : guards)
		{
			if ( guard.getAttackTarget() == null )
			{
				this.getNavigator().tryMoveToEntityLiving(guard, 0.7F);
				
				if ( attacker instanceof EntityPlayer ) 
				{
					guard.setAnnoyed( (EntityPlayer)attacker );
					if ( !flag && guard.actionReady() && guard.getDistance(attacker) <= 8.0D )
					{
						guard.chat((EntityPlayer)attacker, "attackvillager", null);
						flag = true;
					}
				}
				
				if ( guard.isAnnoyed() || attackThem )
				{
					guard.setAttackTarget(attacker);
				}
			}
		}
	}
	
	// guards move to the player
	public void reportToGuards( EntityPlayer player )
	{
		List<EntityToroNpc> guards = world.getEntitiesWithinAABB(EntityToroNpc.class, new AxisAlignedBB(getPosition()).grow(16, 12, 16), new Predicate<EntityToroNpc>()
		{
			public boolean apply(@Nullable EntityToroNpc entity)
			{
				return true;
			}
		});
		Collections.shuffle(guards);
		for (EntityToroNpc guard : guards)
		{
			if ( !guard.inCombat )
			{
				this.getNavigator().tryMoveToEntityLiving(guard, 0.5D);
				guard.getNavigator().tryMoveToEntityLiving(player, 0.5D);
			}
		}
	}
	
	@Override
	public MerchantRecipeList getRecipes(EntityPlayer player)
	{
//		if ( this.buyingList == null )
//      {
        this.buyingList = this.createTradesBaseOnRep(player);
//      }

        return net.minecraftforge.event.ForgeEventFactory.listTradeOffers(this, player, buyingList);
	}
	
    private MerchantRecipeList buyingList;
	boolean hitSafety = false;
	
	@Override
	public void onLivingUpdate()
	{

		super.onLivingUpdate();
		this.uiClick = true;
		
		if ( this.world.isRemote )
		{
			return;
		}
		
//		livingUpdateTimer++;
//		livingUpdateTimer %= 100;
//		if ( livingUpdateTimer == 0 )
		
//		if ( this.world.isRemote )
//		{
//			return;
//		}


		if ( this.ticksExisted % 100 == 0 )
		{
//			if ( world % 1200 == 0 )
//			{
//				if ( this.maxTrades > 0 )
//				{
//					this.maxTrades--;
//				}
//			}
			if ( this.getHealth() >= this.getMaxHealth() )
			{
				if ( this.blockedTrade <= 0 )
				{
					this.hitSafety = true;
				}
			}
			else this.heal(1.0f);
			
			if ( this.canTalk > 0 )
			{	
				this.canTalk--;
			}
			
			if ( this.blockedTrade > 0 )
    		{
    			this.blockedTrade--;
    		}
			
			if ( this.blockedTrade <= 0 )
			{
				this.underAttack = null;
			}
		}
	}

	// Create trades from toroquest config
	protected MerchantRecipeList createTradesBaseOnRep(EntityPlayer player)
	{
		RepData repData = getReputation(player);
		
		if ( repData == null || repData.civ == null || repData.rep == null || repData.rep < -50 )
		{
			return new MerchantRecipeList();
		}
		
        // this.getProfessionForge().getCareer(this.careerId).getName()
		// System.out.println( "job name: " + this.jobName + " " + this.varient + " " + this.maxTrades);
		
		return ToroVillagerTrades.trades(this, player, repData.rep, repData.civ, this.jobName, ""+this.varient );
	}
	
	@Override
	public boolean canBeLeashedTo(EntityPlayer player)
	{
		this.callForHelp( player, false );
		if ( !this.getLeashed() )
		{
			RepData repData = getReputation(player);
			if ( repData != null )
			{
				CivilizationHandlers.adjustPlayerRep(player, repData.civ, -ToroQuestConfiguration.leashVillagerRepLoss);
			}
		}
		return true;
	}
	
	public EntityToroVillager(World worldIn)
	{
		super(worldIn);
		this.stepHeight = 1.05F;
//        NBTTagCompound compound = new NBTTagCompound();
//        this.writeEntityToNBT(compound);
	}
	
	public EntityToroVillager(World worldIn, int professionId )
	{
		super(worldIn, professionId);
		this.stepHeight = 1.05F;
//        NBTTagCompound compound = new NBTTagCompound();
//        this.writeEntityToNBT(compound);
	}
    
    @Override
    public void readEntityFromNBT(NBTTagCompound compound)
    {
    	super.readEntityFromNBT(compound);
		//this.maxTrades = compound.getInteger("MaxTrades");
        this.varient = compound.getInteger("Varient");
        this.job = compound.getInteger("Career");
        this.jobName = compound.getString("JobName");
    }
    
    @Override
    public void writeEntityToNBT(NBTTagCompound compound)
    {
    	if ( this.varient == null )
        {
        	this.varient = rand.nextInt(ToroQuestConfiguration.villagerUniqueShopInventoryVarients+1);
        }
        compound.setInteger("Varient", this.varient);
        
//      if ( this.maxTrades == null )
//      {
//          this.maxTrades = 0;
//      }
//      compound.setInteger("MaxTrades", this.maxTrades);
        
        this.job = compound.getInteger("Career");
        {
        	this.jobName = this.getProfessionForge().getCareer(this.job-1).getName();
        }
        compound.setString("JobName", this.jobName);
        
    	super.writeEntityToNBT(compound);
    }
    
	// =========================== REPUTATION ============================
	static class RepData
	{
		private CivilizationType civ;
		private Province prov;
		private Integer rep;
	}

	protected RepData getReputation(EntityPlayer player)
	{
		if ( player == null )
		{
			return null;
		}
		
		RepData repData = new RepData();
		
		Province province = CivilizationUtil.getProvinceAt( player.world, player.chunkCoordX, player.chunkCoordZ);

		if ( province == null || province.getCiv() == null )
		{
			return null;
		}
		
		repData.civ = province.getCiv();
		repData.prov = province;
		repData.rep = PlayerCivilizationCapabilityImpl.get(player).getReputation( repData.civ );
				
		return repData;
	}
	// ===================================================================

	// ============================ ATTACKED =============================
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		if ( this.world.isRemote )
        {
            return false;
        }
		
		if ( source.getTrueSource() == null )
		{
	        Vec3d vec3d = RandomPositionGenerator.getLandPos(this, 8, 4);
            if ( vec3d != null )
            {
		        this.getNavigator().tryMoveToXYZ(vec3d.x, vec3d.y, vec3d.z, 0.5D);
            }
		}
		else if ( source.getTrueSource() instanceof EntityLivingBase )
		{
			if ( source.getTrueSource() instanceof EntityToroNpc )
			{
				amount = 0.0F;
				return false;
			}
			
			EntityLivingBase e = (EntityLivingBase)source.getTrueSource();
			
			if ( e instanceof EntityPlayer )
			{
				if ( this.hitSafety )
				{
					this.hitSafety = false;
					this.playSound(SoundEvents.BLOCK_CLOTH_BREAK, 1.0F, 1.0F);
					amount = 0.0F;
					return false;
				}
				
				List<EntityLivingBase> enemies = e.getEntityWorld().getEntitiesWithinAABB(EntityGuard.class, new AxisAlignedBB(this.getPosition()).grow(16, 12, 16), new Predicate<EntityLivingBase>()
				{
					public boolean apply(@Nullable EntityLivingBase entity)
					{
						if ( entity instanceof IMob || entity instanceof EntityMob )
						{
							return true;
						}
						else
						{
							return false;
						}
					}
				});
				
				if ( !enemies.isEmpty() )
				{
					amount = 0.0F;
					return false;
				}
			}
			this.callForHelp(e, true);
		}
		
		return super.attackEntityFrom(source, amount);
	}
	// ===================================================================

	// ============================= TRADING ==============================
	@Override
	public void useRecipe(MerchantRecipe recipe)
    {
        this.livingSoundTime = -this.getTalkInterval();
        
        if ( this.canTalk <= 0 )
        {
        	this.canTalk = 1;
        	if (rand.nextBoolean()) this.playSound(SoundEvents.ENTITY_VILLAGER_YES, 1.0F, 1.0F);
        }
        
        if ( this.uiClick ) 
    	{
    		this.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);
    		this.uiClick = false;
    	}
    	
        try
        {
        	QuestTradeWithVillagers.INSTANCE.onTrade(this.getCustomer());
        }
        catch(Exception e)
        {
        	
        }
    }
	// ===================================================================
	
	// ========================== UNDER ATTACK ===========================
	public boolean isUnderAttack()
	{
		return this.underAttack != null && this.underAttack.isEntityAlive();
	}
			
	public boolean canTrade()
	{
		return this.blockedTrade < 1;
	}
	
	public void blockTrade()
	{
		if ( this.blockedTrade < 8 )
		{
			this.blockedTrade += 4;
		}
	}
	
	public void setUnderAttack( EntityLivingBase entity )
	{
		if ( entity instanceof EntityPlayer )
		{
			this.underAttack = (EntityPlayer)entity;
			if ( this.blockedTrade < 16 )
			{
				this.blockedTrade += 4;
			}
		}
		else
		{
			this.underAttack = null;
			if ( this.blockedTrade < 8 )
			{
				this.blockedTrade += 2;
			}
		}
	}
	
	public void setMurder( EntityPlayer player )
	{
		this.underAttack = player;
		this.blockedTrade = 64;
	}
	// ===================================================================
	
	// ============================= MATING ==============================
	@Override
	public boolean getIsWillingToMate(boolean updateFirst)
    {
        if ( updateFirst )
        {
        	int s = 8;
        	int x = (int)(this.posX+0.5D);
        	int y = (int)(this.posY+0.5D);
        	int z = (int)(this.posZ+0.5D);
			
			for ( int xx = x-s; x+s >= xx; xx++ )
			{
				for ( int yy = y-s; y+s >= yy; yy++ )
				{
					for ( int zz = z-s; z+s >= zz; zz++ )
					{
						Block bed = this.world.getBlockState((new BlockPos(xx, yy, zz))).getBlock();
    					if ( bed instanceof BlockBed )
    					{
    						//System.out.println(bed);
    						this.bedLocation = new BlockPos(xx, yy, zz);
    				        return this.bedLocation != null ? true: false;
    					}
					}
				}
			}
        }
        return this.bedLocation != null ? true: false;
    }
	
	@Override
	public void setIsWillingToMate(boolean isWillingToMate)
    {
        if ( isWillingToMate )
        {
        	this.getIsWillingToMate(true);
        }
        else
        {
        	this.bedLocation = null;
        }
    }
	
	@SuppressWarnings("deprecation")
	@Override
	public EntityVillager createChild(EntityAgeable ageable)
    {
		EntityVillager villager = new EntityVillager(null);
		villager.setDead();
        EntityToroVillager entityvillager = new EntityToroVillager( this.world, villager.getProfession() );
        entityvillager.onInitialSpawn(this.world.getDifficultyForLocation(new BlockPos(entityvillager)), (IEntityLivingData)null);
        return entityvillager;
    }
	// ===================================================================
	
	// =============================== MISC ==============================
	@Override
	public int getHorizontalFaceSpeed()
	{
		return 10;
	}
	
	@Override
	protected boolean canDespawn()
	{
		return false;
	}
	
	static
	{
		if (ToroQuestConfiguration.specificEntityNames)
		{
			NAME = ToroQuestEntities.ENTITY_PREFIX + NAME;
		}
	}
	
	public static void init(int entityId)
	{
		EntityRegistry.registerModEntity(new ResourceLocation(ToroQuest.MODID, NAME), EntityToroVillager.class, NAME, entityId, ToroQuest.INSTANCE, 80, 3,
				true, 0x000000, 0xe0d6b9);
	}
	
	@Nullable
	@Override
	protected SoundEvent getAmbientSound()
    {
        if ( this.isTrading() ) 
        {
        	return SoundEvents.ENTITY_VILLAGER_TRADING;
        }
        else if ( rand.nextBoolean() )
        {
        	return SoundEvents.ENTITY_VILLAGER_AMBIENT;
        }
        else
        {
        	return null;
        }
    }
	
	@Override
	public boolean attackEntityAsMob(Entity victim)
	{
		this.setAttackTarget(null);
		return false;
	}
	// ===================================================================
}