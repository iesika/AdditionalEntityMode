package com.iesika.aem.common.items;

import java.util.List;

import com.iesika.aem.AdditionalEntityMode;
import com.iesika.aem.common.handler.GuiHandler;
import com.iesika.aem.util.tasks.MaidTaskBase;
import com.iesika.aem.util.tasks.MaidTaskManager;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class Memo extends Item{

	public Memo() {
		// TODO 自動生成されたコンストラクター・スタブ
		super();
		setCreativeTab(CreativeTabs.tabMisc);
		setUnlocalizedName("memo");
		setTextureName("aem:memo");
		setMaxStackSize(1);
		GameRegistry.registerItem(this, "memo");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
		if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("maidtasks")) {
			list.add(StatCollector.translateToLocal("aem.text.nowork"));
		} else {
			MaidTaskManager mtm = new MaidTaskManager(stack);
			for (MaidTaskBase task : mtm.tasks){
				List<String> tinfo = task.getToolTipInfo();
				for (String line : tinfo){
					list.add(line);
				}
			}
			if (mtm.tasks.isEmpty()){
				list.add("Empty");
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
			player.openGui(AdditionalEntityMode.INSTANCE, GuiHandler.AEM_IOFILTER_GUI_ID, world, x, y, z);
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
