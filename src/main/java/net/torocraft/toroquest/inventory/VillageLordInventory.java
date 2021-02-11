package net.torocraft.toroquest.inventory;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.toroquest.civilization.CivilizationsWorldSaveData;
import net.torocraft.toroquest.civilization.Province;
import net.torocraft.toroquest.civilization.quests.QuestBase;
import net.torocraft.toroquest.entities.EntityVillageLord;

public class VillageLordInventory extends InventoryBasic implements IVillageLordInventory
{
	// *** VillageLordContainer ***
	
	private final EntityVillageLord lord;

	public VillageLordInventory(EntityVillageLord lord, String inventoryTitle, int slotCount) {
		super(inventoryTitle, false, slotCount);
		this.lord = lord;
	}

	@SideOnly(Side.CLIENT)
	public VillageLordInventory(EntityVillageLord lord, ITextComponent inventoryTitle, int slotCount) {
		super(inventoryTitle, slotCount);
		this.lord = lord;
	}

	// Index = 0,1,2,3
	@Override
	public List<ItemStack> getGivenItems()
	{
		List<ItemStack> items = new ArrayList<ItemStack>();
		for (int i = 0; i < 4; i++)
		{
			items.add(removeStackFromSlot(i));
		}
		return items;
	}

	@Override
	public void setGivenItems(List<ItemStack> items)
	{
		dropItems(getGivenItems());
		items = QuestBase.removeEmptyItemStacks(items);
		items = dropOverItems(items, 4);
		for (int i = 0; i < 4; i++)
		{
			if (i >= items.size())
			{
				setInventorySlotContents(i, ItemStack.EMPTY);
			}
			else
			{
				setInventorySlotContents(i, items.get(i));
			}
		}
	}

	// Index = 4
	@Override
	public List<ItemStack> getReturnItems()
	{
		List<ItemStack> items = new ArrayList<ItemStack>();
		items.add(removeStackFromSlot(5));
		return items;
	}
	
//	@Override
//	public List<ItemStack> getReturnItems()
//	{
//		List<ItemStack> items = new ArrayList<ItemStack>();
//		for (int i = 0; i < 4; i++)
//		{
//			items.add(removeStackFromSlot(i + 5));
//		}
//		return items;
//	}

	@Override
	public void setReturnItems(List<ItemStack> items)
	{
		dropItems(getReturnItems());
		items = QuestBase.removeEmptyItemStacks(items);
		items = dropOverItems(items, 5);
		if ( items.size() < 1 )
		{
			setInventorySlotContents(5, ItemStack.EMPTY);
		}
		else
		{
			setInventorySlotContents(5, items.get(0));
		}
	}
	
	@Override
	public void setReturnItems(ItemStack items)
	{
		setInventorySlotContents(5, items);
	}
	
//	@Override
//	public void setReturnItems(List<ItemStack> items)
//	{
//		dropItems(getReturnItems());
//		items = QuestBase.removeEmptyItemStacks(items);
//		items = dropOverItems(items, 4);
//		for (int i = 0; i < 4; i++)
//		{
//			if (i >= items.size())
//			{
//				setInventorySlotContents(i + 5, ItemStack.EMPTY);
//			}
//			else
//			{
//				setInventorySlotContents(i + 5, items.get(i));
//			}
//		}
//	}

	@Override
	public ItemStack getDonationItem()
	{
		return getStackInSlot(4);
	}

	@Override
	public void setDonationItem(ItemStack item)
	{
		setInventorySlotContents(4, item);
	}
	
	

	private List<ItemStack> dropOverItems(List<ItemStack> items, int maxIndex)
	{
		if (items.size() <= maxIndex)
		{
			return items;
		}

		List<ItemStack> over = new ArrayList<ItemStack>();
		for (int i = maxIndex; i < items.size(); i++)
		{
			over.add(items.get(i));
		}

		for (ItemStack item : over)
		{
			items.remove(item);
		}

		dropItems(over);

		return items;
	}

	private void dropItems(List<ItemStack> items)
	{
		for (ItemStack stack : items)
		{
			EntityItem dropItem = new EntityItem(lord.world, lord.posX, lord.posY, lord.posZ, stack);
			dropItem.setNoPickupDelay();
			lord.world.spawnEntity(dropItem);
		}
	}

	public NBTTagList saveAllItems()
	{
		NBTTagList list = new NBTTagList();
		for (int i = 0; i < getSizeInventory(); ++i) {
			ItemStack itemstack = (ItemStack) getStackInSlot(i);
			if (!itemstack.isEmpty()) {
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Slot", (byte) i);
				itemstack.writeToNBT(nbttagcompound);
				list.appendTag(nbttagcompound);
			}
		}
		return list;
	}

	public void loadAllItems(NBTTagList list)
	{
		for (int i = 0; i < list.tagCount(); ++i)
		{
			NBTTagCompound nbttagcompound = list.getCompoundTagAt(i);
			int slot = nbttagcompound.getByte("Slot") & 255;
			if (slot >= 0 && slot < getSizeInventory())
			{
				setInventorySlotContents(slot, new ItemStack(nbttagcompound));
			}
		}
	}

	@Override
	public Province getProvince()
	{
		return CivilizationsWorldSaveData.get(lord.world).atLocation(lord.chunkCoordX, lord.chunkCoordZ);
	}

	@Override
	public int getEntityId()
	{
		return lord.getEntityId();
	}
	
	@Override
	public boolean hasTrophy(String trophyName)
	{
		List<ItemStack> trophies = getTrophies();
		for ( ItemStack trophy : trophies )
		{
			// System.out.println(trophy.getUnlocalizedName());
			// System.out.println(trophyName);
			if (trophy.getUnlocalizedName().equals((trophyName)))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean addTrophy(Item item)
	{
		List<ItemStack> trophies = getTrophies();
		//int trophyInventorySize = 5;
		
		int slot = 0;
		int openSlot = -1;
		
		for ( ItemStack itemstack : trophies )
		{
			if ( itemstack == ItemStack.EMPTY )
			{
				openSlot = slot;
			}
			else if ( item == itemstack.getItem() )
			{
				return false;
			}
			slot++;
		}
		
		if ( openSlot < 0 )
		{
			return false;
		}
		
		setInventorySlotContents( openSlot + 6, new ItemStack(item,1));
		return true;

		// 11000
//		for (int i = 5; i > 5; i--) // TROPHY AMOUNT
//		{
//			if (i > trophies.size())
//			{
//				setInventorySlotContents(i + 6, ItemStack.EMPTY);
//			}
//			elseCOCO
//			{
//				setInventorySlotContents(i + 6, trophies.get(i));
//			}
//		}
//		
//		return false;
	}

	@Override
	public List<ItemStack> getTrophies()
	{
		List<ItemStack> items = new ArrayList<ItemStack>();
		for (int i = 0; i < 8; i++)
		{
			items.add(getStackInSlot(i + 6));
		}
		return items;
	}
	
}