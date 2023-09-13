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
        this.newSize = worldborder.getLerpTarget();
        this.lerpTime = worldborder.getLerpRemainingTime();
    }

    public ClientboundSetBorderLerpSizePacket(PacketDataSerializer packetdataserializer) {
        this.oldSize = packetdataserializer.readDouble();
        this.newSize = packetdataserializer.readDouble();
        this.lerpTime = packetdataserializer.readVarLong();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeDouble(this.oldSize);
        packetdataserializer.writeDouble(this.newSize);
        packetdataserializer.writeVarLong(this.lerpTime);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleSetBorderLerpSize(this);
    }

    public double getOldSize() {
        return this.oldSize;
    }

    public double getNewSize() {
        return this.newSize;
    }

    public long getLerpTime() {
        return this.lerpTime;
    }
}
