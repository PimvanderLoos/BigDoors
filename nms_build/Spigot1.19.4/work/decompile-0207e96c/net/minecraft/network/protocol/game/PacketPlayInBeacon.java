package net.minecraft.network.protocol.game;

import java.util.Optional;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.effect.MobEffectList;

public class PacketPlayInBeacon implements Packet<PacketListenerPlayIn> {

    private final Optional<MobEffectList> primary;
    private final Optional<MobEffectList> secondary;

    public PacketPlayInBeacon(Optional<MobEffectList> optional, Optional<MobEffectList> optional1) {
        this.primary = optional;
        this.secondary = optional1;
    }

    public PacketPlayInBeacon(PacketDataSerializer packetdataserializer) {
        this.primary = packetdataserializer.readOptional((packetdataserializer1) -> {
            return (MobEffectList) packetdataserializer1.readById(BuiltInRegistries.MOB_EFFECT);
        });
        this.secondary = packetdataserializer.readOptional((packetdataserializer1) -> {
            return (MobEffectList) packetdataserializer1.readById(BuiltInRegistries.MOB_EFFECT);
        });
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeOptional(this.primary, (packetdataserializer1, mobeffectlist) -> {
            packetdataserializer1.writeId(BuiltInRegistries.MOB_EFFECT, mobeffectlist);
        });
        packetdataserializer.writeOptional(this.secondary, (packetdataserializer1, mobeffectlist) -> {
            packetdataserializer1.writeId(BuiltInRegistries.MOB_EFFECT, mobeffectlist);
        });
    }

    public void handle(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.handleSetBeaconPacket(this);
    }

    public Optional<MobEffectList> getPrimary() {
        return this.primary;
    }

    public Optional<MobEffectList> getSecondary() {
        return this.secondary;
    }
}
