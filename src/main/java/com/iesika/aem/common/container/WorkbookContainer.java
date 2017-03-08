package com.iesika.aem.common.container;

import com.iesika.aem.common.inventory.SlotWorkbook;
import com.iesika.aem.common.inventory.WorkbookFilterInventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class WorkbookContainer extends Container {

	private WorkbookFilterInventory filterInventory;
	private ItemStack workbook;

	public WorkbookContainer(int x, int y, int z, EntityPlayer entityPlayer, int GUIID) {
		filterInventory = new WorkbookFilterInventory(entityPlayer.inventory, x, y, z);
		this.workbook = entityPlayer.inventory.getCurrentItem();
		filterInventory.openInventory(entityPlayer);

		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				this.addSlotToContainer(new SlotWorkbook(filterInventory, j + i * 3, 62 + j * 18, 17 + i * 18));
			}
		}
		//dispenserのソースより
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlotToContainer(new SlotWorkbook(entityPlayer.inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int i = 0; i < 9; ++i) {
			this.addSlotToContainer(new SlotWorkbook(entityPlayer.inventory, i, 8 + i * 18, 142));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}

	@Override
	protected Slot addSlotToContainer(Slot p_75146_1_) {
		return super.addSlotToContainer(p_75146_1_);
	}

	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);
		filterInventory.closeInventory(playerIn);
	}
}
