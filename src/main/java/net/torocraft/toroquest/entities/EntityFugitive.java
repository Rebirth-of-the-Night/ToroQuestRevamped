//TODO
package net.torocraft.toroquest.entities;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIHarvestFarmland;
import net.minecraft.entity.ai.EntityAIMoveIndoors;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIVillagerInteract;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.torocraft.toroquest.ToroQuest;
import net.torocraft.toroquest.config.ToroQuestConfiguration;
import net.torocraft.toroquest.entities.render.RenderFugitive;

public class EntityFugitive extends EntityVillager
{

	public static String NAME = "fugitive";

	static
	{
		if (ToroQuestConfiguration.specificEntityNames)
		{
			NAME = ToroQuestEntities.ENTITY_PREFIX + NAME;
		}
	}
	//TODO
	//public static Achievement BOUNTY_HUNTER_ACHIEVEMNT = new Achievement("bounty_hunter", "bounty_hunter", 0, 0, Items.DIAMOND_SWORD, null)
	//		.registerStat();

	public static void init(int entityId)
	{
		EntityRegistry.registerModEntity(new ResourceLocation(ToroQuest.MODID, NAME), EntityFugitive.class, NAME, entityId, ToroQuest.INSTANCE, 80, 3,
				true, 0x000000, 0xe0d6b9);
	}

	public static void registerRenders()
	{
		RenderingRegistry.registerEntityRenderingHandler(EntityFugitive.class, new IRenderFactory<EntityFugitive>()
		{
			@Override
			public RenderFugitive createRenderFor(RenderManager manager)
			{
				return new RenderFugitive(manager);
			}
		});
	}

	public EntityFugitive(World worldIn)
	{
		super(worldIn);
        this.setSize(0.6F, 1.95F);
	}

	@Override
	public boolean canBeLeashedTo(EntityPlayer player)
	{
		return true;
	}

	public boolean processInteract(EntityPlayer player, EnumHand hand)
	{
		return false;
	}

//	@Override
//	public IEntityLivingData finalizeMobSpawn(DifficultyInstance p_190672_1_, @Nullable IEntityLivingData p_190672_2_, boolean p_190672_3_) {
//		return p_190672_2_;
//	}

	public EntityPlayer underAttack = null;
	
	@Override
	protected void initEntityAI()
	{
		this.tasks.addTask(0, new EntityAISwimming(this));
//		this.tasks.addTask(4, new EntityAIAvoidEntity<EntityToroMob>(this, EntityToroMob.class, 8.0F, 0.5D, 0.7D));
//		this.tasks.addTask(5, new EntityAIAvoidEntity<EntityMob>(this, EntityMob.class, 8.0F, 0.5D, 0.7D));
		this.tasks.addTask(1, new EntityAIPanic(this, 0.7D)
		{
			
			@Override
			public boolean shouldExecute()
		    {
		        if ( underAttack != null )
		        {
		            return this.findRandomPosition();
		        }
		        return false;
		    }
			
			@Override
			protected boolean findRandomPosition()
		    {
		        Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.creature, 16, 8, underAttack.getPositionVector());

		        if (vec3d == null)
		        {
		            return false;
		        }
		        else
		        {
		            this.randPosX = vec3d.x;
		            this.randPosY = vec3d.y;
		            this.randPosZ = vec3d.z;
		            return true;
		        }
		    }
			
		});
		this.tasks.addTask(2, new EntityAIAvoidEntity<EntityPlayer>(this, EntityPlayer.class, PLAYER_WITH_LEAD, 16.0F, 0.6D, 0.8D));
        this.tasks.addTask(3, new EntityAITempt(this, 0.4D, Items.EMERALD, false));
		this.tasks.addTask(4, new EntityAIAvoidEntity<EntityPlayer>(this, EntityPlayer.class, 8.0F, 0.5D, 0.7D));
		this.tasks.addTask(5, new EntityAIAvoidEntity<EntityToroNpc>(this, EntityToroNpc.class, 8.0F, 0.4D, 0.6D));
        this.tasks.addTask(8, new EntityAIMoveIndoors(this));
        this.tasks.addTask(9, new EntityAIRestrictOpenDoor(this));
        this.tasks.addTask(10, new EntityAIOpenDoor(this, true));
        this.tasks.addTask(11, new EntityAIMoveTowardsRestriction(this, 0.55D));
        this.tasks.addTask(13, new EntityAIVillagerInteract(this));
        this.tasks.addTask(14, new EntityAIWanderAvoidWater(this, 0.55D)
        {
        	@Override
            protected Vec3d getPosition()
            {
                if ( this.entity.isInWater() || this.entity.isInLava() )
                {
                    Vec3d vec3d = RandomPositionGenerator.getLandPos(this.entity, 16, 8);
                    return vec3d == null ? super.getPosition() : vec3d;
                }
                else
                {
                    return this.entity.getRNG().nextFloat() >= this.probability ? RandomPositionGenerator.getLandPos(this.entity, 10, 7) : super.getPosition();
                }
            }
        });
        this.tasks.addTask(15, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
	}
	
	

