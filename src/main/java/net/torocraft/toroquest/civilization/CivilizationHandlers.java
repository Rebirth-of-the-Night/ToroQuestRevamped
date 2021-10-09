


					// reputation level for when you kill an entity or destroy a block! //


package net.torocraft.toroquest.civilization;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockBanner;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.BlockClay;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockDoubleStoneSlab;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockGravel;
import net.minecraft.block.BlockLadder;
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
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
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
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.server.command.TextComponentHelper;
import net.torocraft.toroquest.EventHandlers.SyncTask;
import net.torocraft.toroquest.ToroQuest;
import net.torocraft.toroquest.civilization.player.PlayerCivilizationCapability;
import net.torocraft.toroquest.civilization.player.PlayerCivilizationCapabilityImpl;
import net.torocraft.toroquest.civilization.quests.QuestBase;
import net.torocraft.toroquest.civilization.quests.QuestBreed;
import net.torocraft.toroquest.civilization.quests.QuestFarm;
import net.torocraft.toroquest.civilization.quests.QuestMine;
import net.torocraft.toroquest.civilization.quests.util.QuestData;
import net.torocraft.toroquest.config.ToroQuestConfiguration;
import net.torocraft.toroquest.entities.EntityAdventurer;
import net.torocraft.toroquest.entities.EntityCaravan;
import net.torocraft.toroquest.entities.EntityFugitive;
import net.torocraft.toroquest.entities.EntityGuard;
import net.torocraft.toroquest.entities.EntityOrc;
import net.torocraft.toroquest.entities.EntitySentry;
import net.torocraft.toroquest.entities.EntityToroMob;
import net.torocraft.toroquest.entities.EntityToroNpc;
import net.torocraft.toroquest.entities.EntityToroVillager;
import net.torocraft.toroquest.entities.EntityVillageLord;
import net.torocraft.toroquest.entities.EntityWolfRaider;
import net.torocraft.toroquest.entities.EntityZombieRaider;
import net.torocraft.toroquest.entities.EntityZombieVillagerRaider;
import net.torocraft.toroquest.entities.ai.EntityAIDespawnGuard;
import net.torocraft.toroquest.entities.ai.EntityAIRaid;
import net.torocraft.toroquest.util.TaskRunner;

@SuppressWarnings("deprecation")
public class CivilizationHandlers
{
	protected static Random rand = new Random();
	public static int SPAWN_HEIGHT = ToroQuestConfiguration.spawnHeight;
	
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
		if (event.getEntityPlayer().getEntityWorld().isRemote)
		{
			return;
		}
		PlayerCivilizationCapability cap = PlayerCivilizationCapabilityImpl.get(event.getEntityPlayer());
		if (cap == null)
		{
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
		
		if (cap == null)
		{
			return;
		}

		NBTTagCompound c = event.getEntityPlayer().getEntityData().getCompoundTag(ToroQuest.MODID + ".playerCivilization");

		if (c == null)
		{
			// System.out.println("******************Missing civ data on load");
		}
		else
		{
			// System.out.println("LOAD: " + c.toString());
		}

		cap.readNBT(c);
	}

