package net.minecraft.server;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public class RegistryBlockID<T> implements Registry<T> {

    private final IdentityHashMap<T, Integer> a;
    private final List<T> b;

    public RegistryBlockID() {
        this(512);
    }

    public RegistryBlockID(int i) {
        this.b = Lists.newArrayListWithExpectedSize(i);
        this.a = new IdentityHashMap(i);
    }

    public void a(T t0, int i) {
        this.a.put(t0, Integer.valueOf(i));

        while (this.b.size() <= i) {
            this.b.add((Object) null);
        }

        this.b.set(i, t0);
    }

    public int getId(T t0) {
        Integer integer = (Integer) this.a.get(t0);

        return integer == null ? -1 : integer.intValue();
    }

    @Nullable
    public final T fromId(int i) {
        return i >= 0 && i < this.b.size() ? this.b.get(i) : null;
    }

    public Iterator<T> iterator() {
        return Iterators.filter(this.b.iterator(), Predicates.notNull());
    }

    public int a() {
        return this.a.size();
    }
}
