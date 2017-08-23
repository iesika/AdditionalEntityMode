package com.iesika.aem.common.inventory;

import java.util.HashMap;
import java.util.Map;

import com.iesika.aem.common.handler.GuiHandler;
import com.iesika.aem.common.tasks.MaidTaskItemIO;
import com.iesika.aem.common.tasks.MaidTaskManager;
import com.iesika.aem.util.Logger;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;

public class WorkbookFilterInventory implements IInventory {

	private InventoryPlayer inventoryPlayer;
	private ItemStack[] filters;
	private BlockPos pos;

	public WorkbookFilterInventory(InventoryPlayer inventoryPlayer, int x, int y, int z, int guiID) {
		this.filters = new ItemStack[9];
		this.inventoryPlayer = inventoryPlayer;
		this.pos = new BlockPos(x, y, z);
	}

	public int getInventorySizeFromGuiID(int guiID) {
		switch (guiID) {
		case GuiHandler.AEM_WORKBOOK_TIER0_GUI_ID:
			return 1;
		case GuiHandler.AEM_WORKBOOK_TIER1_GUI_ID:
			return 5;
		case GuiHandler.AEM_WORKBOOK_TIER2_GUI_ID:
			return 9;
		default:
			Logger.info("incorrect guiID : WorkbookFilterInventory");
			return 1;
		}
	}

	@Override
	public String getName() {
		return "Filter";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return null;
	}

	@Override
	public int getSizeInventory() {
		return filters.length;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return filters[index];
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		return null;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		this.filters[index] = stack;
		if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
			stack.stackSize = this.getInventoryStackLimit();
		}
		this.markDirty();
	}

	@Override
	public int getInventoryStackLimit() {
		return 0;
	}

	@Override
	public void markDirty() {}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return false;
	}

	@Override
	public void openInventory(EntityPlayer player) {
		for (int i = 0; i < filters.length; i++) {
			filters[i] = null;
		}
		MaidTaskManager mtm = new MaidTaskManager(inventoryPlayer.getCurrentItem());
		mtm.shouldRegisterNewTask(pos, new MaidTaskItemIO(pos));
		for (Map.Entry<Byte, ItemStack> bar : ((MaidTaskItemIO) mtm.getRegisteredTaskFromBlockPos(pos)).filter.entrySet()) {
			filters[bar.getKey()] = bar.getValue();
		}
		mtm.writeToNBT(inventoryPlayer.getCurrentItem());
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		MaidTaskManager maidTaskManager = new MaidTaskManager(inventoryPlayer.getCurrentItem());
		MaidTaskItemIO mtb =  (MaidTaskItemIO) maidTaskManager.getRegisteredTaskFromBlockPos(pos);
		Map<Byte, ItemStack> afilter = new HashMap<Byte, ItemStack>();
		for (byte i = 0; i < filters.length; i++){
			if(filters[i] != null){
				afilter.put(i, filters[i]);
			}
		}
		mtb.filter = afilter;
		//許可されるアイテムがない場合
		if (mtb.filter.isEmpty() && mtb.isWhitelist){
			maidTaskManager.tasks.remove(mtb);
		}
		maidTaskManager.writeToNBT(inventoryPlayer.getCurrentItem());
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return false;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {

	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {

	}

}
