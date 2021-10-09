package net.torocraft.toroquest.entities.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.toroquest.ToroQuest;
import net.torocraft.toroquest.entities.EntityConstruct;


@SideOnly(Side.CLIENT)
public class RenderConstructQuest extends RenderConstruct
{
	
	private static final ResourceLocation IRON_GOLEM_TEXTURES = new ResourceLocation(ToroQuest.MODID + ":textures/entity/construct/construct.png");

	public RenderConstructQuest(RenderManager renderManagerIn)
    {
        super(renderManagerIn);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityConstruct entity)
    {
        return IRON_GOLEM_TEXTURES;
    }
    
    @Override
    protected void applyRotations(EntityConstruct entityLiving, float p_77043_2_, float rotationYaw, float partialTicks)
    {
        super.applyRotations(entityLiving, p_77043_2_, rotationYaw, partialTicks);
		GlStateManager.scale(1.6F, 1.6F, 1.6F);
    }
}