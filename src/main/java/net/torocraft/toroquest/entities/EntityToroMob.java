package net.torocraft.toroquest.entities;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.torocraft.toroquest.config.ToroQuestConfiguration;

public class EntityToroMob extends EntityCreature implements IMob
{
    public float fleeModifier = (ToroQuestConfiguration.banditAndOrcFleeHealthPercentBase + this.world.rand.nextInt(ToroQuestConfiguration.banditAndOrcFleeHealthPercentRange))/100.0F;
	
    public boolean useHealingPotion = false;
	public EntityPlayer enemy = null;

	public EntityToroMob(World worldIn)
	{
		super(worldIn);
	}

	public SoundCategory getSoundCategory()
	{
		return SoundCategory.HOSTILE;
	}

	@Override
	protected boolean canDespawn()
	{
		return false;
	}
	
//	public void spawnHitParticles( int amount )
//	{
//		double xx = this.posX;//  + (double) (-MathHelper.sin(this.rotationYaw * 0.017453292F));
//		double yy = this.posY + 0.5D + this.height * 0.5D;
//		double zz = this.posZ;// + (double) MathHelper.cos(this.rotationYaw * 0.017453292F);
//
//		double x = this.rand.nextGaussian() * 0.01D;
//      double y = -0.1D - this.rand.nextDouble() * 0.02D;
//      double z = this.rand.nextGaussian() * 0.01D;
//        
//		for ( int i = amount; i > 0; i-- )
//		{
//			if ( rand.nextInt(4) == 0 )
//			{
//				this.world.spawnParticle(EnumParticleTypes.REDSTONE, xx+this.rand.nextGaussian()/6.0D, yy+this.rand.nextGaussian()/3.0D, zz+this.rand.nextGaussian()/6.0D, x * amount, -y * amount, z * amount, Block.getStateId(Blocks.REDSTONE_BLOCK.getDefaultState()));
//			}
//			else
//			{
//				this.world.spawnParticle(EnumParticleTypes.REDSTONE, xx+this.rand.nextGaussian()/6.0D, yy+this.rand.nextGaussian()/3.0D, zz+this.rand.nextGaussian()/6.0D, z, y, x, Block.getStateId(Blocks.REDSTONE_BLOCK.getDefaultState()));
//			}
//		}
//	}
	
	public void spawnHitParticles( int amount )
	{
		double xx = this.posX + -MathHelper.sin(this.rotationYaw * 0.017453292F)/16.0D;
		double yy = this.posY + 0.5D + this.height * 0.5D;
		double zz = this.posZ + MathHelper.cos(this.rotationYaw * 0.017453292F)/16.0D;

		if (this.world instanceof WorldServer)
		{
			for ( int i = (int)amount; i > 0; i-- )
			{
				((WorldServer) this.world).spawnParticle(EnumParticleTypes.REDSTONE, xx+this.rand.nextGaussian()/10.0D, yy-this.rand.nextDouble()/4.0D, zz+this.rand.nextGaussian()/10.0D, 0, 0, 0, 0, 0.4D, new int[0]);
			}
		}
	}
	
//	  @SideOnly(Side.CLIENT)
//    @Override
//    public void handleStatusUpdate(byte id)
//    {
//	    if (id == 25)
//	    {
//	    	this.spawnHitParticles(1);
//        }
//	    else if (id == 26)
//	    {
//	    	
//	    	this.spawnHitParticles(3);
//        }
//	    else if (id == 27)
//	    {
//	    	
//	    	this.spawnHitParticles(5);
//        }
//	    else if (id == 28)
//	    {
//	    	
//	    	this.spawnHitParticles(9);
//        }
//        else
//        {
//            super.handleStatusUpdate(id);
//        }
//    }

	@Override
	public void onLivingUpdate()
	{
		this.updateArmSwingProgress();
		super.onLivingUpdate();
	}

	protected SoundEvent getSwimSound()
	{
		return SoundEvents.ENTITY_HOSTILE_SWIM;
	}

	protected SoundEvent getSplashSound()
	{
		return SoundEvents.ENTITY_HOSTILE_SPLASH;
	}

	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		if ( super.attackEntityFrom(source, amount) )
		{
			if ( ToroQuestConfiguration.enableBloodParticles )
			{
				int a = (int)MathHelper.clamp(Math.sqrt(amount-1), 0, 8);
				if ( a > 0 ) this.spawnHitParticles(a);
			}
			return true;
		}
		return false;
	}

	protected SoundEvent getHurtSound()
	{
		return SoundEvents.ENTITY_HOSTILE_HURT;
	}

	protected SoundEvent getDeathSound()
	{
		return SoundEvents.ENTITY_HOSTILE_DEATH;
	}

	protected SoundEvent getFallSound(int heightIn)
	{
		return heightIn > 4 ? SoundEvents.ENTITY_HOSTILE_BIG_FALL : SoundEvents.ENTITY_HOSTILE_SMALL_FALL;
	}

	public boolean attackEntityAsMob(Entity entityIn)
	{
		float f = (float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
		int knockback = 0;

		if (entityIn instanceof EntityLivingBase)
		{
			f += EnchantmentHelper.getModifierForCreature(this.getHeldItemMainhand(), ((EntityLivingBase) entityIn).getCreatureAttribute());
			knockback += EnchantmentHelper.getKnockbackModifier(this);
		}

		boolean successfulAttack = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), f);

		if (successfulAttack)
		{
			handleSuccessfulAttack(entityIn, knockback);
		}

		return successfulAttack;
	}

	protected void handleSuccessfulAttack(Entity entityIn, int knockback)
	{
		if (knockback > 0 && entityIn instanceof EntityLivingBase)
		{
			((EntityLivingBase) entityIn).knockBack(this, (float) knockback * 0.5F, (double) MathHelper.sin(this.rotationYaw * 0.017453292F), (double) (-MathHelper.cos(this.rotationYaw * 0.017453292F)));
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
			EntityPlayer entityplayer = (EntityPlayer) entityIn;
			ItemStack itemstack = this.getHeldItemMainhand();
			ItemStack itemstack1 = entityplayer.isHandActive() ? entityplayer.getActiveItemStack() : null;

			if ( itemstack != null && itemstack1 != null && itemstack.getItem() instanceof ItemAxe && itemstack1.getItem() instanceof ItemShield )
			{
				float f1 = 0.25F + (float) EnchantmentHelper.getEfficiencyModifier(this) * 0.05F;

				if (this.rand.nextFloat() < f1)
				{
					entityplayer.getCooldownTracker().setCooldown(Items.SHIELD, 100);
					this.world.setEntityState(entityplayer, (byte) 30);
				}
			}
		}

		this.applyEnchantments(this, entityIn);
	}

	@Override
	public float getBlockPathWeight(BlockPos pos)
	{
		return 0.0F;
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
	}
	
	protected void callForHelp(EntityLivingBase attacker)
	{
	}

	@Override
	protected boolean canDropLoot()
	{
		return true;
	}
	
	@Override
	protected void updateLeashedState()
	{
	   this.clearLeashed(true, false);
	   return;
	}
		
	@Override
	public boolean canBeLeashedTo(EntityPlayer player)
	{
		return false;
	}
}