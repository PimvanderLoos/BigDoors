package net.minecraft.network.protocol.login;

import net.minecraft.network.PacketListener;

public interface PacketLoginInListener extends PacketListener {

    void a(PacketLoginInStart packetlogininstart);

    void a(PacketLoginInEncryptionBegin packetlogininencryptionbegin);

    void a(PacketLoginInCustomPayload packetloginincustompayload);
}
