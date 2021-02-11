package net.torocraft.toroquest.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.walkers.ItemStackDataLists;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.village.Village;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.torocraft.toroquest.ToroQuest;
import net.torocraft.toroquest.civilization.CivilizationDataAccessor;
import net.torocraft.toroquest.civilization.CivilizationHandlers;
import net.torocraft.toroquest.civilization.CivilizationType;
import net.torocraft.toroquest.civilization.CivilizationUtil;
import net.torocraft.toroquest.civilization.CivilizationsWorldSaveData;
import net.torocraft.toroquest.civilization.Province;
import net.torocraft.toroquest.civilization.player.PlayerCivilizationCapabilityImpl;
import net.torocraft.toroquest.config.ToroQuestConfiguration;
import net.torocraft.toroquest.entities.render.RenderVillageLord;
import net.torocraft.toroquest.gui.VillageLordGuiHandler;
import net.torocraft.toroquest.inventory.IVillageLordInventory;
import net.torocraft.toroquest.inventory.VillageLordInventory;
import net.torocraft.toroquest.item.armor.ItemRoyalArmor;

public class EntityVillageLord extends EntityToroNpc implements IInventoryChangedListener
{

	protected boolean isAnnoyed = false;
	protected int isAnnoyedTimer = 0;

	protected EntityLivingBase underAttack = null;
	protected int underAttackTimer = 0;
	
	protected EntityPlayer murderWitness = null;
	protected int murderTimer = 0;
	
	protected boolean inCombat = false;
	
	public static String NAME = "village_lord";
	
	private String toroName = "Lord";

	static
	{
		if (ToroQuestConfiguration.specificEntityNames)
		{
			NAME = ToroQuestEntities.ENTITY_PREFIX + NAME;
		}
	}
	
	@Override
	protected void pledgeAllegianceIfUnaffiliated()
	{
		if ( getCivilization() != null && getProvince() != null )
		{
			return;
		}

		Province civ = CivilizationUtil.getProvinceAt(world, chunkCoordX, chunkCoordZ);

		if (civ == null || civ.civilization == null)
		{
			return;
		}

		setProvince(civ.name);
		setCivilization(civ.civilization);
	}
	
	@Override
	public CivilizationType getCivilization()
	{
		if (c != null)
		{
			return c;
		}
		return enumCiv(dataManager.get(CIV));
	}
	
	CivilizationType c = null;
	
	//
	
	
	public static void init(int entityId)
	{
		EntityRegistry.registerModEntity(new ResourceLocation(ToroQuest.MODID, NAME), EntityVillageLord.class, NAME, entityId, ToroQuest.INSTANCE, 80,
				3, true, 0xeca58c, 0xba12c8);
	}

	public static void registerRenders()
	{
		RenderingRegistry.registerEntityRenderingHandler(EntityVillageLord.class, new IRenderFactory<EntityVillageLord>() {
			@Override
			public Render<EntityVillageLord> createRenderFor(RenderManager manager) {
				return new RenderVillageLord(manager);
			}
		});
	}
	
	@Override
	public void setAttackTarget(EntityLivingBase e)
	{
		super.setAttackTarget(null);
	}
	
	// =-=-=-=-=-=-=-=-=-=-=-=-=-= TROHPY =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	protected int getInventorySize()
	{
		// return VillageLordContainer.LORD_INVENTORY_SLOT_COUNT;
		return 14;
	}
	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

	public void onLivingUpdate()
	{
		super.onLivingUpdate();
		
		if ( this.world.isRemote )
		{
			return;
		}
		
		if ( this.ticksExisted % 100 == 0 )
		{
			if ( this.getHealth() >= this.getMaxHealth() )
			{
				this.hitSafety = true;
			}
			else this.heal(1.0f);
			
			if ( this.isAnnoyedTimer > 0 && rand.nextInt(2) == 0 )
    		{
    			if ( --this.isAnnoyedTimer < 1 )
    			{
    				this.isAnnoyed = false;
    			}
    		}
    		
    		if ( this.underAttackTimer > 0 && rand.nextInt(2) == 0 )
    		{
    			if ( --this.underAttackTimer < 1 )
    			{
    				this.underAttack = null;
    			}
    		}
    		
    		if ( this.murderTimer > 0 && rand.nextInt(3) == 0 )
    		{
    			if ( --this.murderTimer < 1 )
    			{
    				this.murderWitness = null;
    			}
    		}
			
    		if ( this.raidX != null && this.raidZ != null )
    		{
    			BlockPos pos = getSurfacePosition( this.world, this.raidX, this.raidZ );
    			this.getNavigator().tryMoveToXYZ(pos.getX()+0.5, pos.getY(), pos.getZ()+0.5, 0.25);
    		}
    		
			if ( this.ticksExisted % 600 == 0 && isEntityAlive() )
			{
				this.setHasLord(true);
				this.pledgeAllegianceIfUnaffiliated();
			}
		}
	}
	
