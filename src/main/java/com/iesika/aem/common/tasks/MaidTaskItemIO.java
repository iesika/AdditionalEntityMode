package com.iesika.aem.common.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.iesika.aem.util.FacingUtil;
import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;

public class MaidTaskItemIO extends MaidTaskBase {

	public boolean isImport;
	public boolean isWhitelist;
	public boolean ignoreNBT;
	public EnumFacing facing;
	public Map<Byte, ItemStack> filter;

	public MaidTaskItemIO(BlockPos pos) {
		super("itemIO", pos);
		this.isImport = false;
		this.isWhitelist = false;
		this.ignoreNBT = false;
		this.facing = EnumFacing.NORTH;
		this.filter = new HashMap<Byte, ItemStack>();
	}

	public MaidTaskItemIO(BlockPos pos, boolean isImport, boolean whitelist, boolean matchNBT, EnumFacing facing, Map<Byte, ItemStack> filter) {
		super("itemIO", pos);
		this.isImport = isImport;
		this.isWhitelist = whitelist;
		this.ignoreNBT = matchNBT;
		this.facing = facing;
		this.filter = filter;
	}

	public NBTTagCompound writeTaskToTag(NBTTagCompound tag) {
		super.writeTaskToTag(tag);
		tag.setBoolean("isImport", this.isImport);
		tag.setBoolean("whitelist", this.isWhitelist);
		tag.setBoolean("ignoreNBT", this.ignoreNBT);
		tag.setInteger("facing", this.facing.ordinal());

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
		super.readTaskFromTag(tag);
		this.isImport = tag.getBoolean("isImport");
		this.isWhitelist = tag.getBoolean("whitelist");
		this.ignoreNBT = tag.getBoolean("ignoreNBT");
		this.facing = EnumFacing.values()[tag.getInteger("facing")];
		this.filter.clear();
		NBTTagList itemdata = tag.getTagList("filter", Constants.NBT.TAG_COMPOUND);
		for (int j = 0; j < itemdata.tagCount(); j++) {
			NBTTagCompound tagCompound = itemdata.getCompoundTagAt(j);
			byte slot = tagCompound.getByte("slot");
			if (slot >= 0 && slot < 9) {
				this.filter.put(slot, ItemStack.loadItemStackFromNBT(tagCompound));
			}
		}
		return this;
	}

	@Override
	public List<String> getToolTipInfo() {
		List<String> tlist = new ArrayList<String>();

		String im = I18n.format("aem.text.import");
		String ex =I18n.format("aem.text.export");
		String whl = I18n.format("aem.text.whitelist");
		String bll = I18n.format("aem.text.blacklist");
		String ign = I18n.format("aem.text.ignoreNBT");
		String man = I18n.format("aem.text.matchNBT");

		String io = isImport ? ChatFormatting.WHITE + im : ChatFormatting.DARK_GRAY + ex;
		String wb = isWhitelist ? ChatFormatting.WHITE + whl : ChatFormatting.DARK_GRAY + bll;
		String sNBT = ignoreNBT ? ChatFormatting.WHITE + man : ChatFormatting.DARK_GRAY + ign;
		String face = ChatFormatting.WHITE +I18n.format(FacingUtil.getFacingString(facing));

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
