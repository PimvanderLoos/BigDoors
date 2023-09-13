package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;

public class ClientboundSetTitleTextPacket implements Packet<PacketListenerPlayOut> {

    private final IChatBaseComponent text;

    public ClientboundSetTitleTextPacket(IChatBaseComponent ichatbasecomponent) {
        this.text = ichatbasecomponent;
    }

    public ClientboundSetTitleTextPacket(PacketDataSerializer packetdataserializer) {
        this.text = packetdataserializer.readComponent();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeComponent(this.text);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.setTitleText(this);
    }

    public IChatBaseComponent getText() {
        return this.text;
    }
}
