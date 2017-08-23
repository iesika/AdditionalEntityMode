package com.iesika.aem.common.tasks;

import java.util.ArrayList;
import java.util.List;

import com.iesika.aem.common.AEMItems;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
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
			if (taskName.equals("itemIO")){
				task = new MaidTaskItemIO(new BlockPos(nbt.getInteger("posX"), nbt.getInteger("posY"), nbt.getDouble("posZ")));
			}
			if (task != null){
				task.readTaskFromTag(nbt);
				tasks.add(task);
			}
		}
	}

	//引数のworkboookのNBTに書き込む
	public void writeToNBT(ItemStack itemStack){
		if (itemStack.getItem() != AEMItems.workbook){
			return;
		}
		if (!itemStack.hasTagCompound()){
			itemStack.setTagCompound(new NBTTagCompound());
		}
		itemStack.getTagCompound().setTag("maidtasks", new NBTTagList());
		NBTTagList taskList = itemStack.getTagCompound().getTagList("maidtasks", Constants.NBT.TAG_LIST);
		for (MaidTaskBase task : tasks){
			taskList.appendTag(task.writeTaskToTag(new NBTTagCompound()));
		}
	}

	//新しいタスクを登録する
	//同じ位置に別のタスクが登録されている場合塗替える
	//同じタスクが登録されている場合塗り替えない
	public void shouldRegisterNewTask(BlockPos pos, MaidTaskBase mtb){
		MaidTaskBase nmtb = getRegisteredTaskFromBlockPos(pos);
		if (nmtb == null){
			tasks.add(mtb);
		}else if (mtb.getClass() != nmtb.getClass()){
			tasks.remove(nmtb);
			tasks.add(mtb);
		}
	}

	public MaidTaskBase getRegisteredTaskFromBlockPos(BlockPos pos){
		for(MaidTaskBase task : tasks){
			if (task.pos.equals(pos)){
				return task;
			}
		}
		return null;
	}

	//isImportがtrueならImport地点のリストを返す
	public List<MaidTaskItemIO> getIOList(boolean isImport){
		List<MaidTaskItemIO> iotasks = new ArrayList<MaidTaskItemIO>();
		for(MaidTaskBase task : tasks){
			if (task instanceof MaidTaskItemIO){
				MaidTaskItemIO mtio = (MaidTaskItemIO)task;
				if (mtio.isImport == isImport){
					iotasks.add((MaidTaskItemIO) task);
				}
			}
		}
		return iotasks;
	}

	public int getNumberOfItemIOtask(){
		int count = 0;
		for (MaidTaskBase mtb : tasks){
			if (mtb instanceof MaidTaskItemIO){
				count++;
			}
		}
		return count;
	}
}
