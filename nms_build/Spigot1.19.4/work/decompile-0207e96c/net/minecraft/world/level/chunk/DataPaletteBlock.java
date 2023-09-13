package net.minecraft.world.level.chunk;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.IntUnaryOperator;
import java.util.function.Predicate;
import java.util.stream.LongStream;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.util.DataBits;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.MathHelper;
import net.minecraft.util.SimpleBitStorage;
import net.minecraft.util.ThreadingDetector;
import net.minecraft.util.ZeroBitStorage;

public class DataPaletteBlock<T> implements DataPaletteExpandable<T>, PalettedContainerRO<T> {

    private static final int MIN_PALETTE_BITS = 0;
    private final DataPaletteExpandable<T> dummyPaletteResize = (i, object) -> {
        return 0;
    };
    public final Registry<T> registry;
    private volatile DataPaletteBlock.c<T> data;
    private final DataPaletteBlock.d strategy;
    private final ThreadingDetector threadingDetector = new ThreadingDetector("PalettedContainer");

    public void acquire() {
        this.threadingDetector.checkAndLock();
    }

    public void release() {
        this.threadingDetector.checkAndUnlock();
    }

    public static <T> Codec<DataPaletteBlock<T>> codecRW(Registry<T> registry, Codec<T> codec, DataPaletteBlock.d datapaletteblock_d, T t0) {
        PalettedContainerRO.b<T, DataPaletteBlock<T>> palettedcontainerro_b = DataPaletteBlock::unpack;

        return codec(registry, codec, datapaletteblock_d, t0, palettedcontainerro_b);
    }

    public static <T> Codec<PalettedContainerRO<T>> codecRO(Registry<T> registry, Codec<T> codec, DataPaletteBlock.d datapaletteblock_d, T t0) {
        PalettedContainerRO.b<T, PalettedContainerRO<T>> palettedcontainerro_b = (registry1, datapaletteblock_d1, palettedcontainerro_a) -> {
            return unpack(registry1, datapaletteblock_d1, palettedcontainerro_a).map((datapaletteblock) -> {
                return datapaletteblock;
            });
        };

        return codec(registry, codec, datapaletteblock_d, t0, palettedcontainerro_b);
    }

    private static <T, C extends PalettedContainerRO<T>> Codec<C> codec(Registry<T> registry, Codec<T> codec, DataPaletteBlock.d datapaletteblock_d, T t0, PalettedContainerRO.b<T, C> palettedcontainerro_b) {
        return RecordCodecBuilder.create((instance) -> {
            return instance.group(codec.mapResult(ExtraCodecs.orElsePartial(t0)).listOf().fieldOf("palette").forGetter(PalettedContainerRO.a::paletteEntries), Codec.LONG_STREAM.optionalFieldOf("data").forGetter(PalettedContainerRO.a::storage)).apply(instance, PalettedContainerRO.a::new);
        }).comapFlatMap((palettedcontainerro_a) -> {
            return palettedcontainerro_b.read(registry, datapaletteblock_d, palettedcontainerro_a);
        }, (palettedcontainerro) -> {
            return palettedcontainerro.pack(registry, datapaletteblock_d);
        });
    }

    public DataPaletteBlock(Registry<T> registry, DataPaletteBlock.d datapaletteblock_d, DataPaletteBlock.a<T> datapaletteblock_a, DataBits databits, List<T> list) {
        this.registry = registry;
        this.strategy = datapaletteblock_d;
        this.data = new DataPaletteBlock.c<>(datapaletteblock_a, databits, datapaletteblock_a.factory().create(datapaletteblock_a.bits(), registry, this, list));
    }

    private DataPaletteBlock(Registry<T> registry, DataPaletteBlock.d datapaletteblock_d, DataPaletteBlock.c<T> datapaletteblock_c) {
        this.registry = registry;
        this.strategy = datapaletteblock_d;
        this.data = datapaletteblock_c;
    }

    public DataPaletteBlock(Registry<T> registry, T t0, DataPaletteBlock.d datapaletteblock_d) {
        this.strategy = datapaletteblock_d;
        this.registry = registry;
        this.data = this.createOrReuseData((DataPaletteBlock.c) null, 0);
        this.data.palette.idFor(t0);
    }

