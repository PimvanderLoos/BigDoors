package net.minecraft.nbt;

public class NBTTagTypes {

    private static final NBTTagType<?>[] TYPES = new NBTTagType[]{NBTTagEnd.TYPE, NBTTagByte.TYPE, NBTTagShort.TYPE, NBTTagInt.TYPE, NBTTagLong.TYPE, NBTTagFloat.TYPE, NBTTagDouble.TYPE, NBTTagByteArray.TYPE, NBTTagString.TYPE, NBTTagList.TYPE, NBTTagCompound.TYPE, NBTTagIntArray.TYPE, NBTTagLongArray.TYPE};

    public NBTTagTypes() {}

    public static NBTTagType<?> getType(int i) {
        return i >= 0 && i < NBTTagTypes.TYPES.length ? NBTTagTypes.TYPES[i] : NBTTagType.createInvalid(i);
    }
}
