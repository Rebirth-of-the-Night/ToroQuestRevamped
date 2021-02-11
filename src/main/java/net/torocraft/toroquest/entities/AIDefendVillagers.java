package net.torocraft.toroquest.entities;


import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.village.Village;
import net.minecraftforge.fml.relauncher.Side;

public class AIDefendVillagers extends EntityAITarget
{
	EntityToroNpc guard;
    /** The aggressor of the iron golem's village which is now the golem's attack target. */
    EntityLivingBase villageAgressorTarget;

    public AIDefendVillagers(EntityToroNpc guardIn)
    {
        super(guardIn, true, false);
        this.guard = guardIn;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        Village village = this.guard.getVillage();

        if ( village == null )
        {
            return false;
        }
        else
        {
            this.villageAgressorTarget = village.findNearestVillageAggressor(this.guard);

            if (this.villageAgressorTarget instanceof EntityCreeper)
            {
                return false;
            }
            else if (this.isSuitableTarget(this.villageAgressorTarget, false))
            {
                return true;
            }
            else if (this.taskOwner.getRNG().nextInt(20) == 0)
            {
                this.villageAgressorTarget = village.getNearestTargetPlayer(this.guard);
                return this.isSuitableTarget(this.villageAgressorTarget, false);
            }
            else
            {
                return false;
            }
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
    	if ( this.villageAgressorTarget != null && !this.villageAgressorTarget.isDead )
		{
			this.guard.setAttackTarget(this.villageAgressorTarget);
		}
        super.startExecuting();
    }
}