package net.silentchaos512.extendeddays.config;

import net.minecraftforge.common.config.Configuration;
import net.silentchaos512.extendeddays.ExtendedDays;
import net.silentchaos512.extendeddays.event.TimeEvents;
import net.silentchaos512.lib.config.ConfigBase;
import net.silentchaos512.lib.config.ConfigMultiValueLineParser;

public class Config extends ConfigBase {

  public static int CLOCK_POS_X;
  public static int CLOCK_POS_Y;
  public static boolean CLOCK_SHOW_ALWAYS;
  public static boolean DEBUG_MODE;
  public static boolean MORPHEUS_OVERRIDE;
  public static int PACKET_DELAY;

  /*
   * Defaults
   */
  private static final int DEFAULT_CLOCK_POS_X = 5;
  private static final int DEFAULT_CLOCK_POS_Y = 5;
  private static final boolean DEFAULT_CLOCK_SHOW_ALWAYS = false;
  private static boolean DEFAULT_DEBUG_MODE = false;
  private static final String[] DEFAULT_EXTENDED_PERIODS = new String[] { "6000 30", "18000 10" };
  private static final boolean DEFAULT_MORPHEUS_OVERRIDE = true;
  private static final int DEFAULT_PACKET_DELAY = 20;

  /*
   * Comments
   */
  private static final String COMMENT_CLOCK_POS = "Sets the position of the clock HUD element."
      + " Entering negative numbers will anchor the clock to the right/bottom of the screen.";
  private static final String COMMENT_CLOCK_SHOW_ALWAYS = "If enabled, the clock HUD will show at"
      + " all times. Otherwise, you must either be above ground, able to see the sky, or have a"
      + " watch.";
  private static final String COMMENT_DEBUG_MODE = "When enabled, additional information may be"
      + " logged or displayed on screen.";
  private static final String COMMENT_EXTENDED_PERIODS = "Sets the times of day/night that will be"
      + " \"extended\". Each line contains two values separated by a space. The first is the time"
      + " of the day to add the period (in ticks, whole number between 0 and 23999, same as the"
      + " numbers you would use in the \"/time set\" command). The second is the number of minutes"
      + " to add (real minutes, not ticks! You can use non-whole numbers if you want to).";
  private static final String COMMENT_MORPHEUS_OVERRIDE = "Override the Morpheus new day handler"
      + " to allow time to advance correctly.";
  private static final String COMMENT_PACKET_DELAY = "The delay (in ticks) between sync packets"
      + " being sent to the client.";

  /*
   * Categories
   */
  static final String split = Configuration.CATEGORY_SPLITTER;
  public static final String CAT_MAIN = "main";
  public static final String CAT_CLIENT = CAT_MAIN + split + "client";
  public static final String CAT_CLOCK = CAT_CLIENT + split + "clock_hud";
  public static final String CAT_COMPAT = CAT_MAIN + split + "compatibility";
  public static final String CAT_DEBUG = CAT_MAIN + split + "debug";
  public static final String CAT_NETWORK = CAT_MAIN + split + "network";
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

      // Clock HUD
      CLOCK_POS_X = loadInt("Position X", CAT_CLOCK, DEFAULT_CLOCK_POS_X, COMMENT_CLOCK_POS);
      CLOCK_POS_Y = loadInt("Position Y", CAT_CLOCK, DEFAULT_CLOCK_POS_Y, COMMENT_CLOCK_POS);
      CLOCK_SHOW_ALWAYS = loadBoolean("Show Always", CAT_CLOCK, DEFAULT_CLOCK_SHOW_ALWAYS,
          COMMENT_CLOCK_SHOW_ALWAYS);

      // Network
      PACKET_DELAY = loadInt("Packet Delay", CAT_NETWORK, DEFAULT_PACKET_DELAY,
          COMMENT_PACKET_DELAY);

      // Compatibility
      MORPHEUS_OVERRIDE = loadBoolean("Morpheus Support", CAT_COMPAT, DEFAULT_MORPHEUS_OVERRIDE,
          COMMENT_MORPHEUS_OVERRIDE);

      // Debug
      DEBUG_MODE = loadBoolean("Debug Mode Enabled", CAT_DEBUG, DEFAULT_DEBUG_MODE,
          COMMENT_DEBUG_MODE);
    } catch (Exception ex) {
      ExtendedDays.logHelper.severe("Could not load configuration file!");
      ex.printStackTrace();
    }
  }
}
