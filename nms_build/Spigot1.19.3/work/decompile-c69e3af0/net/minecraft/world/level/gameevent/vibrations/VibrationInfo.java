package net.minecraft.world.level.gameevent.vibrations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.IProjectile;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3D;

public record VibrationInfo(GameEvent gameEvent, float distance, Vec3D pos, @Nullable UUID uuid, @Nullable UUID projectileOwnerUuid, @Nullable Entity entity) {

    public static final Codec<VibrationInfo> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(BuiltInRegistries.GAME_EVENT.byNameCodec().fieldOf("game_event").forGetter(VibrationInfo::gameEvent), Codec.floatRange(0.0F, Float.MAX_VALUE).fieldOf("distance").forGetter(VibrationInfo::distance), Vec3D.CODEC.fieldOf("pos").forGetter(VibrationInfo::pos), UUIDUtil.CODEC.optionalFieldOf("source").forGetter((vibrationinfo) -> {
            return Optional.ofNullable(vibrationinfo.uuid());
        }), UUIDUtil.CODEC.optionalFieldOf("projectile_owner").forGetter((vibrationinfo) -> {
            return Optional.ofNullable(vibrationinfo.projectileOwnerUuid());
        })).apply(instance, (gameevent, ofloat, vec3d, optional, optional1) -> {
            return new VibrationInfo(gameevent, ofloat, vec3d, (UUID) optional.orElse((Object) null), (UUID) optional1.orElse((Object) null));
        });
    });

    public VibrationInfo(GameEvent gameevent, float f, Vec3D vec3d, @Nullable UUID uuid, @Nullable UUID uuid1) {
        this(gameevent, f, vec3d, uuid, uuid1, (Entity) null);
    }

    public VibrationInfo(GameEvent gameevent, float f, Vec3D vec3d, @Nullable Entity entity) {
        this(gameevent, f, vec3d, entity == null ? null : entity.getUUID(), getProjectileOwner(entity), entity);
    }

    @Nullable
    private static UUID getProjectileOwner(@Nullable Entity entity) {
        if (entity instanceof IProjectile) {
            IProjectile iprojectile = (IProjectile) entity;

            if (iprojectile.getOwner() != null) {
                return iprojectile.getOwner().getUUID();
            }
        }

        return null;
    }

    public Optional<Entity> getEntity(WorldServer worldserver) {
        return Optional.ofNullable(this.entity).or(() -> {
            Optional optional = Optional.ofNullable(this.uuid);

            Objects.requireNonNull(worldserver);
            return optional.map(worldserver::getEntity);
        });
    }

    public Optional<Entity> getProjectileOwner(WorldServer worldserver) {
        return this.getEntity(worldserver).filter((entity) -> {
            return entity instanceof IProjectile;
        }).map((entity) -> {
            return (IProjectile) entity;
        }).map(IProjectile::getOwner).or(() -> {
            Optional optional = Optional.ofNullable(this.projectileOwnerUuid);

            Objects.requireNonNull(worldserver);
            return optional.map(worldserver::getEntity);
        });
    }
}
