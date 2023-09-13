package net.minecraft.world.item;

import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;

public class ItemBed extends ItemBlock {

    public ItemBed(Block block, Item.Info item_info) {
        super(block, item_info);
    }

    @Override
    protected boolean placeBlock(BlockActionContext blockactioncontext, IBlockData iblockdata) {
        return blockactioncontext.getLevel().setBlock(blockactioncontext.getClickedPos(), iblockdata, 26);
    }
}
