package net.minecraft.server;

public class PredicateBlockLiquid implements PredicateBlock<IBlockData> {

    private static final PredicateBlockLiquid a = new PredicateBlockLiquid();

    public PredicateBlockLiquid() {}

    public boolean test(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return !iblockdata.s().e();
    }

    public static PredicateBlockLiquid a() {
        return PredicateBlockLiquid.a;
    }
}
