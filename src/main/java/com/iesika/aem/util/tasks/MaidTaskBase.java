package com.iesika.aem.util.tasks;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;

public abstract class MaidTaskBase {

	public String taskName;
	public Vec3 pos;

	public MaidTaskBase(String taskName, Vec3 pos) {
		this.taskName = taskName;
		this.pos = pos;
	}

	public void writeBasicInfoToTag(NBTTagCompound tag){
		tag.setString("task", taskName);
		tag.setDouble("xCoord", pos.xCoord);
		tag.setDouble("yCoord", pos.yCoord);
		tag.setDouble("zCoord", pos.zCoord);
	}

	public void readBasicInfoFromTag(NBTTagCompound tag){
		this.taskName = tag.getString("task");
		this.pos = Vec3.createVectorHelper(tag.getDouble("xCoord"), tag.getDouble("yCoord"), tag.getDouble("zCoord"));
	}

	public abstract List<String> getToolTipInfo();

	public abstract NBTTagCompound makeTaskTag();

	public abstract MaidTaskBase readTaskFromTag(NBTTagCompound tag);

}
