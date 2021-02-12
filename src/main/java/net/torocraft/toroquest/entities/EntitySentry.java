       package net.torocraft.toroquest.entities;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.BlockFire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveIndoors;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
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
import net.torocraft.toroquest.entities.ai.EntityAIBreakDoorBandit;
import net.torocraft.toroquest.entities.ai.EntityAIDespawn;
import net.torocraft.toroquest.entities.ai.EntityAIRaid;
import net.torocraft.toroquest.entities.render.RenderSentry;

public class EntitySentry extends EntityToroMob implements IRangedAttackMob, IMob
{

	// sentry type ----------------------------------
	
	protected final int sentryMain = (rand.nextInt(2)); // 0 == sword, 1 == axe
	protected final int sentryOff = (rand.nextInt(3)); // 0 == none, 1 == sword, 2 == axe
	
	protected ItemStack weaponMain = new ItemStack(Items.AIR, 1);
	protected ItemStack weaponOff = new ItemStack(Items.AIR, 1);
	
	protected final AIArcher<EntitySentry> aiArrowAttack = new AIArcher<EntitySentry>(this, 0.4D, 40, 40.0F);
    protected EntityCreature creature;
    protected boolean inCombat = false;
    protected double randPosX;
    protected double randPosY;
    protected double randPosZ;
    public int despawnTimer = 100;
    //private EntityPlayer attackedByPlayer;
    
    protected int stance = (rand.nextInt(6)+5);
	protected float strafeVer = 0;
	protected float strafeHor = 0;
    
//    protected final EntityAIBreakDoorBandit breakDoor = new EntityAIBreakDoorBandit(this);
//    
//    protected boolean isBreakDoorsTaskSet;
//    protected boolean isBreakDoorsTaskSet()
//    {
//        return this.isBreakDoorsTaskSet;
//    }
    /**
     * Sets or removes EntityAIBreakDoorBandit task
     */
//    public void setBreakDoorsAItask(boolean enabled)
//    {
//        if (this.isBreakDoorsTaskSet != enabled)
//        {
//            this.isBreakDoorsTaskSet = enabled;
//            ((PathNavigateGround)this.getNavigator()).setBreakDoors(enabled);
//
//            if (enabled)
//            {
//                this.tasks.addTask(9, this.breakDoor);
//            }
//            else
//            {
//                //this.tasks.removeTask(this.breakDoor);
//            }
//        }
//    }
	public float capeAni = 0;
	public boolean capeAniUp = true;
	protected boolean blocking = false;
	protected int blockingTimer = 0;

	public static String NAME = "sentry";
	static
	{
		if (ToroQuestConfiguration.specificEntityNames)
		{
			NAME = ToroQuestEntities.ENTITY_PREFIX + NAME;
		}
	}
	public static void init(int entityId)
	{
		EntityRegistry.registerModEntity(new ResourceLocation(ToroQuest.MODID, NAME), EntitySentry.class, NAME, entityId, ToroQuest.INSTANCE, 80, 2,
				true, 0x8f3026, 0xe0d359);
	}

	public static void registerRenders()
	{
		RenderingRegistry.registerEntityRenderingHandler(EntitySentry.class, new IRenderFactory<EntitySentry>()
		{
			@Override
			public Render<EntitySentry> createRenderFor(RenderManager manager)
			{
				return new RenderSentry(manager);
			}
		});
	}
	
	public EntitySentry(World worldIn)
	{
		super(worldIn);

        this.setSize(0.6F, 1.93F);
        this.setCombatTask();
        
		Arrays.fill(inventoryArmorDropChances, ToroQuestConfiguration.banditArmorDropChance);
		Arrays.fill(inventoryHandsDropChances, ToroQuestConfiguration.banditHandsDropChance);
		
		this.inCombat = false;
		this.blocking = false;
		this.blockingTimer = 0;
		this.setAttackTarget(null);
		this.setRevengeTarget(null);
		this.resetActiveHand();
		this.setActiveHand(EnumHand.MAIN_HAND);
		this.activeItemStackUseCount = 0;
    	this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.0D);
    	this.strafeVer = 0;
    	this.strafeHor = 0;
    	this.getMoveHelper().strafe( 0.0F, 0.0F );
    	this.getNavigator().clearPath();
    	
