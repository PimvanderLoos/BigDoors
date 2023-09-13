package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.EntityExperienceOrb;

public class PacketPlayOutSpawnEntityExperienceOrb implements Packet<PacketListenerPlayOut> {

    private final int id;
    private final double x;
    private final double y;
    private final double z;
    private final int value;

    public PacketPlayOutSpawnEntityExperienceOrb(EntityExperienceOrb entityexperienceorb) {
        this.id = entityexperienceorb.getId();
        this.x = entityexperienceorb.locX();
        this.y = entityexperienceorb.locY();
        this.z = entityexperienceorb.locZ();
        this.value = entityexperienceorb.h();
    }

    public PacketPlayOutSpawnEntityExperienceOrb(PacketDataSerializer packetdataserializer) {
        this.id = packetdataserializer.j();
        this.x = packetdataserializer.readDouble();
        this.y = packetdataserializer.readDouble();
        this.z = packetdataserializer.readDouble();
        this.value = packetdataserializer.readShort();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.d(this.id);
        packetdataserializer.writeDouble(this.x);
        packetdataserializer.writeDouble(this.y);
        packetdataserializer.writeDouble(this.z);
        packetdataserializer.writeShort(this.value);
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

    public int f() {
        return this.value;
    }
}
