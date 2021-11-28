package net.torocraft.toroquest.entities.ai;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.Village;
import net.minecraft.world.World;
import net.torocraft.toroquest.civilization.CivilizationUtil;
import net.torocraft.toroquest.civilization.Province;
import net.torocraft.toroquest.civilization.player.PlayerCivilizationCapabilityImpl;
import net.torocraft.toroquest.config.ToroQuestConfiguration;
import net.torocraft.toroquest.entities.EntityShopkeeper;
import net.torocraft.toroquest.entities.EntityToroVillager;

public class EntityAIToroVillagerMate extends EntityAIBase
{
    private final EntityToroVillager villager;
    private EntityToroVillager mate;
    private final World world;
    private int matingTimeout;

    Village village;

    public EntityAIToroVillagerMate(EntityToroVillager villagerIn)
    {
        this.villager = villagerIn;
        this.world = villagerIn.world;
        this.setMutexBits(1);
    }

    private final int toroVillagerMateChance = ToroQuestConfiguration.toroVillagerMateChance;
    private final double villageDoorsModifier = ToroQuestConfiguration.villageDoorsModifier;
    private final int maxVillagersPerVillage = ToroQuestConfiguration.maxVillagersPerVillage;

    
    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
    	if ( this.villager.getGrowingAge() == 0 && !this.villager.isChild() && this.villager.underAttack == null && this.villager.blockedTrade < 1 && this.villager.isEntityAlive() )
        {

        }
		else
		{
			return false;
		}
        
        if ( this.villager.getRNG().nextInt(this.toroVillagerMateChance) != 0 || this.villager.isUnderAttack() )
        {
            return false;
        }
        
        this.village = this.world.getVillageCollection().getNearestVillage(new BlockPos(this.villager), 0);

        if ( this.village == null )
        {
            return false;
        }
        
		Province prov = CivilizationUtil.getProvinceAt(this.villager.world, this.villager.chunkCoordX, this.villager.chunkCoordZ);

		if ( prov == null || prov.civilization == null )
        {
            return false;
        }
        
        List<EntityPlayer> players = this.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(this.villager.getPosition()).grow(25, 12, 25), new Predicate<EntityPlayer>()
		{
			public boolean apply(@Nullable EntityPlayer entity)
			{
				return true;
			}
		});
        
        int rep = 0;
        
        EntityPlayer p = null;
        
		for ( EntityPlayer player : players )
        {
    		int tempRep = PlayerCivilizationCapabilityImpl.get(player).getReputation(prov.civilization);
    		
    		if ( tempRep > rep)
    		{
    			rep = tempRep;
    			p = player;
    		}
        }
        
		if ( p == null )
		{
			return false;
		}
        
