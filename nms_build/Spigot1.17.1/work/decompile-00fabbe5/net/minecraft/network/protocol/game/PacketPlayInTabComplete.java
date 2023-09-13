package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayInTabComplete implements Packet<PacketListenerPlayIn> {

    private final int id;
    private final String command;

    public PacketPlayInTabComplete(int i, String s) {
        this.id = i;
        this.command = s;
    }

    public PacketPlayInTabComplete(PacketDataSerializer packetdataserializer) {
        this.id = packetdataserializer.j();
        this.command = packetdataserializer.e(32500);
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.d(this.id);
        packetdataserializer.a(this.command, 32500);
    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public int b() {
        return this.id;
    }

    public String c() {
        return this.command;
    }
}
