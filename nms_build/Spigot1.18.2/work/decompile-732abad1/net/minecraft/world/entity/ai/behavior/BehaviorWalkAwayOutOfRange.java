package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.function.Function;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryTarget;

public class BehaviorWalkAwayOutOfRange extends Behavior<EntityInsentient> {

    private static final int PROJECTILE_ATTACK_RANGE_BUFFER = 1;
    private final Function<EntityLiving, Float> speedModifier;

    public BehaviorWalkAwayOutOfRange(float f) {
        this((entityliving) -> {
            return f;
        });
    }

    public BehaviorWalkAwayOutOfRange(Function<EntityLiving, Float> function) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryStatus.REGISTERED));
        this.speedModifier = function;
    }

    protected void start(WorldServer worldserver, EntityInsentient entityinsentient, long i) {
        EntityLiving entityliving = (EntityLiving) entityinsentient.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();

        if (BehaviorUtil.canSee(entityinsentient, entityliving) && BehaviorUtil.isWithinAttackRange(entityinsentient, entityliving, 1)) {
            this.clearWalkTarget(entityinsentient);
        } else {
            this.setWalkAndLookTarget(entityinsentient, entityliving);
        }

    }

    private void setWalkAndLookTarget(EntityLiving entityliving, EntityLiving entityliving1) {
        BehaviorController<?> behaviorcontroller = entityliving.getBrain();

        behaviorcontroller.setMemory(MemoryModuleType.LOOK_TARGET, (Object) (new BehaviorPositionEntity(entityliving1, true)));
        MemoryTarget memorytarget = new MemoryTarget(new BehaviorPositionEntity(entityliving1, false), (Float) this.speedModifier.apply(entityliving), 0);

        behaviorcontroller.setMemory(MemoryModuleType.WALK_TARGET, (Object) memorytarget);
    }

    private void clearWalkTarget(EntityLiving entityliving) {
        entityliving.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
    }
}
