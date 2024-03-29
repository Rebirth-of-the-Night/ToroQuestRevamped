package net.torocraft.toroquest.entities;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.toroquest.ToroQuest;
import net.torocraft.toroquest.config.ToroQuestConfiguration;
import net.torocraft.toroquest.entities.render.RenderMonolithEye;
import net.torocraft.toroquest.generation.WorldGenPlacer;

public class EntityMonolithEye extends EntityMob implements IRangedAttackMob, IMob
{
	
	public static String NAME = "monolitheye";
	static
	{
		if (ToroQuestConfiguration.specificEntityNames)
		{
			NAME = ToroQuestEntities.ENTITY_PREFIX + NAME;
		}
	}
	
	@Override
	public boolean getAlwaysRenderNameTag()
    {
        return false;
    }
	
	@Override
    protected float getWaterSlowDown()
    {
        return 0.0F;
    }
	
    public boolean startRiding(Entity entityIn, boolean force)
    {
    	return false;
    }
    
    public boolean isNonBoss()
    {
        return false;
    }
    
	@Override
	protected void updateLeashedState()
    {
	   this.clearLeashed(true, true);
       return;
    }
	
	@Override
	public boolean canBeLeashedTo(EntityPlayer player)
    {
		return false;
    }
	
	public EntityMonolithEye(World worldIn)
	{
		super(worldIn);
		this.isImmuneToFire = true;
		this.setSize(14.0F, 36.0F);
		
		// this.setSize(2.95F, 2.95F);

        this.bossInfo.setColor(BossInfo.Color.WHITE);
		this.setEntityInvulnerable(true);
		this.setNoGravity(true);
		this.experienceValue = 440;
	}
	
	@Override
	@Nullable
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata)
    {
		WorldGenPlacer.clearTrees(this.world, new BlockPos((int)this.posX, (int)this.posY, (int)this.posZ), 56);
		this.setRaidLocation((int)this.posX, (int)this.posY+16, (int)this.posZ);
        return super.onInitialSpawn(difficulty, livingdata);
    }
	
	@Override
    public boolean canBePushed()
    {
        return false;
    }
	
