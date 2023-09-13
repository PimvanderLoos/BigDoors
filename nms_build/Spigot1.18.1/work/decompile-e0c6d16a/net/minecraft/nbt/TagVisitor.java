package net.minecraft.nbt;

public interface TagVisitor {

    void visitString(NBTTagString nbttagstring);

    void visitByte(NBTTagByte nbttagbyte);

    void visitShort(NBTTagShort nbttagshort);

    void visitInt(NBTTagInt nbttagint);

    void visitLong(NBTTagLong nbttaglong);

    void visitFloat(NBTTagFloat nbttagfloat);

    void visitDouble(NBTTagDouble nbttagdouble);

    void visitByteArray(NBTTagByteArray nbttagbytearray);

    void visitIntArray(NBTTagIntArray nbttagintarray);

    void visitLongArray(NBTTagLongArray nbttaglongarray);

    void visitList(NBTTagList nbttaglist);

    void visitCompound(NBTTagCompound nbttagcompound);

    void visitEnd(NBTTagEnd nbttagend);
}
