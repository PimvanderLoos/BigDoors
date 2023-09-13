package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.EnumHand;

public class PacketPlayOutOpenBook implements Packet<PacketListenerPlayOut> {

    private final EnumHand hand;

    public PacketPlayOutOpenBook(EnumHand enumhand) {
        this.hand = enumhand;
    }

    public PacketPlayOutOpenBook(PacketDataSerializer packetdataserializer) {
        this.hand = (EnumHand) packetdataserializer.readEnum(EnumHand.class);
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeEnum(this.hand);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleOpenBook(this);
    }

    public EnumHand getHand() {
        return this.hand;
    }
}
