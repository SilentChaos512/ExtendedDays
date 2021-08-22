package net.silentchaos512.extendeddays.event;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DerivedWorldInfo;
import net.minecraft.world.storage.IServerWorldInfo;
import net.minecraft.world.storage.IWorldInfo;
import net.minecraft.world.storage.ServerWorldInfo;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;
import net.silentchaos512.extendeddays.ExtendedDays;
import net.silentchaos512.extendeddays.capability.ExtendedTime;
import net.silentchaos512.extendeddays.capability.ExtendedTimeCapability;
import net.silentchaos512.extendeddays.capability.IExtendedTime;
import net.silentchaos512.extendeddays.config.Config;
import net.silentchaos512.extendeddays.network.Network;
import net.silentchaos512.extendeddays.network.SetClientTimePacket;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = ExtendedDays.MOD_ID)
public final class TimeEvents {
    private static final Map<RegistryKey<World>, ExtendedTime> TIME_CAPS = new HashMap<>();

    @SubscribeEvent
    public static void onAttachWorldCapabilities(AttachCapabilitiesEvent<World> event) {
        if (!event.getObject().getCapability(ExtendedTimeCapability.INSTANCE).isPresent()) {
            event.addCapability(ExtendedTimeCapability.NAME, new ICapabilityProvider() {
                @Nonnull
                @Override
                public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
                    if (cap == ExtendedTimeCapability.INSTANCE) {
                        return ExtendedTimeCapability.INSTANCE.orEmpty(cap, LazyOptional.of(() ->
                                TIME_CAPS.computeIfAbsent(event.getObject().dimension(), key ->
                                        new ExtendedTime())));
                    }
                    return LazyOptional.empty();
                }
            });
        }
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        IExtendedTime timeCapability = event.world.getCapability(ExtendedTimeCapability.INSTANCE).orElseThrow(IllegalStateException::new);

        if (event.phase != TickEvent.Phase.START || hasFixedTime(event.world) || !(event.world instanceof ServerWorld)) {
            if (hasFixedTime(event.world)) {
                updateTrueTimeWithWorldTime(event.world, timeCapability);
            }
            return;
        }

//        ExtendedDays.LOGGER.debug("{}: {}", timeCapability, timeCapability.getTotalTicksPassed());

        timeCapability.tick();
        long trueTime = timeCapability.getTotalTicksPassed();
        long totalDayLength = getTotalDayLength();

        int daysPassed = (int) (trueTime / totalDayLength);
        int dayTime = (int) (trueTime % totalDayLength);

        long newVanillaDayTime;
        long newVanillaTime;
        if (dayTime < getDaytimeLength()) {
            newVanillaDayTime = 12_000L * dayTime / getDaytimeLength();
            newVanillaTime = newVanillaDayTime + 24_000L * daysPassed;
        } else {
            newVanillaDayTime = 24_000L * dayTime / getTotalDayLength();
            newVanillaTime = newVanillaDayTime + 24_000L * daysPassed;
        }

        long diff = Math.abs(newVanillaDayTime - event.world.getDayTime());
        if (diff > 99) {
            // Good chance the time has been altered by something (sleeping, command, etc.)
            ExtendedDays.LOGGER.info("Updating true time to match new day time: {}", event.world.getDayTime());
            updateTrueTimeWithWorldTime(event.world, timeCapability);
        } else {
            // Update time for server and clients
            setWorldTime((ServerWorld) event.world, newVanillaTime, trueTime);
        }
    }

    private static void updateTrueTimeWithWorldTime(World world, IExtendedTime cap) {
        // Update the true time based on the world time
        long dayTime = world.getDayTime();
        long trueTime;
        if (dayTime % 24_000L < 12_000L) {
            trueTime = getDaytimeLength() * dayTime / 12_000L;
        } else {
            trueTime = getTotalDayLength() * dayTime / 24_000L;
        }

        cap.setTotalTicksPassed(trueTime);
        setGameTime(world.getLevelData(), dayTime);
    }

    private static void setGameTime(IWorldInfo levelData, long gameTime) {
        //noinspection ChainOfInstanceofChecks
        if (levelData instanceof ServerWorldInfo) {
            IServerWorldInfo serverLevelData = (ServerWorldInfo) levelData;
//            serverLevelData.setGameTime(gameTime);
            serverLevelData.setDayTime(gameTime);
        } else if (levelData instanceof ClientWorld.ClientWorldInfo) {
            ClientWorld.ClientWorldInfo clientLevelData = (ClientWorld.ClientWorldInfo) levelData;
//            clientLevelData.setGameTime(gameTime);
            clientLevelData.setDayTime(gameTime);
        } else if (levelData instanceof DerivedWorldInfo) {
            IWorldInfo wrapped = ((DerivedWorldInfo) levelData).wrapped;
            setGameTime(wrapped, gameTime);
        } else {
            ExtendedDays.LOGGER.error("Unknown level data type: {}", levelData);
        }
    }

    private static void setWorldTime(ServerWorld world, long vanillaTime, long trueTime) {
        world.serverLevelData.setDayTime(vanillaTime % 24_000L);
//        world.serverLevelData.setGameTime(vanillaTime);
        Network.channel.send(PacketDistributor.ALL.noArg(), new SetClientTimePacket(vanillaTime, trueTime));
    }

    private static boolean hasFixedTime(World world) {
        return world.dimensionType().hasFixedTime() || !world.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT);
    }

    public static long getTotalDayLength() {
        // Total extended length of a day, in ticks
        return 1200 * (Config.Common.dayLength.get() + Config.Common.nightLength.get());
    }

    public static int getDaytimeLength() {
        return 1200 * Config.Common.dayLength.get();
    }

    public static int getNighttimeLength() {
        return 1200 * Config.Common.nightLength.get();
    }
}
