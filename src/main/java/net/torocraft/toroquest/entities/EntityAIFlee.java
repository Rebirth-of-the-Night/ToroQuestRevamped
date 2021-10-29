package net.torocraft.toroquest.entities;


import javax.annotation.Nullable;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import scala.util.Random;

public class EntityAIFlee extends EntityAIBase
{
    protected final EntitySentry creature;
    // protected EntityLivingBase attacker;
    protected double speed;
    protected double randPosX;
    protected double randPosY;
    protected double randPosZ;
    //protected boolean running;
    protected int runningTimer = 0;
    
    public EntityAIFlee( EntitySentry creature, double speedIn )
    {
        this.creature = creature;
        this.speed = speedIn;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
    	if ( this.creature.isDrinkingPotion() || this.creature.useHealingPotion )
    	{
            this.creature.fleeing = false;
            this.runningTimer = 0;
    	    return false;
    	}
    	
    	if ( this.runningTimer > 0 && this.creature.hasPath() && this.creature.getRevengeTarget() != null )
    	{
    		this.creature.fleeing = true;
            this.creature.setSprinting(true);
    		this.creature.faceAwayEntity(this.creature.getRevengeTarget());
        	this.creature.setAttackTarget(null);
        	return true;
    	}
    	
    	if ( this.creature.getRevengeTarget() != null && !this.creature.isInWater() && ( this.creature.forceFleeing || ( this.creature.getHealth() <= this.creature.fleeModifier * this.creature.getMaxHealth() + ((this.runningTimer>0)?1.5F:0.0F) ) ) )
		{
			//this.attacker = victim;
			
            boolean flag = this.findRandomPosition(this.creature.getRevengeTarget());
            int i = 0;
            
            while ( !flag || i < 8 )
            {
            	flag = this.findRandomPosition(this.creature.getRevengeTarget());
            	i++;
            }
            
            if ( flag )
            {
    			this.creature.resetActiveHand();

                this.creature.faceAwayEntity(this.creature.getRevengeTarget());
            	this.creature.setAttackTarget(null);
            	
            	this.creature.fleeing = true;
                this.creature.forceFleeing = false;

                this.creature.setSprinting(true);
                if ( this.runningTimer < 1 )
                {
                	this.runningTimer = 25 + this.creature.world.rand.nextInt(25);
                }
                return true;
            }
		}
        this.creature.fleeing = false;
        this.runningTimer = 0;
	    return false;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        if ( this.creature.getNavigator().tryMoveToXYZ(this.randPosX, this.randPosY, this.randPosZ, this.speed) )
        {
    		this.creature.setSprinting(true);
            this.creature.fleeing = true;
        }
        // this.creature.getLookHelper().setLookPosition(this.creature.posX, this.creature.posY + (double)this.creature.getEyeHeight(), this.creature.posZ, (float)this.creature.getHorizontalFaceSpeed(), (float)this.creature.getVerticalFaceSpeed());
    }
    Random rand = new Random();
    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean shouldContinueExecuting()
    {
    	
//    	if ( this.creature.getNavigator().tryMoveToXYZ(this.randPosX, this.randPosY, this.randPosZ, this.speed) )
//        {
//    		this.creature.setSprinting(true);
//            this.creature.fleeing = true;
//        }
    	
    	if ( this.creature.isDrinkingPotion() || this.creature.useHealingPotion ) // Backup to stop the task
    	{
    		this.creature.setSprinting(false);
            this.runningTimer = 0;
            this.creature.fleeing = false;
            this.creature.getNavigator().clearPath();
        	return false;
    	}
    	
        if ( --this.runningTimer < 1 && this.creature.limitPotions > 0 ) // If the bandit has ran far enough and has a potion
        {
        	if ( this.creature.getRevengeTarget() != null )
        	{
        		this.creature.setRevengeTarget(this.creature.getRevengeTarget());
        		this.creature.setAttackTarget(this.creature.getRevengeTarget());
                this.creature.faceEntity(this.creature.getRevengeTarget(), 20.0F, 20.0F);
        	}
    		this.creature.useHealingPotion = true;
    		this.creature.setSprinting(false);
            this.runningTimer = 0;
            this.creature.fleeing = false;
            this.creature.getNavigator().clearPath();
        	return false;        	
        }
        else
    	{
        	if ( this.creature.getRevengeTarget() != null )
        	{
        		// Reset revenge timer so that the bandit will keep running
        		if ( this.rand.nextInt(3) == 0 ) this.creature.setRevengeTarget(this.creature.getRevengeTarget());
                this.creature.faceAwayEntity(this.creature.getRevengeTarget());
                
                if ( !this.creature.getNavigator().tryMoveToXYZ(this.randPosX, this.randPosY, this.randPosZ, this.speed) )
                {
                	this.runningTimer -= 5;
                }
        	}
    		return shouldExecute();
    	}
    }
    
    @Override
    public void resetTask()
    {
		this.creature.setSprinting(false);
        this.creature.fleeing = false;
        this.runningTimer = 0;
    	//running = false;
    	super.resetTask();
    }
    
    protected boolean findRandomPosition( EntityLivingBase attacker )
    {
    	if ( attacker == null ) // || attacker.getPositionVector() == null )
    	{
    		return false;
    	}
    	
    	Vec3d enemyPos = attacker.getPositionVector();
    	
    	if ( enemyPos == null )
    	{
    		return false;
    	}
    	
        Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.creature, 16, 8, enemyPos);
        
        if ( vec3d == null )
        {
            return false;
        }
        else
        {
            this.randPosX = vec3d.x;
            this.randPosY = vec3d.y;
            this.randPosZ = vec3d.z;
            return true;
        }
    }

    @Nullable
    private BlockPos getRandPos(World worldIn, Entity entityIn, int horizontalRange, int verticalRange)
    {
        BlockPos blockpos = new BlockPos(entityIn);
        int i = blockpos.getX();
        int j = blockpos.getY();
        int k = blockpos.getZ();
        float f = (float)(horizontalRange * horizontalRange * verticalRange * 2);
        BlockPos blockpos1 = null;
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for (int l = i - horizontalRange; l <= i + horizontalRange; ++l)
        {
            for (int i1 = j - verticalRange; i1 <= j + verticalRange; ++i1)
            {
                for (int j1 = k - horizontalRange; j1 <= k + horizontalRange; ++j1)
                {
                    blockpos$mutableblockpos.setPos(l, i1, j1);
                    IBlockState iblockstate = worldIn.getBlockState(blockpos$mutableblockpos);

                    if (iblockstate.getMaterial() == Material.WATER)
                    {
                        float f1 = (float)((l - i) * (l - i) + (i1 - j) * (i1 - j) + (j1 - k) * (j1 - k));

                        if (f1 < f)
                        {
                            f = f1;
                            blockpos1 = new BlockPos(blockpos$mutableblockpos);
                        }
                    }
                }
            }
        }
        return blockpos1;
    }
}