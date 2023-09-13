package net.minecraft.server;

import javax.annotation.Nullable;

public class PredicateBlockType implements PredicateBlock<IBlockData> {

    private final Block a;

    public PredicateBlockType(Block block) {
        this.a = block;
    }

    public static PredicateBlockType a(Block block) {
        return new PredicateBlockType(block);
    }

    public boolean test(@Nullable IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockdata != null && iblockdata.getBlock() == this.a;
    }
}
