package net.minecraft.network.protocol.game;

import java.io.IOException;
import java.util.UUID;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.player.EntityHuman;

public class PacketPlayOutNamedEntitySpawn implements Packet<PacketListenerPlayOut> {

    private int a;
    private UUID b;
    private double c;
    private double d;
    private double e;
    private byte f;
    private byte g;

    public PacketPlayOutNamedEntitySpawn() {}

    public PacketPlayOutNamedEntitySpawn(EntityHuman entityhuman) {
        this.a = entityhuman.getId();
        this.b = entityhuman.getProfile().getId();
        this.c = entityhuman.locX();
        this.d = entityhuman.locY();
        this.e = entityhuman.locZ();
        this.f = (byte) ((int) (entityhuman.yaw * 256.0F / 360.0F));
        this.g = (byte) ((int) (entityhuman.pitch * 256.0F / 360.0F));
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.i();
        this.b = packetdataserializer.k();
        this.c = packetdataserializer.readDouble();
        this.d = packetdataserializer.readDouble();
        this.e = packetdataserializer.readDouble();
        this.f = packetdataserializer.readByte();
        this.g = packetdataserializer.readByte();
    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.d(this.a);
        packetdataserializer.a(this.b);
        packetdataserializer.writeDouble(this.c);
        packetdataserializer.writeDouble(this.d);
        packetdataserializer.writeDouble(this.e);
        packetdataserializer.writeByte(this.f);
        packetdataserializer.writeByte(this.g);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }
}
