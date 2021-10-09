package net.torocraft.toroquest.entities.ai;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.torocraft.toroquest.config.ToroQuestConfiguration;

public class EntityAIAvoidEnemies<T extends Entity> extends EntityAIBase
{
    /** The entity we are attached to */
    protected EntityCreature entity;
    private final double farSpeed;
    private final double nearSpeed;
    private final double avoidDistance;
    protected EntityLiving closestLivingEntity;
    /** The PathEntity of our entity */
    private Path path;
    /** The PathNavigate of our entity */
    private final PathNavigate navigation;
    /** Class of entity this behavior seeks to avoid */

    public EntityAIAvoidEnemies(EntityCreature entityIn, float avoidDistanceIn, double farSpeedIn, double nearSpeedIn)
    {
        this.entity = entityIn;
        this.farSpeed = farSpeedIn;
        this.nearSpeed = nearSpeedIn;
        this.avoidDistance = avoidDistanceIn;
        this.navigation = entityIn.getNavigator();
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if ( this.entity.getRNG().nextInt(20) != 0 )
        {
        	return false;
        }

    	List<EntityLiving> list = this.entity.world.getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(this.entity.getPosition()).grow(this.avoidDistance, this.avoidDistance/2.0D, this.avoidDistance), new Predicate<EntityLiving>()
		{
			public boolean apply(@Nullable EntityLiving enemy )
			{
				if ( ( enemy instanceof IMob || enemy instanceof EntityMob ) && entity.canEntityBeSeen(enemy) )
				{
					return true;
				}
				return false;
			}
		});
        for ( EntityLiving e : list )
        {
            this.closestLivingEntity = e;
            
            if ( ToroQuestConfiguration.iMobAttackVillagers && this.closestLivingEntity.getAttackTarget() != null )
            {
            	this.closestLivingEntity.setAttackTarget(this.entity);
            }
            
            Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.entity, 16, 7, new Vec3d(this.closestLivingEntity.posX, this.closestLivingEntity.posY, this.closestLivingEntity.posZ));

            if (vec3d == null)
            {
                return false;
            }
            else if (this.closestLivingEntity.getDistanceSq(vec3d.x, vec3d.y, vec3d.z) < this.closestLivingEntity.getDistanceSq(this.entity))
            {
                return false;
            }
            else
            {
                this.path = this.navigation.getPathToXYZ(vec3d.x, vec3d.y, vec3d.z);
                return this.path != null;
            }
        }
        return false;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean shouldContinueExecuting()
    {
        return !this.navigation.noPath();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.navigation.setPath(this.path, this.farSpeed);
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void resetTask()
    {
        this.closestLivingEntity = null;
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void updateTask()
    {
        if (this.entity.getDistanceSq(this.closestLivingEntity) < 40.0D)
        {
            this.entity.getNavigator().setSpeed(this.nearSpeed);
        }
        else
        {
            this.entity.getNavigator().setSpeed(this.farSpeed);
        }
    }
}