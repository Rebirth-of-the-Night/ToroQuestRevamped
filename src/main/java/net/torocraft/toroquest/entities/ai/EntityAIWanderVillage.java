package net.torocraft.toroquest.entities.ai;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.Village;
import net.minecraft.village.VillageDoorInfo;
import net.torocraft.toroquest.entities.EntityAdventurer;

public class EntityAIWanderVillage extends EntityAIBase
{
    private final EntityAdventurer entity;
    private final double movementSpeed;
    /** The PathNavigate of our entity. */
    private Path path;
    private VillageDoorInfo doorInfo;
    private final List<VillageDoorInfo> doorList = Lists.<VillageDoorInfo>newArrayList();
    private Random rand = new Random();
    
    private Village village = null;

    public EntityAIWanderVillage(EntityAdventurer entityIn, double movementSpeedIn)
    {
        this.entity = entityIn;
        this.movementSpeed = movementSpeedIn;
        this.setMutexBits(1);

        if (!(entityIn.getNavigator() instanceof PathNavigateGround))
        {
            throw new IllegalArgumentException("Unsupported mob for MoveThroughVillageGoal");
        }
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
    	if ( this.entity.getAttackTarget() != null || rand.nextInt(200) != 0 )
    	{
    		return false;
    	}
    	
        this.resizeDoorList();
        {
            if (village == null)
            {
                return false;
            }
            else
            {
                this.village = this.entity.world.getVillageCollection().getNearestVillage(new BlockPos(this.entity), 88);

                this.doorInfo = this.findNearestDoor(village);

                if (this.doorInfo == null)
                {
                    return false;
                }
                else
                {
                    PathNavigateGround pathnavigateground = (PathNavigateGround)this.entity.getNavigator();
                    boolean flag = pathnavigateground.getEnterDoors();
                    pathnavigateground.setBreakDoors(false);
                    this.path = pathnavigateground.getPathToPos(this.doorInfo.getDoorBlockPos());
                    pathnavigateground.setBreakDoors(flag);

                    if (this.path != null)
                    {
                        return true;
                    }
                    else
                    {
                        Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockTowards(this.entity, 16, 12, new Vec3d((double)this.doorInfo.getDoorBlockPos().getX(), (double)this.doorInfo.getDoorBlockPos().getY(), (double)this.doorInfo.getDoorBlockPos().getZ()));

                        if (vec3d == null)
                        {
                            return false;
                        }
                        else
                        {
                            pathnavigateground.setBreakDoors(false);
                            this.path = this.entity.getNavigator().getPathToXYZ(vec3d.x, vec3d.y, vec3d.z);
                            pathnavigateground.setBreakDoors(flag);
                            return this.path != null;
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean shouldContinueExecuting()
    {
        if (this.entity.getNavigator().noPath())
        {
            return false;
        }
        else
        {
			float f = this.entity.width + 4.0F;
            return this.entity.getDistanceSq(this.doorInfo.getDoorBlockPos()) > (double)(f * f);
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.entity.getNavigator().setPath(this.path, this.movementSpeed);
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void resetTask()
    {
        if (this.entity.getNavigator().noPath() || this.entity.getDistanceSq(this.doorInfo.getDoorBlockPos()) < 16.0D)
        {
            this.doorList.add(this.doorInfo);
        }
    }

    private VillageDoorInfo findNearestDoor(Village villageIn)
    {
        VillageDoorInfo villagedoorinfo = null;
        int i = Integer.MAX_VALUE;

        for (VillageDoorInfo villagedoorinfo1 : villageIn.getVillageDoorInfoList())
        {
            int j = villagedoorinfo1.getDistanceSquared(MathHelper.floor(this.entity.posX), MathHelper.floor(this.entity.posY), MathHelper.floor(this.entity.posZ));

            if (j < i && !this.doesDoorListContain(villagedoorinfo1))
            {
                villagedoorinfo = villagedoorinfo1;
                i = j;
            }
        }

        return villagedoorinfo;
    }

    private boolean doesDoorListContain(VillageDoorInfo doorInfoIn)
    {
        for (VillageDoorInfo villagedoorinfo : this.doorList)
        {
            if (doorInfoIn.getDoorBlockPos().equals(villagedoorinfo.getDoorBlockPos()))
            {
                return true;
            }
        }

        return false;
    }

    private void resizeDoorList()
    {
        if (this.doorList.size() > 15)
        {
            this.doorList.remove(0);
        }
    }
}