package net.minecraft.world.level;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.block.Block;

public record BlockActionData(BlockPosition a, Block b, int c, int d) {

    private final BlockPosition pos;
    private final Block block;
    private final int paramA;
    private final int paramB;

    public BlockActionData(BlockPosition blockposition, Block block, int i, int j) {
        this.pos = blockposition;
        this.block = block;
        this.paramA = i;
        this.paramB = j;
    }

    public BlockPosition pos() {
        return this.pos;
    }

    public Block block() {
        return this.block;
    }

    public int paramA() {
        return this.paramA;
    }

    public int paramB() {
        return this.paramB;
    }
}
