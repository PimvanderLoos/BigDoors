package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.World;

public class PacketPlayOutCamera implements Packet<PacketListenerPlayOut> {

    private final int cameraId;

    public PacketPlayOutCamera(Entity entity) {
        this.cameraId = entity.getId();
    }

    public PacketPlayOutCamera(PacketDataSerializer packetdataserializer) {
        this.cameraId = packetdataserializer.j();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.d(this.cameraId);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    @Nullable
    public Entity a(World world) {
        return world.getEntity(this.cameraId);
    }
}
