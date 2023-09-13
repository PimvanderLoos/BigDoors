package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;

public class PacketPlayOutEntityTeleport implements Packet<PacketListenerPlayOut> {

    private final int id;
    private final double x;
    private final double y;
    private final double z;
    private final byte yRot;
    private final byte xRot;
    private final boolean onGround;

    public PacketPlayOutEntityTeleport(Entity entity) {
        this.id = entity.getId();
        this.x = entity.getX();
        this.y = entity.getY();
        this.z = entity.getZ();
        this.yRot = (byte) ((int) (entity.getYRot() * 256.0F / 360.0F));
        this.xRot = (byte) ((int) (entity.getXRot() * 256.0F / 360.0F));
        this.onGround = entity.isOnGround();
    }

    public PacketPlayOutEntityTeleport(PacketDataSerializer packetdataserializer) {
        this.id = packetdataserializer.readVarInt();
        this.x = packetdataserializer.readDouble();
        this.y = packetdataserializer.readDouble();
        this.z = packetdataserializer.readDouble();
        this.yRot = packetdataserializer.readByte();
        this.xRot = packetdataserializer.readByte();
        this.onGround = packetdataserializer.readBoolean();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeVarInt(this.id);
        packetdataserializer.writeDouble(this.x);
        packetdataserializer.writeDouble(this.y);
        packetdataserializer.writeDouble(this.z);
        packetdataserializer.writeByte(this.yRot);
        packetdataserializer.writeByte(this.xRot);
        packetdataserializer.writeBoolean(this.onGround);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleTeleportEntity(this);
    }

    public int getId() {
        return this.id;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public byte getyRot() {
        return this.yRot;
    }

    public byte getxRot() {
        return this.xRot;
    }

    public boolean isOnGround() {
        return this.onGround;
    }
}
