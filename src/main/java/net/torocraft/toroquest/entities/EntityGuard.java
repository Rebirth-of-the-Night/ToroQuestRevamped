package net.torocraft.toroquest.entities;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.toroquest.ToroQuest;
import net.torocraft.toroquest.civilization.CivilizationHandlers;
import net.torocraft.toroquest.civilization.CivilizationType;
import net.torocraft.toroquest.civilization.CivilizationUtil;
import net.torocraft.toroquest.civilization.Province;
import net.torocraft.toroquest.civilization.player.PlayerCivilizationCapabilityImpl;
import net.torocraft.toroquest.civilization.quests.QuestCaptureEntity;
import net.torocraft.toroquest.civilization.quests.QuestCaptureFugitives;
import net.torocraft.toroquest.config.ToroQuestConfiguration;
import net.torocraft.toroquest.entities.ai.EntityAIAvoidTooClose;
import net.torocraft.toroquest.entities.ai.EntityAINearestAttackableBanditTarget;
import net.torocraft.toroquest.entities.ai.EntityAINearestAttackableCivTarget;
import net.torocraft.toroquest.entities.ai.EntityAIPatrolVillage;
import net.torocraft.toroquest.entities.ai.EntityAISmartTempt;
import net.torocraft.toroquest.entities.render.RenderGuard;

public class EntityGuard extends EntityToroNpc implements IRangedAttackMob
{
	protected final AIArcher<EntityGuard> aiArrowAttack = new AIArcher<EntityGuard>(this, 0.5D, 40, 40.0F);
    protected double randPosX;
    protected double randPosY;
    protected double randPosZ;
    protected int stance = (rand.nextInt(6)+5);
	protected float strafeVer = 0;
	protected float strafeHor = 0;
	protected int actionTimer = 0;
	protected boolean blocking = false;
	protected int blockingTimer = 0;
	protected int lastTargetY = 0;
	
	protected boolean isAnnoyed = false;
	protected int isAnnoyedTimer = 0;

	protected EntityLivingBase underAttack = null;
	protected int underAttackTimer = 0;
	
	protected EntityPlayer murderWitness = null;
	protected int murderTimer = 0;
	
	protected boolean inCombat = false;
	
	public static String NAME = "guard";
	
	static
	{
		if (ToroQuestConfiguration.specificEntityNames)
		{
			NAME = ToroQuestEntities.ENTITY_PREFIX + NAME;
		}
	}
	public static void init(int entityId)
	{
		EntityRegistry.registerModEntity(new ResourceLocation(ToroQuest.MODID, NAME), EntityGuard.class, NAME, entityId, ToroQuest.INSTANCE, 80, 2, true, 0x3f3024, 0xe0d6b9);
	}

	public static void registerRenders()
	{
		RenderingRegistry.registerEntityRenderingHandler(EntityGuard.class, new IRenderFactory<EntityGuard>()
		{
			@Override
			public Render<EntityGuard> createRenderFor(RenderManager manager)
			{
				return new RenderGuard(manager);
			}
		});
	}
	
	public EntityPlayer murderWitness()
	{
		return murderWitness;
	}
	
//	@SideOnly(Side.CLIENT)
//    public AxisAlignedBB getRenderBoundingBox()
//    {
//        return this.getEntityBoundingBox().grow(16.0);
//    }
	
	public EntityGuard(World worldIn)
	{
		this(worldIn, null);
	}

	public EntityGuard(World worldIn, CivilizationType civ)
	{
		super(worldIn, civ);
		this.setSize(0.6F, 1.93F);
		this.experienceValue = 20;
        this.setCombatTask();
		Arrays.fill(inventoryArmorDropChances, 0.3F);
		Arrays.fill(inventoryHandsDropChances, 0.3F);
		((PathNavigateGround) this.getNavigator()).setBreakDoors(true);
		this.setCanPickUpLoot(false);
		this.pledgeAllegianceIfUnaffiliated();
	}
	
	protected void entityInit()
    {
        super.entityInit();
    }
		
	@Override
    protected void applyEntityAttributes()
    {
    	super.applyEntityAttributes();
    	this.setLeftHanded(false);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(ToroQuestConfiguration.guardHealth);
    	this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D);
		this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(ToroQuestConfiguration.guardArmor);
		this.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).setBaseValue(ToroQuestConfiguration.guardArmorToughness);
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64.0D);
    	this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
    	this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.0D);
    }

	// TTASKS
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void initEntityAI()
	{
		this.tasks.addTask(1, new EntityAISwimming(this));
		//door
		// this.tasks.addTask(2, new EntityAIMoveIndoors(this));
        //this.tasks.addTask(2, new EntityAIAvoidTooClose(this, EntityToroNpc.class, 1.5F, 0.5D, 0.5D));
        	// this.tasks.addTask(8, new EntityAIMoveIndoors(this));
        	// this.tasks.addTask(9, new EntityAIRestrictOpenDoor(this));
        this.tasks.addTask(10, new EntityAIOpenDoor(this, false));
        	// this.tasks.addTask(11, new EntityAIMoveTowardsRestriction(this, 0.45D));
        // this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 0.4D));
        // movement
        this.tasks.addTask(8, new EntityAISmartTempt(this, 0.45D, Item.getByNameOrId("toroquest:recruitment_papers"))
        {
        	@Override
            public boolean shouldExecute()
            {
        		if ( inCombat || isAnnoyed || underAttackTimer > 0 || murderTimer > 0 ) 
        		{
        			return false;
        		}
            	return super.shouldExecute();
            }
        });
		this.tasks.addTask(9, new EntityAIAvoidTooClose(this, 0.45D, 0.45D));
        this.tasks.addTask(11, new EntityAIPatrolVillage(this, 0.45D));
        this.tasks.addTask(12, new EntityAIWatchClosest(this, EntityLivingBase.class, 8.0F));
        this.tasks.addTask(13, new EntityAILookIdle(this));
        
		this.targetTasks.addTask(0, new EntityAINearestAttackableCivTarget(this));
//		this.targetTasks.addTask(1, new AIDefendVillagers(this)
//		{
//			@Override
//			public boolean shouldExecute()
//			{
//				Village village = getVillage();
//
//		        if (village == null)
//		        {
//		            return false;
//		        }
//		        else
//		        {
//		            this.villageAgressorTarget = village.findNearestVillageAggressor(this.guard);
//
//		            if (this.villageAgressorTarget instanceof EntityCreeper || this.villageAgressorTarget instanceof EntityToroNpc || this.villageAgressorTarget instanceof EntityIronGolem )
//		            {
//		                return false;
//		            }
//		            else if (this.isSuitableTarget(this.villageAgressorTarget, false))
//		            {
//		                return true;
//		            }
//		            else
//		            {
//		                this.villageAgressorTarget = village.getNearestTargetPlayer(this.guard);
//		                return this.isSuitableTarget(this.villageAgressorTarget, false);
//		            }
//		        }
//			}
//		});
		this.targetTasks.addTask(1, new EntityAINearestAttackableBanditTarget(this));
		// if ( ToroQuestConfiguration.vampirismCompatability ) this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityMob.class, false));
	}

	//public final EntityAIMoveIntoArea areaAI = new EntityAIMoveIntoArea(this, 0.4D, 20, 20, 10, 32);
	//public final EntityAIMoveThroughVillage moveAI = new EntityAIMoveThroughVillage(this, 0.4D, false);


	@Override
	public boolean inCombat()
	{
		return this.inCombat;
	}
	
	@Override
	public boolean isAnnoyed()
	{
		return this.isAnnoyed;
	}
	
	public int actionTimer()
	{
		return this.actionTimer;
	}
	
	public void setActionTimer(int n)
	{
		this.actionTimer = n;
	}
	
	@Override
	public void setAnnoyed()
	{
		this.isAnnoyed = true;
		this.isAnnoyedTimer = 8;
	}
	
	@Override
	public void setUnderAttack( EntityPlayer player )
	{
		this.setAnnoyed();
		this.underAttack = player;
		this.underAttackTimer = 16;
	}
	
	@Override
	public void setMurder( EntityPlayer player )
	{
		this.setUnderAttack( player );
		this.murderWitness = player;
		this.murderTimer = 64;
	}
	
