package net.torocraft.toroquest.entities;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.toroquest.ToroQuest;
import net.torocraft.toroquest.config.ToroQuestConfiguration;
import net.torocraft.toroquest.entities.ai.AIHelper;
import net.torocraft.toroquest.entities.ai.EntityAIRaid;
import net.torocraft.toroquest.entities.ai.EntityAIThrow;
import net.torocraft.toroquest.entities.render.RenderSpiderLord;

public class EntitySpiderLord extends EntityCaveSpider implements IMob
{
	
    private static final DataParameter<Integer> STATE = EntityDataManager.<Integer>createKey(EntitySpiderLord.class, DataSerializers.VARINT);

	public static String NAME = "spider_lord";
	private int combatTimer = 2;

	static
	{
		if (ToroQuestConfiguration.specificEntityNames)
		{
			NAME = ToroQuestEntities.ENTITY_PREFIX + NAME;
		}
	}
	
	public static void registerRenders()
	{
		RenderingRegistry.registerEntityRenderingHandler(EntitySpiderLord.class, new IRenderFactory<EntitySpiderLord>() {
			@Override
			public RenderSpiderLord createRenderFor(RenderManager manager)
			{
				return new RenderSpiderLord(manager);
			}
		});
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
	public int getHorizontalFaceSpeed()
	{
		return 5;
	}
	
	// INCREASE RENDER DISTNACE
	@SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        return this.getEntityBoundingBox().grow(64.0);
    }
		
	public static void registerFixesSpider(DataFixer fixer)
    {
        EntityLiving.registerFixesMob(fixer, EntitySpiderLord.class);
    }

	public static void init(int entityId)
	{
		EntityRegistry.registerModEntity(new ResourceLocation(ToroQuest.MODID, NAME), EntitySpiderLord.class, NAME, entityId, ToroQuest.INSTANCE, 80, 3,
				true, 0xffffff, 0x909090);
	}
	
	
	public EntitySpiderLord(World world)
	{
		super(world);
        this.dataManager.register(STATE, Integer.valueOf(-1));
        this.lastActiveTime = 0;
        this.timeSinceIgnited = 0;
        this.fuseTime = 30;
        this.isImmuneToFire = true;
        this.setCreeperState(-1);
		this.setSize(3.9F, 1.9F);
		this.setRealSize(3.9F, 1.9F);
		this.experienceValue = 400;
		this.stepHeight = 4.05F;
	}

