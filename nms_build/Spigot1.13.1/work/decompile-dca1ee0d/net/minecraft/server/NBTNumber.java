package net.minecraft.server;

public abstract class NBTNumber implements NBTBase {

    protected NBTNumber() {}

    public abstract long d();

    public abstract int e();

    public abstract short f();

    public abstract byte g();

    public abstract double asDouble();

    public abstract float i();

    public abstract Number j();
}
