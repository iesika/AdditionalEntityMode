package com.iesika.aem.common.handler;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class GuiMessage implements IMessage {

	public byte data;
	public byte facing;
	public int x;
	public int y;
	public int z;

	public GuiMessage() {}

    public GuiMessage(byte par1) {
        this.data= par1;
    }

    public GuiMessage(byte par1, byte facing, int ax, int ay, int az) {
        this.data= par1;
        this.facing = facing;
        this.x = ax;
        this.y = ay;
        this.z = az;
    }

    @Override//IMessageのメソッド。ByteBufからデータを読み取る。
    public void fromBytes(ByteBuf buf) {
        this.data= buf.readByte();
        this.facing = buf.readByte();
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
    }

    @Override//IMessageのメソッド。ByteBufにデータを書き込む。
    public void toBytes(ByteBuf buf) {
        buf.writeByte(this.data);
        buf.writeByte(this.facing);
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
    }
}
