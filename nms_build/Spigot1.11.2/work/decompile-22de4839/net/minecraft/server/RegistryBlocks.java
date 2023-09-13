package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.Validate;

public class RegistryBlocks<K, V> extends RegistryMaterials<K, V> {

    private final K d;
    private V e;

    public RegistryBlocks(K k0) {
        this.d = k0;
    }

    public void a(int i, K k0, V v0) {
        if (this.d.equals(k0)) {
            this.e = v0;
        }

        super.a(i, k0, v0);
    }

    public void a() {
        Validate.notNull(this.e, "Missing default of DefaultedMappedRegistry: " + this.d, new Object[0]);
    }

    public int a(V v0) {
        int i = super.a(v0);

        return i == -1 ? super.a(this.e) : i;
    }

    @Nonnull
    public K b(V v0) {
        Object object = super.b(v0);

        return object == null ? this.d : object;
    }

    @Nonnull
    public V get(@Nullable K k0) {
        Object object = super.get(k0);

        return object == null ? this.e : object;
    }

    @Nonnull
    public V getId(int i) {
        Object object = super.getId(i);

        return object == null ? this.e : object;
    }

    @Nonnull
    public V a(Random random) {
        Object object = super.a(random);

        return object == null ? this.e : object;
    }
}
