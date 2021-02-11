


					// reputation level for when you kill an entity or destroy a block! //


package net.torocraft.toroquest.civilization;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockClay;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockGravel;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockMagma;
import net.minecraft.block.BlockOre;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockQuartz;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockStem;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityDonkey;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityMule;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.village.Village;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.EntityPlaceEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.toroquest.EventHandlers.SyncTask;
import net.torocraft.toroquest.ToroQuest;
import net.torocraft.toroquest.civilization.player.PlayerCivilizationCapability;
import net.torocraft.toroquest.civilization.player.PlayerCivilizationCapabilityImpl;
import net.torocraft.toroquest.civilization.quests.QuestBase;
import net.torocraft.toroquest.civilization.quests.QuestBreed;
import net.torocraft.toroquest.civilization.quests.QuestFarm;
import net.torocraft.toroquest.config.ToroQuestConfiguration;
import net.torocraft.toroquest.entities.EntityCaravan;
import net.torocraft.toroquest.entities.EntityFugitive;
import net.torocraft.toroquest.entities.EntityGuard;
import net.torocraft.toroquest.entities.EntityOrc;
import net.torocraft.toroquest.entities.EntitySentry;
import net.torocraft.toroquest.entities.EntityToro;
import net.torocraft.toroquest.entities.EntityToroMob;
import net.torocraft.toroquest.entities.EntityToroNpc;
import net.torocraft.toroquest.entities.EntityToroVillager;
import net.torocraft.toroquest.entities.EntityVillageLord;
import net.torocraft.toroquest.entities.EntityWolfRaider;
import net.torocraft.toroquest.entities.EntityZombieRaider;
import net.torocraft.toroquest.entities.EntityZombieVillagerRaider;
import net.torocraft.toroquest.entities.ai.EntityAIDespawn;
import net.torocraft.toroquest.util.TaskRunner;


