package net.minecraft.server;

import java.io.IOException;

public class PacketPlayOutTags implements Packet<PacketListenerPlayOut> {

    private ITagRegistry a;

    public PacketPlayOutTags() {}

    public PacketPlayOutTags(ITagRegistry itagregistry) {
        this.a = itagregistry;
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = ITagRegistry.b(packetdataserializer);
    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        this.a.a(packetdataserializer);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }
}
