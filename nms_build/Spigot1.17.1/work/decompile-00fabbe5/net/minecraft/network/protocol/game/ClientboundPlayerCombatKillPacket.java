package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.damagesource.CombatTracker;

public class ClientboundPlayerCombatKillPacket implements Packet<PacketListenerPlayOut> {

    private final int playerId;
    private final int killerId;
    private final IChatBaseComponent message;

    public ClientboundPlayerCombatKillPacket(CombatTracker combattracker, IChatBaseComponent ichatbasecomponent) {
        this(combattracker.h().getId(), combattracker.j(), ichatbasecomponent);
    }

    public ClientboundPlayerCombatKillPacket(int i, int j, IChatBaseComponent ichatbasecomponent) {
        this.playerId = i;
        this.killerId = j;
        this.message = ichatbasecomponent;
    }

    public ClientboundPlayerCombatKillPacket(PacketDataSerializer packetdataserializer) {
        this.playerId = packetdataserializer.j();
        this.killerId = packetdataserializer.readInt();
        this.message = packetdataserializer.i();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.d(this.playerId);
        packetdataserializer.writeInt(this.killerId);
        packetdataserializer.a(this.message);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    @Override
    public boolean a() {
        return true;
    }

    public int b() {
        return this.killerId;
    }

    public int c() {
        return this.playerId;
    }

    public IChatBaseComponent d() {
        return this.message;
    }
}