public class CivilizationHandlers
{
	//public final static int player.world.getHeight()/2 = 128;
	protected static Random rand = new Random();

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void handleEnterProvince(CivilizationEvent.Enter event)
	{
		if ( event.getEntityPlayer() != null && event.getEntityPlayer().dimension == 0 && ToroQuestConfiguration.showProvinceEnterLeaveMessage )
		{
			TextComponentString message = enteringMessage(event.getEntityPlayer(),event.province);
			{
				event.getEntityPlayer().sendStatusMessage(message, true);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void handleLeaveProvince(CivilizationEvent.Leave event)
	{
		if ( event.getEntityPlayer() != null && event.getEntityPlayer().dimension == 0 && ToroQuestConfiguration.showProvinceEnterLeaveMessage )
		{
			TextComponentString message = leavingMessage(event.getEntityPlayer(),event.province);
			{
				event.getEntityPlayer().sendStatusMessage(message, true);
			}
		}
	}

	@SubscribeEvent
	public void onDeath(PlayerEvent.Clone event)
	{
		if ( event.getEntityPlayer().getEntityWorld().isRemote )
		{
			return;
		}

		PlayerCivilizationCapability newCap = PlayerCivilizationCapabilityImpl.get(event.getEntityPlayer());
		PlayerCivilizationCapability originalCap = PlayerCivilizationCapabilityImpl.get(event.getOriginal());

		if (originalCap == null)
		{
			return;
		}

		if (newCap == null)
		{
			throw new NullPointerException("missing player capability during clone");
		}

		// System.out.println("CLONE: " + originalCap.writeNBT());

		newCap.readNBT(originalCap.writeNBT());
	}

	@SubscribeEvent
	public void onSave(PlayerEvent.SaveToFile event)
	{
		// rrmote
				if (event.getEntityPlayer().getEntityWorld().isRemote)
				{
					return;
				}
		PlayerCivilizationCapability cap = PlayerCivilizationCapabilityImpl.get(event.getEntityPlayer());
		if (cap == null) {
			return;
		}

		NBTTagCompound civData = cap.writeNBT();

		// System.out.println("SAVE: " + civData);

		if (civData == null || civData.getTag("reputations") == null || ((NBTTagList) civData.getTag("reputations")).tagCount() < 1)
		{
			// System.out.println("******************Not writing empty ToroQuest
			// data for player " + event.getEntityPlayer().getName());
			return;
		}

		event.getEntityPlayer().getEntityData().setTag(ToroQuest.MODID + ".playerCivilization", civData);
	}

	@SubscribeEvent
	public void onLoad(PlayerEvent.LoadFromFile event)
	{
		// rrmote
		if (event.getEntityPlayer().getEntityWorld().isRemote) 
		{
			return;
		}

		PlayerCivilizationCapability cap = PlayerCivilizationCapabilityImpl.get(event.getEntityPlayer());
		if (cap == null) {
			return;
		}

		NBTTagCompound c = event.getEntityPlayer().getEntityData().getCompoundTag(ToroQuest.MODID + ".playerCivilization");

		if (c == null) {
			// System.out.println("******************Missing civ data on load");
		} else {
			System.out.println("LOAD: " + c.toString());
		}

		cap.readNBT(c);
	}

	@SubscribeEvent
	public void onEntityLoad(AttachCapabilitiesEvent<Entity> event)
	{
		if (!(event.getObject() instanceof EntityPlayer)) {
			return;
		}
		EntityPlayer player = (EntityPlayer) event.getObject();
		event.addCapability(new ResourceLocation(ToroQuest.MODID, "playerCivilization"), new PlayerCivilizationCapabilityProvider(player));
		syncClientCapability(player);
	}

	private void syncClientCapability(EntityPlayer player)
	{
		// rrmote
		if (player.getEntityWorld().isRemote)
		{
			TaskRunner.queueTask(new SyncTask(), 30);
		}
	}

	public static class PlayerCivilizationCapabilityProvider implements ICapabilityProvider {

		@CapabilityInject(PlayerCivilizationCapability.class)
		public static final Capability<PlayerCivilizationCapability> CAP = null;

		private PlayerCivilizationCapability instance;

		public PlayerCivilizationCapabilityProvider(EntityPlayer player) {
			instance = new PlayerCivilizationCapabilityImpl(player);
		}

		@Override
		public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
			return capability == CAP;
		}

		@Override
		public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
			if (CAP != null && capability == CAP) {
				return PlayerCivilizationCapabilityImpl.INSTANCE.cast(instance);
			}
			return null;
		}
	}

	public static void adjustPlayerRep(EntityPlayer player, int chunkX, int chunkZ, int value)
	{
		if ( player == null )
		{
			return;
		}
		
		Province province = CivilizationUtil.getProvinceAt(player.getEntityWorld(), chunkX, chunkZ);
		
		if ( province == null )
		{
			return;
		}
		
		CivilizationType civ = province.civilization;
		
		if ( civ == null )
		{
			return;
		}
		
		int startRep = PlayerCivilizationCapabilityImpl.get(player).getReputation(civ);
		int afterRep = startRep + value;
		PlayerCivilizationCapabilityImpl.get(player).adjustReputation(civ, value);
		
		if ( ToroQuestConfiguration.sendRepLevelMessage )
		{
			try
			{
				if ( !player.world.isRemote )
				{
					String message = repLevelMessage(player,civ,startRep,afterRep);
		
					if ( message != null )
					{
						player.sendMessage(new TextComponentString(message));
					}
				}
			}
			catch ( Exception e )
			{
				
			}
		}

	}
	
	public static void adjustPlayerRep(EntityPlayer player, CivilizationType civ, int value)
	{
		if ( player == null || civ == null || player.dimension != 0 )
		{
			return;
		}
		if ( value < 0 )
		{
			if ( !player.world.isRemote )
			{
				player.sendStatusMessage( new TextComponentString("§oCrime reported!§r"), true);
			}
		}
		int startRep = PlayerCivilizationCapabilityImpl.get(player).getReputation(civ);
		int afterRep = startRep + value;
		PlayerCivilizationCapabilityImpl.get(player).adjustReputation(civ, value);
		
		try
		{
			if ( !player.world.isRemote )
			{
				String message = repLevelMessage(player,civ,startRep,afterRep);

				if ( message != null )
				{
					player.sendMessage(new TextComponentString(message));
				}
			}
		} catch ( Exception e ) {}
	}

	@SubscribeEvent
	public void checkButcherInCivilization(LivingDeathEvent event)
	{		
		DamageSource source = event.getSource();
		
		if ( source == null )
		{
			return;
		}
		
		Entity s = source.getTrueSource();
		
		if ( !(s instanceof EntityPlayer) || s.world.isRemote )
		{
			return;
		}
		
		EntityPlayer player = (EntityPlayer) s;
		
		
		Entity e = event.getEntity();
		
		if ( e == null || !( e instanceof EntityLivingBase ) )
		{
			return;
		}
		
		EntityLivingBase victim = (EntityLivingBase) e;
		
		if (victim instanceof EntityMule && e instanceof EntityPlayer)
		{
			List<EntityCaravan> caravans = victim.getEntityWorld().getEntitiesWithinAABB(EntityCaravan.class, victim.getEntityBoundingBox().grow(20.0D, 10.0D, 20.0D));
			for (EntityCaravan caravan : caravans)
			{
				if ( ((EntityMule)victim).getLeashHolder() == caravan )
				{
					((EntityToroVillager)caravan).setMurder( (EntityPlayer)e );
				}
			}
		}
		
		Province province = PlayerCivilizationCapabilityImpl.get(player).getInCivilization();
		
		if (province == null || province.civilization == null )
		{
			return;
		}
		
		if ( victim instanceof IMob || victim instanceof EntityToroMob || victim instanceof EntityZombieRaider || victim instanceof EntityZombieVillagerRaider || victim instanceof EntityWolfRaider )
		{
			adjustPlayerRep( player, province.civilization, ToroQuestConfiguration.killMobRepGain );
		}
		
		int rep = PlayerCivilizationCapabilityImpl.get(player).getReputation(province.civilization);
		
		if ( victim instanceof EntityVillager || victim instanceof EntityGuard )
	    {
			World world = victim.world;
			
			callForHelp( world, victim, player );
									
			adjustPlayerRep( player, province.civilization, -100 );
			
			List<EntityToroNpc> guards = world.getEntitiesWithinAABB(EntityToroNpc.class, new AxisAlignedBB(player.getPosition()).grow(40, 20, 40), new Predicate<EntityToroNpc>()
			{
				public boolean apply(@Nullable EntityToroNpc entity)
				{
					return true;
				}
			});

			for (EntityToroNpc guard : guards)
			{
				guard.setAttackTarget( player );
				guard.setMurder( player );
			}
			
			List<EntityToroVillager> villagers = world.getEntitiesWithinAABB(EntityToroVillager.class, new AxisAlignedBB(player.getPosition()).grow(20, 10, 20), new Predicate<EntityToroVillager>()
			{
				public boolean apply(@Nullable EntityToroVillager entity)
				{
					return true;
				}
			});

			for (EntityToroVillager villager : villagers)
			{
				if ( !(villager.canEntityBeSeen( player )) )
				{
					continue;
				}
				villager.setMurder( player );
			}
	    }
		
		if ( rep >= 100 || (!ToroQuestConfiguration.loseReputationForAnimalGrief) )
		{
			return;
		}
		
		if ( victim instanceof EntityChicken || (victim instanceof EntityHorse && ( !((EntityHorse)victim).isTame() && !((EntityHorse)victim).isHorseSaddled()) ) || (victim instanceof EntityDonkey && ( !((EntityDonkey)victim).isTame() || !((EntityDonkey)victim).isHorseSaddled()) ) || victim instanceof EntityPig || victim instanceof EntitySheep || victim instanceof EntityCow || victim instanceof EntityToro || victim instanceof EntityMule )
		{
			
			boolean witnessed = villagersReportCrime( player.getEntityWorld(), player );

			List<EntityGuard> help = player.world.getEntitiesWithinAABB(EntityGuard.class, new AxisAlignedBB(player.getPosition()).grow(20, 10, 20), new Predicate<EntityGuard>()
			{
				public boolean apply(@Nullable EntityGuard entity)
				{
					return true;
				}
			});
			Collections.shuffle(help);
			boolean flag = false;
			for (EntityGuard entity : help)
			{
				if ( !entity.canEntityBeSeen(player) )
				{
					continue;
				}

				if ( entity.isAnnoyed() && rand.nextBoolean() )
				{
					entity.setAttackTarget(player);
				}
				entity.setAnnoyed();

				if ( !flag )
				{
					flag = true;
					switch (rand.nextInt(6) )
					{
						case 0: {chat(player, entity, "Kill our " + victim.getName().toLowerCase() + "s and we kill you!" );break;}
						case 1:	{chat(player, entity, "What did that " + victim.getName().toLowerCase() + " ever do to you!?");break;}
						case 2:	{chat(player, entity, player.getName() + " is killing our livestock!");break;} 
						case 3:	{chat(player, entity, "You really shouldn't have done that...");break;}
						case 4:	{chat(player, entity, "What are you doing!?");break;}
						case 5:	{chat(player, entity, "You'll pay for that!");break;}
					}
				}
			}
			if ( flag || witnessed ) adjustPlayerRep( player, province.civilization, -ToroQuestConfiguration.murderLivestockRepLoss );
		}
		
	}
	
	public void callForHelp( World world, EntityLivingBase villager, EntityLivingBase attacker )
	{
		if ( villager instanceof EntityToroVillager )
		{
			((EntityToroVillager)(villager)).setUnderAttack(attacker);	
		}
		
		List<EntityGuard> guards = world.getEntitiesWithinAABB(EntityGuard.class, new AxisAlignedBB(villager.getPosition()).grow(40, 20, 40), new Predicate<EntityGuard>()
		{
			public boolean apply(@Nullable EntityGuard entity)
			{
				return true;
			}
		});

		for (EntityGuard guard : guards)
		{
			if ( !guard.inCombat() && rand.nextBoolean() )
			{
				if ( villager instanceof EntityLiving ) ((EntityLiving)villager).getNavigator().tryMoveToEntityLiving(guard, 0.7F);
			}
			if ( guard.getAttackTarget() == null && guard.canEntityBeSeen(attacker) ) guard.setAttackTarget(attacker);
		}
		
		List<EntityVillager> villagers = world.getEntitiesWithinAABB(EntityVillager.class, new AxisAlignedBB(villager.getPosition()).grow(20, 10, 20), new Predicate<EntityVillager>()
		{
			public boolean apply(@Nullable EntityVillager entity)
			{
				return true;
			}
		});

		for (EntityVillager v : villagers)
		{
			if ( !(v.canEntityBeSeen(attacker)) )
			{
				continue;
			}
			if ( v instanceof EntityToroVillager ) ((EntityToroVillager)(v)).setUnderAttack(attacker);
		}
	}

	@SubscribeEvent
	public void handleEnteringProvince(EntityEvent.EnteringChunk event)
	{
		if (!(event.getEntity() instanceof EntityPlayerMP))
		{
			return;
		}
		EntityPlayerMP player = (EntityPlayerMP) event.getEntity();
		PlayerCivilizationCapabilityImpl.get(player).updatePlayerLocation(event.getNewChunkX(), event.getNewChunkZ());
	}

	@SideOnly(Side.CLIENT)
	public static TextComponentString leavingMessage(EntityPlayer player, Province province)
	{
		int rep = PlayerCivilizationCapabilityImpl.get(player).getReputation(province.civilization);
		String s;
		if (rep >= 50) 
		{
			s = province.civilization.getFriendlyLeavingMessage(province);
		}
		else if (rep <= -10)
		{
			s = province.civilization.getHostileLeavingMessage(province);
		}
		else
		{
			s = province.civilization.getNeutralLeavingMessage(province);
		}
		return new TextComponentString(s);
	}

	@SideOnly(Side.CLIENT)
	public static TextComponentString enteringMessage(EntityPlayer player, Province province)
	{
		int rep = PlayerCivilizationCapabilityImpl.get(player).getReputation(province.civilization);
		String s;
		if (rep >= 10) {
			s = province.civilization.getFriendlyEnteringMessage(province);
		} else if (rep <= -10) {
			s = province.civilization.getHostileEnteringMessage(province);
		} else {
			s = province.civilization.getNeutralEnteringMessage(province);
		}
		return new TextComponentString(s);
	}
	
	
	
	// TextComponentString
	// @SideOnly(Side.CLIENT)
	public static String repLevelMessage(EntityPlayer player, CivilizationType civ, int startRep, int afterRep)
	{
		String s = null;

		if (startRep > afterRep)
		{
			return s;
		}
		
		ReputationLevel before = ReputationLevel.fromReputation(startRep);
		ReputationLevel after = ReputationLevel.fromReputation(afterRep);

		if (before == after)
		{
			return s;
		}
		
		// String color = CivilizationUtil.chatColor(civ);

		if ( after == ReputationLevel.FRIENDLY ) 		// 50
		{
			s = "§rYou are now §oFriendly§r with §lHouse " + CivilizationType.civServerName(civ.getCivName()) +
			"§r! You may harvest crops without losing reputation.";
		}
		else if ( after == ReputationLevel.HONORED )	// 100
		{
			s = "§rYou are now §oHonored§r with §lHouse " + CivilizationType.civServerName(civ.getCivName()) +
			"§r! You may cull livestock without losing reputation.";

		}
		else if ( after == ReputationLevel.REVERED )	// 250
		{
			s = "§rYou are now §oRevered§r with §lHouse " + CivilizationType.civServerName(civ.getCivName()) +
			"§r! You may take from chests, fire spread, and destroy building blocks without losing reputation.";
		}
		else if ( after == ReputationLevel.EXALTED )	// 500
		{
			s = "§rYou are now §oExalted§r with §lHouse " + CivilizationType.civServerName(civ.getCivName()) +
			"§r! You are reputable enough to rename provinces under the rule of this civilization.";

		}
		else if ( after == ReputationLevel.CHAMPION )	// 1000
		{
			s = "§rYou are now a §oChampion§r of §lHouse " + CivilizationType.civServerName(civ.getCivName()) +
			"§r! You have unlocked Legendary Quests!";
		}
		else if ( after == ReputationLevel.HERO )		// 2000
		{
			s = "§rYou are now a §oHero§r of §lHouse " + CivilizationType.civServerName(civ.getCivName()) + "§r!";
		}
		else if ( after == ReputationLevel.LEGEND )		// 3000
		{
			s = "§rYou are now a §oLegend§r of §lHouse " + CivilizationType.civServerName(civ.getCivName()) + 
			"§r! You have acheived the highest reputation rank. Quest rewards are doubled!";

		}
		return s;
	}

	// BREED
	@SubscribeEvent
	public void breed(BabyEntitySpawnEvent event)
	{
		EntityPlayer e = event.getCausedByPlayer();
		if ( e == null ) return;
		Province province = PlayerCivilizationCapabilityImpl.get(e).getInCivilization();
		
		if (province == null || province.civilization == null )
		{
			return;
		}
		
		if (!(event.getParentA() instanceof EntityAnimal))
		{
			return;
		}
		
		EntityAnimal animal = (EntityAnimal)event.getParentA();
		
		if (!(event.getParentB() instanceof EntityAnimal))
		{
			return;
		}

		EntityPlayer playerA = ((EntityAnimal) event.getParentA()).getLoveCause();
		EntityPlayer playerB = ((EntityAnimal) event.getParentB()).getLoveCause();

		if (playerA != null)
		{
			if ( !e.world.isRemote ) 
			{
	            try
	            {
	            	QuestBreed.INSTANCE.onBreed( e, animal );
	            }
	            catch(Exception ex)
	            {
	            	
	            }
			}
			return;
		}

		animal = (EntityAnimal)event.getParentB();

		if (playerB != null)
		{
			//adjustPlayerRep(playerB, event.getParentB().chunkCoordX, event.getParentB().chunkCoordZ, 1);
			if ( !e.world.isRemote ) 
			{
	            try
	            {
	            	QuestBreed.INSTANCE.onBreed( e, animal );
	            }
	            catch(Exception ex)
	            {
	            	
	            }
			}
			return;
		}
	}
	
	@SubscribeEvent
	public void onBucketUse( FillBucketEvent event )
	{
		if ( event.getWorld().isRemote )
		{
			return;
		}
		EntityPlayer player = event.getEntityPlayer();
		if ( player == null )
		{
			return;
		}
		Province province = PlayerCivilizationCapabilityImpl.get(player).getInCivilization();
		
		if (province == null || province.civilization == null )
		{
			return;
		}
		
		int rep = PlayerCivilizationCapabilityImpl.get(player).getReputation(province.civilization);

		if ( rep >= 250 || (!ToroQuestConfiguration.loseReputationForBlockGrief) )
		{
			return;
		}
		
		// int reportChance = MathHelper.clamp(rep, 100, 500)/100;
		
		
		final RayTraceResult target = event.getTarget();
		if (target == null || target.typeOfHit != RayTraceResult.Type.BLOCK) return;
		
		if ( event.getEmptyBucket() == null ) return;

		if ( event.getEmptyBucket().toString().contains("bucketLava") )
		{
			boolean witnessed = villagersReportCrime( event.getWorld(), player );

			List<EntityGuard> help = event.getWorld().getEntitiesWithinAABB(EntityGuard.class, new AxisAlignedBB(player.getPosition()).grow(20, 10, 20), new Predicate<EntityGuard>()
			{
				public boolean apply(@Nullable EntityGuard entity)
				{
					return true;
				}
			});
			Collections.shuffle(help);
			boolean flag = false;
			for (EntityGuard entity : help)
			{
				if ( !entity.canEntityBeSeen(player) )
				{
					continue;
				}
				entity.setAttackTarget(player);
				entity.setAnnoyed();
				if ( !flag )
				{
					flag = true;
					switch ( rand.nextInt(10) )
					{
						case 0: {chat( player, entity, "§r: Stop! What are you doing??");break;}
						case 1:	{chat( player, entity, "§r: That is very dangerous, stop!");break;}
						case 2:	{chat( player, entity, "§r: " + player.getName() + " is trying to burn down the village, help!");break;} 
						case 3:	{chat( player, entity, "§r: You really shouldn't be doing that!");break;}
						case 4:	{chat( player, entity, "§r: Are you insane?! You could hurt someone!");break;}
						case 5:	{chat( player, entity, "§r: Lava! Lava!");break;}
						case 6:	{chat( player, entity, "§r: That is not allowed here!");break;}
						case 7:	{chat( player, entity, "§r: Your crimes will not be tolerated!");break;}
						case 8:	{chat( player, entity, "§r: Griefer!");break;}
						case 9:	{chat( player, entity, "§r: Stop griefing our village, immediately!");break;}
					}
				}
			}
			if ( flag || witnessed ) adjustPlayerRep( player, province.civilization, -ToroQuestConfiguration.lavaGriefRepLoss );
		}
	}
	
	@SubscribeEvent
	public void fire(EntityPlaceEvent event)
	{
		if ( event.getWorld().isRemote )
		{
			return;
		}
		Entity eventEntity = event.getEntity();
		if ( eventEntity == null || !(eventEntity instanceof EntityPlayer) )
		{
			return;
		}
		EntityPlayer player = (EntityPlayer)eventEntity;
		
		Province province = PlayerCivilizationCapabilityImpl.get(player).getInCivilization();
		
		if (province == null || province.civilization == null )
		{
			return;
		}
		
		int rep = PlayerCivilizationCapabilityImpl.get(player).getReputation(province.civilization);
		
		if ( rep >= 250 || (!ToroQuestConfiguration.loseReputationForBlockGrief) )
		{
			return;
		}
		
		Block e = event.getState().getBlock();
		
		if ( e instanceof BlockFire || e instanceof BlockTNT )
		{
			boolean witnessed = villagersReportCrime( event.getWorld(), player );

			List<EntityToroNpc> help = event.getWorld().getEntitiesWithinAABB(EntityToroNpc.class, new AxisAlignedBB(player.getPosition()).grow(20, 10, 20), new Predicate<EntityToroNpc>()
			{
				public boolean apply(@Nullable EntityToroNpc entity)
				{
					return true;
				}
			});
			Collections.shuffle(help);
			boolean flag = false;
			for (EntityToroNpc entity : help)
			{
				if ( !entity.canEntityBeSeen(player) )
				{
					continue;
				}
				
				entity.setAttackTarget(player);
				entity.setAnnoyed();
				
				if ( !flag )
				{
					flag = true;
					switch (rand.nextInt(9) )
					{
						case 0: {chat(player, entity, "§r: Stop! What are you doing??");break;}
						case 1:	{chat(player, entity, "§r: That is very dangerous, stop!");break;}
						case 2:	
						{
							if ( e instanceof BlockFire ){chat(player, entity, "§r: " + player.getName() + " is trying to burn down the village, help!");break;}
							else {chat(player, entity, "§r: " + player.getName() + " is trying to blow up the village, help!");break;}
						}
						case 3:	{chat(player, entity, "§r: You really shouldn't be doing that!");break;}
						case 4:	{chat(player, entity, "§r: Are you insane?! You could hurt someone!");break;}
						case 5:
						{
							if ( e instanceof BlockFire ){chat(player, entity, "§r: Fire! Fire!");break;}
							else {chat(player, entity, "§r: What are you doing with those explosives?!");break;}
						}
						case 6:	{chat(player, entity, "§r: That is not allowed here!");break;}
						case 7:	{chat( player, entity, "§r: Griefer!");break;}
						case 8:	{chat( player, entity, "§r: Stop griefing our village, immediately!");break;}
					}
				}
			}
			if ( flag || witnessed ) adjustPlayerRep( player, province.civilization, -ToroQuestConfiguration.fireGriefRepLoss );
		}
	}
	
	@SubscribeEvent
	public void harvestDrops(HarvestDropsEvent event)
	{
		if ( event.getWorld().isRemote || rand.nextBoolean() )
		{
			return;
		}
		if ( isCrop(event.getState().getBlock()) )
		{
//			List<EntityVillager> villagers = event.getWorld().getEntitiesWithinAABB(EntityVillager.class, new AxisAlignedBB(event.getPos()).grow(1, 1, 1));
//			for (EntityVillager villager : villagers)
//			{
//				return;
//			}

			List<EntityPlayer> players = event.getWorld().getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(event.getPos()).grow(4, 4, 4));
			for (EntityPlayer player : players)
			{
				Province province = PlayerCivilizationCapabilityImpl.get(player).getInCivilization();
				if ( province == null || province.civilization == null )
				{
					continue;
				}
				
            	QuestFarm.INSTANCE.destroyedCrop(player);
				
				int rep = PlayerCivilizationCapabilityImpl.get(player).getReputation(province.civilization);
				
				if ( rep >= 50 || (!ToroQuestConfiguration.loseReputationForCropGrief) )
				{
					continue;
				}
				
				boolean witnessed = villagersReportCrime( event.getWorld(), player );
				
				List<EntityToroNpc> help = event.getWorld().getEntitiesWithinAABB(EntityToroNpc.class, new AxisAlignedBB(player.getPosition()).grow(20, 16, 20), new Predicate<EntityToroNpc>()
				{
					public boolean apply(@Nullable EntityToroNpc entity)
					{
						return true;
					}
				});
				
				Collections.shuffle(help);
				
				boolean flag = false;
				
				for (EntityToroNpc entity : help)
				{
					if ( !entity.canEntityBeSeen(player) )
					{
						continue;
					}
					
					if ( entity.isAnnoyed() && !entity.inCombat() )
					{
						entity.setAttackTarget(player);
					}
					
					if ( rand.nextBoolean() )
					{
						entity.setAnnoyed();
					}
					
					if ( !flag )
					{
						flag = true;
						if ( !entity.inCombat() )
						{
							switch ( rand.nextInt(9) )
							{
								case 0: {chat(player, entity, "§r: Stop! Those crops are not yours!");break;}
								case 1:	{chat(player, entity, "§r: Those crops aren't yours to take!");break;}
								case 3:	{chat(player, entity, "§r: You really shouldn't be doing that!");break;}
								case 4:	{chat(player, entity, "§r: " + player.getName() + "! You need to stop.");break;}
								case 5:	{chat(player, entity, "§r: What in the Nether? Don't go breaking our crops!");break;}
								case 6:	{chat(player, entity, "§r: Our precious crops!");break;}
								case 7:	{chat(player, entity, "§r: Stop griefing our farmland!");break;}
								case 8:	{chat(player, entity, "§r: You'll pay for that!");break;}
							}
						}
					}
				}
				if ( flag || witnessed ) adjustPlayerRep( player, province.civilization, -ToroQuestConfiguration.unexpensiveRepLoss );
			}
		}
	}
	
