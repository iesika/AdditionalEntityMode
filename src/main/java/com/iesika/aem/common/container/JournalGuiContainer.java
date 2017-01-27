package com.iesika.aem.common.container;

import org.lwjgl.opengl.GL11;

import com.iesika.aem.common.handler.GuiMessage;
import com.iesika.aem.common.handler.PacketHandler;
import com.iesika.aem.util.FacingUtil;
import com.iesika.aem.util.JournalData;
import com.iesika.aem.util.Logger;
import com.iesika.aem.util.Pos3;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class JournalGuiContainer extends GuiContainer {

	private static final ResourceLocation TEXTURE = new ResourceLocation("aem", "textures/gui/journal.png");
	private EntityPlayer ep;
	private GuiButton ioButton;
	private GuiButton wbButton;
	private GuiButton nbtButton;
	private GuiButton facingButton;
	private boolean isImport;
	private boolean isWhitelist;
	private boolean isIgnoreNBT;
	private byte facing;
	private Pos3 point;

	public JournalGuiContainer(int x,int y, int z, EntityPlayer e){
		super(new JournalContainer(x, y, z, e.inventory));
		this.ep = e;
		this.point = new Pos3(x, y, z);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseZ) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(TEXTURE);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, xSize, ySize);
	}

	@Override
	public void initGui() {
		super.initGui();
	    JournalData jd = new JournalData(ep.inventory.getCurrentItem());
	    isImport = jd.isImport(point);
	    isWhitelist = jd.isWhitelist(point);
	    isIgnoreNBT = jd.isIgnoreNBT(point);
	    facing = jd.getFacing(point);
	    String s1 = StatCollector.translateToLocal(isImport ? "aem.text.import" : "aem.text.export");
	    String s2 = StatCollector.translateToLocal(isWhitelist ? "aem.text.whitelist" : "aem.text.blacklist");
	    String s3 = StatCollector.translateToLocal(isIgnoreNBT ? "aem.text.ignoreNBT" : "aem.text.matchNBT");
	    String s4 = StatCollector.translateToLocal(FacingUtil.getFacingString(facing));
		this.buttonList.add(ioButton = new GuiButton(0, guiLeft + 5, guiTop + 20, 50, 20, s1));
	    this.buttonList.add(wbButton = new GuiButton(1, guiLeft + 5, guiTop + 45, 50, 20, s2));
	    this.buttonList.add(nbtButton = new GuiButton(2, guiLeft + 120, guiTop + 20, 50, 20, s3));
	    this.buttonList.add(facingButton = new GuiButton(3, guiLeft + 120, guiTop + 45, 50, 20, s4));
	}

	@Override
	protected void actionPerformed(GuiButton pushedButton) {
		super.actionPerformed(pushedButton);
		Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
		if(pushedButton == ioButton){
			isImport = !isImport;
			ioButton.displayString = StatCollector.translateToLocal(isImport ? "aem.text.import" : "aem.text.export");
		}else if(pushedButton == wbButton){
			isWhitelist = !isWhitelist;
			wbButton.displayString = StatCollector.translateToLocal(isWhitelist ? "aem.text.whitelist" : "aem.text.blacklist");
		}else if(pushedButton == nbtButton){
			isIgnoreNBT = !isIgnoreNBT;
			nbtButton.displayString = StatCollector.translateToLocal(isIgnoreNBT ? "aem.text.ignoreNBT" : "aem.text.matchNBT");
		}else if(pushedButton == facingButton){
			facing++;
			facing %= 6;
			Logger.info("facing:" +Byte.toString(facing));
			facingButton.displayString = StatCollector.translateToLocal(FacingUtil.getFacingString(facing));
		}
		//serverに通知用byteじゃなくて全部booleanにしよ？
		byte data = (byte)0;
		data += isImport ? (byte)2 : (byte)0;
		data += isWhitelist ? (byte)1 : (byte)0;
		data += isIgnoreNBT ? (byte)4 : (byte)0;
		/*
		 * isimport isWhiteList
		 * */

		PacketHandler.INSTANCE.sendToServer(new GuiMessage(data, facing, point.x, point.y, point.z));
		JournalData jd = new JournalData(ep.inventory.getCurrentItem());
		jd.setImport((data & 2) != 0, point);
		jd.setWhitelist((data & 1) != 0, point);
		jd.setIgnoreNBT((data & 4) != 0, point);
		jd.setFacing(facing, point);
		jd.writeToNBT(ep.inventory.getCurrentItem());
	}

    /*GUIが開いている時にゲームの処理を止めるかどうか。*/
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

}
