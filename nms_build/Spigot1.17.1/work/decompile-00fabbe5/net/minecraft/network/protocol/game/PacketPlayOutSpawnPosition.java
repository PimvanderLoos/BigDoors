package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPosition;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayOutSpawnPosition implements Packet<PacketListenerPlayOut> {

    public final BlockPosition pos;
    private final float angle;

    public PacketPlayOutSpawnPosition(BlockPosition blockposition, float f) {
        this.pos = blockposition;
        this.angle = f;
    }

    public PacketPlayOutSpawnPosition(PacketDataSerializer packetdataserializer) {
        this.pos = packetdataserializer.f();
        this.angle = packetdataserializer.readFloat();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.a(this.pos);
        packetdataserializer.writeFloat(this.angle);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public BlockPosition b() {
        return this.pos;
    }

    public float c() {
        return this.angle;
    }
}
