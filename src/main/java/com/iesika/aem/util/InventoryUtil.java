package com.iesika.aem.util;

import java.util.ArrayList;
import java.util.List;

import net.blacklab.lmr.inventory.InventoryLittleMaid;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class InventoryUtil {
	//戻り値は入り切らなかったアイテムスタックのstackSize
	//simがtrueならインベントリの中身を直接渡さずスタックのインスタンスをコピーして渡さないと正しく動かない
	public static int insertIntoMaidInventory(ItemStack stack, InventoryLittleMaid maidInventory, boolean sim) {
		final int limit = 64;
		if (stack == null || maidInventory == null) {
			return stack == null ? 0 : stack.stackSize;
		}
		for (int slot = 0; slot < InventoryLittleMaid.maxInventorySize; slot++) {
			if (stack.stackSize > 0 && maidInventory.isItemValidForSlot(slot, stack)) {
				int maxStackSize = Math.min(maidInventory.getInventoryStackLimit(), stack.getMaxStackSize());
				ItemStack existing = maidInventory.getStackInSlot(slot);
				boolean shouldMerge = existing != null && existing.stackSize < maxStackSize && existing.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(existing, stack);
				if (shouldMerge) {
					int space = maxStackSize - existing.stackSize;
					int amount = Math.min(space, Math.min(stack.stackSize, limit));
					stack.stackSize -= amount;
					if (!sim){
						existing.stackSize += amount;
						maidInventory.markDirty();
					}
				} else if (existing == null) {
					int amount = Math.min(maxStackSize, Math.min(stack.stackSize, limit));
					if(!sim){
						maidInventory.setInventorySlotContents(slot, stack.splitStack(amount));
						maidInventory.markDirty();
					}else{
						stack.splitStack(amount);
					}
				}
			}
			if (stack.stackSize <= 0){
				return 0;
			}
		}
		return stack.stackSize;
	}

	//simがtrueならインベントリの中身を直接渡さずスタックのインスタンスをコピーして渡さないと正しく動かない
	public static int insertIntoInventory(ItemStack stack, IInventory inventory, EnumFacing facing, boolean sim) {
		final int limit = 64;
		if (stack == null || inventory == null) {
			return stack == null ? 0 : stack.stackSize;
		}
		List<Integer> range = new ArrayList<Integer>();
		if (inventory instanceof ISidedInventory) {
			ISidedInventory sided = (ISidedInventory) inventory;
			int[] accessible = sided.getSlotsForFace(facing);
			for (int slot : accessible) {
				range.add(slot);
			}
		} else {
			for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
				range.add(slot);
			}
		}
		for (Integer slot : range){
			if (stack.stackSize > 0 && inventory.isItemValidForSlot(slot, stack)) {
				int maxStackSize = Math.min(inventory.getInventoryStackLimit(), stack.getMaxStackSize());
				ItemStack existing = inventory.getStackInSlot(slot);
				boolean shouldMerge = existing != null && existing.stackSize < maxStackSize && existing.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(existing, stack);
				if (shouldMerge) {
					int space = maxStackSize - existing.stackSize;
					int amount = Math.min(space, Math.min(stack.stackSize, limit));
					stack.stackSize -= amount;
					if (!sim){
						existing.stackSize += amount;
						inventory.markDirty();
					}
				} else if (existing == null) {
					int amount = Math.min(maxStackSize, Math.min(stack.stackSize, limit));
					if(!sim){
						inventory.setInventorySlotContents(slot, stack.splitStack(amount));
						inventory.markDirty();
					}else{
						stack.splitStack(amount);
					}
				}
			}
			if (stack.stackSize <= 0){
				return 0;
			}
		}

		return stack.stackSize;
	}

}
