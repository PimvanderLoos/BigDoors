package net.minecraft.world.level.chunk;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.Registry;
import net.minecraft.network.PacketDataSerializer;
import org.apache.commons.lang3.Validate;

public class DataPaletteLinear<T> implements DataPalette<T> {

    private final Registry<T> registry;
    private final T[] values;
    private final DataPaletteExpandable<T> resizeHandler;
    private final int bits;
    private int size;

    private DataPaletteLinear(Registry<T> registry, int i, DataPaletteExpandable<T> datapaletteexpandable, List<T> list) {
        this.registry = registry;
        this.values = new Object[1 << i];
        this.bits = i;
        this.resizeHandler = datapaletteexpandable;
        Validate.isTrue(list.size() <= this.values.length, "Can't initialize LinearPalette of size %d with %d entries", new Object[]{this.values.length, list.size()});

        for (int j = 0; j < list.size(); ++j) {
            this.values[j] = list.get(j);
        }

        this.size = list.size();
    }

    private DataPaletteLinear(Registry<T> registry, T[] at, DataPaletteExpandable<T> datapaletteexpandable, int i, int j) {
        this.registry = registry;
        this.values = at;
        this.resizeHandler = datapaletteexpandable;
        this.bits = i;
        this.size = j;
    }

    public static <A> DataPalette<A> create(int i, Registry<A> registry, DataPaletteExpandable<A> datapaletteexpandable, List<A> list) {
        return new DataPaletteLinear<>(registry, i, datapaletteexpandable, list);
    }

    @Override
    public int idFor(T t0) {
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
    public boolean maybeHas(Predicate<T> predicate) {
        for (int i = 0; i < this.size; ++i) {
            if (predicate.test(this.values[i])) {
                return true;
            }
        }

        return false;
    }

    @Override
    public T valueFor(int i) {
        if (i >= 0 && i < this.size) {
            return this.values[i];
        } else {
            throw new MissingPaletteEntryException(i);
        }
    }

    @Override
    public void read(PacketDataSerializer packetdataserializer) {
        this.size = packetdataserializer.readVarInt();

        for (int i = 0; i < this.size; ++i) {
            this.values[i] = this.registry.byIdOrThrow(packetdataserializer.readVarInt());
        }

    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeVarInt(this.size);

        for (int i = 0; i < this.size; ++i) {
            packetdataserializer.writeVarInt(this.registry.getId(this.values[i]));
        }

    }

    @Override
    public int getSerializedSize() {
        int i = PacketDataSerializer.getVarIntSize(this.getSize());

        for (int j = 0; j < this.getSize(); ++j) {
            i += PacketDataSerializer.getVarIntSize(this.registry.getId(this.values[j]));
        }

        return i;
    }

    @Override
    public int getSize() {
        return this.size;
    }

    @Override
    public DataPalette<T> copy() {
        return new DataPaletteLinear<>(this.registry, (Object[]) this.values.clone(), this.resizeHandler, this.bits, this.size);
    }
}