	@Override
	public float getEyeHeight()
	{
		return 1.85F;
	}
	
	

	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.6D);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(300D * ToroQuestConfiguration.bossHealthMultiplier);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5D * ToroQuestConfiguration.bossAttackDamageMultiplier);
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(50.0D);
		this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(10.0D);
		this.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).setBaseValue(10.0D);
		this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(100.0D);
	}
	/**
	 * Called only once on an entity when first time spawned, via egg, mob
	 * spawner, natural spawning etc, but not called when entity is reloaded
	 * from nbt. Mainly used for initializing attributes and inventory
	 */
	@Nullable
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata)
	{
		livingdata = super.onInitialSpawn(difficulty, livingdata);
		this.setRaidLocation( (int)this.posX, (int)this.posZ, (int)this.posY );
		return livingdata;
	}

	@Override
	protected boolean canDespawn()
	{
		return false;
	}

	protected void initEntityAI()
	{
		ai();
	}

	protected void ai()
	{
		this.tasks.addTask(1, new EntityAISwimming(this));		
		this.tasks.addTask(2, new EntityAIThrow(this, 0.5D, false, true));
        this.tasks.addTask(5, new EntityAIWanderAvoidWater(this, 0.8D));
        //this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        //this.tasks.addTask(6, new EntityAILookIdle(this));
        this.targetTasks.addTask(0, new EntityAIHurtByTarget(this, false, new Class[0]));
		this.targetTasks.addTask(1, new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, true));
	}

	public boolean isClearWebsReady = false;
    public int bossTimer = 160;
    
    Integer targetLastPosX = null;
    Integer targetLastPosZ = null;
    
    protected void bossAbility(EntityLivingBase leapTarget)
    {
		double dist = this.getDistanceSq(leapTarget);
		
        if ( dist < 40 )
        {
        	this.getNavigator().clearPath();
        }
        
    	// decrement the boss timer
    	if ( this.bossTimer > 0 )
    	{
    		this.bossTimer--;
    	}
    	
    	// when the boss timer equals 40, capture that entity's last position
    	if ( this.bossTimer == 40 )
		{
			if ( dist < 516 )
			{
				this.targetLastPosX = (int)leapTarget.posX;
				this.targetLastPosZ = (int)leapTarget.posZ;
				this.playSound(SoundEvents.ENTITY_SPIDER_AMBIENT, 2.0F, 1.2F);
				this.playSound(SoundEvents.ENTITY_CREEPER_PRIMED, 2.0F, 0.8F);
                this.setCreeperState(1);
			}
			else
			{
	            this.bossTimer++;
			}
		}
    	// when the boss timer is below 40 and greater than 0, stop
    	// moving capture and look at that entity's last position
    	if ( this.bossTimer <= 40 && this.bossTimer > 0) 
		{
    		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
		}
    	else
    	{
    		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.6D);
    	}
    	
        // if the boss ability is ready, and boss is on the ground
        if ( this.bossTimer < 1 ) //&& !this.isClearWebsReady )
        {
        	this.isClearWebsReady = true;
    		this.bossTimer = 150 + this.getRNG().nextInt(50);
    		this.combatTimer = 2;
    		this.leap(leapTarget);
        }
        
    }
    
    // LEAP
    protected void leap(EntityLivingBase leapTarget)
    {
    	double d0 = 0;
    	double d1 = 0;

    	if ( this.targetLastPosX != null && this.targetLastPosZ != null )
        {
        	d0 = this.targetLastPosX - this.posX;
            d1 = this.targetLastPosZ - this.posZ;
        }
//        else // fallback position, in case of null
//        {
//        	d0 = 0; //leapTarget.posX - this.posX;
//        	d1 = 0; //leapTarget.posZ - this.posZ;
//        }
    	
		this.playSound(SoundEvents.BLOCK_CLOTH_PLACE, 3.0F, 0.3F);
		this.playSound(SoundEvents.BLOCK_SAND_FALL, 3.0F, 0.8F);
		
		this.setPositionAndUpdate(this.posX, this.posY+2, this.posZ);
		
		if ( !this.world.isRemote )
		{
			this.addVelocity(d0/6.0D, 2.0D, d1/6.0D);
		}
    }
    
    private void clearWebs() // clear webs, then explode
    {
    	if ( !this.isClearWebsReady )
    	{
    		return;
    	}
    	
    	this.isClearWebsReady = false;
    	
    	this.lastActiveTime = 0;
        this.timeSinceIgnited = 0;
        this.fuseTime = 30;
        this.setCreeperState(-1);

    	this.spawnExplosionParticle();
    	this.spawnExplosionParticle();
    	this.spawnExplosionParticle();
		this.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 2.0F, 0.8F);
		this.playSound(SoundEvents.ENTITY_LIGHTNING_THUNDER, 2.0F, 0.6F);
		this.playSound(SoundEvents.ENTITY_LIGHTNING_IMPACT, 2.0F, 0.4F);
		
		this.playStompEffect();
		this.world.setEntityState(this, (byte)36);

		int x = this.getPosition().getX();
		int y = this.getPosition().getY();
		int z = this.getPosition().getZ();
		int range = 28;
		for ( int xx = -range/2; xx < range/2; xx++ ) // /2 range TODO
		{
			for ( int yy = -range/4; yy < range/4; yy++ )
			{
				for ( int zz = -range/2; zz < range/2; zz++ ) // /2
				{
					if ( Math.pow(Math.abs(xx)+7, 2) + Math.pow(Math.abs(zz)+7, 2) <= 554 )
					{
						BlockPos pos = new BlockPos(new BlockPos(x+xx,y+yy,z+zz));
						IBlockState block = world.getBlockState(pos);
						if ( block == Blocks.WEB.getDefaultState() )
						{
							this.world.setBlockState(pos, Blocks.AIR.getDefaultState());
						}
						else if ( block.getBlock() instanceof BlockDynamicLiquid || block.getBlock() instanceof BlockStaticLiquid )
						{
							this.world.setBlockState(pos, Blocks.DIRT.getDefaultState());
						}
					}
				}
			}
		}
    	this.explode = true;
    }
    
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte id)
    {
    	if ( id == 36 )
    	{
    		this.playStompEffect();
    	}
    	super.handleStatusUpdate(id);
    }
    protected boolean explode = false;
    
    private void explode()
    {
    	if ( !this.explode )
    	{
    		return;
    	}
    	this.explode = false;
    	this.targetLastPosX = null;
        this.targetLastPosZ = null;
		
    	List<EntityLivingBase> e = this.world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(this.getPosition()).grow(22, 22, 22), new Predicate<EntityLivingBase>()
		{
			public boolean apply(@Nullable EntityLivingBase entity)
			{
				return true;
			}
		});

		for (EntityLivingBase entity : e)
		{
			double dist = this.getDistanceSq(entity)+1;
			
			if ( entity != this && dist < 512 )
			{
				entity.setPositionAndUpdate(entity.posX, entity.posY+((512.0-dist)/256.0), entity.posZ);
				entity.addVelocity((0.8/(entity.posX-this.posX)), MathHelper.clamp(37.5/(Math.sqrt(dist)),1.0,15.0), 0.8/(entity.posZ-this.posZ)); //-entity.getDistanceSq(this)/(entity.getDistanceSq(this)+1)
				entity.velocityChanged = true;
            	entity.attackEntityFrom(new DamageSource("explosion"), (float)((22.0-(Math.sqrt(dist)))*ToroQuestConfiguration.bossAttackDamageMultiplier) );
            	entity.setLastAttackedEntity(this);
				entity.setRevengeTarget(this);
			}
		}
    }
    
    protected void playStompEffect()
    {
        EnumParticleTypes enumparticletypes = EnumParticleTypes.CLOUD;
        
        for (int i = 0; i <= 16; i++)
        {
            double x = MathHelper.sqrt(i/16.0D) * 1.0D;
            double y = MathHelper.sqrt((16.0D-i)/16.0D) * 1.0D;
            this.world.spawnParticle(enumparticletypes, this.posX + this.width/2.0F, this.posY + 0.2F, this.posZ + this.width/2.0F, x, 0, y);
        }
        for (int i = 0; i <= 16; i++)
        {
            double x = MathHelper.sqrt(i/16.0D) * 1.0D;
            double y = -MathHelper.sqrt((16.0D-i)/16.0D) * 1.0D;
            this.world.spawnParticle(enumparticletypes, this.posX + this.width/2.0F, this.posY + 0.2F, this.posZ + this.width/2.0F, x, 0, y);
        }
        for (int i = 0; i <= 16; i++)
        {
            double x = -MathHelper.sqrt(i/16.0D) * 1.0D;
            double y = MathHelper.sqrt((16.0D-i)/16.0D) * 1.0D;
            this.world.spawnParticle(enumparticletypes, this.posX + this.width/2.0F, this.posY + 0.2F, this.posZ + this.width/2.0F, x, 0, y);
        }
        for (int i = 0; i <= 16; i++)
        {
            double x = -MathHelper.sqrt(i/16.0D) * 1.0D;
            double y = -MathHelper.sqrt((16.0D-i)/16.0D) * 1.0D;
            this.world.spawnParticle(enumparticletypes, this.posX + this.width/2.0F, this.posY + 0.2F, this.posZ + this.width/2.0F, x, 0, y);
        }
    }
    
    @Override
    public boolean canBePushed()
    {
        return false;
    }
    
