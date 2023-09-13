package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;

public record ClientboundSystemChatPacket(IChatBaseComponent content, boolean overlay) implements Packet<PacketListenerPlayOut> {

    public ClientboundSystemChatPacket(PacketDataSerializer packetdataserializer) {
        this(packetdataserializer.readComponent(), packetdataserializer.readBoolean());
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeComponent(this.content);
        packetdataserializer.writeBoolean(this.overlay);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleSystemChat(this);
    }

    @Override
    public boolean isSkippable() {
        return true;
    }
}
