package net.minecraft.util;

import com.google.common.base.Suppliers;
import java.util.Objects;
import java.util.function.Supplier;

/** @deprecated */
@Deprecated
public class LazyInitVar<T> {

    private final Supplier<T> factory;

    public LazyInitVar(Supplier<T> supplier) {
        Objects.requireNonNull(supplier);
        this.factory = Suppliers.memoize(supplier::get);
    }

    public T get() {
        return this.factory.get();
    }
}
