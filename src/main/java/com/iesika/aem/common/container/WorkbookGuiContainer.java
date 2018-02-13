package com.iesika.aem.common.container;

import java.io.IOException;

import com.iesika.aem.common.handler.GuiHandler;
import com.iesika.aem.common.tasks.MaidTaskManager;

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

public class WorkbookGuiContainer extends GuiContainer{

	private static final ResourceLocation TEXTURE_TIER0 = new ResourceLocation("aem", "textures/gui/workbook_tier0.png");
	private static final ResourceLocation TEXTURE_TIER1 = new ResourceLocation("aem", "textures/gui/workbook_tier1.png");
	private static final ResourceLocation TEXTURE_TIER2 = new ResourceLocation("aem", "textures/gui/workbook_tier2.png");

	private EntityPlayer entityPlayer;
	private int GUIID;

	private GuiButton prevButton;
	private GuiButton nextButton;

	private GuiButton ioButton;
	private GuiButton wbButton;
	private GuiButton nbtButton;
	private GuiButton facingButton;

	private boolean isImport;
	private boolean isWhitelist;
	private boolean ignoreNBT;
	private EnumFacing facing;
	private BlockPos pos;

	private MaidTaskManager mtm;
	//現在見ているページ, mtm.tasksのインデックス
	private int viewPage = 0;

	public WorkbookGuiContainer(int x, int y, int z, EntityPlayer entityPlayer, int GUIID) {
		super(new WorkbookContainer(x, y, z, entityPlayer, GUIID));
		this.entityPlayer = entityPlayer;
		this.GUIID = GUIID;
		this.ySize = 202;
	}

	@Override
	protected void actionPerformed(GuiButton pushedButton) throws IOException {
		super.actionPerformed(pushedButton);
		Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
		//
		int maxPage = mtm.getNumberOfItemIOtask() - 1;

		if(pushedButton == prevButton){
			viewPage--;
			if (viewPage < 0){
				viewPage = maxPage;
			}
		}else if (pushedButton == nextButton){
			viewPage++;
			if (viewPage > maxPage){
				viewPage = 0;
			}
		}

		//entityPlayer.worldObj.
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		switch (this.GUIID) {
		case GuiHandler.AEM_WORKBOOK_TIER0_GUI_ID:
			this.mc.renderEngine.bindTexture(TEXTURE_TIER0);
			break;
		case GuiHandler.AEM_WORKBOOK_TIER1_GUI_ID:
			this.mc.renderEngine.bindTexture(TEXTURE_TIER1);
			break;
		case GuiHandler.AEM_WORKBOOK_TIER2_GUI_ID:
			this.mc.renderEngine.bindTexture(TEXTURE_TIER2);
			break;
		}
		this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, xSize, ySize);
	}

	@Override
	public void initGui() {
		super.initGui();

		this.buttonList.add(ioButton = new GuiButton(2, guiLeft + 3, guiTop + 56, 54, 20, I18n.format(isImport ? "aem.text.import" : "aem.text.export")));
		this.buttonList.add(wbButton = new GuiButton(3, guiLeft + 3, guiTop + 81, 54, 20, I18n.format(isWhitelist ? "aem.text.whitelist" : "aem.text.blacklist")));
		this.buttonList.add(nbtButton = new GuiButton(4, guiLeft + 118, guiTop + 56, 54, 20, I18n.format(ignoreNBT ? "aem.text.ignoreNBT" : "aem.text.matchNBT")));
		//this.buttonList.add(facingButton = new GuiButton(5, guiLeft + 118, guiTop + 81, 54, 20, I18n.format(FacingUtil.getFacingString(facing))));

		//このGUIを開くときは確実にworkbookを持っているはず
		mtm = new MaidTaskManager(entityPlayer.inventory.getCurrentItem());
		if (mtm == null || mtm.tasks.size() == 0){
			return;
		}

		this.buttonList.add(prevButton = new GuiSmallButton(0, guiLeft + 4, guiTop + 4, 10, 10, "<"));
		this.buttonList.add(nextButton = new GuiSmallButton(1, guiLeft + 160, guiTop + 4, 10, 10, ">"));

	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	private void showDummyTileEntity(){
		
	}

}
