package net.torocraft.toroquest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityMule;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.torocraft.toroquest.civilization.CivilizationDataAccessor;
import net.torocraft.toroquest.civilization.CivilizationHandlers;
import net.torocraft.toroquest.civilization.CivilizationUtil;
import net.torocraft.toroquest.civilization.CivilizationsWorldSaveData;
import net.torocraft.toroquest.civilization.Province;
import net.torocraft.toroquest.civilization.player.PlayerCivilizationCapabilityImpl;
import net.torocraft.toroquest.config.ToroQuestConfiguration;
import net.torocraft.toroquest.config.ToroQuestConfiguration.Trade;
import net.torocraft.toroquest.entities.EntityCaravan;
import net.torocraft.toroquest.entities.EntityFugitive;
import net.torocraft.toroquest.entities.EntityGuard;
import net.torocraft.toroquest.entities.EntityMonolithEye;
import net.torocraft.toroquest.entities.EntitySentry;
import net.torocraft.toroquest.entities.EntityToro;
import net.torocraft.toroquest.entities.EntityToroCow;
import net.torocraft.toroquest.entities.EntityToroMob;
import net.torocraft.toroquest.entities.EntityToroNpc;
import net.torocraft.toroquest.entities.EntityToroVillager;
import net.torocraft.toroquest.entities.EntityWolfRaider;
import net.torocraft.toroquest.entities.EntityZombieVillagerRaider;
import net.torocraft.toroquest.network.ToroQuestPacketHandler;
import net.torocraft.toroquest.network.message.MessageRequestPlayerCivilizationSync;
import net.torocraft.toroquest.util.TaskRunner;

public class EventHandlers
{

	private Random rand = new Random();
	
