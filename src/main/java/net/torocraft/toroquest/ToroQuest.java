package net.torocraft.toroquest;


import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.torocraft.toroquest.block.BlockVillageSpawner;
import net.torocraft.toroquest.block.TileEntityVillageSpawner;
import net.torocraft.toroquest.configuration.ConfigurationHandler;

@Mod(modid = ToroQuest.MODID, name = ToroQuest.MODNAME, version = ToroQuest.VERSION, guiFactory = "net.torocraft." + ToroQuest.MODID + ".gui.GuiFactoryToroQuest")
public class ToroQuest
{
	public static final String MODID = "toroquest";
	public static final String VERSION = "1.12.2-5.3";
	public static final String MODNAME = "ToroQuest";

	@SidedProxy(clientSide = "net.torocraft.toroquest.ClientProxy", serverSide = "net.torocraft.toroquest.ServerProxy")
	public static CommonProxy proxy;

	@Instance(value = ToroQuest.MODID)
	public static ToroQuest INSTANCE;

	@EventHandler
	public void preInit(FMLPreInitializationEvent e)
	{
		proxy.preInit(e);

		ConfigurationHandler.init(e.getSuggestedConfigurationFile());
		MinecraftForge.EVENT_BUS.register(new ConfigurationHandler());
		MinecraftForge.EVENT_BUS.register(new EventHandlers());
		// GameRegistry.register(BlockVillageSpawner.class, new ResourceLocation(MODID, BlockVillageSpawner.NAME));
	}

	@EventHandler
	public void init(FMLInitializationEvent e) {
		proxy.init(e);
		
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent e)
	{
		proxy.postInit(e);
	}

	@EventHandler
	public void serverLoad(FMLServerStartingEvent e) {
		e.registerServerCommand(new ToroQuestCommand());
	}
}
