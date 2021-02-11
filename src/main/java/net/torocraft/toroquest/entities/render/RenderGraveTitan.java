package net.torocraft.toroquest.entities.render;

import net.minecraft.client.model.ModelZombieVillager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.toroquest.entities.EntityGraveTitan;

@SideOnly(Side.CLIENT)
public class RenderGraveTitan extends RenderBiped<EntityGraveTitan>
{
	private static final ResourceLocation SKELETON_TEXTURES = new ResourceLocation("textures/entity/zombie_villager/zombie_villager.png");

	public RenderGraveTitan(RenderManager renderManagerIn)
	{
		super(renderManagerIn, new ModelZombieVillager(), 2.0F);

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
		float health = (entitylivingbaseIn.getHealth()+25)/entitylivingbaseIn.getMaxHealth();
		GlStateManager.scale(8.0D * health, 8.0D * health, 8.0D * health);
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called
	 * unless you call Render.bindEntityTexture.
	 */
	@Override
	protected ResourceLocation getEntityTexture(EntityGraveTitan entity)
	{
		return SKELETON_TEXTURES;
	}

}
