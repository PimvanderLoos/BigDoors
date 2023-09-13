package net.minecraft.server;

import javax.annotation.Nullable;

public class PredicateBlockLightTransmission implements PredicateBlock<IBlockData> {

    private static final PredicateBlockLightTransmission a = new PredicateBlockLightTransmission();

    public PredicateBlockLightTransmission() {}

    public boolean a(@Nullable IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockdata != null && iblockdata.b(iblockaccess, blockposition) == 0;
    }

    public static PredicateBlockLightTransmission a() {
        return PredicateBlockLightTransmission.a;
    }

    public boolean test(@Nullable Object object, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.a((IBlockData) object, iblockaccess, blockposition);
    }
}
