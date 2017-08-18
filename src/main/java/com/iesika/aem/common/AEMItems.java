package com.iesika.aem.common;

import com.iesika.aem.common.items.Memo;

import net.minecraft.item.Item;

public class AEMItems {
	public static Item journal;
	public static Item memo;

	public static void registryItems(){
		//journal = new Journal();
		memo = new Memo();
	}
}
