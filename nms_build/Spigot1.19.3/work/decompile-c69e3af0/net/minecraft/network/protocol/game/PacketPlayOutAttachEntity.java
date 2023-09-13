package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;

public class PacketPlayOutAttachEntity implements Packet<PacketListenerPlayOut> {

    private final int sourceId;
    private final int destId;

    public PacketPlayOutAttachEntity(Entity entity, @Nullable Entity entity1) {
        this.sourceId = entity.getId();
        this.destId = entity1 != null ? entity1.getId() : 0;
    }

    public PacketPlayOutAttachEntity(PacketDataSerializer packetdataserializer) {
        this.sourceId = packetdataserializer.readInt();
        this.destId = packetdataserializer.readInt();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeInt(this.sourceId);
        packetdataserializer.writeInt(this.destId);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleEntityLinkPacket(this);
    }

    public int getSourceId() {
        return this.sourceId;
    }

    public int getDestId() {
        return this.destId;
    }
}