@Override
public void readEntityFromNBT(NBTTagCompound compound)
{
    super.readEntityFromNBT(compound);
    if ( compound.hasKey("raidX") && compound.hasKey("raidY") && compound.hasKey("raidZ") )
    {
    	this.raidX = compound.getInteger("raidX");
    	this.raidY = compound.getInteger("raidY");
    	this.raidZ = compound.getInteger("raidZ");
    }
}
	
	@Override
	public boolean hasHome()
	{
		return false;// ( this.getCivilization() != null );
	}
	
	public Integer raidX = null;
	public Integer raidY = null;
	public Integer raidZ = null;
	
	@Override
	public void writeEntityToNBT(NBTTagCompound compound)
	{
		super.writeEntityToNBT(compound);
		if ( this.raidX != null && this.raidY != null && this.raidZ != null )
		{
			compound.setInteger("raidX", this.raidX);
			compound.setInteger("raidY", this.raidY);
			compound.setInteger("raidZ", this.raidZ);
		}
	}
	
	@Override
	public void onLivingUpdate()
	{
		super.onLivingUpdate();
		
//		if ( this.world.isRemote )
//		{
//			return;
//		}

		//EntityLivingBase attacker = this.getAttackTarget();
		
		if ( this.world.isRemote )
		{
			return;
		}
		
       	if ( this.ticksExisted % 100 == 0 )
    	{
       		this.pledgeAllegianceIfUnaffiliated();
       		
       		if ( this.getHealth() >= this.getMaxHealth() )
			{
				if ( !this.inCombat && this.underAttackTimer <= 0 )
				{
					this.hitSafety = true;
					BlockPos pos = this.getPosition();
					IBlockState block = world.getBlockState(pos);
					if ( block instanceof BlockLiquid )
					{
				        Vec3d vec3d = RandomPositionGenerator.getLandPos(this, 8, 4);
				        if ( vec3d != null )
				        {
				        	this.getNavigator().tryMoveToXYZ(vec3d.x, vec3d.y, vec3d.z, 0.5);
				        }
					}
				}
			}
       		else this.heal(1.0f);
    		
    		if ( this.isAnnoyedTimer > 0 && rand.nextInt(2) == 0 )
    		{
    			if ( --this.isAnnoyedTimer < 1 )
    			{
    				this.isAnnoyed = false;
    			}
    		}
    		
    		if ( this.underAttackTimer > 0 && rand.nextBoolean() )
    		{
    			if ( !this.inCombat )
    			{
    				this.underAttackTimer -= 5;
    			}
    			
    			if ( --this.underAttackTimer < 1 )
    			{
    				this.underAttack = null;
    				
    				// guards will drop aggro on players if not attacked for some time
    				if ( this.getAttackTarget() != null && this.getAttackTarget() instanceof EntityPlayer )
    				{
    					this.blocking = false;
    					this.blockingTimer = -200;
    					// this.activeItemStackUseCount = 0;
    			    	this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.0D);
    					this.resetActiveHand();
    					this.playSound(SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 1.0F, 0.8F + rand.nextFloat()/5 );
    					//if ( !this.world.isRemote )
    					{
    						this.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Items.BOW, 1));
    						this.setHeldItem(EnumHand.OFF_HAND, ItemStack.EMPTY);
    					}
    					this.strafeVer = 0.0F;
    					this.strafeHor = 0.0F;
    			    	this.getMoveHelper().strafe( 0.0F, 0.0F );
    			    	this.getNavigator().clearPath();
    					this.setAttackTarget(null);
    				}
    			}
    		}
    		
    		if ( this.murderTimer > 0 && rand.nextInt(3) == 0 )
    		{
    			if ( --this.murderTimer < 1 )
    			{
    				this.murderWitness = null;
    			}
    		}
    		    
    		
    		
    		
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
	    		
	    		if ( rand.nextBoolean() && this.raidX != null && this.raidY != null && this.raidZ != null && this.murderTimer <= 0 && this.underAttackTimer <= 0 ) 
	    		{
	        		BlockPos pos = new BlockPos( this.raidX, this.raidY, this.raidZ );
	        		this.getNavigator().tryMoveToXYZ(pos.getX()+0.5, pos.getY()+rand.nextInt(3)-1, pos.getZ()+0.5, 0.45D);
	    		}
    		}
    		else
    		{
	    		if ( this.getAttackTarget() != null )
	    		{
		    		List<EntityGuard> guards = this.world.getEntitiesWithinAABB(EntityGuard.class, new AxisAlignedBB(this.getPosition()).grow(12, 12, 12), new Predicate<EntityGuard>()
		    		{
		    			public boolean apply(@Nullable EntityGuard entity)
		    			{
		    				return true;
		    			}
		    		});
		    		for (EntityGuard guard: guards)
		    		{
		    			if ( guard.getAttackTarget() == null && guard.canEntityBeSeen( this.getAttackTarget() ) )
		    			{
							guard.setAttackTarget( this.getAttackTarget() );
		    			}
		    		}
	    		}
	    		
	    		if ( this.getAttackTarget() == null || !this.getAttackTarget().isEntityAlive() )
	        	{
	    			this.setAttackTarget( this.getRevengeTarget() );
	        	}
    		}
    	}
       	
    	// if has an attacker
		if ( this.getAttackTarget() != null && this.getAttackTarget().isEntityAlive() && !(this.getAttackTarget() instanceof EntityToroNpc) && !(this.getAttackTarget() instanceof EntityVillager) && !(this.getAttackTarget() instanceof EntityGolem) )
		{
	        double dist = this.getDistanceSq(this.getAttackTarget());
	        //double d = this.getDistance(attacker);
	        this.lastTargetY = (int)Math.abs(this.posY - this.getAttackTarget().posY);
	        ItemStack iStack = this.getHeldItemMainhand();
	        if ( !this.inCombat )
	        {
	            if ( rand.nextBoolean() && this.actionTimer <= 0 )
	            {
	            	this.playSound( SoundEvents.VINDICATION_ILLAGER_AMBIENT, 1.0F, 0.9F + rand.nextFloat()/5 );
	            	this.actionTimer = 1;
	            }
				this.getMoveHelper().strafe( 0.0F, 0.0F );
	        	this.getNavigator().clearPath();
				this.resetActiveHand();
				this.inCombat = true;
				this.strafeVer = 0.8F;
				this.strafeHor = getStrafe(stance);
				//this.canSwap = 0;
	        	//if ( this.getNavigator().getPathToEntityLiving(attacker) == null )
	        	if ( !this.getNavigator().tryMoveToEntityLiving(this.getAttackTarget(), this.strafeVer) )
				{
	        		this.blockingTimer = -200;
	        	}
	        	else
	        	{
	        		//this.blockingTimer = 0;
	        	}
	        }
	        // 199,   0 < -201
	        // if within range and has not been in melee range for a short amount of time, or very close and has not been in melee range for a long amount of time
			if ( ( (dist < 200+this.blockingTimer && this.blockingTimer > -200 && this.lastTargetY < 6) || (dist <= 16 && this.lastTargetY < 4 && this.canEntityBeSeen(this.getAttackTarget())) ) )
	        {
				// if this does not have a sword, swap to sword and board
				if ( iStack != null && (iStack.getItem() instanceof ItemBow) ) // SSS
				{
					this.resetActiveHand();
					this.playSound(SoundEvents.ITEM_ARMOR_EQUIP_IRON, 1.0F, 0.9F + rand.nextFloat()/10 );
					this.getMoveHelper().strafe( 0.0F, 0.0F );
	            	this.getNavigator().clearPath();
	            	if ( !this.world.isRemote )
					{
	            		setMeleeWeapon();
					}
					this.blockingTimer = 0;
					this.strafeVer = 0.8F;
					this.strafeHor = getStrafe(stance);
				}
				// if this is not blocking, is within range, and block is ready, start blocking
				if ( !this.blocking && dist <= 12 && this.blockingTimer <= -( (int)(this.stance*8-dist) ) && this.getHealth()/this.getMaxHealth() <= 0.8 )
				{
					this.stance = (rand.nextInt(6)+5);
					this.blockingTimer = (int)(this.stance*8-dist); // (this.stance*6+dist)
					this.blocking = true;
					this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
					this.resetActiveHand();
					this.setActiveHand(EnumHand.OFF_HAND);
					this.updateActiveHand();
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
					
					if ( this.getAttackTarget() != null )
					{
						if ( this.getNavigator().tryMoveToEntityLiving(this.getAttackTarget(), this.strafeVer) ) // move this up to ^
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
			else if ( iStack != null && !(iStack.getItem() instanceof ItemBow) )
			{
//		    	System.out.println ( attacker + " " + this.getRevengeTarget() + " " + this.world.isRemote );
//				if ( this.getAttackTarget() == null ) this.setAttackTarget(this.getRevengeTarget());
//		    	System.out.println ( this.getRevengeTarget() + " " + this.world.isRemote );
				this.blocking = false;
				this.blockingTimer = -200;
				// this.activeItemStackUseCount = 0;
		    	this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.0D);
				this.resetActiveHand();
				this.playSound(SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 1.0F, 0.9F + rand.nextFloat()/10 );
				//if ( !this.world.isRemote )
				{
					this.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Items.BOW, 1));
					this.setHeldItem(EnumHand.OFF_HAND, ItemStack.EMPTY);
				}
				this.strafeVer = 0.0F;
				this.strafeHor = 0.0F;
		    	this.getMoveHelper().strafe( 0.0F, 0.0F );
		    	this.getNavigator().clearPath();
		    	// this.setAttackTarget(attacker);
			}
			this.blockingTimer--;
		}
		else if ( this.blocking || this.inCombat ) // end of combat
		{
			this.inCombat = false;
			this.blocking = false;
			this.setAttackTarget(null);
			this.resetActiveHand();
			this.activeItemStackUseCount = 0;
	    	this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.0D);
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
	
	boolean hitSafety = true;
	
	//========================== when this guard is damaged ===========================
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		//if ( this.world.isRemote ) return false;

		Entity e = source.getTrueSource();
		
		if ( source == DamageSource.IN_WALL || source == DamageSource.CRAMMING ) 
		{
			return false;
		}
		
		if ( source == DamageSource.FALL )
		{
			amount = amount/4;
			if ( amount <= 1 )
			{
				return false;
			}
			return super.attackEntityFrom(source, amount);
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
				else if ( this.rand.nextBoolean() )
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
			if ( source.isFireDamage() || source.isExplosion() || source.isMagicDamage() || source.isProjectile() )
			{
				return super.attackEntityFrom(source, amount);
			}
			return false;
		}

		if ( e instanceof EntityToroNpc || e instanceof EntityVillager )
		{
			return false;
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
		
		if ( e instanceof EntityPlayer )
		{
			if ( this.hitSafety && this.getAttackTarget() != e )
			{
				this.hitSafety = false;
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
			
			int entities = this.world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(this.getPosition()).grow(2, 2, 2), new Predicate<EntityLivingBase>()
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
			}).size();
			
			if ( entities > 0 )
			{
				return false;
			}
			
			if (super.attackEntityFrom(source, amount))
			{
				adjustRep(e,-(int)MathHelper.clamp(amount*4,5,this.getHealth()*4));
				this.setUnderAttack((EntityPlayer)e);
				return true;
			}

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
		
		return super.attackEntityFrom(source, amount);
	}
	
	private void adjustRep(Entity entity, int amount)
	{
		if (entity == null)
		{
			return;
		}
		if (!(entity instanceof EntityPlayer))
		{
			return;
		}
		EntityPlayer player = (EntityPlayer) entity;
		CivilizationType civ = getCivilization();
		if (civ == null)
		{
			return;
		}
		CivilizationHandlers.adjustPlayerRep(player, civ, amount);
	}
	
	// public static final DataParameter<String> RENDER = EntityDataManager.<String>createKey(EntityGuard.class, DataSerializers.STRING);
	

	
	// ========================= CIV ==========================
	
	@Override
	public void setCivilization(CivilizationType civ)
	{
		if (civ == null)
		{
			dataManager.set(CIV, "");
		}
		else
		{
			dataManager.set(CIV, civ.toString());
			c = civ;
			this.tasks.removeTask(followNoCiv);
		}
		dataManager.setDirty(CIV);
	}
	
	@Override
	protected void pledgeAllegianceIfUnaffiliated()
	{
		if ( this.getCivilization() != null )
		{
			return;
		}

		Province civ = CivilizationUtil.getProvinceAt(this.world, this.chunkCoordX, this.chunkCoordZ);

		if (civ == null || civ.civilization == null)
		{
			return;
		}
		
		this.setCivilization(civ.civilization);
	}
	
	@Override
	public CivilizationType getCivilization()
	{
		if (c != null)
		{
			return c;
		}
		return enumCiv(dataManager.get(CIV));
	}
	
	CivilizationType c = null;
	
	// ========================================================
	
    private boolean canBlockDamageSource(DamageSource damageSourceIn)
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
	@Nullable
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata)
	{
		livingdata = super.onInitialSpawn(difficulty, livingdata);
		((PathNavigateGround) this.getNavigator()).setBreakDoors(true);
		this.setCanPickUpLoot(false);
		this.setLeftHanded(false);
		this.pledgeAllegianceIfUnaffiliated();
		if ( !this.world.isRemote )
		{
			setMeleeWeapon();
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
        this.setCombatTask();
        this.raidX = (int)this.posX;
		this.raidY = (int)this.posY;
		this.raidZ = (int)this.posZ;
		CivilizationType civ = this.getCivilization();
        if ( civ == null || ( civ != null && civ.toString() == "" ) ) this.tasks.addTask(9, followNoCiv);
		this.writeEntityToNBT(new NBTTagCompound());
		return livingdata;
	}
	

	
	protected void setMeleeWeapon()
	{
		CivilizationType civ = this.getCivilization();
		if ( civ == null )
		{
			this.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Items.IRON_SWORD, 1));
    		this.setHeldItem(EnumHand.OFF_HAND, new ItemStack(Items.SHIELD, 1));
    		return;
		}
		switch ( civ )
		{
			case FIRE:
			{
				this.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Item.getByNameOrId(ToroQuestConfiguration.guardWeapon_RED_BRIAR), 1));
	    		this.setHeldItem(EnumHand.OFF_HAND, new ItemStack(Item.getByNameOrId(ToroQuestConfiguration.guardShield_RED_BRIAR), 1));
	    		return;
			}
			case EARTH:
			{
				this.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Item.getByNameOrId(ToroQuestConfiguration.guardWeapon_GREEN_WILD), 1));
	    		this.setHeldItem(EnumHand.OFF_HAND, new ItemStack(Item.getByNameOrId(ToroQuestConfiguration.guardShield_GREEN_WILD), 1));
	    		return;
			}
			case SUN:
			{
				this.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Item.getByNameOrId(ToroQuestConfiguration.guardWeapon_YELLOW_DAWN), 1));
	    		this.setHeldItem(EnumHand.OFF_HAND, new ItemStack(Item.getByNameOrId(ToroQuestConfiguration.guardShield_YELLOW_DAWN), 1));
	    		return;
			}
			case WIND:
			{
				this.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Item.getByNameOrId(ToroQuestConfiguration.guardWeapon_BROWN_MITHRIL), 1));
	    		this.setHeldItem(EnumHand.OFF_HAND, new ItemStack(Item.getByNameOrId(ToroQuestConfiguration.guardShield_BROWN_MITHRIL), 1));
	    		return;
			}
			case MOON:
			{
				this.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Item.getByNameOrId(ToroQuestConfiguration.guardWeapon_BLACK_MOOR), 1));
	    		this.setHeldItem(EnumHand.OFF_HAND, new ItemStack(Item.getByNameOrId(ToroQuestConfiguration.guardShield_BLACK_MOOR), 1));
	    		return;
			}
			case WATER:
			{
				this.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Item.getByNameOrId(ToroQuestConfiguration.guardWeapon_BLUE_GLACIER), 1));
	    		this.setHeldItem(EnumHand.OFF_HAND, new ItemStack(Item.getByNameOrId(ToroQuestConfiguration.guardShield_BLUE_GLACIER), 1));
	    		return;
			}
			default:
			{
				this.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Items.IRON_SWORD, 1));
	    		this.setHeldItem(EnumHand.OFF_HAND, new ItemStack(Items.SHIELD, 1));
	    		return;
			}
		}
	}
	

	public static BlockPos findSpawnSurface( World world, BlockPos pos )
	{
		pos = pos.up(20);
		IBlockState blockState;
		int yOffset = 20;
		boolean[] airSpace = { false, false };

		while ( yOffset > 0 )
		{
			blockState = world.getBlockState(pos);

			if ( blockState.getBlock() == Blocks.AIR )
			{
				if (airSpace[0])
				{
					airSpace[1] = true;
				}
				else
				{
					airSpace[0] = true;
				}
			}
			else if ( blockState.getBlock() instanceof BlockLiquid )
			{
				break;
			}
			else if ( blockState.getBlock() instanceof BlockFence )
			{
				return null;
			}
			else if ( !(blockState.getBlock() instanceof BlockAir) )
			{
				if (airSpace[0] && airSpace[1])
				{
					return pos.up();
				}
				else
				{
					airSpace[0] = false;
					airSpace[1] = false;
				}
			}
			else
			{
				airSpace[0] = false;
				airSpace[1] = false;
			}
			pos = pos.down();
			yOffset--;
		}
		return null;
	}
	
