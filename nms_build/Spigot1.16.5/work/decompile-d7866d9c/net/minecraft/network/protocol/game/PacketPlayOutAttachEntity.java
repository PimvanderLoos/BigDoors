package net.minecraft.network.protocol.game;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;

public class PacketPlayOutAttachEntity implements Packet<PacketListenerPlayOut> {

    private int a;
    private int b;

    public PacketPlayOutAttachEntity() {}

    public PacketPlayOutAttachEntity(Entity entity, @Nullable Entity entity1) {
        this.a = entity.getId();
        this.b = entity1 != null ? entity1.getId() : 0;
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.readInt();
        this.b = packetdataserializer.readInt();
    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.writeInt(this.a);
        packetdataserializer.writeInt(this.b);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }
}
