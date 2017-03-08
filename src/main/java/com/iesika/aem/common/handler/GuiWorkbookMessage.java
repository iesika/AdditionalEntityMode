package com.iesika.aem.common.handler;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class GuiWorkbookMessage implements IMessage {

	public boolean isImoprt;
	public boolean isWhitelist;
	public boolean ignoreNBT;
	public int facing;
	public int x;
	public int y;
	public int z;

	public GuiWorkbookMessage(){}

	public GuiWorkbookMessage(boolean isImoprt,boolean isWhitelist, boolean ignoreNBT, int facing, int ax, int ay, int az) {
		this.isImoprt = isImoprt;
		this.isWhitelist = isWhitelist;
		this.ignoreNBT = ignoreNBT;
		this.facing = facing;
		this.x = ax;
		this.y = ay;
		this.z = az;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.isImoprt = buf.readBoolean();
		this.isWhitelist = buf.readBoolean();
		this.ignoreNBT = buf.readBoolean();
		this.facing = buf.readInt();
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(this.isImoprt);
		buf.writeBoolean(this.isWhitelist);
		buf.writeBoolean(this.ignoreNBT);
		buf.writeInt(this.facing);
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
	}

}
