package net.minecraft.server;

public class ItemBook extends Item {

    public ItemBook(Item.Info item_info) {
        super(item_info);
    }

    public boolean a(ItemStack itemstack) {
        return itemstack.getCount() == 1;
    }

    public int c() {
        return 1;
    }
}
