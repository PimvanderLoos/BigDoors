package net.minecraft.server;

public interface DataWatcherSerializer<T> {

    void a(PacketDataSerializer packetdataserializer, T t0);

    T a(PacketDataSerializer packetdataserializer);

    DataWatcherObject<T> a(int i);

    T a(T t0);
}
