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
    public static final NBTTagType<NBTTagList> TYPE = new NBTTagType.b<NBTTagList>() {
        @Override
        public NBTTagList load(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
            nbtreadlimiter.accountBits(296L);
            if (i > 512) {
                throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
            } else {
                byte b0 = datainput.readByte();
                int j = datainput.readInt();

                if (b0 == 0 && j > 0) {
                    throw new RuntimeException("Missing type on ListTag");
                } else {
                    nbtreadlimiter.accountBits(32L * (long) j);
                    NBTTagType<?> nbttagtype = NBTTagTypes.getType(b0);
                    List<NBTBase> list = Lists.newArrayListWithCapacity(j);

                    for (int k = 0; k < j; ++k) {
                        list.add(nbttagtype.load(datainput, i + 1, nbtreadlimiter));
                    }

                    return new NBTTagList(list, b0);
                }
            }
        }

        @Override
        public StreamTagVisitor.b parse(DataInput datainput, StreamTagVisitor streamtagvisitor) throws IOException {
            NBTTagType<?> nbttagtype = NBTTagTypes.getType(datainput.readByte());
            int i = datainput.readInt();

            switch (streamtagvisitor.visitList(nbttagtype, i)) {
                case HALT:
                    return StreamTagVisitor.b.HALT;
                case BREAK:
                    nbttagtype.skip(datainput, i);
                    return streamtagvisitor.visitContainerEnd();
                default:
                    int j = 0;

                    while (true) {
                        if (j < i) {
                            label31:
                            {
                                switch (streamtagvisitor.visitElement(nbttagtype, j)) {
                                    case HALT:
                                        return StreamTagVisitor.b.HALT;
                                    case BREAK:
                                        nbttagtype.skip(datainput);
                                        break label31;
                                    case SKIP:
                                        nbttagtype.skip(datainput);
                                        break;
                                    default:
                                        switch (nbttagtype.parse(datainput, streamtagvisitor)) {
                                            case HALT:
                                                return StreamTagVisitor.b.HALT;
                                            case BREAK:
                                                break label31;
                                        }
                                }

                                ++j;
                                continue;
                            }
                        }

                        int k = i - 1 - j;

                        if (k > 0) {
                            nbttagtype.skip(datainput, k);
                        }

                        return streamtagvisitor.visitContainerEnd();
                    }
            }
        }

        @Override
        public void skip(DataInput datainput) throws IOException {
            NBTTagType<?> nbttagtype = NBTTagTypes.getType(datainput.readByte());
            int i = datainput.readInt();

            nbttagtype.skip(datainput, i);
        }

        @Override
        public String getName() {
            return "LIST";
        }

        @Override
        public String getPrettyName() {
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
            this.type = ((NBTBase) this.list.get(0)).getId();
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
    public byte getId() {
        return 9;
    }

    @Override
    public NBTTagType<NBTTagList> getType() {
        return NBTTagList.TYPE;
    }

    @Override
    public String toString() {
        return this.getAsString();
    }

    private void updateTypeAfterRemove() {
        if (this.list.isEmpty()) {
            this.type = 0;
        }

    }

    @Override
    public NBTBase remove(int i) {
        NBTBase nbtbase = (NBTBase) this.list.remove(i);

        this.updateTypeAfterRemove();
        return nbtbase;
    }

    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    public NBTTagCompound getCompound(int i) {
        if (i >= 0 && i < this.list.size()) {
            NBTBase nbtbase = (NBTBase) this.list.get(i);

            if (nbtbase.getId() == 10) {
                return (NBTTagCompound) nbtbase;
            }
        }

        return new NBTTagCompound();
    }

    public NBTTagList getList(int i) {
        if (i >= 0 && i < this.list.size()) {
            NBTBase nbtbase = (NBTBase) this.list.get(i);

            if (nbtbase.getId() == 9) {
                return (NBTTagList) nbtbase;
            }
        }

        return new NBTTagList();
    }

    public short getShort(int i) {
        if (i >= 0 && i < this.list.size()) {
            NBTBase nbtbase = (NBTBase) this.list.get(i);

            if (nbtbase.getId() == 2) {
                return ((NBTTagShort) nbtbase).getAsShort();
            }
        }

        return 0;
    }

    public int getInt(int i) {
        if (i >= 0 && i < this.list.size()) {
            NBTBase nbtbase = (NBTBase) this.list.get(i);

            if (nbtbase.getId() == 3) {
                return ((NBTTagInt) nbtbase).getAsInt();
            }
        }

        return 0;
    }

    public int[] getIntArray(int i) {
        if (i >= 0 && i < this.list.size()) {
            NBTBase nbtbase = (NBTBase) this.list.get(i);

            if (nbtbase.getId() == 11) {
                return ((NBTTagIntArray) nbtbase).getAsIntArray();
            }
        }

        return new int[0];
    }

    public long[] getLongArray(int i) {
        if (i >= 0 && i < this.list.size()) {
            NBTBase nbtbase = (NBTBase) this.list.get(i);

            if (nbtbase.getId() == 11) {
                return ((NBTTagLongArray) nbtbase).getAsLongArray();
            }
        }

        return new long[0];
    }

    public double getDouble(int i) {
        if (i >= 0 && i < this.list.size()) {
            NBTBase nbtbase = (NBTBase) this.list.get(i);

            if (nbtbase.getId() == 6) {
                return ((NBTTagDouble) nbtbase).getAsDouble();
            }
        }

        return 0.0D;
    }

    public float getFloat(int i) {
        if (i >= 0 && i < this.list.size()) {
            NBTBase nbtbase = (NBTBase) this.list.get(i);

            if (nbtbase.getId() == 5) {
                return ((NBTTagFloat) nbtbase).getAsFloat();
            }
        }

        return 0.0F;
    }

    public String getString(int i) {
        if (i >= 0 && i < this.list.size()) {
            NBTBase nbtbase = (NBTBase) this.list.get(i);

            return nbtbase.getId() == 8 ? nbtbase.getAsString() : nbtbase.toString();
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

        if (!this.setTag(i, nbtbase)) {
            throw new UnsupportedOperationException(String.format("Trying to add tag of type %d to list of %d", nbtbase.getId(), this.type));
        } else {
            return nbtbase1;
        }
    }

    @Override
    public void add(int i, NBTBase nbtbase) {
        if (!this.addTag(i, nbtbase)) {
            throw new UnsupportedOperationException(String.format("Trying to add tag of type %d to list of %d", nbtbase.getId(), this.type));
        }
    }

    @Override
    public boolean setTag(int i, NBTBase nbtbase) {
        if (this.updateType(nbtbase)) {
            this.list.set(i, nbtbase);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean addTag(int i, NBTBase nbtbase) {
        if (this.updateType(nbtbase)) {
            this.list.add(i, nbtbase);
            return true;
        } else {
            return false;
        }
    }

    private boolean updateType(NBTBase nbtbase) {
        if (nbtbase.getId() == 0) {
            return false;
        } else if (this.type == 0) {
            this.type = nbtbase.getId();
            return true;
        } else {
            return this.type == nbtbase.getId();
        }
    }

    @Override
    public NBTTagList copy() {
        Iterable<NBTBase> iterable = NBTTagTypes.getType(this.type).isValue() ? this.list : Iterables.transform(this.list, NBTBase::copy);
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
    public void accept(TagVisitor tagvisitor) {
        tagvisitor.visitList(this);
    }

    @Override
    public byte getElementType() {
        return this.type;
    }

    public void clear() {
        this.list.clear();
        this.type = 0;
    }

    @Override
    public StreamTagVisitor.b accept(StreamTagVisitor streamtagvisitor) {
        switch (streamtagvisitor.visitList(NBTTagTypes.getType(this.type), this.list.size())) {
            case HALT:
                return StreamTagVisitor.b.HALT;
            case BREAK:
                return streamtagvisitor.visitContainerEnd();
            default:
                int i = 0;

                while (i < this.list.size()) {
                    NBTBase nbtbase = (NBTBase) this.list.get(i);

                    switch (streamtagvisitor.visitElement(nbtbase.getType(), i)) {
                        case HALT:
                            return StreamTagVisitor.b.HALT;
                        case BREAK:
                            return streamtagvisitor.visitContainerEnd();
                        default:
                            switch (nbtbase.accept(streamtagvisitor)) {
                                case HALT:
                                    return StreamTagVisitor.b.HALT;
                                case BREAK:
                                    return streamtagvisitor.visitContainerEnd();
                            }
                        case SKIP:
                            ++i;
                    }
                }

                return streamtagvisitor.visitContainerEnd();
        }
    }
}
