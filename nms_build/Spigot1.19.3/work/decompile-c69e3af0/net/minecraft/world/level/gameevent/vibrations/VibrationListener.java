package net.minecraft.world.level.gameevent.vibrations;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.particles.VibrationParticleOption;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.GameEventTags;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipBlockStateContext;
import net.minecraft.world.level.World;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.Vec3D;

public class VibrationListener implements GameEventListener {

    @VisibleForTesting
    public static final Object2IntMap<GameEvent> VIBRATION_FREQUENCY_FOR_EVENT = Object2IntMaps.unmodifiable((Object2IntMap) SystemUtils.make(new Object2IntOpenHashMap(), (object2intopenhashmap) -> {
        object2intopenhashmap.put(GameEvent.STEP, 1);
        object2intopenhashmap.put(GameEvent.FLAP, 2);
        object2intopenhashmap.put(GameEvent.SWIM, 3);
        object2intopenhashmap.put(GameEvent.ELYTRA_GLIDE, 4);
        object2intopenhashmap.put(GameEvent.HIT_GROUND, 5);
        object2intopenhashmap.put(GameEvent.TELEPORT, 5);
        object2intopenhashmap.put(GameEvent.SPLASH, 6);
        object2intopenhashmap.put(GameEvent.ENTITY_SHAKE, 6);
        object2intopenhashmap.put(GameEvent.BLOCK_CHANGE, 6);
        object2intopenhashmap.put(GameEvent.NOTE_BLOCK_PLAY, 6);
        object2intopenhashmap.put(GameEvent.PROJECTILE_SHOOT, 7);
        object2intopenhashmap.put(GameEvent.DRINK, 7);
        object2intopenhashmap.put(GameEvent.PRIME_FUSE, 7);
        object2intopenhashmap.put(GameEvent.PROJECTILE_LAND, 8);
        object2intopenhashmap.put(GameEvent.EAT, 8);
        object2intopenhashmap.put(GameEvent.ENTITY_INTERACT, 8);
        object2intopenhashmap.put(GameEvent.ENTITY_DAMAGE, 8);
        object2intopenhashmap.put(GameEvent.EQUIP, 9);
        object2intopenhashmap.put(GameEvent.SHEAR, 9);
        object2intopenhashmap.put(GameEvent.ENTITY_ROAR, 9);
        object2intopenhashmap.put(GameEvent.BLOCK_CLOSE, 10);
        object2intopenhashmap.put(GameEvent.BLOCK_DEACTIVATE, 10);
        object2intopenhashmap.put(GameEvent.BLOCK_DETACH, 10);
        object2intopenhashmap.put(GameEvent.DISPENSE_FAIL, 10);
        object2intopenhashmap.put(GameEvent.BLOCK_OPEN, 11);
        object2intopenhashmap.put(GameEvent.BLOCK_ACTIVATE, 11);
        object2intopenhashmap.put(GameEvent.BLOCK_ATTACH, 11);
        object2intopenhashmap.put(GameEvent.ENTITY_PLACE, 12);
        object2intopenhashmap.put(GameEvent.BLOCK_PLACE, 12);
        object2intopenhashmap.put(GameEvent.FLUID_PLACE, 12);
        object2intopenhashmap.put(GameEvent.ENTITY_DIE, 13);
        object2intopenhashmap.put(GameEvent.BLOCK_DESTROY, 13);
        object2intopenhashmap.put(GameEvent.FLUID_PICKUP, 13);
        object2intopenhashmap.put(GameEvent.ITEM_INTERACT_FINISH, 14);
        object2intopenhashmap.put(GameEvent.CONTAINER_CLOSE, 14);
        object2intopenhashmap.put(GameEvent.PISTON_CONTRACT, 14);
        object2intopenhashmap.put(GameEvent.PISTON_EXTEND, 15);
        object2intopenhashmap.put(GameEvent.CONTAINER_OPEN, 15);
        object2intopenhashmap.put(GameEvent.EXPLODE, 15);
        object2intopenhashmap.put(GameEvent.LIGHTNING_STRIKE, 15);
        object2intopenhashmap.put(GameEvent.INSTRUMENT_PLAY, 15);
    }));
    protected final PositionSource listenerSource;
    protected final int listenerRange;
    protected final VibrationListener.a config;
    @Nullable
    protected VibrationInfo currentVibration;
    protected int travelTimeInTicks;
    private final VibrationSelector selectionStrategy;

