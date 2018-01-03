package net.silentchaos512.extendeddays;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.silentchaos512.extendeddays.config.Config;
import net.silentchaos512.extendeddays.proxy.CommonProxy;
import net.silentchaos512.lib.SilentLib;
import net.silentchaos512.lib.registry.SRegistry;
import net.silentchaos512.lib.util.LocalizationHelper;
import net.silentchaos512.lib.util.LogHelper;

@Mod(modid = ExtendedDays.MOD_ID, name = ExtendedDays.MOD_NAME, version = ExtendedDays.VERSION, dependencies = ExtendedDays.DEPENDENCIES)
public class ExtendedDays {

  public static final String MOD_ID = "extendeddays";
  public static final String MOD_NAME = "Extended Days";
  public static final String VERSION = "@VERSION@";
  public static final String VERSION_SILENTLIB = "SL_VERSION";
  public static final int BUILD_NUM = 0;
  public static final String DEPENDENCIES = "required-after:silentlib@[" + VERSION_SILENTLIB
      + ",);";
  public static final String RESOURCE_PREFIX = MOD_ID + ":";

  @Instance
  public static ExtendedDays instance;

  @SidedProxy(clientSide = "net.silentchaos512.extendeddays.proxy.ClientProxy", serverSide = "net.silentchaos512.extendeddays.proxy.CommonProxy")
  public static CommonProxy proxy;

  public static LogHelper logHelper = new LogHelper(MOD_NAME, BUILD_NUM);
  public static LocalizationHelper localizationHelper;

  public static SRegistry registry = new SRegistry(MOD_ID, logHelper);

  @EventHandler
  public void preInit(FMLPreInitializationEvent event) {

    localizationHelper = new LocalizationHelper(MOD_ID).setReplaceAmpersand(true);
    SilentLib.instance.registerLocalizationHelperForMod(MOD_ID, localizationHelper);

    Config.INSTANCE.init(event.getSuggestedConfigurationFile());

    // TODO: Registration handlers go here.

    proxy.preInit(registry);
  }

  @EventHandler
  public void init(FMLInitializationEvent event) {

    proxy.init(registry);
    Config.INSTANCE.save();
  }

  @EventHandler
  public void postInit(FMLPostInitializationEvent event) {

    proxy.postInit(registry);
  }
}
