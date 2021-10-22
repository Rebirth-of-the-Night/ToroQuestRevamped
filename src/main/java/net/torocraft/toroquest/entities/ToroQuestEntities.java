package net.torocraft.toroquest.entities;

public class ToroQuestEntities
{

	public static final String ENTITY_PREFIX = "toroquest_";

	public static void init()
	{
		int id = 1;
		EntityMage.init(id++);
		EntityMonolithEye.init(id++);
		EntityVillageLord.init(id++);
		EntityGuard.init(id++);
		EntityShopkeeper.init(id++);
		EntityBas.init(id++);
		EntityVampireBat.init(id++);
		EntitySentry.init(id++);
		EntityFugitive.init(id++);
		EntityToroVillager.init(id++);
		EntityPigLord.init(id++);
		EntityZombieRaider.init(id++);
		EntityZombieVillagerRaider.init(id++);
		EntitySpiderLord.init(id++);
		EntityWolfRaider.init(id++);
		EntityGraveTitan.init(id++);
		EntityBanditLord.init(id++);
		EntityCaravan.init(id++);
		EntitySmartArrow.init(id++);
		EntityOrc.init(id++);
		EntityConstruct.init(id++);
		EntityConstructQuest.init(id++);
		//EntityAdventurer.init(id++);
	}
}