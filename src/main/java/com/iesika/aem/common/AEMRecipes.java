package com.iesika.aem.common;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class AEMRecipes {

	public static void registeryRecipes(){
		//Workbookのレシピ
		//tier0
		GameRegistry.addShapelessRecipe(new ItemStack(AEMItems.workbook, 1, 0), Items.WRITABLE_BOOK, Items.IRON_INGOT);
		//tier1
		GameRegistry.addShapelessRecipe(new ItemStack(AEMItems.workbook, 1, 1), Items.WRITABLE_BOOK, Items.GOLD_INGOT);
		//tier2
		GameRegistry.addShapelessRecipe(new ItemStack(AEMItems.workbook, 1, 2), Items.WRITABLE_BOOK, Items.DIAMOND);
	}

}
