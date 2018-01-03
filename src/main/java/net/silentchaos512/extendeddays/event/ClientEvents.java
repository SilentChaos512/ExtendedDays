package net.silentchaos512.extendeddays.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientEvents {

  public static String debugText = "";

  @SubscribeEvent
  public void onRenderGameOverlay(RenderGameOverlayEvent event) {

    if (event.getType() != ElementType.TEXT)
      return;

    GlStateManager.pushMatrix();
    float scale = 1.0f;
    GlStateManager.scale(scale, scale, 1.0f);
    int y = 5;
    FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
    for (String line : debugText.split("\\n")) {
      fontRenderer.drawStringWithShadow(line, 5, y, 0xFFFFFF);
      y += 10;
    }
    GlStateManager.popMatrix();
  }
}
