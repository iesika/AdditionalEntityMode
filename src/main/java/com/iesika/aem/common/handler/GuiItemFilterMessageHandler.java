package com.iesika.aem.common.handler;

import com.iesika.aem.common.tasks.MaidTaskItemIO;
import com.iesika.aem.common.tasks.MaidTaskManager;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class GuiItemFilterMessageHandler implements IMessageHandler<GuiItemFilterMessage, IMessage> {

	@Override
	public IMessage onMessage(GuiItemFilterMessage message, MessageContext ctx) {

		ItemStack workbook = ctx.getServerHandler().playerEntity.inventory.getCurrentItem();
		BlockPos pos = new BlockPos(message.x, message.y, message.z);

		MaidTaskManager mtm = new MaidTaskManager(workbook);
		MaidTaskItemIO mtio = (MaidTaskItemIO)mtm.getRegisteredTaskFromBlockPos(pos);
		mtio.isImport = message.isImoprt;
		mtio.isWhitelist = message.isWhitelist;
		mtio.ignoreNBT = message.ignoreNBT;
		mtio.facing = EnumFacing.values()[message.facing];
		mtm.writeToNBT(workbook);

		return null;
	}

}