	public BlockPos getSurfacePosition(World world, int x, int z)
	{
		BlockPos search = new BlockPos(x, world.getActualHeight()/2, z);
		IBlockState blockState;
		while (search.getY() > 0)
		{
			search = search.down();
			blockState = world.getBlockState(search);
			if (isLiquid(blockState))
			{
				break;
			}
			if ((blockState).isOpaqueCube())
			{
				break;
			}
		}
		return search.up();
	}
	private boolean isLiquid(IBlockState blockState)
	{
		return blockState.getBlock() == Blocks.WATER || blockState.getBlock() == Blocks.LAVA;
	}

	protected Map<UUID, VillageLordInventory> inventories = new HashMap<UUID, VillageLordInventory>();

	public EntityVillageLord(World world)
	{
		super(world, null);
		this.initInventories();
		this.pledgeAllegianceIfUnaffiliated();
	}

	public IVillageLordInventory getInventory(UUID playerId)
	{
		if (inventories.get(playerId) == null)
		{
			inventories.put(playerId, new VillageLordInventory(this, "VillageLordInventory", this.getInventorySize()));
		}
		return inventories.get(playerId);
	}

	protected void initInventories()
	{
		Map<UUID, VillageLordInventory> newInventories = new HashMap<UUID, VillageLordInventory>();
		for (UUID playerId : inventories.keySet())
		{
			newInventories.put(playerId, initInventory(inventories.get(playerId)));
		}
	}

	protected VillageLordInventory initInventory(VillageLordInventory prevInventory)
	{
		VillageLordInventory newInventory = new VillageLordInventory(this, "VillageLordInventory", this.getInventorySize());
		newInventory.setCustomName(this.getName());

		if (prevInventory != null)
		{
			prevInventory.removeInventoryChangeListener(this);
			int i = Math.min(prevInventory.getSizeInventory(), newInventory.getSizeInventory());

			for (int j = 0; j < i; ++j)
			{
				ItemStack itemstack = prevInventory.getStackInSlot(j);

				if (!itemstack.isEmpty())
				{
					newInventory.setInventorySlotContents(j, itemstack.copy());
				}
			}
		}

		newInventory.addInventoryChangeListener(this);
		return newInventory;
	}

	public void openGUI(EntityPlayer player)
	{
//		if (world.isRemote)
//		{
//			return;
//		}
		player.openGui(ToroQuest.INSTANCE, VillageLordGuiHandler.getGuiID(), this.world, getPosition().getX(), getPosition().getY(), getPosition().getZ());
	}

	@Override
	protected boolean processInteract(EntityPlayer player, EnumHand hand) 
	{
		
		//player.sendMessage( new TextComponentString("1") );
		
		if ( this.isEntityAlive() && !this.isChild() )
		{
			Province lordProvince = CivilizationUtil.getProvinceAt(this.world, this.chunkCoordX, this.chunkCoordZ);
			
			if ( lordProvince == null )
			{
				return true;
			}
			//player.sendMessage( new TextComponentString("2") );

			Province playerProvince = CivilizationUtil.getProvinceAt(player.world, player.chunkCoordX, player.chunkCoordZ);

			if ( playerProvince == null )
			{
				return true;
			}
			//player.sendMessage( new TextComponentString("3") );

			int rep = PlayerCivilizationCapabilityImpl.get(player).getReputation(playerProvince.civilization);

			//player.sendMessage( new TextComponentString("4") );
			
			if ( rep > -10 && (this.murderWitness == null || this.murderWitness != player) && (this.underAttack == null || this.underAttack != player ) )
			{
				//player.sendMessage( new TextComponentString("5") );

				if ( this.recentlyHit < 1 )
				{
					ItemStack itemstack = player.getHeldItem(hand);
					String name = itemstack.getDisplayName();
			        if ( itemstack.getItem() == Item.getByNameOrId("toroquest:city_key") && !(name.equals("Key to the City")) )
			        {
			        	
			        	if ( rep >= 500 )
			        	{	
							CivilizationDataAccessor worldData = CivilizationsWorldSaveData.get(player.world);
							if ( worldData == null )
							{
								return true;
							}
					        playerProvince.name = name;
							worldData.setName(playerProvince.id, name);
							player.sendStatusMessage( new TextComponentString("Province renamed to " + name + "!"), true);
			        	}
			        	else
			        	{
			        		if ( !this.world.isRemote ) chat(player, "I would consider naming the province " + name + " if your reputation was high enough.");
			        	}
			        }
			        else
			        {
						this.openGUI(player);
					}
					return true;
				}
			}
			else
			{
				if ( !this.isAnnoyed ) 
				{
					if ( !this.world.isRemote ) chat(player, "Help, guards!");
					this.setAnnoyed();
				}
				callForHelp(player);
			}
		}
		return true;
	}

