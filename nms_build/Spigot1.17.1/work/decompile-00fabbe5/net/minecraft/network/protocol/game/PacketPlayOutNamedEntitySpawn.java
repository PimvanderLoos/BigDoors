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
        this.playerId = entityhuman.getProfile().getId();
        this.x = entityhuman.locX();
        this.y = entityhuman.locY();
        this.z = entityhuman.locZ();
        this.yRot = (byte) ((int) (entityhuman.getYRot() * 256.0F / 360.0F));
        this.xRot = (byte) ((int) (entityhuman.getXRot() * 256.0F / 360.0F));
    }

    public PacketPlayOutNamedEntitySpawn(PacketDataSerializer packetdataserializer) {
        this.entityId = packetdataserializer.j();
        this.playerId = packetdataserializer.l();
        this.x = packetdataserializer.readDouble();
        this.y = packetdataserializer.readDouble();
        this.z = packetdataserializer.readDouble();
        this.yRot = packetdataserializer.readByte();
        this.xRot = packetdataserializer.readByte();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.d(this.entityId);
        packetdataserializer.a(this.playerId);
        packetdataserializer.writeDouble(this.x);
        packetdataserializer.writeDouble(this.y);
        packetdataserializer.writeDouble(this.z);
        packetdataserializer.writeByte(this.yRot);
        packetdataserializer.writeByte(this.xRot);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public int b() {
        return this.entityId;
    }

    public UUID c() {
        return this.playerId;
    }

    public double d() {
        return this.x;
    }

    public double e() {
        return this.y;
    }

    public double f() {
        return this.z;
    }

    public byte g() {
        return this.yRot;
    }

    public byte h() {
        return this.xRot;
    }
}
