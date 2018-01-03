package net.silentchaos512.extendeddays.client.gui.config;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.silentchaos512.extendeddays.ExtendedDays;
import net.silentchaos512.extendeddays.config.Config;

public class GuiConfigExtendedDays extends GuiConfig {

  public GuiConfigExtendedDays(GuiScreen parent) {

    super(parent, Config.INSTANCE.getConfigElements(), ExtendedDays.MOD_ID, false, false,
        "Extended Days Config");
  }
}
