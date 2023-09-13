package net.minecraft.server;

public interface PredicateBlock<T> {

    boolean test(T t0, IBlockAccess iblockaccess, BlockPosition blockposition);
}
