package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.BlockBase;

public class BlockSand extends BlockFalling {

    private final int a;

    public BlockSand(int i, BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.a = i;
    }
}
