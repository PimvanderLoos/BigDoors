package net.minecraft.world.level.gameevent;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;

public class EntityPositionSource implements PositionSource {

    public static final Codec<EntityPositionSource> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(UUIDUtil.CODEC.fieldOf("source_entity").forGetter(EntityPositionSource::getUuid), Codec.FLOAT.fieldOf("y_offset").orElse(0.0F).forGetter((entitypositionsource) -> {
            return entitypositionsource.yOffset;
        })).apply(instance, (uuid, ofloat) -> {
            return new EntityPositionSource(Either.right(Either.left(uuid)), ofloat);
        });
    });
    private Either<Entity, Either<UUID, Integer>> entityOrUuidOrId;
    final float yOffset;

    public EntityPositionSource(Entity entity, float f) {
        this(Either.left(entity), f);
    }

    EntityPositionSource(Either<Entity, Either<UUID, Integer>> either, float f) {
        this.entityOrUuidOrId = either;
        this.yOffset = f;
    }

    @Override
    public Optional<Vec3D> getPosition(World world) {
        if (this.entityOrUuidOrId.left().isEmpty()) {
            this.resolveEntity(world);
        }

        return this.entityOrUuidOrId.left().map((entity) -> {
            return entity.position().add(0.0D, (double) this.yOffset, 0.0D);
        });
    }

    private void resolveEntity(World world) {
        ((Optional) this.entityOrUuidOrId.map(Optional::of, (either) -> {
            Function function = (uuid) -> {
                Entity entity;

                if (world instanceof WorldServer) {
                    WorldServer worldserver = (WorldServer) world;

                    entity = worldserver.getEntity(uuid);
                } else {
                    entity = null;
                }

                return entity;
            };

            Objects.requireNonNull(world);
            return Optional.ofNullable((Entity) either.map(function, world::getEntity));
        })).ifPresent((entity) -> {
            this.entityOrUuidOrId = Either.left(entity);
        });
    }

    private UUID getUuid() {
        return (UUID) this.entityOrUuidOrId.map(Entity::getUUID, (either) -> {
            return (UUID) either.map(Function.identity(), (integer) -> {
                throw new RuntimeException("Unable to get entityId from uuid");
            });
        });
    }

    int getId() {
        return (Integer) this.entityOrUuidOrId.map(Entity::getId, (either) -> {
            return (Integer) either.map((uuid) -> {
                throw new IllegalStateException("Unable to get entityId from uuid");
            }, Function.identity());
        });
    }

    @Override
    public PositionSourceType<?> getType() {
        return PositionSourceType.ENTITY;
    }

    public static class a implements PositionSourceType<EntityPositionSource> {

        public a() {}

        @Override
        public EntityPositionSource read(PacketDataSerializer packetdataserializer) {
            return new EntityPositionSource(Either.right(Either.right(packetdataserializer.readVarInt())), packetdataserializer.readFloat());
        }

        public void write(PacketDataSerializer packetdataserializer, EntityPositionSource entitypositionsource) {
            packetdataserializer.writeVarInt(entitypositionsource.getId());
            packetdataserializer.writeFloat(entitypositionsource.yOffset);
        }

        @Override
        public Codec<EntityPositionSource> codec() {
            return EntityPositionSource.CODEC;
        }
    }
}
