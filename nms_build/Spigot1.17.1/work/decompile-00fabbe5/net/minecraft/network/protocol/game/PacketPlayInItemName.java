package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayInItemName implements Packet<PacketListenerPlayIn> {

    private final String name;

    public PacketPlayInItemName(String s) {
        this.name = s;
    }

    public PacketPlayInItemName(PacketDataSerializer packetdataserializer) {
        this.name = packetdataserializer.p();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.a(this.name);
    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public String b() {
        return this.name;
    }
}
