package net.silentchaos512.extendeddays.command;

import com.google.common.collect.ImmutableList;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.silentchaos512.extendeddays.ExtendedDays;
import net.silentchaos512.extendeddays.event.TimeEvents;
import net.silentchaos512.lib.command.CommandBaseSL;

import java.util.List;

// TODO: Add "advance to day" subcommand?
public class CommandExtTime extends CommandBaseSL {
    @Override
    public String getName() {
        return "edtime";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "Gets/sets the Extended Days time. Currently, the set command requires 2 values, I'm working on making it more 'sensible'.\n"
                + "Usage: /" + getName() + " <get|set> <value>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (args.length < 1) {
            tell(sender, getUsage(sender), false);
            return;
        }

        String command = args[0];
        if (command.equals("get")) {
            executeGet(server, sender);
        } else if (command.equals("set")) {
            if (args.length < 2) {
                tell(sender, getUsage(sender), false);
                return;
            }
            // TODO: Allow user to enter actual time.
            if (args.length < 3) {
                tell(sender,
                        "This command is currently incomplete. You must enter two values, the world time and the extended time.",
                        false);
                return;
            }
            long value1;
            int value2;
            try {
                value1 = Long.parseLong(args[1]);
                value2 = Integer.parseInt(args[2]);
                executeSet(server, sender, value1, value2);
            } catch (NumberFormatException ex) {
                tell(sender, getUsage(sender), false);
            }
        } else {
            tell(sender, getUsage(sender), false);
        }
    }

    private void executeGet(MinecraftServer server, ICommandSender sender) {
        World world = server.worlds[0];
        int currentTime = TimeEvents.INSTANCE.getCurrentTime(world);
        int totalDayLength = TimeEvents.INSTANCE.getTotalDayLength();
        String str = String.format("Actual time: %d / %d", currentTime, totalDayLength);
        tell(sender, str, false);
    }

    private void executeSet(MinecraftServer server, ICommandSender sender, long amount) {
        // TODO
        tell(sender, "Not yet implemented.", false);
    }

    // TODO: Remove me when proper version is implemented.
    private void executeSet(MinecraftServer server, ICommandSender sender, long amount, int amountExt) {
        ExtendedDays.logHelper.info("Trying to set time ({}, {})", amount, amountExt);
        TimeEvents.INSTANCE.setTime(server.worlds[0], amount, amountExt);
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {

        return ((server.isDedicatedServer() && !(sender instanceof EntityPlayer))
                || server.isSinglePlayer() && server.worlds[0].getWorldInfo().areCommandsAllowed())
                || server.getPlayerList().getOppedPlayers().getGameProfileFromName(sender.getName()) != null;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
        return args.length < 1 ? ImmutableList.of("get", "set") : ImmutableList.of();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    private void tell(ICommandSender sender, String key, boolean fromLocalizationFile, Object... args) {
        String value = fromLocalizationFile
                ? ExtendedDays.localizationHelper.getLocalizedString("command." + key, args)
                : key;
        sender.sendMessage(new TextComponentString(value));
    }
}
