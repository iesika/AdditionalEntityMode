package com.iesika.aem.common.container;

import com.iesika.aem.common.handler.GuiHandler;
import com.iesika.aem.common.inventory.WorkbookInventory;
import com.iesika.aem.common.inventory.WorkbookSlot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class WorkbookContainer extends Container {

	private WorkbookInventory workbookInventory;
	private ItemStack workbook;

	public WorkbookContainer(int x, int y, int z, EntityPlayer entityPlayer, int guiID) {
		workbookInventory = new WorkbookInventory(entityPlayer.inventory);
		//workbookのGUIなので持ってるのはworkbookのはず・・・
		this.workbook = entityPlayer.inventory.getCurrentItem();
		workbookInventory.openInventory(entityPlayer);

		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				if (guiID == GuiHandler.AEM_WORKBOOK_TIER2_GUI_ID){
					this.addSlotToContainer(new WorkbookSlot(workbookInventory, j + i * 3, 62 + j * 18, 53 + i * 18));
				}else if (guiID == GuiHandler.AEM_WORKBOOK_TIER1_GUI_ID && (i == 1 || j == 1) ){
					this.addSlotToContainer(new WorkbookSlot(workbookInventory, j + i * 3, 62 + j * 18, 53 + i * 18));
				}else if(guiID == GuiHandler.AEM_WORKBOOK_TIER0_GUI_ID && (i == 1 && j == 1)){
					this.addSlotToContainer(new WorkbookSlot(workbookInventory, j + i * 3, 62 + j * 18, 53 + i * 18));
				}
			}
		}

		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlotToContainer(new WorkbookSlot(entityPlayer.inventory, j + i * 9 + 9, 8 + j * 18, 120 + i * 18));
			}
		}

		for (int i = 0; i < 9; ++i) {
			this.addSlotToContainer(new WorkbookSlot(entityPlayer.inventory, i, 8 + i * 18, 178));
		}
	}

	@Override
	protected Slot addSlotToContainer(Slot slot) {
		return super.addSlotToContainer(slot);
	}

	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);
		workbookInventory.closeInventory(playerIn);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}

}
