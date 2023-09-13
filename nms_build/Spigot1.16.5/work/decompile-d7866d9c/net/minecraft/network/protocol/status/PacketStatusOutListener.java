package net.minecraft.network.protocol.status;

import net.minecraft.network.PacketListener;

public interface PacketStatusOutListener extends PacketListener {

    void a(PacketStatusOutServerInfo packetstatusoutserverinfo);

    void a(PacketStatusOutPong packetstatusoutpong);
}
