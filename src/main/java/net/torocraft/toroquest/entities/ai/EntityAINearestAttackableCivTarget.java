package net.torocraft.toroquest.entities.ai;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.torocraft.toroquest.civilization.CivilizationType;
import net.torocraft.toroquest.civilization.CivilizationUtil;
import net.torocraft.toroquest.civilization.Province;
import net.torocraft.toroquest.civilization.player.PlayerCivilizationCapabilityImpl;
import net.torocraft.toroquest.config.ToroQuestConfiguration;
import net.torocraft.toroquest.entities.EntityGuard;
import net.torocraft.toroquest.entities.EntityToroNpc;

public class EntityAINearestAttackableCivTarget extends EntityAITarget {

	protected final EntityAINearestAttackableTarget.Sorter theNearestAttackableTargetSorter;
	protected final Predicate<EntityPlayer> targetEntitySelector;
	protected EntityPlayer targetEntity;

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
				if ( !isSuitableTarget(taskOwner, target, false, true) )
				{
					return false;
				}
				
				return shouldAttackPlayerBasedOnCivilization(target);
			}
		};
	}

	protected boolean shouldAttackPlayerBasedOnCivilization(EntityPlayer player)
	{
		if ( !this.taskOwner.canEntityBeSeen( player ) || player.isInvisible() )
		{
			return false;
		}
		
		if ( !this.taskOwner.playerGuard.equals("") )
		{
			if ( player.getName().equals(this.taskOwner.playerGuard) )
			{
				if ( player.getRevengeTarget() != null )
				{
					if ( player.getRevengeTarget() instanceof EntityPlayer )
					{
						this.taskOwner.setAnnoyed((EntityPlayer)player.getRevengeTarget());
					}
					this.taskOwner.setAttackTarget(player.getRevengeTarget());
				}
				return false;
			}
			else if ( player.getRevengeTarget() instanceof EntityPlayer && player.getRevengeTarget().getName().equals(this.taskOwner.playerGuard) )
			{
				if ( this.taskOwner.actionReady() )
				{
					this.taskOwner.insult(player);
				}
				//this.taskOwner.setAnnoyed(player);
				return true;
			}
		}
		else for ( ItemStack itemStack : player.getArmorInventoryList() )
		{
			if ( itemStack.getItem().equals(Item.getByNameOrId("toroquest:bandit_helmet") ) || itemStack.getItem().equals(Item.getByNameOrId("toroquest:legendary_bandit_helmet") ) )
			{
				if ( this.taskOwner.actionReady() )
				{
					this.taskOwner.chat(player, "bandit", null);
				}
				//this.taskOwner.setAnnoyed(player);
				return true;
			}
			else if ( itemStack.getItem().equals(Item.getByNameOrId("toroquest:royal_helmet") ) )
			{
				Province prov = CivilizationUtil.getProvinceAt(this.taskOwner.world, this.taskOwner.chunkCoordX, this.taskOwner.chunkCoordZ);
				
				if ( prov != null )
				{
					if ( prov.hasLord )
					{
						this.taskOwner.chat(player, "falselord", prov.name);
						//this.taskOwner.setAnnoyed(player);
						return true;
					}
					else
					{
						if ( this.taskOwner.actionReady() )
						{
							this.taskOwner.chat(player, "lord", prov.name);
						}
						return false;
					}
				}
			}
		}

		CivilizationType civ = this.taskOwner.getCivilization();

		if ( civ == null )
		{
			return false;
		}
		
		int rep = PlayerCivilizationCapabilityImpl.get(player).getReputation(civ);

		if ( this.taskOwner.murderWitness() == player || this.taskOwner.underAttack() == player )
		{
			if ( this.taskOwner.actionReady() )
			{
				this.taskOwner.insult(player);
			}
			//this.taskOwner.setAnnoyed(player);
			return true;
		}
		
		if ( rep > -50 )
		{
			return false;
		}
		
		rep = -(1000/rep);
		
		if ( rep < 1 )
		{
			if ( this.taskOwner.actionReady() )
			{
				this.taskOwner.insult(player);
			}
			//this.taskOwner.setAnnoyed(player);
			return true;
		}

		if ( this.taskOwner.world.rand.nextInt(rep) == 0 )
		{
			if ( this.taskOwner.actionReady() )
			{
				this.taskOwner.insult(player);
			}
			//this.taskOwner.setAnnoyed(player);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	public boolean shouldExecute()
	{
		if ( this.taskOwner.world.rand.nextInt(30) != 0 || this.taskOwner.getAttackTarget() != null )
        {
			return false;
	    }
		
//		if (this.taskOwner.getCivilization() == null)
//		{
//			return false;
//		}

//		if (shouldExecuteNonPlayer()) {
//			return true;
//		}

		return this.shouldExecutePlayer();
	}
	
	protected boolean shouldExecutePlayer()
	{
		this.targetEntity = this.taskOwner.world.getNearestAttackablePlayer(this.taskOwner.posX, this.taskOwner.posY, this.taskOwner.posZ, 20, 10, null, this.targetEntitySelector);

		if ( this.targetEntity != null )
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
		if ( this.targetEntity != null && this.targetEntity.isEntityAlive() )
		{
			this.taskOwner.setAnnoyed(this.targetEntity);
			this.taskOwner.setAttackTarget(this.targetEntity);
		}
		super.startExecuting();
	}

	
}