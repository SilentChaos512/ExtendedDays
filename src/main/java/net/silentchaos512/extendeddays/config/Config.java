package net.silentchaos512.extendeddays.config;

import net.minecraftforge.common.config.Configuration;
import net.silentchaos512.extendeddays.ExtendedDays;
import net.silentchaos512.extendeddays.event.TimeEvents;
import net.silentchaos512.lib.config.ConfigBase;
import net.silentchaos512.lib.config.ConfigMultiValueLineParser;

public class Config extends ConfigBase {

  private static final String[] DEFAULT_EXTENDED_PERIODS = new String[] { "6000 30", "18000 10" };
  private static final String COMMENT_EXTENDED_PERIODS = "Sets the times of day/night that will be"
      + " \"extended\". Each line contains two values separated by a space. The first is the time"
      + " of the day to add the period (in ticks, whole number between 0 and 23999, same as the"
      + " numbers you would use in the \"/time set\" command). The second is the number of minutes"
      + " to add (real minutes, not ticks! You can use non-whole numbers if you want to).";

  static final String split = Configuration.CATEGORY_SPLITTER;
  public static final String CAT_MAIN = "main";
  public static final String CAT_CLIENT = CAT_MAIN + split + "client";
  public static final String CAT_TIME = CAT_MAIN + split + "time";

  public static final Config INSTANCE = new Config();

  public Config() {

    super(ExtendedDays.MOD_ID);
  }

  @Override
  public void load() {

    try {
      ConfigMultiValueLineParser parser;

      // Extended time periods
      parser = new ConfigMultiValueLineParser("Extended Periods", ExtendedDays.logHelper, "\\s",
          Integer.class, Float.class);
      TimeEvents.extendedPeriods.clear();
      for (String str : config.getStringList("Extended Periods", CAT_TIME, DEFAULT_EXTENDED_PERIODS,
          COMMENT_EXTENDED_PERIODS)) {
        Object[] values = parser.parse(str);
        if (values != null) {
          TimeEvents.extendedPeriods.put((int) values[0], (float) values[1]);
        }
      }
    } catch (Exception ex) {
      ExtendedDays.logHelper.severe("Could not load configuration file!");
      ex.printStackTrace();
    }
  }
}
