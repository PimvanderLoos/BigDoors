package net.minecraft.network.protocol.login;

import java.util.Optional;
import java.util.UUID;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public record PacketLoginInStart(String name, Optional<UUID> profileId) implements Packet<PacketLoginInListener> {

    public PacketLoginInStart(PacketDataSerializer packetdataserializer) {
        this(packetdataserializer.readUtf(16), packetdataserializer.readOptional(PacketDataSerializer::readUUID));
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeUtf(this.name, 16);
        packetdataserializer.writeOptional(this.profileId, PacketDataSerializer::writeUUID);
    }

    public void handle(PacketLoginInListener packetlogininlistener) {
        packetlogininlistener.handleHello(this);
    }
}
