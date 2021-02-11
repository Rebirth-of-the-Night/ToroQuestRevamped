// move items around and set up inventory boxes

package net.torocraft.toroquest.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.torocraft.toroquest.civilization.CivilizationDataAccessor;
import net.torocraft.toroquest.civilization.CivilizationUtil;
import net.torocraft.toroquest.civilization.CivilizationsWorldSaveData;
import net.torocraft.toroquest.civilization.Province;
import net.torocraft.toroquest.civilization.player.PlayerCivilizationCapabilityImpl;
import net.torocraft.toroquest.civilization.quests.util.QuestData;
import net.torocraft.toroquest.inventory.IVillageLordInventory;
import net.torocraft.toroquest.network.ToroQuestPacketHandler;
import net.torocraft.toroquest.network.message.MessageSetItemReputationAmount;
import net.torocraft.toroquest.network.message.MessageSetQuestInfo;

public class VillageLordContainer extends Container
{
	private final int HOTBAR_SLOT_COUNT = 9;
	private final int INVENTORY_ROW_COUNT = 3;
	private final int INVENTORY_COLUMN_COUNT = 9;
	
	private final int DONATE_ITEM_ROW_COUNT = 1;
	private final int DONATE_ITEM_COLUMN_COUNT = 1;
	
	private final int QUEST_INPUT_ITEM_ROW_COUNT = 4;
	private final int QUEST_INPUT_ITEM_COLUMN_COUNT = 1;
	
	private final int QUEST_OUTPUT_ITEM_ROW_COUNT = 1; // TODO changed from 4
	private final int QUEST_OUTPUT_ITEM_COLUMN_COUNT = 1;
	
	private final int TROPHY_ROW_COUNT = 4;
	private final int TROPHY_COLUMN_COUNT = 2;
	
	// this equals all non-vanilla slots combined
	private final int LORD_INVENTORY_SLOT_COUNT = TROPHY_ROW_COUNT*TROPHY_COLUMN_COUNT + QUEST_INPUT_ITEM_ROW_COUNT*QUEST_INPUT_ITEM_COLUMN_COUNT + DONATE_ITEM_ROW_COUNT*DONATE_ITEM_COLUMN_COUNT;
	
	private final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + (INVENTORY_COLUMN_COUNT * INVENTORY_ROW_COUNT);

	private final int VANILLA_FIRST_SLOT_INDEX = 0;
	// vanilla inventory index is from 0 to 35
	private final int LORD_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

	private final int SLOT_X_SPACING = 18;
	private final int SLOT_Y_SPACING = 18;

	private final int HOTBAR_XPOS = 8;
	private final int HOTBAR_YPOS = 106 + 109;

	private final int INVENTORY_XPOS = 8;
	private final int INVENTORY_YPOS = 48 + 109;

	private final int DONATE_ITEM_XPOS = 152;
	private final int DONATE_ITEM_YPOS = 18;

	private final int QUEST_INPUT_ITEM_XPOS = 8;
	private final int QUEST_INPUT_ITEM_YPOS = 56;

	private final int QUEST_OUTPUT_ITEM_XPOS = 152;
	private final int QUEST_OUTPUT_ITEM_YPOS = 133;

	private final int TROPHY_ITEM_XPOS = 191;
	private final int TROPHY_ITEM_YPOS = 56;

	private final IVillageLordInventory inventory;
	private final EntityPlayer player;

	private final List<Integer> inputSlot = new ArrayList<Integer>();
	private final List<Integer> outputSlot = new ArrayList<Integer>();
	private int donationGuiSlotId;

