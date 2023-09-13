package net.minecraft.server;

import java.io.IOException;

public class PacketPlayInJigsawGenerate implements Packet<PacketListenerPlayIn> {

    private BlockPosition a;
    private int b;
    private boolean c;

    public PacketPlayInJigsawGenerate() {}

    @Override
    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.e();
        this.b = packetdataserializer.i();
        this.c = packetdataserializer.readBoolean();
    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a(this.a);
        packetdataserializer.d(this.b);
        packetdataserializer.writeBoolean(this.c);
    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public BlockPosition b() {
        return this.a;
    }

    public int c() {
        return this.b;
    }

    public boolean d() {
        return this.c;
    }
}
