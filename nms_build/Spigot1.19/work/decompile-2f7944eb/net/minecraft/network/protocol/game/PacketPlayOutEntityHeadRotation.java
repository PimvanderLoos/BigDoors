package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.World;

public class PacketPlayOutEntityHeadRotation implements Packet<PacketListenerPlayOut> {

    private final int entityId;
    private final byte yHeadRot;

    public PacketPlayOutEntityHeadRotation(Entity entity, byte b0) {
        this.entityId = entity.getId();
        this.yHeadRot = b0;
    }

    public PacketPlayOutEntityHeadRotation(PacketDataSerializer packetdataserializer) {
        this.entityId = packetdataserializer.readVarInt();
        this.yHeadRot = packetdataserializer.readByte();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeVarInt(this.entityId);
        packetdataserializer.writeByte(this.yHeadRot);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleRotateMob(this);
    }

    public Entity getEntity(World world) {
        return world.getEntity(this.entityId);
    }

    public byte getYHeadRot() {
        return this.yHeadRot;
    }
}
