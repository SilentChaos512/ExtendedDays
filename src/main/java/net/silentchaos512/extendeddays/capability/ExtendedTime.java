package net.silentchaos512.extendeddays.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;

public class ExtendedTime implements IExtendedTime {
    private long time;

    @Override
    public void tick() {
        ++time;
    }

    @Override
    public long getTotalTicksPassed() {
        return time;
    }

    @Override
    public void setTotalTicksPassed(long time) {
        this.time = time;
    }

    @Override
    public INBT serializeNbt() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putLong("Time", time);
        return nbt;
    }

    @Override
    public void deserializeNbt(INBT nbt) {
        CompoundNBT compound = (CompoundNBT) nbt;
        time = compound.getLong("Time");
    }
}