	@SubscribeEvent
	public void onEntitySpawn(EntityJoinWorldEvent event)
	{
		Entity entity = event.getEntity();
		World world = event.getWorld();
		
		if ( world == null || entity == null )
		{
			 return;
		}
		 
		// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= MOB =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
		if ( entity instanceof EntityMob )
		{
			EntityMob mob = (EntityMob) entity;
			int entityPosX = mob.getPosition().getX();
			int entityPosZ = mob.getPosition().getZ();
			
			Province province = CivilizationUtil.getProvinceAt(mob.getEntityWorld(), entityPosX/16, entityPosZ/16);
			
			// =-=-=-=-=-=-=-=-= PROVINCE =-=-=-=-=-=-=-=-=-=
			if ( province != null )
			{
				if ( mob instanceof EntityCreeper || mob instanceof EntityEnderman )
				{
					mob.setDead();
					event.setCanceled(true);
					return;
				}
				
				int villageCenterX = province.getCenterX();
				int villageCenterZ = province.getCenterZ();
				/*
			 * Village length is equal to 176.
			 */
				int allowedDistance = ToroQuestConfiguration.disableMobSpawningNearVillage;
				
				if ( ( allowedDistance < 176 ) && ( mob.getPosition().getY() >= 40 && Math.abs(villageCenterX-entityPosX) < allowedDistance && Math.abs(villageCenterZ-entityPosZ) < allowedDistance ) )
				{
					mob.setDead();
					event.setCanceled(true);
					return;
				}
			}
			// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
			
			if ( !(mob instanceof EntityCreeper) && !(mob instanceof EntityEnderman) )
			{
				if ( ToroQuestConfiguration.entityMobAttackGuardsTask )
				{
					mob.targetTasks.addTask(4, new EntityAINearestAttackableTarget<EntityToroNpc>(mob, EntityToroNpc.class, 30, true, true, new Predicate<EntityToroNpc>()
					{
						@Override
						public boolean apply(EntityToroNpc target)
						{
							return true;
						}
					}));
				}
				
				if ( ToroQuestConfiguration.entityMobAttackVillagersTask && !(mob instanceof EntityZombie) )
				{
					mob.targetTasks.addTask(5, new EntityAINearestAttackableTarget<EntityVillager>(mob, EntityVillager.class, 30, true, true, new Predicate<EntityVillager>()
					{
						@Override
						public boolean apply(EntityVillager target)
						{
							return true;
						}
					}));
				}
			}
			if ( mob.getClass().equals(EntityZombie.class) )
			{
				if ( rand.nextInt( 4 ) == 0 )
				{
					if ( province == null )
					{
						province = CivilizationUtil.getProvinceAt(world, entityPosX/16+2, entityPosZ/16+2);
						if ( province == null )
						{
							province = CivilizationUtil.getProvinceAt(world, entityPosX/16+2, entityPosZ/16-2);
							if ( province == null )
							{
								province = CivilizationUtil.getProvinceAt(world, entityPosX/16-2, entityPosZ/16+2);
								if ( province == null )
								{
									province = CivilizationUtil.getProvinceAt(world, entityPosX/16-2, entityPosZ/16-2);
								}
							}
						}
					}
					if ( province != null )
					{
						 entity.setDead();
						 event.setCanceled(true);
						 if ( !world.isRemote )
						 {
							 EntityZombieVillagerRaider newEntity = new EntityZombieVillagerRaider( world );
							 BlockPos pos = entity.getPosition();
							 newEntity.setPosition( pos.getX()+0.5, pos.getY(), pos.getZ()+0.5 );
							 world.spawnEntity(newEntity);
							 newEntity.setRaidLocation( province.getCenterX(), province.getCenterZ() );
						 }
					}
				}
				return;
			}
		}
		// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
		else if ( (ToroQuestConfiguration.toroSpawnChance > 0) && !(ToroQuestConfiguration.defaultCowBreeding) && entity.getClass().equals(EntityCow.class) )
		{
			BlockPos pos = entity.getPosition();
			EntityCow cow = (EntityCow)entity;
			 
			if ( ToroQuestConfiguration.toroSpawnChance > rand.nextInt(100) )
			{
				
				if (entity.getEntityData().getBoolean("AddedToWorld"))
				{
					return;
				}
	
				entity.getEntityData().setBoolean("AddedToWorld", true);
					
				if (pos == null)
				{
					return;
				}
				if ( !world.isRemote )
				{
					EntityToro toro = new EntityToro(world);
					toro.setPosition(pos.getX()+0.5, pos.getY() + 0.5, pos.getZ()+0.5);
					toro.setGrowingAge(cow.getGrowingAge());
					world.spawnEntity(toro);
				}
			 }
			 entity.setDead();
			 cow.setDead();
			 event.setCanceled(true);
			 if ( !world.isRemote )
			 {
				EntityToroCow newEntity = new EntityToroCow(world);
				newEntity.setPosition(pos.getX()+0.5, pos.getY(), pos.getZ()+0.5);
				newEntity.setGrowingAge(cow.getGrowingAge());
				world.spawnEntity(newEntity);
			 }
			 return;
		}
		else if ( (entity instanceof EntityVillager) && !(entity instanceof EntityToroVillager) && ToroQuestConfiguration.useToroVillagers )
		{
			// entity.getClass().equals(EntityVillager.class)
			if ( ToroQuestConfiguration.useDefaultVillagersOutsideOfProvince)
			{
				Province province = CivilizationUtil.getProvinceAt(entity.getEntityWorld(), entity.getPosition().getX()/16, entity.getPosition().getZ()/16);
				
				if ( province == null )
				{
					return;
				}
			}
			
			EntityVillager villager = (EntityVillager)entity;
			
			if ( villager.world.isRemote )
			{
				return;
			}

			String jobName = villager.getProfessionForge().getCareer(0).getName();
			
//          System.out.println( jobName );
//			List<EntityPlayer> players = world.playerEntities;
//			for ( EntityPlayer player : players )
//			{
//				player.sendStatusMessage( new TextComponentString( ( " " + villager.world.isRemote + "  " + jobName ) ), false );
//			}
			
			boolean flag = false;
			
			for ( Trade trade : ToroQuestConfiguration.trades )
			{
				if ( jobName.equals(trade.job) )
				{
					flag = true;
					break;
				}
			}
			
			if ( flag )
			{
				villager.setDead();
				event.setCanceled(true);
				if ( !world.isRemote )
				{
					EntityToroVillager newEntity = new EntityToroVillager( world, villager.getProfession() );
					BlockPos pos = entity.getPosition();
					newEntity.setPosition(pos.getX()+0.5, pos.getY(), pos.getZ()+0.5);
					newEntity.setGrowingAge(villager.getGrowingAge());
					world.spawnEntity(newEntity);
				}
			}
			return;
		}
		else if ( !ToroQuestConfiguration.villagesSpawnGolems && entity.getClass().equals(EntityIronGolem.class) )
		{
			EntityIronGolem golem = (EntityIronGolem)entity;
			
			if ( !golem.isPlayerCreated() )
			{
				entity.setDead();
				golem.setDead();
				event.setCanceled(true);
				return;
		 	}

			golem.targetTasks.addTask(4, new EntityAINearestAttackableTarget<EntityToroMob>(golem, EntityToroMob.class, 0, true, true, new Predicate<EntityToroMob>()
			{
				@Override
				public boolean apply(EntityToroMob target)
				{
					return true;
				}
			}));
			golem.targetTasks.addTask(4, new EntityAINearestAttackableTarget<EntityWolfRaider>(golem, EntityWolfRaider.class, 0, true, true, new Predicate<EntityWolfRaider>()
			{
				@Override
				public boolean apply(EntityWolfRaider target)
				{
					return true;
				}
			}));
		}
	}
	 
