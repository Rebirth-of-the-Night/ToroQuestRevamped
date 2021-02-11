package net.torocraft.toroquest.entities.ai;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.passive.EntityMule;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.Village;
import net.minecraft.world.World;
import net.torocraft.toroquest.civilization.CivilizationType;
import net.torocraft.toroquest.civilization.CivilizationUtil;
import net.torocraft.toroquest.civilization.Province;
import net.torocraft.toroquest.civilization.player.PlayerCivilizationCapabilityImpl;
import net.torocraft.toroquest.entities.EntityGuard;
import net.torocraft.toroquest.entities.EntitySentry;
import net.torocraft.toroquest.entities.EntityToroMob;
import net.torocraft.toroquest.entities.EntityToroNpc;
import net.torocraft.toroquest.entities.EntityToroVillager;
import net.torocraft.toroquest.item.armor.ItemBanditArmor;
import net.torocraft.toroquest.item.armor.ItemLegendaryBanditArmor;

public class EntityAIBanditAttack extends EntityAITarget
{

	protected final EntityAINearestAttackableTarget.Sorter theNearestAttackableTargetSorter;
	protected final Predicate<EntityLivingBase> targetEntitySelector;
	protected EntityLivingBase targetEntity;

	protected EntityToroMob taskOwner;

	public EntityAIBanditAttack(EntityToroMob npc)
	{
		// checkSight, onlyNearby
		super(npc, true, false);
		this.taskOwner = npc;
		this.theNearestAttackableTargetSorter = new EntityAINearestAttackableTarget.Sorter(npc);
		this.setMutexBits(1);
		
		this.targetEntitySelector = new Predicate<EntityLivingBase>()
		{
			public boolean apply(@Nullable EntityLivingBase target)
			{
				if (target == null || !target.isEntityAlive())
				{
					return false;
				}

				if (!isSuitableTarget(taskOwner, target, false, true))
				{
					return false;
				}
				
				if ( target instanceof EntityToroNpc || target instanceof EntityVillager || ( target instanceof EntityPlayer && !(isPlayerBandit(target, npc) ) ) || target instanceof EntityMule )
				{
					return true;
				}
				
				return false;
			}
		};
	}
	
	public boolean isPlayerBandit( EntityLivingBase target, EntityToroMob npc )
    {
		EntityPlayer player = (EntityPlayer)target;
		if ( npc.getRevengeTarget() != null && npc.getRevengeTarget() == target )
		{
			return false;
		}
		for ( ItemStack i: player.inventory.armorInventory )
		{
			if ( i.getItem() instanceof ItemBanditArmor || i.getItem() instanceof ItemLegendaryBanditArmor )
			{
				return true;
			}
		}
		Province province = null;
		
		Village village = npc.world.getVillageCollection().getNearestVillage(new BlockPos(npc.getPosition()), 256);
		if ( village != null )
		{
			province = CivilizationUtil.getProvinceAt(npc.world, village.getCenter().getX()/16,village.getCenter().getZ()/16);
		}
		else
		{
			province = CivilizationUtil.getProvinceAt(npc.world, (int)(target.posX/16),(int)(target.posZ/16));
		}
		
		if ( province == null )
		{
			return false;
		}
		
		CivilizationType civ = province.civilization;
		
		if ( civ == null )
		{
			return false;
		}
		
		int rep = PlayerCivilizationCapabilityImpl.get((EntityPlayer)target).getReputation(civ);
		
		if ( rep <= -50 )
		{
			return true;
		}
		
		return false;
    }

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	public boolean shouldExecute()
	{
		if (shouldExecuteNonPlayer())
		{
			return true;
		}
		return false;
	}

	protected boolean shouldExecuteNonPlayer()
	{
		List<EntityLivingBase> list = this.taskOwner.world.<EntityLivingBase>getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(this.taskOwner.getPosition()).grow(40, 24, 40), targetEntitySelector);
	
		if (list.isEmpty())
		{
			//resetTask();
			//this.setMutexBits(2);
			return false;
		}
		else
		{
			Collections.sort(list, this.theNearestAttackableTargetSorter);

			for (EntityLivingBase npc : list)
			{
				targetEntity = npc;
				return true;
			}
			return false;
		}
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void startExecuting()
	{
		this.taskOwner.setAttackTarget(this.targetEntity);
		super.startExecuting();
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
}