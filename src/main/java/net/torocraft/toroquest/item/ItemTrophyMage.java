package net.torocraft.toroquest.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.toroquest.ToroQuest;

@Mod.EventBusSubscriber
public class ItemTrophyMage extends ItemTrophy
{

	public static ItemTrophyMage INSTANCE;
	public static final String NAME = "trophy_archmage";
	private static ResourceLocation REGISTRY_NAME = new ResourceLocation(ToroQuest.MODID, NAME);

	@SubscribeEvent
	public static void init(final RegistryEvent.Register<Item> event)
	{
		INSTANCE = new ItemTrophyMage();
		INSTANCE.setRegistryName(REGISTRY_NAME);
		event.getRegistry().register(INSTANCE);
	}

	public static void registerRenders()
	{
		ModelResourceLocation model = new ModelResourceLocation(ToroQuest.MODID + ":" + NAME, "inventory");
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(INSTANCE, 0, model);
		RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
		renderItem.getItemModelMesher().register(INSTANCE, 0, new ModelResourceLocation(ToroQuest.MODID + ":" + NAME, "inventory"));
	}

	public ItemTrophyMage()
	{
		setUnlocalizedName(NAME);
		this.maxStackSize = 1;
		this.setCreativeTab(CreativeTabs.MATERIALS);
	}
	
	@Override
    @SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
		tooltip.add("Give this trophy to a Village Lord: The ghost of the slain archmage is forever doomed to cast Haste on you while in that province.\n\n§oHe kinda deserved to die though, right? He must have tossed you off that tower at least a dozen times...");
    }
	
//	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
//    {
//    	
//		
//		
//    	Random rand = new Random();
//        playerIn.setActiveHand(handIn);
//        
//        {
//	        Vec3d vec = playerIn.getLookVec();
//			
//			if ( vec != null )
//			{
//				BlockPos pos = new BlockPos(vec.x, vec.y, vec.z);
//				Block block = worldIn.getBlockState(pos).getBlock();
//				if ( block != null )
//				{
//					worldIn.addWeatherEffect(new EntityLightningBolt(worldIn, vec.x, vec.y, vec.z, false));
//				}
//			}
//        }
//		
//        double x = playerIn.posX;
//    	double y = playerIn.posY;
//    	double z = playerIn.posZ;
//        //if ( !playerIn.world.isRemote )
//        //{
//        	
//
//        	playerIn.world.playSound((EntityPlayer)null, x, y, z, SoundEvents.EVOCATION_ILLAGER_CAST_SPELL, SoundCategory.PLAYERS, 1.0F, 1.0F);			
//        	playerIn.world.playSound((EntityPlayer)null, x, y, z, SoundEvents.BLOCK_PORTAL_AMBIENT, SoundCategory.PLAYERS, 1.0F, 1.5F);	
//        //}
//        for (int i = 0; i < 16; i++)
//    	{
//    		playerIn.world.spawnParticle(EnumParticleTypes.PORTAL, x+rand.nextDouble()*2-1, y+rand.nextDouble()+1, z+rand.nextDouble()*2-1, rand.nextDouble()-0.5, +rand.nextDouble()-0.5, +rand.nextDouble()-0.5, 0);
//    	}
//
//        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
//    }

}