    	((PathNavigateGround)this.getNavigator()).setBreakDoors(true);
	    this.setCanPickUpLoot(false);
	}
	
	@Override
	public void onDeath(DamageSource cause)
	{
		this.replaceItemInInventory(100 + EntityEquipmentSlot.HEAD.getIndex(), ItemStack.EMPTY);
		super.onDeath(cause);
		dropLoot();
	}
	
	protected ResourceLocation banditSkin = new ResourceLocation(ToroQuest.MODID + ":textures/entity/bandit/bandit_" + rand.nextInt(ToroQuestConfiguration.banditSkins) + ".png");
	
	public ResourceLocation getSkin()
	{
		return this.banditSkin;
	}
	
	public double renderSize = 0.95D + rand.nextDouble()/16;
	
	public double getRenderSize()
	{
		return this.renderSize;
	}

	public void dropLoot()
	{
		if (!world.isRemote)
		{
			if ( ToroQuestConfiguration.banditsDropMasks )
			{
				dropMask();
			}
			if ( ToroQuestConfiguration.banditsDropEmeralds && rand.nextInt(3) == 0 )
			{
				ItemStack stack = new ItemStack(Items.EMERALD, rand.nextInt(3)+1);
				EntityItem dropItem = new EntityItem(world, posX, posY, posZ, stack.copy());
				world.spawnEntity(dropItem);
			}
		}
	}
	
	protected void dropMask()
	{
		ItemStack stack = new ItemStack(Item.getByNameOrId("toroquest:bandit_helmet"));
		EntityItem dropItem = new EntityItem(world, posX, posY, posZ, stack.copy());
		world.spawnEntity(dropItem);
	}
	
	@Override
    protected void applyEntityAttributes()
    {
		super.applyEntityAttributes();
    	this.setLeftHanded(false);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(ToroQuestConfiguration.banditHealth);
	    this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D);
	    this.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).setBaseValue(1.0D);
    	this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(ToroQuestConfiguration.banditArmor);
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
    	this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.4D);
    	this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.0D);
    }
	
	protected void initEntityAI()
	{
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(2, new EntityAIRangedFlee(this, 0.8D));
		this.tasks.addTask(3, new EntityAIFlee(this, 0.8D));
		// door
        //this.tasks.addTask(7, new EntityAIBreakDoorBandit(this)); //this.tasks.addTask(8, new EntityAIOpenDoor(this, true));
		this.tasks.addTask(8, new EntityAIMoveIndoors(this));
        this.tasks.addTask(9, new EntityAIRestrictOpenDoor(this));
        this.tasks.addTask(10, new EntityAIOpenDoor(this, false));
        this.tasks.addTask(11, new EntityAIMoveTowardsRestriction(this, 0.5D));
        // ----
	    this.tasks.addTask(13, new EntityAIMoveThroughVillage(this, 0.6D, false));
	    this.tasks.addTask(14, new EntityAIWanderAvoidWater(this, 0.5D)
	    {
	    	@Nullable
    	    protected Vec3d getPosition()
    	    {
    	        if (this.entity.isInWater())
    	        {
    	            Vec3d vec3d = RandomPositionGenerator.getLandPos(this.entity, 15, 7);
    	            return vec3d == null ? super.getPosition() : vec3d;
    	        }
    	        else if ( this.entity.isRiding() )
    	        {
    	            return null;
    	        }
    	        else
    	        {
    	            return this.entity.getRNG().nextFloat() >= this.probability ? RandomPositionGenerator.getLandPos(this.entity, 10, 7) : super.getPosition();
    	        }
    	    }
		});
        this.tasks.addTask(15, new EntityAIWatchClosest(this, EntityLivingBase.class, 8.0F));
        this.tasks.addTask(16, new EntityAILookIdle(this));
        
        // checksight // onlynearby
		this.targetTasks.addTask(0, new EntityAIBanditAttack(this));
//        this.targetTasks.addTask(0, new EntityAINearestAttackableTarget<EntityToroNpc>(this, EntityToroNpc.class, true, false));
//        this.targetTasks.addTask(1, new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, true, false));
//        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<EntityToroVillager>(this, EntityToroVillager.class, false, false));
        //this.targetTasks.addTask(1, new EntityAINearestAttackableTarget<EntityVillager>(this, EntityVillager.class, 500, false, false, null));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0])
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
	}
	
	@Override
	protected void entityInit()
	{
		super.entityInit();
        this.dataManager.register(CLIMBING, Byte.valueOf((byte)0));
        // this.dataManager.register(SWINGING_ARMS, Boolean.valueOf(false));
	}
	
	// =======================================================================================================================================
	@Override
	public void onLivingUpdate()
	{
		super.onLivingUpdate();
		
//		if ( !this.isEntityAlive() )
//		{
//			this.setDead();
//			return;
//		}
		
//		if ( this.world.isRemote )
//		{
//			return;
//		}
		
		if ( this.world.isRemote )
		{
			return;
		}
		
		if ( this.ticksExisted % 100 == 0 )
    	{
    		this.heal(0.5f);
    		
    		if ( this.despawnTimer < 100 && --this.despawnTimer <= 0 )
    		{
    			List<EntityPlayer> nearbyPlayers = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(this.getPosition()).grow(32, 16, 32));
				if ( nearbyPlayers.isEmpty() )
				{
	    			this.setHealth(0);
	    			this.setDead();
	    			return;
				}
    		}
    		
    	}
		
		ItemStack iStackM = this.getHeldItemMainhand();
		ItemStack iStackO = this.getHeldItemOffhand();
		
		if ( iStackO.getItem() instanceof ItemShield )
		{
			sentryTypeTank( );
    		return;
		}
		else if ( iStackM.getItem() instanceof ItemBow )
		{
			sentryTypeRanged( );
    		return;
		}
		else
		{
			sentryTypeDPS( );
    		return;
		}
	}
	
	//==================================================== blocking ===========================================================
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		if ( this.world.isRemote ) return false;

		Entity e = source.getTrueSource();
		
		if ( source == DamageSource.IN_WALL || source == DamageSource.CRAMMING ) 
		{
			return false;
		}
		
		if ( source == DamageSource.FALL )
		{
			amount = amount/6;
			if ( amount <= 1 )
			{
				return false;
			}
			else
			{
				super.attackEntityFrom(source, amount);
			}
		}
		
		
		if ( e == null )
		{
			if ( this.rand.nextBoolean() )
			{
				BlockPos pos = this.getPosition();
				IBlockState block = world.getBlockState(pos);
				if ( block == Blocks.LAVA.getDefaultState() || block.getBlock() == Blocks.LAVA )
				{
					if ( this.dimension == 0 )
					{
						this.swingArm(EnumHand.MAIN_HAND);
						if ( this.world.isRemote )
						{
							this.addVelocity(0, 0.25, 0);
						}
						this.world.setBlockState(pos, Blocks.WATER.getDefaultState());
					}
			        Vec3d vec3d = RandomPositionGenerator.getLandPos(this, 8, 4);
		            if ( vec3d != null )
		            {
				        this.getNavigator().tryMoveToXYZ(vec3d.x, vec3d.y, vec3d.z, 0.5D);
		            }
				}
				else if ( block.getBlock() instanceof BlockFire )
				{
					this.swingArm(EnumHand.MAIN_HAND);
					this.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 1.0F, 1.0F);
					world.setBlockToAir(pos);
				}
				else if ( this.rand.nextBoolean() && !this.getNavigator().noPath() )
				{
					this.extinguish();
				}
				else
				{
					Vec3d vec3d = RandomPositionGenerator.getLandPos(this, 8, 4);
		            if ( vec3d != null )
		            {
				        this.getNavigator().tryMoveToXYZ(vec3d.x, vec3d.y, vec3d.z, 0.5D);
		            }
				}
			}
			return super.attackEntityFrom(source, amount);
		}
		
		if ( e instanceof EntityToroMob ) 
		{
			return false;
		}
		
		if ( e instanceof EntityLivingBase ) 
		{
			if ( rand.nextBoolean() )
			{
				this.setAttackTarget((EntityLivingBase)e);
			}
			this.setRevengeTarget((EntityLivingBase)e);
			this.callForHelp((EntityLivingBase)e);
		}
		
		if ( this.blocking && canBlockDamageSource(source) )
		{
			double dist = e.getDistanceSq(this);
			if ( dist <= 9 && !source.isProjectile() && !source.isMagicDamage() && !source.isFireDamage() )
			{
				this.playSound(SoundEvents.ITEM_SHIELD_BLOCK, 1.0F, 0.8F + rand.nextFloat()/5);
				if ( e instanceof EntityLivingBase )
				{
					if ( ((EntityLivingBase)e).getHeldItemMainhand().getItem() instanceof ItemAxe )
					{
						this.resetActiveHand();
						this.playSound(SoundEvents.ITEM_SHIELD_BREAK, 1.0F, 0.8F + rand.nextFloat()/5);
						if ( !this.world.isRemote )
						{
							Vec3d velocityVector = new Vec3d(e.posX - this.posX, 0, e.posZ - this.posZ);
							this.addVelocity(-(velocityVector.x)/( dist+1 )*1.16D, 0.16D, -(velocityVector.z)/( dist+1 )*1.16D);
							this.velocityChanged = true;
						}
						return false;
					}
				}
				if ( !this.world.isRemote )
				{
					Vec3d velocityVector = new Vec3d(e.posX - this.posX, 0, e.posZ - this.posZ);
					e.addVelocity((velocityVector.x)/( dist+1 )*1.16D, 0.16D, (velocityVector.z)/( dist+1 )*1.16D);

	                e.velocityChanged = true;
				}
			}
			return false;
		}
		if (super.attackEntityFrom(source, amount))
		{
			return true;
		}
		return false;
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
                    this.playSound(soundevent1, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
                }

                this.setHealth(0.0F);
                this.onDeath(DamageSource.GENERIC);
            }
            else if (id == 30)
            {
                this.playSound(SoundEvents.ITEM_SHIELD_BREAK, 0.8F, 0.8F + this.world.rand.nextFloat() * 0.5F);
            }
            else if (id == 29)
            {
                this.playSound(SoundEvents.ITEM_SHIELD_BLOCK, 1.0F, 0.8F + this.world.rand.nextFloat() * 0.5F);
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
                this.playSound(SoundEvents.ENCHANT_THORNS_HIT, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
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
                //this.playSound(soundevent, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            }

            this.attackEntityFrom(DamageSource.GENERIC, -1.0F);
        }
    }
    //=============================================================================================================================

	
	public void onUpdate_OFF()
	{
		super.onUpdate();

		// this.renderOffsetY = 0.0F;
		//super.onUpdate();
		this.prevLimbSwingAmount = this.limbSwingAmount;
		double d0 = this.posX - this.prevPosX;
		double d1 = this.posZ - this.prevPosZ;
		float f = MathHelper.sqrt(d0 * d0 + d1 * d1) * 4.0F;

		if (f > 1.0F)
		{
			f = 1.0F;
		}

		this.limbSwingAmount += (f - this.limbSwingAmount) * 0.5F;
		this.limbSwing += this.limbSwingAmount;
	}

	/**
	 * Called only once on an entity when first time spawned, via egg, mob
	 * spawner, natural spawning etc, but not called when entity is reloaded
	 * from nbt. Mainly used for initializing attributes and inventory
	 */
	@Nullable
	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata)
	{
		livingdata = super.onInitialSpawn(difficulty, livingdata);
		((PathNavigateGround) this.getNavigator()).setBreakDoors(true);
		this.setCanPickUpLoot(false);
		this.setLeftHanded(false);
		this.inCombat = false;
		this.blocking = false;
		this.blockingTimer = 0;
		this.setAttackTarget(null);
		this.setRevengeTarget(null);
		this.resetActiveHand();
		this.setActiveHand(EnumHand.MAIN_HAND);
		this.activeItemStackUseCount = 0;
    	this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.0D);
    	this.strafeVer = 0;
    	this.strafeHor = 0;
    	this.getMoveHelper().strafe( 0.0F, 0.0F );
    	this.getNavigator().clearPath();
		
		setEquipmentBasedOnDifficulty(difficulty);
		setEnchantmentBasedOnDifficulty(difficulty);
			    
		addEquipment();
		
		// break doors
	    //this.setBreakDoorsAItask(true);
	    setMount();
	    // skinT = rand.nextInt(ToroQuestConfiguration.banditSkins);
		this.writeEntityToNBT(new NBTTagCompound());
		return livingdata;
	}

	public void setMount()
	{
		if ( ToroQuestConfiguration.banditAndOrcMountChance > 0 && rand.nextInt(11-ToroQuestConfiguration.banditAndOrcMountChance) == 0 )
	    {
    		if ( !(this.getHeldItemMainhand().getItem() instanceof ItemBow) )
    		{
	    		this.weaponMain = new ItemStack(Item.getByNameOrId("spartanweaponry:lance_gold"), 1);
	    		if (!this.weaponMain.isEmpty())
	    		{
	    			setHeldItem(EnumHand.MAIN_HAND, this.weaponMain );
	    	    	if ( !(this.getHeldItemOffhand().getItem() instanceof ItemShield) )
	    	    	{
	    	    		if ( rand.nextBoolean() )
	    	    		{
	    	    			this.weaponOff = ItemStack.EMPTY;
	    	    		}
	    	    		else
	    	    		{
	    	    			this.weaponOff = new ItemStack(Items.SHIELD, 1);
	    	    		}
	    	    		setHeldItem(EnumHand.OFF_HAND, this.weaponOff );
	    	    	}
	    		}
    		}
	    	
	    	if ( !this.world.isRemote )
	    	{
		        EntityHorse mount = new EntityHorse(this.world);
		    	mount.setGrowingAge(24000);
		        mount.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0F);
		        mount.setHorseTamed(true);
				mount.replaceItemInInventory(400, new ItemStack((Items.SADDLE)));
				if ( rand.nextInt(6) == 0 ) mount.replaceItemInInventory(399, new ItemStack((Items.GOLDEN_HORSE_ARMOR),1));
		        mount.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10);
		        //mount.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(8);
		        this.world.spawnEntity(mount);
		        mount.removePassengers();
		        this.startRiding(mount);
		        mount.tasks.addTask(0, new EntityAIDespawn(mount));
	    	}
	    }
	}
	
