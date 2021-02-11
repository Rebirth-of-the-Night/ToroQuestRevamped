package net.torocraft.toroquest.entities;

import java.util.Arrays;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveIndoors;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateClimber;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.toroquest.ToroQuest;
import net.torocraft.toroquest.config.ToroQuestConfiguration;
import net.torocraft.toroquest.entities.ai.EntityAIBanditAttack;
import net.torocraft.toroquest.entities.render.RenderBanditLord;

public class EntityBanditLord extends EntitySentry implements IRangedAttackMob, IMob
{

	public static String NAME = "bandit_lord";

	static
	{
		if (ToroQuestConfiguration.specificEntityNames)
		{
			NAME = ToroQuestEntities.ENTITY_PREFIX + NAME;
		}
	}

	public static void init(int entityId)
	{
		EntityRegistry.registerModEntity(new ResourceLocation(ToroQuest.MODID, NAME), EntityBanditLord.class, NAME, entityId, ToroQuest.INSTANCE, 80, 3,
				true, 0xffffff, 0x909090);
	}

	public static void registerRenders()
	{
		RenderingRegistry.registerEntityRenderingHandler(EntityBanditLord.class, new IRenderFactory<EntityBanditLord>()
		{
			@Override
			public RenderBanditLord createRenderFor(RenderManager manager)
			{
				return new RenderBanditLord(manager);
			}
		});
	}

	public EntityBanditLord(World world)
	{
		super(world);
		this.setSize(0.95F, 2.75F);
        this.setCombatTask();
        
		this.experienceValue = 240;
        this.isImmuneToFire = true;
        
    	Arrays.fill(inventoryArmorDropChances, 0.0F);
    	Arrays.fill(inventoryHandsDropChances, 0.0F);
    	
    	this.inCombat = false;
    	this.blocking = false;
    	this.blockingTimer = 0;
    	this.setAttackTarget(null);
    	this.setRevengeTarget(null);
    	this.resetActiveHand();
    	this.setActiveHand(EnumHand.MAIN_HAND);
    	this.activeItemStackUseCount = 0;
    	this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(100.0D);
    	this.strafeVer = 0;
    	this.strafeHor = 0;
    	this.getMoveHelper().strafe( 0.0F, 0.0F );
    	this.getNavigator().clearPath();
    	
    	((PathNavigateGround)this.getNavigator()).setBreakDoors(true);
        this.setCanPickUpLoot(false);
	}
	
