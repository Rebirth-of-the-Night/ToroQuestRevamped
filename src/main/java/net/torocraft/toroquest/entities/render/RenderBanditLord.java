package net.torocraft.toroquest.entities.render;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.toroquest.ToroQuest;
import net.torocraft.toroquest.entities.EntityBanditLord;
import net.torocraft.toroquest.entities.EntitySentry;
import net.torocraft.toroquest.entities.model.ModelSentry;

@SideOnly(Side.CLIENT)
public class RenderBanditLord extends RenderBiped<EntityBanditLord>
{
	private static ResourceLocation TEXTURES = new ResourceLocation(ToroQuest.MODID + ":textures/entity/bandit/bandit_0.png");
	private final ModelSentry defaultModel;

	private final List<LayerRenderer<EntityBanditLord>> defaultLayers;

	public RenderBanditLord(RenderManager renderManagerIn)
	{
		super(renderManagerIn, new ModelSentry(), 0.75F);

		//super(renderManagerIn, new ModelToroVillager(), 0.5F);
        this.addLayer(new LayerHeldItem(this));
        // this.addLayer(new LayerArrow(this));
        //this.addLayer(new LayerDeadmau5Head(this));
        //this.addLayer(new LayerCape(this));
        //this.addLayer(new LayerCustomHead(this.getMainModel().bipedHead));
        //this.addLayer(new LayerElytra(this));
        //this.addLayer(new LayerEntityOnShoulder(renderManager));
		//LayerRenderer<?> layerrenderer = (LayerRenderer) this.layerRenderers.get(0);
        
		defaultModel = (ModelSentry) mainModel;

		LayerBipedArmor layerbipedarmor = new LayerBipedArmor(this)
		{
			protected void initArmor()
			{
				this.modelLeggings = new ModelSentry(0.5F, true);
				this.modelArmor = new ModelSentry(0.75F, true);
			}
		};
		this.addLayer(layerbipedarmor);
		
		this.defaultLayers = Lists.newArrayList(this.layerRenderers);
	}

	/**
	 * Allows the render to do state modifications necessary before the model is
	 * rendered.
	 */
	@Override
	protected void preRenderCallback(EntityBanditLord entitylivingbaseIn, float partialTickTime)
	{
		super.preRenderCallback(entitylivingbaseIn, partialTickTime);
		GlStateManager.scale(1.4F, 1.4F, 1.4F);
	}

//	public ResourceLocation getCapeTexture() {
//		return CAPETEXTURE;
//	}

	/**
	 * Renders the desired {@code T} type Entity.
	 */
	@Override
	public void doRender(EntityBanditLord entity, double x, double y, double z, float entityYaw, float partialTicks)
	{
		this.setModelVisibilities(entity);
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	private void setModelVisibilities(EntitySentry clientPlayer)
	{
		ModelSentry modelplayer = (ModelSentry) this.getMainModel();

		ItemStack itemstack = clientPlayer.getHeldItemMainhand();
		ItemStack itemstack1 = clientPlayer.getHeldItemOffhand();
		modelplayer.setVisible(true);

		modelplayer.isSneak = clientPlayer.isSneaking();
		ModelBiped.ArmPose modelbiped$armpose = ModelBiped.ArmPose.EMPTY;
		ModelBiped.ArmPose modelbiped$armpose1 = ModelBiped.ArmPose.EMPTY;

		if (itemstack != null) {
			modelbiped$armpose = ModelBiped.ArmPose.ITEM;

			if (clientPlayer.getItemInUseCount() > 0) {
				EnumAction enumaction = itemstack.getItemUseAction();

				if (enumaction == EnumAction.BLOCK) {
					modelbiped$armpose = ModelBiped.ArmPose.BLOCK;
				} else if (enumaction == EnumAction.BOW) {
					modelbiped$armpose = ModelBiped.ArmPose.BOW_AND_ARROW;
				}
			}
		}

		if (itemstack1 != null) {
			modelbiped$armpose1 = ModelBiped.ArmPose.ITEM;

			if (clientPlayer.getItemInUseCount() > 0) {
				EnumAction enumaction1 = itemstack1.getItemUseAction();

				if (enumaction1 == EnumAction.BLOCK) {
					modelbiped$armpose1 = ModelBiped.ArmPose.BLOCK;
				}
			}
		}

		if (clientPlayer.getPrimaryHand() == EnumHandSide.RIGHT) {
			modelplayer.rightArmPose = modelbiped$armpose;
			modelplayer.leftArmPose = modelbiped$armpose1;
		} else {
			modelplayer.rightArmPose = modelbiped$armpose1;
			modelplayer.leftArmPose = modelbiped$armpose;
		}

	}
	
	@Override
	protected ResourceLocation getEntityTexture(EntityBanditLord entity)
	{
		return TEXTURES;
	}
}