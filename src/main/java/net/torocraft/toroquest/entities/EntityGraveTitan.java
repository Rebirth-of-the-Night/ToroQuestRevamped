package net.torocraft.toroquest.entities;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.entity.Render;
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
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.toroquest.ToroQuest;
import net.torocraft.toroquest.config.ToroQuestConfiguration;
import net.torocraft.toroquest.entities.ai.AIHelper;
import net.torocraft.toroquest.entities.ai.EntityAIThrow;
import net.torocraft.toroquest.entities.render.RenderGraveTitan;
import net.torocraft.toroquest.generation.WorldGenPlacer;

public class EntityGraveTitan extends EntityZombie implements IMob
{

	public static String NAME = "grave_titan";

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
	
    public boolean isNonBoss()
    {
        return false;
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

	public static void init(int entityId)
	{
		EntityRegistry.registerModEntity(new ResourceLocation(ToroQuest.MODID, NAME), EntityGraveTitan.class, NAME, entityId, ToroQuest.INSTANCE, 80, 3,
				true, 0xffffff, 0x909090);
	}

	public static void registerRenders()
	{
		RenderingRegistry.registerEntityRenderingHandler(EntityGraveTitan.class, new IRenderFactory<EntityGraveTitan>()
		{
			@Override
			public Render<EntityGraveTitan> createRenderFor(RenderManager manager)
			{
				return new RenderGraveTitan(manager);
			}
		});
	}

	private float xsize = 4.5F;
	private float ysize = 12.5F;
	public EntityGraveTitan(World world)
	{
		super(world);
		this.setChild(false);
        
		//this.adjustSize();
		this.setRealSize( xsize, ysize);
		this.setSize( xsize, ysize );
		this.stepHeight = 4.05F;

		//this.setRealSize(10, 26);
		//AxisAlignedBB axisalignedbb = this.getEntityBoundingBox();
        //this.setEntityBoundingBox(new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.minX + (double)this.width + 5, axisalignedbb.minY + (double)this.height + 20, axisalignedbb.minZ + (double)this.width + 5 ));

		this.experienceValue = 200;
        this.isImmuneToFire = true;
	}

	@Override
	protected boolean shouldBurnInDay()
    {
        return false;
    }
	
	// INCREASE RENDER DISTNACE
	@SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        return this.getEntityBoundingBox().grow(64.0);
    }
	
//	protected ItemStack colorArmor(ItemStack stack, int color) {
//		ItemArmor armor = (ItemArmor) stack.getItem();
//		armor.setColor(stack, color);
//		stack.getTagCompound().setBoolean("Unbreakable", true);
//		return stack;
//	}

//	@Override
//	public float getEyeHeight()
//	{
//		return eyeHeight;
//	}
//	
//	public float eyeHeight = 8.0f;
	
