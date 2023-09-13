package net.minecraft.nbt;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportSystemDetails;
import net.minecraft.ReportedException;

public class NBTTagCompound implements NBTBase {

    public static final Codec<NBTTagCompound> CODEC = Codec.PASSTHROUGH.comapFlatMap((dynamic) -> {
        NBTBase nbtbase = (NBTBase) dynamic.convert(DynamicOpsNBT.INSTANCE).getValue();

        return nbtbase instanceof NBTTagCompound ? DataResult.success((NBTTagCompound) nbtbase) : DataResult.error(() -> {
            return "Not a compound tag: " + nbtbase;
        });
    }, (nbttagcompound) -> {
        return new Dynamic(DynamicOpsNBT.INSTANCE, nbttagcompound);
    });
    private static final int SELF_SIZE_IN_BYTES = 48;
    private static final int MAP_ENTRY_SIZE_IN_BYTES = 32;
    public static final NBTTagType<NBTTagCompound> TYPE = new NBTTagType.b<NBTTagCompound>() {
        @Override
        public NBTTagCompound load(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
            nbtreadlimiter.accountBytes(48L);
            if (i > 512) {
                throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
            } else {
                HashMap hashmap = Maps.newHashMap();

                byte b0;

                while ((b0 = NBTTagCompound.readNamedTagType(datainput, nbtreadlimiter)) != 0) {
                    String s = NBTTagCompound.readNamedTagName(datainput, nbtreadlimiter);

                    nbtreadlimiter.accountBytes((long) (28 + 2 * s.length()));
                    NBTBase nbtbase = NBTTagCompound.readNamedTagData(NBTTagTypes.getType(b0), s, datainput, i + 1, nbtreadlimiter);

                    if (hashmap.put(s, nbtbase) == null) {
                        nbtreadlimiter.accountBytes(36L);
                    }
                }

                return new NBTTagCompound(hashmap);
            }
        }

        @Override
        public StreamTagVisitor.b parse(DataInput datainput, StreamTagVisitor streamtagvisitor) throws IOException {
            while (true) {
                byte b0;

                if ((b0 = datainput.readByte()) != 0) {
                    NBTTagType<?> nbttagtype = NBTTagTypes.getType(b0);

                    switch (streamtagvisitor.visitEntry(nbttagtype)) {
                        case HALT:
                            return StreamTagVisitor.b.HALT;
                        case BREAK:
                            NBTTagString.skipString(datainput);
                            nbttagtype.skip(datainput);
                            break;
                        case SKIP:
                            NBTTagString.skipString(datainput);
                            nbttagtype.skip(datainput);
                            continue;
                        default:
                            String s = datainput.readUTF();

                            switch (streamtagvisitor.visitEntry(nbttagtype, s)) {
                                case HALT:
                                    return StreamTagVisitor.b.HALT;
                                case BREAK:
                                    nbttagtype.skip(datainput);
                                    break;
                                case SKIP:
                                    nbttagtype.skip(datainput);
                                    continue;
                                default:
                                    switch (nbttagtype.parse(datainput, streamtagvisitor)) {
                                        case HALT:
                                            return StreamTagVisitor.b.HALT;
                                        case BREAK:
                                        default:
                                            continue;
                                    }
                            }
                    }
                }

                if (b0 != 0) {
                    while ((b0 = datainput.readByte()) != 0) {
                        NBTTagString.skipString(datainput);
                        NBTTagTypes.getType(b0).skip(datainput);
                    }
                }

                return streamtagvisitor.visitContainerEnd();
            }
        }

        @Override
        public void skip(DataInput datainput) throws IOException {
            byte b0;

            while ((b0 = datainput.readByte()) != 0) {
                NBTTagString.skipString(datainput);
                NBTTagTypes.getType(b0).skip(datainput);
            }

        }

        @Override
        public String getName() {
            return "COMPOUND";
        }

        @Override
        public String getPrettyName() {
            return "TAG_Compound";
        }
    };
    private final Map<String, NBTBase> tags;

    protected NBTTagCompound(Map<String, NBTBase> map) {
        this.tags = map;
    }

    public NBTTagCompound() {
        this(Maps.newHashMap());
    }

