package net.minecraft.network.protocol.login;

import java.io.IOException;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;

public class PacketLoginOutDisconnect implements Packet<PacketLoginOutListener> {

    private IChatBaseComponent a;

    public PacketLoginOutDisconnect() {}

    public PacketLoginOutDisconnect(IChatBaseComponent ichatbasecomponent) {
        this.a = ichatbasecomponent;
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = IChatBaseComponent.ChatSerializer.b(packetdataserializer.e(262144));
    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a(this.a);
    }

    public void a(PacketLoginOutListener packetloginoutlistener) {
        packetloginoutlistener.a(this);
    }
}
