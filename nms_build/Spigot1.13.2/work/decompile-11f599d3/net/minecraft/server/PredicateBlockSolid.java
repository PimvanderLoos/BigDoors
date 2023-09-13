package net.minecraft.server;

import javax.annotation.Nullable;

public class PredicateBlockSolid implements PredicateBlock<IBlockData> {

    private static final PredicateBlockSolid a = new PredicateBlockSolid();

    public PredicateBlockSolid() {}

    public boolean test(@Nullable IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockdata != null && !iblockdata.getMaterial().isSolid();
    }

    public static PredicateBlockSolid a() {
        return PredicateBlockSolid.a;
    }
}
