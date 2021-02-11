package net.torocraft.toroquest.entities;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.torocraft.toroquest.ToroQuest;
import net.torocraft.toroquest.config.ToroQuestConfiguration;

public class EntityToroQuest extends EntityToro
{

	public EntityToroQuest(World worldIn)
	{
		super(worldIn);
	}

	public static String NAME = "quest_toro";

	static
	{
		if (ToroQuestConfiguration.specificEntityNames)
		{
			NAME = ToroQuestEntities.ENTITY_PREFIX + NAME;
		}
	}

	private static final DataParameter<Boolean> CHARGING = EntityDataManager.<Boolean> createKey(EntityToroQuest.class, DataSerializers.BOOLEAN);

	public static void init(int entityId)
	{
		EntityRegistry.registerModEntity(new ResourceLocation(ToroQuest.MODID, NAME), EntityToroQuest.class, NAME, entityId, ToroQuest.INSTANCE, 80, 3,
				true, 0x3f3024, 0xe0d6b9);
	}

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
	
	protected void initEntityAI()
	{
		super.initEntityAI();
		this.tasks.addTask(1, new EntityAIAvoidEntity<EntityPlayer>(this, EntityPlayer.class, PLAYER_WITH_LEAD, 32.0F, 0.7D, 0.9D));
		this.tasks.addTask(2, new EntityAIAvoidEntity<EntityPlayer>(this, EntityPlayer.class, 32.0F, 0.6D, 0.8D));
	}

//	public static void registerRenders()
//	{
//		RenderingRegistry.registerEntityRenderingHandler(EntityToroQuest.class, new IRenderFactory<EntityToroQuest>()
//		{
//			@Override
//			public RenderToroQuest createRenderFor(RenderManager manager)
//			{
//				return new RenderToroQuest(manager, new ModelToro(), 0.7F);
//			}
//		});
//	}
	
	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40.0D * ToroQuestConfiguration.toroHealthMultiplier);
		//this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(15.0D * ToroQuestConfiguration.toroAttackDamageMultiplier);
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
	}
	
	@Override
	public boolean canDespawn()
	{
		return false;
	}
	
	@Override
	public boolean canMateWith(EntityAnimal otherAnimal)
    {
        return false;
    }
	
	@Override
	public void dropLoot()
	{
		if ( ToroQuestConfiguration.toroSpawnChance > 0 )
		{
			super.dropLoot();;
		}
	}

}