package net.torocraft.toroquest.entities;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.INpc;
import net.minecraft.entity.ai.EntityAILookAtTradePlayer;
import net.minecraft.entity.ai.EntityAIMoveIndoors;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITradePlayer;
import net.minecraft.entity.ai.EntityAIVillagerInteract;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.torocraft.toroquest.ToroQuest;
import net.torocraft.toroquest.civilization.CivilizationHandlers;
import net.torocraft.toroquest.civilization.CivilizationType;
import net.torocraft.toroquest.civilization.CivilizationUtil;
import net.torocraft.toroquest.civilization.Province;
import net.torocraft.toroquest.civilization.player.PlayerCivilizationCapabilityImpl;
import net.torocraft.toroquest.civilization.quests.QuestRecruit;
import net.torocraft.toroquest.civilization.quests.QuestTradeWithVillagers;
import net.torocraft.toroquest.config.ToroQuestConfiguration;
import net.torocraft.toroquest.entities.ai.EntityAIAvoidBanditPlayer;
import net.torocraft.toroquest.entities.ai.EntityAIAvoidEnemies;
import net.torocraft.toroquest.entities.ai.EntityAISmartTempt;
import net.torocraft.toroquest.entities.ai.EntityAIToroVillagerMate;
import net.torocraft.toroquest.entities.trades.ToroVillagerTrades;

public class EntityToroVillager extends EntityVillager implements INpc, IMerchant
{
	public int canTalk;
	
	public boolean uiClick = false;
	
	@Override
	public int getHorizontalFaceSpeed()
	{
		return 10;
	}
	
	@Override
	protected boolean canDespawn()
	{
		return false;
	}
	
//	@Override
//	public void setScaleForAge(boolean child)
//    {
//        this.setScale(child ? 0.5F : 1.0F); // (70000.0F-this.getGrowingAge()) / 100000.0F : 1.0F)
//    }
	
//	public void setAnnoyed()
//	{
//		this.isAnnoyed = true;
//		this.isAnnoyedTimer = 10;
//	}
	
//	@Override
//	public boolean canEntityBeSeen(Entity entityIn)
//    {
//		double xm = 0.25;
//		if ( (entityIn.posX - this.posX) > 0 )
//		{
//			xm = -0.25;
//		}
//		double zm = 0.25;
//		if ( (entityIn.posZ - this.posZ) > 0 )
//		{
//			zm = -0.25;
//		}
//		RayTraceResult result = this.world.rayTraceBlocks(new Vec3d(this.posX + xm, this.posY + (double)this.getEyeHeight(), this.posZ + zm), new Vec3d(entityIn.posX, entityIn.posY + (double)entityIn.getEyeHeight(), entityIn.posZ), false, true, false);
//		if ( result == null )
//		{
//			return true;
//		}
//		BlockPos target = result.getBlockPos();
//		if ( target != null && (!world.getBlockState(target).isOpaqueCube() || !world.getBlockState(target).isFullBlock()) )
//		{
//			return true;
//		}
//						result = this.world.rayTraceBlocks(new Vec3d(this.posX + xm, this.posY + 0.25, this.posZ + zm), 								new Vec3d(entityIn.posX, entityIn.posY + (double)entityIn.getEyeHeight(), entityIn.posZ), false, true, false);
//		if ( result == null )
//		{
//			return true;
//		}
//		target = result.getBlockPos();
//		if ( target != null && (!world.getBlockState(target).isOpaqueCube() || !world.getBlockState(target).isFullBlock()) )
//		{
//			return true;
//		}
//		return false;
//    }
	

	public int blockedTrade = 0;
	public EntityLivingBase underAttack = null;
	
	public void blockTrade()
	{
		if ( this.blockedTrade < 8 )
		{
			this.blockedTrade += 4;
		}
	}
	
	public void setUnderAttack( EntityLivingBase entity )
	{
		if ( entity instanceof EntityPlayer )
		{
			this.underAttack = (EntityPlayer)entity;
			if ( this.blockedTrade < 16 )
			{
				this.blockedTrade += 4;
			}
		}
		else
		{
			this.underAttack = null;
			if ( this.blockedTrade < 8 )
			{
				this.blockedTrade += 2;
			}
		}
	}
	
	public void setMurder( EntityPlayer player )
	{
		this.underAttack = player;
		this.blockedTrade = 64;
	}
	
