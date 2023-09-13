package net.minecraft.world.item;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;

public class ItemBisected extends ItemBlock {

    public ItemBisected(Block block, Item.Info item_info) {
        super(block, item_info);
    }

    @Override
    protected boolean placeBlock(BlockActionContext blockactioncontext, IBlockData iblockdata) {
        World world = blockactioncontext.getLevel();
        BlockPosition blockposition = blockactioncontext.getClickedPos().above();
        IBlockData iblockdata1 = world.isWaterAt(blockposition) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();

        world.setBlock(blockposition, iblockdata1, 27);
        return super.placeBlock(blockactioncontext, iblockdata);
    }
}
