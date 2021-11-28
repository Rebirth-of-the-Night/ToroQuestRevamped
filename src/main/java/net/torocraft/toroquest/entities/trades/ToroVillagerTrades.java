package net.torocraft.toroquest.entities.trades;

import java.util.Locale;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.village.Village;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;
import net.torocraft.toroquest.civilization.CivilizationType;
import net.torocraft.toroquest.civilization.CivilizationUtil;
import net.torocraft.toroquest.civilization.Province;
import net.torocraft.toroquest.config.ToroQuestConfiguration;
import net.torocraft.toroquest.config.ToroQuestConfiguration.Trade;
import net.torocraft.toroquest.entities.EntityToroVillager;

public class ToroVillagerTrades
{
	@SuppressWarnings("deprecation")
	public static MerchantRecipeList trades( EntityToroVillager villager, EntityPlayer player, int rep, CivilizationType civ, String jobName, String varient )
	{
		
		// could limit trades by per villager, replace 999999
		
		MerchantRecipeList recipeList = new MerchantRecipeList();
		
		for ( Trade trade : ToroQuestConfiguration.trades )
		{
			try
			{
				if ( (trade.varient.equals(varient) || trade.varient.equals("x")) && (jobName.equals(trade.job) || trade.job.equals("x")) && (trade.province.equals("x") || trade.province.equals(CivilizationType.biomeName(civ.toString()))) && ( trade.minimunRepRequired.equals("x") || rep >= Integer.parseInt(trade.minimunRepRequired) ) )
				{
					int sell = Integer.parseInt(trade.sellAmount);
					int buy  = Integer.parseInt(trade.buyAmount);
					
					ItemStack sellStack = ItemStack.EMPTY;
					ItemStack optional = ItemStack.EMPTY;
					ItemStack buyStack = ItemStack.EMPTY;
					
					Integer sellDamage = null;
					Integer buyDamage = null;
					
					String buyName = "" + trade.buyName;
					String sellName = "" + trade.sellName;
					
					if ( count(trade.sellName,':') > 1 )
					{
						int index = trade.sellName.lastIndexOf(':');
						sellDamage = Integer.parseInt( trade.sellName.substring( index + 1 ) );
						sellName = sellName.substring( 0, index );
					}
					
					if ( count(trade.buyName,':') > 1 )
					{
						int index = trade.buyName.lastIndexOf(':');
						buyDamage = Integer.parseInt( trade.buyName.substring( index + 1 ) );
						buyName = buyName.substring( 0, index );
					}

					if ( !trade.sellOptional.equals("x") )
					{
						optional = new ItemStack( Item.getByNameOrId(trade.sellOptional), 1 );
					}
					
					if ( sell < buy )
					{
						Item item = Item.getByNameOrId(sellName);
						int maxStackSize = item.getItemStackLimit();
						if ( sell > maxStackSize )
						{
							if ( !trade.sellOptional.equals("x") || sell > maxStackSize * 2 )
							{
								continue;
							}
							else
							{
								optional = new ItemStack( item, sell - maxStackSize );
							}
							sell = maxStackSize;
						}
						sellStack = new ItemStack( item, sell );

						item = Item.getByNameOrId( buyName );
						maxStackSize = item.getItemStackLimit();
						buy = getBuyPrice( buy, rep );
						if ( buy > maxStackSize )
						{
							continue;
						}
						buyStack = new ItemStack( item, buy );
					}
					else
					{
						sell = getSellPrice( sell, rep );
						Item item = Item.getByNameOrId( sellName );
						int maxStackSize = item.getItemStackLimit();
						if ( sell > maxStackSize )
						{
							if ( !trade.sellOptional.equals("x") || sell > maxStackSize * 2 )
							{
								continue;
							}
							else
							{
								optional = new ItemStack( item, sell - maxStackSize );
							}
							sell = maxStackSize;
						}
						sellStack = new ItemStack( item, sell );
						
						item = Item.getByNameOrId( buyName );
						if ( buy > item.getItemStackLimit() )
						{
							buy = item.getItemStackLimit();
						}
						buyStack = new ItemStack( item, buy );
						
						
						if ( trade.enchantment != null )
						{
							// 0        , 1               , 2                , 3               , 4                , 5  , 6
							// item name, enchantment name, enchantment power, enchantment name, enchantment power, ..., ...
							String[] metaArray = trade.enchantment.split("~");
							
							if ( buyName.equals("minecraft:enchanted_book") )
							{
								for ( int i = 0; i < metaArray.length; )
								{
									ItemEnchantedBook.addEnchantment(buyStack, new EnchantmentData(Enchantment.getEnchantmentByLocation(metaArray[i++]), Integer.parseInt(metaArray[i++])));
								}
							}
							else if ( buyName.equals("minecraft:potion") )
							{
								try
								{
									buyStack = PotionUtils.addPotionToItemStack(new ItemStack(Item.getByNameOrId(metaArray[1])), PotionType.getPotionTypeForName(metaArray[0]));
								}
								catch ( Exception e )
								{
									player.sendMessage( new TextComponentString( "ERROR GENERATING TRADE: " + trade.sellName + " & " + trade.buyName ));
									System.err.println( new TextComponentString( "ERROR GENERATING TRADE: " + trade.sellName + " & " + trade.buyName + "." ));
									continue;
								}
							}
							else
							{
								buyStack.setStackDisplayName(metaArray[0]);
								for ( int i = 0; i < metaArray.length-1; )
								{
									buyStack.addEnchantment(Enchantment.getEnchantmentByLocation(metaArray[++i]), Integer.parseInt(metaArray[++i]));
								}
							}
						}
					}
					
					if ( sellDamage != null )
					{
						sellStack.setItemDamage( sellDamage );
					}
					
					if ( buyDamage != null )
					{
						buyStack.setItemDamage( buyDamage );
					}
					recipeList.add( new MerchantRecipe( sellStack, optional, buyStack, 0, 999999 ) );
				}
			}
			catch ( Exception e )
			{
				player.sendMessage( new TextComponentString( "ERROR GENERATING TRADE: " + trade.sellName + " & " + trade.buyName ));
				System.err.println( new TextComponentString( "ERROR GENERATING TRADE: " + trade.sellName + " & " + trade.buyName + "." ));
				continue;
			}
		}
		
		// MAPS FOR EMERALDS
		if ( ToroQuestConfiguration.cartographerMapTrade && jobName.equals("cartographer") )
		{
			if ( varient.equals("0") )
			{
				if ( villager.treasureMap != null )
				{
					recipeList.add( new MerchantRecipe( new ItemStack(Items.EMERALD, 8), new ItemStack(Items.COMPASS, 1), villager.treasureMap, 0, 99999 ) );
				}
				else
				{
					villager.treasureMap = TreasureMapForEmeralds(villager, player, "Mansion", MapDecoration.Type.MANSION);
					
					if ( villager.treasureMap != null )
					{
						// villager.treasureMap.setStackDisplayName("Map to Woodland Mansion");

						recipeList.add( new MerchantRecipe( new ItemStack(Items.EMERALD, 8), new ItemStack(Items.COMPASS, 1), villager.treasureMap, 0, 99999 ) );
					}
				}
			}
			else if ( varient.equals("1") )
			{
				if ( villager.treasureMap != null )
				{
					recipeList.add( new MerchantRecipe( new ItemStack(Items.EMERALD, 6), new ItemStack(Items.COMPASS, 1), villager.treasureMap, 0, 99999 ) );
				}
				else
				{
					villager.treasureMap = TreasureMapForEmeralds(villager, player, "Monument", MapDecoration.Type.MONUMENT);
				
					if ( villager.treasureMap != null )
					{
						// villager.treasureMap.setStackDisplayName("Map to Ocean Monument");

						recipeList.add( new MerchantRecipe( new ItemStack(Items.EMERALD, 6), new ItemStack(Items.COMPASS, 1), villager.treasureMap, 0, 99999 ) );
					}
				}
			}
			else
			{
				// BlockPos blockpos = player.world.findNearestStructure("Village", player.getPosition(), true);

				
				if ( villager.treasureMap != null )
				{
					recipeList.add( new MerchantRecipe( new ItemStack(Items.EMERALD, 4), new ItemStack(Items.COMPASS, 1), villager.treasureMap, 0, 99999 ) );
				}
				else
				{
					villager.treasureMap = civMapForEmeralds(villager, player, "Village", MapDecoration.Type.MANSION);
					
					if ( villager.treasureMap != null )
					{
						recipeList.add( new MerchantRecipe( new ItemStack(Items.EMERALD, 4), new ItemStack(Items.COMPASS, 1), villager.treasureMap, 0, 99999 ) );
					}
				}
			}
		}
		return recipeList;
	}
		
