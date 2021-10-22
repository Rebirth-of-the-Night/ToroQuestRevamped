package net.torocraft.toroquest.entities;

import javax.annotation.Nullable;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
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
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.toroquest.ToroQuest;
import net.torocraft.toroquest.config.ToroQuestConfiguration;
import net.torocraft.toroquest.entities.ai.AIHelper;
import net.torocraft.toroquest.entities.ai.EntityAIThrow;
import net.torocraft.toroquest.entities.ai.EntityAIZombieLeap;
import net.torocraft.toroquest.entities.render.RenderBas;

public class EntityBas extends EntitySkeleton implements IMob
{

	public static String NAME = "bas";

	static
	{
		if (ToroQuestConfiguration.specificEntityNames)
		{
			NAME = ToroQuestEntities.ENTITY_PREFIX + NAME;
		}
	}

	//public static Achievement BASTION_ACHIEVEMNT = new Achievement("bastion", "bastion", 0, 0, Items.DIAMOND_SWORD, null).registerStat();

	public static void init(int entityId)
	{
		EntityRegistry.registerModEntity(new ResourceLocation(ToroQuest.MODID, NAME), EntityBas.class, NAME, entityId, ToroQuest.INSTANCE, 80, 3,
				true, 0xffffff, 0x909090);
	}

	public static void registerRenders() {
		RenderingRegistry.registerEntityRenderingHandler(EntityBas.class, new IRenderFactory<EntityBas>() {
			@Override
			public Render<EntityBas> createRenderFor(RenderManager manager) {
				return new RenderBas(manager);
			}
		});
	}
	
    public boolean isNonBoss()
    {
        return false;
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
		return 10;
	}
	
	// INCREASE RENDER DISTNACE
	@SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        return this.getEntityBoundingBox().grow(64.0);
    }

	public EntityBas(World world)
	{
		super(world);
		this.setSize(1.95F, 3.5F);
		this.setRealSize(1.95F, 3.5F);
		this.experienceValue = 320;
        this.isImmuneToFire = true;
		this.stepHeight = 4.05F;
	}
	
    
    public void faceMovingDirection()
    {
    	try
    	{
	    	PathPoint p = this.getNavigator().getPath().getFinalPathPoint(); // TODO get target..?

	        double d0 = (p.x - this.posX) * 2;
	        double d2 = (p.z - this.posZ) * 2;
	        double d1 = p.y - this.posY;
	
	        double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
	        float f = (float)(MathHelper.atan2(d2, d0) * (180D / Math.PI)) - 90.0F;
	        float f1 = (float)(-(MathHelper.atan2(d1, d3) * (180D / Math.PI)));
	        this.rotationPitch = f1;
	        this.rotationYaw = f;
    	}
    	catch ( Exception e ) {}
    }
	
	@Override
    public boolean canBePushed()
    {
        return false;
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

	@Override
	protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty)
	{
		this.setLeftHanded(false);
		//setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.STONE_SWORD));
		//setItemStackToSlot(EntityEquipmentSlot.HEAD, colorArmor(new ItemStack(Items.LEATHER_HELMET, 1), 0xb0b0b0));
	}

//	protected ItemStack colorArmor(ItemStack stack, int color) {
//		ItemArmor armor = (ItemArmor) stack.getItem();
//		armor.setColor(stack, color);
//		stack.getTagCompound().setBoolean("Unbreakable", true);
//		return stack;
//	}

	@Override
	public float getEyeHeight()
	{
		return 2.2F;
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
    	this.setLeftHanded(false);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(250D * ToroQuestConfiguration.bossHealthMultiplier);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(7D * ToroQuestConfiguration.bossAttackDamageMultiplier);
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64.0D);
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
		this.spawnBat(null);
		this.spawnBat(null);
		this.spawnBat(null);
		return livingdata;
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
		super.initEntityAI();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void ai()
	{
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(2, new EntityAIThrow(this, 1.0D, true, 0.2D, -6, 35));
        this.tasks.addTask(5, new EntityAIZombieLeap(this, 0.4F, false));
        this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(3, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false, new Class[0]));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
	}
	
	
	
	
	

	
