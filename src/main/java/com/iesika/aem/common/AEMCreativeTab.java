package com.iesika.aem.common;

import com.iesika.aem.AdditionalEntityMode;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class AEMCreativeTab extends CreativeTabs {

	public AEMCreativeTab() {
		super(CreativeTabs.getNextID(), AdditionalEntityMode.NAME);
	}

	@Override
	public Item getTabIconItem() {
		return AEMItems.workbook;
	}

	@Override
	public int getIconItemDamage() {
		return 0;
	}

}
