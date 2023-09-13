package net.minecraft.network.protocol.game;

import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;

public record ClientboundDamageEventPacket(int entityId, int sourceTypeId, int sourceCauseId, int sourceDirectId, Optional<Vec3D> sourcePosition) implements Packet<PacketListenerPlayOut> {

    public ClientboundDamageEventPacket(Entity entity, DamageSource damagesource) {
        this(entity.getId(), entity.getLevel().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getId(damagesource.type()), damagesource.getEntity() != null ? damagesource.getEntity().getId() : -1, damagesource.getDirectEntity() != null ? damagesource.getDirectEntity().getId() : -1, Optional.ofNullable(damagesource.sourcePositionRaw()));
    }

    public ClientboundDamageEventPacket(PacketDataSerializer packetdataserializer) {
        this(packetdataserializer.readVarInt(), packetdataserializer.readVarInt(), readOptionalEntityId(packetdataserializer), readOptionalEntityId(packetdataserializer), packetdataserializer.readOptional((packetdataserializer1) -> {
            return new Vec3D(packetdataserializer1.readDouble(), packetdataserializer1.readDouble(), packetdataserializer1.readDouble());
        }));
    }

    private static void writeOptionalEntityId(PacketDataSerializer packetdataserializer, int i) {
        packetdataserializer.writeVarInt(i + 1);
    }

    private static int readOptionalEntityId(PacketDataSerializer packetdataserializer) {
        return packetdataserializer.readVarInt() - 1;
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeVarInt(this.entityId);
        packetdataserializer.writeVarInt(this.sourceTypeId);
        writeOptionalEntityId(packetdataserializer, this.sourceCauseId);
        writeOptionalEntityId(packetdataserializer, this.sourceDirectId);
        packetdataserializer.writeOptional(this.sourcePosition, (packetdataserializer1, vec3d) -> {
            packetdataserializer1.writeDouble(vec3d.x());
            packetdataserializer1.writeDouble(vec3d.y());
            packetdataserializer1.writeDouble(vec3d.z());
        });
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleDamageEvent(this);
    }

    public DamageSource getSource(World world) {
        Holder<DamageType> holder = (Holder) world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolder(this.sourceTypeId).get();

        if (this.sourcePosition.isPresent()) {
            return new DamageSource(holder, (Vec3D) this.sourcePosition.get());
        } else {
            Entity entity = world.getEntity(this.sourceCauseId);
            Entity entity1 = world.getEntity(this.sourceDirectId);

            return new DamageSource(holder, entity1, entity);
        }
    }
}