//    private void deadSpidersCreateWebs(EntityLivingBase spider)
//    {
//    	double x = spider.posX;
//    	double y = spider.posY;
//    	double z = spider.posZ;
//    	Block block = this.world.getBlockState(new BlockPos(x,y,z)).getBlock(); 
//		if ((block instanceof BlockBush) || block instanceof BlockAir)
//		{
//			this.world.setBlockState(new BlockPos(x,y,z), Blocks.WEB.getDefaultState());
//		}
//		block = this.world.getBlockState(new BlockPos(x-1,y,z)).getBlock(); 
//		if ((block instanceof BlockBush) || block instanceof BlockAir)
//		{
//			this.world.setBlockState(new BlockPos(x-1,y,z), Blocks.WEB.getDefaultState());
//		}
//		block = this.world.getBlockState(new BlockPos(x+1,y,z)).getBlock(); 
//		if ((block instanceof BlockBush) || block instanceof BlockAir)
//		{
//			this.world.setBlockState(new BlockPos(x+1,y,z), Blocks.WEB.getDefaultState());
//		}
//		block = this.world.getBlockState(new BlockPos(x,y,z-1)).getBlock(); 
//		if ((block instanceof BlockBush) || block instanceof BlockAir)
//		{
//			this.world.setBlockState(new BlockPos(x,y,z-1), Blocks.WEB.getDefaultState());
//		}
//		block = this.world.getBlockState(new BlockPos(x,y,z+1)).getBlock(); 
//		if ((block instanceof BlockBush) || block instanceof BlockAir)
//		{
//			this.world.setBlockState(new BlockPos(x,y,z+1), Blocks.WEB.getDefaultState());
//		}
//    }
    
    @Override
    public boolean attackEntityAsMob(Entity entityIn)
    {
        return false;
    }
    
    public void onUpdate()
    {
        if (this.isEntityAlive())
        {
            this.lastActiveTime = this.timeSinceIgnited;

            if (this.bossTimer <= 40)
            {
                this.setCreeperState(1);
            }

            int i = this.getCreeperState();

            if (i > 0 && this.timeSinceIgnited == 0)
            {
                this.playSound(SoundEvents.ENTITY_CREEPER_PRIMED, 2.0F, 0.7F);
            }

            this.timeSinceIgnited += i;

            if (this.timeSinceIgnited < 0)
            {
                this.timeSinceIgnited = 0;
            }

            if (this.timeSinceIgnited >= this.fuseTime)
            {
                this.timeSinceIgnited = this.fuseTime;
                //this.explode();
            }
        }

        super.onUpdate();
    }

    
	public void onLivingUpdate() // LIVING UPDATE ***
	{
		super.onLivingUpdate();
		
		if ( this.world.isRemote )
		{
			return;
		}
		        
    	// if the boss has landed, create isClearWebsReady
    	if ( this.onGround && this.isClearWebsReady ) // in the air
        {
    		if ( this.combatTimer < 1 )
    		{
    			this.clearWebs();
    			this.combatTimer = 2;
    		}
    		else
    		{
    			this.combatTimer--;
    		}
        }
    	else if ( this.explode )
		{
			if ( this.combatTimer < 1 )
			{
				this.explode();
				this.combatTimer = 2;
			}
			else
			{
				this.combatTimer--;
			}
		}
		
		if ( this.getAttackTarget() != null )
    	{
	    	bossAbility(this.getAttackTarget());
	    	AIHelper.faceEntitySmart(this, this.getAttackTarget());
		}
		else
		{
		    this.bossTimer = 100;
    		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.6D);
		}

		if ( this.ticksExisted % 25 == 0)
		{
			this.heal(ToroQuestConfiguration.bossHealthMultiplier);
	        this.bossInfo.setPercent(this.getHealth()/this.getMaxHealth());
		}
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		if (this.world.isRemote)
		{
			return false;
		}
		
        this.bossInfo.setPercent(this.getHealth()/this.getMaxHealth());
		
		if ( source == null || this.isEntityInvulnerable(source) || source == DamageSource.FALL || source.getTrueSource() == null || !(this.isEntityAlive()) )
		{
			return false;
		}
		
		if ( source.getTrueSource() instanceof EntityLivingBase )
		{
			double dist = source.getTrueSource().getDistanceSq(this.getPosition());
			
			if ( dist > 256 )
			{
				amount = (float)(amount*(256.0f/dist));
			}
			
			if ( !(source.getTrueSource() instanceof EntityPlayer) )
			{
				amount = amount/16.0f;
			}
		}
		
		return super.attackEntityFrom(source, amount);
	}
	
