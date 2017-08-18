package com.iesika.aem.common.handler;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class IOFIlterMessage implements IMessage {

	public boolean isImport;
	public boolean whitelist;
	public boolean matchNBT;
	public byte facing;
	public double x;
	public double y;
	public double z;

	public IOFIlterMessage(){}

    public IOFIlterMessage(boolean isImport, boolean whitelist, boolean matchNBT, byte facing, double x, double y, double z) {
        this.isImport = isImport;
        this.whitelist = whitelist;
        this.matchNBT = matchNBT;
        this.facing = facing;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.isImport = buf.readBoolean();
        this.whitelist = buf.readBoolean();
        this.matchNBT = buf.readBoolean();
        this.facing = buf.readByte();
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
    }

    @Override
    public void toBytes(ByteBuf buf) {
    	buf.writeBoolean(this.isImport);
    	buf.writeBoolean(this.whitelist);
    	buf.writeBoolean(this.matchNBT);
        buf.writeByte(this.facing);
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
    }
}