//	static class AIFireballAttack extends EntityAIBase
//    {
//        private final EntityMonolithEye parentEntity;
//        public int attackTimer;
//    	public int attacks = 3;
//
//        public AIFireballAttack(EntityMonolithEye ghast)
//        {
//            this.parentEntity = ghast;
//        }
//
//        /**
//         * Returns whether the EntityAIBase should begin execution.
//         */
//        public boolean shouldExecute()
//        {
//            return this.parentEntity.getAttackTarget() != null;
//        }
//
//        /**
//         * Execute a one shot task or start executing a continuous task
//         */
//        public void startExecuting()
//        {
//            this.attackTimer = 0;
//        }
//
//        /**
//         * Keep ticking a continuous task that has already been started
//         */
//        public void updateTask()
//        {
//            EntityLivingBase entitylivingbase = this.parentEntity.getAttackTarget();
//            
//            if ( entitylivingbase == null )
//            {
//            	return;
//            }
//            
//            this.parentEntity.getLookHelper().setLookPositionWithEntity(entitylivingbase, 30.0F, 30.0F);
//
//            if (entitylivingbase.getDistanceSq(this.parentEntity) < 9216.0D)
//            {
//                World world = this.parentEntity.world;
//                ++this.attackTimer;
//
//                if (this.attackTimer == 5)
//                {
//                    world.playEvent((EntityPlayer)null, 1015, new BlockPos(this.parentEntity), 0);
//                	this.attacks = 3;
//                }
//
//                
//                if (this.attackTimer == 20)
//                {
//                    Vec3d vec3d = this.parentEntity.getLook(1.0F);
//                    double d2 = entitylivingbase.posX - (this.parentEntity.posX + vec3d.x * 4.0D);
//                    double d3 = entitylivingbase.getEntityBoundingBox().minY + (double)(entitylivingbase.height / 4.0F) - (this.parentEntity.posY);
//                    double d4 = entitylivingbase.posZ - (this.parentEntity.posZ + vec3d.z * 4.0D);
//                    world.playEvent((EntityPlayer)null, 1016, new BlockPos(this.parentEntity), 0);
//                    EntityLargeFireball entitylargefireball = new EntityLargeFireball(world, this.parentEntity, d2, d3, d4);
//                    entitylargefireball.explosionPower = 2;
//                    entitylargefireball.posX = this.parentEntity.posX + vec3d.x * 4.0D;
//                    entitylargefireball.posY = this.parentEntity.posY + (double)(this.parentEntity.height / 2.0F) + 0.5D;
//                    entitylargefireball.posZ = this.parentEntity.posZ + vec3d.z * 4.0D;
//                    world.spawnEntity(entitylargefireball);
//                    if ( this.attacks > 0 )
//                    {
//                    	this.attackTimer = 10;
//                    	this.attacks--;
//                    }
//                    else
//                    {
//                    	this.attackTimer = -40;
//                    	this.attacks = 3;
//                    }
//                }
//            }
//            else if (this.attackTimer > 0)
//            {
//                this.attackTimer = 0;
//            	this.attacks = 3;
//            }
//        }
//
//    }
	
	public static void registerRenders()
	{
		RenderingRegistry.registerEntityRenderingHandler(EntityMonolithEye.class, new IRenderFactory<EntityMonolithEye>()
		{
			@Override
			public Render<EntityMonolithEye> createRenderFor(RenderManager manager)
			{
				return new RenderMonolithEye(manager);
			}
		});
	}

	@Override
	protected void collideWithEntity(Entity entityIn)
	{
		super.collideWithEntity(entityIn);
		
		if ( entityIn instanceof EntityLivingBase )
		{
			float damage = 6f;
	
			DamageSource ds = DamageSource.causeIndirectMagicDamage(this, this);
	
			this.spawnExplosionParticle();
			entityIn.attackEntityFrom(ds, damage);
			entityIn.attackEntityFrom(DamageSource.causeMobDamage(this),
			(float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue());
			this.world.setEntityState(this, (byte) 15);
			this.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
			if ( !this.world.isRemote )
			{
				Vec3d velocityVector = new Vec3d(entityIn.posX - this.posX,entityIn.posY -  this.posY,entityIn.posZ -  this.posZ);
				entityIn.addVelocity((velocityVector.x),(velocityVector.y),(velocityVector.z));
				entityIn.velocityChanged = true;
			}
		}
	}

	public static void init(int entityId)
	{
		EntityRegistry.registerModEntity(new ResourceLocation(ToroQuest.MODID, NAME), EntityMonolithEye.class, NAME, entityId, ToroQuest.INSTANCE, 80,
				3, true, 0xff3024, 0xe0d6b9);
	}
	

	@Override
	protected boolean canDespawn()
	{
		return false;
	}

	protected void initEntityAI()
	{
		//this.tasks.addTask(3, new EntityMonolithEye.AIFireballAttack(this));
		//this.targetTasks.addTask(1, new EntityAIFindEntityNearestPlayer(this));
		//this.tasks.addTask(1, new EntityAIStayCentered(this));
		//tasks.addTask(4, new AIAttack(this));
		// this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 64.0F));
		//this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityLivingBase.class, 0, true, false, new MonolithEyeTargetSelector(this)));
	}

	static class MonolithEyeTargetSelector implements Predicate<EntityLivingBase> {
		private final EntityMonolithEye parentEntity;

		public MonolithEyeTargetSelector(EntityMonolithEye guardian) {
			this.parentEntity = guardian;
		}

		public boolean apply(@Nullable EntityLivingBase e)
		{
			return (e instanceof EntityLivingBase  && e.getDistanceSq(this.parentEntity) > 512.0D );
		}
	}

	protected SoundEvent getAmbientSound()
	{
		EntityLivingBase entitylivingbase = this.getAttackTarget();
		if ( entitylivingbase != null )
		{
			this.world.playSound((EntityPlayer)null, entitylivingbase.posX, entitylivingbase.posY, entitylivingbase.posZ, SoundEvents.ENTITY_ENDERMEN_STARE, this.getSoundCategory(), 1.0F, 0.7F + rand.nextFloat()/100.0F);
		}
		return SoundEvents.ENTITY_ENDERMEN_AMBIENT;
	}

	protected SoundEvent getHurtSound()
	{
		EntityLivingBase entitylivingbase = this.getRevengeTarget();
		if ( entitylivingbase != null )
		{
			this.world.playSound((EntityPlayer)null, entitylivingbase.posX, entitylivingbase.posY, entitylivingbase.posZ, SoundEvents.ENTITY_ENDERDRAGON_HURT, this.getSoundCategory(), 1.0F, 0.8F + rand.nextFloat()/100.0F);
		}
		return SoundEvents.ENTITY_ENDERDRAGON_HURT;
	}

	protected SoundEvent getDeathSound()
	{
		EntityLivingBase entitylivingbase = this.getRevengeTarget();
		if ( entitylivingbase != null )
		{
			this.world.playSound((EntityPlayer)null, entitylivingbase.posX, entitylivingbase.posY, entitylivingbase.posZ, SoundEvents.ENTITY_ENDERDRAGON_DEATH, this.getSoundCategory(), 1.0F, 0.8F + rand.nextFloat()/100.0F);
		}
		return SoundEvents.ENTITY_ENDERDRAGON_DEATH;
	}

	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
		this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(200D * ToroQuestConfiguration.bossHealthMultiplier);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5D * ToroQuestConfiguration.bossAttackDamageMultiplier);
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64.0D);
		this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(10.0D);
		this.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).setBaseValue(10.0D);
	}

	public Integer raidX = null;
	public Integer raidY = null;
	public Integer raidZ = null;
	
	@Override
	public void readEntityFromNBT(NBTTagCompound compound)
	{
	    if ( compound.hasKey("raidX") && compound.hasKey("raidY") && compound.hasKey("raidZ") )
	    {
	    	this.setRaidLocation(compound.getInteger("raidX"), compound.getInteger("raidY"), compound.getInteger("raidZ"));
	    }
	    super.readEntityFromNBT(compound);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound)
	{
		if ( this.raidX != null && this.raidZ != null && this.raidY != null )
		{
			compound.setInteger("raidX", this.raidX);
			compound.setInteger("raidY", this.raidY);
			compound.setInteger("raidZ", this.raidZ);
		}
		super.writeEntityToNBT(compound);
	}

	public void setRaidLocation( int x, int y, int z)
	{
		this.raidX = x;
		this.raidY = y;
		this.raidZ = z;
		this.setPosition(this.raidX+0.5, this.raidY+1, this.raidZ+0.5);
		this.writeEntityToNBT(new NBTTagCompound());
	}

	protected int crystalsDestroyed = 0;
	
	public void onLivingUpdate()
	{
		super.onLivingUpdate();
		
		if (this.world.isRemote)
		{
			return;
		}
		
		EntityLivingBase entitylivingbase = this.getAttackTarget();
		if ( entitylivingbase != null )
		{
	        this.getLookHelper().setLookPositionWithEntity(entitylivingbase, 30.0F, 30.0F);
	        this.faceEntity(entitylivingbase, 30.0F, 30.0F);
		}
		
		if ( this.ticksExisted % 25 == 0 )
		{
			
			{
				this.heal(ToroQuestConfiguration.bossHealthMultiplier);
		        this.bossInfo.setPercent(this.getHealth()/this.getMaxHealth());
			}
			
			entitylivingbase = this.getAttackTarget();

			if ( entitylivingbase != null )
			{
				this.spawnAuraParticle();
				this.world.setEntityState(this, (byte)16);
				
				if ( !entitylivingbase.isEntityAlive() )
				{
					this.setAttackTarget(null);
				}
			}
			
			List<EntityPlayer> e = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(getPosition()).grow(88,64,88), new Predicate<EntityPlayer>()
			{
				public boolean apply(@Nullable EntityPlayer entity)
				{
					return true;
				}
			});
			for ( EntityPlayer p : e )
			{
				if ( this.getDistance(p) < 88 )
				{
					this.setAttackTarget(p);
				}
				break;
			}
			
			List<EntityEnderCrystal> crystals = world.getEntitiesWithinAABB(EntityEnderCrystal.class, new AxisAlignedBB(this.getPosition()).grow(96, 64, 96));
			if ( crystals.size() < 1 )
			{
				if ( this.getIsInvulnerable() )
				{
					this.spawnExplosionParticle();
					this.world.setEntityState(this, (byte) 15);
					this.world.playSound((EntityPlayer)null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_ENDERDRAGON_DEATH, this.getSoundCategory(), 1.0F, 1.0F);
					if ( entitylivingbase != null )
					{
						this.world.playSound((EntityPlayer)null, entitylivingbase.posX, entitylivingbase.posY, entitylivingbase.posZ, SoundEvents.ENTITY_ENDERDRAGON_DEATH, this.getSoundCategory(), 1.0F, 1.0F);
			            this.world.playSound((EntityPlayer)null, entitylivingbase.posX, entitylivingbase.posY, entitylivingbase.posZ, SoundEvents.ENTITY_GENERIC_EXPLODE, this.getSoundCategory(), 1.0F, 1.0F);
			            this.world.playSound((EntityPlayer)null, entitylivingbase.posX, entitylivingbase.posY, entitylivingbase.posZ, SoundEvents.ENTITY_ENDEREYE_DEATH, this.getSoundCategory(), 1.0F, 1.0F);
					}
			    	this.world.addWeatherEffect(new EntityLightningBolt(this.world, this.posX + rand.nextGaussian() * 32, this.posY - rand.nextFloat() * 32, this.posZ + rand.nextGaussian() * 16, false));
			    	this.world.addWeatherEffect(new EntityLightningBolt(this.world, this.posX + rand.nextGaussian() * 16, this.posY - rand.nextFloat() * 32, this.posZ + rand.nextGaussian() * 16, false));
			    	this.world.addWeatherEffect(new EntityLightningBolt(this.world, this.posX + rand.nextGaussian() * 32, this.posY - rand.nextFloat() * 32, this.posZ + rand.nextGaussian() * 16, false));
			    	this.world.addWeatherEffect(new EntityLightningBolt(this.world, this.posX + rand.nextGaussian() * 16, this.posY - rand.nextFloat() * 32, this.posZ + rand.nextGaussian() * 16, false));
			    	this.world.addWeatherEffect(new EntityLightningBolt(this.world, this.posX + rand.nextGaussian() * 32, this.posY - rand.nextFloat() * 32, this.posZ + rand.nextGaussian() * 16, false));
			    	this.world.addWeatherEffect(new EntityLightningBolt(this.world, this.posX + rand.nextGaussian() * 16, this.posY - rand.nextFloat() * 32, this.posZ + rand.nextGaussian() * 16, false));
			    	this.world.addWeatherEffect(new EntityLightningBolt(this.world, this.posX + rand.nextGaussian() * 32, this.posY - rand.nextFloat() * 16, this.posZ + rand.nextGaussian() * 16, false));
			    	this.world.addWeatherEffect(new EntityLightningBolt(this.world, this.posX + rand.nextGaussian() * 16, this.posY - rand.nextFloat() * 16, this.posZ + rand.nextGaussian() * 16, false));
			    	this.world.addWeatherEffect(new EntityLightningBolt(this.world, this.posX + rand.nextGaussian() * 32, this.posY - rand.nextFloat() * 16, this.posZ + rand.nextGaussian() * 16, false));
			    	this.world.addWeatherEffect(new EntityLightningBolt(this.world, this.posX + rand.nextGaussian() * 16, this.posY - rand.nextFloat() * 16, this.posZ + rand.nextGaussian() * 16, false));
			    	this.world.addWeatherEffect(new EntityLightningBolt(this.world, this.posX + rand.nextGaussian() * 32, this.posY - rand.nextFloat() * 16, this.posZ + rand.nextGaussian() * 16, false));
			    	this.world.addWeatherEffect(new EntityLightningBolt(this.world, this.posX + rand.nextGaussian() * 16, this.posY - rand.nextFloat() * 16, this.posZ + rand.nextGaussian() * 16, false));
				}
				this.bossInfo.setColor(BossInfo.Color.PURPLE);
				this.setEntityInvulnerable(false);
			}
			else
			{
		        this.bossInfo.setColor(BossInfo.Color.WHITE);
				this.setEntityInvulnerable(true);
			}
			
			int current = this.crystalsDestroyed;
			this.crystalsDestroyed = MathHelper.clamp(12 - crystals.size(), 0, 12);
			if ( current != this.crystalsDestroyed && entitylivingbase != null )
			{
				this.world.playSound((EntityPlayer)null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_ENDERDRAGON_DEATH, this.getSoundCategory(), 1.0F, 1.0F);
				this.world.playSound((EntityPlayer)null, entitylivingbase.posX, entitylivingbase.posY, entitylivingbase.posZ, SoundEvents.ENTITY_ENDERDRAGON_HURT, this.getSoundCategory(), 1.0F, 1.0F + rand.nextFloat()/50.0F);
	            this.world.playSound((EntityPlayer)null, entitylivingbase.posX, entitylivingbase.posY, entitylivingbase.posZ, SoundEvents.ENTITY_ENDEREYE_DEATH, this.getSoundCategory(), 1.0F, 1.0F);
			}
		}
		
		// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
		
		if ( this.raidX != null && this.raidY != null && this.raidZ != null )
		{
			this.setPosition(this.raidX+0.5, this.raidY+1, this.raidZ+0.5);
		}
		
		if ( entitylivingbase == null || !entitylivingbase.isEntityAlive() )
		{
			return;
		}

		double d0 = this.getDistance(entitylivingbase);
	    
        if ( d0 <= 32 )
        {
        	this.collideWithEntity(entitylivingbase);
        }
        
        if ( d0 <= 88)
	    {
	    	 int attackTimer = this.ticksExisted % 132;
	    	 
	    	 if ( attackTimer == 28 )
	         {
		         this.world.playSound((EntityPlayer)null, entitylivingbase.posX, entitylivingbase.posY, entitylivingbase.posZ, SoundEvents.ENTITY_ENDERDRAGON_GROWL, this.getSoundCategory(), 1.0F, 1.2F);
	        	 // this.world.playEvent((EntityPlayer)null, 1015, new BlockPos(this), 0);
	         }
	    	 else if ( ( attackTimer == 30 && this.crystalsDestroyed >= 8 ) || attackTimer == 40 || ( attackTimer == 50 && this.crystalsDestroyed >= 4 ) || ( attackTimer == 60 && this.crystalsDestroyed >= 1 ) || ( attackTimer == 70 && this.crystalsDestroyed >= 12 ) )
	         {
	             Vec3d vec3d = this.getLook(1.0F);
	             double d2 = entitylivingbase.posX - (this.posX + vec3d.x * 4.0D) + rand.nextGaussian() * (this.crystalsDestroyed/8.0D);
	             double d3 = entitylivingbase.getEntityBoundingBox().minY + (double)(entitylivingbase.height) - (this.posY + (double)(this.height / 9.0D)) + rand.nextGaussian() * (this.crystalsDestroyed/12.0D) - 1.0D;
	             double d4 = entitylivingbase.posZ - (this.posZ + vec3d.z * 4.0D) + rand.nextGaussian() * (this.crystalsDestroyed/8.0D);
	             //this.world.playEvent((EntityPlayer)null, 1016, new BlockPos(this), 0);
		         this.world.playSound((EntityPlayer)null, entitylivingbase.posX, entitylivingbase.posY, entitylivingbase.posZ, SoundEvents.ENTITY_ENDERDRAGON_SHOOT, this.getSoundCategory(), 1.0F, 0.9F + rand.nextFloat()/25.0F);
	             EntityLargeFireball entitylargefireball = new EntityLargeFireball(world, this, d2, d3, d4);
	             entitylargefireball.explosionPower = 2;
	             entitylargefireball.posX = this.posX + vec3d.x * 3.7D;
	             entitylargefireball.posY = this.posY + 7;
	             entitylargefireball.posZ = this.posZ + vec3d.z * 3.7D;
	             this.world.spawnEntity(entitylargefireball);
	         }
	    }
	    else
	    {
	    	this.setAttackTarget(null);
	    	return;
	    }
	}

	protected void updateLogic()
	{
		handleAttachLogicUpdate();

		if (this.rand.nextFloat() < 7.5E-4F) {
			this.world.setEntityState(this, (byte) 15);
		}

	}

