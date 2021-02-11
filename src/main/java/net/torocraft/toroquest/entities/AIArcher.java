package net.torocraft.toroquest.entities;


import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBow;
import net.minecraft.util.EnumHand;

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
    private float hStrafeMod = (rand.nextInt(6)+1)/16.0F;
    private float vStrafeMod = (rand.nextInt(3)+1)/16.0F;
    
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
        return (this.shouldExecute() || !this.entity.getNavigator().noPath()) && this.isBowInMainhand();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
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
    public void resetTask()
    {
        super.resetTask();
        this.entity.getMoveHelper().strafe( 0.0F, 0.0F );
        this.entity.getNavigator().clearPath();
//      if ( this.entity.getAttackTarget() != null && this.entity.getAttackTarget().isEntityAlive() )
//      {
//      	this.entity.getNavigator().tryMoveToEntityLiving(this.entity.getAttackTarget(), this.moveSpeedAmp+0.4F);
//      }
        this.entity.setAttackTarget(null);
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
    	// if ( this.entity.world.isRemote ) return;
    	
        EntityLivingBase entitylivingbase = this.entity.getAttackTarget();

        if ( shouldExecute() )
        {
            double d0 = this.entity.getDistanceSq(entitylivingbase.posX, entitylivingbase.getEntityBoundingBox().minY, entitylivingbase.posZ);
            boolean canSee = this.entity.getEntitySenses().canSee(entitylivingbase);
            boolean seeTimeIsGreaterThanZero = this.seeTime > 0;
            boolean outOfReach = false;
           
            // when seeTime is less than 80, stop
            // when seeTime is greater than 0, start strafing
            
        	this.entity.faceEntity(entitylivingbase, 30.0F, 30.0F);

            if (canSee != seeTimeIsGreaterThanZero)
            {
                this.seeTime = 0;
            }

            if (canSee)
            {
                this.entity.getLookHelper().setLookPositionWithEntity(entitylivingbase, 30.0F, 30.0F);
                ++this.seeTime;
            }
            else
            {
                --this.seeTime;
            }
            
            if ( this.seeTime <= -20 )
            {
                this.entity.setAttackTarget(null);
                return;
            }

            if (d0 <= (double)this.maxAttackDistance && this.seeTime >= 20)
            {
            	this.entity.getMoveHelper().strafe( 0.0F, 0.0F );
            	this.entity.getNavigator().clearPath();
            	++this.strafingTime;
            }
            else
            {
            	this.entity.getNavigator().tryMoveToEntityLiving(entitylivingbase, this.moveSpeedAmp);
				this.strafingTime = -1;
            }
            
            if ( d0 > (double)this.maxAttackDistance )
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
            	hAmount /= 16;
            	vAmount /= 2;
            	sChance /= 4;
            }
            
            if (this.strafingTime >= 20)
            {
                if ((double)this.entity.getRNG().nextFloat() < sChance)
                {
                    this.strafingClockwise = !this.strafingClockwise;
                }
                if ((double)this.entity.getRNG().nextFloat() < sChance)
                {
                    this.strafingBackwards = !this.strafingBackwards;
                }
                this.strafingTime = 0;
            }

           
            if ( this.strafingTime > -1 && canSee )
            {
                if (d0 > (double)(this.maxAttackDistance * 0.6F))
                {
                    this.strafingBackwards = false;
                }
                else if (d0 < (double)(this.maxAttackDistance * 0.35F))
                {
                    this.strafingBackwards = true;
                }
                
                if ( this.attackTime != 0 || this.entity.isRiding() )
                {
                    this.entity.getMoveHelper().strafe(this.strafingBackwards ? -vAmount/2 : vAmount, this.strafingClockwise ? -hAmount : hAmount);
                }
                else
                {
                	this.entity.getMoveHelper().strafe(this.strafingBackwards ? -vAmount/4 : hAmount/2, this.strafingClockwise ? -hAmount/8 : hAmount/8);
                }
            }
            else
            {
                // this.entity.getLookHelper().setLookPositionWithEntity(entitylivingbase, 30.0F, 30.0F);
                this.entity.getNavigator().tryMoveToEntityLiving(entitylivingbase, this.moveSpeedAmp);
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
	                        ((IRangedAttackMob)this.entity).attackEntityWithRangedAttack(entitylivingbase, ItemBow.getArrowVelocity(i));
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
                this.entity.getNavigator().tryMoveToEntityLiving( entitylivingbase, this.moveSpeedAmp );
            }
        }
    }
}