package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPosition;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayOutOpenSignEditor implements Packet<PacketListenerPlayOut> {

    private final BlockPosition pos;

    public PacketPlayOutOpenSignEditor(BlockPosition blockposition) {
        this.pos = blockposition;
    }

    public PacketPlayOutOpenSignEditor(PacketDataSerializer packetdataserializer) {
        this.pos = packetdataserializer.f();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.a(this.pos);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public BlockPosition b() {
        return this.pos;
    }
}
