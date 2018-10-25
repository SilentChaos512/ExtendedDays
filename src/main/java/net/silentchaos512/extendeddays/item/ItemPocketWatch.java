package net.silentchaos512.extendeddays.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.silentchaos512.extendeddays.ExtendedDays;
import net.silentchaos512.extendeddays.event.ClientEvents;

import javax.annotation.Nullable;
import java.util.List;

public class ItemPocketWatch extends Item {
    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (world.isRemote) {
            ClientEvents.playerHasPocketWatch = true;
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(ExtendedDays.i18n.subText(this, "desc"));
    }
}
