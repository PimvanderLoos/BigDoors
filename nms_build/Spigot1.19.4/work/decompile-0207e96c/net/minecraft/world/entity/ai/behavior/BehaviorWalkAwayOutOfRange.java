package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import java.util.function.Function;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryTarget;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;

public class BehaviorWalkAwayOutOfRange {

    private static final int PROJECTILE_ATTACK_RANGE_BUFFER = 1;

    public BehaviorWalkAwayOutOfRange() {}

    public static BehaviorControl<EntityInsentient> create(float f) {
        return create((entityliving) -> {
            return f;
        });
    }

    public static BehaviorControl<EntityInsentient> create(Function<EntityLiving, Float> function) {
        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.registered(MemoryModuleType.WALK_TARGET), behaviorbuilder_b.registered(MemoryModuleType.LOOK_TARGET), behaviorbuilder_b.present(MemoryModuleType.ATTACK_TARGET), behaviorbuilder_b.registered(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)).apply(behaviorbuilder_b, (memoryaccessor, memoryaccessor1, memoryaccessor2, memoryaccessor3) -> {
                return (worldserver, entityinsentient, i) -> {
                    EntityLiving entityliving = (EntityLiving) behaviorbuilder_b.get(memoryaccessor2);
                    Optional<NearestVisibleLivingEntities> optional = behaviorbuilder_b.tryGet(memoryaccessor3);

                    if (optional.isPresent() && ((NearestVisibleLivingEntities) optional.get()).contains(entityliving) && BehaviorUtil.isWithinAttackRange(entityinsentient, entityliving, 1)) {
                        memoryaccessor.erase();
                    } else {
                        memoryaccessor1.set(new BehaviorPositionEntity(entityliving, true));
                        memoryaccessor.set(new MemoryTarget(new BehaviorPositionEntity(entityliving, false), (Float) function.apply(entityinsentient), 0));
                    }

                    return true;
                };
            });
        });
    }
}
