package net.minecraft.world.entity.monster.warden;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Predicate;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.IEntitySelector;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;

public class WardenSpawnTracker {

    public static final Codec<WardenSpawnTracker> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(ExtraCodecs.NON_NEGATIVE_INT.fieldOf("ticks_since_last_warning").orElse(0).forGetter((wardenspawntracker) -> {
            return wardenspawntracker.ticksSinceLastWarning;
        }), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("warning_level").orElse(0).forGetter((wardenspawntracker) -> {
            return wardenspawntracker.warningLevel;
        }), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("cooldown_ticks").orElse(0).forGetter((wardenspawntracker) -> {
            return wardenspawntracker.cooldownTicks;
        })).apply(instance, WardenSpawnTracker::new);
    });
    public static final int MAX_WARNING_LEVEL = 4;
    private static final double PLAYER_SEARCH_RADIUS = 16.0D;
    private static final int WARNING_CHECK_DIAMETER = 48;
    private static final int DECREASE_WARNING_LEVEL_EVERY_INTERVAL = 12000;
    private static final int WARNING_LEVEL_INCREASE_COOLDOWN = 200;
    private int ticksSinceLastWarning;
    private int warningLevel;
    private int cooldownTicks;

    public WardenSpawnTracker(int i, int j, int k) {
        this.ticksSinceLastWarning = i;
        this.warningLevel = j;
        this.cooldownTicks = k;
    }

    public void tick() {
        if (this.ticksSinceLastWarning >= 12000) {
            this.decreaseWarningLevel();
            this.ticksSinceLastWarning = 0;
        } else {
            ++this.ticksSinceLastWarning;
        }

        if (this.cooldownTicks > 0) {
            --this.cooldownTicks;
        }

    }

    public void reset() {
        this.ticksSinceLastWarning = 0;
        this.warningLevel = 0;
        this.cooldownTicks = 0;
    }

    public static OptionalInt tryWarn(WorldServer worldserver, BlockPosition blockposition, EntityPlayer entityplayer) {
        if (hasNearbyWarden(worldserver, blockposition)) {
            return OptionalInt.empty();
        } else {
            List<EntityPlayer> list = getNearbyPlayers(worldserver, blockposition);

            if (!list.contains(entityplayer)) {
                list.add(entityplayer);
            }

            if (list.stream().anyMatch((entityplayer1) -> {
                return (Boolean) entityplayer1.getWardenSpawnTracker().map(WardenSpawnTracker::onCooldown).orElse(false);
            })) {
                return OptionalInt.empty();
            } else {
                Optional<WardenSpawnTracker> optional = list.stream().flatMap((entityplayer1) -> {
                    return entityplayer1.getWardenSpawnTracker().stream();
                }).max(Comparator.comparingInt(WardenSpawnTracker::getWarningLevel));

                if (optional.isPresent()) {
                    WardenSpawnTracker wardenspawntracker = (WardenSpawnTracker) optional.get();

                    wardenspawntracker.increaseWarningLevel();
                    list.forEach((entityplayer1) -> {
                        entityplayer1.getWardenSpawnTracker().ifPresent((wardenspawntracker1) -> {
                            wardenspawntracker1.copyData(wardenspawntracker);
                        });
                    });
                    return OptionalInt.of(wardenspawntracker.warningLevel);
                } else {
                    return OptionalInt.empty();
                }
            }
        }
    }

    private boolean onCooldown() {
        return this.cooldownTicks > 0;
    }

    private static boolean hasNearbyWarden(WorldServer worldserver, BlockPosition blockposition) {
        AxisAlignedBB axisalignedbb = AxisAlignedBB.ofSize(Vec3D.atCenterOf(blockposition), 48.0D, 48.0D, 48.0D);

        return !worldserver.getEntitiesOfClass(Warden.class, axisalignedbb).isEmpty();
    }

    private static List<EntityPlayer> getNearbyPlayers(WorldServer worldserver, BlockPosition blockposition) {
        Vec3D vec3d = Vec3D.atCenterOf(blockposition);
        Predicate<EntityPlayer> predicate = (entityplayer) -> {
            return entityplayer.position().closerThan(vec3d, 16.0D);
        };

        return worldserver.getPlayers(predicate.and(EntityLiving::isAlive).and(IEntitySelector.NO_SPECTATORS));
    }

    private void increaseWarningLevel() {
        if (!this.onCooldown()) {
            this.ticksSinceLastWarning = 0;
            this.cooldownTicks = 200;
            this.setWarningLevel(this.getWarningLevel() + 1);
        }

    }

    private void decreaseWarningLevel() {
        this.setWarningLevel(this.getWarningLevel() - 1);
    }

    public void setWarningLevel(int i) {
        this.warningLevel = MathHelper.clamp(i, 0, 4);
    }

    public int getWarningLevel() {
        return this.warningLevel;
    }

    private void copyData(WardenSpawnTracker wardenspawntracker) {
        this.warningLevel = wardenspawntracker.warningLevel;
        this.cooldownTicks = wardenspawntracker.cooldownTicks;
        this.ticksSinceLastWarning = wardenspawntracker.ticksSinceLastWarning;
    }
}
