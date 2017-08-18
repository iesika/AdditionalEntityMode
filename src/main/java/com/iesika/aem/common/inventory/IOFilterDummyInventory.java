package com.iesika.aem.common.inventory;

import java.util.HashMap;
import java.util.Map;

import com.iesika.aem.util.tasks.MaidTaskInOut;
import com.iesika.aem.util.tasks.MaidTaskManager;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;

public class IOFilterDummyInventory implements IInventory {

	private InventoryPlayer inventoryPlayer;
	private ItemStack[] dummys;
	private Vec3 pos;

	public IOFilterDummyInventory(InventoryPlayer inventoryPlayer, int x, int y, int z) {
		this.dummys = new ItemStack[9];
		this.inventoryPlayer = inventoryPlayer;
		this.pos = Vec3.createVectorHelper(x + 0.5D, y + 0.5D, z + 0.5D);
	}

	@Override
	public int getSizeInventory() {
		return dummys.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return dummys[slot];
	}

	@Override
	public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		this.dummys[slot] = stack;
		if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
			stack.stackSize = this.getInventoryStackLimit();
		}

		this.markDirty();
	}

	@Override
	public String getInventoryName() {
		return "JournalDummy";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 0;
	}

	@Override
	public void markDirty() {
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
		return true;
	}

	@Override
	public void openInventory() {
		//前回開いたインベントリをクリア
		clearInventory();
		MaidTaskManager mtm = new MaidTaskManager(inventoryPlayer.getCurrentItem());
		//JournalData jd = new JournalData(inventoryPlayer.getCurrentItem());
		mtm.shouldRegisterNewTask(pos, new MaidTaskInOut(pos));
		MaidTaskInOut mtb =  (MaidTaskInOut) mtm.getRegisteredTaskFromVec3(pos);
		Map<Byte, ItemStack> imap = mtb.filter;
		for (Map.Entry<Byte, ItemStack> bar : imap.entrySet()) {
			dummys[bar.getKey()] = bar.getValue();
		}
		mtm.writeToNBT(inventoryPlayer.getCurrentItem());
	}

	//パケットによりcurrentItemがcurrentではなくなる可能性がある
	@Override
	public void closeInventory() {
		MaidTaskManager mtm = new MaidTaskManager(inventoryPlayer.getCurrentItem());
		MaidTaskInOut mtb =  (MaidTaskInOut) mtm.getRegisteredTaskFromVec3(pos);
		Map<Byte, ItemStack> afilter = new HashMap<Byte, ItemStack>();
		for (byte i = 0; i < dummys.length; i++){
			if(dummys[i] != null){
				afilter.put(i, dummys[i]);
			}
		}
		mtb.filter = afilter;
		//許可されるアイテムがない場合
		if (mtb.filter.isEmpty() && mtb.whitelist){
			mtm.tasks.remove(mtb);
		}
		mtm.writeToNBT(inventoryPlayer.getCurrentItem());
	}

	@Override
	public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	private void clearInventory(){
		for (int i = 0; i < dummys.length; i++){
			dummys[i] = null;
		}
	}
}