//	@SideOnly(Side.CLIENT)
	public void spawnAuraParticle( )
	{
		EntityLivingBase player = this.getAttackTarget();
		if ( player != null )
		{
			if (this.world instanceof WorldServer)
			{
				for ( int i = 16; i > 0; i-- )
				{
					((WorldServer) this.world).spawnParticle(EnumParticleTypes.PORTAL, player.posX + this.rand.nextGaussian() * 0.06999999523162842D, player.posY + this.rand.nextGaussian() * 0.20999999523162842D, player.posZ + this.rand.nextGaussian() * 0.06999999523162842D, this.rand.nextGaussian() * 0.05D, this.rand.nextGaussian() * 0.05D, this.rand.nextGaussian() * 0.05D, new int[0]);
				}
			}
		}
		// else
//		{
//			this.world.spawnParticle(EnumParticleTypes.PORTAL, this.posX + this.rand.nextGaussian() * 16, this.posY + this.rand.nextGaussian() * 16, this.posZ + this.rand.nextGaussian() * 16, this.rand.nextGaussian() * 0.05D, this.rand.nextGaussian() * 0.05D, this.rand.nextGaussian() * 0.05D, new int[0]);
//			this.world.spawnParticle(EnumParticleTypes.PORTAL, this.posX + this.rand.nextGaussian() * 16, this.posY + this.rand.nextGaussian() * 16, this.posZ + this.rand.nextGaussian() * 16, this.rand.nextGaussian() * 0.05D, this.rand.nextGaussian() * 0.05D, this.rand.nextGaussian() * 0.05D, new int[0]);
//			this.world.spawnParticle(EnumParticleTypes.PORTAL, this.posX + this.rand.nextGaussian() * 16, this.posY + this.rand.nextGaussian() * 16, this.posZ + this.rand.nextGaussian() * 16, this.rand.nextGaussian() * 0.05D, this.rand.nextGaussian() * 0.05D, this.rand.nextGaussian() * 0.05D, new int[0]);
//			this.world.spawnParticle(EnumParticleTypes.PORTAL, this.posX + this.rand.nextGaussian() * 16, this.posY + this.rand.nextGaussian() * 16, this.posZ + this.rand.nextGaussian() * 16, this.rand.nextGaussian() * 0.05D, this.rand.nextGaussian() * 0.05D, this.rand.nextGaussian() * 0.05D, new int[0]);
//			this.world.spawnParticle(EnumParticleTypes.PORTAL, this.posX + this.rand.nextGaussian() * 16, this.posY + this.rand.nextGaussian() * 16, this.posZ + this.rand.nextGaussian() * 16, this.rand.nextGaussian() * 0.05D, this.rand.nextGaussian() * 0.05D, this.rand.nextGaussian() * 0.05D, new int[0]);
//			this.world.spawnParticle(EnumParticleTypes.PORTAL, this.posX + this.rand.nextGaussian() * 16, this.posY + this.rand.nextGaussian() * 16, this.posZ + this.rand.nextGaussian() * 16, this.rand.nextGaussian() * 0.05D, this.rand.nextGaussian() * 0.05D, this.rand.nextGaussian() * 0.05D, new int[0]);
//			this.world.spawnParticle(EnumParticleTypes.PORTAL, this.posX + this.rand.nextGaussian() * 16, this.posY + this.rand.nextGaussian() * 16, this.posZ + this.rand.nextGaussian() * 16, this.rand.nextGaussian() * 0.05D, this.rand.nextGaussian() * 0.05D, this.rand.nextGaussian() * 0.05D, new int[0]);
//			this.world.spawnParticle(EnumParticleTypes.PORTAL, this.posX + this.rand.nextGaussian() * 16, this.posY + this.rand.nextGaussian() * 16, this.posZ + this.rand.nextGaussian() * 16, this.rand.nextGaussian() * 0.05D, this.rand.nextGaussian() * 0.05D, this.rand.nextGaussian() * 0.05D, new int[0]);
//		}
	}

	public void spawnAttackParticles()
	{
		if (this.world instanceof WorldServer)
		{
			for ( int i = 16; i > 0; i-- )
			{
				((WorldServer) this.world).spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, this.posX + this.rand.nextGaussian() * 2.05D, this.posY + this.rand.nextGaussian() * 2.05D, this.posZ + this.rand.nextGaussian() * 2.05D, this.rand.nextGaussian() * 2.05D, this.rand.nextGaussian() * 2.05D, this.rand.nextGaussian() * 2.05D, new int[0]);
			}
		}
	}

	protected void handleAttachLogicUpdate()
	{

	}

	@Nullable
	protected ResourceLocation getLootTable() {
		return LootTableList.ENTITIES_GUARDIAN;
	}


	@Override
	public void setSwingingArms(boolean swingingArms)
	{

	}