	public VillageLordContainer(EntityPlayer player, IVillageLordInventory inventory, World world)
	{
		this.player = player;
		this.inventory = inventory;
		this.inventory.openInventory(player);

		updateClientQuest();
		updateDonationInfo();

		int guiSlotIndex = 0;

		
		// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= Vanilla Inventory =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
		for (int x = 0; x < HOTBAR_SLOT_COUNT; x++)
		{
			addSlotToContainer(new Slot(player.inventory, x, HOTBAR_XPOS + SLOT_X_SPACING * x, HOTBAR_YPOS));
			guiSlotIndex++;
		}

		for (int x = 0; x < INVENTORY_ROW_COUNT; x++)
		{
			for (int y = 0; y < INVENTORY_COLUMN_COUNT; y++)
			{
				int slotNumber = HOTBAR_SLOT_COUNT + x * INVENTORY_COLUMN_COUNT + y;
				int xPos = INVENTORY_XPOS + y * SLOT_X_SPACING;
				int yPos = INVENTORY_YPOS + x * SLOT_Y_SPACING;
				addSlotToContainer(new Slot(player.inventory, slotNumber, xPos, yPos));
				guiSlotIndex++;
			}
		}
		// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

		
		
		
		
		
		
		
		// Index = 0,1,2,3
		for (int x = 0; x < QUEST_INPUT_ITEM_ROW_COUNT; x++)
		{
			for (int y = 0; y < QUEST_INPUT_ITEM_COLUMN_COUNT; y++)
			{
				int slotNumber = x * QUEST_INPUT_ITEM_COLUMN_COUNT + y;
				int xPos = QUEST_INPUT_ITEM_XPOS + y * SLOT_X_SPACING;
				int yPos = QUEST_INPUT_ITEM_YPOS + x * SLOT_Y_SPACING;
				addSlotToContainer(new Slot(this.inventory, slotNumber, xPos, yPos));
				inputSlot.add(guiSlotIndex);
				guiSlotIndex++;
			}
		}
		
		// Index = 4
		for (int x = 0; x < DONATE_ITEM_ROW_COUNT; x++)
		{
			for (int y = 0; y < DONATE_ITEM_COLUMN_COUNT; y++)
			{
				int slotNumber = x * DONATE_ITEM_COLUMN_COUNT + y + 4;
				int xPos = DONATE_ITEM_XPOS + y * SLOT_X_SPACING;
				int yPos = DONATE_ITEM_YPOS + x * SLOT_Y_SPACING;
				addSlotToContainer(new Slot(this.inventory, slotNumber, xPos, yPos));
				donationGuiSlotId = guiSlotIndex;
				guiSlotIndex++;
			}
		}
		
		// Index = 5
		for (int x = 0; x < QUEST_OUTPUT_ITEM_ROW_COUNT; x++)
		{
			for (int y = 0; y < QUEST_OUTPUT_ITEM_COLUMN_COUNT; y++)
			{
				int slotNumber = x * QUEST_OUTPUT_ITEM_COLUMN_COUNT + y + 5;
				int xPos = QUEST_OUTPUT_ITEM_XPOS + y * SLOT_X_SPACING;
				int yPos = QUEST_OUTPUT_ITEM_YPOS + x * SLOT_Y_SPACING;
				addSlotToContainer(new SlotOutput(this.inventory, slotNumber, xPos, yPos));
				outputSlot.add(guiSlotIndex);
				guiSlotIndex++;
			}
		}
		
		// Index = 6,7,8,9,10,11,12,13
		for (int x = 0; x < TROPHY_ROW_COUNT; x++)
		{
			for (int y = 0; y < TROPHY_COLUMN_COUNT; y++)
			{
				//System.out.println("===================" + guiSlotIndex);

				int slotNumber = x * TROPHY_COLUMN_COUNT + y + 6;
				//System.out.println("----------- " + slotNumber);

				int xPos = TROPHY_ITEM_XPOS + y * (SLOT_X_SPACING + 2);
				int yPos = TROPHY_ITEM_YPOS + x * SLOT_Y_SPACING;
				addSlotToContainer(new SlotTrophy(this.inventory, slotNumber, xPos, yPos));
				outputSlot.add(guiSlotIndex);
				guiSlotIndex++;
			}
		}

		addListener(new IContainerListener()
		{

			@Override
			public void sendAllContents(Container containerToSend, NonNullList<ItemStack> itemsList)
			{

			}

			@Override
			public void sendSlotContents(Container containerToSend, int slotInd, ItemStack stack)
			{
				if (slotInd == donationGuiSlotId)
				{
					donationItemUpdated(stack);
				}
			}

			@Override
			public void sendWindowProperty(Container containerIn, int varToUpdate, int newValue) {

			}

			@Override
			public void sendAllWindowProperties(Container containerIn, IInventory inventory) {

			}
		});
	}

	private void donationItemUpdated(ItemStack stack)
	{
		updateDonationInfo();
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index)
	{
		Slot slot = (Slot) this.inventorySlots.get(index);
		if (slot == null || !slot.getHasStack())
		{
			return ItemStack.EMPTY;
		}

		ItemStack sourceStack = slot.getStack();
		ItemStack copyOfSourceStack = sourceStack.copy();

		if (indexIsForAVanillaSlot(index))
		{
			if (!mergeItemStack(sourceStack, LORD_INVENTORY_FIRST_SLOT_INDEX, LORD_INVENTORY_FIRST_SLOT_INDEX + LORD_INVENTORY_SLOT_COUNT, false))
			{
				return ItemStack.EMPTY;
			}
		}
		else if (indexIsForALordInventorySlot(index) || indexIsForLordOutputSlot(index))
		{
			if (!mergeStackFromLordToPlayer(sourceStack))
			{
				return ItemStack.EMPTY;
			}
		}
		else
		{
			return ItemStack.EMPTY;
		}

		int stackSize = sourceStack.getCount();

		if (stackSize == 0)
		{
			slot.putStack(ItemStack.EMPTY);
		}
		else
		{
			slot.onSlotChanged();
		}
		return copyOfSourceStack;
	}

