package net.silentchaos512.extendeddays.proxy;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.silentchaos512.extendeddays.client.gui.ClockHud;
import net.silentchaos512.extendeddays.event.ClientEvents;
import net.silentchaos512.lib.registry.SRegistry;

public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(SRegistry registry, FMLPreInitializationEvent event) {
        super.preInit(registry, event);
        registry.clientPreInit(event);

        MinecraftForge.EVENT_BUS.register(new ClientEvents());
        MinecraftForge.EVENT_BUS.register(ClockHud.INSTANCE);
    }

    @Override
    public void init(SRegistry registry, FMLInitializationEvent event) {
        super.init(registry, event);
        registry.clientInit(event);
    }

    @Override
    public void postInit(SRegistry registry, FMLPostInitializationEvent event) {
        super.postInit(registry, event);
        registry.postInit(event);
    }
}
