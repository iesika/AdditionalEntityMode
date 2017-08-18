package com.iesika.aem.mode;

import com.iesika.aem.common.AEMItems;
import com.iesika.aem.common.ai.EntityAIExport;
import com.iesika.aem.common.ai.EntityAIImport;
import com.iesika.aem.common.config.AEMConfig;
import com.iesika.aem.util.Logger;
import com.iesika.aem.util.tasks.MaidTaskManager;

import littleMaidMobX.LMM_EntityLittleMaid;
import littleMaidMobX.LMM_EntityModeBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class AEM_EntityMode_Carrier extends LMM_EntityModeBase {
	public final int mmode_Carrier = 0x4201;
	public final String CarrierModeName = "Carrier";

	public LMM_EntityLittleMaid owner;
	public MaidTaskManager mtm;

	static {
		Logger.info("initializing AEM_EntityMode_Carrier ");
	}

	public AEM_EntityMode_Carrier(LMM_EntityLittleMaid owner) {
		super(owner);
		this.owner = owner;
	}

	@Override
	public int priority() {
		return 5998;
	}

	@Override
	public boolean changeMode(EntityPlayer pentityplayer) {
		if (shouldChangeToCarrierMode()){
			owner.setMaidMode(CarrierModeName);
			return true;
		}
		return false;
	}

	@Override
	public void addEntityMode(EntityAITasks pDefaultMove, EntityAITasks pDefaultTargeting) {
		if (AEMConfig.enableCarrierMode) {
			EntityAITasks[] ltasks = new EntityAITasks[2];
			// ltasks[0] = pDefaultMove;
			ltasks[1] = pDefaultTargeting;
			ltasks[0] = new EntityAITasks(owner.aiProfiler);
			ltasks[0].addTask(1, new EntityAISit(owner));
			ltasks[0].addTask(2, new EntityAIImport(owner));
			ltasks[0].addTask(3, new EntityAIExport(owner));
			// ltasks[0].addTask(2, new EntityAICarrier(owner));
			ltasks[0].addTask(32, owner.aiWander);
			ltasks[0].addTask(40, owner.aiCloseDoor);
			ltasks[0].addTask(41, owner.aiOpenDoor);
			//首の動き
			ltasks[0].addTask(51, new EntityAIWatchClosest(owner, EntityLivingBase.class, 10F));
			ltasks[0].addTask(52, new EntityAILookIdle(owner));
			owner.addMaidMode(ltasks, CarrierModeName, mmode_Carrier);
		}
	}

	@Override
	public boolean setMode(int pMode) {
		switch (pMode) {
		case mmode_Carrier:
			if (shouldChangeToCarrierMode()){
				owner.setBloodsuck(false);
				return true;
			}
			return false;
		}
		return false;
	}

	// メイドがCarrierModeになるべきアイテムを所持しているか
	private boolean shouldChangeToCarrierMode() {
		ItemStack stack = owner.maidInventory.getStackInSlot(0);
		if (stack != null) {
			if (stack.getItem() == AEMItems.memo && stack.hasTagCompound()) {
				mtm = new MaidTaskManager(stack);
				if (mtm.tasks.isEmpty()) {
					return false;
				} else {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean checkItemStack(ItemStack pItemStack) {
		// TODO 自動生成されたメソッド・スタブ
		return super.checkItemStack(pItemStack);
	}

}
