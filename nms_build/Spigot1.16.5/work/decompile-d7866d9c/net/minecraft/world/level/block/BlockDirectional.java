package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateDirection;

public abstract class BlockDirectional extends Block {

    public static final BlockStateDirection FACING = BlockProperties.M;

    protected BlockDirectional(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }
}