	@SubscribeEvent
	public void onEntityLoad(AttachCapabilitiesEvent<Entity> event)
	{
		if (!(event.getObject() instanceof EntityPlayer))
		{
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

	public static class PlayerCivilizationCapabilityProvider implements ICapabilityProvider
	{

		@CapabilityInject(PlayerCivilizationCapability.class)
		public static final Capability<PlayerCivilizationCapability> CAP = null;

		private PlayerCivilizationCapability instance;

		public PlayerCivilizationCapabilityProvider(EntityPlayer player)
		{
			instance = new PlayerCivilizationCapabilityImpl(player);
		}

		@Override
		public boolean hasCapability(Capability<?> capability, EnumFacing facing)
		{
			return capability == CAP;
		}

		@Override
		public <T> T getCapability(Capability<T> capability, EnumFacing facing)
		{
			if (CAP != null && capability == CAP)
			{
				return PlayerCivilizationCapabilityImpl.INSTANCE.cast(instance);
			}
			return null;
		}
	}

	public static void adjustPlayerRep(EntityPlayer player, int chunkX, int chunkZ, int value)
	{
		if ( player == null || player.world.isRemote|| player.dimension != 0 )
		{
			return;
		}
		
		Province province = CivilizationUtil.getProvinceAt(player.getEntityWorld(), chunkX, chunkZ);
		
		if ( province == null )
		{
			return;
		}
		
		adjustPlayerRep(player, province.civilization, value);
	}
	
	
	
	public static void adjustPlayerRep(EntityPlayer player, CivilizationType civ, int value)
	{
		if ( player == null || player.world.isRemote || civ == null || player.dimension != 0 )
		{
			return;
		}
		if ( value < 0 )
		{
			//if ( !player.world.isRemote ) // SERVER
			{
				//TextComponentHelper.createComponentTranslation(player, "text.toroquest.crime_reported", new Object[0]);
				player.sendStatusMessage(TextComponentHelper.createComponentTranslation(player, "text.toroquest.crime_reported", new Object[0]), true);
				//player.sendStatusMessage( new TextComponentString(I18n.format("text.toroquest.crime_reported")), true);
			}
		}
		
		int startRep = PlayerCivilizationCapabilityImpl.get(player).getReputation(civ);
		int afterRep = startRep + value;
		
		// v 1500
		
		
		// s 2000
				
		// a 3500
		
		//  1500 + 3000 - 3500 = 1000
		
		if ( afterRep < -3000 )
		{
			PlayerCivilizationCapabilityImpl.get(player).setReputation(civ, -3000);
		}
		else
		{
			PlayerCivilizationCapabilityImpl.get(player).adjustReputation(civ, value);
		}
		
		try
		{
			//if ( !player.world.isRemote )
			{
				String message = repLevelMessage(player,civ,startRep,afterRep);

				if ( message != null )
				{
					player.sendMessage(new TextComponentString(message));
				}
			}
		} catch ( Exception e ) {}
	}
	
	public static void reportCrimeRep(EntityPlayer player, Province province, int value)
	{
		if ( player == null || player.world.isRemote || province == null || province.civilization == null || player.dimension != 0 )
		{
			return;
		}
		
		//if ( !player.world.isRemote )
		{
			player.sendStatusMessage(TextComponentHelper.createComponentTranslation(player, "text.toroquest.crime_reported", new Object[0]), true);

			//player.sendStatusMessage( new TextComponentString(I18n.format("text.toroquest.crime_reported")), true);
		}
		
		CivilizationType civ = province.getCiv();
		
//		if ( province.getLord() != null )
//		{
//			province.getLord().setAnnoyed(player);
//		}
		
		int startRep = PlayerCivilizationCapabilityImpl.get(player).getReputation(civ);
		int afterRep = startRep + value;
		
		// v 1500
		
		
		// s 2000
				
		// a 3500
		
		//  1500 + 3000 - 300 = 1000
		
		if ( afterRep < -3000 )
		{
			PlayerCivilizationCapabilityImpl.get(player).setReputation(civ, -3000);
		}
		else
		{
			PlayerCivilizationCapabilityImpl.get(player).adjustReputation(civ, value);
		}
	}

	@SubscribeEvent
	public void checkKillInCivilization(LivingDeathEvent event)
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
			List<EntityCaravan> caravans = victim.getEntityWorld().getEntitiesWithinAABB(EntityCaravan.class, victim.getEntityBoundingBox().grow(16, 16, 16));
			for (EntityCaravan caravan : caravans)
			{
				if ( ((EntityMule)victim).getLeashHolder() == caravan )
				{
					((EntityToroVillager)caravan).setMurder( (EntityPlayer)e );
				}
			}
		}
		
		Province province = PlayerCivilizationCapabilityImpl.get(player).getInCivilization();
				
		if ( province == null || province.civilization == null )
		{
			return;
		}
		
		if ( victim instanceof IMob || victim instanceof EntityMob )
		{
			adjustPlayerRep( player, province.civilization, ToroQuestConfiguration.killMobRepGain );
		}		
		else if ( victim instanceof EntityVillager || victim instanceof EntityGuard )
	    {
			World world = victim.world;
			
			reportCrimeRep( player, province, -100 );
			
			List<EntityToroNpc> guards = world.getEntitiesWithinAABB(EntityToroNpc.class, new AxisAlignedBB(player.getPosition()).grow(20, 16, 20), new Predicate<EntityToroNpc>()
			{
				public boolean apply(@Nullable EntityToroNpc entity)
				{
					return true;
				}
			});

			for (EntityToroNpc guard : guards)
			{
				guard.setMurder( player );
				guard.setAttackTarget(player);
			}
			
			List<EntityToroVillager> villagers = world.getEntitiesWithinAABB(EntityToroVillager.class, new AxisAlignedBB(player.getPosition()).grow(20, 16, 20), new Predicate<EntityToroVillager>()
			{
				public boolean apply(@Nullable EntityToroVillager entity)
				{
					return true;
				}
			});

			for (EntityToroVillager villager : villagers)
			{
				villager.setMurder( player );
			}
	    }
		
		int rep = PlayerCivilizationCapabilityImpl.get(player).getReputation(province.civilization);

		if ( rep >= 100 || (!ToroQuestConfiguration.loseReputationForAnimalGrief) )
		{
			return;
		}
		
		if ( victim instanceof EntityChicken || (victim instanceof EntityHorse && ( !((EntityHorse)victim).isTame() && !((EntityHorse)victim).isHorseSaddled()) ) || (victim instanceof EntityDonkey && ( !((EntityDonkey)victim).isTame() || !((EntityDonkey)victim).isHorseSaddled()) ) || victim instanceof EntityPig || victim instanceof EntitySheep || victim instanceof EntityCow || victim instanceof EntityMule )
		{
			
			boolean witnessed = villagersReportCrime( player.getEntityWorld(), player );

			List<EntityToroNpc> help = player.world.getEntitiesWithinAABB(EntityToroNpc.class, new AxisAlignedBB(player.getPosition()).grow(16, 12, 16), new Predicate<EntityToroNpc>()
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
				
				witnessed = true;
				entity.getNavigator().tryMoveToEntityLiving(player, 0.6D);
				
				entity.setAnnoyed(player);
				entity.setAttackTarget(player);

				if ( !flag )
				{
					flag = true;
					entity.chat(entity, player, "butcher", null);
				}
			}
			if ( witnessed ) reportCrimeRep( player, province, -ToroQuestConfiguration.murderLivestockRepLoss );
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
		
		if ( after == ReputationLevel.FRIENDLY ) 		// 50
		{
			s = "§eYou are now §oFriendly§e with §lHouse " + CivilizationType.configHouseName(civ) +
			"§e. You may harvest crops without losing reputation!";
		}
		else if ( after == ReputationLevel.HONORED )	// 100
		{
			s = "§eYou are now §oHonored§e with §lHouse " + CivilizationType.configHouseName(civ) +
			"§e. You may butcher livestock without losing reputation!";
		}
		else if ( after == ReputationLevel.REVERED )	// 250
		{
			s = "§eYou are now §oRevered§e with §lHouse " + CivilizationType.configHouseName(civ) +
			"§e. You may take from chests, fire spread, and destroy building blocks without losing reputation!";
		}
		else if ( after == ReputationLevel.EXALTED )	// 500
		{
			s = "§eYou are now §oExalted§e with §lHouse " + CivilizationType.configHouseName(civ) +
			"§e. You are reputable enough to rename provinces under the rule of this civilization!";
		}
		else if ( after == ReputationLevel.CHAMPION )	// 1000
		{
			s = "§eYou are now a §oChampion§e of §lHouse " + CivilizationType.configHouseName(civ) +
			"§e. You have unlocked dangerous and rewarding Legendary Quests!";
		}
		else if ( after == ReputationLevel.HERO )		// 2000
		{
			s = "§eYou are now a §oHero§e of §lHouse " + CivilizationType.configHouseName(civ) + 
					"§e. Quests rewards are doubled!";
		}
		else if ( after == ReputationLevel.LEGEND )		// 3000
		{
			s = "§eYou are now a §oLegend§e of §lHouse " + CivilizationType.configHouseName(civ) + 
			"§e. You have achieved the highest reputation rank, and quests will grant experience in addition to their rewards!";

		}
		return s;
	}

	// BREED
	@SubscribeEvent
	public void breed(BabyEntitySpawnEvent event)
	{
		EntityPlayer e = event.getCausedByPlayer();
		
		if ( e == null || e.world.isRemote )
		{
			return;
		}
		
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
			if ( rand.nextInt(3) == 0 ) adjustPlayerRep(playerA, event.getParentA().chunkCoordX, event.getParentA().chunkCoordZ, 1);
			//if ( !e.world.isRemote ) 
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
			if ( rand.nextInt(3) == 0 ) adjustPlayerRep(playerB, event.getParentB().chunkCoordX, event.getParentB().chunkCoordZ, 1);
			//if ( !e.world.isRemote ) 
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
	
//	public void chat( EntityLiving e, EntityPlayer player, String message )
//	{
//		if ( ToroQuestConfiguration.guardsHaveDialogue )
//		{
//			e.getLookHelper().setLookPositionWithEntity(player, 30.0F, 30.0F);
//			e.faceEntity(player, 30.0F, 30.0F);
//			if ( player.world.isRemote )
//			{
//				return;
//			}
//			player.sendMessage(new TextComponentString( "§l" + e.getName() + "§r: " + (I18n.format(message)).replace("@p", player.getDisplayNameString())));
//		    e.playSound( SoundEvents.VINDICATION_ILLAGER_AMBIENT, 1.0F, 0.9F + rand.nextFloat()/5.0F );
//		}
//	}
	
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
		
		final RayTraceResult target = event.getTarget();
		if ( target == null || target.typeOfHit != RayTraceResult.Type.BLOCK ) return;
		BlockPos blockPos = new BlockPos(target.hitVec.x, target.hitVec.y, target.hitVec.z);
		
		if ( event.getEmptyBucket() == null ) return;
		
		// reputation
		Province province = PlayerCivilizationCapabilityImpl.get(player).getInCivilization();
		
		if ( province == null || province.civilization == null )
		{
			return;
		}
		
		int rep = PlayerCivilizationCapabilityImpl.get(player).getReputation(province.civilization);

		if ( event.getEmptyBucket().getUnlocalizedName().contains("Water") )
		{
			// bandits
			List<EntityToroMob> mob = event.getWorld().getEntitiesWithinAABB(EntityToroMob.class, new AxisAlignedBB(blockPos).grow(6, 6, 6), new Predicate<EntityToroMob>()
			{
				public boolean apply(@Nullable EntityToroMob entity)
				{
					return true;
				}
			});
			for (EntityToroMob m : mob)
			{
				if ( rand.nextBoolean() ) m.setAttackTarget(player);
			}
			
			// guards
			List<EntityToroNpc> guards = event.getWorld().getEntitiesWithinAABB(EntityToroNpc.class, new AxisAlignedBB(blockPos).grow(4, 4, 4), new Predicate<EntityToroNpc>()
			{
				public boolean apply(@Nullable EntityToroNpc entity)
				{
					return true;
				}
			});
			Collections.shuffle(guards);
			
			boolean flag = false;
			
			if ( rep >= 250 || (!ToroQuestConfiguration.loseReputationForBlockGrief) )
			{
				for (EntityToroNpc entity : guards)
				{
					if ( !entity.canEntityBeSeen(player) )
					{
						continue;
					}
					
//					if ( entity instanceof EntityVillageLord )
//					{
//						entity.setAnnoyed(player);
//					}
//					else if ( rand.nextBoolean() )
//					{
//						entity.setAnnoyed(player);
//					}
					
					if ( !flag )
					{
						flag = true;
						entity.chat(entity, player, "water", null);
					}
				}
			}
			else
			{
				for (EntityToroNpc entity : guards)
				{
					if ( !entity.canEntityBeSeen(player) )
					{
						continue;
					}
					
					if ( entity instanceof EntityVillageLord )
					{
						entity.setAnnoyed(player);
						((EntityVillageLord)entity).callForHelp(player);
					}
					else
					{
						if ( entity.isAnnoyed() )
						{
							if ( !entity.inCombat() )
							{
								entity.setAnnoyed(player);
								entity.setAttackTarget(player);
							}
						}
						else
						{
							entity.setAnnoyed(player);
						}
					}
					
					if ( !flag )
					{
						flag = true;
						entity.chat(entity, player, "water", null);
					}
				}
			}
			if ( flag ) reportCrimeRep( player, province, -ToroQuestConfiguration.unexpensiveRepLoss );
		}
		else if ( event.getEmptyBucket().getUnlocalizedName().contains("Lava") )
		{
			// bandits
			List<EntityToroMob> mob = event.getWorld().getEntitiesWithinAABB(EntityToroMob.class, new AxisAlignedBB(player.getPosition()).grow(6, 6, 6), new Predicate<EntityToroMob>()
			{
				public boolean apply(@Nullable EntityToroMob entity)
				{
					return true;
				}
			});
			for (EntityToroMob m : mob)
			{
				m.setAttackTarget(player);
			}
			
			// guards
			List<EntityToroNpc> help = event.getWorld().getEntitiesWithinAABB(EntityToroNpc.class, new AxisAlignedBB(player.getPosition()).grow(16, 12, 16), new Predicate<EntityToroNpc>()
			{
				public boolean apply(@Nullable EntityToroNpc entity)
				{
					return true;
				}
			});
			Collections.shuffle(help);
			
			boolean flag = false;
			boolean witnessed = false;
			
			boolean onGuard = !(event.getWorld().getEntitiesWithinAABB(EntityToroNpc.class, new AxisAlignedBB(blockPos).grow(4, 4, 4), new Predicate<EntityToroNpc>()
			{
				public boolean apply(@Nullable EntityToroNpc entity)
				{
					return true;
				}
			}).isEmpty());
			
			boolean onVillager = !(event.getWorld().getEntitiesWithinAABB(EntityVillager.class, new AxisAlignedBB(blockPos).grow(4, 4, 4), new Predicate<EntityVillager>()
			{
				public boolean apply(@Nullable EntityVillager entity)
				{
					return true;
				}
			}).isEmpty());
			
			if ( onGuard || onVillager)
			{
				witnessed = villagersReportCrime( event.getWorld(), player );
				for ( EntityToroNpc entity : help )
				{
					if ( !entity.canEntityBeSeen(player) )
					{
						continue;
					}
					entity.setAnnoyed(player);
					entity.setAttackTarget(player);
					if ( !flag )
					{
						flag = true;
						entity.chat(entity, player, "lavaonperson", null);
					}
				}
				reportCrimeRep( player, province, -ToroQuestConfiguration.lavaGriefRepLoss );
			}
			else if ( rep >= 250 || (!ToroQuestConfiguration.loseReputationForBlockGrief) )
			{
				for (EntityToroNpc entity : help)
				{
					if ( !entity.canEntityBeSeen(player) )
					{
						continue;
					}
					if ( !flag )
					{
						flag = true;
						if ( entity.actionReady() )
						{
							entity.chat(entity, player, "lavaallowed", null);
						}
					}
				}
				return;
			}
			else
			{
				witnessed = villagersReportCrime( event.getWorld(), player );

				for (EntityToroNpc entity : help)
				{
					if ( !entity.canEntityBeSeen(player) )
					{
						continue;
					}
					
					witnessed = true;
					entity.getNavigator().tryMoveToEntityLiving(player, 0.6D);
					
					entity.setAnnoyed(player);
					entity.setAttackTarget(player);
					
					if ( !flag )
					{
						flag = true;
						entity.chat(entity, player, "lavacrime", null);
					}
				}
				if ( witnessed ) reportCrimeRep( player, province, -ToroQuestConfiguration.lavaGriefRepLoss );
			}
		}
	}
	
	@SubscribeEvent
	public void fire(EntityPlaceEvent event)
	{
		if ( event.getWorld().isRemote )
		{
			return;
		}
		
		Block e = event.getState().getBlock();
		
		Entity eventEntity = event.getEntity();
		if ( eventEntity == null || !(eventEntity instanceof EntityPlayer) )
		{
			return;
		}
		
		EntityPlayer player = (EntityPlayer)eventEntity;

		if ( e instanceof BlockCrops || e instanceof BlockStem )
		{
			try
			{
				QuestFarm.INSTANCE.onFarm(player);
			}
			catch ( Exception ee )
			{
				
			}
			return;
		}
		
		if ( !(e instanceof BlockFire) && !(e instanceof BlockTNT) ) return;
		
		BlockPos blockPos = event.getPos();
		if ( blockPos == null ) return;
		
		// Bandits
		List<EntityToroMob> mob = event.getWorld().getEntitiesWithinAABB(EntityToroMob.class, new AxisAlignedBB(blockPos).grow(6, 6, 6), new Predicate<EntityToroMob>()
		{
			public boolean apply(@Nullable EntityToroMob entity)
			{
				return true;
			}
		});
		for (EntityToroMob m : mob)
		{
			m.setAttackTarget(player);
		}
		
		boolean onGuard = !(event.getWorld().getEntitiesWithinAABB(EntityToroNpc.class, new AxisAlignedBB(blockPos).grow(3, 3, 3), new Predicate<EntityToroNpc>()
		{
			public boolean apply(@Nullable EntityToroNpc entity)
			{
				return true;
			}
		}).isEmpty());
		
		boolean onVillager = !(event.getWorld().getEntitiesWithinAABB(EntityVillager.class, new AxisAlignedBB(blockPos).grow(3, 3, 3), new Predicate<EntityVillager>()
		{
			public boolean apply(@Nullable EntityVillager entity)
			{
				return true;
			}
		}).isEmpty());
							
		Province province = PlayerCivilizationCapabilityImpl.get(player).getInCivilization();
		
		if ( province == null || province.civilization == null )
		{
			return;
		}
		
		List<EntityToroNpc> help = event.getWorld().getEntitiesWithinAABB(EntityToroNpc.class, new AxisAlignedBB(player.getPosition()).grow(16, 12, 16), new Predicate<EntityToroNpc>()
		{
			public boolean apply(@Nullable EntityToroNpc entity)
			{
				return true;
			}
		});
		Collections.shuffle(help);
		
		int rep = PlayerCivilizationCapabilityImpl.get(player).getReputation(province.civilization);
		
		boolean witnessed = false;

		boolean flag = false;
		
		if ( e instanceof BlockTNT )
		{
			for (EntityToroNpc entity : help)
			{
				if ( !entity.canEntityBeSeen(player) )
				{
					continue;
				}
				
				if ( entity.isAnnoyed() )
				{
					if ( !entity.inCombat() )
					{
						entity.setAnnoyed(player);
						entity.setAttackTarget(player);
					}
				}
				else
				{
					entity.setAnnoyed(player);
				}
								
				if ( !flag )
				{
					flag = true;
					entity.chat(entity, player, "explosives", null);
				}
			}
			witnessed = villagersReportCrime( event.getWorld(), player );
		}
		else if ( onGuard || onVillager )
		{
			for (EntityToroNpc entity : help)
			{
				entity.setAnnoyed(player);
				if ( !entity.inCombat() ) entity.setAttackTarget(player);
				
				if ( !flag )
				{
					flag = true;
					entity.chat(entity, player, "fireonperson", null);
				}
			}
			witnessed = villagersReportCrime( event.getWorld(), player );
		}
		else if ( rep < 250 && ToroQuestConfiguration.loseReputationForBlockGrief )
		{
			for (EntityToroNpc entity : help)
			{
				if ( !entity.canEntityBeSeen(player) )
				{
					continue;
				}
				
				witnessed = true;
				entity.getNavigator().tryMoveToEntityLiving(player, 0.6D);
				
				if ( entity.isAnnoyed() )
				{
					if ( !entity.inCombat() )
					{
						entity.setAnnoyed(player);
						entity.setAttackTarget(player);
					}
				}
				else
				{
					entity.setAnnoyed(player);
				}
								
				if ( !flag )
				{
					flag = true;
					entity.chat(entity, player, "firespread", null);
				}
			}
			witnessed = villagersReportCrime( event.getWorld(), player );
		}
		if ( witnessed ) reportCrimeRep( player, province, -ToroQuestConfiguration.fireGriefRepLoss );
	}
	
//	@SubscribeEvent
//	public void onFarm(PlaceEvent event)
//	{
//		if ( event.getWorld().isRemote || event.getPlayer() == null )
//		{
//			return;
//		}
//		
//		if ( event.getPlacedBlock() instanceof BlockCrops || event.getPlacedBlock() instanceof BlockStem )
//		{
//			try
//			{
//				QuestFarm.INSTANCE.onFarm(event.getPlayer());
//			}
//			catch ( Exception e )
//			{
//				
//			}
//		}
//	}
	
	@SubscribeEvent
	public void harvestDrops(HarvestDropsEvent event)
	{		
		if ( event.getWorld().isRemote )
		{
			return;
		}
		
		if ( isCrop(event.getState().getBlock()) )
		{
			List<EntityPlayer> players = event.getWorld().getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(event.getPos()).grow(6.5, 6.5, 6.5));
			
			for (EntityPlayer player : players)
			{
				Province province = PlayerCivilizationCapabilityImpl.get(player).getInCivilization();
				
				if ( province == null || province.civilization == null )
				{
					continue;
				}
				
				try
				{
	            	QuestFarm.INSTANCE.destroyedCrop(player);
				}
				catch ( Exception e )
				{
					
				}
				
				List<EntityVillager> villagers = event.getWorld().getEntitiesWithinAABB(EntityVillager.class, new AxisAlignedBB(event.getPos()).grow(1.25, 1.25, 1.25), new Predicate<EntityVillager>()
				{
					public boolean apply(@Nullable EntityVillager entity)
					{
						return true;
					}
				});
				
				if ( !villagers.isEmpty() )
				{
					return;
				}
								
				int rep = PlayerCivilizationCapabilityImpl.get(player).getReputation(province.civilization);
				
				if ( rep >= 50 || (!ToroQuestConfiguration.loseReputationForCropGrief) || player.isPotionActive(MobEffects.INVISIBILITY) )
				{
					continue;
				}
				
				boolean witnessed = villagersReportCrime( event.getWorld(), player );
				
				List<EntityToroNpc> help = event.getWorld().getEntitiesWithinAABB(EntityToroNpc.class, new AxisAlignedBB(player.getPosition()).grow(16, 12, 16), new Predicate<EntityToroNpc>()
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
					
					witnessed = true;
					entity.getNavigator().tryMoveToEntityLiving(player, 0.6D);
					
					if ( entity.isAnnoyed() )
					{
						if ( !entity.inCombat() )
						{
							entity.setAnnoyed(player);
							entity.setAttackTarget(player);
						}
					}
					else
					{
						entity.setAnnoyed(player);
					}
					
					if ( !flag && entity.actionReady() )
					{
						flag = true;
						entity.chat(entity, player, "crops", null);
					}
				}
				if ( witnessed ) reportCrimeRep( player, province, -ToroQuestConfiguration.unexpensiveRepLoss );
			}
		}
		else if ( event.getHarvester() != null )
		{
			Set<QuestData> quests = PlayerCivilizationCapabilityImpl.get(event.getHarvester()).getCurrentQuests();

			// DataWrapper quest = new DataWrapper();
			
			for ( QuestData data : quests )
			{
				try
				{
					if ( data.getiData().containsKey("block_type") )
					{
						int bt = data.getiData().get("block_type");
						for ( ItemStack drop : event.getDrops() )
						{
							if ( QuestMine.INSTANCE.isCorrectBlock(data.getPlayer(), drop.getItem(), bt) )
							{
								QuestMine.INSTANCE.perform(data, drop.getCount());
							}
						}
					}
				}
				catch (Exception e)
				{
					
				}
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

		if ( rep >= 250 || (!ToroQuestConfiguration.loseReputationForBlockGrief) || player.isPotionActive(MobEffects.INVISIBILITY) )
		{
			return;
		}
		
		boolean valuable = false;
		boolean witnessed = false;
		boolean flag = false;

		if ( isBuilding( block ) )
		{
			// if ( rand.nextBoolean() ) return;
			valuable = false;
			witnessed = villagersReportCrime( event.getWorld(), player );
		}
		else if ( isValuableBuilding( block ) )
		{
			valuable = true;
			witnessed = villagersReportCrime( event.getWorld(), player );
			
			List<EntityVillageLord> villageLord = event.getWorld().getEntitiesWithinAABB(EntityVillageLord.class, new AxisAlignedBB(player.getPosition()).grow(16, 12, 16), new Predicate<EntityVillageLord>()
			{
				public boolean apply(@Nullable EntityVillageLord entity)
				{
					return true;
				}
			});
			// Village Lord
			if ( block instanceof BlockQuartz || block == Blocks.GOLD_BLOCK )
			{
				for (EntityVillageLord entity : villageLord)
				{
					entity.setAnnoyed(player);
					entity.chat(entity, player, "throne", null);
					flag = true;
					break;
				}
			}
			else
			{
				for (EntityVillageLord entity : villageLord)
				{
					entity.setAnnoyed(player);
					entity.chat(entity, player, "crime", null);
					flag = true;
					break;
				}
			}
		}
		else
		{
			return;
		}
		
		List<EntityToroNpc> help = player.world.getEntitiesWithinAABB(EntityToroNpc.class, new AxisAlignedBB(player.getPosition()).grow(16, 12, 16), new Predicate<EntityToroNpc>()
		{
			public boolean apply(@Nullable EntityToroNpc entity)
			{
				return true;
			}
		});
		
		Collections.shuffle(help);
		
		for (EntityToroNpc entity : help)
		{
			if ( !entity.canEntityBeSeen(player) )
			{
				continue;
			}
			
			witnessed = true;
			entity.getNavigator().tryMoveToEntityLiving(player, 0.6D);
			
			if ( !valuable )
			{
				if ( entity.isAnnoyed() )
				{
					if ( !entity.inCombat() )
					{
						entity.setAnnoyed(player);
						entity.setAttackTarget(player);
					}
				}
				else
				{
					entity.setAnnoyed(player);
				}
				
				if ( !flag )
				{
					flag = true;
					entity.chat(entity, player, "grief", null);
				}
			}
			else
			{
				entity.setAnnoyed(player);
				entity.setAttackTarget(player);
				if ( !flag )
				{
					flag = true;
					entity.chat(entity, player, "grief", null);
				}
			}
		}
		if ( witnessed )
		{
			if ( !valuable )
			{
				reportCrimeRep( event.getPlayer(), province, -ToroQuestConfiguration.unexpensiveRepLoss );
			}
			else
			{
				reportCrimeRep( event.getPlayer(), province, -ToroQuestConfiguration.expensiveRepLoss );
			}
		}
	}
	
	// all villagers, within range and can see, reports to guards
	private boolean villagersReportCrime(World world, EntityPlayer player)
	{
		List<EntityToroVillager> villagerList = world.getEntitiesWithinAABB(EntityToroVillager.class, new AxisAlignedBB(player.getPosition()).grow(12, 12, 12), new Predicate<EntityToroVillager>()
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
		        || (block.getDefaultState() == Blocks.SANDSTONE.getDefaultState())
				//|| (block.getDefaultState() == Blocks.STONE_SLAB.getDefaultState())
		        || (block.getDefaultState() == Blocks.LOG.getDefaultState())
		        || (block.getDefaultState() == Blocks.WOODEN_PRESSURE_PLATE.getDefaultState())
		        //|| (block.getDefaultState() == Blocks.CRAFTING_TABLE.getDefaultState())
		        || (block.getDefaultState() == Blocks.TORCH.getDefaultState())
		        || (block instanceof BlockCarpet)
		        || (block instanceof BlockFence)
		        || (block instanceof BlockColored)
		        || (block instanceof BlockLog)
		        || (block instanceof BlockFlowerPot)
		        || (block instanceof BlockSlab)
		        || (block instanceof BlockStairs)
		        || (block instanceof BlockLadder)
		        || (block instanceof BlockTrapDoor)
		        || (block instanceof BlockDoubleStoneSlab);
				// || (block.getDefaultState() == Block.getBlockFromName(""));
	}
	
	public static boolean isValuableBuilding(Block block)
	{
		return     
		           (block.getDefaultState() == Blocks.BOOKSHELF.getDefaultState())
		        || (block.getDefaultState() == Blocks.GOLD_BLOCK.getDefaultState())
		        //|| (block instanceof Blocks.)
		        || (block.getDefaultState() == Blocks.TRAPPED_CHEST.getDefaultState())
		        || (block.getDefaultState() == Blocks.EMERALD_BLOCK.getDefaultState())
		        || (block instanceof BlockQuartz)
		        || (block instanceof BlockCauldron)
		        || (block instanceof BlockBanner)
		        || (block.getDefaultState() == Blocks.JUKEBOX)
		        || (block.getDefaultState() == Blocks.QUARTZ_STAIRS.getDefaultState())
		        //|| (block.getDefaultState() == Blocks.CHEST.getDefaultState())
		        || (block instanceof BlockDoor)
		        || (block instanceof BlockBed)
		        || (block.getDefaultState() == Blocks.ANVIL.getDefaultState());
	}
	
	public static boolean isCrop(Block block)
	{
		return block instanceof BlockCrops || block instanceof BlockStem || block.getDefaultState() == Blocks.FARMLAND.getDefaultState();
	}
	
	// ====================================================================
	protected int spawningTicks = 0;
	// public int entityTicks = 0;
	
	@SubscribeEvent
	public void civTimer(WorldTickEvent event)
	{
	    if ( TickEvent.Phase.START.equals(event.phase) || event.world == null )
		{
			return;
		}
	    
//		EntityPlayer player = event.player;
//		World world = player.getEntityWorld();

	    // this.spawningTicks++;

	    if ( event.world.isRemote || ++this.spawningTicks % 200 != 0 )
		{
			return;
		}

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
			if ( worldData.hasTrophyLord(province.id) )
			{
	    		p.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, duration, power, true, false));
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
	    
	    if ( this.spawningTicks < 1200 )
	    {
	    	return;
	    }

	    this.spawningTicks = 0;
	    
		int players = event.world.playerEntities.size();

		if ( players > 0 )
		{
			if ( ToroQuestConfiguration.banditSpawnRate > 0 && (rand.nextInt(100)) < ToroQuestConfiguration.banditSpawnRate + MathHelper.clamp((players-1)*2, 0, ToroQuestConfiguration.banditSpawnRate) )
	    	{
	    		spawnBanditsNearPlayer( event.world );
	    	}
			
			if ( ToroQuestConfiguration.caravanSpawnRate > 0 && ( event.world.getWorldTime() <= 11000 || event.world.getWorldTime() >= 23000 ) && (rand.nextInt(100)) < ToroQuestConfiguration.caravanSpawnRate + MathHelper.clamp((players-1)*2, 0, ToroQuestConfiguration.caravanSpawnRate) )
	    	{
	    		spawnCaravanNearProvince( event.world );
	    	}
			
//			if ( ToroQuestConfiguration.adventurerSpawnRate > 0 && ( event.world.getWorldTime() <= 11000 || event.world.getWorldTime() >= 23000 ) && (rand.nextInt(100)) < ToroQuestConfiguration.adventurerSpawnRate + MathHelper.clamp((players-1)*2, 0, ToroQuestConfiguration.adventurerSpawnRate) )
//	    	{
//	    		spawnAdventurersNearPlayer( event.world );
//	    	}
			
			if ( ToroQuestConfiguration.provinceSiegeRate > 0 && (rand.nextInt(100)) < ToroQuestConfiguration.provinceSiegeRate + MathHelper.clamp((players-1)*2, 0, ToroQuestConfiguration.provinceSiegeRate) )
		    {
		    	if ( rand.nextBoolean() && event.world.getWorldTime() >= 15000 && event.world.getWorldTime() <= 20000 )
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
		    		spawnBandits( event.world );
			        if ( rand.nextInt(4) == 0 )
			        {
			        	spawnBandits( event.world );
			        }
		    	}
		    }
			
			if ( ToroQuestConfiguration.fugitiveSpawnRate > 0 && (rand.nextInt(100)) < ToroQuestConfiguration.fugitiveSpawnRate + MathHelper.clamp((players-1)*2, 0, ToroQuestConfiguration.fugitiveSpawnRate) )
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
					PlayerCivilizationCapabilityImpl.get(p).adjustReputation(CivilizationType.EARTH, (int)MathHelper.clamp(-PlayerCivilizationCapabilityImpl.get(p).getReputation(CivilizationType.EARTH)/30.0,1.0,100.0));
				}
				if ( PlayerCivilizationCapabilityImpl.get(p).getReputation(CivilizationType.FIRE) < 0 )
				{
					PlayerCivilizationCapabilityImpl.get(p).adjustReputation(CivilizationType.FIRE, (int)MathHelper.clamp(-PlayerCivilizationCapabilityImpl.get(p).getReputation(CivilizationType.FIRE)/30.0,1.0,100.0));
				}
				if ( PlayerCivilizationCapabilityImpl.get(p).getReputation(CivilizationType.MOON) < 0 )
				{
					PlayerCivilizationCapabilityImpl.get(p).adjustReputation(CivilizationType.MOON, (int)MathHelper.clamp(-PlayerCivilizationCapabilityImpl.get(p).getReputation(CivilizationType.MOON)/30.0,1.0,100.0));
				}
				if ( PlayerCivilizationCapabilityImpl.get(p).getReputation(CivilizationType.SUN) < 0 )
				{
					PlayerCivilizationCapabilityImpl.get(p).adjustReputation(CivilizationType.SUN, (int)MathHelper.clamp(-PlayerCivilizationCapabilityImpl.get(p).getReputation(CivilizationType.SUN)/30.0,1.0,100.0));
				}
				if ( PlayerCivilizationCapabilityImpl.get(p).getReputation(CivilizationType.WATER) < 0 )
				{
					PlayerCivilizationCapabilityImpl.get(p).adjustReputation(CivilizationType.WATER, (int)MathHelper.clamp(-PlayerCivilizationCapabilityImpl.get(p).getReputation(CivilizationType.WATER)/30.0,1.0,100.0));
				}
				if ( PlayerCivilizationCapabilityImpl.get(p).getReputation(CivilizationType.WIND) < 0 )
				{
					PlayerCivilizationCapabilityImpl.get(p).adjustReputation(CivilizationType.WIND, (int)MathHelper.clamp(-PlayerCivilizationCapabilityImpl.get(p).getReputation(CivilizationType.WIND)/30.0,1.0,100.0));
				}
			}
		}
		catch ( Exception e )
		{
			
		}
	}

	// =-=-=-=-=-=-=-=-=-=-=-=-= Spawns BANDITS to attack a PROVINCE =-=-=-=-=-=-=-=-=-=-=-=-=
	
	protected void spawnBandits( World world )
	{
		if ( world.isRemote )
		{
			return;
		}
		
		try
		{
			int range = 192;
			List<EntityPlayer> players = world.playerEntities;
			Collections.shuffle(players);
			int tries = 3;
			
			while ( tries > 0 )
			{
				tries--;
				for ( EntityPlayer player : players )
				{
					if ( player.world.provider.getDimension() != 0 )
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
					
//					System.out.println("X"+province.getCenterX()); // 264
//					System.out.println(province.chunkX);			// 16
//					System.out.println("p"+player.posX);			// 289.5
//					System.out.println(player.chunkCoordX);			// 18


					int villageCenterZ = province.getCenterZ();
					
//					System.out.println("Z"+province.getCenterZ());
//					System.out.println(province.chunkZ);			// 85
//					System.out.println("p"+player.posZ);			// 1335
//					System.out.println(player.chunkCoordZ);			// 83

					
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
					
//					if ( tries > 0 && world.getChunkProvider().isChunkGeneratedAt(x, z) )
//					{
//						continue;
//					}
					
					BlockPos loc = new BlockPos(x,SPAWN_HEIGHT,z);
					BlockPos banditSpawnPos = findSpawnLocationFrom(world, loc);
					
					if ( banditSpawnPos == null )
					{
						continue;
					}

					if ( CivilizationUtil.getProvinceAt(world, banditSpawnPos.getX()/16, banditSpawnPos.getZ()/16) != null )
					{
						continue;
					}

					if ( !(world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(banditSpawnPos).grow(25, 12, 25))).isEmpty() )
					{
						continue;
					}
					
					if ( world.getEntitiesWithinAABB(EntityToroMob.class, new AxisAlignedBB(banditSpawnPos).grow(32, 32, 32)).size() >= 5 )
					{
						return;
					}	

					int rep = Math.abs(PlayerCivilizationCapabilityImpl.get(player).getReputation(province.civilization));
					int count = ( rand.nextInt( MathHelper.clamp( (int)((Math.abs(rep)+700)/200), 3, 9) ) + 1 );

					if ( ToroQuestConfiguration.orcsAreNeutral || rand.nextBoolean() )
					{
						for ( int i = count; i > 0; i-- )
						{
							EntitySentry e = new EntitySentry(world);
							e.despawnTimer-=10;
							e.setPosition(banditSpawnPos.getX() + 0.5,banditSpawnPos.getY()+0.1, banditSpawnPos.getZ() + 0.5 );
							e.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(e)), (IEntityLivingData) null);
							world.spawnEntity(e);
							e.setAttackTarget(player);
							e.setRaidLocation(villageCenterX, villageCenterZ);
						}
					}
					else
					{
						for ( int i = count; i > 0; i-- )
						{
							EntityOrc e = new EntityOrc(world);
							e.despawnTimer-=10;
							e.setPosition(banditSpawnPos.getX() + 0.5,banditSpawnPos.getY()+0.1, banditSpawnPos.getZ() + 0.5 );
							e.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(e)), (IEntityLivingData) null);
							world.spawnEntity(e);
							e.setAttackTarget(player);
							e.setRaidLocation(villageCenterX, villageCenterZ);
						}
					}
					// System.out.println("X" + banditSpawnPos.getX() + " " + " Z " + banditSpawnPos.getZ());
					return;
					
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("ERROR SPAWNING EntityBandit: " + e);
			return;
		}
	}
	
	// =-=-=-=-=-=-=-=-=-=-=-=-= Spawns CARAVANS near a PROVINCE =-=-=-=-=-=-=-=-=-=-=-=-=

	public void spawnCaravanNearProvince( World world )
	{
		if ( world.isRemote )
		{
			return;
		}
		
		try
		{
			int range = 360;
			List<EntityPlayer> players = world.playerEntities;
			Collections.shuffle(players);
			int tries = 3;
			while ( tries > 0 )
			{
				tries--;
				for ( EntityPlayer player : players )
				{
					if ( player.world.provider.getDimension() != 0 )
					{
						continue;
					}
					
					Village village = world.getVillageCollection().getNearestVillage(player.getPosition(), 360);
					
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
					if ( provinceNear == null ) {provinceNear = CivilizationUtil.getProvinceAt(world, (x+2)*16, (z+2)*16);}
					if ( provinceNear == null ) {provinceNear = CivilizationUtil.getProvinceAt(world, (x+2)*16, (z-2)*16);}
					if ( provinceNear == null ) {provinceNear = CivilizationUtil.getProvinceAt(world, (x-2)*16, (z+2)*16);}
					if ( provinceNear == null ) {provinceNear = CivilizationUtil.getProvinceAt(world, (x-2)*16, (z-2)*16);}
					
					if ( provinceNear != null )
					{
						continue;
					}
	
//					if ( tries > 0 && world.getChunkProvider().isChunkGeneratedAt(x, z) )
//					{
//						continue;
//					}

					BlockPos loc = new BlockPos(x,SPAWN_HEIGHT,z);
					BlockPos banditSpawnPos = findSpawnLocationFrom(world, loc);
					
					if ( banditSpawnPos == null )
					{
						continue;
					}
					
					if ( CivilizationUtil.getProvinceAt(world, banditSpawnPos.getX()/16, banditSpawnPos.getZ()/16) != null )
					{
						continue;
					}
					
					if ( !(world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(banditSpawnPos).grow(25, 12, 25))).isEmpty() )
					{
						continue;
					}
					
					int i = rand.nextInt(3)+1;
					while ( i > 0 )
					{
						i--;
						EntityCaravan e = new EntityCaravan(world);
						e.setPosition(banditSpawnPos.getX() + 0.5,banditSpawnPos.getY()+0.1, banditSpawnPos.getZ() + 0.5 );
						world.spawnEntity(e);
						
						if ( rand.nextBoolean() )
						{
							if ( rand.nextBoolean() )
							{
								EntityGuard g = new EntityGuard(world, null, true);
								g.setPosition(banditSpawnPos.getX() + 0.5,banditSpawnPos.getY()+0.1, banditSpawnPos.getZ() + 0.5 );
								g.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Items.IRON_SWORD, 1));
								ItemStack istack = new ItemStack(Item.getByNameOrId("spartanshields:shield_tower_wood"));
								if ( istack != null && !istack.isEmpty() )
								{
						    		g.setHeldItem(EnumHand.OFF_HAND, istack);
								}
								else
								{
									g.setHeldItem(EnumHand.OFF_HAND, new ItemStack(Items.SHIELD, 1));
								}
					    		g.tasks.addTask(0, new EntityAIDespawnGuard(g));
								world.spawnEntity(g);
					    		g.setCivilizationCaravan(province.civilization);
								g.getNavigator().tryMoveToEntityLiving(player, 0.6);
								g.setAttackTarget(null);
							}
							else
							{
								e.addCaravan();
								if ( rand.nextBoolean() )
								{
									e.addCaravan();
								}
							}
						}
						e.getNavigator().tryMoveToEntityLiving(player, 0.6);
						e.setAttackTarget(player);
					}
					return;
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("ERROR SPAWNING EntityCaravan: " + e);
			return;
		}
	}
	
	// =-=-=-=-=-=-=-=-=-=-=-=-= Spawns BANDITS to attack a PLAYER =-=-=-=-=-=-=-=-=-=-=-=-=

	protected void spawnBanditsNearPlayer( World world )
	{
		if ( world.isRemote )
		{
			return;
		}
		
		try
		{
			int range = 96;
			List<EntityPlayer> players = world.playerEntities;
			Collections.shuffle(players);
			int tries = 3;
			while ( tries > 0 )
			{
				tries--;
				for ( EntityPlayer player : players )
				{
					if ( player.world.provider.getDimension() != 0 )
					{
						continue;
					}
					
					int playerPosX = (int)player.posX;
					int playerPosZ = (int)player.posZ;
					
					if ( CivilizationUtil.getProvinceAt(world, playerPosX/16, playerPosZ/16) != null )
					{
						continue;
					}
					
					int x = (rand.nextInt(range));
					int z = (rand.nextInt(range));
					
					while ( x < range/2 && z < range/2 )
					{
						x = (rand.nextInt(range));
						z = (rand.nextInt(range));
					}
					
					x *= (rand.nextInt(2)*2-1);
					z *= (rand.nextInt(2)*2-1);
					
					x += playerPosX;
					z += playerPosZ;
					
					BlockPos loc = new BlockPos(x,SPAWN_HEIGHT,z);
					
//					if ( tries > 0 && !(world.getChunkFromBlockCoords(loc).isLoaded()) )
//					{
//						continue;
//					}
					
					BlockPos banditSpawnPos = findSpawnLocationFrom(world, loc);
					
					
					if ( banditSpawnPos == null )
					{
						continue;
					}
					
					if ( CivilizationUtil.getProvinceAt(world, banditSpawnPos.getX()/16, banditSpawnPos.getZ()/16) != null )
					{
						continue;
					}
					
					if ( !(world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(banditSpawnPos).grow(25, 15, 25))).isEmpty() )
					{
						continue;
					}

					// =-=-=-=-=-=-=-=-=-=-=-=-=
					//			BANDIT
					// =-=-=-=-=-=-=-=-=-=-=-=-=
					
					// float difficulty = world.getDifficultyForLocation(banditSpawnPos).getAdditionalDifficulty();

					boolean raiders = rand.nextBoolean();
					
					if ( ToroQuestConfiguration.orcsAreNeutral || rand.nextBoolean() )
					{
						boolean cavalry = ToroQuestConfiguration.banditMountChance > 0 && rand.nextInt(11-ToroQuestConfiguration.banditMountChance) == 0;

						for ( int i = rand.nextInt(5)+2; i > 0; i-- )
						{
							EntitySentry e = new EntitySentry(world);
							e.despawnTimer-=10;
							e.setPosition(banditSpawnPos.getX() + 0.5,banditSpawnPos.getY()+0.1, banditSpawnPos.getZ() + 0.5 );
							e.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(e)), (IEntityLivingData) null);
							world.spawnEntity(e);
							e.setAttackTarget(player);
							if ( cavalry )
							{
								e.setMount();
							}
							else if ( raiders )
							{
								//System.out.println(banditSpawnPos.getX() + " " + banditSpawnPos.getZ());
								e.setAttackTarget(player);
								e.setRaidLocation(playerPosX*2-banditSpawnPos.getX(), playerPosZ*2-banditSpawnPos.getZ());
							}
						}
					}
					else
					{
						boolean cavalry = ToroQuestConfiguration.orcMountChance > 0 && rand.nextInt(11-ToroQuestConfiguration.orcMountChance) == 0;

						for ( int i = rand.nextInt(5)+2; i > 0; i-- )
						{
							EntityOrc e = new EntityOrc(world);
							e.despawnTimer-=10;
							e.setPosition(banditSpawnPos.getX() + 0.5,banditSpawnPos.getY()+0.1, banditSpawnPos.getZ() + 0.5 );
							e.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(e)), (IEntityLivingData) null);
							world.spawnEntity(e);
							e.setAttackTarget(player);
							if ( cavalry )
							{
								e.setMount();
							}
							else if ( raiders )
							{
								//System.out.println(banditSpawnPos.getX() + " " + banditSpawnPos.getZ());
								e.setAttackTarget(player);
								e.setRaidLocation(playerPosX*2-banditSpawnPos.getX(), playerPosZ*2-banditSpawnPos.getZ());
							}
						}
					}
					return;
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("ERROR SPAWNING EntityBandit: " + e);
			return;
		}
	}
	
	// =-=-=-=-=-=-=-=-=-=-=-=-= Spawns ADVENTURERS near PLAYER =-=-=-=-=-=-=-=-=-=-=-=-=

		protected void spawnAdventurersNearPlayer( World world )
		{
			if ( world.isRemote )
			{
				return;
			}
			
			try
			{
				int range = 96;
				List<EntityPlayer> players = world.playerEntities;
				Collections.shuffle(players);
				int tries = 3;
				while ( tries > 0 )
				{
					tries--;
					for ( EntityPlayer player : players )
					{
						if ( player.world.provider.getDimension() != 0 )
						{
							continue;
						}
						
						int playerPosX = (int)player.posX;
						int playerPosZ = (int)player.posZ;
						
						int x = (rand.nextInt(range));
						int z = (rand.nextInt(range));
						
						while ( x < range/2 && z < range/2 )
						{
							x = (rand.nextInt(range));
							z = (rand.nextInt(range));
						}
						
						x *= (rand.nextInt(2)*2-1);
						z *= (rand.nextInt(2)*2-1);
						
						x += playerPosX;
						z += playerPosZ;
						
						BlockPos loc = new BlockPos(x,SPAWN_HEIGHT,z);
						
//						if ( tries > 0 && !(world.getChunkFromBlockCoords(loc).isLoaded()) )
//						{
//							continue;
//						}
						
						BlockPos banditSpawnPos = findSpawnLocationFrom(world, loc);
						
						if ( banditSpawnPos == null )
						{
							continue;
						}
						
//						if ( CivilizationUtil.getProvinceAt(world, banditSpawnPos.getX()/16, banditSpawnPos.getZ()/16) != null )
//						{
//							continue;
//						}
						
						if ( !(world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(banditSpawnPos).grow(20, 12, 20))).isEmpty() )
						{
							continue;
						}
						
						if ( !(world.getEntitiesWithinAABB(EntityMob.class, new AxisAlignedBB(banditSpawnPos).grow(12, 6, 12))).isEmpty() )
						{
							continue;
						}
						
						// =-=-=-=-=-=-=-=-=-=-=-=-=
						//			BANDIT
						// =-=-=-=-=-=-=-=-=-=-=-=-=
						
						// float difficulty = world.getDifficultyForLocation(banditSpawnPos).getAdditionalDifficulty();


						for ( int i = rand.nextInt(3)+1; i > 0; i-- )
						{
							EntityAdventurer e = new EntityAdventurer(world);
							e.despawnTimer-=10;
							e.setPosition(banditSpawnPos.getX()+0.5,banditSpawnPos.getY()+0.1, banditSpawnPos.getZ()+0.5 );
							e.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(e)), (IEntityLivingData) null);
							world.spawnEntity(e);
							e.setAttackTarget(null);
							e.setRaidLocation(playerPosX*2-banditSpawnPos.getX(), playerPosZ*2-banditSpawnPos.getZ());
						}
						return;
					}
				}
			}
			catch (Exception e)
			{
				System.out.println("ERROR SPAWNING EntityBandit: " + e);
				return;
			}
		}
	
	// =-=-=-=-=-=-=-=-=-=-=-=-= Spawns WOLVES to attack a PROVINCE =-=-=-=-=-=-=-=-=-=-=-=-=
	
	protected void spawnWolves( World world )
	{
		if ( world.isRemote )
		{
			return;
		}
		
		try
		{
			int range = 176;
			List<EntityPlayer> players = world.playerEntities;
			Collections.shuffle(players);
			int tries = 3;
			while ( tries > 0 )
			{
				tries--;
				for ( EntityPlayer player : players )
				{
					if ( player.world.provider.getDimension() != 0 )
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
					
					BlockPos loc = new BlockPos(x,SPAWN_HEIGHT,z);
					
//					if ( tries > 0 && !(world.getChunkFromBlockCoords(loc).isLoaded()) )
//					{
//						continue;
//					}
					
					BlockPos banditSpawnPos = findSpawnLocationFrom(world, loc);
					
					if ( banditSpawnPos == null )
					{
						continue;
					}
					
					if ( CivilizationUtil.getProvinceAt(world, banditSpawnPos.getX()/16, banditSpawnPos.getZ()/16) != null )
					{
						continue;
					}
					
					if ( !(world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(banditSpawnPos).grow(25, 12, 25))).isEmpty() )
					{
						continue;
					}
					
					int rep = Math.abs(PlayerCivilizationCapabilityImpl.get(player).getReputation(province.civilization));
					int count = ( rand.nextInt( MathHelper.clamp( (int)((Math.abs(rep)+700)/200), 3, 9) ) + 1 );

					try
					{
						for ( int i = count; i > 0; i-- )
						{
							String className = "net.its_meow.betteranimalsplus.common.entity.EntityFeralWolf";
							EntityCreature feralWolf = (EntityCreature) Class.forName(className).getConstructor(new Class[] {World.class}).newInstance(new Object[] {world});
							EntityAIRaid task = new EntityAIRaid(feralWolf, 1.2D, 16, 32);
							task.setCenter(villageCenterX-banditSpawnPos.getX(), villageCenterZ-banditSpawnPos.getZ());
							feralWolf.setPosition(banditSpawnPos.getX() + 0.5,banditSpawnPos.getY()+0.1, banditSpawnPos.getZ() + 0.5 );
							feralWolf.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(feralWolf)), (IEntityLivingData) null);
							world.spawnEntity(feralWolf);
							feralWolf.setAttackTarget(player);
							feralWolf.tasks.addTask( 1, task );
							if ( rand.nextBoolean() ) i--;
						}
					}
					catch ( Exception error )
					{
						for ( int i = count; i > 0; i-- )
						{
							EntityWolfRaider e = new EntityWolfRaider(world);
							e.setPosition(banditSpawnPos.getX() + 0.5,banditSpawnPos.getY()+0.1, banditSpawnPos.getZ() + 0.5 );
							e.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(e)), (IEntityLivingData) null);
							world.spawnEntity(e);
							e.setAttackTarget(player);
							e.setRaidLocation(villageCenterX-banditSpawnPos.getX(), villageCenterZ-banditSpawnPos.getZ());
						}
					}
					return;
				}
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
		
		if ( rep >= 250 || (!ToroQuestConfiguration.loseReputationForBlockGrief) || player.isPotionActive(MobEffects.INVISIBILITY) )
		{
			return;
		}
		
		// int reportChance = (MathHelper.clamp(rep, 150, 500))/150;
		
		boolean witnessed = villagersReportCrime( event.getWorld(), player );
		
		List<EntityToroNpc> help = player.world.getEntitiesWithinAABB(EntityToroNpc.class, new AxisAlignedBB(player.getPosition()).grow(16, 12, 16), new Predicate<EntityToroNpc>()
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
			
			witnessed = true;
			entity.getNavigator().tryMoveToEntityLiving(player, 0.6D);
			
			entity.setAnnoyed(player);
			entity.setAttackTarget(player);
			
			if ( !flag )
			{
				flag = true;
				entity.chat(entity, player, "steal", null);
			}
		}
		if ( witnessed ) reportCrimeRep( player, province, -ToroQuestConfiguration.expensiveRepLoss );
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
	
	private void spawnFugitive(World world)
	{	
		if ( world.isRemote )
		{
			return;
		}
		
		try
		{
			int spawnDistance = 60;
			List<EntityPlayer> players= world.playerEntities;
			Collections.shuffle(players);
			int tries = 3;
			while ( tries > 0 )
			{
				tries--;
				for ( EntityPlayer player : players )
				{
					if ( world.provider.getDimension() != 0 )
					{
						continue;
					}
					
					Province province = CivilizationUtil.getProvinceAt(world, player.chunkCoordX, player.chunkCoordZ);
					
					if ( province == null )
					{
						continue;
					}
					
					int villageCenterX = province.getCenterX();
					int villageCenterZ = province.getCenterZ();
	
					BlockPos loc = new BlockPos(villageCenterX + (rand.nextInt(spawnDistance)*(rand.nextInt(2)*2-1)),SPAWN_HEIGHT,villageCenterZ + (rand.nextInt(spawnDistance)*(rand.nextInt(2)*2-1)) );
					BlockPos spawnPos = findSpawnLocationFrom(world, loc);
					
					if (spawnPos == null)
					{
						continue;
					}
					
					List<EntityPlayer> nearbyPlayers = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(spawnPos).grow(16, 8, 16));
					
					if ( nearbyPlayers.size() > 0 )
					{
						loc = new BlockPos(villageCenterX + (rand.nextInt(spawnDistance)*(rand.nextInt(2)*2-1)),SPAWN_HEIGHT,villageCenterZ + (rand.nextInt(spawnDistance)*(rand.nextInt(2)*2-1)));
						spawnPos = findSpawnLocationFrom(world, loc);
						
						if (spawnPos == null)
						{
							continue;
						}
					}
					
					int localFugitiveCount = world.getEntitiesWithinAABB(EntityFugitive.class, new AxisAlignedBB(spawnPos).grow(86, 86, 86)).size();
	
					if ( localFugitiveCount > 3 )
					{
						continue;
					}
					
					if ( localFugitiveCount == 3 && rand.nextBoolean() )
					{
						continue;
					}
					EntityFugitive e = new EntityFugitive(world);
					e.setPosition(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
					world.spawnEntity(e);
					e.setAttackTarget(player);
					return;
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
		if ( world.isRemote )
		{
			return;
		}
		
		try
		{
			int range = 176;
			List<EntityPlayer> players = world.playerEntities;
			Collections.shuffle(players);
			int tries = 3;
			while ( tries > 0 )
			{
				tries--;
				for ( EntityPlayer player : players )
				{
					if ( player.world.provider.getDimension() != 0 )
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
					
					BlockPos loc = new BlockPos(x,SPAWN_HEIGHT,z);
					
					BlockPos banditSpawnPos = findSpawnLocationFrom(world, loc);
					
					if ( banditSpawnPos == null )
					{
						continue;
					}
					
					if ( CivilizationUtil.getProvinceAt(world, banditSpawnPos.getX()/16, banditSpawnPos.getZ()/16) != null )
					{
						continue;
					}
					
					if ( !(world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(banditSpawnPos).grow(25, 12, 25))).isEmpty() )
					{
						continue;
					}
					
					int rep = Math.abs(PlayerCivilizationCapabilityImpl.get(player).getReputation(province.civilization));
					int count = ( rand.nextInt( MathHelper.clamp( (int)((Math.abs(rep)+350)/100), 5, 10) ) + 5 );

					for ( int i = count; i > 0; i-- )
					{
						if ( rand.nextInt(100) > ToroQuestConfiguration.zombieRaiderVillagerChance )
						{
							EntityZombieVillagerRaider e = new EntityZombieVillagerRaider(world, province.getCenterX(), province.getCenterZ());
							e.setPosition(banditSpawnPos.getX() + 0.5,banditSpawnPos.getY()+0.1, banditSpawnPos.getZ() + 0.5 );
							e.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(e)), (IEntityLivingData)null);
//							final EntityAIRaid areaAI = new EntityAIRaid(e, 0.7D, 48);
//							areaAI.setCenter(province.getCenterX(), province.getCenterZ());
//							e.tasks.addTask(7, areaAI);
					        //e.tasks.addTask(2, new EntityAIZombieLeap(e, 0.35F, false));
							world.spawnEntity(e);
							e.setAttackTarget(player);
						}
						else
						{
							EntityZombieRaider e = new EntityZombieRaider(world, province.getCenterX(), province.getCenterZ());
							e.setPosition(banditSpawnPos.getX() + 0.5,banditSpawnPos.getY()+0.1, banditSpawnPos.getZ() + 0.5 );
							e.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(e)), (IEntityLivingData)null);
							world.spawnEntity(e);
							e.setAttackTarget(player);
						}
					}
					return;
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("ERROR SPAWNING province EntityZombieRaider: " + e);
			return;
		}
	}

	public static BlockPos findSpawnLocationFrom(World world, BlockPos spawnPos) // TODO
	{
		boolean[] airSpace = { false, false };
		IBlockState blockState;
		for (int j = 0; j <= 6; j++)
		{
			for (int i = 0; i <= 38; i++)
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
			spawnPos = spawnPos.add( j*2 * (rand.nextBoolean()?1:-1), 0, j*2 * (rand.nextBoolean()?1:-1) );
		}
		return null;
	}
	
	public static BlockPos findTeleportLocationFrom(World world, BlockPos pos)
	{
		boolean[] airSpace = { false, false };
		IBlockState blockState;
		BlockPos spawnPos;
		for (int j = 0; j < 16; j++)
		{
			spawnPos = new BlockPos(pos.getX()+rand.nextInt(32)+j*2 * (rand.nextBoolean()?1:-1), ToroQuestConfiguration.spawnHeight, pos.getZ()+rand.nextInt(32)+j*2 * (rand.nextBoolean()?1:-1) );

			for (int i = 0; i < ToroQuestConfiguration.spawnHeight; i++)
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
		if ( !blockState.getBlock().getDefaultState().isFullCube() || blockState.getBlock() instanceof BlockLeaves || blockState.getBlock().getDefaultState() == Blocks.WOOL.getDefaultState() || blockState.getBlock().getDefaultState() == Blocks.CONCRETE.getDefaultState() || blockState.getBlock().getDefaultState() == Blocks.STONEBRICK.getDefaultState() || blockState.getBlock() instanceof BlockPlanks || blockState.getBlock() instanceof BlockMagma )
		{
			return true;
		}
		return false;
	}
	
//	private static final Item[] STOLEN_ITEMS =
//	{
//		Items.GOLDEN_PICKAXE,
//		Items.GOLDEN_AXE,
//		Items.GOLDEN_SWORD,
//		Items.GOLDEN_HOE,
//		Items.BOOK
//	};

	public static ItemStack randomStolenItem(World world, Province province)
	{
		if ( world == null )
		{
			return null;
		}
		
		// ItemStack stolenItem = new ItemStack(STOLEN_ITEMS[rand.nextInt(STOLEN_ITEMS.length)]);
		
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
		
		ItemStack stolenItem = null;
		
		switch ( civ )
		{
			case FIRE :
			{
				stolenItem = new ItemStack(Item.getByNameOrId("toroquest:artifact_red"));
				break;
			}
			case MOON :
			{
				stolenItem = new ItemStack(Item.getByNameOrId("toroquest:artifact_black"));
				break;
			}
			case EARTH :
			{
				stolenItem = new ItemStack(Item.getByNameOrId("toroquest:artifact_green"));
				break;
			}
			case WATER :
			{
				stolenItem = new ItemStack(Item.getByNameOrId("toroquest:artifact_blue"));
				break;
			}
			case WIND :
			{
				stolenItem = new ItemStack(Item.getByNameOrId("toroquest:artifact_brown"));
				break;
			}
			case SUN :
			{
				stolenItem = new ItemStack(Item.getByNameOrId("toroquest:artifact_yellow"));
				break;
			}
			default :
			{
				return null;
			}
		}

		if ( stolenItem.getItem() == null )
		{
			return null;
		}
		
		if (!stolenItem.hasTagCompound())
		{
			stolenItem.setTagCompound(new NBTTagCompound());
		}

		// String civName = CivilizationType.civServerName(civ.getCivName());
		stolenItem.getTagCompound().setString("civilizationName", civ.name());
		stolenItem.getTagCompound().setBoolean("isStolen", true);
		// stolenItem.addEnchantment(Enchantment.getEnchantmentByID(-1), 0);
		return stolenItem;
	}

//	public static void setProvince(ItemStack stolenItem, World world, Province province)
//	{
//		
//	}
	
//	private void chat(EntityPlayer player, EntityToroNpc entity, String text)
//	{
//		if ( player.world.isRemote )
//		{
//			return;
//		}
//		player.sendMessage(new TextComponentString( "§l" + entity.getName() + "§r: " + text));
//        entity.playSound( SoundEvents.VINDICATION_ILLAGER_AMBIENT, 0.9F, 0.9F );
//	}

}