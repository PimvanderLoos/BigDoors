package net.minecraft.world.level.gameevent.vibrations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.IRegistry;
import net.minecraft.core.particles.VibrationParticleOption;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.GameEventTags;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.IProjectile;
import net.minecraft.world.level.ClipBlockStateContext;
import net.minecraft.world.level.World;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.Vec3D;

public class VibrationListener implements GameEventListener {

    protected final PositionSource listenerSource;
    protected final int listenerRange;
    protected final VibrationListener.b config;
    @Nullable
    protected VibrationListener.a receivingEvent;
    protected float receivingDistance;
    protected int travelTimeInTicks;

    public static Codec<VibrationListener> codec(VibrationListener.b vibrationlistener_b) {
        return RecordCodecBuilder.create((instance) -> {
            return instance.group(PositionSource.CODEC.fieldOf("source").forGetter((vibrationlistener) -> {
                return vibrationlistener.listenerSource;
            }), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("range").forGetter((vibrationlistener) -> {
                return vibrationlistener.listenerRange;
            }), VibrationListener.a.CODEC.optionalFieldOf("event").forGetter((vibrationlistener) -> {
                return Optional.ofNullable(vibrationlistener.receivingEvent);
            }), Codec.floatRange(0.0F, Float.MAX_VALUE).fieldOf("event_distance").orElse(0.0F).forGetter((vibrationlistener) -> {
                return vibrationlistener.receivingDistance;
            }), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("event_delay").orElse(0).forGetter((vibrationlistener) -> {
                return vibrationlistener.travelTimeInTicks;
            })).apply(instance, (positionsource, integer, optional, ofloat, integer1) -> {
                return new VibrationListener(positionsource, integer, vibrationlistener_b, (VibrationListener.a) optional.orElse((Object) null), ofloat, integer1);
            });
        });
    }

    public VibrationListener(PositionSource positionsource, int i, VibrationListener.b vibrationlistener_b, @Nullable VibrationListener.a vibrationlistener_a, float f, int j) {
        this.listenerSource = positionsource;
        this.listenerRange = i;
        this.config = vibrationlistener_b;
        this.receivingEvent = vibrationlistener_a;
        this.receivingDistance = f;
        this.travelTimeInTicks = j;
    }

    public void tick(World world) {
        if (world instanceof WorldServer) {
            WorldServer worldserver = (WorldServer) world;

            if (this.receivingEvent != null) {
                --this.travelTimeInTicks;
                if (this.travelTimeInTicks <= 0) {
                    this.travelTimeInTicks = 0;
                    this.config.onSignalReceive(worldserver, this, new BlockPosition(this.receivingEvent.pos), this.receivingEvent.gameEvent, (Entity) this.receivingEvent.getEntity(worldserver).orElse((Object) null), (Entity) this.receivingEvent.getProjectileOwner(worldserver).orElse((Object) null), this.receivingDistance);
                    this.receivingEvent = null;
                }
            }
        }

    }

    @Override
    public PositionSource getListenerSource() {
        return this.listenerSource;
    }

    @Override
    public int getListenerRadius() {
        return this.listenerRange;
    }

    @Override
    public boolean handleGameEvent(WorldServer worldserver, GameEvent.b gameevent_b) {
        if (this.receivingEvent != null) {
            return false;
        } else {
            GameEvent gameevent = gameevent_b.gameEvent();
            GameEvent.a gameevent_a = gameevent_b.context();

            if (!this.config.isValidVibration(gameevent, gameevent_a)) {
                return false;
            } else {
                Optional<Vec3D> optional = this.listenerSource.getPosition(worldserver);

                if (optional.isEmpty()) {
                    return false;
                } else {
                    Vec3D vec3d = gameevent_b.source();
                    Vec3D vec3d1 = (Vec3D) optional.get();

                    if (!this.config.shouldListen(worldserver, this, new BlockPosition(vec3d), gameevent, gameevent_a)) {
                        return false;
                    } else if (isOccluded(worldserver, vec3d, vec3d1)) {
                        return false;
                    } else {
                        this.scheduleSignal(worldserver, gameevent, gameevent_a, vec3d, vec3d1);
                        return true;
                    }
                }
            }
        }
    }

