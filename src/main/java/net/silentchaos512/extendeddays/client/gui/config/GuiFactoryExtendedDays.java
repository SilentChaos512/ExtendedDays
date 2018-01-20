package net.silentchaos512.extendeddays.client.gui.config;

import net.minecraft.client.gui.GuiScreen;
import net.silentchaos512.lib.gui.config.GuiFactorySL;

public class GuiFactoryExtendedDays extends GuiFactorySL {

  @Override
  public Class<? extends GuiScreen> mainConfigGuiClass() {

    return GuiConfigExtendedDays.class;
  }

  @Override
  public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {

    // TODO Auto-generated method stub
    return null;
  }

}
