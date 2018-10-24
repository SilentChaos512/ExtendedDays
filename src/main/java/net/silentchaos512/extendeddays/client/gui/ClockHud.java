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
import net.silentchaos512.extendeddays.compat.BloodmoonCompat;
import net.silentchaos512.extendeddays.config.Config;
import net.silentchaos512.extendeddays.event.ClientEvents;
import net.silentchaos512.extendeddays.event.TimeEvents;

public class ClockHud extends Gui {
    public static final ClockHud INSTANCE = new ClockHud();

    private static final ResourceLocation TEXTURE = new ResourceLocation(ExtendedDays.MOD_ID, "textures/gui/hud.png");
    private static final int CLOCK_BAR_WIDTH = 82;
    private static final int CLOCK_BAR_HEIGHT = 12;

    public enum TextPosition {
        AUTO, LEFT, RIGHT, TOP, BOTTOM
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (!Config.clockEnabled || event.getType() != ElementType.TEXT || mc.gameSettings.showDebugInfo) {
            return;
        }

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
        if (Config.clockShowAlways || ClientEvents.playerHasVanillaClock || ClientEvents.playerCanSeeSky) {
            renderClock(mc, world, width, height, false);
        }
    }

    private void renderClock(Minecraft mc, World world, int screenWidth, int screenHeight, boolean hasPocketWatch) {
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();

        mc.renderEngine.bindTexture(TEXTURE);

        int posX = Config.clockPosX;
        if (posX < 0)
            posX = posX + screenWidth - CLOCK_BAR_WIDTH;
        int posY = Config.clockPosY;
        if (posY < 0)
            posY = posY + screenHeight - CLOCK_BAR_HEIGHT;

        long worldTime = world.getWorldTime() % 24000;
        boolean isNight = worldTime > 12000;

        // Main bar
        int texX = 0;
        int texY = CLOCK_BAR_HEIGHT * (BloodmoonCompat.INSTANCE.isBloodmoonActive() ? 2 : isNight ? 1 : 0);
        drawTexturedModalRect(posX, posY, texX, texY, CLOCK_BAR_WIDTH, CLOCK_BAR_HEIGHT, 0xAAFFFFFF);

        // Extended period markers
        // TODO

        // Sun/Moon
        texX = 87;
        int dayLength = isNight ? TimeEvents.INSTANCE.getNighttimeLength(world)
                : TimeEvents.INSTANCE.getDaytimeLength(world);
        int currentTime = TimeEvents.INSTANCE.getCurrentTime(world);
        if (isNight)
            currentTime -= TimeEvents.INSTANCE.getDaytimeLength(world);
        int x = 2 + (int) (posX + 78 * ((float) currentTime) / dayLength) - 6;
        drawTexturedModalRect(x, posY, texX, texY, 12, 12, 0xCCFFFFFF);

        if (hasPocketWatch && Config.watchShowTime) {
            // currentTime = TimeEvents.INSTANCE.getCurrentTime(world);
            // int totalDayLength = TimeEvents.INSTANCE.getTotalDayLength();
            // int adjustedTime = (int) (24000L * currentTime / totalDayLength);
            currentTime = (int) TimeEvents.INSTANCE.getCelestialAdjustedTime(world);
            int adjustedTime = currentTime;
            int hour = adjustedTime / 1000 + 6;
            if (hour >= 24)
                hour -= 24;
            int minute = (int) (60 * (adjustedTime % 1000 / 1000f));

            // Adjust for 12-hour clock
            String suffix = "";
            if (Config.watchUse12Hour) {
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

            // TODO: Better formatting options, consider other locales?
            String str = String.format("%d:%02d %s", hour, minute, suffix);
            int strWidth = mc.fontRenderer.getStringWidth(str);
            int clockX = getTimeTextPosX(mc, posX, strWidth);
            int clockY = getTimeTextPosY(mc, posY, strWidth);
            mc.fontRenderer.drawStringWithShadow(str, clockX, clockY, 0xFFFFFF);
        }
    }

    private static int getTimeTextPosX(Minecraft mc, int baseX, int strWidth) {
        switch (Config.clockTextPosition) {
            case AUTO:
                return baseX + (Config.clockPosX < 0 ? -(strWidth + 5) : CLOCK_BAR_WIDTH);
            case LEFT:
                return baseX + -(strWidth + 5);
            case RIGHT:
                return baseX + CLOCK_BAR_WIDTH;
            case TOP:
            case BOTTOM:
                return baseX + CLOCK_BAR_WIDTH / 2 - strWidth / 2;
        }
        throw new IllegalStateException("Unknown clock text position: " + Config.clockTextPosition);
    }
    private static int getTimeTextPosY(Minecraft mc, int baseY, int strWidth) {
        switch (Config.clockTextPosition) {
            case AUTO:
            case LEFT:
            case RIGHT:
                return baseY + mc.fontRenderer.FONT_HEIGHT / 2 - 2;
            case TOP:
                return baseY - 10;
            case BOTTOM:
                return baseY + 13;
        }
        throw new IllegalStateException("Unknown clock text position: " + Config.clockTextPosition);
    }


    private void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height, int color) {
        float a = ((color >> 24) & 255) / 255f;
        float r = ((color >> 16) & 255) / 255f;
        float g = ((color >> 8) & 255) / 255f;
        float b = (color & 255) / 255f;
        GlStateManager.color(r, g, b, a);
        drawTexturedModalRect(x, y, textureX, textureY, width, height);
        GlStateManager.color(1f, 1f, 1f, 1f);
    }
}
