package net.minecraft.server;

public class ItemArmorColorable extends ItemArmor {

    public ItemArmorColorable(ArmorMaterial armormaterial, EnumItemSlot enumitemslot, Item.Info item_info) {
        super(armormaterial, enumitemslot, item_info);
    }

    public boolean e(ItemStack itemstack) {
        NBTTagCompound nbttagcompound = itemstack.b("display");

        return nbttagcompound != null && nbttagcompound.hasKeyOfType("color", 99);
    }

    public int f(ItemStack itemstack) {
        NBTTagCompound nbttagcompound = itemstack.b("display");

        return nbttagcompound != null && nbttagcompound.hasKeyOfType("color", 99) ? nbttagcompound.getInt("color") : 10511680;
    }

    public void g(ItemStack itemstack) {
        NBTTagCompound nbttagcompound = itemstack.b("display");

        if (nbttagcompound != null && nbttagcompound.hasKey("color")) {
            nbttagcompound.remove("color");
        }

    }

    public void a(ItemStack itemstack, int i) {
        itemstack.a("display").setInt("color", i);
    }
}
