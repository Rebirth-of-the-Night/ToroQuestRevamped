package net.torocraft.toroquest.entities.ai;


import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.torocraft.toroquest.entities.EntityToro;

public class EntityAIToroFlee extends EntityAIBase
{
    protected final EntityToro creature;
    protected EntityLivingBase attacker;
    protected double speed;
    protected double randPosX;
    protected double randPosY;
    protected double randPosZ;

    public EntityAIToroFlee( EntityToro creature, double speedIn )
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
    	if ( this.creature.isBurning() )
        {
            BlockPos blockpos = this.getRandPos(this.creature.world, this.creature, 16, 4);
            if (blockpos != null)
            {
                this.randPosX = (double)blockpos.getX();
                this.randPosY = (double)blockpos.getY();
                this.randPosZ = (double)blockpos.getZ();
                return true;
            }
        }
    	else if ( !this.creature.isCharging() && this.creature.getAttackTarget() != null && this.creature.getAttackTarget().isEntityAlive() )
		{
    		return this.findRandomPosition();
		}
    	return false;
    }

    protected boolean findRandomPosition()
    {
    	Vec3d enemyPos = this.attacker.getPositionVector();
        Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.creature, 64, 32, enemyPos);

        if (vec3d == null)
        {
            return false;
        }
        else
        {
            this.randPosX = vec3d.x + (this.creature.posX - this.attacker.posX)/2;
            this.randPosY = vec3d.y;
            this.randPosZ = vec3d.z + (this.creature.posZ - this.attacker.posZ)/2;
            return true;
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.creature.getNavigator().tryMoveToXYZ(this.randPosX, this.randPosY, this.randPosZ, this.speed);
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean shouldContinueExecuting()
    {
        return !this.creature.getNavigator().noPath();
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