	public void chat(EntityPlayer player, String message)
	{
		player.sendMessage(new TextComponentString("§l" + this.toroName + "§r: " + message));
	}
	
	// private EntityAIMoveIntoArea areaAI;
	
	@Override
	protected void initEntityAI()
	{
		super.initEntityAI();
		setHasLord(true);
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIPanic(this, 0.5D));
		//this.tasks.addTask(2, this.areaAI);
		this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(8, new EntityAILookIdle(this));
	}
	
//	@Override
//	public boolean canBePushed()
//	{
//		return false;
//	}
//	
//	@Override
//    protected void collideWithNearbyEntities()
//    {
//		return;
//	}

	protected boolean hitSafety = true;
	
	@Nullable
	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata)
	{
		livingdata = super.onInitialSpawn(difficulty, livingdata);
		setCanPickUpLoot(false);
		this.detachHome();
		addArmor();
		setHasLord(true);
		this.pledgeAllegianceIfUnaffiliated();
		this.raidX = (int)this.posX;
		this.raidZ = (int)this.posZ;
		this.setRaidLocation(this.raidX, this.raidZ);
		return livingdata;
	}
	
	@Override
	public boolean hasHome()
	{
		return false;
	}

	protected void addArmor()
	{
		setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(ItemRoyalArmor.helmetItem, 1));
		setItemStackToSlot(EntityEquipmentSlot.FEET, new ItemStack(ItemRoyalArmor.bootsItem, 1));
		setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(ItemRoyalArmor.leggingsItem, 1));
		setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(ItemRoyalArmor.chestplateItem, 1));
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		if ( this.world.isRemote )
		{
			return false;
		}
		if ( !(source.getTrueSource() instanceof EntityLivingBase) || source.getTrueSource() instanceof EntityToroNpc ) 
		{
			return false;
		}
		
		Entity entity = source.getTrueSource();
		if ( entity instanceof EntityPlayer )
		{
            if ( this.hitSafety )
			{
				this.hitSafety = false;
				return false;
			}
			EntityPlayer player = (EntityPlayer)(entity);
			this.setUnderAttack(player);
			dropRepTo(player,-(int)MathHelper.clamp(amount*20,25,this.getHealth()*20));
		}
		
		if ( entity instanceof EntityLivingBase )
		{
			callForHelp( (EntityLivingBase) source.getTrueSource());
		}
		
		return super.attackEntityFrom(source, amount);
	}
	
	
	@Nullable
    Village village;

    @Override
    public Village getVillage()
    {
        return this.village = this.world.getVillageCollection().getNearestVillage(new BlockPos(this), 32);
    }

	private boolean dropRepTo(Entity entity, int amount)
	{
		if (entity == null)
		{
			return false;
		}

		if (!(entity instanceof EntityPlayer))
		{
			return false;
		}

		EntityPlayer player = (EntityPlayer) entity;

		CivilizationType civ = getCivilization();
		if (civ == null)
		{
			return false;
		}
		CivilizationHandlers.adjustPlayerRep(player, civ, amount);
		return true;
	}

	@Override
	public void onInventoryChanged(IInventory invBasic) {

	}

	public void onDeath(DamageSource cause)
	{
		super.onDeath(cause);
		setHasLord(false);
		
		if (world.isRemote || inventories == null)
		{
			return;
		}
		killer(cause);
	}

	protected void killer(DamageSource cause)
	{
		if ( world.isRemote )
		{
			return;
		}
		
		Entity entity = cause.getTrueSource();
		
		if (entity != null && entity instanceof EntityPlayer)
		{
			if ( dropRepTo( entity, -3000 ) )
			{
				for (IVillageLordInventory inventory : inventories.values())
				{
					dropInventory(inventory);
				}
			}
		}
		
		for ( EntityPlayer player : this.world.playerEntities )
		{
			if ( getProvince().equals("") )
			{
				player.sendMessage(new TextComponentString("§lA Lord has been slain!"));
			}
			else if ( getProvince() != null )
			{
				player.sendMessage(new TextComponentString(CivilizationUtil.chatColor(getCivilization())+"The Lord of " + this.getProvince() + " has been slain!"));
			}
		}
	}

	protected void dropInventory(IVillageLordInventory inventory)
	{
		if (inventory == null) {
			return;
		}
		for (int i = 0; i < inventory.getSizeInventory(); ++i)
		{
			ItemStack itemstack = inventory.getStackInSlot(i);
			if (!itemstack.isEmpty())
			{
				entityDropItem(itemstack, 0.0F);
			}
		}
	}

	public static void registerFixesVillageLord(DataFixer fixer) {
		EntityLiving.registerFixesMob(fixer, EntityVillageLord.class);
		fixer.registerWalker(FixTypes.ENTITY, new ItemStackDataLists(EntityVillageLord.class, new String[] { "Items" }));
	}

	private void setHasLord(boolean hasLord)
	{
		Province province = CivilizationUtil.getProvinceAt(world, chunkCoordX, chunkCoordZ);

		if (province == null)
		{
			return;
		}
		
		CivilizationDataAccessor worldData = CivilizationsWorldSaveData.get(world);
		
		if (worldData.provinceHasLord(province.id) == hasLord)
		{
			return;
		}

		if (!isEntityAlive() && hasLord)
		{
			hasLord = false;
		}
		
		if ( hasLord )
		{
			getVillage();
		}
		else
		{
			worldData.setTrophyPig(province.id, false);
			worldData.setTrophyMage(province.id, false);
			worldData.setTrophySpider(province.id, false);
			worldData.setTrophySkeleton(province.id, false);
			worldData.setTrophyBandit(province.id, false);
			worldData.setTrophyLord(province.id, false);
			worldData.setTrophyTitan(province.id, false);
			worldData.setTrophyBeholder(province.id, false);
		}
		
		worldData.setProvinceHasLord(province.id, hasLord);
	}
	
	@Override
	protected void updateLeashedState()
    {
	   this.clearLeashed(true, false);
       return;
    }
	
	@Override
	public boolean canBeLeashedTo(EntityPlayer player)
    {
		return false;
    }
	
   	public Integer raidX = null;
	public Integer raidZ = null;
	public Random rand = new Random();

	/* Set the direction for bandits to move to */
	
	public void setRaidLocation(Integer x, Integer z)
	{
		if ( x != null && z != null )
		{
			this.raidX = x;
			this.raidZ = z;
			NBTTagCompound nbt = new NBTTagCompound(); //this.getEntityData();
			this.writeEntityToNBT(nbt);
		}
	}

