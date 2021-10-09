package net.torocraft.toroquest.entities;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.Vec3d;
import scala.util.Random;

public class EntityAIFleeAdventurer extends EntityAIBase
{
    protected final EntityAdventurer creature;
    protected EntityLivingBase attacker;
    protected double speed;
    protected double randPosX;
    protected double randPosY;
    protected double randPosZ;
    protected boolean running;
    
    public EntityAIFleeAdventurer( EntityAdventurer creature, double speedIn )
    {
        this.creature = creature;
        this.speed = speedIn;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
    	if ( this.creature.useHealingPotion )
    	{
    	    running = false;
    	    return false;
    	}
    	
    	float m = 0.0F;
    	
    	if ( running )
    	{
    		m = 2.5F;
    	}
    	
    	if ( this.creature.getHealth() <= m + this.creature.fleeModifier )
		{
    		EntityLivingBase victim = this.creature.getRevengeTarget(); // timer is 100 ticks (5 seconds)
    		if ( victim != null )
    		{
	    		Vec3d enemyPos = victim.getPositionVector();
	            Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.creature, 16, 8, enemyPos);
	            if (vec3d != null)
	            {
	            	//if ( this.creature.getNavigator().tryMoveToXYZ(vec3d.x, vec3d.y, vec3d.z, this.speed) )
	            	{
	            		this.randPosX = vec3d.x;
	            		this.randPosY = vec3d.y;
	            		this.randPosZ = vec3d.z;
	            		running = true;
	            		return true;
	            	}
	            }
    		}
		}
    	running = false;
	    return false;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        if ( this.creature.getNavigator().tryMoveToXYZ(this.randPosX, this.randPosY, this.randPosZ, this.speed) )
        {
    		this.creature.setSprinting(true);
    		this.creature.setAttackTarget(null);
        	running = true;
        }
        // this.creature.getLookHelper().setLookPosition(this.creature.posX, this.creature.posY + (double)this.creature.getEyeHeight(), this.creature.posZ, (float)this.creature.getHorizontalFaceSpeed(), (float)this.creature.getVerticalFaceSpeed());
    }
    Random rand = new Random();
    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean shouldContinueExecuting()
    {
    	// reset revenge target timer
    	
    	if ( this.creature.useHealingPotion )
    	{
    		this.creature.setSprinting(false);
    		running = false;
        	return false;
    	}
    	
    	if ( this.creature.getRevengeTarget() != null )
    	{
    		this.creature.setRevengeTarget(this.creature.getRevengeTarget());
    	}
    	
        if ( this.creature.getNavigator().noPath() )
        {
        	if ( rand.nextInt(3) == 0 )
        	{
        		if ( rand.nextBoolean() ) this.creature.useHealingPotion = true;
        		this.creature.setAttackTarget(this.creature.getRevengeTarget());
        		this.creature.setSprinting(false);
        		running = false;
            	return false;
        	}
        	else
        	{
        		return shouldExecute();
        	}
        }
		this.creature.setSprinting(true);
		this.creature.setAttackTarget(null);
    	running = true;
    	return true;
    }
    
    @Override
    public void resetTask()
    {
		this.creature.setSprinting(false);
    	running = false;
    	super.resetTask();
    }
    
}