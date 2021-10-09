package net.torocraft.toroquest.entities;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityMule;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.torocraft.toroquest.ToroQuest;
import net.torocraft.toroquest.civilization.CivilizationHandlers;
import net.torocraft.toroquest.civilization.CivilizationUtil;
import net.torocraft.toroquest.civilization.Province;
import net.torocraft.toroquest.config.ToroQuestConfiguration;
import net.torocraft.toroquest.entities.ai.EntityAIDespawn;
import net.torocraft.toroquest.entities.ai.EntityAISmartTempt;

public class EntityCaravan extends EntityToroVillager implements IMerchant
{

	public static String NAME = "caravan";

	static
	{
		if (ToroQuestConfiguration.specificEntityNames)
		{
			NAME = ToroQuestEntities.ENTITY_PREFIX + NAME;
		}
	}
	
	public boolean escorted = false;
	protected Random rand = new Random();
	protected int despawnTimer = 1200;
	protected int banditSpawns = 1;
	protected boolean backupLeash = true;
	
	protected boolean despawn = false;
	
	@Override
	protected boolean canDespawn()
	{
		return this.despawn;
	}
		
	@Override
	public void setMurder( EntityPlayer player )
	{
		super.setMurder(player);
		this.escorted = true;
		this.writeEntityToNBT(new NBTTagCompound());
		//try {tasks.removeTask( caravanTask );} catch ( Exception e ) {}
	}
		
	@Override
	public void readEntityFromNBT(NBTTagCompound compound)
	{
		this.escorted = compound.getBoolean("escorted");
		this.despawnTimer = compound.getInteger("despawnTimer");
		this.backupLeash = compound.getBoolean("backupLeash");
	    super.readEntityFromNBT(compound);
	}



	@Override
	public void writeEntityToNBT(NBTTagCompound compound)
	{
		compound.setBoolean("escorted", this.escorted);
		compound.setInteger("despawnTimer", this.despawnTimer);
		compound.setBoolean("backupLeash", this.backupLeash);
		super.writeEntityToNBT(compound);
	}

	public static void init(int entityId)
	{
		EntityRegistry.registerModEntity(new ResourceLocation(ToroQuest.MODID, NAME), EntityCaravan.class, NAME, entityId, ToroQuest.INSTANCE, 80,
				3, true, 0x000000, 0xe0d6b9);
	}

	public EntityCaravan(World worldIn)
	{
		super(worldIn, 0);
		//this.tasks.addTask(1, caravanTask);
	}
	
	public void addCaravan()
	{
		if ( !this.world.isRemote )
		{
			EntityMule mule = new EntityMule( this.world );
			mule.addTag(this.getUniqueID().toString());
			mule.addTag("toroquest_caravan");
			//mule.setHorseTamed(true);
			mule.replaceItemInInventory(499, new ItemStack(Item.getItemFromBlock(Blocks.CHEST)));
			mule.replaceItemInInventory(400, new ItemStack((Items.SADDLE)));
			
			if ( rand.nextInt(4) == 0 )
			{
				for ( int i = rand.nextInt(4)+4; i > 0; i-- ) mule.replaceItemInInventory(500+rand.nextInt(18), new ItemStack((Items.BREAD),1));
				for ( int i = rand.nextInt(4)+4; i > 0; i-- ) mule.replaceItemInInventory(500+rand.nextInt(18), new ItemStack((Items.WHEAT),5));
			}
			else if ( rand.nextInt(4) == 0 )
			{
				for ( int i = rand.nextInt(4)+4; i > 0; i-- ) mule.replaceItemInInventory(500+rand.nextInt(18), new ItemStack((Items.EMERALD),5));
			}
			else if ( rand.nextInt(4) == 0 )
			{
				//for ( int i = rand.nextInt(3)+3; i > 0; i-- ) mule.replaceItemInInventory(500+rand.nextInt(18), new ItemStack((Blocks.),rand.nextInt(5)+1));
				for ( int i = rand.nextInt(4)+4; i > 0; i-- ) mule.replaceItemInInventory(500+rand.nextInt(18), new ItemStack((Items.RABBIT_HIDE),5));
				for ( int i = rand.nextInt(4)+2; i > 0; i-- ) mule.replaceItemInInventory(500+rand.nextInt(18), new ItemStack((Items.LEATHER),5));
			}
			else
			{
				for ( int i = rand.nextInt(3)+2; i > 0; i-- ) mule.replaceItemInInventory(500+rand.nextInt(18), new ItemStack((Items.BEETROOT),5));
				for ( int i = rand.nextInt(4); i > 0; i-- ) mule.replaceItemInInventory(500+rand.nextInt(18), new ItemStack((Items.APPLE),5));
				for ( int i = rand.nextInt(4); i > 0; i-- ) mule.replaceItemInInventory(500+rand.nextInt(18), new ItemStack((Items.CARROT),5));
				for ( int i = rand.nextInt(4); i > 0; i-- ) mule.replaceItemInInventory(500+rand.nextInt(18), new ItemStack((Items.POTATO),5));
			}
			
			mule.setPosition(this.posX, this.posY, this.posZ);
			mule.tasks.addTask(1, new EntityAISmartTempt( mule, 1.5, Items.AIR ) );
			mule.tasks.addTask(0, new EntityAIDespawn(mule));
			world.spawnEntity(mule);
			mule.removePassengers();
		}
        // this.tasks.addTask( 1, this.caravanTask );
	}
	
