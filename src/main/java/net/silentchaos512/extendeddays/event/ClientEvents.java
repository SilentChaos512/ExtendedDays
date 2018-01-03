package net.silentchaos512.extendeddays.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.silentchaos512.extendeddays.config.Config;
import net.silentchaos512.lib.util.TimeHelper;

public class ClientEvents {

  private static final int PLAYER_UPDATE_FREQUENCY = TimeHelper.ticksFromSeconds(5);

  public static boolean playerCanSeeSky = true;

  public static String debugText = "";

  @SubscribeEvent
  public void onRenderDebugText(RenderGameOverlayEvent event) {

    if (!Config.DEBUG_MODE || event.getType() != ElementType.TEXT)
      return;

    GlStateManager.pushMatrix();
    float scale = 1.0f;
    GlStateManager.scale(scale, scale, 1.0f);
    int y = 25;
    FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
    for (String line : debugText.split("\\n")) {
      fontRenderer.drawStringWithShadow(line, 5, y, 0xFFFFFF);
      y += 10;
    }
    GlStateManager.popMatrix();
  }

  @SubscribeEvent
  public void onClientPlayerTick(PlayerTickEvent event) {

    if (event.phase != Phase.START || event.player.ticksExisted % PLAYER_UPDATE_FREQUENCY != 0)
      return;

    playerCanSeeSky = checkCanPlayerSeeSky(event.player);
  }

  /**
   * Determines if the player can "see the sky". Basically just ray traces for solid blocks.
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
}
