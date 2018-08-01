package net.silentchaos512.extendeddays.init;

import net.minecraft.item.Item;
import net.silentchaos512.extendeddays.item.ItemPocketWatch;
import net.silentchaos512.lib.registry.IRegistrationHandler;
import net.silentchaos512.lib.registry.SRegistry;

public class ModItems implements IRegistrationHandler<Item> {
    public static final ItemPocketWatch POCKET_WATCH = new ItemPocketWatch();

    @Override
    public void registerAll(SRegistry reg) {
        reg.registerItem(POCKET_WATCH);
    }
}