    public static ItemStack TreasureMapForEmeralds( EntityToroVillager villager, EntityPlayer player, String destination, MapDecoration.Type destinationType )
	{
    	World world = player.world;
        BlockPos blockpos = world.findNearestStructure(destination, player.getPosition(), true);

        if (blockpos != null)
        {
            ItemStack itemstack = ItemMap.setupNewMap(world, (double)blockpos.getX(), (double)blockpos.getZ(), (byte)4, true, true);
            ItemMap.renderBiomePreviewMap(world, itemstack);
            MapData.addTargetDecoration(itemstack, blockpos, "+", destinationType);
            itemstack.setTranslatableName("filled_map." + destination.toLowerCase(Locale.ROOT));
            return itemstack;
        }
		return null;
    }
	
    public static ItemStack civMapForEmeralds( EntityToroVillager villager, EntityPlayer player, String destination, MapDecoration.Type destinationType )
	{
    	World world = villager.world;
    	
    	BlockPos pos = villager.getPosition();
    	
    	Village village = null;
    	
    	if ( pos == null )
    	{
    		return null;
    	}
    	
    	pos = pos.add((world.rand.nextBoolean()?1:-1)*world.rand.nextInt(40)*16,0,(world.rand.nextBoolean()?1:-1)*world.rand.nextInt(40)*16);
    	
    	if ( pos == null )
    	{
    		pos = villager.getPosition();
    	}
    	else
    	{
    		village = world.villageCollection.getNearestVillage(pos, 704);
    	}
        
    	if ( village == null )
        {
            village = world.villageCollection.getNearestVillage(villager.getPosition(), 704);
        }
        
        if ( village == null )
        {
            return null;
        }
        
    	ItemStack itemstack = ItemMap.setupNewMap(world, (double)village.getCenter().getX(), (double)village.getCenter().getZ(), (byte)4, true, true);
        
    	ItemMap.renderBiomePreviewMap(world, itemstack);
        
    	MapData.addTargetDecoration(itemstack, village.getCenter(), "+", destinationType);

    	Province province = CivilizationUtil.getProvinceAt( villager.world, villager.chunkCoordX, villager.chunkCoordZ);

		if ( province == null || province.getCiv() == null )
		{
			villager.treasureMap.setStackDisplayName("Map to Village");
		}
		else
		{
			villager.treasureMap.setStackDisplayName("Map to " + province.getCiv().getDisplayName(player) );
		}
		
        villager.treasureMap = itemstack;
        return itemstack;
    }
		
