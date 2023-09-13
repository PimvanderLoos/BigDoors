package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.border.WorldBorder;

public class ClientboundSetBorderLerpSizePacket implements Packet<PacketListenerPlayOut> {

    private final double oldSize;
    private final double newSize;
    private final long lerpTime;

    public ClientboundSetBorderLerpSizePacket(WorldBorder worldborder) {
        this.oldSize = worldborder.getSize();
        this.newSize = worldborder.k();
        this.lerpTime = worldborder.j();
    }

    public ClientboundSetBorderLerpSizePacket(PacketDataSerializer packetdataserializer) {
        this.oldSize = packetdataserializer.readDouble();
        this.newSize = packetdataserializer.readDouble();
        this.lerpTime = packetdataserializer.k();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeDouble(this.oldSize);
        packetdataserializer.writeDouble(this.newSize);
        packetdataserializer.b(this.lerpTime);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public double b() {
        return this.oldSize;
    }

    public double c() {
        return this.newSize;
    }

    public long d() {
        return this.lerpTime;
    }
}