	// protected EntityCaravan e = this;
	
	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

	
	@Override
	public void onLivingUpdate()
	{
		super.onLivingUpdate();

		if ( this.world.isRemote || this.escorted )
		{
			return;
		}
		
		if ( !this.escorted && ticksExisted % 20 == 0 )
		{
			if ( this.underAttack != null ) 
			{
				if ( --this.blockedTrade <= 0 )
				{
					this.underAttack = null;
				}
				else
				{
					return;
				}
			}
    		
			List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(this.getPosition()).grow(25, 15, 25), new Predicate<EntityPlayer>()
			{
				public boolean apply(@Nullable EntityPlayer entity)
				{
					return true;
				}
			});
			
			if ( players.isEmpty() || this.world.getWorldTime() >= 21900 && this.world.getWorldTime() <= 22000 )
			{
    			if ( --despawnTimer < 0 )
    			{
    				this.despawn = true;
    				this.setHealth(0);
    				setDead();
    			}
    			return;
			}
			
			List<EntityMule> mules = world.getEntitiesWithinAABB(EntityMule.class, new AxisAlignedBB(getPosition()).grow(25, 15, 25), new Predicate<EntityMule>()
			{	
    			public boolean apply(@Nullable EntityMule entity)
    			{
    				return true;
    			}
	    	});
    		for (EntityMule mule: mules)
    		{
    			if ( mule.isEntityAlive() && mule.hasChest() && mule.isHorseSaddled() && mule.getTags().contains( this.entityUniqueID.toString() ) )
    			{
	    			if ( mule.getLeashHolder() == null )
        			{
	    				if ( despawnTimer > 600 )
	    				{
	        				mule.setLeashHolder(this, true);
	    				}
	    				if ( backupLeash && !mule.getLeashed() )
	    				{
	        				mule.setLeashHolder(this, true);
	        				backupLeash = false;
	    				}
        			}
	    			if ( rand.nextBoolean() && !mule.getPassengers().isEmpty() )
    				{
	    				mule.removePassengers();
		            	this.playSound( SoundEvents.ENTITY_VILLAGER_NO, 1.0F, 1.0F );
    				}
    				if ( mule.getDistance(this) > 9 )
    				{
    					mule.getNavigator().tryMoveToEntityLiving(this, 1.5 );
    				}
    			}
    		}
			
			if ( banditSpawns > 0 && rand.nextInt(100) == 0 )
			{
				if ( spawnSentryNearPlayer() )
				{
					banditSpawns--;
				}
			}
			
			Province province = CivilizationUtil.getProvinceAt(this.world, this.chunkCoordX, this.chunkCoordZ);
			
			if ( province != null && Math.abs(this.posX - province.getCenterX()) + Math.abs(this.posZ - province.getCenterZ()) <= 86 )
			{
    			float multiplier = 1.0f;
	    		for ( EntityMule mule: mules )
	    		{
	    			if ( mule != null && mule.isEntityAlive() && mule.getTags().contains( this.entityUniqueID.toString() ) ) // && mule.getLeashHolder() != null && mule.getLeashHolder() == creature )
	    			{
	    				mule.removePassengers();
        	        	mule.clearLeashed(true, false);
        	        	this.clearLeashed(true, false);
	    				if ( ToroQuestConfiguration.removeMuleOnCaravanEscort )
	    				{
	    					try
	    					{
    	    					mule.removePassengers();
	    					}
	    					catch ( Exception e )
	    					{
	    						
	    					}
	    					mule.setDead();
	    				}
        	    		multiplier += 0.25f;
    				}
				}
				for ( EntityPlayer player: players )
        		{
    	    		ITextComponent message = new TextComponentString( "§lCaravan Escorted!§r" );
    	    		this.escorted = true;
    				player.sendStatusMessage(message, true);
    	        	playSound(SoundEvents.ENTITY_MULE_AMBIENT, 1.0F, 1.0F);
    	        	playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
    	    		CivilizationHandlers.adjustPlayerRep(player, province.civilization, (int)(ToroQuestConfiguration.escortCaravanRepGain*multiplier));
    			}
	        	this.writeEntityToNBT(new NBTTagCompound());
			}
			else for ( EntityPlayer player: players )
			{
				this.getNavigator().tryMoveToEntityLiving(player, 0.6);
				ITextComponent message = new TextComponentString( "§oEscorting Caravan§r" );
				player.sendStatusMessage(message, true);
				despawnTimer = 600;
			}
		}
		
		
	}
	