@Override
public double getYOffset()
{
    return -0.5D; //(double)this.height * 0.5D;
}

@Override
public EnumCreatureAttribute getCreatureAttribute()
{
    return EnumCreatureAttribute.UNDEFINED;
}
 
 /**
 * sets this entity's combat AI.
 */
public void setCombatTask() // combat task
{
    //if (this.world != null && !this.world.isRemote)
    {
	    this.aiArrowAttack.setAttackCooldown(40);
		this.tasks.addTask(4, new AIAttackWithSword(this, 0.7D, true));
		this.tasks.addTask(5, this.aiArrowAttack);    		
    }
    this.inCombat = false;
	this.blocking = false;
	this.blockingTimer = 0;
	this.setAttackTarget(null);
	this.setRevengeTarget(null);
	this.resetActiveHand();
	this.setActiveHand(EnumHand.MAIN_HAND);
	this.activeItemStackUseCount = 0;
	this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.0D);
	this.strafeVer = 0;
	this.strafeHor = 0;
	this.getMoveHelper().strafe( 0.0F, 0.0F );
	this.getNavigator().clearPath();
}

//@Override
//public void updateRidden()
//{
//    super.updateRidden();
//
//    if (this.getRidingEntity() instanceof EntityCreature)
//    {
//        EntityCreature entitycreature = (EntityCreature)this.getRidingEntity();
//        this.renderYawOffset = entitycreature.renderYawOffset;
//        System.out.println(entitycreature.renderYawOffset);
//    }
//}

