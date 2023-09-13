package net.minecraft.util;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import java.util.Arrays;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;

public class RegistryID<K> implements Registry<K> {

    private static final int NOT_FOUND = -1;
    private static final Object EMPTY_SLOT = null;
    private static final float LOADFACTOR = 0.8F;
    private K[] keys;
    private int[] values;
    private K[] byId;
    private int nextId;
    private int size;

    private RegistryID(int i) {
        this.keys = new Object[i];
        this.values = new int[i];
        this.byId = new Object[i];
    }

    private RegistryID(K[] ak, int[] aint, K[] ak1, int i, int j) {
        this.keys = ak;
        this.values = aint;
        this.byId = ak1;
        this.nextId = i;
        this.size = j;
    }

    public static <A> RegistryID<A> create(int i) {
        return new RegistryID<>((int) ((float) i / 0.8F));
    }

    @Override
    public int getId(@Nullable K k0) {
        return this.getValue(this.indexOf(k0, this.hash(k0)));
    }

    @Nullable
    @Override
    public K byId(int i) {
        return i >= 0 && i < this.byId.length ? this.byId[i] : null;
    }

    private int getValue(int i) {
        return i == -1 ? -1 : this.values[i];
    }

    public boolean contains(K k0) {
        return this.getId(k0) != -1;
    }

    public boolean contains(int i) {
        return this.byId(i) != null;
    }

    public int add(K k0) {
        int i = this.nextId();

        this.addMapping(k0, i);
        return i;
    }

    private int nextId() {
        while (this.nextId < this.byId.length && this.byId[this.nextId] != null) {
            ++this.nextId;
        }

        return this.nextId;
    }

    private void grow(int i) {
        K[] ak = this.keys;
        int[] aint = this.values;
        RegistryID<K> registryid = new RegistryID<>(i);

        for (int j = 0; j < ak.length; ++j) {
            if (ak[j] != null) {
                registryid.addMapping(ak[j], aint[j]);
            }
        }

        this.keys = registryid.keys;
        this.values = registryid.values;
        this.byId = registryid.byId;
        this.nextId = registryid.nextId;
        this.size = registryid.size;
    }

    public void addMapping(K k0, int i) {
        int j = Math.max(i, this.size + 1);
        int k;

        if ((float) j >= (float) this.keys.length * 0.8F) {
            for (k = this.keys.length << 1; k < i; k <<= 1) {
                ;
            }

            this.grow(k);
        }

        k = this.findEmpty(this.hash(k0));
        this.keys[k] = k0;
        this.values[k] = i;
        this.byId[i] = k0;
        ++this.size;
        if (i == this.nextId) {
            ++this.nextId;
        }

    }

    private int hash(@Nullable K k0) {
        return (MathHelper.murmurHash3Mixer(System.identityHashCode(k0)) & Integer.MAX_VALUE) % this.keys.length;
    }

    private int indexOf(@Nullable K k0, int i) {
        int j;

        for (j = i; j < this.keys.length; ++j) {
            if (this.keys[j] == k0) {
                return j;
            }

            if (this.keys[j] == RegistryID.EMPTY_SLOT) {
                return -1;
            }
        }

        for (j = 0; j < i; ++j) {
            if (this.keys[j] == k0) {
                return j;
            }

            if (this.keys[j] == RegistryID.EMPTY_SLOT) {
                return -1;
            }
        }

        return -1;
    }

    private int findEmpty(int i) {
        int j;

        for (j = i; j < this.keys.length; ++j) {
            if (this.keys[j] == RegistryID.EMPTY_SLOT) {
                return j;
            }
        }

        for (j = 0; j < i; ++j) {
            if (this.keys[j] == RegistryID.EMPTY_SLOT) {
                return j;
            }
        }

        throw new RuntimeException("Overflowed :(");
    }

    public Iterator<K> iterator() {
        return Iterators.filter(Iterators.forArray(this.byId), Predicates.notNull());
    }

    public void clear() {
        Arrays.fill(this.keys, (Object) null);
        Arrays.fill(this.byId, (Object) null);
        this.nextId = 0;
        this.size = 0;
    }

    @Override
    public int size() {
        return this.size;
    }

    public RegistryID<K> copy() {
        return new RegistryID<>((Object[]) this.keys.clone(), (int[]) this.values.clone(), (Object[]) this.byId.clone(), this.nextId, this.size);
    }
}
