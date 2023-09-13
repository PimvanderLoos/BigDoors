package net.minecraft.network.protocol;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.PacketListener;

public class BundleDelimiterPacket<T extends PacketListener> implements Packet<T> {

    public BundleDelimiterPacket() {}

    @Override
    public final void write(PacketDataSerializer packetdataserializer) {}

    @Override
    public final void handle(T t0) {
        throw new AssertionError("This packet should be handled by pipeline");
    }
}