//	protected EntityAISmartTempt caravanTask = new EntityAISmartTempt(this, 0.5D, Items.AIR)
//	{
//    	@Override
//		public boolean shouldExecute()
//	    {
//	        if ( escorted ) // || ( underAttack != null && underAttack instanceof EntityPlayer ) || blockedTrade > 0 )
//	        {
//		        return false;
//	        }
//	        return super.shouldExecute();
//	    }
//    	
//    	@Override
//    	protected boolean isTempting(ItemStack stack)
//        {
//            return !escorted;
//        }
//    	
//    	@Override
//    	public void updateTask()
//    	{
//    		if ( !escorted && world.getWorldTime() % 20 == 0 )
//    		{
//    			if ( creature.underAttack != null ) 
//    			{
//    				creature.underAttack = null;
//    				return;
//    			}
//    			List<EntityMule> mules = world.getEntitiesWithinAABB(EntityMule.class, new AxisAlignedBB(getPosition()).grow(32, 16, 32), new Predicate<EntityMule>()
//				{	
//	    			public boolean apply(@Nullable EntityMule entity)
//	    			{
//	    				return true;
//	    			}
//		    	});
//	    		for (EntityMule mule: mules)
//	    		{
//	    			if ( mule.isEntityAlive() && mule.hasChest() && mule.isHorseSaddled() && mule.getTags().contains( e.entityUniqueID.toString() ) )
//	    			{
//	    				mule.removePassengers();
//		    			if ( mule.getLeashHolder() == null )
//	        			{
//		    				if ( despawnTimer > 600 )
//		    				{
//		        				mule.setLeashHolder(creature, true);
//		    				}
//		    				else if ( backupLeash )
//		    				{
//		        				mule.setLeashHolder(creature, true);
//		        				backupLeash = false;
//		    				}
//	        			}
//	    				if ( mule.getDistance(creature) > 9 )
//	    				{
//	    					mule.getNavigator().tryMoveToEntityLiving(creature, 2.0 );
//	    				}
//	    			}
//	    		}
//	    		
//    			List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(e.getPosition()).grow(32, 16, 32), new Predicate<EntityPlayer>()
//    			{
//    				public boolean apply(@Nullable EntityPlayer entity)
//    				{
//    					return true;
//    				}
//    			});
//    			
//    			if ( players.size() < 1 )
//    			{
//	    			if ( --despawnTimer < 0 )
//	    			{
//	    				setDead();
//	    			}
//	    			return;
//    			}
//
//    			if ( rand.nextInt(40) == 0 )
//    			{
//    	    		spawnSentryNearPlayer();
//    			}
//    			
//    			Province province = CivilizationUtil.getProvinceAt(e.world, e.chunkCoordX, e.chunkCoordZ);
//    			
//    			if ( province != null && Math.abs(e.posX - province.getCenterX()) + Math.abs(e.posZ - province.getCenterZ()) <= 86 )
//    			{
//	    			float multiplier = 1.0f;
//					escorted = true;
//    	        	writeToNBT(new NBTTagCompound());
//    	    		for ( EntityMule mule: mules )
//    	    		{
//    	    			if ( mule != null && mule.isEntityAlive() && mule.getTags().contains( e.entityUniqueID.toString() ) ) // && mule.getLeashHolder() != null && mule.getLeashHolder() == creature )
//    	    			{
//    	    				mule.removePassengers();
//	        	        	mule.clearLeashed(true, false);
//	        	        	e.clearLeashed(true, false);
//		    				if ( ToroQuestConfiguration.removeMuleOnCaravanEscort )
//		    				{
//		    					try
//		    					{
//	    	    					mule.getRidingEntity().dismountRidingEntity();
//		    					}
//		    					catch ( Exception e )
//		    					{
//		    						
//		    					}
//		    					mule.setDead();
//		    				}
//	        	    		multiplier += 0.5f;
//	    				}
//    				}
//    				for ( EntityPlayer player: players )
//	        		{
//        	    		ITextComponent message = new TextComponentString( "§lCaravan Escorted!§r" );
//        				player.sendStatusMessage(message, true);
//        	        	playSound(SoundEvents.ENTITY_MULE_AMBIENT, 1.0F, 1.0F);
//        	        	playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
//        	    		CivilizationHandlers.adjustPlayerRep(player, province.civilization, (int)(ToroQuestConfiguration.escortCaravanRepGain*multiplier));
//        			}
//    			}
//				for ( EntityPlayer player: players )
//    			{
//    				ITextComponent message = new TextComponentString( "§oEscorting Caravan§r" );
//    				player.sendStatusMessage(message, true);
//					despawnTimer = 600;
//    			}
//        		super.updateTask();
//    		}
//    	}
//    };
	
	protected boolean spawnSentryNearPlayer()
	{
		World world = this.world;
		
		if ( world.isRemote ) 
		{
			return false;
		}

		try
		{
			int range = 50;
			{
				Province province = CivilizationUtil.getProvinceAt(world, this.chunkCoordX, this.chunkCoordZ);
				
				if ( province != null )
				{
					return false;
				}
				
				int villageCenterX = (int)this.posX;
				int villageCenterZ = (int)this.posZ;
				
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
				
				BlockPos loc = new BlockPos(x,world.getHeight()/2,z);
				
				BlockPos banditSpawnPos = CivilizationHandlers.findSpawnLocationFrom(world, loc);
				
				if (banditSpawnPos == null)
				{
					return false;
				}
				
				province = CivilizationUtil.getProvinceAt(world, banditSpawnPos.getX()/16, banditSpawnPos.getZ()/16);
				
				if ( province != null )
				{
					return false;
				}
				
				if ( !world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(banditSpawnPos).grow(20, 10, 20)).isEmpty() )
				{
					return false;
				}
				
				if ( ToroQuestConfiguration.orcsAreNeutral || rand.nextBoolean() )
				{
					for ( int i = rand.nextInt(3) + 2; i > 0; i-- )
					{
						EntitySentry entity = new EntitySentry(world);
						entity.despawnTimer--;
						entity.setPosition(banditSpawnPos.getX() + 0.5,banditSpawnPos.getY() + 0.1, banditSpawnPos.getZ() + 0.5 );
						entity.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entity)), (IEntityLivingData) null);
						world.spawnEntity(entity);
						entity.setAttackTarget(this);
						entity.setRaidLocation(villageCenterX, villageCenterZ);
					}
				}
				else
				{
					for ( int i = rand.nextInt(3) + 2; i > 0; i-- )
					{
						EntitySentry entity = new EntityOrc(world);
						entity.despawnTimer--;
						entity.setPosition(banditSpawnPos.getX() + 0.5,banditSpawnPos.getY() + 0.1, banditSpawnPos.getZ() + 0.5 );
						entity.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entity)), (IEntityLivingData) null);
						world.spawnEntity(entity);
						entity.setAttackTarget(this);
						entity.setRaidLocation(villageCenterX, villageCenterZ);
					}
				}
				return true;
			}
		}
		catch (Exception e)
		{
			System.out.println("ERROR SPAWNING EntityBandit: " + e);
			return false;
		}
	}
}