    private DataPaletteBlock.c<T> createOrReuseData(@Nullable DataPaletteBlock.c<T> datapaletteblock_c, int i) {
        DataPaletteBlock.a<T> datapaletteblock_a = this.strategy.getConfiguration(this.registry, i);

        return datapaletteblock_c != null && datapaletteblock_a.equals(datapaletteblock_c.configuration()) ? datapaletteblock_c : datapaletteblock_a.createData(this.registry, this, this.strategy.size());
    }

    @Override
    public int onResize(int i, T t0) {
        DataPaletteBlock.c<T> datapaletteblock_c = this.data;
        DataPaletteBlock.c<T> datapaletteblock_c1 = this.createOrReuseData(datapaletteblock_c, i);

        datapaletteblock_c1.copyFrom(datapaletteblock_c.palette, datapaletteblock_c.storage);
        this.data = datapaletteblock_c1;
        return datapaletteblock_c1.palette.idFor(t0);
    }

    public T getAndSet(int i, int j, int k, T t0) {
        this.acquire();

        Object object;

        try {
            object = this.getAndSet(this.strategy.getIndex(i, j, k), t0);
        } finally {
            this.release();
        }

        return object;
    }

    public T getAndSetUnchecked(int i, int j, int k, T t0) {
        return this.getAndSet(this.strategy.getIndex(i, j, k), t0);
    }

    private T getAndSet(int i, T t0) {
        int j = this.data.palette.idFor(t0);
        int k = this.data.storage.getAndSet(i, j);

        return this.data.palette.valueFor(k);
    }

    public void set(int i, int j, int k, T t0) {
        this.acquire();

        try {
            this.set(this.strategy.getIndex(i, j, k), t0);
        } finally {
            this.release();
        }

    }

    private void set(int i, T t0) {
        int j = this.data.palette.idFor(t0);

        this.data.storage.set(i, j);
    }

    @Override
    public T get(int i, int j, int k) {
        return this.get(this.strategy.getIndex(i, j, k));
    }

    protected T get(int i) {
        DataPaletteBlock.c<T> datapaletteblock_c = this.data;

        return datapaletteblock_c.palette.valueFor(datapaletteblock_c.storage.get(i));
    }

    @Override
    public void getAll(Consumer<T> consumer) {
        DataPalette<T> datapalette = this.data.palette();
        IntArraySet intarrayset = new IntArraySet();
        DataBits databits = this.data.storage;

        Objects.requireNonNull(intarrayset);
        databits.getAll(intarrayset::add);
        intarrayset.forEach((i) -> {
            consumer.accept(datapalette.valueFor(i));
        });
    }

    public void read(PacketDataSerializer packetdataserializer) {
        this.acquire();

        try {
            byte b0 = packetdataserializer.readByte();
            DataPaletteBlock.c<T> datapaletteblock_c = this.createOrReuseData(this.data, b0);

            datapaletteblock_c.palette.read(packetdataserializer);
            packetdataserializer.readLongArray(datapaletteblock_c.storage.getRaw());
            this.data = datapaletteblock_c;
        } finally {
            this.release();
        }

    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        this.acquire();

        try {
            this.data.write(packetdataserializer);
        } finally {
            this.release();
        }

    }

    private static <T> DataResult<DataPaletteBlock<T>> unpack(Registry<T> registry, DataPaletteBlock.d datapaletteblock_d, PalettedContainerRO.a<T> palettedcontainerro_a) {
        List<T> list = palettedcontainerro_a.paletteEntries();
        int i = datapaletteblock_d.size();
        int j = datapaletteblock_d.calculateBitsForSerialization(registry, list.size());
        DataPaletteBlock.a<T> datapaletteblock_a = datapaletteblock_d.getConfiguration(registry, j);
        Object object;

        if (j == 0) {
            object = new ZeroBitStorage(i);
        } else {
            Optional<LongStream> optional = palettedcontainerro_a.storage();

            if (optional.isEmpty()) {
                return DataResult.error(() -> {
                    return "Missing values for non-zero storage";
                });
            }

            long[] along = ((LongStream) optional.get()).toArray();

            try {
                if (datapaletteblock_a.factory() == DataPaletteBlock.d.GLOBAL_PALETTE_FACTORY) {
                    DataPalette<T> datapalette = new DataPaletteHash<>(registry, j, (k, object1) -> {
                        return 0;
                    }, list);
                    SimpleBitStorage simplebitstorage = new SimpleBitStorage(j, i, along);
                    int[] aint = new int[i];

                    simplebitstorage.unpack(aint);
                    swapPalette(aint, (k) -> {
                        return registry.getId(datapalette.valueFor(k));
                    });
                    object = new SimpleBitStorage(datapaletteblock_a.bits(), i, aint);
                } else {
                    object = new SimpleBitStorage(datapaletteblock_a.bits(), i, along);
                }
            } catch (SimpleBitStorage.a simplebitstorage_a) {
                return DataResult.error(() -> {
                    return "Failed to read PalettedContainer: " + simplebitstorage_a.getMessage();
                });
            }
        }

        return DataResult.success(new DataPaletteBlock<>(registry, datapaletteblock_d, datapaletteblock_a, (DataBits) object, list));
    }

