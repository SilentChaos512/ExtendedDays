package net.silentchaos512.extendeddays.capability;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.silentchaos512.extendeddays.ExtendedDays;

import javax.annotation.Nullable;

public final class ExtendedTimeCapability {
    @CapabilityInject(IExtendedTime.class)
    public static Capability<IExtendedTime> INSTANCE = null;
    public static final ResourceLocation NAME = ExtendedDays.getId("time");

    private ExtendedTimeCapability() {}

    public static void register() {
        CapabilityManager.INSTANCE.register(IExtendedTime.class,
                new Capability.IStorage<IExtendedTime>() {
                    @Nullable
                    @Override
                    public INBT writeNBT(Capability<IExtendedTime> capability, IExtendedTime instance, Direction side) {
                        return instance.serializeNbt();
                    }

                    @Override
                    public void readNBT(Capability<IExtendedTime> capability, IExtendedTime instance, Direction side, INBT nbt) {
                        instance.deserializeNbt(nbt);
                    }
                },
                ExtendedTime::new);
    }
}
