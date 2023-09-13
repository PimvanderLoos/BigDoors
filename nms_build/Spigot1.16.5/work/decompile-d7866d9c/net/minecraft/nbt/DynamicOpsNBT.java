package net.minecraft.nbt;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.PeekingIterator;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.RecordBuilder.AbstractStringBuilder;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public class DynamicOpsNBT implements DynamicOps<NBTBase> {

    public static final DynamicOpsNBT a = new DynamicOpsNBT();

    protected DynamicOpsNBT() {}

    public NBTBase empty() {
        return NBTTagEnd.b;
    }

    public <U> U convertTo(DynamicOps<U> dynamicops, NBTBase nbtbase) {
        switch (nbtbase.getTypeId()) {
            case 0:
                return dynamicops.empty();
            case 1:
                return dynamicops.createByte(((NBTNumber) nbtbase).asByte());
            case 2:
                return dynamicops.createShort(((NBTNumber) nbtbase).asShort());
            case 3:
                return dynamicops.createInt(((NBTNumber) nbtbase).asInt());
            case 4:
                return dynamicops.createLong(((NBTNumber) nbtbase).asLong());
            case 5:
                return dynamicops.createFloat(((NBTNumber) nbtbase).asFloat());
            case 6:
                return dynamicops.createDouble(((NBTNumber) nbtbase).asDouble());
            case 7:
                return dynamicops.createByteList(ByteBuffer.wrap(((NBTTagByteArray) nbtbase).getBytes()));
            case 8:
                return dynamicops.createString(nbtbase.asString());
            case 9:
                return this.convertList(dynamicops, nbtbase);
            case 10:
                return this.convertMap(dynamicops, nbtbase);
            case 11:
                return dynamicops.createIntList(Arrays.stream(((NBTTagIntArray) nbtbase).getInts()));
            case 12:
                return dynamicops.createLongList(Arrays.stream(((NBTTagLongArray) nbtbase).getLongs()));
            default:
                throw new IllegalStateException("Unknown tag type: " + nbtbase);
        }
    }

    public DataResult<Number> getNumberValue(NBTBase nbtbase) {
        return nbtbase instanceof NBTNumber ? DataResult.success(((NBTNumber) nbtbase).k()) : DataResult.error("Not a number");
    }

    public NBTBase createNumeric(Number number) {
        return NBTTagDouble.a(number.doubleValue());
    }

    public NBTBase createByte(byte b0) {
        return NBTTagByte.a(b0);
    }

    public NBTBase createShort(short short0) {
        return NBTTagShort.a(short0);
    }

    public NBTBase createInt(int i) {
        return NBTTagInt.a(i);
    }

    public NBTBase createLong(long i) {
        return NBTTagLong.a(i);
    }

    public NBTBase createFloat(float f) {
        return NBTTagFloat.a(f);
    }

    public NBTBase createDouble(double d0) {
        return NBTTagDouble.a(d0);
    }

    public NBTBase createBoolean(boolean flag) {
        return NBTTagByte.a(flag);
    }

    public DataResult<String> getStringValue(NBTBase nbtbase) {
        return nbtbase instanceof NBTTagString ? DataResult.success(nbtbase.asString()) : DataResult.error("Not a string");
    }

    public NBTBase createString(String s) {
        return NBTTagString.a(s);
    }

    private static NBTList<?> a(byte b0, byte b1) {
        return (NBTList) (a(b0, b1, (byte) 4) ? new NBTTagLongArray(new long[0]) : (a(b0, b1, (byte) 1) ? new NBTTagByteArray(new byte[0]) : (a(b0, b1, (byte) 3) ? new NBTTagIntArray(new int[0]) : new NBTTagList())));
    }

    private static boolean a(byte b0, byte b1, byte b2) {
        return b0 == b2 && (b1 == b2 || b1 == 0);
    }

    private static <T extends NBTBase> void a(NBTList<T> nbtlist, NBTBase nbtbase, NBTBase nbtbase1) {
        if (nbtbase instanceof NBTList) {
            NBTList<?> nbtlist1 = (NBTList) nbtbase;

            nbtlist1.forEach((nbtbase2) -> {
                nbtlist.add(nbtbase2);
            });
        }

        nbtlist.add(nbtbase1);
    }

    private static <T extends NBTBase> void a(NBTList<T> nbtlist, NBTBase nbtbase, List<NBTBase> list) {
        if (nbtbase instanceof NBTList) {
            NBTList<?> nbtlist1 = (NBTList) nbtbase;

            nbtlist1.forEach((nbtbase1) -> {
                nbtlist.add(nbtbase1);
            });
        }

        list.forEach((nbtbase1) -> {
            nbtlist.add(nbtbase1);
        });
    }

    public DataResult<NBTBase> mergeToList(NBTBase nbtbase, NBTBase nbtbase1) {
        if (!(nbtbase instanceof NBTList) && !(nbtbase instanceof NBTTagEnd)) {
            return DataResult.error("mergeToList called with not a list: " + nbtbase, nbtbase);
        } else {
            NBTList<?> nbtlist = a(nbtbase instanceof NBTList ? ((NBTList) nbtbase).d_() : 0, nbtbase1.getTypeId());

            a(nbtlist, nbtbase, nbtbase1);
            return DataResult.success(nbtlist);
        }
    }

    public DataResult<NBTBase> mergeToList(NBTBase nbtbase, List<NBTBase> list) {
        if (!(nbtbase instanceof NBTList) && !(nbtbase instanceof NBTTagEnd)) {
            return DataResult.error("mergeToList called with not a list: " + nbtbase, nbtbase);
        } else {
            NBTList<?> nbtlist = a(nbtbase instanceof NBTList ? ((NBTList) nbtbase).d_() : 0, (Byte) list.stream().findFirst().map(NBTBase::getTypeId).orElse((byte) 0));

            a(nbtlist, nbtbase, list);
            return DataResult.success(nbtlist);
        }
    }

    public DataResult<NBTBase> mergeToMap(NBTBase nbtbase, NBTBase nbtbase1, NBTBase nbtbase2) {
        if (!(nbtbase instanceof NBTTagCompound) && !(nbtbase instanceof NBTTagEnd)) {
            return DataResult.error("mergeToMap called with not a map: " + nbtbase, nbtbase);
        } else if (!(nbtbase1 instanceof NBTTagString)) {
            return DataResult.error("key is not a string: " + nbtbase1, nbtbase);
        } else {
            NBTTagCompound nbttagcompound = new NBTTagCompound();

            if (nbtbase instanceof NBTTagCompound) {
                NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbtbase;

                nbttagcompound1.getKeys().forEach((s) -> {
                    nbttagcompound.set(s, nbttagcompound1.get(s));
                });
            }

            nbttagcompound.set(nbtbase1.asString(), nbtbase2);
            return DataResult.success(nbttagcompound);
        }
    }

    public DataResult<NBTBase> mergeToMap(NBTBase nbtbase, MapLike<NBTBase> maplike) {
        if (!(nbtbase instanceof NBTTagCompound) && !(nbtbase instanceof NBTTagEnd)) {
            return DataResult.error("mergeToMap called with not a map: " + nbtbase, nbtbase);
        } else {
            NBTTagCompound nbttagcompound = new NBTTagCompound();

            if (nbtbase instanceof NBTTagCompound) {
                NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbtbase;

                nbttagcompound1.getKeys().forEach((s) -> {
                    nbttagcompound.set(s, nbttagcompound1.get(s));
                });
            }

            List<NBTBase> list = Lists.newArrayList();

            maplike.entries().forEach((pair) -> {
                NBTBase nbtbase1 = (NBTBase) pair.getFirst();

                if (!(nbtbase1 instanceof NBTTagString)) {
                    list.add(nbtbase1);
                } else {
                    nbttagcompound.set(nbtbase1.asString(), (NBTBase) pair.getSecond());
                }
            });
            return !list.isEmpty() ? DataResult.error("some keys are not strings: " + list, nbttagcompound) : DataResult.success(nbttagcompound);
        }
    }

    public DataResult<Stream<Pair<NBTBase, NBTBase>>> getMapValues(NBTBase nbtbase) {
        if (!(nbtbase instanceof NBTTagCompound)) {
            return DataResult.error("Not a map: " + nbtbase);
        } else {
            NBTTagCompound nbttagcompound = (NBTTagCompound) nbtbase;

            return DataResult.success(nbttagcompound.getKeys().stream().map((s) -> {
                return Pair.of(this.createString(s), nbttagcompound.get(s));
            }));
        }
    }

    public DataResult<Consumer<BiConsumer<NBTBase, NBTBase>>> getMapEntries(NBTBase nbtbase) {
        if (!(nbtbase instanceof NBTTagCompound)) {
            return DataResult.error("Not a map: " + nbtbase);
        } else {
            NBTTagCompound nbttagcompound = (NBTTagCompound) nbtbase;

            return DataResult.success((biconsumer) -> {
                nbttagcompound.getKeys().forEach((s) -> {
                    biconsumer.accept(this.createString(s), nbttagcompound.get(s));
                });
            });
        }
    }

    public DataResult<MapLike<NBTBase>> getMap(NBTBase nbtbase) {
        if (!(nbtbase instanceof NBTTagCompound)) {
            return DataResult.error("Not a map: " + nbtbase);
        } else {
            final NBTTagCompound nbttagcompound = (NBTTagCompound) nbtbase;

            return DataResult.success(new MapLike<NBTBase>() {
                @Nullable
                public NBTBase get(NBTBase nbtbase1) {
                    return nbttagcompound.get(nbtbase1.asString());
                }

                @Nullable
                public NBTBase get(String s) {
                    return nbttagcompound.get(s);
                }

                public Stream<Pair<NBTBase, NBTBase>> entries() {
                    return nbttagcompound.getKeys().stream().map((s) -> {
                        return Pair.of(DynamicOpsNBT.this.createString(s), nbttagcompound.get(s));
                    });
                }

                public String toString() {
                    return "MapLike[" + nbttagcompound + "]";
                }
            });
        }
    }

    public NBTBase createMap(Stream<Pair<NBTBase, NBTBase>> stream) {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        stream.forEach((pair) -> {
            nbttagcompound.set(((NBTBase) pair.getFirst()).asString(), (NBTBase) pair.getSecond());
        });
        return nbttagcompound;
    }

    public DataResult<Stream<NBTBase>> getStream(NBTBase nbtbase) {
        return nbtbase instanceof NBTList ? DataResult.success(((NBTList) nbtbase).stream().map((nbtbase1) -> {
            return nbtbase1;
        })) : DataResult.error("Not a list");
    }

    public DataResult<Consumer<Consumer<NBTBase>>> getList(NBTBase nbtbase) {
        if (nbtbase instanceof NBTList) {
            NBTList<?> nbtlist = (NBTList) nbtbase;

            nbtlist.getClass();
            return DataResult.success(nbtlist::forEach);
        } else {
            return DataResult.error("Not a list: " + nbtbase);
        }
    }

    public DataResult<ByteBuffer> getByteBuffer(NBTBase nbtbase) {
        return nbtbase instanceof NBTTagByteArray ? DataResult.success(ByteBuffer.wrap(((NBTTagByteArray) nbtbase).getBytes())) : super.getByteBuffer(nbtbase);
    }

    public NBTBase createByteList(ByteBuffer bytebuffer) {
        return new NBTTagByteArray(DataFixUtils.toArray(bytebuffer));
    }

    public DataResult<IntStream> getIntStream(NBTBase nbtbase) {
        return nbtbase instanceof NBTTagIntArray ? DataResult.success(Arrays.stream(((NBTTagIntArray) nbtbase).getInts())) : super.getIntStream(nbtbase);
    }

    public NBTBase createIntList(IntStream intstream) {
        return new NBTTagIntArray(intstream.toArray());
    }

    public DataResult<LongStream> getLongStream(NBTBase nbtbase) {
        return nbtbase instanceof NBTTagLongArray ? DataResult.success(Arrays.stream(((NBTTagLongArray) nbtbase).getLongs())) : super.getLongStream(nbtbase);
    }

    public NBTBase createLongList(LongStream longstream) {
        return new NBTTagLongArray(longstream.toArray());
    }

    public NBTBase createList(Stream<NBTBase> stream) {
        PeekingIterator<NBTBase> peekingiterator = Iterators.peekingIterator(stream.iterator());

        if (!peekingiterator.hasNext()) {
            return new NBTTagList();
        } else {
            NBTBase nbtbase = (NBTBase) peekingiterator.peek();
            ArrayList arraylist;

            if (nbtbase instanceof NBTTagByte) {
                arraylist = Lists.newArrayList(Iterators.transform(peekingiterator, (nbtbase1) -> {
                    return ((NBTTagByte) nbtbase1).asByte();
                }));
                return new NBTTagByteArray(arraylist);
            } else if (nbtbase instanceof NBTTagInt) {
                arraylist = Lists.newArrayList(Iterators.transform(peekingiterator, (nbtbase1) -> {
                    return ((NBTTagInt) nbtbase1).asInt();
                }));
                return new NBTTagIntArray(arraylist);
            } else if (nbtbase instanceof NBTTagLong) {
                arraylist = Lists.newArrayList(Iterators.transform(peekingiterator, (nbtbase1) -> {
                    return ((NBTTagLong) nbtbase1).asLong();
                }));
                return new NBTTagLongArray(arraylist);
            } else {
                NBTTagList nbttaglist = new NBTTagList();

                while (peekingiterator.hasNext()) {
                    NBTBase nbtbase1 = (NBTBase) peekingiterator.next();

                    if (!(nbtbase1 instanceof NBTTagEnd)) {
                        nbttaglist.add(nbtbase1);
                    }
                }

                return nbttaglist;
            }
        }
    }

    public NBTBase remove(NBTBase nbtbase, String s) {
        if (nbtbase instanceof NBTTagCompound) {
            NBTTagCompound nbttagcompound = (NBTTagCompound) nbtbase;
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();

            nbttagcompound.getKeys().stream().filter((s1) -> {
                return !Objects.equals(s1, s);
            }).forEach((s1) -> {
                nbttagcompound1.set(s1, nbttagcompound.get(s1));
            });
            return nbttagcompound1;
        } else {
            return nbtbase;
        }
    }

    public String toString() {
        return "NBT";
    }

    public RecordBuilder<NBTBase> mapBuilder() {
        return new DynamicOpsNBT.a();
    }

    class a extends AbstractStringBuilder<NBTBase, NBTTagCompound> {

        protected a() {
            super(DynamicOpsNBT.this);
        }

        protected NBTTagCompound initBuilder() {
            return new NBTTagCompound();
        }

        protected NBTTagCompound append(String s, NBTBase nbtbase, NBTTagCompound nbttagcompound) {
            nbttagcompound.set(s, nbtbase);
            return nbttagcompound;
        }

        protected DataResult<NBTBase> build(NBTTagCompound nbttagcompound, NBTBase nbtbase) {
            if (nbtbase != null && nbtbase != NBTTagEnd.b) {
                if (!(nbtbase instanceof NBTTagCompound)) {
                    return DataResult.error("mergeToMap called with not a map: " + nbtbase, nbtbase);
                } else {
                    NBTTagCompound nbttagcompound1 = new NBTTagCompound(Maps.newHashMap(((NBTTagCompound) nbtbase).h()));
                    Iterator iterator = nbttagcompound.h().entrySet().iterator();

                    while (iterator.hasNext()) {
                        Entry<String, NBTBase> entry = (Entry) iterator.next();

                        nbttagcompound1.set((String) entry.getKey(), (NBTBase) entry.getValue());
                    }

                    return DataResult.success(nbttagcompound1);
                }
            } else {
                return DataResult.success(nbttagcompound);
            }
        }
    }
}
