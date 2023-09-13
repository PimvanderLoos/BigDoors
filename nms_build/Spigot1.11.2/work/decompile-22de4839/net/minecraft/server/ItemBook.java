package net.minecraft.server;

public class ItemBook extends Item {

    public ItemBook() {}

    public boolean g_(ItemStack itemstack) {
        return itemstack.getCount() == 1;
    }

    public int c() {
        return 1;
    }
}
