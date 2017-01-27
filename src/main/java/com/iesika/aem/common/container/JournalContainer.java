package com.iesika.aem.common.container;

import com.iesika.aem.common.inventory.JournalDummyInventory;
import com.iesika.aem.common.inventory.SlotJournal;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class JournalContainer extends Container {

	private JournalDummyInventory dummyinventory;
	private ItemStack journal;

    public JournalContainer(int x, int y, int z, InventoryPlayer inventoryPlayer) {
    	dummyinventory = new JournalDummyInventory(inventoryPlayer, x, y, z);
        //開いているということは,currentItemはJournalのはず
        this.journal = inventoryPlayer.getCurrentItem();
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
                this.addSlotToContainer(new SlotJournal(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (i = 0; i < 9; ++i){
            this.addSlotToContainer(new SlotJournal(inventoryPlayer, i, 8 + i * 18, 142));
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