	// LEFT >
	public static int getSellPrice(int price, int rep) // sell price is reduced the higher reputation
	{
		// System.out.println(price + "   " + rep + "   " + (int)(Math.round(MathHelper.clamp(((double)price * MathHelper.clamp( ( 2.0 - rep/3000 ), 1.0, 2.0)), 1, 128))));
		
		// System.out.println( 2.0D - rep/3000.0D);
		
		// System.out.println( ( (double)price * MathHelper.clamp( ( 2.0D - rep / 3000.0D ), 1.0D, 2.0D ) ) );
		
		if ( rep < 0 )
		{
			return (int)(Math.round(MathHelper.clamp(((double)price * MathHelper.clamp( ( 2.0D + rep/150.0D ), 2.0D, 4.0D)), 1, 128)));
		}
		else
		{
			return (int)(Math.round(MathHelper.clamp( ((double)price * MathHelper.clamp( ( 2.0D - rep/3000.0D ), 1.0D, 2.0D)), 1, 128)));
		}
	}
	
	// < RIGHT
	public static int getBuyPrice(int price, double rep) // buy price is increased the higher reputation
	{
		// System.out.println(price + "   " + (int)(Math.round(MathHelper.clamp(((double)price * MathHelper.clamp( ( 1.0D - rep/6000.0D ), 0.5D, 1.0D)), 1, 128))));
		
		if ( rep < 0 )
		{
			return (int)(Math.round(MathHelper.clamp(((double)price * MathHelper.clamp( ( 0.5D - rep/1200.0D ), 0.25D, 0.5D)), 1, 128)));
		}
		else
		{
			return (int)(Math.round(MathHelper.clamp(((double)price * MathHelper.clamp( ( 0.5D + rep/6000.0D ), 0.5D, 1.0D)), 1, 128)));
		}
	}
	
