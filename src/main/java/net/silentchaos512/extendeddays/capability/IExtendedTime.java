package net.silentchaos512.extendeddays.capability;

import net.minecraft.nbt.INBT;

public interface IExtendedTime {
    void tick();

    long getTotalTicksPassed();

    void setTotalTicksPassed(long time);

    INBT serializeNbt();

    void deserializeNbt(INBT nbt);
}
