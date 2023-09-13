package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;

public class ItemRestricted extends ItemBlock {

    public ItemRestricted(Block block, Item.Info item_info) {
        super(block, item_info);
    }

    @Nullable
    @Override
    protected IBlockData c(BlockActionContext blockactioncontext) {
        EntityHuman entityhuman = blockactioncontext.getEntity();

        return entityhuman != null && !entityhuman.isCreativeAndOp() ? null : super.c(blockactioncontext);
    }
}
