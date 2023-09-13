package net.minecraft.core;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;

public class RegistryBlockID<T> implements Registry<T> {

    private int nextId;
    private final Object2IntMap<T> tToId;
    private final List<T> idToT;

    public RegistryBlockID() {
        this(512);
    }

    public RegistryBlockID(int i) {
        this.idToT = Lists.newArrayListWithExpectedSize(i);
        this.tToId = new Object2IntOpenCustomHashMap(i, SystemUtils.identityStrategy());
        this.tToId.defaultReturnValue(-1);
    }

    public void addMapping(T t0, int i) {
        this.tToId.put(t0, i);

        while (this.idToT.size() <= i) {
            this.idToT.add((Object) null);
        }

        this.idToT.set(i, t0);
        if (this.nextId <= i) {
            this.nextId = i + 1;
        }

    }

    public void add(T t0) {
        this.addMapping(t0, this.nextId);
    }

    @Override
    public int getId(T t0) {
        return this.tToId.getInt(t0);
    }

    @Nullable
    @Override
    public final T byId(int i) {
        return i >= 0 && i < this.idToT.size() ? this.idToT.get(i) : null;
    }

    public Iterator<T> iterator() {
        return Iterators.filter(this.idToT.iterator(), Objects::nonNull);
    }

    public boolean contains(int i) {
        return this.byId(i) != null;
    }

    @Override
    public int size() {
        return this.tToId.size();
    }
}
