package net.torocraft.toroquest.entities;


import java.util.Random;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.item.ItemBow;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;

public class AIArcher<T extends EntityLiving & IRangedAttackMob> extends EntityAIBase
{
    private final T entity;
    private final double moveSpeedAmp;
    private int attackCooldown;
    private final float maxAttackDistance;
    private int attackTime = -1;
    private int seeTime;
    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime = -1;
    private Random rand = new Random();
    private float hStrafeMod = (rand.nextInt(6)+1)/14.0F;
    private float vStrafeMod = (rand.nextInt(3)+1)/14.0F;
    // private EntityLivingBase attackTarget = null;

    
    public AIArcher(T p_i47515_1_, double p_i47515_2_, int p_i47515_4_, float p_i47515_5_)
    {
        this.entity = p_i47515_1_;
        this.moveSpeedAmp = p_i47515_2_;
        this.attackCooldown = p_i47515_4_;
        this.maxAttackDistance = p_i47515_5_ * p_i47515_5_;
        this.setMutexBits(3);
    }

    public void setAttackCooldown(int p_189428_1_)
    {
        this.attackCooldown = p_189428_1_;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
    	// if there is no target or if there is no bow in hand do not execute
    	if ( this.entity.getAttackTarget() == null || !(this.entity.getAttackTarget().isEntityAlive()) || !this.isBowInMainhand() )
    	{
    		return false;
    	}
    	return true;
    }

