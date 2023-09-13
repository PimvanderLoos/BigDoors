package net.minecraft.network.protocol.login;

import net.minecraft.network.protocol.game.ServerPacketListener;

public interface PacketLoginInListener extends ServerPacketListener {

    void handleHello(PacketLoginInStart packetlogininstart);

    void handleKey(PacketLoginInEncryptionBegin packetlogininencryptionbegin);

    void handleCustomQueryPacket(PacketLoginInCustomPayload packetloginincustompayload);
}
