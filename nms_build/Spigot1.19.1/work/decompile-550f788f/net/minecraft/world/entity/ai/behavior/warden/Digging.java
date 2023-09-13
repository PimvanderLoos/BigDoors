package net.minecraft.world.entity.ai.behavior.warden;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.monster.warden.Warden;

public class Digging<E extends Warden> extends Behavior<E> {

    public Digging(int i) {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT), i);
    }

    protected boolean canStillUse(WorldServer worldserver, E e0, long i) {
        return e0.getRemovalReason() == null;
    }

    protected boolean checkExtraStartConditions(WorldServer worldserver, E e0) {
        return e0.isOnGround() || e0.isInWater() || e0.isInLava();
    }

    protected void start(WorldServer worldserver, E e0, long i) {
        if (e0.isOnGround()) {
            e0.setPose(EntityPose.DIGGING);
            e0.playSound(SoundEffects.WARDEN_DIG, 5.0F, 1.0F);
        } else {
            e0.playSound(SoundEffects.WARDEN_AGITATED, 5.0F, 1.0F);
            this.stop(worldserver, e0, i);
        }

    }

    protected void stop(WorldServer worldserver, E e0, long i) {
        if (e0.getRemovalReason() == null) {
            e0.remove(Entity.RemovalReason.DISCARDED);
        }

    }
}
