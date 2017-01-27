package com.iesika.aem.common.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class AEMConfig {
	public static int carrierModeCooltime = 50;

	public void config(Configuration cfg) {
		try {
			cfg.load();

			Property pCarrierCooltime = cfg.get("Cooltime", "CarrierMode", carrierModeCooltime, "CarrierMode of cooltime");

			carrierModeCooltime = pCarrierCooltime.getInt();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cfg.save();
		}
	}
}