    @Override
    public void write(DataOutput dataoutput) throws IOException {
        Iterator iterator = this.tags.keySet().iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();
            NBTBase nbtbase = (NBTBase) this.tags.get(s);

            writeNamedTag(s, nbtbase, dataoutput);
        }

        dataoutput.writeByte(0);
    }

    @Override
    public int sizeInBytes() {
        int i = 48;

        Entry entry;

        for (Iterator iterator = this.tags.entrySet().iterator(); iterator.hasNext(); i += ((NBTBase) entry.getValue()).sizeInBytes()) {
            entry = (Entry) iterator.next();
            i += 28 + 2 * ((String) entry.getKey()).length();
            i += 36;
        }

        return i;
    }

    public Set<String> getAllKeys() {
        return this.tags.keySet();
    }

    @Override
    public byte getId() {
        return 10;
    }

    @Override
    public NBTTagType<NBTTagCompound> getType() {
        return NBTTagCompound.TYPE;
    }

    public int size() {
        return this.tags.size();
    }

    @Nullable
    public NBTBase put(String s, NBTBase nbtbase) {
        return (NBTBase) this.tags.put(s, nbtbase);
    }

    public void putByte(String s, byte b0) {
        this.tags.put(s, NBTTagByte.valueOf(b0));
    }

    public void putShort(String s, short short0) {
        this.tags.put(s, NBTTagShort.valueOf(short0));
    }

    public void putInt(String s, int i) {
        this.tags.put(s, NBTTagInt.valueOf(i));
    }

    public void putLong(String s, long i) {
        this.tags.put(s, NBTTagLong.valueOf(i));
    }

    public void putUUID(String s, UUID uuid) {
        this.tags.put(s, GameProfileSerializer.createUUID(uuid));
    }

    public UUID getUUID(String s) {
        return GameProfileSerializer.loadUUID(this.get(s));
    }

    public boolean hasUUID(String s) {
        NBTBase nbtbase = this.get(s);

        return nbtbase != null && nbtbase.getType() == NBTTagIntArray.TYPE && ((NBTTagIntArray) nbtbase).getAsIntArray().length == 4;
    }

    public void putFloat(String s, float f) {
        this.tags.put(s, NBTTagFloat.valueOf(f));
    }

    public void putDouble(String s, double d0) {
        this.tags.put(s, NBTTagDouble.valueOf(d0));
    }

    public void putString(String s, String s1) {
        this.tags.put(s, NBTTagString.valueOf(s1));
    }

    public void putByteArray(String s, byte[] abyte) {
        this.tags.put(s, new NBTTagByteArray(abyte));
    }

    public void putByteArray(String s, List<Byte> list) {
        this.tags.put(s, new NBTTagByteArray(list));
    }

    public void putIntArray(String s, int[] aint) {
        this.tags.put(s, new NBTTagIntArray(aint));
    }

    public void putIntArray(String s, List<Integer> list) {
        this.tags.put(s, new NBTTagIntArray(list));
    }

    public void putLongArray(String s, long[] along) {
        this.tags.put(s, new NBTTagLongArray(along));
    }

    public void putLongArray(String s, List<Long> list) {
        this.tags.put(s, new NBTTagLongArray(list));
    }

    public void putBoolean(String s, boolean flag) {
        this.tags.put(s, NBTTagByte.valueOf(flag));
    }

    @Nullable
    public NBTBase get(String s) {
        return (NBTBase) this.tags.get(s);
    }

    public byte getTagType(String s) {
        NBTBase nbtbase = (NBTBase) this.tags.get(s);

        return nbtbase == null ? 0 : nbtbase.getId();
    }

    public boolean contains(String s) {
        return this.tags.containsKey(s);
    }

    public boolean contains(String s, int i) {
        byte b0 = this.getTagType(s);

        return b0 == i ? true : (i != 99 ? false : b0 == 1 || b0 == 2 || b0 == 3 || b0 == 4 || b0 == 5 || b0 == 6);
    }

    public byte getByte(String s) {
        try {
            if (this.contains(s, 99)) {
                return ((NBTNumber) this.tags.get(s)).getAsByte();
            }
        } catch (ClassCastException classcastexception) {
            ;
        }

        return 0;
    }

    public short getShort(String s) {
        try {
            if (this.contains(s, 99)) {
                return ((NBTNumber) this.tags.get(s)).getAsShort();
            }
        } catch (ClassCastException classcastexception) {
            ;
        }

        return 0;
    }

    public int getInt(String s) {
        try {
            if (this.contains(s, 99)) {
                return ((NBTNumber) this.tags.get(s)).getAsInt();
            }
        } catch (ClassCastException classcastexception) {
            ;
        }

        return 0;
    }

    public long getLong(String s) {
        try {
            if (this.contains(s, 99)) {
                return ((NBTNumber) this.tags.get(s)).getAsLong();
            }
        } catch (ClassCastException classcastexception) {
            ;
        }

        return 0L;
    }

    public float getFloat(String s) {
        try {
            if (this.contains(s, 99)) {
                return ((NBTNumber) this.tags.get(s)).getAsFloat();
            }
        } catch (ClassCastException classcastexception) {
            ;
        }

        return 0.0F;
    }

    public double getDouble(String s) {
        try {
            if (this.contains(s, 99)) {
                return ((NBTNumber) this.tags.get(s)).getAsDouble();
            }
        } catch (ClassCastException classcastexception) {
            ;
        }

        return 0.0D;
    }

    public String getString(String s) {
        try {
            if (this.contains(s, 8)) {
                return ((NBTBase) this.tags.get(s)).getAsString();
            }
        } catch (ClassCastException classcastexception) {
            ;
        }

        return "";
    }

    public byte[] getByteArray(String s) {
        try {
            if (this.contains(s, 7)) {
                return ((NBTTagByteArray) this.tags.get(s)).getAsByteArray();
            }
        } catch (ClassCastException classcastexception) {
            throw new ReportedException(this.createReport(s, NBTTagByteArray.TYPE, classcastexception));
        }

        return new byte[0];
    }

    public int[] getIntArray(String s) {
        try {
            if (this.contains(s, 11)) {
                return ((NBTTagIntArray) this.tags.get(s)).getAsIntArray();
            }
        } catch (ClassCastException classcastexception) {
            throw new ReportedException(this.createReport(s, NBTTagIntArray.TYPE, classcastexception));
        }

        return new int[0];
    }

    public long[] getLongArray(String s) {
        try {
            if (this.contains(s, 12)) {
                return ((NBTTagLongArray) this.tags.get(s)).getAsLongArray();
            }
        } catch (ClassCastException classcastexception) {
            throw new ReportedException(this.createReport(s, NBTTagLongArray.TYPE, classcastexception));
        }

        return new long[0];
    }

    public NBTTagCompound getCompound(String s) {
        try {
            if (this.contains(s, 10)) {
                return (NBTTagCompound) this.tags.get(s);
            }
        } catch (ClassCastException classcastexception) {
            throw new ReportedException(this.createReport(s, NBTTagCompound.TYPE, classcastexception));
        }

        return new NBTTagCompound();
    }

    public NBTTagList getList(String s, int i) {
        try {
            if (this.getTagType(s) == 9) {
                NBTTagList nbttaglist = (NBTTagList) this.tags.get(s);

                if (!nbttaglist.isEmpty() && nbttaglist.getElementType() != i) {
                    return new NBTTagList();
                }

                return nbttaglist;
            }
        } catch (ClassCastException classcastexception) {
            throw new ReportedException(this.createReport(s, NBTTagList.TYPE, classcastexception));
        }

        return new NBTTagList();
    }

    public boolean getBoolean(String s) {
        return this.getByte(s) != 0;
    }

    public void remove(String s) {
        this.tags.remove(s);
    }

    @Override
    public String toString() {
        return this.getAsString();
    }

    public boolean isEmpty() {
        return this.tags.isEmpty();
    }

    private CrashReport createReport(String s, NBTTagType<?> nbttagtype, ClassCastException classcastexception) {
        CrashReport crashreport = CrashReport.forThrowable(classcastexception, "Reading NBT data");
        CrashReportSystemDetails crashreportsystemdetails = crashreport.addCategory("Corrupt NBT tag", 1);

        crashreportsystemdetails.setDetail("Tag type found", () -> {
            return ((NBTBase) this.tags.get(s)).getType().getName();
        });
        Objects.requireNonNull(nbttagtype);
        crashreportsystemdetails.setDetail("Tag type expected", nbttagtype::getName);
        crashreportsystemdetails.setDetail("Tag name", (Object) s);
        return crashreport;
    }

    @Override
    public NBTTagCompound copy() {
        Map<String, NBTBase> map = Maps.newHashMap(Maps.transformValues(this.tags, NBTBase::copy));

        return new NBTTagCompound(map);
    }

    public boolean equals(Object object) {
        return this == object ? true : object instanceof NBTTagCompound && Objects.equals(this.tags, ((NBTTagCompound) object).tags);
    }

    public int hashCode() {
        return this.tags.hashCode();
    }

    private static void writeNamedTag(String s, NBTBase nbtbase, DataOutput dataoutput) throws IOException {
        dataoutput.writeByte(nbtbase.getId());
        if (nbtbase.getId() != 0) {
            dataoutput.writeUTF(s);
            nbtbase.write(dataoutput);
        }
    }

    static byte readNamedTagType(DataInput datainput, NBTReadLimiter nbtreadlimiter) throws IOException {
        return datainput.readByte();
    }

    static String readNamedTagName(DataInput datainput, NBTReadLimiter nbtreadlimiter) throws IOException {
        return datainput.readUTF();
    }

    static NBTBase readNamedTagData(NBTTagType<?> nbttagtype, String s, DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) {
        try {
            return nbttagtype.load(datainput, i, nbtreadlimiter);
        } catch (IOException ioexception) {
            CrashReport crashreport = CrashReport.forThrowable(ioexception, "Loading NBT data");
            CrashReportSystemDetails crashreportsystemdetails = crashreport.addCategory("NBT Tag");

            crashreportsystemdetails.setDetail("Tag name", (Object) s);
            crashreportsystemdetails.setDetail("Tag type", (Object) nbttagtype.getName());
            throw new ReportedException(crashreport);
        }
    }

    public NBTTagCompound merge(NBTTagCompound nbttagcompound) {
        Iterator iterator = nbttagcompound.tags.keySet().iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();
            NBTBase nbtbase = (NBTBase) nbttagcompound.tags.get(s);

            if (nbtbase.getId() == 10) {
                if (this.contains(s, 10)) {
                    NBTTagCompound nbttagcompound1 = this.getCompound(s);

                    nbttagcompound1.merge((NBTTagCompound) nbtbase);
                } else {
                    this.put(s, nbtbase.copy());
                }
            } else {
                this.put(s, nbtbase.copy());
            }
        }

        return this;
    }

    @Override
    public void accept(TagVisitor tagvisitor) {
        tagvisitor.visitCompound(this);
    }

    protected Map<String, NBTBase> entries() {
        return Collections.unmodifiableMap(this.tags);
    }

    @Override
    public StreamTagVisitor.b accept(StreamTagVisitor streamtagvisitor) {
        Iterator iterator = this.tags.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<String, NBTBase> entry = (Entry) iterator.next();
            NBTBase nbtbase = (NBTBase) entry.getValue();
            NBTTagType<?> nbttagtype = nbtbase.getType();
            StreamTagVisitor.a streamtagvisitor_a = streamtagvisitor.visitEntry(nbttagtype);

            switch (streamtagvisitor_a) {
                case HALT:
                    return StreamTagVisitor.b.HALT;
                case BREAK:
                    return streamtagvisitor.visitContainerEnd();
                case SKIP:
                    break;
                default:
                    streamtagvisitor_a = streamtagvisitor.visitEntry(nbttagtype, (String) entry.getKey());
                    switch (streamtagvisitor_a) {
                        case HALT:
                            return StreamTagVisitor.b.HALT;
                        case BREAK:
                            return streamtagvisitor.visitContainerEnd();
                        case SKIP:
                            break;
                        default:
                            StreamTagVisitor.b streamtagvisitor_b = nbtbase.accept(streamtagvisitor);

                            switch (streamtagvisitor_b) {
                                case HALT:
                                    return StreamTagVisitor.b.HALT;
                                case BREAK:
                                    return streamtagvisitor.visitContainerEnd();
                            }
                    }
            }
        }

        return streamtagvisitor.visitContainerEnd();
    }
}
