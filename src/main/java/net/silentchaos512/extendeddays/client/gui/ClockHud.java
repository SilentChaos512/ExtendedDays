package net.silentchaos512.extendeddays.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.silentchaos512.extendeddays.ExtendedDays;
import net.silentchaos512.extendeddays.config.Config;
import net.silentchaos512.extendeddays.event.ClientEvents;
import net.silentchaos512.extendeddays.event.TimeEvents;
import net.silentchaos512.lib.util.InventoryHelper;

public class ClockHud extends Gui {

  public static final ClockHud INSTANCE = new ClockHud();

  public static final ResourceLocation TEXTURE = new ResourceLocation(ExtendedDays.MOD_ID,
      "textures/gui/hud.png");

  @SubscribeEvent
  public void onRenderOverlay(RenderGameOverlayEvent.Post event) {

    if (event.getType() != ElementType.TEXT)
      return;

    Minecraft mc = Minecraft.getMinecraft();
    EntityPlayer player = mc.player;
    World world = player.world;

    int width = event.getResolution().getScaledWidth();
    int height = event.getResolution().getScaledHeight();

    // TODO: Conditions to show clock
    // Should check if player can see the sky, but not every render tick!
    // Maybe every few seconds? Also consider a pocketwatch item (with Baubles
    // compat) that allows player to see time always.
    if (ClientEvents.playerHasPocketWatch) {
      renderClock(mc, world, width, height, true);
    }
    if (Config.CLOCK_SHOW_ALWAYS || ClientEvents.playerHasVanillaClock
        || ClientEvents.playerCanSeeSky) {
      renderClock(mc, world, width, height, false);
    }
  }

  public void renderClock(Minecraft mc, World world, int screenWidth, int screenHeight,
      boolean hasPocketWatch) {

    GlStateManager.enableBlend();
    GlStateManager.enableAlpha();

    mc.renderEngine.bindTexture(TEXTURE);

    int posX = Config.CLOCK_POS_X;
    if (posX < 0)
      posX = posX + screenWidth - 80;
    int posY = Config.CLOCK_POS_Y;
    if (posY < 0)
      posY = posY + screenHeight - 12;

    long worldTime = world.getWorldTime() % 24000;
    boolean isNight = worldTime > 12000;

    // Main bar
    int texX = 0;
    int texY = isNight ? 12 : 0;
    drawTexturedModalRect(posX, posY, texX, texY, 80, 12, 0xAAFFFFFF);

    // Extended period markers
    // TODO

    // Sun/Moon
    texX = 84;
    int dayLength = isNight ? TimeEvents.INSTANCE.getNighttimeLength()
        : TimeEvents.INSTANCE.getDaytimeLength();
    int currentTime = TimeEvents.INSTANCE.getCurrentTime(world);
    if (isNight)
      currentTime -= TimeEvents.INSTANCE.getDaytimeLength();
    int x = 2 + (int) (posX + 78 * ((float) currentTime) / dayLength) - 6;
    drawTexturedModalRect(x, posY, texX, texY, 12, 12, 0xCCFFFFFF);

    if (hasPocketWatch && Config.WATCH_SHOW_TIME) {
      currentTime = TimeEvents.INSTANCE.getCurrentTime(world);
      int totalDayLength = TimeEvents.INSTANCE.getTotalDayLength();
      int adjustedTime = (int) (24000L * currentTime / totalDayLength);
      int hour = adjustedTime / 1000 + 6;
      if (hour >= 24)
        hour -= 24;
      int minute = (int) (60 * (adjustedTime % 1000 / 1000f));

      // Adjust for 12-hour clock
      String suffix = "";
      if (Config.WATCH_USE_AM_PM) {
        suffix = "AM";
        if (hour > 12) {
          // Afternoon
          hour -= 12;
          suffix = "PM";
        }
        if (hour == 0) {
          // Midnight
          hour = 12;
        }
      }

      String str = String.format("%d:%02d %s", hour, minute, suffix);
      int strWidth = mc.fontRenderer.getStringWidth(str);
      int clockX = posX + (Config.CLOCK_POS_X < 0 ? -(strWidth + 5) : 85);
      int clockY = posY + mc.fontRenderer.FONT_HEIGHT / 2 - 2;
      mc.fontRenderer.drawStringWithShadow(str, clockX, clockY, 0xFFFFFF);
    }
  }

  protected void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width,
      int height, int color) {

    float a = ((color >> 24) & 255) / 255f;
    float r = ((color >> 16) & 255) / 255f;
    float g = ((color >> 8) & 255) / 255f;
    float b = (color & 255) / 255f;
    GlStateManager.color(r, g, b, a);
    drawTexturedModalRect(x, y, textureX, textureY, width, height);
    GlStateManager.color(1f, 1f, 1f, 1f);
  }
}
