package net.torocraft.toroquest.entities;

import java.util.Random;

import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIZombieAttack;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.Village;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.torocraft.toroquest.ToroQuest;
import net.torocraft.toroquest.config.ToroQuestConfiguration;
import net.torocraft.toroquest.entities.ai.EntityAIRaid;

public class EntityZombieVillagerRaider extends EntityZombieVillager implements IMob
{
	public boolean despawn = true;
	public Integer raidX = null;
	public Integer raidZ = null;
	protected Random rand = new Random();
	protected final EntityAIRaid areaAI = new EntityAIRaid(this, 0.7D, 48);
	
	@Override
	public void readEntityFromNBT(NBTTagCompound compound)
	{
	    super.readEntityFromNBT(compound);
	    if ( compound.hasKey("raidX") && compound.hasKey("raidZ") )
	    {
	    	this.raidX = compound.getInteger("raidX");
	    	this.raidZ = compound.getInteger("raidZ");
	    	this.setRaidLocation(compound.getInteger("raidX"), compound.getInteger("raidZ"));
	    }
	    this.setBreakDoorsAItask(true);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound)
	{
		super.writeEntityToNBT(compound);
		if ( this.raidX != null && this.raidZ != null )
		{
			compound.setInteger("raidX", this.raidX);
			compound.setInteger("raidZ", this.raidZ);
			this.despawn = false;
		}
		else
		{
			this.despawn = true;
		}
	}

	/* Set the direction for bandits to move to */
	public void setRaidLocation(Integer x, Integer z)
	{
		this.tasks.removeTask(this.areaAI);
		if ( x != null && z != null )
		{
			this.raidX = x;
			this.raidZ = z;
			this.areaAI.setCenter(x, z);
			this.tasks.addTask(7, this.areaAI);
			NBTTagCompound nbt = new NBTTagCompound();
			this.writeEntityToNBT(nbt);
			this.despawn = false;
		}
		else
		{
			this.despawn = true;
		}
	}
	
	@Override
	public boolean hasHome()
	{
		return false;
	}

	public EntityZombieVillagerRaider(World worldIn)
	{
		super(worldIn);
	}
	
	@Override
	public void onLivingUpdate()
    {
		super.onLivingUpdate();
		if ( this.world.isRemote )
		{
			return;
		}
		if ( this.ticksExisted % 100 == 0 )
		{
			if ( !this.despawn && this.world.isDaytime() )
			{
				this.despawn = true;
			}
			if ( rand.nextInt(8) == 0 )
			{
				Village village = this.world.getVillageCollection().getNearestVillage(this.getPosition(), 64);
				if ( village != null )
				{
					this.setRaidLocation(village.getCenter().getX(), village.getCenter().getZ());
				}
			}
		}
    }
		
	@Override
	protected boolean canDespawn()
	{
		return this.despawn;
	}

	public static String NAME = "zombie_villager_raider";
	static
	{
		if (ToroQuestConfiguration.specificEntityNames)
		{
			NAME = ToroQuestEntities.ENTITY_PREFIX + NAME;
		}
	}
	public static void init(int entityId)
	{
		EntityRegistry.registerModEntity(new ResourceLocation(ToroQuest.MODID, NAME), EntityZombieVillagerRaider.class, NAME, entityId, ToroQuest.INSTANCE, 80, 3,
				true, 0x203090, 0xe09939);
	}
	
	@Override
	protected void initEntityAI()
    {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIZombieAttack(this, 1.0D, false));
        this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
        this.tasks.addTask(8, new EntityAIWanderAvoidWater(this, 1.0D));
        this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(10, new EntityAILookIdle(this));
        this.applyEntityAI();
    }
	
	public void setImmuneFire()
	{
		this.isImmuneToFire = true;
		this.despawn = true;
	}
	
	@Override
	protected boolean shouldBurnInDay()
    {
        return !this.isImmuneToFire;
    }
	
	@Override
	protected void applyEntityAI()
	{
	    this.tasks.addTask(6, new EntityAIMoveThroughVillage(this, 1.0D, false));
	    this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[] {EntityPigZombie.class}));
	    this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
	    this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityVillager.class, false));
	    this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityIronGolem.class, true));
	    this.targetTasks.addTask(4, new EntityAINearestAttackableTarget(this, EntityToroNpc.class, true));
	}
}