//	@Override
//    @SideOnly(Side.CLIENT)
//    public void handleStatusUpdate(byte id)
//    {
//        if ( id == 7 )
//        {
//        	this.spawnLifestealParticles();
//        }
//        super.handleStatusUpdate(id);
//    }
	
	public void spawnSweepParticles()
	{
        this.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, 1.0F, 0.6F + this.rand.nextFloat()/6.0F);
        this.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 1.0F, 0.6F + this.rand.nextFloat()/6.0F);
        this.playSound(SoundEvents.BLOCK_NOTE_SNARE, 1.0F, 0.5F);

		//this.swingArm(EnumHand.MAIN_HAND);
		
		double xx = this.posX + (double) (-MathHelper.sin(this.rotationYaw * 0.02F));
		double yy = this.posY + (double) this.height * 0.2D;
		double zz = this.posZ + (double) MathHelper.cos(this.rotationYaw * 0.017453292F);

		if (this.world instanceof WorldServer)
		{
			for ( int i = 16; i > 0; i-- )
			{
				((WorldServer) this.world).spawnParticle(EnumParticleTypes.SMOKE_LARGE, xx+this.rand.nextGaussian()/6.0D, yy+this.rand.nextGaussian()/6.0D, zz+this.rand.nextGaussian()/6.0D, 0, 0, 0, 0, 0.01D, new int[0]);
//				((WorldServer) this.world).spawnParticle(EnumParticleTypes.SPELL_INSTANT, xx+this.rand.nextGaussian()/10.0D, yy+this.rand.nextGaussian()/10.0D, zz+this.rand.nextGaussian()/10.0D, 0, 0, 0, 0, 0.01D, new int[0]);
//				((WorldServer) this.world).spawnParticle(EnumParticleTypes.SPELL, xx+this.rand.nextGaussian()/10.0D, yy+this.rand.nextGaussian()/10.0D, zz+this.rand.nextGaussian()/10.0D, 0, 0, 0, 0, 0.01D, new int[0]);
//				((WorldServer) this.world).spawnParticle(EnumParticleTypes.SPELL_MOB, xx+this.rand.nextGaussian()/10.0D, yy+this.rand.nextGaussian()/10.0D, zz+this.rand.nextGaussian()/10.0D, 0, 0, 0, 0, 0.01D, new int[0]);
//				((WorldServer) this.world).spawnParticle(EnumParticleTypes.SPELL_MOB_AMBIENT, xx+this.rand.nextGaussian()/10.0D, yy+this.rand.nextGaussian()/10.0D, zz+this.rand.nextGaussian()/10.0D, 0, 0, 0, 0, 0.01D, new int[0]);
//				((WorldServer) this.world).spawnParticle(EnumParticleTypes.SPELL_WITCH, xx+this.rand.nextGaussian()/10.0D, yy+this.rand.nextGaussian()/10.0D, zz+this.rand.nextGaussian()/10.0D, 0, 0, 0, 0, 0.01D, new int[0]);
			}
		}
	}
	
	public boolean attackEntityAsMob(Entity entityIn)
    {
        float f = (float)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        int i = 0;

        if (entityIn instanceof EntityLivingBase)
        {
            f += EnchantmentHelper.getModifierForCreature(this.getHeldItemMainhand(), ((EntityLivingBase)entityIn).getCreatureAttribute());
            i += EnchantmentHelper.getKnockbackModifier(this);
        }

        boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), f);

        if (flag)
        {
			this.spawnSweepParticles();

//			if ( this.world.isRemote ) 
//			{
//                this.world.setEntityState(this, (byte)7);
//			}
        	//this.spawnLifestealParticles();
        	
            if (i > 0 && entityIn instanceof EntityLivingBase)
            {
                ((EntityLivingBase)entityIn).knockBack(this, (float)i * 0.5F, (double)MathHelper.sin(this.rotationYaw * 0.017453292F), (double)(-MathHelper.cos(this.rotationYaw * 0.017453292F)));
                this.motionX *= 0.6D;
                this.motionZ *= 0.6D;
            }

            int j = EnchantmentHelper.getFireAspectModifier(this);

            if (j > 0)
            {
                entityIn.setFire(j * 4);
            }

            if (entityIn instanceof EntityPlayer)
            {
                EntityPlayer entityplayer = (EntityPlayer)entityIn;
                ItemStack itemstack = this.getHeldItemMainhand();
                ItemStack itemstack1 = entityplayer.isHandActive() ? entityplayer.getActiveItemStack() : ItemStack.EMPTY;

                if (!itemstack.isEmpty() && !itemstack1.isEmpty() && itemstack.getItem().canDisableShield(itemstack, itemstack1, entityplayer, this) && itemstack1.getItem().isShield(itemstack1, entityplayer))
                {
                    float f1 = 0.25F + (float)EnchantmentHelper.getEfficiencyModifier(this) * 0.05F;

                    if (this.rand.nextFloat() < f1)
                    {
                        entityplayer.getCooldownTracker().setCooldown(itemstack1.getItem(), 100);
                        this.world.setEntityState(entityplayer, (byte)30);
                    }
                }
            }

            this.applyEnchantments(this, entityIn);
        }

        return flag;
    }

	protected void spawnBat(EntityLivingBase target)
	{
		if ( this.world.isRemote )
		{
			return;
		}
		
		int players = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(getPosition()).grow(32, 16, 32)).size();
		int batCount = world.getEntitiesWithinAABB(EntityVampireBat.class, new AxisAlignedBB(getPosition()).grow(32, 16, 32)).size();

		if ( batCount > 7 * players )
		{
			return;
		}

		EntityVampireBat mob = new EntityVampireBat(world);

		if ( target == null )
		{
			BlockPos pos = new BlockPos(this.posX + rand.nextInt(9) - 4, this.posY + 6, this.posZ + rand.nextInt(9) - 4);
			if ( mob.world.getBlockState(pos).getBlock() != Blocks.AIR )
			{
				pos = new BlockPos(this.posX + rand.nextInt(9) - 4, this.posY + 6, this.posZ + rand.nextInt(9) - 4);
				if ( mob.world.getBlockState(pos).getBlock() != Blocks.AIR )
				{
					mob.setPosition(this.posX,this.posY,this.posZ);
				}
			}
			mob.setPosition(pos.getX(),pos.getY(),pos.getZ());
		}
		else
		{
			BlockPos pos = new BlockPos(target.posX + rand.nextInt(9) - 4, target.posY + 4 + rand.nextInt(2), target.posZ + rand.nextInt(9) - 4);
			if ( mob.world.getBlockState(pos).getBlock() != Blocks.AIR )
			{
				pos = new BlockPos(target.posX + rand.nextInt(9) - 4, target.posY + 4 + rand.nextInt(2), target.posZ + rand.nextInt(9) - 4);
				if ( mob.world.getBlockState(pos).getBlock() != Blocks.AIR )
				{
					mob.setPosition(this.posX,this.posY,this.posZ);
				}
			}
			mob.setPosition(pos.getX(),pos.getY(),pos.getZ());
		}

		world.spawnEntity(mob);
		
		if ( target != null )
		{
			mob.setAttackTarget(target);
		}
	}

	@Override
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
		
		float health = this.getHealth()/this.getMaxHealth();
        this.bossInfo.setPercent(health);
		if ( this.ticksExisted % 25 == 0 )
		{
			if ( this.rand.nextInt(5) == 0 )
			{
				if ( this.getAttackTarget() instanceof EntityPlayer )
				{
					teleportToEntity(this.getAttackTarget());
				}
				spawnBat(null);
			}
			((WorldServer) this.world).spawnParticle(EnumParticleTypes.SMOKE_LARGE, this.posX, this.posY + this.height / 2 - 1, this.posZ, 2, 0.1D, 0.1D, 0.1D, 0.01D, new int[0]);
			this.heal(ToroQuestConfiguration.bossHealthMultiplier);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
		}
		EntityLivingBase attacker = this.getAttackTarget();
    	if ( attacker == null || !attacker.isEntityAlive() )
    	{
    		attacker = this.getRevengeTarget();
    		if (attacker != null && attacker.isEntityAlive())
    		{
    			this.setAttackTarget( attacker );
    		}
    	}
    	
		if ( attacker != null && attacker.isEntityAlive() )
		{
	        double dist = this.getDistanceSq(attacker);
	        if ( dist < 8 )
	        {
	    		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.1D);
	        }
		}

