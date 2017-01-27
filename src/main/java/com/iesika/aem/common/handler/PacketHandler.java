package com.iesika.aem.common.handler;

import com.iesika.aem.AdditionalEntityMode;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler {

	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(AdditionalEntityMode.MODID);

	public static void init() {
		INSTANCE.registerMessage(GuiMessageHandler.class, GuiMessage.class, 0, Side.SERVER);
	}
}
