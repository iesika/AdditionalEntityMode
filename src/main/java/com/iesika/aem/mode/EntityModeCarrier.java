package com.iesika.aem.mode;

import com.iesika.aem.common.AEMItems;
import com.iesika.aem.common.ai.EntityAIExport;
import com.iesika.aem.common.ai.EntityAIImport;
import com.iesika.aem.common.tasks.MaidTaskManager;
import com.iesika.aem.util.Logger;

import net.blacklab.lmr.entity.EntityLittleMaid;
import net.blacklab.lmr.entity.mode.EntityModeBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class EntityModeCarrier extends EntityModeBase {

	public static final int mmode_Carrier = 0x4201;
	public static final String CarrierModeName = "Carrier";

	public MaidTaskManager maidTaskManager;

	static {
		Logger.info("EntityModeCarrier Init");
	}

	public EntityModeCarrier(EntityLittleMaid maid) {
		super(maid);
	}

	@Override
	public int priority() {
		return 5998;
	}

	@Override
	public boolean changeMode(EntityPlayer pentityplayer) {
		ItemStack mainHand = owner.maidInventory.mainHandInventory[0];
		if (mainHand != null){
			if (shouldChangeToCarrierMode(mainHand)){
				owner.setMaidMode(CarrierModeName);
				Logger.info("maid mode -> Carrier (use main hand)");
				return true;
			}
		}
		return false;
	}

	@Override
	public void addEntityMode(EntityAITasks pDefaultMove, EntityAITasks pDefaultTargeting) {
		EntityAITasks[] ltasks = new EntityAITasks[2];
		// ltasks[0] = pDefaultMove;
		ltasks[1] = pDefaultTargeting;
		ltasks[0] = new EntityAITasks(owner.aiProfiler);
		ltasks[0].addTask(1, owner.aiSwiming);
		ltasks[0].addTask(2, owner.getAISit());
		ltasks[0].addTask(3, new EntityAIImport(owner));
		ltasks[0].addTask(4, new EntityAIExport(owner));
		ltasks[0].addTask(32, owner.aiWander);
		ltasks[0].addTask(40, owner.aiCloseDoor);
		ltasks[0].addTask(41, owner.aiOpenDoor);
		//首の動き
		ltasks[0].addTask(51, new EntityAIWatchClosest(owner, EntityLivingBase.class, 10F));
		ltasks[0].addTask(52, new EntityAILookIdle(owner));
		owner.addMaidMode(ltasks, CarrierModeName, mmode_Carrier);
	}

	@Override
	public boolean setMode(int pMode) {
		switch (pMode) {
		case mmode_Carrier:
			owner.setBloodsuck(false);
			return shouldChangeToCarrierMode(owner.maidInventory.mainHandInventory[0]);
		}
		return false;
	}

	// メイドがCarrierModeになるべきアイテムを所持しているか
	private boolean shouldChangeToCarrierMode(ItemStack stack) {
		if (stack != null) {
			if (stack.getItem() == AEMItems.workbook && stack.hasTagCompound()) {
				maidTaskManager = new MaidTaskManager(stack);
				return !maidTaskManager.tasks.isEmpty();
			}
		}
		return false;
	}

}
