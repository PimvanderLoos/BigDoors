package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayInDifficultyLock implements Packet<PacketListenerPlayIn> {

    private final boolean locked;

    public PacketPlayInDifficultyLock(boolean flag) {
        this.locked = flag;
    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public PacketPlayInDifficultyLock(PacketDataSerializer packetdataserializer) {
        this.locked = packetdataserializer.readBoolean();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeBoolean(this.locked);
    }

    public boolean b() {
        return this.locked;
    }
}
