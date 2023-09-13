package net.minecraft.world.level.levelgen;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.block.state.IBlockData;

@FunctionalInterface
public interface BaseStoneSource {

    default IBlockData a(BlockPosition blockposition) {
        return this.getBaseBlock(blockposition.getX(), blockposition.getY(), blockposition.getZ());
    }

    IBlockData getBaseBlock(int i, int j, int k);
}