	@SubscribeEvent
	public void harvest(BreakEvent event)
	{
		if ( event.getWorld().isRemote )
		{	
			return;
		}
		
		Block block = event.getState().getBlock();
		BlockPos pos = event.getPos();
		EntityPlayer player = event.getPlayer();
		
		if ( player == null || pos == null || block == null )
		{
			return;
		}
		
		Province province = PlayerCivilizationCapabilityImpl.get(player).getInCivilization();
		
		if ( rand.nextInt(8000) <= ToroQuestConfiguration.artifactDropRate && ( block instanceof BlockStone || block instanceof BlockGrass || block instanceof BlockDirt || block instanceof BlockOre || block instanceof BlockGravel || block instanceof BlockClay || block instanceof BlockSand ) )
		{
			World world = event.getWorld();
			block.dropXpOnBlockBreak(world, pos, 10);
			ItemStack stack = randomStolenItem(world, province);
			if ( stack == null ) return;
			EntityItem entityitem = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), stack);
            entityitem.setDefaultPickupDelay();
            world.spawnEntity(entityitem);
		}
		
		if ( province == null || province.civilization == null )
		{
			return;
		}
		
		int rep = PlayerCivilizationCapabilityImpl.get(player).getReputation(province.civilization);
		
        if ( (!ToroQuestConfiguration.loseReputationForCropGrief) || (rep >= 50 && isCrop(block)) )
        {
        	return;
        }
		
		if ( rep >= 250 || (!ToroQuestConfiguration.loseReputationForBlockGrief) )
		{
			return;
		}
		
		boolean valuable = false;
		boolean witnessed = false;
		
		if ( isBuilding( block ) )
		{
			if ( rand.nextBoolean() ) return;
			valuable = false;
			witnessed = villagersReportCrime( event.getWorld(), player );
		}
		else if ( isValuableBuilding( block ) )
		{
			valuable = true;
			witnessed = villagersReportCrime( event.getWorld(), player );
			if ( block instanceof BlockQuartz || block == Blocks.GOLD_BLOCK )
			{
				List<EntityVillageLord> villageLord = event.getWorld().getEntitiesWithinAABB(EntityVillageLord.class, new AxisAlignedBB(player.getPosition()).grow(20, 16, 20), new Predicate<EntityVillageLord>()
				{
					public boolean apply(@Nullable EntityVillageLord entity)
					{
						return true;
					}
				});
				if ( villageLord.size() > 0 )
				{
					try
					{
						villageLord.get(0).chat(player, "My beautiful throne!");
						villageLord.get(0).callForHelp(player);
					}
					catch (Exception e)
					{
						
					}
				}
			}
		}
		else
		{
			return;
		}
		
		List<EntityToroNpc> help = player.world.getEntitiesWithinAABB(EntityToroNpc.class, new AxisAlignedBB(player.getPosition()).grow(20, 16, 20), new Predicate<EntityToroNpc>()
		{
			public boolean apply(@Nullable EntityToroNpc entity)
			{
				return true;
			}
		});
		Collections.shuffle(help);
		boolean flag = false;
		for (EntityToroNpc entity : help)
		{
			if ( !entity.canEntityBeSeen(player) )
			{
				continue;
			}
			
			if ( !valuable )
			{
				if ( entity.isAnnoyed() && !entity.inCombat() )
				{
					entity.setAttackTarget(player);
				}
				
				entity.setAnnoyed();
				
				if ( !flag )
				{
					flag = true;
					if ( !entity.inCombat() ) // reemote
					{
						entity.getNavigator().tryMoveToEntityLiving(player, 0.5D);
						switch (rand.nextInt(23) )
						{
							case 0: {chat(player, entity, "§r: Filthy criminal!");break;}
							case 1:	{chat(player, entity, "§r: Stop breaking things!");break;}
							case 2:	{chat(player, entity, "§r: Please don't break that!");break;}
							case 3:	{chat(player, entity, "§r: You really shouldn't be doing that!");break;}
							case 4:	{chat(player, entity, "§r: " + player.getName() + "! You need to stop.");break;}
							case 5:	{chat(player, entity, "§r: What in the Nether? Don't go breaking that!");break;}
							case 6:
							{
								//String name = block.getLocalizedName().toLowerCase();
								//if ( !name.contains(".") )
								//{
								//	chat(player, entity, "§r: Hey, give back the " + block.getLocalizedName().toLowerCase() + "!");
								//}
								chat(player, entity, "§r: Hey, give that back!");
								break;
							}
							case 7:	{chat(player, entity, "§r: What are you doing?!");break;}
							case 8:	{chat(player, entity, "§r: Help! " + player.getName() + " is destroying the village!");break;}
							case 9:	{chat(player, entity, "§r: Stop right there, griefer!");break;}
							case 10:{chat(player, entity, "§r: Griefer!");break;}
							case 11:{chat(player, entity, "§r: Filthy griefer!");break;}
							case 12:{chat(player, entity, "§r: Please stop griefing the village!");break;}
							case 13:{chat(player, entity, "§r: What are you doing?!");break;}
							case 14:{chat(player, entity, "§r: That will be your last mistake!");break;}
							//case 0: {chat(player, entity, "§r: Stop! That was valuable!");break;}
							case 15:
							{
								chat(player, entity, "§r: You dumb circlehead. Stealing is not tolerated!");break;// that " + block.getLocalizedName().toLowerCase() + "!");break;
							}
							case 16:
							{
								chat(player, entity, "§r: You really shouldn't be stealing!");break;// that " + block.getLocalizedName().toLowerCase() + "!");break;
							}
							case 17:{chat(player, entity, "§r: Stop stealing from us, filthy rat!");break;}
							case 18:{chat(player, entity, "§r: Don't take what isn't yours, thief!");break;}
							case 19:{chat(player, entity, "§r: You little thief!");break;}
							case 21:{chat(player, entity, "§r: Hah! Caught you!");break;}
							case 22:{chat(player, entity, "§r: That wasn't yours, thief!");break;}
						}
					}
				}
			}
			else
			{
				entity.getNavigator().tryMoveToEntityLiving(player, 0.5D);
				entity.setAttackTarget(player);
				entity.setAnnoyed();
				if ( !flag )
				{
					flag = true;
					switch ( rand.nextInt(23) )
					{
					case 0: {chat(player, entity, "§r: Filthy criminal!");break;}
					case 1:	{chat(player, entity, "§r: Stop breaking things!");break;}
					case 2:	{chat(player, entity, "§r: Please don't break that!");break;}
					case 3:	{chat(player, entity, "§r: You really shouldn't be doing that!");break;}
					case 4:	{chat(player, entity, "§r: " + player.getName() + "! You need to stop.");break;}
					case 5:	{chat(player, entity, "§r: What in the Nether? Don't go breaking that!");break;}
					case 6:
					{
						//String name = block.getLocalizedName().toLowerCase();
						//if ( !name.contains(".") )
						//{
						//	chat(player, entity, "§r: Hey, give back the " + block.getLocalizedName().toLowerCase() + "!");
						//}
						chat(player, entity, "§r: Hey, give that back!");
						break;
					}
					case 7:	{chat(player, entity, "§r: What are you doing?!");break;}
					case 8:	{chat(player, entity, "§r: Help! " + player.getName() + " is destroying the village!");break;}
					case 9:	{chat(player, entity, "§r: Stop right there, griefer!");break;}
					case 10:{chat(player, entity, "§r: Griefer!");break;}
					case 11:{chat(player, entity, "§r: Filthy griefer!");break;}
					case 12:{chat(player, entity, "§r: Please stop griefing the village!");break;}
					case 13:{chat(player, entity, "§r: What are you doing?!");break;}
					case 14:{chat(player, entity, "§r: That will be your last mistake!");break;}
					//case 0: {chat(player, entity, "§r: Stop! That was valuable!");break;}
					case 15:
					{
						chat(player, entity, "§r: You dumb circlehead. Stealing is not tolerated!");break;// that " + block.getLocalizedName().toLowerCase() + "!");break;
					}
					case 16:
					{
						chat(player, entity, "§r: You really shouldn't be stealing!");break;// that " + block.getLocalizedName().toLowerCase() + "!");break;
					}
					case 17:{chat(player, entity, "§r: Stop stealing from us, filthy rat!");break;}
					case 18:{chat(player, entity, "§r: Don't take what isn't yours, thief!");break;}
					case 19:{chat(player, entity, "§r: You little thief!");break;}
					case 21:{chat(player, entity, "§r: Hah! Caught you!");break;}
					case 22:{chat(player, entity, "§r: That wasn't yours, thief!");break;}
					}
				}
			}
		}
		if ( flag || witnessed )
		{
			if ( !valuable )
			{
				adjustPlayerRep( event.getPlayer(), province.civilization, -ToroQuestConfiguration.unexpensiveRepLoss );
			}
			else
			{
				adjustPlayerRep( event.getPlayer(), province.civilization, -ToroQuestConfiguration.expensiveRepLoss );
			}
		}
	}
	
	// all villagers, within range and can see, reports to guards
	private boolean villagersReportCrime(World world, EntityPlayer player)
	{
		List<EntityToroVillager> villagerList = world.getEntitiesWithinAABB(EntityToroVillager.class, new AxisAlignedBB(player.getPosition()).grow(12, 8, 12), new Predicate<EntityToroVillager>()
		{
			public boolean apply(@Nullable EntityToroVillager entity)
			{
				return true;
			}
		});
		Collections.shuffle(villagerList);
		boolean flag = false;
		for ( EntityToroVillager villager : villagerList )
		{
			if ( !villager.canEntityBeSeen(player) )
			{
				continue;
			}
			if ( !flag )
			{
				villager.reportToGuards(player);
				villager.playSound(SoundEvents.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
				flag = true;
			}
			villager.blockTrade();
		}
		return flag;
	}
	
	public static boolean isBuilding(Block block)
	{
		return     
				   (block instanceof BlockPlanks)
		        || (block.getDefaultState() == Blocks.COBBLESTONE.getDefaultState())
		        || (block.getDefaultState() == Blocks.WOOL.getDefaultState())
		        || (block.getDefaultState() == Blocks.STAINED_HARDENED_CLAY.getDefaultState())
		        || (block.getDefaultState() == Blocks.STONEBRICK.getDefaultState())
				|| (block.getDefaultState() == Blocks.GLASS_PANE.getDefaultState())
				|| (block.getDefaultState() == Blocks.GRASS_PATH.getDefaultState())
				|| (block.getDefaultState() == Blocks.FARMLAND.getDefaultState())
				|| (block.getDefaultState() == Blocks.STONE_SLAB.getDefaultState())
		        || (block.getDefaultState() == Blocks.LOG.getDefaultState())
		        || (block.getDefaultState() == Blocks.WOODEN_PRESSURE_PLATE.getDefaultState())
		        //|| (block.getDefaultState() == Blocks.CRAFTING_TABLE.getDefaultState())
		        || (block.getDefaultState() == Blocks.TORCH.getDefaultState())
		        || (block instanceof BlockCarpet)
		        || (block instanceof BlockFence)
		        || (block instanceof BlockColored)
		        || (block instanceof BlockLog)
		        || (block instanceof BlockStairs);
	}
	
	public static boolean isValuableBuilding(Block block)
	{
		return     
		           (block.getDefaultState() == Blocks.BOOKSHELF.getDefaultState())
		        || (block.getDefaultState() == Blocks.GOLD_BLOCK.getDefaultState())
		        || (block instanceof BlockChest)
		        || (block instanceof BlockQuartz)
		        || (block.getDefaultState() == Blocks.QUARTZ_STAIRS.getDefaultState())
		        || (block.getDefaultState() == Blocks.FURNACE.getDefaultState())
		        || (block.getDefaultState() == Blocks.LIT_FURNACE.getDefaultState())
		        || (block.getDefaultState() == Blocks.CHEST.getDefaultState())
		        || (block instanceof BlockDoor)
		        || (block instanceof BlockBed)
		        || (block.getDefaultState() == Blocks.ANVIL.getDefaultState());
	}
	
	public static boolean isCrop(Block block)
	{
		return block instanceof BlockCrops || block instanceof BlockStem;
	}
	
	// ==================================== spawn bandits ================================
	@SubscribeEvent
	public void civTimer(WorldTickEvent event)
	{
	    if (TickEvent.Phase.START.equals(event.phase))
		{
			return;
		}
	    
	    if ( event.world.getWorldTime() % 200 == 0 )
		{
			for ( EntityPlayer p : event.world.playerEntities )
			{
				Province province = PlayerCivilizationCapabilityImpl.get(p).getInCivilization();
				if ( province == null )
				{
					continue;
				}
				CivilizationDataAccessor worldData = CivilizationsWorldSaveData.get(p.world);
				if ( worldData == null )
				{
					continue;
				}
				int duration = PlayerCivilizationCapabilityImpl.get(p).getReputation(province.civilization) + 1;
				if ( duration <= 50 )
				{
					continue;
				}
				duration = MathHelper.clamp(PlayerCivilizationCapabilityImpl.get(p).getReputation(province.civilization),0,3000)*4;
				int power = 0;
				if ( worldData.hasTrophyBeholder(province.id) )
				{
					power = 1;
				}
				if ( worldData.hasTrophyMage(province.id) )
				{
		    		p.addPotionEffect(new PotionEffect(MobEffects.HASTE, duration, power, true, false));
				}
				if ( worldData.hasTrophyTitan(province.id) )
				{
		    		p.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, duration, 1, true, false));
				}
				if ( worldData.hasTrophyPig(province.id) )
				{
		    		p.addPotionEffect(new PotionEffect(MobEffects.SATURATION, duration, power, true, false));
				}
				if ( worldData.hasTrophyBandit(province.id) )
				{
		    		p.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, duration, power, true, false));
				}
				if ( worldData.hasTrophySkeleton(province.id) )
				{
		    		p.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, duration, power, true, false));
				}
				if ( worldData.hasTrophySpider(province.id) )
				{
		    		p.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, duration, power, true, false));
		    		p.addPotionEffect(new PotionEffect(MobEffects.SPEED, duration, power, true, false));
				}
			}
		}
		
		if ( event.world.getWorldTime() % 1200 != 0 )
		{
			return;
		}

		// ======================================== SPAWN RATE ========================================
		
		int players = event.world.playerEntities.size();
		
		if ( players > 0 ) // && event.world.getWorldTime() % 1200 == 0 )
		{
			if ( ToroQuestConfiguration.banditSpawnRate > 0 && (rand.nextInt(100)) < ToroQuestConfiguration.banditSpawnRate + MathHelper.clamp((players-1)*2, 0, 25) )
	    	{
	    		spawnSentryNearPlayer( event.world );
	    	}
			
			if ( ToroQuestConfiguration.caravanSpawnRate > 0 && event.world.getWorldTime() <= 12000 && (rand.nextInt(100)) < ToroQuestConfiguration.caravanSpawnRate + MathHelper.clamp((players-1)*2, 0, 25) )
	    	{
	    		spawnCaravanNearProvince( event.world );
	    	}
			
			if ( ToroQuestConfiguration.provinceSiegeRate > 0 && (rand.nextInt(100)) < ToroQuestConfiguration.provinceSiegeRate + MathHelper.clamp((players-1)*2, 0, 25) )
		    {
		    	if ( rand.nextBoolean() && event.world.getWorldTime() >= 15000 && event.world.getWorldTime() <= 19000 )
		    	{
			    	spawnZombies( event.world );
			        if ( rand.nextInt(4) == 0 )
			        {
			        	spawnZombies( event.world );
			        }
		    	}
		    	else if ( rand.nextInt(4) == 0 )
		    	{
			    	spawnWolves( event.world );
		    		if ( rand.nextInt(4) == 0 )
			        {
			        	spawnWolves( event.world );
			        }
		    	}
		    	else
		    	{
		    		spawnSentry( event.world );
			        if ( rand.nextInt(4) == 0 )
			        {
			        	spawnSentry( event.world );
			        }
		    	}
		    }
			
			if ( ToroQuestConfiguration.fugitiveSpawnRate > 0 && (rand.nextInt(100)) < ToroQuestConfiguration.fugitiveSpawnRate + MathHelper.clamp((players-1)*2, 2, 25) )
		    {
		    	spawnFugitive( event.world );
		        if ( rand.nextInt(4) == 0 )
		        {
		        	spawnFugitive( event.world );
		        }
		    }
			
			
		}
		
		try
		{
			for ( EntityPlayer p : event.world.playerEntities )
			{
				if ( PlayerCivilizationCapabilityImpl.get(p).getReputation(CivilizationType.EARTH) < 0 )
				{
					PlayerCivilizationCapabilityImpl.get(p).adjustReputation(CivilizationType.EARTH, 1);
				}
				if ( PlayerCivilizationCapabilityImpl.get(p).getReputation(CivilizationType.FIRE) < 0 )
				{
					PlayerCivilizationCapabilityImpl.get(p).adjustReputation(CivilizationType.FIRE, 1);
				}
				if ( PlayerCivilizationCapabilityImpl.get(p).getReputation(CivilizationType.MOON) < 0 )
				{
					PlayerCivilizationCapabilityImpl.get(p).adjustReputation(CivilizationType.MOON, 1);
				}
				if ( PlayerCivilizationCapabilityImpl.get(p).getReputation(CivilizationType.SUN) < 0 )
				{
					PlayerCivilizationCapabilityImpl.get(p).adjustReputation(CivilizationType.SUN, 1);
				}
				if ( PlayerCivilizationCapabilityImpl.get(p).getReputation(CivilizationType.WATER) < 0 )
				{
					PlayerCivilizationCapabilityImpl.get(p).adjustReputation(CivilizationType.WATER, 1);
				}
				if ( PlayerCivilizationCapabilityImpl.get(p).getReputation(CivilizationType.WIND) < 0 )
				{
					PlayerCivilizationCapabilityImpl.get(p).adjustReputation(CivilizationType.WIND, 1);
				}
			}
		}
		catch ( Exception e )
		{
			
		}
	}

	// Spawns bandits to attack a province //
	protected void spawnSentry( World world )
	{
		// if ( world.isRemote ) return;

		try
		{
			int range = 176;
			List<EntityPlayer> players = world.playerEntities;
			Collections.shuffle(players);
			for ( EntityPlayer player : players )
			{
				if ( player == null || player.world == null || world == null || world.provider == null || world.provider.getDimension() != 0 )
				{
					continue;
				}
				
				Province 			     province = CivilizationUtil.getProvinceAt(world, player.chunkCoordX,   player.chunkCoordZ);
				if ( province == null ) {province = CivilizationUtil.getProvinceAt(world, player.chunkCoordX+2, player.chunkCoordZ+2);}
				if ( province == null ) {province = CivilizationUtil.getProvinceAt(world, player.chunkCoordX+2, player.chunkCoordZ-2);}
				if ( province == null ) {province = CivilizationUtil.getProvinceAt(world, player.chunkCoordX-2, player.chunkCoordZ+2);}
				if ( province == null ) {province = CivilizationUtil.getProvinceAt(world, player.chunkCoordX-2, player.chunkCoordZ-2);}
				if ( province == null )
				{
					continue;
				}
				
				int villageCenterX = province.getCenterX();
				int villageCenterZ = province.getCenterZ();
				
				int x = (rand.nextInt(range));
				int z = (rand.nextInt(range));
				
				while ( x < range/2 && z < range/2 )
				{
					x = (rand.nextInt(range));
					z = (rand.nextInt(range));
				}
				
				x *= (rand.nextInt(2)*2-1);
				z *= (rand.nextInt(2)*2-1);
				
				x += villageCenterX;
				z += villageCenterZ;
				
				BlockPos loc = new BlockPos(x,player.world.getHeight()/2,z);
				
				BlockPos banditSpawnPos = findSpawnLocationFrom(world, loc);
				
				if (banditSpawnPos == null)
				{
					continue;
				}
				
				List<EntityPlayer> nearbyPlayers = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(banditSpawnPos).grow(48, 32, 48));
				if ( nearbyPlayers.size() > 0 )
				{
					continue;
				}
				

				int rep = Math.abs(PlayerCivilizationCapabilityImpl.get(player).getReputation(province.civilization));
				int count = ( rand.nextInt( MathHelper.clamp( (int)((Math.abs(rep)+900)/300), 3, 12) )
										  + MathHelper.clamp( (int)((Math.abs(rep)+750)/300), 2, 8) );
				

				if ( !world.isRemote )
				{
					if ( ToroQuestConfiguration.orcsAreNeutral || rand.nextBoolean() )
					{
						for ( int i = count; i > 0; i-- )
						{
							EntitySentry e = new EntitySentry(world);
							e.despawnTimer--;
							//e.setSkin(banditType);
							e.setPosition(banditSpawnPos.getX() + 0.5,banditSpawnPos.getY()+0.1, banditSpawnPos.getZ() + 0.5 );
							e.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(e)), (IEntityLivingData) null);
							world.spawnEntity(e);
							e.setAttackTarget(player);
							e.setRaidLocation(villageCenterX, villageCenterZ); // TODO
						}
					}
					else
					{
						for ( int i = count; i > 0; i-- )
						{
							EntityOrc e = new EntityOrc(world);
							e.despawnTimer--;
							//e.setSkin(banditType);
							e.setPosition(banditSpawnPos.getX() + 0.5,banditSpawnPos.getY()+0.1, banditSpawnPos.getZ() + 0.5 );
							e.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(e)), (IEntityLivingData) null);
							world.spawnEntity(e);
							e.setAttackTarget(player);
							e.setRaidLocation(villageCenterX, villageCenterZ); // TODO
						}
					}
				}
				//System.out.println( "Bandits spawned at location:" + x + " " + z );

				return;
			}
		}
		catch (Exception e)
		{
			System.out.println("ERROR SPAWNING EntityBandit: " + e);
			return;
		}
	}
	
	public void spawnCaravanNearProvince( World world )
	{
		try
		{
			int range = 352;
			List<EntityPlayer> players = world.playerEntities;
			Collections.shuffle(players);
			for ( EntityPlayer player : players )
			{
				if ( player == null || player.world == null || world == null || world.provider == null || world.provider.getDimension() != 0 )
				{
					continue;
				}
				
				Village village = world.getVillageCollection().getNearestVillage(player.getPosition(), 640);
				
				if ( village == null )
				{
					continue;
				}
				
				Province province = CivilizationUtil.getProvinceAt(world, village.getCenter().getX()/16, village.getCenter().getZ()/16 );

				if ( province == null )
				{
					continue;
				}
				
				int villageCenterX = province.getCenterX();
				int villageCenterZ = province.getCenterZ();
				
				int x = (rand.nextInt(range));
				int z = (rand.nextInt(range));
				
				while ( x < range/2 && z < range/2 )
				{
					x = (rand.nextInt(range));
					z = (rand.nextInt(range));
				}
				
				x *= (rand.nextInt(2)*2-1);
				z *= (rand.nextInt(2)*2-1);
				
				x += villageCenterX;
				z += villageCenterZ;
				
				Province 			    	 provinceNear = CivilizationUtil.getProvinceAt(world,  x*16,     z*16);
				if ( provinceNear == null ) {provinceNear = CivilizationUtil.getProvinceAt(world, (x+3)*16, (z+3)*16);}
				if ( provinceNear == null ) {provinceNear = CivilizationUtil.getProvinceAt(world, (x+3)*16, (z-3)*16);}
				if ( provinceNear == null ) {provinceNear = CivilizationUtil.getProvinceAt(world, (x-3)*16, (z+3)*16);}
				if ( provinceNear == null ) {provinceNear = CivilizationUtil.getProvinceAt(world, (x-3)*16, (z-3)*16);}
				
				if ( provinceNear != null )
				{
					continue;
				}

				BlockPos loc = new BlockPos( x, player.world.getHeight()/2, z );
								
				BlockPos banditSpawnPos = findSpawnLocationFrom(world, loc);
				
				if ( banditSpawnPos == null )
				{
					continue;
				}
								
				List<EntityPlayer> nearbyPlayers = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(banditSpawnPos).grow(40, 20, 40));
				
				if ( nearbyPlayers.size() > 0 )
				{
					continue;
				}
				
				int i = 1;
				
				if ( !world.isRemote )
				{
					while ( i > 0 )
					{
						EntityCaravan e = new EntityCaravan(world);
						e.setPosition(banditSpawnPos.getX() + 0.5,banditSpawnPos.getY()+0.1, banditSpawnPos.getZ() + 0.5 );
						world.spawnEntity(e);
		
						e.addCaravan();
						
						if ( rand.nextInt(i+2) == 0 )
						{
							e.addCaravan();
							if ( rand.nextInt(i+2) == 0 )
							{
								e.addCaravan();
							}
						}
						
						if ( rand.nextInt(i+1) == 0 )
						{
							EntityGuard g = new EntityGuard(world);
							g.setPosition(banditSpawnPos.getX() + 0.5,banditSpawnPos.getY()+0.1, banditSpawnPos.getZ() + 0.5 );
							g.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Items.IRON_SWORD, 1));
				    		g.setHeldItem(EnumHand.OFF_HAND, new ItemStack(Items.SHIELD, 1));
				    		g.tasks.addTask(0, new EntityAIDespawn(g));
							world.spawnEntity(g);
						}
						
						e.getNavigator().tryMoveToEntityLiving(player, 0.6);
						e.setAttackTarget(player);
						
						if ( rand.nextInt(i+1) == 0 )
						{
							i = 0;
						}
						else
						{
							i++;
						}
					}
				}

				return;
			}
			
		}
		catch (Exception e)
		{
			System.out.println("ERROR SPAWNING EntityCaravan: " + e);
			return;
		}
	}
	
	protected void spawnSentryNearPlayer( World world )
	{
		try
		{
			int range = 96;
			List<EntityPlayer> players = world.playerEntities;
			Collections.shuffle(players);
			for ( EntityPlayer player : players )
			{
				if ( player == null || player.world == null || world == null || world.provider == null || world.provider.getDimension() != 0 )
				{
					continue;
				}
				
				Province province = CivilizationUtil.getProvinceAt(world, player.chunkCoordX, player.chunkCoordZ);
				
				if ( province != null )
				{
					continue;
				}
				
				int villageCenterX = (int)player.posX;
				int villageCenterZ = (int)player.posY;
				
				int x = (rand.nextInt(range));
				int z = (rand.nextInt(range));
				
				while ( x < range/2 && z < range/2 )
				{
					x = (rand.nextInt(range));
					z = (rand.nextInt(range));
				}
				
				x *= (rand.nextInt(2)*2-1);
				z *= (rand.nextInt(2)*2-1);
				
				x += villageCenterX;
				z += villageCenterZ;
				
				BlockPos loc = new BlockPos(x,player.world.getHeight()/2,z);
				
				BlockPos banditSpawnPos = findSpawnLocationFrom(world, loc);
				
				if (banditSpawnPos == null)
				{
					continue;
				}
				
				province = CivilizationUtil.getProvinceAt(world, banditSpawnPos.getX()/16, banditSpawnPos.getZ()/16);
				
				if ( province != null )
				{
					continue;
				}
				
				List<EntityPlayer> nearbyPlayers = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(banditSpawnPos).grow(32, 32, 32));
				
				if ( nearbyPlayers.size() > 0 )
				{
					continue;
				}
				
				// =-=-=-=-=-=-=-=-=-=-=-=-=
				//			BANDIT
				// =-=-=-=-=-=-=-=-=-=-=-=-=
				
//				DifficultyInstance bandits;
//				bandits = world.getDifficultyForLocation(banditSpawnPos);
//				float m = bandits.getAdditionalDifficulty();
				if ( !world.isRemote )
				{
					if ( ToroQuestConfiguration.orcsAreNeutral || rand.nextBoolean() )
					{
						for ( int i = rand.nextInt(5)+2; i > 0; i-- )
						{
							EntitySentry e = new EntitySentry(world);
							e.despawnTimer--;
							e.setPosition(banditSpawnPos.getX() + 0.5,banditSpawnPos.getY()+0.1, banditSpawnPos.getZ() + 0.5 );
							e.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(e)), (IEntityLivingData) null);
							world.spawnEntity(e);
							e.setAttackTarget(player);
							if ( rand.nextBoolean() ) e.setRaidLocation(villageCenterX, villageCenterZ);
						}
					}
					else
					{
						
						for ( int i = rand.nextInt(5)+2; i > 0; i-- )
						{
							EntityOrc e = new EntityOrc(world);
							e.despawnTimer--;
							e.setPosition(banditSpawnPos.getX() + 0.5,banditSpawnPos.getY()+0.1, banditSpawnPos.getZ() + 0.5 );
							e.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(e)), (IEntityLivingData) null);
							world.spawnEntity(e);
							e.setAttackTarget(player);
							if ( rand.nextBoolean() ) e.setRaidLocation(villageCenterX, villageCenterZ);
						}
					}
				}
				return;
			}
		}
		catch (Exception e)
		{
			System.out.println("ERROR SPAWNING EntityBandit: " + e);
			return;
		}
	}
	
	protected void spawnWolves( World world )
	{

		try
		{
			int range = 176;
			List<EntityPlayer> players= world.playerEntities;
			Collections.shuffle(players);
			for ( EntityPlayer player : players )
			{
				if ( player == null || player.world == null || world == null || world.provider == null || world.provider.getDimension() != 0 )
				{
					continue;
				}
				
				Province 			     province = CivilizationUtil.getProvinceAt(world, player.chunkCoordX,   player.chunkCoordZ);
				if ( province == null ) {province = CivilizationUtil.getProvinceAt(world, player.chunkCoordX+2, player.chunkCoordZ+2);}
				if ( province == null ) {province = CivilizationUtil.getProvinceAt(world, player.chunkCoordX+2, player.chunkCoordZ-2);}
				if ( province == null ) {province = CivilizationUtil.getProvinceAt(world, player.chunkCoordX-2, player.chunkCoordZ+2);}
				if ( province == null ) {province = CivilizationUtil.getProvinceAt(world, player.chunkCoordX-2, player.chunkCoordZ-2);}
				if ( province == null )
				{
					continue;
				}
				
				int villageCenterX = province.getCenterX();
				int villageCenterZ = province.getCenterZ();
				
				int x = (rand.nextInt(range));
				int z = (rand.nextInt(range));
				
				while ( x < range/2 && z < range/2 )
				{
					x = (rand.nextInt(range));
					z = (rand.nextInt(range));
				}

				x *= (rand.nextInt(2)*2-1);
				z *= (rand.nextInt(2)*2-1);
				
				x += villageCenterX;
				z += villageCenterZ;
				
				BlockPos loc = new BlockPos(x,player.world.getHeight()/2,z);
				
				BlockPos banditSpawnPos = findSpawnLocationFrom(world, loc);
				
				if (banditSpawnPos == null)
				{
					continue;
				}
				
				List<EntityPlayer> nearbyPlayers = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(banditSpawnPos).grow(48, 32, 48));
				if ( nearbyPlayers.size() > 0 )
				{
					continue;
				}
				
				// =-=-=-=-=-=-=-=-=-=-=-=-=
				//			WOLVES
				// =-=-=-=-=-=-=-=-=-=-=-=-=
				
//				List<EntityWolfRaider> bandits = world.getEntitiesWithinAABB(EntityWolfRaider.class, new AxisAlignedBB(new BlockPos(villageCenterX,128,villageCenterZ)).grow(512, 128, 512));
//				int banditCount = bandits.size();
//				for ( EntityWolfRaider bandit : bandits )
//				{
//					if ( --bandit.wolfDespawnTimer < 0 )
//					{
//						bandit.setDead();
//					}
//				}

				// if ( world.getDifficulty() == EnumDifficulty.EASY ) return;
				
				int rep = Math.abs(PlayerCivilizationCapabilityImpl.get(player).getReputation(province.civilization));
				int count = ( rand.nextInt( MathHelper.clamp( (int)((Math.abs(rep)+900)/300), 3, 12) )
										  + MathHelper.clamp( (int)((Math.abs(rep)+750)/300), 2, 8) );
				
//				int rep = Math.abs(PlayerCivilizationCapabilityImpl.get(player).getReputation(province.civilization));
//				int count = ( rand.nextInt( MathHelper.clamp( (int)((Math.abs(rep)+450)/150), 3, 24) )
//										  + MathHelper.clamp( (int)((Math.abs(rep)+375)/150), 2, 20) );
				

				if ( !world.isRemote )
				{
					for ( int i = count; i > 0; i-- )
					{
						EntityWolfRaider e = new EntityWolfRaider(world);
						e.setPosition(banditSpawnPos.getX() + 0.5,banditSpawnPos.getY()+0.1, banditSpawnPos.getZ() + 0.5 );
						e.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(e)), (IEntityLivingData) null);
						world.spawnEntity(e);
						e.despawnTimer--;
						e.setAttackTarget(player);
						e.setRaidLocation(villageCenterX, villageCenterZ); // TODO
					}
				}
				
				return;
			}
		}
		catch (Exception e)
		{
			System.out.println("ERROR SPAWNING EntityWolfRaider: " + e);
			return;
		}
	}
	
	@SubscribeEvent
	public void onGuiOpen(RightClickBlock event)
	{
		Vec3d vec = event.getHitVec();
		
		if ( vec == null )
		{
			return;
		}
		
		BlockPos pos = new BlockPos(vec.x, vec.y, vec.z);
		
		Block block = event.getWorld().getBlockState(pos).getBlock();
		
		if ( block == null )
		{
			return;
		}
		if ( !(block == Blocks.TRAPPED_CHEST) )
		{
			return;
		}
		EntityPlayer player = event.getEntityPlayer();
		if ( player == null )
		{
			return;
		}
		Province province = CivilizationUtil.getProvinceAt(event.getWorld(), player.chunkCoordX, player.chunkCoordZ);
		
		if ( province == null )
		{
			return;
		}
		
		int rep = PlayerCivilizationCapabilityImpl.get(player).getReputation(province.civilization);
		
		if ( rep >= 250 || (!ToroQuestConfiguration.loseReputationForBlockGrief) )
		{
			return;
		}
		
		// int reportChance = (MathHelper.clamp(rep, 150, 500))/150;
		
		boolean witnessed = villagersReportCrime( event.getWorld(), player );
		
		List<EntityToroNpc> help = player.world.getEntitiesWithinAABB(EntityToroNpc.class, new AxisAlignedBB(player.getPosition()).grow(20, 10, 20), new Predicate<EntityToroNpc>()
		{
			public boolean apply(@Nullable EntityToroNpc entity)
			{
				return true;
			}
		});
		Collections.shuffle(help);
		boolean flag = false;
		for (EntityToroNpc entity : help)
		{
			if ( !entity.canEntityBeSeen(player) )
			{
				continue;
			}
			
			entity.setAttackTarget(player);
			entity.setAnnoyed();
			
			if ( !flag )
			{
				flag = true;
				switch (rand.nextInt(9) )
				{
					case 0: {chat(player, entity, "§r: Theif!");break;}
					case 1:	{chat(player, entity, "§r: Stop right there, criminal!");break;}
					case 2:	{chat(player, entity, "§r: Keep your hands to yourself, theif!");break;} 
					case 3:	{chat(player, entity, "§r: You really shouldn't have done that...");break;}
					case 4:	{chat(player, entity, "§r: What are you doing!?");break;}
					case 5:	{chat(player, entity, "§r: That chest isn't yours to take from!");break;}
					case 6:	{chat(player, entity, "§r: Hah! Caught you!");break;}
					case 7: {chat(player, entity, "§r: That isn't yours, theif!");break;}
					case 8:	{chat(player, entity, "§r: Criminal scum!");break;}
				}
			}
		}
		if ( flag || witnessed ) adjustPlayerRep( player, province.civilization, -ToroQuestConfiguration.expensiveRepLoss );
	}
	/*
	 	formula to generate a random , but controlled, spawn distance
	 */
	public static int randomSpawnDistance(int num)
	{ 
		Random rand = new Random();
		int result = rand.nextInt(num/2) + num;
		if (rand.nextBoolean())
		{
			result = -result;
		}
		return result;
	}
	
	// spawn a fugitive at a location FOR QUEST
