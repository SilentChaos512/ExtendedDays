package net.silentchaos512.extendeddays.world;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

public class ExtendedDaysSavedData extends WorldSavedData {
    private static final String DATA_NAME = "ExtendedDays_Time";
    private static final String NBT_EXTENDED_TIME = "ExtendedTime";
    private static final String NBT_WORLD_TIME = "WorldTime";

    public int extendedTime = -1;
    public long worldTime = -1;

    public ExtendedDaysSavedData() {
        super(DATA_NAME);
    }

    public ExtendedDaysSavedData(String s) {
        super(s);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        extendedTime = nbt.getInteger(NBT_EXTENDED_TIME);
        worldTime = nbt.getLong(NBT_WORLD_TIME);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setInteger(NBT_EXTENDED_TIME, extendedTime);
        nbt.setLong(NBT_WORLD_TIME, worldTime);
        return nbt;
    }

    public static ExtendedDaysSavedData get(World world) {
        MapStorage storage = world.getPerWorldStorage();
        ExtendedDaysSavedData instance = (ExtendedDaysSavedData) storage.getOrLoadData(ExtendedDaysSavedData.class, DATA_NAME);

        if (instance == null) {
            instance = new ExtendedDaysSavedData();
            storage.setData(DATA_NAME, instance);
        }
        return instance;
    }
}
