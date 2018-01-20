package net.silentchaos512.extendeddays.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.IRenderHandler;
import net.silentchaos512.extendeddays.event.TimeEvents;

public class SkyRenderer extends IRenderHandler {

  @Override
  public void render(float partialTicks, WorldClient world, Minecraft mc) {

    /*
     * Copied from RenderGlobal#renderSky(float, int). Basically just a straight copy. I used access transformers to
     * make the private fields of RenderGlobal I need public.
     * 
     * It might be fun to try messing around with this later, try making sky rendering a bit more realistic (lunar
     * cycles, for example). For now, it renders just like vanilla... although with no 3D anaglyph support.
     */

    GlStateManager.disableTexture2D();
    Vec3d vec3d = world.getSkyColor(mc.getRenderViewEntity(), partialTicks);
    float f = (float) vec3d.x;
    float f1 = (float) vec3d.y;
    float f2 = (float) vec3d.z;

    GlStateManager.color(f, f1, f2);
    Tessellator tessellator = Tessellator.getInstance();
    VertexBuffer bufferbuilder = tessellator.getBuffer();
    GlStateManager.depthMask(false);
    GlStateManager.enableFog();
    GlStateManager.color(f, f1, f2);

    if (mc.renderGlobal.vboEnabled) {
      mc.renderGlobal.skyVBO.bindBuffer();
      GlStateManager.glEnableClientState(32884);
      GlStateManager.glVertexPointer(3, 5126, 12, 0);
      mc.renderGlobal.skyVBO.drawArrays(7);
      mc.renderGlobal.skyVBO.unbindBuffer();
      GlStateManager.glDisableClientState(32884);
    } else {
      GlStateManager.callList(mc.renderGlobal.glSkyList);
    }

    GlStateManager.disableFog();
    GlStateManager.disableAlpha();
    GlStateManager.enableBlend();
    GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
        GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
        GlStateManager.DestFactor.ZERO);
    RenderHelper.disableStandardItemLighting();
    float[] afloat = world.provider.calcSunriseSunsetColors(getCelestialAngle(world, partialTicks),
        partialTicks);

    if (afloat != null) {
      GlStateManager.disableTexture2D();
      GlStateManager.shadeModel(7425);
      GlStateManager.pushMatrix();
      GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(
          MathHelper.sin(getCelestialAngleRadians(world, partialTicks)) < 0.0F ? 180.0F : 0.0F,
          0.0F, 0.0F, 1.0F);
      GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
      float f6 = afloat[0];
      float f7 = afloat[1];
      float f8 = afloat[2];

      bufferbuilder.begin(6, DefaultVertexFormats.POSITION_COLOR);
      bufferbuilder.pos(0.0D, 100.0D, 0.0D).color(f6, f7, f8, afloat[3]).endVertex();
      int j = 16;

      for (int l = 0; l <= 16; ++l) {
        float f21 = (float) l * ((float) Math.PI * 2F) / 16.0F;
        float f12 = MathHelper.sin(f21);
        float f13 = MathHelper.cos(f21);
        bufferbuilder
            .pos((double) (f12 * 120.0F), (double) (f13 * 120.0F),
                (double) (-f13 * 40.0F * afloat[3]))
            .color(afloat[0], afloat[1], afloat[2], 0.0F).endVertex();
      }

      tessellator.draw();
      GlStateManager.popMatrix();
      GlStateManager.shadeModel(7424);
    }

