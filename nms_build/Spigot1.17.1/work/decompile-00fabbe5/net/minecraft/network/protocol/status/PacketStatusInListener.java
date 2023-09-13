package net.minecraft.network.protocol.status;

import net.minecraft.network.PacketListener;

public interface PacketStatusInListener extends PacketListener {

    void a(PacketStatusInPing packetstatusinping);

    void a(PacketStatusInStart packetstatusinstart);
}
