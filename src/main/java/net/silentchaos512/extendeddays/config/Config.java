package net.silentchaos512.extendeddays.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public final class Config {
    public static final class Common {
        static final ForgeConfigSpec spec;

        public static final ForgeConfigSpec.IntValue dayLength;
        public static final ForgeConfigSpec.IntValue nightLength;

        static {
            ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

            {
                builder.push("time");

                dayLength = builder
                        .comment("The length of daytime in a day (in minutes). In vanilla this is 10 minutes.")
                        .defineInRange("dayLength", 40, 1, Integer.MAX_VALUE);

                nightLength = builder
                        .comment("The length of nighttime in a day (in minutes). In vanilla, this is 10 minutes.")
                        .defineInRange("nightLength", 20, 1, Integer.MAX_VALUE);

                builder.pop();
            }

            spec = builder.build();
        }

        private Common() {}
    }

    public static final class Client {
        static final ForgeConfigSpec spec;

        static {
            ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

            spec = builder.build();
        }

        private Client() {}
    }

    private Config() {}

    public static void init() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Common.spec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Client.spec);
    }

    public static void sync() {
    }

    @SubscribeEvent
    public static void sync(ModConfig.Loading event) {
        sync();
    }

    @SubscribeEvent
    public static void sync(ModConfig.Reloading event) {
        sync();
    }
}
