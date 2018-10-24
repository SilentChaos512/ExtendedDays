package net.silentchaos512.extendeddays.config;

import net.minecraftforge.common.config.Configuration;
import net.silentchaos512.extendeddays.ExtendedDays;
import net.silentchaos512.extendeddays.client.gui.ClockHud;
import net.silentchaos512.extendeddays.event.TimeEvents;
import net.silentchaos512.lib.config.ConfigBaseNew;
import net.silentchaos512.lib.config.ConfigMultiValueLineParser;
import net.silentchaos512.lib.config.ConfigOption;
import net.silentchaos512.lib.util.I18nHelper;
import net.silentchaos512.lib.util.LogHelper;

public final class Config extends ConfigBaseNew {
    private static final String CAT_MAIN = "main";
    private static final String CAT_CLIENT = CAT_MAIN + Configuration.CATEGORY_SPLITTER + "client";
    private static final String CAT_CLOCK = CAT_CLIENT + Configuration.CATEGORY_SPLITTER + "clock_hud";
    private static final String CAT_COMPAT = CAT_MAIN + Configuration.CATEGORY_SPLITTER + "compatibility";
    private static final String CAT_DEBUG = CAT_MAIN + Configuration.CATEGORY_SPLITTER + "debug";
    private static final String CAT_NETWORK = CAT_MAIN + Configuration.CATEGORY_SPLITTER + "network";
    private static final String CAT_SKY = CAT_CLIENT + Configuration.CATEGORY_SPLITTER + "sky";
    private static final String CAT_TIME = CAT_MAIN + Configuration.CATEGORY_SPLITTER + "time";

    @ConfigOption(name = "Enabled", category = CAT_CLOCK)
    @ConfigOption.BooleanDefault(true)
    @ConfigOption.Comment("If set to false, the clock will not render under any circumstances.")
    public static boolean clockEnabled;
    @ConfigOption(name = "Position X", category = CAT_CLOCK)
    @ConfigOption.RangeInt(5)
    @ConfigOption.Comment("Sets the position of the clock HUD element. Entering negative numbers will anchor the clock to the right/bottom of the screen.")
    public static int clockPosX;
    @ConfigOption(name = "Position Y", category = CAT_CLOCK)
    @ConfigOption.RangeInt(5)
    @ConfigOption.Comment("Sets the position of the clock HUD element. Entering negative numbers will anchor the clock to the right/bottom of the screen.")
    public static int clockPosY;
    @ConfigOption(name = "Show Always", category = CAT_CLOCK)
    @ConfigOption.BooleanDefault(false)
    @ConfigOption.Comment("If enabled, the clock HUD will show at all times. Otherwise, you must either be above ground, able to see the sky, or have a watch.")
    public static boolean clockShowAlways;
    public static ClockHud.TextPosition clockTextPosition;
    @ConfigOption(name = "Debug Mode Enabled", category = CAT_DEBUG)
    @ConfigOption.BooleanDefault(false)
    @ConfigOption.Comment("When enabled, additional information may be logged or displayed on screen.")
    public static boolean debugMode;
    @ConfigOption(name = "Morpheus Support", category = CAT_COMPAT)
    @ConfigOption.BooleanDefault(true)
    @ConfigOption.Comment("Override the Morpheus new day handler to allow time to advance correctly.")
    public static boolean morpheusOverride;
    @ConfigOption(name = "Packet Delay", category = CAT_NETWORK)
    @ConfigOption.RangeInt(value = 20, min = 1)
    @ConfigOption.Comment("The delay (in ticks) between sync packets being sent to the client.")
    public static int packetDelay;
    @ConfigOption(name = "Override Sky Rendering", category = CAT_SKY)
    @ConfigOption.BooleanDefault(true)
    @ConfigOption.Comment("Override sky rendering. Without this, the sun/moon will likely freeze during extended periods. Disable if this feature conflicts with another mod.")
    public static boolean skyOverride;
    @ConfigOption(name = "Show Time With Pocket Watch", category = CAT_CLOCK)
    @ConfigOption.BooleanDefault(true)
    @ConfigOption.Comment("Displays the exact time when the player has a pocket watch in their inventory.")
    public static boolean watchShowTime;
    @ConfigOption(name = "Use 12-Hour Clock", category = CAT_CLOCK)
    @ConfigOption.BooleanDefault(false)
    @ConfigOption.Comment("Display time with a 12-hour clock (AM/PM instead of 24-hour).")
    public static boolean watchUse12Hour;

    private static final String[] DEFAULT_EXTENDED_PERIODS = new String[]{"6000 30", "18000 10"};

    private static final String COMMENT_EXTENDED_PERIODS = "Sets the times of day/night that will be"
            + " \"extended\". Each line contains two values separated by a space. The first is the time"
            + " of the day to add the period (in ticks, whole number between 0 and 23999, same as the"
            + " numbers you would use in the \"/time set\" command). The second is the number of minutes"
            + " to add (real minutes, not ticks! You can use non-whole numbers if you want to).";

    public static final Config INSTANCE = new Config();

    private Config() {
        super(ExtendedDays.MOD_ID);
    }

    @Override
    public void load() {
        try {
            super.load();

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

            clockTextPosition = loadEnum("Text Position", CAT_CLOCK, ClockHud.TextPosition.class, ClockHud.TextPosition.AUTO,
                    "Positioning of time text, relative to the clock bar. AUTO will position it either LEFT or RIGHT, depending on where the clock bar is anchored.");
        } catch (Exception ex) {
            ExtendedDays.logHelper.fatal("Could not load configuration file!");
            ex.printStackTrace();
        }
    }

    @Override
    public I18nHelper i18n() {
        return ExtendedDays.i18n;
    }

    @Override
    public LogHelper log() {
        return ExtendedDays.logHelper;
    }
}
