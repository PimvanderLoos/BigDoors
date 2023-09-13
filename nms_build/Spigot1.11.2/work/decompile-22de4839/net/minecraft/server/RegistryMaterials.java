package net.minecraft.server;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;

public class RegistryMaterials<K, V> extends RegistrySimple<K, V> implements Registry<V> {

    protected final RegistryID<V> a = new RegistryID(256);
    protected final Map<V, K> b;

    public RegistryMaterials() {
        this.b = ((BiMap) this.c).inverse();
    }

    public void a(int i, K k0, V v0) {
        this.a.a(v0, i);
        this.a(k0, v0);
    }

    protected Map<K, V> b() {
        return HashBiMap.create();
    }

    @Nullable
    public V get(@Nullable K k0) {
        return super.get(k0);
    }

    @Nullable
    public K b(V v0) {
        return this.b.get(v0);
    }

    public boolean d(K k0) {
        return super.d(k0);
    }

    public int a(@Nullable V v0) {
        return this.a.getId(v0);
    }

    @Nullable
    public V getId(int i) {
        return this.a.fromId(i);
    }

    public Iterator<V> iterator() {
        return this.a.iterator();
    }
}
