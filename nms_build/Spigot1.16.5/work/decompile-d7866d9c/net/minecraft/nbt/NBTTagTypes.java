package net.minecraft.nbt;

public class NBTTagTypes {

    private static final NBTTagType<?>[] a = new NBTTagType[]{NBTTagEnd.a, NBTTagByte.a, NBTTagShort.a, NBTTagInt.a, NBTTagLong.a, NBTTagFloat.b, NBTTagDouble.b, NBTTagByteArray.a, NBTTagString.a, NBTTagList.a, NBTTagCompound.b, NBTTagIntArray.a, NBTTagLongArray.a};

    public static NBTTagType<?> a(int i) {
        return i >= 0 && i < NBTTagTypes.a.length ? NBTTagTypes.a[i] : NBTTagType.a(i);
    }
}
