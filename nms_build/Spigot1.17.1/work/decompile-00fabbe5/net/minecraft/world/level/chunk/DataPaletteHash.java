package net.minecraft.world.level.chunk;

import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.RegistryBlockID;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.util.RegistryID;

public class DataPaletteHash<T> implements DataPalette<T> {

    private final RegistryBlockID<T> registry;
    private final RegistryID<T> values;
    private final DataPaletteExpandable<T> resizeHandler;
    private final Function<NBTTagCompound, T> reader;
    private final Function<T, NBTTagCompound> writer;
    private final int bits;

    public DataPaletteHash(RegistryBlockID<T> registryblockid, int i, DataPaletteExpandable<T> datapaletteexpandable, Function<NBTTagCompound, T> function, Function<T, NBTTagCompound> function1) {
        this.registry = registryblockid;
        this.bits = i;
        this.resizeHandler = datapaletteexpandable;
        this.reader = function;
        this.writer = function1;
        this.values = new RegistryID<>(1 << i);
    }

    @Override
    public int a(T t0) {
        int i = this.values.getId(t0);

        if (i == -1) {
            i = this.values.c(t0);
            if (i >= 1 << this.bits) {
                i = this.resizeHandler.onResize(this.bits + 1, t0);
            }
        }

        return i;
    }

    @Override
    public boolean a(Predicate<T> predicate) {
        for (int i = 0; i < this.b(); ++i) {
            if (predicate.test(this.values.fromId(i))) {
                return true;
            }
        }

        return false;
    }

    @Nullable
    @Override
    public T a(int i) {
        return this.values.fromId(i);
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        this.values.a();
        int i = packetdataserializer.j();

        for (int j = 0; j < i; ++j) {
            this.values.c(this.registry.fromId(packetdataserializer.j()));
        }

    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) {
        int i = this.b();

        packetdataserializer.d(i);

        for (int j = 0; j < i; ++j) {
            packetdataserializer.d(this.registry.getId(this.values.fromId(j)));
        }

    }

    @Override
    public int a() {
        int i = PacketDataSerializer.a(this.b());

        for (int j = 0; j < this.b(); ++j) {
            i += PacketDataSerializer.a(this.registry.getId(this.values.fromId(j)));
        }

        return i;
    }

    @Override
    public int b() {
        return this.values.b();
    }

    @Override
    public void a(NBTTagList nbttaglist) {
        this.values.a();

        for (int i = 0; i < nbttaglist.size(); ++i) {
            this.values.c(this.reader.apply(nbttaglist.getCompound(i)));
        }

    }

    public void b(NBTTagList nbttaglist) {
        for (int i = 0; i < this.b(); ++i) {
            nbttaglist.add((NBTBase) this.writer.apply(this.values.fromId(i)));
        }

    }
}