    public static Codec<VibrationListener> codec(VibrationListener.a vibrationlistener_a) {
        return RecordCodecBuilder.create((instance) -> {
            return instance.group(PositionSource.CODEC.fieldOf("source").forGetter((vibrationlistener) -> {
                return vibrationlistener.listenerSource;
            }), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("range").forGetter((vibrationlistener) -> {
                return vibrationlistener.listenerRange;
            }), VibrationInfo.CODEC.optionalFieldOf("event").forGetter((vibrationlistener) -> {
                return Optional.ofNullable(vibrationlistener.currentVibration);
            }), VibrationSelector.CODEC.fieldOf("selector").forGetter((vibrationlistener) -> {
                return vibrationlistener.selectionStrategy;
            }), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("event_delay").orElse(0).forGetter((vibrationlistener) -> {
                return vibrationlistener.travelTimeInTicks;
            })).apply(instance, (positionsource, integer, optional, vibrationselector, integer1) -> {
                return new VibrationListener(positionsource, integer, vibrationlistener_a, (VibrationInfo) optional.orElse((Object) null), vibrationselector, integer1);
            });
        });
    }

    private VibrationListener(PositionSource positionsource, int i, VibrationListener.a vibrationlistener_a, @Nullable VibrationInfo vibrationinfo, VibrationSelector vibrationselector, int j) {
        this.listenerSource = positionsource;
        this.listenerRange = i;
        this.config = vibrationlistener_a;
        this.currentVibration = vibrationinfo;
        this.travelTimeInTicks = j;
        this.selectionStrategy = vibrationselector;
    }

    public VibrationListener(PositionSource positionsource, int i, VibrationListener.a vibrationlistener_a) {
        this(positionsource, i, vibrationlistener_a, (VibrationInfo) null, new VibrationSelector(), 0);
    }

    public static int getGameEventFrequency(GameEvent gameevent) {
        return VibrationListener.VIBRATION_FREQUENCY_FOR_EVENT.getOrDefault(gameevent, 0);
    }

    public void tick(World world) {
        if (world instanceof WorldServer) {
            WorldServer worldserver = (WorldServer) world;

            if (this.currentVibration == null) {
                this.selectionStrategy.chosenCandidate(worldserver.getGameTime()).ifPresent((vibrationinfo) -> {
                    this.currentVibration = vibrationinfo;
                    Vec3D vec3d = this.currentVibration.pos();

                    this.travelTimeInTicks = MathHelper.floor(this.currentVibration.distance());
                    worldserver.sendParticles(new VibrationParticleOption(this.listenerSource, this.travelTimeInTicks), vec3d.x, vec3d.y, vec3d.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                    this.config.onSignalSchedule();
                    this.selectionStrategy.startOver();
                });
            }

            if (this.currentVibration != null) {
                --this.travelTimeInTicks;
                if (this.travelTimeInTicks <= 0) {
                    this.travelTimeInTicks = 0;
                    this.config.onSignalReceive(worldserver, this, new BlockPosition(this.currentVibration.pos()), this.currentVibration.gameEvent(), (Entity) this.currentVibration.getEntity(worldserver).orElse((Object) null), (Entity) this.currentVibration.getProjectileOwner(worldserver).orElse((Object) null), this.currentVibration.distance());
                    this.currentVibration = null;
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
    public boolean handleGameEvent(WorldServer worldserver, GameEvent gameevent, GameEvent.a gameevent_a, Vec3D vec3d) {
        if (this.currentVibration != null) {
            return false;
        } else if (!this.config.isValidVibration(gameevent, gameevent_a)) {
            return false;
        } else {
            Optional<Vec3D> optional = this.listenerSource.getPosition(worldserver);

            if (optional.isEmpty()) {
                return false;
            } else {
                Vec3D vec3d1 = (Vec3D) optional.get();

                if (!this.config.shouldListen(worldserver, this, new BlockPosition(vec3d), gameevent, gameevent_a)) {
                    return false;
                } else if (isOccluded(worldserver, vec3d, vec3d1)) {
                    return false;
                } else {
                    this.scheduleVibration(worldserver, gameevent, gameevent_a, vec3d, vec3d1);
                    return true;
                }
            }
        }
    }

    public void forceGameEvent(WorldServer worldserver, GameEvent gameevent, GameEvent.a gameevent_a, Vec3D vec3d) {
        this.listenerSource.getPosition(worldserver).ifPresent((vec3d1) -> {
            this.scheduleVibration(worldserver, gameevent, gameevent_a, vec3d, vec3d1);
        });
    }

    public void scheduleVibration(WorldServer worldserver, GameEvent gameevent, GameEvent.a gameevent_a, Vec3D vec3d, Vec3D vec3d1) {
        this.selectionStrategy.addCandidate(new VibrationInfo(gameevent, (float) vec3d.distanceTo(vec3d1), vec3d, gameevent_a.sourceEntity()), worldserver.getGameTime());
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

    public interface a {

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
}
