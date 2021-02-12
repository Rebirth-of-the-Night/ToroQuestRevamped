package net.torocraft.toroquest.entities;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.torocraft.toroquest.ToroQuest;
import net.torocraft.toroquest.config.ToroQuestConfiguration;
import net.torocraft.toroquest.entities.ai.EntityAIAvoidTooCloseToro;
import net.torocraft.toroquest.entities.ai.EntityAIToroFlee;
import net.torocraft.toroquest.entities.ai.EntityAIToroMate;
import net.torocraft.toroquest.entities.model.ModelToro;
import net.torocraft.toroquest.entities.render.RenderToro;
import net.torocraft.toroquest.item.ItemToroLeather;

public class EntityToro extends EntityTameable
{

	public static String NAME = "toro";

	static
	{
		if (ToroQuestConfiguration.specificEntityNames)
		{
			NAME = ToroQuestEntities.ENTITY_PREFIX + NAME;
		}
	}

	private static final DataParameter<Boolean> CHARGING = EntityDataManager.<Boolean> createKey(EntityToro.class, DataSerializers.BOOLEAN);

	public static void init(int entityId)
	{
		EntityRegistry.registerModEntity(new ResourceLocation(ToroQuest.MODID, NAME), EntityToro.class, NAME, entityId, ToroQuest.INSTANCE, 80, 3,
				true, 0x3f3024, 0xe0d6b9);
	}

	public static void registerRenders()
	{
		RenderingRegistry.registerEntityRenderingHandler(EntityToro.class, new IRenderFactory<EntityToro>()
		{
			@Override
			public Render<EntityToro> createRenderFor(RenderManager manager)
			{
				return new RenderToro(manager, new ModelToro(), 0.7F);
			}
		});
	}

	protected void entityInit()
	{
		super.entityInit();
		this.dataManager.register(CHARGING, Boolean.valueOf(false));
	}

	private void setCharging(boolean b)
	{
		dataManager.set(CHARGING, b);
	}

	public boolean isCharging()
	{
		return ((Boolean) this.dataManager.get(CHARGING)).booleanValue();
	}

	public EntityToro(World worldIn)
	{
		super(worldIn);
		this.setSize(0.8F, 1.95F);
		this.setScaleForAge(this.isChild());
		this.setTamed(true);
	}

