package net.torocraft.toroquest.entities;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.toroquest.ToroQuest;
import net.torocraft.toroquest.civilization.CivilizationHandlers;
import net.torocraft.toroquest.civilization.CivilizationUtil;
import net.torocraft.toroquest.civilization.Province;
import net.torocraft.toroquest.civilization.quests.QuestRecruit;
import net.torocraft.toroquest.config.ToroQuestConfiguration;
import net.torocraft.toroquest.config.ToroQuestConfiguration.AdventurerArmorSet;
import net.torocraft.toroquest.entities.AIArcher;
import net.torocraft.toroquest.entities.AIAttackWithSword;
import net.torocraft.toroquest.entities.EntityAIFleeAdventurer;
import net.torocraft.toroquest.entities.EntityAIRangedFlee;
import net.torocraft.toroquest.entities.EntityConstruct;
import net.torocraft.toroquest.entities.EntityGuard;
import net.torocraft.toroquest.entities.EntitySmartArrow;
import net.torocraft.toroquest.entities.EntityToroNpc;
import net.torocraft.toroquest.entities.ToroNPC;
import net.torocraft.toroquest.entities.ToroQuestEntities;
import net.torocraft.toroquest.entities.ai.EntityAIDespawn;
import net.torocraft.toroquest.entities.ai.EntityAIPatrolVillage;
import net.torocraft.toroquest.entities.ai.EntityAIRaid;
import net.torocraft.toroquest.entities.ai.EntityAISmartTempt;
import net.torocraft.toroquest.entities.ai.EntityAIWanderVillage;
import net.torocraft.toroquest.entities.ai.EntityAIZombieLeap;
import net.torocraft.toroquest.entities.render.RenderAdventurer;

public class EntityAdventurer extends EntityCreature implements IRangedAttackMob, ToroNPC
{
	
	protected ResourceLocation banditSkin = new ResourceLocation(ToroQuest.MODID + ":textures/entity/adventurer/adventurer_" + rand.nextInt(ToroQuestConfiguration.adventurerSkins) + ".png");

	@Override
	public boolean canPickUpLoot()
	{
		return true;
	}
	
	@Override
    protected float getWaterSlowDown()
    {
        return this.isHandActive()?0.8F:0.9F;
    }
	
	public EntityAdventurer(World worldIn)
	{
		super(worldIn);
		this.setDead(); // -------- DEAD --------
        this.setSize(0.6F, 1.9F);
        this.setCombatTask();
		Arrays.fill(inventoryHandsDropChances, ToroQuestConfiguration.banditHandsDropChance);
		Arrays.fill(inventoryArmorDropChances, ToroQuestConfiguration.banditArmorDropChance);
		this.experienceValue = 20;
		this.inCombat = false;
		this.blocking = false;
        this.setSprinting(false);
		this.blockingTimer = 0;
		this.setAttackTarget(null);
    	this.canShieldPush = true;
		this.resetActiveHand();
		this.setActiveHand(EnumHand.MAIN_HAND);
		this.activeItemStackUseCount = 0;
    	this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.25D);
    	this.strafeVer = 0.0F;
    	this.strafeHor = 0.0F;
    	this.getMoveHelper().strafe( 0.0F, 0.0F );
    	this.getNavigator().clearPath();
    	((PathNavigateGround)this.getNavigator()).setBreakDoors(true);
	    this.setCanPickUpLoot(false);
		this.stepHeight = 2.05F;
	    
	    // XXX
	    this.setAlwaysRenderNameTag(true);
	    this.setCustomNameTag("...");
	}
	
	public String getChatName()
    {
		return I18n.format("entity.toroquest_adventurer.name");
    }

	public static String NAME = "adventurer";
	static
	{
		if (ToroQuestConfiguration.specificEntityNames)
		{
			NAME = ToroQuestEntities.ENTITY_PREFIX + NAME;
		}
	}
	
	public static void init(int entityId)
	{
		EntityRegistry.registerModEntity(new ResourceLocation(ToroQuest.MODID, NAME), EntityAdventurer.class, NAME, entityId, ToroQuest.INSTANCE, 80, 1,
				true, 0x8f3026, 0xe0d359);
	}

	public static void registerRenders()
	{
		RenderingRegistry.registerEntityRenderingHandler(EntityAdventurer.class, new IRenderFactory<EntityAdventurer>()
		{
			@Override
			public Render<EntityAdventurer> createRenderFor(RenderManager manager)
			{
				return new RenderAdventurer(manager);
			}
		});
	}
	
	@Override
	public void onDeath(DamageSource cause)
	{
		super.onDeath(cause);
		this.dropLoot();
	}
	
	protected void initEntityAI()
	{
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(2, new EntityAIRangedFlee(this, 0.7D));
		this.tasks.addTask(3, new EntityAIFleeAdventurer(this, 0.7D));
        this.tasks.addTask(11, new EntityAIWanderVillage(this, 0.55D));

        //this.tasks.addTask(4, new EntityAIBreakDoorBandit(this));
        this.tasks.addTask(4, new EntityAIOpenDoor(this, false));
        this.tasks.addTask(5, new EntityAIZombieLeap(this, 0.4F, true));
//        this.tasks.addTask(6, new EntityAISmartTempt(this, 0.55D, Items.EMERALD)
//        {
//        	@Override
//			public boolean shouldExecute()
//		    {
//        		super.shouldExecute();
//		        if ( inCombat || getAttackTarget() != null || isBurning() )
//		        {
//			        return false;
//		        }
//		        return super.shouldExecute();
//		    }
//		});
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityLivingBase.class, 8.0F));
//	    this.tasks.addTask(7, new EntityAIMoveThroughVillage(this, 0.6D, false)
//	    {
//	    	@Override
//	    	public boolean shouldExecute()
//	        {
//	    		if ( greetingTimer > 0 ) return false;
//	    		return super.shouldExecute();
//	        }
//	    });
//	    this.tasks.addTask(8, new EntityAIWanderAvoidWater(this, 0.6D)
//	    {
//	    	@Nullable
//    	    protected Vec3d getPosition()
//    	    {
//    	        if (this.entity.isInWater())
//    	        {
//    	            Vec3d vec3d = RandomPositionGenerator.getLandPos(this.entity, 15, 7);
//    	            return vec3d == null ? super.getPosition() : vec3d;
//    	        }
//    	        else
//    	        {
//    	            if ( greetingTimer <= 0 && this.entity.getNavigator().noPath() && this.entity.getRNG().nextFloat() >= 0.002F )
//    	            {
//						return RandomPositionGenerator.getLandPos(this.entity, 15, 7);
//    	            }
//					else
//					{
//						return super.getPosition();
//					}
//    	        }
//    	    }
//		});
        this.tasks.addTask(10, new EntityAILookIdle(this)
	    {
        	public boolean shouldExecute()
	        {
        		if ( greetingTimer > 0 ) return false;
	            return super.shouldExecute();
	        }
	    });
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0])
		{
			@Override
			public boolean shouldExecute()
			{
	    		EntityLivingBase attacker = this.taskOwner.getRevengeTarget();
		        return attacker != null && this.isSuitableTarget(attacker, false) && !(attacker instanceof EntityAdventurer);
			}
		});
	}
	
	//===================================================== Add Equipment =======================================================

	private Integer weaponMainInteger = null;
	
	private Integer weaponOffInteger = null;
	
	public ItemStack getMeleeWeapon()
	{		
		if ( this.weaponMainInteger != null )
		{
	    	this.weaponMain = new ItemStack(Item.getByNameOrId(ToroQuestConfiguration.banditOneHandedMeleeWeapons[this.weaponMainInteger]),1);
		}
		
		if ( this.weaponMain == null || this.weaponMain.isEmpty() )
		{
			int r = rand.nextInt(ToroQuestConfiguration.banditOneHandedMeleeWeapons.length);
			
	    	this.weaponMain = new ItemStack(Item.getByNameOrId(ToroQuestConfiguration.banditOneHandedMeleeWeapons[r]),1);

			if ( this.weaponMain == null || this.weaponMain.isEmpty() )
			{
				this.weaponMainInteger = null;
				return new ItemStack(Items.IRON_SWORD);
			}
			else
			{
				this.weaponMainInteger = r;
				return this.weaponMain;
			}
		}
		else
		{
			return this.weaponMain;
		}
	}
	
	public ItemStack getShield()
	{
		if ( this.weaponOffInteger != null )
		{
	    	this.weaponOff = new ItemStack(Item.getByNameOrId(ToroQuestConfiguration.banditShields[this.weaponOffInteger]),1);
		}
		
		if ( this.weaponOff == null || this.weaponOff.isEmpty() )
		{
			int r = rand.nextInt(ToroQuestConfiguration.banditShields.length);
			
	    	this.weaponOff = new ItemStack(Item.getByNameOrId(ToroQuestConfiguration.banditShields[r]),1);

			if ( this.weaponOff == null || this.weaponOff.isEmpty() )
			{
				this.weaponMainInteger = null;
				return new ItemStack(Items.IRON_SWORD);
			}
			else
			{
				this.weaponOffInteger = r;
				return this.weaponOff;
			}
		}
		else
		{
			return this.weaponOff;
		}
	}
	
	public void equipMeleeWeapon()
	{
		this.setHeldItem(EnumHand.MAIN_HAND, this.getMeleeWeapon() );
		this.setHeldItem(EnumHand.OFF_HAND, this.getShield() );
	}
	
	public void addEquipment()
    {
    	if ( this.world.isRemote )
    	{
    		return;
    	}
	    
    	this.equipMeleeWeapon();
    	
	    AdventurerArmorSet armorSet = ToroQuestConfiguration.adventurerArmorSets.get(rand.nextInt(ToroQuestConfiguration.adventurerArmorSets.size()));
	   
	    this.setItemStackToSlot(EntityEquipmentSlot.HEAD, armorSet.helmet);
		this.setItemStackToSlot(EntityEquipmentSlot.CHEST, armorSet.chestplate);
		this.setItemStackToSlot(EntityEquipmentSlot.LEGS, armorSet.leggings);
		this.setItemStackToSlot(EntityEquipmentSlot.FEET, armorSet.boots);
    }
	
	protected EntityPlayer enemy = null;
	protected ItemStack weaponMain = new ItemStack(Items.AIR, 1);
	protected ItemStack weaponOff = new ItemStack(Items.AIR, 1);
	protected final AIArcher<EntityAdventurer> aiArrowAttack = new AIArcher<EntityAdventurer>(this, 0.45D, 40, 40.0F);
    protected boolean inCombat = false;
    protected double randPosX;
    protected double randPosY;
    protected double randPosZ;
    public int despawnTimer = 100;
    public int stance = rand.nextInt(6)+5;
	protected float strafeVer = 0.0F;
	protected float strafeHor = 0.0F;
    public int passiveTimer = -1;
	protected boolean bribed = false;
	public float capeAni = 0;
	public boolean capeAniUp = true;
	protected boolean blocking = false;
	protected int blockingTimer = 0;
	protected boolean despawn = false;
	protected boolean canTalk = true;
	public short potionImmunity = 0;
    //private int potionUseTimer;
	public Integer raidX = null;
	public Integer raidZ = null;
	public boolean canShieldPush = true;
    protected Vec3d vec3d;
    protected int splashPotionTimer = 6;
    protected int hasSplashPotion = 1;
    public int limitPotions = rand.nextInt(3)+1;
	public double renderSizeXZ = 0.9D + rand.nextDouble()/12.0D;
	public double renderSizeY =  renderSizeXZ * (1.025D + rand.nextFloat()/16.0D);
	protected final EntityAIRaid areaAI = new EntityAIRaid(this, 0.6D, 16, 48)
	{
		@Override
		public void startExecuting()
		{
			super.startExecuting();
			if ( !entity.world.isRemote )
			{
				if ( entity.isSprinting() && rand.nextBoolean() )
				{
					entity.addVelocity(0.0D,0.35D,0.0D);
				}
				entity.setSprinting(rand.nextBoolean());
			}
		}
	};
