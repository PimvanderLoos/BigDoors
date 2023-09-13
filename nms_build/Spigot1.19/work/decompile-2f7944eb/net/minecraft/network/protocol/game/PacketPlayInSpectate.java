package net.minecraft.network.protocol.game;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;

public class PacketPlayInSpectate implements Packet<PacketListenerPlayIn> {

    private final UUID uuid;

    public PacketPlayInSpectate(UUID uuid) {
        this.uuid = uuid;
    }

    public PacketPlayInSpectate(PacketDataSerializer packetdataserializer) {
        this.uuid = packetdataserializer.readUUID();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeUUID(this.uuid);
    }

    public void handle(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.handleTeleportToEntityPacket(this);
    }

    @Nullable
    public Entity getEntity(WorldServer worldserver) {
        return worldserver.getEntity(this.uuid);
    }
}
