package net.torocraft.toroquest.entities.ai;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.torocraft.toroquest.entities.EntityGuard;
import net.torocraft.toroquest.entities.EntityOrc;
import net.torocraft.toroquest.entities.EntitySentry;
import net.torocraft.toroquest.entities.EntityToroMob;
import net.torocraft.toroquest.entities.EntityToroNpc;

public class EntityAINearestAttackableBanditTarget extends EntityAITarget
{
	
    protected final EntityAINearestAttackableTarget.Sorter sorter;
	protected final Predicate<EntityLiving> targetEntitySelector;
	protected EntityLiving targetEntity;

	protected EntityGuard taskOwner;
	
	public EntityAINearestAttackableBanditTarget( EntityGuard npc )
	{
		//    checkSight, onlyNearby
		super( npc, false, false );
		this.taskOwner = npc;
		this.sorter = new EntityAINearestAttackableTarget.Sorter(npc);
		this.setMutexBits(1);
		
		this.targetEntitySelector = new Predicate<EntityLiving>()
		{
			public boolean apply(@Nullable EntityLiving target)
			{
				if (!isSuitableTarget(taskOwner, target, false, false))
				{
					return false;
				}
				
				if ( target.getAttackTarget() instanceof EntityToroNpc || target.getAttackTarget() instanceof EntityVillager )
				{
					return true;
				}
				
				if ( !taskOwner.playerGuard.equals("") )
				{
					if ( target.getAttackTarget() instanceof EntityPlayer )
					{
						if ( target.getAttackTarget().getName().equals(taskOwner.playerGuard) )
						{
							return true;
						}
					}
					
					if ( target.getRevengeTarget() instanceof EntityPlayer )
					{
						if ( target.getRevengeTarget().getName().equals(taskOwner.playerGuard) )
						{
							return true;
						}
					}
					
					if ( target instanceof IMob || target instanceof EntityMob )
					{
						if ( target instanceof EntitySentry && !(target instanceof EntityOrc) )
						{
							EntitySentry t = (EntitySentry)(target);
							if ( t.getTame() || t.passiveTimer > 0 )
							{
								return false;
							}
							else
							{
								return true;
							}
						}
						else if ( taskOwner.getDistance(target) <= 16 || (taskOwner.raidX != null && taskOwner.raidZ != null && taskOwner.getDistance(taskOwner.raidX, taskOwner.posY, taskOwner.raidZ) <= 24) )
						{
							return true;
						}
					}
				}
				else
				{
					if ( target instanceof IMob || target instanceof EntityMob )
					{
						if ( target instanceof EntitySentry )
						{
							return true;
						}
						if ( taskOwner.getDistance(target) <= 12 || (taskOwner.raidX != null && taskOwner.raidZ != null && taskOwner.getDistance(taskOwner.raidX, taskOwner.posY, taskOwner.raidZ) <= 24) )
						{
							return true;
						}
					}
				}
				
				if ( target instanceof EntityTameable )
				{
					EntityTameable tameable = (EntityTameable) target;
										
					if ( !tameable.isTamed() && target.getHealth() > 0 )
					{
						if ( target.getEntityData().hasKey("ModelDead") )
						{
							return !target.getEntityData().getBoolean("ModelDead");
						}
						if ( target.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE) != null && target.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue() >= 1 )
						{
						    return true;
						}
					}
				}
				return false;
			}
		};
	}

	Random rand = new Random();
	
	@Override
	public boolean shouldExecute()
	{
		if ( !this.taskOwner.searchNextEnemy && rand.nextInt(16) != 0 )
        {
			return false;
	    }
		
		this.taskOwner.searchNextEnemy = false;
		
		List<EntityLiving> list = this.taskOwner.world.<EntityLiving>getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(this.taskOwner.getPosition()).grow(32, 16, 32), this.targetEntitySelector);
	
		if ( list.isEmpty() )
		{
			return false;
		}
		else
		{
			Collections.sort(list, this.sorter);

			for ( EntityLiving npc : list )
			{
				if ( npc instanceof EntityToroMob || npc.getAttackTarget() instanceof EntityVillager || npc.getAttackTarget() instanceof EntityToroNpc || this.taskOwner.getDistance(npc) <= 5.0D )
				{
					this.targetEntity = npc;
					return true;
				}
			}
			for ( EntityLiving npc : list )
			{
				if ( this.taskOwner.canEntityBeSeen(npc) && !npc.isInvisible() )
				{
					this.targetEntity = npc;
					return true;
				}
			}
			return false;
		}
	}
	
//	protected AxisAlignedBB getTargetableArea(double targetDistance)
//	{
//		return this.taskOwner.getEntityBoundingBox().grow(targetDistance, 32, targetDistance);
//	}

	public static class Sorter implements Comparator<Entity>
	{
		private final Entity theEntity;

		public Sorter(Entity theEntityIn)
		{
			this.theEntity = theEntityIn;
		}

		public int compare(Entity entity1, Entity entity2)
		{
			double d0 = this.theEntity.getDistanceSq( entity1 );
			double d1 = this.theEntity.getDistanceSq( entity2 );
			return d0 < d1 ? -1 : (d0 > d1 ? 1 : 0);
		}
	}
	
	@Override
    public void startExecuting()
    {
        this.taskOwner.setAttackTarget(this.targetEntity);
        super.startExecuting();
    }
}