package net.torocraft.toroquest.entities.render;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.torocraft.toroquest.entities.EntityBanditLord;
import net.torocraft.toroquest.entities.EntityBas;
import net.torocraft.toroquest.entities.EntityConstruct;
import net.torocraft.toroquest.entities.EntityConstructQuest;
import net.torocraft.toroquest.entities.EntityFugitive;
import net.torocraft.toroquest.entities.EntityGraveTitan;
import net.torocraft.toroquest.entities.EntityGuard;
import net.torocraft.toroquest.entities.EntityMage;
import net.torocraft.toroquest.entities.EntityMonolithEye;
import net.torocraft.toroquest.entities.EntityOrc;
import net.torocraft.toroquest.entities.EntityPigLord;
import net.torocraft.toroquest.entities.EntitySentry;
import net.torocraft.toroquest.entities.EntitySpiderLord;
import net.torocraft.toroquest.entities.EntityVampireBat;
import net.torocraft.toroquest.entities.EntityVillageLord;

public class ToroQuestEntityRenders
{

	public static void init()
	{
		registerMageRenderer();
		registerMonolithEyeRenderer();
		EntityGuard.registerRenders();
		EntityBas.registerRenders();
		EntityVampireBat.registerRenders();
		EntitySentry.registerRenders();
		EntityVillageLord.registerRenders();
		EntityFugitive.registerRenders();
		EntityPigLord.registerRenders();
		EntitySpiderLord.registerRenders();
		EntityGraveTitan.registerRenders();
		EntityBanditLord.registerRenders();
		EntityOrc.registerRenders();
		EntityConstruct.registerRenders();
		EntityConstructQuest.registerRenders();
		//EntityAdventurer.registerRenders();
	}
	
	public static void registerMageRenderer()
	{
		RenderingRegistry.registerEntityRenderingHandler(EntityMage.class, new IRenderFactory<EntityMage>() {
			@Override 
			public Render<EntityMage> createRenderFor(RenderManager manager) { 
				return new RenderMage(manager); 
			}
		});
	}
	
	public static void registerMonolithEyeRenderer() {
		RenderingRegistry.registerEntityRenderingHandler(EntityMonolithEye.class, new IRenderFactory<EntityMonolithEye>() {
			@Override 
			public Render<EntityMonolithEye> createRenderFor(RenderManager manager) { 
				return new RenderMonolithEye(manager); 
			}
		});
	}
	
}