//    {
//		public boolean continueExecuting()
//		{
//			if ( entity.getDistance(null)  > 0 )
//			{
//				
//			}
//			return super.continueExecuting();
//		}
//	};
	protected static final UUID MODIFIER_UUID = UUID.fromString("5CD17E52-A79A-43D3-A529-90FDE04B181E");
	protected static final AttributeModifier MODIFIER = (new AttributeModifier(MODIFIER_UUID, "Drinking speed penalty", -0.35D, 0)).setSaved(false);
	protected static final DataParameter<Boolean> IS_DRINKING = EntityDataManager.<Boolean>createKey(EntityAdventurer.class, DataSerializers.BOOLEAN);
	protected static final DataParameter<Byte> CLIMBING = EntityDataManager.<Byte>createKey(EntityAdventurer.class, DataSerializers.BYTE);
	
	@Override
	protected void entityInit()
	{
		super.entityInit();
        this.getDataManager().register(CLIMBING, Byte.valueOf((byte)0));
        this.getDataManager().register(IS_DRINKING, Boolean.valueOf(false));
	}
	
	public ResourceLocation getSkin()
	{
		return this.banditSkin;
	}
	
	@Override
	public EnumCreatureAttribute getCreatureAttribute()
	{
	    return EnumCreatureAttribute.ILLAGER;
	}
	
	@Override
    public void setSwingingArms(boolean swingingArms)
    {

    }
	
	@Override
	public double getYOffset()
	{
	    return -0.5D;
	}
	
    @Override
	public boolean hasHome()
	{
		return false;
	}
    
    protected int decreaseAirSupply(int air)
    {
        return air;
    }
	
	public double getRenderSizeXZ()
	{
		return this.renderSizeXZ;
	}
	
	
	public double getRenderSizeY()
	{
		return this.renderSizeY;
	}
	
	public boolean getTame()
	{
		return this.bribed;
	}
	
	@Override
	public boolean canBeHitWithPotion()
    {
        return potionImmunity <= 0;
    }
	
	@Override
	public float getEyeHeight()
	{
	    return 1.9F;
	}
	
	@Override
	protected boolean canDespawn()
	{
		return this.despawn;
	}
	
	@Override
	public int getHorizontalFaceSpeed()
	{
		return 10;
	}
	
	public int fleeModifier = rand.nextInt((int)this.getMaxHealth())+1;
    public boolean useHealingPotion = false;

	public SoundCategory getSoundCategory()
	{
		return SoundCategory.HOSTILE;
	}

	@Override
	public void setAttackTarget(EntityLivingBase e)
	{
		if ( !(e instanceof EntityAdventurer) )
		{
			super.setAttackTarget(e);
		}
	}

	protected SoundEvent getSwimSound()
	{
		return SoundEvents.ENTITY_HOSTILE_SWIM;
	}

	protected SoundEvent getSplashSound()
	{
		return SoundEvents.ENTITY_HOSTILE_SPLASH;
	}

	protected SoundEvent getHurtSound()
	{
		return SoundEvents.ENTITY_HOSTILE_HURT;
	}

	protected SoundEvent getFallSound(int heightIn)
	{
		return heightIn > 4 ? SoundEvents.ENTITY_HOSTILE_BIG_FALL : SoundEvents.ENTITY_HOSTILE_SMALL_FALL;
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
	
	protected void callForHelp(EntityLivingBase attacker)
	{
		List<EntityAdventurer> help = world.getEntitiesWithinAABB(EntityAdventurer.class, new AxisAlignedBB(getPosition()).grow(20, 10, 20), new Predicate<EntityAdventurer>()
		{
			public boolean apply(@Nullable EntityAdventurer entity)
			{
				return true;
			}
		});

		for ( EntityAdventurer entity : help )
		{
			if ( entity.getAttackTarget() == null )
			{
				entity.setAttackTarget(attacker);
			}
		}
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
	
	//===================================================== Chat =======================================================

	public void chat( EntityPlayer player, String message, @Nullable String extra )
	{
		if ( ToroQuestConfiguration.guardsHaveDialogue )
		{
			if ( this.getDistance(player) > 12 ) return;
			
			this.getLookHelper().setLookPositionWithEntity(player, 20.0F, 20.0F);
			this.faceEntity(player, 20.0F, 20.0F);
			
			if ( player.world.isRemote )
			{
				return;
			}
			
			try
			{
				String s = "§l" + this.getChatName() + "§r: " + (I18n.format("entity.toroquest.bandit."+message+rand.nextInt(Integer.parseInt(I18n.format("entity.toroquest.bandit."+message)))).replace("@p", player.getName()));
				
				if ( extra != null )
				{
					s.replace("@e", extra);
				}
				
				player.sendMessage(new TextComponentString(s));
			}
			catch ( Exception e )
			{
				
			}
			
			this.playSound( SoundEvents.VINDICATION_ILLAGER_AMBIENT, 1.0F, 0.9F + rand.nextFloat()/5.0F );
		}
	}
	
	//===================================================== Interact =======================================================
	
	@Override
	public boolean processInteract( EntityPlayer player, EnumHand hand )
	{
		if ( player == null || hand == null || !this.isEntityAlive() || player.isInvisible() )
    	{
			return false;
    	}
		
		ItemStack itemstack = player.getHeldItem(hand);
		Item item = itemstack.getItem();
		
		if ( !this.world.isRemote && !this.getTame() && this.getHealth() >= this.getMaxHealth() && !this.inCombat && this.getAttackTarget() == null && itemstack.getItem() == Items.EMERALD )
        {
			itemstack.shrink(1);
        	this.getLookHelper().setLookPositionWithEntity(player, 20.0F, 20.0F);
        	this.faceEntity(player, 20.0F, 20.0F);
        	
        	player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, SoundCategory.AMBIENT, 1.0F, 1.1F);
            {
	            {
	                this.getNavigator().clearPath();
	
	                if ( rand.nextInt(3) == 0 )
	                {
		            	player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.VINDICATION_ILLAGER_AMBIENT, SoundCategory.AMBIENT, 1.0F, 1.1F);
	                	this.setTame();
	        			this.world.setEntityState(this, (byte)7);
	                }
	                else
	                {
	                	this.playTameEffect(false);
	        			this.world.setEntityState(this, (byte)6);
	                }
	            }
            }
            return true;
        }
		else if ( !this.inCombat && this.getAttackTarget() == null && ToroQuestConfiguration.recruitBandits && item.equals(Item.getByNameOrId("toroquest:recruitment_papers")) )
        {
			if ( this.getTame() )
			{
	        	if ( !world.isRemote )
	        	{
		        	playSound(SoundEvents.ENTITY_ILLAGER_CAST_SPELL, 1.5F, 1.5F);
		        	playSound(SoundEvents.BLOCK_ANVIL_USE, 1.0F, 1.0F);
		        	player.setHeldItem(hand, new ItemStack(item, itemstack.getCount()-1 ));
					EntityGuard newEntity = new EntityGuard(world);
					newEntity.setPosition(this.posX, this.posY, this.posZ);
					this.setDead();
					newEntity.setPlayerGuard(player.getName());
					newEntity.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(this.getPosition())), (IEntityLivingData) null);
					newEntity.actionTimer = 1;

					world.spawnEntity(newEntity);
					newEntity.playTameEffect(false);
        			newEntity.world.setEntityState(newEntity, (byte)6);
					newEntity.setMeleeWeapon();
					
					Province province = CivilizationUtil.getProvinceAt(player.getEntityWorld(), player.chunkCoordX, player.chunkCoordZ);
					
					if ( province != null && province.getCiv() != null )
					{
						CivilizationHandlers.adjustPlayerRep(player, province.getCiv(), ToroQuestConfiguration.recruitGuardRepGain);
						try
						{
							QuestRecruit.INSTANCE.onRecruit(player, province);
						}
						catch ( Exception e )
						{
							
						}
						this.chat(player, "civbanditrecruit", province.getCiv().getDisplayName(player));
					}
					else
					{
						newEntity.chat(newEntity, player, "nocivbanditrecruit", I18n.format("civilization.null.name"));
					}
		    	}
			}
			else if ( this.canTalk )
	    	{
	    		this.canTalk = false;
	    		this.chat(player, "moreemeralds", null);
	        }
	    }
		return false;
	}
	
	//===================================================== Potion =======================================================
	
	public void throwSplashPotion(EntityLivingBase target)
    {
        //if ( !this.isDrinkingPotion() )
        {
            double d0 = target.posY;
            double d1 = target.posX + target.motionX - this.posX;
            double d2 = d0 - this.posY;
            double d3 = target.posZ + target.motionZ - this.posZ;
            float f = MathHelper.sqrt(d1 * d1 + d3 * d3);
            PotionType potiontype = PotionTypes.STRONG_HARMING;

            if ( rand.nextInt(3) == 0 && !target.isPotionActive(MobEffects.SLOWNESS) )
            {
                potiontype = PotionTypes.SLOWNESS;
            }
            else if (rand.nextInt(4) == 0 && target.getHealth() >= 8.0F && !target.isPotionActive(MobEffects.POISON))
            {
                potiontype = PotionTypes.POISON;
                if ( rand.nextInt(3) == 0 ) potiontype = PotionTypes.STRONG_POISON;
            }
            else if ( rand.nextInt(4) == 0 && !target.isPotionActive(MobEffects.WEAKNESS) )
            {
                potiontype = PotionTypes.WEAKNESS;
            }

            EntityPotion entitypotion = new EntityPotion(this.world, this, PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), potiontype));
            entitypotion.rotationPitch -= -20.0F;
            entitypotion.shoot(d1, d2 + (double)(f * 0.2F), d3, 0.75F, 8.0F);
            this.world.playSound((EntityPlayer)null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_SPLASH_POTION_THROW, this.getSoundCategory(), 1.0F, 0.8F + this.rand.nextFloat() * 0.4F);
            this.world.spawnEntity(entitypotion);
        }
    }
	
	@Override
    protected float applyPotionDamageCalculations(DamageSource source, float damage)
    {
        damage = super.applyPotionDamageCalculations(source, damage);

        if ( source.getTrueSource() == this || source.getTrueSource() instanceof EntityAdventurer )
        {
            damage = 0.0F;
        }
        return damage;
    }
	
    public void setDrinkingPotion(boolean drinkingPotion)
    {
        this.getDataManager().set(IS_DRINKING, Boolean.valueOf(drinkingPotion));
    }

    public boolean isDrinkingPotion()
    {
        return ((Boolean)this.getDataManager().get(IS_DRINKING)).booleanValue();
    }
	
	//===================================================== Drop Loot =======================================================
	
	public void dropLoot()
	{
		if (!this.world.isRemote)
		{
			if ( ToroQuestConfiguration.banditsDropEmeralds && rand.nextInt(3) == 0 )
			{
				ItemStack stack = new ItemStack(Items.EMERALD, rand.nextInt(3)+1);
				EntityItem dropItem = new EntityItem(world, posX, posY, posZ, stack.copy());
				world.spawnEntity(dropItem);
			}
			if ( ToroQuestConfiguration.banditsDropPotions > 0 )
			{
				if ( this.getHeldItemOffhand().getItem() instanceof ItemPotion && rand.nextBoolean() )
				{
					ItemStack stack = new ItemStack(this.getHeldItemOffhand().getItem(), 1);
					EntityItem dropItem = new EntityItem(world, posX, posY, posZ, stack.copy());
					world.spawnEntity(dropItem);
					this.setHeldItem(EnumHand.OFF_HAND, ItemStack.EMPTY);
				}
				else if ( rand.nextInt(ToroQuestConfiguration.banditsDropPotions) == 0 )
				{
					if ( rand.nextInt(5) == 0 )
					{
						ItemStack stack = PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), PotionTypes.POISON);
						EntityItem dropItem = new EntityItem(world, posX, posY, posZ, stack.copy());
						world.spawnEntity(dropItem);
					}
					else if ( rand.nextInt(4) == 0 )
					{
						ItemStack stack = PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), PotionTypes.HARMING);
						EntityItem dropItem = new EntityItem(world, posX, posY, posZ, stack.copy());
						world.spawnEntity(dropItem);
					}
					else if ( rand.nextInt(3) == 0 )
					{
						ItemStack stack = PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), PotionTypes.SLOWNESS);
						EntityItem dropItem = new EntityItem(world, posX, posY, posZ, stack.copy());
						world.spawnEntity(dropItem);
					}
					else if ( rand.nextBoolean() )
					{
						ItemStack stack = PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), PotionTypes.WEAKNESS);
						EntityItem dropItem = new EntityItem(world, posX, posY, posZ, stack.copy());
						world.spawnEntity(dropItem);
					}
					else
					{
						ItemStack stack = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.HEALING);
						EntityItem dropItem = new EntityItem(world, posX, posY, posZ, stack.copy());
						world.spawnEntity(dropItem);
					}
				}
			}
		}
	}
	
	//===================================================== Attributes =======================================================

	@Override
    protected void applyEntityAttributes()
    {
		super.applyEntityAttributes();
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
		
    	this.setLeftHanded(false);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
	    this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D);
    	this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(ToroQuestConfiguration.banditArmor);
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(40.0D);
    	this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.395D+rand.nextDouble()/50.0D);
    	this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.25D);
    }
	
	//===================================================== Combat Task =======================================================
	
	public void setCombatTask()
	{
	    this.aiArrowAttack.setAttackCooldown(40);
		this.tasks.addTask(4, new AIAttackWithSword(this, 0.65D));
		this.tasks.addTask(5, this.aiArrowAttack);    
		
	    this.inCombat = false;
		this.blocking = false;
	    this.setSprinting(false);
		this.blockingTimer = 0;
		this.setAttackTarget(null);
		this.canShieldPush = true;
		this.resetActiveHand();
		this.setActiveHand(EnumHand.MAIN_HAND);
		this.activeItemStackUseCount = 0;
		this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.25D);
		this.strafeVer = 0.0F;
		this.strafeHor = 0.0F;
		this.getMoveHelper().strafe( 0.0F, 0.0F );
		this.getNavigator().clearPath();
	}
	
	//===================================================== Living Update =======================================================

	public int greetingTimer = 0; // 0 is ready, -1 is not ready
	
	public void greetPlayer()
	{
		this.greetingTimer = 12;
	}
	
	protected int lastTargetY = 0;

	@Override
	public void onLivingUpdate()
	{		
		this.updateArmSwingProgress();
		super.onLivingUpdate();
		
		if ( this.world.isRemote )
		{
			return;
		}
		
		if ( this.greetingTimer >= 0 && this.getAttackTarget() == null )
		{
			if ( this.greetingTimer == 0 )
			{
				List<EntityPlayer> nearbyPlayers = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(this.getPosition()).grow(6, 3, 6));
				if ( !nearbyPlayers.isEmpty() && this.canEntityBeSeen(nearbyPlayers.get(0)) )
				{
					if ( rand.nextInt(20) == 0 )
					{
						if ( this.rand.nextInt(3) == 0 )
						{
							this.greetingTimer = -1;
						}
						else
						{
							EntityPlayer p = (nearbyPlayers.get(0));

							if ( this.rand.nextInt(3) == 0 ) this.chat(p, "hello", null);
							
							this.greetPlayer();
							this.getNavigator().clearPath();
							
							this.getNavigator().tryMoveToXYZ(((p.posX - this.posX)/2.0+this.posX),p.posY,((p.posZ - this.posZ)/2.0+this.posZ), 0.5D);
							
							this.faceEntity(p, 0.20F, 0.20F);
							this.getLookHelper().setLookPositionWithEntity(p, 0.20F, 0.20F);
						}
					}
				}
			}
			else if ( this.greetingTimer > 0 )
			{
				List<EntityPlayer> nearbyPlayers = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(this.getPosition()).grow(6, 3, 6));
				if ( !nearbyPlayers.isEmpty() && this.canEntityBeSeen(nearbyPlayers.get(0)) )
				{
					EntityPlayer p = (nearbyPlayers.get(0));
					this.getNavigator().clearPath();
					this.faceEntity(p, 0.20F, 0.20F);
					this.getLookHelper().setLookPositionWithEntity(p, 0.20F, 0.20F);
				}
				
				if ( this.greetingTimer == 12 && this.rand.nextBoolean() )
				{
					this.greetingTimer = 0;
					this.addVelocity(0.005D, 0.3D, -0.005D);
				}
				else
				{
					if ( greetingTimer > 8 )
					{
		    			this.setSneaking(true);
					}
					else if ( greetingTimer == 10 && this.rand.nextInt(3) == 0 )
					{
		    			this.setSneaking(false);
						this.swingArm(EnumHand.MAIN_HAND);
					}
					else if ( greetingTimer < 4 )
					{
		    			this.setSneaking(true);
					}
					else if ( greetingTimer == 1 && this.rand.nextInt(3) == 0 )
					{
						this.greetingTimer = 8;
		    			this.setSneaking(false);
						this.swingArm(EnumHand.MAIN_HAND);
					}
					else
					{
		    			this.setSneaking(false);
					}
				}
				this.greetingTimer--;
				if ( this.greetingTimer == 0 )
				{
					this.greetingTimer = -1;
				}
			}
		}
		else
		{
			this.setSneaking(false);
		}
		
       	if ( this.ticksExisted % 100 == 0 )
    	{
       		if ( this.getHealth() >= this.getMaxHealth() )
			{
       			if ( this.getAttackTarget() == null && rand.nextInt(3) == 0 )
       			{
	       			List<EntityVillager> villagers = this.world.getEntitiesWithinAABB(EntityVillager.class, new AxisAlignedBB(this.getPosition()).grow(6, 4, 6));
	       			Collections.shuffle(villagers);
	       			for ( EntityVillager p : villagers )
	       			{
	       				if ( !p.isTrading() )
	       				{
		       				this.getNavigator().clearPath();
							this.getNavigator().tryMoveToXYZ(((p.posX - this.posX)/3.0+this.posX),p.posY,((p.posZ - this.posZ)/3.0+this.posZ), 0.5D);
							this.faceEntity(p, 0.20F, 0.20F);
							this.getLookHelper().setLookPositionWithEntity(p, 0.20F, 0.20F);
							
							p.getNavigator().clearPath();
							p.getNavigator().tryMoveToXYZ(((this.posX - p.posX)/3.0+p.posX),this.posY,((this.posZ - p.posZ)/3.0+p.posZ), 0.5D);
							p.faceEntity(this, 0.20F, 0.20F);
							p.getLookHelper().setLookPositionWithEntity(this, 0.20F, 0.20F);
	       				}
						break;
	       			}
       			}
			}
       		else
       		{
       			this.heal(1.0f);
       		}
       		
    		if ( this.isInWater() && this.getAttackTarget() == null )
       		{
       			this.swingArm(EnumHand.MAIN_HAND);
       			EntityBoat boat = new EntityBoat(this.world);
				boat.setPosition(this.posX,this.posY+0.5D,this.posZ);
       			this.world.spawnEntity(boat);
       			this.startRiding(boat);
       		}
       		else if ( this.isRiding() )
       		{
       			if ( this.getRidingEntity() instanceof EntityBoat ) this.getRidingEntity().setDead();
       		}
       		
       		if ( this.greetingTimer < 0 && rand.nextInt(16) == 0 )
       		{
       			this.greetingTimer = 0;
       		}
    		
       		BlockPos pos = this.getPosition();
			IBlockState block = world.getBlockState(pos);
			if ( block instanceof BlockLiquid )
			{
		        Vec3d vec3d = RandomPositionGenerator.getLandPos(this, 16, 8);
		        if ( vec3d != null )
		        {
		        	this.getNavigator().tryMoveToXYZ(vec3d.x, vec3d.y, vec3d.z, 0.5);
		        }
			}
			    		
    		if ( !this.inCombat )
    		{
    	        this.setSprinting(false);
    			ItemStack iStack = this.getHeldItemMainhand();
    			
    			if ( this.getAttackTarget() == null )
    			{
	    			if ( this.lastTargetY < 4 && iStack != null && (iStack.getItem() instanceof ItemBow) )
					{
						this.resetActiveHand();
			        	this.playSound(SoundEvents.ITEM_ARMOR_EQUIP_IRON, 1.0F, 0.8F + rand.nextFloat()/5.0F );
			        	if ( !this.world.isRemote )
						{
			        		this.equipMeleeWeapon();
			        		if ( this.world.canSeeSky(this.getPosition()) && this.world.getWorldTime() >= 12500 && this.world.getWorldTime() <= 23500 )
			        		{
			        			this.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Blocks.TORCH, 1));
			        		}
						}
						this.blockingTimer = 0;
					}
	    			else if ( this.world.canSeeSky(this.getPosition()) && !(iStack.getItem() instanceof ItemBow) && this.world.getWorldTime() >= 12500 && this.world.getWorldTime() <= 23500 )
	        		{
	    				if ( !(iStack.getItem() == Item.getItemFromBlock(Blocks.TORCH)) )
	    				{
							this.resetActiveHand();
				        	this.playSound(SoundEvents.ITEM_ARMOR_EQUIP_IRON, 1.0F, 0.8F + rand.nextFloat()/5.0F );
				        	if ( !this.world.isRemote )
				        	{
				        		this.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Blocks.TORCH, 1));
				        	}
							this.blockingTimer = 0;
	    				}
	        		}
	    			else if ( iStack.getItem() == Item.getItemFromBlock(Blocks.TORCH) )
	    			{
						this.resetActiveHand();
			        	this.playSound(SoundEvents.ITEM_ARMOR_EQUIP_IRON, 1.0F, 0.8F + rand.nextFloat()/5.0F );
			        	if ( !this.world.isRemote )
			        	{
			        		this.equipMeleeWeapon();
			        	}
	    			}
    				this.canShieldPush = true;
    			}
	    		
