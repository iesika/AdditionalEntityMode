package com.iesika.aem.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.iesika.aem.common.AEMItems;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

//NBTから毎度読み出すのが面倒なのでjournalのデータを管理するクラス
public class JournalData {

	public class JDPoint {
		public Pos3 pos;
		public boolean isImport;
		public boolean isWhitelist;
		public boolean isIgnoreNBT;//変数名ガバ
		public byte facing;
		public Map<Byte, ItemStack> items;

		public JDPoint(Pos3 pos, boolean isImport, boolean isWhitelist, boolean isIgnoreNBT, byte facing, Map<Byte, ItemStack> items) {
			this.pos = pos;
			this.isImport = isImport;
			this.isWhitelist = isWhitelist;
			this.isIgnoreNBT = isIgnoreNBT;
			this.facing = facing;
			this.items = items;
		}
	}

	public List<JDPoint> data;
	public Map<Pos3, JDPoint> importMap;
	public Map<Pos3, JDPoint> exportMap;

	public JournalData(ItemStack itemStack) {
		this.data = new ArrayList<JDPoint>();
		this.importMap = new HashMap<Pos3, JDPoint>();
		this.exportMap = new HashMap<Pos3, JDPoint>();
		if (itemStack == null || itemStack.getItem() != AEMItems.journal || !itemStack.hasTagCompound() || !itemStack.getTagCompound().hasKey("io")) {
			return;
		}
		NBTTagList ioList = itemStack.getTagCompound().getTagList("io", Constants.NBT.TAG_COMPOUND);
		if (ioList == null) {
			return;
		}
		for (int i = 0; i < ioList.tagCount(); i++) {
			//Logger.info("jd :" + Integer.toString(i));
			NBTTagCompound nbtdata = ioList.getCompoundTagAt(i);
			Pos3 tpos3 = new Pos3(nbtdata.getInteger("PosX"), nbtdata.getInteger("PosY"), nbtdata.getInteger("PosZ"));
			boolean tisImport = nbtdata.getBoolean("isImport");
			boolean tisWhiteList = nbtdata.getBoolean("isWhitelist");
			boolean tisIgnoreNBT = nbtdata.getBoolean("isIgnoreNBT");
			byte tfacing = nbtdata.getByte("facing");
			NBTTagList titemdata = nbtdata.getTagList("itemdata", Constants.NBT.TAG_COMPOUND);
			Map<Byte, ItemStack> titems = new HashMap<Byte, ItemStack>();
			for (int j = 0; j < titemdata.tagCount(); j++) {
				NBTTagCompound tagCompound = titemdata.getCompoundTagAt(j);
				byte slot = tagCompound.getByte("Slot");
				if (slot >= 0 && slot < 9) {// dummyinventoryのスロット数
					titems.put(slot, ItemStack.loadItemStackFromNBT(tagCompound));
				}
			}
			data.add(new JDPoint(tpos3, tisImport, tisWhiteList, tisIgnoreNBT, tfacing, titems));
		}
	}

	// 指定された位置がwhiteListかどうか
	public boolean isWhitelist(Pos3 apos3) {
		for (JDPoint point : data) {
			if (point.pos.x == apos3.x && point.pos.y == apos3.y && point.pos.z == apos3.z) {
				return point.isWhitelist;
			}
		}
		return true;
	}

	public boolean setWhitelist(boolean b, Pos3 apos3){
		for (JDPoint point : data) {
			if (point.pos.x == apos3.x && point.pos.y == apos3.y && point.pos.z == apos3.z) {
				point.isWhitelist = b;
				return true;
			}
		}
		return false;
	}

	// 指定された位置がimportかどうか
	public boolean isImport(Pos3 apos3) {
		for (JDPoint point : data) {
			if (point.pos.x == apos3.x && point.pos.y == apos3.y && point.pos.z == apos3.z) {
				return point.isImport;
			}
		}
		return true;
	}

	public boolean setImport(boolean b, Pos3 apos3){
		for (JDPoint point : data) {
			if (point.pos.x == apos3.x && point.pos.y == apos3.y && point.pos.z == apos3.z) {
				point.isImport = b;
				return true;
			}
		}
		return false;
	}

	public boolean setIgnoreNBT(boolean b, Pos3 apos3) {
		for (JDPoint point : data) {
			if (point.pos.x == apos3.x && point.pos.y == apos3.y && point.pos.z == apos3.z) {
				point.isIgnoreNBT = b;
				return true;
			}
		}
		return false;
	}

	public boolean isIgnoreNBT(Pos3 apos3) {
		for (JDPoint point : data) {
			if (point.pos.x == apos3.x && point.pos.y == apos3.y && point.pos.z == apos3.z) {
				return point.isIgnoreNBT;
			}
		}
		return true;
	}

	public boolean setFacing(byte facing, Pos3 apos3) {
		for (JDPoint point : data) {
			if (point.pos.x == apos3.x && point.pos.y == apos3.y && point.pos.z == apos3.z) {
				point.facing = facing;
				Logger.info("set facing:" + point.facing);
				return true;
			}
		}
		return false;
	}

