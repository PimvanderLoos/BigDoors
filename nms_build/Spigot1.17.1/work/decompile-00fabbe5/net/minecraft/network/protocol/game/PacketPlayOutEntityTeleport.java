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
        this.x = entity.locX();
        this.y = entity.locY();
        this.z = entity.locZ();
        this.yRot = (byte) ((int) (entity.getYRot() * 256.0F / 360.0F));
        this.xRot = (byte) ((int) (entity.getXRot() * 256.0F / 360.0F));
        this.onGround = entity.isOnGround();
    }

    public PacketPlayOutEntityTeleport(PacketDataSerializer packetdataserializer) {
        this.id = packetdataserializer.j();
        this.x = packetdataserializer.readDouble();
        this.y = packetdataserializer.readDouble();
        this.z = packetdataserializer.readDouble();
        this.yRot = packetdataserializer.readByte();
        this.xRot = packetdataserializer.readByte();
        this.onGround = packetdataserializer.readBoolean();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.d(this.id);
        packetdataserializer.writeDouble(this.x);
        packetdataserializer.writeDouble(this.y);
        packetdataserializer.writeDouble(this.z);
        packetdataserializer.writeByte(this.yRot);
        packetdataserializer.writeByte(this.xRot);
        packetdataserializer.writeBoolean(this.onGround);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public int b() {
        return this.id;
    }

    public double c() {
        return this.x;
    }

    public double d() {
        return this.y;
    }

    public double e() {
        return this.z;
    }

    public byte f() {
        return this.yRot;
    }

    public byte g() {
        return this.xRot;
    }

    public boolean h() {
        return this.onGround;
    }
}