//	    		if ( this.underAttackTimer < 1 && rand.nextInt(5) == 0 && this.raidX != null && this.raidY != null && this.raidZ != null && this.murderTimer <= 0 && !this.isAnnoyed() ) 
//	    		{
//	    			BlockPos pos = new BlockPos( this.raidX, this.raidY, this.raidZ );
//	        		if ( this.getNavigator().tryMoveToXYZ(pos.getX()+0.5, pos.getY(), pos.getZ()+0.5, 0.45D) )
//	        		{
//	        			this.returnToPost = true;
//	        		}
//	        		else if ( this.returnToPost(0) )
//	        		{
//	        			this.returnToPost = true;
//	        		}
//	        		else if ( this.returnToPost(25) )
//        			{
//	        			this.returnToPost = true;
//        			}
//	    		}
    		}
    		else if ( this.getAttackTarget() != null )
    		{
	    		this.callForHelp( this.getAttackTarget() );
    		}
    	}
       	
       	// Player shoots arrow near
    	List<EntityArrow> arrowsNear = this.world.getEntitiesWithinAABB(EntityArrow.class, new AxisAlignedBB(this.getPosition()).grow(6, 4, 6), new Predicate<EntityArrow>()
		{
			public boolean apply(@Nullable EntityArrow entity)
			{
				if ( entity.lastTickPosX == 0 && entity.shootingEntity instanceof EntityPlayer && entity.shootingEntity != getAttackTarget() )
				{
					return true;
				}
				return false;
			}
		});
		
		if ( !arrowsNear.isEmpty() )
		{
			this.setAttackTarget( (EntityPlayer) arrowsNear.get(0).shootingEntity );
		}
		
		if ( this.getAttackTarget() != null && this.getAttackTarget().isEntityAlive() && !(this.getAttackTarget() instanceof EntityToroNpc) && !(this.getAttackTarget() instanceof EntityVillager) && ( !(this.getAttackTarget() instanceof EntityGolem) || this.getAttackTarget() instanceof EntityConstruct ) )
		{
			List<EntityArrow> arrows = this.world.getEntitiesWithinAABB(EntityArrow.class, new AxisAlignedBB(this.getPosition()).grow(8, 8, 8), new Predicate<EntityArrow>()
			{
				public boolean apply(@Nullable EntityArrow entity)
				{
					if ( entity.lastTickPosX == 0 && entity.shootingEntity == getAttackTarget() )
					{
						return true;
					}
					return false;
				}
			});
			
	        double dist = this.getDistanceSq(this.getAttackTarget());
			
			if ( !arrows.isEmpty() )
			{
				this.stance = rand.nextInt(6)+5;
				if ( dist <= 12 )
				{
					this.blockingTimer = 25;
				}
				else
				{
					this.blockingTimer = 50;
				}
				this.blocking = true;
				this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
	        	this.canShieldPush = true;
				this.resetActiveHand();
				this.setActiveHand(EnumHand.OFF_HAND);
				this.updateActiveHand();
				this.strafeHor = getStrafe(stance);
				this.strafeVer = 0.4F;
			}
			
	        this.lastTargetY = (int)(Math.abs(this.posY - this.getAttackTarget().posY)+0.5D);
	        ItemStack iStack = this.getHeldItemMainhand();
	        if ( !this.inCombat )
	        {
				this.getMoveHelper().strafe( 0.0F, 0.0F );
	        	this.getNavigator().clearPath();
	        	this.canShieldPush = true;
				this.resetActiveHand();
				this.inCombat = true;
				this.stance = rand.nextInt(6)+5;
				this.strafeHor = getStrafe(stance);
	        	if ( this.onGround && this.getNavigator().getPathToEntityLiving(this.getAttackTarget()) == null )
				{
	        		this.blockingTimer = -200;
	        	}
	        	else
				{
					this.getMoveHelper().strafe( 0.0F, 0.0F );
					this.getNavigator().clearPath();
				}
	        }
	        // if within range and has not been in melee range for a short amount of time, or very close and has not been in melee range for a long amount of time
			if (  ( ( dist < 200+this.blockingTimer ) || ( this.lastTargetY < 4 && dist <= 20 && this.canEntityBeSeen(this.getAttackTarget())) ) )
	        {
				// if this does not have a sword, swap to sword and board
				if ( iStack != null && ( iStack.getItem() instanceof ItemBow || iStack.getItem() == Item.getItemFromBlock(Blocks.TORCH) ) ) // SSS
				{
		        	this.canShieldPush = true;
					this.resetActiveHand();
					this.playSound(SoundEvents.ITEM_ARMOR_EQUIP_IRON, 1.0F, 0.9F + rand.nextFloat()/10.0F );
					this.getMoveHelper().strafe( 0.0F, 0.0F );
	            	this.getNavigator().clearPath();
	            	if ( !this.world.isRemote )
					{
	            		this.equipMeleeWeapon();
					}
					this.blockingTimer = 0;
					this.strafeHor = getStrafe(stance);
				}
				
				// if this is not blocking, is within range, and block is ready, start blocking
				if ( !this.blocking && dist <= 12 && this.blockingTimer <= -((int)(this.stance*5+dist+20)) && this.getRevengeTarget() != null && this.getRevengeTarget().isEntityAlive() )
				{
					this.stance = rand.nextInt(8)+3;
					this.strafeHor = getStrafe(stance);
					this.blockingTimer = (int)MathHelper.clamp((rand.nextInt(70)+20-dist), 20, 80);
					this.blocking = true;
					this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
		        	this.canShieldPush = true;
					this.resetActiveHand();
					this.setActiveHand(EnumHand.OFF_HAND);
					this.updateActiveHand();
					if ( dist <= 6 )
					{
						this.strafeVer = 0.2F;
					}
					else
					{
						this.strafeVer = 0.4F;
					}
				}
				else if ( this.blocking && this.blockingTimer % 16 == 0 )
				{
		        	this.canShieldPush = true;

					if ( dist <= 3 )
					{
						this.strafeVer = 0.2F;
					}
					else
					{
						this.strafeVer = 0.4F;
					}
				}
				
				// if this is blocking and should no longer block, stop blocking
				if ( this.blocking && this.blockingTimer <= 0 )
				{
					this.blocking = false;
					this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.25D);
					this.stance = rand.nextInt(8)+3;
					this.strafeHor = getStrafe(stance);
		        	this.canShieldPush = true;
					this.resetActiveHand();
				}
				// otherwise, if this is in melee range, strafe
				else if ( !this.blocking && dist <= 64 )
				{
					if ( this.blockingTimer == -12 || this.blockingTimer == -32 || ( this.blockingTimer < -32 && this.blockingTimer % 14 == 0 ) )
					{
						if ( rand.nextInt(3) == 0 )
						{
							this.stance = rand.nextInt(8)+3;
							this.strafeHor = getStrafe(stance);
						}
					}
				}
				
				if ( !this.blocking )
				{
					float strafeMod = 1.0F;
					
					if ( this.stance < 5 )
					{
						this.strafeVer = 0.4F;

						if ( dist <= 25 )
						{
							if ( !this.world.isRemote && this.onGround )
							{
								Vec3d velocityVector = new Vec3d(this.posX - this.getAttackTarget().posX, 0, this.posZ - this.getAttackTarget().posZ);
								double push = (12.0D+dist*dist);
								this.addVelocity((velocityVector.x)/push, 0.0D, (velocityVector.z)/push);
			                	this.velocityChanged = true;
							}
						}
						else
						{
							this.stance = rand.nextInt(6)+5;
							this.strafeHor = getStrafe(stance);
						}
					}
					else if ( dist <= 2.5 )
					{
						this.strafeVer = 0.4F;
					}
					else if ( dist <= 7 )
					{
						this.strafeVer = 0.7F;
						strafeMod = 0.9F;
					}
					else if ( dist <= 12 )
					{
						this.strafeVer = 0.8F;
						strafeMod = 0.8F;
					}
					else
					{
						this.strafeVer = 0.9F;
						strafeMod = 0.7F;
					}
								
					if ( this.getNavigator().tryMoveToEntityLiving(this.getAttackTarget(), this.strafeVer) )
					{						
						if ( dist >= 12 ) // if this is too far away and blocking, stop blocking faster
						{
							this.blockingTimer--;
						}
						else if ( dist <= 3 )
						{
							if ( !this.world.isRemote && this.onGround && !this.isSprinting() )
							{
								Vec3d velocityVector = new Vec3d(this.posX - this.getAttackTarget().posX, 0, this.posZ - this.getAttackTarget().posZ);
								double push = (1.0D+dist*dist);
								this.addVelocity((velocityVector.x)/push, 0.0D, (velocityVector.z)/push);
			                	this.velocityChanged = true;
							}
						}
						
						if ( this.posY + 1.5D < this.getAttackTarget().posY )
						{
							this.getMoveHelper().strafe( this.strafeVer, 0.0F );
						}
						else
						{
							this.getMoveHelper().strafe( this.strafeVer, this.strafeHor*strafeMod );
						}
					}
					else
					{
						if ( this.posY + 1.5D < this.getAttackTarget().posY )
						{
							this.getMoveHelper().strafe( this.strafeVer*0.5F, 0.0F );
						}
						else
						{
							this.getMoveHelper().strafe( this.strafeVer*0.5F, this.strafeHor*0.5F*strafeMod );
						}
					}
				}
				else // is blocking
				{
					if ( this.strafeVer < 0.4F )
					{
						if ( !this.world.isRemote && this.onGround )
						{
							Vec3d velocityVector = new Vec3d(this.posX - this.getAttackTarget().posX, 0, this.posZ - this.getAttackTarget().posZ);
							double push = (1.0D+dist*dist);
							this.addVelocity((velocityVector.x)/push, 0.0D, (velocityVector.z)/push);
		                	this.velocityChanged = true;
						}
					}
					else if ( this.strafeVer > 0.4F )
					{
						this.strafeVer = 0.4F;
					}
					
					if ( this.getNavigator().tryMoveToEntityLiving(this.getAttackTarget(), this.strafeVer) )
					{
						this.getMoveHelper().strafe( this.strafeVer, this.strafeHor*1.25F);
					}
					else
					{
						this.getMoveHelper().strafe( this.strafeVer*0.5F, this.strafeHor*0.5F );
					}
				}
				
	        }
			else if ( iStack != null && !(iStack.getItem() instanceof ItemBow) )
			{
				if ( ToroQuestConfiguration.mobsAttackGuards && this.getAttackTarget() instanceof EntityLiving )
				{
					EntityLiving v = (EntityLiving)this.getAttackTarget();
					if ( v.getAttackTarget() == null )
					{
						v.setAttackTarget(this);
					}
				}
				else if ( this.getAttackTarget() instanceof EntityPlayer )
				{
					this.enemy = ((EntityPlayer)this.getAttackTarget());
				}
				this.blocking = false;
				this.blockingTimer = -200;
		    	this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.25D);
	        	this.canShieldPush = true;
				this.resetActiveHand();
				this.playSound(SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 1.0F, 0.9F + rand.nextFloat()/5.0F );
				
				if ( !this.world.isRemote )
				{
					this.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Items.BOW, 1));
					this.setHeldItem(EnumHand.OFF_HAND, ItemStack.EMPTY);
				}
				
				this.strafeVer = 0.0F;
				this.strafeHor = 0.0F;
		    	this.getMoveHelper().strafe( 0.0F, 0.0F );
		    	this.getNavigator().clearPath();
			}
			this.blockingTimer--;
		}
		else if ( this.blocking || this.inCombat ) // end of combat
		{
			this.inCombat = false;
			this.blocking = false;
//			if (this.raidX != null && this.raidZ != null && this.getDistance(this.raidX, this.posY, this.raidZ) > 20)
//	    	{
//	    		if ( returnToPost(0) )
//	    		{
//	    			this.returnToPost = true;
//	    		}
//	    		else
//	    		{
//		    		if ( returnToPost(20) )
//		    		{
//		    			this.returnToPost = true;
//		    		}
//	    		}
//	    	}
			this.setAttackTarget(null);
			// this.searchNextEnemy = true;
        	this.canShieldPush = true;
			this.resetActiveHand();
			this.activeItemStackUseCount = 0;
	    	this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.25D);
	    	this.strafeHor = 0F;
	    	this.strafeHor = 0F;
	    	this.getMoveHelper().strafe( 0.0F, 0.0F );
	    	this.getNavigator().clearPath();
		}
		
		if ( this.getAttackTarget() != null )
    	{
    		this.faceEntity(this.getAttackTarget(), 20.0F, 20.0F);
    		this.getLookHelper().setLookPositionWithEntity(this.getAttackTarget(), 20.0F, 20.0F);
    		this.prevRotationPitch = 0;
    		this.prevRotationYaw = 0;
    		this.newPosRotationIncrements = 0;
    	}
		
	}
	
	//==================================================== Take Damage ===========================================================
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		if (this.world.isRemote)
        {
            return false;
        }

		Entity e = source.getTrueSource();
		
		if (source.getTrueSource() == this)
        {
            amount = 0.0F;
        }
		
		if ( source == DamageSource.IN_WALL || source == DamageSource.CRAMMING ) 
		{
			return false;
		}
		
		if ( source == DamageSource.FALL )
		{
			amount = amount/6.0F;
			if ( amount <= 1 )
			{
				return false;
			}
			else
			{
				super.attackEntityFrom(source, amount);
			}
		}
		
		if ( this.onGround && this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).getBaseValue() == 2.22 );
		{
	    	this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.25D);
		}
		
		if ( e == null )
		{
			if ( this.rand.nextBoolean() )
			{
				BlockPos pos = this.getPosition();
				IBlockState block = world.getBlockState(pos);
				if ( block == Blocks.LAVA.getDefaultState() || block.getBlock() == Blocks.LAVA )
				{
					if ( this.dimension == 0 )
					{
						this.swingArm(EnumHand.MAIN_HAND);
						//if ( this.world.isRemote )
						{
							this.addVelocity(0, 0.25, 0);
						}
						this.world.setBlockState(pos, Blocks.WATER.getDefaultState());
					}
			        Vec3d vec3d = RandomPositionGenerator.getLandPos(this, 8, 4);
		            if ( vec3d != null )
		            {
				        this.getNavigator().tryMoveToXYZ(vec3d.x, vec3d.y, vec3d.z, 0.5D);
		            }
				}
				else if ( block.getBlock() instanceof BlockFire )
				{
					this.swingArm(EnumHand.MAIN_HAND);
					this.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 1.0F, 1.0F);
					world.setBlockToAir(pos);
				}
				else if ( this.rand.nextBoolean() && !this.getNavigator().noPath() )
				{
					this.extinguish();
				}
				else
				{
					Vec3d vec3d = RandomPositionGenerator.getLandPos(this, 8, 4);
		            if ( vec3d != null )
		            {
				        this.getNavigator().tryMoveToXYZ(vec3d.x, vec3d.y, vec3d.z, 0.5D);
		            }
				}
			}
			return super.attackEntityFrom(source, amount);
		}
		
		if ( e instanceof EntityAdventurer ) 
		{
			return false;
		}
		
