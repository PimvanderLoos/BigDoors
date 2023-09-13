package net.minecraft.network.protocol.game;

import java.util.List;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public record ClientboundCustomChatCompletionsPacket(ClientboundCustomChatCompletionsPacket.Action action, List<String> entries) implements Packet<PacketListenerPlayOut> {

    public ClientboundCustomChatCompletionsPacket(PacketDataSerializer packetdataserializer) {
        this((ClientboundCustomChatCompletionsPacket.Action) packetdataserializer.readEnum(ClientboundCustomChatCompletionsPacket.Action.class), packetdataserializer.readList(PacketDataSerializer::readUtf));
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeEnum(this.action);
        packetdataserializer.writeCollection(this.entries, PacketDataSerializer::writeUtf);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleCustomChatCompletions(this);
    }

    public static enum Action {

        ADD, REMOVE, SET;

        private Action() {}
    }
}
