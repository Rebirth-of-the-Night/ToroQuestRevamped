package net.torocraft.toroquest.entities;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.block.BlockFire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
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
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.potion.PotionEffect;
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
import net.minecraftforge.server.command.TextComponentHelper;
import net.torocraft.toroquest.ToroQuest;
import net.torocraft.toroquest.civilization.CivilizationHandlers;
import net.torocraft.toroquest.civilization.CivilizationUtil;
import net.torocraft.toroquest.civilization.Province;
import net.torocraft.toroquest.civilization.quests.QuestRecruit;
import net.torocraft.toroquest.config.ToroQuestConfiguration;
import net.torocraft.toroquest.entities.ai.AIHelper;
import net.torocraft.toroquest.entities.ai.EntityAIBanditAttack;
import net.torocraft.toroquest.entities.ai.EntityAIDespawn;
import net.torocraft.toroquest.entities.ai.EntityAIRaid;
import net.torocraft.toroquest.entities.ai.EntityAISmartTempt;
import net.torocraft.toroquest.entities.ai.EntityAIZombieLeap;
import net.torocraft.toroquest.entities.render.RenderSentry;

public class EntitySentry extends EntityToroMob implements IRangedAttackMob, IMob, IBandit
{
	protected ItemStack weaponMain = new ItemStack(Items.AIR, 1);
	protected ItemStack weaponOff = new ItemStack(Items.AIR, 1);
	protected final AIArcher<EntitySentry> aiArrowAttack = new AIArcher<EntitySentry>(this, 0.6D, 40, 40.0F);
    protected boolean inCombat = false;
    public boolean forceFleeing = false;
    protected double randPosX;
    protected double randPosY;
    protected double randPosZ;
    public int despawnTimer = 100;
    public int stance = 0;
	protected float strafeVer = 0.0F;
	protected float strafeHor = 0.0F;
    public int passiveTimer = -1;
	//public boolean spawnedNearBandits = false;
	protected boolean bribed = false;
	public float capeAni = 0;
	public boolean capeAniUp = true;
	protected boolean blocking = false;
	protected int blockingTimer = 0;
	protected boolean despawn = false;
	public int climbingTimer = 0;
	protected boolean canTalk = true;
	public short potionImmunity = 0;
    private int potionUseTimer;
    public boolean useHealingPotion = false;
	boolean flanking = false;
	boolean fleeing = false;
	public Integer raidX = null;
	public Integer raidZ = null;
	public boolean canShieldPush = true;
    protected Vec3d vec3d;
    protected int splashPotionTimer = 6;
    protected int hasSplashPotion = 1;
    public int limitPotions = rand.nextInt(3);
	public double renderSizeXZ = 0.9D + rand.nextDouble()/12.0D;
	public double renderSizeY =  renderSizeXZ * (1.025D + rand.nextFloat()/16.0D);
	protected final EntityAIRaid areaAI = new EntityAIRaid(this, 0.65D, 16, 32);
	private ResourceLocation banditSkin = new ResourceLocation(ToroQuest.MODID + ":textures/entity/bandit/bandit_" + rand.nextInt(ToroQuestConfiguration.banditSkins) + ".png");
	protected static final UUID MODIFIER_UUID = UUID.fromString("5CD17E52-A79A-43D3-A529-90FDE04B181E");
	protected static final AttributeModifier MODIFIER = (new AttributeModifier(MODIFIER_UUID, "Drinking speed penalty", -0.34D, 0)).setSaved(false);
	protected static final DataParameter<Boolean> IS_DRINKING = EntityDataManager.<Boolean>createKey(EntitySentry.class, DataSerializers.BOOLEAN);
	protected static final DataParameter<Byte> CLIMBING = EntityDataManager.<Byte>createKey(EntitySentry.class, DataSerializers.BYTE);

	public boolean inCombat()
	{
		return this.getAttackTarget() != null || this.getRevengeTarget() != null || this.inCombat;
	}
	
	public static String NAME = "sentry";
	static
	{
		if (ToroQuestConfiguration.specificEntityNames)
		{
			NAME = ToroQuestEntities.ENTITY_PREFIX + NAME;
		}
	}
	
	public static void init(int entityId)
	{
		EntityRegistry.registerModEntity(new ResourceLocation(ToroQuest.MODID, NAME), EntitySentry.class, NAME, entityId, ToroQuest.INSTANCE, 80, 1,
				true, 0x8f3026, 0xe0d359);
	}
	
	@Override
	protected void entityInit()
	{
		super.entityInit();
        this.getDataManager().register(CLIMBING, Byte.valueOf((byte)0));
        this.getDataManager().register(IS_DRINKING, Boolean.valueOf(false));
	}

	public static void registerRenders()
	{
		RenderingRegistry.registerEntityRenderingHandler(EntitySentry.class, new IRenderFactory<EntitySentry>()
		{
			@Override
			public Render<EntitySentry> createRenderFor(RenderManager manager)
			{
				return new RenderSentry(manager);
			}
		});
	}
	
    //private static final AttributeModifier SPRINTING_SPEED_BOOST = (new AttributeModifier(SPRINTING_SPEED_BOOST_ID, "Sprinting speed boost", 10.30000001192092896D, 2)).setSaved(false);

	@Override
	public void setSprinting( boolean b )
	{
		if ( this.getAttackTarget() != null && !this.isFleeing() && !(this.getHeldItemMainhand().getItem() instanceof ItemBow) ) AIHelper.faceEntitySmart(this, this.getAttackTarget());
		super.setSprinting(b);
	}
	
	// ========================== CUSTOM CHAT ==========================
	
	public String getChatName()
    {
		return "Bandit";
    }
	
	@Override
	public boolean hasCustomName()
    {
        if ( this.actionTimer <= 3 || this.getCustomNameTag() == null || this.getCustomNameTag().equals("...") || this.getCustomNameTag().equals(this.getChatName()) )
        {
    		this.setAlwaysRenderNameTag(false);
        	return false;
        }
        else
        {
    		this.setAlwaysRenderNameTag(true);
        	return true;
        }
    }
		
	// =================================================================
	