@Override
protected boolean processInteract(EntityPlayer player, EnumHand hand)
{
	
	if ( !this.isEntityAlive() )
	{
		return false;
	}
	
	List<EntityFugitive> fugitives = player.world.getEntitiesWithinAABB(EntityFugitive.class, new AxisAlignedBB(player.getPosition()).grow(8, 4, 8), new Predicate<EntityFugitive>()
	{
		public boolean apply(@Nullable EntityFugitive entity)
		{
			return true;
		}
	});
	for (EntityFugitive v : fugitives)
	{
		if ( !this.inCombat && v != null && v.isEntityAlive() && v.getLeashHolder() != null && ( this.murderWitness == null || this.murderWitness != player ) && ( this.underAttack == null || this.underAttack != player ) )
		{
			
			if ( !this.world.isRemote ) 
			{
	            try
	            {
	            	QuestCaptureFugitives.INSTANCE.onReturn(player);
	            	switch ( rand.nextInt(6) )
	            	{
		            	case 0:chat(player, "Thank you for turning in this fugitive.");break;
		            	case 1:chat(player, "I'll take it from here. Thank you, " + player.getName() + "." );break;
		            	case 2:chat(player, "I appreciate the help. Thank you for capturing this fugitive." );break;
		            	case 3:chat(player, "Crime must not go unpunished. I'll escort this criminal to the stockades at once, sir." );break;
		            	case 4:chat(player, "We've been looking all over for this one! The guard recognizes your service, " + player.getName() + "." );break;
		            	case 5:chat(player, "Sir! Thank you for capturing this criminal.");break;
	            	}

	    	        this.playSound( SoundEvents.BLOCK_ANVIL_LAND, 0.8F, 0.8F );
	    	        this.playSound( SoundEvents.ENTITY_VILLAGER_NO, 0.8F, 0.8F );
	    	        v.setDead();
	    			v.setHealth(0);
		            CivilizationHandlers.adjustPlayerRep(player, (int)(player.posX / 16), (int)(player.posZ / 16), ToroQuestConfiguration.returnFugitiveRepGain);
		            this.actionTimer = 3;
	    	        return true;
	            }
	            catch(Exception e)
	            {
	            	
	            }
			}
		}
	}
	
	List<EntityToroQuest> toros = player.world.getEntitiesWithinAABB(EntityToroQuest.class, new AxisAlignedBB(player.getPosition()).grow(8, 4, 8), new Predicate<EntityToroQuest>()
	{
		public boolean apply(@Nullable EntityToroQuest entity)
		{
			return true;
		}
	});
	for (EntityToroQuest v : toros)
	{
		if ( !this.inCombat && v != null && v.isEntityAlive() && v.getLeashHolder() != null && ( this.murderWitness == null || this.murderWitness != player ) && ( this.underAttack == null || this.underAttack != player ) )
		{
			if ( !this.world.isRemote ) 
			{
	            try
	            {
	            	if ( QuestCaptureEntity.INSTANCE.onReturn(player) )
	            	{
						chat(player, "Thank you for returning our lord's toro, " + player.getName() + ".");
						v.setDead();
						v.setHealth(0);
						this.playSound( SoundEvents.BLOCK_ANVIL_LAND, 0.8F, 0.8F );
				        this.playSound( SoundEvents.ENTITY_COW_HURT, 0.8F, 0.8F );
			            CivilizationHandlers.adjustPlayerRep(player, (int)(player.posX / 16), (int)(player.posZ / 16), ToroQuestConfiguration.returnFugitiveRepGain);
			            this.actionTimer = 3;
				        return true;
	            	}
	            }
	            catch(Exception e)
	            {
	            	
	            }
			}
		}
	}
	
	
	
	//if ( !this.world.isRemote ) 
	{
		try
		{
			ItemStack itemstack = player.getHeldItem(hand);
			Item item = itemstack.getItem();
			CivilizationType civ = this.getCivilization();
			
			int rep = PlayerCivilizationCapabilityImpl.get(player).getReputation(civ);
			
			if ( item.equals(Item.getByNameOrId("toroquest:recruitment_papers") ) && this.actionTimer <= 0 )
	        {
    			this.actionTimer = 1;
	        	if ( rep >= 0 && ( this.murderWitness == null || this.murderWitness != player ) && ( this.underAttack == null || this.underAttack != player ) )
	    		{
	        		if ( player.isSneaking() )
	        		{
						BlockPos pos = findSpawnSurface( world, this.getPosition() );
						if ( pos != null )
						{
							this.setPosition(pos.getX()+0.5, pos.getY(), pos.getZ()+0.5);
							this.raidX = pos.getX();
							this.raidY = pos.getY();
							this.raidZ = pos.getZ();
							this.writeEntityToNBT(new NBTTagCompound());
				        	playSound(SoundEvents.BLOCK_DISPENSER_LAUNCH, 1.0F, 1.0F);
							player.sendStatusMessage(new TextComponentString( "§oGuard posted at [" + this.raidX + ", " + this.raidY + ", " + this.raidZ + "]§r" ), true);
				        	playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
						}
						else
						{
							player.sendStatusMessage(new TextComponentString( "§oInvalid post location.§r" ), true);
				        	playSound(SoundEvents.BLOCK_NOTE_PLING, 0.8F, 1.0F);
						}
					}
	        		else
	        		{
	        			this.raidX = (int)this.posX;
	        			this.raidY = (int)this.posY;
	        			this.raidZ = (int)this.posZ;
						this.writeEntityToNBT(new NBTTagCompound());
						player.sendStatusMessage(new TextComponentString( "§oGuard posted at [" + this.raidX + ", " + this.raidY + ", " + this.raidZ + "]§r" ), true);
			        	playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
	        		}
	    		}
	        	else
	        	{
	        	    this.playSound( SoundEvents.VINDICATION_ILLAGER_AMBIENT, 1.0F, 0.9F );
	        	}
	        	return true;
	        }
			else if ( item == Items.EMERALD && rep < 0 )
			{
				// rep -7
				// emeralds = 2   8   12
				// 26
				int maxRepGain = -rep;
				int emeraldRep = itemstack.getCount()*ToroQuestConfiguration.donateEmeraldRepGain;
				if ( emeraldRep > maxRepGain )
				{
					chat(player,"You've cleared your bounty.");
	    			this.actionTimer = 1;
					int remainder = emeraldRep-maxRepGain;
					adjustRep( player, maxRepGain );
		        	player.setHeldItem( hand, new ItemStack( item, (int)(remainder/ToroQuestConfiguration.donateEmeraldRepGain) ) );
		        	this.underAttack = null;
		        	this.murderWitness = null;
				}
				else
				{
					chat(player,"There's still a bounty on your head.");
	    			this.actionTimer = 1;
					adjustRep( player, emeraldRep );
		        	player.setHeldItem( hand, new ItemStack( Items.AIR, 0 ) );
				}
				this.setAttackTarget(null);
				return true;
			}
		}
		catch ( Exception e )
		{
			if ( this.actionTimer <= 0 )
			{
			    this.playSound( SoundEvents.VINDICATION_ILLAGER_AMBIENT, 1.0F, 0.9F );
    			this.actionTimer = 1;
			}
		}
		if ( !this.inCombat && this.actionTimer <= 0 )
		{
			this.actionTimer = 3;
			this.getNavigator().clearPath();
            this.getLookHelper().setLookPosition(player.posX, player.posY + (double)player.getEyeHeight(), player.posZ, (float)this.getHorizontalFaceSpeed(), (float)this.getVerticalFaceSpeed());
			return this.guardSpeak(player);
		}
	}
	return false;
}
	
	