@Override
public void writeEntityToNBT(NBTTagCompound compound)
{
	super.writeEntityToNBT(compound);

	NBTTagCompound c = new NBTTagCompound();
	for (Entry<UUID, VillageLordInventory> e : inventories.entrySet())
	{
		c.setTag(e.getKey().toString(), e.getValue().saveAllItems());
	}
	compound.setTag("Items", c);
	
	if ( this.raidX != null && this.raidZ != null )
	{
		compound.setInteger("raidX", this.raidX);
		compound.setInteger("raidZ", this.raidZ);
	}
}

@Override
public void readEntityFromNBT(NBTTagCompound compound)
{
	super.readEntityFromNBT(compound);
	
	NBTTagCompound c = compound.getCompoundTag("Items");
	inventories = new HashMap<UUID, VillageLordInventory>();
	for (String sPlayerId : c.getKeySet())
	{
		VillageLordInventory inv = new VillageLordInventory(this, "VillageLordInventory", getInventorySize());
		inv.loadAllItems(c.getTagList(sPlayerId, 10));
		inventories.put(UUID.fromString(sPlayerId), inv);
	}
	if ( compound.hasKey("raidX") && compound.hasKey("raidZ") )
    {
    	this.raidX = compound.getInteger("raidX");
    	this.raidZ = compound.getInteger("raidZ");
    	this.setRaidLocation(this.raidX, this.raidZ);
    }
}

}
