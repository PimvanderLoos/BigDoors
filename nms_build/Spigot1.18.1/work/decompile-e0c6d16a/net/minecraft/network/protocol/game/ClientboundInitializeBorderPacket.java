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
        this.lerpTime = packetdataserializer.readVarLong();
        this.newAbsoluteMaxSize = packetdataserializer.readVarInt();
        this.warningBlocks = packetdataserializer.readVarInt();
        this.warningTime = packetdataserializer.readVarInt();
    }

    public ClientboundInitializeBorderPacket(WorldBorder worldborder) {
        this.newCenterX = worldborder.getCenterX();
        this.newCenterZ = worldborder.getCenterZ();
        this.oldSize = worldborder.getSize();
        this.newSize = worldborder.getLerpTarget();
        this.lerpTime = worldborder.getLerpRemainingTime();
        this.newAbsoluteMaxSize = worldborder.getAbsoluteMaxSize();
        this.warningBlocks = worldborder.getWarningBlocks();
        this.warningTime = worldborder.getWarningTime();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeDouble(this.newCenterX);
        packetdataserializer.writeDouble(this.newCenterZ);
        packetdataserializer.writeDouble(this.oldSize);
        packetdataserializer.writeDouble(this.newSize);
        packetdataserializer.writeVarLong(this.lerpTime);
        packetdataserializer.writeVarInt(this.newAbsoluteMaxSize);
        packetdataserializer.writeVarInt(this.warningBlocks);
        packetdataserializer.writeVarInt(this.warningTime);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleInitializeBorder(this);
    }

    public double getNewCenterX() {
        return this.newCenterX;
    }

    public double getNewCenterZ() {
        return this.newCenterZ;
    }

    public double getNewSize() {
        return this.newSize;
    }

    public double getOldSize() {
        return this.oldSize;
    }

    public long getLerpTime() {
        return this.lerpTime;
    }

    public int getNewAbsoluteMaxSize() {
        return this.newAbsoluteMaxSize;
    }

    public int getWarningTime() {
        return this.warningTime;
    }

    public int getWarningBlocks() {
        return this.warningBlocks;
    }
}
