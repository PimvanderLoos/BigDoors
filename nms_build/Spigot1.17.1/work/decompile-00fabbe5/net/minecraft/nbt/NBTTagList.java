package net.minecraft.nbt;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class NBTTagList extends NBTList<NBTBase> {

    private static final int SELF_SIZE_IN_BITS = 296;
    public static final NBTTagType<NBTTagList> TYPE = new NBTTagType<NBTTagList>() {
        @Override
        public NBTTagList b(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
            nbtreadlimiter.a(296L);
            if (i > 512) {
                throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
            } else {
                byte b0 = datainput.readByte();
                int j = datainput.readInt();

                if (b0 == 0 && j > 0) {
                    throw new RuntimeException("Missing type on ListTag");
                } else {
                    nbtreadlimiter.a(32L * (long) j);
                    NBTTagType<?> nbttagtype = NBTTagTypes.a(b0);
                    List<NBTBase> list = Lists.newArrayListWithCapacity(j);

                    for (int k = 0; k < j; ++k) {
                        list.add(nbttagtype.b(datainput, i + 1, nbtreadlimiter));
                    }

                    return new NBTTagList(list, b0);
                }
            }
        }

        @Override
        public String a() {
            return "LIST";
        }

        @Override
        public String b() {
            return "TAG_List";
        }
    };
    private final List<NBTBase> list;
    private byte type;

    NBTTagList(List<NBTBase> list, byte b0) {
        this.list = list;
        this.type = b0;
    }

    public NBTTagList() {
        this(Lists.newArrayList(), (byte) 0);
    }

    @Override
    public void write(DataOutput dataoutput) throws IOException {
        if (this.list.isEmpty()) {
            this.type = 0;
        } else {
            this.type = ((NBTBase) this.list.get(0)).getTypeId();
        }

        dataoutput.writeByte(this.type);
        dataoutput.writeInt(this.list.size());
        Iterator iterator = this.list.iterator();

        while (iterator.hasNext()) {
            NBTBase nbtbase = (NBTBase) iterator.next();

            nbtbase.write(dataoutput);
        }

    }

    @Override
    public byte getTypeId() {
        return 9;
    }

    @Override
    public NBTTagType<NBTTagList> b() {
        return NBTTagList.TYPE;
    }

    @Override
    public String toString() {
        return this.asString();
    }

    private void f() {
        if (this.list.isEmpty()) {
            this.type = 0;
        }

    }

    @Override
    public NBTBase remove(int i) {
        NBTBase nbtbase = (NBTBase) this.list.remove(i);

        this.f();
        return nbtbase;
    }

    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    public NBTTagCompound getCompound(int i) {
        if (i >= 0 && i < this.list.size()) {
            NBTBase nbtbase = (NBTBase) this.list.get(i);

            if (nbtbase.getTypeId() == 10) {
                return (NBTTagCompound) nbtbase;
            }
        }

        return new NBTTagCompound();
    }

    public NBTTagList b(int i) {
        if (i >= 0 && i < this.list.size()) {
            NBTBase nbtbase = (NBTBase) this.list.get(i);

            if (nbtbase.getTypeId() == 9) {
                return (NBTTagList) nbtbase;
            }
        }

        return new NBTTagList();
    }

    public short d(int i) {
        if (i >= 0 && i < this.list.size()) {
            NBTBase nbtbase = (NBTBase) this.list.get(i);

            if (nbtbase.getTypeId() == 2) {
                return ((NBTTagShort) nbtbase).asShort();
            }
        }

        return 0;
    }

    public int e(int i) {
        if (i >= 0 && i < this.list.size()) {
            NBTBase nbtbase = (NBTBase) this.list.get(i);

            if (nbtbase.getTypeId() == 3) {
                return ((NBTTagInt) nbtbase).asInt();
            }
        }

        return 0;
    }

    public int[] f(int i) {
        if (i >= 0 && i < this.list.size()) {
            NBTBase nbtbase = (NBTBase) this.list.get(i);

            if (nbtbase.getTypeId() == 11) {
                return ((NBTTagIntArray) nbtbase).getInts();
            }
        }

        return new int[0];
    }

    public long[] g(int i) {
        if (i >= 0 && i < this.list.size()) {
            NBTBase nbtbase = (NBTBase) this.list.get(i);

            if (nbtbase.getTypeId() == 11) {
                return ((NBTTagLongArray) nbtbase).getLongs();
            }
        }

        return new long[0];
    }

    public double h(int i) {
        if (i >= 0 && i < this.list.size()) {
            NBTBase nbtbase = (NBTBase) this.list.get(i);

            if (nbtbase.getTypeId() == 6) {
                return ((NBTTagDouble) nbtbase).asDouble();
            }
        }

        return 0.0D;
    }

    public float i(int i) {
        if (i >= 0 && i < this.list.size()) {
            NBTBase nbtbase = (NBTBase) this.list.get(i);

            if (nbtbase.getTypeId() == 5) {
                return ((NBTTagFloat) nbtbase).asFloat();
            }
        }

        return 0.0F;
    }

    public String getString(int i) {
        if (i >= 0 && i < this.list.size()) {
            NBTBase nbtbase = (NBTBase) this.list.get(i);

            return nbtbase.getTypeId() == 8 ? nbtbase.asString() : nbtbase.toString();
        } else {
            return "";
        }
    }

    public int size() {
        return this.list.size();
    }

    public NBTBase get(int i) {
        return (NBTBase) this.list.get(i);
    }

    @Override
    public NBTBase set(int i, NBTBase nbtbase) {
        NBTBase nbtbase1 = this.get(i);

        if (!this.a(i, nbtbase)) {
            throw new UnsupportedOperationException(String.format("Trying to add tag of type %d to list of %d", nbtbase.getTypeId(), this.type));
        } else {
            return nbtbase1;
        }
    }

    @Override
    public void add(int i, NBTBase nbtbase) {
        if (!this.b(i, nbtbase)) {
            throw new UnsupportedOperationException(String.format("Trying to add tag of type %d to list of %d", nbtbase.getTypeId(), this.type));
        }
    }

    @Override
    public boolean a(int i, NBTBase nbtbase) {
        if (this.a(nbtbase)) {
            this.list.set(i, nbtbase);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean b(int i, NBTBase nbtbase) {
        if (this.a(nbtbase)) {
            this.list.add(i, nbtbase);
            return true;
        } else {
            return false;
        }
    }

    private boolean a(NBTBase nbtbase) {
        if (nbtbase.getTypeId() == 0) {
            return false;
        } else if (this.type == 0) {
            this.type = nbtbase.getTypeId();
            return true;
        } else {
            return this.type == nbtbase.getTypeId();
        }
    }

    @Override
    public NBTTagList clone() {
        Iterable<NBTBase> iterable = NBTTagTypes.a(this.type).c() ? this.list : Iterables.transform(this.list, NBTBase::clone);
        List<NBTBase> list = Lists.newArrayList((Iterable) iterable);

        return new NBTTagList(list, this.type);
    }

    public boolean equals(Object object) {
        return this == object ? true : object instanceof NBTTagList && Objects.equals(this.list, ((NBTTagList) object).list);
    }

    public int hashCode() {
        return this.list.hashCode();
    }

    @Override
    public void a(TagVisitor tagvisitor) {
        tagvisitor.a(this);
    }

    @Override
    public byte e() {
        return this.type;
    }

    public void clear() {
        this.list.clear();
        this.type = 0;
    }
}
