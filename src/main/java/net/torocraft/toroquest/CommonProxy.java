package net.torocraft.toroquest;

import java.io.File;

import net.minecraft.item.Item;
import net.minecraft.util.datafix.DataFixer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.torocraft.toroquest.civilization.CivilizationGeneratorHandlers;
import net.torocraft.toroquest.civilization.CivilizationHandlers;
import net.torocraft.toroquest.civilization.player.PlayerCivilizationCapabilityImpl;
import net.torocraft.toroquest.civilization.quests.util.Quests;
import net.torocraft.toroquest.config.ToroQuestConfiguration;
import net.torocraft.toroquest.entities.EntityVillageLord;
import net.torocraft.toroquest.entities.ToroQuestEntities;
import net.torocraft.toroquest.generation.WorldGenPlacer;
import net.torocraft.toroquest.generation.village.VillageHandlerBarracks;
import net.torocraft.toroquest.generation.village.VillageHandlerGuardTower;
import net.torocraft.toroquest.generation.village.VillageHandlerKeep;
import net.torocraft.toroquest.generation.village.VillageHandlerShop;
import net.torocraft.toroquest.gui.VillageLordGuiHandler;
//import net.torocraft.toroquest.material.ToolMaterials;
import net.torocraft.toroquest.network.ToroQuestPacketHandler;

public class CommonProxy
{

//	@Mod.EventHandler
//	public void preInit(FMLPreInitializationEvent event)
//	{
//		Configuration config = new Configuration(new File(event.getModConfigurationDirectory().getAbsolutePath() + "test"));
//		
//	}
	
//	@Config(modid = ToroQuest.MODID, name = "toroquest/" + "toroquest")
//	public static class ConfigHandler
//	{
//
//		public static final Compat compat = new Compat();
//
//		public static class Compat
//		{
////			@Config.LangKey("config.toroquest:toroquest")
////   		@Config.Comment("Should Echo add any interactions at all with Botania?")
////			public boolean botania = true;
//		}
//	}
	
	public void preInit(FMLPreInitializationEvent e)
	{
		
//		Configuration config = new Configuration(new File(e.getModConfigurationDirectory().getAbsolutePath()));
//		MinecraftForge.EVENT_BUS.register(new ToroQuestConfiguration());
//		config = new Configuration(config);

		// configDirectory = new File(e.getModConfigurationDirectory(), ToroQuest.MODID);
		// ToolMaterials.init();
		initConfig(e.getSuggestedConfigurationFile());
		MinecraftForge.EVENT_BUS.register(new CivilizationGeneratorHandlers());
		MinecraftForge.EVENT_BUS.register(new EventHandlers());
		MinecraftForge.EVENT_BUS.register(new CivilizationHandlers());
		VillageHandlerKeep.init();
		VillageHandlerShop.init();
		VillageHandlerGuardTower.init();
		VillageHandlerBarracks.init();
		ToroQuestPacketHandler.init();
		NetworkRegistry.INSTANCE.registerGuiHandler(ToroQuest.INSTANCE, new VillageLordGuiHandler());
		DataFixer datafixer = new DataFixer(922);
		EntityVillageLord.registerFixesVillageLord(datafixer);
		Quests.init();
	}

	private void initConfig(File configFile)
	{
		ToroQuestConfiguration.init(configFile);
		MinecraftForge.EVENT_BUS.register(new ToroQuestConfiguration());
	}

	public void init(FMLInitializationEvent e)
	{
		PlayerCivilizationCapabilityImpl.register();

		WorldGenPlacer.init();
		ToroQuestEntities.init();
	}

	public void postInit(FMLPostInitializationEvent e)
	{

	}
}
