package com.iesika.aem.common.items;

import java.util.List;

import com.iesika.aem.AdditionalEntityMode;
import com.iesika.aem.common.handler.GuiHandler;
import com.iesika.aem.common.tasks.MaidTaskBase;
import com.iesika.aem.common.tasks.MaidTaskManager;
import com.iesika.aem.util.Logger;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Workbook extends Item {

	public Workbook() {
		super();
		this.setMaxDamage(0);
		this.setMaxStackSize(1);
		this.setHasSubtypes(true);
		this.setCreativeTab(CreativeTabs.MISC);
		this.setUnlocalizedName("workbook");
		GameRegistry.register(this, new ResourceLocation(AdditionalEntityMode.MODID, "workbook"));
		regiterModels();
	}

	public int meta2GUIID(int meta) {
		switch (meta) {
		case 0:
			return GuiHandler.AEM_WORKBOOK_TIER0_GUI_ID;
		case 1:
			return GuiHandler.AEM_WORKBOOK_TIER1_GUI_ID;
		case 2:
			return GuiHandler.AEM_WORKBOOK_TIER2_GUI_ID;
		default:
			Logger.warn("Incorrect GUI ID");
			return GuiHandler.AEM_WORKBOOK_TIER0_GUI_ID;
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName(stack) + "_" + stack.getItemDamage();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean advanced) {
		if (!itemStack.hasTagCompound() || !itemStack.getTagCompound().hasKey("maidtasks")) {
			list.add(I18n.format("aem.text.nowork"));
		} else {
			MaidTaskManager mtm = new MaidTaskManager(itemStack);
			for (MaidTaskBase task : mtm.tasks){
				List<String> tinfo = task.getToolTipInfo();
				for (String line : tinfo){
					list.add(line);
				}
			}
//			WorkbookHelper wh = new WorkbookHelper(itemStack);
//			for (WorkbookHelper.Node node : wh.nodeList) {
//				String spos = "x:" + Integer.toString(node.pos.getX()) + " y:" + Integer.toString(node.pos.getY()) + " z:" + Integer.toString(node.pos.getZ());
//				String sio = node.isImport ? ChatFormatting.WHITE + I18n.format("aem.text.import") : ChatFormatting.DARK_GRAY + I18n.format("aem.text.export");
//				String swb = node.isWhitelist ? ChatFormatting.WHITE + I18n.format("aem.text.whitelist") : ChatFormatting.DARK_GRAY + I18n.format("aem.text.blacklist");
//				String sNBT = node.ignoreNBT ? ChatFormatting.WHITE + I18n.format("aem.text.ignoreNBT") : ChatFormatting.DARK_GRAY + I18n.format("aem.text.matchNBT");
//				String sface = ChatFormatting.WHITE + I18n.format(FacingUtil.getFacingString(node.facing));
//				list.add(ChatFormatting.UNDERLINE + sio + " " + spos + " " + swb + " " + sNBT + " " + sface);
//				if (node.items.size() == 0 && !node.isWhitelist) {
//					list.add(ChatFormatting.GRAY + (node.isImport ? "Import" : "Export") + " all items");
//				} else {
//					for (ItemStack is : node.items.values()) {
//						list.add(ChatFormatting.GRAY + is.getDisplayName());
//					}
//				}
//			}
		}
	}

	@SideOnly(Side.CLIENT)
	public void regiterModels() {
		for (int meta = 0; meta < 3; meta++) {
			ModelLoader.setCustomModelResourceLocation(this, meta, new ModelResourceLocation(this.getRegistryName() + "_tier" + Integer.toString(meta), "inventory"));
			Logger.info(this.getRegistryName() + "_tier" + Integer.toString(meta));
		}
	}

	@Override
	public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
		for (int i = 0; i < 3; ++i) {
			subItems.add(new ItemStack(itemIn, 1, i));
		}
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		super.onItemUse(stack, playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ);
		if (!playerIn.isSneaking()) {
			return EnumActionResult.FAIL;
		}
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile != null && tile instanceof IInventory) {
			playerIn.openGui(AdditionalEntityMode.INSTANCE, meta2GUIID(stack.getMetadata()), worldIn, pos.getX(), pos.getY(), pos.getZ());
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.FAIL;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
		super.onItemRightClick(itemStackIn, worldIn, playerIn, hand);
		if (playerIn.isSneaking()) {
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemStackIn);
		}
		if (itemStackIn.getTagCompound() != null) {
			itemStackIn.setTagCompound(new NBTTagCompound());
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
	}

}
