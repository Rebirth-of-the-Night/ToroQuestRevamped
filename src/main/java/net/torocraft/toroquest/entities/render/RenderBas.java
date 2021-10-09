package net.torocraft.toroquest.entities.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.toroquest.ToroQuest;
import net.torocraft.toroquest.entities.EntityBas;
import net.torocraft.toroquest.entities.model.ModelVampireLord;

@SideOnly(Side.CLIENT)
public class RenderBas extends RenderLiving<EntityBas>
{
	private static final ResourceLocation SKELETON_TEXTURE = new ResourceLocation(ToroQuest.MODID + ":textures/entity/vampire_lord.png");

	public RenderBas(RenderManager renderManagerIn)
	{
		super(renderManagerIn, new ModelVampireLord(), 1.0F);
		// this.addLayer(new LayerHeldItem(this));
	}

//	@Override
//	protected void preRenderCallback(EntityBas entitylivingbaseIn, float partialTickTime)
//	{
//		GlStateManager.scale(1.0F, 1.0F, 1.0F);
//	}

//	public void transformHeldFull3DItemLayer()
//	{
//		GlStateManager.translate(0.09375F, 0.1875F, 0.0F);
//	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called
	 * unless you call Render.bindEntityTexture.
	 */
	@Override
	protected ResourceLocation getEntityTexture(EntityBas entity)
	{
		return SKELETON_TEXTURE;
	}

}