/**
 * Attack the specified entity using a ranged attack.
 */
@Override
public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor)
{
	if ( target == null || this.getHeldItemMainhand() == null )
	{
		return;
	}
	
    EntityArrow entityarrow = this.getArrow(distanceFactor);

    if ( EnchantmentHelper.getEnchantments(this.getHeldItemMainhand()).containsKey(Enchantment.getEnchantmentByLocation("minecraft:flame")) )
    {
        entityarrow.setFire(12);
    }

    entityarrow.setIsCritical(true);
    double d0 = target.posX - this.posX;
    //double d1 = target.getEntityBoundingBox().minY - entityarrow.posY;
    double d1 = target.getEntityBoundingBox().minY + target.height/2.0 - entityarrow.posY - 1 - rand.nextDouble();
    double d2 = target.posZ - this.posZ;
    double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
    entityarrow.shoot( d0, d1 + d3 * 0.2D, d2, 2.2F, 2.0F );
    this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.5F + 0.8F));
    this.world.spawnEntity(entityarrow);
}

protected EntityArrow getArrow(float p_190726_1_)
{
    return new EntitySmartArrow(this.world, this);
}

//===================================== Raid Location =================================================
/*
* Uses NBT tag compound to read and write data ( raid location ) when the
* entity is reloaded - such as a server restart or when the save is loaded
*/

@Override
public boolean hasHome()
{
	return false;// ( this.getCivilization() != null );
}

public Integer raidX = null;
public Integer raidZ = null;
protected final EntityAIRaid areaAI = new EntityAIRaid(this, 0.7D, 48);



@Override
public void readEntityFromNBT(NBTTagCompound compound)
{
    super.readEntityFromNBT(compound);
    if ( compound.hasKey("raidX") && compound.hasKey("raidZ") )
    {
    	this.raidX = compound.getInteger("raidX");
    	this.raidZ = compound.getInteger("raidZ");
    	this.setRaidLocation(compound.getInteger("raidX"), compound.getInteger("raidZ"));
    }
    if ( compound.hasKey("despawnTimer") )
    {
    	this.despawnTimer = compound.getInteger("despawnTimer");
    }
//    if ( compound.hasKey("skinT") )
//    {
//    	this.skinT = compound.getInteger("skinT");
//    	banditSkin = new ResourceLocation(ToroQuest.MODID + ":textures/entity/bandit/bandit_" + skinT + ".png");
//    }
    //this.setBreakDoorsAItask(true);
    this.setCombatTask();
    
}

@Override
public void writeEntityToNBT(NBTTagCompound compound)
{
	if ( this.raidX != null && this.raidZ != null )
	{
		compound.setInteger("raidX", this.raidX);
		compound.setInteger("raidZ", this.raidZ);
	}
	compound.setInteger("despawnTimer", this.despawnTimer);
//	compound.setInteger("skinT", this.skinT);
//	banditSkin = new ResourceLocation(ToroQuest.MODID + ":textures/entity/bandit/bandit_" + skinT + ".png");
	super.writeEntityToNBT(compound);
}

/* Set the direction for bandits to move to */
public void setRaidLocation(Integer x, Integer z)
{
	this.tasks.removeTask(this.areaAI);
	if ( x != null && z != null )
	{
		this.raidX = x;
		this.raidZ = z;
		this.tasks.addTask(11, this.areaAI);
		this.areaAI.setCenter(x, z);
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeEntityToNBT(nbt);
	}
}
// ====================================================================================================

@Override
public void setItemStackToSlot(EntityEquipmentSlot slotIn, ItemStack stack)
{
    super.setItemStackToSlot(slotIn, stack);

    if ( slotIn == EntityEquipmentSlot.MAINHAND)
    {
        this.setCombatTask();
    }
}
@Override
public float getEyeHeight()
{
    return 1.935F;
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
	        else // NOT this.blocking
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
	
	
	
	
	
	
	
	
	// can't be moved with a leash
//	@Override
//	protected void updateLeashedState()
//    {
//      return;
//    }
	
	
	
	
	
	
	
	// --------------------------------------------------------------------------------------------------------------------------------
    @Nullable
    //Village village;

    /**
     * Decrements the entity's air supply when underwater
     */
    protected int decreaseAirSupply(int air)
    {
        return air;
    }
    
    @Override
    @Nullable
    protected SoundEvent getAmbientSound()
    {
        return null;
    }

	@Override
	@Nullable
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
		if ( rand.nextInt(4) == 0 )
		{
			this.playSound(SoundEvents.VINDICATION_ILLAGER_DEATH, 1.0F, 0.9F + rand.nextFloat()/5 );
		}
		return super.getHurtSound(damageSourceIn);
    }

	@Override
	@Nullable
    protected SoundEvent getDeathSound()
    {
		if ( rand.nextBoolean() )
		{
			this.playSound(SoundEvents.EVOCATION_ILLAGER_DEATH, 1.0F, 0.9F + rand.nextFloat()/5 );
		}
		else
		{
			this.playSound(SoundEvents.ENTITY_ILLAGER_DEATH, 1.0F, 0.9F + rand.nextFloat()/5 );
		}
		return super.getDeathSound();
    }
    
    
    
