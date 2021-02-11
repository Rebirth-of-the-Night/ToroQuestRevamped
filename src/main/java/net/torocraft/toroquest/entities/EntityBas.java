package net.torocraft.toroquest.entities;

import javax.annotation.Nullable;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
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
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
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
import net.torocraft.toroquest.entities.ai.EntityAIThrow;
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
	
	// INCREASE RENDER DISTNACE
	@SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        return this.getEntityBoundingBox().grow(64.0);
    }

	public EntityBas(World world)
	{
		super(world);
		this.setSize(2.9F, 7.9F);
		this.setRealSize(2.9F, 7.9F);
		this.experienceValue = 320;
        this.isImmuneToFire = true;
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
		setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.STONE_SWORD));
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
		return super.getEyeHeight() * 3.0F;
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
    	this.setLeftHanded(false);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.35D);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(275D * ToroQuestConfiguration.bossHealthMultiplier);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(7D * ToroQuestConfiguration.bossAttackDamageMultiplier);
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64.0D);
		this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(10.0D);
		this.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).setBaseValue(2.0D);
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
		spawnLimitedBats();
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

	protected void ai()
	{
		tasks.addTask(1, new EntityAISwimming(this));
		tasks.addTask(2, new EntityAIThrow(this, 0.7D, true, 0.2, -4, 30));
		tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		tasks.addTask(3, new EntityAILookIdle(this));
		targetTasks.addTask(1, new EntityAIHurtByTarget(this, false, new Class[0]));
		targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
	}

	private void spawnLimitedBats()
	{
		if ( this.world.isRemote )
		{
			return;
		}
		int playerCount = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(getPosition()).grow(64, 32, 64)).size();
		int batCount = world.getEntitiesWithinAABB(EntityVampireBat.class, new AxisAlignedBB(getPosition()).grow(64, 32, 64)).size();

		if (batCount > 6 * playerCount)
		{
			return;
		}

		spawnBats(null);
	}

	private void spawnBats(EntityLivingBase target)
	{
		if ( this.world.isRemote )
		{
			return;
		}
		//for (int i = 0; i < 3 + rand.nextInt(4); i++)
		{
			spawnBat(target);
		}
	}

	protected void spawnBat(EntityLivingBase target)
	{
		if ( this.world.isRemote )
		{
			return;
		}

		EntityVampireBat mob = new EntityVampireBat(world);

		if (target == null)
		{
			mob.setPosition(this.posX + rand.nextInt(9) - 4, this.posY + 6, this.posZ + rand.nextInt(9) - 4);
		}
		else
		{
			mob.setPosition(target.posX + rand.nextInt(9) - 4, target.posY + 4 + rand.nextInt(2), target.posZ + rand.nextInt(9) - 4);
		}

		world.spawnEntity(mob);
		
		if (target != null)
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
		
		float health = this.getHealth()/this.getMaxHealth();
        this.bossInfo.setPercent(health);
		if ( this.ticksExisted % 25 == 0 )
		{
			if ( Math.random() > health )
			{
				spawnLimitedBats();
			}
			this.heal(ToroQuestConfiguration.bossHealthMultiplier);
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
	        	this.getNavigator().clearPath();
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
		
		if ( this.isEntityInvulnerable(source) || source == DamageSource.FALL || source == null || source.getTrueSource() == null || !(this.isEntityAlive()) )
		{
			return false;
		}
		
		if (source instanceof EntityDamageSourceIndirect)
		{
			attackDistantAttackerWithBats(source);
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

	protected void attackDistantAttackerWithBats(DamageSource source)
	{
		if (!(source.getTrueSource() instanceof EntityLivingBase))
		{
			return;
		}
		EntityLivingBase distantAttacker = (EntityLivingBase) source.getTrueSource();
		if ( rand.nextInt(3) == 0 ) spawnBats(distantAttacker);
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
		dropLootItem(Items.STONE_SWORD, 1);
	}
	
	private void dropTrophy()
	{
		ItemStack stack = new ItemStack(Item.getByNameOrId("toroquest:trophy_skeleton"));
		EntityItem dropItem = new EntityItem(world, posX, posY, posZ, stack.copy());
		dropItem.setNoPickupDelay();
		dropItem.motionY = 0.25;
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
}
