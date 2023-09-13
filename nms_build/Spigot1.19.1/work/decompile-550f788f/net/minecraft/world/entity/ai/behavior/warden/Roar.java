package net.minecraft.world.entity.ai.behavior.warden;

import com.google.common.collect.ImmutableMap;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtil;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.monster.warden.WardenAi;

public class Roar extends Behavior<Warden> {

    private static final int TICKS_BEFORE_PLAYING_ROAR_SOUND = 25;
    private static final int ROAR_ANGER_INCREASE = 20;

    public Roar() {
        super(ImmutableMap.of(MemoryModuleType.ROAR_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.ROAR_SOUND_COOLDOWN, MemoryStatus.REGISTERED, MemoryModuleType.ROAR_SOUND_DELAY, MemoryStatus.REGISTERED), WardenAi.ROAR_DURATION);
    }

    protected void start(WorldServer worldserver, Warden warden, long i) {
        BehaviorController<Warden> behaviorcontroller = warden.getBrain();

        behaviorcontroller.setMemoryWithExpiry(MemoryModuleType.ROAR_SOUND_DELAY, Unit.INSTANCE, 25L);
        behaviorcontroller.eraseMemory(MemoryModuleType.WALK_TARGET);
        EntityLiving entityliving = (EntityLiving) warden.getBrain().getMemory(MemoryModuleType.ROAR_TARGET).get();

        BehaviorUtil.lookAtEntity(warden, entityliving);
        warden.setPose(EntityPose.ROARING);
        warden.increaseAngerAt(entityliving, 20, false);
    }

    protected boolean canStillUse(WorldServer worldserver, Warden warden, long i) {
        return true;
    }

    protected void tick(WorldServer worldserver, Warden warden, long i) {
        if (!warden.getBrain().hasMemoryValue(MemoryModuleType.ROAR_SOUND_DELAY) && !warden.getBrain().hasMemoryValue(MemoryModuleType.ROAR_SOUND_COOLDOWN)) {
            warden.getBrain().setMemoryWithExpiry(MemoryModuleType.ROAR_SOUND_COOLDOWN, Unit.INSTANCE, (long) (WardenAi.ROAR_DURATION - 25));
            warden.playSound(SoundEffects.WARDEN_ROAR, 3.0F, 1.0F);
        }
    }

    protected void stop(WorldServer worldserver, Warden warden, long i) {
        if (warden.hasPose(EntityPose.ROARING)) {
            warden.setPose(EntityPose.STANDING);
        }

        Optional optional = warden.getBrain().getMemory(MemoryModuleType.ROAR_TARGET);

        Objects.requireNonNull(warden);
        optional.ifPresent(warden::setAttackTarget);
        warden.getBrain().eraseMemory(MemoryModuleType.ROAR_TARGET);
    }
}
