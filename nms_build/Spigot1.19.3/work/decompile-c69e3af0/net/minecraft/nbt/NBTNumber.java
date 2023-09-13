package net.minecraft.nbt;

public abstract class NBTNumber implements NBTBase {

    protected NBTNumber() {}

    public abstract long getAsLong();

    public abstract int getAsInt();

    public abstract short getAsShort();

    public abstract byte getAsByte();

    public abstract double getAsDouble();

    public abstract float getAsFloat();

    public abstract Number getAsNumber();

    @Override
    public String toString() {
        return this.getAsString();
    }
}
