package net.torocraft.toroquest.entities.ai;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.torocraft.toroquest.civilization.CivilizationType;
import net.torocraft.toroquest.civilization.player.PlayerCivilizationCapabilityImpl;
import net.torocraft.toroquest.entities.EntityGuard;
import net.torocraft.toroquest.entities.EntityToroNpc;
import net.torocraft.toroquest.item.armor.ItemBanditArmor;
import net.torocraft.toroquest.item.armor.ItemLegendaryBanditArmor;

public class EntityAINearestAttackableCivTarget extends EntityAITarget {

	protected final EntityAINearestAttackableTarget.Sorter theNearestAttackableTargetSorter;
	protected final Predicate<EntityPlayer> targetEntitySelector;
	protected EntityLivingBase targetEntity;

	protected EntityGuard taskOwner;

	public EntityAINearestAttackableCivTarget(EntityGuard npc)
	{
		super(npc, true, false);
		this.taskOwner = npc;
		this.theNearestAttackableTargetSorter = new EntityAINearestAttackableTarget.Sorter(npc);
		this.setMutexBits(1);
		
		this.targetEntitySelector = new Predicate<EntityPlayer>()
		{
			public boolean apply(@Nullable EntityPlayer target)
			{

				if (!isSuitableTarget(taskOwner, target, false, true))
				{
					return false;
				}

				if (!EntitySelectors.NOT_SPECTATING.apply(target))
				{
					return false;
				}
				
				return shouldAttackPlayerBasedOnCivilization(target);
			}
		};
	}

	protected boolean shouldAttackPlayerBasedOnCivilization(EntityPlayer target)
	{

		for ( ItemStack itemStack : target.getArmorInventoryList() )
		{
			if ( itemStack.getItem().equals(Item.getByNameOrId("toroquest:bandit_helmet") ) || itemStack.getItem().equals(Item.getByNameOrId("toroquest:legendary_bandit_helmet") ) )
			{
				return true;
			}
		}

		CivilizationType civ = this.taskOwner.getCivilization();

		if (civ == null)
		{
			return false;
		}
		
		int rep = PlayerCivilizationCapabilityImpl.get(target).getReputation(civ);

		if ( this.taskOwner.murderWitness() == target ) // this.taskOwner.underAttack == target ||
		{
			return true;
		}
		
		if ( rep > -50 )
		{
			return false;
		}
		
		int attackOdds = ( rep + 300 ) * 2;
		
		if ( attackOdds < 1 )
		{
			return true;
		}

		return rand.nextInt(attackOdds) == 0;
	}
	
	Random rand = new Random();

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	public boolean shouldExecute()
	{
		if (this.taskOwner.getCivilization() == null)
		{
			return false;
		}

//		if (shouldExecuteNonPlayer()) {
//			return true;
//		}

		if (shouldExecutePlayer())
		{
			return true;
		}

		return false;
	}

	protected boolean shouldExecutePlayer()
	{
		//ItemStack itemstack = this.taskOwner.getActiveItemStack();

		double maxXZDistance = getTargetDistance();
		double maxYDistance = getTargetDistance();

		targetEntity = taskOwner.world.getNearestAttackablePlayer(taskOwner.posX, taskOwner.posY + (double) taskOwner.getEyeHeight(), taskOwner.posZ,
				maxXZDistance, maxYDistance, null, targetEntitySelector);

		if ( targetEntity != null && this.taskOwner.getDistance(targetEntity) < (getTargetDistance()) ) // ( ( this.taskOwner.canEntityBeSeen(targetEntity ) || ( itemstack != null && ( itemstack.getItem() instanceof ItemBow ) ) ) ) )
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
//	protected AxisAlignedBB getTargetableArea(double targetDistance)
//	{
//		return this.taskOwner.getEntityBoundingBox().grow(targetDistance, 32, targetDistance);
//	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void startExecuting()
	{
		if ( this.targetEntity != null && !this.targetEntity.isDead )
		{
			this.taskOwner.setAttackTarget(this.targetEntity);
		}
		super.startExecuting();
	}

	
}