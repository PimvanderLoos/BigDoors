package net.minecraft.world;

import javax.annotation.Nullable;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.item.ItemStack;

public interface IWorldInventory extends IInventory {

    int[] getSlotsForFace(EnumDirection enumdirection);

    boolean canPlaceItemThroughFace(int i, ItemStack itemstack, @Nullable EnumDirection enumdirection);

    boolean canTakeItemThroughFace(int i, ItemStack itemstack, EnumDirection enumdirection);
}
