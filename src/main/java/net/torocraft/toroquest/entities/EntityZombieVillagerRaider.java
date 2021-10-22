package net.torocraft.toroquest.entities;

import java.util.Random;

import com.google.common.base.Predicate;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIZombieAttack;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.torocraft.toroquest.ToroQuest;
import net.torocraft.toroquest.config.ToroQuestConfiguration;
import net.torocraft.toroquest.entities.ai.EntityAIRaid;
import net.torocraft.toroquest.entities.ai.EntityAIZombieLeap;

public class EntityZombieVillagerRaider extends EntityZombieVillager implements IMob
{

	public EntityZombieVillagerRaider(World worldIn )
	{
		super(worldIn);
		this.isImmuneToFire();
	}
	
	public EntityZombieVillagerRaider(World worldIn, int x, int z)
	{
		super(worldIn);
		this.setRaidLocation(x,z);
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
				true, 0x000000, 0x000000);
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
	
	protected boolean despawn = false;
	public Integer raidX = null;
	public Integer raidZ = null;
	protected Random rand = new Random();
	protected final EntityAIRaid areaAI = new EntityAIRaid(this, 0.7D, 16, 48);
    public int despawnTimer = 100;

	@Override
	public void readEntityFromNBT(NBTTagCompound compound)
	{
	    if ( compound.hasKey("raidX") && compound.hasKey("raidZ") )
	    {
	    	this.raidX = compound.getInteger("raidX");
	    	this.raidZ = compound.getInteger("raidZ");
	    	this.setRaidLocation(this.raidX, this.raidZ);
	    }
	    if ( compound.hasKey("despawnTimer") )
	    {
	    	this.despawnTimer = compound.getInteger("despawnTimer");
	    }
	    this.setBreakDoorsAItask(true);
	    super.readEntityFromNBT(compound);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound)
	{	
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
		compound.setInteger("despawnTimer", this.despawnTimer);
		super.writeEntityToNBT(compound);
	}

	/* Set the direction for bandits to move to */
	public void setRaidLocation(Integer x, Integer z)
	{
		this.tasks.removeTask(this.areaAI);
		if ( x != null && z != null )
		{
			if ( x == 0 && z == 0 )
			{
				this.despawn = true;
				return;
			}
			this.raidX = x;
			this.raidZ = z;
			this.areaAI.setCenter(x, z);
			this.tasks.addTask(7, this.areaAI);
			this.writeEntityToNBT(new NBTTagCompound());
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
	
	@Override
	public void onLivingUpdate()
    {
		super.onLivingUpdate();
		
		if ( world.isRemote )
		{
			return;
		}
		
		if ( this.ticksExisted % 100 == 0 )
    	{
    		if ( --this.despawnTimer < 0 )
    		{
    			this.setHealth( 0 );
				this.despawn = true;

				if ( world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(this.getPosition()).grow(25, 15, 25)).isEmpty() )
				{
	    			this.setDead();
	    			return;
				}
    		}
    		
			if ( this.rand.nextBoolean() )
			{
				vector3d = null;
			}
    		
    		EntityLivingBase attacker = this.getAttackTarget();
    		
            if ( attacker == null )
            {
            	return;
            }
            
            double dist = this.getDistanceSq(attacker);
            
            if ( dist > 64 ) return;
    		
    		if ( dist >= 4 && Math.pow(this.posY - attacker.posY,2) > Math.abs((this.posX - attacker.posX)*(this.posZ - attacker.posZ)) )
    		{
    			if ( vector3d == null )
    			{
    				vector3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this, 24, 12, attacker.getPositionVector());
    				this.setAttackTarget( null );
    			}
    		}
    	}
		
	    if ( vector3d != null )
	    {
			this.setAttackTarget( null );
            double rPosX = vector3d.x;
            double rPosY = vector3d.y;
		    double rPosZ = vector3d.z;
	        this.getNavigator().tryMoveToXYZ(rPosX, rPosY, rPosZ, 0.8D);
	    }
		
    }
	
	private Vec3d vector3d = null;
	
	@Override
	protected boolean canDespawn()
	{
		return this.despawn;
	}
	
	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand)
    {
		return false;
    }
	
//	@Override
//	protected void applyEntityAttributes()
//    {
//        super.applyEntityAttributes();
//        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(35.0D);
//        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D);
//        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.22D+rand.nextDouble()/50.0D);
//        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(2.0D);
//    }
	
	@Override
	protected void initEntityAI()
    {
		this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIZombieLeap(this, 0.38F, false));
        this.tasks.addTask(3, new EntityAIZombieAttack(this, 1.0D, false));
        this.tasks.addTask(4, new EntityAIMoveTowardsRestriction(this, 1.0D));
        this.tasks.addTask(5, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(6, new EntityAILookIdle(this));
	    this.tasks.addTask(7, new EntityAIMoveThroughVillage(this, 0.8D, false));
        this.tasks.addTask(8, new EntityAIWanderAvoidWater(this, 0.8D));
        this.tasks.addTask(9, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<EntityVillager>(this, EntityVillager.class, 20, false, false, new Predicate<EntityVillager>()
		{
			@Override
			public boolean apply(EntityVillager target)
			{
				return true;
			}
		}));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<EntityToroNpc>(this, EntityToroNpc.class, 20, true, false, new Predicate<EntityToroNpc>()
		{
			@Override
			public boolean apply(EntityToroNpc target)
			{
				return true;
			}
		}));
        this.targetTasks.addTask(4, new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, 20, true, false, new Predicate<EntityPlayer>()
		{
			@Override
			public boolean apply(EntityPlayer target)
			{
				return true;
			}
		}));
    }
	
	@Override
	protected void applyEntityAI()
	{
		
	}
	
	
}