package net.torocraft.toroquest.entities;

import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITargetNonTamed;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
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

public class EntityWolfRaider extends EntityWolf implements IMob
{
	protected boolean despawn = false;
	public Integer raidX = null;
	public Integer raidZ = null;
	protected Random rand = new Random();
	protected final EntityAIRaid areaAI = new EntityAIRaid(this, 1.2D, 16, 32);
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

@Override
protected void applyEntityAttributes()
{
	super.applyEntityAttributes();
    this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(35.0D);
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

	public EntityWolfRaider(World worldIn)
	{
		super(worldIn);
		this.setAngry(true);
		this.setTamed(false);
		//this.tasks.removeTask(this.areaAI);
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

		if ( !this.isAngry() )
		{
			this.setAngry(true);
		}
		
		if ( this.ticksExisted % 100 == 0 )
    	{
    		if ( --this.despawnTimer < 0 )
    		{
				if ( world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(this.getPosition()).grow(32, 16, 32)).isEmpty() )
				{
					this.despawn = true;
	    			this.setHealth( 0 );
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

	public static String NAME = "wolf_raider";
	static
	{
		if (ToroQuestConfiguration.specificEntityNames)
		{
			NAME = ToroQuestEntities.ENTITY_PREFIX + NAME;
		}
	}
	public static void init(int entityId)
	{
		EntityRegistry.registerModEntity(new ResourceLocation(ToroQuest.MODID, NAME), EntityWolfRaider.class, NAME, entityId, ToroQuest.INSTANCE, 80, 3,
				true, 0x203090, 0xe09939);
	}
	
	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand)
    {
		return false;
    }
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void initEntityAI()
    {
		this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(4, new EntityAILeapAtTarget(this, 0.45F));
        this.tasks.addTask(5, new EntityAIAttackMelee(this, 1.2D, true));
	    this.tasks.addTask(6, new EntityAIMoveThroughVillage(this, 0.8D, false));
        this.tasks.addTask(8, new EntityAIWanderAvoidWater(this, 0.8D));
        this.tasks.addTask(10, new EntityAILookIdle(this));
        this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, true, new Class[0]));
        this.targetTasks.addTask(4, new EntityAITargetNonTamed(this, EntityAnimal.class, true, new Predicate<EntityAnimal>()
        {
            public boolean apply(@Nullable EntityAnimal p_apply_1_)
            {
                return ( despawnTimer < 80 && !( p_apply_1_ instanceof EntityWolf ) );
            }
        }));
		this.targetTasks.addTask(5, new EntityAINearestAttackableTarget<EntityVillager>(this, EntityVillager.class, 20, false, false, new Predicate<EntityVillager>()
		{
			@Override
			public boolean apply(EntityVillager target)
			{
				return true;
			}
		}));
        this.targetTasks.addTask(6, new EntityAINearestAttackableTarget<EntityToroNpc>(this, EntityToroNpc.class, 20, true, false, new Predicate<EntityToroNpc>()
		{
			@Override
			public boolean apply(EntityToroNpc target)
			{
				return true;
			}
		}));
        this.targetTasks.addTask(7, new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, 20, false, false, new Predicate<EntityPlayer>()
		{
			@Override
			public boolean apply(EntityPlayer target)
			{
				return true;
			}
		}));
    }
}