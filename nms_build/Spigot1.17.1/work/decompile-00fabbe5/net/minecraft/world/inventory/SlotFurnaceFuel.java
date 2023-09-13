package net.minecraft.world.inventory;

import net.minecraft.world.IInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class SlotFurnaceFuel extends Slot {

    private final ContainerFurnace menu;

    public SlotFurnaceFuel(ContainerFurnace containerfurnace, IInventory iinventory, int i, int j, int k) {
        super(iinventory, i, j, k);
        this.menu = containerfurnace;
    }

    @Override
    public boolean isAllowed(ItemStack itemstack) {
        return this.menu.d(itemstack) || c(itemstack);
    }

    @Override
    public int getMaxStackSize(ItemStack itemstack) {
        return c(itemstack) ? 1 : super.getMaxStackSize(itemstack);
    }

    public static boolean c(ItemStack itemstack) {
        return itemstack.a(Items.BUCKET);
    }
}
