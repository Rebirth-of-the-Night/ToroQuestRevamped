package net.torocraft.toroquest.entities.render;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerArrow;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.toroquest.entities.EntityBanditLord;
import net.torocraft.toroquest.entities.EntitySentry;
import net.torocraft.toroquest.entities.model.Model64Layer;
import net.torocraft.toroquest.entities.model.ModelGuard;

@SideOnly(Side.CLIENT)
public class RenderBanditLord extends RenderBiped<EntityBanditLord>
{
	// private static ResourceLocation TEXTURES = new ResourceLocation(ToroQuest.MODID + ":textures/entity/bandit/bandit.png");

	private final Model64Layer defaultModel = (Model64Layer) mainModel;

	public RenderBanditLord(RenderManager renderManagerIn)
	{
		super(renderManagerIn, new Model64Layer(), 0.5F);

		LayerBipedArmor layerbipedarmor = new LayerBipedArmor(this)
		{
			protected void initArmor()
			{
				this.modelLeggings = new ModelBiped(0.5F);
				this.modelArmor = new ModelBiped(0.9F);
			}
		};
		this.addLayer(layerbipedarmor);
		
        this.addLayer(new LayerArrow(this));
		
		// this.defaultLayers = Lists.newArrayList(this.layerRenderers);
	}

	/**
	 * Renders the desired {@code T} type Entity.
	 */
	@Override
	public void doRender(EntityBanditLord entity, double x, double y, double z, float entityYaw, float partialTicks)
	{
		this.setModelVisibilities(entity);
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	private void setModelVisibilities(EntitySentry entity)
	{
		ItemStack itemstack = entity.getHeldItemMainhand();
		ItemStack itemstack1 = entity.getHeldItemOffhand();
		
		defaultModel.setVisible(true);

		Model64Layer.ArmPose modelbiped$armpose = Model64Layer.ArmPose.EMPTY;
		Model64Layer.ArmPose modelbiped$armpose1 = Model64Layer.ArmPose.EMPTY;

		if (itemstack != null)
		{
			modelbiped$armpose = Model64Layer.ArmPose.ITEM;

			if (entity.getItemInUseCount() > 0)
			{
				EnumAction enumaction = itemstack.getItemUseAction();

				if (enumaction == EnumAction.BLOCK)
				{
					modelbiped$armpose = Model64Layer.ArmPose.BLOCK;
				}
				else if (enumaction == EnumAction.BOW)
				{
					modelbiped$armpose = Model64Layer.ArmPose.BOW_AND_ARROW;
				}
			}
		}

		if (itemstack1 != null)
		{
			modelbiped$armpose1 = Model64Layer.ArmPose.ITEM;

			if (entity.getItemInUseCount() > 0)
			{
				EnumAction enumaction1 = itemstack1.getItemUseAction();

				if (enumaction1 == EnumAction.BLOCK)
				{
					modelbiped$armpose1 = Model64Layer.ArmPose.BLOCK;
				}
			}
		}
		
		if ( entity.isDrinkingPotion() )
		{
			modelbiped$armpose1 = Model64Layer.ArmPose.BOW_AND_ARROW;
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
	
	@Override
	protected void preRenderCallback(EntityBanditLord entitylivingbaseIn, float partialTickTime)
	{
		GlStateManager.scale(1.4F, 1.4F, 1.4F);
	}
	
	@Override
	protected ResourceLocation getEntityTexture(EntityBanditLord entity)
	{
		return entity.getSkin();
	}
}