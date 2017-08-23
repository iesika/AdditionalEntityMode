package com.iesika.aem.common;

import com.iesika.aem.AdditionalEntityMode;
import com.iesika.aem.util.Logger;

import net.minecraftforge.common.config.Configuration;

public class AEMConfig {

	private static final String CATEGORY_general = "General";
    private static final String CATEGORY_ItemCarier = "item carrier mode";

    //General
    public static int maidPathFindingRange = 64;

    //ItemCarrier
    public static int workbookTier0NodeSize = 2;
    public static int workbookTier1NodeSize = 4;
    public static int workbookTier2NodeSize = 6;


	public static void readConfig() {

		Configuration cfg = AdditionalEntityMode.config;

		try {
			cfg.load();
			initGeneralConfig(cfg);
			initItemCarrierConfig(cfg);
		} catch (Exception e1) {
			Logger.error("failed loading config file...");
		} finally {
			if (cfg.hasChanged()) {
				cfg.save();
			}
		}
	}

	private static void initGeneralConfig(Configuration cfg) {
		 cfg.addCustomCategoryComment(CATEGORY_general, "general configuration");
		 maidPathFindingRange = cfg.getInt("maidPathFindingRange", CATEGORY_general, maidPathFindingRange, 20, 1000, "maid of path finding range, may cause rag with high value [20-1000] default : 64", null);
	}

	 private static void initItemCarrierConfig(Configuration cfg) {
		 cfg.addCustomCategoryComment(CATEGORY_ItemCarier, "Item carrier mode configuration");
		 workbookTier0NodeSize = cfg.getInt("workbookTier0NodeSize", CATEGORY_ItemCarier, workbookTier0NodeSize, 2, 100, "The number of nodes that workbook tier0 can be recorded [2-100] default : 2", null);
		 workbookTier1NodeSize = cfg.getInt("workbookTier1NodeSize", CATEGORY_ItemCarier, workbookTier1NodeSize, 2, 100, "The number of nodes that workbook tier1 can be recorded [2-100] default : 4", null);
		 workbookTier2NodeSize = cfg.getInt("workbookTier2NodeSize", CATEGORY_ItemCarier, workbookTier2NodeSize, 2, 100, "The number of nodes that workbook tier2 can be recorded [2-100] default : 6", null);
	 }
}
