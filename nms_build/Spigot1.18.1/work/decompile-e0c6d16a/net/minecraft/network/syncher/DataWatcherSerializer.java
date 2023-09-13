package net.minecraft.network.syncher;

import net.minecraft.network.PacketDataSerializer;

public interface DataWatcherSerializer<T> {

    void write(PacketDataSerializer packetdataserializer, T t0);

    T read(PacketDataSerializer packetdataserializer);

    default DataWatcherObject<T> createAccessor(int i) {
        return new DataWatcherObject<>(i, this);
    }

    T copy(T t0);
}
