package net.torocraft.toroquest.entities.ai;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.torocraft.toroquest.civilization.CivilizationUtil;
import net.torocraft.toroquest.civilization.Province;
import net.torocraft.toroquest.entities.EntityGuard;

public class EntityAIDespawnGuard extends EntityAIBase
{

	private EntityGuard entity;
	private BlockPos spawn;

	public EntityAIDespawnGuard(EntityGuard entity)
	{
		super();
		this.entity = entity;
		this.spawn = entity.getPosition();
		this.setMutexBits(3);
	}

	@Override
	public boolean shouldExecute()
	{
		if ( entity.ticksExisted % 100 == 0 )
		{
			Province province = CivilizationUtil.getProvinceAt(entity.world, (int)entity.posX*16, (int)entity.posZ*16);
			
			if ( province != null )
			{
				try{entity.tasks.removeTask(entity.followNoCiv);}catch(Exception e){}
				try{entity.tasks.removeTask(this);}catch(Exception e){}
			}

			if ( entity.world.getWorldTime() >= 21900 && entity.world.getWorldTime() <= 22000 )
			{
				if ( !entity.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(entity.getPosition()).grow(25, 15, 25)).isEmpty() || ( spawn != null && entity.getDistanceSq(spawn) >= 400 ) )
				{
					try {entity.tasks.removeTask(this);} catch ( Exception e ) {}
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
