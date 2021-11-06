package net.torocraft.toroquest.config;

import java.io.File;
import java.util.ArrayList;

import javax.annotation.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.torocraft.toroquest.ToroQuest;

public class ToroQuestConfiguration
{
	private static final String CATEGORY = "ToroQuest Settings";
	private static final String CATEGORY_SKINS = "Custom Mob Skins";
	private static final String CATEGORY_TRADES = "Trades";
	private static final String CATEGORY_RAIDS = "Village Raids";
	private static final String CATEGORY_MOBS = "Mobs";
	private static final String CATEGORY_WEAPONS = "Bandit, Orc, and Guard Weapons & Shields";
	private static final String CATEGORY_GEN = "Generation";
	private static final String CATEGORY_SPAWNING = "Spawning";
	private static final String CATEGORY_BOSSES = "Bosses";
	private static final String CATEGORY_REPUTATION = "Reputation";

	private static Configuration config;
	
	public static boolean useCrownToCreateNewProvinces = true;
	
	public static float bossHealthMultiplier = 1.0F;
	public static float bossAttackDamageMultiplier = 1.0F;
		
	// BANDIT
	public static int banditBaseHealth = 20;
	public static float banditAttackDamage = 4.0F;
	public static int banditArmor = 0;
	public static int banditArmorToughness = 0;
	public static float banditDamageMultiplier = 0.6F;
	//public static float banditDamageAdditiveModifierPer1HP = 0.05F;
	
	// ORC
	public static int orcBaseHealth = 24;
	public static float orcAttackDamage = 5.0F;
	public static int orcArmor = 0;
	public static int orcArmorToughness = 0;
	public static float orcDamageMultiplier = 0.7F;
	//public static float orcDamageAdditiveModifierPer1HP = 0.06F;
	
	public static int banditAndOrcFleeHealthPercentBase = 15;
	public static int banditAndOrcFleeHealthPercentRange = 15;
	
	// GUARD
	public static float guardSpeakChance = 0.25F;
	public static int guardBaseHealth = 40;
	public static float guardAttackDamage = 5.0F;
	public static int guardArmor = 0;
	public static int guardArmorToughness = 8;
	public static float guardDamageBaseMultiplierToMobs = 1.0F;
	public static float guardDamageBaseMultiplierToPlayers = 0.7F;
	// public static float guardDamageAdditiveModifierPerAttackDamage = 0.05F;
	
	public static float trophyTitanAdditionalGuardDamageMulitiplier = 1.5F;
	public static float guardDamageBaseMultiplierToMobsOutsideProvinceOrToBosses = 0.6F;
	public static int minBaseHealthToBeConsideredBossMob = 500;
	
	public static int raiderSiegeChance = 40;

	public static boolean enableBloodParticles = false;
	public static boolean betterKnockback = true;
	public static float knockUpStrength = 0.5F;
	
	// public static float banditDamageBasedOnHealthModifier = 0.01; per 1HP
	// public static float orcDamageBasedOnHealthModifier = 0.01; per 1HP
	// public static float guardDamageBasedOnHealthModifier = 0.01;
	// increases the damage modifier by X for every 1HP over their base health. So with the default value (0.01), 
	// a bandit with 50HP over their default base health (20) would have a damage multiplier of...   
	//1 * ( banditDamageMultiplier + banditDamageBasedOnHealthModifier )  ->  1 * (1.25 * 0.5)  -> 1.75
	
	public static int spawnHeight = 96;
	
	public static int toroVillagerMateChance = 1200;
	public static float villageDoorsModifier = 0.4F;
	public static int maxVillagersPerVillage = 20;
	
	public static String lanternResourceName = "futuremc:lantern";
	public static String chainResourceName = "futuremc:chain";
	public static String craftingTableResourceName = "minecraft:crafting_table";
	public static String enchantingTableResourceName = "minecraft:enchanting_table";
	public static boolean replaceVillageChestsWithTrappedChests = true;
	
	public static float banditArmorDropChance = 0.1F;
	public static float banditHandsDropChance = 0.3F;
	
	public static float guardArmorDropChance = 0.1F;
	public static float guardHandsDropChance = 0.3F;
		
	public static boolean banditsUseBoats = true;
	public static boolean banditsDropEmeralds = true;
	public static boolean banditsDropMasks = true;
	public static boolean orcsDropEmeralds = true;
	public static boolean orcsDropMasks = false;
	public static boolean steamGolemsDropLoot = true;
	public static boolean anyAnimalForBreedQuest = false;
	public static boolean cartographerMapTrade = true;
	public static int banditsDropPotions = 6;
	
	public static boolean guardWatchClosestTask = false;
	public static boolean guardLookIdleTask = false;

	//public static boolean renderGuardCape = true;
	public static boolean renderBanditMask = true;
	public static boolean renderOrcMask = false;
	public static boolean banditsHaveArmorForSpartanWeaponry = true;
	public static boolean guardsHaveArmorForSpartanWeaponry = true;
	
	public static boolean showProvinceEnterLeaveMessage = true;
	public static boolean sendRepLevelMessage = true;
	
	public static boolean mobsAttackGuards = true;
	public static boolean iMobAttackVillagers = true;
	public static boolean removeMuleOnCaravanEscort = false;
	
	public static boolean enderIdolTeleport = true;
	// public static boolean useOreDicForMineQuest = true;

	public static boolean titanBoss = true;
	public static boolean pigBoss = true;
	public static boolean banditBoss = true;
	public static boolean golemBoss = true;
	public static boolean skeletonBoss = true;
	public static boolean spiderBoss = true;
	public static boolean mageBoss = true;
	public static boolean enderBoss = true;
		
	public static int unexpensiveRepLoss = 1;
	public static int leashVillagerRepLoss = 5;
	public static int fireGriefRepLoss = 5;
	public static int lavaGriefRepLoss = 10;
	public static int murderLivestockRepLoss = 10;
	public static int abandonQuestRepLoss = 10;
	public static int expensiveRepLoss = 10;
	
	public static int killMobRepGain = 1;
	public static int donateEmeraldRepGain = 1;
	public static int donateBanditMaskRepGain = 4;
	public static int recruitGuardRepGain = 2;
	public static int returnFugitiveRepGain = 8;
	public static int escortCaravanRepGain = 12;
	public static int donateArtifactRepGain = 25;
	public static int donateTrophyRepGain = 25;
	public static boolean disableTreeChoppingQuest = false;
	
	public static String scrollTradeItem = "minecraft:emerald";
	public static int scrollTradeAmount = 3;
	public static int bannerTradeAmount = 2;
	
	public static int destroyedVillagesNearSpawnDistance = 320;
	public static boolean unregisterDestroyedVillages = true;
	public static boolean useBiomeSpecificProvinces = true;
	public static boolean unleashSandwhichHorrorOnVillages = true;
	public static boolean useIronBarsForHeadSpike = true;
	
	public static boolean specificEntityNames = true;
	public static boolean useDefaultVillagersOutsideOfProvince = true;
	public static boolean useDefaultVillagers = false;
	public static boolean villagesSpawnGolems = false;

	public static int banditMountChance = 3;
	public static int orcMountChance = 0;
	public static boolean orcsAreNeutral = false;

	public static boolean loseReputationForCropGrief = true;
	public static boolean loseReputationForAnimalGrief = true;
	public static boolean loseReputationForBlockGrief = true;
	
	public static boolean showQuestCompletionAboveActionBar = true;
	
	//public static boolean banditsWearArmor = true;
	public static boolean guardsHaveDialogue = true;
	public static boolean recruitBandits = true;
	public static boolean recruitVillagers = true;
	
//	public static boolean vampirismCompatability = false;

	public static int zombieAttackVillageChance = 25;
	public static int zombieRaiderVillagerChance = 25;
	public static int provinceSiegeRate = 8;
	public static int caravanSpawnRate = 10;
	//public static int adventurerSpawnRate = 6;
	public static int banditSpawnRate = 8;
	public static int fugitiveSpawnRate = 8;
	public static int artifactDropRate = 10;
	public static int disableMobSpawningNearVillage = 104;
	public static int disableZombieSpawningNearVillage = 80;
	
	public static String[] tradeList = new String[]{};
	public static ArrayList<Trade> trades = new ArrayList<Trade>();
	
	public static String[] mobsList = new String[]{};
	public static ArrayList<KillMob> mobs = new ArrayList<KillMob>();
	
	public static String[] donateList = new String[]{};
	public static ArrayList<Donate> donate = new ArrayList<Donate>();

//	public static String[] pitQuestList = new String[]{};
//	public static ArrayList<pitMob> pitQuestMobs = new ArrayList<pitMob>();
		
	public static String[] raiderString_BROWN_MITHRIL = new String[]{};
	public static ArrayList<Raider> raiderList_BROWN_MITHRIL = new ArrayList<Raider>();
	
	public static String[] raiderString_GREEN_WILD = new String[]{};
	public static ArrayList<Raider> raiderList_GREEN_WILD = new ArrayList<Raider>();
	
	public static String[] raiderString_RED_BRIAR = new String[]{};
	public static ArrayList<Raider> raiderList_RED_BRIAR = new ArrayList<Raider>();
	
	public static String[] raiderString_BLACK_MOOR = new String[]{};
	public static ArrayList<Raider> raiderList_BLACK_MOOR = new ArrayList<Raider>();
	
	public static String[] raiderString_YELLOW_DAWN = new String[]{};
	public static ArrayList<Raider> raiderList_YELLOW_DAWN = new ArrayList<Raider>();
	
	public static String[] raiderString_BLUE_GLACIER = new String[]{};
	public static ArrayList<Raider> raiderList_BLUE_GLACIER = new ArrayList<Raider>();
	
	public static String[] banditShields = new String[]{};
	public static String[] banditOneHandedMeleeWeapons = new String[]{};
	public static String[] banditTwoHandedMeleeWeapons = new String[]{};
	public static String[] banditRangedWeapons = new String[]{};

	public static String[] banditShieldsPowerful = new String[]{};
	public static String[] banditOneHandedMeleeWeaponsPowerful = new String[]{};
	public static String[] banditTwoHandedMeleeWeaponsPowerful = new String[]{};
	public static String[] banditRangedWeaponsPowerful = new String[]{};

	
	public static String[] orcShields = new String[]{};
	public static String[] orcOneHandedMeleeWeapons = new String[]{};
	public static String[] orcTwoHandedMeleeWeapons = new String[]{};
	public static String[] orcRangedWeapons = new String[]{};

	public static String[] orcShieldsPowerful = new String[]{};
	public static String[] orcOneHandedMeleeWeaponsPowerful = new String[]{};
	public static String[] orcTwoHandedMeleeWeaponsPowerful = new String[]{};
	public static String[] orcRangedWeaponsPowerful = new String[]{};

	public static String[] enchantFirstBanditAndOrcMeleeWeapon = new String[]{};
	public static String[] enchantSecondBanditAndOrcMeleeWeapon = new String[]{};

	public static String[] enchantFirstBanditAndOrcShield = new String[]{};
//	public static String[] enchantSecondBanditAndOrcShield = new String[]{};

	public static String[] enchantFirstBanditAndOrcRanged = new String[]{};
	public static String[] enchantSecondBanditAndOrcRanged = new String[]{};

	public static int enchantFirstBanditAndOrcChance = 20;
	public static int enchantSecondBanditAndOrcChance = 60;
	
	public static String guardWeapon_RED_BRIAR = "minecraft:iron_sword";
	public static String guardShield_RED_BRIAR = "minecraft:shield";
	
	public static String guardWeapon_GREEN_WILD = "minecraft:iron_sword";
	public static String guardShield_GREEN_WILD = "minecraft:shield";

	public static String guardWeapon_BROWN_MITHRIL = "minecraft:iron_sword";
	public static String guardShield_BROWN_MITHRIL = "minecraft:shield";
	
	public static String guardWeapon_YELLOW_DAWN = "minecraft:iron_sword";
	public static String guardShield_YELLOW_DAWN = "minecraft:shield";
	
	public static String guardWeapon_BLUE_GLACIER = "minecraft:iron_sword";
	public static String guardShield_BLUE_GLACIER= "minecraft:shield";
	
	public static String guardWeapon_BLACK_MOOR = "minecraft:iron_sword";
	public static String guardShield_BLACK_MOOR = "minecraft:shield";

	
	public static String greenName = "Wild";
	public static String brownName = "Mithril";
	public static String redName = "Briar";
	public static String blackName = "Moor";
	public static String yellowName = "Dawn";
	public static String blueName = "Glacier";
			
	//public static File configDirectory;
	//public static String configFolderName = "toroquest_skins";


	// GeneralConfig.init(new File(configDirectory, "general.cfg"));
	
	
	
	public static void init(File configFile)
	{
		if (config == null)
		{
			config = new Configuration(configFile);
			loadConfiguration();
		}
	}
	
	public static int adventurerSkins = 1;
	public static int banditSkins = 1;
	public static int orcSkins = 1;

