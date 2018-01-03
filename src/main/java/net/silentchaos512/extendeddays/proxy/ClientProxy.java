package net.silentchaos512.extendeddays.proxy;

import net.minecraftforge.common.MinecraftForge;
import net.silentchaos512.extendeddays.client.gui.ClockHud;
import net.silentchaos512.extendeddays.event.ClientEvents;
import net.silentchaos512.lib.registry.SRegistry;

public class ClientProxy extends CommonProxy {

  @Override
  public void preInit(SRegistry registry) {

    super.preInit(registry);
    registry.clientPreInit();

    MinecraftForge.EVENT_BUS.register(new ClientEvents());
    MinecraftForge.EVENT_BUS.register(ClockHud.INSTANCE);
  }

  @Override
  public void init(SRegistry registry) {

    super.init(registry);
    registry.clientInit();
  }

  @Override
  public void postInit(SRegistry registry) {

    super.postInit(registry);
    registry.postInit();
  }
}
