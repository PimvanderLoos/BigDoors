package net.minecraft.core;

import javax.annotation.Nullable;

public interface Registry<T> extends Iterable<T> {

    int DEFAULT = -1;

    int getId(T t0);

    @Nullable
    T byId(int i);

    default T byIdOrThrow(int i) {
        T t0 = this.byId(i);

        if (t0 == null) {
            throw new IllegalArgumentException("No value with id " + i);
        } else {
            return t0;
        }
    }

    int size();
}
