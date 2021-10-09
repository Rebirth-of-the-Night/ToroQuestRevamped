package net.torocraft.toroquest.civilization.quests.util;

import java.util.HashMap;
import java.util.Map;

import net.torocraft.toroquest.civilization.quests.QuestBreed;
import net.torocraft.toroquest.civilization.quests.QuestBuild;
import net.torocraft.toroquest.civilization.quests.QuestCaptureEntity;
import net.torocraft.toroquest.civilization.quests.QuestCaptureFugitives;
import net.torocraft.toroquest.civilization.quests.QuestCourier;
import net.torocraft.toroquest.civilization.quests.QuestEnemyEncampment;
import net.torocraft.toroquest.civilization.quests.QuestEnemyGolem;
import net.torocraft.toroquest.civilization.quests.QuestEnemySpiderPit;
import net.torocraft.toroquest.civilization.quests.QuestFarm;
import net.torocraft.toroquest.civilization.quests.QuestGather;
import net.torocraft.toroquest.civilization.quests.QuestKillBossArchmage;
import net.torocraft.toroquest.civilization.quests.QuestKillBossBanditLord;
import net.torocraft.toroquest.civilization.quests.QuestKillBossBastion;
import net.torocraft.toroquest.civilization.quests.QuestKillBossGraveTitan;
import net.torocraft.toroquest.civilization.quests.QuestKillBossMonolithEye;
import net.torocraft.toroquest.civilization.quests.QuestKillBossSpiderLord;
import net.torocraft.toroquest.civilization.quests.QuestKillBossZombiePig;
import net.torocraft.toroquest.civilization.quests.QuestKillMobs;
import net.torocraft.toroquest.civilization.quests.QuestMine;
import net.torocraft.toroquest.civilization.quests.QuestRecruit;
import net.torocraft.toroquest.civilization.quests.QuestTradeWithVillagers;

public class Quests {

	private static final Map<Integer, Quest> REGISTRY = new HashMap<Integer, Quest>();

	public static void registerQuest(int id, Quest instance)
	{
		REGISTRY.put(id, instance);
	}

	public static Quest getQuestForId(Integer id)
	{
		if (id == null)
		{
			throw new NullPointerException("quest ID is null");
		}
		return REGISTRY.get(id);
	}

	// ============================================== POSSIBLE QUESTS TO GIVE ===============================================================

	public static void init()
	{

		// Go into PlayerCivilizationCapabilityImpl > generateNextQuestFor()
		int id = 0;
		QuestFarm.init(++id);
		QuestMine.init(++id);
		QuestTradeWithVillagers.init(++id);
		QuestBreed.init(++id);
		QuestKillMobs.init(++id);
		QuestRecruit.init(++id);
		QuestCaptureFugitives.init(++id);
		QuestGather.init(++id);
		QuestCaptureEntity.init(++id);
		QuestCourier.init(++id);
		QuestEnemySpiderPit.init(++id);
		QuestEnemyEncampment.init(++id);
		// LEGENDARY QUESTS
		QuestKillBossGraveTitan.init(++id);
		QuestKillBossBanditLord.init(++id);
		QuestKillBossZombiePig.init(++id);
		QuestKillBossBastion.init(++id);
		QuestKillBossArchmage.init(++id);
		QuestKillBossSpiderLord.init(++id);
		QuestKillBossMonolithEye.init(++id);
		QuestBuild.init(++id);
		QuestEnemyGolem.init(++id);
	}

}
