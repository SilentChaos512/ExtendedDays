package net.silentchaos512.extendeddays.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemClock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.silentchaos512.extendeddays.client.render.SkyRenderer;
import net.silentchaos512.extendeddays.config.Config;
import net.silentchaos512.extendeddays.item.ItemPocketWatch;
import net.silentchaos512.lib.util.ChatHelper;
import net.silentchaos512.lib.util.PlayerHelper;
import net.silentchaos512.lib.util.TimeHelper;

public class ClientEvents {

  private static final int PLAYER_UPDATE_FREQUENCY = TimeHelper.ticksFromSeconds(5);

  public static boolean playerCanSeeSky = true;
  public static boolean playerHasVanillaClock = true;
  public static boolean playerHasPocketWatch = true;
  public static long worldTime = 0L;

  public static String debugText = "";

  @SubscribeEvent
  public void onRenderDebugText(RenderGameOverlayEvent.Post event) {

    if (!Config.DEBUG_MODE || event.getType() != ElementType.TEXT
        || Minecraft.getMinecraft().gameSettings.showDebugInfo) {
      return;
    }

    GlStateManager.pushMatrix();
    float scale = 0.75f;
    GlStateManager.scale(scale, scale, 1.0f);
    int y = 25;
    FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
    for (String line : debugText.split("\\n")) {
      fontRenderer.drawStringWithShadow(line, 5, y, 0xFFFFFF);
      y += 10;
    }
    GlStateManager.popMatrix();
  }

  private static final int RENDERER_ERROR_REPORT_DELAY = TimeHelper.ticksFromSeconds(30);
  private int ticksUnableToReplaceRenderer = 0;
  private boolean reportedUnableToReplaceRenderer = false;

  @SubscribeEvent
  public void onClientPlayerTick(PlayerTickEvent event) {

    if (event.phase != Phase.START)
      return;

    int extendedTime = TimeEvents.INSTANCE.getExtendedTime();

    // Make sure world time is correct.
    if (event.player.world.provider.getDimension() == 0) {
      // Setting the world time incorrectly here can change the actual world time?
      if (extendedTime > 0 && worldTime > 0) {
        // ExtendedDays.logHelper.info("Client set world time to " + worldTime);
        event.player.world.setWorldTime(worldTime);
      }
    }

    /*
     * Replace the sky renderer
     */
    if (Config.SKY_OVERRIDE) {
      Minecraft mc = Minecraft.getMinecraft();
      // Do some null checks. If unable to replace for an extended period, report it to the player.
      if (mc == null || mc.world == null || mc.world.provider == null) {
        ++ticksUnableToReplaceRenderer;
        if (!reportedUnableToReplaceRenderer
            && ticksUnableToReplaceRenderer > RENDERER_ERROR_REPORT_DELAY) {
          ChatHelper.sendMessage(event.player, "Extended Days was unable to replace sky renderer!");
          reportedUnableToReplaceRenderer = true;
        }
        return;
      }
      WorldProvider provider = mc.world.provider;
      if (provider.getDimension() == 0 && !(provider.getSkyRenderer() instanceof SkyRenderer)) {
        provider.setSkyRenderer(new SkyRenderer());
        ticksUnableToReplaceRenderer = 0;
      }
    }

    // If playing on a dedicated server, we should update time here?
    if (!Minecraft.getMinecraft().isSingleplayer()) {
      if (extendedTime > 0) {
        TimeEvents.INSTANCE.setExtendedTime(extendedTime - 1);
      }
    }

    // Update debug text overlay
    World world = event.player.world;
    ClientEvents.debugText = "Time (MC, Ext): " + world.getWorldTime() + ", "
        + TimeEvents.INSTANCE.getExtendedTime()
        + (TimeEvents.INSTANCE.isInExtendedPeriod(world) ? " (E)" : "") + "\n" + "Actual Time: "
        + TimeEvents.INSTANCE.getCurrentTime(world) + " / "
        + TimeEvents.INSTANCE.getTotalDayLength() + "\n" + "Day/Night Length: "
        + TimeEvents.INSTANCE.getDaytimeLength() + ", " + TimeEvents.INSTANCE.getNighttimeLength()
        + "\n\nClientEvents#worldTime: " + worldTime;

    // We don't want to check for sky visibility or watches every tick.
    if (event.player.ticksExisted % PLAYER_UPDATE_FREQUENCY != 0)
      return;

    // Check if player has a clear sky overhead.
    playerCanSeeSky = checkCanPlayerSeeSky(event.player);
    // Check if player has a pocket watch or vanilla clock.
    checkPlayerHasWatchOrClock(event.player);
  }

  /**
   * Determines if the player can "see the sky". Basically just ray traces for solid blocks.
   *
   * @param player
   * @return
   */
  private boolean checkCanPlayerSeeSky(EntityPlayer player) {

    if (player == null)
      return true;
    RayTraceResult raytrace = player.world.rayTraceBlocks(player.getPositionEyes(0f),
        player.getPositionEyes(0f).add(new Vec3d(0, 256 - player.posY, 0)), false, true, false);
    return raytrace == null;
  }

  private void checkPlayerHasWatchOrClock(EntityPlayer player) {

    playerHasVanillaClock = playerHasPocketWatch = false;

    for (ItemStack stack : PlayerHelper.getNonEmptyStacks(player, true, true, false)) {
      if (stack.getItem() instanceof ItemClock) {
        playerHasVanillaClock = true;
        // Keep looking, player may have a pocket watch as well.
      } else if (stack.getItem() instanceof ItemPocketWatch) {
        playerHasPocketWatch = true;
        // Stop looking, pocket watch is best time-telling device!
        return;
      }
    }
  }
}
