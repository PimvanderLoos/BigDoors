package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.tags.ITagRegistry;

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
