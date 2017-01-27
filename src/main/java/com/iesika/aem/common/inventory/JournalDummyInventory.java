package com.iesika.aem.common.inventory;

import java.util.Map;

import com.iesika.aem.util.JournalData;
import com.iesika.aem.util.Logger;
import com.iesika.aem.util.Pos3;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class JournalDummyInventory implements IInventory {

	private InventoryPlayer inventoryPlayer;
	private ItemStack[] dummys;
	private Pos3 point;

	public JournalDummyInventory(InventoryPlayer inventoryPlayer, int x, int y, int z) {
		this.dummys = new ItemStack[9];
		this.inventoryPlayer = inventoryPlayer;
		this.point = new Pos3(x, y, z);
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
//		if (this.dummys[slot] != null) {
//			ItemStack itemstack = this.dummys[slot];
//			this.dummys[slot] = null;
//			return itemstack;
//		}
		return null;
	}

	@Override
	public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {
		this.dummys[p_70299_1_] = p_70299_2_;
		if (p_70299_2_ != null && p_70299_2_.stackSize > this.getInventoryStackLimit()) {
			p_70299_2_.stackSize = this.getInventoryStackLimit();
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
		for (int i = 0; i < dummys.length; i++){
			dummys[i] = null;
		}
		Logger.info(inventoryPlayer.getCurrentItem().toString());
		JournalData jd = new JournalData(inventoryPlayer.getCurrentItem());
		jd.registerNewPoint(point);
		Map<Byte, ItemStack> imap = jd.getItemStackMap(point);
		for (Map.Entry<Byte, ItemStack> bar : imap.entrySet()) {
			dummys[bar.getKey()] = bar.getValue();
		}
		jd.writeToNBT(inventoryPlayer.getCurrentItem());
	}

	//パケットによりcurrentItemがcurrentではなくなる可能性がある
	@Override
	public void closeInventory() {
		JournalData jd = new JournalData(inventoryPlayer.getCurrentItem());
		jd.setItemStackData(point, dummys);
		//許可されるアイテムがない場合
		if (jd.getItemStackMap(point).isEmpty() && jd.isWhitelist(point)){
			jd.removeJournalPointData(point);
		}
		jd.writeToNBT(inventoryPlayer.getCurrentItem());

//		// ItemStackをセットする。NBTは右クリック等のタイミングでしか保存されないため再セットで保存している。
//		ItemStack result = new ItemStack(tinventoryPlayer.getCurrentItem().getItem(), 1);
//		result.setTagCompound(nbt);
//		tinventoryPlayer.mainInventory[tinventoryPlayer.currentItem] = result;
	}

	@Override
	public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}
}
