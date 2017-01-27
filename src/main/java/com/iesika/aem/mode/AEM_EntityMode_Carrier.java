package com.iesika.aem.mode;

import com.iesika.aem.common.AEMItems;
import com.iesika.aem.common.ai.EntityAICarrier;
import com.iesika.aem.util.ItemUtil;
import com.iesika.aem.util.JournalData;
import com.iesika.aem.util.Logger;

import littleMaidMobX.LMM_EntityLittleMaid;
import littleMaidMobX.LMM_EntityModeBase;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class AEM_EntityMode_Carrier extends LMM_EntityModeBase {
	public final int mmode_Carrier = 0x4201;
	public final String CarrierModeName = "Carrier";

	public LMM_EntityLittleMaid owner;
	public JournalData journalData;

	static {
		Logger.info("initializing AEM_EntityMode_Carrier ");
	}

	public AEM_EntityMode_Carrier(LMM_EntityLittleMaid owner) {
		super(owner);
		this.owner = owner;
		Logger.info("Carrier instance");
	}

	@Override
	public int priority() {
		return 5998;
	}

	@Override
	public boolean changeMode(EntityPlayer pentityplayer) {
		ItemStack litemstack = owner.maidInventory.getStackInSlot(0);
		if (litemstack != null) {
			if (litemstack.getItem() == AEMItems.journal && litemstack.hasTagCompound()) {
				owner.setMaidMode(CarrierModeName);
				return true;
			}
		}
		return false;
	}

	@Override
	public void addEntityMode(EntityAITasks pDefaultMove, EntityAITasks pDefaultTargeting) {
		EntityAITasks[] ltasks = new EntityAITasks[2];
		//ltasks[0] = pDefaultMove;
		ltasks[1] = pDefaultTargeting;
		ltasks[0] = new EntityAITasks(owner.aiProfiler);
		ltasks[0].addTask(1, new EntityAISit(owner));
		ltasks[0].addTask(2, new EntityAICarrier(owner));
		ltasks[0].addTask(32, owner.aiWander);
		ltasks[0].addTask(40, owner.aiCloseDoor);
		ltasks[0].addTask(41, owner.aiOpenDoor);
		owner.addMaidMode(ltasks, CarrierModeName, mmode_Carrier);
	}

	@Override
	public boolean setMode(int pMode) {
		switch (pMode) {
		case mmode_Carrier:
			owner.setBloodsuck(false);
			if (ItemUtil.isValidItemStackForCarrierMode(owner.maidInventory.getStackInSlot(0))) {
				journalData = new JournalData(owner.maidInventory.getStackInSlot(0));
				if (journalData == null){
					Logger.info("WGTRGTRG");
				}
				Logger.info("SET CARrier");
				return true;

			}
			return false;
		}
		return false;
	}

	@Override
	public boolean checkItemStack(ItemStack pItemStack) {
		// TODO 自動生成されたメソッド・スタブ
		return super.checkItemStack(pItemStack);
	}



}