    GlStateManager.enableTexture2D();
    GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
        GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE,
        GlStateManager.DestFactor.ZERO);
    GlStateManager.pushMatrix();
    float f16 = 1.0F - world.getRainStrength(partialTicks);
    GlStateManager.color(1.0F, 1.0F, 1.0F, f16);
    GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
    GlStateManager.rotate(getCelestialAngle(world, partialTicks) * 360.0F, 1.0F, 0.0F, 0.0F);
    float f17 = 30.0F;
    mc.renderEngine.bindTexture(RenderGlobal.SUN_TEXTURES);
    bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
    bufferbuilder.pos((double) (-f17), 100.0D, (double) (-f17)).tex(0.0D, 0.0D).endVertex();
    bufferbuilder.pos((double) f17, 100.0D, (double) (-f17)).tex(1.0D, 0.0D).endVertex();
    bufferbuilder.pos((double) f17, 100.0D, (double) f17).tex(1.0D, 1.0D).endVertex();
    bufferbuilder.pos((double) (-f17), 100.0D, (double) f17).tex(0.0D, 1.0D).endVertex();
    tessellator.draw();
    f17 = 20.0F;
    mc.renderEngine.bindTexture(RenderGlobal.MOON_PHASES_TEXTURES);
    int i = world.getMoonPhase();
    int k = i % 4;
    int i1 = i / 4 % 2;
    float f22 = (float) (k + 0) / 4.0F;
    float f23 = (float) (i1 + 0) / 2.0F;
    float f24 = (float) (k + 1) / 4.0F;
    float f14 = (float) (i1 + 1) / 2.0F;
    bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
    bufferbuilder.pos((double) (-f17), -100.0D, (double) f17).tex((double) f24, (double) f14)
        .endVertex();
    bufferbuilder.pos((double) f17, -100.0D, (double) f17).tex((double) f22, (double) f14)
        .endVertex();
    bufferbuilder.pos((double) f17, -100.0D, (double) (-f17)).tex((double) f22, (double) f23)
        .endVertex();
    bufferbuilder.pos((double) (-f17), -100.0D, (double) (-f17)).tex((double) f24, (double) f23)
        .endVertex();
    tessellator.draw();
    GlStateManager.disableTexture2D();
    float f15 = world.getStarBrightness(partialTicks) * f16;

    if (f15 > 0.0F) {
      GlStateManager.color(f15, f15, f15, f15);

      if (mc.renderGlobal.vboEnabled) {
        mc.renderGlobal.starVBO.bindBuffer();
        GlStateManager.glEnableClientState(32884);
        GlStateManager.glVertexPointer(3, 5126, 12, 0);
        mc.renderGlobal.starVBO.drawArrays(7);
        mc.renderGlobal.starVBO.unbindBuffer();
        GlStateManager.glDisableClientState(32884);
      } else {
        GlStateManager.callList(mc.renderGlobal.starGLCallList);
      }
    }

    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    GlStateManager.disableBlend();
    GlStateManager.enableAlpha();
    GlStateManager.enableFog();
    GlStateManager.popMatrix();
    GlStateManager.disableTexture2D();
    GlStateManager.color(0.0F, 0.0F, 0.0F);
    double d0 = mc.player.getPositionEyes(partialTicks).y - world.getHorizon();

    if (d0 < 0.0D) {
      GlStateManager.pushMatrix();
      GlStateManager.translate(0.0F, 12.0F, 0.0F);

      if (mc.renderGlobal.vboEnabled) {
        mc.renderGlobal.sky2VBO.bindBuffer();
        GlStateManager.glEnableClientState(32884);
        GlStateManager.glVertexPointer(3, 5126, 12, 0);
        mc.renderGlobal.sky2VBO.drawArrays(7);
        mc.renderGlobal.sky2VBO.unbindBuffer();
        GlStateManager.glDisableClientState(32884);
      } else {
        GlStateManager.callList(mc.renderGlobal.glSkyList2);
      }

      GlStateManager.popMatrix();
      float f18 = 1.0F;
      float f19 = -((float) (d0 + 65.0D));
      float f20 = -1.0F;
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
      bufferbuilder.pos(-1.0D, (double) f19, 1.0D).color(0, 0, 0, 255).endVertex();
      bufferbuilder.pos(1.0D, (double) f19, 1.0D).color(0, 0, 0, 255).endVertex();
      bufferbuilder.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
      bufferbuilder.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
      bufferbuilder.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
      bufferbuilder.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
      bufferbuilder.pos(1.0D, (double) f19, -1.0D).color(0, 0, 0, 255).endVertex();
      bufferbuilder.pos(-1.0D, (double) f19, -1.0D).color(0, 0, 0, 255).endVertex();
      bufferbuilder.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
      bufferbuilder.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
      bufferbuilder.pos(1.0D, (double) f19, 1.0D).color(0, 0, 0, 255).endVertex();
      bufferbuilder.pos(1.0D, (double) f19, -1.0D).color(0, 0, 0, 255).endVertex();
      bufferbuilder.pos(-1.0D, (double) f19, -1.0D).color(0, 0, 0, 255).endVertex();
      bufferbuilder.pos(-1.0D, (double) f19, 1.0D).color(0, 0, 0, 255).endVertex();
      bufferbuilder.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
      bufferbuilder.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
      bufferbuilder.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
      bufferbuilder.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
      bufferbuilder.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
      bufferbuilder.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
      tessellator.draw();
    }

    if (world.provider.isSkyColored()) {
      GlStateManager.color(f * 0.2F + 0.04F, f1 * 0.2F + 0.04F, f2 * 0.6F + 0.1F);
    } else {
      GlStateManager.color(f, f1, f2);
    }

    GlStateManager.pushMatrix();
    GlStateManager.translate(0.0F, -((float) (d0 - 16.0D)), 0.0F);
    GlStateManager.callList(mc.renderGlobal.glSkyList2);
    GlStateManager.popMatrix();
    GlStateManager.enableTexture2D();
    GlStateManager.depthMask(true);
  }

  private float getCelestialAngleRadians(World world, float partialTicks) {

    return getCelestialAngle(world, partialTicks) * ((float) Math.PI * 2f);
  }

  private float getCelestialAngle(World world, float partialTicks) {

    double adjustedTime = TimeEvents.INSTANCE.getCelestialAdjustedTime(world);
    return world.provider.calculateCelestialAngle((long) adjustedTime, partialTicks);
  }
}
