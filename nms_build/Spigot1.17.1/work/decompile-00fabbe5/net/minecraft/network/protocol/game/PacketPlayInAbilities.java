package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.player.PlayerAbilities;

public class PacketPlayInAbilities implements Packet<PacketListenerPlayIn> {

    private static final int FLAG_FLYING = 2;
    private final boolean isFlying;

    public PacketPlayInAbilities(PlayerAbilities playerabilities) {
        this.isFlying = playerabilities.flying;
    }

    public PacketPlayInAbilities(PacketDataSerializer packetdataserializer) {
        byte b0 = packetdataserializer.readByte();

        this.isFlying = (b0 & 2) != 0;
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        byte b0 = 0;

        if (this.isFlying) {
            b0 = (byte) (b0 | 2);
        }

        packetdataserializer.writeByte(b0);
    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public boolean isFlying() {
        return this.isFlying;
    }
}