//	private void spawnParticles(double xSpeed, double ySpeed, double zSpeed)
//	{
//		if (this.world.isRemote)
//		{
//			for (int i = 0; i < 32; ++i) 
//			{
//				world.spawnParticle(EnumParticleTypes.PORTAL, posX, posY, posZ, xSpeed, ySpeed, zSpeed, new int[0]);
//			}
//		}
//		else
//		{
//			this.world.setEntityState(this, (byte) 42);
//		}
//
//	}

	public float getEyeHeight()
	{
		return 16.0F;
	}
	
	// INCREASE RENDER DISTNACE
	@SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        return this.getEntityBoundingBox().grow(88.0);
    }

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		if (this.world.isRemote)
		{
			return false;
		}
		
		if (this.getIsInvulnerable())
        {
			this.playSound(SoundEvents.ENTITY_ENDERMEN_AMBIENT, 0.8F, 0.7F + rand.nextFloat()/25.0F);
			this.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 0.8F, 0.7F + rand.nextFloat()/25.0F);
            return false;
        }
		
        this.bossInfo.setPercent(this.getHealth()/this.getMaxHealth());
		
		if ( this.isEntityInvulnerable(source) || source == DamageSource.FALL || source == null || source.getTrueSource() == null || !(this.isEntityAlive()) )
		{
			return false;
		}
		
		if ( source.getTrueSource() instanceof EntityLivingBase )
		{
			if ( super.attackEntityFrom(source, amount) )
			{
		        this.world.playSound((EntityPlayer)null, source.getTrueSource().posX, source.getTrueSource().posY, source.getTrueSource().posZ, SoundEvents.ENTITY_ENDERDRAGON_GROWL, this.getSoundCategory(), 1.0F, 1.4F);
		        this.world.playSound((EntityPlayer)null, source.getTrueSource().posX, source.getTrueSource().posY, source.getTrueSource().posZ, SoundEvents.ENTITY_ENDERDRAGON_HURT, this.getSoundCategory(), 1.0F, 1.2F);
		        return true;
			}
		}
		return false;
	}
