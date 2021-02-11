package net.torocraft.toroquest.entities.ai;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.List;
import java.util.Random;

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
import net.torocraft.toroquest.entities.EntityGuard;
import net.torocraft.toroquest.entities.EntityToroNpc;
import net.torocraft.toroquest.entities.EntityGuard;

public class EntityAIAvoidTooClose<T extends Entity> extends EntityAIBase
{
    private final Predicate<Entity> canBeSeenSelector;
    /** The entity we are attached to */
    protected EntityGuard entity;
    private final double farSpeed;
    private final double nearSpeed;
    //private final float avoidDistance;
    /** The PathEntity of our entity */
    private Path path;
    /** The PathNavigate of our entity */
    private final PathNavigate navigation;
    /** Class of entity this behavior seeks to avoid */
    //private final Predicate <? super T > avoidTargetSelector;
    private Random rand = new Random();

    public EntityAIAvoidTooClose(EntityGuard entityIn, double farSpeedIn, double nearSpeedIn)
    {
        this(entityIn, Predicates.alwaysTrue(), farSpeedIn, nearSpeedIn);
    }

    public EntityAIAvoidTooClose(EntityGuard entityIn, Predicate <? super T > avoidTargetSelectorIn, double farSpeedIn, double nearSpeedIn)
    {
        this.canBeSeenSelector = new Predicate<Entity>()
        {
            public boolean apply(@Nullable Entity p_apply_1_)
            {
                return p_apply_1_.isEntityAlive() && EntityAIAvoidTooClose.this.entity.getEntitySenses().canSee(p_apply_1_) && !EntityAIAvoidTooClose.this.entity.isOnSameTeam(p_apply_1_);
            }
        };
        this.entity = entityIn;
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
    	if ( this.entity.inCombat() )
    	{
    		return false;
    	}
    	
    	if ( this.rand.nextInt(8) != 0 )
    	{
    		return false;
    	}
    	
    	if ( rand.nextInt(8) == 0 )
        {
	    	List<EntityPlayer> players = this.entity.world.<EntityPlayer>getEntitiesWithinAABB(EntityPlayer.class, this.entity.getEntityBoundingBox().grow(3.0D, 3.0D, 3.0D));
	        
	    	for ( EntityPlayer player : players )
	        {
            	if ( player.getDistanceSq(this.entity) >= 1.5D )
            	{
            		if ( rand.nextInt(4) == 0 )
                    {
	                	if ( entity.actionTimer() <= 0 )
	                	{
	                		entity.setActionTimer(3);
	                		entity.getNavigator().clearPath();
	                        entity.getLookHelper().setLookPosition(player.posX, player.posY + (double)player.getEyeHeight(), player.posZ, (float)this.entity.getHorizontalFaceSpeed(), (float)this.entity.getVerticalFaceSpeed());
	                		entity.guardSpeak((EntityPlayer)(player));
	                	}
                    }
            	}
            	else if ( entity.actionTimer() <= 2 )
            	{
            		Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.entity, 8, 8, new Vec3d(player.posX, player.posY, player.posZ));
                	
                    if (vec3d == null)
                    {
                        return false;
                    }
                    else if (player.getDistanceSq(vec3d.x, vec3d.y, vec3d.z) < player.getDistanceSq(this.entity))
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
	    	return false;
        }
    	
        List<EntityToroNpc> guards = this.entity.world.<EntityToroNpc>getEntitiesWithinAABB(EntityToroNpc.class, this.entity.getEntityBoundingBox().grow(3.0D, 3.0D, 3.0D));
        
        for ( EntityToroNpc guard : guards )
        {
        	Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.entity, 8, 8, new Vec3d(guard.posX, guard.posY, guard.posZ));
        	
            if (vec3d == null)
            {
                return false;
            }
            else if (guard.getDistanceSq(vec3d.x, vec3d.y, vec3d.z) < guard.getDistanceSq(this.entity))
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
        //this.closestLivingEntity = null;
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void updateTask()
    {
//        if (this.entity.getDistanceSq(this.closestLivingEntity) < 32.0D)
//        {
//            this.entity.getNavigator().setSpeed(this.nearSpeed);
//        }
//        else
//        {
//            this.entity.getNavigator().setSpeed(this.farSpeed);
//        }
    }
}