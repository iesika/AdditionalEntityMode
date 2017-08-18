package com.iesika.aem.common.container;

import org.lwjgl.opengl.GL11;

import com.iesika.aem.common.handler.IOFIlterMessage;
import com.iesika.aem.common.handler.PacketHandler;
import com.iesika.aem.util.FacingUtil;
import com.iesika.aem.util.tasks.MaidTaskInOut;
import com.iesika.aem.util.tasks.MaidTaskManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;

public class IOFilterGuiContainer extends GuiContainer {

	private static final ResourceLocation TEXTURE = new ResourceLocation("aem", "textures/gui/iofilter.png");
	private EntityPlayer player;
	private GuiButton ioButton;
	private GuiButton wbButton;
	private GuiButton nbtButton;
	private GuiButton facingButton;
	private boolean isImport;
	private boolean whitelist;
	private boolean matchNBT;
	private byte facing;
	private Vec3 pos;

	public IOFilterGuiContainer(int x,int y, int z, EntityPlayer e){
		super(new IOFilterContainer(x, y, z, e));
		this.player = e;
		this.pos = Vec3.createVectorHelper(x + 0.5D,y + 0.5D, z + 0.5D);
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
		MaidTaskManager mtm = new MaidTaskManager(player.inventory.getCurrentItem());
		MaidTaskInOut mtb = (MaidTaskInOut) mtm.getRegisteredTaskFromVec3(pos);

	    //JournalData jd = new JournalData(ep.inventory.getCurrentItem());
	    this.isImport = mtb.isImport;
	    this.whitelist = mtb.whitelist;
	    this.matchNBT = mtb.matchNBT;
	    this.facing = mtb.facing;
	    String s1 = StatCollector.translateToLocal(isImport ? "aem.text.import" : "aem.text.export");
	    String s2 = StatCollector.translateToLocal(whitelist ? "aem.text.whitelist" : "aem.text.blacklist");
	    String s3 = StatCollector.translateToLocal(matchNBT ? "aem.text.ignoreNBT" : "aem.text.matchNBT");
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
			whitelist = !whitelist;
			wbButton.displayString = StatCollector.translateToLocal(whitelist ? "aem.text.whitelist" : "aem.text.blacklist");
		}else if(pushedButton == nbtButton){
			matchNBT = !matchNBT;
			nbtButton.displayString = StatCollector.translateToLocal(matchNBT ? "aem.text.ignoreNBT" : "aem.text.matchNBT");
		}else if(pushedButton == facingButton){
			facing++;
			facing %= 6;
			facingButton.displayString = StatCollector.translateToLocal(FacingUtil.getFacingString(facing));
		}

		PacketHandler.INSTANCE.sendToServer(new IOFIlterMessage(isImport, whitelist, matchNBT, facing, pos.xCoord, pos.yCoord, pos.zCoord));
		MaidTaskManager mtm = new MaidTaskManager(player.inventory.getCurrentItem());
		MaidTaskInOut mtb = (MaidTaskInOut) mtm.getRegisteredTaskFromVec3(pos);
		mtb.isImport = isImport;
		mtb.whitelist = whitelist;
		mtb.matchNBT = matchNBT;
		mtb.facing = facing;
		mtm.writeToNBT(player.inventory.getCurrentItem());
	}

    /*GUIが開いている時にゲームの処理を止めるかどうか。*/
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

}