	// public boolean canPickPocket = true;
	
	
			// Farmer			1
			// Miner			2
			// Lumberjack		3
			// Jeweler
			// Blacksmith
			// Leatherworker
			// Fletcher
			// Fisherman
			// Builder
			// Alchemist
			// Nitwit
			// Cleric
			// Butcher
			// Architect
			// Lady
			// Wizard
	
	
	// Jeweler
	// Farmer
	// Blacksmith
	// Leatherworker
	// Fletcher
	// Fisherman
	// Builder
	// Lumberjack
	// Miner
	// Alchemist
	// Nitwit
	// Cleric
	// Butcher
	// Architect
	
	// Lady
	
	// Wizard
	
	@Override
	protected void initEntityAI()
    {              
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIAvoidBanditPlayer<EntityPlayer>(this, EntityPlayer.class, 12.0F, 0.5D, 0.65D));
		this.tasks.addTask(1, new EntityAIAvoidEnemies<EntityLiving>(this, 12.0F, 0.5D, 0.65D));
		this.tasks.addTask(1, new EntityAIPanic(this, 0.65D)
		{
			
			@Override
			public boolean shouldExecute()
		    {
		        if ( (underAttack != null && underAttack.isEntityAlive() && this.creature.getDistance(underAttack) < 16) || isBurning() )
		        {
		            return this.findRandomPosition();
		        }
		        return false;
		    }
			
			@Override
			protected boolean findRandomPosition()
		    {
		        Vec3d vec3d = null;

				if ( underAttack != null && underAttack.getPositionVector() != null )
				{
			        vec3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.creature, 16, 8, underAttack.getPositionVector());
				}
				else
				{
			        vec3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.creature, 16, 8, this.creature.getPositionVector());
				}
				
		        if ( vec3d == null )
		        {
		            return false;
		        }
		        else
		        {
		            this.randPosX = vec3d.x;
		            this.randPosY = vec3d.y;
		            this.randPosZ = vec3d.z;
		            return true;
		        }
		    }
		});
        this.tasks.addTask(3, new EntityAIToroVillagerMate(this));
        this.tasks.addTask(4, new EntityAITradePlayer(this));
        this.tasks.addTask(5, new EntityAILookAtTradePlayer(this));
//        this.tasks.addTask(6, new EntityAISmartTempt(this, 0.4D, Item.getByNameOrId("toroquest:recruitment_papers"))
//		{
//        	@Override
//			public boolean shouldExecute()
//		    {
//		        if ( (underAttack != null && underAttack.isEntityAlive()) || blockedTrade > 0 || isBurning() || isMating() )
//		        {
//			        return false;
//		        }
//		        return super.shouldExecute();
//		    }
//		});
        this.tasks.addTask(6, new EntityAISmartTempt(this, 0.4D, Items.EMERALD)
        {
        	@Override
			public boolean shouldExecute()
		    {
        		super.shouldExecute();
		        if ( (underAttack != null && underAttack.isEntityAlive()) || blockedTrade > 0 || isBurning() || isMating() )
		        {
			        return false;
		        }
		        return super.shouldExecute();
		    }
		});
        this.tasks.addTask(7, new EntityAIMoveIndoors(this));
        this.tasks.addTask(8, new EntityAIRestrictOpenDoor(this));
        this.tasks.addTask(9, new EntityAIOpenDoor(this, true));
        this.tasks.addTask(10, new EntityAIMoveTowardsRestriction(this, 0.6D));
        // this.tasks.addTask(9, new EntityAIFollowGolem(this));