    @Override
    public PalettedContainerRO.a<T> pack(Registry<T> registry, DataPaletteBlock.d datapaletteblock_d) {
        this.acquire();

        PalettedContainerRO.a palettedcontainerro_a;

        try {
            DataPaletteHash<T> datapalettehash = new DataPaletteHash<>(registry, this.data.storage.getBits(), this.dummyPaletteResize);
            int i = datapaletteblock_d.size();
            int[] aint = new int[i];

            this.data.storage.unpack(aint);
            swapPalette(aint, (j) -> {
                return datapalettehash.idFor(this.data.palette.valueFor(j));
            });
            int j = datapaletteblock_d.calculateBitsForSerialization(registry, datapalettehash.getSize());
            Optional optional;

            if (j != 0) {
                SimpleBitStorage simplebitstorage = new SimpleBitStorage(j, i, aint);

                optional = Optional.of(Arrays.stream(simplebitstorage.getRaw()));
            } else {
                optional = Optional.empty();
            }

            palettedcontainerro_a = new PalettedContainerRO.a<>(datapalettehash.getEntries(), optional);
        } finally {
            this.release();
        }

        return palettedcontainerro_a;
    }

    private static <T> void swapPalette(int[] aint, IntUnaryOperator intunaryoperator) {
        int i = -1;
        int j = -1;

        for (int k = 0; k < aint.length; ++k) {
            int l = aint[k];

            if (l != i) {
                i = l;
                j = intunaryoperator.applyAsInt(l);
            }

            aint[k] = j;
        }

    }

    @Override
    public int getSerializedSize() {
        return this.data.getSerializedSize();
    }

    @Override
    public boolean maybeHas(Predicate<T> predicate) {
        return this.data.palette.maybeHas(predicate);
    }

    public DataPaletteBlock<T> copy() {
        return new DataPaletteBlock<>(this.registry, this.strategy, this.data.copy());
    }

    @Override
    public DataPaletteBlock<T> recreate() {
        return new DataPaletteBlock<>(this.registry, this.data.palette.valueFor(0), this.strategy);
    }

    @Override
    public void count(DataPaletteBlock.b<T> datapaletteblock_b) {
        if (this.data.palette.getSize() == 1) {
            datapaletteblock_b.accept(this.data.palette.valueFor(0), this.data.storage.getSize());
        } else {
            Int2IntOpenHashMap int2intopenhashmap = new Int2IntOpenHashMap();

            this.data.storage.getAll((i) -> {
                int2intopenhashmap.addTo(i, 1);
            });
            int2intopenhashmap.int2IntEntrySet().forEach((entry) -> {
                datapaletteblock_b.accept(this.data.palette.valueFor(entry.getIntKey()), entry.getIntValue());
            });
        }
    }

    public abstract static class d {

