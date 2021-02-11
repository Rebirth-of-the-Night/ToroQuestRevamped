package net.torocraft.toroquest.entities.render;

import net.minecraft.client.model.ModelVillager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerArrow;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.toroquest.ToroQuest;
import net.torocraft.toroquest.entities.EntityFugitive;

@SideOnly(Side.CLIENT)
public class RenderFugitive extends RenderLiving<EntityFugitive>
{
	private static final ResourceLocation VILLAGER_TEXTURES = new ResourceLocation(ToroQuest.MODID + ":textures/entity/villager.png");
	
	public RenderFugitive(RenderManager renderManagerIn)
	{
		super(renderManagerIn, new ModelVillager(0.0F), 0.5F);

	}

	public ModelVillager getMainModel()
	{
		return (ModelVillager) super.getMainModel();
	}
	
	@Override
	protected ResourceLocation getEntityTexture(EntityFugitive entity)
	{
		// TODO Auto-generated method stub
		return VILLAGER_TEXTURES;
	}

}