//		System.out.println(this.checkSufficientDoorsPresentForNewVillager(rep));
//		System.out.println(this.villager.getIsWillingToMate(true));
		
        if ( this.checkSufficientDoorsPresentForNewVillager(rep) && this.villager.getIsWillingToMate(true) )
        {
//			System.out.println("!!!");

			List<EntityToroVillager> entities = this.world.getEntitiesWithinAABB(EntityToroVillager.class, new AxisAlignedBB(this.villager.getPosition()).grow(10, 6, 10), new Predicate<EntityToroVillager>()
			{
				public boolean apply(@Nullable EntityToroVillager entity)
				{
					return !(entity instanceof EntityShopkeeper);
//					if ( entity != villager && entity.getGrowingAge() == 0 && !entity.isChild() && entity.underAttack == null && entity.blockedTrade < 1 )
//					{
//						return true;
//					}
//					return false;
				}
			});
			
			Collections.shuffle(entities);

			for ( EntityToroVillager entity : entities )
			{
				if ( entity != villager && entity.getGrowingAge() == 0 && !entity.isChild() && entity.underAttack == null && entity.blockedTrade < 1 && entity.isEntityAlive() )
				{
    				this.mate = entity;
                    this.mate.bedLocation = this.villager.bedLocation;
                    if ( this.mate.getNavigator().tryMoveToXYZ(this.villager.bedLocation.getX(),this.villager.bedLocation.getY(),this.villager.bedLocation.getZ(), 0.5D) 
                    && this.villager.getNavigator().tryMoveToXYZ(this.villager.bedLocation.getX(),this.villager.bedLocation.getY(),this.villager.bedLocation.getZ(), 0.5D) )
                    {
                    	return true;
                    }
                }
			}
        }
    	return false;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.matingTimeout = 256;
        this.villager.setMating(true);
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void resetTask()
    {
        this.village = null;
        this.mate = null;
        this.villager.setMating(false);
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean shouldContinueExecuting()
    {
        return this.matingTimeout >= 0 && this.villager.getGrowingAge() == 0;
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void updateTask()
    {
        if ( this.villager != null && this.villager.bedLocation != null && this.mate != null && this.mate.bedLocation != null )
        {
        	--this.matingTimeout;
            this.villager.getLookHelper().setLookPositionWithEntity(this.mate, 10.0F, 30.0F);
            this.mate.getLookHelper().setLookPositionWithEntity(this.villager, 10.0F, 30.0F);
            
	        if // both on the bed
	        (  (this.villager.getDistance(this.villager.bedLocation.getX(), this.villager.bedLocation.getY(), this.villager.bedLocation.getZ()) < 1.75 || 
	        	this.villager.getNavigator().tryMoveToXYZ(this.villager.bedLocation.getX(),this.villager.bedLocation.getY(),this.villager.bedLocation.getZ(), 0.5D))
	        		
	        	&&(this.mate.getDistance(this.villager.bedLocation.getX(), this.villager.bedLocation.getY(), this.villager.bedLocation.getZ()) < 1.75 || 
	        	   this.mate.getNavigator().tryMoveToXYZ(this.villager.bedLocation.getX(),this.villager.bedLocation.getY(),this.villager.bedLocation.getZ(), 0.5D))
	        )
	        {
	        	if ( this.villager.getDistance(this.mate) < 2.25D )
	        	{
		        	if ( this.matingTimeout == 0 && this.mate.isMating() )
		            {
		                this.villager.getNavigator().tryMoveToEntityLiving(this.mate, 0.25D);
		        		this.villager.playSound(SoundEvents.ENTITY_VILLAGER_YES, 1.25F, 1.0F + this.world.rand.nextFloat()*0.4F);
		                this.world.setEntityState(this.villager, (byte)12);
		                this.giveBirth();
		            }
		        	else if ( this.villager.getRNG().nextInt(25) == 0 )
		            {
		        		if ( !this.world.isRemote )
						{
							Vec3d velocityVector = new Vec3d(this.villager.posX - this.mate.posX, 0, this.villager.posZ - this.mate.posZ);
							double push = (2.25D+this.villager.getDistanceSq(this.mate));
							this.villager.addVelocity((velocityVector.x)/push, 0.0D, (velocityVector.z)/push);
		                	this.villager.velocityChanged = true;
						}		                this.villager.getNavigator().tryMoveToEntityLiving(this.mate, 0.25D);
		    			this.villager.playSound(SoundEvents.ENTITY_VILLAGER_YES, 1.0F, 0.8F + this.world.rand.nextFloat()*0.4F);
		                this.world.setEntityState(this.villager, (byte)12);
		                // sexyTime = true;
		            }
	        	}
	        }
        }
        else
        {
        	this.resetTask();
        }
    }

    private boolean checkSufficientDoorsPresentForNewVillager( double repMod )
    {
//      if (!this.village.isMatingSeason())
//      {
//          return false;
//      }
    	if ( this.village.getNumVillagers() > this.maxVillagersPerVillage )
    	{
    		return false;
    	}
    	else
        {
            return this.village.getNumVillagers() < (int)( this.village.getNumVillageDoors() * this.villageDoorsModifier * ( 1.0D + repMod/800.0D ) );
        }
    }

    private void giveBirth()
    {
        net.minecraft.entity.EntityAgeable EntityToroVillager = this.villager.createChild(this.mate);
        this.villager.setGrowingAge(10000);
        this.mate.setGrowingAge(10000);
        this.villager.setIsWillingToMate(false);
        this.mate.setIsWillingToMate(false);
        final net.minecraftforge.event.entity.living.BabyEntitySpawnEvent event = new net.minecraftforge.event.entity.living.BabyEntitySpawnEvent(this.villager, this.mate, EntityToroVillager);
        if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event) || event.getChild() == null) { return; }
        EntityToroVillager = event.getChild();
        EntityToroVillager.setGrowingAge(-30000);
        EntityToroVillager.setLocationAndAngles(this.villager.posX, this.villager.posY, this.villager.posZ, 0.0F, 0.0F);
        this.world.spawnEntity(EntityToroVillager);
        this.world.setEntityState(EntityToroVillager, (byte)12);
    }
}