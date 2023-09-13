package net.minecraft.network.protocol.game;

import java.util.UUID;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.player.EntityHuman;

public class PacketPlayOutNamedEntitySpawn implements Packet<PacketListenerPlayOut> {

    private final int entityId;
    private final UUID playerId;
    private final double x;
    private final double y;
    private final double z;
    private final byte yRot;
    private final byte xRot;

    public PacketPlayOutNamedEntitySpawn(EntityHuman entityhuman) {
        this.entityId = entityhuman.getId();
        this.playerId = entityhuman.getGameProfile().getId();
        this.x = entityhuman.getX();
        this.y = entityhuman.getY();
        this.z = entityhuman.getZ();
        this.yRot = (byte) ((int) (entityhuman.getYRot() * 256.0F / 360.0F));
        this.xRot = (byte) ((int) (entityhuman.getXRot() * 256.0F / 360.0F));
    }

    public PacketPlayOutNamedEntitySpawn(PacketDataSerializer packetdataserializer) {
        this.entityId = packetdataserializer.readVarInt();
        this.playerId = packetdataserializer.readUUID();
        this.x = packetdataserializer.readDouble();
        this.y = packetdataserializer.readDouble();
        this.z = packetdataserializer.readDouble();
        this.yRot = packetdataserializer.readByte();
        this.xRot = packetdataserializer.readByte();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeVarInt(this.entityId);
        packetdataserializer.writeUUID(this.playerId);
        packetdataserializer.writeDouble(this.x);
        packetdataserializer.writeDouble(this.y);
        packetdataserializer.writeDouble(this.z);
        packetdataserializer.writeByte(this.yRot);
        packetdataserializer.writeByte(this.xRot);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleAddPlayer(this);
    }

    public int getEntityId() {
        return this.entityId;
    }

    public UUID getPlayerId() {
        return this.playerId;
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
}
