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
    protected IBlockData getPlacementState(BlockActionContext blockactioncontext) {
        EntityHuman entityhuman = blockactioncontext.getPlayer();

        return entityhuman != null && !entityhuman.canUseGameMasterBlocks() ? null : super.getPlacementState(blockactioncontext);
    }
}
