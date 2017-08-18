package com.iesika.aem.common.ai;

import java.util.List;

import com.iesika.aem.common.config.AEMConfig;
import com.iesika.aem.util.ItemUtil;
import com.iesika.aem.util.JournalData;
import com.iesika.aem.util.Logger;
import com.iesika.aem.util.Pos3;

import littleMaidMobX.LMM_EntityLittleMaid;
import littleMaidMobX.LMM_EnumSound;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class EntityAICarrier extends EntityAIBase {

	protected LMM_EntityLittleMaid owner;
	protected boolean enable;
	// メイドさんの手の届く範囲
	private final double maidHandRange = 1.5D;
	// 再始動までの時間
	private final int cooltimeMax = AEMConfig.carrierModeCooltime;
	public int cooltime;
	// インベントリ操作中か
	private boolean opinv;
	// インベントリ側から見て搬入か
	private boolean isImporting;
	// trueならAI続行
	private boolean cflag;
	private JournalData journalData;
	private JournalData.JDPoint targetJDPoint;

	public EntityAICarrier(LMM_EntityLittleMaid par1EntityLittleMaid) {
		this.owner = par1EntityLittleMaid;
		this.enable = false;
		this.cooltime = cooltimeMax;
		this.isImporting = false;
		setMutexBits(7);
	}

	@Override
	public boolean shouldExecute() {
		if (owner.isMaidWait()) {
			return false;
		}
		Logger.info("cooltime : " + cooltime);
		// cooltime判別
		cooltime++;
		if (cooltime < cooltimeMax) {
			return false;
		}
		cooltime = 0;
		Logger.info("cooltime clear");
		ItemStack journal = owner.maidInventory.getStackInSlot(0);
		if (!ItemUtil.isValidItemStackForCarrierMode(journal)) {
			return false;
		}
		journalData = new JournalData(journal);

		// 搬出/搬入地点が１つでもあれば開始
		if (journalData.data == null || journalData.data.size() == 0) {
			return false;
		}
		return true;
	}

	@Override
	public void startExecuting() {
		Logger.info("startExecuting()");
		cflag = true;
	}

	@Override
	public boolean continueExecuting() {
		if (owner.isMaidWait()) {
			return false;
		}
		return cflag;
	}

	@Override
	public void updateTask() {
		if (opinv) {// インベントリ操作中
			opinv = false;
			boolean flag;
			if (!isImporting) {// 搬出中
				flag = getItemFromTargetInventory();
			} else {// 搬入中
				//flag = putItemToTargetInventory();
			}
		} else if (!owner.getNavigator().noPath()) {// 移動中
			Pos3 pos3 = targetJDPoint.pos;
			TileEntity tileEntity = owner.worldObj.getTileEntity(pos3.x, pos3.y, pos3.z);
			if (tileEntity != null && tileEntity instanceof IInventory && !owner.isAirBorne) {
				if (owner.getDistanceTilePos(tileEntity) < maidHandRange) {// 目標に接近
					Logger.info("reached!");
					opinv = true;
					owner.getNavigator().clearPathEntity();
					owner.setSwing(15, LMM_EnumSound.Null);
				}else{
					//何らかの理由でパス消失(entity同士の衝突とか？)
//					double dx = (double) pos3.x + 0.5D;
//					double dy = (double) pos3.y + 0.5D;
//					double dz = (double) pos3.z + 0.5D;
//					owner.getNavigator().tryMoveToXYZ(dx, dy, dz, 1.0F);
//					Logger.info("reset Target x:" + dx + " y:" + dy + " :z" + dz);
				}
			} else {// 目標を消失
				owner.getNavigator().clearPathEntity();
			}
		} else {// 何もしていない場合
			targetJDPoint = getNextJDPoint(isImporting);
			if (targetJDPoint == null) {// 次の搬入出地点が見つからない場合
				isImporting = !isImporting;
			} else {
				Pos3 pos3 = targetJDPoint.pos;
				TileEntity tileEntity = owner.worldObj.getTileEntity(pos3.x, pos3.y, pos3.z);
				if (tileEntity != null && tileEntity instanceof IInventory) {
					double dx = (double) pos3.x + 0.5D;
					double dy = (double) pos3.y + 0.5D;
					double dz = (double) pos3.z + 0.5D;
					Logger.info("set Target x:" + dx + " y:" + dy + " :z" + dz);
					boolean flag = owner.getNavigator().tryMoveToXYZ(dx, dy, dz, 1.0F);
					if (!flag) {
						Logger.info("cant reach x:" + dx + " y:" + dy + " :z" + dz);
					}
				}
			}
		}
	}

	@Override
	public boolean isInterruptible() {
		return false;
	}

	@Override
	public void resetTask() {
		// this.isExporting = false;
		targetJDPoint = null;
		isImporting = false;
		opinv = false;
		cflag = true;
		cooltime = 0;
		Logger.info("resetTask");
	}

	@Override
	public void setMutexBits(int mutex) {
		super.setMutexBits(mutex);
	}

	// 次のio地点を得る
	private JournalData.JDPoint getNextJDPoint(boolean isImport) {
		if (journalData == null || journalData.data == null) {
			return null;
		}
		boolean flag = false;
		for (int index = 0; index < journalData.data.size(); index++) {
			JournalData.JDPoint jp = journalData.data.get(index);
			if (jp != null && jp.isImport == isImport) {
				if (flag || targetJDPoint == null || targetJDPoint.isImport != isImport) {
					return jp;
				}
			}
			if (jp == targetJDPoint) {
				flag = true;
			}
		}
		return null;
	}

	// アイテムが１つでも移動すればtrue
	private boolean getItemFromTargetInventory() {
		if (targetJDPoint == null) {
			return false;
		}
		Pos3 pos3 = targetJDPoint.pos;
		TileEntity tileEntity = owner.worldObj.getTileEntity(pos3.x, pos3.y, pos3.z);
		if (tileEntity != null && tileEntity instanceof IInventory) {
			IInventory inventory = (IInventory) tileEntity;
			if (inventory instanceof ISidedInventory) {
				Logger.info("sided inventory");
				ISidedInventory sided = (ISidedInventory) inventory;
				// TODO どの方面からインベントリにアクセスするか(現在は上面からのみ)
				int[] accessible = sided.getAccessibleSlotsFromSide(1);
				for (int i : accessible) {
					ItemStack item = sided.getStackInSlot(i);
				}
			} else {
				Logger.info("normal inventory");
				inventory.openInventory();
				for (int i = 0; i < inventory.getSizeInventory(); i++) {
					ItemStack item = inventory.getStackInSlot(i);
					if (item != null) {
						owner.maidInventory.addItemStackToInventory(item);

						if (item.stackSize == 0){
							Logger.info("Stacksize = 0");

							item = null;
							inventory.setInventorySlotContents(i, null);
						}
						owner.maidInventory.markDirty();
					}
				}
				inventory.markDirty();
				inventory.closeInventory();
			}
			return false;
		} else {
			return false;
		}
	}

	//１つでも移動できるアイテムがあればtrue
	private boolean canMoveItemStack(IInventory targetInventory, boolean isWhitelist, boolean isIgnoreNBT){
		for (int slot = 0; slot < targetInventory.getSizeInventory(); slot++){
			ItemStack is = targetInventory.getStackInSlot(slot);
			boolean isMatch = false;
			if (is != null){
				for (ItemStack filter : targetJDPoint.items.values()) {
					if(filter == null){
						continue;
					}
					if((isWhitelist ^ ItemUtil.isItemMatch(filter, is)) & (isIgnoreNBT ^ ItemUtil.isNBTMatch(filter, is))){
						isMatch = true;
					}
				}
			}
		}


		return false;
	}

	private boolean putItemToTargetInventory(){
		return false;
	}

	private boolean isItemMatch(List<ItemStack> filter, ItemStack item, boolean isWhitelist) {
		if (item == null) {
			return false;
		}
		if (filter.size() == 0) {
			if (isWhitelist) {
				return false;
			} else {
				return true;
			}
		}
		for (ItemStack it : filter) {
			if (it != null && Item.getIdFromItem(it.getItem()) == Item.getIdFromItem(item.getItem())) {
				return true;
			}
		}
		return false;
	}

	private boolean isNBTMatch(ItemStack filter, ItemStack item) {
		if (filter.stackTagCompound == null && item.stackTagCompound == null)
			return true;
		if (filter.stackTagCompound == null || item.stackTagCompound == null)
			return false;
		NBTTagCompound filterTag = (NBTTagCompound) filter.getTagCompound().copy();
		NBTTagCompound itemTag = (NBTTagCompound) item.getTagCompound().copy();
		return filterTag.equals(itemTag);
	}

	private static boolean isItemStackable(ItemStack current) {
		return false;
	}

}
