package net.torocraft.toroquest.entities;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.torocraft.toroquest.ToroQuest;
import net.torocraft.toroquest.config.ToroQuestConfiguration;
import net.torocraft.toroquest.entities.ai.EntityAIRaid;
import net.torocraft.toroquest.entities.render.RenderOrc;

public class EntityOrc extends EntitySentry implements IRangedAttackMob, IMob
{
	
	public double renderSizeXZ = 1.0D + (rand.nextDouble()/12.0D);
	public double renderSizeY = renderSizeXZ * (1.025D + rand.nextGaussian()/16.0D);
	
	public static String NAME = "orc";
	static
	{
		if (ToroQuestConfiguration.specificEntityNames)
		{
			NAME = ToroQuestEntities.ENTITY_PREFIX + NAME;
		}
	}
	public static void init(int entityId)
	{
		EntityRegistry.registerModEntity(new ResourceLocation(ToroQuest.MODID, NAME), EntityOrc.class, NAME, entityId, ToroQuest.INSTANCE, 80, 1,
				true, 0x308f26, 0xe0d359);
	}

	public static void registerRenders()
	{
		RenderingRegistry.registerEntityRenderingHandler(EntityOrc.class, new IRenderFactory<EntityOrc>()
		{
			@Override
			public Render<EntityOrc> createRenderFor(RenderManager manager)
			{
				return new RenderOrc(manager);
			}
		});
	}
	
	public String getChatName()
    {
		return null;
    }
	
	@Override
	public boolean hasCustomName()
    {
		this.setAlwaysRenderNameTag(false);
        return false;
    }
	
    @Override
    public void addMask()
    {
		if ( ToroQuestConfiguration.renderOrcMask )
		{
			ItemStack head = new ItemStack(Item.getByNameOrId("toroquest:bandit_helmet"), 1);
			setItemStackToSlot(EntityEquipmentSlot.HEAD, head);
		}
    }
    
    private ResourceLocation banditSkin = new ResourceLocation(ToroQuest.MODID + ":textures/entity/orc/orc_" + rand.nextInt(ToroQuestConfiguration.orcSkins) + ".png");
	
	public ResourceLocation getSkin()
	{
		return this.banditSkin;
	}
	
	public double getRenderSizeXZ()
	{
		return this.renderSizeXZ;
	}
	
	
	public double getRenderSizeY()
	{
		return this.renderSizeY;
	}

	public EntityOrc(World worldIn)
	{
		super(worldIn);
        this.setSize(0.6F, 1.9F);
		this.setAlwaysRenderNameTag(false);
		this.experienceValue = 30;
		this.splashPotionTimer = -1;
	    this.hasSplashPotion = -1;
	    this.limitPotions = -1;
	}
	
//	@Override
//	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata)
//	{
//		return super.onInitialSpawn(difficulty, livingdata);
//	}
	
	@Override
	public boolean processInteract( EntityPlayer player, EnumHand hand )
	{
		return false;
	}

