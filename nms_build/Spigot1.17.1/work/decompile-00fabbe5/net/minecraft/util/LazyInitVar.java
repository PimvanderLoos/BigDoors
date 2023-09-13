package net.minecraft.util;

import com.google.common.base.Suppliers;
import java.util.Objects;
import java.util.function.Supplier;

@Deprecated
public class LazyInitVar<T> {

    private final Supplier<T> factory;

    public LazyInitVar(Supplier<T> supplier) {
        Objects.requireNonNull(supplier);
        this.factory = Suppliers.memoize(supplier::get);
    }

    public T a() {
        return this.factory.get();
    }
}
