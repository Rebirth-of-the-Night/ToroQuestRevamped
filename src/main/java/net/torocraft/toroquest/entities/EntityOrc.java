package net.torocraft.toroquest.entities;

import javax.annotation.Nullable;

import net.minecraft.block.BlockFire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveIndoors;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.torocraft.toroquest.ToroQuest;
import net.torocraft.toroquest.config.ToroQuestConfiguration;
import net.torocraft.toroquest.entities.ai.EntityAIBanditAttack;
import net.torocraft.toroquest.entities.ai.EntityAIBreakDoorBandit;
import net.torocraft.toroquest.entities.render.RenderOrc;

public class EntityOrc extends EntitySentry implements IRangedAttackMob, IMob
{
	public static String NAME = "orc";
	static
	{
		if (ToroQuestConfiguration.specificEntityNames)
		{
			NAME = ToroQuestEntities.ENTITY_PREFIX + NAME;
		}
	}
	public static void init(int entityId)
	{
		EntityRegistry.registerModEntity(new ResourceLocation(ToroQuest.MODID, NAME), EntityOrc.class, NAME, entityId, ToroQuest.INSTANCE, 80, 2,
				true, 0x308f26, 0xe0d359);
	}

	public static void registerRenders()
	{
		RenderingRegistry.registerEntityRenderingHandler(EntityOrc.class, new IRenderFactory<EntityOrc>()
		{
			@Override
			public Render<EntityOrc> createRenderFor(RenderManager manager)
			{
				return new RenderOrc(manager);
			}
		});
	}
	
	protected int splashPotionTimer = -1;
    protected int hasSplashPotion = -1;
	
    @Override
    public void addMask()
    {
		if ( ToroQuestConfiguration.renderOrcMask )
		{
			ItemStack head = new ItemStack(Item.getByNameOrId("toroquest:bandit_helmet"), 1);
			setItemStackToSlot(EntityEquipmentSlot.HEAD, head);
		}
    }
    
	@Override
	protected void initEntityAI()
	{
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(2, new EntityAIRangedFlee(this, 0.8D));
		this.tasks.addTask(3, new EntityAIFlee(this, 0.8D));
		// door
        			//*this.tasks.addTask(7, new EntityAIBreakDoorBandit(this)); //this.tasks.addTask(8, new EntityAIOpenDoor(this, true));
        this.tasks.addTask(8, new EntityAIRestrictOpenDoor(this));
		this.tasks.addTask(9, new EntityAIMoveIndoors(this));
        this.tasks.addTask(10, new EntityAIMoveTowardsRestriction(this, 0.5D));
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
        if ( !ToroQuestConfiguration.orcsAreNeutral )
        {
			this.targetTasks.addTask(0, new EntityAIBanditAttack(this));
	//        this.targetTasks.addTask(0, new EntityAINearestAttackableTarget<EntityToroNpc>(this, EntityToroNpc.class, true, false));
	//        this.targetTasks.addTask(1, new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, true, false));
	//        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<EntityToroVillager>(this, EntityToroVillager.class, false, false));
	        this.targetTasks.addTask(1, new EntityAINearestAttackableTarget<EntityVillager>(this, EntityVillager.class, 500, false, false, null));
        }
        this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, true, new Class[0])
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
	
	public ResourceLocation orcSkin = new ResourceLocation(ToroQuest.MODID + ":textures/entity/orc/orc_" + rand.nextInt(ToroQuestConfiguration.orcSkins) + ".png");
	
	public ResourceLocation getSkin()
	{
		return this.orcSkin;
	}
	
	public double renderSize = 1.05D + rand.nextDouble()/16;
	
	public double getRenderSize()
	{
		return this.renderSize;
	}

	public EntityOrc(World worldIn)
	{
		super(worldIn);
		
		// TODO Auto-generated constructor stub
	}

	@Override
	public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
		// TODO Auto-generated method stub
		super.attackEntityWithRangedAttack(target, distanceFactor);
	}

