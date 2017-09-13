package com.iesika.aem.common.container;

import java.io.IOException;

import com.iesika.aem.common.handler.GuiHandler;
import com.iesika.aem.common.handler.GuiWorkbookMessage;
import com.iesika.aem.common.handler.PacketHandler;
import com.iesika.aem.common.tasks.MaidTaskItemIO;
import com.iesika.aem.common.tasks.MaidTaskManager;
import com.iesika.aem.util.FacingUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class WorkbookGuiContainer extends GuiContainer {

	private static final ResourceLocation TEXTURE_TIER0 = new ResourceLocation("aem", "textures/gui/workbook_tier0.png");
	private static final ResourceLocation TEXTURE_TIER1 = new ResourceLocation("aem", "textures/gui/workbook_tier1.png");
	private static final ResourceLocation TEXTURE_TIER2 = new ResourceLocation("aem", "textures/gui/workbook_tier2.png");
	private EntityPlayer entityPlayer;
	private GuiButton ioButton;
	private GuiButton wbButton;
	private GuiButton nbtButton;
	private GuiButton facingButton;
	private boolean isImport;
	private boolean isWhitelist;
	private boolean ignoreNBT;
	private EnumFacing facing;
	private BlockPos pos;
	private int GUIID;

	public WorkbookGuiContainer(int x, int y, int z, EntityPlayer entityPlayer, int GUIID) {
		super(new WorkbookContainer(x, y, z, entityPlayer, GUIID));
		this.entityPlayer = entityPlayer;
		this.pos = new BlockPos(x, y, z);
		this.GUIID = GUIID;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		switch (this.GUIID) {
		case GuiHandler.AEM_ITEMFILTER_TIER0_GUI_ID:
			this.mc.renderEngine.bindTexture(TEXTURE_TIER0);
			break;
		case GuiHandler.AEM_ITEMFILTER_TIER1_GUI_ID:
			this.mc.renderEngine.bindTexture(TEXTURE_TIER1);
			break;
		case GuiHandler.AEM_ITEMFILTER_TIER2_GUI_ID:
			this.mc.renderEngine.bindTexture(TEXTURE_TIER2);
			break;
		}
		this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, xSize, ySize);
	}

	@Override
	public void initGui() {
		super.initGui();
		MaidTaskManager mtm = new MaidTaskManager(entityPlayer.inventory.getCurrentItem());
		MaidTaskItemIO mtb = (MaidTaskItemIO) mtm.getRegisteredTaskFromBlockPos(pos);
		isImport = mtb.isImport;
		isWhitelist = mtb.isWhitelist;
		ignoreNBT = mtb.ignoreNBT;
		facing = mtb.facing;
		this.buttonList.add(ioButton = new GuiButton(0, guiLeft + 3, guiTop + 20, 54, 20, I18n.format(isImport ? "aem.text.import" : "aem.text.export")));
		this.buttonList.add(wbButton = new GuiButton(1, guiLeft + 3, guiTop + 45, 54, 20, I18n.format(isWhitelist ? "aem.text.whitelist" : "aem.text.blacklist")));
		this.buttonList.add(nbtButton = new GuiButton(2, guiLeft + 118, guiTop + 20, 54, 20, I18n.format(ignoreNBT ? "aem.text.ignoreNBT" : "aem.text.matchNBT")));
		this.buttonList.add(facingButton = new GuiButton(3, guiLeft + 118, guiTop + 45, 54, 20, I18n.format(FacingUtil.getFacingString(facing))));
	}

	@Override
	protected void actionPerformed(GuiButton pushedButton) throws IOException {
		super.actionPerformed(pushedButton);
		Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
		if (pushedButton == ioButton) {
			isImport = !isImport;
			ioButton.displayString = I18n.format(isImport ? "aem.text.import" : "aem.text.export");
		} else if (pushedButton == wbButton) {
			isWhitelist = !isWhitelist;
			wbButton.displayString = I18n.format(isWhitelist ? "aem.text.whitelist" : "aem.text.blacklist");
		} else if (pushedButton == nbtButton) {
			ignoreNBT = !ignoreNBT;
			nbtButton.displayString = I18n.format(ignoreNBT ? "aem.text.ignoreNBT" : "aem.text.matchNBT");
		} else if (pushedButton == facingButton) {
			facing = FacingUtil.getNextFacing(facing);
			facingButton.displayString = I18n.format(FacingUtil.getFacingString(facing));
		}
		PacketHandler.INSTANCE.sendToServer(new GuiWorkbookMessage(isImport, isWhitelist, ignoreNBT, facing.ordinal(), pos.getX(), pos.getY(), pos.getZ()));
		MaidTaskManager mtm = new MaidTaskManager(entityPlayer.inventory.getCurrentItem());
		MaidTaskItemIO mtb = (MaidTaskItemIO) mtm.getRegisteredTaskFromBlockPos(pos);
		mtb.isImport = isImport;
		mtb.isWhitelist = isWhitelist;
		mtb.ignoreNBT = ignoreNBT;
		mtb.facing = facing;
		mtm.writeToNBT(entityPlayer.inventory.getCurrentItem());
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

}
