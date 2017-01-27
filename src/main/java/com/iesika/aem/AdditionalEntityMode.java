package com.iesika.aem;

import com.iesika.aem.common.AEMItems;
import com.iesika.aem.common.config.AEMConfig;
import com.iesika.aem.common.handler.GuiHandler;
import com.iesika.aem.common.handler.PacketHandler;
import com.iesika.aem.util.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraftforge.common.config.Configuration;

@Mod(
		modid = AdditionalEntityMode.MODID,
		name =AdditionalEntityMode.NAME,
		version = AdditionalEntityMode.VERSION,
		dependencies = "required-after:lmmx"
)
public class AdditionalEntityMode
{
    public static final String MODID = "aem";
    public static final String NAME = "AdditionalEntityMode";
    public static final String VERSION = "1.0";

    @Instance(MODID)
	public static AdditionalEntityMode INSTANCE;


    @SidedProxy(clientSide = "com.iesika.aem.ClientProxy", serverSide = "com.iesika.aem.CommonProxy")
    public static CommonProxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		Logger.register(event);
		Logger.info("preInit");
		PacketHandler.init();
		//config
		(new AEMConfig()).config(new Configuration(event.getSuggestedConfigurationFile()));
		AEMItems.registryItems();
	}

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	Logger.info("init");
    	NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
    }
}
