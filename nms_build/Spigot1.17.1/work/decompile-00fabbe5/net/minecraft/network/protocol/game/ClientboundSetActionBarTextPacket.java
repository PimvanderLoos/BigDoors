package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;

public class ClientboundSetActionBarTextPacket implements Packet<PacketListenerPlayOut> {

    private final IChatBaseComponent text;

    public ClientboundSetActionBarTextPacket(IChatBaseComponent ichatbasecomponent) {
        this.text = ichatbasecomponent;
    }

    public ClientboundSetActionBarTextPacket(PacketDataSerializer packetdataserializer) {
        this.text = packetdataserializer.i();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.a(this.text);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public IChatBaseComponent b() {
        return this.text;
    }
}
