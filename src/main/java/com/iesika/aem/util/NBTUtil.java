package com.iesika.aem.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

public class NBTUtil {
	/*
	 nbt---io(NBTList)---data---isInput
	 		           |      |-isWhiteList
	 		           |      |-PosX
	 		           |      |-PosY
	 		           |      |-PosZ
	 		           |       -Data---
	 		           |-data

	 */
	//アイテムjournal内にある(x, y, z)座標のNBTCompoundを入手する存在しない場合null
	public static NBTTagCompound getJournalNBT(ItemStack journal,int x, int y, int z){
		if (!existIOTag(journal)){
			return null;
		}
		NBTTagList ioList = journal.getTagCompound().getTagList("io", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < ioList.tagCount(); i++){
			NBTTagCompound data = ioList.getCompoundTagAt(i);
			int PosX = data.getInteger("PosX");
			int PosY = data.getInteger("PosY");
			int PosZ = data.getInteger("PosZ");
			//data.getTagList("data", Constants.NBT.TAG_COMPOUND);
			if ( x == PosX && y == PosY && z == PosZ){
				return data;
			}
		}
		return null;
	}

	public static int getJournalNBTindex(ItemStack journal,int x, int y, int z){
		if (!existIOTag(journal)){
			return -1;
		}
		NBTTagList ioList = journal.getTagCompound().getTagList("io", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < ioList.tagCount(); i++){
			NBTTagCompound data = ioList.getCompoundTagAt(i);
			int PosX = data.getInteger("PosX");
			int PosY = data.getInteger("PosY");
			int PosZ = data.getInteger("PosZ");
			//data.getTagList("data", Constants.NBT.TAG_COMPOUND);
			if ( x == PosX && y == PosY && z == PosZ){
				return  i;
			}
		}
		return -1;
	}

	private static boolean existIOTag(ItemStack journal){
		if (!journal.hasTagCompound()){
			return false;
		}
		NBTTagCompound nbt = journal.getTagCompound();
		if (!nbt.hasKey("io")){
			return false;
		}
		return true;
	}
}