    public static int count(String str, char c)
    {
        int count = 0;

        for(int i=0; i < str.length(); i++)
        {    if(str.charAt(i) == c)
                count++;
        }

        return count;
    }
	
	
	
	
	
//	
//	
//	
//	
//	
//	private static ItemStack enchantedbook_luckofthesea()
//	{
//		ItemStack stack = new ItemStack(Items.FISHING_ROD);
//		stack.setStackDisplayName("Nat Pagle's Lucky Pole");
//		stack.addEnchantment(Enchantment.REGISTRY.getObjectById(61), 1);
//		stack.addEnchantment(Enchantment.REGISTRY.getObjectById(62), 1);
//		return stack;
//	}
//	
//	private static ItemStack level2Sword() {
//		ItemStack stack = new ItemStack(Items.IRON_SWORD);
//		stack.setStackDisplayName("Sol Sword");
//		stack.addEnchantment(Enchantment.REGISTRY.getObjectById(17), 3);
//		stack.addEnchantment(Enchantment.REGISTRY.getObjectById(16), 3);
//		return stack;
//	}
//	
//	private static ItemStack level3Sword() {
//		ItemStack stack = new ItemStack(Items.DIAMOND_SWORD);
//		stack.setStackDisplayName("Helios Sword");
//		stack.addEnchantment(Enchantment.REGISTRY.getObjectById(17), 2);
//		stack.addEnchantment(Enchantment.REGISTRY.getObjectById(16), 2);
//		return stack;
//	}
//	
//	private static ItemStack level4Sword() {
//		ItemStack stack = new ItemStack(Items.DIAMOND_SWORD);
//		stack.setStackDisplayName("Amaterasu Sword");
//		stack.addEnchantment(Enchantment.REGISTRY.getObjectById(17), 3);
//		stack.addEnchantment(Enchantment.REGISTRY.getObjectById(16), 3);
//		return stack;
//	}
//	
//	private static ItemStack level5Sword() {
//		ItemStack stack = new ItemStack(Items.DIAMOND_SWORD);
//		stack.setStackDisplayName("Ra Sword");
//		stack.addEnchantment(Enchantment.REGISTRY.getObjectById(17), 5);
//		stack.addEnchantment(Enchantment.REGISTRY.getObjectById(16), 5);
//		stack.addEnchantment(Enchantment.REGISTRY.getObjectById(70), 1);
//		return stack;
//	}
//
//	private static ItemStack level1BlastProtection() {
//		ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
//		ItemEnchantedBook.addEnchantment(stack, new EnchantmentData(Enchantment.REGISTRY.getObjectById(3), 1));
//		return stack;
//	}
//
//	private static ItemStack level2BlastProtection() {
//		ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
//		ItemEnchantedBook.addEnchantment(stack, new EnchantmentData(Enchantment.REGISTRY.getObjectById(3), 2));
//		return stack;
//	}
//
//	private static ItemStack level3BlastProtection() {
//		ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
//		ItemEnchantedBook.addEnchantment(stack, new EnchantmentData(Enchantment.REGISTRY.getObjectById(3), 3));
//		return stack;
//	}
//
//	private static ItemStack level4BlastProtection() {
//		ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
//		ItemEnchantedBook.addEnchantment(stack, new EnchantmentData(Enchantment.REGISTRY.getObjectById(3), 4));
//		return stack;
//	}
	
}