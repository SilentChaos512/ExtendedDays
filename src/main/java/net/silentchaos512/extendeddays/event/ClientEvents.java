package net.silentchaos512.extendeddays.event;

import net.silentchaos512.lib.util.TimeUtils;

public class ClientEvents {
    private static final int PLAYER_UPDATE_FREQUENCY = TimeUtils.ticksFromSeconds(5);

    public static boolean playerCanSeeSky = true;
    public static boolean playerHasVanillaClock = true;
    public static boolean playerHasPocketWatch = true;
    public static long worldTime = 0L;

    private static final int RENDERER_ERROR_REPORT_DELAY = TimeUtils.ticksFromSeconds(30);
    private int ticksUnableToReplaceRenderer = 0;
    private boolean reportedUnableToReplaceRenderer = false;
}
