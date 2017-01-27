package com.iesika.aem.common.handler;

import com.iesika.aem.util.JournalData;
import com.iesika.aem.util.Pos3;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.item.ItemStack;

public class GuiMessageHandler implements IMessageHandler<GuiMessage, IMessage>  {
    @Override
    public IMessage onMessage(GuiMessage message, MessageContext ctx) {

    	ItemStack journal = ctx.getServerHandler().playerEntity.inventory.getCurrentItem();
    	Pos3 point = new Pos3(message.x, message.y, message.z);
    	JournalData jd = new JournalData(journal);

    	jd.setImport((message.data & 2) != 0, point);
    	jd.setWhitelist((message.data & 1) != 0, point);
    	jd.setIgnoreNBT((message.data & 4) != 0, point);
    	jd.setFacing(message.facing, point);
    	jd.writeToNBT(journal);

        return null;
    }
}
