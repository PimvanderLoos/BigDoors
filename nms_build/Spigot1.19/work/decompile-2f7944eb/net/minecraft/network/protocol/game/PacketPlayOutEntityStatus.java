package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.World;

public class PacketPlayOutEntityStatus implements Packet<PacketListenerPlayOut> {

    private final int entityId;
    private final byte eventId;

    public PacketPlayOutEntityStatus(Entity entity, byte b0) {
        this.entityId = entity.getId();
        this.eventId = b0;
    }

    public PacketPlayOutEntityStatus(PacketDataSerializer packetdataserializer) {
        this.entityId = packetdataserializer.readInt();
        this.eventId = packetdataserializer.readByte();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeInt(this.entityId);
        packetdataserializer.writeByte(this.eventId);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleEntityEvent(this);
    }

    @Nullable
    public Entity getEntity(World world) {
        return world.getEntity(this.entityId);
    }

    public byte getEventId() {
        return this.eventId;
    }
}
