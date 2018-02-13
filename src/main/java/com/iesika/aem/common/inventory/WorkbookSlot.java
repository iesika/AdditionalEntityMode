package com.iesika.aem.common.inventory;

import com.iesika.aem.common.AEMItems;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class WorkbookSlot extends Slot{

	public WorkbookSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
	}

	@Override
	public boolean canTakeStack(EntityPlayer playerIn) {
		return !(getHasStack() && getStack().getItem() == AEMItems.workbook);
	}
}
