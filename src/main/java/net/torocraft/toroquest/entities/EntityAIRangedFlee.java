package net.torocraft.toroquest.entities;


import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.ItemBow;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityAIRangedFlee extends EntityAIBase
{
    protected final EntityCreature creature;
    protected double speed;
    protected double randPosX;
    protected double randPosY;
    protected double randPosZ;

    public EntityAIRangedFlee( EntityCreature creature, double speedIn )
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
		if ( this.creature.getHeldItemMainhand().getItem() instanceof ItemBow )
		{
    		EntityLivingBase attacker = this.creature.getAttackTarget();
    		if ( attacker != null && !(attacker instanceof EntityVillager) && !this.creature.isHandActive() && this.creature.getDistanceSq( attacker ) <= 40 )
    		{
    			this.creature.resetActiveHand();
	            boolean flag = this.findRandomPosition(attacker);
	            
	        	int i = 0;
	            while ( !flag || i < 8 )
	            {
	            	flag = this.findRandomPosition(attacker);
	            	i++;
	            }
	            
	            this.creature.setSprinting(flag);
	            return flag;
    		}
            this.creature.setSprinting(false);
		}
    	return false;
    }
    
    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.creature.getNavigator().tryMoveToXYZ(this.randPosX, this.randPosY, this.randPosZ, this.speed);
        // this.creature.getLookHelper().setLookPosition(this.creature.posX, this.creature.posY + (double)this.creature.getEyeHeight(), this.creature.posZ, (float)this.creature.getHorizontalFaceSpeed(), (float)this.creature.getVerticalFaceSpeed());
    }

    protected boolean findRandomPosition( EntityLivingBase attacker )
    {
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