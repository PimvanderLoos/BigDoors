package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class BehaviorStartRiding<E extends EntityLiving> extends Behavior<E> {

    private static final int CLOSE_ENOUGH_TO_START_RIDING_DIST = 1;
    private final float speedModifier;

    public BehaviorStartRiding(float f) {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.RIDE_TARGET, MemoryStatus.VALUE_PRESENT));
        this.speedModifier = f;
    }

    @Override
    protected boolean checkExtraStartConditions(WorldServer worldserver, E e0) {
        return !e0.isPassenger();
    }

    @Override
    protected void start(WorldServer worldserver, E e0, long i) {
        if (this.isCloseEnoughToStartRiding(e0)) {
            e0.startRiding(this.getRidableEntity(e0));
        } else {
            BehaviorUtil.setWalkAndLookTargetMemories(e0, this.getRidableEntity(e0), this.speedModifier, 1);
        }

    }

    private boolean isCloseEnoughToStartRiding(E e0) {
        return this.getRidableEntity(e0).closerThan(e0, 1.0D);
    }

    private Entity getRidableEntity(E e0) {
        return (Entity) e0.getBrain().getMemory(MemoryModuleType.RIDE_TARGET).get();
    }
}
