package com.iesika.aem.common.items;

import java.util.ArrayList;
import java.util.List;

import com.iesika.aem.AdditionalEntityMode;
import com.iesika.aem.common.handler.GuiHandler;
import com.iesika.aem.util.FacingUtil;
import com.mojang.realmsclient.gui.ChatFormatting;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public class Journal extends Item {

	public Journal() {
		// TODO 自動生成されたコンストラクター・スタブ
		super();
		setCreativeTab(CreativeTabs.tabMisc);
		setUnlocalizedName("journal");
		setTextureName("aem:journal");
		setMaxStackSize(1);
		GameRegistry.registerItem(this, "journal");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean advanced) {
		if (!itemStack.hasTagCompound() || !itemStack.getTagCompound().hasKey("io")) {
			list.add(StatCollector.translateToLocal("aem.text.nowork"));
		} else {
			String im = StatCollector.translateToLocal("aem.text.import");
			String ex = StatCollector.translateToLocal("aem.text.export");
			String whl = StatCollector.translateToLocal("aem.text.whitelist");
			String bll = StatCollector.translateToLocal("aem.text.blacklist");
			String ign = StatCollector.translateToLocal("aem.text.ignoreNBT");
			String man = StatCollector.translateToLocal("aem.text.matchNBT");

			NBTTagList ioList = itemStack.getTagCompound().getTagList("io", Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < ioList.tagCount(); i++) {
				List<ItemStack> itemList = new ArrayList<ItemStack>();
				NBTTagCompound data = ioList.getCompoundTagAt(i);
				int x = data.getInteger("PosX");
				int y = data.getInteger("PosY");
				int z = data.getInteger("PosZ");
				boolean isImport = data.getBoolean("isImport");
				boolean isWhiteList = data.getBoolean("isWhitelist");
				boolean isIgnoreNBT = data.getBoolean("isIgnoreNBT");
				byte facing = data.getByte("facing");
				NBTTagList itemdata = data.getTagList("itemdata", Constants.NBT.TAG_COMPOUND);
				for (int j = 0; j < itemdata.tagCount(); j++) {
					NBTTagCompound tagCompound = itemdata.getCompoundTagAt(j);
					int slot = tagCompound.getByte("Slot");
					ItemStack is = ItemStack.loadItemStackFromNBT(tagCompound);
					if (is != null) {
						itemList.add(is);
					}
				}

				String pos = "x:" + Integer.toString(x) + " y:" + Integer.toString(y) + " z:" + Integer.toString(z);
				pos = ChatFormatting.GRAY + pos;
				String io = isImport ? ChatFormatting.WHITE + im : ChatFormatting.DARK_GRAY + ex;
				String wb = isWhiteList ? ChatFormatting.WHITE + whl : ChatFormatting.DARK_GRAY + bll;
				String sNBT = isIgnoreNBT ? ChatFormatting.WHITE + ign : ChatFormatting.DARK_GRAY + man;
				String face = ChatFormatting.WHITE + StatCollector.translateToLocal(FacingUtil.getFacingString(facing));
				list.add(ChatFormatting.UNDERLINE + io + " " + pos + " " + wb + " " + sNBT + " " + face);
				if(itemList.size() == 0){
					String sss;
					if (isImport){
						sss = "Import";
					}else{
						sss = "Export";
					}
					list.add(ChatFormatting.GRAY + sss +" all items");
				}else{
					for (ItemStack is : itemList) {
						list.add(ChatFormatting.GRAY + is.getDisplayName());
					}
				}
			}
		}
	}

	@Override
	public boolean onItemUse(ItemStack tool, EntityPlayer player, World world, int x, int y, int z, int par7, float xf, float yf, float zf) {
		// shift押して無いなら無視
		if (!player.isSneaking()) {
			return false;
		}
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile != null && tile instanceof IInventory) {
			player.openGui(AdditionalEntityMode.INSTANCE, GuiHandler.AEM_JOURNAL_GUI_ID, world, x, y, z);
			return true;
		}
		return false;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
		super.onItemRightClick(itemStack, world, entityPlayer);
		if (entityPlayer.isSneaking()) {
			return itemStack;
		}
		if (itemStack.getTagCompound() != null){
			itemStack.setTagCompound(new NBTTagCompound());//クリアー
		}
		return itemStack;
	}

}
