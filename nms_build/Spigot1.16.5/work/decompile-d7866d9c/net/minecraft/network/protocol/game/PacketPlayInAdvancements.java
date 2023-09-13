package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.MinecraftKey;

public class PacketPlayInAdvancements implements Packet<PacketListenerPlayIn> {

    private PacketPlayInAdvancements.Status a;
    private MinecraftKey b;

    public PacketPlayInAdvancements() {}

    @Override
    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = (PacketPlayInAdvancements.Status) packetdataserializer.a(PacketPlayInAdvancements.Status.class);
        if (this.a == PacketPlayInAdvancements.Status.OPENED_TAB) {
            this.b = packetdataserializer.p();
        }

    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a((Enum) this.a);
        if (this.a == PacketPlayInAdvancements.Status.OPENED_TAB) {
            packetdataserializer.a(this.b);
        }

    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public PacketPlayInAdvancements.Status c() {
        return this.a;
    }

    public MinecraftKey d() {
        return this.b;
    }

    public static enum Status {

        OPENED_TAB, CLOSED_SCREEN;

        private Status() {}
    }
}