	@Override
	public float getEyeHeight()
	{
		return 2.5F;
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
    	this.setLeftHanded(false);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.4D);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(225D * ToroQuestConfiguration.bossHealthMultiplier);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(9D * ToroQuestConfiguration.bossAttackDamageMultiplier);
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64.0D);
		this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(10.0D);
		this.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).setBaseValue(2.0D);
    	this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(100.0D);
	}

	@Override
	protected boolean canDespawn()
	{
		return false;
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount)
	{
    	this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(100.0D);
    	
		if (this.world.isRemote)
		{
			return false;
		}
		
        this.bossInfo.setPercent(this.getHealth()/this.getMaxHealth());
		
		if ( this.isEntityInvulnerable(source) || source == DamageSource.FALL || source == null || source.getTrueSource() == null || !(this.isEntityAlive()) )
		{
			return false;
		}
		
		if ( source.getTrueSource() instanceof EntityToroMob )
		{
			return false;
		}
		
		if ( source.getTrueSource() instanceof EntityLivingBase )
		{
			double dist = source.getTrueSource().getDistanceSq(this.getPosition());
			
			if ( dist > 256 )
			{
				amount = (float)(amount*(256.0f/dist));
			}
			
			if ( !(source.getTrueSource() instanceof EntityPlayer) )
			{
				amount = amount/16.0f;
			}
			
			this.callForHelp((EntityLivingBase)source.getTrueSource());
			
			if ( this.blocking && canBlockDamageSource(source) )
			{
				this.playSound(SoundEvents.ITEM_SHIELD_BLOCK, 1.0F, 1.0F);
				if ( dist <= 16 && !source.isProjectile() && !source.isMagicDamage() && !source.isFireDamage() )
				{
					if ( !this.world.isRemote )
					{
						Vec3d velocityVector = new Vec3d(source.getTrueSource().posX - this.posX, 0, source.getTrueSource().posZ - this.posZ);
						source.getTrueSource().addVelocity((velocityVector.x)/( dist+1 ), 0.3D, (velocityVector.z)/( dist+1 ));
						source.getTrueSource().velocityChanged = true;
					}
				}
				return false;
			}
		}
		return super.attackEntityFrom(source, amount);	
	}
	
	/*
	public boolean attackEntityFromBoss(DamageSource source, float amount)
	{
		if (this.isEntityInvulnerable(source))
	    {
	        return false;
	    }
	    else if ( this.world.isRemote )
	    {
	        return false;
	    }
	    else if ( !(source.getTrueSource() instanceof EntityPlayer) )
	    {
	    	return false;
	    }
	    else
	    {
	        this.idleTime = 0;
	
	        if (this.getHealth() <= 0.0F)
	        {
	            return false;
	        }
	        else
	        {
	            float f = amount;
	            boolean flag = false;
	            boolean flag1 = true;
	
	            if ((float)this.hurtResistantTime > (float)this.maxHurtResistantTime / 2.0F)
	            {
	                if (amount <= this.lastDamage)
	                {
	                    return false;
	                }
	
	                this.damageEntity(source, amount - this.lastDamage);
	                this.lastDamage = amount;
	                flag1 = false;
	            }
	            else
	            {
	                this.lastDamage = amount;
	                this.hurtResistantTime = this.maxHurtResistantTime;
	                this.damageEntity(source, amount);
	                this.maxHurtTime = 10;
	                this.hurtTime = this.maxHurtTime;
	            }
	
	            this.attackedAtYaw = 0.0F;
	            Entity entity1 = source.getTrueSource();
	
	            if (entity1 != null)
	            {
	                if (entity1 instanceof EntityLivingBase)
	                {
	                    this.setRevengeTarget((EntityLivingBase)entity1);
	                }
	
	                if (entity1 instanceof EntityPlayer)
	                {
	                    this.recentlyHit = 100;
	                    this.attackingPlayer = (EntityPlayer)entity1;
	                    System.out.println(this.getHealth());
	                }
	                else
	                {
	    	    		System.out.println("333");
	
	                    return false;
	                }
	            }
	    		System.out.println("99");
	
	            if (flag1)
	            {
	                if (flag)
	                {
	                    this.world.setEntityState(this, (byte)29);
	                }
	                else if (source instanceof EntityDamageSource && ((EntityDamageSource)source).getIsThornsDamage())
	                {
	                    this.world.setEntityState(this, (byte)33);
	                }
	                
	                if (entity1 != null)
	                {
	                    double d1 = entity1.posX - this.posX;
	                    double d0;
	
	                    for (d0 = entity1.posZ - this.posZ; d1 * d1 + d0 * d0 < 1.0E-4D; d0 = (Math.random() - Math.random()) * 0.01D)
	                    {
	                        d1 = (Math.random() - Math.random()) * 0.01D;
	                    }
	
	                    this.attackedAtYaw = (float)(MathHelper.atan2(d0, d1) * (180D / Math.PI) - (double)this.rotationYaw);
	                    this.knockBack(entity1, 0.4F, d1, d0);
	                }
	                else
	                {
	                    this.attackedAtYaw = (float)((int)(Math.random() * 2.0D) * 180);
	                }
	            }
	    		System.out.println("1000");
	
	            if (flag1)
	            {
	                this.playHurtSound(source);
	            }
	
	            boolean flag2 = !flag || amount > 0.0F;
	            
	            if (entity1 instanceof EntityPlayerMP)
	            {
	                CriteriaTriggers.PLAYER_HURT_ENTITY.trigger((EntityPlayerMP)entity1, this, source, f, amount, flag);
	            }
	            return flag2;
	        }
	    }
	}
	*/
	
	@Override
    protected void damageEntity(DamageSource damageSrc, float damageAmount)
    {
		float f1 = this.getHealth();
        this.getCombatTracker().trackDamage(damageSrc, f1, damageAmount);
        this.setHealth(f1 - damageAmount);
    }


	@Override
	public void onDeath(DamageSource cause)
	{
		super.onDeath(cause);
		if (!this.world.isRemote)
		{
			dropLoot();
		}
	}
	
	@Override
	public void dropLoot()
	{
		if ( !this.world.isRemote )
		{
			dropTrophy();
//			dropLootItem(Item.getByNameOrId("toroquest:toro_armor_chestplate"), 1);
//			dropLootItem(Item.getByNameOrId("toroquest:toro_armor_leggins"), 1);
//			dropLootItem(Item.getByNameOrId("toroquest:toro_armor_boots"), 1);
//			dropLootItem(Items.IRON_SWORD, 1);
//			dropLootItem(Items.SHIELD, 1);
		}
	}
	
	private void dropTrophy()
	{
		if ( !this.world.isRemote )
		{
			ItemStack stack = new ItemStack( Item.getByNameOrId("toroquest:legendary_bandit_helmet"), 1);
			stack.addEnchantment(Enchantment.getEnchantmentByLocation("minecraft:binding_curse"), 1);
			//stack.addEnchantment(Enchantment.getEnchantmentByLocation("minecraft:vanishing_curse"), 1);
			EntityItem dropItem = new EntityItem(world, posX, posY, posZ, stack);
			dropItem.setNoPickupDelay();
			dropItem.motionY = 0.25;
			dropItem.motionZ = 0.0;
			dropItem.motionX = 0.0;
			dropItem.setGlowing(false);
			this.world.spawnEntity(dropItem);
			dropItem.setGlowing(false);
		}
	}

	private void dropLootItem(Item item, int amount)
	{
		if (amount == 0) {
			return;
		}

		for (int i = 0; i < amount; i++)
		{
			ItemStack stack = new ItemStack(item);
			EntityItem dropItem = new EntityItem(world, posX, posY, posZ, stack.copy());
			dropItem.setNoPickupDelay();
			dropItem.motionY = rand.nextDouble();
			dropItem.motionZ = rand.nextDouble() - 0.5d;
			dropItem.motionX = rand.nextDouble() - 0.5d;
			this.world.spawnEntity(dropItem);
		}

	}
	
    private final BossInfoServer bossInfo = (BossInfoServer)(new BossInfoServer(this.getDisplayName(), BossInfo.Color.PURPLE, BossInfo.Overlay.PROGRESS)).setDarkenSky(true);

	/**
     * Add the given player to the list of players tracking this entity. For instance, a player may track a boss in
     * order to view its associated boss bar.
     */
    public void addTrackingPlayer(EntityPlayerMP player)
    {
        super.addTrackingPlayer(player);
        this.bossInfo.addPlayer(player);
    }

	    /**
	     * Removes the given player from the list of players tracking this entity. See {@link Entity#addTrackingPlayer} for
	     * more information on tracking.
	     */
	    public void removeTrackingPlayer(EntityPlayerMP player)
	    {
	        super.removeTrackingPlayer(player);
	        this.bossInfo.removePlayer(player);
	    }

    	protected final AIArcher<EntityBanditLord> aiArrowAttack = new AIArcher<EntityBanditLord>(this, 0.4D, 20, 40.0F);
        protected double randPosX;
        protected double randPosY;
        protected double randPosZ;
        protected int stance = (rand.nextInt(6)+5);
    	protected float strafeVer = 0;
    	protected float strafeHor = 0;
    	protected int actionTimer = 0;
    	public float capeAni = 0;
    	public boolean capeAniUp = true;
    	protected boolean blocking = false;
    	protected int blockingTimer = 0;
    	protected int lastTargetY = 0;
    	protected int canSwap = 0;
    	
    	protected void entityInit()
        {
            super.entityInit();
        }
    	
    	// TASKSMETHOD
    	@Override
    	protected void initEntityAI()
    	{
            
    		this.tasks.addTask(1, new EntityAISwimming(this));
    		//door
    		this.tasks.addTask(2, new EntityAIMoveIndoors(this));
            this.tasks.addTask(3, new EntityAIRestrictOpenDoor(this));
            this.tasks.addTask(4, new EntityAIOpenDoor(this, true));
            this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 0.5D));
            // move thru
            this.tasks.addTask(12, new EntityAIWatchClosest(this, EntityLivingBase.class, 8.0F));
            this.tasks.addTask(13, new EntityAILookIdle(this));
    		this.targetTasks.addTask(0, new EntityAIBanditAttack(this));
    		this.targetTasks.addTask(5, new EntityAIHurtByTarget(this, true, new Class[0])
    		{
    			private int revengeTimerOld;

    			@Override
    			public boolean shouldExecute()
    			{
    				int i = this.taskOwner.getRevengeTimer();
    		        EntityLivingBase entitylivingbase = this.taskOwner.getRevengeTarget();
    		        return i != this.revengeTimerOld && entitylivingbase != null && this.isSuitableTarget(entitylivingbase, false) && !(entitylivingbase instanceof EntitySentry);
    			}
    		});
    		targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
    	}

    @Override
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
        this.setCombatTask();
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound)
    {
    	super.writeEntityToNBT(compound);
    }
    
    	//TODO
    
    	@Override
    	public void onLivingUpdate()
    	{
    		super.onLivingUpdate();
    		if ( this.world.isRemote )
    		{
    			return;
    		}

    		this.bossInfo.setPercent(this.getHealth()/this.getMaxHealth());
    		
    		EntityLivingBase attacker = this.getAttackTarget();
    		            
            if (this.ticksExisted % 100 == 0)
    		{
    			this.heal(ToroQuestConfiguration.bossHealthMultiplier);
        		
        		if ( !this.inCombat )
        		{
        			// swap weapons
        			ItemStack iStack = this.getHeldItemMainhand();
        			if ( this.actionTimer > 0 )
        			{
        				if ( rand.nextBoolean() ) this.actionTimer--;
        			}
        			else if ( attacker == null && this.lastTargetY <= 4 && iStack != null && !(iStack.getItem() instanceof ItemSword) )
    				{
    					this.resetActiveHand();
    		        	this.playSound(SoundEvents.ITEM_ARMOR_EQUIP_IRON, 1.0F, 1.0F);
    					this.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Items.IRON_SWORD, 1));
    					this.setHeldItem(EnumHand.OFF_HAND, new ItemStack(Items.SHIELD, 1));
    					this.blockingTimer = 0;
    				}
        		}
        		
        		if ( attacker == null || !attacker.isEntityAlive() )
            	{
            		attacker = this.getRevengeTarget();
            		if ( attacker != null && attacker.isEntityAlive() )
            		{
            			this.setAttackTarget( attacker );
            		}
            	}
        	}
        	
        	// if there is no attacker, attack revenge target
        	
        	
        	// if has an attacker
    		if ( attacker != null && attacker.isEntityAlive() && !(attacker instanceof EntityToroMob) )
    		{
    	        double dist = this.getDistanceSq(attacker);
    	        //double d = this.getDistance(attacker);
    	        boolean canSee = this.canEntityBeSeen(attacker);
    	        this.lastTargetY = (int)Math.abs(this.posY - attacker.posY);
    	        ItemStack iStack = this.getHeldItemMainhand();
    	        if ( !this.inCombat )
    	        {
    				this.getMoveHelper().strafe( 0.0F, 0.0F );
    	        	this.getNavigator().clearPath();
    				this.resetActiveHand();
    				this.inCombat = true;
    				this.strafeVer = 0.8F;
    				this.strafeHor = getStrafe(stance);
    				//this.canSwap = 0;
    	        	//if ( this.getNavigator().getPathToEntityLiving(attacker) == null )
    	        	if ( !this.getNavigator().tryMoveToEntityLiving(attacker, this.strafeVer) )
    				{
    	        		this.blockingTimer = -200;
    	        	}
    	        	else
    	        	{
    	        		//this.blockingTimer = 0;
    	        	}
    	        }
    	        if ( this.canSwap > 0 )
    	        {
    	        	this.canSwap--;
    	        }
    	        // 199,   0 < -201
    	        // if within range and has not been in melee range for a short amount of time, or very close and has not been in melee range for a long amount of time
    			if ( ( (dist < 200+this.blockingTimer && this.blockingTimer > -200 && this.lastTargetY < 6) || (dist <= 16 && this.lastTargetY < 4 && canSee) ) )
    	        {
    				// if this does not have a sword, swap to sword and board
    				if ( iStack != null && !(iStack.getItem() instanceof ItemSword) && this.canSwap <= 0 )
    				{
    					this.resetActiveHand();
    					this.setActiveHand(EnumHand.MAIN_HAND);
    					this.playSound(SoundEvents.ITEM_ARMOR_EQUIP_IRON, 1.0F, 1.0F);
    					this.getMoveHelper().strafe( 0.0F, 0.0F );
    	            	this.getNavigator().clearPath();
    					this.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Items.IRON_SWORD, 1));
    					this.setHeldItem(EnumHand.OFF_HAND, new ItemStack(Items.SHIELD, 1));
    					this.blockingTimer = 0;
    					this.strafeVer = 0.8F;
    					this.strafeHor = getStrafe(stance);
    				}
    				// if this is not blocking, is within range, and block is ready, start blocking
    				if ( !this.blocking && dist <= 12 && this.blockingTimer <= -( (int)(this.stance*8-dist) ))
    				{
    					this.stance = (rand.nextInt(6)+5);
    					this.blockingTimer = (int)(this.stance*8-dist); // (this.stance*6+dist)
    					this.blocking = true;
    					this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(100.0D);
    					this.updateActiveHand();
    					this.setActiveHand(EnumHand.OFF_HAND);
    					this.strafeHor = getStrafe(stance);
    					if ( dist <= 6 )
    					{
    						this.strafeVer = 0.0F;
    					}
    					else
    					{
    						this.strafeVer = 0.4F;
    					}
    				}
    				else if ( this.blocking && this.blockingTimer % 16 == 0 )
    				{
    					if ( dist <= 6 )
    					{
    						this.strafeVer = 0.0F;
    					}
    					else
    					{
    						this.strafeVer = 0.4F;
    					}
    				}
    				// if this is blocking and should no longer block, stop blocking
    				if ( this.blocking && this.blockingTimer <= 0 )
    				{
    					this.blocking = false;
    					this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(100.0D);
    					this.stance = rand.nextInt(6)+5;
    					this.strafeHor = getStrafe(stance);
    					this.strafeVer = 1.0F;
    					this.resetActiveHand();
    				}
    				// otherwise, if this is in melee range, strafe
    				else if ( !this.blocking && this.blockingTimer < 0 && dist <= 48 )
    				{
    					if ( this.blockingTimer == -12 || this.blockingTimer == -32 )
    					{
    						if ( rand.nextInt(3) == 0 )
    						{
    							this.stance = rand.nextInt(6)+5;
    						}
    						this.strafeHor = getStrafe(stance);
    					}
    				}
    				
    				// not blocking
    				if (!this.blocking)
    				{
    					if ( dist <= 6 && this.blockingTimer <= -dist*2 )
    					{
    						this.strafeVer = (float)((2+dist)/10);
    					}
    					else if ( dist >= 48 )
    					{
    						this.strafeVer = 1.0F;
    					}
    					else if ( this.blockingTimer < -16 )
    					{
    						this.strafeVer = 0.8F;
    					}
    					
    					// if the attacker y is bigger than the xz distance
    					if ( dist >= 4 && Math.pow(this.posY - attacker.posY,2) > Math.abs((this.posX - attacker.posX)*(this.posZ - attacker.posZ)) )
    					{
//    					    Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.creature, 16, 16, attacker.getPositionVector());
//
//    					    if (vec3d != null)
//    					    {
//    				            double rPosX = vec3d.x;
//    				            double rPosY = vec3d.y;
//    						    double rPosZ = vec3d.z;
//    					        this.getNavigator().tryMoveToXYZ(rPosX, rPosY, rPosZ, this.strafeVer);
//    					        this.blockingTimer = -200;
//    					    }
    					}
    					else
    					{
    						if ( this.getNavigator().tryMoveToEntityLiving(attacker, this.strafeVer) )
    						{

    						}
    						if ( dist <= 6 )
    						{
    							this.getMoveHelper().strafe( this.strafeVer, this.strafeHor-0.1F );
    						}
    						else
    						{
    							this.getMoveHelper().strafe( this.strafeVer, this.strafeHor );
    						}
    					}
    				}
    				// blocking
    				else
    				{
    					if ( this.strafeVer <= 0.0F )
    					{
    						float push = (float)((2+dist)*10);
    						Vec3d velocityVector = new Vec3d(attacker.posX - this.posX, 0, attacker.posZ - this.posZ);
    				        this.addVelocity(-(velocityVector.x)/push,0,-(velocityVector.z)/push);
    				        this.velocityChanged = true;
    				        this.getNavigator().tryMoveToEntityLiving(attacker, 0.2F);
    						this.getMoveHelper().strafe( 0.4F, this.strafeHor+0.1F );
    					}
    					else
    					{
    						this.getNavigator().tryMoveToEntityLiving(attacker, this.strafeVer);
    						this.getMoveHelper().strafe( this.strafeVer, this.strafeHor+0.1F );
    					}
    				}
    	        }
    			else if ( iStack != null && !(iStack.getItem() instanceof ItemBow) && this.canSwap <= 0 )
    			{
    				this.canSwap = 20;
    				this.blocking = false;
    				this.blockingTimer = -200;
    				this.activeItemStackUseCount = 0;
    		    	this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(100.0D);
    				this.resetActiveHand();
    				this.playSound(SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 1.0F, 1.0F);
    				this.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Items.BOW, 1));
    				this.setHeldItem(EnumHand.OFF_HAND, new ItemStack(Items.AIR));
    				this.strafeVer = 0.0F;
    				this.strafeHor = 0.0F;
    		    	this.getMoveHelper().strafe( 0.0F, 0.0F );
    		    	this.getNavigator().clearPath();
    			}
    			this.blockingTimer--;
    		}
    		else if ( this.blocking || this.inCombat ) // end of combat
    		{
    			this.inCombat = false;
    			this.blocking = false;
    			if ( attacker != null && attacker.isEntityAlive() )
    			{
    				attacker = null;
    				this.setAttackTarget(null);
    				this.setRevengeTarget(null);
    			}
    			//this.blockingTimer = 0;
    			this.resetActiveHand();
    			this.activeItemStackUseCount = 0;
    	    	this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(100.0D);
    	    	this.strafeVer = 0;
    	    	this.strafeHor = 0;
    	    	this.getMoveHelper().strafe( 0.0F, 0.0F );
    	    	this.getNavigator().clearPath();
    		}
    	}
    	
    	public float getStrafe(int stance)
    	{
    		switch ( stance )
    		{
    			case 5:
    			{
    				return -0.2F;
    			}
    			case 6:
    			{
    				return 0.2F;
    			}
    			case 7:
    			{
    				return -0.25F;
    			}
    			case 8:
    			{
    				return 0.25F;
    			}
    			case 9:
    			{
    				return -0.3F;
    			}
    			case 10:
    			{
    				return 0.3F;
    			}
    		}
    		return 0;
    	}
    	
        protected boolean canBlockDamageSource(DamageSource damageSourceIn)
    	{
            if (!damageSourceIn.isUnblockable() && this.isActiveItemStackBlocking())
            {
                Vec3d vec3d = damageSourceIn.getDamageLocation();

                if (vec3d != null)
                {
                    Vec3d vec3d1 = this.getLook(1.0F);
                    Vec3d vec3d2 = vec3d.subtractReverse(new Vec3d(this.posX, this.posY, this.posZ)).normalize();
                    vec3d2 = new Vec3d(vec3d2.x, 0.0D, vec3d2.z);

                    if (vec3d2.dotProduct(vec3d1) < 0.0D)
                    {
                        return true;
                    }
                }
            }

            return false;
        }
    	

    	/**
    	 * Called only once on an entity when first time spawned, via egg, mob
    	 * spawner, natural spawning etc, but not called when entity is reloaded
    	 * from nbt. Mainly used for initializing attributes and inventory
    	 */
    	@Override
    	@Nullable
    	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata)
    	{
    		livingdata = super.onInitialSpawn(difficulty, livingdata);
    		((PathNavigateGround) this.getNavigator()).setBreakDoors(true);
    		this.setCanPickUpLoot(false);
    		this.setLeftHanded(false);
    		this.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Items.IRON_SWORD, 1));
    		this.setHeldItem(EnumHand.OFF_HAND, new ItemStack(Items.SHIELD, 1));
    		this.inCombat = false;
    		this.blocking = false;
    		this.blockingTimer = 0;
    		this.setAttackTarget(null);
    		this.setRevengeTarget(null);
    		this.resetActiveHand();
    		this.setActiveHand(EnumHand.MAIN_HAND);
    		this.activeItemStackUseCount = 0;
        	this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(100.0D);
        	this.strafeVer = 0;
        	this.strafeHor = 0;
        	this.getMoveHelper().strafe( 0.0F, 0.0F );
        	this.getNavigator().clearPath();
        	
    		ItemStack head = new ItemStack(Item.getByNameOrId("toroquest:royal_helmet"), 1);
    		setItemStackToSlot(EntityEquipmentSlot.HEAD, head);

    		return livingdata;
    	}
    	
    	@Override
    	public void setMount()
    	{
    		return;
    	}


    @Override
    public void setItemStackToSlot(EntityEquipmentSlot slotIn, ItemStack stack)
    {
        super.setItemStackToSlot(slotIn, stack);

        if (!this.world.isRemote && slotIn == EntityEquipmentSlot.MAINHAND)
        {
            this.setCombatTask();
        }
    }
    
    
    
    @Override
    protected void updateActiveHand()
    	{
    	    if (this.isHandActive())
    	    {
    	        ItemStack itemstack = this.getHeldItem(this.getActiveHand());
    	        if ( itemstack.getItem() == Items.SHIELD ) // this.blocking
    	        {
    	        	activeItemStackUseCount = 30;
    	        	if (activeItemStackUseCount > 0)
    	        	{
    	        		activeItemStack.getItem().onUsingTick(activeItemStack, this, activeItemStackUseCount);
    	        	}
    		        
    		        if (this.getItemInUseCount() <= 25 && this.getItemInUseCount() % 4 == 0)
    		        {
    		            this.updateItemUse(this.activeItemStack, 5);
    		        }
    		
    		        if (--this.activeItemStackUseCount <= 0 && !this.world.isRemote)
    		        {
    		            this.onItemUseFinish();
    		        }
    	        }
    	        else // NOT blocking
    	        {
    	        	if (itemstack == this.activeItemStack)
    		        {
    		            if (!this.activeItemStack.isEmpty())
    		            {
    		            	activeItemStackUseCount = net.minecraftforge.event.ForgeEventFactory.onItemUseTick(this, activeItemStack, activeItemStackUseCount);
    		                if (activeItemStackUseCount > 0)
    		                {
    		                    activeItemStack.getItem().onUsingTick(activeItemStack, this, activeItemStackUseCount);
    		                }
    		            }
    		
    		            if (this.getItemInUseCount() <= 25 && this.getItemInUseCount() % 4 == 0)
    		            {
    		                this.updateItemUse(this.activeItemStack, 5);
    		            }
    		
    		            if (--this.activeItemStackUseCount <= 0 && !this.world.isRemote)
    		            {
    		                this.onItemUseFinish();
    		            }
    		        }
    		        else
    		        {
    		            this.resetActiveHand();
    		        }
    	        }
    	    }
    	}
    	
    @Override
    protected void updateLeashedState()
        {
    	   this.clearLeashed(true, false);
           return;
        }
    	
    @Override
    public boolean canBeLeashedTo(EntityPlayer player)
    {
    	return false;
    }
    
    	/**
         * Handler for {@link World#setEntityState}
         */
    	@Override
        @SideOnly(Side.CLIENT)
        public void handleStatusUpdate(byte id)
        {
            boolean flag = id == 33;
            boolean flag1 = id == 36;
            boolean flag2 = id == 37;

            if (id != 2 && !flag && !flag1 && !flag2)
            {
                if (id == 3)
                {
                    SoundEvent soundevent1 = this.getDeathSound();

                    if (soundevent1 != null)
                    {
                        this.playSound(soundevent1, this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
                    }

                    this.setHealth(0.0F);
                    this.onDeath(DamageSource.GENERIC);
                }
                else if (id == 30)
                {
                    this.playSound(SoundEvents.ITEM_SHIELD_BREAK, 0.8F, 0.8F + this.world.rand.nextFloat() * 0.4F);
                }
                else if (id == 29)
                {
                    this.playSound(SoundEvents.ITEM_SHIELD_BLOCK, 1.0F, 0.8F + this.world.rand.nextFloat() * 0.4F);
                }
                else
                {
                    super.handleStatusUpdate(id);
                }
            }
            else
            {
                this.limbSwingAmount = 1.5F;
                this.hurtResistantTime = this.maxHurtResistantTime;
                this.maxHurtTime = 10;
                this.hurtTime = this.maxHurtTime;
                this.attackedAtYaw = 0.0F;

                if (flag)
                {
                    this.playSound(SoundEvents.ENCHANT_THORNS_HIT, this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
                }

                DamageSource damagesource;

                if (flag2)
                {
                    damagesource = DamageSource.ON_FIRE;
                }
                else if (flag1)
                {
                    damagesource = DamageSource.DROWN;
                }
                else
                {
                    damagesource = DamageSource.GENERIC;
                }

                SoundEvent soundevent = this.getHurtSound(damagesource);

                if (soundevent != null)
                {
                    this.playSound(soundevent, this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
                }

                this.attackEntityFrom(DamageSource.GENERIC, -1.0F);
            }
        }
    	
        public void setSwingingArms(boolean swingingArms)
        {
            return;
        }

    	@Override
    	public boolean attackEntityAsMob(Entity victim)
    	{
    		if ( victim instanceof EntityToroMob )
    		{
    			setAttackTarget(null);
    			return false;
    		}
    		else
    		{
    			attackTargetEntityWithCurrentItem(victim);
    			return true;
    		}
    	}
    	
    	
    	@Override
    	public void attackTargetEntityWithCurrentItem(Entity targetEntity)
    	{
    		
    		if (targetEntity.canBeAttackedWithItem()) {
    			if (!targetEntity.hitByEntity(this))
    			{
    				this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(9D * ToroQuestConfiguration.bossAttackDamageMultiplier);
    				float attackDamage = (float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
    				float modifierForCreature;

    				if (targetEntity instanceof EntityLivingBase) {
    					modifierForCreature = EnchantmentHelper.getModifierForCreature(this.getHeldItemMainhand(),
    							((EntityLivingBase) targetEntity).getCreatureAttribute());
    				} else {
    					modifierForCreature = EnchantmentHelper.getModifierForCreature(this.getHeldItemMainhand(), EnumCreatureAttribute.UNDEFINED);
    				}

    				if (attackDamage > 0.0F || modifierForCreature > 0.0F) {

    					int i = 0;
    					i = i + EnchantmentHelper.getKnockbackModifier(this);

    					boolean criticalHit = this.fallDistance > 0.0F && !this.onGround && !this.isOnLadder() && !this.isInWater()
    							&& !this.isPotionActive(MobEffects.BLINDNESS) && !this.isRiding() && targetEntity instanceof EntityLivingBase;
    					criticalHit = criticalHit && !this.isSprinting();

    					if (criticalHit) {
    						attackDamage *= 1.5F;
    					}

    					attackDamage = attackDamage + modifierForCreature;
    					boolean swordSweep = false;
    					double d0 = (double) (this.distanceWalkedModified - this.prevDistanceWalkedModified);

    					if (!criticalHit && this.onGround && d0 < (double) this.getAIMoveSpeed()) {
    						ItemStack itemstack = this.getHeldItem(EnumHand.MAIN_HAND);

    						if (itemstack != null && itemstack.getItem() instanceof ItemSword) {
    							swordSweep = true;
    						}
    					}

    					float targetHealth = 0.0F;
    					boolean setFireToTarget = false;
    					int fireAspectModiferOfGuard = EnchantmentHelper.getFireAspectModifier(this);

    					if (targetEntity instanceof EntityLivingBase) {
    						targetHealth = ((EntityLivingBase) targetEntity).getHealth();

    						if (fireAspectModiferOfGuard > 0 && !targetEntity.isBurning()) {
    							setFireToTarget = true;
    							targetEntity.setFire(1);
    						}
    					}

    					double targetMotionX = targetEntity.motionX;
    					double targetMotionY = targetEntity.motionY;
    					double targetMotionZ = targetEntity.motionZ;

    					boolean successfulAttack = targetEntity.attackEntityFrom(DamageSource.causeMobDamage(this), attackDamage);

    					if (successfulAttack) {
    						if (i > 0) {
    							if (targetEntity instanceof EntityLivingBase) {
    								((EntityLivingBase) targetEntity).knockBack(this, (float) i * 0.5F,
    										(double) MathHelper.sin(this.rotationYaw * 0.017453292F),
    										(double) (-MathHelper.cos(this.rotationYaw * 0.017453292F)));
    							} else {
    								targetEntity.addVelocity((double) (-MathHelper.sin(this.rotationYaw * 0.017453292F) * (float) i * 0.5F), 0.1D,
    										(double) (MathHelper.cos(this.rotationYaw * 0.017453292F) * (float) i * 0.5F));
    							}

    							this.motionX *= 0.6D;
    							this.motionZ *= 0.6D;
    							this.setSprinting(false);
    						}

    						if (swordSweep) {
    							for (EntityLivingBase entitylivingbase : this.world.getEntitiesWithinAABB(EntityLivingBase.class,
    									targetEntity.getEntityBoundingBox().grow(1.0D, 0.25D, 1.0D))) {
    								if (entitylivingbase != this && entitylivingbase != targetEntity && !this.isOnSameTeam(entitylivingbase)
    										&& this.getDistanceSq(entitylivingbase) < 9.0D) {
    									entitylivingbase.knockBack(this, 0.4F, (double) MathHelper.sin(this.rotationYaw * 0.017453292F),
    											(double) (-MathHelper.cos(this.rotationYaw * 0.017453292F)));
    									entitylivingbase.attackEntityFrom(DamageSource.causeMobDamage(this), 1.0F);
    								}
    							}

    							world.playSound((EntityPlayer) null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP,
    									this.getSoundCategory(), 1.0F, 1.0F);
    							this.spawnSweepParticles();
    						}

    						if (targetEntity instanceof EntityPlayerMP && targetEntity.velocityChanged) {
    							((EntityPlayerMP) targetEntity).connection.sendPacket(new SPacketEntityVelocity(targetEntity));
    							targetEntity.velocityChanged = false;
    							targetEntity.motionX = targetMotionX;
    							targetEntity.motionY = targetMotionY;
    							targetEntity.motionZ = targetMotionZ;
    						}

    						if (criticalHit) {
    							this.world.playSound((EntityPlayer) null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_CRIT,
    									this.getSoundCategory(), 1.0F, 1.0F);
    							this.onCriticalHit(targetEntity);
    						}

    						if (!criticalHit && !swordSweep) {
    							this.world.playSound((EntityPlayer) null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_STRONG,
    									this.getSoundCategory(), 1.0F, 1.0F);
    						}

    						if (modifierForCreature > 0.0F) {
    							this.onEnchantmentCritical(targetEntity);
    						}

    						if (!world.isRemote && targetEntity instanceof EntityPlayer) {
    							EntityPlayer entityplayer = (EntityPlayer) targetEntity;
    							ItemStack itemstack2 = this.getHeldItemMainhand();
    							ItemStack itemstack3 = entityplayer.isHandActive() ? entityplayer.getActiveItemStack() : null;

    							if (itemstack2 != null && itemstack3 != null && itemstack2.getItem() instanceof ItemAxe
    									&& itemstack3.getItem() == Items.SHIELD) {
    								float f3 = 0.25F + (float) EnchantmentHelper.getEfficiencyModifier(this) * 0.05F;
    								if (this.rand.nextFloat() < f3) {
    									entityplayer.getCooldownTracker().setCooldown(Items.SHIELD, 100);
    									this.world.setEntityState(entityplayer, (byte) 30);
    								}
    							}
    						}

    						this.setLastAttackedEntity(targetEntity);

    						if (targetEntity instanceof EntityLivingBase) {
    							EnchantmentHelper.applyThornEnchantments((EntityLivingBase) targetEntity, this);
    						}

    						EnchantmentHelper.applyArthropodEnchantments(this, targetEntity);
    						ItemStack mainhandItem = this.getHeldItemMainhand();
    						Entity entity = targetEntity;

    						if (mainhandItem != null && entity instanceof EntityLivingBase) {
    							mainhandItem.getItem().hitEntity(mainhandItem, (EntityLivingBase) entity, this);

    							if (mainhandItem.getCount() <= 0) {
    								this.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
    							}
    						}

    						if (targetEntity instanceof EntityLivingBase) {
    							float damageDealt = targetHealth - ((EntityLivingBase) targetEntity).getHealth();

    							if (fireAspectModiferOfGuard > 0) {
    								targetEntity.setFire(fireAspectModiferOfGuard * 4);
    							}

    							if (world instanceof WorldServer && damageDealt > 2.0F) {
    								int k = (int) ((double) damageDealt * 0.5D);
    								((WorldServer) this.world).spawnParticle(EnumParticleTypes.DAMAGE_INDICATOR, targetEntity.posX,
    										targetEntity.posY + (double) (targetEntity.height * 0.5F), targetEntity.posZ, k, 0.1D, 0.0D, 0.1D, 0.2D,
    										new int[0]);
    							}
    						}

    					} else {
    						this.world.playSound((EntityPlayer) null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE,
    								this.getSoundCategory(), 1.0F, 1.0F);

    						if (setFireToTarget) {
    							targetEntity.extinguish();
    						}
    					}
    				}
    			}
    		}
    	}

    	
    	public void onCriticalHit(Entity entityHit)
    	{
    		
    	}

    	public void onEnchantmentCritical(Entity entityHit)
    	{
    		
    	}

    	public void spawnSweepParticles() {
    		double d0 = (double) (-MathHelper.sin(this.rotationYaw * 0.017453292F));
    		double d1 = (double) MathHelper.cos(this.rotationYaw * 0.017453292F);

    		if (this.world instanceof WorldServer) {
    			((WorldServer) this.world).spawnParticle(EnumParticleTypes.SWEEP_ATTACK, this.posX + d0, this.posY + (double) this.height * 0.5D,
    					this.posZ + d1, 0, d0, 0.0D, d1, 0.0D, new int[0]);
    		}
    	}
    	
    	
    	
    	// step sound
    	@Override
    	protected void playStepSound(BlockPos pos, Block blockIn)
        {
    		super.playStepSound(pos, blockIn);
            //this.playSound(SoundEvents.BLOCK_METAL_STEP, 1.0F, 1.0F);
        }
    	
    	@Override
    	@Nullable
        protected SoundEvent getHurtSound(DamageSource damageSourceIn)
        {
    		this.playSound(SoundEvents.BLOCK_METAL_HIT, 1.0F, 1.0F);
    		return super.getHurtSound(damageSourceIn);
        }
    	        
    	@Override
    	public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor)
    	{
    		if ( target == null ) return;
    	    EntityArrow entityarrow0 = this.getArrow(distanceFactor);
    	    EntityArrow entityarrow1 = this.getArrow(distanceFactor);
    	    EntityArrow entityarrow2 = this.getArrow(distanceFactor);
    	    entityarrow0.setIsCritical(true);
    	    entityarrow1.setIsCritical(true);
    	    entityarrow2.setIsCritical(true);
    	    double d0 = target.posX - this.posX;
    	    //double d1 = target.getEntityBoundingBox().minY - entityarrow.posY;
    	    double d1 = target.getEntityBoundingBox().minY + target.height/2.0 - entityarrow0.posY - 1.5 - rand.nextDouble();
    	    double d2 = target.posZ - this.posZ;
    	    double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
    	    entityarrow0.setFire(12);
    	    entityarrow1.setFire(12);
    	    entityarrow2.setFire(12);
    	    this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.5F + 0.8F));
    	    entityarrow0.shoot( d0, d1 + d3 * 0.2D, d2, 2.35F, 4.0F );
    	    this.world.spawnEntity(entityarrow0);
    	    entityarrow1.shoot( d0, d1 + d3 * 0.2D, d2, 2.35F, 4.0F );
    	    this.world.spawnEntity(entityarrow1);
    	    entityarrow2.shoot( d0, d1 + d3 * 0.2D, d2, 2.35F, 4.0F );
    	    this.world.spawnEntity(entityarrow2);
    	}
    	
    	protected EntityArrow getArrow(float p_190726_1_)
    	{
    	    return new EntitySmartArrow(this.world, this);
    	}
    	
//        @Override
//        protected PathNavigate createNavigator(World worldIn)
//        {
//            return new PathNavigateClimber(this, worldIn);
//        }
//
//
//        /**
//         * Called to update the entity's position/logic.
//         */
//        @Override
//        public void onUpdate()
//        {
//            super.onUpdate();
//        }
//        
//        @Override
//        public boolean canBePushed()
//        {
//            return false;
//        }
//        
//        @Override
//        public boolean isOnLadder()
//        {
//            return false;
//        }
//        
//        @Override
//        public boolean isBesideClimbableBlock()
//        {
//            return false;
//        }
//        
//        
//
//        /**
//         * Updates the WatchableObject (Byte) created in entityInit(), setting it to 0x01 if par1 is true or 0x00 if it is
//         * false.
//         */
//        @Override
//        public void setBesideClimbableBlock(boolean climbing)
//        {
//        	
//        }
    
}
