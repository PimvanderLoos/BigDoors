package net.minecraft.network.protocol.handshake;

import net.minecraft.network.PacketListener;

public interface PacketHandshakingInListener extends PacketListener {

    void handleIntention(PacketHandshakingInSetProtocol packethandshakinginsetprotocol);
}
