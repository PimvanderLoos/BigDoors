package net.minecraft.server;

import javax.annotation.Nullable;

public class PredicateBlockNotSolidOrLiquid implements PredicateBlock<IBlockData> {

    private static final PredicateBlockNotSolidOrLiquid a = new PredicateBlockNotSolidOrLiquid();

    public PredicateBlockNotSolidOrLiquid() {}

    public boolean a(@Nullable IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockdata != null && !iblockdata.getMaterial().isSolid() && iblockdata.s().e();
    }

    public static PredicateBlockNotSolidOrLiquid a() {
        return PredicateBlockNotSolidOrLiquid.a;
    }

    public boolean test(@Nullable Object object, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.a((IBlockData) object, iblockaccess, blockposition);
    }
}
