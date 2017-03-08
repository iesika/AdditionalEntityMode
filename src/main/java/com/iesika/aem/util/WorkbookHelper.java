package com.iesika.aem.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.iesika.aem.common.AEMItems;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;

public class WorkbookHelper {

	public List<Node> nodeList;
	public Map<BlockPos, Node> importMap;
	public Map<BlockPos, Node> exportMap;

	public class Node {
		public BlockPos pos;
		public boolean isImport;
		public boolean isWhitelist;
		public boolean ignoreNBT;
		public EnumFacing facing;
		public Map<Byte, ItemStack> items;

		public Node(BlockPos pos, boolean isImport, boolean isWhitelist, boolean ignoreNBT, EnumFacing facing, Map<Byte, ItemStack> items) {
			this.pos = pos;
			this.isImport = isImport;
			this.isWhitelist = isWhitelist;
			this.ignoreNBT = ignoreNBT;
			this.facing = facing;
			this.items = items;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Node)) {
				return false;
			}
			Node node = (Node) obj;
			if (!node.pos.equals(pos) || node.isWhitelist != isWhitelist || node.isImport != isImport || node.ignoreNBT != ignoreNBT || node.facing != facing || !node.items.equals(items)) {
				return false;
			}
			return true;
		}
	}

	public WorkbookHelper(ItemStack itemStack) {
		this.nodeList = new ArrayList<Node>();
		this.importMap = new HashMap<BlockPos, Node>();
		this.exportMap = new HashMap<BlockPos, Node>();

		if (itemStack == null || itemStack.getItem() != AEMItems.workbook || !itemStack.hasTagCompound() || !itemStack.getTagCompound().hasKey("itemIO")) {
			return;
		}
		NBTTagList itemIOList = itemStack.getTagCompound().getTagList("itemIO", Constants.NBT.TAG_COMPOUND);
		if (itemIOList == null) {
			return;
		}
		for (int i = 0; i < itemIOList.tagCount(); i++) {
			NBTTagCompound itemIONode = itemIOList.getCompoundTagAt(i);
			BlockPos pos = new BlockPos(itemIONode.getInteger("posX"), itemIONode.getInteger("posY"), itemIONode.getInteger("posZ"));
			boolean isImport = itemIONode.getBoolean("isImport");
			boolean isWhiteList = itemIONode.getBoolean("isWhitelist");
			boolean ignoreNBT = itemIONode.getBoolean("ignoreNBT");
			EnumFacing facing = EnumFacing.values()[itemIONode.getInteger("facing")];
			NBTTagList itemdata = itemIONode.getTagList("itemdata", Constants.NBT.TAG_COMPOUND);
			Map<Byte, ItemStack> items = new HashMap<Byte, ItemStack>();
			for (int j = 0; j < itemdata.tagCount(); j++) {
				NBTTagCompound tagCompound = itemdata.getCompoundTagAt(j);
				byte slot = tagCompound.getByte("slot");
				if (slot >= 0 && slot < 9) {
					items.put(slot, ItemStack.loadItemStackFromNBT(tagCompound));
				}
			}
			nodeList.add(new Node(pos, isImport, isWhiteList, ignoreNBT, facing, items));
		}
	}


	// 指定された位置がimportかどうか
	public boolean isImport(BlockPos pos) {
		for (Node node : nodeList) {
			if (node.pos.equals(pos)) {
				return node.isImport;
			}
		}
		return true;
	}

	public boolean setImport(boolean b, BlockPos pos) {
		for (Node node : nodeList) {
			if (node.pos.equals(pos)) {
				node.isImport = b;
				return true;
			}
		}
		return false;
	}

	// 指定された位置がwhiteListかどうか
	public boolean isWhitelist(BlockPos pos) {
		Logger.info("Search:::   " + pos.getX() + ":" + pos.getY() + ":" + pos.getZ());
		for (Node node : nodeList) {
			Logger.info(node.pos.getX() + ":" + node.pos.getY() + ":" + node.pos.getZ() + " iswhitelist" + node.isWhitelist);
			if (node.pos.equals(pos)) {
				return node.isWhitelist;
			}

		}
		Logger.info("not match pos");
		return true;
	}

	public boolean setWhitelist(boolean b, BlockPos pos) {
		for (Node node : nodeList) {
			if (node.pos.equals(pos)) {
				node.isWhitelist = b;
				return true;
			}
		}
		return true;
	}

	public boolean isIgnoreNBT(BlockPos pos) {
		for (Node node : nodeList) {
			if (node.pos.equals(pos)) {
				return node.ignoreNBT;
			}
		}
		return true;
	}

	public boolean setIgnoreNBT(boolean b, BlockPos pos) {
		for (Node node : nodeList) {
			if (node.pos.equals(pos)) {
				node.ignoreNBT = b;
				return true;
			}
		}
		return false;
	}

	public boolean setFacing(EnumFacing facing, BlockPos pos) {
		for (Node node : nodeList) {
			if (node.pos.equals(pos)) {
				node.facing = facing;
				return true;
			}
		}
		return false;
	}

	public EnumFacing getFacing(BlockPos pos) {
		for (Node node : nodeList) {
			if (node.pos.equals(pos)) {
				return node.facing;
			}
		}
		return EnumFacing.NORTH;
	}

	// 指定された位置がimportかどうか
	public Map<Byte, ItemStack> getItemStackMap(BlockPos pos) {
		for (Node node : nodeList) {
			if (node.pos.equals(pos)) {
				return node.items;
			}
		}
		return new HashMap<Byte, ItemStack>();
	}

	public void setItemStackMap(BlockPos pos, ItemStack[] filters) {
		Map<Byte, ItemStack> items = new HashMap<Byte, ItemStack>();
		for (byte i = 0; i < filters.length; i++) {
			if (filters[i] != null) {
				items.put(i, filters[i]);
			}
		}
		for (Node node : nodeList) {
			if (node.pos.equals(pos)) {
				node.items = items;
				return;
			}
		}
	}

	public void registerNode(BlockPos pos) {
		for (Node node : nodeList) {
			if (node.pos.equals(pos)) {
				Logger.info("Node already registered");
				return;
			}
		}
		nodeList.add(new Node(pos, true, true, true, EnumFacing.NORTH, new HashMap<Byte, ItemStack>()));
		Logger.info("Node added");
	}

	public void removeNode(BlockPos pos) {
		for (Node node : nodeList) {
			if (node.pos.equals(pos)) {
				nodeList.remove(node);
				Logger.info("Node removed : " + nodeList.size());
				return;
			}
		}
	}

	//itemStackにNBTを書き込む
	public boolean writeNBT(ItemStack itemStack) {
		if (itemStack.getItem() != AEMItems.workbook) {
			return false;
		}
		if (!itemStack.hasTagCompound()) {
			itemStack.setTagCompound(new NBTTagCompound());
		}
		itemStack.getTagCompound().setTag("itemIO", new NBTTagList());
		NBTTagList itenIOList = itemStack.getTagCompound().getTagList("itemIO", Constants.NBT.TAG_COMPOUND);
		for (Node node : nodeList) {
			NBTTagCompound itemIONode = new NBTTagCompound();
			itemIONode.setBoolean("isImport", node.isImport);
			itemIONode.setBoolean("isWhitelist", node.isWhitelist);
			itemIONode.setBoolean("ignoreNBT", node.ignoreNBT);
			itemIONode.setInteger("facing", node.facing.ordinal());
			itemIONode.setInteger("posX", node.pos.getX());
			itemIONode.setInteger("posY", node.pos.getY());
			itemIONode.setInteger("posZ", node.pos.getZ());
			NBTTagList itemdata = new NBTTagList();
			for (Map.Entry<Byte, ItemStack> bar : node.items.entrySet()) {
				NBTTagCompound itemNBT = new NBTTagCompound();
				itemNBT.setByte("slot", bar.getKey());
				(bar.getValue()).writeToNBT(itemNBT);
				itemdata.appendTag(itemNBT);
			}
			itemIONode.setTag("itemdata", itemdata);
			itenIOList.appendTag(itemIONode);
			Logger.info("nbt Node written");
		}
		Logger.info("nbt written");
		return true;
	}

}
