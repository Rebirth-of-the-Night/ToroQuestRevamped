package net.torocraft.toroquest.entities;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class AIAttackWithSword extends EntityAIBase
{
    World world;
    protected EntityCreature attacker;
    /** An amount of decrementing ticks that allows the entity to attack once the tick reaches 0. */
    protected int attackTick = 0;
    /** The speed with which the mob will approach the target */
    double speedTowardsTarget;
    /** When true, the mob will continue chasing its target, even if it can't find a path to them right now. */
    boolean longMemory;
    /** The PathEntity of our entity. */
    Path path;
    //private int delayCounter = 2;
//    private double targetX;
//    private double targetY;
//    private double targetZ;
//    protected final int attackInterval = 22;
//    private int failedPathFindingPenalty = 0;
//    private boolean canPenalize = false;
    protected boolean offhandAttack = false;
    
	protected float range = 3.0F;

    public AIAttackWithSword(EntityCreature creature, double speedIn, boolean useLongMemory)
    {
        this.attacker = creature;
        this.world = creature.world;
        this.speedTowardsTarget = speedIn;
        this.longMemory = true;
        this.setMutexBits(3);
        
        ItemStack iStack = this.attacker.getHeldItemMainhand();
		
        if ( iStack != null )
    	{
        	String s = iStack.getItem().getRegistryName().toString();
        		 if ( s.contains("lance") ) 		{range = 6.0F;}
        	else if ( s.contains("pike") )  	 	{range = 6.0F;}
        	else if ( s.contains("glaive") ) 		{range = 4.5F;}
        	else if ( s.contains("halberd") ) 		{range = 4.5F;}
        	else if ( s.contains("greatsword") ) 	{range = 4.0F;}
        	else if ( s.contains("spear") ) 		{range = 4.0F;}
    	}
        
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
    	ItemStack iStack = this.attacker.getHeldItemMainhand();
		if (iStack != null && iStack.getItem() instanceof ItemBow)
		{
    		return false;
    	}
		
		
		
        EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
        
        if (entitylivingbase == null)
        {
            return false;
        }
        else if (!entitylivingbase.isEntityAlive())
        {
            return false;
        }
        else
        {
//            if (canPenalize)
//            {
//                if (--this.delayCounter < 0)
//                {
//                    this.path = this.attacker.getNavigator().getPathToEntityLiving(entitylivingbase);
//                    this.delayCounter = 2;
//                    return this.path != null;
//                }
//                else
//                {
//                    return true;
//                }
//            }
            this.path = this.attacker.getNavigator().getPathToEntityLiving(entitylivingbase);

            if (this.path != null)
            {
                return true;
            }
            else
            {
                return this.getAttackReachSqr(entitylivingbase) >= this.attacker.getDistanceSq(entitylivingbase.posX, entitylivingbase.getEntityBoundingBox().minY, entitylivingbase.posZ);
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean shouldContinueExecuting()
    {
    	ItemStack iStack = this.attacker.getHeldItemMainhand();
		if (iStack != null && iStack.getItem() instanceof ItemBow)
		{
    		return false;
    	}
		
        EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();

        if (entitylivingbase == null)
        {
            return false;
        }
        else if (!entitylivingbase.isEntityAlive())
        {
            return false;
        }
        else if (!this.longMemory)
        {
            return !this.attacker.getNavigator().noPath();
        }
        else if (!this.attacker.isWithinHomeDistanceFromPosition(new BlockPos(entitylivingbase)))
        {
            return false;
        }
        else
        {
            return !(entitylivingbase instanceof EntityPlayer) || !((EntityPlayer)entitylivingbase).isSpectator() && !((EntityPlayer)entitylivingbase).isCreative();
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
    	ItemStack iStack = this.attacker.getHeldItemMainhand();
		if ( iStack == null || iStack.getItem() instanceof ItemBow )
		{
    		return;
    	}
		if ( this.attacker.isRiding() )
		{
			this.attacker.getRidingEntity().setSprinting(true);
			//this.attacker.setSprinting(true);
		}
		else
		{
			this.attacker.getNavigator().setPath( this.path, this.speedTowardsTarget );
		}
        //this.delayCounter = 2;
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void resetTask()
    {
        EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();

        if (entitylivingbase instanceof EntityPlayer && (((EntityPlayer)entitylivingbase).isSpectator() || ((EntityPlayer)entitylivingbase).isCreative()))
        {
            this.attacker.setAttackTarget((EntityLivingBase)null);
        }
        if ( this.attacker.isRiding() )
		{
			this.attacker.getRidingEntity().setSprinting(false);
			//this.attacker.setSprinting(true);
		}
        this.attacker.getNavigator().clearPath();
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void updateTask()
    {
    	ItemStack iStack = this.attacker.getHeldItemMainhand();
		if (iStack != null && iStack.getItem() instanceof ItemBow)
		{
    		return;
    	}
        EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
        if ( entitylivingbase == null || !entitylivingbase.isEntityAlive() )
        {
        	return;
        }
        // this.attacker.faceEntity(attacker, 30.0F, 30.0F);
        this.attacker.getLookHelper().setLookPositionWithEntity(entitylivingbase, 30.0F, 30.0F);
        double d0 = this.attacker.getDistanceSq(entitylivingbase.posX, entitylivingbase.getEntityBoundingBox().minY, entitylivingbase.posZ);
        //--this.delayCounter;

        //if ((this.longMemory || this.attacker.getEntitySenses().canSee(entitylivingbase)) && (this.targetX == 0.0D && this.targetY == 0.0D && this.targetZ == 0.0D || entitylivingbase.getDistanceSq(this.targetX, this.targetY, this.targetZ) > 2.0D || this.attacker.getRNG().nextFloat() < 0.05F))
        if ((this.longMemory || this.attacker.getEntitySenses().canSee(entitylivingbase)));
        {
//            this.targetX = entitylivingbase.posX;
//            this.targetY = entitylivingbase.getEntityBoundingBox().minY;
//            this.targetZ = entitylivingbase.posZ;
            
            if ( this.attacker.getRidingEntity() instanceof EntityLiving )
    		{
				EntityLiving e = (EntityLiving)(this.attacker.getRidingEntity());
				this.attacker.faceEntity(entitylivingbase, 30.0F, 30.0F);
				e.faceEntity(entitylivingbase, 30.0F, 30.0F);
				e.getLookHelper().setLookPositionWithEntity(entitylivingbase, 30.0F, 30.0F);
            	if ( d0 >= 5 )
            	{
	            	if ( Math.abs(this.attacker.getRidingEntity().motionX) + Math.abs(this.attacker.getRidingEntity().motionZ) < 0.3 )
	            	{
	            		this.attacker.getNavigator().clearPath();
	    				if ( !world.isRemote )
	    				{
	        				//Vec3d velocityVector = new Vec3d(entitylivingbase.posX - this.attacker.posX, 0, entitylivingbase.posZ - this.attacker.posZ);
		    				this.attacker.getRidingEntity().motionX += (double)(entitylivingbase.posX - this.attacker.posX)/(d0*3);
		    				//if ( this.attacker.getRidingEntity().motionY < 0.1 && !this.attacker.isAirBorne ) { this.attacker.isAirBorne = true; this.attacker.getRidingEntity().motionY += 0.1; }
		            		this.attacker.getRidingEntity().motionZ += (double)(entitylivingbase.posZ - this.attacker.posZ)/(d0*3);
		            		
//		            		if ( this.world.rand.nextInt(32) == 0 )
//		            		{
//		            			this.attacker.getRidingEntity().motionX += (this.attacker.world.rand.nextFloat()-0.5)/20;
//		            		}
//		            		if ( this.world.rand.nextInt(32) == 0 )
//		            		{
//		            			this.attacker.getRidingEntity().motionZ += (this.attacker.world.rand.nextFloat()-0.5)/20;
//		            		}
	    				}
	            	}
            	}
            	else
            	{
            		this.attacker.getNavigator().clearPath();
            		if ( !world.isRemote )
    				{
	            		this.attacker.getRidingEntity().motionX = 0;
	            		this.attacker.getRidingEntity().motionZ = 0;
    				}
            	}
    		}
            else if ( (entitylivingbase.onGround && !entitylivingbase.isAirBorne && this.attacker.getNavigator().noPath()) || this.attacker.world.rand.nextInt(100) == 0 )
    		{
    			if ( this.attacker.motionY == 0 && this.attacker.onGround && !this.attacker.isInWater() && !this.attacker.isAirBorne && d0 > 3 && d0 <= 25 )
    			{
					if ( !world.isRemote )
					{
	    				Vec3d velocityVector = new Vec3d(entitylivingbase.posX - this.attacker.posX, 0, entitylivingbase.posZ - this.attacker.posZ);
						this.attacker.addVelocity( velocityVector.x/d0, 0.4, velocityVector.z/d0 );
					}
					this.attacker.isAirBorne = true;
					this.attacker.onGround = false;
    			}
    		}
            //this.delayCounter = 2;

//            if (this.canPenalize)
//            {
//                this.delayCounter += failedPathFindingPenalty;
//                if (this.attacker.getNavigator().getPath() != null)
//                {
//                    net.minecraft.pathfinding.PathPoint finalPathPoint = this.attacker.getNavigator().getPath().getFinalPathPoint();
//                    if (finalPathPoint != null && entitylivingbase.getDistanceSq(finalPathPoint.x, finalPathPoint.y, finalPathPoint.z) < 1)
//                        failedPathFindingPenalty = 0;
//                    else
//                        failedPathFindingPenalty += 2;
//                }
//                else
//                {
//                    failedPathFindingPenalty += 2;
//                }
//            }

//            if (d0 > 1024.0D)
//            {
//                this.delayCounter += 2;
//            }
//            else if (d0 > 256.0D)
//            {
//                this.delayCounter += 1;
//            }
//
//            if (!this.attacker.getNavigator().tryMoveToEntityLiving(entitylivingbase, this.speedTowardsTarget))
//            {
//                this.delayCounter += 3;
//            }
            
              //if (iStack != null && !(iStack.getItem() instanceof ItemBow) ) this.attacker.getNavigator().tryMoveToEntityLiving(entitylivingbase, this.speedTowardsTarget);

        }

        this.attackTick = Math.max(this.attackTick - 1, 0);
        this.checkAndPerformAttack(entitylivingbase, d0);
    }

    protected void checkAndPerformAttack(EntityLivingBase p_190102_1_, double p_190102_2_)
    {
        double d0 = this.getAttackReachSqr(p_190102_1_);
        
        if ( d0 <= 3 && this.attackTick <= 3 )
        {
	        Vec3d veloctiyVector = new Vec3d(p_190102_1_.posX - this.attacker.posX, 0, p_190102_1_.posZ - this.attacker.posZ);
	        this.attacker.addVelocity((veloctiyVector.x),0,(veloctiyVector.z));
	        // this.attacker.velocityChanged = true;
        }
        
        if ( this.attacker.getActiveHand().equals(EnumHand.OFF_HAND) )
        {
        	this.attackTick = 2;
        }
        else if (p_190102_2_ <= d0 && this.attackTick <= 0)
        {
        	
            if ( !offhandAttack )
            {
            	this.attacker.swingArm(EnumHand.MAIN_HAND);
            	this.attackTick = 22;
            	this.attacker.attackEntityAsMob(p_190102_1_);
            }
            else
            {
            	this.attacker.swingArm(EnumHand.OFF_HAND);
            	this.attackTick = 11;
            	offhandAttack = false;
            	this.attacker.attackEntityAsMob(p_190102_1_);
            	return;
            }
        	
            ItemStack iStack = this.attacker.getHeldItem(EnumHand.OFF_HAND);
            if ( iStack != null && !( iStack.isEmpty() ) && !( iStack.getItem() instanceof ItemBow ) && !( iStack.getItem() instanceof ItemPotion ) && !( iStack.getItem() instanceof ItemShield ) )
            {
            	offhandAttack = true;
            	this.attackTick = 11;
            }
        }
    }

    protected double getAttackReachSqr(EntityLivingBase attackTarget)
    {
        return (double)(this.attacker.width * range * this.attacker.width * range + attackTarget.width);
    }

	public static boolean canReach(EntityCreature creature)
	{
		return !creature.getNavigator().noPath();
	}
}