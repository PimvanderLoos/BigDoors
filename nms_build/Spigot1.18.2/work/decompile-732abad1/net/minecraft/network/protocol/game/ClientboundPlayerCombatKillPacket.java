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
        this(combattracker.getMob().getId(), combattracker.getKillerId(), ichatbasecomponent);
    }

    public ClientboundPlayerCombatKillPacket(int i, int j, IChatBaseComponent ichatbasecomponent) {
        this.playerId = i;
        this.killerId = j;
        this.message = ichatbasecomponent;
    }

    public ClientboundPlayerCombatKillPacket(PacketDataSerializer packetdataserializer) {
        this.playerId = packetdataserializer.readVarInt();
        this.killerId = packetdataserializer.readInt();
        this.message = packetdataserializer.readComponent();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeVarInt(this.playerId);
        packetdataserializer.writeInt(this.killerId);
        packetdataserializer.writeComponent(this.message);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handlePlayerCombatKill(this);
    }

    @Override
    public boolean isSkippable() {
        return true;
    }

    public int getKillerId() {
        return this.killerId;
    }

    public int getPlayerId() {
        return this.playerId;
    }

    public IChatBaseComponent getMessage() {
        return this.message;
    }
}