	@SubscribeEvent
	public void handleWorldTick(ClientTickEvent event)
	{
		TaskRunner.run();
	}

	public static class SyncTask implements Runnable
	{
		public void run()
		{
			ToroQuestPacketHandler.INSTANCE.sendToServer(new MessageRequestPlayerCivilizationSync());
		}
	}
	
	@SubscribeEvent
	public void crystalAttackFrom( AttackEntityEvent event )
	{
		Entity entityCrystal = event.getTarget();
		if ( entityCrystal != null && entityCrystal instanceof EntityEnderCrystal )
		{
			List<EntityMonolithEye> eyes = entityCrystal.world.getEntitiesWithinAABB(EntityMonolithEye.class, new AxisAlignedBB(entityCrystal.getPosition()).grow(96, 64, 96));
			if ( eyes.size() > 0 )
			{
				if ( event != null && !(event.getEntity() instanceof EntityMonolithEye) )
				{
					event.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent
	public void crystalProjectileImpact(ProjectileImpactEvent event)
	{
		if ( event == null || event.getEntity() == null )
		{
			return;
		}
		Entity entity = event.getRayTraceResult().entityHit;
		if ( entity == null )
		{
			return;
		}
		if ( entity instanceof EntityEnderCrystal )
		{
			if ( !(event.getEntity() instanceof EntityFireball) )
			{
				List<EntityMonolithEye> eyes = entity.world.getEntitiesWithinAABB(EntityMonolithEye.class, new AxisAlignedBB(entity.getPosition()).grow(96, 64, 96));
				if ( eyes.size() > 0 )
				{
					if ( event.getEntity() != null )
					{
						event.setCanceled(true);
					}
				}
			}
			return;
		}
		else if ( entity instanceof EntityGuard )
		{
			if ( event.getEntity() instanceof EntityArrow )
			{
				EntityArrow arrow = (EntityArrow)event.getEntity();
				if ( arrow.shootingEntity != null && arrow.shootingEntity instanceof EntityGuard )
				{
					event.setCanceled(true);
				}
			}
			return;
		}
		else if ( entity instanceof EntityIronGolem )
		{
			if ( event.getEntity() instanceof EntityArrow )
			{
				EntityArrow arrow = (EntityArrow)event.getEntity();
				if ( arrow.shootingEntity != null && arrow.shootingEntity instanceof EntityGuard )
				{
					event.setCanceled(true);
				}
			}
			return;
		}
	}

	// attackentityfrom damage
	@SubscribeEvent
	public void livingHurtEvent(LivingHurtEvent event)
	{
		EntityLivingBase victim = event.getEntityLiving();
		EntityLivingBase attacker = getAttacker(event);
		
		if ( victim == null || attacker == null )
		{
			return;
		}
		
		if ( victim instanceof EntityVillager && !(victim instanceof EntityFugitive) )
		{
			EntityVillager villager = (EntityVillager)victim;
			
			if ( attacker instanceof EntityToroNpc )
			{
				event.setCanceled(true);
				return;
			}
			
			if ( villager instanceof EntityToroVillager && attacker instanceof EntityLivingBase )
			{
				EntityLivingBase e = (EntityLivingBase)villager;
				((EntityToroVillager)villager).callForHelp(e);
			}

			if ( attacker instanceof EntityPlayer )
			{
            	EntityPlayer player = (EntityPlayer)attacker;
				Province province = CivilizationUtil.getProvinceAt(player.world, player.chunkCoordX, player.chunkCoordZ);
				if ( province == null )
				{
					province = CivilizationUtil.getProvinceAt(villager.world, villager.chunkCoordX, villager.chunkCoordZ);
				}

				if ( province != null )
				{
					CivilizationHandlers.adjustPlayerRep(player, province.civilization, -(int)MathHelper.clamp(event.getAmount()*4,5,villager.getHealth()*4));
				}
			}
		}
		else if ( attacker instanceof EntityGuard )
		{
			if ( victim instanceof EntityCreeper )
			{
				victim.setHealth(0);
			}
			else if ( victim instanceof EntityIronGolem || victim instanceof EntityVillager || victim instanceof EntityToroNpc )
			{
				((EntityGuard) attacker).setAttackTarget(null);
				event.setAmount(0);
				event.setCanceled(true);
			}
			else if ( !(victim instanceof EntityPlayer) )
			{
				event.setAmount(event.getAmount()*(ToroQuestConfiguration.guardDamageMultiplierToMobs + (rand.nextFloat() - 0.5f)/5f ) );
				Province province = CivilizationUtil.getProvinceAt( attacker.getEntityWorld(), attacker.chunkCoordX, attacker.chunkCoordZ );
				if ( province == null )
				{
					event.setAmount(event.getAmount()*ToroQuestConfiguration.guardNerfOutsideProvince);
					return;
				}
				CivilizationDataAccessor worldData = CivilizationsWorldSaveData.get(attacker.world);
				if ( worldData == null )
				{
					return;
				}
				if ( worldData.hasTrophyLord(province.id) )
				{
					event.setAmount(event.getAmount()*2.0F);
				}
			}
		}
		else if (victim instanceof EntityAnimal)
		{
			if (victim instanceof EntityCow)
			{
				List<EntityToro> nearbyToros = victim.getEntityWorld().getEntitiesWithinAABB(EntityToro.class, victim.getEntityBoundingBox().grow(20.0D, 10.0D, 20.0D));
				for (EntityToro toro : nearbyToros)
				{
					toro.setAttackTarget(attacker);
				}
			}
			else if (victim instanceof EntityMule && attacker instanceof EntityPlayer)
			{
				List<EntityCaravan> caravans = victim.getEntityWorld().getEntitiesWithinAABB(EntityCaravan.class, victim.getEntityBoundingBox().grow(20.0D, 10.0D, 20.0D));
				for (EntityCaravan caravan : caravans)
				{
					((EntityToroVillager)caravan).setUnderAttack( attacker );
				}
			}
		}
		else if ( attacker instanceof EntityIronGolem )
		{
			if ( victim instanceof EntityToroNpc || victim instanceof EntityVillager )
			{
				((EntityIronGolem) attacker).setAttackTarget(null);
				event.setAmount(0);
				event.setCanceled(true);
			}
		}
		else if ( attacker instanceof EntityToroMob )
		{
			if( attacker instanceof EntitySentry )
			{
				event.setAmount(event.getAmount()*(ToroQuestConfiguration.banditDamageMultiplier + ((rand.nextFloat() - 0.5f)/5f ))/2 );
			}
			else // if( attacker instanceof EntityOrc )
			{
				event.setAmount(event.getAmount()*(ToroQuestConfiguration.orcDamageMultiplier + ((rand.nextFloat() - 0.5f)/5f ))/2 );
			}
		}
	}

	private EntityLivingBase getAttacker(LivingHurtEvent event)
	{
		try
		{
			return (EntityLivingBase) event.getSource().getTrueSource();
		}
		catch (Exception e)
		{
			return null;
		}
	}

	/*
	private BlockPos findSurface(World world, BlockPos start) {

		int minY = world.getActualHeight();

		BlockPos pos;

		IBlockState blockState;

		for (int y = world.getActualHeight(); y > 0; y--) {

			pos = new BlockPos(start.getX(), y, start.getZ());
			blockState = world.getBlockState(pos);

			if (isLiquid(blockState)) {
				return null;
			}

			if (isGroundBlock(blockState)) {
				if (y < minY) {
					minY = y;
				}

				break;
			}
		}

		return new BlockPos(start.getX(), minY, start.getZ());
	}

	private boolean isLiquid(IBlockState blockState) {
		return blockState.getBlock() == Blocks.WATER || blockState.getBlock() == Blocks.LAVA;
	}

	private boolean isGroundBlock(IBlockState blockState) {

		if (blockState.getBlock() == Blocks.LEAVES || blockState.getBlock() == Blocks.LEAVES2 || blockState.getBlock() == Blocks.LOG
				|| blockState.getBlock() instanceof BlockBush) {
			return false;
		}

		return blockState.isOpaqueCube();

	}
	*/
	
	static class SavedInventory
	{
		NonNullList<ItemStack> mainInventory = NonNullList.<ItemStack>withSize(36, ItemStack.EMPTY);
	    NonNullList<ItemStack> armorInventory = NonNullList.<ItemStack>withSize(4, ItemStack.EMPTY);
	    NonNullList<ItemStack> offHandInventory = NonNullList.<ItemStack>withSize(1, ItemStack.EMPTY);
		int experienceTotal;
	}
	
	private Map<String, SavedInventory> stack = new HashMap<String, SavedInventory>();
  
	@SubscribeEvent
	public void death(LivingDeathEvent event)
	{
		if ( (event.getEntity() instanceof EntityPlayer) )
		{
			EntityPlayer player = (EntityPlayer) event.getEntity();
			if ( player == null )
			{
				return;
			}
			SavedInventory savedInventory = new SavedInventory();
			boolean hasEnderIdol = false;
			int m = 0;
			// =-=-=-=-=-=-= MAIN =-=-=-=-=-=-=
			for ( ItemStack itemStack : player.inventory.mainInventory )
			{
				if ( !hasEnderIdol && itemStack.getItem().equals(Item.getByNameOrId("toroquest:ender_idol")) )
				{
					player.inventory.mainInventory.set(m, ItemStack.EMPTY);
					savedInventory.experienceTotal = (0 + player.experienceTotal);
					hasEnderIdol = true;
				}
				else
				{
					savedInventory.mainInventory.set(m, itemStack);
				}
				m++;
			}
			// =-=-=-=-=-=-= ARMOR =-=-=-=-=-=-=
			int a = 0;
			for ( ItemStack itemStack : player.inventory.armorInventory )
			{
				savedInventory.armorInventory.set(a, itemStack);
				a++;
			}
			// =-=-=-=-=-=-= OFF =-=-=-=-=-=-=
			if ( !hasEnderIdol && player.inventory.offHandInventory.get(0).getItem().equals(Item.getByNameOrId("toroquest:ender_idol") ) )
			{
				player.inventory.offHandInventory.set(0, ItemStack.EMPTY);
				savedInventory.experienceTotal = (0 + player.experienceTotal);
				hasEnderIdol = true;
			}
			else
			{
				savedInventory.offHandInventory.set(0, player.inventory.offHandInventory.get(0));
			}
			if ( hasEnderIdol )
			{
				player.inventory.clear();
				player.closeScreen();
				player.inventory.closeInventory(player);
				player.experienceTotal = 0;
				stack.put( player.getName(), savedInventory );
				player.world.playSound(player, player.posX, player.posY, player.posZ, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.PLAYERS, 1.0F, 1.0F );
				player.world.playSound(player, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ENDEREYE_DEATH, SoundCategory.PLAYERS, 1.0F, 1.0F );
				event.setCanceled(true);
			}
			else
			{
				savedInventory = null;
				//if ( stack.containsKey( player.getName()) )
				try
				{
					stack.remove( player.getName() );
				}
				catch ( Exception e )
				{
					
				}
			}
		}
		else if ( event.getEntity() instanceof IMob || event.getEntity() instanceof EntityMob )
		{
			if ( !(event.getEntity() instanceof EntityLivingBase) )
			{
				return;
			}
			EntityLivingBase mob = (EntityLivingBase)(event.getEntity());
			DamageSource source = event.getSource();
			if ( source == null ) return;
			if ( source.getTrueSource() == null ) return;
			
			if ( !(source.getTrueSource() instanceof EntityPlayer) )
			{
				return;
			}
			
			EntityPlayer player = (EntityPlayer)(source.getTrueSource());
	
			Province province = PlayerCivilizationCapabilityImpl.get(player).getInCivilization();
			
			if ( rand.nextInt(2000) <= ToroQuestConfiguration.artifactDropRate )
			{
				ItemStack item = CivilizationHandlers.randomStolenItem( mob.world, province );
				if ( item == null ) return;
				mob.entityDropItem( item, 1 );
			}
//			if (province == null || province.civilization == null)
//			{
//				return;
//			}
			//CivilizationHandlers.adjustPlayerRep(player, province.civilization, 1);
		}
	}

	@SubscribeEvent
	public void respawn(PlayerEvent.Clone event)
	{
		EntityPlayer player = event.getEntityPlayer();
		if ( player == null )
		{
			return;
		}
		try
		{
			SavedInventory savedIventory = stack.get( player.getName() );
			int m = 0;
			for ( ItemStack itemStack : savedIventory.mainInventory )
			{
				player.inventory.mainInventory.set(m, itemStack);
				m++;
			}
			int a = 0;
			for ( ItemStack itemStack : savedIventory.armorInventory )
			{
				player.inventory.armorInventory.set(a, itemStack);
				a++;
			}
			player.inventory.offHandInventory.set(0, savedIventory.offHandInventory.get(0));
			player.addExperience( savedIventory.experienceTotal );
			player.world.playSound(player, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 0.8F );
			player.world.playSound(player, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ENDERMEN_SCREAM, SoundCategory.PLAYERS, 0.8F, 0.8F );
			stack.remove( player.getName() );
		}
		catch ( Exception e )
		{
			
		}
	}
}
