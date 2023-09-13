package net.minecraft.world.level;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.block.Block;

public class BlockActionData {

    private final BlockPosition a;
    private final Block b;
    private final int c;
    private final int d;

    public BlockActionData(BlockPosition blockposition, Block block, int i, int j) {
        this.a = blockposition;
        this.b = block;
        this.c = i;
        this.d = j;
    }

    public BlockPosition a() {
        return this.a;
    }

    public Block b() {
        return this.b;
    }

    public int c() {
        return this.c;
    }

    public int d() {
        return this.d;
    }

    public boolean equals(Object object) {
        if (!(object instanceof BlockActionData)) {
            return false;
        } else {
            BlockActionData blockactiondata = (BlockActionData) object;

            return this.a.equals(blockactiondata.a) && this.c == blockactiondata.c && this.d == blockactiondata.d && this.b == blockactiondata.b;
        }
    }

    public int hashCode() {
        int i = this.a.hashCode();

        i = 31 * i + this.b.hashCode();
        i = 31 * i + this.c;
        i = 31 * i + this.d;
        return i;
    }

    public String toString() {
        return "TE(" + this.a + ")," + this.c + "," + this.d + "," + this.b;
    }
}
