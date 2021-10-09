package net.torocraft.toroquest.entities.ai;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class EntityAIDespawn extends EntityAIBase
{

	private EntityAnimal entity;
	private BlockPos spawn;

	public EntityAIDespawn(EntityAnimal entity)
	{
		super();
		this.entity = entity;
		this.spawn = entity.getPosition();
		this.setMutexBits(3);
	}

	@Override
	public boolean shouldExecute()
	{
		if ( entity.world.getWorldTime() == 22000 )
		{
			if ( !entity.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(entity.getPosition()).grow(25, 15, 25)).isEmpty() || ( spawn != null && entity.getDistanceSq(spawn) >= 400 ) )
			{
				try {entity.tasks.removeTask( this );} catch ( Exception e ) {}
			}
			else
			{
				entity.setHealth(0);
				entity.setDead();
			}
		}
		return false;
	}

}
