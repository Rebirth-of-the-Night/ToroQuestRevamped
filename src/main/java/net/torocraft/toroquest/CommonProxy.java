package net.torocraft.toroquest;

import java.io.File;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.datafix.DataFixer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.torocraft.toroquest.block.BlockSmartBanner;
import net.torocraft.toroquest.block.BlockVillageSpawner;
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
import net.torocraft.toroquest.network.ToroQuestPacketHandler;

@EventBusSubscriber
public class CommonProxy
{
	
    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().register(new BlockVillageSpawner());
        event.getRegistry().register(new BlockSmartBanner());
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().register(new ItemBlock(BlockVillageSpawner.INSTANCE).setRegistryName(BlockVillageSpawner.REGISTRY_NAME));
        //event.getRegistry().register(new ItemBlock(BlockSmartBanner.INSTANCE).setRegistryName(BlockSmartBanner.REGISTRY_NAME));
    }
	
	//@Mod.EventHandler
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

	//@Mod.EventHandler
	private void initConfig(File configFile)
	{
		ToroQuestConfiguration.init(configFile);
		MinecraftForge.EVENT_BUS.register(new ToroQuestConfiguration());
	}

	//@Mod.EventHandler
	public void init(FMLInitializationEvent e)
	{
		PlayerCivilizationCapabilityImpl.register();
		WorldGenPlacer.init();
		ToroQuestEntities.init();
		initRegistries();
	}

	//@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent e)
	{

	}
	
	public static void initRegistries()
	{
		SoundHandler.registerSounds();
	}
	
}
