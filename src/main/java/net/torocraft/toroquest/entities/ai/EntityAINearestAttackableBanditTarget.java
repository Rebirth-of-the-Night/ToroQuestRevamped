package net.torocraft.toroquest.entities.ai;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.torocraft.toroquest.config.ToroQuestConfiguration;
import net.torocraft.toroquest.entities.EntityGuard;
import net.torocraft.toroquest.entities.EntityToro;
import net.torocraft.toroquest.entities.EntityToroMob;
import net.torocraft.toroquest.entities.EntityToroNpc;

public class EntityAINearestAttackableBanditTarget extends EntityAITarget
{
	
    protected final EntityAINearestAttackableTarget.Sorter sorter;
	protected final Predicate<EntityLiving> targetEntitySelector;
	protected EntityLivingBase targetEntity;

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
				
				for ( String entity : ToroQuestConfiguration.guardTargetBlacklistLIST )
				{
					if ( target.getName().equals(entity) )
					{
						return false;
					}
				}
				
				if ( target instanceof IMob || target instanceof EntityMob )
				{
					 return true;
				}
				
				if ( target instanceof EntityTameable )
				{
					EntityTameable tameable = (EntityTameable) target;
										
					if ( !tameable.isTamed() && target.getHealth() > 0 )
					{
						if ( target.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE) != null && target.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue() > 0 )
						{
						    return true;
						}
					}
				}
				
				return false;
			}
		};
	}

	@Override
	public boolean shouldExecute()
	{
		if ( this.taskOwner.getRNG().nextBoolean() )
        {
			return false;
	    }
		
		List<EntityLiving> list = this.taskOwner.world.<EntityLiving>getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(this.taskOwner.getPosition()).grow(48, 32, 48), targetEntitySelector);
	
		if (list.isEmpty())
		{
			return false;
		}
		else
		{
			Collections.sort(list, this.sorter);

			for ( EntityLiving npc : list )
			{
				if ( this.taskOwner.canEntityBeSeen(npc) )
				{
					this.targetEntity = npc;
					return true;
				}
			}
			for ( EntityLiving npc : list )
			{
				this.targetEntity = npc;
				return true;
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