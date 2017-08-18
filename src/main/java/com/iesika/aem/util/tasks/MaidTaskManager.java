package com.iesika.aem.util.tasks;

import java.util.ArrayList;
import java.util.List;

import com.iesika.aem.common.AEMItems;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.Constants;

public class MaidTaskManager {

	public List<MaidTaskBase> tasks;

	//NBTから読み出す
	public MaidTaskManager(ItemStack stack){
		this.tasks = new ArrayList<MaidTaskBase>();
		if (stack == null || !stack.hasTagCompound() || !stack.getTagCompound().hasKey("maidtasks")){
			return;
		}
		NBTTagList taskList = stack.getTagCompound().getTagList("maidtasks", Constants.NBT.TAG_COMPOUND);
		if(taskList == null){
			return;
		}
		for (int i = 0; i < taskList.tagCount(); i++) {
			NBTTagCompound nbt = taskList.getCompoundTagAt(i);
			MaidTaskBase task = null;
			String taskName = nbt.getString("task");
			Vec3 pos = Vec3.createVectorHelper(nbt.getDouble("xCoord"), nbt.getDouble("yCoord"), nbt.getDouble("zCoord"));

			if (taskName.equals("io")){
				task = new MaidTaskInOut(pos);
			}
			if (task != null){
				task.readTaskFromTag(nbt);
				tasks.add(task);
			}
		}
	}

	public void writeToNBT(ItemStack itemStack){
		if (itemStack.getItem() != AEMItems.journal && itemStack.getItem() != AEMItems.memo){
			return;
		}
		if (!itemStack.hasTagCompound()){
			itemStack.setTagCompound(new NBTTagCompound());
		}
		itemStack.getTagCompound().setTag("maidtasks", new NBTTagList());
		NBTTagList taskList = itemStack.getTagCompound().getTagList("maidtasks", Constants.NBT.TAG_LIST);
		for (MaidTaskBase task : tasks){
			taskList.appendTag(task.makeTaskTag());
		}
	}

	//新しいタスクを登録する
	//同じ位置に別のタスクが登録されている場合塗替える
	//同じタスクが登録されている場合塗り替えない
	public void shouldRegisterNewTask(Vec3 pos, MaidTaskBase mtb){
		MaidTaskBase nmtb = getRegisteredTaskFromVec3(pos);
		if (nmtb == null){
			tasks.add(mtb);
		}else if (mtb.getClass() != nmtb.getClass()){
			tasks.remove(nmtb);
			tasks.add(mtb);
		}
	}

	public MaidTaskBase getRegisteredTaskFromVec3(Vec3 pos){
		for(MaidTaskBase task : tasks){
			if (task.pos.xCoord == pos.xCoord && task.pos.yCoord == pos.yCoord && task.pos.zCoord == pos.zCoord){
				return task;
			}
		}
		return null;
	}

	//isImportがtrueならImport地点のリストを返す
	public List<MaidTaskInOut> getIOList(boolean isImport){
		List<MaidTaskInOut> iotasks = new ArrayList<MaidTaskInOut>();
		for(MaidTaskBase task : tasks){
			if (task instanceof MaidTaskInOut){
				MaidTaskInOut mtio = (MaidTaskInOut)task;
				if (mtio.isImport == isImport){
					iotasks.add((MaidTaskInOut) task);
				}
			}
		}
		return iotasks;
	}

}