	public byte getFacing(Pos3 apos3){
		for (JDPoint point : data) {
			if (point.pos.x == apos3.x && point.pos.y == apos3.y && point.pos.z == apos3.z) {
				Logger.info("get facing:" + point.facing);
				return point.facing;
			}
		}
		return (byte)0;
	}



//	public List<JournalPointData> getImportPointList(){
//		//List<JournalPointData> = new ArrayList<>()
//	}

	// 指定された位置がimportかどうか
	public Map<Byte, ItemStack> getItemStackMap(Pos3 apos3) {
		for (JDPoint point : data) {
			if (point.pos.x == apos3.x && point.pos.y == apos3.y && point.pos.z == apos3.z) {
				return point.items;
			}
		}
		return new HashMap<Byte, ItemStack>();
	}

	//既に位置情報が登録されているか
	public boolean isExistAlready(Pos3 apos3){
		for (JDPoint point : data) {
			if (point.pos.x == apos3.x && point.pos.y == apos3.y && point.pos.z == apos3.z) {
				return true;
			}
		}
		return false;
	}

	public void setItemStackData(Pos3 apos3, ItemStack[] dummys) {
		Map<Byte, ItemStack> aitems = new HashMap<Byte, ItemStack>();
		for (byte i = 0; i < dummys.length; i++){
			if(dummys[i] != null){
				aitems.put(i, dummys[i]);
			}
		}
		for (JDPoint point : data) {
			if (point.pos.x == apos3.x && point.pos.y == apos3.y && point.pos.z == apos3.z) {
				point.items = aitems;
				return;
			}
		}
		//data.add(new JournalPointData(apos3, aisImport, aisWhitelist, aitems));
	}

	public void registerNewPoint(Pos3 apos3){
		for (JDPoint point : data) {
			//既に登録されている位置であれば登録しない
			if (point.pos.x == apos3.x && point.pos.y == apos3.y && point.pos.z == apos3.z) {
				return;
			}
		}
		data.add(new JDPoint(apos3, true, true, true, (byte) 0, new HashMap<Byte, ItemStack>()));
	}

	public void removeJournalPointData(Pos3 apos3){
		for (JDPoint point : data) {
			if (point.pos.x == apos3.x && point.pos.y == apos3.y && point.pos.z == apos3.z) {
				data.remove(point);
				return;
			}
		}
	}

	// 既存のNBTのioListにこのクラスのioListを(上書き)保存する
	// 成功でtrue;
	public boolean writeToNBT(ItemStack itemStack){
		if (itemStack.getItem() != AEMItems.journal){
			return false;
		}
		if (!itemStack.hasTagCompound()){
			itemStack.setTagCompound(new NBTTagCompound());
		}
		//ioに関しては上書きする
		itemStack.getTagCompound().setTag("io", new NBTTagList());
		NBTTagList ioList = itemStack.getTagCompound().getTagList("io", Constants.NBT.TAG_LIST);
		for (JDPoint point : data) {
			NBTTagCompound NBTJournalPointData = new NBTTagCompound();
			NBTJournalPointData.setInteger("PosX", point.pos.x);
			NBTJournalPointData.setInteger("PosY", point.pos.y);
			NBTJournalPointData.setInteger("PosZ", point.pos.z);
			NBTJournalPointData.setBoolean("isWhitelist", point.isWhitelist);
			NBTJournalPointData.setBoolean("isImport", point.isImport);
			NBTJournalPointData.setBoolean("isIgnoreNBT", point.isIgnoreNBT);
			NBTJournalPointData.setByte("facing", point.facing);
			NBTTagList itemdata = new NBTTagList();
			for (Map.Entry<Byte, ItemStack> bar : point.items.entrySet()) {
				NBTTagCompound itemNBT = new NBTTagCompound();
				itemNBT.setByte("Slot", bar.getKey());
//				Logger.info("YY" +(bar.getKey()).toString());
//				if(bar.getValue() == null) {
//					Logger.info("WHY" +(bar.getKey()).toString());
//					continue;
//				}
				Logger.info("keydata:" + (bar.getKey()).toString());
				Logger.info("valdata:" + (bar.getValue()).toString());
				(bar.getValue()).writeToNBT(itemNBT);
				itemdata.appendTag(itemNBT);
			}
			NBTJournalPointData.setTag("itemdata", itemdata);
			ioList.appendTag(NBTJournalPointData);
		}
		return true;
	}

//	//dataが確定したらMapにする
//	public void makeIOMap(){
//		importMap = new HashMap<Pos3, JDPoint>();
//		exportMap = new HashMap<Pos3, JDPoint>();
//		for (JDPoint jpd : data){
//			if (jpd.isImport){
//				importMap.put(jpd.pos, jpd);
//			}else{
//				exportMap.put(jpd.pos, jpd);
//			}
//		}
//	}

}
