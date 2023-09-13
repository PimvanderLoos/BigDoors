package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayOutUpdateHealth implements Packet<PacketListenerPlayOut> {

    private final float health;
    private final int food;
    private final float saturation;

    public PacketPlayOutUpdateHealth(float f, int i, float f1) {
        this.health = f;
        this.food = i;
        this.saturation = f1;
    }

    public PacketPlayOutUpdateHealth(PacketDataSerializer packetdataserializer) {
        this.health = packetdataserializer.readFloat();
        this.food = packetdataserializer.j();
        this.saturation = packetdataserializer.readFloat();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeFloat(this.health);
        packetdataserializer.d(this.food);
        packetdataserializer.writeFloat(this.saturation);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public float b() {
        return this.health;
    }

    public int c() {
        return this.food;
    }

    public float d() {
        return this.saturation;
    }
}
