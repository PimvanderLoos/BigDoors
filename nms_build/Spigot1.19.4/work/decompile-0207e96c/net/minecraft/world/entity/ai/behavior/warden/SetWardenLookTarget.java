package net.minecraft.world.entity.ai.behavior.warden;

import java.util.Optional;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.BehaviorTarget;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class SetWardenLookTarget {

    public SetWardenLookTarget() {}

    public static BehaviorControl<EntityLiving> create() {
        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.registered(MemoryModuleType.LOOK_TARGET), behaviorbuilder_b.registered(MemoryModuleType.DISTURBANCE_LOCATION), behaviorbuilder_b.registered(MemoryModuleType.ROAR_TARGET), behaviorbuilder_b.absent(MemoryModuleType.ATTACK_TARGET)).apply(behaviorbuilder_b, (memoryaccessor, memoryaccessor1, memoryaccessor2, memoryaccessor3) -> {
                return (worldserver, entityliving, i) -> {
                    Optional<BlockPosition> optional = behaviorbuilder_b.tryGet(memoryaccessor2).map(Entity::blockPosition).or(() -> {
                        return behaviorbuilder_b.tryGet(memoryaccessor1);
                    });

                    if (optional.isEmpty()) {
                        return false;
                    } else {
                        memoryaccessor.set(new BehaviorTarget((BlockPosition) optional.get()));
                        return true;
                    }
                };
            });
        });
    }
}
