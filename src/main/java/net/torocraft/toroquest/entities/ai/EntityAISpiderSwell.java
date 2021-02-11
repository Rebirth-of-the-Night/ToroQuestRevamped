package net.torocraft.toroquest.entities.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.torocraft.toroquest.entities.EntitySpiderLord;

public class EntityAISpiderSwell extends EntityAIBase
{
    /** The creeper that is swelling. */
    EntitySpiderLord swellingSpider;
    /** The creeper's attack target. This is used for the changing of the creeper's state. */
    EntityLivingBase creeperAttackTarget;

    public EntityAISpiderSwell(EntitySpiderLord entitycreeperIn)
    {
        this.swellingSpider = entitycreeperIn;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        //EntityLivingBase entitylivingbase = this.swellingSpider.getAttackTarget();
        return this.swellingSpider.bossTimer <= 40; // || entitylivingbase != null && this.swellingSpider.getDistanceSq(entitylivingbase) < 9.0D;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.swellingSpider.getNavigator().clearPath();
        this.creeperAttackTarget = this.swellingSpider.getAttackTarget();
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void resetTask()
    {
    	
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void updateTask()
    {
    	this.swellingSpider.setCreeperState(1);
    }
}