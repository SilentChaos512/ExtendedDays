package net.silentchaos512.extendeddays.item;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.silentchaos512.extendeddays.ExtendedDays;
import net.silentchaos512.extendeddays.event.ClientEvents;
import net.silentchaos512.lib.item.ItemSL;

public class ItemPocketWatch extends ItemSL {

  public static final String NAME = "pocket_watch";

  public ItemPocketWatch() {

    super(1, ExtendedDays.MOD_ID, NAME);
  }

  @Override
  public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {

    if (world.isRemote) {
      ClientEvents.playerHasPocketWatch = true;
    }
  }
}
