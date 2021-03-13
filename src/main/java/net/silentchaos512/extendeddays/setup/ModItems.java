package net.silentchaos512.extendeddays.setup;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.silentchaos512.extendeddays.item.ItemPocketWatch;

import java.util.function.Supplier;

public class ModItems {
    public static final RegistryObject<ItemPocketWatch> POCKET_WATCH = register("pocket_watch", () ->
            new ItemPocketWatch(new Item.Properties().group(ItemGroup.MISC).maxStackSize(1)));

    private ModItems() {}

    static void register() {}

    private static <T extends Item> RegistryObject<T> register(String name, Supplier<T> itemSupplier) {
        return Registration.ITEMS.register(name, itemSupplier);
    }
}
