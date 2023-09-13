package net.minecraft.network.protocol.game;

import java.util.List;
import java.util.UUID;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public record ClientboundPlayerInfoRemovePacket(List<UUID> profileIds) implements Packet<PacketListenerPlayOut> {

    public ClientboundPlayerInfoRemovePacket(PacketDataSerializer packetdataserializer) {
        this(packetdataserializer.readList(PacketDataSerializer::readUUID));
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeCollection(this.profileIds, PacketDataSerializer::writeUUID);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handlePlayerInfoRemove(this);
    }
}
