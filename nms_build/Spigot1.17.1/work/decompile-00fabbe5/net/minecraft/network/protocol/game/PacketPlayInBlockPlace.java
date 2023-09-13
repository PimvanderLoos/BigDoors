package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.EnumHand;

public class PacketPlayInBlockPlace implements Packet<PacketListenerPlayIn> {

    private final EnumHand hand;

    public PacketPlayInBlockPlace(EnumHand enumhand) {
        this.hand = enumhand;
    }

    public PacketPlayInBlockPlace(PacketDataSerializer packetdataserializer) {
        this.hand = (EnumHand) packetdataserializer.a(EnumHand.class);
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.a((Enum) this.hand);
    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public EnumHand b() {
        return this.hand;
    }
}
