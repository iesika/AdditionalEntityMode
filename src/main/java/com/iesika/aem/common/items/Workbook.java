package com.iesika.aem.common.items;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.iesika.aem.AdditionalEntityMode;
import com.iesika.aem.common.AEMConfig;
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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
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

	public int meta2itemFilterGUIID(int meta) {
		switch (meta) {
		case 0:
			return GuiHandler.AEM_ITEMFILTER_TIER0_GUI_ID;
		case 1:
			return GuiHandler.AEM_ITEMFILTER_TIER1_GUI_ID;
		case 2:
			return GuiHandler.AEM_ITEMFILTER_TIER2_GUI_ID;
		default:
			Logger.warn("Incorrect GUI ID");
			return GuiHandler.AEM_ITEMFILTER_TIER0_GUI_ID;
		}
	}

	public int meta2workbookGUIID(int meta){
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
		int current = getNumberOfItemIOPoint(itemStack);
		int max = getMaxNunmberOfItemIOPoint(itemStack);
		list.add(I18n.format("aem.text.registered") + " " + current + " / " + max);
		if (!itemStack.hasTagCompound() || !itemStack.getTagCompound().hasKey("maidtasks")) {
			list.add(I18n.format("aem.text.nowork"));
		} else {
			MaidTaskManager mtm = new MaidTaskManager(itemStack);
			list.add(StringUtils.repeat("-", 50));
			for (MaidTaskBase task : mtm.tasks){
				List<String> tinfo = task.getToolTipInfo();
				for (String line : tinfo){
					list.add(line);
				}
				list.add(StringUtils.repeat("-", 50));
			}
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
			int current = getNumberOfItemIOPoint(stack);
			int max = getMaxNunmberOfItemIOPoint(stack);
			if (max > current){
				playerIn.openGui(AdditionalEntityMode.INSTANCE, meta2itemFilterGUIID(stack.getMetadata()), worldIn, pos.getX(), pos.getY(), pos.getZ());
				return EnumActionResult.SUCCESS;
			}else{
				if (!worldIn.isRemote){
					playerIn.addChatMessage(new TextComponentTranslation("aem.text.cantregister"));
				}
			}
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
			playerIn.openGui(AdditionalEntityMode.INSTANCE, meta2workbookGUIID(itemStackIn.getMetadata()), worldIn, 0, 0, 0);
			//itemStackIn.setTagCompound(new NBTTagCompound());
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
	}

	private int getNumberOfItemIOPoint(ItemStack workbook){
		return (new MaidTaskManager(workbook)).getNumberOfItemIOtask();
	}

	private int getMaxNunmberOfItemIOPoint(ItemStack workbook){
		int max = 2;
		if (workbook.getMetadata() == 0) {
			max = AEMConfig.workbookTier0NodeSize;
		} else if (workbook.getMetadata() == 1) {
			max = AEMConfig.workbookTier1NodeSize;
		} else if (workbook.getMetadata() == 2) {
			max = AEMConfig.workbookTier2NodeSize;
		}
		return max;
	}
}
