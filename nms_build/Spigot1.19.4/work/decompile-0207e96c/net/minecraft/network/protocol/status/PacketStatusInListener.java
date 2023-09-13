package net.minecraft.network.protocol.status;

import net.minecraft.network.protocol.game.ServerPacketListener;

public interface PacketStatusInListener extends ServerPacketListener {

    void handlePingRequest(PacketStatusInPing packetstatusinping);

    void handleStatusRequest(PacketStatusInStart packetstatusinstart);
}
