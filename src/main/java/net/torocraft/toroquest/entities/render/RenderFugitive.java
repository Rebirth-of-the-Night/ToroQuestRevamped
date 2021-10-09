package net.torocraft.toroquest.entities.render;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderVillager;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.toroquest.ToroQuest;

@SideOnly(Side.CLIENT)
public class RenderFugitive extends RenderVillager
{
	public RenderFugitive(RenderManager renderManagerIn)
	{
		super(renderManagerIn);
	}

	private static final ResourceLocation VILLAGER_TEXTURES = new ResourceLocation(ToroQuest.MODID + ":textures/entity/villager.png");
	
	@Override
    protected ResourceLocation getEntityTexture(EntityVillager entity)
    {
        return VILLAGER_TEXTURES;
    }

}