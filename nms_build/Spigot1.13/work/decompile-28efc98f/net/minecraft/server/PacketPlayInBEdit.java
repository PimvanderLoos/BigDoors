package net.minecraft.server;

import java.io.IOException;

public class PacketPlayInBEdit implements Packet<PacketListenerPlayIn> {

    private ItemStack a;
    private boolean b;

    public PacketPlayInBEdit() {}

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.k();
        this.b = packetdataserializer.readBoolean();
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a(this.a);
        packetdataserializer.writeBoolean(this.b);
    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public ItemStack b() {
        return this.a;
    }

    public boolean c() {
        return this.b;
    }
}