//    public boolean isPlayerBandit( EntityLivingBase attacker ) // TODO range of check
//    {
//		if ( !(attacker instanceof EntityPlayer) )
//		{
//			return false;
//		}
//		//try
//		{
//			EntityPlayer player = (EntityPlayer)attacker;
//			if ( this.getRevengeTarget() == null || ( this.getRevengeTarget() != null && this.getRevengeTarget() != attacker ) )
//			{
//				return false;
//			}
//			if ( this.attackingPlayer == attacker )
//			{
//				return false;
//			}
//			for ( ItemStack i: player.inventory.armorInventory )
//			{
//				if ( i.getItem() instanceof ItemBanditArmor || i.getItem() instanceof ItemLegendaryBanditArmor )
//				{
//					return true;
//				}
//			}
//			Village village = this.world.getVillageCollection().getNearestVillage(new BlockPos(this), 256);
//			if ( village == null )
//			{
//				return false;
//			}
//			Province province = CivilizationUtil.getProvinceAt(this.world, village.getCenter().getX()/16,village.getCenter().getZ());
//			if ( province == null )
//			{
//				return false;
//			}
//			CivilizationType civ = province.civilization;
//			if ( civ == null )
//			{
//				return false;
//			}
//			int rep = PlayerCivilizationCapabilityImpl.get((EntityPlayer)attacker).getReputation(civ);
//			if ( rep <= -10 )
//			{
//				return true;
//			}
//		}
//		//catch (Exception e)
//		{
//			
//		}
//		return false;
//    }
    
    
    
    
    
    
    
    
	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= DPS =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

    protected Vec3d vec3d;
    protected int splashPotionTimer = 6;
    protected int hasSplashPotion = 1;
    
	private void sentryTypeDPS( )
	{
//		EntityLivingBase attacker = this.getAttackTarget();
//
//		if ( attacker == null || !attacker.isEntityAlive() )
//    	{
//    		attacker = this.getRevengeTarget();
//    		if ( attacker != null && attacker.isEntityAlive() )
//    		{
//    			this.setAttackTarget( attacker );
//    		}
//    	}
		
//		if ( isPlayerBandit(attacker) )
//		{
//			attacker = null;
//			this.setAttackTarget( attacker );
//		}
		
		if ( this.getAttackTarget() != null && this.getAttackTarget().isEntityAlive() && !(this.getAttackTarget() instanceof EntityToroMob) )
		{
	        double dist = this.getDistanceSq(this.getAttackTarget());
            
	        if ( !this.inCombat )
	        {
	        	this.getNavigator().clearPath();
				this.inCombat = true;
				this.strafeVer = 0.8F;
				this.strafeHor = getStrafe(stance);
	        }
	        // if within range and has not been in melee range for a short amount of time, or very close and has not been in melee range for a long amount of time
			// if ( ( dist <= 96 && this.blockingTimer > -256+dist*2 ) || ( dist <= 16 && this.blockingTimer < -dist*2 ) )
	        
			if ( this.blockingTimer < 0 )
			{
				this.stance = (rand.nextInt(6)+5);
				this.blockingTimer = (rand.nextInt(32)+16); // (this.stance*6+dist)
				this.strafeHor = getStrafe(stance);
			}
			
			if ( this.hasSplashPotion == 1 && dist >= 24 && dist <= 128 && this.getHeldItemOffhand().isEmpty() && !this.isBesideClimbableBlock() )
			{
				if ( world.getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(this.getPosition()).grow(3, 3, 3)).size() > 1 )
				{
					this.hasSplashPotion = 0;
					setHeldItem(EnumHand.OFF_HAND, new ItemStack(Items.SPLASH_POTION,1) );
				}
			}
			else if ( this.hasSplashPotion == 0 && --this.splashPotionTimer < 0 )
			{
				if ( dist > 6 && dist <= 64 )
				{
					if ( world.getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(this.getPosition()).grow(3, 3, 3)).size() > 1 )
					{
						this.splashPotionTimer = 6;
					}
					else
					{
						this.hasSplashPotion = -1;
						this.swingArm(EnumHand.OFF_HAND);
						this.throwSplashPotion(this.getAttackTarget());
						setHeldItem(EnumHand.OFF_HAND, ItemStack.EMPTY );
						float push = (float)((2+dist));
						if ( !world.isRemote )
						{
							Vec3d velocityVector = new Vec3d(this.getAttackTarget().posX - this.posX, 0, this.getAttackTarget().posZ - this.posZ);
							this.addVelocity(-(velocityVector.x)/push,0,-(velocityVector.z)/push);
							this.getNavigator().clearPath();
						}
					}
				}
				else
				{
					this.splashPotionTimer = 3;
//					this.hasSplashPotion = 1;
//					setHeldItem(EnumHand.OFF_HAND, ItemStack.EMPTY );
				}
			}

			if ( dist <= 6 )
			{
				this.strafeVer = 0.5F;
			}
			else
			{
				this.strafeVer = 0.8F;
			}
						
			if ( this.getNavigator().tryMoveToEntityLiving(this.getAttackTarget(), this.strafeVer) )
			{
				if ( dist <= 6 )
				{
					this.getMoveHelper().strafe( this.strafeVer, this.strafeHor-0.1F );
				}
				else
				{
					this.getMoveHelper().strafe( this.strafeVer, this.strafeHor );
				}
			}
