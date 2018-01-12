package net.silentchaos512.extendeddays.proxy;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.silentchaos512.extendeddays.compat.morpheus.MorpheusCompat;
import net.silentchaos512.extendeddays.config.Config;
import net.silentchaos512.extendeddays.event.TimeEvents;
import net.silentchaos512.lib.registry.SRegistry;

public class CommonProxy {

  public void preInit(SRegistry registry) {

    registry.preInit();

    MinecraftForge.EVENT_BUS.register(TimeEvents.INSTANCE);

    // Morpheus compat
    if (Loader.isModLoaded("morpheus") && Config.MORPHEUS_OVERRIDE)
      MorpheusCompat.init();
  }

  public void init(SRegistry registry) {

    registry.init();
  }

  public void postInit(SRegistry registry) {

    registry.postInit();
  }
}