void chat(EntityPlayer player, String message)
{
	if ( ToroQuestConfiguration.guardsHaveDialogue )
	{
		if ( player.world.isRemote )
		{
			return;
		}
		player.sendMessage(new TextComponentString( "§l" + this.getName() + "§r: " + message));
	    this.playSound( SoundEvents.VINDICATION_ILLAGER_AMBIENT, 1.0F, 0.9F + rand.nextFloat()/5 );
	}
}

protected final EntityAISmartTempt followNoCiv = new EntityAISmartTempt(this, 0.625D, Items.AIR)
{
	@Override
	public boolean shouldExecute()
    {
        if ( underAttack instanceof EntityPlayer || murderWitness instanceof EntityPlayer || inCombat )
        {
	        return false;
        }
        return super.shouldExecute();
    }
	
	@Override
	protected boolean isTempting(ItemStack stack)
    {
		if ( underAttack instanceof EntityPlayer || murderWitness instanceof EntityPlayer || inCombat )
        {
	        return false;
        }
        return true;
    }
};

public void setCombatTask()
{
    //if (this.world != null && !this.world.isRemote)
    {
	    this.aiArrowAttack.setAttackCooldown(40);
		this.tasks.addTask(6, new AIAttackWithSword(this, 0.6D, true));
		this.tasks.addTask(7, this.aiArrowAttack);
		// CivilizationType civ = this.getCivilization();
        // if ( civ == null || ( civ != null && civ.toString() == "" ) ) this.tasks.addTask(9, followNoCiv);
    }
    // [RESET]
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

@Override
public float getEyeHeight()
{
    return 1.935F;
}

@Override
public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor)
{
	if ( target == null ) return;
    EntityArrow entityarrow = this.getArrow(distanceFactor);
    // if ( ToroQuestConfiguration.fireArrowsOnGuards ) entityarrow.setFire(12);
    entityarrow.setIsCritical(true);
    entityarrow.setDamage(entityarrow.getDamage()*(rand.nextFloat()/2+1));
    double d0 = target.posX - this.posX;
    //double d1 = target.getEntityBoundingBox().minY - entityarrow.posY;
    double d1 = target.getEntityBoundingBox().minY + target.height/4.0 - entityarrow.posY - 1 - rand.nextDouble();
    double d2 = target.posZ - this.posZ;
    double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
    entityarrow.shoot( d0, d1 + d3 * 0.2D, d2, 2.35F, 1.0F );
    this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.5F + 0.8F));
    this.world.spawnEntity(entityarrow);
}

