package net.torocraft.toroquest.entities.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIDoorInteract;
import net.minecraft.init.Blocks;
import net.torocraft.toroquest.entities.EntitySentry;

public class EntityAIBreakDoorBandit extends EntityAIDoorInteract
{
	// EntityAIBreakDoor
    private int breakingTime;
    private int previousBreakProgress = -1;
    EntitySentry creature;
    
    public EntityAIBreakDoorBandit(EntitySentry entityIn)
    {
        super(entityIn);
    	this.creature = entityIn;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
    public boolean shouldExecute()
    {
        if (!super.shouldExecute())
        {
            return false;
        }
        else if ( !this.entity.world.getBlockState(this.doorPosition).getBlock().canEntityDestroy(this.entity.world.getBlockState(this.doorPosition), this.entity.world, this.doorPosition, this.entity) )
        {
            return false;
        }
        else if ( this.creature != null && this.creature.getAttackTarget() == null && !this.creature.isRiding() )
        {
        	return false;
        }
        else
        {
            BlockDoor blockdoor = this.doorBlock;
            
            Block block = this.entity.world.getBlockState(this.doorPosition).getBlock();
            
            if ( block == null )
            {
            	return false;
            }
            else if ( block.getBlockState() != Blocks.IRON_DOOR.getBlockState() && ( block instanceof BlockDoor || block instanceof BlockTrapDoor || block instanceof BlockFence || block instanceof BlockFenceGate ) )
            {
            	return !BlockDoor.isOpen(this.entity.world, this.doorPosition);
            }
            else
            {
            	return false;
            }
        }
        // else if (!net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.entity.world, this.entity) || !this.entity.world.getBlockState(this.doorPosition).getBlock().canEntityDestroy(this.entity.world.getBlockState(this.doorPosition), this.entity.world, this.doorPosition, this.entity) || !net.minecraftforge.event.ForgeEventFactory.onEntityDestroyBlock(this.entity, this.doorPosition, this.entity.world.getBlockState(this.doorPosition)))
        // !net.minecraftforge.event.ForgeEventFactory.onEntityDestroyBlock(this.entity, this.doorPosition, this.entity.world.getBlockState(this.doorPosition)))
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void startExecuting()
    {
        super.startExecuting();
        this.breakingTime = 0;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Override
    public boolean shouldContinueExecuting()
    {
    	double d0 = this.entity.getDistanceSq(this.doorPosition);

        if (this.breakingTime <= 120)
        {
            BlockDoor blockdoor = this.doorBlock;

            if ( !BlockDoor.isOpen(this.entity.world, this.doorPosition) && d0 < 4.0D )
            {
                this.setMutexBits(4);
                return true;
            }
        }

        return false;
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    @Override
    public void resetTask()
    {
        super.resetTask();
        this.entity.world.sendBlockBreakProgress(this.entity.getEntityId(), this.doorPosition, -1);
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    @Override
    public void updateTask()
    {
        super.updateTask();
    	this.entity.getNavigator().clearPath();

        if (this.entity.getRNG().nextInt(20) == 0)
        {
            this.entity.world.playEvent(1019, this.doorPosition, 0);
        }

        ++this.breakingTime;
        int i = this.breakingTime/12;

        if (i != this.previousBreakProgress)
        {
            this.entity.world.sendBlockBreakProgress(this.entity.getEntityId(), this.doorPosition, i);
            this.previousBreakProgress = i;
        }

        if ( this.breakingTime >= 40 )
        {
            this.entity.getLookHelper().setLookPosition(this.doorPosition.getX(), this.doorPosition.getY(), this.doorPosition.getZ(), 30.0F, 30.0F);
	        if (this.breakingTime >= 120)
	        {
	        	/*
	        	this.entity.world.setBlockToAir(this.doorPosition);
	            this.entity.world.playEvent(1021, this.doorPosition, 0);
	            this.entity.world.playEvent(2001, this.doorPosition, Block.getIdFromBlock(this.doorBlock));
	            */
	        	this.entity.world.setBlockToAir(this.doorPosition);
	            this.entity.world.setBlockState(this.doorPosition, Blocks.AIR.getDefaultState(), 2);
	            this.entity.world.setBlockState(this.doorPosition.down(), Blocks.AIR.getDefaultState(), 2);
	            this.entity.world.playEvent(1021, this.doorPosition, 0);
	            this.entity.world.playEvent(2001, this.doorPosition, Block.getIdFromBlock(this.doorBlock));
	            this.resetTask();
	            this.breakingTime = 0;
	            this.previousBreakProgress = 0;
	        }
        }
    }
}