	private static void loadConfiguration()
	{
		try
		{
			
			// SKINS =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
			
			adventurerSkins = config.getInt("adventurerSkins", CATEGORY_SKINS, 1, 1, 5120,
					"The number of adventurer skins in your Resource Folder (check mod description on CurseForge for details) that you wish to use. For example, in your folder you have adventurer_0,"
					+ " adventurer_1, adventurer_2, adventurer_3, adventurer_4, then you would set this number to 5.");
			
			banditSkins = config.getInt("banditSkins", CATEGORY_SKINS, 1, 1, 5120,
					"The number of bandit skins in your Resource Folder (check mod description on CurseForge for details) that you wish to use. For example, in your folder you have bandit_0,"
					+ " bandit_1, bandit_2, bandit_3, bandit_4, then you would set this number to 5.");
			
			orcSkins = config.getInt("orcSkins", CATEGORY_SKINS, 1, 1, 5120,
					"The number of orc skins in your Resource Folder (check mod description on CurseForge for details) that you wish to use. For example, in your folder you have orc_0,"
					+ " orc_1, orc_2, then you would set this number to 3.");
			
			// GEN =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
			
			useBiomeSpecificProvinces = config.getBoolean("useBiomeSpecificProvinces", CATEGORY_GEN, true,
					"If set to true, villages will generate their province based off the biome they are in, otherwise they will be random.  "
					+ "(I highly recommened you include the mod Mo' Villages so that villages spawn more frequently and in any biome. ***IMPORTANT*** If Biomes O' Plenty is installed I reccomend going throught and changing the settings for each biome to canGenerateVillages=true. To have these changes persist, copy the files in the 'defaults' folder and paste them into the folder with the 'Put biome configs here' file so that the biome settings will not be overwritten! You can also use BiomeTweaker to allow villages to spawn in every biome!!!)");

			unregisterDestroyedVillages = config.getBoolean("unregisterDestroyedVillages", CATEGORY_GEN, true,
					"Village destoyed through destroyedVillagesNearSpawnDistance are unregistered and no longer show UI.");
			
			unleashSandwhichHorrorOnVillages = config.getBoolean("unleashSandwhichHorrorOnVillages", CATEGORY_GEN, true,
					"If set to true, a giant Sandwhich Horror comes through to destroy structures, grief chests, and eats all the poor innocent villagers in villages. Guards should just 'git gud' next time. #rekt");
			
			useIronBarsForHeadSpike = config.getBoolean("useIronBarsForHeadSpike", CATEGORY_GEN, true,
					"If set to true, use iron bars for the head spikes that generate in villages.");
			
			destroyedVillagesNearSpawnDistance = config.getInt("destroyedVillagesNearSpawnDistance", CATEGORY_GEN, 320, 0, Integer.MAX_VALUE,
					"Villages within X blocks of [0,0] will generate as ruined/raided/destoyed villages.");

			lanternResourceName = config.getString("lanternResourceName", CATEGORY_GEN, "futuremc:lantern",
					"Resource string for Lanterns that generate in villages. Some mods add lanterns so pick your fav.");
			
			chainResourceName = config.getString("chainResourceName", CATEGORY_GEN, "futuremc:chain",
					"Resource string for Chains that generate in villages.");
			
			craftingTableResourceName = config.getString("craftingTableResourceName", CATEGORY_GEN, "minecraft:crafting_table",
					"Resource string for Crafting Tables that generate in villages. This setting is mostly for me, I change the crafting tables with thebetweenlands ones because the items show up on top which looks cool.");
			
			replaceVillageChestsWithTrappedChests = config.getBoolean("replaceVillageChestsWithTrappedChests", CATEGORY_GEN, true,
					"Replace the chests that generate in villages with trapped chests. Stealing/ breaking a trapped chest will trigger a crime report. However, stealing/ breaking normal chests is not a crime! This is so players can place their usual normal chests in villages and open/use them without getting aggrod by every guard in sight. This setting replaces those normal chests that generate in villages with trapped ones to differentiate between (normal) player chests and (trapped) village chests.");
			
			enchantingTableResourceName = config.getString("enchantingTableResourceName", CATEGORY_GEN, "minecraft:enchanting_table",
					"Resource string for Enchanting Tables that generate in villages.");
			
			// MOBS =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

			toroVillagerMateChance = config.getInt("toroVillagerMateChance", CATEGORY_MOBS, 1200, 1, Integer.MAX_VALUE,
					"Chance for villagers to mate (vanilla is 500). However, villagers no longer need to be 'willing' (enough food and/or enough trades), they only need a bed to mate.");

			villageDoorsModifier = config.getFloat("villageDoorsModifier", CATEGORY_MOBS, 0.4F, 0.0F, 8.0F,
					"If the number of (village doors) * villageDoorsModifier is less than the number of (villagers), then allow villagers to breed (vanilla is 0.35F, lower value means more villagers breeding, 0.5F means 2 villagers are allowed per valid village door. Set to 0.0F to disable breeding).");
			
			maxVillagersPerVillage = config.getInt("maxVillagersPerVillage", CATEGORY_MOBS, 20, 0, 256,
					"The number of villagers allowed in a village.");
			
			orcsAreNeutral = config.getBoolean("orcsAreNeutral", CATEGORY_MOBS, false,
					"Enable to make orcs neutral like pigmen.");
			
			mobsAttackGuards = config.getBoolean("mobsAttackGuards", CATEGORY_MOBS, true,
					"Enable to have mobs attack guards on sight.");
			
			iMobAttackVillagers = config.getBoolean("iMobAttackVillagers", CATEGORY_MOBS, true,
					"Enable to add have all IMobs attack villagers on sight.");
			
			useDefaultVillagersOutsideOfProvince = config.getBoolean("useDefaultVillagersOutsideOfProvince", CATEGORY_MOBS, true, "Enable to make default villagers"
					+ "spawn (instead of toro villagers) outside the bounds of a province. Enable this if you want to be able to trade with villagers that spawn from random structures such as Recurrent Complex.");
			
			useDefaultVillagers = config.getBoolean("useDefaultVillagers", CATEGORY_MOBS, false, "If you are using another village mod such as Minecraft Comes Alive, enable this to disable toro villagers and allow normal villager spawning");
			
			banditsDropEmeralds = config.getBoolean("banditsDropEmeralds", CATEGORY_MOBS, true,
					"Enable to allow bandits a 1/3 chance to drop 1-3 Emeralds on death.");
			
			banditsUseBoats = config.getBoolean("banditsUseBoats", CATEGORY_MOBS, true,
					"Enable to allow bandits to use boats to make them a deadly threat on water.");
			
			enableBloodParticles = config.getBoolean("enableBloodParticles", CATEGORY_MOBS, false,
					"Enable to have guards and bandits show blood particles when attacked.");
			
			betterKnockback = config.getBoolean("betterKnockback", CATEGORY_MOBS, true,
					"Enable have the knockback_resistance attribute reduce the STRENGTH of knockback effects, rather than reducing the CHANCE to not be knocked back. (For example: by default, a knockback_resistance of 0.5 means a 50% chance to not be knocked back from an attack. However, if this setting is true, a knockback_resistance of 0.5 means the distance or effects of being knocked back are 50% less far or powerful.");			
			
			knockUpStrength = config.getFloat("knockUpStrength", CATEGORY_MOBS, 0.5f, 0.0f, 2.0f,
					"Multiply the motionY amount of knockback by this amount. Set to 0.5 by default to reduce the motionY by 50%. Does nothing if betterKnockback is disabled.");
			
			banditsDropPotions = config.getInt("banditsDropPotions", CATEGORY_MOBS, 6, 0, 128,
					"Chance ( 1 out of X ) for bandits to drop 1 Potion on death. Set to 0 to disable.");
			
			banditsDropMasks = config.getBoolean("banditsDropMasks", CATEGORY_MOBS, true,
					"Enable to allow orcs to drop their masks on death, which can be worn or turned in for reputation.");
			
			steamGolemsDropLoot = config.getBoolean("steamGolemsDropLoot", CATEGORY_MOBS, true,
					"Enable to allow Steam Golems to drop loot.");
			
			orcsDropEmeralds = config.getBoolean("orcsDropEmeralds", CATEGORY_MOBS, true,
					"Enable to allow orcs a 1/3 chance to drop 1-4 Emeralds on death.");
			
			orcsDropMasks = config.getBoolean("orcsDropMasks", CATEGORY_MOBS, false,
					"Enable to allow ors to drop their masks on death, which can be worn or turned in for reputation. If this is true, they will become neutral to players wearing masks as well.");
			
			banditHandsDropChance = config.getFloat("banditHandsDropChance", CATEGORY_MOBS, 0.3f, 0.0f, 1.0f,
					"Chance for bandits to drop their held items on death.");

			banditArmorDropChance = config.getFloat("banditArmorDropChance", CATEGORY_MOBS, 0.1f, 0.0f, 1.0f,
					"Chance for bandits to drop their armor on death. Does nothing if banditsHaveArmorForSpartanWeaponry is set to false.");
			
			guardHandsDropChance = config.getFloat("guardHandsDropChance", CATEGORY_MOBS, 0.3f, 0.0f, 1.0f,
					"Chance for guards to drop their held items on death.");

			guardArmorDropChance = config.getFloat("guardArmorDropChance", CATEGORY_MOBS, 0.1f, 0.0f, 1.0f,
					"Chance for guards to drop their armor on death. Does nothing if guardsHaveArmorForSpartanWeaponry is set to false.");
			
//			renderGuardCape = config.getBoolean("renderGuardCape", CATEGORY_MOBS, true,
//					"Enable to allow cape to be rendered/ visible on guards. Disabling this will slightly improve performance!");
			
			renderBanditMask = config.getBoolean("renderBanditMask", CATEGORY_MOBS, true,
					"Enable to allow mask to be rendered/ visible on bandits.");
			
			guardWatchClosestTask = config.getBoolean("guardWatchClosestTask", CATEGORY_MOBS, false,
					"Enable to add watch closest task to guards (they look at entities around them randomly). Disable to slightly improve performance.");
			
			guardLookIdleTask = config.getBoolean("guardLookIdleTask", CATEGORY_MOBS, false,
					"Enable to add look idle task to guards (they look around randomly). Disable to slightly improve performance.");
			
			renderOrcMask = config.getBoolean("renderOrcMask", CATEGORY_MOBS, false,
					"Enable to allow mask to be rendered/ visible on orcs.");

			banditsHaveArmorForSpartanWeaponry = config.getBoolean("banditsHaveArmorForSpartanWeaponry", CATEGORY_MOBS, true,
					"Enable to have sabers, rapiers, and warhammers deal the correct damage against bandits and orcs. This setting, when true, adds leather armor to bandits (which is not rendered! Meaning it will be invisible). However, if banditArmorDropChance is higher than 0, they will be able to drop leather armor.");

			guardsHaveArmorForSpartanWeaponry = config.getBoolean("guardsHaveArmorForSpartanWeaponry", CATEGORY_MOBS, true,
					"Enable to have sabers, rapiers, warhammers, and hammers deal the correct damage against guards. This setting, when true, adds chainmail armor to guards (which is not rendered! Meaning it will be invisible). However, if guardArmorDropChance is higher than 0, they will be able to drop chainmail armor.");

			villagesSpawnGolems = config.getBoolean("villagesSpawnGolems", CATEGORY_MOBS, false,
					"Enable to allow Iron Golems spawning in villages.");
			
			removeMuleOnCaravanEscort = config.getBoolean("removeMuleOnCaravanEscort", CATEGORY_MOBS, false,
					"Setting this to true despawns the caravan's mule after the caravan has been escorted.");
			
			guardsHaveDialogue = config.getBoolean("guardsHaveDialogue", CATEGORY_MOBS, true,
					"Enable to allow guards to speak in the chat. I worked very hard adding all that dialogue so shame on you if this is set to false. But I get it, sometimes dialogue really doesn't fit in minecraft.");
			
			

//			feralWolfResourceName = config.getString("feralWolfResourceName", CATEGORY_MOBS, "its_meow.betteranimalsplus.common.entity.EntityFeralWolf",
//					"Resource string for feral wolves. These will spawn to attack villages instead of normal wolves.");
			
			
			// GUARD
			
			guardSpeakChance = config.getFloat("guardSpeakChance", CATEGORY_MOBS, 0.25f, 0.0f, 1.0f,
					"Chance for guards to speak to players who are near them. The value 0.25f means a 25% chance to speak, and if they do not, their ability to speak passively will be put on a cooldown.");
			
			guardDamageBaseMultiplierToMobs = config.getFloat("guardDamageBaseMultiplierToMobs", CATEGORY_MOBS, 1.0f, 0.0f, 16.0f,
					"Guard base multiplier to mobs. Guard final damage to mobs = DAMAGE * ( guardDamageBaseMultiplierToMobs + ( guardDamageAdditiveModifierPer1HP * ( MAX_HEALTH_ATTRIBUTE - guardBaseHealth ) ) )");
			
			guardDamageBaseMultiplierToPlayers = config.getFloat("guardDamageBaseMultiplierToPlayers", CATEGORY_MOBS, 0.7f, 0.25f, 16.0f,
					"Guard base multiplier to players. Guard damage to players = DAMAGE * ( guardDamageBaseMultiplierToPlayers + ( guardDamageAdditiveModifierPer1HP * ( MAX_HEALTH_ATTRIBUTE - guardBaseHealth ) ) )");
			
//			guardDamageAdditiveModifierPer1HP = config.getFloat("guardDamageAdditiveModifierPer1HP", CATEGORY_MOBS, 0.05f, 0.0f, 1.0f,
//					"Guard additive multiplier to players/mobs. For every 1 HP the guard has over guardBaseHealth, add guardDamageAdditiveModifierPer1HP to the guard's damage multiplier. (Assuming default config values) For example:   guardBaseHealth is 24 in the config, but the guards MAX_HEALTH_ATTRIBUTE is increased to 40 through another mod (such as WAIG). This guard that spawned now has 16 health over it's base value. So, the damage the guard now does to mobs is multiplied by (guardDamageBaseMultiplierToMobs + guardDamageAdditiveModifierPer1HP * 16) = (1.0 + 0.05 * 16) = (1.8)");
//			
			trophyTitanAdditionalGuardDamageMulitiplier = config.getFloat("trophyTitanAdditionalGuardDamageMulitiplier", CATEGORY_MOBS, 1.5f, 1.0f, 16.0f,
					"Guard final damage is muliplied by this amount (default 1.5) to mobs in provinces with the titan trophy active.");
			
			guardDamageBaseMultiplierToMobsOutsideProvinceOrToBosses = config.getFloat("guardDamageBaseMultiplierToMobsOutsideProvinceOrToBosses", CATEGORY_MOBS, 0.6f, 0.0f, 1.0f,
					"Guard final damage is multiplied by this value while outside a province or when fighting bosses. This is useful if you want to prevent the effectiveness of players using guards to 'cheese' bosses.");
			
			minBaseHealthToBeConsideredBossMob = config.getInt("minBaseHealthToBeConsideredBossMob", CATEGORY_MOBS, 500, 0, 2560,
					"When a mob has at least minBaseHealthToBeConsideredBossMob health, they will be considered a 'boss'. This is useful for mods with powerful creatures you don't want players to 'cheese' with guards.");
			
			guardBaseHealth = config.getInt("guardBaseHealth", CATEGORY_MOBS, 30, 1, 256,
					"Base HP of guards.");
			
			guardArmor = config.getInt("guardArmor", CATEGORY_MOBS, 0, 0, 20,
					"Guard armor value.");
			
			guardAttackDamage = config.getFloat("guardAttackDamage", CATEGORY_MOBS, 5.0f, 0.0f, 256.0f,
					"Guard attack damage value.");
			
			guardArmorToughness = config.getInt("guardArmorToughness", CATEGORY_MOBS, 8, 0, 20,
					"Guard armor toughness value.");
			
			// BANDIT
			
			banditBaseHealth = config.getInt("banditBaseHealth", CATEGORY_MOBS, 20, 1, 256,
					"Base HP of bandits.");
						
			banditAttackDamage = config.getFloat("banditAttackDamage", CATEGORY_MOBS, 4.0f, 0.0f, 256.0f,
					"Bandit attack damage value.");
			
			banditArmor = config.getInt("banditArmor", CATEGORY_MOBS, 0, 0, 20,
					"Armor value of bandits.");
			
			banditArmorToughness = config.getInt("banditArmorToughness", CATEGORY_MOBS, 0, 0, 20,
					"Armor toughness value of orcs.");
			
			banditDamageMultiplier = config.getFloat("banditDamageMultiplier", CATEGORY_MOBS, 0.6f, 0.0f, 16.0f,
					"See guardDamageBaseMultiplierToMobs for more info.");
			
//			banditDamageAdditiveModifierPer1HP = config.getFloat("banditDamageAdditiveModifierPer1HP", CATEGORY_MOBS, 0.05f, 0.0f, 1.0f,
//					"See guardDamageAdditiveModifierPer1HP for more info.");
			
			// ORC
			
			orcBaseHealth = config.getInt("orcBaseHealth", CATEGORY_MOBS, 24, 1, 256,
					"Base HP of orcs.");
			
			orcArmor = config.getInt("orcArmor", CATEGORY_MOBS, 0, 0, 20,
					"Armor value of orcs.");
			
			orcAttackDamage = config.getFloat("orcAttackDamage", CATEGORY_MOBS, 4.0f, 0.0f, 256.0f,
					"Orc attack damage value.");
			
			orcArmorToughness = config.getInt("orcArmorToughness", CATEGORY_MOBS, 0, 0, 20,
					"Armor toughness value of orcs.");
			
			orcDamageMultiplier = config.getFloat("orcDamageMultiplier", CATEGORY_MOBS, 0.7f, 0.0f, 16.0f,
					"See guardDamageBaseMultiplierToMobs for more info.");
			
//			orcDamageAdditiveModifierPer1HP = config.getFloat("orcDamageAdditiveModifierPer1HP", CATEGORY_MOBS, 0.06f, 0.0f, 1.0f,
//					"See guardDamageAdditiveModifierPer1HP for more info.");
			
			//
			
			
			banditMountChance = config.getInt("banditMountChance", CATEGORY_MOBS, 0, 0, 10,
					"The chance (out of 10) for a group of non-village-raiding bandits to spawn as archers on horse mounts. Set to 0 to disable.");
			
			orcMountChance = config.getInt("orcMountChance", CATEGORY_MOBS, 0, 0, 10,
					"The chance (out of 10) for a group of non-village-raiding orcs to spawn as archers on horse mounts. Set to 0 to disable.");
			
			banditAndOrcFleeHealthPercentBase = config.getInt("banditAndOrcFleeHealthPercentBase", CATEGORY_MOBS, 15, 0, 100,
					"When orcs or bandits have their & percent max health reduced to or below this number, they will flee.");

			banditAndOrcFleeHealthPercentRange = config.getInt("banditAndOrcFleeHealthPercentRange", CATEGORY_MOBS, 15, 1, 100,
					"This percent is randomly added to the base flee modifier.");
			
			// SPAWNING =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
			
			spawnHeight = config.getInt("spawnHeight", CATEGORY_MOBS, 96, -2560, 2560,
					"***You probably don't want to touch this setting. Max spawn height of toroquest entities. Have this number be ~32 blocks higher than what the y-axis that terrain generates at. Vanilla is 63.");
			
			disableMobSpawningNearVillage = config.getInt("disableMobSpawningNearVillage", CATEGORY_SPAWNING, 104, 0, 208,
					"Disable mob spawns within X blocks from the village center. The higher the number, the further from the center of a province mobs will spawn. 208 blocks is the max distance of a province. Setting this to 208 means mobs can NOT spawn anywhere in a province. Setting this to 0 disables this feature and mobs can spawn anywhere - RIP villages!");
			
			disableZombieSpawningNearVillage = config.getInt("disableZombieSpawningNearVillage", CATEGORY_SPAWNING, 80, 0, 208,
					"Disable zombie spawns within X blocks from the village center. The higher the number, the further from the center of a province mobs will spawn. 208 blocks is the max distance of a province. Setting this to 208 means zombies can NOT spawn anywhere in a province. Setting this to 0 disables this feature and zombies can spawn anywhere.");
						
			fugitiveSpawnRate = config.getInt("fugitiveSpawnRate", CATEGORY_SPAWNING, 8, 0, 100, "every 1200 ticks/ every minute, the chance out of 100 for a random village (with a player visiting or nearby) to spawn a fugitive."
					+ " This chance increases by 1 for each other player online, up to a max of fugitiveSpawnRate. Setting to 0 will disable spawning. NOTE: there are 24000 ticks per day/night cycle. These fugitives will not despawn, although there is a max of 4 that can be in a village!");
			
			caravanSpawnRate = config.getInt("caravanSpawnRate", CATEGORY_SPAWNING, 10, 0, 100, "every 1200 ticks/ every minute, the chance out of 100 for a random player to have a caravan spawned near them."
					+ " This chance increases by 2 for each other player online, up to a max of caravanSpawnRate*2. Setting to 0 will disable spawning. NOTE: there are 24000 ticks per day/night cycle. These caravans will despawn, but will most likely die before they do. Caravans only spawn in the daytime.");

			banditSpawnRate = config.getInt("banditSpawnRate", CATEGORY_SPAWNING, 8, 0, 100, "every 1200 ticks/ every minute, the chance out of 100 for a random player to have bandits spawned near them."
					+ " This chance increases by 2 for each other player online, up to a max of banditSpawnRate*2. Setting to 0 will disable spawning. NOTE: there are 24000 ticks per day/night cycle. These bandits eventually despawn.");
			
//			adventurerSpawnRate = config.getInt("adventurerSpawnRate", CATEGORY_SPAWNING, 6, 0, 100, "every 1200 ticks/ every minute, the chance out of 100 for a random player to have adventurers spawned near them."
//					+ " This chance increases by 2 for each other player online, up to a max of adventurersSpawnRate*2. Setting to 0 will disable spawning. NOTE: there are 24000 ticks per day/night cycle. These adventurers eventually despawn.");
						
			// REPUTATION =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

			unexpensiveRepLoss = config.getInt("unexpensiveRepLoss", CATEGORY_REPUTATION, 1, 0, 3000,
					"The amount of rep you will lose for destroying un-expensive village blocks such as crops or wood/stone blocks (when your reputation is not high enough).");
			
			leashVillagerRepLoss = config.getInt("leashVillagerRepLoss", CATEGORY_REPUTATION, 5, 0, 3000,
					"The amount of rep you will lose for placing fire in a province (when your reputation is not high enough).");
			
			fireGriefRepLoss = config.getInt("fireGriefRepLoss", CATEGORY_REPUTATION, 5, 0, 3000,
					"The amount of rep you will lose for placing fire in a province (when your reputation is not high enough).");
			
			lavaGriefRepLoss = config.getInt("lavaGriefRepLoss", CATEGORY_REPUTATION, 10, 0, 3000,
					"The amount of rep you will lose for placing lava in a province (when your reputation is not high enough).");

			murderLivestockRepLoss = config.getInt("murderLivestockRepLoss", CATEGORY_REPUTATION, 10, 0, 3000,
					"The amount of rep you will lose for butchering livestock (when your reputation is not high enough).");
			
			expensiveRepLoss = config.getInt("expensiveRepLoss", CATEGORY_REPUTATION, 10, 0, 3000,
					"The amount of rep you will lose for destroying expensive village blocks such as bookshelves, doors, gold blocks, or stealing from villager chests (when your reputation is not high enough).");

			abandonQuestRepLoss = config.getInt("abandonQuestRepLoss", CATEGORY_REPUTATION, 10, 0, 3000,
					"The amount of rep you will lose for abandoning a quest.");
			
			killMobRepGain = config.getInt("killMobRepGain", CATEGORY_REPUTATION, 1, 0, 3000,
					"The amount of rep you will gain for killing mobs in a province.");
			
			donateEmeraldRepGain = config.getInt("donateEmeraldRepGain", CATEGORY_REPUTATION, 1, 1, 3000,
					"The amount of rep you will gain for donating emeralds to a village lord.");
			
			donateBanditMaskRepGain = config.getInt("donateBanditMaskRepGain", CATEGORY_REPUTATION, 4, 0, 3000,
					"The amount of rep you will gain for donating bandit masks to a village lord.");
			
			recruitGuardRepGain = config.getInt("recruitGuardRepGain", CATEGORY_REPUTATION, 2, 0, 3000,
					"The amount of rep you will gain for recruiting guards.");
			
			returnFugitiveRepGain = config.getInt("returnFugitiveRepGain", CATEGORY_REPUTATION, 8, 0, 3000,
					"The amount of rep you will gain for capturing fugitives with a lead and returning them to guards.");
			
			escortCaravanRepGain = config.getInt("escortCaravanRepGain", CATEGORY_REPUTATION, 12, 0, 3000,
					"The amount of rep you will gain for escorting caravans to a province. This number is increased by 25% per mule escorted.");
			
			donateArtifactRepGain = config.getInt("donateArtifactRepGain", CATEGORY_REPUTATION, 25, 0, 3000,
					"The amount of rep you will gain for donating lost artifacts to a village lord.");
			
			donateTrophyRepGain = config.getInt("donateTrophyRepGain", CATEGORY_REPUTATION, 25, 0, 3000,
					"The amount of rep you will gain for donating legendary trophies to a village lord.");
			
			loseReputationForCropGrief = config.getBoolean("loseReputationForCropGrief", CATEGORY_REPUTATION, true,
					"If set to true and a player's reputation is below 50, they will lose reputation for griefing crops.");
			
			loseReputationForAnimalGrief = config.getBoolean("loseReputationForAnimalGrief", CATEGORY_REPUTATION, true,
					"If set to true and a player's reputation is below 100, they will lose reputation for butchering animals.");
			
			loseReputationForBlockGrief = config.getBoolean("loseReputationForBlockGrief", CATEGORY_REPUTATION, true,
					"If set to true and a player's reputation is below 250, they will lose reputation for griefing villages.");
			
			// BOSSES =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

			bossHealthMultiplier = config.getFloat("bossHealthMultiplier", CATEGORY_BOSSES, 1, 0.01f, 100f, "ToroQuest boss health multipler");
			
			bossAttackDamageMultiplier = config.getFloat("bossAttackDamageMultiplier", CATEGORY_BOSSES, 1, 0.01f, 100f, "ToroQuest boss damage multipler");

			titanBoss = config.getBoolean("titanBoss", CATEGORY_BOSSES, true,
					"Enable to allow Grave Titan boss quest to generate.");
			
			banditBoss = config.getBoolean("banditBoss", CATEGORY_BOSSES, true,
					"Enable to allow  Bandit boss quest to generate.");
			
			pigBoss = config.getBoolean("pigBoss", CATEGORY_BOSSES, true,
					"Enable to allow Zombie Pigman boss quest to generate.");
			
			golemBoss = config.getBoolean("golemBoss", CATEGORY_BOSSES, true,
					"Enable to allow Steam Golem boss quest to generate.");
			
			skeletonBoss = config.getBoolean("skeletonBoss", CATEGORY_BOSSES, true,
					"Enable to allow Wither Skeleton boss quest to generate.");
			
//			blazeBoss = config.getBoolean("blazeBoss", CATEGORY_QUESTS, true,
//					"Enable to allow Blaze boss quest to generate.");
			
			spiderBoss = config.getBoolean("spiderBoss", CATEGORY_BOSSES, true,
					"Enable to allow Spider Boss boss quest to generate.");
			
			mageBoss = config.getBoolean("mageBoss", CATEGORY_BOSSES, true,
					"Enable to allow Mage boss quest to generate.");
			
			enderBoss = config.getBoolean("enderBoss", CATEGORY_BOSSES, true,
					"Enable to allow Ender boss quest to generate.");
			
			// OTHER =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
			
			recruitBandits = config.getBoolean("recruitBandits", CATEGORY, true,
					"If set to true, players can use Recruitment Papers on bribed (with emeralds) Bandits to recruit them as Guards.");
			
			recruitVillagers = config.getBoolean("recruitVillagers", CATEGORY, true,
					"If set to true, players can use Recruitment Papers on Villagers to recruit them as Guards.");
			
			useCrownToCreateNewProvinces = config.getBoolean("useCrownToCreateNewProvinces", CATEGORY, true,
					"If set to true, players can create new provinces using the crown on a bandit.");
			
			artifactDropRate = config.getInt("artifactDropRate", CATEGORY, 10, 0 ,2000,
					"Drop rate of lost artifacts. X out of 2000 mobs will drop an artifact on death, and X out of 8000 dirt/stone/sand blocks will drop an artifact on harvest. Set to 0 to disable.");
	
			showQuestCompletionAboveActionBar = config.getBoolean("showQuestCompletionAboveActionBar", CATEGORY, true,
					"If set to true, the Quest Complete! notification will appear above the action bar instead of in chat.");
			
			showProvinceEnterLeaveMessage = config.getBoolean("showProvinceEnterLeaveMessage", CATEGORY, true,
					"If set to true, show province enter and leave message.");
			
			sendRepLevelMessage = config.getBoolean("sendRepLevelMessage", CATEGORY, true,
					"If set to true, show the reputation level message that appears at certain reputation level thresholds (at 50, 100, 250, 500, 1000, 2000, 3000)");
			
			enderIdolTeleport = config.getBoolean("enderIdolTeleport", CATEGORY, true,
					"If set to true, the ender idol item will save you (similar to totem of undying) and teleport you on death, instead of having you respawn with all of your items and experience after a death. Set to true if you have other inventory death mods such as corpse complex, as there may be inventory dupe interactions.");
			
//			useOreDicForMineQuest = config.getBoolean("useOreDicForMineQuest", CATEGORY, true,
//					"If set to true, uses oreDict to look up stone for the mining quest. Otherwise, it just checks if the material is rock. Set this to false as a final resort if the mining quest does not work correctly with your modpack.");
			
			anyAnimalForBreedQuest = config.getBoolean("anyAnimalForBreedQuest", CATEGORY, false,
					"Set to true if you want to have all animals count for the breed quest. Set to true of you have mods installed that include many extra animals, such as Animania.");
			
			disableTreeChoppingQuest = config.getBoolean("disableTreeChoppingQuest", CATEGORY, false,
					"Set to true if you have DynamicTrees installed and have the option for felling an entire tree (removes tree chopping quest).");
			
			cartographerMapTrade = config.getBoolean("cartographerMapTrade", CATEGORY, true,
					"Set true to torovillager enable map trade.");
			
			tradeList = config.getStringList("tradeList", CATEGORY_TRADES,
					
		    new String[]
		    {
		    	// FARMER =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
		    	// 0 roots

		    	"minecraft:emerald,1,x,minecraft:beetroot_seeds,24,x,x,farmer,0",
		    	"minecraft:beetroot_seeds,24,x,minecraft:emerald,1,x,x,farmer,0",
		    	
		    	"minecraft:emerald,1,x,minecraft:beetroot,20,x,x,farmer,0",
		    	"minecraft:beetroot,20,x,minecraft:emerald,1,x,x,farmer,0",
		    	
		    	"minecraft:emerald,1,x,minecraft:carrot,20,x,50,farmer,0",
		    	"minecraft:carrot,20,x,minecraft:emerald,1,x,50,farmer,0",

		    	"minecraft:emerald,1,x,minecraft:potato,20,x,100,farmer,0",
		    	"minecraft:potato,20,x,minecraft:emerald,1,x,100,farmer,0",

		    	// 1 wheat
	    		
		    	"minecraft:emerald,1,x,minecraft:wheat_seeds,24,x,x,farmer,1",
		    	"minecraft:wheat_seeds,24,x,minecraft:emerald,1,x,x,farmer,1",
		    	
		    	"minecraft:emerald,1,x,minecraft:wheat,16,x,x,farmer,1",
		    	"minecraft:wheat,16,x,minecraft:emerald,1,x,x,farmer,1",
		    	
		    	// 2 sugarcane
		    	"minecraft:emerald,1,x,minecraft:reeds,24,x,x,farmer,2",
		    	"minecraft:reeds,24,x,minecraft:emerald,1,x,x,farmer,2",
		    			    			    			    	
		    	// 3 melon/pumpkins
		    	"minecraft:emerald,1,x,minecraft:pumpkin,6,x,x,farmer,3",
		    	"minecraft:pumpkin,6,x,minecraft:emerald,1,x,x,farmer,3",
		    	
		    	"minecraft:emerald,1,x,minecraft:melon,24,x,100,farmer,3",
		    	"minecraft:melon,24,x,minecraft:emerald,1,x,100,farmer,3",
		    	
		    	// 4 apple
		    	"minecraft:emerald,1,x,minecraft:apple,6,x,x,farmer,4",
		    	"minecraft:apple,6,x,minecraft:emerald,1,x,x,farmer,4",
		    	
		    	"minecraft:sapling,12,x,minecraft:emerald,1,x,100,farmer,4",
		    	"minecraft:emerald,1,x,minecraft:emerald,12,x,100,farmer,4",
		    	
		    	// "minecraft:charcoal,14,x,minecraft:emerald,1,x,50,farmer,x",
		    	// "minecraft:dye:15,36,x,minecraft:emerald,1,x,100,farmer,x",

		    	// FISHERMAN =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
		    	
		    	"minecraft:fish,3,x,minecraft:emerald,1,x,x,fisherman,x",
		    	"minecraft:fish:1,2,x,minecraft:emerald,1,x,50,fisherman,x",
		    	"minecraft:fish:2,1,x,minecraft:emerald,1,x,100,fisherman,x",
		    	"minecraft:fish:3,1,x,minecraft:emerald,8,x,250,fisherman,x",
		    	
		    	"minecraft:emerald,8,x,minecraft:enchanted_book,1,x,50,fisherman,0,minecraft:lure~1",
		    	"minecraft:emerald,16,x,minecraft:enchanted_book,1,x,250,fisherman,0,minecraft:lure~2",
		    	"minecraft:emerald,32,x,minecraft:enchanted_book,1,x,500,fisherman,0,minecraft:lure~3",
		    	"minecraft:emerald,64,x,minecraft:enchanted_book,1,x,1000,fisherman,0,minecraft:lure~4",
		    	"minecraft:emerald,128,x,minecraft:enchanted_book,1,x,3000,fisherman,0,minecraft:lure~5",

		    	"minecraft:emerald,8,x,minecraft:enchanted_book,1,x,50,fisherman,1,minecraft:luck_of_the_sea~1",
		    	"minecraft:emerald,16,x,minecraft:enchanted_book,1,x,250,fisherman,1,minecraft:luck_of_the_sea~2",
		    	"minecraft:emerald,32,x,minecraft:enchanted_book,1,x,500,fisherman,1,minecraft:luck_of_the_sea~3",
		    	"minecraft:emerald,64,x,minecraft:enchanted_book,1,x,1000,fisherman,1,minecraft:luck_of_the_sea~4",
		    	"minecraft:emerald,128,x,minecraft:enchanted_book,1,x,3000,fisherman,1,minecraft:luck_of_the_sea~5",
		    	
		    	// FLETCHER =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
		    	
		    	"minecraft:emerald,1,x,minecraft:bow,1,x,x,fletcher,x",
		    	"minecraft:emerald,1,x,minecraft:arrow,20,x,x,fletcher,x",
		    	
		    	"minecraft:string,9,x,minecraft:emerald,1,x,50,fletcher,x",
		    	"minecraft:feather,32,x,minecraft:emerald,1,x,50,fletcher,x",
		    	"minecraft:flint,8,x,minecraft:emerald,1,x,50,fletcher,x",
		    	
		    	"minecraft:log,16,x,minecraft:emerald,1,x,100,fletcher,x",
		    	"minecraft:log:1,16,x,minecraft:emerald,1,x,100,fletcher,x",
		    	"minecraft:log:2,16,x,minecraft:emerald,1,x,100,fletcher,x",
		    	"minecraft:log:3,16,x,minecraft:emerald,1,x,100,fletcher,x",
		    	"minecraft:log2,16,x,minecraft:emerald,1,x,100,fletcher,x",
		    	"minecraft:log2:1,16,x,minecraft:emerald,1,x,100,fletcher,x",
		    	
		    	"minecraft:emerald,8,x,minecraft:enchanted_book,1,x,50,fletcher,1,minecraft:power~1",
		    	"minecraft:emerald,16,x,minecraft:enchanted_book,1,x,250,fletcher,1,minecraft:power~2",
		    	"minecraft:emerald,24,x,minecraft:enchanted_book,1,x,500,fletcher,1,minecraft:power~3",
		    	"minecraft:emerald,40,x,minecraft:enchanted_book,1,x,1000,fletcher,1,minecraft:power~4",
		    	"minecraft:emerald,64,x,minecraft:enchanted_book,1,x,2000,fletcher,1,minecraft:power~5",
		    	"minecraft:emerald,256,x,minecraft:enchanted_book,1,x,3000,fletcher,1,minecraft:power~6",

		    	"minecraft:emerald,8,x,minecraft:enchanted_book,1,x,50,fletcher,2,minecraft:punch~1",
		    	"minecraft:emerald,16,x,minecraft:enchanted_book,1,x,250,fletcher,2,minecraft:punch~2",
		    	"minecraft:emerald,32,x,minecraft:enchanted_book,1,x,500,fletcher,2,minecraft:punch~3",
		    	"minecraft:emerald,64,x,minecraft:enchanted_book,1,x,1000,fletcher,2,minecraft:punch~4",
		    	"minecraft:emerald,96,x,minecraft:enchanted_book,1,x,2000,fletcher,2,minecraft:punch~5",
		    	"minecraft:emerald,128,x,minecraft:enchanted_book,1,x,3000,fletcher,2,minecraft:punch~2",
		    	
		    	"minecraft:emerald,64,x,minecraft:enchanted_book,1,x,1000,fletcher,3,minecraft:infinity~1",
		    	
		    	"minecraft:emerald,40,x,minecraft:enchanted_book,0,YELLOW,500,fletcher,4,minecraft:flame~1",

		    	//"minecraft:emerald,1,x,minecraft:tipped_arrow,2,x,x,farmer,x,minecraft:healing~minecraft:tipped_arrow",
		    	//"minecraft:emerald,1,x,minecraft:tipped_arrow,2,x,x,farmer,x,minecraft:tipped_arrow~minecraft:healing",
		    	// "minecraft:emerald,16,x,minecraft:enchanted_book,1,x,50,librarian,1,minecraft:smite~5~minecraft:fire_aspect~2",
		    	// "minecraft:emerald,16,x,minecraft:golden_sword,1,x,100,weapon,1,Sword of Righteousness~minecraft:smite~5~minecraft:fire_aspect~5",

		    	// LIBRARIAN =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
		    	
		    	"minecraft:paper,24,x,minecraft:emerald,1,x,x,librarian,x",
		    	
		    	"minecraft:enchanted_book,1,x,minecraft:emerald,2,x,x,librarian,x",
		    	
		    	"minecraft:emerald,1,x,minecraft:bookshelf,3,x,x,librarian,x",

		    	// CARTOGRAPHER =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
		    	
		    	"minecraft:filled_map,1,x,minecraft:emerald,2,x,x,cartographer,x",
		    	
		    	// CLERIC =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
		    	
		    	// 0 undead
		    	"minecraft:rotten_flesh,20,x,minecraft:emerald,1,x,x,cleric,0",
		    	"minecraft:gold_ingot,3,x,minecraft:emerald,1,x,100,cleric,0",
		    	"minecraft:bone,12,x,minecraft:emerald,1,x,50,cleric,0",
		    	"minecraft:emerald,1,x,minecraft:bone,12,x,50,cleric,0",

		    	"minecraft:emerald,1,x,minecraft:potion,1,x,x,cleric,0,minecraft:healing~minecraft:potion",
		    	"minecraft:emerald,1,x,minecraft:potion,1,x,x,cleric,0,minecraft:healing~minecraft:splash_potion",
		    	"minecraft:emerald,2,x,minecraft:potion,1,x,1000,cleric,0,minecraft:healing~minecraft:lingering_potion",
		    	
		    	// 1 end
		    	
		    	"minecraft:emerald,1,x,minecraft:blaze_powder,9,x,50,cleric,1",
		    	"minecraft:emerald,1,x,minecraft:ender_pearl,1,x,250,cleric,1",
		    	"minecraft:emerald,4,x,minecraft:ghast_tear,1,x,500,cleric,1",
		    	"minecraft:emerald,1,x,minecraft:dragon_breath,1,x,1000,cleric,1",
		    	
		    	"minecraft:emerald,3,x,minecraft:potion,1,x,x,cleric,1,minecraft:regeneration~minecraft:potion",
		    	"minecraft:emerald,3,x,minecraft:potion,1,x,x,cleric,1,minecraft:regeneration~minecraft:splash_potion",
		    	"minecraft:emerald,4,x,minecraft:potion,1,x,1000,cleric,1,minecraft:regeneration~minecraft:lingering_potion",

		    	// 2 overworld
		    	
		    	"minecraft:emerald,1,x,minecraft:spider_eye,10,x,x,cleric,2",
		    	"minecraft:emerald,1,x,minecraft:gunpowder,8,x,50,cleric,2",
		    	"minecraft:emerald,1,x,minecraft:rabbit_foot,1,x,250,cleric,2",
		    	"minecraft:emerald,1,x,minecraft:slime_ball,2,x,500,cleric,2",
		    	
		    	"minecraft:emerald,3,x,minecraft:potion,1,x,x,cleric,2,minecraft:swiftness~minecraft:potion",
		    	"minecraft:emerald,3,x,minecraft:potion,1,x,x,cleric,2,minecraft:swiftness~minecraft:splash_potion",
		    	"minecraft:emerald,4,x,minecraft:potion,1,x,1000,cleric,2,minecraft:swiftness~minecraft:lingering_potion",
		    	
		    	// 3 flowers
		    	
		    	"minecraft:emerald,1,x,minecraft:yellow_flower,8,x,x,cleric,3",
		    	"minecraft:emerald,1,x,minecraft:red_flower,8,x,x,cleric,3",
		    	"minecraft:emerald,1,x,minecraft:red_flower:1,8,x,x,cleric,3",
		    	"minecraft:emerald,1,x,minecraft:red_flower:2,8,x,x,cleric,3",
		    	"minecraft:emerald,1,x,minecraft:red_flower:3,8,x,x,cleric,3",
		    	"minecraft:emerald,1,x,minecraft:red_flower:4,8,x,x,cleric,3",
		    	"minecraft:emerald,1,x,minecraft:red_flower:5,8,x,x,cleric,3",
		    	"minecraft:emerald,1,x,minecraft:red_flower:6,8,x,x,cleric,3",
		    	"minecraft:emerald,1,x,minecraft:red_flower:7,8,x,x,cleric,3",
		    	"minecraft:emerald,1,x,minecraft:red_flower:8,8,x,x,cleric,3",
		    	
		    	"minecraft:emerald,1,x,minecraft:double_plant:1,3,x,x,cleric,3",
		    	"minecraft:emerald,1,x,minecraft:double_plant:4,3,x,x,cleric,3",
		    	"minecraft:emerald,1,x,minecraft:double_plant:5,3,x,x,cleric,3",
		    	
		    	"minecraft:emerald,1,x,minecraft:experience_bottle,1,x,1000,cleric,3",

		    	// 4 shrooms
		    	
		    	"minecraft:sugar,36,x,minecraft:emerald,1,x,x,cleric,4",
		    	
		    	"minecraft:brown_mushroom,10,x,minecraft:emerald,1,x,50,cleric,4",
		    	"minecraft:emerald,1,x,minecraft:brown_mushroom,10,x,50,cleric,4",
		    	
		    	"minecraft:red_mushroom,10,x,minecraft:emerald,1,x,100,cleric,4",
		    	"minecraft:emerald,1,x,minecraft:red_mushroom,10,x,100,cleric,4",
		    	
		    	"minecraft:nether_wart,20,x,minecraft:emerald,1,x,250,cleric,4",
		    	"minecraft:emerald,1,x,minecraft:nether_wart,20,x,250,cleric,4",
		    			    	
		    	"minecraft:emerald,1,x,minecraft:potion,1,x,x,cleric,4,minecraft:poison~minecraft:splash_potion",
		    	"minecraft:emerald,2,x,minecraft:potion,1,x,1000,cleric,4,minecraft:poison~minecraft:lingering_potion",
		    	
		    	"minecraft:emerald,2,x,minecraft:potion,1,x,x,cleric,4,minecraft:harming~minecraft:splash_potion",
		    	"minecraft:emerald,3,x,minecraft:potion,1,x,1000,cleric,4,minecraft:harming~minecraft:lingering_potion",
		    	
		    	// x
		    	
		    	"minecraft:glass_bottle,48,x,minecraft:emerald,1,x,50,cleric,x",
		    	"minecraft:redstone,5,x,minecraft:emerald,1,x,250,cleric,x",
		    	"minecraft:glowstone_dust,16,x,minecraft:emerald,1,x,500,cleric,x",
		    	
		    	// ARMOR =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
		    	
		    	"minecraft:emerald,2,x,minecraft:iron_boots,1,x,x,armor,x",
		    	"minecraft:emerald,3,x,minecraft:iron_leggings,1,x,x,armor,x",
		    	"minecraft:emerald,2,x,minecraft:iron_helmet,1,x,x,armor,x",
		    	"minecraft:emerald,4,x,minecraft:iron_chestplate,1,x,x,armor,x",
		    			    	
		    	"minecraft:emerald,1,x,minecraft:chainmail_boots,1,x,100,armor,x",
		    	"minecraft:emerald,2,x,minecraft:chainmail_leggings,1,x,100,armor,x",
		    	"minecraft:emerald,1,x,minecraft:chainmail_helmet,1,x,100,armor,x",
		    	"minecraft:emerald,3,x,minecraft:chainmail_chestplate,1,x,100,armor,x",
		    	
		    	"minecraft:iron_ingot,2,x,minecraft:emerald,1,x,x,armor,x",
		    	
		    	"minecraft:emerald,16,x,minecraft:enchanted_book,0,BLUE,x,armor,x,minecraft:aqua_affinity~1",
		    	
		    	"minecraft:emerald,8,x,minecraft:enchanted_book,1,BLUE,50,armor,x,minecraft:respiration~1",
		    	"minecraft:emerald,16,x,minecraft:enchanted_book,1,BLUE,250,armor,x,minecraft:respiration~2",
		    	"minecraft:emerald,32,x,minecraft:enchanted_book,1,BLUE,500,armor,x,minecraft:respiration~3",
		    	"minecraft:emerald,128,x,minecraft:enchanted_book,1,BLUE,3000,armor,x,minecraft:respiration~4",
		    	
		    	"minecraft:emerald,12,x,minecraft:enchanted_book,2,BLUE,50,armor,x,minecraft:frost_walker~1",
		    	"minecraft:emerald,24,x,minecraft:enchanted_book,2,BLUE,250,armor,x,minecraft:frost_walker~2",
		    	"minecraft:emerald,64,x,minecraft:enchanted_book,2,BLUE,3000,armor,x,minecraft:frost_walker~3",

		    	"minecraft:emerald,8,x,minecraft:enchanted_book,3,BLUE,50,armor,x,minecraft:depth_strider~1",
		    	"minecraft:emerald,16,x,minecraft:enchanted_book,3,BLUE,250,armor,x,minecraft:depth_strider~2",
		    	"minecraft:emerald,32,x,minecraft:enchanted_book,3,BLUE,500,armor,x,minecraft:depth_strider~3",
		    	"minecraft:emerald,128,x,minecraft:enchanted_book,3,BLUE,3000,armor,x,minecraft:depth_strider~4",

		    	"minecraft:emerald,24,x,minecraft:enchanted_book,1,RED,500,armor,x,minecraft:thorns~1",
		    	"minecraft:emerald,48,x,minecraft:enchanted_book,1,RED,1000,armor,x,minecraft:thorns~2",
		    	"minecraft:emerald,96,x,minecraft:enchanted_book,1,RED,2000,armor,x,minecraft:thorns~3",
		    	"minecraft:emerald,192,x,minecraft:enchanted_book,1,RED,3000,armor,x,minecraft:thorns~4",
		    	
		    	"minecraft:emerald,14,x,minecraft:enchanted_book,1,YELLOW,250,armor,1,minecraft:fire_protection~1",
		    	"minecraft:emerald,28,x,minecraft:enchanted_book,1,YELLOW,500,armor,1,minecraft:fire_protection~2",
		    	"minecraft:emerald,42,x,minecraft:enchanted_book,1,YELLOW,1000,armor,1,minecraft:fire_protection~3",
		    	"minecraft:emerald,56,x,minecraft:enchanted_book,1,YELLOW,2000,armor,1,minecraft:fire_protection~4",
		    	"minecraft:emerald,112,x,minecraft:enchanted_book,1,YELLOW,3000,armor,1,minecraft:fire_protection~5",
		    	
		    	"minecraft:emerald,8,x,minecraft:enchanted_book,2,YELLOW,250,armor,1,minecraft:blast_protection~1",
		    	"minecraft:emerald,16,x,minecraft:enchanted_book,2,YELLOW,500,armor,1,minecraft:blast_protection~2",
		    	"minecraft:emerald,24,x,minecraft:enchanted_book,2,YELLOW,1000,armor,1,minecraft:blast_protection~3",
		    	"minecraft:emerald,32,x,minecraft:enchanted_book,2,YELLOW,2000,armor,1,minecraft:blast_protection~4",
		    	
		    	"minecraft:emerald,128,x,minecraft:enchanted_book,2,YELLOW,3000,armor,1,minecraft:blast_protection~5",
//		    	water - resp - frost walk
//		    	sun - blast / fire
//		    	mount - prot - unbrea
//		    	thorn - thorn
//		    	leaf
//		    	swamp/skull
		    	
		    	"minecraft:emerald,24,x,minecraft:enchanted_book,1,BROWN,250,armor,1,minecraft:protection~1",
		    	"minecraft:emerald,48,x,minecraft:enchanted_book,1,BROWN,500,armor,1,minecraft:protection~2",
		    	"minecraft:emerald,96,x,minecraft:enchanted_book,1,BROWN,1000,armor,1,minecraft:protection~3",
		    	"minecraft:emerald,192,x,minecraft:enchanted_book,1,BROWN,2000,armor,1,minecraft:protection~4",
		    	"minecraft:emerald,256,x,minecraft:enchanted_book,1,BROWN,3000,armor,1,minecraft:protection~5",
		    	
		    	// WEAPON =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
		    	
		    	"minecraft:emerald,1,x,minecraft:iron_sword,1,x,x,weapon,x",
		    	
		    	"minecraft:iron_ingot,2,x,minecraft:emerald,1,x,x,armor,x",
		    	
		    	"minecraft:emerald,1,x,minecraft:shield,1,x,x,armor,x",
		    	
		    	"minecraft:emerald,8,x,minecraft:enchanted_book,1,YELLOW,250,weapon,1,minecraft:smite~1",
		    	"minecraft:emerald,16,x,minecraft:enchanted_book,1,YELLOW,500,weapon,1,minecraft:smite~2",
		    	"minecraft:emerald,24,x,minecraft:enchanted_book,1,YELLOW,1000,weapon,1,minecraft:smite~3",
		    	"minecraft:emerald,48,x,minecraft:enchanted_book,1,YELLOW,2000,weapon,1,minecraft:smite~4",
		    	"minecraft:emerald,96,x,minecraft:enchanted_book,1,YELLOW,2000,weapon,1,minecraft:smite~5",
		    	
		    	"minecraft:emerald,24,x,minecraft:enchanted_book,2,YELLOW,250,weapon,1,minecraft:fire_aspect~1",
		    	"minecraft:emerald,48,x,minecraft:enchanted_book,2,YELLOW,1000,weapon,1,minecraft:fire_aspect~2",
		    	"minecraft:emerald,128,x,minecraft:enchanted_book,2,YELLOW,3000,weapon,1,minecraft:fire_aspect~3",

		    	"minecraft:emerald,8,x,minecraft:enchanted_book,3,YELLOW,50,weapon,1,minecraft:smite~1",
		    	"minecraft:emerald,16,x,minecraft:enchanted_book,3,YELLOW,250,weapon,1,minecraft:smite~2",
		    	"minecraft:emerald,24,x,minecraft:enchanted_book,3,YELLOW,500,weapon,1,minecraft:smite~3",
		    	"minecraft:emerald,36,x,minecraft:enchanted_book,3,YELLOW,1000,weapon,1,minecraft:smite~4",
		    	"minecraft:emerald,48,x,minecraft:enchanted_book,3,YELLOW,2000,weapon,1,minecraft:smite~5",
		    	"minecraft:emerald,128,x,minecraft:enchanted_book,3,YELLOW,3000,weapon,1,minecraft:smite~6",

		    	"minecraft:emerald,12,x,minecraft:enchanted_book,1,BROWN,250,weapon,1,minecraft:unbreaking~1",
		    	"minecraft:emerald,24,x,minecraft:enchanted_book,1,BROWN,500,weapon,1,minecraft:unbreaking~2",
		    	"minecraft:emerald,48,x,minecraft:enchanted_book,1,BROWN,1000,weapon,1,minecraft:unbreaking~3",
		    	"minecraft:emerald,128,x,minecraft:enchanted_book,1,BROWN,3000,weapon,1,minecraft:unbreaking~4",
		    	
		    	// TOOL =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
		    	// 0 ores
		    	"minecraft:iron_pickaxe,1,x,minecraft:emerald,1,x,x,tool,0",
		    	
		    	"minecraft:emerald,1,x,minecraft:coal,14,x,x,tool,0",
		    	"minecraft:emerald,1,x,minecraft:iron_ingot,2,x,50,tool,0",
		    	"minecraft:emerald,1,x,minecraft:gold_ingot,3,x,100,tool,0",
		    	"minecraft:emerald,1,x,minecraft:redstone,5,x,250,tool,0",
		    	"minecraft:emerald,1,x,minecraft:dye:4,3,x,500,tool,0",
		    	"minecraft:emerald,10,x,minecraft:diamond,1,x,1000,tool,0",
		    	
		    	// MASON =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

		    	"minecraft:iron_pickaxe,1,x,minecraft:emerald,1,x,x,tool,1",		    	
		    	
		    	"minecraft:emerald,1,x,minecraft:cobblestone,64,x,x,tool,1",
		    	
		    	"minecraft:emerald,1,x,minecraft:stone:1,64,x,100,tool,1",
		    	"minecraft:emerald,1,x,minecraft:stone:3,64,x,100,tool,1",
		    	"minecraft:emerald,1,x,minecraft:stone:5,64,x,100,tool,1",

		    	"minecraft:emerald,1,x,minecraft:obsidian,12,x,500,tool,1",
		    	// "minecraft:obsidian,12,x,minecraft:emerald,1,x,500,tool,1",
		    	
		    	"minecraft:emerald,1,x,minecraft:quartz,16,x,500,tool,1",
		    	// "minecraft:quartz,12,x,minecraft:emerald,1,x,x,tool,1",
		    	
		    	"minecraft:emerald,1,x,minecraft:netherbrick,32,x,250,tool,1",
		    	// "minecraft:nether_bricks,12,x,minecraft:emerald,1,x,x,tool,1",
		    	
		    	"minecraft:emerald,1,x,minecraft:end_stone,16,x,250,tool,1",
		    	// "minecraft:end_stone,12,x,minecraft:emerald,1,x,x,tool,1",
		    	
	    		"minecraft:emerald,1,x,minecraft:glowstone,2,x,500,tool,1",

		    	"minecraft:emerald,1,x,minecraft:iron_pickaxe,1,x,x,tool,2",
		    	
		    	"minecraft:emerald,1,x,minecraft:stone_bricks,32,x,x,tool,2",
		    	"minecraft:stone_bricks,32,x,minecraft:emerald,1,x,x,tool,2",
		    	
		    	// 3 wood
		    	"minecraft:iron_axe,1,x,minecraft:emerald,1,x,x,tool,3",
		    	
		    	"minecraft:emerald,1,x,minecraft:log,16,x,x,tool,3",
		    	"minecraft:emerald,1,x,minecraft:log:1,16,x,x,tool,3",
		    	"minecraft:emerald,1,x,minecraft:log:2,16,x,x,tool,3",
		    	"minecraft:emerald,1,x,minecraft:log:3,16,x,x,tool,3",
		    	"minecraft:emerald,1,x,minecraft:log2,16,x,x,tool,3",
		    	"minecraft:emerald,1,x,minecraft:log2:1,16,x,x,tool,3",

		    	// 4 sands
		    	
		    	"minecraft:emerald,1,x,minecraft:iron_shovel,1,x,x,tool,4",

		    	"minecraft:emerald,1,x,minecraft:dirt,64,x,x,tool,4",
		    	"minecraft:emerald,1,x,minecraft:sand,64,x,50,tool,4",
		    	"minecraft:emerald,1,x,minecraft:gravel,32,x,100,tool,4",
		    	"minecraft:emerald,1,x,minecraft:clay,16,x,100,tool,4",
		    	"minecraft:emerald,1,x,minecraft:red_sand,64,x,500,tool,4",
		    	
		    	"minecraft:dirt,64,x,minecraft:emerald,1,x,x,tool,4",
		    	"minecraft:sand,64,x,minecraft:emerald,1,x,50,tool,4",
		    	"minecraft:gravel,32,x,minecraft:emerald,1,x,100,tool,4",
		    	"minecraft:clay,16,x,minecraft:emerald,1,x,100,tool,4",
		    	"minecraft:red_sand,64,x,minecraft:emerald,1,x,500,tool,4",


		    	// BUTCHER =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
		    	
		    	"minecraft:emerald,1,x,minecraft:cooked_beef,10,x,x,butcher,x",
		    	"minecraft:emerald,1,x,minecraft:cooked_porkchop,10,x,x,butcher,x",
		    	"minecraft:emerald,1,x,minecraft:cooked_mutton,12,x,x,butcher,x",
		    	"minecraft:emerald,1,x,minecraft:cooked_chicken,14,x,x,butcher,x",
		    	"minecraft:emerald,1,x,minecraft:cooked_rabbit,16,x,x,butcher,x",
		    	
		    	"minecraft:beef,10,x,minecraft:emerald,1,x,x,butcher,x",
		    	"minecraft:porkchop,10,x,minecraft:emerald,1,x,x,butcher,x",
		    	"minecraft:mutton,12,x,minecraft:emerald,1,x,x,butcher,x",
		    	"minecraft:chicken,14,x,minecraft:emerald,1,x,x,butcher,x",
		    	"minecraft:rabbit,16,x,minecraft:emerald,1,x,x,butcher,x",
		    	
		    	// LEATHER =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
		    	
		    	"minecraft:emerald,1,x,minecraft:leather,10,x,x,leather,x",
		    	"minecraft:leather,10,x,minecraft:emerald,1,x,x,leather,x",
		    	
		    	"minecraft:rabbit_hide,4,x,minecraft:emerald,1,x,x,leather,0",
		    	
		    	"minecraft:string,12,x,minecraft:emerald,1,x,x,leather,1",
		    	
		    	"minecraft:emerald,1,x,minecraft:leather_boots,1,x,x,leather,x",
		    	"minecraft:emerald,2,x,minecraft:leather_leggings,1,x,x,leather,x",
		    	"minecraft:emerald,1,x,minecraft:leather_helmet,1,x,x,leather,x",
		    	"minecraft:emerald,3,x,minecraft:leather_chestplate,1,x,x,leather,x",
		    	
		    	"minecraft:emerald,1,x,toroquest:toro_leather,5,x,100,leather,2",
//		    	"toroquest:toro_leather,5,x,minecraft:emerald,1,x,100,leather,2",
//		    	
//		    	"minecraft:emerald,2,x,toroquest:toro_leather_boots,1,x,100,leather,2",
//		    	"minecraft:emerald,3,x,toroquest:toro_leather_leggings,1,x,100,leather,2",
//		    	"minecraft:emerald,2,x,toroquest:toro_leather_helmet,1,x,100,leather,2",
//		    	"minecraft:emerald,4,x,toroquest:toro_leather_chestplate,1,x,100,leather,2",
		    			    	
		    	"minecraft:emerald,16,x,minecraft:saddle,1,x,250,leather,3",
		    	
		    	"minecraft:emerald,32,x,minecraft:golden_horse_armor,1,x,500,leather,3",
		    	"minecraft:emerald,64,x,minecraft:iron_horse_armor,1,x,1000,leather,3",
		    	"minecraft:emerald,96,x,minecraft:diamond_horse_armor,1,x,2000,leather,3",

		    	// SHEPHERD =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
		    	
		    	// 0
		    	"minecraft:wool,16,x,minecraft:emerald,1,x,x,shepherd,x",
		    	"minecraft:emerald,1,x,minecraft:wool,16,x,x,shepherd,x",
		    	
		    	"minecraft:wool:14,16,x,minecraft:emerald,1,RED,x,shepherd,x",
		    	"minecraft:emerald,1,x,minecraft:wool:14,16,RED,x,shepherd,x",
		    	
		    	"minecraft:wool:13,16,x,minecraft:emerald,1,GREEN,x,shepherd,x",
		    	"minecraft:emerald,1,x,minecraft:wool:13,16,GREEN,x,shepherd,x",
		    	
		    	"minecraft:wool:9,16,x,minecraft:emerald,1,BLUE,x,shepherd,x",
		    	"minecraft:emerald,1,x,minecraft:wool:9,16,BLUE,x,shepherd,x",
		    	
		    	"minecraft:wool:15,16,x,minecraft:emerald,1,BLACK,x,shepherd,x",
		    	"minecraft:emerald,1,x,minecraft:wool:15,16,BLACK,x,shepherd,x",
		    	
		    	"minecraft:wool:4,16,x,minecraft:emerald,1,YELLOW,x,shepherd,x",
		    	"minecraft:emerald,1,x,minecraft:wool:4,16,YELLOW,x,shepherd,x",
		    	
		    	"minecraft:wool:12,16,x,minecraft:emerald,1,BROWN,x,shepherd,x",
		    	"minecraft:emerald,1,x,minecraft:wool:12,16,BROWN,x,shepherd,x",		    	
		    	
		    	"minecraft:emerald,1,x,minecraft:string,12,x,x,shepherd,x",
		    	
		    	"minecraft:wheat,16,x,minecraft:emerald,1,x,x,shepherd,x",
		    	"minecraft:shears,2,x,minecraft:emerald,1,x,x,shepherd,x",

		    	"minecraft:egg,16,x,minecraft:emerald,1,x,x,shepherd,x",
		    	"minecraft:emerald,1,x,minecraft:egg,16,x,x,shepherd,x",
		    	
		    	"minecraft:emerald,1,x,minecraft:feather,28,x,x,shepherd,x",
		    	
		    	"minecraft:wheat_seeds,24,x,minecraft:emerald,1,x,x,shepherd,x",
		    	"minecraft:melon_seeds,24,x,minecraft:emerald,1,x,x,shepherd,x",
		    	"minecraft:beetroot_seeds,24,x,minecraft:emerald,1,x,x,shepherd,x",
		    	"minecraft:pumpkin_seeds,24,x,minecraft:emerald,1,x,x,shepherd,x",
		    			    	
		    	// NITWIT =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
		    	
		    	"minecraft:poisonous_potato,1,x,minecraft:emerald,1,x,x,nitwit,0",
		    	
		    	"minecraft:emerald,3,x,minecraft:emerald,1,x,x,nitwit,1",
		    	
		    	"minecraft:anvil:2,1,x,minecraft:emerald,12,x,x,nitwit,2",
		    	
		    	"minecraft:bedrock,1,x,minecraft:emerald,6,x,x,nitwit,3",
		    	
		    	"minecraft:emerald,5,x,minecraft:jukebox,1,x,x,nitwit,4",
		    	"minecraft:emerald,64,x,minecraft:record_13,1,x,x,nitwit,4",
		    	"minecraft:emerald,64,x,minecraft:record_cat,1,x,x,nitwit,4",
		    	"minecraft:emerald,64,x,minecraft:record_blocks,1,x,x,nitwit,4",
		    	"minecraft:emerald,64,x,minecraft:record_chirp,1,x,x,nitwit,4",
		    	"minecraft:emerald,64,x,minecraft:record_far,1,x,x,nitwit,4",
		    	"minecraft:emerald,64,x,minecraft:record_mall,1,x,x,nitwit,4",
		    	"minecraft:emerald,64,x,minecraft:record_mellohi,1,x,x,nitwit,4",
		    	"minecraft:emerald,64,x,minecraft:record_stal,1,x,x,nitwit,4",
		    	"minecraft:emerald,64,x,minecraft:record_strad,1,x,x,nitwit,4",
		    	"minecraft:emerald,64,x,minecraft:record_ward,1,x,x,nitwit,4",
		    	"minecraft:emerald,64,x,minecraft:record_11,1,x,x,nitwit,4",
		    	"minecraft:emerald,64,x,minecraft:record_wait,1,x,x,nitwit,4",
		    	
		    	// SHOPKEEPER =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
		    	
		    	"minecraft:emerald,5,x,toroquest:recruitment_papers,1,x,0,shopkeeper,x",
		    	"minecraft:emerald,3,x,minecraft:experience_bottle,1,x,x,shopkeeper,x",
		    	"minecraft:emerald,5,x,minecraft:lead,1,x,x,shopkeeper,x",
		    	"minecraft:emerald,64,x,toroquest:city_key,1,x,500,shopkeeper,x",
		    	"toroquest:trophy_titan,1,x,toroquest:ender_idol,2,x,1000,shopkeeper,x",
		    	"toroquest:legendary_bandit_helmet,1,x,toroquest:ender_idol,2,x,1200,shopkeeper,x",
		    	"toroquest:trophy_pig,1,x,toroquest:ender_idol,2,x,1400,shopkeeper,x",
		    	"toroquest:trophy_skeleton,1,x,toroquest:ender_idol,2,x,1600,shopkeeper,x",
		    	"toroquest:trophy_spider,1,x,toroquest:ender_idol,2,x,1800,shopkeeper,x",
		    	"toroquest:trophy_archmage,1,x,toroquest:ender_idol,2,x,2000,shopkeeper,x",
		    	"toroquest:trophy_beholder,1,x,toroquest:ender_idol,2,x,2200,shopkeeper,x"
		    },
					
			"A list of trades for villagers. Follow the format to add trades (do not include spaces, it is shown like this to make it easier to understand):"
			+ "\n  modId:itemToSell  , amount , secondItemToSell, modId:itemToBuy:damageValue , amount , province , minRepRequired ,   job    , jobVarient\n"
	    	+ "\n  minecraft:emerald ,   1    ,        x        ,     minecraft:wool:14       ,   16   ,     x    ,       100      , shepherd ,      4"
			+ "\nFor ENCHANTED ITEMS, use the following format:\n\n" 
			+ "\nitemToSell,amount,secondItemToSell,itemToBuy,amount,minRepRequired,province,job,jobVarient,enchantedItemName~enchantment~power~enchantment~power\n" 
			+ "\n  modId:itemToSell , amount , secondItemToSell, modId:enchantedItem:damageValue , amount , province , minRepRequired ,     job    , jobVarient ~ enchantedItemDisplayName ~ modId:enchantment ~ power ~   modId:enchantment ~   power\n"
	    	+ "\n minecraft:emerald ,   1    ,        x        ,     minecraft:diamond_sword     ,   1    ,  YELLOW  ,      2000      ,    weapon  ,      0     ~        Sun Blade         ~  minecraft:smite  ~   3   ~  minecraft:sharpness  ~   5"
			+ "\nYou can add as many additional enchantments as you'd like. For ENCHANTED BOOKS, put minecraft:enchanted_book in place of modId:enchantedItem:damageValue, and DO NOT include enchantedItemDisplayName"
	    	+ "\nHere is a trade example for potions --> minecraft:emerald,1,x,minecraft:potion,1,x,x,cleric,0,minecraft:healing~minecraft:splash_potion"
			+ "\n\nItem to Sell:  minecraft:wheat, minecraft:stone, toroquest:recruitment_papers, ... visit https://www.minecraftinfo.com/idnamelist.htm OR https://minecraftitemids.com/ for help\n"
			+ "\nItem Amount:   1, 2, 3... 64.\n\n"
			+ "\n2nd Item:      x, minecraft:wheat, minecraft:stone, toroquest:recruitment_papers, ...\n"
			+ "\nItem to Buy:   minecraft:wheat, minecraft:stone, toroquest:recruitment_papers, ...\n"
			+ "\nItem Amount:   1, 2, 3... 128.\n\n"
			+ "\nProvinces:     x, GREEN, BROWN, RED, BLUE, YELLOW, BLACK.\n\n"
			+ "\nRep Required:  x, -50, 0, 100... 3000\n\n"
			+ "\nJobs:          x, farmer, fisherman, shepherd, fletcher, librarian, cartographer, cleric, armor, weapon, tool, butcher, leather, nitwit, shopkeeper, ..."
			+ "\nJob varients:  x, 0, 1, 2, 3, 4."
			+ "\n\nVillagers will not trade with players at or below -50 rep. Prices are not improved for the player past 3000.");

			scrollTradeItem = config.getString("scrollTradeItem", CATEGORY_TRADES, "minecraft:emerald",
					"The item you will use to trade for a shopkeeper's teleport scroll. Delete this text to disable trade.");
			
			scrollTradeAmount = config.getInt("scrollTradeAmount", CATEGORY_TRADES, 3, 0, 128,
					"The amount of scrollTradeItem a shopkeeper's teleport scroll will cost you. Set to 0 to disable this trade.");
			
			bannerTradeAmount = config.getInt("bannerTradeAmount", CATEGORY_TRADES, 2, 0, 128,
					"The amount of scrollTradeItem a shopkeeper's banner will cost you. Set to 0 to disable this trade.");
			
			// RAIDERS =-=-=-=-=-=-=-=-=-=-=
			
			zombieAttackVillageChance = config.getInt("zombieAttackVillageChance", CATEGORY_RAIDS, 25, 0, 100,
					"The chance (out of 100) for zombies that spawn near a province to siege it (these raiding zombies will only replace VANILLA zombies that spawn, so you may need to increase this number if you have mods that add more zombie types). Set to 0 to disable.");
			
			zombieRaiderVillagerChance = config.getInt("zombieRaiderVillagerChance", CATEGORY_RAIDS, 25, 0, 100,
					"The chance (out of 100) for normal raider zombies that spawn near a province to become a zombie villager instead. Set to 0 to disable.");

			provinceSiegeRate = config.getInt("provinceSiegeRate", CATEGORY_RAIDS, 8, 0, 100, "every 1200 ticks/ every minute, the chance out of 100 for a random village (with a player visiting or nearby) to spawn a siege."
					+ " This chance increases by 2 for each other player online, up to a max of provinceSiegeRate*2. Setting to 0 will disable spawning. NOTE: there are 24000 ticks per day/night cycle. These mobs eventually despawn.");

			raiderSiegeChance = config.getInt("raiderSiegeChance", CATEGORY_RAIDS, 40, 0, 100,
					"The chance (out of 100) to have custom raiders spawn, instead of bandits when a raid happens. These custom raiders can be configured for each House (raiderString_RED_BRIAR, raiderString_YELLOW_DAWN, raiderString_GREEN_WILD...)");

			// RED
			raiderString_RED_BRIAR = config.getStringList("raiderString_RED_BRIAR", CATEGORY_RAIDS,
				    
					new String[]
				    {
				    	"its_meow.betteranimalsplus.common.entity.EntityFeralWolf~0~1.2~2~3~x",
				    	"its_meow.betteranimalsplus.common.entity.EntityFeralWolf~500~1.2~4~9~x",
				    },
				    
					"House Briar (Red) raiders. FORMAT: entityResourceName~minReputationToRaid~raidSpeed~minAmountToSpawn~maxAmountToSpawn~timeType"
				    + "\n\n entityResourceName is the resource path to the entity -- minReputationToRaid is the minimum amount of reputation required for this entity to spawn in a raid -- raidSpeed is the movement speed of the AI task that is added to the entity so it can path find to a village, this will usually be 1.0 -- minAmountToSpawn is the minimum amount of this entity that can spawn in a raid -- maxAmountToSpawn is the maximum amount of this entity that can spawn in a raid -- timeType determines at what time can the entity spawn, 'night' = restricted to night raids only, 'day' = restricted to day raids only, 'x' = both night & day raids");
			
			for ( String s : raiderString_RED_BRIAR )
			{
				try
				{
					String[] list = s.split("~");
					Raider raider = new Raider();
					raider.entityResourceName = list[0];
					raider.minReputationRequired  = Integer.parseInt(list[1]);
					raider.raidSpeed = Double.parseDouble(list[2]);
					raider.minSpawn = Integer.parseInt(list[3]);
					raider.maxSpawn = Integer.parseInt(list[4]);
					raider.timeType = list[5];
					raiderList_RED_BRIAR.add(raider);
				}
				catch ( Exception e )
				{
					System.out.print("raiderString_RED_BRIAR config incorrect format! It follows a strict format. Error:" + e );
				}
			}
			
			// YELLOW
			raiderString_YELLOW_DAWN = config.getStringList("raiderString_YELLOW_DAWN", CATEGORY_RAIDS,
				    
					new String[]
				    {
				    	"minecraft.entity.monster.EntityHusk~0~1.0~5~13~x"
				    },
				    
					"House Dawn (Yellow) raiders. FORMAT: entityResourceName~minReputationToRaid~raidSpeed~minAmountToSpawn~maxAmountToSpawn~timeType");
			
			for ( String s : raiderString_YELLOW_DAWN )
			{
				try
				{
					String[] list = s.split("~");
					Raider raider = new Raider();
					raider.entityResourceName = list[0];
					raider.minReputationRequired  = Integer.parseInt(list[1]);
					raider.raidSpeed = Double.parseDouble(list[2]);
					raider.minSpawn = Integer.parseInt(list[3]);
					raider.maxSpawn = Integer.parseInt(list[4]);
					raider.timeType = list[5];
					raiderList_YELLOW_DAWN.add(raider);
				}
				catch ( Exception e )
				{
					System.out.print("raiderString_YELLOW_DAWN config incorrect format! It follows a strict format. Error:" + e );
				}
			}
						
			// GREEN
			raiderString_GREEN_WILD = config.getStringList("raiderString_GREEN_WILD", CATEGORY_RAIDS,
				    
					new String[]
				    {
				    	"its_meow.betteranimalsplus.common.entity.EntityFeralWolf~0~1.2~2~3~x",
				    	"its_meow.betteranimalsplus.common.entity.EntityFeralWolf~500~1.2~4~9~x",
				    },
				    
					"House Wild (Green) raiders. FORMAT: entityResourceName~minReputationToRaid~raidSpeed~minAmountToSpawn~maxAmountToSpawn~timeType");
			
			for ( String s : raiderString_GREEN_WILD )
			{
				try
				{
					String[] list = s.split("~");
					Raider raider = new Raider();
					raider.entityResourceName = list[0];
					raider.minReputationRequired  = Integer.parseInt(list[1]);
					raider.raidSpeed = Double.parseDouble(list[2]);
					raider.minSpawn = Integer.parseInt(list[3]);
					raider.maxSpawn = Integer.parseInt(list[4]);
					raider.timeType = list[5];
					raiderList_GREEN_WILD.add(raider);
				}
				catch ( Exception e )
				{
					System.out.print("raiderString_GREEN_WILD config incorrect format! It follows a strict format. Error:" + e );
				}
			}
			
			// BLUE
			raiderString_BLUE_GLACIER = config.getStringList("raiderString_BLUE_GLACIER", CATEGORY_RAIDS,
				    
					new String[]
				    {
				    	"its_meow.betteranimalsplus.common.entity.EntityFeralWolf~0~1.2~2~3~x",
				    	"its_meow.betteranimalsplus.common.entity.EntityFeralWolf~500~1.2~4~9~x",
				    },
				    
					"House Glacier (Blue) raiders. FORMAT: entityResourceName~minReputationToRaid~raidSpeed~minAmountToSpawn~maxAmountToSpawn~timeType");
			
			for ( String s : raiderString_BLUE_GLACIER )
			{
				try
				{
					String[] list = s.split("~");
					Raider raider = new Raider();
					raider.entityResourceName = list[0];
					raider.minReputationRequired  = Integer.parseInt(list[1]);
					raider.raidSpeed = Double.parseDouble(list[2]);
					raider.minSpawn = Integer.parseInt(list[3]);
					raider.maxSpawn = Integer.parseInt(list[4]);
					raider.timeType = list[5];
					raiderList_BLUE_GLACIER.add(raider);
				}
				catch ( Exception e )
				{
					System.out.print("raiderString_BLUE_GLACIER config incorrect format! It follows a strict format. Error:" + e );
				}
			}
			
			// BROWN
			raiderString_BROWN_MITHRIL = config.getStringList("raiderString_BROWN_MITHRIL", CATEGORY_RAIDS,
				    
					new String[]
				    {
				    	"its_meow.betteranimalsplus.common.entity.EntityFeralWolf~0~1.2~2~3~x",
				    	"its_meow.betteranimalsplus.common.entity.EntityFeralWolf~500~1.2~4~9~x",
				    },
				    
					"House Mithril (Brown) raiders. FORMAT: entityResourceName~minReputationToRaid~raidSpeed~minAmountToSpawn~maxAmountToSpawn~timeType");
			
			for ( String s : raiderString_BROWN_MITHRIL )
			{
				try
				{
					String[] list = s.split("~");
					Raider raider = new Raider();
					raider.entityResourceName = list[0];
					raider.minReputationRequired  = Integer.parseInt(list[1]);
					raider.raidSpeed = Double.parseDouble(list[2]);
					raider.minSpawn = Integer.parseInt(list[3]);
					raider.maxSpawn = Integer.parseInt(list[4]);
					raider.timeType = list[5];
					raiderList_BROWN_MITHRIL.add(raider);
				}
				catch ( Exception e )
				{
					System.out.print("raiderString_BROWN_MITHRIL config incorrect format! It follows a strict format. Error:" + e );
				}
			}
			
			// BLACK
			raiderString_BLACK_MOOR = config.getStringList("raiderString_MOOR_BLACK", CATEGORY_RAIDS,
				    
					new String[]
				    {
				    	"thebetweenlands.common.entity.mobs.EntitySwampHag~0~1.0~6~10~x",
				    	"minecraft.entity.monster.EntityWitch~0~1.0~4~6~x",
				    	"thebetweenlands.common.entity.mobs.EntityPeatMummy~250~1.0~4~6~x",
				    	"thebetweenlands.common.entity.mobs.EntityStalker~500~1.0~3~5~x",
				    },
				    
					"House Moor (Black) raiders. FORMAT: entityResourceName~minReputationToRaid~raidSpeed~minAmountToSpawn~maxAmountToSpawn~timeType");
			
			for ( String s : raiderString_BLACK_MOOR )
			{
				try
				{
					String[] list = s.split("~");
					Raider raider = new Raider();
					raider.entityResourceName = list[0];
					raider.minReputationRequired  = Integer.parseInt(list[1]);
					raider.raidSpeed = Double.parseDouble(list[2]);
					raider.minSpawn = Integer.parseInt(list[3]);
					raider.maxSpawn = Integer.parseInt(list[4]);
					raider.timeType = list[5];
					raiderList_BLACK_MOOR.add(raider);
				}
				catch ( Exception e )
				{
					System.out.print("raiderString_BLACK_MOOR config incorrect format! It follows a strict format. Error:" + e );
				}
			}
						
			// BANDIT =-=-=-=-=-=-=-=-=-=-=

			banditOneHandedMeleeWeapons = config.getStringList("banditOneHandedMeleeWeapons", CATEGORY_WEAPONS,
				    
					new String[]
				    {
				    	"minecraft:iron_sword",
				    	"minecraft:iron_axe",
				    	"spartanweaponry:caestus",
				    	"spartanweaponry:caestus_studded",
				    	"spartanweaponry:hammer_iron",
				    	"spartanweaponry:mace_iron",
				    	"spartanweaponry:saber_iron",
				    	"spartanweaponry:katana_iron",
				    	"spartanweaponry:rapier_iron",
				    	"spartanweaponry:warhammer_iron",
				    	"spartanweaponry:battleaxe_iron",
				    	"spartanweaponry:longsword_iron"
				    },
				    
					"One-handed melee weapons that bandits will spawn with.");
			
			banditTwoHandedMeleeWeapons = config.getStringList("banditTwoHandedMeleeWeapons", CATEGORY_WEAPONS,
				    
					new String[]
				    {
				    	"minecraft:iron_sword",
				    	"minecraft:iron_axe",
				    	"spartanweaponry:spear_iron",
				    	"spartanweaponry:longsword_iron",
				    	"spartanweaponry:battleaxe_iron",
				    	"spartanweaponry:greatsword_iron",
				    	"spartanweaponry:glaive_iron",
				    	"spartanweaponry:halberd_iron"
				    },
				    
					"Two-handed melee weapons that bandits will spawn with.");
		
			banditRangedWeapons = config.getStringList("banditRangedWeapons", CATEGORY_WEAPONS,
				    
					new String[]
				    {
				    	"minecraft:bow"
				    },
				    
					"Ranged weapons that bandits will spawn with (crossbows currently do not work).");
			
			banditShields = config.getStringList("banditShields", CATEGORY_WEAPONS,
				    
					new String[]
				    {
				    	"minecraft:shield",
				    	"spartanshields:shield_basic_wood",
				    	"spartanshields:shield_basic_stone",
				    	"spartanshields:shield_basic_iron",
				    	"spartanshields:shield_basic_gold",
				    	"spartanshields:shield_basic_obsidian"
				    },
				    
					"Shields that bandits will spawn with.");
			
			// ORC =-=-=-=-=-=-=-=-=-=-=
			
			orcOneHandedMeleeWeapons = config.getStringList("orcOneHandedMeleeWeapons", CATEGORY_WEAPONS,
				    
					new String[]
				    {
				    	"minecraft:stone_sword",
				    	"minecraft:stone_axe",
				    	"spartanweaponry:caestus",
				    	"spartanweaponry:caestus_studded",
				    	"spartanweaponry:club_wood",
				    	"spartanweaponry:club_studded",
				    	"spartanweaponry:hammer_stone",
				    	"spartanweaponry:mace_stone",
				    	"spartanweaponry:saber_stone",
				    	"spartanweaponry:katana_stone",
				    	"spartanweaponry:warhammer_stone",
				    	"spartanweaponry:battleaxe_stone",
				    	"spartanweaponry:longsword_stone"
				    },
				    
					"One-handed melee weapons that orcs will spawn with.");
			
		orcTwoHandedMeleeWeapons = config.getStringList("orcTwoHandedMeleeWeapons", CATEGORY_WEAPONS,
				    
					new String[]
				    {
			    		"minecraft:stone_sword",
				    	"minecraft:stone_axe",
				    	"spartanweaponry:spear_stone",
				    	"spartanweaponry:longsword_stone",
				    	"spartanweaponry:battleaxe_stone",
				    	"spartanweaponry:greatsword_stone",
				    	"spartanweaponry:glaive_stone",
				    	"spartanweaponry:halberd_stone"
				    },
				    
					"Two-handed melee weapons that orcs will spawn with.");
		
			orcRangedWeapons = config.getStringList("orcRangedWeapons", CATEGORY_WEAPONS,
				    
					new String[]
				    {
				    	"spartanweaponry:longbow_wood",
				    	"spartanweaponry:longbow_leather",
				    	"spartanweaponry:longbow_iron"
				    },
				    
					"Ranged weapons that orcs will spawn with (crossbows currently do not work).");
			
			orcShields = config.getStringList("orcShields", CATEGORY_WEAPONS,
				    
					new String[]
				    {
				    	"spartanshields:shield_tower_wood",
						"spartanshields:shield_tower_stone",
						"spartanshields:shield_tower_iron",
						"spartanshields:shield_tower_gold",
						"spartanshields:shield_tower_obsidian"
				    },
				    
					"Shields that orcs will spawn with.");
			
			
										banditOneHandedMeleeWeaponsPowerful = config.getStringList("banditOneHandedMeleeWeaponsPowerful", CATEGORY_WEAPONS,
											    
												new String[]
											    {
											    	"minecraft:iron_sword",
											    	"minecraft:iron_axe",
											    	"spartanweaponry:caestus",
											    	"spartanweaponry:caestus_studded",
											    	"spartanweaponry:hammer_iron",
											    	"spartanweaponry:mace_iron",
											    	"spartanweaponry:saber_iron",
											    	"spartanweaponry:katana_iron",
											    	"spartanweaponry:rapier_iron",
											    	"spartanweaponry:warhammer_iron",
											    	"spartanweaponry:battleaxe_iron",
											    	"spartanweaponry:longsword_iron"
											    },
											    
												"Powerful One-handed melee weapons that bandits will spawn with.");
										
										banditTwoHandedMeleeWeaponsPowerful = config.getStringList("banditTwoHandedMeleeWeaponsPowerful", CATEGORY_WEAPONS,
											    
												new String[]
											    {
											    	"minecraft:iron_sword",
											    	"minecraft:iron_axe",
											    	"spartanweaponry:spear_iron",
											    	"spartanweaponry:longsword_iron",
											    	"spartanweaponry:battleaxe_iron",
											    	"spartanweaponry:greatsword_iron",
											    	"spartanweaponry:glaive_iron",
											    	"spartanweaponry:halberd_iron"
											    },
											    
												"Powerful Two-handed melee weapons that bandits will spawn with.");
									
										banditRangedWeaponsPowerful = config.getStringList("banditRangedWeaponsPowerful", CATEGORY_WEAPONS,
											    
												new String[]
											    {
											    	"minecraft:bow"
											    },
											    
												"Powerful Ranged weapons that bandits will spawn with (crossbows currently do not work).");
										
										banditShieldsPowerful = config.getStringList("banditShieldsPowerful", CATEGORY_WEAPONS,
											    
												new String[]
											    {
											    	"minecraft:shield",
											    	"spartanshields:shield_basic_wood",
											    	"spartanshields:shield_basic_stone",
											    	"spartanshields:shield_basic_iron",
											    	"spartanshields:shield_basic_gold",
											    	"spartanshields:shield_basic_obsidian"
											    },
											    
												"Powerful Shields that bandits will spawn with.");
										
										// ORC =-=-=-=-=-=-=-=-=-=-=
										
										orcOneHandedMeleeWeaponsPowerful = config.getStringList("orcOneHandedMeleeWeaponsPowerful", CATEGORY_WEAPONS,
											    
												new String[]
											    {
											    	"minecraft:stone_sword",
											    	"minecraft:stone_axe",
											    	"spartanweaponry:caestus",
											    	"spartanweaponry:caestus_studded",
											    	"spartanweaponry:club_wood",
											    	"spartanweaponry:club_studded",
											    	"spartanweaponry:hammer_stone",
											    	"spartanweaponry:mace_stone",
											    	"spartanweaponry:saber_stone",
											    	"spartanweaponry:katana_stone",
											    	"spartanweaponry:warhammer_stone",
											    	"spartanweaponry:battleaxe_stone",
											    	"spartanweaponry:longsword_stone"
											    },
											    
												"Powerful One-handed melee weapons that orcs will spawn with.");
										
										orcTwoHandedMeleeWeaponsPowerful = config.getStringList("orcTwoHandedMeleeWeaponsPowerful", CATEGORY_WEAPONS,
											    
												new String[]
											    {
										    		"minecraft:stone_sword",
											    	"minecraft:stone_axe",
											    	"spartanweaponry:spear_stone",
											    	"spartanweaponry:longsword_stone",
											    	"spartanweaponry:battleaxe_stone",
											    	"spartanweaponry:greatsword_stone",
											    	"spartanweaponry:glaive_stone",
											    	"spartanweaponry:halberd_stone"
											    },
											    
												"Powerful Two-handed melee weapons that orcs will spawn with.");
									
										orcRangedWeaponsPowerful = config.getStringList("orcRangedWeaponsPowerful", CATEGORY_WEAPONS,
											    
												new String[]
											    {
											    	"spartanweaponry:longbow_wood",
											    	"spartanweaponry:longbow_leather",
											    	"spartanweaponry:longbow_iron"
											    },
											    
												"Powerful Ranged weapons that orcs will spawn with (crossbows currently do not work).");
										
										orcShieldsPowerful = config.getStringList("orcShieldsPowerful", CATEGORY_WEAPONS,
											    
												new String[]
											    {
											    	"spartanshields:shield_tower_wood",
													"spartanshields:shield_tower_stone",
													"spartanshields:shield_tower_iron",
													"spartanshields:shield_tower_gold",
													"spartanshields:shield_tower_obsidian"
											    },
											    
												"Powerful Shields that orcs will spawn with.");
			
			enchantFirstBanditAndOrcMeleeWeapon = config.getStringList("enchantFirstBanditAndOrcMeleeWeapon", CATEGORY_WEAPONS,
				    
					new String[]
				    {
				    	"minecraft:sharpness,2",
				    	"minecraft:sharpness,4",
				    	"minecraft:smite,4",
				    	"minecraft:bane_of_arthropods,4",
				    	"minecraft:knockback,4",
				    	"minecraft:looting,2",
				    	"minecraft:sweeping,3",
				    },
				    
					"First enchant that bandits will spawn with. Format is - modid:enchant,level (add duplicate of the same enchant to increase its chance)");
			
			enchantSecondBanditAndOrcMeleeWeapon = config.getStringList("enchantSecondBanditAndOrcMeleeWeapon", CATEGORY_WEAPONS,
				    
					new String[]
				    {
				    	"minecraft:unbreaking,4",
				    	"minecraft:unbreaking,4",
				    	"minecraft:unbreaking,4",
				    	"minecraft:fire_aspect,2",
				    },
				    
					"Second enchant that bandits will spawn with. Format is - modid:enchant,level (add duplicate of the same enchant to increase its chance)");
			
			enchantFirstBanditAndOrcRanged = config.getStringList("enchantFirstBanditAndOrcRanged", CATEGORY_WEAPONS,
				    
					new String[]
				    {
				    	"minecraft:power,2",
				    	"minecraft:power,5",
				    	"minecraft:punch,5",
				    	"minecraft:flame,1",
				    	"minecraft:flame,1",
				    },
				    
					"First enchant that bandits will spawn with. Format is - modid:enchant,level (add duplicate of the same enchant to increase its chance)");
			
			enchantSecondBanditAndOrcRanged = config.getStringList("enchantSecondBanditAndOrcRanged", CATEGORY_WEAPONS,
				    
					new String[]
				    {
				    	"minecraft:unbreaking,4",
				    	"minecraft:unbreaking,4",
				    	"minecraft:unbreaking,4",
				    	"minecraft:infinity,1",
				    },
				    
					"Second enchant that bandits will spawn with. Format is - modid:enchant,level (add duplicate of the same enchant to increase its chance)");
			
			enchantFirstBanditAndOrcShield = config.getStringList("enchantFirstBanditAndOrcShield", CATEGORY_WEAPONS,
				    
					new String[]
				    {
					    "minecraft:unbreaking,4",
				    },
				    
					"First enchant that bandits will spawn with. Format is - modid:enchant,level (add duplicate of the same enchant to increase its chance)");
			
			mobsList = config.getStringList("mobsList", CATEGORY_MOBS,
				    
					new String[]
				    {
				    	"§lUndead Rising§r~EntityZombie~Zombie~10~20~0.6~x~0~Countless undead have risen from fallen villagers after the recent bandit attacks. @p, cut down any walking corpses you see and lay their souls to rest.",
				    	"§lBand of Bandits§r~EntitySentry~Bandit~6~12~1.0~x~250~The bandit attacks have getting worse, @p. Cut them down.",
				    	"§6§lDragon Sighting§r~EntityDragon~Dragon~1~1~32.0~x~1000~My scouts have reported a seeing a dragon not too far from here! Go, slay it at once! Before it catches scent of the village!"
				    },
				    
					"Custom configuration for the Kill Mobs quest:\n questTitle~mobToKill~mobDisplayName~minimumNumberOfKills~maximimumNumberOfKills~EmeraldsPerKill~ProvinceAllowed~minReputationRequired~lordDialogue \n ProvinceAllowed: x, RED, YELLOW, BLUE, BROWN, BLACK --- example:   Great Hunt~EntityBear~Bear~1~1~32.0~BROWN,GREEN~50~Hunt a bear!\nProvinceAllowed can include multiple provinces (unlike the villager trade format)");

			donateList = config.getStringList("donateList", CATEGORY_REPUTATION,
				    
					new String[]
				    {
				    	"betteranimalsplus:wolfhead~15",
				    	"betteranimalsplus:wolfhead_1~15",
				    	"betteranimalsplus:wolfhead_2~15",
				    	"betteranimalsplus:wolfhead_3~15",
				    	"betteranimalsplus:wolfhead_4~15",
				    	"betteranimalsplus:wolfhead_5~15",
				    	"betteranimalsplus:wolfhead_6~15",
				    	"betteranimalsplus:reindeerhead~15",
				    	"betteranimalsplus:reindeerhead_1~15",
				    	"betteranimalsplus:reindeerhead_2~15",
				    	"betteranimalsplus:reindeerhead_3~15",
				    	"betteranimalsplus:reindeerhead_4~15",
				    	"betteranimalsplus:moosehead~15",
				    	"betteranimalsplus:moosehead_1~15",
				    	"betteranimalsplus:moosehead_2~15",
				    	"betteranimalsplus:moosehead_3~15",
				    	"betteranimalsplus:moosehead_4~15",
				    	"betteranimalsplus:hirschgeistskull~25",
				    	"betteranimalsplus:hirschgeistskull_1~25",
				    	"betteranimalsplus:foxhead~10",
				    	"betteranimalsplus:foxhead_1~10",
				    	"betteranimalsplus:foxhead_2~10",
				    	"betteranimalsplus:foxhead_3~10",
				    	"betteranimalsplus:foxhead_4~10",
				    	"betteranimalsplus:deerhead~10",
				    	"betteranimalsplus:deerhead_1~10",
				    	"betteranimalsplus:deerhead_2~10",
				    	"betteranimalsplus:deerhead_3~10",
				    	"betteranimalsplus:deerhead_4~10",
				    	"betteranimalsplus:boarhead~10",
				    	"betteranimalsplus:boarhead_1~10",
				    	"betteranimalsplus:boarhead_2~10",
				    	"betteranimalsplus:boarhead_3~10",
				    	"betteranimalsplus:boarhead_4~10",
				    	"betteranimalsplus:coyotehead~10",
				    	"betteranimalsplus:coyotehead_1~10",
				    	"betteranimalsplus:bearhead~20",
				    	"betteranimalsplus:bearhead_1~20",
				    	"betteranimalsplus:bearhead_2~20",
				    	"betteranimalsplus:bearhead_3~20",
				    },
				    
					"Custom configuration for donating items: \nitemToDonate~repGain");
//			pitQuestList = config.getStringList("pitQuestMobList", CATEGORY_MOBS,
//				    
//					new String[]
//				    {
//				    	"EntityZombie~Zombie~12~16~0.6~X~Countless undead have risen from fallen villagers after the recent bandit attacks. @p, cut down any walking corpses you see and lay their souls to rest.",
//				    	"EntitySentry~Bandit~8~12~1.0~X~The bandit attacks have getting worse, @p. Cut them down.",
//				    },
//				    
//					"Custom configuration for the (Underground Mob Pit / Heirloom) quest:\n mobToKill|mobDisplayName|minimumNumberOfKills|maximimumNumberOfKills|EmeraldsPerKill|ProvinceAllowed|lordDialogue \n ProvinceAllowed: X, RED, YELLOW, BLUE, BROWN, BLACK");
//			
//			public static String[] pitQuestList = new String[]{};
//			public static ArrayList<pitMob> pitQuestMobs = new ArrayList<pitMob>();
			
			
//			enchantSecondBanditAndOrcShield = config.getStringList("enchantSecondBanditAndOrcShield", CATEGORY_WEAPONS,
//				    
//					new String[]
//				    {
//				    		
//				    },
//				    
//					"Second enchant that bandits will spawn with. Format is - modid:enchant,level (add duplicate of the same enchant to increase its chance)");
			
			enchantFirstBanditAndOrcChance = config.getInt("enchantFirstBanditAndOrcChance", CATEGORY_SPAWNING, 20, 0, 100, "X chance out of 100 for the first enchant to be applied to weapon/ shield/ bow.");
			
			enchantSecondBanditAndOrcChance = config.getInt("enchantSecondBanditAndOrcChance", CATEGORY_SPAWNING, 60, 0, 100, "X chance out of 100 for an additional enchant to be applied to weapon/ shield/ bow (NOTE: this second enchant only has a chance to be applied if the first enchant is applied).");
			
			guardWeapon_RED_BRIAR = config.getString("guardWeapon_RED_BRIAR", CATEGORY_WEAPONS, "minecraft:iron_sword",
					"Weapons that House Briar guards use. Cannot be blank or invalid, as weapons are built into the AI. Accepts weapons from other mods. Example - - - spartanweaponry:saber_iron");
			
			guardShield_RED_BRIAR = config.getString("guardShield_RED_BRIAR", CATEGORY_WEAPONS, "minecraft:shield",
					"Shields that House Briar guards use. Cannot be blank or invalid, as shields are built into the AI. Accepts sheilds from other mods. Example - - - spartanshields:shield_tower_wood");
			
			
			guardWeapon_GREEN_WILD = config.getString("guardWeapon_GREEN_WILD", CATEGORY_WEAPONS, "minecraft:iron_sword",
					"Weapons that House Wild guards use. Cannot be blank or invalid, as weapons are built into the AI. Accepts weapons from other mods. Example - - - spartanweaponry:saber_iron");
			
			guardShield_GREEN_WILD  = config.getString("guardShield_GREEN_WILD", CATEGORY_WEAPONS, "minecraft:shield",
					"Shields that House Wild guards use. Cannot be blank or invalid, as shields are built into the AI. Accepts sheilds from other mods. Example - - - spartanshields:shield_tower_wood");
			
			
			guardWeapon_BROWN_MITHRIL = config.getString("guardWeapon_BROWN_MITHRIL", CATEGORY_WEAPONS, "minecraft:iron_sword",
					"Weapons that House Mithril guards use. Cannot be blank or invalid, as weapons are built into the AI. Accepts weapons from other mods. Example - - - spartanweaponry:saber_iron");
			
			guardShield_BROWN_MITHRIL = config.getString("guardShield_BROWN_MITHRIL", CATEGORY_WEAPONS, "minecraft:shield",
					"Shields that House Mithril guards use. Cannot be blank or invalid, as shields are built into the AI. Accepts sheilds from other mods. Example - - - spartanshields:shield_tower_wood");
			
			
			guardWeapon_BLUE_GLACIER = config.getString("guardWeapon_BLUE_GLACIER", CATEGORY_WEAPONS, "minecraft:iron_sword",
					"Weapons that House Glacier guards use. Cannot be blank or invalid, as weapons are built into the AI. Accepts weapons from other mods. Example - - - spartanweaponry:saber_iron");
			
			guardShield_BLUE_GLACIER = config.getString("guardShield_BLUE_GLACIER", CATEGORY_WEAPONS, "minecraft:shield",
					"Shields that House Glacier guards use. Cannot be blank or invalid, as shields are built into the AI. Accepts sheilds from other mods. Example - - - spartanshields:shield_tower_wood");
			
			
			guardWeapon_YELLOW_DAWN = config.getString("guardWeapon_YELLOW_DAWN", CATEGORY_WEAPONS, "minecraft:iron_sword",
					"Weapons that House Dawn guards use. Cannot be blank or invalid, as weapons are built into the AI. Accepts weapons from other mods. Example - - - spartanweaponry:saber_iron");
			
			guardShield_YELLOW_DAWN = config.getString("guardShield_YELLOW_DAWN", CATEGORY_WEAPONS, "minecraft:shield",
					"Shields that House Dawn guards use. Cannot be blank or invalid, as shields are built into the AI. Accepts sheilds from other mods. Example - - - spartanshields:shield_tower_wood");
			
			
			guardWeapon_BLACK_MOOR = config.getString("guardWeapon_BLACK_MOOR", CATEGORY_WEAPONS, "minecraft:iron_sword",
					"Weapons that House Moor guards use. Cannot be blank or invalid, as weapons are built into the AI. Accepts weapons from other mods. Example - - - spartanweaponry:saber_iron");
			
			guardShield_BLACK_MOOR = config.getString("guardShield_BLACK_MOOR", CATEGORY_WEAPONS, "minecraft:shield",
					"Shields that House Moor guards use. Cannot be blank or invalid, as shields are built into the AI. Accepts sheilds from other mods. Example - - - spartanshields:shield_tower_wood");
			
			greenName = config.getString("greenName", CATEGORY_REPUTATION, "Wild",
					"Name for House, I need this setting for when I am unable to get the client name translations for Houses.");
			
			brownName = config.getString("brownName", CATEGORY_REPUTATION, "Mithril",
					"Name for House, I need this setting for when I am unable to get the client name translations for Houses.");
			
			redName = config.getString("redName", CATEGORY_REPUTATION, "Briar",
					"Name for House, I need this setting for when I am unable to get the client name translations for Houses.");
			
			blackName = config.getString("blackName", CATEGORY_REPUTATION, "Moor",
					"Name for House, I need this setting for when I am unable to get the client name translations for Houses.");
			
			yellowName = config.getString("yellowName", CATEGORY_REPUTATION, "Dawn",
					"Name for House, I need this setting for when I am unable to get the client name translations for Houses.");
			
			blueName = config.getString("blueName", CATEGORY_REPUTATION, "Glacier",
					"Name for House, I need this setting for when I am unable to get the client name translations for Houses.");
		
			for ( String s : tradeList )
			{
				try
				{
					String[] list = s.split(",");
					Trade trade = new Trade();
					trade.sellName = list[0];
					trade.sellAmount = list[1];
					trade.sellOptional = list[2];
					trade.buyName = list[3];
					trade.buyAmount = list[4];
					trade.province = list[5];
					trade.minimunRepRequired = list[6];
					trade.job = list[7];
					trade.varient = list[8];
					try
					{
						trade.enchantment = list[9];
					}
					catch(Exception e)
					{
						trade.enchantment = null;
					}
					trades.add(trade);
				}
				catch ( Exception e )
				{
					System.out.print("Trades config incorrect format! It follows a strict format. Error:" + e );
				}
			}
			
			for ( String s : mobsList )
			{
				try
				{
					String[] list = s.split("~");
					KillMob mob = new KillMob();
					mob.title = list[0];
					mob.mobName = list[1];
					mob.mobDisplayName = list[2];
					mob.minKills = Integer.parseInt(list[3]);
					mob.maxKills = Integer.parseInt(list[4]);
					mob.emeraldsPerKill = Double.parseDouble(list[5]);
					mob.provinceAllowed = list[6];
					mob.minRepRequired = Integer.parseInt(list[7]);
					mob.acceptChat = list[8];
					mobs.add(mob);
				}
				catch ( Exception e )
				{
					System.out.print("Mobs config incorrect format! It follows a strict format. Error:" + e + s );
				}
			}
			
			for ( String s : donateList )
			{
				try
				{
					String[] list = s.split("~");
					Donate d = new Donate();
					d.item = Item.getByNameOrId(list[0]);
					d.rep = Integer.parseInt(list[1]);
					if ( d.item != null )
					{
						donate.add(d);
					}
				}
				catch ( Exception e )
				{
					System.out.print("Donate config incorrect format! It follows a strict format. Error:" + e + s );
				}
			}
			
//			if ( !guardTargetBlacklist.isEmpty() )
//			{
//				guardTargetBlacklistLIST = guardTargetBlacklist.split(",");
//			}
//			
			config.save();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	

	@SubscribeEvent
	public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if (event.getModID().equalsIgnoreCase(ToroQuest.MODID))
		{
			loadConfiguration();
		}
	}
	
	@Nullable
	public static class Donate
	{
		public Item item = null;
		public int rep = 0;
	}
	
	@Nullable
	public static class Trade
	{
		public String sellName = "";
		public String sellAmount = "";
		public String sellOptional = "";
		public String buyName = "";
		public String buyAmount = "";
		public String minimunRepRequired = "";
		public String province = "";
		public String job = "";
		public String varient = "";
		public String enchantment = null;
	}
	
	@Nullable
	public static class KillMob
	{
		public String title = "";
		public String mobName = "";
		public String mobDisplayName = "";
		public int minKills = 0;
		public int maxKills = 0;
		public double emeraldsPerKill = 0.0;
		public String provinceAllowed = "";
		public int minRepRequired = 0;
		public String acceptChat = "";
		
	}
	
	@Nullable
	public static class Raider
	{
		public String entityResourceName = "";
		public int minReputationRequired = 0;
		public double raidSpeed = 0.0D;
		public int minSpawn = 0;
		public int maxSpawn = 0;
		public String timeType = "x";
	}
}