        public static final DataPalette.a SINGLE_VALUE_PALETTE_FACTORY = SingleValuePalette::create;
        public static final DataPalette.a LINEAR_PALETTE_FACTORY = DataPaletteLinear::create;
        public static final DataPalette.a HASHMAP_PALETTE_FACTORY = DataPaletteHash::create;
        static final DataPalette.a GLOBAL_PALETTE_FACTORY = DataPaletteGlobal::create;
        public static final DataPaletteBlock.d SECTION_STATES = new DataPaletteBlock.d(4) {
            @Override
            public <A> DataPaletteBlock.a<A> getConfiguration(Registry<A> registry, int i) {
                DataPaletteBlock.a datapaletteblock_a;

                switch (i) {
                    case 0:
                        datapaletteblock_a = new DataPaletteBlock.a<>(null.SINGLE_VALUE_PALETTE_FACTORY, i);
                        break;
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                        datapaletteblock_a = new DataPaletteBlock.a<>(null.LINEAR_PALETTE_FACTORY, 4);
                        break;
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                        datapaletteblock_a = new DataPaletteBlock.a<>(null.HASHMAP_PALETTE_FACTORY, i);
                        break;
                    default:
                        datapaletteblock_a = new DataPaletteBlock.a<>(DataPaletteBlock.d.GLOBAL_PALETTE_FACTORY, MathHelper.ceillog2(registry.size()));
                }

                return datapaletteblock_a;
            }
        };
        public static final DataPaletteBlock.d SECTION_BIOMES = new DataPaletteBlock.d(2) {
            @Override
            public <A> DataPaletteBlock.a<A> getConfiguration(Registry<A> registry, int i) {
                DataPaletteBlock.a datapaletteblock_a;

                switch (i) {
                    case 0:
                        datapaletteblock_a = new DataPaletteBlock.a<>(null.SINGLE_VALUE_PALETTE_FACTORY, i);
                        break;
                    case 1:
                    case 2:
                    case 3:
                        datapaletteblock_a = new DataPaletteBlock.a<>(null.LINEAR_PALETTE_FACTORY, i);
                        break;
                    default:
                        datapaletteblock_a = new DataPaletteBlock.a<>(DataPaletteBlock.d.GLOBAL_PALETTE_FACTORY, MathHelper.ceillog2(registry.size()));
                }

                return datapaletteblock_a;
            }
        };
        private final int sizeBits;

        d(int i) {
            this.sizeBits = i;
        }

        public int size() {
            return 1 << this.sizeBits * 3;
        }

        public int getIndex(int i, int j, int k) {
            return (j << this.sizeBits | k) << this.sizeBits | i;
        }

        public abstract <A> DataPaletteBlock.a<A> getConfiguration(Registry<A> registry, int i);

        <A> int calculateBitsForSerialization(Registry<A> registry, int i) {
            int j = MathHelper.ceillog2(i);
            DataPaletteBlock.a<A> datapaletteblock_a = this.getConfiguration(registry, j);

            return datapaletteblock_a.factory() == DataPaletteBlock.d.GLOBAL_PALETTE_FACTORY ? j : datapaletteblock_a.bits();
        }
    }

    private static record c<T> (DataPaletteBlock.a<T> configuration, DataBits storage, DataPalette<T> palette) {

        public void copyFrom(DataPalette<T> datapalette, DataBits databits) {
            for (int i = 0; i < databits.getSize(); ++i) {
                T t0 = datapalette.valueFor(databits.get(i));

                this.storage.set(i, this.palette.idFor(t0));
            }

        }

        public int getSerializedSize() {
            return 1 + this.palette.getSerializedSize() + PacketDataSerializer.getVarIntSize(this.storage.getSize()) + this.storage.getRaw().length * 8;
        }

        public void write(PacketDataSerializer packetdataserializer) {
            packetdataserializer.writeByte(this.storage.getBits());
            this.palette.write(packetdataserializer);
            packetdataserializer.writeLongArray(this.storage.getRaw());
        }

        public DataPaletteBlock.c<T> copy() {
            return new DataPaletteBlock.c<>(this.configuration, this.storage.copy(), this.palette.copy());
        }
    }

    private static record a<T> (DataPalette.a factory, int bits) {

        public DataPaletteBlock.c<T> createData(Registry<T> registry, DataPaletteExpandable<T> datapaletteexpandable, int i) {
            Object object = this.bits == 0 ? new ZeroBitStorage(i) : new SimpleBitStorage(this.bits, i);
            DataPalette<T> datapalette = this.factory.create(this.bits, registry, datapaletteexpandable, List.of());

            return new DataPaletteBlock.c<>(this, (DataBits) object, datapalette);
        }
    }

    @FunctionalInterface
    public interface b<T> {

        void accept(T t0, int i);
    }
}