//	@Override
//	public void setSwingingArms(boolean swingingArms)
//	{
//		// TODO Auto-generated method stub
//		super.setSwingingArms(swingingArms);
//	}
	
	@Override
    protected void applyEntityAttributes()
    {
		super.applyEntityAttributes();
    	this.setLeftHanded(false);
    	this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(ToroQuestConfiguration.orcHealth);
    	this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0D);
	    this.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).setBaseValue(1.0D);
    	this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(ToroQuestConfiguration.orcArmor);
		this.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).setBaseValue(0.0D);
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(48.0D);
    	this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.45D);
    	this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.0D);
    }
	
	
	@Override
	public void addEquipment()
    {
	    int weapon = rand.nextInt(3);
	    
	    if ( weapon == 0 ) // MELEE
	    {
	    	if ( rand.nextBoolean() ) // TWO-HANDED
	    	{
		    	this.weaponMain = new ItemStack(Item.getByNameOrId(ToroQuestConfiguration.orcTwoHandedMeleeWeapons[rand.nextInt(ToroQuestConfiguration.orcTwoHandedMeleeWeapons.length)]),1);
		    	if ( this.weaponMain == null || this.weaponMain.isEmpty() )
				{
		    		if ( rand.nextBoolean() )
					{
						this.weaponMain = new ItemStack(Items.STONE_SWORD, 1);
					}
					else
					{
						this.weaponMain = new ItemStack(Items.STONE_AXE, 1);
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
	    		this.weaponMain = new ItemStack(Item.getByNameOrId(ToroQuestConfiguration.orcOneHandedMeleeWeapons[rand.nextInt(ToroQuestConfiguration.orcOneHandedMeleeWeapons.length)]),1);
		    	if ( this.weaponMain == null || this.weaponMain.isEmpty() )
		    	{
					if ( rand.nextBoolean() )
					{
						this.weaponMain = new ItemStack(Items.STONE_SWORD, 1);
					}
					else
					{
						this.weaponMain = new ItemStack(Items.STONE_AXE, 1);
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
	    	this.weaponMain = new ItemStack(Item.getByNameOrId(ToroQuestConfiguration.orcOneHandedMeleeWeapons[rand.nextInt(ToroQuestConfiguration.orcOneHandedMeleeWeapons.length)]),1);
	    	if ( this.weaponMain == null || this.weaponMain.isEmpty() )
	    	{
				if ( rand.nextBoolean() )
				{
					this.weaponMain = new ItemStack(Items.STONE_SWORD, 1);
				}
				else
				{
					this.weaponMain = new ItemStack(Items.STONE_AXE, 1);
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

	    	this.weaponOff = new ItemStack(Item.getByNameOrId(ToroQuestConfiguration.orcShields[rand.nextInt(ToroQuestConfiguration.orcShields.length)]),1);
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
	    	this.weaponMain = new ItemStack(Item.getByNameOrId(ToroQuestConfiguration.orcRangedWeapons[rand.nextInt(ToroQuestConfiguration.orcRangedWeapons.length)]),1);
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
		
    }
	
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
				
				if ( block.getBlock() instanceof BlockFire )
				{
					this.swingArm(EnumHand.MAIN_HAND);
					this.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 1.0F, 1.0F);
					world.setBlockToAir(pos);
				}
				else if ( this.rand.nextBoolean() || this.getNavigator().noPath() )
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
			this.playSound(SoundEvents.ITEM_SHIELD_BLOCK, 1.0F, 1.0F);
			double dist = e.getDistanceSq(this);
			if ( dist <= 8 && !source.isProjectile() && !source.isMagicDamage() && !source.isFireDamage() )
			{
				if ( !this.world.isRemote )
				{
					Vec3d velocityVector = new Vec3d(e.posX - this.posX, 0, e.posZ - this.posZ);
					e.addVelocity((velocityVector.x)/( dist+1 ), 0.3D, (velocityVector.z)/( dist+1 ));
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
	
	@Override
	public void dropLoot()
	{
		if (!world.isRemote)
		{
			if ( ToroQuestConfiguration.orcsDropMasks )
			{
				dropMask();
			}
			if ( ToroQuestConfiguration.orcsDropEmeralds && rand.nextInt(3) == 0 )
			{
				ItemStack stack = new ItemStack(Items.EMERALD, rand.nextInt(4)+1);
				EntityItem dropItem = new EntityItem(world, posX, posY, posZ, stack.copy());
				world.spawnEntity(dropItem);
			}
		}
	}
	
	
	@Override
    @Nullable
    protected SoundEvent getAmbientSound()
    {
		this.playSound(SoundEvents.ENTITY_ZOMBIE_PIG_AMBIENT, 1.0F, 0.5F + rand.nextFloat()/5 );
        return null;
    }

	@Override
	@Nullable
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
		if ( rand.nextInt(5) == 0 )
		{
			this.playSound(SoundEvents.EVOCATION_ILLAGER_DEATH, 0.8F, 0.7F + rand.nextFloat()/10 );
			this.playSound(SoundEvents.ENTITY_ZOMBIE_PIG_HURT, 1.0F, 0.7F + rand.nextFloat()/5 );
		}
		else if ( rand.nextInt(4) == 0 )
		{
			this.playSound(SoundEvents.ENTITY_ILLAGER_DEATH, 0.8F, 0.7F + rand.nextFloat()/10 );
			this.playSound(SoundEvents.ENTITY_ZOMBIE_PIG_HURT, 1.0F, 0.7F + rand.nextFloat()/5 );
		}
		return null;
    }

	@Override
	@Nullable
    protected SoundEvent getDeathSound()
    {
		this.playSound(SoundEvents.ENTITY_ZOMBIE_PIG_DEATH, 1.0F, 1.0F + rand.nextFloat()/5 );
		return null;
    }
	
	@Override
	public void attackTargetEntityWithCurrentItem(Entity targetEntity)
	{	
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
	
	
}