package com.iesika.aem.util.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.iesika.aem.util.FacingUtil;
import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.Constants;

public class MaidTaskInOut extends MaidTaskBase {

	public boolean isImport;
	public boolean whitelist;
	public boolean matchNBT;
	public byte facing;
	public Map<Byte, ItemStack> filter;

	public MaidTaskInOut(Vec3 pos) {
		super("io", pos);
		this.isImport = false;
		this.whitelist = false;
		this.matchNBT = false;
		this.facing = 0;
		this.filter = new HashMap<Byte, ItemStack>();
	}

	public MaidTaskInOut(Vec3 pos, boolean isImport, boolean whitelist, boolean matchNBT, byte facing, Map<Byte, ItemStack> filter) {
		super("io", pos);
		this.isImport = isImport;
		this.whitelist = whitelist;
		this.matchNBT = matchNBT;
		this.facing = facing;
		this.filter = filter;
	}

	@Override
	public NBTTagCompound makeTaskTag() {
		NBTTagCompound tag = new NBTTagCompound();
		writeBasicInfoToTag(tag);
		tag.setBoolean("isImport", this.isImport);
		tag.setBoolean("whitelist", this.whitelist);
		tag.setBoolean("matchNBT", this.matchNBT);
		tag.setByte("facing", this.facing);

		NBTTagList itemdata = new NBTTagList();
		for (Map.Entry<Byte, ItemStack> bar : filter.entrySet()) {
			NBTTagCompound itemNBT = new NBTTagCompound();
			itemNBT.setByte("slot", bar.getKey());
			(bar.getValue()).writeToNBT(itemNBT);
			itemdata.appendTag(itemNBT);
		}
		tag.setTag("filter", itemdata);
		return tag;
	}

	@Override
	public MaidTaskBase readTaskFromTag(NBTTagCompound tag) {
		readBasicInfoFromTag(tag);
		this.isImport = tag.getBoolean("isImport");
		this.whitelist = tag.getBoolean("whitelist");
		this.matchNBT = tag.getBoolean("matchNBT");
		this.facing = tag.getByte("facing");
		this.filter.clear();
		NBTTagList itemdata = tag.getTagList("filter", Constants.NBT.TAG_COMPOUND);
		for (int j = 0; j < itemdata.tagCount(); j++) {
			NBTTagCompound tagCompound = itemdata.getCompoundTagAt(j);
			byte slot = tagCompound.getByte("slot");
			if (slot >= 0 && slot < 9) {// dummyinventoryのスロット数
				this.filter.put(slot, ItemStack.loadItemStackFromNBT(tagCompound));
			}
		}
		return this;
	}

	@Override
	public List<String> getToolTipInfo() {
		List<String> tlist = new ArrayList<String>();

		String im = StatCollector.translateToLocal("aem.text.import");
		String ex = StatCollector.translateToLocal("aem.text.export");
		String whl = StatCollector.translateToLocal("aem.text.whitelist");
		String bll = StatCollector.translateToLocal("aem.text.blacklist");
		String ign = StatCollector.translateToLocal("aem.text.ignoreNBT");
		String man = StatCollector.translateToLocal("aem.text.matchNBT");

		String io = isImport ? ChatFormatting.WHITE + im : ChatFormatting.DARK_GRAY + ex;
		String wb = whitelist ? ChatFormatting.WHITE + whl : ChatFormatting.DARK_GRAY + bll;
		String sNBT = matchNBT ? ChatFormatting.WHITE + man : ChatFormatting.DARK_GRAY + ign;
		String face = ChatFormatting.WHITE + StatCollector.translateToLocal(FacingUtil.getFacingString(facing));

		tlist.add(io + " " + pos + " " + wb + " " + sNBT + " " + face);
		if (filter.isEmpty()) {
			String sss = isImport ? "Import" : "Export";
			tlist.add(ChatFormatting.GRAY + sss +" all items");
		} else {
			for (Map.Entry<Byte, ItemStack> e : filter.entrySet()) {
				tlist.add(ChatFormatting.GRAY + e.getValue().getDisplayName());
			}
		}
		return tlist;
	}

}