//			else
//			{
//				if ( dist >= 4 && Math.pow(this.posY - attacker.posY,2) > Math.abs((this.posX - attacker.posX)*(this.posZ - attacker.posZ)) )
//				{
//					if ( vector3d == null )
//					{
//						vector3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this, 16, 16, attacker.getPositionVector());
//				        this.blockingTimer = -200;
//					}
//					
//				    if ( vector3d != null )
//				    {
//			            double rPosX = vector3d.x;
//			            double rPosY = vector3d.y;
//					    double rPosZ = vector3d.z;
//				        this.getNavigator().tryMoveToXYZ(rPosX, rPosY, rPosZ, this.strafeVer);
//				    }
//				}
//			}
			
			this.blockingTimer--;
		}
		else if ( this.blocking || this.inCombat ) // end of combat
		{
			this.inCombat = false;
			this.blocking = false;
			// if ( attacker != null && attacker.isEntityAlive() )
			{
				this.setAttackTarget(null);
			}
			//this.blockingTimer = 0;
			this.resetActiveHand();
			this.activeItemStackUseCount = 0;
	    	this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.0D);
	    	this.strafeVer = 0;
	    	this.strafeHor = 0;
	    	this.getMoveHelper().strafe( 0.0F, 0.0F );
	    	this.getNavigator().clearPath();
			if ( this.getHeldItemOffhand().getItem() instanceof ItemPotion ) setHeldItem(EnumHand.OFF_HAND, ItemStack.EMPTY );
	    	this.hasSplashPotion = 1;
	    	this.splashPotionTimer = 10;
		}
	}
	
	
	public void throwSplashPotion(EntityLivingBase target)
    {
        //if (!this.isDrinkingPotion())
        {
            double d0 = target.posY;
            double d1 = target.posX + target.motionX - this.posX;
            double d2 = d0 - this.posY;
            double d3 = target.posZ + target.motionZ - this.posZ;
            float f = MathHelper.sqrt(d1 * d1 + d3 * d3);
            PotionType potiontype = PotionTypes.HARMING;

            if ( rand.nextInt(5) == 0 && !target.isPotionActive(MobEffects.SLOWNESS) )
            {
                potiontype = PotionTypes.SLOWNESS;
            }
            else if (rand.nextInt(4) == 0 && target.getHealth() >= 8.0F && !target.isPotionActive(MobEffects.POISON))
            {
                potiontype = PotionTypes.POISON;
            }
            else if ( rand.nextInt(3) == 0 && !target.isPotionActive(MobEffects.WEAKNESS) )
            {
                potiontype = PotionTypes.WEAKNESS;
            }
            else
            {
                potiontype = PotionTypes.HARMING;
            }

            EntityPotion entitypotion = new EntityPotion(this.world, this, PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), potiontype));
            entitypotion.rotationPitch -= -20.0F;
            entitypotion.shoot(d1, d2 + (double)(f * 0.2F), d3, 0.75F, 8.0F);
            this.world.playSound((EntityPlayer)null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_SPLASH_POTION_THROW, this.getSoundCategory(), 1.0F, 0.8F + this.rand.nextFloat() * 0.4F);
            this.world.spawnEntity(entitypotion);
        }
    }
	
	//private Vec3d vector3d = null;
	
	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= Tank =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	private void sentryTypeTank( )
	{
		// EntityLivingBase attacker = this.getAttackTarget();

//		if ( attacker == null || !attacker.isEntityAlive() )
//    	{
//    		attacker = this.getRevengeTarget();
//    		if ( attacker != null && attacker.isEntityAlive() )
//    		{
//    			this.setAttackTarget( attacker );
//    		}
//    	}
		
//		if ( isPlayerBandit(attacker) )
//		{
//			attacker = null;
//			this.setAttackTarget( attacker );
//		}
		
		if ( this.getAttackTarget() != null && this.getAttackTarget().isEntityAlive() && !(this.getAttackTarget() instanceof EntityToroMob) )
		{
	        double dist = this.getDistanceSq(this.getAttackTarget());
            
	        if ( !this.inCombat )
	        {
				this.inCombat = true;
	        	this.getNavigator().clearPath();
				this.strafeVer = 0.8F;
				this.strafeHor = getStrafe(stance);
	        }
	        // if within range and has not been in melee range for a short amount of time, or very close and has not been in melee range for a long amount of time
			// if ( ( dist <= 96 && this.blockingTimer > -256+dist*2 ) || ( dist <= 16 && this.blockingTimer < -dist*2 ) )
	        {
				
				// if this is not blocking, is within range, and block is ready, start blocking
				if ( !this.blocking && dist <= 12 && this.blockingTimer <= -( (int)(this.stance*8-dist) ))
				{
					this.stance = (rand.nextInt(6)+5);
					this.blockingTimer = (int)(this.stance*8-dist); // (this.stance*6+dist)
					this.blocking = true;
					this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
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
					this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.0D);
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
//					if ( dist >= 4 && Math.pow(this.posY - attacker.posY,2) > Math.abs((this.posX - attacker.posX)*(this.posZ - attacker.posZ)) )
//					{
//						if ( vector3d == null )
//						{
//							vector3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this, 16, 16, attacker.getPositionVector());
//					        this.blockingTimer = -200;
//						}
//						
//					    if ( vector3d != null )
//					    {
//				            double rPosX = vector3d.x;
//				            double rPosY = vector3d.y;
//						    double rPosZ = vector3d.z;
//					        this.getNavigator().tryMoveToXYZ(rPosX, rPosY, rPosZ, this.strafeVer);
//					    }
//					}
//					else
					{
						if ( this.getNavigator().tryMoveToEntityLiving(this.getAttackTarget(), this.strafeVer) )
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
						if ( !world.isRemote )
						{
							Vec3d velocityVector = new Vec3d(this.getAttackTarget().posX - this.posX, 0, this.getAttackTarget().posZ - this.posZ);
							this.addVelocity(-(velocityVector.x)/push,0,-(velocityVector.z)/push);
						}
				        this.getNavigator().tryMoveToEntityLiving(this.getAttackTarget(), 0.2F);
						this.getMoveHelper().strafe( 0.4F, this.strafeHor+0.1F );
					}
					else
					{
						this.getNavigator().tryMoveToEntityLiving(this.getAttackTarget(), this.strafeVer);
						this.getMoveHelper().strafe( this.strafeVer, this.strafeHor+0.1F );
					}
				}
	        }
			this.blockingTimer--;
		}
		else if ( this.blocking || this.inCombat ) // end of combat
		{
			this.inCombat = false;
			this.blocking = false;
			if ( this.getAttackTarget() != null && this.getAttackTarget().isEntityAlive() )
			{
				this.setAttackTarget(null);
				this.setRevengeTarget(null);
			}
			this.resetActiveHand();
			this.activeItemStackUseCount = 0;
	    	this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.0D);
	    	this.strafeVer = 0;
	    	this.strafeHor = 0;
	    	this.getMoveHelper().strafe( 0.0F, 0.0F );
	    	this.getNavigator().clearPath();
		}
	}
	
	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= Ranged =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

	private void sentryTypeRanged( )
	{
		
//		EntityLivingBase attacker = this.getAttackTarget();
//		
//		if ( attacker == null || !attacker.isEntityAlive() )
//    	{
//    		attacker = this.getRevengeTarget();
//    		if ( attacker != null && attacker.isEntityAlive() )
//    		{
//    			this.setAttackTarget( attacker );
//    		}
//    	}
//		if ( isPlayerBandit(attacker) )
//		{
//			attacker = null;
//			this.setAttackTarget( attacker );
//		}
		if ( this.getAttackTarget() != null && this.getAttackTarget().isEntityAlive() && !(this.getAttackTarget() instanceof EntityToroMob) )
		{            
	        if ( !this.inCombat )
	        {
				this.inCombat = true;
	        	this.getNavigator().clearPath();
				this.strafeVer = 0.8F;
				this.strafeHor = getStrafe(stance);
	        }
		}
		else if ( this.blocking || this.inCombat ) // end of combat
		{
			this.inCombat = false;
			this.blocking = false;
			if ( this.getAttackTarget() != null && this.getAttackTarget().isEntityAlive() )
			{
				this.setAttackTarget(null);
				this.setRevengeTarget(null);
			}
			this.resetActiveHand();
			this.activeItemStackUseCount = 0;
	    	this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.0D);
	    	this.strafeVer = 0;
	    	this.strafeHor = 0;
	    	this.getMoveHelper().strafe( 0.0F, 0.0F );
	    	this.getNavigator().clearPath();
		}
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
			//super.attackEntityAsMob(victim);
			attackTargetEntityWithCurrentItem(victim); // if there are errors, remove this
			return true;
		}
