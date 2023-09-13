package net.minecraft.server;

public class ItemItemFrame extends ItemHanging {

    public ItemItemFrame(Item.Info item_info) {
        super(EntityItemFrame.class, item_info);
    }

    protected boolean a(EntityHuman entityhuman, EnumDirection enumdirection, ItemStack itemstack, BlockPosition blockposition) {
        return !World.k(blockposition) && entityhuman.a(blockposition, enumdirection, itemstack);
    }
}
