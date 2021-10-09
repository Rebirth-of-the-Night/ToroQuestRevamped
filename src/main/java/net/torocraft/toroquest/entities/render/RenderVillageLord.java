package net.torocraft.toroquest.entities.render;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.toroquest.ToroQuest;
import net.torocraft.toroquest.civilization.CivilizationType;
import net.torocraft.toroquest.entities.EntityVillageLord;
import net.torocraft.toroquest.entities.model.ModelGuard;

@SideOnly(Side.CLIENT)
public class RenderVillageLord extends RenderBiped<EntityVillageLord>
{
	
	private static final ResourceLocation DEFAULT = new ResourceLocation(ToroQuest.MODID + ":textures/entity/lord/lord_null.png");
	private static final ResourceLocation TEXTURES_SUN = new ResourceLocation(ToroQuest.MODID + ":textures/entity/lord/lord_sun.png");
	private static final ResourceLocation TEXTURES_MOON = new ResourceLocation(ToroQuest.MODID + ":textures/entity/lord/lord_moon.png");
	private static final ResourceLocation TEXTURES_EARTH = new ResourceLocation(ToroQuest.MODID + ":textures/entity/lord/lord_earth.png");
	private static final ResourceLocation TEXTURES_WIND = new ResourceLocation(ToroQuest.MODID + ":textures/entity/lord/lord_wind.png");
	private static final ResourceLocation TEXTURES_FIRE = new ResourceLocation(ToroQuest.MODID + ":textures/entity/lord/lord_fire.png");
	private static final ResourceLocation TEXTURES_WATER = new ResourceLocation(ToroQuest.MODID + ":textures/entity/lord/lord_water.png");
	
	//private final ModelGuard defaultModel = (ModelGuard) mainModel;

	public RenderVillageLord(RenderManager renderManagerIn)
	{
		super(renderManagerIn, new ModelGuard(), 0.5F);
        //this.addLayer(new LayerArrow(this));

		LayerBipedArmor layerbipedarmor = new LayerBipedArmor(this)
		{
			protected void initArmor()
			{
				this.modelLeggings = new ModelBiped(0.5F);
				this.modelArmor = new ModelBiped(0.9F);
			}
		};
		this.addLayer(layerbipedarmor);
	}
	
//	@Override
//	public void doRender(EntityVillageLord entity, double x, double y, double z, float entityYaw, float partialTicks)
//	{
//		if ( ToroQuestConfiguration.reduceVillageLag )
//		{
//			boolean flag = false;
//			for ( EntityPlayer player : entity.world.playerEntities )
//			{
//				if ( entity.canEntityBeSeen(player) )
//				{
//					flag = true;
//					continue;
//				}
//			}
//			if ( !flag )
//			{
//				return;
//			}
//		}
//		super.doRender(entity, x, y, z, entityYaw, partialTicks);
//	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called
	 * unless you call Render.bindEntityTexture.
	 */
	
	CivilizationType civ = null;

	@Override
	protected ResourceLocation getEntityTexture(EntityVillageLord entity)
	{
		civ = entity.getCivilization();
		
		if ( civ == null )
		{
			return DEFAULT;
		}
		
		switch ( civ )
		{
			case FIRE:
			{
				return TEXTURES_FIRE;
			}
			case EARTH:
			{
				return TEXTURES_EARTH;
			}
			case MOON:
			{
				return TEXTURES_MOON;
			}
			case SUN:
			{
				return TEXTURES_SUN;
			}
			case WIND:
			{
				return TEXTURES_WIND;
			}
			case WATER:
			{
				return TEXTURES_WATER;
			}
			default:
			{
				return DEFAULT;
			}
		}
	}
}
