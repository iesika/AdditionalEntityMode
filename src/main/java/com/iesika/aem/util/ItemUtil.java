package com.iesika.aem.util;

import com.iesika.aem.common.AEMItems;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemUtil {
	// メソッド名おもいつかない
	public static boolean isValidItemStackForCarrierMode(ItemStack litemstack) {
		if (litemstack != null) {
			if (litemstack.getItem() == AEMItems.journal && litemstack.hasTagCompound()) {
				return true;
			}
		}
		return false;
	}

	public static boolean isItemMatch(ItemStack filter, ItemStack item){
		if (filter == null || item == null){
			return false;
		}
		return Item.getIdFromItem(filter.getItem()) == Item.getIdFromItem(item.getItem());
	}

	public static boolean isNBTMatch(ItemStack filter, ItemStack item){
	    if (filter.stackTagCompound == null && item.stackTagCompound == null) return true;
	    if (filter.stackTagCompound == null || item.stackTagCompound == null) return false;
	    NBTTagCompound filterTag = (NBTTagCompound) filter.getTagCompound().copy();
	    NBTTagCompound itemTag = (NBTTagCompound) item.getTagCompound().copy();
	    return filterTag.equals(itemTag);
	}

}
