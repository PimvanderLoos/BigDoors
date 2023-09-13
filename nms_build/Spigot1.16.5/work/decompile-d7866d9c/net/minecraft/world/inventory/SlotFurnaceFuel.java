package net.minecraft.world.inventory;

import net.minecraft.world.IInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class SlotFurnaceFuel extends Slot {

    private final ContainerFurnace a;

    public SlotFurnaceFuel(ContainerFurnace containerfurnace, IInventory iinventory, int i, int j, int k) {
        super(iinventory, i, j, k);
        this.a = containerfurnace;
    }

    @Override
    public boolean isAllowed(ItemStack itemstack) {
        return this.a.b(itemstack) || c_(itemstack);
    }

    @Override
    public int getMaxStackSize(ItemStack itemstack) {
        return c_(itemstack) ? 1 : super.getMaxStackSize(itemstack);
    }

    public static boolean c_(ItemStack itemstack) {
        return itemstack.getItem() == Items.BUCKET;
    }
}
