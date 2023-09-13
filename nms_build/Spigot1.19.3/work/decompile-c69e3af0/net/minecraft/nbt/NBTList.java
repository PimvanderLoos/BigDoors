package net.minecraft.nbt;

import java.util.AbstractList;

public abstract class NBTList<T extends NBTBase> extends AbstractList<T> implements NBTBase {

    public NBTList() {}

    public abstract T set(int i, T t0);

    public abstract void add(int i, T t0);

    public abstract T remove(int i);

    public abstract boolean setTag(int i, NBTBase nbtbase);

    public abstract boolean addTag(int i, NBTBase nbtbase);

    public abstract byte getElementType();
}
