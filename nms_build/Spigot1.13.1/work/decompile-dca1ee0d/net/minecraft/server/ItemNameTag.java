package net.minecraft.server;

public class ItemNameTag extends Item {

    public ItemNameTag(Item.Info item_info) {
        super(item_info);
    }

    public boolean a(ItemStack itemstack, EntityHuman entityhuman, EntityLiving entityliving, EnumHand enumhand) {
        if (itemstack.hasName() && !(entityliving instanceof EntityHuman)) {
            entityliving.setCustomName(itemstack.getName());
            if (entityliving instanceof EntityInsentient) {
                ((EntityInsentient) entityliving).di();
            }

            itemstack.subtract(1);
            return true;
        } else {
            return false;
        }
    }
}