//		if ( e instanceof EntityGuard && !(((EntityGuard)e).getAttackTarget() != this) )
//		{
//			return false;
//		}
		
		if ( e instanceof EntityLivingBase ) 
		{
			if ( rand.nextInt(3) == 0 )
			{
				this.setAttackTarget((EntityLivingBase)e);
			}
			this.setRevengeTarget((EntityLivingBase)e);
			this.callForHelp((EntityLivingBase)e);
			
			if ( e instanceof EntityPlayer ) 
			{
				this.enemy = (EntityPlayer)e;
			}
		}
		
		if ( this.blocking && canBlockDamageSource(source) )
		{
			
			if ( this.blockingTimer > 10 && rand.nextBoolean() )
			{
				this.blockingTimer = 10;
			}
			
			double dist = e.getDistanceSq(this);
			
			if ( !source.isProjectile() && !source.isMagicDamage() && !source.isFireDamage() )
			{
				if ( e instanceof EntityLivingBase )
				{
					if ( amount >= 5.0F && ( ((EntityLivingBase)e).getHeldItemMainhand().getItem() instanceof ItemAxe || ((EntityLivingBase)e).getHeldItemMainhand().getItem().getRegistryName().toString().contains("halberd") || ((EntityLivingBase)e).getHeldItemMainhand().getItem().getRegistryName().toString().contains("battleaxe") ) )
					{
						this.resetActiveHand();
						if ( this.world.isRemote ) 
						{
							this.handleStatusUpdate((byte)29);
							this.handleStatusUpdate((byte)30);
						}
						if ( dist < 16 )
						{
							this.canShieldPush = true;
							Vec3d velocityVector = new Vec3d(this.posX - e.posX, 0, this.posZ - e.posZ);
							if ( !this.world.isRemote )
							{	
								this.addVelocity((velocityVector.x)/( dist+1 )*MathHelper.clamp(amount, 0.0D, 1.2D), (0.22D-MathHelper.clamp(dist/100.0, 0.0D, 0.16D))*MathHelper.clamp(amount, 0.0D, 1.0D), (velocityVector.z)/( dist+1 )*MathHelper.clamp(amount, 0.0D, 1.2D));
			                	this.velocityChanged = true;
							}
						}
						this.playSound(SoundEvents.ITEM_SHIELD_BLOCK, 1.0F, 0.8F + rand.nextFloat()/5.0F);
						this.playSound(SoundEvents.ITEM_SHIELD_BREAK, 1.0F, 0.8F + rand.nextFloat()/5.0F);
						this.blockingTimer = 50;
						return (super.attackEntityFrom(source, amount/2.0F));
					}
					else
					{
						this.playSound(SoundEvents.ITEM_SHIELD_BLOCK, 1.0F, 0.8F + rand.nextFloat()/5.0F);
						if ( dist < 16 )
						{
							if ( this.canShieldPush )
							{
								this.canShieldPush = false;
								
								Vec3d velocityVector = new Vec3d(e.posX - this.posX, 0, e.posZ - this.posZ);
								//if ( !this.world.isRemote )
								{	
									e.addVelocity((velocityVector.x)/( dist+1 )*MathHelper.clamp(amount, 0.0D, 1.2D), (0.22D-MathHelper.clamp(dist/100.0, 0.0D, 0.16D))*MathHelper.clamp(amount, 0.0D, 1.0D), (velocityVector.z)/( dist+1 )*MathHelper.clamp(amount, 0.0D, 1.2D));
				                	e.velocityChanged = true;
								}
							}
							else
							{								
								Vec3d velocityVector = new Vec3d(e.posX - this.posX, 0, e.posZ - this.posZ);
								//if ( !this.world.isRemote )
								{	
									e.addVelocity((velocityVector.x)/( dist+8 )*MathHelper.clamp(amount, 0.0D, 1.0D), 0, (velocityVector.z)/( dist+8 )*MathHelper.clamp(amount, 0.0D, 1.0D));
				                	e.velocityChanged = true;
								}
							}
						}
		                this.handleStatusUpdate((byte)29);
					}
					return false;
				}
			}
			else if ( source.isProjectile() )
			{
				this.blockingTimer = 8;
				if ( this.world.isRemote ) 
				{
					this.handleStatusUpdate((byte)29);
				}
				this.playSound(SoundEvents.ITEM_SHIELD_BLOCK, 1.0F, 0.8F + rand.nextFloat()/5.0F);
				return false;
			}
			if ( this.world.isRemote ) 
			{
				this.handleStatusUpdate((byte)29);
			}
			this.playSound(SoundEvents.ITEM_SHIELD_BLOCK, 1.0F, 0.8F + rand.nextFloat()/5.0F);
			return false;
		}
		
		if (super.attackEntityFrom(source, amount))
		{
			if ( (!this.canEntityBeSeen(e) || this.getDistance(e) >= 20.0D) && this.getNavigator().getPathToEntityLiving(e) == null )
			{
				if ( e instanceof EntityLivingBase ) this.setAttackTarget((EntityLivingBase) e);
				Vec3d vec3d = RandomPositionGenerator.getLandPos(this, 16, 8);
				if ( vec3d != null )
				{
					this.getNavigator().tryMoveToXYZ(vec3d.x, vec3d.y, vec3d.z, 0.6D);
					this.setRaidLocation((int)vec3d.x, (int)vec3d.z);
				}
			}
			return true;
		}
		return false;
	}
	
	protected boolean canBlockDamageSource(DamageSource damageSourceIn)
	{
        if (!damageSourceIn.isUnblockable() && this.isActiveItemStackBlocking())
        {
            Vec3d vec3d = damageSourceIn.getDamageLocation();

            if (vec3d != null)
            {
                Vec3d vec3d1 = this.getLook(1.0F);
                Vec3d vec3d2 = vec3d.subtractReverse(new Vec3d(this.posX, this.posY, this.posZ)).normalize();
                vec3d2 = new Vec3d(vec3d2.x, 0.0D, vec3d2.z);

                if (vec3d2.dotProduct(vec3d1) < 0.0D)
                {
                    return true;
                }
            }
        }
        return false;
    }

	//===================================================== Status Update =======================================================

	@Override
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte id)
    {
        boolean flag = id == 33;
        boolean flag1 = id == 36;
        boolean flag2 = id == 37;
        
        if (id == 7)
        {
            this.playTameEffect(true);
			this.world.playSound(this.posX, this.posY, this.posZ, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, SoundCategory.AMBIENT, 1.0F, 1.2F, false);
        }
        else if (id == 6)
        {
            this.playTameEffect(false);
			this.world.playSound(this.posX, this.posY, this.posZ, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, SoundCategory.AMBIENT, 1.0F, 1.0F, false);
        }

        if (id != 2 && !flag && !flag1 && !flag2)
        {
            if (id == 3)
            {
                SoundEvent soundevent1 = this.getDeathSound();

                if (soundevent1 != null)
                {
                    this.playSound(soundevent1, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
                }

                this.setHealth(0.0F);
                this.onDeath(DamageSource.GENERIC);
            }
            else if (id == 30)
            {
                this.playSound(SoundEvents.ITEM_SHIELD_BREAK, 0.8F, 0.8F + this.world.rand.nextFloat() * 0.5F);
            }
            else if (id == 29)
            {
                this.playSound(SoundEvents.ITEM_SHIELD_BLOCK, 1.0F, 0.8F + this.world.rand.nextFloat() * 0.5F);
            }
            else
            {
                super.handleStatusUpdate(id);
            }
        }
        else
        {
            this.limbSwingAmount = 1.5F;
            this.hurtResistantTime = this.maxHurtResistantTime;
            this.maxHurtTime = 10;
            this.hurtTime = this.maxHurtTime;
            this.attackedAtYaw = 0.0F;

            if (flag)
            {
                this.playSound(SoundEvents.ENCHANT_THORNS_HIT, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            }

            DamageSource damagesource;

            if (flag2)
            {
                damagesource = DamageSource.ON_FIRE;
            }
            else if (flag1)
            {
                damagesource = DamageSource.DROWN;
            }
            else
            {
                damagesource = DamageSource.GENERIC;
            }

            SoundEvent soundevent = this.getHurtSound(damagesource);

            if (soundevent != null)
            {
                this.playSound(soundevent, this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            }

            this.attackEntityFrom(DamageSource.GENERIC, -1.0F);
        }
        super.handleStatusUpdate(id);
    }
	
	protected void playTameEffect(boolean play)
    {
        EnumParticleTypes enumparticletypes = EnumParticleTypes.HEART;

        if (!play)
        {
            enumparticletypes = EnumParticleTypes.SMOKE_NORMAL;
        }

        for (int i = 0; i < 7; ++i)
        {
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;
            this.world.spawnParticle(enumparticletypes, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2);
        }
    }
	
	public void onUpdate_OFF()
	{
		super.onUpdate();
		
		this.prevLimbSwingAmount = this.limbSwingAmount;
		double d0 = this.posX - this.prevPosX;
		double d1 = this.posZ - this.prevPosZ;
		float f = MathHelper.sqrt(d0 * d0 + d1 * d1) * 4.0F;

		if (f > 1.0F)
		{
			f = 1.0F;
		}

		this.limbSwingAmount += (f - this.limbSwingAmount) * 0.5F;
		this.limbSwing += this.limbSwingAmount;
	}

	//===================================================== Initial Spawn =======================================================

	/**
	 * Called only once on an entity when first time spawned, via egg, mob
	 * spawner, natural spawning etc, but not called when entity is reloaded
	 * from nbt. Mainly used for initializing attributes and inventory
	 */
	@Nullable
	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata)
	{
		livingdata = super.onInitialSpawn(difficulty, livingdata);
		((PathNavigateGround) this.getNavigator()).setBreakDoors(true);
		this.setCanPickUpLoot(true);
		this.setLeftHanded(false);
		this.inCombat = false;
		this.blocking = false;
        this.setSprinting(false);
		this.blockingTimer = 0;
		this.setAttackTarget(null);
    	this.canShieldPush = true;
		this.resetActiveHand();
		this.setActiveHand(EnumHand.MAIN_HAND);
		this.activeItemStackUseCount = 0;
    	this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.25D);
    	this.strafeVer = 0.0F;
    	this.strafeHor = 0.0F;
    	this.getMoveHelper().strafe( 0.0F, 0.0F );
    	this.getNavigator().clearPath();
		
//		setEquipmentBasedOnDifficulty(difficulty);
//		setEnchantmentBasedOnDifficulty(difficulty);
			    
		this.addEquipment();
		
		this.setMount();
		
		this.writeEntityToNBT(new NBTTagCompound());
		return livingdata;
	}
	
	//===================================================== Mount =======================================================

	public void setMount()
	{
		if ( this.world.isRemote || !this.world.canSeeSky(this.getPosition() ) )
		{
			return;
		}
		
		int chance = ToroQuestConfiguration.banditMountChance;

		if ( chance > 0 && rand.nextInt(11-chance) == 0 )
	    {
			if ( !(this.weaponMain.getItem() instanceof ItemBow) )
	    	{
				this.weaponMain = new ItemStack(Item.getByNameOrId(ToroQuestConfiguration.banditRangedWeapons[rand.nextInt(ToroQuestConfiguration.banditRangedWeapons.length)]),1);
			
				if ( this.weaponMain == null || this.weaponMain.isEmpty() )
		    	{
					this.weaponMain = new ItemStack(Items.BOW, 1);
				}
				
				if ( ToroQuestConfiguration.enchantFirstBanditAndOrcChance > 0 && rand.nextInt(100) < ToroQuestConfiguration.enchantFirstBanditAndOrcChance )
		    	{
			    	String[] enchant = ToroQuestConfiguration.enchantFirstBanditAndOrcRanged[rand.nextInt(ToroQuestConfiguration.enchantFirstBanditAndOrcRanged.length)].split(",");
		    		this.weaponMain.addEnchantment(Enchantment.getEnchantmentByLocation(enchant[0]), rand.nextInt(Integer.parseInt(enchant[1]))+1);
			    	if (  ToroQuestConfiguration.enchantSecondBanditAndOrcChance > 0 && rand.nextInt(100) < ToroQuestConfiguration.enchantSecondBanditAndOrcChance )
			    	{
				    	enchant = ToroQuestConfiguration.enchantSecondBanditAndOrcRanged[rand.nextInt(ToroQuestConfiguration.enchantSecondBanditAndOrcRanged.length)].split(",");
			    		this.weaponMain.addEnchantment(Enchantment.getEnchantmentByLocation(enchant[0]), rand.nextInt(Integer.parseInt(enchant[1]))+1);
			    	}
		    	}
		    	
		    	if ( this.weaponMain != null && !this.weaponMain.isEmpty() )
			    {
		    		this.setHeldItem(EnumHand.MAIN_HAND, this.weaponMain );
		    		this.setHeldItem(EnumHand.OFF_HAND, ItemStack.EMPTY );
			    }
		    	else
		    	{
		    		return;
		    	}
			}
			
	        EntityHorse mount = new EntityHorse(this.world);
	    	mount.setGrowingAge(24000);
	        mount.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0F);
	        mount.setHorseTamed(true);
			mount.replaceItemInInventory(400, new ItemStack((Items.SADDLE)));
			if ( rand.nextInt(3) == 0 ) 
			{
				ItemStack itemStack = new ItemStack((Items.GOLDEN_HORSE_ARMOR),1);
				mount.replaceItemInInventory(401, itemStack);
			}
	        mount.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10);
	        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0);
	        mount.setHealth(10);
	        this.world.spawnEntity(mount);
	        mount.removePassengers();
	        this.startRiding(mount);
	        mount.tasks.addTask(0, new EntityAIDespawn(mount));
	    }
	}
	
	//===================================================== Ranged Attack =======================================================
	
	@Override
	public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor)
	{
		if ( target == null || this.getHeldItemMainhand() == null )
		{
			return;
		}
		
	    EntityArrow entityarrow = this.getArrow(distanceFactor);
	
	    if ( EnchantmentHelper.getEnchantments(this.getHeldItemMainhand()).containsKey(Enchantment.getEnchantmentByLocation("minecraft:flame")) )
	    {
	        entityarrow.setFire(12);
	    }
	
	    entityarrow.setIsCritical(true);
	    double d0 = target.posX - this.posX;
	    double d1 = target.getEntityBoundingBox().minY + target.height/2.0 - entityarrow.posY - 1 - rand.nextDouble();
	    double d2 = target.posZ - this.posZ;
	    double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
	    entityarrow.shoot( d0, d1 + d3 * 0.2D, d2, 2.2F, 2.0F );
	    this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.5F + 0.8F));
	    this.world.spawnEntity(entityarrow);
	}
	
	protected EntityArrow getArrow(float p_190726_1_)
	{
	    return new EntitySmartArrow(this.world, this);
	}
	
	//===================================================== NBT =======================================================
	
	@Override
	public void readEntityFromNBT(NBTTagCompound compound)
	{
		
		
		/// XXX
		
		
	    if ( compound.hasKey("raidX") && compound.hasKey("raidZ") )
	    {
	    	this.raidX = compound.getInteger("raidX");
	    	this.raidZ = compound.getInteger("raidZ");
	    	this.setRaidLocation(compound.getInteger("raidX"), compound.getInteger("raidZ"));
	    }
	    if ( compound.hasKey("despawnTimer") )
	    {
	    	this.despawnTimer = compound.getInteger("despawnTimer");
	    }
	    
	    if ( compound.hasKey("bribed") )
	    {
	    	this.bribed = compound.getBoolean("bribed");
	    }
	    
	    if ( compound.hasKey("weaponMainInteger") )
	    {
	    	this.weaponMainInteger = compound.getInteger("weaponMainInteger");
	    }
	    
	    if ( compound.hasKey("weaponOffInteger") )
	    {
	    	this.weaponOffInteger = compound.getInteger("weaponOffInteger");
	    }
	    
	    super.readEntityFromNBT(compound);
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound compound)
	{
		if ( this.raidX != null && this.raidZ != null )
		{
			compound.setInteger("raidX", this.raidX);
			compound.setInteger("raidZ", this.raidZ);
		}
		if ( this.weaponMainInteger != null ) compound.setInteger("weaponMainInteger", this.weaponMainInteger);
		if ( this.weaponOffInteger != null ) compound.setInteger("weaponOffInteger", this.weaponOffInteger);
		compound.setInteger("despawnTimer", this.despawnTimer);
		compound.setBoolean("bribed", this.bribed);

		super.writeEntityToNBT(compound);
	}
	
	//===================================================== Raid Location =======================================================

	public void setRaidLocation(Integer x, Integer z)
	{
		this.tasks.removeTask(this.areaAI);
		if ( x != null && z != null )
		{
			this.raidX = x;
			this.raidZ = z;
			this.tasks.addTask(11, this.areaAI);
			this.areaAI.setCenter(x, z);
			this.writeEntityToNBT(new NBTTagCompound());
		}
	}
	
	//===================================================== Set Tame =======================================================

	public void setTame()
	{
	    this.playTameEffect(true);
		this.bribed = true;
		this.writeToNBT(new NBTTagCompound());
	}

	//===================================================== Update Blocking =======================================================

	@Override
	protected void updateActiveHand()
	{
	    if (this.isHandActive())
	    {
	        ItemStack itemstack = this.getHeldItem(this.getActiveHand());
	        if ( itemstack.getItem() instanceof ItemShield ) // this.blocking
	        {
	        	activeItemStackUseCount = 30;
	        	if (activeItemStackUseCount > 0)
	        	{
	        		activeItemStack.getItem().onUsingTick(activeItemStack, this, activeItemStackUseCount);
	        	}
		        
		        if (this.getItemInUseCount() <= 25 && this.getItemInUseCount() % 4 == 0)
		        {
		        	this.canShieldPush = true;
		            this.updateItemUse(this.activeItemStack, 5);
		        }
		
		        if (--this.activeItemStackUseCount <= 0 && !this.world.isRemote)
		        {
		            this.onItemUseFinish();
		        }
	        }
	        else // NOT this.blocking
	        {
	        	if (itemstack == this.activeItemStack)
		        {
		            if (!this.activeItemStack.isEmpty())
		            {
		            	activeItemStackUseCount = net.minecraftforge.event.ForgeEventFactory.onItemUseTick(this, activeItemStack, activeItemStackUseCount);
		                if (activeItemStackUseCount > 0)
		                {
		                    activeItemStack.getItem().onUsingTick(activeItemStack, this, activeItemStackUseCount);
		                }
		            }
		
		            if (this.getItemInUseCount() <= 25 && this.getItemInUseCount() % 4 == 0)
		            {
			        	this.canShieldPush = true;
		                this.updateItemUse(this.activeItemStack, 5);
		            }
		
		            if (--this.activeItemStackUseCount <= 0 && !this.world.isRemote)
		            {
		                this.onItemUseFinish();
		            }
		        }
		        else
		        {
		        	this.canShieldPush = true;
		            this.resetActiveHand();
		        }
	        }
	    }
	}
    
	//===================================================== Sounds =======================================================

    @Override
    @Nullable
    protected SoundEvent getAmbientSound()
    {
        return null;
    }

	@Override
	@Nullable
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
		if ( rand.nextInt(4) == 0 )
		{
			this.playSound(SoundEvents.VINDICATION_ILLAGER_DEATH, 1.0F, 0.9F + rand.nextFloat()/5.0F );
		}
		return super.getHurtSound(damageSourceIn);
    }

	@Override
	@Nullable
    protected SoundEvent getDeathSound()
    {
		if ( rand.nextBoolean() )
		{
			this.playSound(SoundEvents.EVOCATION_ILLAGER_DEATH, 1.0F, 0.9F + rand.nextFloat()/5.0F );
		}
		else
		{
			this.playSound(SoundEvents.ENTITY_ILLAGER_DEATH, 1.0F, 0.9F + rand.nextFloat()/5.0F );
		}
		return super.getDeathSound();
    }
    
