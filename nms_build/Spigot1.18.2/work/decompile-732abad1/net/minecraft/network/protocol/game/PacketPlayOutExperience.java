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
        this.experienceLevel = packetdataserializer.readVarInt();
        this.totalExperience = packetdataserializer.readVarInt();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeFloat(this.experienceProgress);
        packetdataserializer.writeVarInt(this.experienceLevel);
        packetdataserializer.writeVarInt(this.totalExperience);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleSetExperience(this);
    }

    public float getExperienceProgress() {
        return this.experienceProgress;
    }

    public int getTotalExperience() {
        return this.totalExperience;
    }

    public int getExperienceLevel() {
        return this.experienceLevel;
    }
}
