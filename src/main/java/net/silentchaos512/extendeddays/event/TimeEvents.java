package net.silentchaos512.extendeddays.event;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.extendeddays.ExtendedDays;
import net.silentchaos512.extendeddays.config.Config;
import net.silentchaos512.extendeddays.network.MessageSetTime;
import net.silentchaos512.extendeddays.network.MessageSyncTime;
import net.silentchaos512.extendeddays.world.ExtendedDaysSavedData;
import net.silentchaos512.lib.util.LogHelper;
import net.silentchaos512.lib.util.TimeHelper;

public class TimeEvents {

  public static final TimeEvents INSTANCE = new TimeEvents();
  public static Map<Integer, Float> extendedPeriods = new HashMap<>();

  private int extendedTime = 0;

  @SubscribeEvent
  public void onWorldTick(WorldTickEvent event) {

    // Overworld only right now.
    if (event.phase != Phase.START || event.world.provider.getDimension() != 0)
      return;

    LogHelper log = ExtendedDays.logHelper;

    int worldTime = (int) (event.world.getWorldTime() % 24000);

    ExtendedDaysSavedData data = ExtendedDaysSavedData.get(event.world);
    if (data == null)
      return;

    // log.debug(worldTime, data.worldTime, extendedTime, data.extendedTime);

    if (data != null && data.extendedTime > 0) {
      startExtendedPeriod(event.world, data.extendedTime);
      // Make sure world time is correct.
      if (worldTime > data.worldTime && worldTime < data.worldTime + 600) {
        if (extendedPeriods.containsKey(data.worldTime) && extendedTime > 0 && data.worldTime > 0) {
          worldTime = data.worldTime;
          event.world.setWorldTime(worldTime);
        }
      }
    }

    // We are on extended time.
    if (extendedTime > 0) {
      --extendedTime;
      // Extended period ended.
      if (extendedTime <= 0) {
        endExtendedPeriod(event.world);
      }
      // Or has the time changed?
      if (worldTime != data.worldTime) {
        endExtendedPeriod(event.world);
      }
    } else {
      // Not on extended time currently. Is it time to start?
      for (Entry<Integer, Float> entry : extendedPeriods.entrySet()) {
        if (worldTime == entry.getKey()) {
          // Start a new extended time period.
          int ticks = TimeHelper.ticksFromMinutes(entry.getValue());
          if (ticks > 0) {
            // Extend time (positive values)
            startExtendedPeriod(event.world, ticks);
          } else {
            // Shorten time (negative values)
            event.world.setWorldTime(worldTime - ticks);
          }
        }
      }
    }

    // Send packet to client?
    if (event.world.getTotalWorldTime() % Config.PACKET_DELAY == 0) {
      long time = event.world.getWorldTime();
      ExtendedDays.network.wrapper.sendToAll(new MessageSyncTime(time, extendedTime));
    }

    // Update world save data.
    if (data != null) {
      data.extendedTime = extendedTime;
      data.worldTime = worldTime;
      data.markDirty();
    }
  }

  @SubscribeEvent
  public void onPlayerWakeUp(PlayerWakeUpEvent event) {

    /*
     * Minecraft will not advance through the night if doDaylightCycle is false. We need to fix the time ourselves in
     * this case.
     * 
     * PlayerWakeUpEvent occurs only on the client-side, so we need to send a packet to the server. The packet will call
     * setTimeFromPacket on the server, ending the extended period and advancing to daytime.
     */

    World world = event.getEntityPlayer().world;
    if (isInExtendedPeriod(world)) {
      endExtendedPeriod(world);
      // long time = world.getWorldTime() + 24000L;
      // time = time - time % 24000L;
      // world.setWorldTime(time);
      // ExtendedDays.network.wrapper.sendToServer(new MessageSetTime(time, 0));
    }
  }

