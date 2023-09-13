package net.minecraft.network.protocol.game;

import net.minecraft.network.protocol.BundlePacket;
import net.minecraft.network.protocol.Packet;

public class ClientboundBundlePacket extends BundlePacket<PacketListenerPlayOut> {

    public ClientboundBundlePacket(Iterable<Packet<PacketListenerPlayOut>> iterable) {
        super(iterable);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleBundlePacket(this);
    }
}
