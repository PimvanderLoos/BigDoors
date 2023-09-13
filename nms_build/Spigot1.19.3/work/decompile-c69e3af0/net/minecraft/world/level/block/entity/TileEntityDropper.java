package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPosition;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.level.block.state.IBlockData;

public class TileEntityDropper extends TileEntityDispenser {

    public TileEntityDropper(BlockPosition blockposition, IBlockData iblockdata) {
        super(TileEntityTypes.DROPPER, blockposition, iblockdata);
    }

    @Override
    protected IChatBaseComponent getDefaultName() {
        return IChatBaseComponent.translatable("container.dropper");
    }
}
