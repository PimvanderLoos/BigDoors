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
    protected boolean a(BlockActionContext blockactioncontext, IBlockData iblockdata) {
        World world = blockactioncontext.getWorld();
        BlockPosition blockposition = blockactioncontext.getClickPosition().up();
        IBlockData iblockdata1 = world.B(blockposition) ? Blocks.WATER.getBlockData() : Blocks.AIR.getBlockData();

        world.setTypeAndData(blockposition, iblockdata1, 27);
        return super.a(blockactioncontext, iblockdata);
    }
}
