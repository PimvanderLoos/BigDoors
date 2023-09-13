package net.minecraft.world.level.chunk;

import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.RegistryBlockID;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketDataSerializer;

public class DataPaletteLinear<T> implements DataPalette<T> {

    private final RegistryBlockID<T> registry;
    private final T[] values;
    private final DataPaletteExpandable<T> resizeHandler;
    private final Function<NBTTagCompound, T> reader;
    private final int bits;
    private int size;

    public DataPaletteLinear(RegistryBlockID<T> registryblockid, int i, DataPaletteExpandable<T> datapaletteexpandable, Function<NBTTagCompound, T> function) {
        this.registry = registryblockid;
        this.values = new Object[1 << i];
        this.bits = i;
        this.resizeHandler = datapaletteexpandable;
        this.reader = function;
    }

    @Override
    public int a(T t0) {
        int i;

        for (i = 0; i < this.size; ++i) {
            if (this.values[i] == t0) {
                return i;
            }
        }

        i = this.size;
        if (i < this.values.length) {
            this.values[i] = t0;
            ++this.size;
            return i;
        } else {
            return this.resizeHandler.onResize(this.bits + 1, t0);
        }
    }

    @Override
    public boolean a(Predicate<T> predicate) {
        for (int i = 0; i < this.size; ++i) {
            if (predicate.test(this.values[i])) {
                return true;
            }
        }

        return false;
    }

    @Nullable
    @Override
    public T a(int i) {
        return i >= 0 && i < this.size ? this.values[i] : null;
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        this.size = packetdataserializer.j();

        for (int i = 0; i < this.size; ++i) {
            this.values[i] = this.registry.fromId(packetdataserializer.j());
        }

    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) {
        packetdataserializer.d(this.size);

        for (int i = 0; i < this.size; ++i) {
            packetdataserializer.d(this.registry.getId(this.values[i]));
        }

    }

    @Override
    public int a() {
        int i = PacketDataSerializer.a(this.b());

        for (int j = 0; j < this.b(); ++j) {
            i += PacketDataSerializer.a(this.registry.getId(this.values[j]));
        }

        return i;
    }

    @Override
    public int b() {
        return this.size;
    }

    @Override
    public void a(NBTTagList nbttaglist) {
        for (int i = 0; i < nbttaglist.size(); ++i) {
            this.values[i] = this.reader.apply(nbttaglist.getCompound(i));
        }

        this.size = nbttaglist.size();
    }
}