//	@Override
//	public boolean attackEntityFrom(DamageSource source, float amount)
//	{
//		if (!net.minecraftforge.common.ForgeHooks.onLivingAttack(this, source, amount)) return false;
//        if (this.getIsInvulnerable())
//        {
//            return false;
//        }
//        else if (this.world.isRemote)
//        {
//            return false;
//        }
//        else
//        {
//            this.idleTime = 0;
//
//            if (this.getHealth() <= 0.0F)
//            {
//                return false;
//            }
//            else
//            {
//                float f = amount;
//                boolean flag = false;
//                boolean flag1 = true;
//
//                if ((float)this.hurtResistantTime > (float)this.maxHurtResistantTime / 2.0F)
//                {
//                    if (amount <= this.lastDamage)
//                    {
//                        return false;
//                    }
//
//                    this.damageEntity(source, amount - this.lastDamage);
//                    this.lastDamage = amount;
//                    flag1 = false;
//                }
//                else
//                {
//                    this.lastDamage = amount;
//                    this.hurtResistantTime = this.maxHurtResistantTime;
//                    this.damageEntity(source, amount);
//                    this.maxHurtTime = 10;
//                    this.hurtTime = this.maxHurtTime;
//                }
//
//                this.attackedAtYaw = 0.0F;
//                Entity entity1 = source.getTrueSource();
//
//                if (entity1 != null)
//                {
//                    if (entity1 instanceof EntityLivingBase)
//                    {
//                        this.setRevengeTarget((EntityLivingBase)entity1);
//                    }
//
//                    if (entity1 instanceof EntityPlayer)
//                    {
//                        this.recentlyHit = 100;
//                        this.attackingPlayer = (EntityPlayer)entity1;
//                    }
//                    else
//                    {
//                        return false;
//                    }
//                }
//
//                if (flag1)
//                {
//                    if (flag)
//                    {
//                        this.world.setEntityState(this, (byte)29);
//                    }
//                    else if (source instanceof EntityDamageSource && ((EntityDamageSource)source).getIsThornsDamage())
//                    {
//                        this.world.setEntityState(this, (byte)33);
//                    }
//                    
//                    if (entity1 != null)
//                    {
////                        double d1 = entity1.posX - this.posX;
////                        double d0;
////
////                        for (d0 = entity1.posZ - this.posZ; d1 * d1 + d0 * d0 < 1.0E-4D; d0 = (Math.random() - Math.random()) * 0.01D)
////                        {
////                            d1 = (Math.random() - Math.random()) * 0.01D;
////                        }
////
////                        this.attackedAtYaw = (float)(MathHelper.atan2(d0, d1) * (180D / Math.PI) - (double)this.rotationYaw);
////                        this.knockBack(entity1, 0.4F, d1, d0);
//                    }
//                    else
//                    {
//                        this.attackedAtYaw = (float)((int)(Math.random() * 2.0D) * 180);
//                    }
//                }
//
//                if (flag1)
//                {
//                    this.playHurtSound(source);
//                }
//
//                boolean flag2 = !flag || amount > 0.0F;
//                
//                if (entity1 instanceof EntityPlayerMP)
//                {
//                    CriteriaTriggers.PLAYER_HURT_ENTITY.trigger((EntityPlayerMP)entity1, this, source, f, amount, flag);
//                }
//                return flag2;
//            }
//        }
//	}
	
	protected void damageEntity(DamageSource damageSrc, float damageAmount)
    {
        if (!this.isEntityInvulnerable(damageSrc))
        {
            damageAmount = net.minecraftforge.common.ForgeHooks.onLivingHurt(this, damageSrc, damageAmount);
            if (damageAmount <= 0) return;
            damageAmount = this.applyArmorCalculations(damageSrc, damageAmount);
            damageAmount = this.applyPotionDamageCalculations(damageSrc, damageAmount);
            float f = damageAmount;
            damageAmount = Math.max(damageAmount - this.getAbsorptionAmount(), 0.0F);
            this.setAbsorptionAmount(this.getAbsorptionAmount() - (f - damageAmount));
            damageAmount = net.minecraftforge.common.ForgeHooks.onLivingDamage(this, damageSrc, damageAmount);

            if (damageAmount != 0.0F)
            {
                float f1 = this.getHealth();
                this.getCombatTracker().trackDamage(damageSrc, f1, damageAmount);
                this.setHealth(f1 - damageAmount); // Forge: moved to fix MC-121048
                this.setAbsorptionAmount(this.getAbsorptionAmount() - damageAmount);
            }
        }
    	if ( this.getHealth() <= 0 )
        {
        	this.dropBossLoot();
        }
    }

	protected void redirectAttack(DamageSource source, float amount)
	{
		Entity attacker = source.getTrueSource();
		if (attacker != null) {
			attacker.attackEntityFrom(source, amount);
		}
	}

