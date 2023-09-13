package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.EnumHand;

public class PacketPlayInBlockPlace implements Packet<PacketListenerPlayIn> {

    private final EnumHand hand;
    private final int sequence;

    public PacketPlayInBlockPlace(EnumHand enumhand, int i) {
        this.hand = enumhand;
        this.sequence = i;
    }

    public PacketPlayInBlockPlace(PacketDataSerializer packetdataserializer) {
        this.hand = (EnumHand) packetdataserializer.readEnum(EnumHand.class);
        this.sequence = packetdataserializer.readVarInt();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeEnum(this.hand);
        packetdataserializer.writeVarInt(this.sequence);
    }

    public void handle(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.handleUseItem(this);
    }

    public EnumHand getHand() {
        return this.hand;
    }

    public int getSequence() {
        return this.sequence;
    }
}
