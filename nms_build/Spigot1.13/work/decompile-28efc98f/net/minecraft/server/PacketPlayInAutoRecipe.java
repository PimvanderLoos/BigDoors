package net.minecraft.server;

import java.io.IOException;

public class PacketPlayInAutoRecipe implements Packet<PacketListenerPlayIn> {

    private int a;
    private MinecraftKey b;
    private boolean c;

    public PacketPlayInAutoRecipe() {}

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.readByte();
        this.b = packetdataserializer.l();
        this.c = packetdataserializer.readBoolean();
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.writeByte(this.a);
        packetdataserializer.a(this.b);
        packetdataserializer.writeBoolean(this.c);
    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public int b() {
        return this.a;
    }

    public MinecraftKey c() {
        return this.b;
    }

    public boolean d() {
        return this.c;
    }
}
