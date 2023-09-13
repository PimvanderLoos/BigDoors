package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayInChat implements Packet<PacketListenerPlayIn> {

    private static final int MAX_MESSAGE_LENGTH = 256;
    private final String message;

    public PacketPlayInChat(String s) {
        if (s.length() > 256) {
            s = s.substring(0, 256);
        }

        this.message = s;
    }

    public PacketPlayInChat(PacketDataSerializer packetdataserializer) {
        this.message = packetdataserializer.e(256);
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.a(this.message);
    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public String b() {
        return this.message;
    }
}
