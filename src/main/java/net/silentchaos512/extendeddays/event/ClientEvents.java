package net.silentchaos512.extendeddays.event;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemClock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.silentchaos512.extendeddays.client.render.SkyRenderer;
import net.silentchaos512.extendeddays.config.Config;
import net.silentchaos512.extendeddays.init.ModItems;
import net.silentchaos512.lib.util.ChatHelper;
import net.silentchaos512.lib.util.PlayerHelper;
import net.silentchaos512.lib.util.TimeHelper;

public class ClientEvents {
    private static final int PLAYER_UPDATE_FREQUENCY = TimeHelper.ticksFromSeconds(5);

    public static boolean playerCanSeeSky = true;
    public static boolean playerHasVanillaClock = true;
    public static boolean playerHasPocketWatch = true;
    public static long worldTime = 0L;

    private static final int RENDERER_ERROR_REPORT_DELAY = TimeHelper.ticksFromSeconds(30);
    private int ticksUnableToReplaceRenderer = 0;
    private boolean reportedUnableToReplaceRenderer = false;

    @SubscribeEvent
    public void onClientPlayerTick(PlayerTickEvent event) {
        if (event.phase != Phase.START)
            return;

        int extendedTime = TimeEvents.INSTANCE.getExtendedTime();

        // Make sure world time is correct.
        final Minecraft mc = Minecraft.getMinecraft();
        final World world = mc.world;
        final boolean isOverworld = TimeEvents.isOverworld(world);

        if (isOverworld) {
            // Setting the world time incorrectly here can change the actual world time?
            if (extendedTime > 0 && worldTime > 0) {
//                 ExtendedDays.logHelper.info("Client set world time to " + worldTime);
                world.setWorldTime(worldTime);
            }
        }

        /*
         * Replace the sky renderer
         */
        if (Config.skyOverride && isOverworld) {
            // Do some null checks. If unable to replace for an extended period, report it to the player.
            if (world == null || world.provider == null) {
                ++ticksUnableToReplaceRenderer;
                if (!reportedUnableToReplaceRenderer
                        && ticksUnableToReplaceRenderer > RENDERER_ERROR_REPORT_DELAY) {
                    ChatHelper.sendMessage(event.player, "Extended Days was unable to replace sky renderer!");
                    reportedUnableToReplaceRenderer = true;
                }
                return;
            }
            WorldProvider provider = mc.world.provider;
            if (!(provider.getSkyRenderer() instanceof SkyRenderer)) {
                provider.setSkyRenderer(new SkyRenderer());
                ticksUnableToReplaceRenderer = 0;
            }
        }

        // If playing on a dedicated server, we should update time here?
        if (!Minecraft.getMinecraft().isSingleplayer() && isOverworld && extendedTime > 0) {
            TimeEvents.INSTANCE.setExtendedTime(extendedTime - 1);
        }

        // We don't want to check for sky visibility or watches every tick.
        if (event.player.ticksExisted % PLAYER_UPDATE_FREQUENCY == 0) {
            // Check if player has a clear sky overhead.
            playerCanSeeSky = checkCanPlayerSeeSky(event.player);
            // Check if player has a pocket watch or vanilla clock.
            checkPlayerHasWatchOrClock(event.player);
        }
    }

    /**
     * Determines if the player can "see the sky". Basically just ray traces for solid blocks.
     */
    private boolean checkCanPlayerSeeSky(EntityPlayer player) {
        if (player == null)
            return true;
        RayTraceResult raytrace = player.world.rayTraceBlocks(player.getPositionEyes(0f),
                player.getPositionEyes(0f).add(new Vec3d(0, 256 - player.posY, 0)), false, true, false);
        return raytrace == null;
    }

    private void checkPlayerHasWatchOrClock(EntityPlayer player) {
        playerHasVanillaClock = playerHasPocketWatch = false;

        for (ItemStack stack : PlayerHelper.getNonEmptyStacks(player, true, true, false)) {
            if (stack.getItem() == ModItems.POCKET_WATCH) {
                playerHasPocketWatch = true;
                // Stop looking, pocket watch is best time-telling device!
                return;
            } else if (stack.getItem() instanceof ItemClock) {
                playerHasVanillaClock = true;
                // Keep looking, player may have a pocket watch as well.
            }
        }
    }
}
