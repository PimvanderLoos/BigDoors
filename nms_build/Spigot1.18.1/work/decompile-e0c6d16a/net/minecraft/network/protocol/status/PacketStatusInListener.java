package net.minecraft.network.protocol.status;

import net.minecraft.network.PacketListener;

public interface PacketStatusInListener extends PacketListener {

    void handlePingRequest(PacketStatusInPing packetstatusinping);

    void handleStatusRequest(PacketStatusInStart packetstatusinstart);
}
