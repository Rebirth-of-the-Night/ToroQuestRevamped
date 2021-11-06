package net.torocraft.toroquest.civilization.quests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.village.Village;
import net.minecraft.world.World;
import net.minecraftforge.server.command.TextComponentHelper;
import net.torocraft.toroquest.block.BlockToroSpawner;
import net.torocraft.toroquest.block.TileEntityToroSpawner;
import net.torocraft.toroquest.civilization.CivilizationHandlers;
import net.torocraft.toroquest.civilization.CivilizationType;
import net.torocraft.toroquest.civilization.CivilizationUtil;
import net.torocraft.toroquest.civilization.CivilizationsWorldSaveData;
import net.torocraft.toroquest.civilization.Province;
import net.torocraft.toroquest.civilization.player.PlayerCivilizationCapabilityImpl;
import net.torocraft.toroquest.civilization.quests.util.Quest;
import net.torocraft.toroquest.civilization.quests.util.QuestData;
import net.torocraft.toroquest.config.ToroQuestConfiguration;

/*
To register quests, go to: ----- Quests
To add quests, go to ----------- PlayerCivilizationCapabilityImpl.generateNextQuestFor()
*/

public abstract class QuestBase implements Quest
{
	protected static ItemStack createMetaBlockStack(Block block, int meta, int amount)
	{
		ItemStack s = new ItemStack(block, amount);
		s.setItemDamage(meta);
		return s;
	}

	protected static Province loadProvince(World world, BlockPos pos)
	{
		return CivilizationUtil.getProvinceAt(world, pos.getX() / 16, pos.getZ() / 16);
	}
	
