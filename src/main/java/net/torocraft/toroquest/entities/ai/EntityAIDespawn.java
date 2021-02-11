package net.torocraft.toroquest.entities.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class EntityAIDespawn extends EntityAIBase
{

	private EntityCreature entity;
	private BlockPos spawn;

	public EntityAIDespawn(EntityCreature entity)
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
			if ( entity.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(entity.getPosition()).grow(32, 16, 32)).isEmpty() || ( spawn != null && entity.getDistanceSq(spawn) > 400 ) )
			{
				try {entity.tasks.removeTask( this );} catch ( Exception e ) {}
			}
			else
			{
				if ( entity instanceof EntityTameable && ((EntityTameable)entity).isTamed() )
				{
					try {entity.tasks.removeTask( this );} catch ( Exception e ) {}
				}
				else
				{
					entity.setHealth(0);
					entity.setDead();
				}
			}
		}
		return false;
	}

}
