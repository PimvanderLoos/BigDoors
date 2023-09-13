package net.minecraft.network.protocol.login;

import net.minecraft.network.PacketListener;

public interface PacketLoginInListener extends PacketListener {

    void handleHello(PacketLoginInStart packetlogininstart);

    void handleKey(PacketLoginInEncryptionBegin packetlogininencryptionbegin);

    void handleCustomQueryPacket(PacketLoginInCustomPayload packetloginincustompayload);
}
