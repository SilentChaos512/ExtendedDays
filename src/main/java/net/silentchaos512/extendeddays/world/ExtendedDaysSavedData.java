package net.silentchaos512.extendeddays.world;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.silentchaos512.extendeddays.ExtendedDays;


public class ExtendedDaysSavedData extends WorldSavedData {

  private static final String DATA_NAME = "ExtendedDays_Time";
  private static final String NBT_EXTENDED_TIME = "ExtendedTime";
  private static final String NBT_WORLD_TIME = "WorldTime";

  public int extendedTime = -1;
  public int worldTime = -1;

  public ExtendedDaysSavedData() {

    super(DATA_NAME);
  }

  public ExtendedDaysSavedData(String s) {

    super(s);
  }

  @Override
  public void readFromNBT(NBTTagCompound nbt) {

    ExtendedDays.logHelper.info("ExtendedDaysSavedData#readFromNBT");
    extendedTime = nbt.getInteger(NBT_EXTENDED_TIME);
    worldTime = nbt.getInteger(NBT_WORLD_TIME);
  }

  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

    ExtendedDays.logHelper.info("ExtendedDaysSavedData#writeToNBT");
    nbt.setInteger(NBT_EXTENDED_TIME, extendedTime);
    nbt.setInteger(NBT_WORLD_TIME, worldTime);
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