	private boolean mergeStackFromLordToPlayer(ItemStack sourceStack)
	{
		return mergeItemStack(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false);
	}

	private boolean indexIsForAVanillaSlot(int index)
	{
		return index >= VANILLA_FIRST_SLOT_INDEX && index < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;
	}

	private boolean indexIsForALordInventorySlot(int index)
	{
		return index >= LORD_INVENTORY_FIRST_SLOT_INDEX && index < LORD_INVENTORY_FIRST_SLOT_INDEX + LORD_INVENTORY_SLOT_COUNT;
	}

	private boolean indexIsForLordOutputSlot(int index)
	{
		// 0-3 input, 4 don, 5 out, 6-13 tro
		return index == LORD_INVENTORY_FIRST_SLOT_INDEX + 5;
	}
	
	private boolean indexIsForLordTrophySlot(int index)
	{
		return index > LORD_INVENTORY_FIRST_SLOT_INDEX + 5;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn)
	{
		return true;
	}

	@Override
	public void onContainerClosed(EntityPlayer player)
	{
		super.onContainerClosed(player);
		this.inventory.closeInventory(player);
	}

	public class SlotOutput extends Slot
	{
		
		public SlotOutput(IInventory inventoryIn, int index, int xPosition, int yPosition)
		{
			super(inventoryIn, index, xPosition, yPosition);
		}

		@Override
		public boolean isItemValid(ItemStack stack)
		{
			return false;
		}
	}
	
	public class SlotTrophy extends Slot
	{
		
		public SlotTrophy(IInventory inventoryIn, int index, int xPosition, int yPosition)
		{
			super(inventoryIn, index, xPosition, yPosition);
		}

		@Override
		public boolean isItemValid(ItemStack stack)
		{
			return false;
		}
		
		@Override
		public boolean canTakeStack(EntityPlayer playerIn)
	    {
	        return false;
	    }
	}

	private void updateDonationInfo()
	{
		// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= TROPHY =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
		if ( this.player != null && this.inventory != null )
		{
			if ( !this.player.world.isRemote ) ToroQuestPacketHandler.INSTANCE.sendTo(new MessageSetItemReputationAmount(this.inventory), (EntityPlayerMP) this.player);
			Province province = CivilizationUtil.getProvinceAt(this.player.getEntityWorld(), this.player.chunkCoordX, this.player.chunkCoordZ);
			if ( province == null )
			{
				return;
			}
			CivilizationDataAccessor worldData = CivilizationsWorldSaveData.get(this.player.world);
			if ( worldData == null )
			{
				return;
			}
			if ( this.inventory.hasTrophy("item.trophy_beholder") )
			{
				province.beholderTrophy = true;
				worldData.setTrophyBeholder(province.id, true);
			}
			if ( this.inventory.hasTrophy("item.trophy_titan") )
			{
				province.titanTrophy = true;
				worldData.setTrophyTitan(province.id, true);
			}
			if ( this.inventory.hasTrophy("item.royal_helmet") )
			{
				province.lordTrophy = true;
				worldData.setTrophyLord(province.id, true);
			}
			if ( this.inventory.hasTrophy("item.legendary_bandit_helmet") )
			{
				province.banditTrophy = true;
				worldData.setTrophyBandit(province.id, true);
			}
			if ( this.inventory.hasTrophy("item.trophy_mage") )
			{
				province.mageTrophy = true;
				worldData.setTrophyMage(province.id, true);
			}
			if ( this.inventory.hasTrophy("item.trophy_skeleton") )
			{
				province.skeletonTrophy = true;
				worldData.setTrophySkeleton(province.id, true);
			}
			if ( this.inventory.hasTrophy("item.trophy_spider") )
			{
				province.spiderTrophy = true;
				worldData.setTrophySpider(province.id, true);
			}
			if ( this.inventory.hasTrophy("item.trophy_pig") )
			{
				province.zombiepigTrophy = true;
				worldData.setTrophyPig(province.id, true);
			}
		}
	}
	
	private void updateClientQuest()
	{
		if (!player.world.isRemote)
		{
			Province province = CivilizationUtil.getProvinceAt(player.getEntityWorld(), player.chunkCoordX, player.chunkCoordZ);
			QuestData currentQuest = PlayerCivilizationCapabilityImpl.get(player).getCurrentQuestFor(province);
			QuestData nextQuest = PlayerCivilizationCapabilityImpl.get(player).getNextQuestFor(province);
			ToroQuestPacketHandler.INSTANCE.sendTo(new MessageSetQuestInfo(province, currentQuest, nextQuest), (EntityPlayerMP) player);
		}
	}

}
