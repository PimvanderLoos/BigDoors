package net.minecraft.server;

public class ItemGoldenAppleEnchanted extends Item {

    public ItemGoldenAppleEnchanted(Item.Info item_info) {
        super(item_info);
    }

    @Override
    public boolean d_(ItemStack itemstack) {
        return true;
    }
}
