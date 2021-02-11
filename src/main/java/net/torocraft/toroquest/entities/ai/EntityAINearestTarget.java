package net.torocraft.toroquest.entities.ai;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.util.math.AxisAlignedBB;
import net.torocraft.toroquest.entities.EntityToroNpc;

public class EntityAINearestTarget extends EntityAITarget
{


	private final EntityAINearestAttackableTarget.Sorter theNearestAttackableTargetSorter;
	protected final Predicate<EntityLivingBase> targetEntitySelector;
	protected EntityLivingBase targetEntity;
	protected EntityToroNpc taskOwner;
	
	public EntityAINearestTarget(EntityToroNpc creature, boolean checkSight)
	{
		super( creature, false );
		this.theNearestAttackableTargetSorter = new EntityAINearestAttackableTarget.Sorter(taskOwner);
		this.taskOwner = creature;
		this.targetEntitySelector = new Predicate<EntityLivingBase>()
		{
			public boolean apply(@Nullable EntityLivingBase target)
			{
				if (target == null)
				{
					return false;
				}

				if (!isSuitableTarget(taskOwner, target, false, true))
				{
					return false;
				}
				return true;
			}
		};
	}



	//public EntityAINearestTarget(EntityToroNpc npc)
	

	

	public static EntityLivingBase searchForEntity( EntityToroNpc taskOwner )
	{
		// System.out.println( " go " );
		EntityAINearestAttackableTarget.Sorter theNearestAttackableTargetSorter = new EntityAINearestAttackableTarget.Sorter(taskOwner);
		Predicate<EntityLivingBase> targetEntitySelector = new Predicate<EntityLivingBase>()
		{
			public boolean apply(@Nullable EntityLivingBase target)
			{
//				if (target == null)
//				{
//					return false;
//				}
//
//				if (!isSuitableTarget(taskOwner, target, false, true))
//				{
//					return false;
//				}
				return true;
			}
		};
		
		if (taskOwner.getCivilization() == null)
		{
			return null;
		}
		
		List<EntityLivingBase> list = taskOwner.world.<EntityLivingBase>getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB( -8,-8,-8,8,8,8 ), targetEntitySelector);
		// System.out.println(list);
		if (list.isEmpty())
		{
			return null;
		}
		else
		{
			Collections.sort(list, theNearestAttackableTargetSorter);

			for (EntityLivingBase npc : list)
			{
				// System.out.println(npc);
				//System.out.println( taskOwner.getDistance(npc));
				//System.out.println((getTargetDistance()));
				//if ( taskOwner.getDistance(npc) < (getTargetDistance()) )
				//{
					return npc;
				//}
				//else
				//{
				//	return null;
				//}
			}
			return null;
		}
	}
	protected AxisAlignedBB getTargetableArea(double targetDistance)
	{
		return this.taskOwner.getEntityBoundingBox().grow(8.0, 2.0, 8.0);
	}


	public static class Sorter implements Comparator<Entity>
	{
		private final Entity theEntity;

		public Sorter(Entity theEntityIn)
		{
			this.theEntity = theEntityIn;
		}

		public int compare(Entity p_compare_1_, Entity p_compare_2_) {
			double d0 = this.theEntity.getDistanceSq(p_compare_1_);
			double d1 = this.theEntity.getDistanceSq(p_compare_2_);
			return d0 < d1 ? -1 : (d0 > d1 ? 1 : 0);
		}
	}


	@Override
	public boolean shouldExecute() {
		// TODO Auto-generated method stub
		return true;
	}
}