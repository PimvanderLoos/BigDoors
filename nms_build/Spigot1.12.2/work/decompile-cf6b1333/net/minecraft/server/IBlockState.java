package net.minecraft.server;

import com.google.common.base.Optional;
import java.util.Collection;

public interface IBlockState<T extends Comparable<T>> {

    String a();

    Collection<T> c();

    Class<T> b();

    Optional<T> b(String s);

    String a(T t0);
}