//        this.tasks.addTask(10, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F)
//        {
//        	@Override
//        	public boolean shouldExecute()
//            {
//				if ( isMating() )
//				{
//					return false;
//				}
//				return super.shouldExecute();
//            }
//        });
        this.tasks.addTask(11, new EntityAIVillagerInteract(this));
        this.tasks.addTask(12, new EntityAIWanderAvoidWater(this, 0.5D)
        {
        	@Override
        	public boolean shouldExecute()
            {
                if ( !this.mustUpdate )
                {
                    if ( this.entity.getIdleTime() >= 100 )
                    {
                        return false;
                    }

                    if ( !this.entity.isInWater() && !this.entity.isInLava() && this.entity.getRNG().nextInt(this.executionChance) != 0)
                    {
                        return false;
                    }
                }

                Vec3d vec3d = this.getPosition();

                if ( vec3d == null )
                {
                    return false;
                }
                else
                {
                    this.x = vec3d.x;
                    this.y = vec3d.y;
                    this.z = vec3d.z;
                    this.mustUpdate = false;
                    return true;
                }
            }
        	
        	@Override
            protected Vec3d getPosition()
            {
        		if ( EntityToroVillager.this.isMating() )
                {
                	return null;
                }
        		if ( !this.entity.hasPath() && ( this.entity.isInWater() || this.entity.isInLava() ) )
                {
                    Vec3d vec3d = RandomPositionGenerator.getLandPos(this.entity, 15, 7);
                    return vec3d == null ? super.getPosition() : vec3d;
                }
                else
                {
                    return this.entity.getRNG().nextFloat() >= this.probability ? RandomPositionGenerator.getLandPos(this.entity, 10, 7) : super.getPosition();
                }
            }
        });
        this.tasks.addTask(13, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F)
        {
        	@Override
        	public boolean shouldExecute()
            {
				if ( isMating() )
				{
					return false;
				}
				return super.shouldExecute();
            }
        });
    }
	
	@Override
	public ITextComponent getDisplayName()
    {
		return super.getDisplayName();
    }
	
	@Override
	public String getName()
    {
        {
            String s = EntityList.getEntityString(this);

            if (s == null)
            {
                s = "generic";
            }

            return I18n.translateToLocal("entity." + s + ".name");
        }
    }

	@Override
	public boolean processInteract( EntityPlayer player, EnumHand hand )
	{		
		if ( world == null || world.isRemote || player == null || hand == null || !this.isEntityAlive() || this.isTrading() || this.isChild() || this.isMating() )
    	{
			return false;
    	}
				
		for ( ItemStack itemStack : player.getArmorInventoryList() )
		{
			if ( itemStack.getItem().equals(Item.getByNameOrId("toroquest:bandit_helmet") ) || itemStack.getItem().equals(Item.getByNameOrId("toroquest:legendary_bandit_helmet") ) )
			{
				if ( this.canTalk < 1 )
				{
					this.playSound(SoundEvents.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
					this.canTalk = 2;
				}
				this.setUnderAttack(player);
				return false;
			}
		}
		
		RepData repData = getReputation(player);
		
		if ( repData == null || repData.civ == null || repData.rep == null )
		{
			if ( this.canTalk < 1 )
			{
				this.playSound(SoundEvents.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
				this.canTalk = 2;
			}
			return false;
		}
		
		// Cheeky way of saving NBT data, career types will not work without this code block!
		if ( this.job == 0 )
		{
			super.getRecipes(player);
	        this.setCustomer(null);
			NBTTagCompound compound = new NBTTagCompound();
	        this.writeEntityToNBT(compound);
		}
		
		ItemStack itemstack = player.getHeldItem(hand);
		Item item = itemstack.getItem();
		
		if ( (this.underAttack == player) || ( repData.rep != null && repData.rep < -50 ) )
		{
			if ( this.canTalk < 1 )
			{
	    		this.callForHelp(player, true);
				this.playSound(SoundEvents.ENTITY_VILLAGER_NO, 1.2F, 0.9F);
				this.canTalk = 2;
			}
			return false;
		}

        if ( ToroQuestConfiguration.recruitVillagers && player.isSneaking() && item.equals(Item.getByNameOrId("toroquest:recruitment_papers")) )
        {
        	if ( repData.rep != null && repData.rep >= 0 && this.underAttack == null && this.blockedTrade < 1 )
    		{
	        	playSound(SoundEvents.ENTITY_ILLAGER_CAST_SPELL, 1.2F, 1.2F);
	        	playSound(SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, 1.0F, 1.0F);
	        	playSound(SoundEvents.BLOCK_ANVIL_USE, 0.6F, 1.0F);
	        	player.setHeldItem(hand, new ItemStack(item, itemstack.getCount()-1 ));
				EntityGuard newEntity = new EntityGuard(world);
				newEntity.setPosition(this.posX, this.posY, this.posZ);
				newEntity.onInitialSpawn(this.world.getDifficultyForLocation(new BlockPos(this.getPosition())), (IEntityLivingData) null);
				this.setDead();
				newEntity.actionTimer = 1;
				world.spawnEntity(newEntity);
    			newEntity.playTameEffect(false);
    			newEntity.world.setEntityState(newEntity, (byte)6);
				// l spawning uuid?
				newEntity.setCivilization(repData.civ);
				newEntity.setMeleeWeapon();
				CivilizationHandlers.adjustPlayerRep(player, repData.civ, ToroQuestConfiguration.recruitGuardRepGain);
				// QUEST
				try
				{
					QuestRecruit.INSTANCE.onRecruit(player);
				}
				catch ( Exception e )
				{
					
				}
				newEntity.chat(newEntity, player, "civvillagerrecruit", repData.civ.getDisplayName(player));
    		}
        	else if ( this.canTalk <= 0 )
			{
    			this.playTameEffect(false);
    			this.playSound(SoundEvents.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
    			this.canTalk = 2;
			}
        	return false;
        }
        
    	
    	if ( this.blockedTrade > 0 )
		{
    		if ( this.canTalk <= 0 )
			{
				this.playSound(SoundEvents.ENTITY_VILLAGER_NO, 1.2F, 0.9F);
				this.canTalk = 1;
			}
    		return false;
		}
		else
		{
			// TRADE
			this.getRecipes(player);
			
			if ( this.buyingList == null || this.buyingList.isEmpty() )
			{
				if ( this.canTalk <= 0 )
    			{
    				this.playSound(SoundEvents.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
    				this.canTalk = 1;
    			}
                return false;
			}
			else
            {
                this.setCustomer(player);
                player.displayVillagerTradeGui(this);

    			if ( this.canTalk <= 0 )
    			{
    				this.playSound(SoundEvents.ENTITY_VILLAGER_TRADING, 1.0F, 1.0F);
    				this.canTalk = 1;
    			}
    			return true;
            }
			
		}
	}
	
	protected void playTameEffect(boolean play)
    {
        EnumParticleTypes enumparticletypes = EnumParticleTypes.HEART;
        
        // TODO chat bubbles?

        if (!play)
        {
            enumparticletypes = EnumParticleTypes.SMOKE_NORMAL;
        }

        for (int i = 0; i < 7; ++i)
        {
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;
            this.world.spawnParticle(enumparticletypes, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2);
        }
    }
	
	public void callForHelp( EntityLivingBase attacker, boolean attackThem )
	{
//		if ( this.world.isRemote )
//		{
//			return;
//		}
		
		this.setUnderAttack(attacker);
		
		List<EntityToroVillager> villagers = world.getEntitiesWithinAABB(EntityToroVillager.class, new AxisAlignedBB(getPosition()).grow(12, 8, 12), new Predicate<EntityToroVillager>()
		{
			public boolean apply(@Nullable EntityToroVillager entity)
			{
				return true;
			}
		});

		for (EntityToroVillager villager : villagers)
		{
			if ( villager.canEntityBeSeen(attacker) )
			{
				villager.setUnderAttack(attacker);
			}
		}
		
		List<EntityGuard> guards = world.getEntitiesWithinAABB(EntityGuard.class, new AxisAlignedBB(getPosition()).grow(15, 10, 15), new Predicate<EntityGuard>()
		{
			public boolean apply(@Nullable EntityGuard entity)
			{
				return true;
			}
		});
		
		boolean flag = false;
		
		for (EntityGuard guard : guards)
		{
			if ( guard.getAttackTarget() == null )
			{
				this.getNavigator().tryMoveToEntityLiving(guard, 0.7F);
				
				if ( attacker instanceof EntityPlayer ) 
				{
					guard.setAnnoyed( (EntityPlayer)attacker );
					if ( !flag && guard.actionReady() && guard.getDistance(attacker) <= 8.0D )
					{
						guard.chat((EntityPlayer)attacker, "attackvillager", null);
						flag = true;
					}
				}
				
				if ( guard.isAnnoyed() || attackThem )
				{
					guard.setAttackTarget(attacker);
				}
			}
		}
		
	}
	
	// guards move to the player
	public void reportToGuards( EntityPlayer player )
	{
		List<EntityToroNpc> guards = world.getEntitiesWithinAABB(EntityToroNpc.class, new AxisAlignedBB(getPosition()).grow(15, 15, 15), new Predicate<EntityToroNpc>()
		{
			public boolean apply(@Nullable EntityToroNpc entity)
			{
				return true;
			}
		});
		Collections.shuffle(guards);
		for (EntityToroNpc guard : guards)
		{
			if ( !guard.inCombat )
			{
				this.getNavigator().tryMoveToEntityLiving(guard, 0.5D);
				guard.getNavigator().tryMoveToEntityLiving(player, 0.5D);
			}
		}
	}
	
	@Override
	public MerchantRecipeList getRecipes(EntityPlayer player)
	{
//		if ( this.buyingList == null )
//      {
        this.buyingList = this.createTradesBaseOnRep(player);
//      }

        return net.minecraftforge.event.ForgeEventFactory.listTradeOffers(this, player, buyingList);
	}
	
    private MerchantRecipeList buyingList;
	boolean hitSafety = false;
	
	@Override
	public void onLivingUpdate()
	{

		super.onLivingUpdate();
		this.uiClick = true;
		
		if ( this.world.isRemote )
		{
			return;
		}
		
//		livingUpdateTimer++;
//		livingUpdateTimer %= 100;
//		if ( livingUpdateTimer == 0 )
		
//		if ( this.world.isRemote )
//		{
//			return;
//		}


		if ( this.ticksExisted % 100 == 0 )
		{
//			if ( world % 1200 == 0 )
//			{
//				if ( this.maxTrades > 0 )
//				{
//					this.maxTrades--;
//				}
//			}
			if ( this.getHealth() >= this.getMaxHealth() )
			{
				if ( this.blockedTrade <= 0 )
				{
					this.hitSafety = true;
				}
			}
			else this.heal(1.0f);
			
			if ( this.canTalk > 0 )
			{	
				this.canTalk--;
			}
			
			if ( this.blockedTrade > 0 )
    		{
    			this.blockedTrade--;
    		}
			
			if ( this.blockedTrade <= 0 )
			{
				this.underAttack = null;
			}
		}
	}

	// Create trades from toroquest config
	protected MerchantRecipeList createTradesBaseOnRep(EntityPlayer player)
	{
		RepData repData = getReputation(player);
		
		if ( repData == null || repData.civ == null || repData.rep == null || repData.rep < -50 ) // TODO if job == null
		{
			return new MerchantRecipeList();
		}
		
        // this.getProfessionForge().getCareer(this.careerId).getName()
		// System.out.println( "job name: " + this.jobName + " " + this.varient + " " + this.maxTrades);
		
		return ToroVillagerTrades.trades(this, player, repData.rep, repData.civ, this.jobName, ""+this.varient );
	}
	
	public ItemStack treasureMap = null;
	
	@Override
	public boolean canBeLeashedTo(EntityPlayer player)
	{
		this.callForHelp( player, false );
		if ( !this.getLeashed() )
		{
			RepData repData = getReputation(player);
			if ( repData != null )
			{
				CivilizationHandlers.adjustPlayerRep(player, repData.civ, -ToroQuestConfiguration.leashVillagerRepLoss);
			}
		}
		return true;
	}
	
	public EntityToroVillager(World worldIn)
	{
		super(worldIn);
		this.setSize(0.59F, 1.85F);
		this.stepHeight = 1.05F;
        NBTTagCompound compound = new NBTTagCompound();
        this.writeEntityToNBT(compound);
	}
	
	public EntityToroVillager(World worldIn, int professionId )
	{
		super(worldIn, professionId);
        this.setSize(0.59F, 1.85F);
		this.stepHeight = 1.05F;
        NBTTagCompound compound = new NBTTagCompound();
        this.writeEntityToNBT(compound);
	}
	
	// public Integer maxTrades = null;
	public Integer varient = null;
	public Integer job = null;
	public String jobName = null;
	
//    public void getJob(NBTTagCompound compound)
//    {
//    	this.writeEntityToNBT(compound);
//		try
//		{
//			this.job = compound.getInteger("Career");
//			this.jobName = this.getProfessionForge().getCareer(this.job-1).getName();
//		}
//		catch ( Exception e )
//		{
//			
//		}
//    }
	
	@Override
	public boolean attackEntityAsMob(Entity victim)
	{
		this.setAttackTarget(null);
		this.setRevengeTarget(null);
		return false;
	}
    
    @Override
    public void readEntityFromNBT(NBTTagCompound compound)
    {
    	super.readEntityFromNBT(compound);
		//this.maxTrades = compound.getInteger("MaxTrades");
        this.varient = compound.getInteger("Varient");
        this.job = compound.getInteger("Career");
        this.jobName = compound.getString("JobName");
    }
    
    @Override
    public void writeEntityToNBT(NBTTagCompound compound)
    {
    	super.writeEntityToNBT(compound);
    	if ( this.varient == null )
        {
        	this.varient = rand.nextInt(5);
        }
        compound.setInteger("Varient", this.varient);
        
//      if ( this.maxTrades == null )
//      {
//          this.maxTrades = 0;
//      }
//      compound.setInteger("MaxTrades", this.maxTrades);

        
        this.job = compound.getInteger("Career");
        {
        	this.jobName = this.getProfessionForge().getCareer(this.job-1).getName();
        }
        compound.setString("JobName", this.jobName);
    }
    
	static class RepData
	{
		CivilizationType civ = null;
		Integer rep = null;
	}

	protected RepData getReputation(EntityPlayer player)
	{
		RepData repData = new RepData();
		
		if ( player == null )
		{
			return null;
		}

		Province province = CivilizationUtil.getProvinceAt( player.world, player.chunkCoordX, player.chunkCoordZ);

		if ( province == null || province.civilization == null )
		{
			return null;
		}
		
		repData.civ = province.civilization;

		repData.rep = PlayerCivilizationCapabilityImpl.get(player).getReputation( repData.civ );
				
		return repData;
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		if ( this.world.isRemote )
        {
            return false;
        }
		
		if ( source.getTrueSource() instanceof EntityToroNpc )
		{
			amount = 0;
			return false;
		}
		if ( source.getTrueSource() == null )
		{
			//if ( this.isInLava() )
			{
		        Vec3d vec3d = RandomPositionGenerator.getLandPos(this, 8, 4);
	            if ( vec3d != null )
	            {
			        this.getNavigator().tryMoveToXYZ(vec3d.x, vec3d.y, vec3d.z, 0.5D);
	            }
			}
			return super.attackEntityFrom(source, amount);
		}
		else if ( source.getTrueSource() instanceof EntityLivingBase )
		{
			EntityLivingBase e = (EntityLivingBase)source.getTrueSource();
			
			if ( this.hitSafety )
			{
				this.hitSafety = false;
				this.playSound(SoundEvents.BLOCK_CLOTH_BREAK, 1.0F, 1.0F);
				return false;
			}
			
			if ( e instanceof EntityPlayer )
			{
				List<EntityLivingBase> enemies = e.getEntityWorld().getEntitiesWithinAABB(EntityGuard.class, new AxisAlignedBB(this.getPosition()).grow(16, 12, 16), new Predicate<EntityLivingBase>()
				{
					public boolean apply(@Nullable EntityLivingBase entity)
					{
						if ( entity instanceof IMob || entity instanceof EntityMob )
						{
							return true;
						}
						else
						{
							return false;
						}
					}
				});
				
				if ( !enemies.isEmpty() )
				{
					return false;
				}
			}
			this.callForHelp(e, true);
		}
		return super.attackEntityFrom(source, amount);
	}
	
	public static String NAME = "toro_villager";

	static
	{
		if (ToroQuestConfiguration.specificEntityNames)
		{
			NAME = ToroQuestEntities.ENTITY_PREFIX + NAME;
		}
	}
	public static void init(int entityId)
	{
		EntityRegistry.registerModEntity(new ResourceLocation(ToroQuest.MODID, NAME), EntityToroVillager.class, NAME, entityId, ToroQuest.INSTANCE, 80, 3,
				true, 0x000000, 0xe0d6b9);
	}
	
	@Override
	public EntityVillager createChild(EntityAgeable ageable)
    {
		
//		EntityToroVillager entityvillager = new EntityToroVillager(this.world);
//        entityvillager.onInitialSpawn(this.world.getDifficultyForLocation(new BlockPos(entityvillager)), (IEntityLivingData)null);
//        return entityvillager;
	        
		EntityVillager villager = new EntityVillager(null);
		villager.setDead();
        EntityToroVillager entityvillager = new EntityToroVillager( this.world, villager.getProfession() ); // TODO
        entityvillager.onInitialSpawn(this.world.getDifficultyForLocation(new BlockPos(entityvillager)), (IEntityLivingData)null);
        return entityvillager;
        
        
        
        
        
        
        /* EntityVillager villager = (EntityVillager)entity;
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
		return;*/
    }
	
//	  @Override
//    @Nullable
//    protected ResourceLocation getLootTable()
//    {
//        return LootTableList.ENTITIES_VILLAGER;
//    }
	
	
	
	@Override
	public void useRecipe(MerchantRecipe recipe)
    {
        this.livingSoundTime = -this.getTalkInterval();
        
    	// this.maxTrades++;
    	
    	// recipe.incrementToolUses();

    	
    	
    	
    	
        // this.setIsWillingToMate(true);
        
        
        
        
        

//      if ( this.world.isRemote ) 
//		{
//        	return;
//		}
        
        if ( this.canTalk <= 0 )
        {
        	this.canTalk = 1;
        	
        	if (rand.nextBoolean()) this.playSound(SoundEvents.ENTITY_VILLAGER_YES, 1.0F, 1.0F);
        }
        
        if ( this.uiClick ) 
    	{
    		this.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);
    		this.uiClick = false;
    	}
        
        EntityPlayer player = this.getCustomer();
    	
    	if ( player == null || !(player.isEntityAlive()) )
    	{
    		return;
    	}
    	
//		if ( this.maxTrades >= ToroQuestConfiguration.maxTradesPerVillager )
//		{
//	        // this.setCustomer(player);
//			player.displayVillagerTradeGui(this);
//		}

    	
        try
        {
        	QuestTradeWithVillagers.INSTANCE.onTrade(player);
        }
        catch(Exception e)
        {
        	
        }
        
    }
	
	@Override
	public boolean getIsWillingToMate(boolean updateFirst)
    {
        if ( updateFirst && this.bedLocation == null )
        {
        	int s = 8;
        	int x = (int)(this.posX+0.5D);
        	int y = (int)(this.posY+0.5D);
        	int z = (int)(this.posZ+0.5D);
			
			for ( int xx = x-s; x+s >= xx; xx++ )
			{
				for ( int yy = y-s; y+s >= yy; yy++ )
				{
					for ( int zz = z-s; z+s >= zz; zz++ )
					{
						Block bed = this.world.getBlockState((new BlockPos(xx, yy, zz))).getBlock();
    					if ( bed instanceof BlockBed )
    					{
    						// System.out.println(bed);
    						this.bedLocation = new BlockPos(xx, yy, zz);
    				        return this.bedLocation != null ? true: false;
    					}
					}
				}
			}
        }
        return this.bedLocation != null ? true: false;
    }
	
    public BlockPos bedLocation = null;
	
	@Nullable
	@Override
	protected SoundEvent getAmbientSound()
    {
        if ( this.isTrading() ) 
        {
        	return SoundEvents.ENTITY_VILLAGER_TRADING;
        }
        else if ( rand.nextBoolean() )
        {
        	return SoundEvents.ENTITY_VILLAGER_AMBIENT;
        }
        else
        {
        	return null;
        }
    }
	
	@Override
	public void setIsWillingToMate(boolean isWillingToMate)
    {
        if ( isWillingToMate )
        {
        	this.getIsWillingToMate(true);
        }
        else
        {
        	this.bedLocation = null;
        }
    }
	
//	static class TreasureMapForEmeralds implements EntityVillager.ITradeList
//    {
//        public EntityVillager.PriceInfo value;
//        public String destination;
//        public MapDecoration.Type destinationType;
//
//        public TreasureMapForEmeralds(EntityVillager.PriceInfo p_i47340_1_, String p_i47340_2_, MapDecoration.Type p_i47340_3_)
//        {
//            this.value = p_i47340_1_;
//            this.destination = p_i47340_2_;
//            this.destinationType = p_i47340_3_;
//        }
//
//        public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random)
//        {
//            int i = this.value.getPrice(random);
//            World world = merchant.getWorld();
//            BlockPos blockpos = world.findNearestStructure(this.destination, merchant.getPos(), true);
//
//            if (blockpos != null)
//            {
//                ItemStack itemstack = ItemMap.setupNewMap(world, (double)blockpos.getX(), (double)blockpos.getZ(), (byte)2, true, true);
//                ItemMap.renderBiomePreviewMap(world, itemstack);
//                MapData.addTargetDecoration(itemstack, blockpos, "+", this.destinationType);
//                itemstack.setTranslatableName("filled_map." + this.destination.toLowerCase(Locale.ROOT));
//                recipeList.add(new MerchantRecipe(new ItemStack(Items.EMERALD, i), new ItemStack(Items.COMPASS), itemstack));
//            }
//        }
//    }
}