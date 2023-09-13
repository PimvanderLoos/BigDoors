package net.minecraft.server;

import java.io.IOException;

public class PacketPlayInAbilities implements Packet<PacketListenerPlayIn> {

    private boolean a;

    public PacketPlayInAbilities() {}

    public PacketPlayInAbilities(PlayerAbilities playerabilities) {
        this.a = playerabilities.isFlying;
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        byte b0 = packetdataserializer.readByte();

        this.a = (b0 & 2) != 0;
    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        byte b0 = 0;

        if (this.a) {
            b0 = (byte) (b0 | 2);
        }

        packetdataserializer.writeByte(b0);
    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public boolean isFlying() {
        return this.a;
    }
}
