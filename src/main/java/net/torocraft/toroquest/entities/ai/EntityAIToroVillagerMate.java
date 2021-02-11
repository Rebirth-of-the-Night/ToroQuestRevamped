package net.torocraft.toroquest.entities.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIVillagerMate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.Village;
import net.minecraft.world.World;
import net.torocraft.toroquest.entities.EntityToroVillager;

public class EntityAIToroVillagerMate extends EntityAIVillagerMate
{
    private final EntityToroVillager villager;
    private EntityToroVillager mate;
    private final World world;
    private int matingTimeout;
    Village village;

    public EntityAIToroVillagerMate(EntityToroVillager villagerIn)
    {
    	super(villagerIn);
        this.villager = villagerIn;
        this.world = villagerIn.world;
        this.setMutexBits(3);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (this.villager.getGrowingAge() != 0)
        {
            return false;
        }
        else if (this.villager.getRNG().nextInt(500) != 0)
        {
            return false;
        }
        else
        {
            this.village = this.world.getVillageCollection().getNearestVillage(new BlockPos(this.villager), 0);

            if (this.village == null)
            {
                return false;
            }
            else if (this.checkSufficientDoorsPresentForNewVillager() && this.villager.getIsWillingToMate(true))
            {
                Entity entity = this.world.findNearestEntityWithinAABB(EntityToroVillager.class, this.villager.getEntityBoundingBox().grow(8.0D, 3.0D, 8.0D), this.villager);

                if (entity == null)
                {
                    return false;
                }
                else
                {
                    this.mate = (EntityToroVillager)entity;
                    return this.mate.getGrowingAge() == 0 && this.mate.getIsWillingToMate(true);
                }
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
        this.matingTimeout = 300;
        this.villager.setMating(true);
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void resetTask()
    {
        this.village = null;
        this.mate = null;
        this.villager.setMating(false);
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean shouldContinueExecuting()
    {
        return this.matingTimeout >= 0 && this.checkSufficientDoorsPresentForNewVillager() && this.villager.getGrowingAge() == 0 && this.villager.getIsWillingToMate(false);
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void updateTask()
    {
        --this.matingTimeout;
        this.villager.getLookHelper().setLookPositionWithEntity(this.mate, 10.0F, 30.0F);

        if (this.villager.getDistanceSq(this.mate) > 2.25D)
        {
            this.villager.getNavigator().tryMoveToEntityLiving(this.mate, 0.25D);
        }
        else if (this.matingTimeout == 0 && this.mate.isMating())
        {
            this.giveBirth();
        }

        if (this.villager.getRNG().nextInt(35) == 0)
        {
            this.world.setEntityState(this.villager, (byte)12);
        }
    }

    private boolean checkSufficientDoorsPresentForNewVillager()
    {
        if (!this.village.isMatingSeason())
        {
            return false;
        }
        else
        {
            int i = (int)((double)((float)this.village.getNumVillageDoors()) * 0.35D);
            return this.village.getNumVillagers() < i;
        }
    }

    private void giveBirth()
    {
        net.minecraft.entity.EntityAgeable EntityToroVillager = this.villager.createChild(this.mate);
        this.mate.setGrowingAge(6000);
        this.villager.setGrowingAge(6000);
        this.mate.setIsWillingToMate(false);
        this.villager.setIsWillingToMate(false);

        final net.minecraftforge.event.entity.living.BabyEntitySpawnEvent event = new net.minecraftforge.event.entity.living.BabyEntitySpawnEvent(villager, mate, EntityToroVillager);
        if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event) || event.getChild() == null) { return; }
        EntityToroVillager = event.getChild();
        EntityToroVillager.setGrowingAge(-24000);
        EntityToroVillager.setLocationAndAngles(this.villager.posX, this.villager.posY, this.villager.posZ, 0.0F, 0.0F);
        this.world.spawnEntity(EntityToroVillager);
        this.world.setEntityState(EntityToroVillager, (byte)12);
    }
}