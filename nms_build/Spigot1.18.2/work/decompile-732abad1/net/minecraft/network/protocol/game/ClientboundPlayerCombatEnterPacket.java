package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class ClientboundPlayerCombatEnterPacket implements Packet<PacketListenerPlayOut> {

    public ClientboundPlayerCombatEnterPacket() {}

    public ClientboundPlayerCombatEnterPacket(PacketDataSerializer packetdataserializer) {}

    @Override
    public void write(PacketDataSerializer packetdataserializer) {}

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handlePlayerCombatEnter(this);
    }
}
