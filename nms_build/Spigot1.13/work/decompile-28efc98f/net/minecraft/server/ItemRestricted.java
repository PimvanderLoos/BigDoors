package net.minecraft.server;

import javax.annotation.Nullable;

public class ItemRestricted extends ItemBlock {

    public ItemRestricted(Block block, Item.Info item_info) {
        super(block, item_info);
    }

    @Nullable
    protected IBlockData b(BlockActionContext blockactioncontext) {
        EntityHuman entityhuman = blockactioncontext.getEntity();

        return entityhuman != null && !entityhuman.isCreativeAndOp() ? null : super.b(blockactioncontext);
    }
}
