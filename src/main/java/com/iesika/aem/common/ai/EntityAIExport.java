package com.iesika.aem.common.ai;

import java.util.List;

import com.iesika.aem.common.config.AEMConfig;
import com.iesika.aem.mode.AEM_EntityMode_Carrier;
import com.iesika.aem.util.InventoryUtil;
import com.iesika.aem.util.tasks.MaidTaskInOut;

import littleMaidMobX.LMM_EntityLittleMaid;
import littleMaidMobX.LMM_EntityModeBase;
import littleMaidMobX.LMM_EnumSound;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class EntityAIExport extends EntityAIBase {

	private LMM_EntityLittleMaid owner;
	protected AEM_EntityMode_Carrier cmode;
	protected List<MaidTaskInOut> exporList;
	protected MaidTaskInOut ptask;// 処理中のタスク

	private boolean opinv;
	private boolean cflag;

	public EntityAIExport(LMM_EntityLittleMaid owner) {
		this.owner = owner;
		this.opinv = false;
		this.cflag = true;
		owner.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(AEMConfig.carrierModeSearchRange);
		setMutexBits(7);
	}

	@Override
	public boolean shouldExecute() {
		if (owner.isMaidWait()) {
			return false;
		}
		LMM_EntityModeBase fmodeBase = owner.getActiveModeClass();
		if (fmodeBase instanceof AEM_EntityMode_Carrier) {
			cmode = (AEM_EntityMode_Carrier) fmodeBase;
			if (cmode.mtm != null) {
				exporList = cmode.mtm.getIOList(false);
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
		if (!owner.onGround){
			//空中だとパスの設定ができない？
			return;
		}

		if (opinv && ptask != null) {
			if (!exportItemStack(ptask, false)){
				opinv = false;
				ptask = null;
			}
		} else if (ptask != null) {
			TileEntity tileEntity = getTileEntity(ptask);
			if (tileEntity == null || !(tileEntity instanceof IInventory)){//インベントリが撤去された
				ptask = null;
				return;
			}
			if (owner.getDistanceTilePos(tileEntity) < 1.5D){//近づいた
				opinv = true;
				owner.setSwing(15, LMM_EnumSound.Null);
				owner.getNavigator().clearPathEntity();
				return;
			}
			if (owner.getNavigator().getPath() == null){
				//パスを消失
				boolean canReach = owner.getNavigator().tryMoveToXYZ(ptask.pos.xCoord, ptask.pos.yCoord, ptask.pos.zCoord, 1.0F);
				if (!canReach){//パス再設定に失敗した
					ptask = null;
					return;
				}
			}
		} else {
			for (MaidTaskInOut mtio : exporList) {
				if (exportItemStack(mtio, true)) {
					boolean canReach = owner.getNavigator().tryMoveToXYZ(mtio.pos.xCoord, mtio.pos.yCoord, mtio.pos.zCoord, 1.0F);
					if (canReach || owner.getDistance(mtio.pos.xCoord, mtio.pos.yCoord, mtio.pos.zCoord) < 1.5D){
						ptask = mtio;
						break;
					}
				}

			}
			if (ptask == null){
				//全てのポイントに到達できない
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

	private boolean existExportableTask() {
		for (MaidTaskInOut mtio : exporList) {
			if (exportItemStack(mtio, true)) {
				return true;
			}
		}
		return false;
	}

	private boolean exportItemStack(MaidTaskInOut mtio, boolean sim) {
		IInventory inventory = getInventory(mtio);
		if (inventory == null) {
			return false;
		}
		if (inventory instanceof ISidedInventory) {
			ISidedInventory sided = (ISidedInventory) inventory;
			int[] accessible = sided.getAccessibleSlotsFromSide(mtio.facing);
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

	public TileEntity getTileEntity(MaidTaskInOut mtio){
		if (mtio == null || mtio.pos == null) {
			return null;
		} else {
			int x = (int) (mtio.pos.xCoord - 0.5D);
			int y = (int) (mtio.pos.yCoord - 0.5D);
			int z = (int) (mtio.pos.zCoord - 0.5D);
			TileEntity tileEntity = owner.worldObj.getTileEntity(x, y, z);
			return tileEntity;
		}
	}

	public IInventory getInventory(MaidTaskInOut mtio) {
		TileEntity tileEntity =  getTileEntity(mtio);
		if (tileEntity instanceof IInventory) {
			return (IInventory) tileEntity;
		}
		return null;
	}

	private boolean exportItemStackFromTargetInventorySlot(IInventory targetInventory, int slot, MaidTaskInOut mtio, boolean sim) {
		boolean isWhitelist = mtio.whitelist;
		boolean matchNBT = mtio.matchNBT;
		boolean isValidItem = mtio.whitelist ? false : true;
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
			if (is.stackSize == left){//移動できない場合
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

