package net.minecraft.world.level.chunk;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.Registry;
import net.minecraft.network.PacketDataSerializer;

public interface DataPalette<T> {

    int idFor(T t0);

    boolean maybeHas(Predicate<T> predicate);

    T valueFor(int i);

    void read(PacketDataSerializer packetdataserializer);

    void write(PacketDataSerializer packetdataserializer);

    int getSerializedSize();

    int getSize();

    DataPalette<T> copy();

    public interface a {

        <A> DataPalette<A> create(int i, Registry<A> registry, DataPaletteExpandable<A> datapaletteexpandable, List<A> list);
    }
}
