package com.iesika.aem;

import java.io.File;

import com.iesika.aem.common.AEMConfig;
import com.iesika.aem.common.AEMCreativeTab;
import com.iesika.aem.common.AEMItems;
import com.iesika.aem.common.AEMRecipes;
import com.iesika.aem.common.CommonProxy;
import com.iesika.aem.common.handler.GuiHandler;
import com.iesika.aem.common.handler.PacketHandler;
import com.iesika.aem.util.Logger;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

@Mod(name = AdditionalEntityMode.NAME, modid = AdditionalEntityMode.MODID, version = AdditionalEntityMode.VERSION, dependencies = "after:lmreengaged")

public class AdditionalEntityMode {

	public static final String NAME = "AdditionalEntityMode";
	public static final String MODID = "aem";
	public static final String VERSION = "@VERSION@";

	@Instance
	public static AdditionalEntityMode INSTANCE;

	@SidedProxy(clientSide = "com.iesika.aem.common.ClientProxy", serverSide = "com.iesika.aem.common.CommonProxy")
	public static CommonProxy proxy;

	public static final CreativeTabs aemTab = new AEMCreativeTab();
	public static Configuration config;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		Logger.register(event);
		Logger.info("AEM preInit");

		File directory = event.getModConfigurationDirectory();
		config = new Configuration(new File(directory.getPath(), "AdditionalEntityMode.cfg"));
		AEMConfig.readConfig();

		PacketHandler.init();
		AEMItems.registerItems(aemTab);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		Logger.info("AEM Init");
		AEMRecipes.registeryRecipes();
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		Logger.info("AEM postInit");
	}
}
