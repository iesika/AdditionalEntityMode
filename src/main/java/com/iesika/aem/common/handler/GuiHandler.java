package com.iesika.aem.common.handler;

import com.iesika.aem.common.container.WorkbookContainer;
import com.iesika.aem.common.container.WorkbookGuiContainer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

	public static final int AEM_WORKBOOK_TIER0_GUI_ID = 0;
	public static final int AEM_WORKBOOK_TIER1_GUI_ID = 1;
	public static final int AEM_WORKBOOK_TIER2_GUI_ID = 2;

	@Override
	public Object getServerGuiElement(int guiID, EntityPlayer player, World world, int x, int y, int z) {
		if (guiID == AEM_WORKBOOK_TIER0_GUI_ID || guiID == AEM_WORKBOOK_TIER1_GUI_ID || guiID == AEM_WORKBOOK_TIER2_GUI_ID){
			return new WorkbookContainer(x, y, z, player, guiID);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int guiID, EntityPlayer player, World world, int x, int y, int z) {
		if (guiID == AEM_WORKBOOK_TIER0_GUI_ID || guiID == AEM_WORKBOOK_TIER1_GUI_ID || guiID == AEM_WORKBOOK_TIER2_GUI_ID){
			return new WorkbookGuiContainer(x, y, z, player, guiID);
		}
		return null;
	}

}
