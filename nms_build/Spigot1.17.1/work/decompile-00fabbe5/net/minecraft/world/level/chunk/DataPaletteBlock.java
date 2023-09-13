package net.minecraft.world.level.chunk;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import java.util.concurrent.Semaphore;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.RegistryBlockID;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.util.DataBits;
import net.minecraft.util.DebugBuffer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ThreadingDetector;

public class DataPaletteBlock<T> implements DataPaletteExpandable<T> {

    private static final int SIZE = 4096;
    public static final int GLOBAL_PALETTE_BITS = 9;
    public static final int MIN_PALETTE_SIZE = 4;
    private final DataPalette<T> globalPalette;
    private final DataPaletteExpandable<T> dummyPaletteResize = (i, object) -> {
        return 0;
    };
    private final RegistryBlockID<T> registry;
    private final Function<NBTTagCompound, T> reader;
    private final Function<T, NBTTagCompound> writer;
    private final T defaultValue;
    protected DataBits storage;
    private DataPalette<T> palette;
    private int bits;
    private final Semaphore lock = new Semaphore(1);
    @Nullable
    private final DebugBuffer<Pair<Thread, StackTraceElement[]>> traces = null;

    public void a() {
        if (this.traces != null) {
            Thread thread = Thread.currentThread();

            this.traces.a(Pair.of(thread, thread.getStackTrace()));
        }

        ThreadingDetector.a(this.lock, this.traces, "PalettedContainer");
    }

    public void b() {
        this.lock.release();
    }

    public DataPaletteBlock(DataPalette<T> datapalette, RegistryBlockID<T> registryblockid, Function<NBTTagCompound, T> function, Function<T, NBTTagCompound> function1, T t0) {
        this.globalPalette = datapalette;
        this.registry = registryblockid;
        this.reader = function;
        this.writer = function1;
        this.defaultValue = t0;
        this.b(4);
    }

    private static int b(int i, int j, int k) {
        return j << 8 | k << 4 | i;
    }

    private void b(int i) {
        if (i != this.bits) {
            this.bits = i;
            if (this.bits <= 4) {
                this.bits = 4;
                this.palette = new DataPaletteLinear<>(this.registry, this.bits, this, this.reader);
            } else if (this.bits < 9) {
                this.palette = new DataPaletteHash<>(this.registry, this.bits, this, this.reader, this.writer);
            } else {
                this.palette = this.globalPalette;
                this.bits = MathHelper.e(this.registry.a());
            }

            this.palette.a(this.defaultValue);
            this.storage = new DataBits(this.bits, 4096);
        }
    }

    @Override
    public int onResize(int i, T t0) {
        DataBits databits = this.storage;
        DataPalette<T> datapalette = this.palette;

        this.b(i);

        for (int j = 0; j < databits.b(); ++j) {
            T t1 = datapalette.a(databits.a(j));

            if (t1 != null) {
                this.setBlockIndex(j, t1);
            }
        }

        return this.palette.a(t0);
    }

    public T setBlock(int i, int j, int k, T t0) {
        Object object;

        try {
            this.a();
            T t1 = this.a(b(i, j, k), t0);

            object = t1;
        } finally {
            this.b();
        }

        return object;
    }

    public T b(int i, int j, int k, T t0) {
        return this.a(b(i, j, k), t0);
    }

    private T a(int i, T t0) {
        int j = this.palette.a(t0);
        int k = this.storage.a(i, j);
        T t1 = this.palette.a(k);

        return t1 == null ? this.defaultValue : t1;
    }

    public void c(int i, int j, int k, T t0) {
        try {
            this.a();
            this.setBlockIndex(b(i, j, k), t0);
        } finally {
            this.b();
        }

    }

    private void setBlockIndex(int i, T t0) {
        int j = this.palette.a(t0);

        this.storage.b(i, j);
    }

    public T a(int i, int j, int k) {
        return this.a(b(i, j, k));
    }

    protected T a(int i) {
        T t0 = this.palette.a(this.storage.a(i));

        return t0 == null ? this.defaultValue : t0;
    }

    public void a(PacketDataSerializer packetdataserializer) {
        try {
            this.a();
            byte b0 = packetdataserializer.readByte();

            if (this.bits != b0) {
                this.b(b0);
            }

            this.palette.a(packetdataserializer);
            packetdataserializer.b(this.storage.a());
        } finally {
            this.b();
        }

    }

    public void b(PacketDataSerializer packetdataserializer) {
        try {
            this.a();
            packetdataserializer.writeByte(this.bits);
            this.palette.b(packetdataserializer);
            packetdataserializer.a(this.storage.a());
        } finally {
            this.b();
        }

    }

    public void a(NBTTagList nbttaglist, long[] along) {
        try {
            this.a();
            int i = Math.max(4, MathHelper.e(nbttaglist.size()));

            if (i != this.bits) {
                this.b(i);
            }

            this.palette.a(nbttaglist);
            int j = along.length * 64 / 4096;

            if (this.palette == this.globalPalette) {
                DataPalette<T> datapalette = new DataPaletteHash<>(this.registry, i, this.dummyPaletteResize, this.reader, this.writer);

                datapalette.a(nbttaglist);
                DataBits databits = new DataBits(i, 4096, along);

                for (int k = 0; k < 4096; ++k) {
                    this.storage.b(k, this.globalPalette.a(datapalette.a(databits.a(k))));
                }
            } else if (j == this.bits) {
                System.arraycopy(along, 0, this.storage.a(), 0, along.length);
            } else {
                DataBits databits1 = new DataBits(j, 4096, along);

                for (int l = 0; l < 4096; ++l) {
                    this.storage.b(l, databits1.a(l));
                }
            }
        } finally {
            this.b();
        }

    }

    public void a(NBTTagCompound nbttagcompound, String s, String s1) {
        try {
            this.a();
            DataPaletteHash<T> datapalettehash = new DataPaletteHash<>(this.registry, this.bits, this.dummyPaletteResize, this.reader, this.writer);
            T t0 = this.defaultValue;
            int i = datapalettehash.a(this.defaultValue);
            int[] aint = new int[4096];

            for (int j = 0; j < 4096; ++j) {
                T t1 = this.a(j);

                if (t1 != t0) {
                    t0 = t1;
                    i = datapalettehash.a(t1);
                }

                aint[j] = i;
            }

            NBTTagList nbttaglist = new NBTTagList();

            datapalettehash.b(nbttaglist);
            nbttagcompound.set(s, nbttaglist);
            int k = Math.max(4, MathHelper.e(nbttaglist.size()));
            DataBits databits = new DataBits(k, 4096);

            for (int l = 0; l < aint.length; ++l) {
                databits.b(l, aint[l]);
            }

            nbttagcompound.a(s1, databits.a());
        } finally {
            this.b();
        }

    }

    public int c() {
        return 1 + this.palette.a() + PacketDataSerializer.a(this.storage.b()) + this.storage.a().length * 8;
    }

    public boolean contains(Predicate<T> predicate) {
        return this.palette.a(predicate);
    }

    public void a(DataPaletteBlock.a<T> datapaletteblock_a) {
        Int2IntOpenHashMap int2intopenhashmap = new Int2IntOpenHashMap();

        this.storage.a((i) -> {
            int2intopenhashmap.put(i, int2intopenhashmap.get(i) + 1);
        });
        int2intopenhashmap.int2IntEntrySet().forEach((entry) -> {
            datapaletteblock_a.accept(this.palette.a(entry.getIntKey()), entry.getIntValue());
        });
    }

    @FunctionalInterface
    public interface a<T> {

        void accept(T t0, int i);
    }
}
