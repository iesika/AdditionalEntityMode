package com.iesika.aem.common.handler;

import com.iesika.aem.common.container.JournalContainer;
import com.iesika.aem.common.container.JournalGuiContainer;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler{

	public static final int AEM_JOURNAL_GUI_ID = 0;

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == GuiHandler.AEM_JOURNAL_GUI_ID){
			return (new JournalContainer(x, y, z, player.inventory));
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == GuiHandler.AEM_JOURNAL_GUI_ID){
			return (new JournalGuiContainer(x, y, z, player));
		}
		return null;
	}

}