	public int actionTimer = 5;
	
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
	    return 1.89F;
	}
	
	@Override
	protected boolean canDespawn()
	{
		return this.despawn;
	}
	
	@Override
	public int getHorizontalFaceSpeed()
	{
		return 5;
	}
		
	public EntitySentry(World worldIn)
	{
		super(worldIn);
        this.setSize(0.6F, 1.95F);
        this.setCombatTask();
        
		this.stepHeight = 2.05F;

        this.setCustomNameTag("...");
		this.setAlwaysRenderNameTag(true);
        
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
	}
	
	//===================================================== Chat =======================================================

	public void chat( EntityPlayer player, String message, @Nullable String extra )
	{
		if ( ToroQuestConfiguration.guardsHaveDialogue && !(this instanceof EntityOrc) )
		{
			if ( this.getDistance(player) > 12 ) return;
			
			this.getLookHelper().setLookPositionWithEntity(player, 30.0F, 30.0F);
			this.faceEntity(player, 30.0F, 30.0F);
			
			if ( player.world.isRemote )
			{
				return;
			}
			
			try
			{
				int i = player.world.rand.nextInt(Integer.parseInt( TextComponentHelper.createComponentTranslation(player, "entity.toroquest.bandit."+message, new Object[0]).getUnformattedText() ));
				String s = TextComponentHelper.createComponentTranslation(player, "entity.toroquest.bandit."+message+i, new Object[0]).getUnformattedText().replace("@p", player.getDisplayNameString());
				
				if ( extra != null )
				{
					s.replace("@e", extra);
				}
				
				player.sendMessage(new TextComponentString("§l" + this.getChatName() + "§r: " + s));
				this.setCustomNameTag(s);
				this.setAlwaysRenderNameTag(true);
				this.actionTimer = 5;
			}
			catch ( Exception e )
			{
				String s = TextComponentHelper.createComponentTranslation(player, "entity.toroquest.bandit."+message, new Object[0]).getUnformattedText().replace("@p", player.getDisplayNameString());
				
				if ( extra != null )
				{
					s.replace("@e", extra);
				}
				
				player.sendMessage(new TextComponentString("§l" + this.getChatName() + "§r: " + s));
				this.setCustomNameTag(s);
				this.setAlwaysRenderNameTag(true);
				this.actionTimer = 5;
			}
			
			this.playSound( SoundEvents.VINDICATION_ILLAGER_AMBIENT, 1.0F, 0.9F + rand.nextFloat()/5.0F );
		}
	}
	
	//===================================================== Interact =======================================================
	
	@Override
	public boolean processInteract( EntityPlayer player, EnumHand hand )
	{
		if ( player == null || player.world.isRemote || !this.isEntityAlive() || player.isInvisible() )
    	{
			return false;
    	}
				
		ItemStack itemstack = player.getHeldItem(hand);
		Item item = itemstack.getItem();
		
		if ( !this.getTame() && this.getHealth() >= this.getMaxHealth() && !this.inCombat() && itemstack.getItem() == Items.EMERALD && CivilizationUtil.getProvinceAt(this.world, this.chunkCoordX, this.chunkCoordZ) == null )
        {
			itemstack.shrink(1);
        	this.getLookHelper().setLookPositionWithEntity(player, 30.0F, 30.0F);
        	this.faceEntity(player, 30.0F, 30.0F);
        	
    		List<EntitySentry> bandits = this.world.<EntitySentry>getEntitiesWithinAABB(EntitySentry.class, new AxisAlignedBB(this.getPosition()).grow(32, 16, 32));
    		{
    			for ( EntitySentry bandit : bandits )
    			{
    				if ( bandit.passiveTimer <= 0 )
    				{
    					bandit.passiveTimer = 4;
        				bandit.getNavigator().tryMoveToEntityLiving(player, 0.4D+rand.nextDouble()/10.0D);
    				}
    				else if ( bandit.passiveTimer < 10 )
    				{
    					bandit.passiveTimer += 2;
    				}
    			}
    		}
        	
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
	                    if ( rand.nextInt(32) == 0 )
	                    {
    						this.setAttackTarget(player);
    						if ( !this.world.isRemote) this.chat(player, "betray", null);
	    				}
	                }
	            }
            }
        }
		else if ( this.getTame() && !this.inCombat() && ToroQuestConfiguration.recruitBandits && item.equals(Item.getByNameOrId("toroquest:recruitment_papers")) )
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
			
			List<EntitySentry> bandits = this.world.getEntitiesWithinAABB(EntitySentry.class, new AxisAlignedBB(this.getPosition()).grow(32, 16, 32), new Predicate<EntitySentry>()
    		{
    			public boolean apply(@Nullable EntitySentry entity)
    			{
    				return true;
    			}
    		});
			
			for ( EntitySentry b : bandits )
			{
				b.passiveTimer = 8;
			}
			
			newEntity.spawnedNearBandits = true;
			world.spawnEntity(newEntity);
			newEntity.setMeleeWeapon();
			
			Province province = CivilizationUtil.getProvinceAt(player.getEntityWorld(), player.chunkCoordX, player.chunkCoordZ);
			
			if ( province != null && province.getCiv() != null )
			{
				newEntity.recruitGuard(player, province, "civbanditrecruit");
			}
			else
			{
				newEntity.chat(newEntity, player, "nocivbanditrecruit", TextComponentHelper.createComponentTranslation(player, "civilization.null.name", new Object[0]).getFormattedText());
			}
			
		}
		else if ( this.canTalk )
    	{
    		this.canTalk = false;
    		this.chat(player, "moreemeralds", null);
        }
		return true;
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

        if ( source.getTrueSource() == this || source.getTrueSource() instanceof EntitySentry )
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
	
	//===================================================== Death =======================================================

	@Override
	public void onDeath(DamageSource cause)
	{
		if ( this.boat != null )
		{
			if ( !this.boat.isBeingRidden() )
			{
				this.boat.setDead();
				this.boatTimer = 5;
			}
		}
		this.replaceItemInInventory(100 + EntityEquipmentSlot.HEAD.getIndex(), ItemStack.EMPTY);
		super.onDeath(cause);
		if ( this.getRevengeTarget() instanceof EntityPlayer ) this.dropLoot();
	}
	
	protected void dropMask()
	{
		ItemStack stack = new ItemStack(Item.getByNameOrId("toroquest:bandit_helmet"));
		EntityItem dropItem = new EntityItem(world, posX, posY, posZ, stack.copy());
		world.spawnEntity(dropItem);
	}
	
	public void dropLoot()
	{
		if (!this.world.isRemote)
		{
			if ( ToroQuestConfiguration.banditsDropMasks )
			{
				dropMask();
			}
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
    	this.setLeftHanded(false);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(ToroQuestConfiguration.banditBaseHealth);
	    this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(ToroQuestConfiguration.banditAttackDamage);
    	this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(ToroQuestConfiguration.banditArmor);
    	this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(ToroQuestConfiguration.banditArmorToughness);
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(40.0D);
    	this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.393D+rand.nextDouble()/50.0D);
    	
    	// this.getEntityAttribute(SharedMonsterAttributes.SPRINTING_SPEED_BOOST).setBaseValue(0.395D+rand.nextDouble()/50.0D);

    	this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.25D);
    }
	
	public boolean isFleeing()
	{
		if ( this.fleeing || this.forceFleeing )
		{
			super.setSprinting(true);
			return true;
		}
		else
		{
			return false;
		}
	}
	//===================================================== Task AI =======================================================

	protected void initEntityAI()
	{
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(2, new EntityAIRangedFlee(this, 0.7D));
		this.tasks.addTask(2, new EntityAIFlee(this, 0.7D));
        //this.tasks.addTask(4, new EntityAIBreakDoorBandit(this));
        this.tasks.addTask(4, new EntityAIOpenDoor(this, false));
        this.tasks.addTask(5, new EntityAIZombieLeap(this, 0.38D, true)
        {
        	@Override
			public boolean shouldExecute()
		    {
		        if ( EntitySentry.this.isDrinkingPotion() || EntitySentry.this.isFleeing() || EntitySentry.this.flanking || EntitySentry.this.blocking || EntitySentry.this.getHeldItemMainhand().getItem() instanceof ItemBow )
		        {
			        return false;
		        }
		        return super.shouldExecute();
		    }
        });

        if ( !(this instanceof EntityOrc) )
        {
	        this.tasks.addTask(6, new EntityAISmartTempt(this, 0.55D, Items.EMERALD)
	        {
//	        	@Override
//				public boolean shouldExecute()
//			    {
//	        		super.shouldExecute();
//			        if ( EntitySentry.this.getAttackTarget() != null || EntitySentry.this.isBurning() )
//			        {
//				        return false;
//			        }
//			        return super.shouldExecute();
//			    }
	        	
	        	@Override
	            public boolean shouldExecute()
	            {
	        		if ( EntitySentry.this.flanking || EntitySentry.this.isRiding() || EntitySentry.this.getAttackTarget() != null || EntitySentry.this.isFleeing() )
	        		{
	        			return false;
	        		}
	        		return super.shouldExecute();
	            }
	        	
			});
        }
	    this.tasks.addTask(8, new EntityAIWanderAvoidWater(this, 0.6D)
	    {
	    	
	    	@Override
            public boolean shouldExecute()
            {
	    		if ( EntitySentry.this.flanking || EntitySentry.this.isRiding() || EntitySentry.this.getAttackTarget() != null || EntitySentry.this.isFleeing() )
        		{
        			return false;
        		}
        		return super.shouldExecute();
            }
	    	
	    	@Nullable
    	    protected Vec3d getPosition()
    	    {
    	        if (this.entity.isInWater())
    	        {
    	            Vec3d vec3d = RandomPositionGenerator.getLandPos(this.entity, 15, 7);
    	            return vec3d == null ? super.getPosition() : vec3d;
    	        }
    	        else
    	        {
    	            if ( this.entity.getNavigator().noPath() && this.entity.getRNG().nextFloat() >= 0.002F )
    	            {
						return RandomPositionGenerator.getLandPos(this.entity, 15, 7);
    	            }
					else
					{
						return super.getPosition();
					}
    	        }
    	    }
		});
        this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityLivingBase.class, 8.0F)
        {
        	@Override
            public boolean shouldExecute()
            {
        		if ( EntitySentry.this.flanking || EntitySentry.this.isRiding() || EntitySentry.this.getAttackTarget() != null || EntitySentry.this.isFleeing() )
        		{
        			return false;
        		}
        		return super.shouldExecute();
            }
        });
        this.tasks.addTask(10, new EntityAILookIdle(this)
        {
        	@Override
            public boolean shouldExecute()
            {
        		if ( EntitySentry.this.flanking || EntitySentry.this.isRiding() || EntitySentry.this.getAttackTarget() != null || EntitySentry.this.isFleeing() )
        		{
        			return false;
        		}
        		return super.shouldExecute();
            }
        });
        if ( !ToroQuestConfiguration.orcsAreNeutral || !(this instanceof EntityOrc) )
        {
        	this.targetTasks.addTask(0, new EntityAIBanditAttack(this));
        }
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0])
		{
			@Override
			public boolean shouldExecute()
			{
	    		EntityLivingBase attacker = this.taskOwner.getRevengeTarget();
		        return attacker != null && this.isSuitableTarget(attacker, false) && attacker.getClass() != taskOwner.getClass();
			}
		});
	}
	
	public void setCombatTask()
	{
	    this.aiArrowAttack.setAttackCooldown(40);
		this.tasks.addTask(4, new AIAttackWithSword(this, 0.65D));
		this.tasks.addTask(5, this.aiArrowAttack);    
    	this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(ToroQuestConfiguration.guardAttackDamage);
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

	@Override
    protected float getWaterSlowDown()
    {
        return this.isHandActive()?0.81F:0.92F;
    }
	
	protected EntityBoat boat;
	protected int boatTimer = 0;
	public int aggroTimer = 0;
	public int flankingTimer = 0;
	
	@Override
	public void onLivingUpdate() // aaa
	{
		if ( !this.world.isRemote )
		{
			if ( this.getRidingEntity() instanceof EntityBoat )
			{
				if ( this.getRidingEntity() == this.boat )
				{
		   			if ( this.getAttackTarget() != null )
		   			{
		   				double d0 = this.getAttackTarget().posX - this.posX;
	 	                double d1 = this.getAttackTarget().posZ - this.posZ;
	 	                double f = 1+MathHelper.sqrt(d0 * d0 + d1 * d1);
	                    	   				
		   				if ( f < 7 && this.getHeldItemMainhand().getItem() instanceof ItemBow )
		   				{
	//	   					this.boat.setDead();
	//	   					this.boatTimer = 5;
		   				}
		   				else
		   				{
			   				this.boat.addVelocity(d0 / f * 0.05D, 0, d1 / f * 0.05D);
		   				}
		   			}
		   			else
		   			{
		   				this.boat.addVelocity(this.boat.getHorizontalFacing().getFrontOffsetX()/32.0D+this.rand.nextGaussian()/32.0D, 0, this.boat.getHorizontalFacing().getFrontOffsetZ()/32.0D+this.rand.nextGaussian()/32.0D);
		   			}
		   			
		   			this.boat.velocityChanged = true;
		   			
		   			try
			    	{
				    	PathPoint p = this.getNavigator().getPath().getFinalPathPoint();
		
				        double d0 = (p.x - this.boat.posX) * 2;
				        double d2 = (p.z - this.boat.posZ) * 2;
				        
				        float f = (float)(MathHelper.atan2(d2, d0) * (180D / Math.PI)) - 90.0F;
				        this.boat.rotationYaw = f*0.5F+this.rand.nextFloat()*0.05F;
			    	}
			    	catch ( Exception err ) {}
		   			
		   			// CAMERA
		        	this.rotationPitch = 0.0F;
		        	this.posX = this.boat.posX;
		        	this.posY = this.boat.posY;
		        	this.posZ = this.boat.posZ;
		        	this.velocityChanged = true;
		        	this.rotationYaw = this.boat.rotationYaw;
		        	this.rotationYawHead = this.boat.rotationYaw;
		        	
		   			// if ( this.boat.collidedHorizontally || this.boat.collidedVertically ) this.boat.setDead(); // ( this.boat.isEntityInsideOpaqueBlock() ) this.boat.setDead();
		   			if ( this.isEntityInsideOpaqueBlock(this.boat) || !this.boat.isInWater() )
		   			{
		   				this.boat.setDead();
		    			this.boatTimer = 5;
		   			}
				}
				else
				{
					if ( this.getRidingEntity().getPassengers().size() > 1 )
					{
	
					}
					else
					{
						this.boat = (EntityBoat) this.getRidingEntity();
					}
				}
			}
			else if ( this.getAttackTarget() != null )
	    	{
				if ( this.isFleeing() )
				{
					super.setSprinting(true);
				}
				else
				{
					if ( EntitySentry.this.getHeldItemMainhand().getItem() instanceof ItemBow )
					{
						
					}
					else
					{
			    		this.faceEntity( this.getAttackTarget(), 30.0F, 30.0F);
					}
		    		this.getLookHelper().setLookPositionWithEntity(this.getAttackTarget(), 30.0F, 30.0F);
				}
	    	}
		}
    	
		super.onLivingUpdate(); // *** === *** === ***
				
		if ( this.world.isRemote )
		{
			return;
		}
		
		if ( this.getRidingEntity() instanceof EntityBoat )
		{
			// CAMERA
        	this.rotationPitch = 0.0F;
        	this.posX = this.getRidingEntity().posX;
        	this.posY = this.getRidingEntity().posY;
        	this.posZ = this.getRidingEntity().posZ;
        	this.velocityChanged = true;
        	this.rotationYaw = this.getRidingEntity().rotationYaw;
        	this.rotationYawHead = this.getRidingEntity().rotationYaw;
		}
		else if ( this.getAttackTarget() != null && !this.isFleeing() )
    	{
			if ( EntitySentry.this.getHeldItemMainhand().getItem() instanceof ItemBow )
			{
				
			}
			else
			{
	    		this.faceEntity( this.getAttackTarget(), 30.0F, 30.0F);
			}
    		this.getLookHelper().setLookPositionWithEntity(this.getAttackTarget(), 30.0F, 30.0F);
    	}
		
		if ( this.potionImmunity > 0 ) this.potionImmunity--;
		
			// FLANKING
			if ( this.flanking )
			{
				if ( 
					   this.getNavigator().noPath()
					|| this.getNavigator().getPath().getFinalPathPoint() == null
					|| this.getAttackTarget() == null
					|| this.getDistance(this.getAttackTarget()) >= 7.5
					|| this.blocking
					|| this.isFleeing() 
				)
				{
					this.flanking = false;
					this.flankingTimer = 0;
					this.getNavigator().clearPath();
				}
				else
				{
					this.flankingTimer++;
					
					if ( this.flankingTimer < 8 )
					{
						this.setSprinting(true);
					}
					else
					{
						this.setSprinting(false);
						double speed = (Math.abs(this.motionX)+(Math.abs(this.motionZ)));
						if ( this.flankingTimer > 64 || speed < 0.02D || this.getDistanceSq(this.getNavigator().getPath().getFinalPathPoint().x,this.getNavigator().getPath().getFinalPathPoint().y,this.getNavigator().getPath().getFinalPathPoint().z) < 3 - speed * 6 )
						{
							this.flanking = false;
							this.flankingTimer = 0;
							this.getNavigator().clearPath();
						}
					}
				}
			}
			else
			{
				this.flankingTimer = 0;
			}
		
		if ( this.ticksExisted % 100 == 0 )
    	{
    		this.heal(1.0f);
	        this.setSprinting(false);
	        
//	    	if ( this.getHealth() > this.fleeModifier*this.getMaxHealth() )
//	    	{
//	    		this.fleeing = false;
//	    	}
	    	
    		if ( this.despawnTimer < 100 && --this.despawnTimer < 0 )
    		{
    			List<EntityPlayer> nearbyPlayers = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(this.getPosition()).grow(32, 16, 32));
				if ( nearbyPlayers.isEmpty() || world.getWorldTime() == 22000 || this.despawnTimer < -100 )
				{
					this.despawn = true;
	    			this.setHealth(0);
	    			this.setDead();
	    			return;
				}
    		}
    		
    		if ( this.getAttackTarget() != null )
    		{
        		if ( !this.climbUntilPlatform && rand.nextBoolean() )
        		{
        			this.climbingTimer = 0;
        		}
        		
    			// ==========================================================================================
    			// ==========================================================================================
    			if ( !this.isDrinkingPotion() && !this.blocking && !this.isFleeing() && !(this.getHeldItemMainhand().getItem() instanceof ItemBow) && this.getDistance(this.getAttackTarget()) <= 6.5 && !this.isInWater() ) // && this.rand.nextBoolean() // && this.getDistance(this.getAttackTarget()) > 2 )
    	        {
    	        	try
    	        	{
    	        		double x = this.getAttackTarget().getPositionVector().x;
    	        		double z = this.getAttackTarget().getPositionVector().z;
    	        		
    	        		double xdif = x-this.getPositionVector().x; // XXX
    	        		double xabs = Math.abs(xdif);
    	        		
    	        		// 1
	    				double zdif = z-this.getPositionVector().z;
    	        		double zabs = Math.abs(zdif);

	    				// 4
	    				double xz = xabs + zabs;
	    				
	    				double xratio = xdif / xz;
	    				double zratio = zdif / xz;
	    				
	    				x += xratio*2;
	    				z += zratio*2;
	    				
	    				double dcap = MathHelper.clamp(this.getDistance(this.getAttackTarget())*(1.0D+this.rand.nextDouble()/3.0D), 2.5D+this.rand.nextDouble(), 6.0D);
	    				
	    				xratio *= dcap;
	    				zratio *= dcap;
	    				
	    				if ( xabs > zabs )
	    				{
	    					z += this.rand.nextBoolean()?xratio:-xratio;
	    				}
	    				else
	    				{
	    					x += this.rand.nextBoolean()?zratio:-zratio;
	    				}
	    				
	    				BlockPos pos = EntityAIRaid.findValidSurface(this.getEntityWorld(), new BlockPos(x,this.getAttackTarget().getPosition().getY(),z), 4);
	    				
	    				if ( pos != null && this.getNavigator().tryMoveToXYZ(pos.getX()+0.5D, pos.getY()+0.5D, pos.getZ()+0.5D, 0.65D) )
	    	        	{
	    			    	this.allStance(true);
	    	        		this.getMoveHelper().strafe(0.2F, this.strafeHor);
	    	        		this.setSprinting(false);
	    	        		this.flanking = true;
	    	        		//this.faceEntitySmart(this.getAttackTarget());
	    	        		//System.out.println("!!!???");
	    	        		double dist = this.getDistanceSq(this.getAttackTarget());
	    	        		Vec3d velocityVector = new Vec3d(this.posX - this.getAttackTarget().posX, 0, this.posZ - this.getAttackTarget().posZ);
	    	        		if ( velocityVector != null )
	    	        		{
		    	        		double push = (8.0D+dist);
								this.addVelocity((velocityVector.x)/push, -0.01D, (velocityVector.z)/push);
								this.velocityChanged = true;

	    	        		}
		    				//this.world.setBlockState(new BlockPos(x,this.posY-1,z), Blocks.GOLD_BLOCK.getDefaultState());
	    	        	}
    	        	}
    	        	catch ( Exception e )
    	        	{
    	        		
    	        	}
    	        }
    			// ==========================================================================================
    			// ==========================================================================================
    			
    			
    			
//    			if ( this.ticksExisted == 100 && this.getDistance(this.getAttackTarget()) > 16 )
//    			{
//    				this.setAttackTarget(null);
//    				return;
//    			}
    			
    			
    			
    			else if ( this.aggroTimer++ > 4 && !this.canEntityBeSeen(this.getAttackTarget() )) // && this.getDistance(this.getAttackTarget()) > 12 && !(this.getHeldItemMainhand().getItem() instanceof ItemBow) )
    			{
    				if ( this.getAttackTarget().getPositionVector() != null )
    				{
	    				Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this, 16, 6, this.getAttackTarget().getPositionVector());
			            if ( vec3d != null && this.getNavigator().tryMoveToXYZ(vec3d.x, vec3d.y, vec3d.z, 0.65D) )
				        {
				        	this.forceFleeing = true;
					        this.setRevengeTarget(this.getAttackTarget());
					        //if ( !(this.getAttackTarget() instanceof EntityPlayer) )
				        	{
					        	this.setAttackTarget(null);
				        	}
				        	this.aggroTimer = 0;
				        	return;
				        }
    				}
    			}
    		}
    		else
    		{
    			//this.aggroTimer = 0;
    			this.fleeing = false;
    		}
    		
    		if ( this.boat != null )
    		{
    			if ( !this.boat.isBeingRidden() )
    			{
    				this.boat.setDead();
    				this.boatTimer = 5;
    			}
    		}
    		
    		if ( ToroQuestConfiguration.banditsUseBoats )
    		{
	    		if ( this.boatTimer < 1 )
	    		{
		    		if ( !this.isRiding() && this.isInWater() && this.motionY < 0.12D && this.motionY > -0.12D && !this.getNavigator().noPath() && !this.isDead ) // this.getAttackTarget() == null
		       		{
		       			this.motionX = 0;
		       			this.motionY = 0;
		       			this.motionZ = 0;
		       			this.swingArm(EnumHand.MAIN_HAND);
		       			boat = new EntityBoat(this.world);
		       			BlockPos pos = this.getPosition();
						this.setPosition(pos.getX()+0.5D,pos.getY()+0.5D,pos.getZ()+0.5D);
						boat.setPosition(pos.getX()+0.5D,pos.getY()+0.5D,pos.getZ()+0.5D);
		       			this.world.spawnEntity(boat);
		       			this.startRiding(boat);
		       		}
	    		}
	    		else
	    		{
	    			this.boatTimer--;
	    		}
    		}
    		
    		if ( this.actionTimer > 0 )
			{
				this.actionTimer--;
				if ( this.actionTimer <= 3 )
				{
					this.setCustomNameTag("...");
					this.setAlwaysRenderNameTag(false);
				}
			}
			else
			{
				this.setCustomNameTag("...");
				this.setAlwaysRenderNameTag(false);
			}
    		
    		if ( this.passiveTimer > 0 )
    		{
    			this.passiveTimer--;
    		}
    		
    		if ( this.getHeldItemMainhand().isEmpty() && this.getHeldItemOffhand().isEmpty() )
    		{
    			this.addEquipment();
    		}
    		
    	}
		// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
		
		ItemStack iStackM = this.getHeldItemMainhand();
		ItemStack iStackO = this.getHeldItemOffhand();

		if ( iStackO.getItem() instanceof ItemShield )
		{
			if ( !this.flanking && !this.isFleeing() ) this.sentryTypeTank( );
		}
		else if ( iStackM.getItem() instanceof ItemBow )
		{
			this.sentryTypeRanged( );
		}
		else
		{
			if ( !this.flanking && !this.isFleeing() ) this.sentryTypeDPS( );
		}
		
		if ( this.isEntityAlive() && this.getHealth() > 0 )
		{
			// UPDATE DRINKING
			if ( this.isDrinkingPotion() )
	        {
	            PotionType potiontype = PotionTypes.HEALING;
	            this.setHeldItem(EnumHand.OFF_HAND, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), potiontype));
	            
	            // START BACKING UP
	            if ( this.getAttackTarget() != null )
            	{
            		float dist = this.getDistance(this.getAttackTarget());
					if ( this.onGround && dist <= 36 )
					{
		            	float push = (float)(8.0F+(dist*dist)*6);
						Vec3d velocityVector = new Vec3d(this.posX-this.getAttackTarget().posX, 0, this.posZ-this.getAttackTarget().posZ);
						if ( velocityVector != null )
    	        		{
							this.addVelocity(velocityVector.x/push,0,velocityVector.z/push);
							this.velocityChanged = true;
						}
						
						try
						{
							if ( !this.hasPath() )
							{
						        Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this, 8, 4, this.getAttackTarget().getPositionVector());
								if ( vec3d != null )
								{
									if ( this.getNavigator().tryMoveToXYZ(vec3d.x, vec3d.y, vec3d.z, 0.5D) )
									{
										this.getMoveHelper().strafe( -0.5F, 0.0F );
									}
								}
							}
						}
						catch ( Exception e ) {}
					}
            	}
	            
	            if ( this.potionUseTimer >= 12 && this.potionUseTimer % 4 == 0 )
	            {
	            	this.world.playSound((EntityPlayer)null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_GENERIC_DRINK, this.getSoundCategory(), 0.35F, 0.8F + this.rand.nextFloat() * 0.4F);
	            	if ( rand.nextInt(3) == 0 )
	            	{
		            	this.world.playSound((EntityPlayer)null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_WITCH_DRINK, this.getSoundCategory(), 1.0F, 0.8F + this.rand.nextFloat() * 0.4F);
	            	}
	            }
	            if (this.potionUseTimer-- <= 0)
	            {
	            	this.useHealingPotion = false;
	            	this.swingProgress = -10;
	            	this.setSprinting(false);
	                this.setDrinkingPotion(false);
	                ItemStack itemstack = this.getHeldItemOffhand();
	                //this.swingArm(EnumHand.OFF_HAND);
	                if (itemstack.getItem() == Items.POTIONITEM)
	                {
	                	potiontype = PotionTypes.STRONG_HEALING;
	                	itemstack = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), potiontype);
	    	            
	                    List<PotionEffect> list = PotionUtils.getEffectsFromStack(itemstack);
	
	                    if (list != null)
	                    {
	                        for (PotionEffect potioneffect : list)
	                        {
	                            this.addPotionEffect(new PotionEffect(potioneffect));
	                        }
	                    }
	                    ItemStack stack = new ItemStack(Items.GLASS_BOTTLE, 1);
	    				EntityItem dropItem = new EntityItem(world, posX, posY, posZ, stack.copy());
	    				world.spawnEntity(dropItem);
	    				this.limitPotions--;
	    				this.potionUseTimer = 10;
	    				dropItem.motionY = rand.nextDouble()/10.0D;
	    				dropItem.motionZ = (rand.nextDouble() - 0.5D)/10.0D;
	    				dropItem.motionX = (rand.nextDouble() - 0.5D)/10.0D;
	    				this.playTameEffect(true);
	        			this.world.setEntityState(this, (byte)7);
	                    this.world.playSound((EntityPlayer)null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_WITCH_DRINK, this.getSoundCategory(), 2.0F, 0.8F + this.rand.nextFloat() * 0.4F);
	                }
					this.playSound(SoundEvents.ITEM_ARMOR_EQUIP_IRON, 1.2F, 0.8F + rand.nextFloat()/5.0F );
					this.setHeldItem(EnumHand.MAIN_HAND, this.weaponMain);
	                this.setHeldItem(EnumHand.OFF_HAND, this.weaponOff);
	                this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(MODIFIER);
	            }
	        }
			// START DRINKING
	        else if ( this.limitPotions > 0 && this.useHealingPotion && this.getHealth()/2.0F <= this.getMaxHealth() & this.getAttackTarget() != null && this.getDistance(this.getAttackTarget()) >= 3 )
	        {
	        	this.getNavigator().clearPath();
	        	this.faceEntity(this.getAttackTarget(), 30.0F, 30.0F);
	        	this.getLookHelper().setLookPositionWithEntity(this.getAttackTarget(), 30.0F, 30.0F);
	            this.weaponMain = this.getHeldItemMainhand();
	            this.weaponOff = this.getHeldItemOffhand();
	            this.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
	            PotionType potiontype = PotionTypes.HEALING;
	            this.setHeldItem(EnumHand.OFF_HAND, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), potiontype));
	            this.potionUseTimer = 45;
	            this.setDrinkingPotion(true);
	            this.world.playSound((EntityPlayer)null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_WITCH_DRINK, this.getSoundCategory(), 2.0F, 0.8F + this.rand.nextFloat() * 0.4F);
	            IAttributeInstance iattributeinstance = this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
	            iattributeinstance.removeModifier(MODIFIER);
	            iattributeinstance.applyModifier(MODIFIER);
	        }
		}
	}
	
	// FACE AWAY
	
	public void faceAwayEntity(Entity entityIn)
    {
        double d0 = this.posX - entityIn.posX;
        double d2 = this.posZ - entityIn.posZ;
        double d1;

        if (entityIn instanceof EntityLivingBase)
        {
            EntityLivingBase entitylivingbase = (EntityLivingBase)entityIn;
            d1 = entitylivingbase.posY + (double)entitylivingbase.getEyeHeight() - (this.posY + (double)this.getEyeHeight());
        }
        else
        {
            d1 = (entityIn.getEntityBoundingBox().minY + entityIn.getEntityBoundingBox().maxY) / 2.0D - (this.posY + (double)this.getEyeHeight());
        }

        double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
        float f = (float)(MathHelper.atan2(d2, d0) * (180D / Math.PI)) - 90.0F;
        float f1 = (float)(-(MathHelper.atan2(d1, d3) * (180D / Math.PI)));
        this.rotationPitch = f1;
        this.rotationYaw = f;
    }
		
	//==================================================== Take Damage ===========================================================
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		if (this.world.isRemote)
        {
            return false;
        }
		
		this.aggroTimer = 0;

		Entity e = source.getTrueSource();
		
		if (source.getTrueSource() == this)
        {
            amount = 0.0F;
        }
		
		if ( source == DamageSource.IN_WALL || source == DamageSource.CRAMMING || source == DamageSource.CACTUS )
		{
			return false;
		}
		
		if ( source == DamageSource.FALL )
		{
			amount = amount/2.0F;
			if ( amount <= 2 )
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
			if ( this instanceof EntityOrc )
			{
				Vec3d vec3d = RandomPositionGenerator.getLandPos(this, 8, 4);
	            if ( vec3d != null )
	            {
			        this.getNavigator().tryMoveToXYZ(vec3d.x, vec3d.y, vec3d.z, 0.5D);
	            }
			}
			else if ( this.rand.nextBoolean() )
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
							this.velocityChanged = true;
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
			return super.attackEntityFrom(source, amount); // ***
		}
		
		if ( e instanceof EntitySentry )
		{
			if ( e instanceof EntityOrc )
			{
				if ( this instanceof EntityOrc )
				{
					if ( this.getAttackTarget() == e ) this.setAttackTarget(null);
					return false;
				}
			}
			else
			{
				if ( this instanceof EntityOrc )
				{
					
				}
				else
				{
					if ( this.getAttackTarget() == e ) this.setAttackTarget(null);
					return false;
				}
			}
		}
		
		if ( e instanceof EntityLivingBase ) 
		{
			// this.flanking = false;

			if ( this.boat != null && this.getDistance(e) < 5 )
    		{
				this.boat.setDead();
				this.boatTimer = 5;
    		}
			
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

			if ( rand.nextBoolean() )
			{
				this.blockingTimer = 4 + rand.nextInt(5);
			}
			
			double dist = e.getDistanceSq(this);
			
			if ( !source.isProjectile() && !source.isMagicDamage() && !source.isFireDamage() )
			{
				if ( e instanceof EntityLivingBase )
				{
					if ( amount >= 5.0F && ( ((EntityLivingBase)e).getHeldItemMainhand().getItem() instanceof ItemAxe || ((EntityLivingBase)e).getHeldItemMainhand().getItem().getRegistryName().toString().contains("halberd") || ((EntityLivingBase)e).getHeldItemMainhand().getItem().getRegistryName().toString().contains("battleaxe") ) )
					{
						this.resetActiveHand();
						this.world.setEntityState(this, (byte)29);
						this.world.setEntityState(this, (byte)30);
						if ( dist < 16 )
						{
							this.canShieldPush = true;
							Vec3d velocityVector = new Vec3d(this.posX - e.posX, 0, this.posZ - e.posZ);
							if ( velocityVector != null )
	    	        		{	
								this.addVelocity((velocityVector.x)/( dist+1 )*MathHelper.clamp(amount, 0.0D, 1.2D), (0.22D-MathHelper.clamp(dist/100.0, 0.0D, 0.16D))*MathHelper.clamp(amount, 0.0D, 1.0D), (velocityVector.z)/( dist+1 )*MathHelper.clamp(amount, 0.0D, 1.2D));
			                	this.velocityChanged = true;
							}
						}
						this.playSound(SoundEvents.ITEM_SHIELD_BLOCK, 1.0F, 0.8F + rand.nextFloat()/5.0F);
						this.playSound(SoundEvents.ITEM_SHIELD_BREAK, 1.0F, 0.8F + rand.nextFloat()/5.0F);
						this.blockingTimer = 50;
						return (super.attackEntityFrom(source, amount/2.0F)); // ***
					}
					else
					{
						this.playSound(SoundEvents.ITEM_SHIELD_BLOCK, 1.0F, 0.8F + rand.nextFloat()/5.0F);
						if ( dist < 16 )
						{
							if ( this.canShieldPush )
							{
								this.canShieldPush = false;
								this.knockBackSmart(((EntityLivingBase)e), (float) (0.25D-dist/100.0D));

//								Vec3d velocityVector = new Vec3d(e.posX - this.posX, 0, e.posZ - this.posZ);
//								if ( velocityVector != null )
//		    	        		{
//									e.addVelocity((velocityVector.x)/( dist+1 )*MathHelper.clamp(amount, 0.0D, 1.2D), (0.22D-MathHelper.clamp(dist/100.0, 0.0D, 0.16D))*MathHelper.clamp(amount, 0.0D, 1.0D), (velocityVector.z)/( dist+1 )*MathHelper.clamp(amount, 0.0D, 1.2D));
//				                	e.velocityChanged = true;
//								}
							}
							else
							{
								if ( e.onGround && !e.isAirBorne )
								{
									this.knockBackSmart(((EntityLivingBase)e), 0.1F);
								}
								else
								{
									this.knockBackSmart(((EntityLivingBase)e), 0.0F);
								}
//								Vec3d velocityVector = new Vec3d(e.posX - this.posX, 0, e.posZ - this.posZ);
//								if ( velocityVector != null )
//		    	        		{
//									e.addVelocity((velocityVector.x)/( dist+8 )*MathHelper.clamp(amount, 0.0D, 1.0D), 0, (velocityVector.z)/( dist+8 )*MathHelper.clamp(amount, 0.0D, 1.0D));
//				                	e.velocityChanged = true;
//								}
							}
						}
						this.world.setEntityState(this, (byte)29);
					}
					return false;
				}
			}
			else if ( source.isProjectile() )
			{
				this.blockingTimer = 6 + rand.nextInt(5);
			}
			this.world.setEntityState(this, (byte)29);
			
			this.playSound(SoundEvents.ITEM_SHIELD_BLOCK, 1.0F, 0.8F + rand.nextFloat()/5.0F);
			
			return false;
		}
		
		if ( super.attackEntityFrom(source, amount) ) // *** run away if can't reach
		{			
			if ( !this.canEntityBeSeen(e) || ( this.getDistance(e) >= 8.0D && this.getNavigator().getPathToEntityLiving(e) == null ) )
			{
				Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this, 16, 6, e.getPositionVector());
				
                if ( vec3d != null && this.getNavigator().tryMoveToXYZ(vec3d.x, vec3d.y, vec3d.z, 0.65D) )
                {
                	this.setRevengeTarget(this.getAttackTarget());
                    this.forceFleeing = true;
			        this.aggroTimer = 0;
                }
			}
			return true;
		}
		return false;
	}
	
	public void knockBackSmart(EntityLivingBase entityIn, float strength)
	{
		try
		{
			Vec3d pos = this.getPositionVector();
	        Vec3d targetPos = entityIn.getPositionVector();
	        entityIn.knockBack(entityIn, strength, pos.x - targetPos.x, pos.z - targetPos.z);
            entityIn.velocityChanged = true;
	    }
	    catch ( Exception e )
	    {
	    	
	    }
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
		
		if ( !(this instanceof EntityOrc) && !this.world.isRemote )
		{
			this.setCustomNameTag("...");
			this.setAlwaysRenderNameTag(true);
		}
		
		this.setCanPickUpLoot(false);
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
		
		// this.setMount();
				
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
		
		//if ( chance > 0 && rand.nextInt(11-chance) == 0 )
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
	        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
	        mount.setHealth(10);
	        mount.width = 0.9F;
	        mount.stepHeight = 1.55F;
	        this.world.spawnEntity(mount);
	        mount.removePassengers();
	        this.startRiding(mount);
	        mount.tasks.addTask(0, new EntityAIDespawn(mount));
	        mount.addVelocity(rand.nextGaussian()/2.0D, 0.1D, rand.nextGaussian()/2.0D);
	        mount.velocityChanged = true;
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
		
		this.aggroTimer = 0;
		
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
	    //entityarrow.setDamage(this.attack);
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
	    if ( compound.hasKey("raidX") && compound.hasKey("raidZ") )
	    {
	    	this.raidX = compound.getInteger("raidX");
	    	this.raidZ = compound.getInteger("raidZ");
	    	this.setRaidLocation(this.raidX, this.raidZ);
	    }
	    if ( compound.hasKey("despawnTimer") )
	    {
	    	this.despawnTimer = compound.getInteger("despawnTimer");
	    }
	    
	    if ( compound.hasKey("bribed") )
	    {
	    	this.bribed = compound.getBoolean("bribed");
	    }
	    super.readEntityFromNBT(compound);
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound compound)
	{
		if ( this.raidX != null && this.raidZ != null && !( this.raidX == 0 && this.raidZ == 0 ) )
		{
			compound.setInteger("raidX", this.raidX);
			compound.setInteger("raidZ", this.raidZ);
		}
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
			if ( x == 0 && z == 0 )
			{
				return;
			}
			this.raidX = x;
			this.raidZ = z;
			this.areaAI.setCenter(x, z);
			this.tasks.addTask(7, this.areaAI);
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
    
	
	public void forwardStance(boolean faceTarget)
	{
		this.stance = this.rand.nextInt(6)+5;
		this.strafeHor = this.getStrafe(this.stance);
		if ( faceTarget && !this.isFleeing() ) AIHelper.faceEntitySmart(this, this.getAttackTarget());

	}
	
	public void allStance(boolean faceTarget)
	{
		this.stance = this.rand.nextInt(8)+3;
		this.strafeHor = this.getStrafe(this.stance);
		if ( faceTarget && !this.isFleeing() ) AIHelper.faceEntitySmart(this, this.getAttackTarget());
	}
	
	//===================================================== DPS =======================================================
    
	private void sentryTypeDPS( )
	{
		if ( this.getAttackTarget() != null && this.getAttackTarget().isEntityAlive() && this.getAttackTarget().getClass() != this.getClass() )
		{
	        double dist = this.getDistanceSq(this.getAttackTarget());
            
	        if ( !this.inCombat )
	        {
	        	this.getNavigator().clearPath();
				this.inCombat = true;
				this.forwardStance(false);
				this.blockingTimer = (int)MathHelper.clamp((rand.nextInt((int)dist+25)), 10, 70);
	        }
	        else if ( this.blockingTimer-- < 0 )
			{
				this.allStance(true);
				this.blockingTimer = (int)MathHelper.clamp((rand.nextInt((int)dist+25)), 10, 70);
			}

			// THROW POTION: 1 = can throw, 0 = ready to throw, -1 = can not throw
			if ( this.limitPotions > 0 && !this.isDrinkingPotion() )
			{
				if ( this.hasSplashPotion == 1 && dist >= 20 && dist <= 160 && this.getHeldItemOffhand().isEmpty() && !this.isBesideClimbableBlock() )
				{
					if ( world.getEntitiesWithinAABB(EntitySentry.class, new AxisAlignedBB(this.getPosition()).grow(3, 3, 3)).size() < 2 )
					{
						this.hasSplashPotion = 0;
						PotionType potiontype = PotionTypes.SLOWNESS;
			            this.setHeldItem(EnumHand.OFF_HAND, PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), potiontype));
					}
				}
				else if ( this.hasSplashPotion == 0 )
				{	
					if ( dist >= 10 && dist <= 80 )
					{
						if ( this.canEntityBeSeen(this.getAttackTarget()) && world.getEntitiesWithinAABB(EntitySentry.class, new AxisAlignedBB(this.getPosition()).grow(3, 3, 3)).size() < 2 )
						{
							this.splashPotionTimer--;
							if ( this.splashPotionTimer < 3 )
							{
								if ( this.onGround )
								{
									Vec3d velocityVector = new Vec3d(this.posX - this.getAttackTarget().posX, 0, this.posZ - this.getAttackTarget().posZ);
									if ( velocityVector != null )
			    	        		{
										double push = (1.0D+dist*2.0D);
										this.addVelocity((velocityVector.x)/push, 0.0D, (velocityVector.z)/push);
					                	this.velocityChanged = true;
			    	        		}
								}
								if ( this.splashPotionTimer < 0 )
								{
									this.splashPotionTimer = 10;
									this.hasSplashPotion = -1;
									this.swingArm(EnumHand.OFF_HAND);
									this.throwSplashPotion(this.getAttackTarget());
									this.limitPotions--;
									this.setHeldItem(EnumHand.OFF_HAND, ItemStack.EMPTY );
									this.getNavigator().clearPath();
								}
							}
						}
						else
						{
							this.splashPotionTimer = 6;
						}
					}
					else
					{
						this.splashPotionTimer = 6;
					}
				}
			}
			
			if ( !this.isFleeing() && !this.flanking )
			{
				float strafeMod = 1.0F;
				
				if ( this.stance < 5 )
				{
					// BACKPEDDLE
					this.setSprinting(false);
					if ( dist <= 32 && dist > 2 )
					{
						if ( this.onGround )
						{
				    		//AIHelper.faceEntitySmart(this, this.getAttackTarget());
							Vec3d velocityVector = new Vec3d(this.posX - this.getAttackTarget().posX, 0, this.posZ - this.getAttackTarget().posZ);
							if ( velocityVector != null )
	    	        		{
								double push = (1.0D+3.8D*dist);
								this.addVelocity((velocityVector.x)/push, -0.002D, (velocityVector.z)/push);
			                	this.velocityChanged = true;
	    	        		}
						}
						this.getNavigator().tryMoveToEntityLiving(this.getAttackTarget(), 0.4F); // bau
						this.getMoveHelper().strafe( -1.0F, this.strafeHor );
					}
					else
					{
						this.forwardStance(true);
						this.getNavigator().clearPath();
				    	this.getMoveHelper().strafe( 0.0F, 0.0F );
					}
					if ( this.rand.nextBoolean() ) this.blockingTimer--;
					return;
				}
				else if ( dist <= 2 )
				{
					this.strafeVer = 0.4F;
				}
				else if ( dist <= 4 )
				{
					this.strafeVer = 0.8F;
					strafeMod = 0.9F;
				}
				else if ( dist <= 9 )
				{
					this.strafeVer = 0.9F;
					strafeMod = 0.8F;
				}
				else
				{
					this.strafeVer = 1.0F;
					strafeMod = 0.7F;
				}
							
				if ( this.getNavigator().tryMoveToEntityLiving(this.getAttackTarget(), this.strafeVer) ) // ttt dps
				{
					if ( dist >= 12 )
					{
						this.blockingTimer--;
					}
					else if ( dist <= 3 )
					{
						if ( this.onGround && !this.isSprinting() )
						{
							Vec3d velocityVector = new Vec3d(this.posX - this.getAttackTarget().posX, 0, this.posZ - this.getAttackTarget().posZ);
							if ( velocityVector != null )
	    	        		{
								double push = (1.0D+dist*dist);
								this.addVelocity((velocityVector.x)/push, 0.0D, (velocityVector.z)/push);
			                	this.velocityChanged = true;
	    	        		}
						}
					}
					
					if ( this.posY + 1.5D < this.getAttackTarget().posY )
					{
						this.getMoveHelper().strafe( this.strafeVer, 0.0F );
						if ( this.onGround && this.rand.nextInt(20) == 0 )
						{
							this.addVelocity(0.0D, 0.38D, 0.0D);
		                	this.velocityChanged = true;
						}
					}
					else
					{
						this.getMoveHelper().strafe( this.strafeVer, this.strafeHor*strafeMod );
					}
				}
				else if ( this.getAttackTarget().onGround && !this.getAttackTarget().isAirBorne && this.onGround && !this.isAirBorne && !this.climbUntilPlatform )
				{
		        	// System.out.println("NONE");

					Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this, 16, 6, this.getAttackTarget().getPositionVector());
		            if ( vec3d != null && this.getNavigator().tryMoveToXYZ(vec3d.x, vec3d.y, vec3d.z, 0.65D) )
		            {
				        this.forceFleeing = true;
				        this.setRevengeTarget(this.getAttackTarget());
				        this.aggroTimer = 0;
				        return;
		            }
				}
//				else if ( this.getAttackTarget().onGround && !this.getAttackTarget().isAirBorne )
//				{
//					this.getMoveHelper().strafe( 0.0F, 0.0F );
//					
//					if ( !( (this.getAttackTarget().posY-this.posY)*2.0D > this.getDistance(this.getAttackTarget()) ) && !this.collidedHorizontallyWide(this.posY+this.height) )
//					{
//						Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this, 12, 6, this.getAttackTarget().getPositionVector());
//			            if ( vec3d != null && this.getNavigator().tryMoveToXYZ(vec3d.x, vec3d.y, vec3d.z, 0.5D) )
//			            {
//					        this.forceFleeing = true;
//					        return;
//			            }
//			            else if ( this.onGround && !this.isAirBorne ) 
//			            {
//			            	this.getNavigator().clearPath();
//			            }
//					}
//				}
			}
			//this.blockingTimer--;
		}
		else if ( this.blocking || this.inCombat )
		{
			this.inCombat = false;
			this.blocking = false;
			this.setAttackTarget(null);
	        this.setSprinting(false);
        	this.canShieldPush = true;
			this.resetActiveHand();
			this.activeItemStackUseCount = 0;
	    	this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.25D);
	    	this.strafeVer = 0.0F;
	    	this.getMoveHelper().strafe( 0.0F, 0.0F );
	    	this.getNavigator().clearPath();
			if ( this.getHeldItemOffhand().getItem() instanceof ItemPotion ) setHeldItem(EnumHand.OFF_HAND, ItemStack.EMPTY );
	    	this.hasSplashPotion = 1;
	    	this.splashPotionTimer = 10;
		}
	}
	
	//===================================================== Tank =======================================================
	
	private void sentryTypeTank( )
	{
		if ( this.getAttackTarget() != null && this.getAttackTarget().isEntityAlive() )
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
				this.forwardStance(false);
	    		AIHelper.faceEntitySmart(this, this.getAttackTarget());
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
				this.strafeVer = 0.4F;
			}
			            
	        if ( !this.inCombat )
	        {
				this.inCombat = true;
	        	this.getNavigator().clearPath();
				this.strafeVer = 0.8F;
				this.forwardStance(false);
	        }
			
			if ( !this.blocking && !this.isSprinting() && dist <= 12 && this.blockingTimer <= -(int)(this.stance*5+dist+20) && !(this.getAttackTarget() instanceof EntityVillager) )
			{
				this.allStance(true);
				this.blockingTimer = (int)MathHelper.clamp((rand.nextInt(70)+20-dist), 20, 80);
				this.blocking = true;
				this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
				this.canShieldPush = true;
				this.updateActiveHand();
				this.setActiveHand(EnumHand.OFF_HAND);
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

			if ( this.blocking && this.blockingTimer <= 0 )
			{
				this.blocking = false;
				this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.25D);
				this.allStance(true);
	        	this.canShieldPush = true;
				this.resetActiveHand();
			}
			else if ( !this.blocking && dist <= 64 )
			{
				if ( this.blockingTimer == -12 || this.blockingTimer == -32 || ( this.blockingTimer < -32 && this.blockingTimer % 14 == 0 ) )
				{
					if ( rand.nextInt(4) == 0 )
					{
						this.allStance(true);
					}
				}
			}
			
			if ( !this.isFleeing() && !this.flanking )
			{
				if ( !this.blocking ) // TODO
				{
					float strafeMod = 1.0F;
					
					if ( this.stance < 5 )
					{
						this.setSprinting(false);
						if ( dist <= 30 )
						{
							if ( this.onGround )
							{
								Vec3d velocityVector = new Vec3d(this.posX - this.getAttackTarget().posX, 0, this.posZ - this.getAttackTarget().posZ);
	
								if ( velocityVector != null )
		    	        		{
									double push = (1.0D+5.0D*dist);
									this.addVelocity((velocityVector.x)/push, -0.002D, (velocityVector.z)/push);
				                	this.velocityChanged = true;
		    	        		}
							}
							this.getNavigator().tryMoveToEntityLiving(this.getAttackTarget(), 0.4F); // bau
							this.getMoveHelper().strafe( -1.0F, this.strafeHor );
						}
						else
						{
							this.forwardStance(true);
							this.getNavigator().clearPath();
					    	this.getMoveHelper().strafe( 0.0F, 0.0F );
						}
						if ( this.rand.nextBoolean() ) this.blockingTimer--;
						return;
					}
					else if ( dist <= 2 )
					{
						this.strafeVer = 0.4F;
					}
					else if ( dist <= 4 )
					{
						this.strafeVer = 0.8F;
						strafeMod = 0.9F;
					}
					else if ( dist <= 9 )
					{
						this.strafeVer = 0.9F;
						strafeMod = 0.8F;
					}
					else
					{
						this.strafeVer = 1.0F;
						strafeMod = 0.7F;
					}
								
					if ( this.getNavigator().tryMoveToEntityLiving(this.getAttackTarget(), this.strafeVer) ) // ttt
					{
						if ( dist >= 12 )
						{
							this.blockingTimer--;
	//						if ( this.onGround && this.rand.nextInt(64) == 0 )
	//						{
	//							this.addVelocity(0.0D, 0.32D, 0.0D);
	//		                	this.velocityChanged = true;
	//						}
						}
						else if ( dist <= 3 )
						{
							if ( this.onGround && !this.isSprinting() )
							{
								Vec3d velocityVector = new Vec3d(this.posX - this.getAttackTarget().posX, 0, this.posZ - this.getAttackTarget().posZ);
								if ( velocityVector != null )
								{
									double push = (1.0D+dist*dist);
									this.addVelocity((velocityVector.x)/push, 0.0D, (velocityVector.z)/push);
				                	this.velocityChanged = true;
								}
							}
						}
	//					else if ( this.onGround && this.rand.nextInt(64) == 0 )
	//					{
	//						this.addVelocity(0.0D, 0.32D, 0.0D);
	//	                	this.velocityChanged = true;
	//					}
						
						if ( this.posY + 1.5D < this.getAttackTarget().posY )
						{
							this.getMoveHelper().strafe( this.strafeVer, 0.0F );
							if ( this.onGround && this.rand.nextInt(20) == 0 )
							{
								this.addVelocity(0.0D, 0.38D, 0.0D);
			                	this.velocityChanged = true;
							}
						}
						else
						{
							this.getMoveHelper().strafe( this.strafeVer, this.strafeHor*strafeMod );
						}
					}
					else if ( this.getAttackTarget().onGround && !this.getAttackTarget().isAirBorne && this.onGround && !this.isAirBorne && !this.climbUntilPlatform )
					{
			        	// System.out.println("NONE");
	
						Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this, 16, 6, this.getAttackTarget().getPositionVector());
			            if ( vec3d != null && this.getNavigator().tryMoveToXYZ(vec3d.x, vec3d.y, vec3d.z, 0.65D) )
			            {
					        this.forceFleeing = true;
					        this.setRevengeTarget(this.getAttackTarget());
					        this.aggroTimer = 0;
					        return;
			            }
					}
				}
				else // is blocking
				{
		    		AIHelper.faceEntitySmart(this, this.getAttackTarget());
	
					if ( this.strafeVer < 0.4F )
					{
						if ( !this.world.isRemote && this.onGround )
						{
							Vec3d velocityVector = new Vec3d(this.posX - this.getAttackTarget().posX, 0, this.posZ - this.getAttackTarget().posZ);
							if ( velocityVector != null )
							{
								double push = (1.0D+dist*dist);
								this.addVelocity((velocityVector.x)/push, 0.0D, (velocityVector.z)/push);
			                	this.velocityChanged = true;
							}
		                	
						}
					}
					else if ( this.strafeVer > 0.4F )
					{
						this.strafeVer = 0.4F;
					}
					
					if ( this.getNavigator().tryMoveToEntityLiving(this.getAttackTarget(), this.strafeVer) )
					{
						this.getMoveHelper().strafe( this.strafeVer, this.strafeHor*1.5F);
					}
					else
					{
						this.getMoveHelper().strafe( this.strafeVer*0.5F, this.strafeHor*0.5F );
					}
				}
			}
			this.blockingTimer--;
		}
		else if ( this.blocking || this.inCombat )
		{
			this.inCombat = false;
			this.blocking = false;
			this.setAttackTarget(null);
			this.resetActiveHand();
        	this.canShieldPush = true;
	        this.setSprinting(false);
			this.activeItemStackUseCount = 0;
	    	this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.25D);
	    	this.strafeVer = 0.0F;
	    	this.getMoveHelper().strafe( 0.0F, 0.0F );
	    	this.getNavigator().clearPath();
		}
	}
	
	//===================================================== Ranged =======================================================

	private void sentryTypeRanged( ) // zzz
	{
		
//		if ( isRiding() )
//		{
//	        if ( getRidingEntity() instanceof EntityHorse )
//			{
//	        	if ( hasPath() )
//	        	{
//	        		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
//	        	}
//	        	else
//	        	{
//	        		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
//	        		getRidingEntity().motionX = 0;
//	        		getRidingEntity().motionZ = 0;
//	        		getRidingEntity().rotationYaw = 0;
//	        		getRidingEntity().rotationPitch = 0;
//	        	}
//			}
//		}
//		else if ( this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue() <= 0.0D )
//		{
//			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.392D+rand.nextDouble()/50.0D);
//		}
		
		if ( this.getAttackTarget() != null && this.getAttackTarget().isEntityAlive() )
		{  
			if ( !this.onGround )
			{
				this.motionX/=2.0D;
				this.motionZ/=2.0D;
			}
			
	        if ( !this.inCombat )
	        {
				this.inCombat = true;
	        	this.getNavigator().clearPath();
//				this.strafeVer = 0.8F;
//				this.getStrafe(this.stance) = getStrafe(stance);
	        }
	        
			if ( this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue() <= 0.0D && !this.isRiding() )
			{
				this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.39D+rand.nextDouble()/50.0D);
			}
		}
		else if ( this.blocking || this.inCombat )
		{
			this.inCombat = false;
			this.blocking = false;
			this.setAttackTarget(null);
			this.resetActiveHand();
        	this.canShieldPush = true;
	        this.setSprinting(false);
			this.activeItemStackUseCount = 0;
	    	this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.25D);
	    	this.strafeVer = 0.0F;
	    	this.strafeHor = 0.0F;
	    	this.getMoveHelper().strafe( 0.0F, 0.0F );
	    	this.getNavigator().clearPath();
		}
	}

	//===================================================== Melee Attack =======================================================

	@Override
	public boolean attackEntityAsMob(Entity victim) // atttack
	{
		if ( victim == null || !victim.isEntityAlive() || victim.getClass().equals(this.getClass()) )
		{
			this.setAttackTarget(null);
			return false;
		}
		else
		{
//			if ( this.flanking )
//			{
//				this.flanking = false;
//				this.getNavigator().clearPath();
//			}
			this.aggroTimer = 0;
			this.attackTargetEntityWithCurrentItem(victim);
			this.setSprinting(false);
			return true;
		}
	}
	
	public void attackTargetEntityWithCurrentItem(Entity targetEntity)
	{

		if ( !(this instanceof EntityOrc) && rand.nextInt(5) == 0 )
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

						if (itemstack != null && ( itemstack.getItem() instanceof ItemSword || itemstack.getItem() instanceof ItemAxe ) )
						{
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
				return -0.32F;
			}
			case 4:
			{
				return 0.32F;
			}
			case 5:
			{
				return -0.31F;
			}
			case 6:
			{
				return 0.31F;
			}
			case 7:
			{
				return -0.3F;
			}
			case 8:
			{
				return 0.3F;
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

	protected boolean climbUntilPlatform = false;
	
	public boolean collidedHorizontallyWide(double d)
	{
		try
		{
			IBlockState block = this.world.getBlockState(new BlockPos(this.posX+0.6D,d,this.posZ));

			if ( block.isFullCube() )
			{
				return true;
			}
			
			block = this.world.getBlockState(new BlockPos(this.posX,d,this.posZ+0.6D));
			if ( block.isFullCube() )
			{
				return true;
			}
			
			block = this.world.getBlockState(new BlockPos(this.posX-0.6D,d,this.posZ));
			if ( block.isFullCube() )
			{
				return true;
			}
			
			block = this.world.getBlockState(new BlockPos(this.posX,d,this.posZ-0.6D));
			if ( block.isFullCube() )
			{
				return true;
			}
		}
		catch ( Exception e )
		{
			
		}
		
		return false;
	}
	
//	public boolean collidedDiagnoally(double d)
//	{
//		try
//		{
//			IBlockState block = this.world.getBlockState(new BlockPos(((int)this.posX)+1.5D,d,((int)this.posZ)+0.5D));
//			if ( block.isFullCube() )
//			{
//				return true;
//			}
//			block = this.world.getBlockState(new BlockPos(((int)this.posX)+0.5D,d,((int)this.posZ)+1.5D));
//			if ( block.isFullCube() )
//			{
//				return true;
//			}
//			block = this.world.getBlockState(new BlockPos(((int)this.posX)-0.5D,d,((int)this.posZ)+0.5D));
//			if ( block.isFullCube() )
//			{
//				return true;
//			}
//			block = this.world.getBlockState(new BlockPos(((int)this.posX)+0.5D,d,((int)this.posZ)-0.5D));
//			if ( block.isFullCube() )
//			{
//				return true;
//			}
//		}
//		catch ( Exception e )
//		{
//			
//		}
//		return false;
//	}
	
	public boolean collidedVerticallySmart(double d)
	{
		try
		{
			IBlockState block = this.world.getBlockState(new BlockPos(this.posX,d,this.posZ));
			if ( block.isFullCube() )
			{
				return true;
			}
		}
		catch ( Exception e )
		{
			
		}
		return false;
	}
		
    @Override
    public void onUpdate()
    {
        if ( !this.world.isRemote )
        {
        	if ( this.getAttackTarget() != null )
        	{
        		boolean ch = false;
        		
            	if ( this.motionY < -0.12D && this.collidedHorizontallyWide(this.posY+this.height-0.5D)?ch=true:false )
        		{
        			this.motionY = -0.12D;
        			this.velocityChanged = true;
        		}
            	
	        	if ( this.climbUntilPlatform )
	        	{
	        		boolean cd = this.collidedHorizontallyWide(((int)this.posY)+this.height+0.5D); // DIAGNAL
	        		if ( !ch ) ch = this.collidedHorizontallyWide(this.posY+this.height-0.5D);
	        		boolean cv = this.collidedVerticallySmart(((int)this.posY)+this.height+0.5D);
	        		
	        		if ( cd || ch || cv || this.collidedHorizontally )
	        		{
	        			if ( cv || ( cd && !ch ) )
	        			{
	            			this.doClimbing(true);
	        			}
	        			else
	        			{
	            			this.doClimbing(false);
	        			}
	        		}
	        		else
	        		{
		    			this.endClimbing();
	        		}
	        	}
	        	else if ( this.collidedHorizontally )
	        	{
	        		this.doClimbing(false);
	        	}
	        	else
	        	{
	    			this.endClimbing();
	        	}
        	}
        	else
        	{
        		this.endClimbing();
        	}
        }
        super.onUpdate();
    }
        
        
    private void doClimbing(boolean backwards)
    {		
		if ( this.climbingTimer <= 320 && !( this.getHeldItemMainhand() != null && this.getHeldItemMainhand().getItem() instanceof ItemBow) && (this.getAttackTarget().posY - 1.0D > this.posY || this.climbUntilPlatform) ) // && this.canEntityBeSeen(this.getAttackTarget())) )
		{
	        this.climbUntilPlatform = true;
	        this.climbingTimer++;
			this.aggroTimer = 0;
	        
	    	this.faceEntity(this.getAttackTarget(), 30.0F, 30.0F);
			
			if ( backwards ) // && this.getDistanceSq(this.getAttackTarget()) < 16 )
			{
				this.getMoveHelper().strafe( 0.0F, 0.0F );
				Vec3d velocityVector = new Vec3d(this.posX - this.getAttackTarget().posX, 0, this.posZ - this.getAttackTarget().posZ);
				if ( velocityVector != null )
				{
					double push = (8.0D+this.getDistanceSq(this.getAttackTarget()));
					this.motionX = (velocityVector.x)/push;
					this.motionZ = (velocityVector.z)/push;
					if ( this.motionY < 0.3F )
					{
						this.motionY = 0.3F;
					}
					this.velocityChanged = true;
				}
			}
			else
			{
				this.getMoveHelper().strafe( this.strafeVer*0.5F, 0.0F );
				Vec3d velocityVector = new Vec3d(this.getAttackTarget().posX-this.posX , 0,this.getAttackTarget().posZ-this.posZ);
				if ( velocityVector != null )
				{
					this.motionX = (velocityVector.x);
					this.motionZ = (velocityVector.z);
					this.velocityChanged = true;
				}
			}
			
			
	    	//this.faceEntity(this.getAttackTarget(), 30.0F, 30.0F);
			this.blockingTimer = 20;
			this.setBesideClimbableBlock(true);
			//this.setSwingingArms(true);
			this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(2.0D);
		}
		else
		{
			this.endClimbing();
		}
    }
    
    private void endClimbing()
    {
	    this.climbUntilPlatform = false;
		this.setBesideClimbableBlock(false);
		//this.setSwingingArms(false);
		if ( !this.blocking && this.onGround ) this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.25D);
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
    
	//===================================================== Add Equipment =======================================================
    
    public void addEquipment()
    {
    	if ( this.world.isRemote )
    	{
    		return;
    	}
    	
	    int weapon = rand.nextInt(3);
	    
	    if ( weapon == 0 ) // MELEE
	    {
	    	if ( rand.nextBoolean() ) // TWO-HANDED
	    	{
		    	this.weaponMain = new ItemStack(Item.getByNameOrId(ToroQuestConfiguration.banditTwoHandedMeleeWeapons[rand.nextInt(ToroQuestConfiguration.banditTwoHandedMeleeWeapons.length)]),1);
		    	if ( this.rand.nextInt((int)this.getMaxHealth()) > ToroQuestConfiguration.banditBaseHealth ) this.weaponMain = new ItemStack(Item.getByNameOrId(ToroQuestConfiguration.banditTwoHandedMeleeWeaponsPowerful[rand.nextInt(ToroQuestConfiguration.banditTwoHandedMeleeWeaponsPowerful.length)]),1);

		    	if ( this.weaponMain == null || this.weaponMain.isEmpty() )
				{
		    		if ( rand.nextBoolean() )
					{
						this.weaponMain = new ItemStack(Items.IRON_SWORD, 1);
					}
					else
					{
						this.weaponMain = new ItemStack(Items.IRON_AXE, 1);
					}
				}
		    	
		    	if ( ToroQuestConfiguration.enchantFirstBanditAndOrcChance > 0 && rand.nextInt(100) < ToroQuestConfiguration.enchantFirstBanditAndOrcChance )
		    	{
			    	String[] enchant = ToroQuestConfiguration.enchantFirstBanditAndOrcMeleeWeapon[rand.nextInt(ToroQuestConfiguration.enchantFirstBanditAndOrcMeleeWeapon.length)].split(",");
		    		this.weaponMain.addEnchantment(Enchantment.getEnchantmentByLocation(enchant[0]), rand.nextInt(Integer.parseInt(enchant[1]))+1);
			    	if (  ToroQuestConfiguration.enchantSecondBanditAndOrcChance > 0 && rand.nextInt(100) < ToroQuestConfiguration.enchantSecondBanditAndOrcChance )
			    	{
				    	enchant = ToroQuestConfiguration.enchantSecondBanditAndOrcMeleeWeapon[rand.nextInt(ToroQuestConfiguration.enchantSecondBanditAndOrcMeleeWeapon.length)].split(",");
			    		this.weaponMain.addEnchantment(Enchantment.getEnchantmentByLocation(enchant[0]), rand.nextInt(Integer.parseInt(enchant[1]))+1);
			    	}
		    	}
	    	}
	    	else // DUAL-WIELD
	    	{
	    		this.weaponMain = new ItemStack(Item.getByNameOrId(ToroQuestConfiguration.banditOneHandedMeleeWeapons[rand.nextInt(ToroQuestConfiguration.banditOneHandedMeleeWeapons.length)]),1);
		    	if ( this.rand.nextInt((int)this.getMaxHealth()) > ToroQuestConfiguration.banditBaseHealth ) this.weaponMain = new ItemStack(Item.getByNameOrId(ToroQuestConfiguration.banditOneHandedMeleeWeaponsPowerful[rand.nextInt(ToroQuestConfiguration.banditOneHandedMeleeWeaponsPowerful.length)]),1);

	    		if ( this.weaponMain == null || this.weaponMain.isEmpty() )
		    	{
					if ( rand.nextBoolean() )
					{
						this.weaponMain = new ItemStack(Items.IRON_SWORD, 1);
					}
					else
					{
						this.weaponMain = new ItemStack(Items.IRON_AXE, 1);
					}
				}
				
		    	if ( ToroQuestConfiguration.enchantFirstBanditAndOrcChance > 0 && rand.nextInt(100) < ToroQuestConfiguration.enchantFirstBanditAndOrcChance )
		    	{
			    	String[] enchant = ToroQuestConfiguration.enchantFirstBanditAndOrcMeleeWeapon[rand.nextInt(ToroQuestConfiguration.enchantFirstBanditAndOrcMeleeWeapon.length)].split(",");
		    		this.weaponMain.addEnchantment(Enchantment.getEnchantmentByLocation(enchant[0]), rand.nextInt(Integer.parseInt(enchant[1]))+1);
			    	if (  ToroQuestConfiguration.enchantSecondBanditAndOrcChance > 0 && rand.nextInt(100) < ToroQuestConfiguration.enchantSecondBanditAndOrcChance )
			    	{
				    	enchant = ToroQuestConfiguration.enchantSecondBanditAndOrcMeleeWeapon[rand.nextInt(ToroQuestConfiguration.enchantSecondBanditAndOrcMeleeWeapon.length)].split(",");
			    		this.weaponMain.addEnchantment(Enchantment.getEnchantmentByLocation(enchant[0]), rand.nextInt(Integer.parseInt(enchant[1]))+1);
			    	}
		    	}
		    	
				this.weaponOff = this.weaponMain.copy();
	    	}
	    }
	    else if ( weapon == 1 ) // SHIELD
	    {
	    	this.weaponMain = new ItemStack(Item.getByNameOrId(ToroQuestConfiguration.banditOneHandedMeleeWeapons[rand.nextInt(ToroQuestConfiguration.banditOneHandedMeleeWeapons.length)]),1);
	    	if ( this.rand.nextInt((int)this.getMaxHealth()) > ToroQuestConfiguration.banditBaseHealth ) this.weaponMain = new ItemStack(Item.getByNameOrId(ToroQuestConfiguration.banditOneHandedMeleeWeaponsPowerful[rand.nextInt(ToroQuestConfiguration.banditOneHandedMeleeWeaponsPowerful.length)]),1);

	    	if ( this.weaponMain == null || this.weaponMain.isEmpty() )
	    	{
				if ( rand.nextBoolean() )
				{
					this.weaponMain = new ItemStack(Items.IRON_SWORD, 1);
				}
				else
				{
					this.weaponMain = new ItemStack(Items.IRON_AXE, 1);
				}
			}
	        
	    	if ( ToroQuestConfiguration.enchantFirstBanditAndOrcChance > 0 && rand.nextInt(100) < ToroQuestConfiguration.enchantFirstBanditAndOrcChance )
	    	{
		    	String[] enchant = ToroQuestConfiguration.enchantFirstBanditAndOrcMeleeWeapon[rand.nextInt(ToroQuestConfiguration.enchantFirstBanditAndOrcMeleeWeapon.length)].split(",");
	    		this.weaponMain.addEnchantment(Enchantment.getEnchantmentByLocation(enchant[0]), rand.nextInt(Integer.parseInt(enchant[1]))+1);
		    	if (  ToroQuestConfiguration.enchantSecondBanditAndOrcChance > 0 && rand.nextInt(100) < ToroQuestConfiguration.enchantSecondBanditAndOrcChance )
		    	{
			    	enchant = ToroQuestConfiguration.enchantSecondBanditAndOrcMeleeWeapon[rand.nextInt(ToroQuestConfiguration.enchantSecondBanditAndOrcMeleeWeapon.length)].split(",");
		    		this.weaponMain.addEnchantment(Enchantment.getEnchantmentByLocation(enchant[0]), rand.nextInt(Integer.parseInt(enchant[1]))+1);
		    	}
	    	}

	    	this.weaponOff = new ItemStack(Item.getByNameOrId(ToroQuestConfiguration.banditShields[rand.nextInt(ToroQuestConfiguration.banditShields.length)]),1);
	    	if ( this.rand.nextInt((int)this.getMaxHealth()) > ToroQuestConfiguration.banditBaseHealth ) this.weaponOff = new ItemStack(Item.getByNameOrId(ToroQuestConfiguration.banditShieldsPowerful[rand.nextInt(ToroQuestConfiguration.banditShieldsPowerful.length)]),1);

			if ( this.weaponOff == null || this.weaponOff.isEmpty() )
	    	{
				this.weaponOff = new ItemStack(Items.SHIELD, 1);
			}
	    	
			if ( ToroQuestConfiguration.enchantFirstBanditAndOrcChance > 0 && rand.nextInt(100) < ToroQuestConfiguration.enchantFirstBanditAndOrcChance )
	    	{
		    	String[] enchant = ToroQuestConfiguration.enchantFirstBanditAndOrcShield[rand.nextInt(ToroQuestConfiguration.enchantFirstBanditAndOrcShield.length)].split(",");
	    		this.weaponOff.addEnchantment(Enchantment.getEnchantmentByLocation(enchant[0]), rand.nextInt(Integer.parseInt(enchant[1]))+1);
	    	}
	    	
	    }
	    else if ( weapon == 2 ) // RANGED
	    {
	    	this.weaponMain = new ItemStack(Item.getByNameOrId(ToroQuestConfiguration.banditRangedWeapons[rand.nextInt(ToroQuestConfiguration.banditRangedWeapons.length)]),1);
	    	if ( this.rand.nextInt((int)this.getMaxHealth()) > ToroQuestConfiguration.banditBaseHealth ) this.weaponMain = new ItemStack(Item.getByNameOrId(ToroQuestConfiguration.banditRangedWeaponsPowerful[rand.nextInt(ToroQuestConfiguration.banditRangedWeaponsPowerful.length)]),1);

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
	    }
	    
	    if ( this.weaponMain != null && !this.weaponMain.isEmpty() )
	    {
			this.setHeldItem(EnumHand.MAIN_HAND, this.weaponMain );
	    }
	    else
	    {
	    	this.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY );
	    }
	    
	    if ( this.weaponOff != null && !this.weaponOff.isEmpty() )
	    {
	    	this.setHeldItem(EnumHand.OFF_HAND, this.weaponOff );
	    }
	    else
	    {
	    	this.setHeldItem(EnumHand.OFF_HAND, ItemStack.EMPTY );
	    }
		
		this.addMask();
		
		if ( ToroQuestConfiguration.banditsHaveArmorForSpartanWeaponry )
		{
			this.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(Items.LEATHER_CHESTPLATE, 1));
			this.setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(Items.LEATHER_LEGGINGS, 1));
			this.setItemStackToSlot(EntityEquipmentSlot.FEET, new ItemStack(Items.LEATHER_BOOTS, 1));
		}
		
    	this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(ToroQuestConfiguration.banditAttackDamage);

    }
    
    public void addMask()
    {
		if ( ToroQuestConfiguration.renderBanditMask )
		{
			ItemStack head = new ItemStack(Item.getByNameOrId("toroquest:bandit_helmet"), 1);
			setItemStackToSlot(EntityEquipmentSlot.HEAD, head);
		}
    }
    

	@Override
	public void setAttackTarget(EntityLivingBase e)
	{
		if ( e == null || !e.isEntityAlive() )
		{
			this.setSprinting(false);
			super.setAttackTarget(null);
			return;
		}
		else if ( !e.getClass().equals(this.getClass()) )
		{
			super.setAttackTarget(e);
			return;
		}
	}
	
    protected void callForHelp(EntityLivingBase attacker)
	{
    	if ( attacker == null || !attacker.isEntityAlive() )
		{
			return;
		}
    	
		List<EntitySentry> help = this.world.getEntitiesWithinAABB(EntitySentry.class, new AxisAlignedBB(getPosition()).grow(20, 12, 20), new Predicate<EntitySentry>()
		{
			public boolean apply(@Nullable EntitySentry entity)
			{
				return true;
			}
		});

		for ( EntitySentry entity : help )
		{
			if ( !(entity instanceof EntityOrc) )
			{
				if ( entity.getAttackTarget() == null )
				{
					entity.setAttackTarget(attacker);
				}
			}
		}
	}
    
    public boolean isEntityInsideOpaqueBlock(EntityBoat b)
    {
        {
            BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();

            for (int i = 0; i < 8; ++i)
            {
                int j = MathHelper.floor(b.posY + (double)(((float)((i >> 0) % 2) - 0.5F) * 0.1F) + (double)b.getEyeHeight());
                int k = MathHelper.floor(b.posX + (double)(((float)((i >> 1) % 2) - 0.5F) * b.width * 1.2F));
                int l = MathHelper.floor(b.posZ + (double)(((float)((i >> 2) % 2) - 0.5F) * b.width * 1.2F));

                if (blockpos$pooledmutableblockpos.getX() != k || blockpos$pooledmutableblockpos.getY() != j || blockpos$pooledmutableblockpos.getZ() != l)
                {
                    blockpos$pooledmutableblockpos.setPos(k, j, l);

                    if (b.world.getBlockState(blockpos$pooledmutableblockpos).causesSuffocation())
                    {
                        blockpos$pooledmutableblockpos.release();
                        return true;
                    }
                }
            }

            blockpos$pooledmutableblockpos.release();
            return false;
        }
    }
}