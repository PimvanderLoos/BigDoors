package net.minecraft.network.protocol.handshake;

import net.minecraft.network.protocol.game.ServerPacketListener;

public interface PacketHandshakingInListener extends ServerPacketListener {

    void handleIntention(PacketHandshakingInSetProtocol packethandshakinginsetprotocol);
}
