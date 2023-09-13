package net.minecraft.server;

public class SlotFurnaceFuel extends Slot {

    public SlotFurnaceFuel(IInventory iinventory, int i, int j, int k) {
        super(iinventory, i, j, k);
    }

    public boolean isAllowed(ItemStack itemstack) {
        return TileEntityFurnace.isFuel(itemstack) || d_(itemstack);
    }

    public int getMaxStackSize(ItemStack itemstack) {
        return d_(itemstack) ? 1 : super.getMaxStackSize(itemstack);
    }

    public static boolean d_(ItemStack itemstack) {
        return itemstack.getItem() == Items.BUCKET;
    }
}