    private void scheduleSignal(WorldServer worldserver, GameEvent gameevent, GameEvent.a gameevent_a, Vec3D vec3d, Vec3D vec3d1) {
        this.receivingDistance = (float) vec3d.distanceTo(vec3d1);
        this.receivingEvent = new VibrationListener.a(gameevent, this.receivingDistance, vec3d, gameevent_a.sourceEntity());
        this.travelTimeInTicks = MathHelper.floor(this.receivingDistance);
        worldserver.sendParticles(new VibrationParticleOption(this.listenerSource, this.travelTimeInTicks), vec3d.x, vec3d.y, vec3d.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        this.config.onSignalSchedule();
    }

    private static boolean isOccluded(World world, Vec3D vec3d, Vec3D vec3d1) {
        Vec3D vec3d2 = new Vec3D((double) MathHelper.floor(vec3d.x) + 0.5D, (double) MathHelper.floor(vec3d.y) + 0.5D, (double) MathHelper.floor(vec3d.z) + 0.5D);
        Vec3D vec3d3 = new Vec3D((double) MathHelper.floor(vec3d1.x) + 0.5D, (double) MathHelper.floor(vec3d1.y) + 0.5D, (double) MathHelper.floor(vec3d1.z) + 0.5D);
        EnumDirection[] aenumdirection = EnumDirection.values();
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection[j];
            Vec3D vec3d4 = vec3d2.relative(enumdirection, 9.999999747378752E-6D);

            if (world.isBlockInLine(new ClipBlockStateContext(vec3d4, vec3d3, (iblockdata) -> {
                return iblockdata.is(TagsBlock.OCCLUDES_VIBRATION_SIGNALS);
            })).getType() != MovingObjectPosition.EnumMovingObjectType.BLOCK) {
                return false;
            }
        }

        return true;
    }

    public interface b {

        default TagKey<GameEvent> getListenableEvents() {
            return GameEventTags.VIBRATIONS;
        }

        default boolean canTriggerAvoidVibration() {
            return false;
        }

        default boolean isValidVibration(GameEvent gameevent, GameEvent.a gameevent_a) {
            if (!gameevent.is(this.getListenableEvents())) {
                return false;
            } else {
                Entity entity = gameevent_a.sourceEntity();

                if (entity != null) {
                    if (entity.isSpectator()) {
                        return false;
                    }

                    if (entity.isSteppingCarefully() && gameevent.is(GameEventTags.IGNORE_VIBRATIONS_SNEAKING)) {
                        if (this.canTriggerAvoidVibration() && entity instanceof EntityPlayer) {
                            EntityPlayer entityplayer = (EntityPlayer) entity;

                            CriterionTriggers.AVOID_VIBRATION.trigger(entityplayer);
                        }

                        return false;
                    }

                    if (entity.dampensVibrations()) {
                        return false;
                    }
                }

                return gameevent_a.affectedState() != null ? !gameevent_a.affectedState().is(TagsBlock.DAMPENS_VIBRATIONS) : true;
            }
        }

        boolean shouldListen(WorldServer worldserver, GameEventListener gameeventlistener, BlockPosition blockposition, GameEvent gameevent, GameEvent.a gameevent_a);

        void onSignalReceive(WorldServer worldserver, GameEventListener gameeventlistener, BlockPosition blockposition, GameEvent gameevent, @Nullable Entity entity, @Nullable Entity entity1, float f);

        default void onSignalSchedule() {}
    }

    public static record a(GameEvent gameEvent, float distance, Vec3D pos, @Nullable UUID uuid, @Nullable UUID projectileOwnerUuid, @Nullable Entity entity) {

        public static final Codec<VibrationListener.a> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(IRegistry.GAME_EVENT.byNameCodec().fieldOf("game_event").forGetter(VibrationListener.a::gameEvent), Codec.floatRange(0.0F, Float.MAX_VALUE).fieldOf("distance").forGetter(VibrationListener.a::distance), Vec3D.CODEC.fieldOf("pos").forGetter(VibrationListener.a::pos), ExtraCodecs.UUID.optionalFieldOf("source").forGetter((vibrationlistener_a) -> {
                return Optional.ofNullable(vibrationlistener_a.uuid());
            }), ExtraCodecs.UUID.optionalFieldOf("projectile_owner").forGetter((vibrationlistener_a) -> {
                return Optional.ofNullable(vibrationlistener_a.projectileOwnerUuid());
            })).apply(instance, (gameevent, ofloat, vec3d, optional, optional1) -> {
                return new VibrationListener.a(gameevent, ofloat, vec3d, (UUID) optional.orElse((Object) null), (UUID) optional1.orElse((Object) null));
            });
        });

        public a(GameEvent gameevent, float f, Vec3D vec3d, @Nullable UUID uuid, @Nullable UUID uuid1) {
            this(gameevent, f, vec3d, uuid, uuid1, (Entity) null);
        }

        public a(GameEvent gameevent, float f, Vec3D vec3d, @Nullable Entity entity) {
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
}