	@Override
    protected void applyEntityAttributes()
    {
		super.applyEntityAttributes();
    	this.setLeftHanded(false);
    	this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(ToroQuestConfiguration.orcBaseHealth);
    	this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(ToroQuestConfiguration.orcAttackDamage);
    	this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(ToroQuestConfiguration.orcArmor);
    	this.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).setBaseValue(ToroQuestConfiguration.orcArmorToughness);
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(40.0D);
    	this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3925D+rand.nextDouble()/50.0D);
    	this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.25D);
    }
	
	@Override
	public void addEquipment()
    {
	    int weapon = rand.nextInt(3);
	    
	    if ( weapon == 0 ) // MELEE
	    {
	    	if ( rand.nextBoolean() ) // TWO-HANDED
	    	{
		    	this.weaponMain = new ItemStack(Item.getByNameOrId(ToroQuestConfiguration.orcTwoHandedMeleeWeapons[rand.nextInt(ToroQuestConfiguration.orcTwoHandedMeleeWeapons.length)]),1);
		    	if ( this.rand.nextInt((int)this.getMaxHealth()) > ToroQuestConfiguration.orcBaseHealth ) this.weaponMain = new ItemStack(Item.getByNameOrId(ToroQuestConfiguration.orcTwoHandedMeleeWeaponsPowerful[rand.nextInt(ToroQuestConfiguration.orcTwoHandedMeleeWeaponsPowerful.length)]),1);

		    	if ( this.weaponMain == null || this.weaponMain.isEmpty() )
				{
		    		if ( rand.nextBoolean() )
					{
						this.weaponMain = new ItemStack(Items.STONE_SWORD, 1);
					}
					else
					{
						this.weaponMain = new ItemStack(Items.STONE_AXE, 1);
					}
				}
		    	
		    	if (  ToroQuestConfiguration.enchantFirstBanditAndOrcChance > 0 && rand.nextInt(100) < ToroQuestConfiguration.enchantFirstBanditAndOrcChance )
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
	    		this.weaponMain = new ItemStack(Item.getByNameOrId(ToroQuestConfiguration.orcOneHandedMeleeWeapons[rand.nextInt(ToroQuestConfiguration.orcOneHandedMeleeWeapons.length)]),1);
		    	if ( this.rand.nextInt((int)this.getMaxHealth()) > ToroQuestConfiguration.orcBaseHealth ) this.weaponMain = new ItemStack(Item.getByNameOrId(ToroQuestConfiguration.orcOneHandedMeleeWeaponsPowerful[rand.nextInt(ToroQuestConfiguration.orcOneHandedMeleeWeaponsPowerful.length)]),1);
	    		
	    		if ( this.weaponMain == null || this.weaponMain.isEmpty() )
		    	{
					if ( rand.nextBoolean() )
					{
						this.weaponMain = new ItemStack(Items.STONE_SWORD, 1);
					}
					else
					{
						this.weaponMain = new ItemStack(Items.STONE_AXE, 1);
					}
				}
				
		    	if (  ToroQuestConfiguration.enchantFirstBanditAndOrcChance > 0 && rand.nextInt(100) < ToroQuestConfiguration.enchantFirstBanditAndOrcChance )
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
	    	this.weaponMain = new ItemStack(Item.getByNameOrId(ToroQuestConfiguration.orcOneHandedMeleeWeapons[rand.nextInt(ToroQuestConfiguration.orcOneHandedMeleeWeapons.length)]),1);
	    	if ( this.rand.nextInt((int)this.getMaxHealth()) > ToroQuestConfiguration.orcBaseHealth ) this.weaponMain = new ItemStack(Item.getByNameOrId(ToroQuestConfiguration.orcOneHandedMeleeWeaponsPowerful[rand.nextInt(ToroQuestConfiguration.orcOneHandedMeleeWeaponsPowerful.length)]),1);

	    	if ( this.weaponMain == null || this.weaponMain.isEmpty() )
	    	{
				if ( rand.nextBoolean() )
				{
					this.weaponMain = new ItemStack(Items.STONE_SWORD, 1);
				}
				else
				{
					this.weaponMain = new ItemStack(Items.STONE_AXE, 1);
				}
			}
	        
	    	if (  ToroQuestConfiguration.enchantFirstBanditAndOrcChance > 0 && rand.nextInt(100) < ToroQuestConfiguration.enchantFirstBanditAndOrcChance )
	    	{
		    	String[] enchant = ToroQuestConfiguration.enchantFirstBanditAndOrcMeleeWeapon[rand.nextInt(ToroQuestConfiguration.enchantFirstBanditAndOrcMeleeWeapon.length)].split(",");
	    		this.weaponMain.addEnchantment(Enchantment.getEnchantmentByLocation(enchant[0]), rand.nextInt(Integer.parseInt(enchant[1]))+1);
		    	if (  ToroQuestConfiguration.enchantSecondBanditAndOrcChance > 0 && rand.nextInt(100) < ToroQuestConfiguration.enchantSecondBanditAndOrcChance )
		    	{
			    	enchant = ToroQuestConfiguration.enchantSecondBanditAndOrcMeleeWeapon[rand.nextInt(ToroQuestConfiguration.enchantSecondBanditAndOrcMeleeWeapon.length)].split(",");
		    		this.weaponMain.addEnchantment(Enchantment.getEnchantmentByLocation(enchant[0]), rand.nextInt(Integer.parseInt(enchant[1]))+1);
		    	}
	    	}

	    	this.weaponOff = new ItemStack(Item.getByNameOrId(ToroQuestConfiguration.orcShields[rand.nextInt(ToroQuestConfiguration.orcShields.length)]),1);
	    	if ( this.rand.nextInt((int)this.getMaxHealth()) > ToroQuestConfiguration.orcBaseHealth ) this.weaponOff = new ItemStack(Item.getByNameOrId(ToroQuestConfiguration.orcShieldsPowerful[rand.nextInt(ToroQuestConfiguration.orcShieldsPowerful.length)]),1);

	    	if ( this.weaponOff == null || this.weaponOff.isEmpty() )
	    	{
				this.weaponOff = new ItemStack(Items.SHIELD, 1);
			}
	    	
			if (  ToroQuestConfiguration.enchantFirstBanditAndOrcChance > 0 && rand.nextInt(100) < ToroQuestConfiguration.enchantFirstBanditAndOrcChance )
	    	{
		    	String[] enchant = ToroQuestConfiguration.enchantFirstBanditAndOrcShield[rand.nextInt(ToroQuestConfiguration.enchantFirstBanditAndOrcShield.length)].split(",");
	    		this.weaponOff.addEnchantment(Enchantment.getEnchantmentByLocation(enchant[0]), rand.nextInt(Integer.parseInt(enchant[1]))+1);
	    	}
	    	
	    }
	    else if ( weapon == 2 ) // RANGED
	    {
	    	this.weaponMain = new ItemStack(Item.getByNameOrId(ToroQuestConfiguration.orcRangedWeapons[rand.nextInt(ToroQuestConfiguration.orcRangedWeapons.length)]),1);
	    	if ( this.rand.nextInt((int)this.getMaxHealth()) > ToroQuestConfiguration.orcBaseHealth ) this.weaponMain = new ItemStack(Item.getByNameOrId(ToroQuestConfiguration.orcRangedWeaponsPowerful[rand.nextInt(ToroQuestConfiguration.orcRangedWeaponsPowerful.length)]),1);

	    	if ( this.weaponMain == null || this.weaponMain.isEmpty() )
	    	{
				this.weaponMain = new ItemStack(Items.BOW, 1);
			}
	    	
	    	if (  ToroQuestConfiguration.enchantFirstBanditAndOrcChance > 0 && rand.nextInt(100) < ToroQuestConfiguration.enchantFirstBanditAndOrcChance )
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
			setHeldItem(EnumHand.MAIN_HAND, this.weaponMain );
	    }
	    if ( this.weaponOff != null && !this.weaponOff.isEmpty() )
	    {
	    	setHeldItem(EnumHand.OFF_HAND, this.weaponOff );
	    }
		
		this.addMask();
		
		if ( ToroQuestConfiguration.banditsHaveArmorForSpartanWeaponry )
		{
			setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(Items.LEATHER_LEGGINGS, 1));
			setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(Items.LEATHER_LEGGINGS, 1));
			setItemStackToSlot(EntityEquipmentSlot.FEET, new ItemStack(Items.LEATHER_BOOTS, 1));
		} 
    	this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(ToroQuestConfiguration.orcAttackDamage);
    }
	
	@Override
	public void dropLoot()
	{
		if (!world.isRemote)
		{
			if ( ToroQuestConfiguration.orcsDropMasks )
			{
				this.dropMask();
			}
			if ( ToroQuestConfiguration.orcsDropEmeralds && rand.nextInt(3) == 0 )
			{
				ItemStack stack = new ItemStack(Items.EMERALD, rand.nextInt(4)+1);
				EntityItem dropItem = new EntityItem(world, posX, posY, posZ, stack.copy());
				world.spawnEntity(dropItem);
			}
			if ( rand.nextInt(3) == 0 )
			{
				ItemStack stack = new ItemStack(Items.BONE, rand.nextInt(2)+1);
				EntityItem dropItem = new EntityItem(world, posX, posY, posZ, stack.copy());
				world.spawnEntity(dropItem);
			}
		}
	}
	
	@Override
    @Nullable
    protected SoundEvent getAmbientSound()
    {
		this.playSound(SoundEvents.ENTITY_ZOMBIE_PIG_AMBIENT, 1.0F, 0.5F + rand.nextFloat()/5.0F );
        return null;
    }

	@Override
	@Nullable
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
		if ( rand.nextInt(5) == 0 )
		{
			this.playSound(SoundEvents.EVOCATION_ILLAGER_DEATH, 0.8F, 0.7F + rand.nextFloat()/10.0F );
			this.playSound(SoundEvents.ENTITY_ZOMBIE_PIG_HURT, 1.0F, 0.7F + rand.nextFloat()/5.0F );
		}
		else if ( rand.nextInt(4) == 0 )
		{
			this.playSound(SoundEvents.ENTITY_ILLAGER_DEATH, 0.8F, 0.7F + rand.nextFloat()/10.0F );
			this.playSound(SoundEvents.ENTITY_ZOMBIE_PIG_HURT, 1.0F, 0.7F + rand.nextFloat()/5.0F );
		}
		return null;
    }

	@Override
	@Nullable
    protected SoundEvent getDeathSound()
    {
		this.playSound(SoundEvents.ENTITY_ZOMBIE_PIG_DEATH, 1.0F, 1.0F + rand.nextFloat()/5.0F );
		return null;
    }
	
//	protected final EntityAIRaid areaAI = new EntityAIRaid(this, 0.6D, 48);
//	public Integer raidX = null;
//	public Integer raidZ = null;
//	
//	public void setRaidLocation(Integer x, Integer z)
//	{
//		this.tasks.removeTask(this.areaAI);
//		if ( x != null && z != null )
//		{
//			this.raidX = x;
//			this.raidZ = z;
//			this.areaAI.setCenter(x, z);
//			this.tasks.addTask(11, this.areaAI);
//			this.writeEntityToNBT(new NBTTagCompound());
//		}
//	}
	
	@Override
	protected void callForHelp(EntityLivingBase attacker)
	{
		List<EntityOrc> help = world.getEntitiesWithinAABB(EntityOrc.class, new AxisAlignedBB(getPosition()).grow(32, 16, 32), new Predicate<EntityOrc>()
		{
			public boolean apply(@Nullable EntityOrc entity)
			{
				return true;
			}
		});

		for ( EntityOrc entity : help )
		{
			if ( entity.getAttackTarget() == null )
			{
				entity.setAttackTarget(attacker);
			}
		}
	}
}