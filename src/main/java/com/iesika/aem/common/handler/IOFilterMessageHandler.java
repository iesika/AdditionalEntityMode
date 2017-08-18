package com.iesika.aem.common.handler;

import com.iesika.aem.util.tasks.MaidTaskInOut;
import com.iesika.aem.util.tasks.MaidTaskManager;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;

public class IOFilterMessageHandler implements IMessageHandler<IOFIlterMessage, IMessage>  {
    @Override
    public IMessage onMessage(IOFIlterMessage message, MessageContext ctx) {

    	ItemStack journal = ctx.getServerHandler().playerEntity.inventory.getCurrentItem();

    	MaidTaskManager mtm = new MaidTaskManager(journal);
    	Vec3 v = Vec3.createVectorHelper(message.x, message.y, message.z);
    	MaidTaskInOut mtb = (MaidTaskInOut)mtm.getRegisteredTaskFromVec3(v);
    	mtb.isImport = message.isImport;
    	mtb.whitelist = message.whitelist;
    	mtb.matchNBT = message.matchNBT;
    	mtb.facing = message.facing;

    	mtm.writeToNBT(journal);

        return null;
    }
}