protected EntityArrow getArrow(float p_190726_1_)
{
    return new EntitySmartArrow(this.world, this);
}

@Override
public void setItemStackToSlot(EntityEquipmentSlot slotIn, ItemStack stack)
{
    super.setItemStackToSlot(slotIn, stack);

    if ( slotIn == EntityEquipmentSlot.MAINHAND ) // remote!
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
	
//	protected boolean isFoe(EntityPlayer target)
//	{
//		EntityToroNpc npc = (EntityToroNpc) this;
//		CivilizationType civ = npc.getCivilization();
//		if (civ == null)
//		{
//			return false;
//		}
////		int rep = PlayerCivilizationCapabilityImpl.get(target).getReputation(civ);
////		if ( (rep > -100 && rep < 1000 && rand.nextInt(10 - (int)(rep/100)) < 5 ) || rep >= 1000 )
////		{
////			if ( this.getRevengeTarget() != target ) return false;
////		}
//		return true;
//	}
	
	// ======================================== old combat tweaks =========================================
	
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
		if ( victim == null || !victim.isEntityAlive() )
		{
			setAttackTarget(null);
			return false;
		}
		if ( victim instanceof EntityToroNpc || victim instanceof EntityIronGolem || victim instanceof EntityVillager )
		{
			setAttackTarget(null);
			return false;
		}
		
		if ( victim instanceof EntityLivingBase && ((EntityLivingBase)victim).getHealth() <= 0 )
		{
			setAttackTarget(null);
		}
			//super.attackEntityAsMob(victim);
			attackTargetEntityWithCurrentItem(victim); // if there are errors, remove this
			
			if ( victim instanceof EntityPlayer )
			{
				EntityPlayer player = (EntityPlayer)victim;
				if ( !player.world.isRemote )
				{
					if ( rand.nextInt(16) == 0 )
					{
						insult(player);
					}
				}
			}
			return true;
	}
	
	public void insult(EntityPlayer player)
	{
		switch ( rand.nextInt(21) )
		{
			case 0:
			{
				chat(player,"Taste my steel!");
				this.setAttackTarget(player);
				break;
			}
			case 1:
			{
				chat(player,"How dare you show your face here, criminal!");
				this.setAttackTarget(player);
				break;
			}
			case 2:
			{
				chat(player,"I will paint the earth red with your blood, filthy outlaw!");
				this.setAttackTarget(player);
				break;
			}
			case 3:
			{
				chat(player,"You will pay for your crimes!");
				this.setAttackTarget(player);
				break;
			}
			case 4:
			{
				chat(player,"How dare you show your face here, criminal!");
				this.setAttackTarget(player);
				break;
			}
			case 5:
			{
				chat(player,"Die you coward!");
				this.setAttackTarget(player);
				break;
			}
			case 6:
			{
				chat(player,"I'll break you like you broke the law!");
				this.setAttackTarget(player);
				break;
			}
			case 7:
			{
				chat(player,"Pay with your blood!");
				this.setAttackTarget(player);
				break;
			}
			case 8:
			{
				chat(player,"I'll gut you!");
				this.setAttackTarget(player);
				break;
			}
			case 9:
			{
				chat(player,"Die, filth!");
				this.setAttackTarget(player);
				break;
			}
			case 10:
			{
				chat(player,"Weakling!");
				this.setAttackTarget(player);
				break;
			}
			case 11:
			{
				chat(player,"I will be your End.");
				this.setAttackTarget(player);
				break;
			}
			case 12:
			{
				chat(player,"Griefer!");
				this.setAttackTarget(player);
				break;
			}
			case 13:
			{
				chat(player,"I hope you rot in the Nether, coward!");
				this.setAttackTarget(player);
				break;
			}
			case 14:
			{
				chat(player,"Criminal!");
				this.setAttackTarget(player);
				break;
			}
			case 15:
			{
				chat(player,"Your kind disgusts me!");
				this.setAttackTarget(player);
				break;
			}
			case 16:
			{
				chat(player,"Your death will be swift!");
				this.setAttackTarget(player);
				break;
			}
			case 17:
			{
				chat(player,"There will be justice!");
				this.setAttackTarget(player);
				break;
			}
			case 18:
			{
				chat(player,"For the king!");
				this.setAttackTarget(player);
				break;
			}
			case 19:
			{
				chat(player,"Filthy outlaw!");
				this.setAttackTarget(player);
				break;
			}
			case 20:
			{
				chat(player,"Go ahead. Try me.");
				this.setAttackTarget(player);
				break;
			}
		}
	}
	
	public void attackTargetEntityWithCurrentItem(Entity targetEntity)
	{
		if ( rand.nextInt(5) == 0 )
        {
        	this.playSound( SoundEvents.VINDICATION_ILLAGER_AMBIENT, 1.0F, 0.9F + rand.nextFloat()/5 );
        }
		
		if (targetEntity.canBeAttackedWithItem())
		{
			if (!targetEntity.hitByEntity(this))
			{
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
								if ( !world.isRemote ) targetEntity.addVelocity((double) (-MathHelper.sin(this.rotationYaw * 0.017453292F) * (float) i * 0.5F), 0.1D,
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
	
	@Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
		if ( rand.nextInt(4) == 0 )
		{
			this.playSound(SoundEvents.VINDICATION_ILLAGER_DEATH, 1.0F, 0.9F + rand.nextFloat()/5 );
		}
		return super.getHurtSound(damageSourceIn);
    }
	
	@Override
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
        return null;
    }

	@Override
    protected SoundEvent getAmbientSound()
    {
        return null;
    }
	
	public boolean guardSpeak(EntityPlayer player)
	{
		CivilizationType civ = this.getCivilization();
		boolean bandit = false;
		for ( ItemStack itemStack : player.getArmorInventoryList() )
		{
			if ( itemStack.getItem().equals(Item.getByNameOrId("toroquest:bandit_helmet") ) || itemStack.getItem().equals(Item.getByNameOrId("toroquest:legendary_bandit_helmet") ) )
			{
				bandit = true;
			}
		}
		
		if ( civ == null )
		{
			if ( bandit || ( this.murderWitness != null && this.murderWitness == player ) || ( this.underAttack != null && this.underAttack == player ) )
			{
				this.actionTimer = 30;
				if ( !this.world.isRemote )
				{
					insult(player);
				}
			}
			else
			{
				if ( !this.world.isRemote )
				{
					int caravans = world.getEntitiesWithinAABB(EntityCaravan.class, new AxisAlignedBB(getPosition()).grow(32, 16, 32), new Predicate<EntityCaravan>()
					{	
		    			public boolean apply(@Nullable EntityCaravan entity)
		    			{
		    				return true;
		    			}
			    	}).size();
					
					if ( caravans > 0 )
					{
						switch ( rand.nextInt(7) )
						{
							case 0:
							{
								chat(player,"There is a village nearby. Do you mind escorting us there, adventurer?");
								break;
							}
							case 1:
							{
								chat(player,"We are headed to town, but these lands are dangerous. Will you guide us?");
								break;
							}
							case 2:
							{
								chat(player,"Greetings, adventurer, we are on our way to town. Which province do you hail from?");
								break;
							}
							case 3:
							{
								chat(player,"Thank the Aether you are not a bandit, you had us worried.");
								break;
							}
							case 4:
							{
								chat(player,"Careful out here, wouldn't want you getting hurt.");
								break;
							}
							case 5:
							{
								chat(player,"We are quite lost, adventurer. Do you know the way to the nearest province?");
								break;
							}
							case 6:
							{
								chat(player,"Will you show us to the nearest village, adventurer?");
								break;
							}
						}
					}
					else
					{
						switch ( rand.nextInt(5) )
						{
							case 0:
							{
								chat(player,"Thank the Aether you are not a bandit, you had me worried.");
								break;
							}
							case 1:
							{
								chat(player,"My caravan was slaughtered by filthy bandits! We must head to the nearest town before we encounter any more.");
								break;
							}
							case 2:
							{
								chat(player,"I lost the rest of my scouting party from a bandit raid... we must head back to town quickly and report the attack!");
								break;
							}
							case 3:
							{
								chat(player,"My caravan was butchered by monsters! We must get to town quickly, it is not safe here.");
								break;
							}
							case 4:
							{
								chat(player,"My scouting party was torn apart by a hoard of zombies... please, we need to get back to town.");
								break;
							}
							case 5:
							{
								chat(player,"Careful out here, wouldn't want you getting hurt.");
								break;
							}
						}
					}
				}
			}
			return true;
		}
		else if ( bandit )
		{
			switch ( rand.nextInt(5) )
			{
				case 0:
				{
					chat(player,"Filthy bandit!");
					break;
				}
				case 1:
				{
					chat(player,"Die, bandit scum!");
					break;
				}
				case 2:
				{
					chat(player,"You will pay with your blood!");
					break;
				}
				case 3:
				{
					chat(player,"Bandit scum!");
					break;
				}
				case 4:
				{
					chat(player,"I will paint the earth red with your blood, filthy bandit!");
					break;
				}
				case 5:
				{
					chat(player,"Die, bandit!");
					break;
				}
			}
			return true;
		}
		
		int rep = PlayerCivilizationCapabilityImpl.get(player).getReputation(civ);
		rep = (int)(rep * 1+((double)(rand.nextInt(5))/10));
		
		if ( rep <= -50 || ( this.murderWitness != null && this.murderWitness == player ) || ( this.underAttack != null && this.underAttack == player ) )
		{
			if ( !this.world.isRemote )
			{
				insult(player);
			}
		}
		else if ( this.isAnnoyed )
		{
			switch ( rand.nextInt(10) )
			{
				case 0:
				{
					chat(player,"Stay out of trouble.");
					break;
				}
				case 1:
				{
					chat(player,"Causing trouble now, are we?");
					break;
				}
				case 2:
				{
					chat(player,"I'd be careful if I were you.");
					break;
				}
				case 3:
				{
					chat(player,"Stay out of trouble, traveler.");
					break;
				}
				case 4:
				{
					chat(player,"Disrespect the law, and you disrespect me.");
					break;
				}
				case 5:
				{
					chat(player,"Stop. You are causing a disturbance.");
					break;
				}
				case 6:
				{
					chat(player,"I'm keeping an eye on you.");
					break;
				}
				case 7:
				{
					chat(player,"This is your last warning.");
					break;
				}
				case 8:
				{
					chat(player,"Go ahead. Try me.");
					break;
				}
			}
		}
		else if ( this.getHealth() < this.getMaxHealth()/2 )
		{
			switch ( rand.nextInt(6) )
			{
				case 0:
				{
					chat(player,"I am quite wounded.");
					break;
				}
				case 1:
				{
					chat(player,"I got pretty scraped up, I may need a healer.");
					break;
				}
				case 2:
				{
					chat(player,"That one got me right in the knee. How bad does it look?");
					break;
				}
				case 3:
				{
					chat(player,"Ah bloody Nether! I need a healing potion to fix this wound.");
					break;
				}
				case 4:
				{
					chat(player,"It hurts... I think I'm bleeding.");
					break;
				}
				case 5:
				{
					chat(player,"I took some damage, I need healing.");
					break;
				}
			}
		}
		else if ( rep < 250 )
		{
			switch ( rand.nextInt(30) )
			{
				case 0:
				{
					chat(player,"Watch yourself, traveler.");
					break;
				}
				case 1:
				{
					chat(player,"I'd be careful if I were you.");
					break;
				}
				case 2:
				{
					chat(player,"You're a bit far from home, traveler.");
					break;
				}
				case 3:
				{
					chat(player,"Let me guess... someone stole your diamonds.");
					break;
				}
				case 4:
				{
					chat(player,"Causing trouble now, are we?");
					break;
				}
				case 5:
				{
					chat(player,"Everything alright?");
					break;
				}
				case 6:
				{
					chat(player,"For the king!");
					break;
				}
				case 7:
				{
					chat(player,"I live to serve.");
					break;
				}
				case 8:
				{
					chat(player,"Another day another diamond.");
					break;
				}
				case 9:
				{
					chat(player,"Yes?");
					break;
				}
				case 10:
				{
					chat(player,"What is it?");
					break;
				}
				case 11:
				{
					chat(player,"Stay out of trouble, traveler.");
					break;
				}
				case 12:
				{
					chat(player,"Disrespect the law, and you disrespect me.");
					break;
				}
				case 13:
				{
					chat(player,"This better be a pressing matter...");
					break;
				}
				case 14:
				{
					chat(player,"Make it quick, traveler.");
					break;
				}
				case 15:
				{
					chat(player,"Great. What do you want?");
					break;
				}
				case 16:
				{
					chat(player,"Well met.");
					break;
				}
				case 17:
				{
					chat(player,"What are you looking at?");
					break;
				}
				case 18:
				{
					chat(player,"Greetings, traveler.");
					break;
				}
				case 19:
				{
					Item item = player.getHeldItemMainhand().getItem();
					if ( item instanceof ItemSword )
					{
						chat(player,"Careful where you swing that weapon!");
					}
					else if ( item instanceof ItemAxe )
					{
						chat(player,"That axe is for chopping trees, I hope.");
					}
					else if ( item instanceof ItemHoe )
					{
						chat(player,"Ha! Leave the farming to the villagers.");
					}
					else if ( item instanceof ItemPickaxe )
					{
						chat(player,"Mining for some diamonds now, are we?");
					}
					else if ( item instanceof ItemSpade )
					{
						chat(player,"You off to play in the dirt?");
					}
					else
					{
						chat(player,"Can I... help you?");
					}
					break;
				}
				case 20:
				{
					Item item = player.getHeldItemMainhand().getItem();
					if ( item instanceof ItemSword )
					{
						chat(player,"Careful where you swing that weapon!");
					}
					else if ( item instanceof ItemAxe )
					{
						chat(player,"That axe is for chopping trees, I hope.");
					}
					else if ( item instanceof ItemHoe )
					{
						chat(player,"Ha! Leave the farming to the villagers.");
					}
					else if ( item instanceof ItemPickaxe )
					{
						chat(player,"Mining for some diamonds now, are we?");
					}
					else if ( item instanceof ItemSpade )
					{
						chat(player,"You off to play in the dirt?");
					}
					else
					{
						chat(player,"Do you need something?");
					}
					break;
				}
				case 21:
				{
					chat(player,"Come to trade with our village?");
					break;
				}
				case 22:
				{
					chat(player,"Great. More outsiders...");
					break;
				}
				case 23:
				{
					chat(player,"These bandit raids have been getting worse...");
					break;
				}
				case 24:
				{
					chat(player,"Move along.");
					break;
				}
				case 25:
				{
					chat(player,"Speak with our village lord, he has work for you.");
					break;
				}
				case 26:
				{
					chat(player,"Welcome. Our village has much to offer.");
					break;
				}
				case 27:
				{
					chat(player,"Which province do you hail from?");
					break;
				}
				case 28:
				{
					chat(player,"Keep the peace or keep out.");
					break;
				}
				case 29:
				{
					int time = (int)this.world.getWorldTime();
					if ( time > 9000 && time < 13000 ) chat(player,"Good evening.");
					else if ( time >= 4000 && time < 9000 ) chat(player,"Good morning.");
					else if ( time <= 9000 ) chat(player,"Good afternoon.");
					else chat(player,"Stay safe. Monsters roam the night.");
					break;
				}
			}
		}
		else if ( rep < 1000 )
		{
			switch ( rand.nextInt(30) )
			{
				case 0:
				{
					chat(player,"Welcome, my friend.");
					break;
				}
				case 1:
				{
					chat(player,"Hail, " + player.getDisplayNameString() + ".");
					break;
				}
				case 2:
				{
					chat(player,"Greetings, " + player.getDisplayNameString() + ".");
					break;
				}
				case 3:
				{
					chat(player,"Good to see you, " + player.getDisplayNameString() + ".");
					break;
				}
				case 4:
				{
					chat(player,"Well met.");
					break;
				}
				case 5:
				{
					chat(player,"You're " + player.getDisplayNameString() + ", aren't you?");
					break;
				}
				case 6:
				{
					chat(player,"For the king!");
				}
				case 7:
				{
					chat(player,"I live to serve.");
					break;
				}
				case 8:
				{
					int time = (int)this.ticksExisted;
					if ( time > 9000 && time < 13000 ) chat(player,"Good evening, " + player.getDisplayNameString() + ".");
					else if ( time >= 4000 && time < 9000 ) chat(player,"Good morning, " + player.getDisplayNameString() + ".");
					else if ( time <= 9000 ) chat(player,"Good afternoon, " + player.getDisplayNameString() + ".");
					else chat(player,"Stay safe, " + player.getDisplayNameString() + ". Monsters roam the night.");
					break;
				}
				case 9:
				{
					int time = (int)this.world.getWorldTime();
					if ( time > 9000 && time < 13000 ) chat(player,"Good evening, " + player.getDisplayNameString() + ".");
					else if ( time >= 4000 && time < 9000 ) chat(player,"Good morning, " + player.getDisplayNameString() + ".");
					else if ( time <= 9000 ) chat(player,"Good afternoon, " + player.getDisplayNameString() + ".");
					else chat(player,"Stay safe, " + player.getDisplayNameString() + ". Monsters roam the night.");
					break;
				}
				case 10:
				{
					chat(player,"You're making quite the name for yourself around here.");
					break;
				}
				case 11:
				{
					chat(player,"Yes, citizen? How may I help you?");
					break;
				}
				case 12:
				{
					chat(player,"May the Aether watch over you.");
					break;
				}
				case 13:
				{
					chat(player,"It is nice to see you again.");
					break;
				}
				case 14:
				{
					chat(player,"Everything alright?");
					break;
				}
				case 15:
				{
					chat(player,"If you ever need anything, let me know.");
					break;
				}
				case 16:
				{
					chat(player,"We stand by your side.");
					break;
				}
				case 17:
				{
					chat(player,"Stay safe.");
					break;
				}
				case 18:
				{
					chat(player,"Everything's in order.");
					break;
				}
				case 19:
				{
					chat(player,"Greetings.");
					break;
				}
				case 20:
				{
					chat(player,"Yes?");
					break;
				}
				case 21:
				{
					chat(player,"What is your command?");
					break;
				}
				case 22:
				{
					chat(player,"We appreciate your service to the king, " + player.getDisplayNameString() + "." );
					break;
				}
				case 23:
				{
					chat(player,"Gut anymore of those filthy bandits?");
					break;
				}
				case 24:
				{
					chat(player,"These bandit raids have been getting worse...");
					break;
				}
				case 25:
				{
					Item item = player.getHeldItemMainhand().getItem();
					if ( item instanceof ItemSword )
					{
						chat(player,"Impressive weapon you got there...");
					}
					else if ( item instanceof ItemAxe )
					{
						chat(player,"That's a hefty axe. You off to go chop down some bandits?");
					}
					else if ( item instanceof ItemHoe )
					{
						chat(player,"I took you for a fighter not a farmer.");
					}
					else if ( item instanceof ItemPickaxe )
					{
						chat(player,"Mining for some diamonds now, are we?");
					}
					else if ( item instanceof ItemSpade )
					{
						chat(player,"You're digging bandit graves, I hope.");
					}
					else
					{
						chat(player,"I wish I was an adventurer like you.");
					}
					break;
				}
				case 26:
				{
					Item item = player.getHeldItemMainhand().getItem();
					if ( item instanceof ItemSword )
					{
						chat(player,"Impressive weapon you got there...");
					}
					else if ( item instanceof ItemAxe )
					{
						chat(player,"Can I AXE what you plan to do with that? Haha... ha... sorry won't happen again sir.");
					}
					else if ( item instanceof ItemHoe )
					{
						chat(player,"Planting more crops I see. We can use all the help we can get.");
					}
					else if ( item instanceof ItemPickaxe )
					{
						chat(player,"Mining again, are we? How many diamonds have you found?");
					}
					else if ( item instanceof ItemSpade )
					{
						chat(player,"Dig up any treasure?");
					}
					else
					{
						chat(player,"Good to see you.");
					}
					break;
				}
				case 27:
				{
					Item item = player.getHeldItemMainhand().getItem();
					if ( item instanceof ItemSword )
					{
						chat(player,"Impressive weapon you got there...");
					}
					else if ( item instanceof ItemAxe )
					{
						chat(player,"Can I AXE what you plan to do with that? Haha... ha... sorry won't happen again sir.");
					}
					else if ( item instanceof ItemHoe )
					{
						chat(player,"Planting more crops I see. We can use all the help we can get.");
					}
					else if ( item instanceof ItemPickaxe )
					{
						chat(player,"Mining again, are we? How many diamonds have you found?");
					}
					else if ( item instanceof ItemSpade )
					{
						chat(player,"Dig up any treasure?");
					}
					else
					{
						chat(player,"Your orders?");
					}
					break;
				}
				case 28:
				{
					chat(player,"These bandit raids have been getting worse...");
					break;
				}
				case 29:
				{
					chat(player,"Our scouts have not reported back. I'm worried...");
					break;
				}
			}
		}
		else
		{
			switch ( rand.nextInt(30) )
			{
				case 0:
				{
					chat(player,"I am at your command, " + player.getDisplayNameString() + ".");
					break;
				}
				case 1:
				{
					chat(player,"Hail, " + player.getDisplayNameString() + ".");
					break;
				}
				case 2:
				{
					chat(player,"Greetings, " + player.getDisplayNameString() + ".");
					break;
				}
				case 3:
				{
					chat(player,"It is an honor, " + player.getDisplayNameString() + ".");
					break;
				}
				case 4:
				{
					chat(player,"The honor is mine.");
					break;
				}
				case 5:
				{
					chat(player,"I am honored to be in your presence.");
					break;
				}
				case 6:
				{
					chat(player,"For the king!");
					break;
				}
				case 7:
				{
					int time = (int)this.world.getWorldTime();
					if ( time > 9000 && time < 13000 ) chat(player,"I hope you're enjoying this fine evening, " + player.getDisplayNameString() + ".");
					else if ( time >= 4000 && time < 9000 ) chat(player,"I hope you're enjoying this fine morning, " + player.getDisplayNameString() + ".");
					else if ( time <= 9000 ) chat(player,"I hope you're enjoying this fine afternoon, " + player.getDisplayNameString() + ".");
					else chat(player,"Stay safe, " + player.getDisplayNameString() + ". Monsters roam the night.");
					break;
				}
				case 8:
				{
					int time = (int)this.world.getWorldTime();
					if ( time > 9000 && time < 13000 ) chat(player,"Good evening, " + player.getDisplayNameString() + ".");
					else if ( time >= 4000 && time < 9000 ) chat(player,"Good morning, " + player.getDisplayNameString() + ".");
					else if ( time <= 9000 ) chat(player,"Good afternoon, " + player.getDisplayNameString() + ".");
					else chat(player,"Stay safe, " + player.getDisplayNameString() + ". Monsters roam the night.");
					break;
				}
				case 9:
				{
					chat(player,"The world needs more heroes like you.");
					break;
				}
				case 10:
				{
					chat(player,player.getDisplayNameString() + "! My hero...");
					break;
				}
				case 11:
				{
					chat(player,"You have my deepest respect, " + player.getDisplayNameString() + ".");
					break;
				}
				case 12:
				{
					chat(player,"How are your journies, " + player.getDisplayNameString() + "?");
					break;
				}
				case 13:
				{
					chat(player,"I envy your heroism, " + player.getDisplayNameString() + ".");
					break;
				}
				case 14:
				{
					chat(player,"May the Aether bless you, " + player.getDisplayNameString() + ".");
					break;
				}
				case 15:
				{
					chat(player,"Dragons may be scary, but have you ever fought a sandwich horror?");
					break;
				}
				case 16:
				{
					Item item = player.getHeldItemMainhand().getItem();
					if ( item instanceof ItemSword )
					{
						chat(player,"Impressive weapon!");
					}
					else if ( item instanceof ItemAxe )
					{
						chat(player,"That's a hefty axe. You off to go chop up some bandits?");
					}
					else if ( item instanceof ItemHoe )
					{
						chat(player,"I took you for a fighter not a farmer!");
					}
					else if ( item instanceof ItemPickaxe )
					{
						chat(player,"May you find many diamonds!");
					}
					else if ( item instanceof ItemSpade )
					{
						chat(player,"You're digging bandit graves, I hope.");
					}
					else
					{
						chat(player,"I wish I was a hero like you.");
					}
					break;
				}
				case 17:
				{
					Item item = player.getHeldItemMainhand().getItem();
					if ( item instanceof ItemSword )
					{
						chat(player,"Impressive weapon!");
					}
					else if ( item instanceof ItemAxe )
					{
						chat(player,"That's a hefty axe. You off to go chop up some bandits?");
					}
					else if ( item instanceof ItemHoe )
					{
						chat(player,"I took you for a fighter not a farmer!");
					}
					else if ( item instanceof ItemPickaxe )
					{
						chat(player,"May you find many diamonds!");
					}
					else if ( item instanceof ItemSpade )
					{
						chat(player,"You're digging bandit graves, I hope.");
					}
					else
					{
						chat(player,"Gut anymore of those filthy bandits?");
					}
					break;
				}
				case 18:
				{
					chat(player,"It is an honor.");
					break;
				}
				case 19:
				{
					chat(player,"Your orders?");
					break;
				}
				case 20:
				{
					chat(player,"Stay safe.");
					break;
				}
				case 21:
				{
					chat(player,"Sir!");
					break;
				}
				case 22:
				{
					chat(player,"Reporting for duty!");
					break;
				}
				case 23:
				{
					chat(player,"I've heard tales of your heroic deeds!");
					break;
				}
				case 24:
				{
					chat(player,"We stand at the ready.");
					break;
				}
				case 25:
				{
					chat(player,"We've sent a party of scouts to investigate the recent attacks, Sir.");
					break;
				}
				case 26:
				{
					chat(player,"What is your command?");
					break;
				}
				case 27:
				{
					chat(player,"We appreciate your service to the king, " + player.getDisplayNameString() + "." );
					break;
				}
				case 28:
				{
					chat(player,"These bandit raids have been getting worse...");
					break;
				}
				case 29:
				{
					chat(player,"Our scouts have not reported back. I'm worried...");
					break;
				}
			}
		}
		return true;
	}
}