	@Override
	protected void initEntityAI()
	{
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIToroFlee(this, 1.0D));
		this.tasks.addTask(2, new EntityAIAttackMelee(this, 1.2, true)
		{
			@Override
			public boolean shouldExecute()
			{
				
				return ( super.shouldExecute() && !this.attacker.isChild() );
			}
		});
		this.tasks.addTask(3, new EntityAIToroMate(this, 1.0D, new EntityCow(world)));
        this.tasks.addTask(4, new EntityAITempt(this, 0.8D, Items.WHEAT, true));
		this.tasks.addTask(9, new EntityAIAvoidTooCloseToro(this, EntityToro.class, 9.0F, 0.6D, 0.6D));
		this.tasks.addTask(5, new EntityAIWander(this, 0.6D));
		this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		this.tasks.addTask(7, new EntityAILookIdle(this));
	}

	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D * ToroQuestConfiguration.toroHealthMultiplier);
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
		
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(10.0D * ToroQuestConfiguration.toroAttackDamageMultiplier);
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
	}

	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_COW_AMBIENT;
	}

	protected SoundEvent getHurtSound() {
		return SoundEvents.ENTITY_COW_HURT;
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_COW_DEATH;
	}

	protected void playStepSound(BlockPos pos, Block blockIn)
	{
		this.playSound(SoundEvents.ENTITY_COW_STEP, 0.15F, 1.0F);
	}

	public void notifyDataManagerChange(DataParameter<?> key) {
		if (CHARGING.equals(key) && this.isCharging() && world.isRemote) {
		}
		super.notifyDataManagerChange(key);
	}
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		Entity e = source.getTrueSource();
		if ( e instanceof EntityLivingBase ) 
		{
			this.setAttackTarget((EntityLivingBase)e);
		}
		return super.attackEntityFrom(source, amount);
	}
	@Override
	public void onEntityUpdate()
	{
		super.onEntityUpdate();
		
		if ( ToroQuestConfiguration.angryBulls && !this.world.isRemote && ticksExisted % 200 == 0 )
		{
			try
			{
				syncChargingWithAttackTarget();
				if ( !this.isChild() )
				{
					List<EntityToro> list = this.world.getEntitiesWithinAABB(EntityToro.class, new AxisAlignedBB(this.getPosition()).grow(16, 8, 16), new Predicate<EntityToro>()
					{
						public boolean apply(@Nullable EntityToro entity)
						{
							return !entity.isChild();
						}
					});
					if ( list.isEmpty() || list.size() < 3 )
					{
						return;
					}
					else
					{
						Collections.shuffle(list);
						for (EntityToro npc : list)
						{
							if ( !(npc.isChild()) && this != npc && this.canEntityBeSeen(npc) )
							{
								if ( this.getDistance(npc) < 4.0f + 2.0f * list.size() )
								{
									this.setAttackTarget(npc);
								}
							}
						}
					}
				}
			}
			catch ( NullPointerException e )
			{
				/*
				 * https://github.com/ToroCraft/ToroQuest/issues/116
				 */
			}
		}
	}

	protected void syncChargingWithAttackTarget()
	{
		setCharging( getAttackTarget() != null && getAttackTarget().isEntityAlive() );
	};

	@Override
	public void setAttackTarget(EntityLivingBase entitylivingbaseIn)
	{
		super.setAttackTarget(entitylivingbaseIn);
		syncChargingWithAttackTarget();
	}
	
	@Override
	public boolean canMateWith(EntityAnimal otherAnimal)
    {
        if (otherAnimal == this)
        {
            return false;
        }
        else if ( !(otherAnimal instanceof EntityCow) )
        {
            return false;
        }
        else
        {
            return this.isInLove() && otherAnimal.isInLove();
        }
    }
	
	@Override
	public void resetInLove()
    {
		this.growingAge = 1;
		return;
    }

	@Override
	public boolean attackEntityAsMob(Entity victim) {

		float attackDamage = (float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
		int knockback = 2;

		if (victim instanceof EntityLivingBase) {
			attackDamage += EnchantmentHelper.getModifierForCreature(this.getHeldItemMainhand(), ((EntityLivingBase) victim).getCreatureAttribute());
			knockback += EnchantmentHelper.getKnockbackModifier(this);
		}

		boolean wasDamaged = victim.attackEntityFrom(DamageSource.causeMobDamage(this), attackDamage);

		if (wasDamaged) {

			// setRevengeTarget(getAttackTarget());
			// if ( rand.nextInt(3) == 0 ) setAttackTarget(null);

			if (knockback > 0 && victim instanceof EntityLivingBase)
			{
				((EntityLivingBase) victim).knockBack(this, (float) knockback * 1.25F, (double) MathHelper.sin(this.rotationYaw * 0.017453292F),
						(double) (-MathHelper.cos(this.rotationYaw * 0.017453292F)));
				this.motionX *= 0.6D;
				this.motionZ *= 0.6D;
			}

			if (victim instanceof EntityPlayer)
			{
				setAttackTarget(null);
				EntityPlayer entityplayer = (EntityPlayer) victim;
				ItemStack itemstack = this.getHeldItemMainhand();
				ItemStack itemstack1 = entityplayer.isHandActive() ? entityplayer.getActiveItemStack() : null;

				if (itemstack != null && itemstack1 != null && itemstack.getItem() instanceof ItemAxe && itemstack1.getItem() == Items.SHIELD) {
					float f1 = 0.25F + (float) EnchantmentHelper.getEfficiencyModifier(this) * 0.05F;

					if (this.rand.nextFloat() < f1) {
						entityplayer.getCooldownTracker().setCooldown(Items.SHIELD, 100);
						this.world.setEntityState(entityplayer, (byte) 30);
					}
				}
			}

			this.applyEnchantments(this, victim);
		}

		return wasDamaged;
	}

	/**
	 * Returns the volume for the sounds this mob makes.
	 */
	protected float getSoundVolume()
	{
		return 0.4F;
	}

	public EntityCow createChild(EntityAgeable ageable)
	{
		return new EntityCow(this.world);
	}

	public float getEyeHeight() {
		return this.isChild() ? this.height : 1.3F;
	}

	@Override
	public void onDeath(DamageSource cause)
	{
		super.onDeath(cause);
		if (!world.isRemote)
		{
			dropLoot();
		}
	}

	public void dropLoot()
	{
		dropBeef();
		dropLeather();
	}
	
	protected void dropBeef()
	{
		ItemStack stack = new ItemStack(Items.BEEF, rand.nextInt(3) + 2);
		EntityItem dropItem = new EntityItem(world, posX, posY, posZ, stack.copy());
		world.spawnEntity(dropItem);
	}

	protected void dropLeather()
	{
		ItemStack stack = new ItemStack(ItemToroLeather.INSTANCE, 1);
		EntityItem dropItem = new EntityItem(world, posX, posY, posZ, stack.copy());
		world.spawnEntity(dropItem);
	}

	@Nullable
	protected ResourceLocation getLootTable()
	{
		return null;
	}
	
	@Override
	public boolean isOnSameTeam(Entity entityIn)
    {
		return false;
    }
	
	@Override
	public boolean shouldAttackEntity(EntityLivingBase target, EntityLivingBase owner)
    {
        return true;
    }
	
	@Override
	public void setScaleForAge(boolean child)
    {
        this.setScale(child ? 0.75F : 1.0F);
    }

}