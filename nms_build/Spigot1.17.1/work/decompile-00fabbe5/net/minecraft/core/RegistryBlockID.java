package net.minecraft.core;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public class RegistryBlockID<T> implements Registry<T> {

    public static final int DEFAULT = -1;
    private int nextId;
    private final IdentityHashMap<T, Integer> tToId;
    private final List<T> idToT;

    public RegistryBlockID() {
        this(512);
    }

    public RegistryBlockID(int i) {
        this.idToT = Lists.newArrayListWithExpectedSize(i);
        this.tToId = new IdentityHashMap(i);
    }

    public void a(T t0, int i) {
        this.tToId.put(t0, i);

        while (this.idToT.size() <= i) {
            this.idToT.add((Object) null);
        }

        this.idToT.set(i, t0);
        if (this.nextId <= i) {
            this.nextId = i + 1;
        }

    }

    public void b(T t0) {
        this.a(t0, this.nextId);
    }

    @Override
    public int getId(T t0) {
        Integer integer = (Integer) this.tToId.get(t0);

        return integer == null ? -1 : integer;
    }

    @Nullable
    @Override
    public final T fromId(int i) {
        return i >= 0 && i < this.idToT.size() ? this.idToT.get(i) : null;
    }

    public Iterator<T> iterator() {
        return Iterators.filter(this.idToT.iterator(), Predicates.notNull());
    }

    public boolean b(int i) {
        return this.fromId(i) != null;
    }

    public int a() {
        return this.tToId.size();
    }
}
