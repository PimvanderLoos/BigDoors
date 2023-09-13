package net.minecraft.server;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegistrySimple<K, V> implements IRegistry<K, V> {

    private static final Logger a = LogManager.getLogger();
    protected final Map<K, V> c = this.b();
    private Object[] b;

    public RegistrySimple() {}

    protected Map<K, V> b() {
        return Maps.newHashMap();
    }

    @Nullable
    public V get(@Nullable K k0) {
        return this.c.get(k0);
    }

    public void a(K k0, V v0) {
        Validate.notNull(k0);
        Validate.notNull(v0);
        this.b = null;
        if (this.c.containsKey(k0)) {
            RegistrySimple.a.debug("Adding duplicate key \'{}\' to registry", k0);
        }

        this.c.put(k0, v0);
    }

    public Set<K> keySet() {
        return Collections.unmodifiableSet(this.c.keySet());
    }

    @Nullable
    public V a(Random random) {
        if (this.b == null) {
            Collection collection = this.c.values();

            if (collection.isEmpty()) {
                return null;
            }

            this.b = collection.toArray(new Object[collection.size()]);
        }

        return this.b[random.nextInt(this.b.length)];
    }

    public boolean d(K k0) {
        return this.c.containsKey(k0);
    }

    public Iterator<V> iterator() {
        return this.c.values().iterator();
    }
}
