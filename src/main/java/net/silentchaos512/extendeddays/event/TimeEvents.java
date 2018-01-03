package net.silentchaos512.extendeddays.event;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.silentchaos512.extendeddays.ExtendedDays;
import net.silentchaos512.extendeddays.world.ExtendedDaysSavedData;
import net.silentchaos512.lib.util.TimeHelper;

public class TimeEvents {

  public static Map<Integer, Float> extendedPeriods = new HashMap<>();

  int extendedTime = 0;
  int extendedPeriodWorldTime = 0;

  @SubscribeEvent
  public void onWorldTick(WorldTickEvent event) {

    int worldTime = (int) (event.world.getWorldTime() % 24000);

    ExtendedDaysSavedData data = ExtendedDaysSavedData.get(event.world);
    if (data == null)
      return;
    if (data != null && data.extendedTime > 0) {
      extendedTime = data.extendedTime;
      // Make sure world time is correct.
//      ExtendedDays.logHelper.debug(worldTime, data.worldTime, data.extendedTime);
      if (worldTime > data.worldTime && worldTime < data.worldTime + 600) {
        if (extendedPeriods.containsKey(data.worldTime) && extendedTime > 0) {
          worldTime = data.worldTime;
          event.world.setWorldTime(worldTime);
        }
      }
    }

    if (event.phase != Phase.START || event.world.provider.getDimension() != 0)
      return;
    // logHelper.debug(event.world.getTotalWorldTime(), event.world);

    // We are on extended time.
    if (extendedTime > 0) {
      --extendedTime;
      // Extended period ended.
      if (extendedTime <= 0) {
        endExtendedPeriod(event.world);
      }
      // Or has the time changed?
      if (worldTime != extendedPeriodWorldTime) {
        endExtendedPeriod(event.world);
      }
    } else {
      // Not on extended time currently. Is it time to start?
      for (Entry<Integer, Float> entry : extendedPeriods.entrySet()) {
        if (worldTime == entry.getKey()) {
          // Start a new extended time period.
          startExtendedPeriod(event.world, TimeHelper.ticksFromMinutes(entry.getValue()));
          extendedPeriodWorldTime = worldTime;
        }
      }
    }

    if (data != null) {
      data.extendedTime = extendedTime;
      data.worldTime = worldTime;
      data.markDirty();
    }

    ClientEvents.debugText = event.world.getWorldTime() + ", " + extendedTime + "\n"
        + "Actual Time: " + getCurrentTime(event.world) + " / " + getTotalDayLength() + "\n";
  }

  private void startExtendedPeriod(World world, int timeInTicks) {

    extendedTime = timeInTicks;
    world.getGameRules().setOrCreateGameRule("doDaylightCycle", "false");
  }

  private void endExtendedPeriod(World world) {

    extendedTime = 0;
    world.getGameRules().setOrCreateGameRule("doDaylightCycle", "true");
  }

  /**
   * Gets the current time, adjusted to include extended periods.
   * 
   * @param world
   * @return
   */
  public int getCurrentTime(World world) {

    int result = (int) (world.getWorldTime() % 24000);
    int worldTime = result;
    for (Entry<Integer, Float> entry : extendedPeriods.entrySet()) {
      int ticksFromMinutes = TimeHelper.ticksFromMinutes(entry.getValue());
      // Extended period passed?
      if (worldTime > entry.getKey()) {
        result += ticksFromMinutes;
      }
      // Currently in extended period?
      if (worldTime == entry.getKey()) {
        result += ticksFromMinutes - extendedTime;
      }
    }
    return result;
  }

  /**
   * Gets the length of a day, adjusted to include extended periods.
   * 
   * @return
   */
  public int getTotalDayLength() {

    int result = 24000;
    for (float minutes : extendedPeriods.values()) {
      result += TimeHelper.ticksFromMinutes(minutes);
    }
    return result;
  }
}
