package com.iesika.aem.common.container;

import com.iesika.aem.common.handler.GuiHandler;
import com.iesika.aem.common.inventory.ItemFilterSlot;
import com.iesika.aem.common.inventory.ItemFilterInventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ItemFilterContainer extends Container {

	private ItemFilterInventory filterInventory;
	private ItemStack workbook;

	public ItemFilterContainer(int x, int y, int z, EntityPlayer entityPlayer, int guiID) {
		filterInventory = new ItemFilterInventory(entityPlayer.inventory, x, y, z, guiID);
		this.workbook = entityPlayer.inventory.getCurrentItem();
		filterInventory.openInventory(entityPlayer);

		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				if (guiID == GuiHandler.AEM_ITEMFILTER_TIER2_GUI_ID){
					this.addSlotToContainer(new ItemFilterSlot(filterInventory, j + i * 3, 62 + j * 18, 17 + i * 18));
				}else if (guiID == GuiHandler.AEM_ITEMFILTER_TIER1_GUI_ID && (i == 1 || j == 1) ){
					this.addSlotToContainer(new ItemFilterSlot(filterInventory, j + i * 3, 62 + j * 18, 17 + i * 18));
				}else if(guiID == GuiHandler.AEM_ITEMFILTER_TIER0_GUI_ID && (i == 1 && j == 1)){
					this.addSlotToContainer(new ItemFilterSlot(filterInventory, j + i * 3, 62 + j * 18, 17 + i * 18));
				}
			}
		}

		//dispenserのソースより
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlotToContainer(new ItemFilterSlot(entityPlayer.inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int i = 0; i < 9; ++i) {
			this.addSlotToContainer(new ItemFilterSlot(entityPlayer.inventory, i, 8 + i * 18, 142));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}

	@Override
	protected Slot addSlotToContainer(Slot slot) {
		return super.addSlotToContainer(slot);
	}

	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);
		filterInventory.closeInventory(playerIn);
	}
}