	public static void chat( QuestData data, String message )
	{
		EntityPlayer player = data.getPlayer();
		if ( player == null )
		{
			return;
		}
		player.sendMessage(new TextComponentString( "§lVillage Lord§r: " + message));
		//player.sendMessage(new TextComponentString( "§lLord of " + getQuestProvince(data).name + "§r: " + message));
		player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_EVOCATION_ILLAGER_AMBIENT, SoundCategory.VOICE, 1.0F, 1.1F);
	}
	
	public static void chat( EntityPlayer player, String provinceName, String message )
	{
		player.sendMessage(new TextComponentString( "§lVillage Lord§r: " + message));
		//player.sendMessage(new TextComponentString( "§lLord of " + provinceName+ "§r: " + message));
		player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_EVOCATION_ILLAGER_AMBIENT, SoundCategory.VOICE, 1.0F, 1.1F);
	}
	
	public void chatCompletedQuest( QuestData data )
	{
		data.getPlayer().sendStatusMessage( TextComponentHelper.createComponentTranslation(data.getPlayer(), "quest.quest_complete_message", new Object[0]), ToroQuestConfiguration.showQuestCompletionAboveActionBar );
		data.getPlayer().world.playSound((EntityPlayer)null, data.getPlayer().posX, data.getPlayer().posY, data.getPlayer().posZ, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1.1F, 1.1F);
	}

	protected static String listItems(List<ItemStack> rewardItems)
	{
		if (rewardItems == null || rewardItems.isEmpty())
		{
			return "";
		}
		
		StringBuilder sb = new StringBuilder();
 		boolean first = true;
		
		for (ItemStack item : rewardItems)
		{
			if (first)
			{
				first = false;
			}
			else
			{
				sb.append(", ");
			}
			sb.append(item.getCount() + " " );
			sb.append( item.getDisplayName() );
			if ( item.getItem() == Items.EMERALD && item.getCount() > 1 )
			{
				sb.append( "s" );
			}
		}
		return sb.toString();
	}
	
	protected static String listKillMobItems(List<ItemStack> rewardItems)
	{
		if (rewardItems == null || rewardItems.isEmpty())
		{
			return "";
		}
		
		StringBuilder sb = new StringBuilder();
 		boolean first = true;
		
		for (ItemStack item : rewardItems)
		{
			if (first)
			{
				first = false;
			}
			else
			{
				sb.append(", ");
			}
			if ( item.getItem() == Items.EMERALD )
			{
				sb.append(item.getCount() + " " );
				sb.append( item.getDisplayName() );
				if ( item.getItem() == Items.EMERALD && item.getCount() > 1 )
				{
					sb.append( "s" );
				}
			}
			else
			{
				sb.append(item.getCount() * 9 + " " );
				sb.append( new ItemStack(Items.EMERALD,1).getDisplayName() );
				sb.append( "s" );
			}
		}
		return sb.toString();
	}
	
	protected static String listItemsBlocks(List<ItemStack> rewardItems)
	{
		if (rewardItems == null || rewardItems.isEmpty())
		{
			return "";
		}
		
		StringBuilder sb = new StringBuilder();
 		boolean first = true;
		
		for (ItemStack item : rewardItems)
		{
			if (first)
			{
				first = false;
			}
			else
			{
				sb.append(", ");
			}
			sb.append(item.getCount() * 9 + " " );
			sb.append( new ItemStack(Items.EMERALD,1).getDisplayName() );
			sb.append( "s" );
		}
		return sb.toString();
	}

	protected static List<ItemStack> removeItems(List<ItemStack> requiredIn, List<ItemStack> itemsIn) throws InsufficientItems
	{
		List<ItemStack> givenItems = copyItems(itemsIn);
		List<ItemStack> requiredItems = copyItems(requiredIn);

		for (ItemStack givenItem : givenItems)
		{
			for (ItemStack requiredItem : requiredItems)
			{
				handleStackDecrement(requiredItem, givenItem);
			}
		}

		for (ItemStack remainingRequired : requiredItems)
		{
			if (remainingRequired.getCount() > 0)
			{
				throw new InsufficientItems(remainingRequired.getCount() + " " + remainingRequired.getDisplayName());
			}
		}

		return givenItems;
	}

	protected static void handleStackDecrement(ItemStack requiredItem, ItemStack givenItem)
	{
		if (!equals(requiredItem, givenItem))
		{
			return;
		}

		if (requiredItem.getCount() < 1 || givenItem.getCount() < 1)
		{
			return;
		}
		int decrementBy = Math.min(requiredItem.getCount(), givenItem.getCount());
		requiredItem.shrink(decrementBy);
		givenItem.shrink(decrementBy);
	}

	protected static boolean equals(ItemStack requiredItem, ItemStack givenItem)
	{
		return requiredItem.getItem() == givenItem.getItem();
	}

	public static class InsufficientItems extends Exception
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public InsufficientItems(String message) {
			super(message);
		}
	}

	protected static List<ItemStack> copyItems(List<ItemStack> itemsIn)
	{
		List<ItemStack> items = new ArrayList<ItemStack>();
		for (ItemStack stack : itemsIn) {
			items.add(stack.copy());
		}
		return items;
	}

	protected static void setRewardItems(QuestData data, List<ItemStack> rewards) {
		setItemsToNbt(data, "rewards", rewards);
	}

	protected static void setRequiredItems(QuestData data, List<ItemStack> required) {
		setItemsToNbt(data, "required", required);
	}

	protected static List<ItemStack> getRequiredItems(QuestData data) {
		return getItemsFromNbt(data, "required");
	}

	protected static List<ItemStack> getRewardItems(QuestData data) {
		return getItemsFromNbt(data, "rewards");
	}

	protected static List<ItemStack> getItemsFromNbt(QuestData data, String name)
	{
		List<ItemStack> items = new ArrayList<ItemStack>();
		NBTTagCompound c = getCustomNbtTag(data);
		try
		{
			NBTTagList list = (NBTTagList) c.getTag(name);
			for (int i = 0; i < list.tagCount(); i++)
			{
				items.add(new ItemStack(list.getCompoundTagAt(i)));
			}
			return items;
		}
		catch (Exception e)
		{
			return getDefaultItems(name);
		}
	}

	protected static List<ItemStack> getDefaultItems(String name) {
		List<ItemStack> items = new ArrayList<ItemStack>();
		items.add(new ItemStack(Items.EMERALD, 5));
		return items;
	}

	protected static void setItemsToNbt(QuestData data, String name, List<ItemStack> items) {
		NBTTagCompound c = getCustomNbtTag(data);
		NBTTagList list = new NBTTagList();
		for (ItemStack stack : items) {
			NBTTagCompound cStack = new NBTTagCompound();
			stack.writeToNBT(cStack);
			list.appendTag(cStack);
		}
		c.setTag(name, list);
	}

	protected static NBTTagCompound getCustomNbtTag(QuestData data) {
		try {
			return (NBTTagCompound) data.getCustom();
		} catch (Exception e) {
			NBTTagCompound c = new NBTTagCompound();
			data.setCustom(c);
			return c;
		}
	}

	protected static Integer getRewardRep(QuestData data) {
		return i(data.getiData().get("rep"));
	}
	
	protected static Integer getTargetAmount(QuestData data) {
		return i(data.getiData().get("target"));
	}

	protected static void setRewardRep(QuestData data, Integer rewardRep) {
		data.getiData().put("rep", rewardRep);
	}

	protected static Integer i(Object o) {
		try {
			return (Integer) o;
		} catch (Exception e) {
			return 0;
		}
	}

	public static boolean isLiquid(IBlockState blockState)
	{
		return blockState.getBlock() instanceof BlockLiquid || blockState == Blocks.WATER || blockState == Blocks.FLOWING_WATER || blockState == Blocks.LAVA || blockState == Blocks.FLOWING_LAVA;
	}

	protected static boolean isGroundBlock(IBlockState blockState)
	{
		if (blockState.getBlock() instanceof BlockLeaves || blockState.getBlock() instanceof BlockLog || blockState.getBlock() instanceof BlockBush || blockState.getBlock() instanceof BlockSlab )
		{
			return false;
		}
		return blockState.isOpaqueCube();
	}

	protected static String getDirections(BlockPos from, BlockPos to)
	{
		if (from == null || to == null)
		{
			return "";
		}
		int x = to.getX()/10;
		int z = to.getZ()/10;
		x*=10;
		z*=10;
		return x + ", " + z;
	}
	
	protected static String getDirections( BlockPos pos )
	{
		if ( pos == null)
		{
			return "";
		}
		int x = pos.getX()/10;
		int z = pos.getZ()/10;
		x*=10;
		z*=10;
		return x + ", " + z;
	}

	/**
	 * @return the province that gave the quest
	 */
	protected static Province getQuestProvince(QuestData data)
	{
		return getProvinceById(data.getPlayer().world, data.getProvinceId());
	}

	protected static BlockPos getProvincePosition(Province province)
	{
		return new BlockPos(province.chunkX * 16, 80, province.chunkZ * 16);
	}

	protected static List<Province> getAllProvinces(World world)
	{
		return CivilizationsWorldSaveData.get(world).getProvinces();
	}

	protected static Province getProvinceById(World world, String id)
	{
		try
		{
			return getProvinceById(world, UUID.fromString(id));
		}
		catch ( Exception e )
		{
			return null;
		}
	}

	protected static Province getProvinceById(World world, UUID id)
	{
		for (Province p : getAllProvinces(world))
		{
			if (p.id.equals(id))
			{
				return p;
			}
		}
		return null;
	}

	protected String getProvinceName(EntityPlayer player, UUID provinceId)
	{
		Province province = getProvinceById(player.world, provinceId);
		if (province == null)
		{
			return "";
		}
		return province.name;
	}

	protected static QuestData getQuestById(EntityPlayer player, String questId)
	{
		try
		{
			return getQuestById(player, UUID.fromString(questId));
		}
		catch ( Exception e )
		{
			return null;
		}
	}

	protected static QuestData getQuestById(EntityPlayer player, UUID questId)
	{
		Set<QuestData> quests = PlayerCivilizationCapabilityImpl.get(player).getCurrentQuests();
		if ( quests == null )
		{
			return null;
		}
		for ( QuestData data : quests )
		{
			if ( data.getQuestId() != null && data.getQuestId().equals(questId) )
			{
				return data;
			}
		}
		return null;
	}

	public static List<ItemStack> removeEmptyItemStacks(List<ItemStack> givenItems) {
		List<ItemStack> itemsToReturn = new ArrayList<ItemStack>();
		for (ItemStack item : givenItems) {
			if (!item.isEmpty()) {
				itemsToReturn.add(item);
			}
		}
		return itemsToReturn;
	}

	protected static void addRewardItems(QuestData data, List<ItemStack> givenItems) {
		if (getRewardItems(data) == null || givenItems == null) {
			return;
		}
		givenItems.addAll(getRewardItems(data));
	}

	// TODO
	public static Province chooseRandomProvince(Province exclude, World world, boolean mustHaveLord)
	{
		List<Province> provinces = getAllProvinces(world);
		if (provinces.size() < 1)
		{
			return null;
		}

		Collections.shuffle(provinces);

		for (Province p : provinces)
		{
			if (exclude != null && p.id == exclude.id)
			{
				continue;
			}

			if (mustHaveLord && !p.hasLord)
			{
				continue;
			}
			return p;
		}
		return null;
	}
	
	public static Province chooseClosestProvince(EntityPlayer player, CivilizationType include, World world, boolean mustHaveLord)
	{
		List<Province> provinces = getAllProvinces(world);
		double distance = -1;
		Province province = null;
		
		if (provinces.size() < 1)
		{
			return null;
		}

		for (Province p : provinces)
		{
			if (include != null && p.civilization != include)
			{
				continue;
			}

			if (mustHaveLord && !p.hasLord)
			{
				continue;
			}
			double contest = (Math.abs(player.posX-p.getCenterX()) + Math.abs(player.posZ-p.getCenterZ()));
			if ( contest < distance || distance < 0 )
			{
				province = p;
				distance = contest;
			}
		}
		return province;
	}
	
	//  =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= Location =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	public Random rand = new Random();
	
	public BlockPos searchForSuitableLocation( QuestData data, int range, int occupiedRange )
	{
		BlockPos pos = null;
		for (int i = 0; i < 100; i++)
		{
			pos = randomLocation(data, range, false, occupiedRange);
			if (pos != null)
			{
				break;
			}
		}
		
		if (pos == null)
		{
			for (int i = 0; i < 10; i++)
			{
				pos = randomLocation(data, range*2, true, occupiedRange);
				if (pos != null)
				{
					break;
				}
			}
		}
		return pos;
	}

	public BlockPos randomLocation(QuestData data, int range, boolean force, int occupiedRange)
	{
		Province province = getQuestProvince(data);
		EntityPlayer player = data.getPlayer();
		//random = new Random( rand.nextInt() + data.hashCode() + data.getQuestId().hashCode() );

		if ( province == null || player == null )
		{
			return null;
		}
		
		range = rand.nextInt(range/2)+range/2;
		
		double angle = rand.nextDouble()*Math.PI*2.0D;

		int x = (int) (Math.cos(angle)*range);
		int z = (int) (Math.sin(angle)*range);
		
		x += province.getCenterX();
		z += province.getCenterZ();
		
//		int x = (random.nextInt(range));aaa
//		int z = (random.nextInt(range));
//		
//		while ( x + z < range/2 )
//		{
//			x = (random.nextInt(range));
//			z = (random.nextInt(range));
//		}
//		
//		x *= (random.nextInt(2)*2-1);
//		z *= (random.nextInt(2)*2-1);
//		
//		x += province.getCenterX();
//		z += province.getCenterZ();
		
		BlockPos pos = findSurface(player.world, x, z, force);

		if ( force )
		{
			return pos;
		}
		
		if (pos == null)
		{
			return null;
		}
		
		Village village = player.world.getVillageCollection().getNearestVillage(pos, 48);

		if ( village != null )
		{
			return null;
		}
		
	    province = CivilizationUtil.getProvinceAt(player.world, x/16, z/16);
	    
		if ( province == null ) {province = CivilizationUtil.getProvinceAt(player.world, x/16+2, z/16+2);}
		if ( province == null ) {province = CivilizationUtil.getProvinceAt(player.world, x/16+2, z/16-2);}
		if ( province == null ) {province = CivilizationUtil.getProvinceAt(player.world, x/16-2, z/16+2);}
		if ( province == null ) {province = CivilizationUtil.getProvinceAt(player.world, x/16-2, z/16-2);}
	
		if ( province != null)
		{
			return null;
		}
		
		if (data.getPlayer().world.isAnyPlayerWithinRangeAt((double)pos.getX(),(double)pos.getY(),(double)pos.getZ(),48))
		{
			return null;
		}
		
		TileEntity tileentity;
		
		if ( occupiedRange < 0 )
		{
			int s = -occupiedRange/2;
			
			for ( int xx = x-s; x+s >= xx; xx++ )
			{
				for ( int yy = 48; yy <= 96; yy++ )
				{
					for ( int zz = z-s; z+s >= zz; zz++ )
					{
						tileentity = data.getPlayer().world.getTileEntity(new BlockPos(xx, yy, zz));
						if (tileentity instanceof TileEntityChest)
						{
							//System.out.println("CHEST HERE");
							return null;
						}
					}
				}
			}
		}
		else if ( occupiedRange > 0 )
		{
			int s = occupiedRange/2;
			
			for ( int xx = x-s; x+s >= xx; xx++ )
			{
				for ( int yy = pos.getY()-s; pos.getY()+s >= yy; yy++ )
				{
					for ( int zz = z-s; z+s >= zz; zz++ )
					{
						tileentity = data.getPlayer().world.getTileEntity(new BlockPos(xx, yy, zz));
						if (tileentity instanceof TileEntityChest)
						{
							//System.out.println("CHEST HERE");
							return null;
						}
					}
				}
			}
		}

		return pos;
	}
	
	

	public BlockPos findSurface(World world, int x, int z, boolean force)
	{
		BlockPos pos = new BlockPos(x, world.getActualHeight(), z);
		IBlockState blockState;
		while (pos.getY() > 0)
		{
			blockState = world.getBlockState(pos);
			
			if ( !force && ( isLiquid(blockState) || CivilizationHandlers.isStructureBlock(blockState) ) )
			{
				return null;
			}
			
			if ( isGroundBlock(blockState) )
			{
				break;
			}
			
			pos = pos.down();
		}
		return pos.up();
	}
	
	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

	public void addToroSpawner(QuestData data, World world, BlockPos blockpos, String tag, List<String> entities)
	{
		world.setBlockState(blockpos, BlockToroSpawner.INSTANCE.getDefaultState());
		TileEntity tileentity = world.getTileEntity(blockpos);
		if (tileentity instanceof TileEntityToroSpawner)
		{
			TileEntityToroSpawner spawner = (TileEntityToroSpawner) tileentity;
			spawner.setTriggerDistance(64);
			spawner.setEntityIds(entities);
			spawner.setSpawnRadius(16);
			spawner.addEntityTag(data.getQuestId().toString());
			spawner.addEntityTag(tag);
		}
		else
		{
			System.out.println("tile entity is missing");
		}
	}
	
	public BlockPos getSpawnPosition(QuestData data)
	{
		NBTTagCompound c = getCustomNbtTag(data);
		if ( !c.getBoolean("locationFound") )
		{
			return null;
		}
		return new BlockPos(c.getInteger("pos_x"), c.getInteger("pos_y"), c.getInteger("pos_z"));
	}

	public void setSpawnPosition(QuestData data, BlockPos pos)
	{
		NBTTagCompound c = getCustomNbtTag(data);
		c.setInteger("pos_x", pos.getX());
		c.setInteger("pos_y", pos.getY());
		c.setInteger("pos_z", pos.getZ());
		c.setBoolean("locationFound", true);
	}
	
	// =-=-=-=-=
	
										//	@SideOnly(Side.CLIENT) CSTA
										//	@SubscribeEvent
										//	protected void closeUI(GuiOpenEvent event)
										//	{	
										//		if ( event.getGui() == null )
										//		{
										//			if ( this.data != null )
										//			{
										//				if ( this.data.getPlayer() != null )
										//				{
										//					if ( !(this.data.getChatStack().equals("")) )
										//					{
										//						QuestBase.chat( this.data, this.data.getChatStack() );
										//						this.data.clearChatStack();
										//					}
										//				}
										//			}
										//		}
										//	}
	
//	public static void messagePlayer()
//	{
//		
//	}
	
	protected QuestData data;
	
	protected void setData( QuestData data )
	{
		this.data = data;
	}
	
	// =-=-=-=-=
		
}
