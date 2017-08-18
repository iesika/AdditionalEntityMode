package com.iesika.aem.common.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class AEMConfig {
	public static boolean enableCarrierMode = true;
	public static int carrierModeCooltime = 50;
	public static double carrierModeSearchRange = 100D;

	public void config(Configuration cfg) {
		try {
			cfg.load();

			Property pEnableCarrierMode = cfg.get("CarrierMode", "enable carrier mode", enableCarrierMode, "enable CarrierMode");
			Property pCarrierModeCooltime = cfg.get("CarrierMode", "cooltime", carrierModeCooltime, "CarrierMode of cooltime");
			Property pCarrierModeSearchRange = cfg.get("CarrierMode", "search range", carrierModeSearchRange, "Path Search Range");

			enableCarrierMode = pEnableCarrierMode.getBoolean();
			carrierModeCooltime = pCarrierModeCooltime.getInt();
			carrierModeSearchRange = pCarrierModeSearchRange.getDouble();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cfg.save();
		}
	}
}
