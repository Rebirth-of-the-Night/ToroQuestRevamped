package net.torocraft.toroquest.entities;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.toroquest.ToroQuest;
import net.torocraft.toroquest.config.ToroQuestConfiguration;
import net.torocraft.toroquest.entities.render.RenderConstructQuest;

public class EntityConstructQuest extends EntityConstruct implements IMob
{
	public EntityConstructQuest(World worldIn)
	{
		super(worldIn);
        this.setSize(1.35F, 2.9F);
        this.experienceValue = 300;
	}
	
	protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(150.0D*ToroQuestConfiguration.bossHealthMultiplier);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
        this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(2.0D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(30.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(10.0D);
		this.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).setBaseValue(10.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(30.0D*ToroQuestConfiguration.bossAttackDamageMultiplier);
    }
	
	@Override
	protected boolean canDespawn()
	{
		return false;
	}
	
    public boolean isNonBoss()
    {
        return false;
    }
	
	public static String NAME = "construct_quest";
	
	static
	{
		if (ToroQuestConfiguration.specificEntityNames)
		{
			NAME = ToroQuestEntities.ENTITY_PREFIX + NAME;
		}
	}
	
	public static void init(int entityId)
	{
		EntityRegistry.registerModEntity(new ResourceLocation(ToroQuest.MODID, NAME), EntityConstructQuest.class, NAME, entityId, ToroQuest.INSTANCE, 80, 1,
				true, 0x995533, 0x99aa33);
	}

	public static void registerRenders()
	{
		RenderingRegistry.registerEntityRenderingHandler(EntityConstructQuest.class, new IRenderFactory<EntityConstructQuest>()
		{
			@Override
			public RenderConstructQuest createRenderFor(RenderManager manager)
			{
				return new RenderConstructQuest(manager);
			}
		});
	}
	
	// INCREASE RENDER DISTNACE
	@SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        return this.getEntityBoundingBox().grow(64.0);
    }
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		if (this.world.isRemote)
		{
			return false;
		}
	    this.bossInfo.setPercent(this.getHealth()/this.getMaxHealth());
	    return super.attackEntityFrom(source, amount);
	}

    private final BossInfoServer bossInfo = (BossInfoServer)(new BossInfoServer(this.getDisplayName(), BossInfo.Color.PURPLE, BossInfo.Overlay.PROGRESS)).setDarkenSky(true);

	/**
     * Add the given player to the list of players tracking this entity. For instance, a player may track a boss in
     * order to view its associated boss bar.
     */
    public void addTrackingPlayer(EntityPlayerMP player)
    {
        super.addTrackingPlayer(player);
        this.bossInfo.addPlayer(player);
    }

    /**
     * Removes the given player from the list of players tracking this entity. See {@link Entity#addTrackingPlayer} for
     * more information on tracking.
     */
    public void removeTrackingPlayer(EntityPlayerMP player)
    {
        super.removeTrackingPlayer(player);
        this.bossInfo.removePlayer(player);
    }
	
	@Override
	public void onDeath(DamageSource cause)
	{
		if (!this.world.isRemote)
		{
			ItemStack stack = new ItemStack(Item.getByNameOrId("toroquest:dwarven_artifact"), 1);
			EntityItem dropItem = new EntityItem(world, posX, posY, posZ, stack.copy());
			dropItem.setGlowing(true);
			world.spawnEntity(dropItem);
		}
		super.onDeath(cause);
	}
	
}