package net.minecraft.server;

import javax.annotation.Nullable;

public class PredicateBlockSolid implements PredicateBlock<IBlockData> {

    private static final PredicateBlockSolid a = new PredicateBlockSolid();

    public PredicateBlockSolid() {}

    public boolean a(@Nullable IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockdata != null && !iblockdata.getMaterial().isSolid();
    }

    public static PredicateBlockSolid a() {
        return PredicateBlockSolid.a;
    }

    public boolean test(@Nullable Object object, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.a((IBlockData) object, iblockaccess, blockposition);
    }
}