//	public static void spawnFugitive(World world, EntityPlayer player, Province province)
//	{
//		if ( world.isRemote ) return;
//
//		int spawnDistance = 60;
//
//		try
//		{
//				if ( player == null || world == null || world.provider == null || world.provider.getDimension() != 0)
//				{
//					return;
//				}
//
//				//BlockPos randomNearbySpot = player.getPosition().add(32, 0, 32);
//				
//				//Province province = CivilizationUtil.getProvinceAt(world, randomNearbySpot.getX() / 16, randomNearbySpot.getZ() / 16);
//				// could check outside province too
//				//Province province = CivilizationUtil.getProvinceAt(world, player.chunkCoordX,   player.chunkCoordZ);
//				if ( province == null )
//				{
//					return;
//				}
//				int villageCenterX = province.getCenterX();
//				int villageCenterZ = province.getCenterZ();
//
//				BlockPos loc = new BlockPos(villageCenterX + (rand.nextInt(spawnDistance)*(rand.nextInt(2)*2-1)),player.world.getHeight()/2,villageCenterZ + (rand.nextInt(spawnDistance)*(rand.nextInt(2)*2-1)));
//				BlockPos spawnPos = findSpawnLocationFrom(world, loc);
//				
//				if (spawnPos == null)
//				{
//					return;
//				}
//				
//				List<EntityPlayer> nearbyPlayers = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(spawnPos).grow(20, 10, 20));
//				if ( nearbyPlayers.size() > 0 )
//				{
//					loc = new BlockPos(villageCenterX + (rand.nextInt(spawnDistance)*(rand.nextInt(2)*2-1)),player.world.getHeight()/2,villageCenterZ + (rand.nextInt(spawnDistance)*(rand.nextInt(2)*2-1)));
//					spawnPos = findSpawnLocationFrom(world, loc);
//					
//					if (spawnPos == null)
//					{
//						return;
//					}
//				}
//				
//				
//				EntityFugitive e = new EntityFugitive(world);
//				e.setPosition(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
//				e.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(e)), (IEntityLivingData) null);
//				world.spawnEntity(e);
//				e.setAttackTarget(player);
//		}
//		catch (Exception e)
//		{
//			System.out.println("ERROR SPAWNING EntityFugitive: " + e);
//			return;
//		}
//	}
	
	// TODO
	// spawn a fugitive at a random province RANDOM SPAWN
	private void spawnFugitive(World world)
	{	
		
		try
		{
			int spawnDistance = 60;
			List<EntityPlayer> players= world.playerEntities;
			Collections.shuffle(players);
			
			for ( EntityPlayer player : players )
			{
				
				if ( player == null || world == null || world.provider == null || world.provider.getDimension() != 0 )
				{
					continue;
				}
				

				//BlockPos randomNearbySpot = player.getPosition().add(32, 0, 32);
				
				//Province province = CivilizationUtil.getProvinceAt(world, randomNearbySpot.getX() / 16, randomNearbySpot.getZ() / 16);
				// could check outside province too
				Province province = CivilizationUtil.getProvinceAt(world, player.chunkCoordX, player.chunkCoordZ);
				if ( province == null )
				{
					continue;
				}
				int villageCenterX = province.getCenterX();
				int villageCenterZ = province.getCenterZ();

				BlockPos loc = new BlockPos(villageCenterX + (rand.nextInt(spawnDistance)*(rand.nextInt(2)*2-1)),player.world.getHeight()/2,villageCenterZ + (rand.nextInt(spawnDistance)*(rand.nextInt(2)*2-1)) );
				BlockPos spawnPos = findSpawnLocationFrom(world, loc);
				
				if (spawnPos == null)
				{
					continue;
				}
				
				List<EntityPlayer> nearbyPlayers = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(spawnPos).grow(16, 8, 16));
				
				if ( nearbyPlayers.size() > 0 )
				{
					loc = new BlockPos(villageCenterX + (rand.nextInt(spawnDistance)*(rand.nextInt(2)*2-1)),player.world.getHeight()/2,villageCenterZ + (rand.nextInt(spawnDistance)*(rand.nextInt(2)*2-1)));
					spawnPos = findSpawnLocationFrom(world, loc);
					
					if (spawnPos == null)
					{
						continue;
					}
				}
				

				int localFugitiveCount = world.getEntitiesWithinAABB(EntityFugitive.class, new AxisAlignedBB(spawnPos).grow(128, 128, 128)).size();

				if ( localFugitiveCount > 3 )
				{
					continue;
				}
				
				if ( localFugitiveCount >= 3 && rand.nextBoolean() )
				{
					continue;
				}

				if ( !world.isRemote )
				{
					EntityFugitive e = new EntityFugitive(world);
					e.setPosition(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
					world.spawnEntity(e);
					e.setAttackTarget(player);
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("ERROR SPAWNING EntityFugitive: " + e);
			return;
		}
	}

	protected void spawnZombies( World world )
	{

		try
		{
			int range = 176;
			List<EntityPlayer> players= world.playerEntities;
			Collections.shuffle(players);
			for ( EntityPlayer player : players )
			{
				if ( player == null || player.world == null || world == null || world.provider == null || world.provider.getDimension() != 0 )
				{
					continue;
				}
				
				Province 			     province = CivilizationUtil.getProvinceAt(world, player.chunkCoordX,   player.chunkCoordZ);
				if ( province == null ) {province = CivilizationUtil.getProvinceAt(world, player.chunkCoordX+2, player.chunkCoordZ+2);}
				if ( province == null ) {province = CivilizationUtil.getProvinceAt(world, player.chunkCoordX+2, player.chunkCoordZ-2);}
				if ( province == null ) {province = CivilizationUtil.getProvinceAt(world, player.chunkCoordX-2, player.chunkCoordZ+2);}
				if ( province == null ) {province = CivilizationUtil.getProvinceAt(world, player.chunkCoordX-2, player.chunkCoordZ-2);}
				if ( province == null )
				{
					continue;
				}
				
				int villageCenterX = province.getCenterX();
				int villageCenterZ = province.getCenterZ();
				
				int x = (rand.nextInt(range));
				int z = (rand.nextInt(range));
				
				while ( x < range/2 && z < range/2 )
				{
					x = (rand.nextInt(range));
					z = (rand.nextInt(range));
				}

				x *= (rand.nextInt(2)*2-1);
				z *= (rand.nextInt(2)*2-1);
				
				x += villageCenterX;
				z += villageCenterZ;
				
				BlockPos loc = new BlockPos(x,player.world.getHeight()/2,z);
				
				BlockPos banditSpawnPos = findSpawnLocationFrom(world, loc);
				
				if (banditSpawnPos == null)
				{
					continue;
				}
				
				List<EntityPlayer> nearbyPlayers = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(banditSpawnPos).grow(32, 16, 32));
				if ( nearbyPlayers.size() > 0 )
				{
					continue;
				}
				
				// =-=-=-=-=-=-=-=-=-=-=-=-=
				//			ZOMBIE
				// =-=-=-=-=-=-=-=-=-=-=-=-=


				int rep = Math.abs(PlayerCivilizationCapabilityImpl.get(player).getReputation(province.civilization));
				int count = ( rand.nextInt( MathHelper.clamp( (int)((Math.abs(rep)+600)/200), 4, 16) )
										  + MathHelper.clamp( (int)((Math.abs(rep)+500)/200), 4, 10) );
				

				if ( !world.isRemote )
				{
					for ( int i = count; i > 0; i-- )
					{
						if ( rand.nextInt(3) == 0)
						{
							EntityZombieVillagerRaider e = new EntityZombieVillagerRaider(world);
							e.setPosition(banditSpawnPos.getX() + 0.5,banditSpawnPos.getY()+0.1, banditSpawnPos.getZ() + 0.5 );
							e.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(e)), (IEntityLivingData) null);
							world.spawnEntity(e);
							e.setRaidLocation(villageCenterX, villageCenterZ);
						}
						else
						{
							EntityZombieRaider e = new EntityZombieRaider(world);
							e.setPosition(banditSpawnPos.getX() + 0.5,banditSpawnPos.getY()+0.1, banditSpawnPos.getZ() + 0.5 );
							e.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(e)), (IEntityLivingData) null);
							world.spawnEntity(e);
							e.setAttackTarget(player);
							e.setRaidLocation(villageCenterX, villageCenterZ);
						}
					}
				}

				return;
			}
		}
		catch (Exception e)
		{
			System.out.println("ERROR SPAWNING EntityZombieRaider: " + e);
			return;
		}
	}
	
	
	

	
	
	
	public static BlockPos findSpawnLocationFrom(World world, BlockPos from) // TODO
	{
		boolean[] airSpace = { false, false };
		IBlockState blockState;
		for (int j = 0; j < 16; j++)
		{
			BlockPos spawnPos = from.add( (rand.nextInt(8)+j)*rand.nextInt(2)*2-1, 0, (rand.nextInt(8)+j)*rand.nextInt(2)*2-1 );
			
			for (int i = 0; i < 80; i++)
			{
				blockState = world.getBlockState(spawnPos);
	
				if (isAir(blockState))
				{
					if (airSpace[0])
					{
						airSpace[1] = true;
					}
					else
					{
						airSpace[0] = true;
					}
				}
				else if ( isStructureBlock(blockState) || isLiquid(blockState) )
				{
					break;
				}
				else if ( isGroundBlock(blockState) )
				{
					if (airSpace[0] && airSpace[1])
					{
						return spawnPos.up();
					}
					else
					{
						airSpace[0] = false;
						airSpace[1] = false;
					}
				}
				else
				{
					airSpace[0] = false;
					airSpace[1] = false;
				}
				spawnPos = spawnPos.down();
			}
		}
		return null;
	}
	
	public static BlockPos findTeleportLocationFrom(World world, BlockPos from) // TODO
	{
		boolean[] airSpace = { false, false };
		IBlockState blockState;
		for (int j = 0; j < 16; j++)
		{
			BlockPos spawnPos = from.add( (rand.nextInt(44)+j)*rand.nextInt(2)*2-1, 0, (rand.nextInt(44)+j)*rand.nextInt(2)*2-1 );
			for (int i = 0; i < 80; i++)
			{
				blockState = world.getBlockState(spawnPos);
	
				if (isAir(blockState))
				{
					if (airSpace[0])
					{
						airSpace[1] = true;
					}
					else
					{
						airSpace[0] = true;
					}
				}
				else if ( isStructureBlock(blockState) || isLiquid(blockState) )
				{
					break;
				}
				else if ( isGroundBlock(blockState) )
				{
					if (airSpace[0] && airSpace[1])
					{
						return spawnPos.up();
					}
					else
					{
						airSpace[0] = false;
						airSpace[1] = false;
					}
				}
				else
				{
					airSpace[0] = false;
					airSpace[1] = false;
				}
				spawnPos = spawnPos.down();
			}
		}
		return null;
	}
	
	public static BlockPos findSpawnSurface( World world, BlockPos pos )
	{
		pos = pos.up(64);
		IBlockState blockState;
		int yOffset = 64;
		boolean[] airSpace = { false, false };

		while ( yOffset > 0 )
		{
			blockState = world.getBlockState(pos);

			if (isAir(blockState))
			{
				if (airSpace[0])
				{
					airSpace[1] = true;
				}
				else
				{
					airSpace[0] = true;
				}
			}
			else if ( isLiquid(blockState) )
			{
				break;
			}
			else if ( !(blockState.getBlock() instanceof BlockAir) )
			{
				if (airSpace[0] && airSpace[1])
				{
					return pos.up();
				}
				else
				{
					airSpace[0] = false;
					airSpace[1] = false;
				}
			}
			else
			{
				airSpace[0] = false;
				airSpace[1] = false;
			}
			pos = pos.down();
			yOffset--;
		}
		return null;
	}

	
	/*	public static BlockPos findSpawnLocationFrom(World world, BlockPos from)
	{
		BlockPos spawnPos = from;
		boolean[] airSpace = { false, false };
		IBlockState blockState;
		int directionx = rand.nextInt(2)*2-1;
		int directionz = rand.nextInt(2)*2-1;
		spawnPos = spawnPos.add( (rand.nextInt(32)+16)*directionx,0 ,(rand.nextInt(32)+16)*directionz );
		for (int j = 0; j < 8; j++)
		{
			if ( rand.nextBoolean() )
			{
				spawnPos = spawnPos.add( (j*rand.nextInt(2)+1)*(-directionx),0 ,0 );
			}
			else
			{
				spawnPos = spawnPos.add( 0,0,(j*rand.nextInt(2)+1)*(-directionz) );
			}
			for (int i = 0; i < 80; i++)
			{
				blockState = world.getBlockState(spawnPos);
	
				if (isAir(blockState))
				{
					if (airSpace[0])
					{
						airSpace[1] = true;
					}
					else
					{
						airSpace[0] = true;
					}
				}
				else if ( isStructureBlock(blockState) && !isLiquid(blockState) )
				{
					continue;
				}
				else if ( isGroundBlock(blockState) )
				{
					if (airSpace[0] && airSpace[1])
					{
						return spawnPos.up();
					}
					else
					{
						airSpace[0] = false;
						airSpace[1] = false;
					}
				}
				else
				{
					airSpace[0] = false;
					airSpace[1] = false;
				}
				spawnPos = spawnPos.down();
			}
		}
		return null;
	}*/
	
	protected static boolean isLiquid(IBlockState blockState)
	{
		return blockState.getBlock() instanceof BlockLiquid;
	}

	public static boolean isGroundBlock(IBlockState blockState)
	{
		if (blockState.getBlock() instanceof BlockLeaves || blockState.getBlock() instanceof BlockLog || blockState.getBlock() instanceof BlockBush)
		{
			return false;
		}
		return blockState.isOpaqueCube();
	}
	
	public static boolean isAir(IBlockState blockState)
	{
		return blockState.getBlock() == Blocks.AIR;
	}
	
	public static boolean isStructureBlock(IBlockState blockState)
	{
		if ( blockState.getBlock() instanceof BlockStairs || blockState.getBlock() instanceof BlockColored || blockState.getBlock().getDefaultState() == Blocks.CONCRETE.getDefaultState() || blockState.getBlock().getDefaultState() == Blocks.STONEBRICK.getDefaultState() || blockState.getBlock() instanceof BlockPlanks || blockState.getBlock() instanceof BlockFence || blockState.getBlock() instanceof BlockMagma || blockState.getBlock() instanceof BlockSlab )
		{
			return true;
		}
		return false;
	}
	
	private static final Item[] STOLEN_ITEMS =
	{
		Items.GOLDEN_PICKAXE,
		Items.GOLDEN_AXE,
		Items.GOLDEN_SWORD,
		Items.GOLDEN_HOE,
		Items.BOOK
	};

	public static ItemStack randomStolenItem(World world, Province province)
	{
		if ( world == null )
		{
			return null;
		}
		
		ItemStack stolenItem = new ItemStack(STOLEN_ITEMS[rand.nextInt(STOLEN_ITEMS.length)]);
		
		if ( stolenItem.getItem() == null )
		{
			return null;
		}
		
		CivilizationType civ = null;
		
		if ( province == null )
		{
			province = QuestBase.chooseRandomProvince(null, world, false);
			
			if ( province != null )
			{
				civ = province.civilization;
			}
			else
			{
				return null;
			}
		}
		else
		{
			civ = province.civilization;
		}
		
		if ( civ == null )
		{
			return null;
		}
		
		if (!stolenItem.hasTagCompound())
		{
			stolenItem.setTagCompound(new NBTTagCompound());
		}

		String civName = CivilizationType.civServerName(civ.getCivName());
		stolenItem.getTagCompound().setString("civilizationName", civ.name());
		stolenItem.getTagCompound().setBoolean("isStolen", true);
		stolenItem.setStackDisplayName("§6Lost Heirloom of House " + civName);
		stolenItem.addEnchantment(Enchantment.getEnchantmentByID(-1), 0);
		return stolenItem;
	}

//	public static void setProvince(ItemStack stolenItem, World world, Province province)
//	{
//		
//	}
	
	private void chat(EntityPlayer player, EntityToroNpc entity, String text)
	{
		if ( player.world.isRemote )
		{
			return;
		}
		player.sendMessage(new TextComponentString( "§l" + entity.getName() + "§r: " + text));
        entity.playSound( SoundEvents.VINDICATION_ILLAGER_AMBIENT, 0.9F, 0.9F );
	}

}
