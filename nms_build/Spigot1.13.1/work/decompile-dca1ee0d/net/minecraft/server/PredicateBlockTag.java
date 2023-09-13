package net.minecraft.server;

import javax.annotation.Nullable;

public class PredicateBlockTag implements PredicateBlock<IBlockData> {

    private final Tag<Block> a;

    public PredicateBlockTag(Tag<Block> tag) {
        this.a = tag;
    }

    public static PredicateBlockTag a(Tag<Block> tag) {
        return new PredicateBlockTag(tag);
    }

    public boolean a(@Nullable IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockdata != null && iblockdata.a(this.a);
    }

    public boolean test(@Nullable Object object, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.a((IBlockData) object, iblockaccess, blockposition);
    }
}
