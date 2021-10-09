package net.torocraft.toroquest.entities.ai;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigateGround;
import net.torocraft.toroquest.entities.EntityGuard;

public class EntityAISmartTempt extends EntityAIBase
{
    /** The entity using this AI that is tempted by the player. */
    public final EntityCreature temptedEntity;
    public final double speed;
    /** X position of player tempting this mob */
    public double targetX;
    /** Y position of player tempting this mob */
    public double targetY;
    /** Z position of player tempting this mob */
    public double targetZ;
    /** Tempting player's pitch */
    public double pitch;
    /** Tempting player's yaw */
    public double yaw;
    /** The player that is tempting the entity that is using this AI. */
    public EntityPlayer temptingPlayer;
    /**
     * A counter that is decremented each time the shouldExecute method is called. The shouldExecute method will always
     * return false if delayTemptCounter is greater than 0.
     */
    public int delayTemptCounter;
    /** True if this EntityAISmartTempt task is running */
    public boolean isRunning;
    public final Set<Item> temptItem;
    
    public EntityAISmartTempt(EntityCreature temptedEntityIn, double speedIn, Item temptItemIn)
    {
        this(temptedEntityIn, speedIn, Sets.newHashSet(temptItemIn));
    }

    public EntityAISmartTempt(EntityCreature temptedEntityIn, double speedIn, Set<Item> temptItemIn)
    {
        this.temptedEntity = temptedEntityIn;
        this.speed = speedIn;
        this.temptItem = temptItemIn;
        this.setMutexBits(3);

        if (!(temptedEntityIn.getNavigator() instanceof PathNavigateGround))
        {
            throw new IllegalArgumentException("Unsupported mob type for TemptGoal");
        }
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (this.delayTemptCounter > 0)
        {
            --this.delayTemptCounter;
            return false;
        }
        else
        {
            this.temptingPlayer = this.temptedEntity.world.getClosestPlayerToEntity(this.temptedEntity, 25.0D);
        	
            if (this.temptingPlayer == null)
            {
                return false;
            }
            else
            {
            	if ( this.temptItem == Items.AIR )
            	{
            		if ( this.temptedEntity instanceof EntityGuard )
            		{
            			//EntityGuard.guardSpeak((EntityGuard)this.temptedEntity, this.temptingPlayer);
            			return true;
            		}
            		return true;
            	}
            	
                return this.isTempting(this.temptingPlayer.getHeldItemMainhand()) || this.isTempting(this.temptingPlayer.getHeldItemOffhand());
            }
        }
    }

    protected boolean isTempting(ItemStack stack)
    {
        return (this.temptItem.contains(stack.getItem()));
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean shouldContinueExecuting()
    {
//        if (this.scaredByPlayerMovement)
//        {
//            if (this.temptedEntity.getDistanceSq(this.temptingPlayer) < 36.0D)
//            {
//                if (this.temptingPlayer.getDistanceSq(this.targetX, this.targetY, this.targetZ) > 0.010000000000000002D)
//                {
//                    return false;
//                }
//
//                if (Math.abs((double)this.temptingPlayer.rotationPitch - this.pitch) > 5.0D || Math.abs((double)this.temptingPlayer.rotationYaw - this.yaw) > 5.0D)
//                {
//                    return false;
//                }
//            }
//            else
//            {
//                this.targetX = this.temptingPlayer.posX;
//                this.targetY = this.temptingPlayer.posY;
//                this.targetZ = this.temptingPlayer.posZ;
//            }
//
//            this.pitch = (double)this.temptingPlayer.rotationPitch;
//            this.yaw = (double)this.temptingPlayer.rotationYaw;
//        }

        return this.shouldExecute();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.targetX = this.temptingPlayer.posX;
        this.targetY = this.temptingPlayer.posY;
        this.targetZ = this.temptingPlayer.posZ;
        this.isRunning = true;
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void resetTask()
    {
        this.temptingPlayer = null;
        this.temptedEntity.getNavigator().clearPath();
        this.delayTemptCounter = 40;
        this.isRunning = false;
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void updateTask()
    {
        this.temptedEntity.getLookHelper().setLookPositionWithEntity(this.temptingPlayer, (float)(this.temptedEntity.getHorizontalFaceSpeed() + 20), (float)this.temptedEntity.getVerticalFaceSpeed());

        if (this.temptedEntity.getDistanceSq(this.temptingPlayer) < 4.5D)
        {
            this.temptedEntity.getNavigator().clearPath();
        }
        else
        {
            this.temptedEntity.getNavigator().tryMoveToEntityLiving(this.temptingPlayer, this.speed);
        }
    }

    /**
     * @see #isRunning
     */
    public boolean isRunning()
    {
        return this.isRunning;
    }
}