package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;

public class PacketPlayOutPlayerListHeaderFooter implements Packet<PacketListenerPlayOut> {

    public IChatBaseComponent header;
    public IChatBaseComponent footer;

    public PacketPlayOutPlayerListHeaderFooter() {}

    @Override
    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.header = packetdataserializer.h();
        this.footer = packetdataserializer.h();
    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a(this.header);
        packetdataserializer.a(this.footer);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }
}
