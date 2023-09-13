package net.minecraft.network.protocol.login;

import net.minecraft.network.PacketListener;

public interface PacketLoginOutListener extends PacketListener {

    void a(PacketLoginOutEncryptionBegin packetloginoutencryptionbegin);

    void a(PacketLoginOutSuccess packetloginoutsuccess);

    void a(PacketLoginOutDisconnect packetloginoutdisconnect);

    void a(PacketLoginOutSetCompression packetloginoutsetcompression);

    void a(PacketLoginOutCustomPayload packetloginoutcustompayload);
}
