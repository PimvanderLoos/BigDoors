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

        return nbtbase instanceof NBTTagCompound ? DataResult.success((NBTTagCompound) nbtbase) : DataResult.error("Not a compound tag: " + nbtbase);
    }, (nbttagcompound) -> {
        return new Dynamic(DynamicOpsNBT.INSTANCE, nbttagcompound);
    });
    private static final int SELF_SIZE_IN_BITS = 384;
    private static final int MAP_ENTRY_SIZE_IN_BITS = 256;
    public static final NBTTagType<NBTTagCompound> TYPE = new NBTTagType<NBTTagCompound>() {
        @Override
        public NBTTagCompound b(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
            nbtreadlimiter.a(384L);
            if (i > 512) {
                throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
            } else {
                HashMap hashmap = Maps.newHashMap();

                byte b0;

                while ((b0 = NBTTagCompound.a(datainput, nbtreadlimiter)) != 0) {
                    String s = NBTTagCompound.b(datainput, nbtreadlimiter);

                    nbtreadlimiter.a((long) (224 + 16 * s.length()));
                    NBTBase nbtbase = NBTTagCompound.a(NBTTagTypes.a(b0), s, datainput, i + 1, nbtreadlimiter);

                    if (hashmap.put(s, nbtbase) != null) {
                        nbtreadlimiter.a(288L);
                    }
                }

                return new NBTTagCompound(hashmap);
            }
        }

        @Override
        public String a() {
            return "COMPOUND";
        }

        @Override
        public String b() {
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

            a(s, nbtbase, dataoutput);
        }

        dataoutput.writeByte(0);
    }

    public Set<String> getKeys() {
        return this.tags.keySet();
    }

    @Override
    public byte getTypeId() {
        return 10;
    }

    @Override
    public NBTTagType<NBTTagCompound> b() {
        return NBTTagCompound.TYPE;
    }

    public int e() {
        return this.tags.size();
    }

    @Nullable
    public NBTBase set(String s, NBTBase nbtbase) {
        return (NBTBase) this.tags.put(s, nbtbase);
    }

    public void setByte(String s, byte b0) {
        this.tags.put(s, NBTTagByte.a(b0));
    }

    public void setShort(String s, short short0) {
        this.tags.put(s, NBTTagShort.a(short0));
    }

    public void setInt(String s, int i) {
        this.tags.put(s, NBTTagInt.a(i));
    }

    public void setLong(String s, long i) {
        this.tags.put(s, NBTTagLong.a(i));
    }

    public void a(String s, UUID uuid) {
        this.tags.put(s, GameProfileSerializer.a(uuid));
    }

    public UUID a(String s) {
        return GameProfileSerializer.a(this.get(s));
    }

    public boolean b(String s) {
        NBTBase nbtbase = this.get(s);

        return nbtbase != null && nbtbase.b() == NBTTagIntArray.TYPE && ((NBTTagIntArray) nbtbase).getInts().length == 4;
    }

    public void setFloat(String s, float f) {
        this.tags.put(s, NBTTagFloat.a(f));
    }

    public void setDouble(String s, double d0) {
        this.tags.put(s, NBTTagDouble.a(d0));
    }

    public void setString(String s, String s1) {
        this.tags.put(s, NBTTagString.a(s1));
    }

    public void setByteArray(String s, byte[] abyte) {
        this.tags.put(s, new NBTTagByteArray(abyte));
    }

    public void a(String s, List<Byte> list) {
        this.tags.put(s, new NBTTagByteArray(list));
    }

    public void setIntArray(String s, int[] aint) {
        this.tags.put(s, new NBTTagIntArray(aint));
    }

    public void b(String s, List<Integer> list) {
        this.tags.put(s, new NBTTagIntArray(list));
    }

    public void a(String s, long[] along) {
        this.tags.put(s, new NBTTagLongArray(along));
    }

    public void c(String s, List<Long> list) {
        this.tags.put(s, new NBTTagLongArray(list));
    }

    public void setBoolean(String s, boolean flag) {
        this.tags.put(s, NBTTagByte.a(flag));
    }

    @Nullable
    public NBTBase get(String s) {
        return (NBTBase) this.tags.get(s);
    }

    public byte d(String s) {
        NBTBase nbtbase = (NBTBase) this.tags.get(s);

        return nbtbase == null ? 0 : nbtbase.getTypeId();
    }

    public boolean hasKey(String s) {
        return this.tags.containsKey(s);
    }

    public boolean hasKeyOfType(String s, int i) {
        byte b0 = this.d(s);

        return b0 == i ? true : (i != 99 ? false : b0 == 1 || b0 == 2 || b0 == 3 || b0 == 4 || b0 == 5 || b0 == 6);
    }

    public byte getByte(String s) {
        try {
            if (this.hasKeyOfType(s, 99)) {
                return ((NBTNumber) this.tags.get(s)).asByte();
            }
        } catch (ClassCastException classcastexception) {
            ;
        }

        return 0;
    }

    public short getShort(String s) {
        try {
            if (this.hasKeyOfType(s, 99)) {
                return ((NBTNumber) this.tags.get(s)).asShort();
            }
        } catch (ClassCastException classcastexception) {
            ;
        }

        return 0;
    }

    public int getInt(String s) {
        try {
            if (this.hasKeyOfType(s, 99)) {
                return ((NBTNumber) this.tags.get(s)).asInt();
            }
        } catch (ClassCastException classcastexception) {
            ;
        }

        return 0;
    }

    public long getLong(String s) {
        try {
            if (this.hasKeyOfType(s, 99)) {
                return ((NBTNumber) this.tags.get(s)).asLong();
            }
        } catch (ClassCastException classcastexception) {
            ;
        }

        return 0L;
    }

    public float getFloat(String s) {
        try {
            if (this.hasKeyOfType(s, 99)) {
                return ((NBTNumber) this.tags.get(s)).asFloat();
            }
        } catch (ClassCastException classcastexception) {
            ;
        }

        return 0.0F;
    }

    public double getDouble(String s) {
        try {
            if (this.hasKeyOfType(s, 99)) {
                return ((NBTNumber) this.tags.get(s)).asDouble();
            }
        } catch (ClassCastException classcastexception) {
            ;
        }

        return 0.0D;
    }

    public String getString(String s) {
        try {
            if (this.hasKeyOfType(s, 8)) {
                return ((NBTBase) this.tags.get(s)).asString();
            }
        } catch (ClassCastException classcastexception) {
            ;
        }

        return "";
    }

    public byte[] getByteArray(String s) {
        try {
            if (this.hasKeyOfType(s, 7)) {
                return ((NBTTagByteArray) this.tags.get(s)).getBytes();
            }
        } catch (ClassCastException classcastexception) {
            throw new ReportedException(this.a(s, NBTTagByteArray.TYPE, classcastexception));
        }

        return new byte[0];
    }

    public int[] getIntArray(String s) {
        try {
            if (this.hasKeyOfType(s, 11)) {
                return ((NBTTagIntArray) this.tags.get(s)).getInts();
            }
        } catch (ClassCastException classcastexception) {
            throw new ReportedException(this.a(s, NBTTagIntArray.TYPE, classcastexception));
        }

        return new int[0];
    }

    public long[] getLongArray(String s) {
        try {
            if (this.hasKeyOfType(s, 12)) {
                return ((NBTTagLongArray) this.tags.get(s)).getLongs();
            }
        } catch (ClassCastException classcastexception) {
            throw new ReportedException(this.a(s, NBTTagLongArray.TYPE, classcastexception));
        }

        return new long[0];
    }

    public NBTTagCompound getCompound(String s) {
        try {
            if (this.hasKeyOfType(s, 10)) {
                return (NBTTagCompound) this.tags.get(s);
            }
        } catch (ClassCastException classcastexception) {
            throw new ReportedException(this.a(s, NBTTagCompound.TYPE, classcastexception));
        }

        return new NBTTagCompound();
    }

    public NBTTagList getList(String s, int i) {
        try {
            if (this.d(s) == 9) {
                NBTTagList nbttaglist = (NBTTagList) this.tags.get(s);

                if (!nbttaglist.isEmpty() && nbttaglist.e() != i) {
                    return new NBTTagList();
                }

                return nbttaglist;
            }
        } catch (ClassCastException classcastexception) {
            throw new ReportedException(this.a(s, NBTTagList.TYPE, classcastexception));
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
        return this.asString();
    }

    public boolean isEmpty() {
        return this.tags.isEmpty();
    }

    private CrashReport a(String s, NBTTagType<?> nbttagtype, ClassCastException classcastexception) {
        CrashReport crashreport = CrashReport.a(classcastexception, "Reading NBT data");
        CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Corrupt NBT tag", 1);

        crashreportsystemdetails.a("Tag type found", () -> {
            return ((NBTBase) this.tags.get(s)).b().a();
        });
        Objects.requireNonNull(nbttagtype);
        crashreportsystemdetails.a("Tag type expected", nbttagtype::a);
        crashreportsystemdetails.a("Tag name", (Object) s);
        return crashreport;
    }

    @Override
    public NBTTagCompound clone() {
        Map<String, NBTBase> map = Maps.newHashMap(Maps.transformValues(this.tags, NBTBase::clone));

        return new NBTTagCompound(map);
    }

    public boolean equals(Object object) {
        return this == object ? true : object instanceof NBTTagCompound && Objects.equals(this.tags, ((NBTTagCompound) object).tags);
    }

    public int hashCode() {
        return this.tags.hashCode();
    }

    private static void a(String s, NBTBase nbtbase, DataOutput dataoutput) throws IOException {
        dataoutput.writeByte(nbtbase.getTypeId());
        if (nbtbase.getTypeId() != 0) {
            dataoutput.writeUTF(s);
            nbtbase.write(dataoutput);
        }
    }

    static byte a(DataInput datainput, NBTReadLimiter nbtreadlimiter) throws IOException {
        return datainput.readByte();
    }

    static String b(DataInput datainput, NBTReadLimiter nbtreadlimiter) throws IOException {
        return datainput.readUTF();
    }

    static NBTBase a(NBTTagType<?> nbttagtype, String s, DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) {
        try {
            return nbttagtype.b(datainput, i, nbtreadlimiter);
        } catch (IOException ioexception) {
            CrashReport crashreport = CrashReport.a(ioexception, "Loading NBT data");
            CrashReportSystemDetails crashreportsystemdetails = crashreport.a("NBT Tag");

            crashreportsystemdetails.a("Tag name", (Object) s);
            crashreportsystemdetails.a("Tag type", (Object) nbttagtype.a());
            throw new ReportedException(crashreport);
        }
    }

    public NBTTagCompound a(NBTTagCompound nbttagcompound) {
        Iterator iterator = nbttagcompound.tags.keySet().iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();
            NBTBase nbtbase = (NBTBase) nbttagcompound.tags.get(s);

            if (nbtbase.getTypeId() == 10) {
                if (this.hasKeyOfType(s, 10)) {
                    NBTTagCompound nbttagcompound1 = this.getCompound(s);

                    nbttagcompound1.a((NBTTagCompound) nbtbase);
                } else {
                    this.set(s, nbtbase.clone());
                }
            } else {
                this.set(s, nbtbase.clone());
            }
        }

        return this;
    }

    @Override
    public void a(TagVisitor tagvisitor) {
        tagvisitor.a(this);
    }

    protected Map<String, NBTBase> h() {
        return Collections.unmodifiableMap(this.tags);
    }
}
