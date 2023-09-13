package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.EnumHand;

public class PacketPlayInArmAnimation implements Packet<PacketListenerPlayIn> {

    private final EnumHand hand;

    public PacketPlayInArmAnimation(EnumHand enumhand) {
        this.hand = enumhand;
    }

    public PacketPlayInArmAnimation(PacketDataSerializer packetdataserializer) {
        this.hand = (EnumHand) packetdataserializer.readEnum(EnumHand.class);
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeEnum(this.hand);
    }

    public void handle(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.handleAnimate(this);
    }

    public EnumHand getHand() {
        return this.hand;
    }
}