//	//===================================================== DPS =======================================================
//    
//	private void sentryTypeDPS( )
//	{
//		if ( this.getAttackTarget() != null && this.getAttackTarget().isEntityAlive() && !(this.getAttackTarget() instanceof EntityAdventurer) )
//		{
//	        double dist = this.getDistanceSq(this.getAttackTarget());
//            
//	        if ( !this.inCombat )
//	        {
//	        	this.getNavigator().clearPath();
//				this.inCombat = true;
//				this.stance = rand.nextInt(6)+5;
//				this.strafeHor = getStrafe(stance);
//	        }
//	        
//			if ( this.blockingTimer < 0 )
//			{
//				this.stance = rand.nextInt(8)+3;
//				this.blockingTimer = (int)MathHelper.clamp((rand.nextInt(50)+15-dist), 15, 60);
//				this.strafeHor = getStrafe(stance);
//			}
//
//			// THROW POTION: 1 = can throw, 0 = ready to throw, -1 = can not throw
//			if ( this.limitPotions > 0 && !this.isDrinkingPotion() )
//			{
//				if ( this.hasSplashPotion == 1 && dist >= 20 && dist <= 160 && this.getHeldItemOffhand().isEmpty() && !this.isBesideClimbableBlock() )
//				{
//					if ( world.getEntitiesWithinAABB(EntityAdventurer.class, new AxisAlignedBB(this.getPosition()).grow(3, 3, 3)).size() < 2 )
//					{
//						this.hasSplashPotion = 0;
//						PotionType potiontype = PotionTypes.SLOWNESS;
//			            this.setHeldItem(EnumHand.OFF_HAND, PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), potiontype));
//					}
//				}
//				else if ( this.hasSplashPotion == 0 )
//				{	
//					if ( dist >= 10 && dist <= 80 )
//					{
//						if ( this.canEntityBeSeen(this.getAttackTarget()) && world.getEntitiesWithinAABB(EntityAdventurer.class, new AxisAlignedBB(this.getPosition()).grow(3, 3, 3)).size() < 2 )
//						{
//							this.splashPotionTimer--;
//							if ( this.splashPotionTimer < 3 )
//							{
//								if ( this.onGround )
//								{
//									Vec3d velocityVector = new Vec3d(this.posX - this.getAttackTarget().posX, 0, this.posZ - this.getAttackTarget().posZ);
//									double push = (2.0D+dist)*2.0D;
//									this.addVelocity((velocityVector.x)/push, 0.0D, (velocityVector.z)/push);
//				                	this.velocityChanged = true;
//								}
//								if ( this.splashPotionTimer < 0 )
//								{
//									this.splashPotionTimer = 10;
//									this.hasSplashPotion = -1;
//									this.swingArm(EnumHand.OFF_HAND);
//									this.throwSplashPotion(this.getAttackTarget());
//									this.limitPotions--;
//									this.setHeldItem(EnumHand.OFF_HAND, ItemStack.EMPTY );
//									this.getNavigator().clearPath();
//								}
//							}
//						}
//						else
//						{
//							this.splashPotionTimer = 6;
//						}
//					}
//					else
//					{
//						this.splashPotionTimer = 6;
//					}
//				}
//			}
//			
//			// if ( !blocking )
//			{
//				float strafeMod = 1.0F;
//				
//				if ( this.stance < 5 )
//				{
//					this.strafeVer = 0.4F;
//
//					if ( dist <= 25 )
//					{
//						if ( this.onGround )
//						{
//							Vec3d velocityVector = new Vec3d(this.posX - this.getAttackTarget().posX, 0, this.posZ - this.getAttackTarget().posZ);
//							double push = (12.0D+dist*dist);
//							this.addVelocity((velocityVector.x)/push, 0.0D, (velocityVector.z)/push);
//		                	this.velocityChanged = true;
//						}
//					}
//					else
//					{
//						this.stance = rand.nextInt(6)+5;
//						this.strafeHor = getStrafe(stance);
//					}
//				}
//				else if ( dist <= 2.5 )
//				{
//					this.strafeVer = 0.4F;
//				}
//				else if ( dist <= 7 )
//				{
//					this.strafeVer = 0.7F;
//					strafeMod = 0.9F;
//				}
//				else if ( dist <= 12 )
//				{
//					this.strafeVer = 0.8F;
//					strafeMod = 0.8F;
//				}
//				else
//				{
//					this.strafeVer = 0.9F;
//					strafeMod = 0.7F;
//				}
//							
//				if ( this.getNavigator().tryMoveToEntityLiving(this.getAttackTarget(), this.strafeVer) )
//				{					
//					if ( dist >= 12 )
//					{
//						this.blockingTimer--;
//					}
//					else if ( dist <= 3 )
//					{
//						if ( !this.world.isRemote && this.onGround && !this.isSprinting() )
//						{
//							Vec3d velocityVector = new Vec3d(this.posX - this.getAttackTarget().posX, 0, this.posZ - this.getAttackTarget().posZ);
//							double push = (1.0D+dist*dist);
//							this.addVelocity((velocityVector.x)/push, 0.0D, (velocityVector.z)/push);
//		                	this.velocityChanged = true;
//						}
//					}
//					
//					if ( this.posY + 1.5D < this.getAttackTarget().posY )
//					{
//						this.getMoveHelper().strafe( this.strafeVer, 0.0F );
//					}
//					else
//					{
//						this.getMoveHelper().strafe( this.strafeVer, this.strafeHor*strafeMod );
//					}
//				}
//				else
//				{
//					if ( this.posY + 1.5D < this.getAttackTarget().posY )
//					{
//						this.getMoveHelper().strafe( this.strafeVer*0.5F, 0.0F );
//					}
//					else
//					{
//						this.getMoveHelper().strafe( this.strafeVer*0.5F, this.strafeHor*0.5F*strafeMod );
//					}
//				}
//			}
//			this.blockingTimer--;
//		}
//		else if ( this.blocking || this.inCombat )
//		{
//			this.inCombat = false;
//			this.blocking = false;
//			this.setAttackTarget(null);
//	        this.setSprinting(false);
//        	this.canShieldPush = true;
//			this.resetActiveHand();
//			this.activeItemStackUseCount = 0;
//	    	this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.25D);
//	    	this.strafeVer = 0F;
//	    	this.strafeHor = 0F;
//	    	this.getMoveHelper().strafe( 0.0F, 0.0F );
//	    	this.getNavigator().clearPath();
//			if ( this.getHeldItemOffhand().getItem() instanceof ItemPotion ) setHeldItem(EnumHand.OFF_HAND, ItemStack.EMPTY );
//	    	this.hasSplashPotion = 1;
//	    	this.splashPotionTimer = 10;
//		}
//	}
//	
//	//===================================================== Tank =======================================================
//	
//	private void sentryTypeTank( )
//	{
//		if ( this.getAttackTarget() != null && this.getAttackTarget().isEntityAlive() && !(this.getAttackTarget() instanceof EntityAdventurer) )
//		{
//			List<EntityArrow> arrows = this.world.getEntitiesWithinAABB(EntityArrow.class, new AxisAlignedBB(this.getPosition()).grow(8, 8, 8), new Predicate<EntityArrow>()
//			{
//				public boolean apply(@Nullable EntityArrow entity)
//				{
//					if ( entity.lastTickPosX == 0 && entity.shootingEntity == getAttackTarget() )
//					{
//						return true;
//					}
//					return false;
//				}
//			});
//			
//	        double dist = this.getDistanceSq(this.getAttackTarget());
//			
//			if ( !arrows.isEmpty() )
//			{
//				this.stance = rand.nextInt(6)+5;
//				if ( dist <= 12 )
//				{
//					this.blockingTimer = 25;
//				}
//				else
//				{
//					this.blockingTimer = 50;
//				}
//				this.blocking = true;
//				this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
//	        	this.canShieldPush = true;
//				this.resetActiveHand();
//				this.setActiveHand(EnumHand.OFF_HAND);
//				this.updateActiveHand();
//				this.strafeHor = getStrafe(stance);
//				this.strafeVer = 0.4F;
//			}
//			            
//	        if ( !this.inCombat )
//	        {
//				this.inCombat = true;
//	        	this.getNavigator().clearPath();
//				this.strafeVer = 0.8F;
//				this.stance = rand.nextInt(6)+5;
//				this.strafeHor = getStrafe(stance);
//	        }
//			
//			if ( !this.blocking && dist <= 12 && this.blockingTimer <= -(int)(this.stance*5+dist+20) )
//			{
//				this.stance = rand.nextInt(8)+3;
//				this.strafeHor = getStrafe(stance);
//				this.blockingTimer = (int)MathHelper.clamp((rand.nextInt(70)+20-dist), 20, 80);
//				this.blocking = true;
//				this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
//				this.canShieldPush = true;
//				this.updateActiveHand();
//				this.setActiveHand(EnumHand.OFF_HAND);
//				if ( dist <= 6 )
//				{
//					this.strafeVer = 0.2F;
//				}
//				else
//				{
//					this.strafeVer = 0.4F;
//				}
//			}
//			else if ( this.blocking && this.blockingTimer % 16 == 0 )
//			{
//	        	this.canShieldPush = true;
//
//				if ( dist <= 3 )
//				{
//					this.strafeVer = 0.2F;
//				}
//				else
//				{
//					this.strafeVer = 0.4F;
//				}
//			}
//
//			if ( this.blocking && this.blockingTimer <= 0 )
//			{
//				this.blocking = false;
//				this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.25D);
//				this.stance = rand.nextInt(8)+3;
//				this.strafeHor = getStrafe(stance);
//	        	this.canShieldPush = true;
//				this.resetActiveHand();
//			}
//			else if ( !this.blocking && dist <= 64 )
//			{
//				if ( this.blockingTimer == -12 || this.blockingTimer == -32 || ( this.blockingTimer < -32 && this.blockingTimer % 14 == 0 ) )
//				{
//					if ( rand.nextInt(3) == 0 )
//					{
//						this.stance = rand.nextInt(8)+3;
//						this.strafeHor = getStrafe(stance);
//					}
//				}
//			}
//			
//			if ( !this.blocking )
//			{
//				float strafeMod = 1.0F;
//				
//				if ( this.stance < 5 )
//				{
//					this.strafeVer = 0.4F;
//
//					if ( dist <= 25 )
//					{
//						if ( !this.world.isRemote && this.onGround )
//						{
//							Vec3d velocityVector = new Vec3d(this.posX - this.getAttackTarget().posX, 0, this.posZ - this.getAttackTarget().posZ);
//							double push = (12.0D+dist*dist);
//							this.addVelocity((velocityVector.x)/push, 0.0D, (velocityVector.z)/push);
//		                	this.velocityChanged = true;
//						}
//					}
//					else
//					{
//						this.stance = rand.nextInt(6)+5;
//						this.strafeHor = getStrafe(stance);
//					}
//				}
//				else if ( dist <= 2.5 )
//				{
//					this.strafeVer = 0.4F;
//				}
//				else if ( dist <= 7 )
//				{
//					this.strafeVer = 0.7F;
//					strafeMod = 0.9F;
//				}
//				else if ( dist <= 12 )
//				{
//					this.strafeVer = 0.8F;
//					strafeMod = 0.8F;
//				}
//				else
//				{
//					this.strafeVer = 0.9F;
//					strafeMod = 0.7F;
//				}
//							
//				if ( this.getNavigator().tryMoveToEntityLiving(this.getAttackTarget(), this.strafeVer) )
//				{
//					
//					if ( dist >= 12 )
//					{
//						this.blockingTimer--;
//					}
//					else if ( dist <= 3 )
//					{
//						if ( !this.world.isRemote && this.onGround && !this.isSprinting() )
//						{
//							Vec3d velocityVector = new Vec3d(this.posX - this.getAttackTarget().posX, 0, this.posZ - this.getAttackTarget().posZ);
//							double push = (1.0D+dist*dist);
//							this.addVelocity((velocityVector.x)/push, 0.0D, (velocityVector.z)/push);
//		                	this.velocityChanged = true;
//						}
//					}
//					
//					if ( this.posY + 1.5D < this.getAttackTarget().posY )
//					{
//						this.getMoveHelper().strafe( this.strafeVer, 0.0F );
//					}
//					else
//					{
//						this.getMoveHelper().strafe( this.strafeVer, this.strafeHor*strafeMod );
//					}
//				}
//				else
//				{
//					if ( this.posY + 1.5D < this.getAttackTarget().posY )
//					{
//						this.getMoveHelper().strafe( this.strafeVer*0.5F, 0.0F );
//					}
//					else
//					{
//						this.getMoveHelper().strafe( this.strafeVer*0.5F, this.strafeHor*0.5F*strafeMod );
//					}
//				}
//			}
//			this.blockingTimer--;
//		}
//		else if ( this.blocking || this.inCombat )
//		{
//			this.inCombat = false;
//			this.blocking = false;
//			if ( this.getAttackTarget() != null && this.getAttackTarget().isEntityAlive() )
//			{
//				this.setAttackTarget(null);
//			}
//			this.resetActiveHand();
//        	this.canShieldPush = true;
//	        this.setSprinting(false);
//			this.activeItemStackUseCount = 0;
//	    	this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.25D);
//	    	this.strafeVer = 0F;
//	    	this.strafeHor = 0F;
//	    	this.getMoveHelper().strafe( 0.0F, 0.0F );
//	    	this.getNavigator().clearPath();
//		}
//	}
//	
//	//===================================================== Ranged =======================================================
//
//	private void sentryTypeRanged( )
//	{
//		if ( this.getAttackTarget() != null && this.getAttackTarget().isEntityAlive() && !(this.getAttackTarget() instanceof EntityAdventurer) )
//		{  
//	        if ( !this.inCombat )
//	        {
//				this.inCombat = true;
//	        	this.getNavigator().clearPath();
//				this.strafeVer = 0.8F;
//				this.strafeHor = getStrafe(stance);
//	        }
//			if ( this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue() <= 0.0D && !this.isRiding() )
//			{
//				this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.392D+rand.nextDouble()/50.0D);
//			}
//		}
//		else if ( this.blocking || this.inCombat )
//		{
//			this.inCombat = false;
//			this.blocking = false;
//			if ( this.getAttackTarget() != null && this.getAttackTarget().isEntityAlive() )
//			{
//				this.setAttackTarget(null);
//			}
//			this.resetActiveHand();
//        	this.canShieldPush = true;
//	        this.setSprinting(false);
//			this.activeItemStackUseCount = 0;
//	    	this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.25D);
//	    	this.strafeVer = 0F;
//	    	this.strafeHor = 0F;
//	    	this.getMoveHelper().strafe( 0.0F, 0.0F );
//	    	this.getNavigator().clearPath();
//		}
//	}

	//===================================================== Melee Attack =======================================================

	@Override
	public boolean attackEntityAsMob(Entity victim)
	{
		if ( victim instanceof EntityAdventurer )
		{
			setAttackTarget(null);
			return false;
		}
		else
		{
			this.attackTargetEntityWithCurrentItem(victim);
			return true;
		}
	}
	
	public void attackTargetEntityWithCurrentItem(Entity targetEntity)
	{
		
		if ( rand.nextInt(5) == 0 )
        {
        	this.playSound( SoundEvents.VINDICATION_ILLAGER_AMBIENT, 1.0F, 0.9F + rand.nextFloat()/5.0F );
        }
		
		if (targetEntity.canBeAttackedWithItem()) {
			if (!targetEntity.hitByEntity(this)) {
				float attackDamage = (float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
				float modifierForCreature;

				if (targetEntity instanceof EntityLivingBase) {
					modifierForCreature = EnchantmentHelper.getModifierForCreature(this.getHeldItemMainhand(),
							((EntityLivingBase) targetEntity).getCreatureAttribute());
				} else {
					modifierForCreature = EnchantmentHelper.getModifierForCreature(this.getHeldItemMainhand(), EnumCreatureAttribute.UNDEFINED);
				}

				if (attackDamage > 0.0F || modifierForCreature > 0.0F) {

					int i = 0;
					i = i + EnchantmentHelper.getKnockbackModifier(this);

					boolean criticalHit = this.fallDistance > 0.0F && !this.onGround && !this.isOnLadder() && !this.isInWater()
							&& !this.isPotionActive(MobEffects.BLINDNESS) && !this.isRiding() && targetEntity instanceof EntityLivingBase;
					criticalHit = criticalHit && !this.isSprinting();

					if (criticalHit) {
						attackDamage *= 1.5F;
					}

					attackDamage = attackDamage + modifierForCreature;
					boolean swordSweep = false;
					double d0 = (double) (this.distanceWalkedModified - this.prevDistanceWalkedModified);

					if (!criticalHit && this.onGround && d0 < (double) this.getAIMoveSpeed()) {
						ItemStack itemstack = this.getHeldItem(EnumHand.MAIN_HAND);

						if (itemstack != null && itemstack.getItem() instanceof ItemSword) {
							swordSweep = true;
						}
					}

					float targetHealth = 0.0F;
					boolean setFireToTarget = false;
					int fireAspectModiferOfGuard = EnchantmentHelper.getFireAspectModifier(this);

					if (targetEntity instanceof EntityLivingBase) {
						targetHealth = ((EntityLivingBase) targetEntity).getHealth();

						if (fireAspectModiferOfGuard > 0 && !targetEntity.isBurning()) {
							setFireToTarget = true;
							targetEntity.setFire(1);
						}
					}

					double targetMotionX = targetEntity.motionX;
					double targetMotionY = targetEntity.motionY;
					double targetMotionZ = targetEntity.motionZ;

					boolean successfulAttack = targetEntity.attackEntityFrom(DamageSource.causeMobDamage(this), attackDamage);

					if (successfulAttack) {
						if (i > 0) {
							if (targetEntity instanceof EntityLivingBase) {
								((EntityLivingBase) targetEntity).knockBack(this, (float) i * 0.5F,
										(double) MathHelper.sin(this.rotationYaw * 0.017453292F),
										(double) (-MathHelper.cos(this.rotationYaw * 0.017453292F)));
							} else {
								targetEntity.addVelocity((double) (-MathHelper.sin(this.rotationYaw * 0.017453292F) * (float) i * 0.5F), 0.1D,
										(double) (MathHelper.cos(this.rotationYaw * 0.017453292F) * (float) i * 0.5F));
							}

							this.motionX *= 0.6D;
							this.motionZ *= 0.6D;
							this.setSprinting(false);
						}

						if (swordSweep) {
							for (EntityLivingBase entitylivingbase : this.world.getEntitiesWithinAABB(EntityLivingBase.class,
									targetEntity.getEntityBoundingBox().grow(1.0D, 0.25D, 1.0D))) {
								if (entitylivingbase != this && entitylivingbase != targetEntity && !this.isOnSameTeam(entitylivingbase)
										&& this.getDistanceSq(entitylivingbase) < 9.0D) {
									entitylivingbase.knockBack(this, 0.4F, (double) MathHelper.sin(this.rotationYaw * 0.017453292F),
											(double) (-MathHelper.cos(this.rotationYaw * 0.017453292F)));
									entitylivingbase.attackEntityFrom(DamageSource.causeMobDamage(this), 1.0F);
								}
							}

							world.playSound((EntityPlayer) null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP,
									this.getSoundCategory(), 1.0F, 1.0F);
							this.spawnSweepParticles();
						}

						if (targetEntity instanceof EntityPlayerMP && targetEntity.velocityChanged) {
							((EntityPlayerMP) targetEntity).connection.sendPacket(new SPacketEntityVelocity(targetEntity));
							targetEntity.velocityChanged = false;
							targetEntity.motionX = targetMotionX;
							targetEntity.motionY = targetMotionY;
							targetEntity.motionZ = targetMotionZ;
						}

						if (criticalHit) {
							this.world.playSound((EntityPlayer) null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_CRIT,
									this.getSoundCategory(), 1.0F, 1.0F);
							this.onCriticalHit(targetEntity);
						}

						if (!criticalHit && !swordSweep) {
							this.world.playSound((EntityPlayer) null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_STRONG,
									this.getSoundCategory(), 1.0F, 1.0F);
						}

						if (modifierForCreature > 0.0F) {
							this.onEnchantmentCritical(targetEntity);
						}

						if (!world.isRemote && targetEntity instanceof EntityPlayer) {
							EntityPlayer entityplayer = (EntityPlayer) targetEntity;
							ItemStack itemstack2 = this.getHeldItemMainhand();
							ItemStack itemstack3 = entityplayer.isHandActive() ? entityplayer.getActiveItemStack() : null;

							if (itemstack2 != null && itemstack3 != null && itemstack3.getItem() instanceof ItemShield && ( itemstack2.getItem() instanceof ItemAxe || itemstack2.getItem().getRegistryName().toString().contains("halberd") || itemstack2.getItem().getRegistryName().toString().contains("battleaxe") ) )
							{
								float f3 = 0.25F + (float) EnchantmentHelper.getEfficiencyModifier(this) * 0.05F;
								if (this.rand.nextFloat() < f3) {
									entityplayer.getCooldownTracker().setCooldown(Items.SHIELD, 100);
									this.world.setEntityState(entityplayer, (byte) 30);
								}
							}
						}

						this.setLastAttackedEntity(targetEntity);

						if (targetEntity instanceof EntityLivingBase) {
							EnchantmentHelper.applyThornEnchantments((EntityLivingBase) targetEntity, this);
						}

						EnchantmentHelper.applyArthropodEnchantments(this, targetEntity);
						ItemStack mainhandItem = this.getHeldItemMainhand();
						Entity entity = targetEntity;

						if (mainhandItem != null && entity instanceof EntityLivingBase) {
							mainhandItem.getItem().hitEntity(mainhandItem, (EntityLivingBase) entity, this);

							if (mainhandItem.getCount() <= 0) {
								this.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
							}
						}

						if (targetEntity instanceof EntityLivingBase) {
							float damageDealt = targetHealth - ((EntityLivingBase) targetEntity).getHealth();

							if (fireAspectModiferOfGuard > 0) {
								targetEntity.setFire(fireAspectModiferOfGuard * 4);
							}

							if (world instanceof WorldServer && damageDealt > 2.0F) {
								int k = (int) ((double) damageDealt * 0.5D);
								((WorldServer) this.world).spawnParticle(EnumParticleTypes.DAMAGE_INDICATOR, targetEntity.posX,
										targetEntity.posY + (double) (targetEntity.height * 0.5F), targetEntity.posZ, k, 0.1D, 0.0D, 0.1D, 0.2D,
										new int[0]);
							}
						}

					} else {
						this.world.playSound((EntityPlayer) null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE,
								this.getSoundCategory(), 1.0F, 1.0F);

						if (setFireToTarget) {
							targetEntity.extinguish();
						}
					}
				}
			}
		}
	}
	
	public void onCriticalHit(Entity entityHit)
	{
		
	}

	public void onEnchantmentCritical(Entity entityHit)
	{
		
	}

	public void spawnSweepParticles()
	{
		double d0 = (double) (-MathHelper.sin(this.rotationYaw * 0.017453292F));
		double d1 = (double) MathHelper.cos(this.rotationYaw * 0.017453292F);

		if (this.world instanceof WorldServer)
		{
			((WorldServer) this.world).spawnParticle(EnumParticleTypes.SWEEP_ATTACK, this.posX + d0, this.posY + (double) this.height * 0.5D, this.posZ + d1, 0, d0, 0.0D, d1, 0.0D, new int[0]);
		}
	}

	//===================================================== Strafe =======================================================

	public float getStrafe(int stance)
	{
		switch ( stance )
		{
			case 3:
			{
				return -1.0F;
			}
			case 4:
			{
				return 1.0F;
			}
			case 5:
			{
				return -0.3F;
			}
			case 6:
			{
				return 0.3F;
			}
			case 7:
			{
				return -0.295F;
			}
			case 8:
			{
				return 0.295F;
			}
			case 9:
			{
				return -0.29F;
			}
			case 10:
			{
				return 0.29F;
			}
		}
		return 0.0F;
	}
	
	//===================================================== Update =======================================================

    @Override
    public void onUpdate()
    {
        if ( !this.world.isRemote )
        {
        	if ( this.collidedHorizontally && this.getAttackTarget() != null )
        	{
    			this.setSwingingArms(false);
                ItemStack iStackM = this.getHeldItemMainhand();
        		if ( ( iStackM.getItem() != null && iStackM.getItem() instanceof ItemBow ) || ( this.getAttackTarget().posY + 1.0D < this.posY ) ) // && this.canEntityBeSeen(this.getAttackTarget())) )
				{
					this.setBesideClimbableBlock(false);
	    			if ( !this.blocking ) this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.25D);
				}
        		else
        		{
        			this.stance = rand.nextInt(6)+5;
    				this.strafeHor = getStrafe(stance);
    				this.motionX = 0.0F;
    				this.motionZ = 0.0F;
					this.getMoveHelper().strafe( this.strafeVer*0.5F, 0.0F );
    		    	this.faceEntity(this.getAttackTarget(), 30.0F, 30.0F);
    				this.blockingTimer = 20;
        			this.setBesideClimbableBlock(true);
        			this.setSwingingArms(true);
        			this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(2.0D);
        		}
        	}
        	else
        	{
        		this.setBesideClimbableBlock(false);
    			if ( !this.blocking ) this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.25D);
        	}
        }
        super.onUpdate();

    }
    
	//===================================================== Climbing =======================================================
    
    @Override
    public boolean canBePushed()
    {
        return this.isEntityAlive();
    }
    
    @Override
    public boolean isOnLadder()
    {
        return this.isBesideClimbableBlock();
    }
    
    public boolean isBesideClimbableBlock()
    {
        return (((Byte)this.dataManager.get(CLIMBING)).byteValue() & 1) != 0;
    }
    
    public void setBesideClimbableBlock(boolean climbing)
    {
        byte b0 = ((Byte)this.dataManager.get(CLIMBING)).byteValue();
        
        if ( climbing )
        {
            b0 = (byte)(b0 | 1);
        }
        else
        {
            b0 = (byte)(b0 & -2);
        }

        this.dataManager.set(CLIMBING, Byte.valueOf(b0));
    }
    
}