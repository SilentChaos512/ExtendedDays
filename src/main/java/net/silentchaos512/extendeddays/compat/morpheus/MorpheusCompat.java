package net.silentchaos512.extendeddays.compat.morpheus;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.quetzi.morpheus.Morpheus;
import net.quetzi.morpheus.MorpheusRegistry;
import net.quetzi.morpheus.api.INewDayHandler;
import net.silentchaos512.extendeddays.ExtendedDays;
import net.silentchaos512.extendeddays.event.TimeEvents;

public class MorpheusCompat {
    public static class NewDayHandler implements INewDayHandler {
        INewDayHandler parent;

        public NewDayHandler(INewDayHandler parent) {
            this.parent = parent;
        }

        @Override
        public void startNewDay() {
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            if (server != null) {
                World world = server.worlds[0];
                if (world != null && TimeEvents.INSTANCE.isInExtendedPeriod(world)) {
                    TimeEvents.INSTANCE.endExtendedPeriod(world);
                }
            }

            if (parent != null)
                parent.startNewDay();
        }
    }

    public static void init() {
        INewDayHandler parent = null;
        if (Morpheus.register.isDimRegistered(0))
            parent = MorpheusRegistry.registry.get(0);

        INewDayHandler newHandler = new NewDayHandler(parent);
        ExtendedDays.logHelper.info("Replacing Morpheus new day handler for dimension 0!");
        ExtendedDays.logHelper.info("Parent handler: " + parent);
        Morpheus.register.registerHandler(newHandler, 0);
    }
}
