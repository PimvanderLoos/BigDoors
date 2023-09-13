package net.minecraft.server;

import java.util.AbstractList;

public abstract class NBTList<T extends NBTBase> extends AbstractList<T> implements NBTBase {

    public NBTList() {}

    public abstract int size();

    public T get(int i) {
        return this.c(i);
    }

    public T set(int i, T t0) {
        T t1 = this.get(i);

        this.a(i, t0);
        return t1;
    }

    public abstract T c(int i);

    public abstract void a(int i, NBTBase nbtbase);

    public abstract void b(int i);
}
