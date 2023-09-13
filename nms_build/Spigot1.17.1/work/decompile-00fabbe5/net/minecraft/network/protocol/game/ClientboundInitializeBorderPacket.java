package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.border.WorldBorder;

public class ClientboundInitializeBorderPacket implements Packet<PacketListenerPlayOut> {

    private final double newCenterX;
    private final double newCenterZ;
    private final double oldSize;
    private final double newSize;
    private final long lerpTime;
    private final int newAbsoluteMaxSize;
    private final int warningBlocks;
    private final int warningTime;

    public ClientboundInitializeBorderPacket(PacketDataSerializer packetdataserializer) {
        this.newCenterX = packetdataserializer.readDouble();
        this.newCenterZ = packetdataserializer.readDouble();
        this.oldSize = packetdataserializer.readDouble();
        this.newSize = packetdataserializer.readDouble();
        this.lerpTime = packetdataserializer.k();
        this.newAbsoluteMaxSize = packetdataserializer.j();
        this.warningBlocks = packetdataserializer.j();
        this.warningTime = packetdataserializer.j();
    }

    public ClientboundInitializeBorderPacket(WorldBorder worldborder) {
        this.newCenterX = worldborder.getCenterX();
        this.newCenterZ = worldborder.getCenterZ();
        this.oldSize = worldborder.getSize();
        this.newSize = worldborder.k();
        this.lerpTime = worldborder.j();
        this.newAbsoluteMaxSize = worldborder.m();
        this.warningBlocks = worldborder.getWarningDistance();
        this.warningTime = worldborder.getWarningTime();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeDouble(this.newCenterX);
        packetdataserializer.writeDouble(this.newCenterZ);
        packetdataserializer.writeDouble(this.oldSize);
        packetdataserializer.writeDouble(this.newSize);
        packetdataserializer.b(this.lerpTime);
        packetdataserializer.d(this.newAbsoluteMaxSize);
        packetdataserializer.d(this.warningBlocks);
        packetdataserializer.d(this.warningTime);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public double b() {
        return this.newCenterX;
    }

    public double c() {
        return this.newCenterZ;
    }

    public double d() {
        return this.newSize;
    }

    public double e() {
        return this.oldSize;
    }

    public long f() {
        return this.lerpTime;
    }

    public int g() {
        return this.newAbsoluteMaxSize;
    }

    public int h() {
        return this.warningTime;
    }

    public int i() {
        return this.warningBlocks;
    }
}
