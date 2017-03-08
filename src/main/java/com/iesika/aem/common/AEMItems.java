package com.iesika.aem.common;

import com.iesika.aem.common.items.Workbook;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class AEMItems {
	public static Item workbook;

	public static void registerItems(CreativeTabs aemTab) {
		workbook = new Workbook();

		workbook.setCreativeTab(aemTab);
	}
}
