package net.torocraft.toroquest.entities;

import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;

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
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.toroquest.civilization.CivilizationType;
import net.torocraft.toroquest.civilization.CivilizationUtil;
import net.torocraft.toroquest.civilization.Province;
import net.torocraft.toroquest.config.ToroQuestConfiguration;

public class EntityToroNpc extends EntityCreature
{	
	// -------------------------------------------------------------------
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
	
	EntityPlayer talkingWith = null;
	
	public Integer raidX = null;
	public Integer raidZ = null;
	
	protected boolean hitSafety = true;
	
	public Random rand = new Random();
	// -------------------------------------------------------------------
	
	// ========================= EntityToroNpc ===========================
	public EntityToroNpc(World worldIn, Province prov)
	{
		super(worldIn);
		this.setSize(0.6F, 1.95F);
		this.experienceValue = 30;
		Arrays.fill(inventoryHandsDropChances, ToroQuestConfiguration.guardHandsDropChance);
		Arrays.fill(inventoryArmorDropChances, ToroQuestConfiguration.guardArmorDropChance);
		((PathNavigateGround) this.getNavigator()).setBreakDoors(true);
		this.setCanPickUpLoot(false);
		this.pledgeAllegianceIfUnaffiliated(false);
	}
	// ===================================================================
	
	// ============================== NBT ===============================
	@Override
	public void readEntityFromNBT(NBTTagCompound compound)
	{
	    if ( compound.hasKey("raidX") && compound.hasKey("raidZ") )
	    {
	    	this.raidX = compound.getInteger("raidX");
	    	this.raidZ = compound.getInteger("raidZ");
	    }
	    
		this.setCivilization( enumCiv(compound.getString("civilization")) );
		this.setProvince( (compound.getString("province")).toString() );
		this.setUUID( enumUUID(compound.getString("provinceUUID")) );
		
		super.readEntityFromNBT(compound);
	}
	
	
	@Override
	public void writeEntityToNBT(NBTTagCompound compound)
	{
		String civ = s(this.getCivilization());
		if ( this.isSet(civ) )
		{
			compound.setString("civilization", s(getCivilization()));
		}
		else
		{
			compound.removeTag("civilization");
		}
		
		String province = this.getProvince();
		if ( this.isSet(province) )
		{
			compound.setString("province", province);
		}
		else
		{
			compound.removeTag("province");
		}
		
		String uuid = s(this.getUUID());
		if ( this.isSet(uuid) )
		{
			compound.setString("provinceUUID", province);
		}
		else
		{
			compound.removeTag("provinceUUID");
		}

		if ( this.posY != 0 && this.raidX != null && this.raidZ != null )
		{
			compound.setInteger("raidX", this.raidX);
			compound.setInteger("raidZ", this.raidZ);
		}
		
		super.writeEntityToNBT(compound);
	}
	
	private boolean isSet(String s)
	{
		return s != null && s.length() > 0;
	}
	
	private String s(CivilizationType civilization)
	{
		if ( civilization == null )
		{
			return null;
		}
		return civilization.toString();
	}
	
	private String s(UUID uuid)
	{
		if ( uuid == null )
		{
			return null;
		}
		return uuid.toString();
	}

	@Override
	protected void entityInit()
	{
		super.entityInit();
		this.dataManager.register(CIV, "");
		this.dataManager.register(PROV, "");
		this.dataManager.register(ID, "");
	}
	// ====================================================================
	
	// ======================== PLEDGE ALLEGIANCE =========================
	/* returns TRUE if allegiance is set by this method */
	protected boolean pledgeAllegianceIfUnaffiliated( boolean force )
	{
		if ( force || this.getProvince() == null || this.getUUID() == null || this.getCivilization() == null )
		{
			Province prov = CivilizationUtil.getProvinceAt(this.world, this.chunkCoordX, this.chunkCoordZ);
			
			if ( this.pledgeAllegiance(prov) )
			{
				if ( force )
				{
					this.playTameEffect((byte)6);
			        this.world.setEntityState(this, (byte)6);
				}
		        return true;
			}
		}
		return false;
	}
	
	/* returns true if the province is not null */
	protected boolean pledgeAllegiance( Province prov )
	{
		if ( prov != null && prov.getCiv() != null && prov.getUUID() != null )
		{
			this.setProvince(prov.getName());
			this.setCivilization(prov.getCiv());
			this.setUUID(prov.getUUID());
			
			this.onPledge(prov);
			
			return true;
		}
		return false;
	}
	
	/* returns true if the province is not null */
	protected boolean pledgeAllegiance()
	{
		return this.pledgeAllegiance(CivilizationUtil.getProvinceAt(this.world, this.chunkCoordX, this.chunkCoordZ));
	}
	
