package com.iesika.aem.common.container;

import com.iesika.aem.common.inventory.IOFilterDummyInventory;
import com.iesika.aem.common.inventory.IOFilterSlot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

public class IOFilterContainer extends Container {

	private IOFilterDummyInventory dummyinventory;

    public IOFilterContainer(int x, int y, int z, EntityPlayer e) {
    	InventoryPlayer inventoryPlayer = e.inventory;
    	dummyinventory = new IOFilterDummyInventory(inventoryPlayer, x, y, z);
        dummyinventory.openInventory();
        int i;
        int j;

        for (i = 0; i < 3; ++i){
            for (j = 0; j < 3; ++j){
                this.addSlotToContainer(new Slot(dummyinventory, j + i * 3, 62 + j * 18, 17 + i * 18));
            }
        }
        //dispenserのソースより
        for (i = 0; i < 3; ++i){
            for (j = 0; j < 9; ++j){
                this.addSlotToContainer(new IOFilterSlot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (i = 0; i < 9; ++i){
            this.addSlotToContainer(new IOFilterSlot(inventoryPlayer, i, 8 + i * 18, 142));
        }
    }

	@Override
	public boolean canInteractWith(EntityPlayer p_75145_1_) {
		return true;
	}

	@Override
	protected Slot addSlotToContainer(Slot p_75146_1_) {
		return super.addSlotToContainer(p_75146_1_);
	}

	@Override
	public void onContainerClosed(EntityPlayer p_75134_1_){
		super.onContainerClosed(p_75134_1_);
		dummyinventory.closeInventory();
	}
}
