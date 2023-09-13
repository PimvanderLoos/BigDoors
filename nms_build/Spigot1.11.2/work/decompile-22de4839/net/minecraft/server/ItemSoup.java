package net.minecraft.server;

public class ItemSoup extends ItemFood {

    public ItemSoup(int i) {
        super(i, false);
        this.d(1);
    }

    public ItemStack a(ItemStack itemstack, World world, EntityLiving entityliving) {
        super.a(itemstack, world, entityliving);
        return new ItemStack(Items.BOWL);
    }
}
