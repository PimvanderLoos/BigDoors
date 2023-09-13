package net.minecraft.world.entity.ai.behavior.warden;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.warden.Warden;

public class SetRoarTarget {

    public SetRoarTarget() {}

    public static <E extends Warden> BehaviorControl<E> create(Function<E, Optional<? extends EntityLiving>> function) {
        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.absent(MemoryModuleType.ROAR_TARGET), behaviorbuilder_b.absent(MemoryModuleType.ATTACK_TARGET), behaviorbuilder_b.registered(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)).apply(behaviorbuilder_b, (memoryaccessor, memoryaccessor1, memoryaccessor2) -> {
                return (worldserver, warden, i) -> {
                    Optional<? extends EntityLiving> optional = (Optional) function.apply(warden);

                    Objects.requireNonNull(warden);
                    if (optional.filter(warden::canTargetEntity).isEmpty()) {
                        return false;
                    } else {
                        memoryaccessor.set((EntityLiving) optional.get());
                        memoryaccessor2.erase();
                        return true;
                    }
                };
            });
        });
    }
}
