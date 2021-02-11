package net.torocraft.toroquest.entities;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.toroquest.ToroQuest;
import net.torocraft.toroquest.config.ToroQuestConfiguration;
import net.torocraft.toroquest.entities.render.RenderVampireBat;

public class EntityVampireBat extends EntityMob implements IMob
{

	public static String NAME = "vampire_bat";

	static
	{
		if (ToroQuestConfiguration.specificEntityNames)
		{
			NAME = ToroQuestEntities.ENTITY_PREFIX + NAME;
		}
	}

	private EntityBas nearbyBas;

	public static void init(int entityId) {
		EntityRegistry.registerModEntity(new ResourceLocation(ToroQuest.MODID, NAME), EntityVampireBat.class, NAME, entityId, ToroQuest.INSTANCE, 80,
				3, true, 0x2015, 0x909090);
	}

	public static void registerRenders()
	{
		RenderingRegistry.registerEntityRenderingHandler(EntityVampireBat.class, new IRenderFactory<EntityVampireBat>()
		{
			@Override
			public Render<EntityVampireBat> createRenderFor(RenderManager manager)
			{
				return new RenderVampireBat(manager);
			}
		});
	}

	private BlockPos spawnPosition;

	public EntityVampireBat(World worldIn)
	{
		super(worldIn);
		this.setSize(0.95F, 0.95F);
		this.experienceValue = 2;
	}

	protected void entityInit() {
		super.entityInit();
	}

	protected void initEntityAI()
	{
		tasks.addTask(2, new EntityAIAttackMelee(this, 0.4D, false));
		targetTasks.addTask(2, new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, true));
		targetTasks.addTask(3, new EntityAINearestAttackableTarget<EntityToroNpc>(this, EntityToroNpc.class, true));
		targetTasks.addTask(4, new EntityAINearestAttackableTarget<EntityVillager>(this, EntityVillager.class, false));
		targetTasks.addTask(5, new EntityAINearestAttackableTarget<EntityAnimal>(this, EntityAnimal.class, false));
	}
	
	@Override
	public boolean attackEntityAsMob(Entity entityIn)
    {
        if ( super.attackEntityAsMob(entityIn) )
        {
    		if ( nearbyBas != null )
    		{
    			//if ( this.world.isRemote )
    			{
    				this.spawnAuraParticle( this );
        			this.spawnAuraParticle( nearbyBas );
    			}
    			nearbyBas.heal(20.0F);
    		}
        	return true;
        }
        return false;
    }
	