	@Override
	public float getEyeHeight()
    {
        return ( this.getHealth()/this.getMaxHealth() ) * 8 + 1;
    }

	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(250D * ToroQuestConfiguration.bossHealthMultiplier);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(7D * ToroQuestConfiguration.bossAttackDamageMultiplier);
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(40.0D);
		this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(10.0D);
		this.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).setBaseValue(10.0D);
	}

	/**
	 * Called only once on an entity when first time spawned, via egg, mob
	 * spawner, natural spawning etc, but not called when entity is reloaded
	 * from nbt. Mainly used for initializing attributes and inventory
	 */
	@Override
	@Nullable
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata)
	{
		WorldGenPlacer.clearTrees(this.world, new BlockPos((int)this.posX, (int)this.posY, (int)this.posZ), 16);
		this.setCanPickUpLoot(false);
		this.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY );
		this.setHeldItem(EnumHand.OFF_HAND, ItemStack.EMPTY );
		//this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(200D * ToroQuestConfiguration.bossHealthMultiplier);
		this.setHealth(this.getMaxHealth());
		return super.onInitialSpawn(difficulty, livingdata);
	}

	@Override
    public boolean getCanSpawnHere()
    {
    	return true;
    }
	
	@Override
	public float getBlockPathWeight(BlockPos pos)
    {
        return this.world.getLightBrightness(pos) - 0.5F;
    }
	
	public static void registerFixesGiantZombie(DataFixer fixer)
    {
        EntityLiving.registerFixesMob(fixer, EntityGiantZombie.class);
    }
	
	@Override
	protected boolean canDespawn()
	{
		return false;
	}

	@Override
	protected void initEntityAI()
	{
		ai();
	}

	@SuppressWarnings("unchecked")
	protected void ai()
	{
		tasks.addTask(1, new EntityAISwimming(this));
//		tasks.addTask(2, new EntityAIThrow(this, 0.6D, true, 0.5, -4, 40));
		tasks.addTask(2, new EntityAIThrow(this, 0.5D, true, 0.2, -12, 60));
		//tasks.addTask(2, new EntityAIAttackMelee(this, 0.5D, false));
		tasks.addTask(3, new EntityAILookIdle(this));
		targetTasks.addTask(1, new EntityAIHurtByTarget(this, false, new Class[0]));
		targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, false, false));
	}

	private void spawnZombies()
	{
		if ( this.world.isRemote )
		{
			return;
		}
		
		if ( rand.nextBoolean() )
		{
			for (int i = 0; i < 1 + this.rand.nextInt( (int)( ((this.getHealth()/this.getMaxHealth())*3) + 1) ); i++)
			{
	
				this.spawnZombie();
			}
		}
	}
	
	@Override
	public int getHorizontalFaceSpeed()
	{
		return 5;
	}
	
	protected void spawnZombie()
	{
		if ( this.world.isRemote )
		{
			return;
		}
		
		EntityZombieVillagerRaider mob = new EntityZombieVillagerRaider(this.world);
		//mob.setChild(false);
		mob.setImmuneFire();
//		if ( rand.nextInt(8) == 0 )
//		{
//			mob.setHeldItem(EnumHand.OFF_HAND, new ItemStack(Item.getByNameOrId("minecraft:iron_sword")));
//		}
		double x = rand.nextInt(7) - 3 + 0.5;
		double y = rand.nextInt(7) + 0.5;
		double z = rand.nextInt(7) - 3 + 0.5;
		mob.setPosition(this.posX + x, this.posY + y, this.posZ + z);
		mob.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(mob)), (IEntityLivingData) null);
		Vec3d velocityVector = new Vec3d(mob.posX - this.posX, 0, mob.posZ - this.posZ);
		mob.addVelocity(velocityVector.x/8, 0.2, velocityVector.z/8);
		this.world.spawnEntity(mob);
	}
	
	@Override
	public boolean canBePushed()
	{
		return false;
	}
	
	private Block getBlockDoor(BlockPos pos)
    {
        IBlockState iblockstate = this.world.getBlockState(pos);
        Block block = iblockstate.getBlock();
        if ( block == Blocks.BEDROCK )
        {
        	return null;
        }
        return block; // instanceof BlockDoor && iblockstate.getMaterial() == Material.WOOD ? (BlockDoor)block : null;
    }
	
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
		
		if (this.ticksExisted % 25 == 0)
		{
			this.heal(ToroQuestConfiguration.bossHealthMultiplier);
	        this.bossInfo.setPercent(this.getHealth()/this.getMaxHealth());
			this.adjustSize( );
			
			if ( this.getAttackTarget() != null && this.collidedHorizontally && this.getHealth() >= 10.0F )
	        {
		        this.playSound(SoundEvents.ENTITY_ENDERDRAGON_FLAP, 2.0F, 0.25F + rand.nextFloat()/10.0F);
		        this.playSound(SoundEvents.BLOCK_NOTE_BASEDRUM, 1.5F, 0.5F + rand.nextFloat()/10.0F);
		        
	            PathNavigateGround pathnavigateground = (PathNavigateGround)this.getNavigator();
	            Path path = pathnavigateground.getPath();

	            if (path != null && !path.isFinished() )
	            {
	                for (int i = 0; i < Math.min(path.getCurrentPathIndex() + 2, path.getCurrentPathLength()); ++i)
	                {
	                    PathPoint pathpoint = path.getPathPointFromIndex(i);
	                    for ( int j = 8; j >= 0; j-- )
	                    {
		                    BlockPos doorPosition = new BlockPos( pathpoint.x + rand.nextInt(4)-3, pathpoint.y + j, pathpoint.z + rand.nextInt(4)-3 );
		                    Block doorBlock = this.getBlockDoor( doorPosition );
		
	                        if ( doorBlock != null && doorBlock != Blocks.AIR && doorBlock.getBlockHardness(doorBlock.getDefaultState(), this.world, doorPosition) <= 3.0F )
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
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(this.getHealth()/this.getMaxHealth()*10.0D);
    	
		if (this.world.isRemote)
		{
			return false;
		}
		
        this.bossInfo.setPercent(this.getHealth()/this.getMaxHealth());
		
		if ( this.isEntityInvulnerable(source) || source == DamageSource.FALL || source == null || source.getTrueSource() == null || !(this.isEntityAlive()) )
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

		{	
            this.idleTime = 0;

            if (this.getHealth() <= 0.0F)
            {
                return false;
            }
            else
            {
                float f = amount;
                boolean flag = false;
                boolean flag1 = true;

                if ((float)this.hurtResistantTime > (float)this.maxHurtResistantTime / 2.0F)
                {
                    if (amount <= this.lastDamage)
                    {
                        return false;
                    }
                    this.damageEntity(source, amount - this.lastDamage);
                    if ( rand.nextBoolean() ) this.spawnZombie();
                    this.lastDamage = amount;
                    flag1 = false;
                }
                else
                {
        	        this.bossInfo.setPercent(this.getHealth()/this.getMaxHealth());
        			this.adjustSize();
                    this.lastDamage = amount;
                    this.hurtResistantTime = this.maxHurtResistantTime;
                    this.damageEntity(source, amount);
                    this.spawnZombies();
                    if ( amount >= 3.0 )
                    {
	                    // int flesh = rand.nextInt((int)amount);
	            		this.dropLootItem(Items.ROTTEN_FLESH, 1 );
	            		this.dropLootItem(Items.ROTTEN_FLESH, 1 );
	            		this.dropLootItem(Items.ROTTEN_FLESH, 1 );
                    }
                    this.maxHurtTime = 10;
                    this.hurtTime = this.maxHurtTime;
                }

                this.attackedAtYaw = 0.0F;
                Entity entity1 = source.getTrueSource();

                if (entity1 != null)
                {
                    if (entity1 instanceof EntityLivingBase)
                    {
                        this.setRevengeTarget((EntityLivingBase)entity1);
                    }

                    if (entity1 instanceof EntityPlayer)
                    {
                        this.recentlyHit = 100;
                        this.attackingPlayer = (EntityPlayer)entity1;
                    }
                    else
                    {

                        return false;
                    }
                }

                if (flag1)
                {
                    if (flag)
                    {
                        this.world.setEntityState(this, (byte)29);
                    }
                    else if (source instanceof EntityDamageSource && ((EntityDamageSource)source).getIsThornsDamage())
                    {
                        this.world.setEntityState(this, (byte)33);
                    }
                    
                    if (entity1 != null)
                    {
                        double d1 = entity1.posX - this.posX;
                        double d0;

                        for (d0 = entity1.posZ - this.posZ; d1 * d1 + d0 * d0 < 1.0E-4D; d0 = (Math.random() - Math.random()) * 0.01D)
                        {
                            d1 = (Math.random() - Math.random()) * 0.01D;
                        }

                        this.attackedAtYaw = (float)(MathHelper.atan2(d0, d1) * (180D / Math.PI) - (double)this.rotationYaw);
                        this.knockBack(entity1, (1/((this.getHealth()/this.getMaxHealth())*4+2)), d1, d0);
                    }
                    else
                    {
                        this.attackedAtYaw = (float)((int)(Math.random() * 2.0D) * 180);
                    }
                }

                if (flag1)
                {
                    this.playHurtSound(source);
                }

                boolean flag2 = !flag || amount > 0.0F;
                
                if (entity1 instanceof EntityPlayerMP)
                {
                    CriteriaTriggers.PLAYER_HURT_ENTITY.trigger((EntityPlayerMP)entity1, this, source, f, amount, flag);
                }
    			List<EntityZombie> help = world.getEntitiesWithinAABB(EntityZombie.class, new AxisAlignedBB(getPosition()).grow(32, 16, 32), new Predicate<EntityZombie>()
    			{
    				public boolean apply(@Nullable EntityZombie entity)
    				{
    					return true;
    				}
    			});

    			for (EntityZombie zomb : help)
    			{
    				if ( zomb.getAttackTarget() == null )
    				{
    					zomb.setAttackTarget((EntityLivingBase)source.getTrueSource());
    					zomb.setRevengeTarget((EntityLivingBase)source.getTrueSource());
    				}
    				else
    				{
    					if ( rand.nextInt(5) == 0 ) this.setAttackTarget((EntityLivingBase)source.getTrueSource());
    				}
    			}

                return flag2;
            }
        }
	}
	
	@Override
    protected void damageEntity(DamageSource damageSrc, float damageAmount)
    {
		super.damageEntity(damageSrc, damageAmount);
		if ( this.getHealth() <= 0 )
		{
			this.onDeath(damageSrc);
		}
		this.adjustSize();
    }

	@Override
	public void onDeath(DamageSource cause)
	{
		if ( !this.world.isRemote )
		{
			ItemStack stack = new ItemStack(Item.getByNameOrId("toroquest:trophy_titan"));
			EntityItem dropItem = new EntityItem(world, posX, posY, posZ, stack.copy());
			dropItem.setNoPickupDelay();
			dropItem.motionY = 0.25;
			dropItem.motionZ = 0.0;
			dropItem.motionX = 0.0;
			this.world.spawnEntity(dropItem);
			dropItem.setGlowing(true);
			this.dropLootItem(Items.ROTTEN_FLESH, rand.nextInt(100)+100);
			this.dropLootItem(Items.BONE, rand.nextInt(100)+100);
		}
		
		super.onDeath(cause);
	}
	
	@Nullable
    protected ResourceLocation getLootTable()
    {
        return LootTableList.ENTITIES_ZOMBIE;
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
		}

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


//	private void adjustSize()
//	{
//		System.out.println("asize");
//		if ( !this.world.isRemote )
//		{
//			return;
//		}
//		System.out.println("!remo asize");
//		float maxHealth = this.getMaxHealth();
//		if ( maxHealth <= 0 ) return;
//		float healthPercentage = 0.25f + MathHelper.clamp( this.getHealth()/this.getMaxHealth(), 0F, 1F );
//		float hsize = 5.0F * healthPercentage;
//        float vsize = 15.0F * healthPercentage;
//		this.setRealSize( hsize , vsize );
//	}
	
	private void adjustSize( )
	{
		if ( this.getHealth() == 0 ) return;
		float percent = (this.getHealth())/this.getMaxHealth();
		float healthPercentage = MathHelper.clamp( percent, 0.1F, 1F );
        this.bossInfo.setPercent(percent);
		float hsize = xsize * healthPercentage;
        float vsize = ysize * healthPercentage;
		this.setRealSize( hsize , vsize );
		this.setSize( hsize, vsize );
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
	
	protected void playStepSound(BlockPos pos, Block blockIn)
    {
    	this.playSound(SoundEvents.ENTITY_ZOMBIE_AMBIENT, 1.5F, 0.5F + rand.nextFloat()/2.0F);
        this.playSound(SoundEvents.ENTITY_ENDERDRAGON_FLAP, 2.0F, 0.25F + rand.nextFloat()/10.0F);
        this.playSound(SoundEvents.BLOCK_NOTE_BASEDRUM, 1.5F, 0.5F + rand.nextFloat()/10.0F);
    }
    
	protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        this.playSound(SoundEvents.ENTITY_ZOMBIE_VILLAGER_AMBIENT, 1.0F, 0.5F + rand.nextFloat()/5.0F);
        return SoundEvents.ENTITY_ZOMBIE_VILLAGER_HURT;
    }

    protected SoundEvent getDeathSound()
    {
    	this.playSound(SoundEvents.ENTITY_ZOMBIE_VILLAGER_DEATH, 1.0F, 0.5F + rand.nextFloat()/5.0F);
        return SoundEvents.ENTITY_ZOMBIE_VILLAGER_DEATH;
    }
    
    @Override
    protected SoundEvent getAmbientSound()
    {
    	this.playSound(SoundEvents.ENTITY_ZOMBIE_AMBIENT, 3.0F, 0.3F);
        return SoundEvents.ENTITY_ZOMBIE_VILLAGER_AMBIENT;
    }
}
