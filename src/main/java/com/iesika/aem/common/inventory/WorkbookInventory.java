package com.iesika.aem.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

public class WorkbookInventory implements IInventory {

	private InventoryPlayer inventoryPlayer;
	private ItemStack[] filters;

	public WorkbookInventory(InventoryPlayer inventoryPlayer) {
		this.filters = new ItemStack[9];
		this.inventoryPlayer = inventoryPlayer;
	}

	@Override
	public String getName() {
		return "List";
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
		// TODO 自動生成されたメソッド・スタブ
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
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	@Override
	public void openInventory(EntityPlayer player) {

	}

	@Override
	public void closeInventory(EntityPlayer player) {

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
