package net.torocraft.toroquest.entities.ai;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.Vec3d;
import net.torocraft.toroquest.civilization.CivilizationType;
import net.torocraft.toroquest.civilization.CivilizationUtil;
import net.torocraft.toroquest.civilization.Province;
import net.torocraft.toroquest.civilization.player.PlayerCivilizationCapabilityImpl;

public class EntityAIAvoidBanditPlayer<T extends Entity> extends EntityAIBase
{
    private final Predicate<Entity> canBeSeenSelector;
    /** The entity we are attached to */
    protected EntityCreature entity;
    private final double farSpeed;
    private final double nearSpeed;
    protected T closestLivingEntity;
    private final float avoidDistance;
    /** The PathEntity of our entity */
    private Path path;
    /** The PathNavigate of our entity */
    private final PathNavigate navigation;
    /** Class of entity this behavior seeks to avoid */
    private final Class<T> classToAvoid;
    private final Predicate <? super T > avoidTargetSelector;

    public EntityAIAvoidBanditPlayer(EntityCreature entityIn, Class<T> classToAvoidIn, float avoidDistanceIn, double farSpeedIn, double nearSpeedIn)
    {
        this(entityIn, classToAvoidIn, Predicates.alwaysTrue(), avoidDistanceIn, farSpeedIn, nearSpeedIn);
    }

    public EntityAIAvoidBanditPlayer(EntityCreature entityIn, Class<T> classToAvoidIn, Predicate <? super T > avoidTargetSelectorIn, float avoidDistanceIn, double farSpeedIn, double nearSpeedIn)
    {
        this.canBeSeenSelector = new Predicate<Entity>()
        {
            public boolean apply(@Nullable Entity p_apply_1_)
            {
                return p_apply_1_.isEntityAlive() && EntityAIAvoidBanditPlayer.this.entity.getEntitySenses().canSee(p_apply_1_) && !EntityAIAvoidBanditPlayer.this.entity.isOnSameTeam(p_apply_1_);
            }
        };
        this.entity = entityIn;
        this.classToAvoid = classToAvoidIn;
        this.avoidTargetSelector = avoidTargetSelectorIn;
        this.avoidDistance = avoidDistanceIn;
        this.farSpeed = farSpeedIn;
        this.nearSpeed = nearSpeedIn;
        this.navigation = entityIn.getNavigator();
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
    	
        if ( this.entity.getRNG().nextInt(30) != 0 )
        {
        	return false;
        }
        
        @SuppressWarnings("unchecked")
		List<T> list = this.entity.world.<T>getEntitiesWithinAABB(this.classToAvoid, this.entity.getEntityBoundingBox().grow((double)this.avoidDistance, 3.0D, (double)this.avoidDistance), Predicates.and(EntitySelectors.CAN_AI_TARGET, this.canBeSeenSelector, this.avoidTargetSelector));

        if (list.isEmpty())
        {
            return false;
        }
        else
        {
            this.closestLivingEntity = list.get(0);
            if ( !(this.closestLivingEntity instanceof EntityPlayer) ) return false;
    		Province province = CivilizationUtil.getProvinceAt(this.closestLivingEntity.world, this.closestLivingEntity.chunkCoordX, this.closestLivingEntity.chunkCoordZ);

    		if (province == null)
    		{
    			return false;
    		}

    		CivilizationType civ = province.civilization;

    		if ( civ == null )
    		{
    			return false;
    		}
    		
    		if ( PlayerCivilizationCapabilityImpl.get((EntityPlayer)this.closestLivingEntity).getReputation(civ) > -50 )
    		{
    			return false;
    		}
    		
            Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.entity, 16, 8, new Vec3d(this.closestLivingEntity.posX, this.closestLivingEntity.posY, this.closestLivingEntity.posZ));

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