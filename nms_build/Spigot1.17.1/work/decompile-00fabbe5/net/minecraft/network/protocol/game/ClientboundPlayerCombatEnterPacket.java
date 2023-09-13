package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class ClientboundPlayerCombatEnterPacket implements Packet<PacketListenerPlayOut> {

    public ClientboundPlayerCombatEnterPacket() {}

    public ClientboundPlayerCombatEnterPacket(PacketDataSerializer packetdataserializer) {}

    @Override
    public void a(PacketDataSerializer packetdataserializer) {}

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }
}
