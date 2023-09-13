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
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeInt(this.sourceId);
        packetdataserializer.writeInt(this.destId);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public int b() {
        return this.sourceId;
    }

    public int c() {
        return this.destId;
    }
}
