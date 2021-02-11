package net.torocraft.toroquest.entities.render;

import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerArrow;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.toroquest.entities.EntityPigLord;

@SideOnly(Side.CLIENT)
public class RenderPigLord extends RenderBiped<EntityPigLord>
{
    private static final ResourceLocation TEXTURES = new ResourceLocation("textures/entity/zombie_pigman.png");
    
	public RenderPigLord(RenderManager renderManagerIn)
	{
		super(renderManagerIn, new ModelZombie(), 0.5F);

		this.addLayer(new LayerHeldItem(this));
        //this.addLayer(new LayerArrow(this));

		this.addLayer(new LayerBipedArmor(this)
		{
			protected void initArmor()
			{
				this.modelLeggings = new ModelZombie(0.5F, true);
				this.modelArmor =  new ModelZombie(0.5F, true);
			}
		});

	}
	
	@Override
	protected void preRenderCallback(EntityPigLord entitylivingbaseIn, float partialTickTime)
	{
		//GL11.glScalef(3.5F, 4F, 3.5F);
		GlStateManager.scale(3.0F, 3.0F, 3.0F);
		super.preRenderCallback(entitylivingbaseIn, partialTickTime);
	}

//	public void transformHeldFull3DItemLayer()
//	{
//		GlStateManager.translate(0.09375F, 0.1875F, 0.0F);
//	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called
	 * unless you call Render.bindEntityTexture.
	 */
	@Override
	protected ResourceLocation getEntityTexture(EntityPigLord entity)
	{
		return TEXTURES;
	}

}
