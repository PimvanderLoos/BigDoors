package net.minecraft.world.inventory;

import net.minecraft.world.item.ItemStack;

public interface ICrafting {

    void slotChanged(Container container, int i, ItemStack itemstack);

    void dataChanged(Container container, int i, int j);
}