//	protected void attackDistantAttackerWithPigs(DamageSource source)
//	{
//		if (!(source.getTrueSource() instanceof EntityLivingBase)) {
//			return;
//		}
//		EntityLivingBase distantAttacker = (EntityLivingBase) source.getTrueSource();
//		spawnPigs(distantAttacker);
//	}


	public Integer raidX = null;
	public Integer raidZ = null;
	public Integer raidY = null;
	protected Random rand = new Random();
	protected final EntityAIRaid areaAI = new EntityAIRaid(this, 0.7D, 16, 16);
	
	@Override
	public void readEntityFromNBT(NBTTagCompound compound)
	{
	    if ( compound.hasKey("raidX") && compound.hasKey("raidZ") && compound.hasKey("raidY") )
	    {
	    	this.raidX = compound.getInteger("raidX");
	    	this.raidZ = compound.getInteger("raidZ");
	    	this.raidY = compound.getInteger("raidY");
	    	this.setRaidLocation( compound.getInteger("raidX"), compound.getInteger("raidZ"), compound.getInteger("raidY") );
	    }
	    super.readEntityFromNBT(compound);
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound compound)
	{
		if ( this.raidX != null && this.raidZ != null && this.raidY != null )
		{
			compound.setInteger("raidX", this.raidX);
			compound.setInteger("raidZ", this.raidZ);
			compound.setInteger("raidY", this.raidY);
		}
		super.writeEntityToNBT(compound);
	}
	
	/* Set the direction for bandits to move to */
	public void setRaidLocation(Integer x, Integer z, Integer y)
	{
		this.tasks.removeTask(this.areaAI);
		if ( x != null && z != null && y != null )
		{
			this.raidX = x;
			this.raidZ = z;
			this.raidY = y;
			this.tasks.addTask(7, this.areaAI);
			this.areaAI.setCenter(x, z);
			NBTTagCompound nbt = new NBTTagCompound();
			this.writeEntityToNBT(nbt);
		}
	}
	
	@Override
	public void onDeath(DamageSource cause)
	{
		super.onDeath(cause);
		if (!this.world.isRemote)
		{
			dropLoot();
			// quest
			
			if ( this.raidX == null || this.raidZ == null || this.raidY == null )
			{
				return;
			}
			
			int x = this.raidX;
			int z = this.raidZ;
			int y = this.raidY;
			
			int range = 128;
			for ( int xx = -range/2; xx < range; xx++ )
			{
				for ( int yy = -range/2; yy < range; yy++ )
				{
					for ( int zz = -range/2; zz < range; zz++ )
					{
						BlockPos pos = new BlockPos(x+xx-16,y+yy-4,z+zz-16);
						IBlockState block = world.getBlockState(pos);
						if ( block != null )
							
						if ( block == Blocks.WEB.getDefaultState() )
						{
							world.setBlockToAir(pos);
						}
					}
				}
			}
			this.createWebPatch(world, new BlockPos(x,y,z));
		}
	}
	
	protected void createWebPatch(World world, BlockPos start)
	{
		int radius = 17;
		int x = start.getX();
		int z = start.getZ();
		for ( int xx = -radius/2; xx < radius/2; xx++ )
		{
			for ( int zz = -radius/2; zz < radius/2; zz++ )
			{
				int distFromCenter = (int)(Math.pow(Math.abs(xx)+4, 2) + Math.pow(Math.abs(zz)+4, 2));
				if ( world.rand.nextInt(distFromCenter) < 64 )
				{
					if ( distFromCenter <= 181 )
					{
						BlockPos pos = new BlockPos(x+xx,0,z+zz);
						pos = this.getSurfacePosition(world, pos);
						if ( pos == null )
						{
							break;
						}
						pos = pos.up();
						world.setBlockState(pos, Blocks.WEB.getDefaultState());
					}
				}
			}
		}
	}
	
	private BlockPos getSurfacePosition(World world, BlockPos start)
	{
		IBlockState blockState;
		BlockPos search = new BlockPos(start.getX(), world.getActualHeight()/2, start.getZ());
		while (search.getY() > 0)
		{
			search = search.down();
			blockState = world.getBlockState(search);
			if ( blockState.getBlock() instanceof BlockLiquid )
			{
				break;
			}
			if ((blockState).isOpaqueCube())
			{
				break;
			}
		}
		return search;
	}
	
	private void dropLoot()
	{
		dropTrophy();
		dropLootItem(Items.STRING, rand.nextInt(50)+50);
		dropLootItem(Items.GUNPOWDER, rand.nextInt(50) + 50);
		dropLootItem(new ItemStack(Blocks.WEB, rand.nextInt(15)+15)); 
		dropLootItem(Items.SPIDER_EYE, 8);
	}
	
	private void dropTrophy()
	{
		ItemStack stack = new ItemStack(Item.getByNameOrId("toroquest:trophy_spider"));
		EntityItem dropItem = new EntityItem(world, posX, posY, posZ, stack.copy());
		dropItem.setNoPickupDelay();
		dropItem.motionY = 0.5;
		dropItem.motionZ = 0.0;
		dropItem.motionX = 0.0;
		this.world.spawnEntity(dropItem);
		dropItem.setGlowing(true);
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
			world.spawnEntity(dropItem);
		}
	}

	private void dropLootItem(ItemStack item)
	{
		EntityItem dropItem = new EntityItem(world, posX, posY, posZ, item.copy());
		dropItem.setNoPickupDelay();
		dropItem.motionY = rand.nextDouble();
		dropItem.motionZ = rand.nextDouble() - 0.5d;
		dropItem.motionX = rand.nextDouble() - 0.5d;
		world.spawnEntity(dropItem);
	}
	
    protected SoundEvent getAmbientSound()
    {
        this.playSound(SoundEvents.ENTITY_SPIDER_AMBIENT, this.getSoundVolume() * 3.0F, ((this.rand.nextInt(10)/10) + 0.6F));
        return null;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
    	this.playSound(SoundEvents.ENTITY_SPIDER_HURT, this.getSoundVolume() * 3.0F, ((this.rand.nextInt(10)/10) + 0.6F));
        return null;
    }

    protected SoundEvent getDeathSound()
    {
    	this.playSound(SoundEvents.ENTITY_SPIDER_DEATH, this.getSoundVolume() * 3.5F, ((this.rand.nextInt(10)/10) + 0.6F));
        return null;
    }
    
    protected SoundEvent getStepSound()
    {
        this.playSound(SoundEvents.ENTITY_SPIDER_STEP, this.getSoundVolume() * 3.0F, 0.5F);
        return null;
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

    
//    private void triggerBossAbilitySpiders()
//	{
//		int spiders = world.getEntitiesWithinAABB(EntitySpider.class, new AxisAlignedBB(this.getPosition()).grow(64, 32, 64)).size();
//
//		if (spiders > 9)
//		{
//			return;
//		}
//		spawnSpiders();
//	}
//
//	private void spawnSpiders()
//	{
//		this.playSound(SoundEvents.ENTITY_SLIME_SQUISH, 2.0F, 1.2F);
//		int amount = 1 + rand.nextInt(3);
//		
//		for (int i = 0; i < amount; i++)
//		{
//			spawnSpider();
//		}
//	}
//
//	protected void spawnSpider()
//	{
//		EntitySpider mob = new EntitySpider(world);
//		BlockPos pos = this.getPosition();
//		mob.setPosition(pos.getX(),pos.getY(),pos.getZ());
//		mob.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.4D);
//    	world.spawnEntity(mob);
//    	mob.spawnRunningParticles();
//		mob.setAttackTarget(this.getAttackTarget());
//		mob.removePotionEffect(MobEffects.INVISIBILITY);
//		mob.removePotionEffect(MobEffects.SPEED);
//		mob.removePotionEffect(MobEffects.STRENGTH);
//		mob.removePotionEffect(MobEffects.REGENERATION);
//	}
	
	 /**
     * Returns the current state of creeper, -1 is idle, 1 is 'in fuse'
     */
    public int getCreeperState()
    {
        return this.dataManager.get(STATE).intValue();
    }

    /**
     * Sets the state of creeper, -1 to idle and 1 to be 'in fuse'
     */
    public void setCreeperState(int state)
    {
        this.dataManager.set(STATE, Integer.valueOf(state));
    }
    
    private int lastActiveTime = 0;
    private int timeSinceIgnited = 0;
    private int fuseTime = 30;
    
    @SideOnly(Side.CLIENT)
    public float getCreeperFlashIntensity(float p_70831_1_)
    {
        return ((float)this.lastActiveTime + (float)(this.timeSinceIgnited - this.lastActiveTime) * p_70831_1_) / (float)(this.fuseTime - 2);
    }
	
	//@Override
    public static class GroupData implements IEntityLivingData
    {
    	
    }
    
    protected void setRealSize(float width, float height)
    {
        if (width != this.width || height != this.height)
        {
            float f = this.width;
            this.width = width;
            this.height = height;

            if (this.width < f)
            {
                double d0 = (double)width / 2.0D;
                this.setEntityBoundingBox(new AxisAlignedBB(this.posX - d0, this.posY, this.posZ - d0, this.posX + d0, this.posY + (double)this.height, this.posZ + d0));
                return;
            }

            AxisAlignedBB axisalignedbb = this.getEntityBoundingBox();
            this.setEntityBoundingBox(new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.minX + (double)this.width, axisalignedbb.minY + (double)this.height, axisalignedbb.minZ + (double)this.width));

            if (this.width > f && !this.firstUpdate && !this.world.isRemote)
            {
                this.move(MoverType.SELF, (double)(f - this.width), 0.0D, (double)(f - this.width));
            }
        }
    }
	
}
