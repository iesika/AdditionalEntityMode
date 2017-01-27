package com.iesika.aem.common;

import com.iesika.aem.common.items.Journal;

import net.minecraft.item.Item;

public class AEMItems {
	public static Item journal;

	public static void registryItems(){
		journal = new Journal();
	}
}
