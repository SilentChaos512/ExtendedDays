package net.silentchaos512.extendeddays.proxy;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.silentchaos512.extendeddays.compat.morpheus.MorpheusCompat;
import net.silentchaos512.extendeddays.config.Config;
import net.silentchaos512.extendeddays.event.TimeEvents;
import net.silentchaos512.lib.registry.SRegistry;

public class CommonProxy {
    public void preInit(SRegistry registry, FMLPreInitializationEvent event) {
        registry.preInit(event);

        MinecraftForge.EVENT_BUS.register(TimeEvents.INSTANCE);

        // Morpheus compat
        if (Loader.isModLoaded("morpheus") && Config.MORPHEUS_OVERRIDE)
            MorpheusCompat.init();
    }

    public void init(SRegistry registry, FMLInitializationEvent event) {
        registry.init(event);
    }

    public void postInit(SRegistry registry, FMLPostInitializationEvent event) {
        registry.postInit(event);
    }
}
