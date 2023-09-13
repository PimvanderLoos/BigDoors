package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import java.util.Collection;

public interface IBlockDataHolder<C> {

    Collection<IBlockState<?>> a();

    <T extends Comparable<T>> boolean b(IBlockState<T> iblockstate);

    <T extends Comparable<T>> T get(IBlockState<T> iblockstate);

    <T extends Comparable<T>, V extends T> C set(IBlockState<T> iblockstate, V v0);

    <T extends Comparable<T>> C a(IBlockState<T> iblockstate);

    ImmutableMap<IBlockState<?>, Comparable<?>> getStateMap();
}
