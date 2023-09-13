package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayOutExperience implements Packet<PacketListenerPlayOut> {

    private final float experienceProgress;
    private final int totalExperience;
    private final int experienceLevel;

    public PacketPlayOutExperience(float f, int i, int j) {
        this.experienceProgress = f;
        this.totalExperience = i;
        this.experienceLevel = j;
    }

    public PacketPlayOutExperience(PacketDataSerializer packetdataserializer) {
        this.experienceProgress = packetdataserializer.readFloat();
        this.experienceLevel = packetdataserializer.j();
        this.totalExperience = packetdataserializer.j();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeFloat(this.experienceProgress);
        packetdataserializer.d(this.experienceLevel);
        packetdataserializer.d(this.totalExperience);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public float b() {
        return this.experienceProgress;
    }

    public int c() {
        return this.totalExperience;
    }

    public int d() {
        return this.experienceLevel;
    }
}
