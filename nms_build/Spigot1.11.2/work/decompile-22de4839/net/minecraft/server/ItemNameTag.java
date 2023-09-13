package net.minecraft.server;

public class ItemNameTag extends Item {

    public ItemNameTag() {
        this.a(CreativeModeTab.i);
    }

    public boolean a(ItemStack itemstack, EntityHuman entityhuman, EntityLiving entityliving, EnumHand enumhand) {
        if (itemstack.hasName() && !(entityliving instanceof EntityHuman)) {
            entityliving.setCustomName(itemstack.getName());
            if (entityliving instanceof EntityInsentient) {
                ((EntityInsentient) entityliving).cS();
            }

            itemstack.subtract(1);
            return true;
        } else {
            return false;
        }
    }
}
