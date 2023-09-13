package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;

public class PacketPlayOutKickDisconnect implements Packet<PacketListenerPlayOut> {

    private IChatBaseComponent a;

    public PacketPlayOutKickDisconnect() {}

    public PacketPlayOutKickDisconnect(IChatBaseComponent ichatbasecomponent) {
        this.a = ichatbasecomponent;
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.h();
    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a(this.a);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }
}
