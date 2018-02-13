package com.iesika.aem.common.ai;

import java.util.List;

import com.iesika.aem.common.AEMItems;
import com.iesika.aem.common.tasks.MaidTaskItemIO;
import com.iesika.aem.mode.EntityModeItemCarrier;
import com.iesika.aem.util.InventoryUtil;

import net.blacklab.lmr.entity.littlemaid.EntityLittleMaid;
import net.blacklab.lmr.entity.littlemaid.mode.EntityModeBase;
import net.blacklab.lmr.util.EnumSound;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

//メイド側からインベントリにアイテムを移すAI
public class EntityAIImport extends EntityAIBase {

	private EntityLittleMaid owner;
	protected EntityModeItemCarrier cmode;
	protected List<MaidTaskItemIO> importList;
	protected MaidTaskItemIO ptask;// 処理中のタスク

	private boolean opinv;
	private boolean cflag;

	public EntityAIImport(EntityLittleMaid owner) {
		this.owner = owner;
		this.opinv = false;
		this.cflag = true;
		setMutexBits(7);
	}

	@Override
	public boolean shouldExecute() {
		if (owner.isMaidWait()) {
			return false;
		}
		EntityModeBase fmodeBase = owner.getActiveModeClass();
		if (fmodeBase instanceof EntityModeItemCarrier) {
			cmode = (EntityModeItemCarrier) fmodeBase;
			if (cmode.maidTaskManager != null) {
				importList = cmode.maidTaskManager.getIOList(true);
				if (!importList.isEmpty() && existImportableTask()) {
					return true;
				}
			}
		}
		return false;
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
		if (!owner.onGround) {
			//空中だとパスの設定ができない？
			return;
		}

		if (opinv && ptask != null) {
			if (!importItemStack(ptask, false)) {
				opinv = false;
				ptask = null;
			}
		} else if (ptask != null) {
			TileEntity tileEntity = getTileEntity(ptask);
			if (tileEntity == null || !(tileEntity instanceof IInventory)) {//インベントリが撤去された
				ptask = null;
				return;
			}
			if (owner.getDistanceTilePos(tileEntity) < 1.5D) {//近づいた
				opinv = true;
				owner.setSwing(15, EnumSound.Null, false);
				owner.getNavigator().clearPathEntity();
				return;
			}
			if (owner.getNavigator().getPath() == null) {
				//パスを消失
				double x = (double)ptask.pos.getX() + 0.5d;
				double y = (double)ptask.pos.getY() + 0.5d;
				double z = (double)ptask.pos.getZ() + 0.5d;
				boolean canReach = owner.getNavigator().tryMoveToXYZ(x, y, z, 1.0F);
				if (!canReach) {//パス再設定に失敗した
					ptask = null;
					return;
				}
			}
		} else {
			for (MaidTaskItemIO mtio : importList) {
				if (importItemStack(mtio, true)) {
					double x = (double)mtio.pos.getX() + 0.5d;
					double y = (double)mtio.pos.getY() + 0.5d;
					double z = (double)mtio.pos.getZ() + 0.5d;
					boolean canReach = owner.getNavigator().tryMoveToXYZ(x, y, z, 1.0F);
					if (canReach || owner.getDistance(x, y, z) < 1.5D) {
						ptask = mtio;
						break;
					}
				}

			}
			if (ptask == null) {
				//全てのポイントに到達できないw
				cflag = false;
			}

		}
	}

	@Override
	public boolean isInterruptible() {
		return false;
	}

	@Override
	public void resetTask() {
		opinv = false;
		cflag = true;
		ptask = null;
	}

	private boolean existImportableTask() {
		for (MaidTaskItemIO mtio : importList) {
			if (importItemStack(mtio, true)) {
				return true;
			}
		}
		return false;
	}

	private boolean importItemStack(MaidTaskItemIO mtio, boolean sim) {
		IInventory inventory = getInventory(mtio);
		if (inventory == null) {
			return false;
		}
		for (int slot = 0; slot < owner.maidInventory.maxInventorySize; slot++) {
			if (importItemStackFromMaidInventorySlot(inventory, slot, mtio, sim)) {
				return true;
			}
		}
		return false;
	}

	public TileEntity getTileEntity(MaidTaskItemIO mtio) {
		if (mtio == null || mtio.pos == null) {
			return null;
		} else {
			TileEntity tileEntity = owner.worldObj.getTileEntity(mtio.pos);
			return tileEntity;
		}
	}

	public IInventory getInventory(MaidTaskItemIO mtio) {
		TileEntity tileEntity = getTileEntity(mtio);
		if (tileEntity instanceof IInventory) {
			return (IInventory) tileEntity;
		}
		return null;
	}

	private boolean importItemStackFromMaidInventorySlot(IInventory targetInventory, int slot, MaidTaskItemIO mtio, boolean sim) {
		boolean isWhitelist = mtio.isWhitelist;
		boolean matchNBT = !mtio.ignoreNBT;
		boolean isValidItem = mtio.isWhitelist ? false : true;
		ItemStack is = owner.maidInventory.getStackInSlot(slot);
		if (is == null || is.getItem() == AEMItems.workbook) {
			return false;
		}
		for (ItemStack filter : mtio.filter.values()) {
			if (filter != null && filter.isItemEqual(is)) {
				if (!matchNBT || (matchNBT & ItemStack.areItemStackTagsEqual(filter, is))) {
					// ホワイトリストなら一致したので搬出許可確定
					// ブラックリストなら一致したので搬出不許可確定
					isValidItem = isWhitelist;
					break;
				}
			}
		}
		if (isValidItem) {
			// インベントリにisを受け入れられるスペースがあれば移動する
			int left = InventoryUtil.insertIntoInventory(is.copy(), targetInventory, mtio.facing, sim);
			if (is.stackSize == left) {
				return false;
			}
			if (sim) {
				return is.stackSize != left;
			} else {
				if (left > 0) {
					is.stackSize = left;
				} else {
					owner.maidInventory.setInventorySlotContents(slot, null);
				}
				owner.maidInventory.markDirty();
			}
			return true;
		}
		return false;
	}

}
