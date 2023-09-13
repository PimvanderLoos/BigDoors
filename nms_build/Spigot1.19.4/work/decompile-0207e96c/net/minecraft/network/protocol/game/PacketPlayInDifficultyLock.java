package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayInDifficultyLock implements Packet<PacketListenerPlayIn> {

    private final boolean locked;

    public PacketPlayInDifficultyLock(boolean flag) {
        this.locked = flag;
    }

    public void handle(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.handleLockDifficulty(this);
    }

    public PacketPlayInDifficultyLock(PacketDataSerializer packetdataserializer) {
        this.locked = packetdataserializer.readBoolean();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeBoolean(this.locked);
    }

    public boolean isLocked() {
        return this.locked;
    }
}
