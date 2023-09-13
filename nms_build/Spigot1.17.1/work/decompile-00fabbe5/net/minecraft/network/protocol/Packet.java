package net.minecraft.network.protocol;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.PacketListener;

public interface Packet<T extends PacketListener> {

    void a(PacketDataSerializer packetdataserializer);

    void a(T t0);

    default boolean a() {
        return false;
    }
}
