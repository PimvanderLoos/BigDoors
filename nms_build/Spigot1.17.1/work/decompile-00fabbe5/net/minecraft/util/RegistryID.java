package net.minecraft.util;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import java.util.Arrays;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;

public class RegistryID<K> implements Registry<K> {

    public static final int NOT_FOUND = -1;
    private static final Object EMPTY_SLOT = null;
    private static final float LOADFACTOR = 0.8F;
    private K[] keys;
    private int[] values;
    private K[] byId;
    private int nextId;
    private int size;

    public RegistryID(int i) {
        i = (int) ((float) i / 0.8F);
        this.keys = new Object[i];
        this.values = new int[i];
        this.byId = new Object[i];
    }

    @Override
    public int getId(@Nullable K k0) {
        return this.c(this.b(k0, this.d(k0)));
    }

    @Nullable
    @Override
    public K fromId(int i) {
        return i >= 0 && i < this.byId.length ? this.byId[i] : null;
    }

    private int c(int i) {
        return i == -1 ? -1 : this.values[i];
    }

    public boolean b(K k0) {
        return this.getId(k0) != -1;
    }

    public boolean b(int i) {
        return this.fromId(i) != null;
    }

    public int c(K k0) {
        int i = this.c();

        this.a(k0, i);
        return i;
    }

    private int c() {
        while (this.nextId < this.byId.length && this.byId[this.nextId] != null) {
            ++this.nextId;
        }

        return this.nextId;
    }

    private void d(int i) {
        K[] ak = this.keys;
        int[] aint = this.values;

        this.keys = new Object[i];
        this.values = new int[i];
        this.byId = new Object[i];
        this.nextId = 0;
        this.size = 0;

        for (int j = 0; j < ak.length; ++j) {
            if (ak[j] != null) {
                this.a(ak[j], aint[j]);
            }
        }

    }

    public void a(K k0, int i) {
        int j = Math.max(i, this.size + 1);
        int k;

        if ((float) j >= (float) this.keys.length * 0.8F) {
            for (k = this.keys.length << 1; k < i; k <<= 1) {
                ;
            }

            this.d(k);
        }

        k = this.e(this.d(k0));
        this.keys[k] = k0;
        this.values[k] = i;
        this.byId[i] = k0;
        ++this.size;
        if (i == this.nextId) {
            ++this.nextId;
        }

    }

    private int d(@Nullable K k0) {
        return (MathHelper.g(System.identityHashCode(k0)) & Integer.MAX_VALUE) % this.keys.length;
    }

    private int b(@Nullable K k0, int i) {
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

    private int e(int i) {
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

    public void a() {
        Arrays.fill(this.keys, (Object) null);
        Arrays.fill(this.byId, (Object) null);
        this.nextId = 0;
        this.size = 0;
    }

    public int b() {
        return this.size;
    }
}
