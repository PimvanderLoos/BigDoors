package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.EntityLiving;

public record ClientboundHurtAnimationPacket(int id, float yaw) implements Packet<PacketListenerPlayOut> {

    public ClientboundHurtAnimationPacket(EntityLiving entityliving) {
        this(entityliving.getId(), entityliving.getHurtDir());
    }

    public ClientboundHurtAnimationPacket(PacketDataSerializer packetdataserializer) {
        this(packetdataserializer.readVarInt(), packetdataserializer.readFloat());
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeVarInt(this.id);
        packetdataserializer.writeFloat(this.yaw);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleHurtAnimation(this);
    }
}