//	@SideOnly(Side.CLIENT)
	public void spawnAuraParticle( EntityLivingBase e )
	{
		double x = e.posX;
	    double y = e.posY;
	    double z = e.posZ;
		for ( int i = 32; i > 0; i-- )
		{
    		e.world.spawnParticle(EnumParticleTypes.DRAGON_BREATH, x+rand.nextDouble()*2-1, y+rand.nextDouble(), z+rand.nextDouble()*2-1, rand.nextDouble()-0.5, rand.nextDouble()-0.5, rand.nextDouble()-0.5, 0);
    		e.world.spawnParticle(EnumParticleTypes.PORTAL, x+rand.nextDouble()*2-1, y+rand.nextDouble(), z+rand.nextDouble()*2-1, rand.nextDouble()-0.5, rand.nextDouble()-0.5, rand.nextDouble()-0.5, 0);
		}
	}

	/**
	 * Returns the volume for the sounds this mob makes.
	 */
	protected float getSoundVolume() {
		return 0.1F;
	}

	/**
	 * Gets the pitch of living sounds in living entities.
	 */
	protected float getSoundPitch() {
		return super.getSoundPitch() * 0.95F;
	}

	@Nullable
	protected SoundEvent getAmbientSound() {
		return this.rand.nextInt(4) != 0 ? null : SoundEvents.ENTITY_BAT_AMBIENT;
	}

	protected SoundEvent getHurtSound() {
		return SoundEvents.ENTITY_BAT_HURT;
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_BAT_DEATH;
	}

	/**
	 * Returns true if this entity should push and be pushed by other entities
	 * when colliding.
	 */
	public boolean canBePushed()
	{
		return false;
	}

	protected void collideWithEntity(Entity entityIn)
	{
		
	}

	protected void collideWithNearbyEntities()
	{
		
	}

	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(2.0D * ToroQuestConfiguration.bossHealthMultiplier);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D * ToroQuestConfiguration.bossAttackDamageMultiplier);
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64.0D);
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	public void onUpdate()
	{
		super.onUpdate();
		this.motionY *= 0.6000000238418579D;
	}

	protected void updateAITasks()
	{
		super.updateAITasks();
		batAiEdit();
	}

	protected void batAiEdit() {

		Entity target = getAttackTarget();

		if (target == null || rand.nextInt(100) > 50)
		{
			this.spawnPosition = nonAttackLocation();
		}
		else
		{
			spawnPosition = target.getPosition().up();
		}

		double d0 = (double) this.spawnPosition.getX() + 0.5D - this.posX;
		double d1 = (double) this.spawnPosition.getY() + 0.1D - this.posY;
		double d2 = (double) this.spawnPosition.getZ() + 0.5D - this.posZ;
		this.motionX += (Math.signum(d0) * 0.5D - this.motionX) * 0.10000000149011612D;
		this.motionY += (Math.signum(d1) * 0.699999988079071D - this.motionY) * 0.10000000149011612D;
		this.motionZ += (Math.signum(d2) * 0.5D - this.motionZ) * 0.10000000149011612D;
		float f = (float) (MathHelper.atan2(this.motionZ, this.motionX) * (180D / Math.PI)) - 90.0F;
		float f1 = MathHelper.wrapDegrees(f - this.rotationYaw);
		this.moveForward = 0.5F;
		this.rotationYaw += f1;
	}

	protected BlockPos nonAttackLocation()
	{
		if (nearbyBas == null || nearbyBas.isDead)
		{
			if (rand.nextInt(100) > 90)
			{
				searchForBas();
			}
			return randomNearByPlace();
		}

		return new BlockPos((int) nearbyBas.posX + rand.nextInt(30) - 15, (int) nearbyBas.posY + rand.nextInt(8) + 2,
				(int) nearbyBas.posZ + rand.nextInt(30) - 15);
	}

	protected BlockPos randomNearByPlace() {
		return new BlockPos((int) posX + rand.nextInt(7) - rand.nextInt(7), (int) posY + rand.nextInt(6) - 2,
				(int) posZ + rand.nextInt(7) - rand.nextInt(7));
	}

	private void searchForBas()
	{
		List<EntityBas> list = world.getEntitiesWithinAABB(EntityBas.class, new AxisAlignedBB(getPosition()).grow(96, 64, 96));
		if (list.size() < 1) {
			return;
		}
		nearbyBas = list.get(rand.nextInt(list.size()));
	}

	/**
	 * returns if this entity triggers Block.onEntityWalking on the blocks they
	 * walk on. used for spiders and wolves to prevent them from trampling crops
	 */
	protected boolean canTriggerWalking()
	{
		return false;
	}

	public void fall(float distance, float damageMultiplier) {
	}

	protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {
	}

	/**
	 * Return whether this entity should NOT trigger a pressure plate or a
	 * tripwire.
	 */
	public boolean doesEntityNotTriggerPressurePlate() {
		return true;
	}

	/**
	 * Called when the entity is attacked.
	 */
	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		if (this.isEntityInvulnerable(source))
		{
			return false;
		}
		else
		{
			return super.attackEntityFrom(source, amount);
		}
	}

	public static void registerFixesBat(DataFixer fixer) {
		EntityLiving.registerFixesMob(fixer, EntityVampireBat.class);
	}

	public float getEyeHeight()
	{
		return this.height / 2.0F;
	}

	@Nullable
	protected ResourceLocation getLootTable() {
		return LootTableList.ENTITIES_BAT;
	}

	@Override
	public void moveRelative(float strafe, float up, float forward, float friction) {
		super.moveRelative(strafe, up, forward, friction);
	}
	
	public boolean isOnLadder()
	{
		return false;
	}
}