	@Override
	public void onDeath(DamageSource cause)
	{
		//super.onDeath(cause);
		if (!world.isRemote)
		{
			//dropItem(Items.LEAD, 1);
			dropItem(Items.EMERALD, rand.nextInt(4)+1);
		}
	}

//	private static final Item[] STOLEN_ITEMS = { Items.DIAMOND_AXE, Items.IRON_AXE, Items.DIAMOND_PICKAXE, Items.IRON_PICKAXE, Items.GOLDEN_APPLE,
//			Items.GOLD_INGOT, Items.DIAMOND, Items.BOW, Items.SHIELD, Items.BANNER, Items.DIAMOND_SWORD, Items.GOLDEN_SWORD, Items.CHAINMAIL_HELMET,
//			Items.DIAMOND_BOOTS };
	
	

//	private void dropItem(ItemStack stack)
//	{
//		EntityItem dropItem = new EntityItem(world, posX, posY, posZ, stack);
//		dropItem.setNoPickupDelay();
//		world.spawnEntity(dropItem);
//	}
//	
	// ===============================

	/**
	 * Get the formatted ChatComponent that will be used for the sender's
	 * username in chat
	 */
	public ITextComponent getDisplayName() {
		TextComponentTranslation textcomponenttranslation = new TextComponentTranslation("entity.toroquest.fugitive.name", new Object[0]);
		textcomponenttranslation.getStyle().setHoverEvent(this.getHoverEvent());
		textcomponenttranslation.getStyle().setInsertion(this.getCachedUniqueIdString());
		return textcomponenttranslation;
	};

	public static final Predicate<EntityPlayer> PLAYER_WITH_LEAD = new Predicate<EntityPlayer>() {
		public boolean apply(@Nullable EntityPlayer player) {
			return player.isEntityAlive() && holdingLead(player);
		}

		private boolean holdingLead(EntityPlayer player) {
			return holdingLeadIn(player, EntityEquipmentSlot.MAINHAND) || holdingLeadIn(player, EntityEquipmentSlot.MAINHAND);
		}

		private boolean holdingLeadIn(EntityPlayer player, EntityEquipmentSlot mainhand) {
			ItemStack itemstack = player.getItemStackFromSlot(mainhand);
			return !itemstack.isEmpty() && itemstack.getItem() == Items.LEAD;
		}
	};
	
	protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.ENTITY_VILLAGER_NO;
    }

    protected SoundEvent getDeathSound()
    {
        return SoundEvents.VINDICATION_ILLAGER_DEATH;
    }

    @Nullable
    protected ResourceLocation getLootTable()
    {
        return LootTableList.ENTITIES_VINDICATION_ILLAGER;
    }
	
    protected SoundEvent getAmbientSound()
    {
        return SoundEvents.ENTITY_VILLAGER_NO;
    }
	
	@Override
    public void readEntityFromNBT(NBTTagCompound compound)
    {
    	super.readEntityFromNBT(compound);
        this.tasks.removeTask(new EntityAIHarvestFarmland(this, 0.0D));
    }
	
	@Override
	protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
    }
}