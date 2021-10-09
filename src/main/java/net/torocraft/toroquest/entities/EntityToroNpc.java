package net.torocraft.toroquest.entities;

import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.Village;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.torocraft.toroquest.civilization.CivilizationType;
import net.torocraft.toroquest.civilization.CivilizationUtil;
import net.torocraft.toroquest.civilization.Province;
import net.torocraft.toroquest.config.ToroQuestConfiguration;

public class EntityToroNpc extends EntityCreature
{
	public static DataParameter<String> CIV = EntityDataManager.<String>createKey(EntityToroNpc.class, DataSerializers.STRING);
	public static DataParameter<String> PROV = EntityDataManager.<String>createKey(EntityToroNpc.class, DataSerializers.STRING);

	public EntityPlayer annoyedAt = null;
	public int isAnnoyedTimer = 0;
	
	public EntityPlayer underAttack = null;
	public int underAttackTimer = 0;
	
	public EntityPlayer murderWitness = null;
	public int murderTimer = 0;
	
	public int actionTimer = 5;
	
	public boolean inCombat = false;
    
	public float capeAni = 0;
	public boolean capeAniUp = true;
	
	public boolean interactTalkReady = true;
	
	
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
	
	public void chat( EntityToroNpc guard, EntityPlayer player, String message, @Nullable String extra )
	{
		return;
	}
	
	public void callForHelp( EntityLivingBase attacker )
	{
		return;
	}
    
	@Override
	public int getHorizontalFaceSpeed()
	{
		return 10;
	}
	
	public EntityToroNpc(World worldIn, CivilizationType civ)
	{
		super(worldIn);
		this.experienceValue = 5;
		setCivilization(civ);
		this.stepHeight = 1.05F;
	}

	@Override
	public SoundCategory getSoundCategory()
	{
		return SoundCategory.NEUTRAL;
	}

	public boolean inCombat()
	{
		return this.inCombat;
	}
	
	public boolean actionReady()
	{
		return this.actionTimer < 1;
	}
	
	public boolean isAnnoyed()
	{
		return this.isAnnoyedTimer > 0;
	}
	
	public int actionTimer()
	{
		return this.actionTimer;
	}
	
	public void setActionTimer(int n)
	{
		this.actionTimer = n;
	}
	
	public void setAnnoyed( EntityPlayer player )
	{
		if ( this.isAnnoyed() )
		{
			this.isAnnoyedTimer = 8;
		}
		else
		{
			this.isAnnoyedTimer = 4;
		}
		this.annoyedAt = player;
	}
	
	public boolean isAnnoyedAt( EntityPlayer player )
	{
		return ( this.isAnnoyed() && this.annoyedAt == player );
	}
	
	public void setUnderAttack( EntityPlayer player )
	{
		this.setAnnoyed( player );
		this.underAttack = player;
		this.underAttackTimer = 16;
	}
	
	public void setMurder( EntityPlayer player )
	{
		this.setUnderAttack( player );
		this.murderWitness = player;
		this.murderTimer = 64;
	}
	
//	public String getChatName()
//    {
//		return I18n.format("entity.toroquest_guard.name");
//    }
	
	public EntityPlayer murderWitness()
	{
		return this.murderWitness;
	}
	
	public EntityLivingBase underAttack()
	{
		return this.underAttack;
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound compound)
	{
		super.writeEntityToNBT(compound);
		String civ = s(getCivilization());
		
		if ( isSet(civ ))
		{
			compound.setString("civilization", s(getCivilization()));
		}
		else
		{
			compound.removeTag("civilization");
		}
		
		String province = getProvince();
		
		if ( isSet(province) )
		{
			compound.setString("province", province);
		}
		else
		{
			compound.removeTag("province");
		}
	}

	private boolean isSet(String civ)
	{
		return civ != null && civ.length() > 0;
	}

	@Override
	public void setAttackTarget(EntityLivingBase e)
	{
		if ( !isCivilizationAlly(e))
		{
			super.setAttackTarget(e);
		}
	}

	public boolean isCivilizationAlly(EntityLivingBase e)
	{
		if (!(e instanceof EntityToroNpc))
		{
			return false;
		}

		CivilizationType otherCiv = ((EntityToroNpc) e).getCivilization();

		if (otherCiv == null)
		{
			return false;
		}

		return otherCiv.equals(getCivilization());
	}

	private String s(CivilizationType civilization)
	{
		if (civilization == null)
		{
			return null;
		}
		return civilization.toString();
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound)
	{
		super.readEntityFromNBT(compound);
		setCivilization( e(compound.getString("civilization")) );
		setProvince( (compound.getString("province")).toString() );
	}

	private CivilizationType e(String s)
	{
		try
		{
			return CivilizationType.valueOf(s);
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	public void setProvince(String string)
	{
		if (string == null)
		{
			dataManager.set(PROV, "");
		}
		else
		{
			dataManager.set(PROV, string.toString());
		}
		dataManager.setDirty(PROV);
	}

	@Override
	protected void entityInit()
	{
		super.entityInit();
		this.dataManager.register(CIV, "");
		this.dataManager.register(PROV, "");
	}

	@Override
	protected boolean canDespawn()
	{
		return false;
	}

	public CivilizationType getCivilization()
	{
		return enumCiv(dataManager.get(CIV));
	}
	
	public void setCivilization(CivilizationType civ)
	{
		if (civ == null)
		{
			dataManager.set(CIV, "");
		}
		else
		{
			dataManager.set(CIV, civ.toString());
		}
		dataManager.setDirty(CIV);
	}
	
	protected void pledgeAllegianceIfUnaffiliated()
	{
		if ( getCivilization() != null && getProvince() != null )
		{
			return;
		}

		Province civ = CivilizationUtil.getProvinceAt(this.world, this.chunkCoordX, this.chunkCoordZ);

		if (civ == null || civ.civilization == null)
		{
			return;
		}

		setProvince(civ.name);
		setCivilization(civ.civilization);
	}
			
	public String getProvince()
	{
		return (dataManager.get(PROV));
	}
	
	protected CivilizationType enumCiv(String s)
	{
		try
		{
			CivilizationType civ = CivilizationType.valueOf(s);
			return civ;
		}
		catch (Exception e)
		{
			return null;
		}
	}

	@Override
	public void onLivingUpdate()
	{
		this.updateArmSwingProgress();
		super.onLivingUpdate();
	}

	@Override
	protected SoundEvent getSwimSound()
	{
		return SoundEvents.ENTITY_HOSTILE_SWIM;
	}

	@Override
	protected SoundEvent getSplashSound()
	{
		return SoundEvents.ENTITY_HOSTILE_SPLASH;
	}

	@Override
	protected SoundEvent getDeathSound()
	{
		return SoundEvents.ENTITY_HOSTILE_DEATH;
	}
	
	@Override
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

		if (successfulAttack) {
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

				if (this.rand.nextFloat() < f1) {
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
	
//	@Nullable
//	public Village getVillage()
//	{
//		return null;
//	}
}
