package net.minecraft.network.protocol.login;

import net.minecraft.network.PacketListener;

public interface PacketLoginOutListener extends PacketListener {

    void handleHello(PacketLoginOutEncryptionBegin packetloginoutencryptionbegin);

    void handleGameProfile(PacketLoginOutSuccess packetloginoutsuccess);

    void handleDisconnect(PacketLoginOutDisconnect packetloginoutdisconnect);

    void handleCompression(PacketLoginOutSetCompression packetloginoutsetcompression);

    void handleCustomQuery(PacketLoginOutCustomPayload packetloginoutcustompayload);
}
