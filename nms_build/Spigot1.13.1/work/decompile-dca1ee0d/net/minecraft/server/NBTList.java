package net.minecraft.server;

import java.util.AbstractList;

public abstract class NBTList<T extends NBTBase> extends AbstractList<T> implements NBTBase {

    public NBTList() {}

    public abstract int size();

    public T get(int i) {
        return this.c(i);
    }

    public T set(int i, T t0) {
        NBTBase nbtbase = this.get(i);

        this.a(i, t0);
        return nbtbase;
    }

    public abstract T c(int i);

    public abstract void a(int i, NBTBase nbtbase);

    public abstract void b(int i);

    public Object set(int i, Object object) {
        return this.set(i, (NBTBase) object);
    }

    public Object get(int i) {
        return this.get(i);
    }
}
