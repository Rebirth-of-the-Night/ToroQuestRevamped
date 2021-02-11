package net.torocraft.toroquest.entities;


import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.village.Village;

public class AIAttackVillagers extends EntityAITarget
{
	EntityToroNpc irongolem;
    /** The aggressor of the iron golem's village which is now the golem's attack target. */
    EntityLivingBase villageAgressorTarget;

    public AIAttackVillagers(EntityToroNpc ironGolemIn)
    {
        super(ironGolemIn, false, true);
        this.irongolem = ironGolemIn;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        Village village = this.irongolem.getVillage();

        if (village == null)
        {
            return false;
        }
        else
        {
            this.villageAgressorTarget = village.findNearestVillageAggressor(this.irongolem);

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
                this.villageAgressorTarget = village.getNearestTargetPlayer(this.irongolem);
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
        this.irongolem.setAttackTarget(this.villageAgressorTarget);
        super.startExecuting();
    }
}