  public void startExtendedPeriod(World world, int timeInTicks) {

    setExtendedTime(timeInTicks);
    ExtendedDaysSavedData data = ExtendedDaysSavedData.get(world);
    if (data != null) {
      data.extendedTime = timeInTicks;
      data.markDirty();
    }
    // world.getGameRules().setOrCreateGameRule("doDaylightCycle", "false");
    ExtendedDays.network.wrapper.sendToAll(new MessageSyncTime(world.getWorldTime(), extendedTime));
  }

  public void endExtendedPeriod(World world) {

    ExtendedDays.logHelper.debug("endExtendedPeriod");
    setExtendedTime(0);
    ExtendedDaysSavedData data = ExtendedDaysSavedData.get(world);
    if (data != null) {
      data.extendedTime = 0;
      data.markDirty();
    }
    // world.getGameRules().setOrCreateGameRule("doDaylightCycle", "true");
    // ExtendedDays.network.wrapper.sendToAll(new MessageSyncTime(extendedTime));
  }

  public boolean isInExtendedPeriod(World world) {

    return extendedTime > 0;
  }

  public int getExtendedTime() {

    return extendedTime;
  }

  protected void setExtendedTime(int value) {

    extendedTime = value;
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
      /*
       * We divide the times in the conditions below because the actual world time bounces around a bit, so we need to
       * make the comparisons less precise. A bit messy, but I just want a solution right now. 10 ticks seems to work
       * well for me, but I'm not sure how laggy worlds will handle this.
       */
      int k = 10;
      // Extended period passed?
      if (worldTime / k > entry.getKey() / k) {
        result += ticksFromMinutes;
      }
      // Currently in extended period?
      if (worldTime / k == entry.getKey() / k) {
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

  public int getDaytimeLength() {

    int result = 12000;
    for (Entry<Integer, Float> entry : extendedPeriods.entrySet()) {
      if (entry.getKey() < 12000) {
        result += TimeHelper.ticksFromMinutes(entry.getValue());
      }
    }
    return result;
  }

  public int getNighttimeLength() {

    int result = 12000;
    for (Entry<Integer, Float> entry : extendedPeriods.entrySet()) {
      if (entry.getKey() >= 12000) {
        result += TimeHelper.ticksFromMinutes(entry.getValue());
      }
    }
    return result;
  }

  public void setActualTime(World world, long time) {

    // TODO
  }

  public void setTimeFromPacket(MessageSetTime msg) {

    if (msg.worldTime <= 0 || msg.extendedTime <= 0)
      return;

    MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
    if (server == null)
      return;
    World world = server.worlds[0];
    if (world == null)
      return;
    setTime(world, msg.worldTime, msg.extendedTime);
  }

  public void setTime(World world, long worldTime, int extendedTime) {

    this.extendedTime = extendedTime;
    world.setWorldTime(worldTime);
    ExtendedDaysSavedData data = ExtendedDaysSavedData.get(world);
    if (data != null) {
      data.worldTime = (int) (worldTime % 24000);
      data.extendedTime = extendedTime;
    }
    ExtendedDays.network.wrapper.sendToAll(new MessageSyncTime(worldTime, extendedTime));
  }

  @SideOnly(Side.CLIENT)
  public void syncTimeFromPacket(MessageSyncTime msg) {

    if (msg.worldTime <= 0 || msg.extendedTime <= 0)
      return;

    Minecraft.getMinecraft().player.world.setWorldTime(msg.worldTime);
    this.extendedTime = msg.extendedTime;
    ClientEvents.worldTime = msg.worldTime;
  }

  public double getAdjustedWorldTime(World world) {

    return 24000.0 * getCurrentTime(world) / getTotalDayLength();
  }

  public double getCelestialAdjustedTime(World world) {

    int currentTime = getCurrentTime(world);
    if (currentTime >= getDaytimeLength()) {
      int nighttime = currentTime - getDaytimeLength();
      return 12000.0 * nighttime / getNighttimeLength() + 12000;
    } else {
      return 12000.0 * currentTime / getDaytimeLength();
    }
  }
}
