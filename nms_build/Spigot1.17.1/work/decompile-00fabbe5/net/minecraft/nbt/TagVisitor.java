package net.minecraft.nbt;

public interface TagVisitor {

    void a(NBTTagString nbttagstring);

    void a(NBTTagByte nbttagbyte);

    void a(NBTTagShort nbttagshort);

    void a(NBTTagInt nbttagint);

    void a(NBTTagLong nbttaglong);

    void a(NBTTagFloat nbttagfloat);

    void a(NBTTagDouble nbttagdouble);

    void a(NBTTagByteArray nbttagbytearray);

    void a(NBTTagIntArray nbttagintarray);

    void a(NBTTagLongArray nbttaglongarray);

    void a(NBTTagList nbttaglist);

    void a(NBTTagCompound nbttagcompound);

    void a(NBTTagEnd nbttagend);
}
