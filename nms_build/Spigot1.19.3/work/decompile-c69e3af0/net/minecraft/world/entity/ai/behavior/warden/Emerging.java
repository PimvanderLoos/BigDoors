package net.minecraft.world.entity.ai.behavior.warden;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.monster.warden.Warden;

public class Emerging<E extends Warden> extends Behavior<E> {

    public Emerging(int i) {
        super(ImmutableMap.of(MemoryModuleType.IS_EMERGING, MemoryStatus.VALUE_PRESENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED), i);
    }

    protected boolean canStillUse(WorldServer worldserver, E e0, long i) {
        return true;
    }

    protected void start(WorldServer worldserver, E e0, long i) {
        e0.setPose(EntityPose.EMERGING);
        e0.playSound(SoundEffects.WARDEN_EMERGE, 5.0F, 1.0F);
    }

    protected void stop(WorldServer worldserver, E e0, long i) {
        if (e0.hasPose(EntityPose.EMERGING)) {
            e0.setPose(EntityPose.STANDING);
        }

    }
}
