package net.torocraft.toroquest.entities.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.toroquest.ToroQuest;
import net.torocraft.toroquest.entities.EntityConstruct;
import net.torocraft.toroquest.entities.model.ModelConstruct;


@SideOnly(Side.CLIENT)
public class RenderConstruct extends RenderLiving<EntityConstruct>
{
	
	private static final ResourceLocation IRON_GOLEM_TEXTURES = new ResourceLocation(ToroQuest.MODID + ":textures/entity/construct/construct.png");

	public RenderConstruct(RenderManager renderManagerIn)
    {
        super(renderManagerIn, new ModelConstruct(), 0.8F);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityConstruct entity)
    {
        return IRON_GOLEM_TEXTURES;
    }
    
    protected void applyRotations(EntityConstruct entityLiving, float p_77043_2_, float rotationYaw, float partialTicks)
    {
        super.applyRotations(entityLiving, p_77043_2_, rotationYaw, partialTicks);

        if ((double)entityLiving.limbSwingAmount >= 0.01D)
        {
            // float f = 13.0F;
            float f1 = entityLiving.limbSwing - entityLiving.limbSwingAmount * (1.0F - partialTicks) + 6.0F;
            float f2 = (Math.abs(f1 % 13.0F - 6.5F) - 3.25F) / 3.25F;
            GlStateManager.rotate(6.5F * f2, 0.0F, 0.0F, 1.0F);
        }
    }
}