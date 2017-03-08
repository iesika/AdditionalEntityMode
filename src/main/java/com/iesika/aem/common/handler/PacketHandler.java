package com.iesika.aem.common.handler;

import com.iesika.aem.AdditionalEntityMode;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {

	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(AdditionalEntityMode.MODID);

	public static void init() {
		INSTANCE.registerMessage(GuiWorkbookMessageHandler.class, GuiWorkbookMessage.class, 0, Side.SERVER);
	}
}
