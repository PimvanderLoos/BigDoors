package net.minecraft.nbt;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.RecordBuilder.AbstractStringBuilder;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public class DynamicOpsNBT implements DynamicOps<NBTBase> {

    public static final DynamicOpsNBT INSTANCE = new DynamicOpsNBT();
    private static final String WRAPPER_MARKER = "";

    protected DynamicOpsNBT() {}

    public NBTBase empty() {
        return NBTTagEnd.INSTANCE;
    }

    public <U> U convertTo(DynamicOps<U> dynamicops, NBTBase nbtbase) {
        switch (nbtbase.getId()) {
            case 0:
                return dynamicops.empty();
            case 1:
                return dynamicops.createByte(((NBTNumber) nbtbase).getAsByte());
            case 2:
                return dynamicops.createShort(((NBTNumber) nbtbase).getAsShort());
            case 3:
                return dynamicops.createInt(((NBTNumber) nbtbase).getAsInt());
            case 4:
                return dynamicops.createLong(((NBTNumber) nbtbase).getAsLong());
            case 5:
                return dynamicops.createFloat(((NBTNumber) nbtbase).getAsFloat());
            case 6:
                return dynamicops.createDouble(((NBTNumber) nbtbase).getAsDouble());
            case 7:
                return dynamicops.createByteList(ByteBuffer.wrap(((NBTTagByteArray) nbtbase).getAsByteArray()));
            case 8:
                return dynamicops.createString(nbtbase.getAsString());
            case 9:
                return this.convertList(dynamicops, nbtbase);
            case 10:
                return this.convertMap(dynamicops, nbtbase);
            case 11:
                return dynamicops.createIntList(Arrays.stream(((NBTTagIntArray) nbtbase).getAsIntArray()));
            case 12:
                return dynamicops.createLongList(Arrays.stream(((NBTTagLongArray) nbtbase).getAsLongArray()));
            default:
                throw new IllegalStateException("Unknown tag type: " + nbtbase);
        }
    }

    public DataResult<Number> getNumberValue(NBTBase nbtbase) {
        if (nbtbase instanceof NBTNumber) {
            NBTNumber nbtnumber = (NBTNumber) nbtbase;

            return DataResult.success(nbtnumber.getAsNumber());
        } else {
            return DataResult.error(() -> {
                return "Not a number";
            });
        }
    }

    public NBTBase createNumeric(Number number) {
        return NBTTagDouble.valueOf(number.doubleValue());
    }

    public NBTBase createByte(byte b0) {
        return NBTTagByte.valueOf(b0);
    }

    public NBTBase createShort(short short0) {
        return NBTTagShort.valueOf(short0);
    }

    public NBTBase createInt(int i) {
        return NBTTagInt.valueOf(i);
    }

    public NBTBase createLong(long i) {
        return NBTTagLong.valueOf(i);
    }

    public NBTBase createFloat(float f) {
        return NBTTagFloat.valueOf(f);
    }

    public NBTBase createDouble(double d0) {
        return NBTTagDouble.valueOf(d0);
    }

    public NBTBase createBoolean(boolean flag) {
        return NBTTagByte.valueOf(flag);
    }

    public DataResult<String> getStringValue(NBTBase nbtbase) {
        if (nbtbase instanceof NBTTagString) {
            NBTTagString nbttagstring = (NBTTagString) nbtbase;

            return DataResult.success(nbttagstring.getAsString());
        } else {
            return DataResult.error(() -> {
                return "Not a string";
            });
        }
    }

    public NBTBase createString(String s) {
        return NBTTagString.valueOf(s);
    }

    public DataResult<NBTBase> mergeToList(NBTBase nbtbase, NBTBase nbtbase1) {
        return (DataResult) createCollector(nbtbase).map((dynamicopsnbt_f) -> {
            return DataResult.success(dynamicopsnbt_f.accept(nbtbase1).result());
        }).orElseGet(() -> {
            return DataResult.error(() -> {
                return "mergeToList called with not a list: " + nbtbase;
            }, nbtbase);
        });
    }

    public DataResult<NBTBase> mergeToList(NBTBase nbtbase, List<NBTBase> list) {
        return (DataResult) createCollector(nbtbase).map((dynamicopsnbt_f) -> {
            return DataResult.success(dynamicopsnbt_f.acceptAll((Iterable) list).result());
        }).orElseGet(() -> {
            return DataResult.error(() -> {
                return "mergeToList called with not a list: " + nbtbase;
            }, nbtbase);
        });
    }

    public DataResult<NBTBase> mergeToMap(NBTBase nbtbase, NBTBase nbtbase1, NBTBase nbtbase2) {
        if (!(nbtbase instanceof NBTTagCompound) && !(nbtbase instanceof NBTTagEnd)) {
            return DataResult.error(() -> {
                return "mergeToMap called with not a map: " + nbtbase;
            }, nbtbase);
        } else if (!(nbtbase1 instanceof NBTTagString)) {
            return DataResult.error(() -> {
                return "key is not a string: " + nbtbase1;
            }, nbtbase);
        } else {
            NBTTagCompound nbttagcompound = new NBTTagCompound();

            if (nbtbase instanceof NBTTagCompound) {
                NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbtbase;

                nbttagcompound1.getAllKeys().forEach((s) -> {
                    nbttagcompound.put(s, nbttagcompound1.get(s));
                });
            }

            nbttagcompound.put(nbtbase1.getAsString(), nbtbase2);
            return DataResult.success(nbttagcompound);
        }
    }

    public DataResult<NBTBase> mergeToMap(NBTBase nbtbase, MapLike<NBTBase> maplike) {
        if (!(nbtbase instanceof NBTTagCompound) && !(nbtbase instanceof NBTTagEnd)) {
            return DataResult.error(() -> {
                return "mergeToMap called with not a map: " + nbtbase;
            }, nbtbase);
        } else {
            NBTTagCompound nbttagcompound = new NBTTagCompound();

            if (nbtbase instanceof NBTTagCompound) {
                NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbtbase;

                nbttagcompound1.getAllKeys().forEach((s) -> {
                    nbttagcompound.put(s, nbttagcompound1.get(s));
                });
            }

            List<NBTBase> list = Lists.newArrayList();

            maplike.entries().forEach((pair) -> {
                NBTBase nbtbase1 = (NBTBase) pair.getFirst();

                if (!(nbtbase1 instanceof NBTTagString)) {
                    list.add(nbtbase1);
                } else {
                    nbttagcompound.put(nbtbase1.getAsString(), (NBTBase) pair.getSecond());
                }
            });
            return !list.isEmpty() ? DataResult.error(() -> {
                return "some keys are not strings: " + list;
            }, nbttagcompound) : DataResult.success(nbttagcompound);
        }
    }

    public DataResult<Stream<Pair<NBTBase, NBTBase>>> getMapValues(NBTBase nbtbase) {
        if (nbtbase instanceof NBTTagCompound) {
            NBTTagCompound nbttagcompound = (NBTTagCompound) nbtbase;

            return DataResult.success(nbttagcompound.getAllKeys().stream().map((s) -> {
                return Pair.of(this.createString(s), nbttagcompound.get(s));
            }));
        } else {
            return DataResult.error(() -> {
                return "Not a map: " + nbtbase;
            });
        }
    }

    public DataResult<Consumer<BiConsumer<NBTBase, NBTBase>>> getMapEntries(NBTBase nbtbase) {
        if (nbtbase instanceof NBTTagCompound) {
            NBTTagCompound nbttagcompound = (NBTTagCompound) nbtbase;

            return DataResult.success((biconsumer) -> {
                nbttagcompound.getAllKeys().forEach((s) -> {
                    biconsumer.accept(this.createString(s), nbttagcompound.get(s));
                });
            });
        } else {
            return DataResult.error(() -> {
                return "Not a map: " + nbtbase;
            });
        }
    }

    public DataResult<MapLike<NBTBase>> getMap(NBTBase nbtbase) {
        if (nbtbase instanceof NBTTagCompound) {
            final NBTTagCompound nbttagcompound = (NBTTagCompound) nbtbase;

            return DataResult.success(new MapLike<NBTBase>() {
                @Nullable
                public NBTBase get(NBTBase nbtbase1) {
                    return nbttagcompound.get(nbtbase1.getAsString());
                }

                @Nullable
                public NBTBase get(String s) {
                    return nbttagcompound.get(s);
                }

                public Stream<Pair<NBTBase, NBTBase>> entries() {
                    return nbttagcompound.getAllKeys().stream().map((s) -> {
                        return Pair.of(DynamicOpsNBT.this.createString(s), nbttagcompound.get(s));
                    });
                }

                public String toString() {
                    return "MapLike[" + nbttagcompound + "]";
                }
            });
        } else {
            return DataResult.error(() -> {
                return "Not a map: " + nbtbase;
            });
        }
    }

    public NBTBase createMap(Stream<Pair<NBTBase, NBTBase>> stream) {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        stream.forEach((pair) -> {
            nbttagcompound.put(((NBTBase) pair.getFirst()).getAsString(), (NBTBase) pair.getSecond());
        });
        return nbttagcompound;
    }

    private static NBTBase tryUnwrap(NBTTagCompound nbttagcompound) {
        if (nbttagcompound.size() == 1) {
            NBTBase nbtbase = nbttagcompound.get("");

            if (nbtbase != null) {
                return nbtbase;
            }
        }

        return nbttagcompound;
    }

    public DataResult<Stream<NBTBase>> getStream(NBTBase nbtbase) {
        if (nbtbase instanceof NBTTagList) {
            NBTTagList nbttaglist = (NBTTagList) nbtbase;

            return nbttaglist.getElementType() == 10 ? DataResult.success(nbttaglist.stream().map((nbtbase1) -> {
                return tryUnwrap((NBTTagCompound) nbtbase1);
            })) : DataResult.success(nbttaglist.stream());
        } else if (nbtbase instanceof NBTList) {
            NBTList<?> nbtlist = (NBTList) nbtbase;

            return DataResult.success(nbtlist.stream().map((nbtbase1) -> {
                return nbtbase1;
            }));
        } else {
            return DataResult.error(() -> {
                return "Not a list";
            });
        }
    }

    public DataResult<Consumer<Consumer<NBTBase>>> getList(NBTBase nbtbase) {
        if (nbtbase instanceof NBTTagList) {
            NBTTagList nbttaglist = (NBTTagList) nbtbase;

            if (nbttaglist.getElementType() == 10) {
                return DataResult.success((consumer) -> {
                    nbttaglist.forEach((nbtbase1) -> {
                        consumer.accept(tryUnwrap((NBTTagCompound) nbtbase1));
                    });
                });
            } else {
                Objects.requireNonNull(nbttaglist);
                return DataResult.success(nbttaglist::forEach);
            }
        } else if (nbtbase instanceof NBTList) {
            NBTList<?> nbtlist = (NBTList) nbtbase;

            Objects.requireNonNull(nbtlist);
            return DataResult.success(nbtlist::forEach);
        } else {
            return DataResult.error(() -> {
                return "Not a list: " + nbtbase;
            });
        }
    }

    public DataResult<ByteBuffer> getByteBuffer(NBTBase nbtbase) {
        if (nbtbase instanceof NBTTagByteArray) {
            NBTTagByteArray nbttagbytearray = (NBTTagByteArray) nbtbase;

            return DataResult.success(ByteBuffer.wrap(nbttagbytearray.getAsByteArray()));
        } else {
            return super.getByteBuffer(nbtbase);
        }
    }

    public NBTBase createByteList(ByteBuffer bytebuffer) {
        return new NBTTagByteArray(DataFixUtils.toArray(bytebuffer));
    }

    public DataResult<IntStream> getIntStream(NBTBase nbtbase) {
        if (nbtbase instanceof NBTTagIntArray) {
            NBTTagIntArray nbttagintarray = (NBTTagIntArray) nbtbase;

            return DataResult.success(Arrays.stream(nbttagintarray.getAsIntArray()));
        } else {
            return super.getIntStream(nbtbase);
        }
    }

    public NBTBase createIntList(IntStream intstream) {
        return new NBTTagIntArray(intstream.toArray());
    }

    public DataResult<LongStream> getLongStream(NBTBase nbtbase) {
        if (nbtbase instanceof NBTTagLongArray) {
            NBTTagLongArray nbttaglongarray = (NBTTagLongArray) nbtbase;

            return DataResult.success(Arrays.stream(nbttaglongarray.getAsLongArray()));
        } else {
            return super.getLongStream(nbtbase);
        }
    }

    public NBTBase createLongList(LongStream longstream) {
        return new NBTTagLongArray(longstream.toArray());
    }

    public NBTBase createList(Stream<NBTBase> stream) {
        return DynamicOpsNBT.d.INSTANCE.acceptAll(stream).result();
    }

    public NBTBase remove(NBTBase nbtbase, String s) {
        if (nbtbase instanceof NBTTagCompound) {
            NBTTagCompound nbttagcompound = (NBTTagCompound) nbtbase;
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();

            nbttagcompound.getAllKeys().stream().filter((s1) -> {
                return !Objects.equals(s1, s);
            }).forEach((s1) -> {
                nbttagcompound1.put(s1, nbttagcompound.get(s1));
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
        return new DynamicOpsNBT.h();
    }

    private static Optional<DynamicOpsNBT.f> createCollector(NBTBase nbtbase) {
        if (nbtbase instanceof NBTTagEnd) {
            return Optional.of(DynamicOpsNBT.d.INSTANCE);
        } else {
            if (nbtbase instanceof NBTList) {
                NBTList<?> nbtlist = (NBTList) nbtbase;

                if (nbtlist.isEmpty()) {
                    return Optional.of(DynamicOpsNBT.d.INSTANCE);
                }

                if (nbtlist instanceof NBTTagList) {
                    NBTTagList nbttaglist = (NBTTagList) nbtlist;
                    Optional optional;

                    switch (nbttaglist.getElementType()) {
                        case 0:
                            optional = Optional.of(DynamicOpsNBT.d.INSTANCE);
                            break;
                        case 10:
                            optional = Optional.of(new DynamicOpsNBT.b(nbttaglist));
                            break;
                        default:
                            optional = Optional.of(new DynamicOpsNBT.c(nbttaglist));
                    }

                    return optional;
                }

                if (nbtlist instanceof NBTTagByteArray) {
                    NBTTagByteArray nbttagbytearray = (NBTTagByteArray) nbtlist;

                    return Optional.of(new DynamicOpsNBT.a(nbttagbytearray.getAsByteArray()));
                }

                if (nbtlist instanceof NBTTagIntArray) {
                    NBTTagIntArray nbttagintarray = (NBTTagIntArray) nbtlist;

                    return Optional.of(new DynamicOpsNBT.e(nbttagintarray.getAsIntArray()));
                }

                if (nbtlist instanceof NBTTagLongArray) {
                    NBTTagLongArray nbttaglongarray = (NBTTagLongArray) nbtlist;

                    return Optional.of(new DynamicOpsNBT.g(nbttaglongarray.getAsLongArray()));
                }
            }

            return Optional.empty();
        }
    }

    private static class d implements DynamicOpsNBT.f {

        public static final DynamicOpsNBT.d INSTANCE = new DynamicOpsNBT.d();

        private d() {}

        @Override
        public DynamicOpsNBT.f accept(NBTBase nbtbase) {
            if (nbtbase instanceof NBTTagCompound) {
                NBTTagCompound nbttagcompound = (NBTTagCompound) nbtbase;

                return (new DynamicOpsNBT.b()).accept(nbttagcompound);
            } else if (nbtbase instanceof NBTTagByte) {
                NBTTagByte nbttagbyte = (NBTTagByte) nbtbase;

                return new DynamicOpsNBT.a(nbttagbyte.getAsByte());
            } else if (nbtbase instanceof NBTTagInt) {
                NBTTagInt nbttagint = (NBTTagInt) nbtbase;

                return new DynamicOpsNBT.e(nbttagint.getAsInt());
            } else if (nbtbase instanceof NBTTagLong) {
                NBTTagLong nbttaglong = (NBTTagLong) nbtbase;

                return new DynamicOpsNBT.g(nbttaglong.getAsLong());
            } else {
                return new DynamicOpsNBT.c(nbtbase);
            }
        }

        @Override
        public NBTBase result() {
            return new NBTTagList();
        }
    }

    private interface f {

        DynamicOpsNBT.f accept(NBTBase nbtbase);

        default DynamicOpsNBT.f acceptAll(Iterable<NBTBase> iterable) {
            DynamicOpsNBT.f dynamicopsnbt_f = this;

            NBTBase nbtbase;

            for (Iterator iterator = iterable.iterator(); iterator.hasNext(); dynamicopsnbt_f = dynamicopsnbt_f.accept(nbtbase)) {
                nbtbase = (NBTBase) iterator.next();
            }

            return dynamicopsnbt_f;
        }

        default DynamicOpsNBT.f acceptAll(Stream<NBTBase> stream) {
            Objects.requireNonNull(stream);
            return this.acceptAll(stream::iterator);
        }

        NBTBase result();
    }

    private class h extends AbstractStringBuilder<NBTBase, NBTTagCompound> {

        protected h() {
            super(DynamicOpsNBT.this);
        }

        protected NBTTagCompound initBuilder() {
            return new NBTTagCompound();
        }

        protected NBTTagCompound append(String s, NBTBase nbtbase, NBTTagCompound nbttagcompound) {
            nbttagcompound.put(s, nbtbase);
            return nbttagcompound;
        }

        protected DataResult<NBTBase> build(NBTTagCompound nbttagcompound, NBTBase nbtbase) {
            if (nbtbase != null && nbtbase != NBTTagEnd.INSTANCE) {
                if (!(nbtbase instanceof NBTTagCompound)) {
                    return DataResult.error(() -> {
                        return "mergeToMap called with not a map: " + nbtbase;
                    }, nbtbase);
                } else {
                    NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbtbase;
                    NBTTagCompound nbttagcompound2 = new NBTTagCompound(Maps.newHashMap(nbttagcompound1.entries()));
                    Iterator iterator = nbttagcompound.entries().entrySet().iterator();

                    while (iterator.hasNext()) {
                        Entry<String, NBTBase> entry = (Entry) iterator.next();

                        nbttagcompound2.put((String) entry.getKey(), (NBTBase) entry.getValue());
                    }

                    return DataResult.success(nbttagcompound2);
                }
            } else {
                return DataResult.success(nbttagcompound);
            }
        }
    }

    private static class b implements DynamicOpsNBT.f {

        private final NBTTagList result = new NBTTagList();

        public b() {}

        public b(Collection<NBTBase> collection) {
            this.result.addAll(collection);
        }

        public b(IntArrayList intarraylist) {
            intarraylist.forEach((i) -> {
                this.result.add(wrapElement(NBTTagInt.valueOf(i)));
            });
        }

        public b(ByteArrayList bytearraylist) {
            bytearraylist.forEach((b0) -> {
                this.result.add(wrapElement(NBTTagByte.valueOf(b0)));
            });
        }

        public b(LongArrayList longarraylist) {
            longarraylist.forEach((i) -> {
                this.result.add(wrapElement(NBTTagLong.valueOf(i)));
            });
        }

        private static boolean isWrapper(NBTTagCompound nbttagcompound) {
            return nbttagcompound.size() == 1 && nbttagcompound.contains("");
        }

        private static NBTBase wrapIfNeeded(NBTBase nbtbase) {
            if (nbtbase instanceof NBTTagCompound) {
                NBTTagCompound nbttagcompound = (NBTTagCompound) nbtbase;

                if (!isWrapper(nbttagcompound)) {
                    return nbttagcompound;
                }
            }

            return wrapElement(nbtbase);
        }

        private static NBTTagCompound wrapElement(NBTBase nbtbase) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();

            nbttagcompound.put("", nbtbase);
            return nbttagcompound;
        }

        @Override
        public DynamicOpsNBT.f accept(NBTBase nbtbase) {
            this.result.add(wrapIfNeeded(nbtbase));
            return this;
        }

        @Override
        public NBTBase result() {
            return this.result;
        }
    }

    private static class c implements DynamicOpsNBT.f {

        private final NBTTagList result = new NBTTagList();

        c(NBTBase nbtbase) {
            this.result.add(nbtbase);
        }

        c(NBTTagList nbttaglist) {
            this.result.addAll(nbttaglist);
        }

        @Override
        public DynamicOpsNBT.f accept(NBTBase nbtbase) {
            if (nbtbase.getId() != this.result.getElementType()) {
                return (new DynamicOpsNBT.b()).acceptAll((Iterable) this.result).accept(nbtbase);
            } else {
                this.result.add(nbtbase);
                return this;
            }
        }

        @Override
        public NBTBase result() {
            return this.result;
        }
    }

    private static class a implements DynamicOpsNBT.f {

        private final ByteArrayList values = new ByteArrayList();

        public a(byte b0) {
            this.values.add(b0);
        }

        public a(byte[] abyte) {
            this.values.addElements(0, abyte);
        }

        @Override
        public DynamicOpsNBT.f accept(NBTBase nbtbase) {
            if (nbtbase instanceof NBTTagByte) {
                NBTTagByte nbttagbyte = (NBTTagByte) nbtbase;

                this.values.add(nbttagbyte.getAsByte());
                return this;
            } else {
                return (new DynamicOpsNBT.b(this.values)).accept(nbtbase);
            }
        }

        @Override
        public NBTBase result() {
            return new NBTTagByteArray(this.values.toByteArray());
        }
    }

    private static class e implements DynamicOpsNBT.f {

        private final IntArrayList values = new IntArrayList();

        public e(int i) {
            this.values.add(i);
        }

        public e(int[] aint) {
            this.values.addElements(0, aint);
        }

        @Override
        public DynamicOpsNBT.f accept(NBTBase nbtbase) {
            if (nbtbase instanceof NBTTagInt) {
                NBTTagInt nbttagint = (NBTTagInt) nbtbase;

                this.values.add(nbttagint.getAsInt());
                return this;
            } else {
                return (new DynamicOpsNBT.b(this.values)).accept(nbtbase);
            }
        }

        @Override
        public NBTBase result() {
            return new NBTTagIntArray(this.values.toIntArray());
        }
    }

    private static class g implements DynamicOpsNBT.f {

        private final LongArrayList values = new LongArrayList();

        public g(long i) {
            this.values.add(i);
        }

        public g(long[] along) {
            this.values.addElements(0, along);
        }

        @Override
        public DynamicOpsNBT.f accept(NBTBase nbtbase) {
            if (nbtbase instanceof NBTTagLong) {
                NBTTagLong nbttaglong = (NBTTagLong) nbtbase;

                this.values.add(nbttaglong.getAsLong());
                return this;
            } else {
                return (new DynamicOpsNBT.b(this.values)).accept(nbtbase);
            }
        }

        @Override
        public NBTBase result() {
            return new NBTTagLongArray(this.values.toLongArray());
        }
    }
}
