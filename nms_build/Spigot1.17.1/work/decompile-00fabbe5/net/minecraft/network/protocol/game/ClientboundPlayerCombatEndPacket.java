package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.damagesource.CombatTracker;

public class ClientboundPlayerCombatEndPacket implements Packet<PacketListenerPlayOut> {

    private final int killerId;
    private final int duration;

    public ClientboundPlayerCombatEndPacket(CombatTracker combattracker) {
        this(combattracker.j(), combattracker.f());
    }

    public ClientboundPlayerCombatEndPacket(int i, int j) {
        this.killerId = i;
        this.duration = j;
    }

    public ClientboundPlayerCombatEndPacket(PacketDataSerializer packetdataserializer) {
        this.duration = packetdataserializer.j();
        this.killerId = packetdataserializer.readInt();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.d(this.duration);
        packetdataserializer.writeInt(this.killerId);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }
}