	protected void onPledge( Province prov )
	{
		
	}
	// ===================================================================
	
	// ========================== CIVILIZATION ===========================
	public static DataParameter<String> CIV = EntityDataManager.<String>createKey(EntityToroNpc.class, DataSerializers.STRING);

	public void setCivilization(CivilizationType civ)
	{
		if (civ == null)
		{
			this.dataManager.set(CIV, "");
		}
		else
		{
			this.dataManager.set(CIV, civ.toString());
		}
		this.dataManager.setDirty(CIV);
	}
	
	public CivilizationType getCivilization()
	{
		return enumCiv(this.dataManager.get(CIV));
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
	// ===================================================================
	
	// ============================== UUID ===============================
	public static DataParameter<String> ID = EntityDataManager.<String>createKey(EntityToroNpc.class, DataSerializers.STRING);
	
	public void setUUID(UUID uuid)
	{
		if ( uuid == null )
		{
			return;
		}
		this.dataManager.set(ID, uuid.toString());
		this.dataManager.setDirty(ID);
	}
	
	public UUID getUUID()
	{
		return enumUUID(this.dataManager.get(ID));
	}
	
	protected UUID enumUUID(String s)
	{
		try
		{
			UUID uuid = UUID.fromString(s);
			return uuid;
		}
		catch (Exception e)
		{
			return null;
		}
	}
	// ===================================================================
	
	// ============================ PROVINCE =============================
	public static DataParameter<String> PROV = EntityDataManager.<String>createKey(EntityToroNpc.class, DataSerializers.STRING);

	public void setProvince(String prov)
	{
		if ( prov == null )
		{
			return;
		}
		this.dataManager.set(PROV, prov.toString());
		this.dataManager.setDirty(PROV);
	}
	
	public String getProvince()
	{
		return (this.dataManager.get(PROV));
	}
	// ===================================================================

	@Override
	public void onLivingUpdate()
	{
		this.updateArmSwingProgress();
		super.onLivingUpdate();
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
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
	}
	
	//============================= Friendly =============================
	public boolean isFriendly( EntityPlayer player)
	{
		return ( this.murderWitness == null || this.murderWitness != player ) && ( this.underAttack == null || this.underAttack != player ) && ( this.annoyedAt == null || this.annoyedAt != player );
	}
	//====================================================================

	// ============================= MISC ================================
	@Override
	public SoundCategory getSoundCategory()
	{
		return SoundCategory.NEUTRAL;
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
		return ( this.isAnnoyed() && this.annoyedAt != null && this.annoyedAt == player );
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
	
	public EntityPlayer murderWitness()
	{
		return this.murderWitness;
	}
	
	public EntityPlayer underAttack()
	{
		return this.underAttack;
	}
	
	public boolean inCombat()
	{
		return this.getAttackTarget() != null || this.getRevengeTarget() != null || this.inCombat;
	}
	
	@Override
	public boolean hasHome()
	{
		return ( this.raidX != null && this.raidZ != null );
	}
	
	@Override
	public BlockPos getHomePosition()
    {
		if ( this.raidX != null && this.raidZ != null )
		{
			return new BlockPos(this.raidX,this.posY,this.raidZ);
		}
		else
		{
			return this.getPosition();
		}
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

	@Override
	protected boolean canDropLoot()
	{
		return true;
	}
	
	@Override
	protected boolean canDespawn()
	{
		return false;
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
	
	@Override
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte id)
    {
		if (id == 8)
        {
            this.playTameEffect(id);
        }
        if (id == 7)
        {
            this.playTameEffect(id);
        }
        else if (id == 6)
        {
            this.playTameEffect(id);
        }
        super.handleStatusUpdate(id);
    }
	
	public void playTameEffect(byte id)
    {
        EnumParticleTypes enumparticletypes = EnumParticleTypes.HEART;

        if (id == 6 )
        {
            enumparticletypes = EnumParticleTypes.SMOKE_NORMAL;
        }
        else if (id == 7)
        {
        	enumparticletypes = EnumParticleTypes.VILLAGER_ANGRY;
        }

        for (int i = 0; i < 7; ++i)
        {
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;
            this.world.spawnParticle(enumparticletypes, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2);
        }
    }
	
	@Override
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
	
	public void setRaidLocation(Integer x, Integer z)
	{
		if ( this.posY != 0 && this.raidX != null && this.raidZ != null )
		{
			this.raidX = x;
			this.raidZ = z;
			this.writeEntityToNBT(new NBTTagCompound());
		}
	}
	// ===================================================================
}
