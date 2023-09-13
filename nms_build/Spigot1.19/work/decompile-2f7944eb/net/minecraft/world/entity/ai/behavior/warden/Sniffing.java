package net.minecraft.world.entity.ai.behavior.warden;

import com.google.common.collect.ImmutableMap;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.monster.warden.WardenAi;

public class Sniffing<E extends Warden> extends Behavior<E> {

    private static final double ANGER_FROM_SNIFFING_MAX_DISTANCE_XZ = 6.0D;
    private static final double ANGER_FROM_SNIFFING_MAX_DISTANCE_Y = 20.0D;

    public Sniffing(int i) {
        super(ImmutableMap.of(MemoryModuleType.IS_SNIFFING, MemoryStatus.VALUE_PRESENT, MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.NEAREST_ATTACKABLE, MemoryStatus.REGISTERED, MemoryModuleType.DISTURBANCE_LOCATION, MemoryStatus.REGISTERED, MemoryModuleType.SNIFF_COOLDOWN, MemoryStatus.REGISTERED), i);
    }

    protected boolean canStillUse(WorldServer worldserver, E e0, long i) {
        return true;
    }

    protected void start(WorldServer worldserver, E e0, long i) {
        e0.playSound(SoundEffects.WARDEN_SNIFF, 5.0F, 1.0F);
    }

    protected void stop(WorldServer worldserver, E e0, long i) {
        if (e0.hasPose(EntityPose.SNIFFING)) {
            e0.setPose(EntityPose.STANDING);
        }

        e0.getBrain().eraseMemory(MemoryModuleType.IS_SNIFFING);
        Optional optional = e0.getBrain().getMemory(MemoryModuleType.NEAREST_ATTACKABLE);

        Objects.requireNonNull(e0);
        optional.filter(e0::canTargetEntity).ifPresent((entityliving) -> {
            if (e0.closerThan(entityliving, 6.0D, 20.0D)) {
                e0.increaseAngerAt(entityliving);
            }

            if (!e0.getBrain().hasMemoryValue(MemoryModuleType.DISTURBANCE_LOCATION)) {
                WardenAi.setDisturbanceLocation(e0, entityliving.blockPosition());
            }

        });
    }
}
