package com.iesika.aem.common.ai;

import java.util.List;

import com.iesika.aem.common.tasks.MaidTaskItemIO;
import com.iesika.aem.mode.EntityModeItemCarrier;
import com.iesika.aem.util.InventoryUtil;

import net.blacklab.lmr.entity.littlemaid.EntityLittleMaid;
import net.blacklab.lmr.entity.littlemaid.mode.EntityModeBase;
import net.blacklab.lmr.util.EnumSound;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

//インベントリ側からメイド側にアイテムを移すAI
public class EntityAIExport extends EntityAIBase {

	private EntityLittleMaid owner;
	protected EntityModeItemCarrier cmode;
	protected List<MaidTaskItemIO> exporList;
	protected MaidTaskItemIO ptask;// 処理中のタスク

	private boolean opinv;
	private boolean cflag;

	public EntityAIExport(EntityLittleMaid owner) {
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
				exporList = cmode.maidTaskManager.getIOList(false);
				if (!exporList.isEmpty() && existExportableTask()) {
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
			if (!exportItemStack(ptask, false)) {
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
				double x = (double) ptask.pos.getX() + 0.5d;
				double y = (double) ptask.pos.getY() + 0.5d;
				double z = (double) ptask.pos.getZ() + 0.5d;
				boolean canReach = owner.getNavigator().tryMoveToXYZ(z, y, z, 1.0F);
				if (!canReach) {//パス再設定に失敗した
					ptask = null;
					return;
				}
			}
			owner.getNavigator().updatePath();
		} else {
			for (MaidTaskItemIO mtio : exporList) {
				if (exportItemStack(mtio, true)) {
					double x = (double) mtio.pos.getX() + 0.5d;
					double y = (double) mtio.pos.getY() + 0.5d;
					double z = (double) mtio.pos.getZ() + 0.5d;
					boolean canReach = owner.getNavigator().tryMoveToXYZ(x, y, z, 1.0F);
					owner.getLookHelper().setLookPosition(x, y, z, 10F, owner.getVerticalFaceSpeed());
					if (canReach || owner.getDistance(x, y, z) < 1.5D) {
						ptask = mtio;
						break;
					}
				}
			}
			if (ptask == null) {
				//全てのポイントに到達できない
				cflag = false;
			}
		}
	}

	@Override
	public boolean isInterruptible() {
		return true;
	}

	@Override
	public void resetTask() {
		opinv = false;
		cflag = true;
		ptask = null;
	}

	private boolean existExportableTask() {
		for (MaidTaskItemIO mtio : exporList) {
			if (exportItemStack(mtio, true)) {
				return true;
			}
		}
		return false;
	}

	private boolean exportItemStack(MaidTaskItemIO mtio, boolean sim) {
		IInventory inventory = getInventory(mtio);
		if (inventory == null) {
			return false;
		}
		if (inventory instanceof ISidedInventory) {
			ISidedInventory sided = (ISidedInventory) inventory;
			int[] accessible = sided.getSlotsForFace(mtio.facing);
			for (int slot : accessible) {
				if (exportItemStackFromTargetInventorySlot(inventory, slot, mtio, sim)) {
					return true;
				}
			}
		} else {
			for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
				if (exportItemStackFromTargetInventorySlot(inventory, slot, mtio, sim)) {
					return true;
				}
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

	private boolean exportItemStackFromTargetInventorySlot(IInventory targetInventory, int slot, MaidTaskItemIO mtio, boolean sim) {
		boolean isWhitelist = mtio.isWhitelist;
		boolean matchNBT = !mtio.ignoreNBT;
		boolean isValidItem = mtio.isWhitelist ? false : true;
		ItemStack is = targetInventory.getStackInSlot(slot);
		if (is == null) {
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
			// メイドインベントリにisを受け入れられるスペースがあれば移動する
			//Logger.info("Movable Item:" + is.toString() + " sim:" + Boolean.toString(sim));
			int left = InventoryUtil.insertIntoMaidInventory(is.copy(), owner.maidInventory, sim);
			//Logger.info("left:" + Integer.toString(left));
			if (is.stackSize == left) {//移動できない場合
				return false;
			}
			if (sim) {
				return is.stackSize != left;
			} else {
				if (left > 0) {
					is.stackSize = left;
				} else {
					targetInventory.setInventorySlotContents(slot, null);
				}
				targetInventory.markDirty();
				return true;
			}
		}
		return false;
	}

}
