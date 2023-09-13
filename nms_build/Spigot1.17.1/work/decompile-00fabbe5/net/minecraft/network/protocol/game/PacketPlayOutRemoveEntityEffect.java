package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
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
        this.entityId = packetdataserializer.j();
        this.effect = MobEffectList.fromId(packetdataserializer.readUnsignedByte());
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.d(this.entityId);
        packetdataserializer.writeByte(MobEffectList.getId(this.effect));
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    @Nullable
    public Entity a(World world) {
        return world.getEntity(this.entityId);
    }

    @Nullable
    public MobEffectList b() {
        return this.effect;
    }
}