//	protected void redirectArrowAtAttacker(DamageSource source) {
//		if ("arrow".equals(source.getDamageType())) {
//
//			if (source.getTrueSource() != null && source.getTrueSource() instanceof EntityLivingBase) {
//				attackWithArrow((EntityLivingBase) source.getTrueSource());
//			}
//
//			if (source.getImmediateSource() != null) {
//				source.getImmediateSource().setDead();
//			}
//
//		}
//	}

//	protected void attackWithArrow(EntityLivingBase target) {
//
//		int charge = 2 + rand.nextInt(10);
//
//		EntityArrow entityarrow = new EntityTippedArrow(this.world, this);
//		double d0 = target.posX - this.posX;
//		double d1 = target.getEntityBoundingBox().minY + (double) (target.height / 3.0F) - entityarrow.posY;
//		double d2 = target.posZ - this.posZ;
//		double d3 = (double) MathHelper.sqrt(d0 * d0 + d2 * d2);
//		entityarrow.shoot(d0, d1 + d3 * 0.20000000298023224D, d2, 1.6F,
//				(float) (14 - this.world.getDifficulty().getDifficultyId() * 4));
//		int i = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.POWER, this);
//		int j = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.PUNCH, this);
//		entityarrow.setDamage((double) (charge * 2.0F) + this.rand.nextGaussian() * 0.25D
//				+ (double) ((float) this.world.getDifficulty().getDifficultyId() * 0.11F));
//
//		if (i > 0) {
//			entityarrow.setDamage(entityarrow.getDamage() + (double) i * 0.5D + 0.5D);
//		}
//
//		if (j > 0) {
//			entityarrow.setKnockbackStrength(j);
//		}
//
//		if (rand.nextBoolean()) {
//			entityarrow.setFire(100);
//		}
//
//		this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
//		this.world.spawnEntity(entityarrow);
//	}

	@Override
	public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
		// TODO Auto-generated method stub
		
	}
	
	private final BossInfoServer bossInfo = (BossInfoServer)(new BossInfoServer(this.getDisplayName(), BossInfo.Color.PURPLE, BossInfo.Overlay.PROGRESS)).setDarkenSky(true);

	/**
     * Add the given player to the list of players tracking this entity. For instance, a player may track a boss in
     * order to view its associated boss bar.
     */
    public void addTrackingPlayer(EntityPlayerMP player)
    {
        super.addTrackingPlayer(player);
        this.bossInfo.addPlayer(player);
    }

    /**
     * Removes the given player from the list of players tracking this entity. See {@link Entity#addTrackingPlayer} for
     * more information on tracking.
     */
    public void removeTrackingPlayer(EntityPlayerMP player)
    {
        super.removeTrackingPlayer(player);
        this.bossInfo.removePlayer(player);
    }
    
    private void dropBossLoot()
	{
    	if (!this.world.isRemote)
		{
			// quest
			int x = this.getPosition().getX();
			int y = this.getPosition().getY();
			int z = this.getPosition().getZ();
			int range = 128;
			for ( int xx = -range/2; xx < range; xx++ )
			{
				for ( int yy = -range/4; yy < range/2; yy++ )
				{
					for ( int zz = -range/2; zz < range; zz++ )
					{
						BlockPos pos = new BlockPos(x+xx-16,y+yy-4,z+zz-16);
						world.extinguishFire(null, pos, EnumFacing.UP);
						IBlockState block = world.getBlockState(pos);
						if ( block != null && block == Blocks.OBSIDIAN )
						{
							world.setBlockToAir(pos);
						}
					}
				}
			}

	    	dropTrophy();
	    	
			this.dropLootItem(Item.getByNameOrId("minecraft:obsidian"), rand.nextInt(3)+1);
			this.dropLootItem(Item.getByNameOrId("minecraft:obsidian"), rand.nextInt(3)+1);
			this.dropLootItem(Item.getByNameOrId("minecraft:obsidian"), rand.nextInt(3)+1);
			this.dropLootItem(Item.getByNameOrId("minecraft:obsidian"), rand.nextInt(3)+1);
			this.dropLootItem(Item.getByNameOrId("minecraft:obsidian"), rand.nextInt(3)+1);
			this.dropLootItem(Item.getByNameOrId("minecraft:obsidian"), rand.nextInt(3)+1);
		}
	}
    
    private void dropTrophy()
	{
		ItemStack stack = new ItemStack(Item.getByNameOrId("toroquest:trophy_beholder"));
		EntityItem dropItem = new EntityItem(world, posX, posY+1, posZ, stack.copy());
		dropItem.setNoPickupDelay();
		dropItem.motionY = 0.5;
		dropItem.motionZ = 0.0;
		dropItem.motionX = 0.0;
		this.world.spawnEntity(dropItem);
	}

	private void dropLootItem(Item item, int amount)
	{
		if (amount == 0)
		{
			return;
		}

		for (int i = 0; i < amount; i++)
		{
			ItemStack stack = new ItemStack(item);
			EntityItem dropItem = new EntityItem(world, posX, posY, posZ, stack.copy());
			dropItem.setNoPickupDelay();
			dropItem.motionY = rand.nextDouble();
			dropItem.motionZ = rand.nextDouble() - 0.5d;
			dropItem.motionX = rand.nextDouble() - 0.5d;
			this.world.spawnEntity(dropItem);
			dropItem.setGlowing(true);
		}

	}
	
}
