package net.minecraft.world.level.chunk;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.Registry;
import net.minecraft.network.PacketDataSerializer;

public class DataPaletteGlobal<T> implements DataPalette<T> {

    private final Registry<T> registry;

    public DataPaletteGlobal(Registry<T> registry) {
        this.registry = registry;
    }

    public static <A> DataPalette<A> create(int i, Registry<A> registry, DataPaletteExpandable<A> datapaletteexpandable, List<A> list) {
        return new DataPaletteGlobal<>(registry);
    }

    @Override
    public int idFor(T t0) {
        int i = this.registry.getId(t0);

        return i == -1 ? 0 : i;
    }

    @Override
    public boolean maybeHas(Predicate<T> predicate) {
        return true;
    }

    @Override
    public T valueFor(int i) {
        T t0 = this.registry.byId(i);

        if (t0 == null) {
            throw new MissingPaletteEntryException(i);
        } else {
            return t0;
        }
    }

    @Override
    public void read(PacketDataSerializer packetdataserializer) {}

    @Override
    public void write(PacketDataSerializer packetdataserializer) {}

    @Override
    public int getSerializedSize() {
        return PacketDataSerializer.getVarIntSize(0);
    }

    @Override
    public int getSize() {
        return this.registry.size();
    }

    @Override
    public DataPalette<T> copy() {
        return this;
    }
}
