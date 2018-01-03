package net.silentchaos512.extendeddays.proxy;

import net.minecraftforge.common.MinecraftForge;
import net.silentchaos512.extendeddays.event.TimeEvents;
import net.silentchaos512.lib.registry.SRegistry;

public class CommonProxy {

  public void preInit(SRegistry registry) {

    registry.preInit();

    MinecraftForge.EVENT_BUS.register(new TimeEvents());
  }

  public void init(SRegistry registry) {

    registry.init();
  }

  public void postInit(SRegistry registry) {

    registry.postInit();
  }
}
