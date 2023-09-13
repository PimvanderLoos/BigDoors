package net.minecraft.world.level.gameevent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.World;

public class EntityPositionSource implements PositionSource {

    public static final Codec<EntityPositionSource> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.INT.fieldOf("source_entity_id").forGetter((entitypositionsource) -> {
            return entitypositionsource.sourceEntityId;
        })).apply(instance, EntityPositionSource::new);
    });
    final int sourceEntityId;
    private Optional<Entity> sourceEntity = Optional.empty();

    public EntityPositionSource(int i) {
        this.sourceEntityId = i;
    }

    @Override
    public Optional<BlockPosition> a(World world) {
        if (!this.sourceEntity.isPresent()) {
            this.sourceEntity = Optional.ofNullable(world.getEntity(this.sourceEntityId));
        }

        return this.sourceEntity.map(Entity::getChunkCoordinates);
    }

    @Override
    public PositionSourceType<?> a() {
        return PositionSourceType.ENTITY;
    }

    public static class a implements PositionSourceType<EntityPositionSource> {

        public a() {}

        @Override
        public EntityPositionSource b(PacketDataSerializer packetdataserializer) {
            return new EntityPositionSource(packetdataserializer.j());
        }

        public void a(PacketDataSerializer packetdataserializer, EntityPositionSource entitypositionsource) {
            packetdataserializer.d(entitypositionsource.sourceEntityId);
        }

        @Override
        public Codec<EntityPositionSource> a() {
            return EntityPositionSource.CODEC;
        }
    }
}
