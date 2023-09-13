package net.minecraft.world.level.chunk;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.core.Registry;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.util.RegistryID;

public class DataPaletteHash<T> implements DataPalette<T> {

    private final Registry<T> registry;
    private final RegistryID<T> values;
    private final DataPaletteExpandable<T> resizeHandler;
    private final int bits;

    public DataPaletteHash(Registry<T> registry, int i, DataPaletteExpandable<T> datapaletteexpandable, List<T> list) {
        this(registry, i, datapaletteexpandable);
        RegistryID registryid = this.values;

        Objects.requireNonNull(this.values);
        list.forEach(registryid::add);
    }

    public DataPaletteHash(Registry<T> registry, int i, DataPaletteExpandable<T> datapaletteexpandable) {
        this(registry, i, datapaletteexpandable, RegistryID.create(1 << i));
    }

    private DataPaletteHash(Registry<T> registry, int i, DataPaletteExpandable<T> datapaletteexpandable, RegistryID<T> registryid) {
        this.registry = registry;
        this.bits = i;
        this.resizeHandler = datapaletteexpandable;
        this.values = registryid;
    }

    public static <A> DataPalette<A> create(int i, Registry<A> registry, DataPaletteExpandable<A> datapaletteexpandable, List<A> list) {
        return new DataPaletteHash<>(registry, i, datapaletteexpandable, list);
    }

    @Override
    public int idFor(T t0) {
        int i = this.values.getId(t0);

        if (i == -1) {
            i = this.values.add(t0);
            if (i >= 1 << this.bits) {
                i = this.resizeHandler.onResize(this.bits + 1, t0);
            }
        }

        return i;
    }

    @Override
    public boolean maybeHas(Predicate<T> predicate) {
        for (int i = 0; i < this.getSize(); ++i) {
            if (predicate.test(this.values.byId(i))) {
                return true;
            }
        }

        return false;
    }

    @Override
    public T valueFor(int i) {
        T t0 = this.values.byId(i);

        if (t0 == null) {
            throw new MissingPaletteEntryException(i);
        } else {
            return t0;
        }
    }

    @Override
    public void read(PacketDataSerializer packetdataserializer) {
        this.values.clear();
        int i = packetdataserializer.readVarInt();

        for (int j = 0; j < i; ++j) {
            this.values.add(this.registry.byIdOrThrow(packetdataserializer.readVarInt()));
        }

    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        int i = this.getSize();

        packetdataserializer.writeVarInt(i);

        for (int j = 0; j < i; ++j) {
            packetdataserializer.writeVarInt(this.registry.getId(this.values.byId(j)));
        }

    }

    @Override
    public int getSerializedSize() {
        int i = PacketDataSerializer.getVarIntSize(this.getSize());

        for (int j = 0; j < this.getSize(); ++j) {
            i += PacketDataSerializer.getVarIntSize(this.registry.getId(this.values.byId(j)));
        }

        return i;
    }

    public List<T> getEntries() {
        ArrayList<T> arraylist = new ArrayList();
        Iterator iterator = this.values.iterator();

        Objects.requireNonNull(arraylist);
        iterator.forEachRemaining(arraylist::add);
        return arraylist;
    }

    @Override
    public int getSize() {
        return this.values.size();
    }

    @Override
    public DataPalette<T> copy() {
        return new DataPaletteHash<>(this.registry, this.bits, this.resizeHandler, this.values.copy());
    }
}
