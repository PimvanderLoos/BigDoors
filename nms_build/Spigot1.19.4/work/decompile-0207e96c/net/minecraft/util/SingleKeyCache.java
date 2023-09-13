package net.minecraft.util;

import java.util.Objects;
import java.util.function.Function;
import javax.annotation.Nullable;

public class SingleKeyCache<K, V> {

    private final Function<K, V> computeValue;
    @Nullable
    private K cacheKey = null;
    @Nullable
    private V cachedValue;

    public SingleKeyCache(Function<K, V> function) {
        this.computeValue = function;
    }

    public V getValue(K k0) {
        if (this.cachedValue == null || !Objects.equals(this.cacheKey, k0)) {
            this.cachedValue = this.computeValue.apply(k0);
            this.cacheKey = k0;
        }

        return this.cachedValue;
    }
}
