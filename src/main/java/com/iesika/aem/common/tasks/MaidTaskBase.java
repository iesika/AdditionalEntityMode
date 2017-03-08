package com.iesika.aem.common.tasks;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public abstract class MaidTaskBase {

	public String taskName;
	public BlockPos pos;

	public MaidTaskBase(String taskName, BlockPos pos) {
		this.taskName = taskName;
		this.pos = pos;
	}

	public NBTTagCompound writeTaskToTag(NBTTagCompound tag){
		tag.setString("task", taskName);
		tag.setInteger("posX", pos.getX());
		tag.setInteger("posY", pos.getY());
		tag.setInteger("posZ", pos.getZ());
		return tag;
	}

	public MaidTaskBase readTaskFromTag(NBTTagCompound tag){
		this.taskName = tag.getString("task");
		this.pos = new BlockPos(tag.getInteger("posX"), tag.getInteger("posY"), tag.getDouble("posZ"));
		return this;
	}

	public abstract List<String> getToolTipInfo();

}
