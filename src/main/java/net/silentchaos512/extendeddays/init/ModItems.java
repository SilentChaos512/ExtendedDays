package net.silentchaos512.extendeddays.init;

import net.silentchaos512.extendeddays.item.ItemPocketWatch;
import net.silentchaos512.lib.registry.SRegistry;

public class ModItems {
    public static final ItemPocketWatch POCKET_WATCH = new ItemPocketWatch();

    public static void registerAll(SRegistry reg) {
        reg.registerItem(POCKET_WATCH, "pocket_watch");
    }
}