    protected boolean isBowInMainhand()
    {
        return !this.entity.getHeldItemMainhand().isEmpty() && this.entity.getHeldItemMainhand().getItem() instanceof ItemBow;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean shouldContinueExecuting()
    {
        return this.shouldExecute();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
    	this.seeTime = 0;
        this.attackTime = -1;
        this.entity.resetActiveHand();
    	this.entity.getMoveHelper().strafe( 0.0F, 0.0F );
    	this.entity.getNavigator().clearPath();
    	this.strafingClockwise = rand.nextBoolean();
    	this.hStrafeMod = (rand.nextInt(6)+1)/16.0F;
    	this.vStrafeMod = (rand.nextInt(3)+1)/16.0F;
        super.startExecuting();
        ((IRangedAttackMob)this.entity).setSwingingArms(true);
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    @Override
    public void resetTask()
    {
        this.entity.getMoveHelper().strafe( 0.0F, 0.0F );
        this.entity.getNavigator().clearPath();
        if ( this.entity.getAttackTarget() != null && ( this.entity.getAttackTarget().isDead || this.entity.getAttackTarget().getHealth() <= 0 ) ) this.entity.setAttackTarget(null);
        ((IRangedAttackMob)this.entity).setSwingingArms(false);
        this.seeTime = 0;
        this.attackTime = -1;
        this.entity.resetActiveHand();
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void updateTask()
    {
        if ( shouldExecute() )
        {
        	
            double d0 = this.entity.getDistance(this.entity.getAttackTarget());
            boolean canSee = this.entity.getEntitySenses().canSee(this.entity.getAttackTarget());
            boolean seeTimeIsGreaterThanZero = this.seeTime > 0;
            boolean outOfReach = false;
           
            // when seeTime is less than 80, stop
            // when seeTime is greater than 0, start strafing
            
        	this.entity.faceEntity(this.entity.getAttackTarget(), 20.0F, 20.0F);

            if (canSee != seeTimeIsGreaterThanZero)
            {
                this.seeTime = 0;
            }

            if (canSee)
            {
                this.entity.getLookHelper().setLookPositionWithEntity(this.entity.getAttackTarget(), 20.0F, 20.0F);
                ++this.seeTime;
            }
            else
            {
                --this.seeTime;
            }
            
            if ( this.seeTime <= -80 )
            {
            	if ( this.entity instanceof EntityCreature )
            	{
	            	Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockTowards((EntityCreature) this.entity, 12, 6, this.entity.getAttackTarget().getPositionVector());
		            if ( vec3d != null && this.entity.getNavigator().tryMoveToXYZ(vec3d.x, vec3d.y, vec3d.z, 0.55D) )
		            {
		                this.entity.setAttackTarget(null);
						return;
		            }
            	}
            	return;
            }

            if (d0 <= this.maxAttackDistance && this.seeTime >= 20)
            {
            	this.entity.getMoveHelper().strafe( 0.0F, 0.0F );
            	this.entity.getNavigator().clearPath();
            	++this.strafingTime;
            }
            else
            {
            	this.entity.getNavigator().tryMoveToEntityLiving(this.entity.getAttackTarget(), this.moveSpeedAmp);
				this.strafingTime = -1;
            }
            
            if ( d0 > this.maxAttackDistance )
            {
            	outOfReach = true;
            	if ( ++this.attackTime >= this.attackCooldown )
            	{
                    this.resetTask();
            	}
            }
           
            /*
           if (d0 <= (double)this.maxAttackDistance )
           {
           	if ( this.seeTime >= 20 )
           	{
                   this.entity.getNavigator().clearPath();
           		++this.strafingTime;
           	}
           }
           else
           {
           	outOfReach = true;
           	if ( this.strafingTime > -1 )
				{
					this.strafingTime = -1;
	                this.entity.getMoveHelper().strafe(0,0);
	                this.entity.getNavigator().clearPath();
				}
               //this.entity.getNavigator().tryMoveToEntityLiving(entitylivingbase, this.moveSpeedAmp);
               if ( this.entity.isHandActive() )
	            {
               	this.entity.resetActiveHand();
	            }
           }
             */
            float hAmount = hStrafeMod;
            float vAmount = vStrafeMod;
            float sChance = 0.2F;

            if ( this.entity.isRiding() )
            {
//            	hAmount += 0.2F;
//            	vAmount += 0.2F;
            	sChance = 0.1F;
            }
            
            if (this.strafingTime >= 20)
            {
                if (this.entity.getRNG().nextFloat() < sChance)
                {
                    this.strafingClockwise = !this.strafingClockwise;
                }
                if (this.entity.getRNG().nextFloat() < sChance)
                {
                    this.strafingBackwards = !this.strafingBackwards;
                }
                this.strafingTime = 0;
            }
            
            boolean moveTo = false;
            
            if ( this.strafingTime > -1 && canSee )
            {
                if (d0 > this.maxAttackDistance * 0.6F)
                {
                    this.strafingBackwards = false;
                }
                else if (d0 < this.maxAttackDistance * 0.35F)
                {
                    this.strafingBackwards = true;
                }
                
                if ( this.attackTime != 0 )
                {
                    this.entity.getMoveHelper().strafe(this.strafingBackwards ? -vAmount/2.0F : vAmount, this.strafingClockwise ? -hAmount : hAmount);
                }
                else
                {
                	this.entity.getMoveHelper().strafe(this.strafingBackwards ? -vAmount/4.0F : hAmount/2.0F, this.strafingClockwise ? -hAmount/8.0F : hAmount/8.0F);
                }
            }
            else
            {
                moveTo = true;
            }
            
            if ( !outOfReach )
            {
	            if (this.entity.isHandActive())
	            {
	                if ( !canSee && this.seeTime <= -80)
	                {
	                	if ( ++this.attackTime >= this.attackCooldown )
	                	{
		                    this.resetTask();
	                	}
	                }
	                else if (canSee && this.seeTime >= 10)
	                {
	                    int i = this.entity.getItemInUseMaxCount();
	
	                    if (i >= 20)
	                    {
	                        this.entity.resetActiveHand();
	                        ((IRangedAttackMob)this.entity).attackEntityWithRangedAttack(this.entity.getAttackTarget(), ItemBow.getArrowVelocity(i));
	                        this.attackTime = this.attackCooldown;
	                    }
	                }
	                return;
	            }
	            else if ( --this.attackTime <= 0 && this.seeTime >= 10 && canSee )
	            {
	                this.entity.setActiveHand(EnumHand.MAIN_HAND);
	            }
	            return;
            }
            else if ( !this.entity.isHandActive() )
            {
                moveTo = true;
            }
            
            if ( moveTo )
            {
            	this.entity.getNavigator().tryMoveToEntityLiving( this.entity.getAttackTarget(), this.moveSpeedAmp );
            }
        }
    }
}