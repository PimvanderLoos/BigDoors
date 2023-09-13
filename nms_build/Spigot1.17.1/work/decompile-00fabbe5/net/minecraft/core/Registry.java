package net.minecraft.core;

import javax.annotation.Nullable;

public interface Registry<T> extends Iterable<T> {

    int getId(T t0);

    @Nullable
    T fromId(int i);
}