//		if (this.world.isDaytime() && !this.world.isRemote) {
//			float f = this.getBrightness();
//			BlockPos blockpos = this.getRidingEntity() instanceof EntityBoat
//					? (new BlockPos(this.posX, (double) Math.round(this.posY), this.posZ)).up()
//					: new BlockPos(this.posX, (double) Math.round(this.posY), this.posZ);
//
//			if (f > 0.5F && this.rand.nextFloat() * 30.0F < (f - 0.4F) * 2.0F && this.world.canSeeSky(blockpos)) {
//				boolean flag = true;
//				ItemStack itemstack = this.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
//
//				if (!itemstack.isEmpty()) {
//					if (itemstack.isItemStackDamageable()) {
//						itemstack.setItemDamage(itemstack.getItemDamage() + this.rand.nextInt(2));
//
//						if (itemstack.getItemDamage() >= itemstack.getMaxDamage()) {
//							this.renderBrokenItemStack(itemstack);
//							this.setItemStackToSlot(EntityEquipmentSlot.HEAD, ItemStack.EMPTY);
//						}
//					}
//
//					flag = false;
//				}
//
//				if (flag) {
//					this.setFire(8);
//				}
//			}
//		}

	}
    
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		if (this.world.isRemote)
		{
			return false;
		}
		
        this.bossInfo.setPercent(this.getHealth()/this.getMaxHealth());
		
		if ( source == null ||  this.isEntityInvulnerable(source) || source == DamageSource.FALL || source.getTrueSource() == null || !(this.isEntityAlive()) )
		{
			return false;
		}
		
		if ( source instanceof EntityDamageSourceIndirect )
		{
			if ( source.getTrueSource() instanceof EntityLivingBase )
			{
				this.spawnBat((EntityLivingBase)source.getTrueSource());
			}
		}
		else if ( rand.nextBoolean() && source.getTrueSource() instanceof EntityLivingBase )
		{
			if ( rand.nextBoolean() )
			{
				this.spawnBat((EntityLivingBase)source.getTrueSource());
			}
			else
			{
				this.spawnBat(null);
			}
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
		
		boolean flag = super.attackEntityFrom(source, amount);

        if ( this.rand.nextInt(3) == 0 )
        {
            this.teleportRandomly();
        }

        return flag;
   	}
	
	protected boolean teleportRandomly()
    {
        double d0 = this.posX + (this.rand.nextDouble() - 0.5D) * 40.0D;
        double d1 = this.posY + (double)(this.rand.nextInt(40) - 20);
        double d2 = this.posZ + (this.rand.nextDouble() - 0.5D) * 40.0D;
        return this.teleportTo(d0, d1, d2);
    }
	
	private boolean teleportTo(double x, double y, double z)
    {
//        net.minecraftforge.event.entity.living.EnderTeleportEvent event = new net.minecraftforge.event.entity.living.EnderTeleportEvent(this, x, y, z, 0);
//        if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) return false;
		this.world.playSound((EntityPlayer)null, this.prevPosX, this.prevPosY, this.prevPosZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, this.getSoundCategory(), 1.0F, 1.0F);
        this.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);
        
		boolean flag = this.attemptTeleport(x, y, z);

        if ( flag )
        {
            this.world.playSound((EntityPlayer)null, this.prevPosX, this.prevPosY, this.prevPosZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, this.getSoundCategory(), 1.0F, 1.0F);
            this.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);
        }

        return true;
    }
	
	protected boolean teleportToEntity(Entity p_70816_1_)
    {
        Vec3d vec3d = new Vec3d(this.posX - p_70816_1_.posX, this.getEntityBoundingBox().minY + (double)(this.height / 2.0F) - p_70816_1_.posY + (double)p_70816_1_.getEyeHeight(), this.posZ - p_70816_1_.posZ);
        vec3d = vec3d.normalize();
        double d1 = this.posX + (this.rand.nextDouble() - 0.5D) * 8.0D - vec3d.x * 16.0D;
        double d2 = this.posY + (double)(this.rand.nextInt(16) - 8) - vec3d.y * 16.0D;
        double d3 = this.posZ + (this.rand.nextDouble() - 0.5D) * 8.0D - vec3d.z * 16.0D;
        return this.teleportTo(d1, d2, d3);
    }

	@Override
	public void onDeath(DamageSource cause)
	{
		super.onDeath(cause);
		if ( !this.world.isRemote )
		{
			dropLoot();
		}
	}

	private void dropLoot()
	{
		dropTrophy();
		dropLootItem(Items.BONE, rand.nextInt(100)+20);
		dropLootItem(Items.COAL, rand.nextInt(20)+5);
		//dropLootItem(Items.STONE_SWORD, 1);
	}
	
	private void dropTrophy()
	{
		ItemStack stack = new ItemStack(Item.getByNameOrId("toroquest:trophy_skeleton"));
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
    
    protected SoundEvent getAmbientSound()
    {
        this.playSound(SoundEvents.ENTITY_WITHER_SKELETON_AMBIENT, 1.0F, 0.8F + this.rand.nextFloat()/6.0F);
        this.playSound(SoundEvents.ENTITY_ENDERMEN_STARE, 1.0F, 0.6F + this.rand.nextFloat()/6.0F);
		return null;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        this.playSound(SoundEvents.ENTITY_WITHER_SKELETON_HURT, 1.0F, 0.8F + this.rand.nextFloat()/6.0F);
        this.playSound(SoundEvents.ENTITY_ENDERMEN_HURT, 1.0F, 0.8F * this.rand.nextFloat()/6.0F);
        return null;
    }

    protected SoundEvent getDeathSound()
    {
        this.playSound(SoundEvents.ENTITY_WITHER_SKELETON_DEATH, 1.0F, 0.8F + this.rand.nextFloat()/6.0F);
        this.playSound(SoundEvents.ENTITY_ENDERMEN_DEATH, 1.0F, 0.8F);
		return null;
    }

    protected SoundEvent getStepSound()
    {
        this.playSound(SoundEvents.ENTITY_WITHER_SKELETON_STEP, 1.0F, 0.8F + this.rand.nextFloat()/6.0F);
		return null;
    }
}
