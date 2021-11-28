package net.torocraft.toroquest.item;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.toroquest.ToroQuest;
import net.torocraft.toroquest.civilization.CivilizationHandlers;
import net.torocraft.toroquest.civilization.CivilizationType;
import net.torocraft.toroquest.civilization.CivilizationUtil;
import net.torocraft.toroquest.civilization.CivilizationsWorldSaveData;
import net.torocraft.toroquest.civilization.Province;
import net.torocraft.toroquest.civilization.player.PlayerCivilizationCapabilityImpl;
import net.torocraft.toroquest.civilization.quests.QuestBase;

@Mod.EventBusSubscriber
public class ItemTownScroll extends Item
{
	public static String P_NAME = "null";
	public static final String NAME = "scroll_";
	public static ItemTownScroll INSTANCE;
	
		//	public String provinceName = "";
		//	public Integer provinceX = null;
		//	public Integer provinceZ = null;
	
    public ItemTownScroll( Province province )
    {
//		this.setUnlocalizedName("town_scroll");
//		this.setRegistryName(REGISTRY_NAME);
    			//this.setData( province );
		this.setHasSubtypes(true);
        this.setMaxStackSize(16);
        // this.setCreativeTab(CreativeTabs.MISC);
    }
    
    @SubscribeEvent
	public static void init(final RegistryEvent.Register<Item> event)
	{
    	/*
    	for ( CivilizationType civ : CivilizationType.values() )
    	{
    		INSTANCE = new ItemTownScroll(NAME + civ.getCivName(), null);
    		INSTANCE.setRegistryName(new ResourceLocation( ToroQuest.MODID, NAME + civ.getCivName() ));
    		event.getRegistry().register(INSTANCE);
    	}
    	*/
    	INSTANCE = new ItemTownScroll(null);
		INSTANCE.setRegistryName(new ResourceLocation( ToroQuest.MODID, NAME + P_NAME ));
		event.getRegistry().register(INSTANCE);
	}
    
		    public static void registerRenders()
		    {
		    	/*
		    	int i = 0;
		    	for ( CivilizationType civ : CivilizationType.values() )
		    	{
		    		ModelResourceLocation model = new ModelResourceLocation(ToroQuest.MODID + ":" + NAME + civ.getCivName(), "inventory");
		    		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(INSTANCE, i, model);
		    		RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
		    		renderItem.getItemModelMesher().register(INSTANCE, i, new ModelResourceLocation(ToroQuest.MODID + ":" + NAME + civ.getCivName(), "inventory"));
		    		i++;
		    	}
		    	*/
		    	ModelResourceLocation model = new ModelResourceLocation(ToroQuest.MODID + ":" + NAME + P_NAME, "inventory");
				Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(INSTANCE, 0, model);
				RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
				renderItem.getItemModelMesher().register(INSTANCE, 0, new ModelResourceLocation(ToroQuest.MODID + ":" + NAME + P_NAME, "inventory"));
			}
		//    
		//    @Override
		//    public boolean updateItemStackNBT(NBTTagCompound nbt)
		//    {
		//    	if ( nbt == null )
		//    	{
		//    		nbt = new NBTTagCompound();
		//    	}
		//    	if ( this.provinceX == null || this.provinceZ == null || this.provinceName == "" )
		//    	{
		//    		return false;
		//    	}
		//    	nbt.setString("Province", this.provinceName);
		//    	nbt.setInteger("X", this.provinceX);
		//    	nbt.setInteger("Z", this.provinceZ);
		//        return true;
		//    }
    
//    public Province loadData( ItemStack scroll )
//    {
//    	NBTTagCompound nbt = scroll.getTagCompound();
//    	if ( nbt == null )
//    	{
//    		nbt = new NBTTagCompound();
//    	}
//    	if ( nbt.hasKey("Province") && nbt.hasKey("X") && nbt.hasKey("Z") )
//    	{
//    		provinceName = nbt.getString("Province");
//    		provinceX = nbt.getInteger("X");
//    		provinceZ = nbt.getInteger("Z");
//
//    	}
//    	scroll.setTagCompound(nbt);
//    }
    
