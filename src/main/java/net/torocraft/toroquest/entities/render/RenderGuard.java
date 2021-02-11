package net.torocraft.toroquest.entities.render;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerArrow;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.toroquest.ToroQuest;
import net.torocraft.toroquest.civilization.CivilizationType;
import net.torocraft.toroquest.entities.EntityGuard;
import net.torocraft.toroquest.entities.model.ModelGuard;

@SideOnly(Side.CLIENT)
public class RenderGuard extends RenderBiped<EntityGuard>
{

	private static final ResourceLocation DEFAULT = new ResourceLocation(ToroQuest.MODID + ":textures/entity/guard/guard_null.png");
	private static final ResourceLocation TEXTURES_SUN = new ResourceLocation(ToroQuest.MODID + ":textures/entity/guard/guard_sun.png");
	private static final ResourceLocation TEXTURES_MOON = new ResourceLocation(ToroQuest.MODID + ":textures/entity/guard/guard_moon.png");
	private static final ResourceLocation TEXTURES_EARTH = new ResourceLocation(ToroQuest.MODID + ":textures/entity/guard/guard_earth.png");
	private static final ResourceLocation TEXTURES_WIND = new ResourceLocation(ToroQuest.MODID + ":textures/entity/guard/guard_wind.png");
	private static final ResourceLocation TEXTURES_FIRE = new ResourceLocation(ToroQuest.MODID + ":textures/entity/guard/guard_fire.png");
	private static final ResourceLocation TEXTURES_WATER = new ResourceLocation(ToroQuest.MODID + ":textures/entity/guard/guard_water.png");
	
	private final ModelGuard defaultModel = (ModelGuard) mainModel;
	
	// private final List<LayerRenderer<EntityGuard>> defaultLayers;

	public RenderGuard(RenderManager renderManagerIn)
	{
		super(renderManagerIn, new ModelGuard(), 0.5F);
		
		
		// System.out.println( CommonProxy.ConfigHandler.compat );
		// LayerRenderer<?> layerrenderer = (LayerRenderer) this.layerRenderers.get(0);
		// defaultModel = (ModelGuard) mainModel;

		// this.addLayer(new LayerHeldItem(this));
		// this.addLayer(new LayerHeldItem(this));
        this.addLayer(new LayerArrow(this));

//		LayerBipedArmor layerbipedarmor = new LayerBipedArmor(this)
//		{
//			protected void initArmor()
//			{
//				modelLeggings = new ModelZombie(0.5F, true);
//				modelArmor = new ModelZombie(1.0F, true);
//				// modelCape = null;
//			}
//		};
		//this.addLayer(layerbipedarmor);
		
		
		

		// addLayer(new LayerCape(this));
		// this.defaultLayers = Lists.newArrayList(this.layerRenderers);

	}

	/**
	 * Renders the desired {@code T} type Entity.
	 */
	@Override
	public void doRender(EntityGuard entity, double x, double y, double z, float entityYaw, float partialTicks)
	{
//		if ( ToroQuestConfiguration.reduceVillageLag )
//		{
//			boolean flag = false;
//			for ( EntityPlayer player : entity.world.playerEntities )
//			{
//				if ( entity.canEntityBeSeen(player) )
//				{
//					flag = true;
//					continue;
//				}
//			}
//			if ( !flag )
//			{
//				return;
//			}
//		}
//		if ( entity.getCivilization() != null ) defaultModel.setCivilization(entity.getCivilization());
		this.setModelVisibilities(entity);
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	private void setModelVisibilities(EntityGuard entity)
	{
		// ModelGuard defaultModel = (ModelGuard) this.getMainModel();

		ItemStack itemstack = entity.getHeldItemMainhand();
		ItemStack itemstack1 = entity.getHeldItemOffhand();
		
		defaultModel.setVisible(true);

		ModelBiped.ArmPose modelbiped$armpose = ModelBiped.ArmPose.EMPTY;
		ModelBiped.ArmPose modelbiped$armpose1 = ModelBiped.ArmPose.EMPTY;

		if (itemstack != null)
		{
			modelbiped$armpose = ModelBiped.ArmPose.ITEM;

			if (entity.getItemInUseCount() > 0)
			{
				EnumAction enumaction = itemstack.getItemUseAction();

				if (enumaction == EnumAction.BLOCK)
				{
					modelbiped$armpose = ModelBiped.ArmPose.BLOCK;
				}
				else if (enumaction == EnumAction.BOW)
				{
					modelbiped$armpose = ModelBiped.ArmPose.BOW_AND_ARROW;
				}
			}
		}

		if (itemstack1 != null)
		{
			modelbiped$armpose1 = ModelBiped.ArmPose.ITEM;

			if (entity.getItemInUseCount() > 0)
			{
				EnumAction enumaction1 = itemstack1.getItemUseAction();

				if (enumaction1 == EnumAction.BLOCK)
				{
					modelbiped$armpose1 = ModelBiped.ArmPose.BLOCK;
				}
			}
		}

//		if (entity.getPrimaryHand() == EnumHandSide.RIGHT)
		{
			defaultModel.rightArmPose = modelbiped$armpose;
			defaultModel.leftArmPose = modelbiped$armpose1;
		}
//		else
//		{
//			defaultModel.rightArmPose = modelbiped$armpose1;
//			defaultModel.leftArmPose = modelbiped$armpose;
//		}

	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called
	 * unless you call Render.bindEntityTexture.
	 */
	
	CivilizationType civ = null;
	
	@Override
	protected ResourceLocation getEntityTexture(EntityGuard entity)
	{
		civ = entity.getCivilization();
		
		if ( civ == null )
		{
			return DEFAULT;
		}
		
		switch ( civ )
		{
			case FIRE:
			{
				return TEXTURES_FIRE;
			}
			case EARTH:
			{
				return TEXTURES_EARTH;
			}
			case MOON:
			{
				return TEXTURES_MOON;
			}
			case SUN:
			{
				return TEXTURES_SUN;
			}
			case WIND:
			{
				return TEXTURES_WIND;
			}
			case WATER:
			{
				return TEXTURES_WATER;
			}
			default:
			{
				return DEFAULT;
			}
		}
	}
}