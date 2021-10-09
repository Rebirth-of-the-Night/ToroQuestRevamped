package net.torocraft.toroquest.entities;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.torocraft.toroquest.ToroQuest;
import net.torocraft.toroquest.config.ToroQuestConfiguration;
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

	@Override
	public boolean getAlwaysRenderNameTag()
    {
        return false;
    }
	
    public boolean startRiding(Entity entityIn, boolean force)
    {
    	return false;
    }
    
	@Override
	protected void updateLeashedState()
    {
	   this.clearLeashed(true, true);
       return;
    }
	
	@Override
	public boolean canBeLeashedTo(EntityPlayer player)
    {
		return false;
    }
	
	public static void init(int entityId)
	{
		EntityRegistry.registerModEntity(new ResourceLocation(ToroQuest.MODID, NAME), EntityBanditLord.class, NAME, entityId, ToroQuest.INSTANCE, 80, 1,
				true, 0xffffff, 0x909090);
	}

	@Override
	public int getHorizontalFaceSpeed()
	{
		return 10;
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
		this.setSize(0.95F, 2.7F);
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
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(200.0D * ToroQuestConfiguration.bossHealthMultiplier);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D * ToroQuestConfiguration.bossAttackDamageMultiplier);
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(40.0D);
		this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(10.0D);
		this.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).setBaseValue(10.0D);
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
		this.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
		this.setHeldItem(EnumHand.OFF_HAND, ItemStack.EMPTY);
		this.replaceItemInInventory(100 + EntityEquipmentSlot.HEAD.getIndex(), ItemStack.EMPTY);
		super.onDeath(cause);
	}
	
	@Override
	public void dropLoot()
	{
		if ( !this.world.isRemote )
		{
			this.dropTrophy();
			
			this.weaponMain = new ItemStack(Item.getByNameOrId("spartanweaponry:warhammer_gold"), 1);
    		
    		if ( this.weaponMain.isEmpty() )
    		{
    			this.weaponMain = new ItemStack(Items.GOLDEN_AXE, 1);
    		}

			this.weaponMain.setStackDisplayName("Sledge");
			this.weaponMain.addEnchantment(Enchantment.getEnchantmentByLocation("minecraft:unbreaking"),10);
			
			EntityItem dropItem = new EntityItem(world, posX, posY, posZ, this.weaponMain);
			dropItem.setNoPickupDelay();
			dropItem.motionY = 0.25;
			dropItem.motionZ = 0.0;
			dropItem.motionX = 0.0;
			this.world.spawnEntity(dropItem);
		}
	}
	
	@Override
    public void addEquipment()
    {
		return;
    }
	
	private void dropTrophy()
	{
		if ( !this.world.isRemote )
		{
			ItemStack stack = new ItemStack( Item.getByNameOrId("toroquest:legendary_bandit_helmet"), 1);
			stack.addEnchantment(Enchantment.getEnchantmentByLocation("minecraft:binding_curse"), 1);
			EntityItem dropItem = new EntityItem(world, posX, posY, posZ, stack);
			dropItem.setNoPickupDelay();
			dropItem.motionY = 0.25;
			dropItem.motionZ = 0.0;
			dropItem.motionX = 0.0;
			this.world.spawnEntity(dropItem);
			dropItem.setGlowing(true);
			dropLootItem(Items.EMERALD, 5);
			dropLootItem(Items.EMERALD, 5);
			dropLootItem(Items.EMERALD, 5);
			
			ItemStack head = new ItemStack(Item.getByNameOrId("toroquest:royal_helmet"), 1);
			EntityItem dropHead = new EntityItem(world, posX, posY, posZ, head.copy());
			world.spawnEntity(dropHead);
		}
	}

	private void dropLootItem(Item item, int amount)
	{
		if (amount == 0)
		{
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
    	
    	@Override
    	public void onLivingUpdate()
    	{
    		super.onLivingUpdate();
    		
    		if ( this.world.isRemote )
    		{
    			return;
    		}
    		
           	if ( this.ticksExisted % 25 == 0 )
           	{
           		this.bossInfo.setPercent(this.getHealth()/this.getMaxHealth());
           		
           		if ( this.getHealth() >= this.getMaxHealth() )
    			{
    				if ( !this.inCombat ) 
    				{
    					BlockPos pos = this.getPosition();
    					IBlockState block = world.getBlockState(pos);
    					if ( block instanceof BlockLiquid )
    					{
    				        Vec3d vec3d = RandomPositionGenerator.getLandPos(this, 15, 7);
    				        if ( vec3d != null )
    				        {
    				        	this.getNavigator().tryMoveToXYZ(vec3d.x, vec3d.y, vec3d.z, 0.5);
    				        }
    					}
    				}
    			}
           		else this.heal(ToroQuestConfiguration.bossHealthMultiplier);
           		
           	if ( this.ticksExisted % 100 == 0 )
        	{
           		if ( !this.inCombat )
        		{
        			ItemStack iStack = this.getHeldItemMainhand();
        			
        			if ( this.actionTimer > 0 )
        			{
        				if ( rand.nextBoolean() ) this.actionTimer--;
        			}
        			
        			if ( this.getAttackTarget() == null && this.lastTargetY <= 4 && iStack != null && (iStack.getItem() instanceof ItemBow) ) // SSS
    				{
    					this.resetActiveHand();
    		        	this.playSound(SoundEvents.ITEM_ARMOR_EQUIP_IRON, 1.0F, 0.8F + rand.nextFloat()/5 );
    		        	if ( !this.world.isRemote )
    					{
    		        		setMeleeWeapon();
    					}
    					this.blockingTimer = 0;
    				}
    	    		
        		}
        		else
        		{
    	    		if ( this.getAttackTarget() == null || !this.getAttackTarget().isEntityAlive() )
    	        	{
    	    			this.setAttackTarget( this.getRevengeTarget() );
    	        	}
        		}
        	}
           	}
           	
        	// if has an attacker
           	if ( this.getAttackTarget() != null && this.getAttackTarget().isEntityAlive() && !(this.getAttackTarget() instanceof EntityToroMob)  )
    		{
    			List<EntityArrow> arrows = this.world.getEntitiesWithinAABB(EntityArrow.class, new AxisAlignedBB(this.getPosition()).grow(8, 8, 8), new Predicate<EntityArrow>()
    			{
    				public boolean apply(@Nullable EntityArrow entity)
    				{
    					if ( entity.lastTickPosX == 0 && entity.shootingEntity == getAttackTarget() )
    					{
    						return true;
    					}
    					return false;
    				}
    			});
    			
    			 double dist = this.getDistanceSq(this.getAttackTarget());
    				
    				if ( !arrows.isEmpty() )
    				{
    					this.stance = rand.nextInt(6)+5;
    					if ( dist <= 12 )
    					{
    						this.blockingTimer = 25;
    					}
    					else
    					{
    						this.blockingTimer = 50;
    					}
    					this.blocking = true;
    		        	this.canShieldPush = true;
    					this.resetActiveHand();
    					this.setActiveHand(EnumHand.OFF_HAND);
    					this.updateActiveHand();
    					this.strafeHor = getStrafe(stance);
    					this.strafeVer = 0.4F;
    				}
    				
    		        this.lastTargetY = (int)(Math.abs(this.posY - this.getAttackTarget().posY)+0.5D);
    		        ItemStack iStack = this.getHeldItemMainhand();
    		        if ( !this.inCombat )
    		        {
    					this.getMoveHelper().strafe( 0.0F, 0.0F );
    		        	this.getNavigator().clearPath();
    		        	this.canShieldPush = true;
    					this.resetActiveHand();
    					this.inCombat = true;
    					this.stance = rand.nextInt(6)+5;
    					this.strafeHor = getStrafe(stance);
    		        	if ( this.onGround && this.getNavigator().getPathToEntityLiving(this.getAttackTarget()) == null )
    					{
    		        		this.blockingTimer = -200;
    		        	}
    		        	else
    					{
    						this.getMoveHelper().strafe( 0.0F, 0.0F );
    						this.getNavigator().clearPath();
    					}
    		        }
    		        // if within range and has not been in melee range for a short amount of time, or very close and has not been in melee range for a long amount of time
    				if (  ( ( dist < 200+this.blockingTimer ) || ( this.lastTargetY < 4 && dist <= 20 && this.canEntityBeSeen(this.getAttackTarget())) ) )
    		        {
    					// if this does not have a sword, swap to sword and board
    					if ( iStack != null && ( iStack.getItem() instanceof ItemBow ) )//|| iStack.getItem() == Item.getItemFromBlock(Blocks.TORCH) ) ) // SSS
    					{
    			        	this.canShieldPush = true;
    						this.resetActiveHand();
    						this.playSound(SoundEvents.ITEM_ARMOR_EQUIP_IRON, 1.0F, 0.9F + rand.nextFloat()/10.0F );
    						this.getMoveHelper().strafe( 0.0F, 0.0F );
    		            	this.getNavigator().clearPath();
    		            	if ( !this.world.isRemote )
    						{
    		            		setMeleeWeapon();
    						}
    						this.blockingTimer = 0;
    						this.strafeHor = getStrafe(stance);
    					}
    					
    					// if this is not blocking, is within range, and block is ready, start blocking
    					if ( !this.blocking && dist <= 12 && this.blockingTimer <= -((int)(this.stance*5+dist+20)) && this.getRevengeTarget() != null && this.getRevengeTarget().isEntityAlive() )
    					{
    						this.stance = rand.nextInt(8)+3;
    						this.strafeHor = getStrafe(stance);
    						this.blockingTimer = (int)MathHelper.clamp((rand.nextInt(70)+20-dist), 20, 80);
    						this.blocking = true;
    			        	this.canShieldPush = true;
    						this.resetActiveHand();
    						this.setActiveHand(EnumHand.OFF_HAND);
    						this.updateActiveHand();
    						if ( dist <= 6 )
    						{
    							this.strafeVer = 0.2F;
    						}
    						else
    						{
    							this.strafeVer = 0.4F;
    						}
    					}
    					else if ( this.blocking && this.blockingTimer % 16 == 0 )
    					{
    			        	this.canShieldPush = true;

    						if ( dist <= 3 )
    						{
    							this.strafeVer = 0.2F;
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
    						this.stance = rand.nextInt(8)+3;
    						this.strafeHor = getStrafe(stance);
    			        	this.canShieldPush = true;
    						this.resetActiveHand();
    					}
    					// otherwise, if this is in melee range, strafe
    					else if ( !this.blocking && dist <= 64 )
    					{
    						if ( this.blockingTimer == -12 || this.blockingTimer == -32 || ( this.blockingTimer < -32 && this.blockingTimer % 14 == 0 ) )
    						{
    							if ( rand.nextInt(3) == 0 )
    							{
    								this.stance = rand.nextInt(8)+3;
    								this.strafeHor = getStrafe(stance);
    							}
    						}
    					}
    					
    					if ( !this.blocking )
    					{
    						float strafeMod = 1.0F;
    						
    						if ( this.stance < 5 )
    						{
    							this.strafeVer = 0.4F;

    							if ( dist <= 25 )
    							{
    								if ( !this.world.isRemote && this.onGround )
    								{
    									Vec3d velocityVector = new Vec3d(this.posX - this.getAttackTarget().posX, 0, this.posZ - this.getAttackTarget().posZ);
    									double push = (12.0D+dist*dist);
    									this.addVelocity((velocityVector.x)/push, 0.0D, (velocityVector.z)/push);
    				                	this.velocityChanged = true;
    								}
    							}
    							else
    							{
    								this.stance = rand.nextInt(6)+5;
    								this.strafeHor = getStrafe(stance);
    							}
    						}
    						else if ( dist <= 2.5 )
    						{
    							this.strafeVer = 0.4F;
    						}
    						else if ( dist <= 7 )
    						{
    							this.strafeVer = 0.7F;
    							strafeMod = 0.9F;
    						}
    						else if ( dist <= 12 )
    						{
    							this.strafeVer = 0.8F;
    							strafeMod = 0.8F;
    						}
    						else
    						{
    							this.strafeVer = 0.9F;
    							strafeMod = 0.7F;
    						}
    									
    						if ( this.getNavigator().tryMoveToEntityLiving(this.getAttackTarget(), this.strafeVer) )
    						{						
    							if ( dist >= 12 ) // if this is too far away and blocking, stop blocking faster
    							{
    								this.blockingTimer--;
    							}
    							else if ( dist <= 3 )
    							{
    								if ( !this.world.isRemote && this.onGround && !this.isSprinting() )
    								{
    									Vec3d velocityVector = new Vec3d(this.posX - this.getAttackTarget().posX, 0, this.posZ - this.getAttackTarget().posZ);
    									double push = (1.0D+dist*dist);
    									this.addVelocity((velocityVector.x)/push, 0.0D, (velocityVector.z)/push);
    				                	this.velocityChanged = true;
    								}
    							}
    							
    							if ( this.posY + 1.5D < this.getAttackTarget().posY )
    							{
    								this.getMoveHelper().strafe( this.strafeVer, 0.0F );
    							}
    							else
    							{
    								this.getMoveHelper().strafe( this.strafeVer, this.strafeHor*strafeMod );
    							}
    						}
    						else
    						{
    							if ( this.posY + 1.5D < this.getAttackTarget().posY )
    							{
    								this.getMoveHelper().strafe( this.strafeVer*0.5F, 0.0F );
    							}
    							else
    							{
    								this.getMoveHelper().strafe( this.strafeVer*0.5F, this.strafeHor*0.5F*strafeMod );
    							}
    						}
    					}
    					else // is blocking
    					{
    						if ( this.strafeVer < 0.4F )
    						{
    							if ( !this.world.isRemote && this.onGround )
    							{
    								Vec3d velocityVector = new Vec3d(this.posX - this.getAttackTarget().posX, 0, this.posZ - this.getAttackTarget().posZ);
    								double push = (1.0D+dist*dist);
    								this.addVelocity((velocityVector.x)/push, 0.0D, (velocityVector.z)/push);
    			                	this.velocityChanged = true;
    							}
    						}
    						else if ( this.strafeVer > 0.4F )
    						{
    							this.strafeVer = 0.4F;
    						}
    						
    						if ( this.getNavigator().tryMoveToEntityLiving(this.getAttackTarget(), this.strafeVer) )
    						{
    							this.getMoveHelper().strafe( this.strafeVer, this.strafeHor*1.25F);
    						}
    						else
    						{
    							this.getMoveHelper().strafe( this.strafeVer*0.5F, this.strafeHor*0.5F );
    						}
    					}
    					
    		        }
    				else if ( iStack != null && !(iStack.getItem() instanceof ItemBow) )
    				{
    					this.blocking = false;
    					this.blockingTimer = -200;
    		        	this.canShieldPush = true;
    					this.resetActiveHand();
    					this.playSound(SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 1.0F, 0.9F + rand.nextFloat()/10 );
    					
    					if ( !this.world.isRemote )
    					{
    						this.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Items.BOW, 1));
    						this.setHeldItem(EnumHand.OFF_HAND, ItemStack.EMPTY);
    					}
    					
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
//    				if (this.raidX != null && this.raidZ != null && this.getDistance(this.raidX, this.posY, this.raidZ) > 20)
//    		    	{
//    		    		if ( returnToPost(0) )
//    		    		{
//    		    			this.returnToPost = true;
//    		    		}
//    		    		else
//    		    		{
//    			    		if ( returnToPost(20) )
//    			    		{
//    			    			this.returnToPost = true;
//    			    		}
//    		    		}
//    		    	}
    				this.setAttackTarget(null);
    				//this.searchNextEnemy = true;
    	        	this.canShieldPush = true;
    				this.resetActiveHand();
    				this.activeItemStackUseCount = 0;
    		    	this.strafeHor = 0F;
    		    	this.strafeHor = 0F;
    		    	this.getMoveHelper().strafe( 0.0F, 0.0F );
    		    	this.getNavigator().clearPath();
    			}
    	}
    	
    	public void setMeleeWeapon()
    	{
    		this.weaponMain = new ItemStack(Item.getByNameOrId("spartanweaponry:warhammer_gold"), 1);
    		
    		if ( this.weaponMain.isEmpty() )
    		{
    			this.weaponMain = new ItemStack(Items.GOLDEN_AXE, 1);
    		}

			this.weaponMain.setStackDisplayName("Sledge");
			this.weaponMain.addEnchantment(Enchantment.getEnchantmentByLocation("minecraft:unbreaking"),10);
			this.setHeldItem(EnumHand.MAIN_HAND, this.weaponMain);
			
			this.weaponOff = new ItemStack(Item.getByNameOrId("spartanshields:shield_basic_gold"), 1);
    		
    		if ( this.weaponOff.isEmpty() )
    		{
    			this.weaponOff = new ItemStack(Items.SHIELD, 1);
    		}
    		
    		this.setHeldItem(EnumHand.OFF_HAND, this.weaponOff);
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
		this.setMeleeWeapon();
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
	public boolean processInteract( EntityPlayer player, EnumHand hand )
	{
		return false;
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
}