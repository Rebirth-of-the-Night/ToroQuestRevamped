package net.torocraft.toroquest.entities.render;

import net.minecraft.client.model.ModelSpider;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderCaveSpider;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.toroquest.ToroQuest;
import net.torocraft.toroquest.entities.EntitySpiderLord;

@SideOnly(Side.CLIENT)
public class RenderSpiderLord extends RenderCaveSpider
{
	public RenderSpiderLord(RenderManager renderManagerIn)
	{
		super(renderManagerIn);
		this.shadowSize = 2.75F;
		this.mainModel = new ModelSpider();
	}
	
	private static ResourceLocation DEFAULT = new ResourceLocation(ToroQuest.MODID + ":textures/entity/cave_spider.png");
	
	protected ResourceLocation getEntityTexture(EntityCaveSpider entity)
    {
        return DEFAULT;
    }
	
//	@Override
//	protected void preRenderCallback(EntityCaveSpider entitylivingbaseIn, float partialTickTime)
//    {
//        GlStateManager.scale(3.9F, 3.7F, 3.9F);
//    }
	
	/**
     * Allows the render to do state modifications necessary before the model is rendered.
     */
	@Override
    protected void preRenderCallback(EntityCaveSpider entitylivingbaseIn, float partialTickTime)
    {
		super.preRenderCallback(entitylivingbaseIn, partialTickTime);
        float f = ((EntitySpiderLord) entitylivingbaseIn).getCreeperFlashIntensity(partialTickTime);
        float f1 = 1.0F + MathHelper.sin(f * 100.0F) * f * 0.01F;
        f = MathHelper.clamp(f, 0.0F, 1.0F);
        f = f * f;
        f = f * f;
        float f2 = (1.0F + f * 0.3F) * f1;
        float f3 = (1.0F + f * 0.2F) / f1;
        GlStateManager.scale(4.0F*f2, 3.8F*f3, 4.0F*f2);
    }

    /**
     * Gets an RGBA int color multiplier to apply.
     */
	@Override
    protected int getColorMultiplier(EntityCaveSpider entitylivingbaseIn, float lightBrightness, float partialTickTime)
    {
        float f = ((EntitySpiderLord) entitylivingbaseIn).getCreeperFlashIntensity(partialTickTime);

        if ((int)(f * 10.0F) % 2 == 0)
        {
            return 0;
        }
        else
        {
            int i = (int)(f * 0.05F * 255.0F);
            i = MathHelper.clamp(i, 0, 255);
            return i << 24 | 822083583;
        }
    }
}