		//    public void setData( Province province )
		//    {
		//    	if ( province == null )
		//    	{
		//    		return;
		//    	}
		//    	if ( this.provinceX != null && this.provinceZ != null && this.provinceName != "" )
		//    	{
		//    		return;
		//    	}
		//    	NBTTagCompound nbt = new NBTTagCompound();
		//    	this.provinceName = province.name;
		//    	this.provinceX = province.getCenterX();
		//    	this.provinceZ = province.getCenterZ();
		//    	nbt.setString("Province", province.name);
		//    	nbt.setInteger("X", province.getCenterX());
		//    	nbt.setInteger("Z", province.getCenterZ());
		//    	this.updateItemStackNBT(nbt);
		//    }
    
//    @Override
//    public String getUnlocalizedName( ItemStack stack )
//    {
//		return null;
//    }
//    @Override
//    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
//    {
//    	int i = 0;
//    	for ( ScrollTypes scroll : items )
//    	{
//    		scroll.add(new ItemStack(scroll, 1, i));
//    		i++;
//    	}
//        super.getSubItems(tab, items);
//    }
    
    /**
     * allows items to add custom lines of information to the mouseover description
     */
	@Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
    	tooltip.add("Teleport scroll not bound.");
    }
	
	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase player, int count)
    {
		player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 5, 1, true, false));
    }
	    
    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving)
    {
        EntityPlayer entityplayer = entityLiving instanceof EntityPlayer ? (EntityPlayer)entityLiving : null;
        try
        {
        	if ( !stack.hasTagCompound() || stack.isEmpty() )
    		{
    			return stack;
    		}

        	String uuid = stack.getTagCompound().getString("province");
        	
        	if ( uuid == null || uuid == "" )
        	{
        		return stack;
        	}

            Province province = findTeleportProvince(entityplayer, uuid, worldIn);
            
            if ( province == null )
            {
            	return stack;
            }

	        BlockPos loc = new BlockPos(province.getCenterX(),0,province.getCenterZ());
	        
			BlockPos teleportLocation = CivilizationHandlers.findTeleportLocationFrom(entityplayer.world, loc);
	        
			if ( teleportLocation == null )
	        {
	        	return stack;
	        }
			
	        if ( entityplayer != null && !entityplayer.capabilities.isCreativeMode ) // && province.civilization != null && PlayerCivilizationCapabilityImpl.get(entityplayer).getReputation(province.civilization) < 2000 )
            {
                stack.shrink(1);
            }

            if ( entityplayer instanceof EntityPlayerMP )
            {
                CriteriaTriggers.CONSUME_ITEM.trigger((EntityPlayerMP)entityplayer, stack);
            }

        	//entityplayer.world.playSound((EntityPlayer)null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.BLOCK_PORTAL_TRAVEL, SoundCategory.PLAYERS, 1.0F, 1.5F);

			//if ( !worldIn.isRemote )
	        {
	        	entityplayer.setPositionAndUpdate(teleportLocation.getX()+0.5,teleportLocation.getY()+0.5,teleportLocation.getZ()+0.5);
	        }
			entityplayer.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 30, 1, true, false));
        	entityplayer.world.playSound((EntityPlayer)null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.BLOCK_PORTAL_TRAVEL, SoundCategory.PLAYERS, 1.0F, 1.5F);
        }
        catch ( Exception e )
        {

        }
        return stack;
    }
    
    
    
    /**
     * Called when the player finishes using this Item (E.g. finishes eating.). Not called when the player stops using
     * the Item before the action is complete.
     */
												//    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving)
												//    {
												//        EntityPlayer entityplayer = entityLiving instanceof EntityPlayer ? (EntityPlayer)entityLiving : null;
												//        
												//        try
												//        {
												//            Province province = QuestBase.chooseClosestProvince(entityplayer, null, worldIn, false);
												//            
												//            if ( province == null )
												//            {
												//            	return stack;
												//            }
												//            
												//	        BlockPos loc = new BlockPos(province.getCenterX(),entityplayer.world.getHeight()/2,province.getCenterZ());
												//			BlockPos teleportLocation = CivilizationHandlers.findSpawnLocationFrom(entityplayer.world, loc);
												//	        if ( teleportLocation == null ) return stack;
												//	        if (entityplayer == null || !entityplayer.capabilities.isCreativeMode)
												//            {
												//                stack.shrink(1);
												//            }
												//
												//            if (entityplayer instanceof EntityPlayerMP)
												//            {
												//                CriteriaTriggers.CONSUME_ITEM.trigger((EntityPlayerMP)entityplayer, stack);
												//            }
												//            
												//        	//entityplayer.world.playSound((EntityPlayer)null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.BLOCK_PORTAL_TRAVEL, SoundCategory.PLAYERS, 1.0F, 1.5F);
												//
												//			if (!worldIn.isRemote)
												//	        {
												//	        	entityplayer.setPositionAndUpdate(teleportLocation.getX(),teleportLocation.getY(),teleportLocation.getZ());
												//	        }
												//
												//        	entityplayer.world.playSound((EntityPlayer)null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.BLOCK_PORTAL_TRAVEL, SoundCategory.PLAYERS, 1.0F, 1.5F);
												//        	entityplayer.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 10, 1, true, false));
												//
												//        }
												//        catch ( Exception e )
												//        {
												//        	
												//        }
												//        return stack;
												//    }

    /**
     * How long it takes to use or consume an item
     */
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return 64;
    }

    /**
     * returns the action that specifies what animation to play when the items is being used
     */
    public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.BOW;
    }

    /**
     * Called when the equipped item is right clicked.
     */
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
    	
		
		
    	Random rand = new Random();
        playerIn.setActiveHand(handIn);
        double x = playerIn.posX;
    	double y = playerIn.posY;
    	double z = playerIn.posZ;
        //if ( !playerIn.world.isRemote )
        //{
        	

        	playerIn.world.playSound((EntityPlayer)null, x, y, z, SoundEvents.EVOCATION_ILLAGER_CAST_SPELL, SoundCategory.PLAYERS, 1.0F, 1.0F);			
        	playerIn.world.playSound((EntityPlayer)null, x, y, z, SoundEvents.BLOCK_PORTAL_AMBIENT, SoundCategory.PLAYERS, 1.0F, 1.5F);	
        //}
        for (int i = 0; i < 16; i++)
    	{
    		playerIn.world.spawnParticle(EnumParticleTypes.PORTAL, x+rand.nextDouble()*2-1, y+rand.nextDouble(), z+rand.nextDouble()*2-1, rand.nextDouble()-0.5, rand.nextDouble()-0.5, rand.nextDouble()-0.5, 0);
    	}

        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
    }
    
    protected static List<Province> getAllProvinces(World world)
    {
		return CivilizationsWorldSaveData.get(world).getProvinces();
	}

    public static Province findTeleportProvince(EntityPlayer player, String uuid, World world)
	{
		List<Province> provinces = getAllProvinces(world);

		for (Province province : provinces)
		{
			if ( province.id.toString().equals(uuid) )
			{
				return province;
			}
		}

		return null;
	}

//    /**
//     * Returns true if this item has an enchantment glint. By default, this returns
//     * <code>stack.isItemEnchanted()</code>, but other items can override it (for instance, written books always return
//     * true).
//     *  
//     * Note that if you override this method, you generally want to also call the super version (on {@link Item}) to get
//     * the glint for enchanted items. Of course, that is unnecessary if the overwritten version always returns true.
//     */
//    @SideOnly(Side.CLIENT)
//    public boolean hasEffect(ItemStack stack)
//    {
//        return super.hasEffect(stack) || !PotionUtils.getEffectsFromStack(stack).isEmpty();
//    }
}