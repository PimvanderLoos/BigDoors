package net.minecraft.world.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

public interface ContainerSynchronizer {

    void sendInitialData(Container container, NonNullList<ItemStack> nonnulllist, ItemStack itemstack, int[] aint);

    void sendSlotChange(Container container, int i, ItemStack itemstack);

    void sendCarriedChange(Container container, ItemStack itemstack);

    void sendDataChange(Container container, int i, int j);
}
