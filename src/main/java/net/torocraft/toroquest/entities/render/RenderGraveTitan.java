package net.torocraft.toroquest.entities.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.toroquest.ToroQuest;
import net.torocraft.toroquest.entities.EntityGraveTitan;
import net.torocraft.toroquest.entities.model.ModelGraveTitan;

@SideOnly(Side.CLIENT)
public class RenderGraveTitan extends RenderLiving<EntityGraveTitan>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(ToroQuest.MODID + ":textures/entity/grave_titan.png");

	public RenderGraveTitan(RenderManager renderManagerIn)
	{
		super(renderManagerIn, new ModelGraveTitan(), 5.0F);

//		this.addLayer(new LayerBipedArmor(this)
//		{
//			protected void initArmor() {
//				this.modelLeggings = new ModelZombie(0.5F, true);
//				this.modelArmor =  new ModelZombie(0.5F, true);
//			}
//		});

	}
	
//	@SideOnly(Side.CLIENT)
//    public AxisAlignedBB getRenderBoundingBox()
//    {
//        return this.getEntityBoundingBox();
//    }

	@Override
	protected void preRenderCallback(EntityGraveTitan entitylivingbaseIn, float partialTickTime)
	{
		float health = (entitylivingbaseIn.getHealth()+100)/entitylivingbaseIn.getMaxHealth();
		GlStateManager.scale(health, health, health);
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called
	 * unless you call Render.bindEntityTexture.
	 */
	@Override
	protected ResourceLocation getEntityTexture(EntityGraveTitan entity)
	{
		return TEXTURE;
	}

}
