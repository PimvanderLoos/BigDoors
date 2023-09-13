package net.minecraft.server;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class DynamicOpsNBT implements DynamicOps<NBTBase> {

    public static final DynamicOpsNBT a = new DynamicOpsNBT();

    protected DynamicOpsNBT() {}

    public NBTBase a() {
        return new NBTTagEnd();
    }

    public Type<?> a(NBTBase nbtbase) {
        switch (nbtbase.getTypeId()) {
        case 0:
            return DSL.nilType();

        case 1:
            return DSL.byteType();

        case 2:
            return DSL.shortType();

        case 3:
            return DSL.intType();

        case 4:
            return DSL.longType();

        case 5:
            return DSL.floatType();

        case 6:
            return DSL.doubleType();

        case 7:
            return DSL.list(DSL.byteType());

        case 8:
            return DSL.string();

        case 9:
            return DSL.list(DSL.remainderType());

        case 10:
            return DSL.compoundList(DSL.remainderType(), DSL.remainderType());

        case 11:
            return DSL.list(DSL.intType());

        case 12:
            return DSL.list(DSL.longType());

        default:
            return DSL.remainderType();
        }
    }

    public Optional<Number> b(NBTBase nbtbase) {
        return nbtbase instanceof NBTNumber ? Optional.of(((NBTNumber) nbtbase).j()) : Optional.empty();
    }

    public NBTBase a(Number number) {
        return new NBTTagDouble(number.doubleValue());
    }

    public NBTBase a(byte b0) {
        return new NBTTagByte(b0);
    }

    public NBTBase a(short short0) {
        return new NBTTagShort(short0);
    }

    public NBTBase a(int i) {
        return new NBTTagInt(i);
    }

    public NBTBase a(long i) {
        return new NBTTagLong(i);
    }

    public NBTBase a(float f) {
        return new NBTTagFloat(f);
    }

    public NBTBase a(double d0) {
        return new NBTTagDouble(d0);
    }

    public Optional<String> c(NBTBase nbtbase) {
        return nbtbase instanceof NBTTagString ? Optional.of(nbtbase.b_()) : Optional.empty();
    }

    public NBTBase a(String s) {
        return new NBTTagString(s);
    }

    public NBTBase a(NBTBase nbtbase, NBTBase nbtbase1) {
        if (nbtbase1 instanceof NBTTagEnd) {
            return nbtbase;
        } else if (!(nbtbase instanceof NBTTagCompound)) {
            if (nbtbase instanceof NBTTagEnd) {
                throw new IllegalArgumentException("mergeInto called with a null input.");
            } else if (nbtbase instanceof NBTList) {
                NBTTagList nbttaglist = new NBTTagList();
                NBTList nbtlist = (NBTList) nbtbase;

                nbttaglist.addAll(nbtlist);
                nbttaglist.add(nbtbase1);
                return nbttaglist;
            } else {
                return nbtbase;
            }
        } else if (!(nbtbase1 instanceof NBTTagCompound)) {
            return nbtbase;
        } else {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbtbase;
            Iterator iterator = nbttagcompound1.getKeys().iterator();

            while (iterator.hasNext()) {
                String s = (String) iterator.next();

                nbttagcompound.set(s, nbttagcompound1.get(s));
            }

            NBTTagCompound nbttagcompound2 = (NBTTagCompound) nbtbase1;
            Iterator iterator1 = nbttagcompound2.getKeys().iterator();

            while (iterator1.hasNext()) {
                String s1 = (String) iterator1.next();

                nbttagcompound.set(s1, nbttagcompound2.get(s1));
            }

            return nbttagcompound;
        }
    }

    public NBTBase a(NBTBase nbtbase, NBTBase nbtbase1, NBTBase nbtbase2) {
        NBTTagCompound nbttagcompound;

        if (nbtbase instanceof NBTTagEnd) {
            nbttagcompound = new NBTTagCompound();
        } else {
            if (!(nbtbase instanceof NBTTagCompound)) {
                return nbtbase;
            }

            NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbtbase;

            nbttagcompound = new NBTTagCompound();
            nbttagcompound1.getKeys().forEach((s) -> {
                nbttagcompound.set(s, nbttagcompound1.get(s));
            });
        }

        nbttagcompound.set(nbtbase1.b_(), nbtbase2);
        return nbttagcompound;
    }

    public NBTBase b(NBTBase nbtbase, NBTBase nbtbase1) {
        if (nbtbase instanceof NBTTagEnd) {
            return nbtbase1;
        } else if (nbtbase1 instanceof NBTTagEnd) {
            return nbtbase;
        } else {
            if (nbtbase instanceof NBTTagCompound && nbtbase1 instanceof NBTTagCompound) {
                NBTTagCompound nbttagcompound = (NBTTagCompound) nbtbase;
                NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbtbase1;
                NBTTagCompound nbttagcompound2 = new NBTTagCompound();

                nbttagcompound.getKeys().forEach((s) -> {
                    nbttagcompound.set(s, nbttagcompound1.get(s));
                });
                nbttagcompound1.getKeys().forEach((s) -> {
                    nbttagcompound.set(s, nbttagcompound1.get(s));
                });
            }

            if (nbtbase instanceof NBTList && nbtbase1 instanceof NBTList) {
                NBTTagList nbttaglist = new NBTTagList();

                nbttaglist.addAll((NBTList) nbtbase);
                nbttaglist.addAll((NBTList) nbtbase1);
                return nbttaglist;
            } else {
                throw new IllegalArgumentException("Could not merge " + nbtbase + " and " + nbtbase1);
            }
        }
    }

    public Optional<Map<NBTBase, NBTBase>> d(NBTBase nbtbase) {
        if (nbtbase instanceof NBTTagCompound) {
            NBTTagCompound nbttagcompound = (NBTTagCompound) nbtbase;

            return Optional.of(nbttagcompound.getKeys().stream().map((s) -> {
                return Pair.of(this.a(s), nbttagcompound.get(s));
            }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
        } else {
            return Optional.empty();
        }
    }

    public NBTBase a(Map<NBTBase, NBTBase> map) {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            nbttagcompound.set(((NBTBase) entry.getKey()).b_(), (NBTBase) entry.getValue());
        }

        return nbttagcompound;
    }

    public Optional<Stream<NBTBase>> e(NBTBase nbtbase) {
        return nbtbase instanceof NBTList ? Optional.of(((NBTList) nbtbase).stream().map((nbtbase) -> {
            return nbtbase;
        })) : Optional.empty();
    }

    public Optional<ByteBuffer> f(NBTBase nbtbase) {
        return nbtbase instanceof NBTTagByteArray ? Optional.of(ByteBuffer.wrap(((NBTTagByteArray) nbtbase).c())) : super.getByteBuffer(nbtbase);
    }

    public NBTBase a(ByteBuffer bytebuffer) {
        return new NBTTagByteArray(DataFixUtils.toArray(bytebuffer));
    }

    public Optional<IntStream> g(NBTBase nbtbase) {
        return nbtbase instanceof NBTTagIntArray ? Optional.of(Arrays.stream(((NBTTagIntArray) nbtbase).d())) : super.getIntStream(nbtbase);
    }

    public NBTBase a(IntStream intstream) {
        return new NBTTagIntArray(intstream.toArray());
    }

    public Optional<LongStream> h(NBTBase nbtbase) {
        return nbtbase instanceof NBTTagLongArray ? Optional.of(Arrays.stream(((NBTTagLongArray) nbtbase).d())) : super.getLongStream(nbtbase);
    }

    public NBTBase a(LongStream longstream) {
        return new NBTTagLongArray(longstream.toArray());
    }

    public NBTBase a(Stream<NBTBase> stream) {
        PeekingIterator peekingiterator = Iterators.peekingIterator(stream.iterator());

        if (!peekingiterator.hasNext()) {
            return new NBTTagList();
        } else {
            NBTBase nbtbase = (NBTBase) peekingiterator.peek();
            ArrayList arraylist;

            if (nbtbase instanceof NBTTagByte) {
                arraylist = Lists.newArrayList(Iterators.transform(peekingiterator, (nbtbase) -> {
                    return Byte.valueOf(((NBTTagByte) nbtbase).g());
                }));
                return new NBTTagByteArray(arraylist);
            } else if (nbtbase instanceof NBTTagInt) {
                arraylist = Lists.newArrayList(Iterators.transform(peekingiterator, (nbtbase) -> {
                    return Integer.valueOf(((NBTTagInt) nbtbase).e());
                }));
                return new NBTTagIntArray(arraylist);
            } else if (nbtbase instanceof NBTTagLong) {
                arraylist = Lists.newArrayList(Iterators.transform(peekingiterator, (nbtbase) -> {
                    return Long.valueOf(((NBTTagLong) nbtbase).d());
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

    public NBTBase a(NBTBase nbtbase, String s) {
        if (nbtbase instanceof NBTTagCompound) {
            NBTTagCompound nbttagcompound = (NBTTagCompound) nbtbase;
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();

            nbttagcompound.getKeys().stream().filter((s) -> {
                return !Objects.equals(s, s1);
            }).forEach((s) -> {
                nbttagcompound.set(s, nbttagcompound1.get(s));
            });
            return nbttagcompound1;
        } else {
            return nbtbase;
        }
    }

    public String toString() {
        return "NBT";
    }

    public Object remove(Object object, String s) {
        return this.a((NBTBase) object, s);
    }

    public Object createLongList(LongStream longstream) {
        return this.a(longstream);
    }

    public Optional getLongStream(Object object) {
        return this.h((NBTBase) object);
    }

    public Object createIntList(IntStream intstream) {
        return this.a(intstream);
    }

    public Optional getIntStream(Object object) {
        return this.g((NBTBase) object);
    }

    public Object createByteList(ByteBuffer bytebuffer) {
        return this.a(bytebuffer);
    }

    public Optional getByteBuffer(Object object) {
        return this.f((NBTBase) object);
    }

    public Object createList(Stream stream) {
        return this.a(stream);
    }

    public Optional getStream(Object object) {
        return this.e((NBTBase) object);
    }

    public Object createMap(Map map) {
        return this.a(map);
    }

    public Optional getMapValues(Object object) {
        return this.d((NBTBase) object);
    }

    public Object merge(Object object, Object object1) {
        return this.b((NBTBase) object, (NBTBase) object1);
    }

    public Object mergeInto(Object object, Object object1, Object object2) {
        return this.a((NBTBase) object, (NBTBase) object1, (NBTBase) object2);
    }

    public Object mergeInto(Object object, Object object1) {
        return this.a((NBTBase) object, (NBTBase) object1);
    }

    public Object createString(String s) {
        return this.a(s);
    }

    public Optional getStringValue(Object object) {
        return this.c((NBTBase) object);
    }

    public Object createDouble(double d0) {
        return this.a(d0);
    }

    public Object createFloat(float f) {
        return this.a(f);
    }

    public Object createLong(long i) {
        return this.a(i);
    }

    public Object createInt(int i) {
        return this.a(i);
    }

    public Object createShort(short short0) {
        return this.a(short0);
    }

    public Object createByte(byte b0) {
        return this.a(b0);
    }

    public Object createNumeric(Number number) {
        return this.a(number);
    }

    public Optional getNumberValue(Object object) {
        return this.b((NBTBase) object);
    }

    public Type getType(Object object) {
        return this.a((NBTBase) object);
    }

    public Object empty() {
        return this.a();
    }
}
