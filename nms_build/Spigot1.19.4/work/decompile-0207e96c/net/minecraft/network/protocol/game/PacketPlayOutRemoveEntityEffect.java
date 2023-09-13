package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.effect.MobEffectList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.World;

public class PacketPlayOutRemoveEntityEffect implements Packet<PacketListenerPlayOut> {

    private final int entityId;
    private final MobEffectList effect;

    public PacketPlayOutRemoveEntityEffect(int i, MobEffectList mobeffectlist) {
        this.entityId = i;
        this.effect = mobeffectlist;
    }

    public PacketPlayOutRemoveEntityEffect(PacketDataSerializer packetdataserializer) {
        this.entityId = packetdataserializer.readVarInt();
        this.effect = (MobEffectList) packetdataserializer.readById(BuiltInRegistries.MOB_EFFECT);
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeVarInt(this.entityId);
        packetdataserializer.writeId(BuiltInRegistries.MOB_EFFECT, this.effect);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleRemoveMobEffect(this);
    }

    @Nullable
    public Entity getEntity(World world) {
        return world.getEntity(this.entityId);
    }

    @Nullable
    public MobEffectList getEffect() {
        return this.effect;
    }
}
