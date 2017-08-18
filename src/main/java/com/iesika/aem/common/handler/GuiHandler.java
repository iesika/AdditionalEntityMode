package com.iesika.aem.common.handler;

import com.iesika.aem.common.container.IOFilterContainer;
import com.iesika.aem.common.container.IOFilterGuiContainer;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler{

	public static final int AEM_IOFILTER_GUI_ID = 0;

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == GuiHandler.AEM_IOFILTER_GUI_ID){
			return (new IOFilterContainer(x, y, z, player));
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == GuiHandler.AEM_IOFILTER_GUI_ID){
			return (new IOFilterGuiContainer(x, y, z, player));
		}
		return null;
	}

}
