package net.silentchaos512.extendeddays;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.loading.FMLLoader;
import net.silentchaos512.extendeddays.capability.ExtendedTimeCapability;
import net.silentchaos512.extendeddays.config.Config;
import net.silentchaos512.extendeddays.event.DebugOverlay;
import net.silentchaos512.extendeddays.network.Network;
import net.silentchaos512.extendeddays.setup.Registration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

@Mod(ExtendedDays.MOD_ID)
@Mod.EventBusSubscriber(modid = ExtendedDays.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ExtendedDays {
    public static final String MOD_ID = "extendeddays";
    public static final String MOD_NAME = "Extended Days";

    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);
    public static final Random RANDOM = new Random();

    public ExtendedDays() {
        Registration.register();
        Config.init();
        Network.init();

        if (isDevBuild()) {
            DebugOverlay.init();
        }
    }

    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        ExtendedTimeCapability.register();
    }

    public static String getVersion() {
        return ModList.get().getModContainerById(MOD_ID)
                .map(c -> c.getModInfo().getVersion().toString())
                .orElse("INVALID"); // Should not happen
    }

    public static boolean isDevBuild() {
        return "NONE".equals(getVersion()) || !FMLLoader.isProduction();
    }


    public static ResourceLocation getId(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
