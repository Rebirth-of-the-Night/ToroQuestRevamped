package net.torocraft.toroquest.entities;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIMoveTowardsTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.toroquest.SoundHandler;
import net.torocraft.toroquest.ToroQuest;
import net.torocraft.toroquest.config.ToroQuestConfiguration;
import net.torocraft.toroquest.entities.ai.AIHelper;
import net.torocraft.toroquest.entities.render.RenderConstruct;

public class EntityConstruct extends EntityGolem implements IMob
{
	// EntityIronGolem
    /** deincrements, and a distance-to-home check is done at 0 */
	
    // protected static final DataParameter<Byte> PLAYER_CREATED = EntityDataManager.<Byte>createKey(EntityConstruct.class, DataSerializers.BYTE);

    private int attackTimerC;
    
    public static String NAME = "construct";
	
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
        return 0.9F;
    }
	
    public boolean startRiding(Entity entityIn, boolean force)
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
	
	@Override
	protected boolean canDespawn()
	{
		return false;
	}
	
	public static void init(int entityId)
	{
		EntityRegistry.registerModEntity(new ResourceLocation(ToroQuest.MODID, NAME), EntityConstruct.class, NAME, entityId, ToroQuest.INSTANCE, 80, 1,
				true, 0x995533, 0x99aa33);
	}
	
	@Override
	public int getHorizontalFaceSpeed()
	{
		return 5;
	}

	public static void registerRenders()
	{
		RenderingRegistry.registerEntityRenderingHandler(EntityConstruct.class, new IRenderFactory<EntityConstruct>()
		{
			@Override
			public RenderConstruct createRenderFor(RenderManager manager)
			{
				return new RenderConstruct(manager);
			}
		});
	}

    public EntityConstruct(World worldIn)
    {
        super(worldIn);
        this.setSize(0.95F, 2.35F);
        this.isImmuneToFire = true;
        this.experienceValue = 80;
    }
    
    @Override
    public boolean isPotionApplicable(PotionEffect potioneffectIn)
    {
        if ( potioneffectIn.getPotion() == MobEffects.POISON )
        {
//        	net.minecraftforge.event.entity.living.PotionEvent.PotionApplicableEvent event = new net.minecraftforge.event.entity.living.PotionEvent.PotionApplicableEvent(this, potioneffectIn);
//        	net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
//        	return event.getResult() == net.minecraftforge.fml.common.eventhandler.Event.Result.ALLOW;
        	return false;
        }	
        return super.isPotionApplicable(potioneffectIn);
    }
    
    @Nullable
    @Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata)
	{
    	Arrays.fill(inventoryHandsDropChances, 0.0F);
		Arrays.fill(inventoryArmorDropChances, 0.0F);
		if ( !this.world.isRemote )
		{
			setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(Items.GOLDEN_HELMET, 1));
			setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(Items.GOLDEN_CHESTPLATE, 1));
			setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(Items.GOLDEN_LEGGINGS, 1));
			setItemStackToSlot(EntityEquipmentSlot.FEET, new ItemStack(Items.GOLDEN_BOOTS, 1));
		}
		return super.onInitialSpawn(difficulty, livingdata);
	}

    protected void initEntityAI()
    {
        this.tasks.addTask(1, new EntityAIAttackMelee(this, 1.0D, true));
        this.tasks.addTask(2, new EntityAIMoveTowardsTarget(this, 0.9D, 32.0F));
        this.tasks.addTask(4, new EntityAIMoveTowardsRestriction(this, 1.0D));
//        this.tasks.addTask(6, new EntityAIWanderAvoidWater(this, 0.6D));
//        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, false, new Class[0]));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, true));
    }

    protected void updateAITasks()
    {
        super.updateAITasks();
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(20.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(100.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
        this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(2.0D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(20.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(10.0D);
    }

    /**
     * Decrements the entity's air supply when underwater
     */
    protected int decreaseAirSupply(int air)
    {
        return air;
    }

    @Override
    public boolean canAttackClass(Class <? extends EntityLivingBase > cls)
    {
        return cls != EntityConstruct.class && cls != EntityConstructQuest.class;
    }

    //@Override
    public static void registerFixesIronGolem(DataFixer fixer)
    {
        EntityLiving.registerFixesMob(fixer, EntityConstruct.class);
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn)
    {
    	if ( this.leaping > 0 ) return false;
        this.attackTimerC = 10;
        this.world.setEntityState(this, (byte)4);
        boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), (float)(this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue()));

        if (flag)
        {
            entityIn.motionY += 0.41D;
            this.applyEnchantments(this, entityIn);
        }
        //this.playSound(SoundEvents.BLOCK_DISPENSER_LAUNCH, 1.0F, 0.7F + rand.nextFloat()/10.0F);
        this.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, 1.0F, 0.7F);
        this.playSound(SoundEvents.ENTITY_IRONGOLEM_ATTACK, 1.0F, 1.0F);
        return flag;
    }
    
    protected boolean canShieldPush = false;
//    protected boolean explodeOnDeath = true;
    
    
    @Override
    public boolean attackEntityFrom(DamageSource source, float amount)
    {

    	if ( this.world.isRemote )
        {
            return false;
        }
    	
    	if ( source == null || source == DamageSource.FALL || source.getTrueSource() == null || source.isFireDamage() || !(this.isEntityAlive()) )
		{
			return false;
		}
		
		if ( source.getTrueSource() instanceof EntityLivingBase )
		{
			EntityLivingBase e = (EntityLivingBase)source.getTrueSource();
			if ( rand.nextBoolean() && !(e instanceof EntityConstruct) ) this.setAttackTarget(e);
			// Reduced damage
			if ( !source.isMagicDamage() && source.isProjectile() )
			{
		    	this.playSound(SoundEvents.BLOCK_ANVIL_LAND, 1.0F, 0.8F + rand.nextFloat()*0.4F);
				return false;
			}
			else if ( !source.isProjectile() && !source.isMagicDamage() && !source.isFireDamage() && ( !(e.getHeldItemMainhand().getItem() instanceof ItemPickaxe) && !(e.getHeldItemMainhand().getItem().getRegistryName().toString().contains("mace")) && !(e.getHeldItemMainhand().getItem().getRegistryName().toString().contains("hammer")) ) )
			{
				double dist = e.getDistanceSq(this);
				if ( this.canShieldPush )
				{
					this.playSound(SoundEvents.BLOCK_ANVIL_LAND, 1.0F, 0.8F + rand.nextFloat()*0.4F);
					this.canShieldPush = false;
					Vec3d velocityVector = new Vec3d(e.posX - this.posX, 0, e.posZ - this.posZ);
					if ( !this.world.isRemote )
					{	
						e.addVelocity((velocityVector.x)/( dist+1 )*MathHelper.clamp(amount, 0.0D, 1.2D), (0.22D-MathHelper.clamp(dist/100.0, 0.0D, 0.16D))*MathHelper.clamp(amount, 0.0D, 1.0D), (velocityVector.z)/( dist+1 )*MathHelper.clamp(amount, 0.0D, 1.2D));
	                	e.velocityChanged = true;
					}
				}
//		    	else
//				{
//					Vec3d velocityVector = new Vec3d(e.posX - this.posX, 0, e.posZ - this.posZ);
//					if ( !this.world.isRemote )
//					{	
//						e.addVelocity((velocityVector.x)/( dist+8 )*MathHelper.clamp(amount, 0.0D, 1.0D), 0, (velocityVector.z)/( dist+8 )*MathHelper.clamp(amount, 0.0D, 1.0D));
//	                	e.velocityChanged = true;
//					}
//				}
				return false;
				//return super.attackEntityFrom(source, amount/8.0F);
			}
		}
		else
		{
			return false;
		}
    	return super.attackEntityFrom(source, amount);
    }
        
    // livingupdate
    
    protected int combatTicks = -1;
    protected int steamCounter = -1;
    protected boolean steamUp = false;
    protected int leaping = -1;

    public void onLivingUpdate()
    {
    	super.onLivingUpdate();
		
		if ( this.world.isRemote )
		{
			return;
		}
		
		if ( this.getAttackTarget() != null )
    	{
    		AIHelper.faceEntitySmart(this, this.getAttackTarget());
    		this.getLookHelper().setLookPositionWithEntity(this.getAttackTarget(), 20.0F, 20.0F);
    	}
		
        if (this.motionX * this.motionX + this.motionZ * this.motionZ > 2D && this.rand.nextInt(5) == 0)
        {
            int i = MathHelper.floor(this.posX);
            int j = MathHelper.floor(this.posY - 0.2D);
            int k = MathHelper.floor(this.posZ);
            IBlockState iblockstate = this.world.getBlockState(new BlockPos(i, j, k));

            if (iblockstate.getMaterial() != Material.AIR)
            {
                this.world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, this.posX + ((double)this.rand.nextFloat() - 0.5D) * (double)this.width, this.getEntityBoundingBox().minY + 0.1D, this.posZ + ((double)this.rand.nextFloat() - 0.5D) * (double)this.width, 4.0D * ((double)this.rand.nextFloat() - 0.5D), 0.5D, ((double)this.rand.nextFloat() - 0.5D) * 4.0D, Block.getStateId(iblockstate));
            }
        }

    	// 5 = sound
        // 6 = steam
        // 7 = steam
        // 8 = jump
        // 9 = stomp
        // 10 = death
        
        if (this.attackTimerC > 0)
        {
            --this.attackTimerC;
        }
        
//        if ( explodeOnDeath && this.getHealth() <= 0 )
//        {
//        	this.explodeOnDeath = false;
//        	this.world.newExplosion(this, this.posX, this.posY, this.posZ, 3.0F, false, true);
//        }
        
//    	if ( this.world.isRemote )
//    	{
//    		return;
//    	}
    	
    	if ( this.getAttackTarget() instanceof EntityConstruct )
		{
			this.setAttackTarget(null);
		}
    	
    	if ( this.getAttackTarget() != null && this.getAttackTarget().isEntityAlive() )
    	{
    		double dist = this.getDistanceSq(this.getAttackTarget())+1;

    		if ( dist <= 200 )
    		{
	    		int tt = this.combatTicks % 200;
	    		
	    		if ( tt % 10 == 0 ) this.canShieldPush = true;
	
		    	// Play steam sound
		    	if ( tt == 0 || rand.nextBoolean() && tt == 12 || rand.nextBoolean() && tt % 33 == 0 )
		    	{
		            if ( tt == 0 ) 
		            {
		            	this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
		            }
		    		switch ( rand.nextInt(4) )
		    		{
		    			case 0: this.playSound(SoundHandler.STEAM_0, 1.0F, 0.8F + rand.nextFloat()*0.4F); break;
		    			case 1: this.playSound(SoundHandler.STEAM_1, 1.0F, 0.8F + rand.nextFloat()*0.4F); break;
		    			case 2: this.playSound(SoundHandler.STEAM_2, 1.0F, 0.8F + rand.nextFloat()*0.4F); break;
		    			case 3: this.playSound(SoundHandler.STEAM_3, 1.0F, 0.8F + rand.nextFloat()*0.4F); break;
		    		}
		    		// this.playSound(SoundEvents.ENTITY_TNT_PRIMED, 1.5F, 1.5F + rand.nextFloat()/10.0F);
	    			this.steamCounter = 10;
	    			this.steamUp = rand.nextBoolean();
		    	}
		    	
		    	if ( tt >= 100 && tt <= 108 )
		    	{
		    		if ( tt == 100 )
		    		{
		    			// Play jump effect
		    			this.attackTimerC = 10;
		    			this.playSound(SoundEvents.BLOCK_CLOTH_PLACE, 2.0F, 0.3F);
		    			this.playSound(SoundEvents.BLOCK_SAND_FALL, 2.0F, 0.8F);
		    	    	this.playSound(SoundEvents.BLOCK_PISTON_EXTEND, 1.5F, 0.8F + rand.nextFloat()/10.0F);
		        		this.world.setEntityState(this, (byte)8);
		    		}
		    		else if ( tt == 108 )
		    		{
		    			this.leaping = 4;
		    		}
		    		Vec3d velocityVector = new Vec3d(this.getAttackTarget().posX - this.posX, 0, this.getAttackTarget().posZ - this.posZ);
		    		double d0 = 0.0;
		    		double d1 = 0.0;
		    		if ( dist < 64 )
		    		{
		    			d0 = (velocityVector.x)/(dist+64.0);
		    			d1 = (velocityVector.z)/(dist+64.0);
		    		}
					this.addVelocity(d0, 0.16D, d1);
	            	this.velocityChanged = true;
		    	}
	    		this.combatTicks++;
	    	}
	    	else
	    	{
	    		this.combatTicks = -1;
	    		this.canShieldPush = true;
	    		this.leaping = -1;
	    	}
	    	
	    	// Play stomp effect
	    	if ( this.leaping > 0 )
	    	{
		    	if ( this.onGround || this.inWater || this.isInLava() )
		    	{
		    		if ( --this.leaping <= 0 )
		    		{
			    		this.leaping = -1;
			            this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.15D);
			    		this.world.setEntityState(this, (byte)9);
			    		
			        	this.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 1.5F, 0.7F + rand.nextFloat()/10.0F);
		
			    		List<EntityLivingBase> e = this.world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(this.getPosition()).grow(10, 10, 10), new Predicate<EntityLivingBase>()
			    		{
			    			public boolean apply(@Nullable EntityLivingBase entity)
			    			{
			    				return true;
			    			}
			    		});
			    		
			    		for (EntityLivingBase entity : e)
			    		{			    			
			    			if ( entity != this && dist <= 81 && entity.posY-this.posY < 0.7 && this.posY-entity.posY < 2.7 )
			    			{
			    				entity.addVelocity((0.8/(entity.posX-this.posX)), MathHelper.clamp(0.8/(Math.sqrt(dist)),0.1,0.4), 0.8/(entity.posZ-this.posZ));
			    				entity.velocityChanged = true;
			                	entity.attackEntityFrom(new DamageSource("explosion"), (float)((this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue()-(Math.sqrt(dist))*2)));
			                	entity.setLastAttackedEntity(this);
			    				entity.setRevengeTarget(this);
	
			    			}
			    		}
		    		}
		    	}
		    	else
		    	{
			    	this.addVelocity(0, -0.08D, 0);
		    	}
	    	}
	    	
	    	// Play steam effect
	    	if ( this.steamCounter >= 0 )
	    	{
				if ( this.steamUp )
				{
					this.world.setEntityState(this, (byte)6);
				}
				else
				{
					this.world.setEntityState(this, (byte)7);
				}
				this.steamCounter--;
	    	}
    	
	    	if ( this.collidedHorizontally )
	        {
	            PathNavigateGround pathnavigateground = (PathNavigateGround)this.getNavigator();
	            Path path = pathnavigateground.getPath();
	
	            if (path != null && !path.isFinished() )
	            {
	                for (int i = 0; i < Math.min(path.getCurrentPathIndex() + 2, path.getCurrentPathLength()); ++i)
	                {
	                    PathPoint pathpoint = path.getPathPointFromIndex(i);
	                    for ( int j = 8; j >= 0; j-- )
	                    {
		                    BlockPos doorPosition = new BlockPos( pathpoint.x + rand.nextInt(3)-2, pathpoint.y + j, pathpoint.z + rand.nextInt(3)-2 );
		                    Block doorBlock = this.getBlockDoor( doorPosition );
		
	                        if ( doorBlock != null && doorBlock != Blocks.AIR && doorBlock.getBlockHardness(doorBlock.getDefaultState(), this.world, doorPosition) <= 2.0F )
	                        {
	                        	this.world.setBlockState( doorPosition, Blocks.AIR.getDefaultState());
	            	            if ( rand.nextBoolean() )
	            	            {
	            	            	if ( rand.nextBoolean() )
	            	            	{
	            	            		this.world.playEvent(1021, doorPosition, 0);
	            	            	}
	            	            	else this.playSound(SoundEvents.BLOCK_STONE_BREAK, 1.0F, 1.0F);
	
	            	            }
	            	            this.world.playEvent(2001, doorPosition, Block.getIdFromBlock( doorBlock ));
	                        }
		                }
	                }
	            }
	        }
    	
    	}
    }
    
    private Block getBlockDoor(BlockPos pos)
    {
        IBlockState iblockstate = this.world.getBlockState(pos);
        Block block = iblockstate.getBlock();
        if ( block == Blocks.BEDROCK )
        {
        	return null;
        }
        return block;
    }
    
    // STEAM EFFECT
    protected void playSteamEffect(boolean up)
    {
        EnumParticleTypes enumparticletypes = EnumParticleTypes.CLOUD;
        
        if ( rand.nextInt(5) == 0 )
        {
        	enumparticletypes = EnumParticleTypes.SMOKE_NORMAL;
        }
        
        if ( up )
        {
	        for (int i = 0; i < 5; ++i)
	        {
	            double d0 = this.rand.nextGaussian() * 0.01D;
	            double d1 = this.rand.nextDouble() * 0.16D;
	            double d2 = this.rand.nextGaussian() * 0.01D;
	            this.world.spawnParticle(enumparticletypes, this.posX + this.width/2.0F, this.posY - 0.2F + this.height, this.posZ + this.width/2.0F, d0, d1, d2);
	        }
        }
        else for (int i = 0; i < 5; ++i)
	    {
            double d0 = -this.getLookVec().x * 0.16D + rand.nextGaussian()/50.0D;
            double d1 = this.rand.nextDouble() * 0.016D;
            double d2 = this.getLookVec().z * 0.16D + rand.nextGaussian()/50.0D;
            if ( rand.nextBoolean() )
        	{
        		d0 = this.getLookVec().x * 0.16D + rand.nextGaussian()/50.0D;
                d2 = -this.getLookVec().z * 0.16D + rand.nextGaussian()/50.0D;
        	}
            this.world.spawnParticle(enumparticletypes, this.posX + this.width/2.0F, this.posY - 0.2F + this.height, this.posZ + this.width/2.0F, d0, d1, d2);
	    }
        
    }
    
    // JUMP
    protected void playJumpEffect()
    {
        EnumParticleTypes enumparticletypes = EnumParticleTypes.CLOUD;
        for (int i = 0; i < 20; ++i)
        {
        	if ( rand.nextInt(5) == 0 )
            {
            	enumparticletypes = EnumParticleTypes.SMOKE_NORMAL;
            }
        	else
        	{
        		enumparticletypes = EnumParticleTypes.CLOUD;
        	}
            double d0 = this.rand.nextGaussian() * 0.01D;
            double d1 = this.rand.nextDouble() * 0.15D;
            double d2 = this.rand.nextGaussian() * 0.01D;
            this.world.spawnParticle(enumparticletypes, this.posX + this.width/2.0F, this.posY, this.posZ + this.width/2.0F, d0, d1, d2);
        }
    }
    
    // STOMP
    protected void playStompEffect()
    {
        EnumParticleTypes enumparticletypes = EnumParticleTypes.CLOUD;
        
        for (int i = 0; i <= 15; i++)
        {
        	if ( rand.nextInt(5) == 0 )
            {
            	enumparticletypes = EnumParticleTypes.SMOKE_NORMAL;
            }
        	else
        	{
        		enumparticletypes = EnumParticleTypes.CLOUD;
        	}
            double x = MathHelper.sqrt(i/15.0D) * 0.75D;
            double y = MathHelper.sqrt((15.0D-i)/15.0D) * 0.75D;
            this.world.spawnParticle(enumparticletypes, this.posX + this.width/2.0F, this.posY - 0.1F, this.posZ + this.width/2.0F, x, 0, y);
        }
        for (int i = 0; i <= 15; i++)
        {
        	if ( rand.nextInt(5) == 0 )
            {
            	enumparticletypes = EnumParticleTypes.SMOKE_NORMAL;
            }
        	else
        	{
        		enumparticletypes = EnumParticleTypes.CLOUD;
        	}
            double x = MathHelper.sqrt(i/15.0D) * 0.75D;
            double y = -MathHelper.sqrt((15.0D-i)/15.0D) * 0.75D;
            this.world.spawnParticle(enumparticletypes, this.posX + this.width/2.0F, this.posY - 0.1F, this.posZ + this.width/2.0F, x, 0, y);
        }
        for (int i = 0; i <= 15; i++)
        {
        	if ( rand.nextInt(5) == 0 )
            {
            	enumparticletypes = EnumParticleTypes.SMOKE_NORMAL;
            }
        	else
        	{
        		enumparticletypes = EnumParticleTypes.CLOUD;
        	}
            double x = -MathHelper.sqrt(i/15.0D) * 0.75D;
            double y = MathHelper.sqrt((15.0D-i)/15.0D) * 0.75D;
            this.world.spawnParticle(enumparticletypes, this.posX + this.width/2.0F, this.posY + 0.2F, this.posZ + this.width/2.0F, x, 0, y);
        }
        for (int i = 0; i <= 15; i++)
        {
        	if ( rand.nextInt(5) == 0 )
            {
            	enumparticletypes = EnumParticleTypes.SMOKE_NORMAL;
            }
        	else
        	{
        		enumparticletypes = EnumParticleTypes.CLOUD;
        	}
            double x = -MathHelper.sqrt(i/15.0D) * 0.75D;
            double y = -MathHelper.sqrt((15.0D-i)/15.0D) * 0.75D;
            this.world.spawnParticle(enumparticletypes, this.posX + this.width/2.0F, this.posY + 0.2F, this.posZ + this.width/2.0F, x, 0, y);
        }
    }
    
    // DEATH
    
    protected void playDeathEffect()
    {
        EnumParticleTypes enumparticletypes = EnumParticleTypes.CLOUD;
        for (int i = 0; i <= 15; i++)
        {
        	if ( rand.nextInt(5) == 0 )
            {
            	enumparticletypes = EnumParticleTypes.EXPLOSION_HUGE;
            }
        	else
        	{
        		enumparticletypes = EnumParticleTypes.CLOUD;
        	}
            double x = MathHelper.sqrt(i/15.0D) * 0.75D;
            double y = MathHelper.sqrt((15.0D-i)/15.0D) * 0.75D;
            this.world.spawnParticle(enumparticletypes, this.posX + this.width/2.0F, this.posY + 0.2F, this.posZ + this.width/2.0F, x, this.rand.nextDouble(), y);
        }
        for (int i = 0; i <= 15; i++)
        {
        	if ( rand.nextInt(5) == 0 )
            {
            	enumparticletypes = EnumParticleTypes.EXPLOSION_HUGE;
            }
        	else
        	{
        		enumparticletypes = EnumParticleTypes.CLOUD;
        	}
            double x = MathHelper.sqrt(i/15.0D) * 0.75D;
            double y = -MathHelper.sqrt((15.0D-i)/15.0D) * 0.75D;
            this.world.spawnParticle(enumparticletypes, this.posX + this.width/2.0F, this.posY + 0.2F, this.posZ + this.width/2.0F, x, this.rand.nextDouble(), y);
        }
        for (int i = 0; i <= 15; i++)
        {
        	if ( rand.nextInt(5) == 0 )
            {
            	enumparticletypes = EnumParticleTypes.EXPLOSION_HUGE;
            }
        	else
        	{
        		enumparticletypes = EnumParticleTypes.CLOUD;
        	}
            double x = -MathHelper.sqrt(i/15.0D) * 0.75D;
            double y = MathHelper.sqrt((15.0D-i)/15.0D) * 0.75D;
            this.world.spawnParticle(enumparticletypes, this.posX + this.width/2.0F, this.posY + 0.2F, this.posZ + this.width/2.0F, x, this.rand.nextDouble(), y);
        }
        for (int i = 0; i <= 15; i++)
        {
        	if ( rand.nextInt(5) == 0 )
            {
            	enumparticletypes = EnumParticleTypes.EXPLOSION_HUGE;
            }
        	else
        	{
        		enumparticletypes = EnumParticleTypes.CLOUD;
        	}
            double x = -MathHelper.sqrt(i/15.0D) * 0.75D;
            double y = -MathHelper.sqrt((15.0D-i)/15.0D) * 0.75D;
            this.world.spawnParticle(enumparticletypes, this.posX + this.width/2.0F, this.posY + 0.2F, this.posZ + this.width/2.0F, x, this.rand.nextDouble(), y);
        }
    }
    
    /**
     * Handler for {@link World#setEntityState} state state
     */
    @SideOnly(Side.CLIENT)
    @Override
    public void handleStatusUpdate(byte id)
    {
	    if (id == 4)
	    {
	        this.attackTimerC = 10;
	        this.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, 1.0F, 0.7F);
	        this.playSound(SoundEvents.ENTITY_IRONGOLEM_ATTACK, 1.0F, 1.0F);
	        this.playSound(SoundEvents.BLOCK_DISPENSER_LAUNCH, 1.0F, 0.7F + rand.nextFloat()/10.0F);
	    }
        if (id == 6)
        {
            this.playSteamEffect(true);
        }
        else if (id == 7)
        {
            this.playSteamEffect(false);
        }
        else if (id == 8)
        {
			this.attackTimerC = 10;
            this.playJumpEffect();
        }
        else if (id == 9)
        {
            this.playStompEffect();
        }
        else if (id == 10)
        {
            this.playDeathEffect();
        }
        else
        {
            super.handleStatusUpdate(id);
        }
    }
    
    @Override
    protected void collideWithEntity(Entity entityIn)
    {
        if (entityIn instanceof IMob && !(entityIn instanceof EntityConstruct) && this.getRNG().nextInt(20) == 0)
        {
            this.setAttackTarget((EntityLivingBase)entityIn);
        }

        super.collideWithEntity(entityIn);
    }

    @SideOnly(Side.CLIENT)
    public int getAttackTimer()
    {
        return this.attackTimerC;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.ENTITY_IRONGOLEM_HURT;
    }

    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_IRONGOLEM_DEATH;
    }
    
    @Override
    protected SoundEvent getAmbientSound()
    {
        return SoundHandler.STEAM_AMBIENT;
    }

    protected void playStepSound(BlockPos pos, Block blockIn)
    {
    	if ( rand.nextBoolean() ) this.playSound(SoundHandler.STEAM_STEP_0, 0.8F, 0.8F + rand.nextFloat()*0.4F);
    	else this.playSound(SoundHandler.STEAM_STEP_1, 0.8F, 0.8F + rand.nextFloat()*0.4F);
        this.playSound(SoundEvents.ENTITY_IRONGOLEM_STEP, 1.0F, 0.9F + rand.nextFloat()/5.0F);
        this.playSound(SoundEvents.BLOCK_NOTE_BASEDRUM, 2.0F, 0.5F + rand.nextFloat()/10.0F);
    }

	@Override
	public void onDeath(DamageSource cause)
	{
		if (ToroQuestConfiguration.steamGolemsDropLoot) this.dropLoot();
		this.world.setEntityState(this, (byte)9);
		this.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 1.5F, 0.7F + rand.nextFloat()/10.0F);
		
		List<EntityLivingBase> e = this.world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(this.getPosition()).grow(10, 10, 10), new Predicate<EntityLivingBase>()
		{
			public boolean apply(@Nullable EntityLivingBase entity)
			{
				return true;
			}
		});
		
		for (EntityLivingBase entity : e)
		{
			double dist = this.getDistanceSq(entity)+1;
			
			if ( entity != this && dist <= 81 && entity.posY-this.posY < 0.7 && this.posY-entity.posY < 2.7 )
			{
				entity.addVelocity((0.8/(entity.posX-this.posX)), MathHelper.clamp(0.8/(Math.sqrt(dist)),0.1,0.4), 0.8/(entity.posZ-this.posZ));
				entity.velocityChanged = true;
            	entity.attackEntityFrom(new DamageSource("explosion"), (float)((this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue()-(Math.sqrt(dist))*2)));
            	entity.setLastAttackedEntity(this);
				entity.setRevengeTarget(this);

			}
		}
		
		super.onDeath(cause);
	}
	
	public void dropLoot()
	{
		if (!this.world.isRemote)
		{
			ItemStack stack = new ItemStack(Items.REDSTONE, rand.nextInt(3)+1);
			EntityItem dropItem = new EntityItem(world, posX, posY, posZ, stack.copy());
			world.spawnEntity(dropItem);
			stack = new ItemStack(Items.GUNPOWDER, rand.nextInt(3)+1);
			dropItem = new EntityItem(world, posX, posY, posZ, stack.copy());
			world.spawnEntity(dropItem);
			stack = new ItemStack(Items.EMERALD, 1);
			dropItem = new EntityItem(world, posX, posY, posZ, stack.copy());
			world.spawnEntity(dropItem);
		}
	}
	
//    @Override
//    public void setHoldingRose(boolean b)
//    {
//    	
//    }
	
	@Nullable
    protected ResourceLocation getLootTable()
    {
        return null;
    }
	
//	@Override
//    public boolean isPlayerCreated()
//    {
//        return false;
//    }

//    @Override
//    public void setPlayerCreated(boolean playerCreated)
//    {
//    	
//    }
	
//	@Override
//	public Village getVillage()
//    {
//		return null;
//    }
	
//	@Override
//	public boolean hasHome()
//    {
//        return false;
//    }
	
	@Override
	protected void dropLoot(boolean wasRecentlyHit, int lootingModifier, DamageSource source)
    {
		
    }
	
}