//		if (victim instanceof EntityPlayer && !isFoe( (EntityPlayer) victim ) )
//		{
//			setAttackTarget(null);
//		}
//		return true;
	}
	
	
	
	public void attackTargetEntityWithCurrentItem(Entity targetEntity)
	{
		
		if ( rand.nextInt(5) == 0 )
        {
        	this.playSound( SoundEvents.VINDICATION_ILLAGER_AMBIENT, 1.0F, 0.9F + rand.nextFloat()/5 );
        }
		
		if (targetEntity.canBeAttackedWithItem()) {
			if (!targetEntity.hitByEntity(this)) {
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

	public float getStrafe(int stance)
	{
		switch ( stance )
		{
			case 5:
			{
				return -0.22F;
			}
			case 6:
			{
				return 0.22F;
			}
			case 7:
			{
				return -0.28F;
			}
			case 8:
			{
				return 0.28F;
			}
			case 9:
			{
				return -0.32F;
			}
			case 10:
			{
				return 0.32F;
			}
		}
		return 0;
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
	
	
	
    //private static final DataParameter<Boolean> SWINGING_ARMS = EntityDataManager.<Boolean>createKey(AbstractSkeleton.class, DataSerializers.BOOLEAN);

//	  @SideOnly(Side.CLIENT)
//    public boolean isSwingingArms()
//    {
//        return ((Boolean)this.dataManager.get(SWINGING_ARMS)).booleanValue();
//    }

    public void setSwingingArms(boolean swingingArms)
    {
        //this.dataManager.set(SWINGING_ARMS, Boolean.valueOf(swingingArms));
    }
	
    private static final DataParameter<Byte> CLIMBING = EntityDataManager.<Byte>createKey(EntitySentry.class, DataSerializers.BYTE);
    
//    @Override
//    protected PathNavigate createNavigator(World worldIn)
//    {
//        return new PathNavigateClimber(this, worldIn);
//    }
    
    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void onUpdate()
    {
        if (!this.world.isRemote)
        {
        	if ( this.collidedHorizontally )
        	{
                ItemStack iStackM = this.getHeldItemMainhand();
        		if ( !this.inCombat || this.isRiding() || ( iStackM.getItem() != null && iStackM.getItem() instanceof ItemBow) )
				{
					this.setBesideClimbableBlock(false);
	    			if ( !this.blocking ) this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.0D);
				}
        		else
        		{
        			this.setBesideClimbableBlock(true);
        			this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
        		}
        	}
        	else
        	{
        		this.setBesideClimbableBlock(false);
    			if ( !this.blocking ) this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.0D);
        	}
        }
        super.onUpdate();

    }
    
    @Override
    public boolean canBePushed()
    {
        return this.isEntityAlive();
    }
    
    @Override
    public boolean isOnLadder()
    {
        return this.isBesideClimbableBlock();
    }
    
    public boolean isBesideClimbableBlock()
    {
        return (((Byte)this.dataManager.get(CLIMBING)).byteValue() & 1) != 0;
    }
    
    

    /**
     * Updates the WatchableObject (Byte) created in entityInit(), setting it to 0x01 if par1 is true or 0x00 if it is
     * false.
     */
    
    public void setBesideClimbableBlock(boolean climbing)
    {
        byte b0 = ((Byte)this.dataManager.get(CLIMBING)).byteValue();
        
        if ( climbing )
        {
            b0 = (byte)(b0 | 1);
        }
        else
        {
            b0 = (byte)(b0 & -2);
        }

        this.dataManager.set(CLIMBING, Byte.valueOf(b0));
    }
    
    public void addEquipment()
    {
	    int weapon = rand.nextInt(3);
	    
	    if ( weapon == 0 ) // MELEE
	    {
	    	if ( rand.nextBoolean() ) // TWO-HANDED
	    	{
		    	this.weaponMain = new ItemStack(Item.getByNameOrId(ToroQuestConfiguration.banditTwoHandedMeleeWeapons[rand.nextInt(ToroQuestConfiguration.banditTwoHandedMeleeWeapons.length)]),1);
		    	if ( this.weaponMain == null || this.weaponMain.isEmpty() )
				{
		    		if ( rand.nextBoolean() )
					{
						this.weaponMain = new ItemStack(Items.IRON_SWORD, 1);
					}
					else
					{
						this.weaponMain = new ItemStack(Items.IRON_AXE, 1);
					}
				}
		    	
		    	if (  ToroQuestConfiguration.enchantFirstBanditAndOrcChance > 0 && rand.nextInt(100) < ToroQuestConfiguration.enchantFirstBanditAndOrcChance )
		    	{
			    	String[] enchant = ToroQuestConfiguration.enchantFirstBanditAndOrcMeleeWeapon[rand.nextInt(ToroQuestConfiguration.enchantFirstBanditAndOrcMeleeWeapon.length)].split(",");
		    		this.weaponMain.addEnchantment(Enchantment.getEnchantmentByLocation(enchant[0]), rand.nextInt(Integer.parseInt(enchant[1]))+1);
			    	if (  ToroQuestConfiguration.enchantSecondBanditAndOrcChance > 0 && rand.nextInt(100) < ToroQuestConfiguration.enchantSecondBanditAndOrcChance )
			    	{
				    	enchant = ToroQuestConfiguration.enchantSecondBanditAndOrcMeleeWeapon[rand.nextInt(ToroQuestConfiguration.enchantSecondBanditAndOrcMeleeWeapon.length)].split(",");
			    		this.weaponMain.addEnchantment(Enchantment.getEnchantmentByLocation(enchant[0]), rand.nextInt(Integer.parseInt(enchant[1]))+1);
			    	}
		    	}

	    	}
	    	else // DUAL-WIELD
	    	{
	    		this.weaponMain = new ItemStack(Item.getByNameOrId(ToroQuestConfiguration.banditOneHandedMeleeWeapons[rand.nextInt(ToroQuestConfiguration.banditOneHandedMeleeWeapons.length)]),1);
		    	if ( this.weaponMain == null || this.weaponMain.isEmpty() )
		    	{
					if ( rand.nextBoolean() )
					{
						this.weaponMain = new ItemStack(Items.IRON_SWORD, 1);
					}
					else
					{
						this.weaponMain = new ItemStack(Items.IRON_AXE, 1);
					}
				}
				
		    	if (  ToroQuestConfiguration.enchantFirstBanditAndOrcChance > 0 && rand.nextInt(100) < ToroQuestConfiguration.enchantFirstBanditAndOrcChance )
		    	{
			    	String[] enchant = ToroQuestConfiguration.enchantFirstBanditAndOrcMeleeWeapon[rand.nextInt(ToroQuestConfiguration.enchantFirstBanditAndOrcMeleeWeapon.length)].split(",");
		    		this.weaponMain.addEnchantment(Enchantment.getEnchantmentByLocation(enchant[0]), rand.nextInt(Integer.parseInt(enchant[1]))+1);
			    	if (  ToroQuestConfiguration.enchantSecondBanditAndOrcChance > 0 && rand.nextInt(100) < ToroQuestConfiguration.enchantSecondBanditAndOrcChance )
			    	{
				    	enchant = ToroQuestConfiguration.enchantSecondBanditAndOrcMeleeWeapon[rand.nextInt(ToroQuestConfiguration.enchantSecondBanditAndOrcMeleeWeapon.length)].split(",");
			    		this.weaponMain.addEnchantment(Enchantment.getEnchantmentByLocation(enchant[0]), rand.nextInt(Integer.parseInt(enchant[1]))+1);
			    	}
		    	}
		    	
				this.weaponOff = this.weaponMain.copy();
	    	}
	    }
	    else if ( weapon == 1 ) // SHIELD
	    {
	    	this.weaponMain = new ItemStack(Item.getByNameOrId(ToroQuestConfiguration.banditOneHandedMeleeWeapons[rand.nextInt(ToroQuestConfiguration.banditOneHandedMeleeWeapons.length)]),1);
	    	if ( this.weaponMain == null || this.weaponMain.isEmpty() )
	    	{
				if ( rand.nextBoolean() )
				{
					this.weaponMain = new ItemStack(Items.IRON_SWORD, 1);
				}
				else
				{
					this.weaponMain = new ItemStack(Items.IRON_AXE, 1);
				}
			}
	        
	    	if (  ToroQuestConfiguration.enchantFirstBanditAndOrcChance > 0 && rand.nextInt(100) < ToroQuestConfiguration.enchantFirstBanditAndOrcChance )
	    	{
		    	String[] enchant = ToroQuestConfiguration.enchantFirstBanditAndOrcMeleeWeapon[rand.nextInt(ToroQuestConfiguration.enchantFirstBanditAndOrcMeleeWeapon.length)].split(",");
	    		this.weaponMain.addEnchantment(Enchantment.getEnchantmentByLocation(enchant[0]), rand.nextInt(Integer.parseInt(enchant[1]))+1);
		    	if (  ToroQuestConfiguration.enchantSecondBanditAndOrcChance > 0 && rand.nextInt(100) < ToroQuestConfiguration.enchantSecondBanditAndOrcChance )
		    	{
			    	enchant = ToroQuestConfiguration.enchantSecondBanditAndOrcMeleeWeapon[rand.nextInt(ToroQuestConfiguration.enchantSecondBanditAndOrcMeleeWeapon.length)].split(",");
		    		this.weaponMain.addEnchantment(Enchantment.getEnchantmentByLocation(enchant[0]), rand.nextInt(Integer.parseInt(enchant[1]))+1);
		    	}
	    	}

	    	this.weaponOff = new ItemStack(Item.getByNameOrId(ToroQuestConfiguration.banditShields[rand.nextInt(ToroQuestConfiguration.banditShields.length)]),1);
			if ( this.weaponOff == null || this.weaponOff.isEmpty() )
	    	{
				this.weaponOff = new ItemStack(Items.SHIELD, 1);
			}
	    	
			if (  ToroQuestConfiguration.enchantFirstBanditAndOrcChance > 0 && rand.nextInt(100) < ToroQuestConfiguration.enchantFirstBanditAndOrcChance )
	    	{
		    	String[] enchant = ToroQuestConfiguration.enchantFirstBanditAndOrcShield[rand.nextInt(ToroQuestConfiguration.enchantFirstBanditAndOrcShield.length)].split(",");
	    		this.weaponOff.addEnchantment(Enchantment.getEnchantmentByLocation(enchant[0]), rand.nextInt(Integer.parseInt(enchant[1]))+1);
//		    	if (  ToroQuestConfiguration.enchantSecondBanditAndOrcChance > 0 && rand.nextInt(100) < ToroQuestConfiguration.enchantSecondBanditAndOrcChance )
//		    	{
//			    	enchant = ToroQuestConfiguration.enchantSecondBanditAndOrcShield[rand.nextInt(ToroQuestConfiguration.enchantSecondBanditAndOrcShield.length)].split(",");
//		    		this.weaponOff.addEnchantment(Enchantment.getEnchantmentByLocation(enchant[0]), rand.nextInt(Integer.parseInt(enchant[1]))+1);
//		    	}
	    	}
	    	
	    }
	    else if ( weapon == 2 ) // RANGED
	    {
	    	this.weaponMain = new ItemStack(Item.getByNameOrId(ToroQuestConfiguration.banditRangedWeapons[rand.nextInt(ToroQuestConfiguration.banditRangedWeapons.length)]),1);
	    	if ( this.weaponMain == null || this.weaponMain.isEmpty() )
	    	{
				this.weaponMain = new ItemStack(Items.BOW, 1);
			}
	    	
	    	if (  ToroQuestConfiguration.enchantFirstBanditAndOrcChance > 0 && rand.nextInt(100) < ToroQuestConfiguration.enchantFirstBanditAndOrcChance )
	    	{
		    	String[] enchant = ToroQuestConfiguration.enchantFirstBanditAndOrcRanged[rand.nextInt(ToroQuestConfiguration.enchantFirstBanditAndOrcRanged.length)].split(",");
	    		this.weaponMain.addEnchantment(Enchantment.getEnchantmentByLocation(enchant[0]), rand.nextInt(Integer.parseInt(enchant[1]))+1);
		    	if (  ToroQuestConfiguration.enchantSecondBanditAndOrcChance > 0 && rand.nextInt(100) < ToroQuestConfiguration.enchantSecondBanditAndOrcChance )
		    	{
			    	enchant = ToroQuestConfiguration.enchantSecondBanditAndOrcRanged[rand.nextInt(ToroQuestConfiguration.enchantSecondBanditAndOrcRanged.length)].split(",");
		    		this.weaponMain.addEnchantment(Enchantment.getEnchantmentByLocation(enchant[0]), rand.nextInt(Integer.parseInt(enchant[1]))+1);
		    	}
	    	}
	    }
	    
	    if ( this.weaponMain != null && !this.weaponMain.isEmpty() )
	    {
			setHeldItem(EnumHand.MAIN_HAND, this.weaponMain );
	    }
	    if ( this.weaponOff != null && !this.weaponOff.isEmpty() )
	    {
	    	setHeldItem(EnumHand.OFF_HAND, this.weaponOff );
	    }
		
		addMask();
		
//		ItemStack chest = new ItemStack(Item.getByNameOrId("toroquest:toro_armor_chestplate"), 1);
//		ItemStack legs = new ItemStack(Item.getByNameOrId("toroquest:toro_armor_leggings"), 1);
//		ItemStack feet = new ItemStack(Item.getByNameOrId("toroquest:toro_armor_boots"), 1);
//			
//		setItemStackToSlot(EntityEquipmentSlot.HEAD, head);
//		setItemStackToSlot(EntityEquipmentSlot.CHEST, chest);
//		setItemStackToSlot(EntityEquipmentSlot.LEGS, legs);
//		setItemStackToSlot(EntityEquipmentSlot.FEET, feet);
		
    }
    
    public void addMask()
    {
		if ( ToroQuestConfiguration.renderBanditMask )
		{
			ItemStack head = new ItemStack(Item.getByNameOrId("toroquest:bandit_helmet"), 1);
			setItemStackToSlot(EntityEquipmentSlot.HEAD, head);
		}
    }

}