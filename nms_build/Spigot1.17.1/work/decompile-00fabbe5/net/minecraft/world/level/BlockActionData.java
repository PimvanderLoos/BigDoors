package net.minecraft.world.level;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.block.Block;

public class BlockActionData {

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

    public BlockPosition a() {
        return this.pos;
    }

    public Block b() {
        return this.block;
    }

    public int c() {
        return this.paramA;
    }

    public int d() {
        return this.paramB;
    }

    public boolean equals(Object object) {
        if (!(object instanceof BlockActionData)) {
            return false;
        } else {
            BlockActionData blockactiondata = (BlockActionData) object;

            return this.pos.equals(blockactiondata.pos) && this.paramA == blockactiondata.paramA && this.paramB == blockactiondata.paramB && this.block == blockactiondata.block;
        }
    }

    public int hashCode() {
        int i = this.pos.hashCode();

        i = 31 * i + this.block.hashCode();
        i = 31 * i + this.paramA;
        i = 31 * i + this.paramB;
        return i;
    }

    public String toString() {
        return "TE(" + this.pos + ")," + this.paramA + "," + this.paramB + "," + this.block;
    }
}
