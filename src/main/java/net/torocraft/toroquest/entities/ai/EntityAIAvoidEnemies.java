package net.torocraft.toroquest.entities.ai;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.torocraft.toroquest.civilization.CivilizationType;
import net.torocraft.toroquest.civilization.CivilizationUtil;
import net.torocraft.toroquest.civilization.Province;
import net.torocraft.toroquest.civilization.player.PlayerCivilizationCapabilityImpl;
import net.torocraft.toroquest.config.ToroQuestConfiguration;
import net.torocraft.toroquest.entities.EntityToroVillager;

public class EntityAIAvoidEnemies extends EntityAIBase
{
    /** The entity we are attached to */
    protected EntityToroVillager entity;
    private final double farSpeed;
    private final double nearSpeed;
    protected EntityLivingBase closestLivingEntity;
    /** The PathEntity of our entity */
    private Path path;
    /** The PathNavigate of our entity */
    private final PathNavigate navigation;
    /** Class of entity this behavior seeks to avoid */

    public EntityAIAvoidEnemies(EntityToroVillager entityIn, double farSpeedIn, double nearSpeedIn)
    {
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
        if ( this.entity.getRNG().nextInt(30) != 0 )
        {
        	return false;
        }

        /* MONSTERS */
    	List<EntityLiving> list = this.entity.world.getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(this.entity.getPosition()).grow(12.0D, 6.0D, 12.0D), new Predicate<EntityLiving>()
		{
			public boolean apply(@Nullable EntityLiving enemy )
			{
				if ( enemy instanceof IMob || enemy instanceof EntityMob )
				{
					return true;
				}
				return false;
			}
		});
        for ( EntityLiving e : list )
        {
            this.closestLivingEntity = e;
            
            if ( ToroQuestConfiguration.iMobAttackVillagers && e.getAttackTarget() != null )
            {
            	e.setAttackTarget(this.entity);
            }
            
            Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.entity, 16, 8, new Vec3d(e.posX, e.posY, e.posZ));

            if ( vec3d == null || e.getDistanceSq(vec3d.x, vec3d.y, vec3d.z) < e.getDistanceSq(this.entity) )
            {
            	vec3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.entity, 16, 8, new Vec3d(e.posX, e.posY, e.posZ));
            }
            
            if ( vec3d == null )
            {
            	return false;
            }
            
            this.path = this.navigation.getPathToXYZ(vec3d.x, vec3d.y, vec3d.z);
            return this.path != null;
        }
        
        /* PLAYER */
        EntityPlayer player = this.entity.world.getClosestPlayerToEntity(this.entity, 12);
        
        if ( player != null )
        {
        	this.closestLivingEntity = player;
        	
    		Province province = CivilizationUtil.getProvinceAt(player.world, player.chunkCoordX, player.chunkCoordZ);

    		if ( province == null )
    		{
    			return false;
    		}

    		CivilizationType civ = province.getCiv();

    		if ( civ == null )
    		{
    			return false;
    		}
    		
    		if ( PlayerCivilizationCapabilityImpl.get(player).getReputation(civ) > -50 )
    		{
    			return false;
    		}
    		
            Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.entity, 16, 8, new Vec3d(player.posX, player.posY, player.posZ));

            if ( vec3d == null || player.getDistanceSq(vec3d.x, vec3d.y, vec3d.z) < player.getDistanceSq(this.entity) )
            {
            	vec3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.entity, 16, 8, new Vec3d(player.posX, player.posY, player.posZ));
            }
            
            if ( vec3d == null )
            {
            	return false;
            }
            
            this.path = this.navigation.getPathToXYZ(vec3d.x, vec3d.y, vec3d.z);
            return this.path != null;
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