package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class BehaviorPacify {

    public BehaviorPacify() {}

    public static BehaviorControl<EntityLiving> create(MemoryModuleType<?> memorymoduletype, int i) {
        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.registered(MemoryModuleType.ATTACK_TARGET), behaviorbuilder_b.absent(MemoryModuleType.PACIFIED), behaviorbuilder_b.present(memorymoduletype)).apply(behaviorbuilder_b, behaviorbuilder_b.point(() -> {
                return "[BecomePassive if " + memorymoduletype + " present]";
            }, (memoryaccessor, memoryaccessor1, memoryaccessor2) -> {
                return (worldserver, entityliving, j) -> {
                    memoryaccessor1.setWithExpiry(true, (long) i);
                    memoryaccessor.erase();
                    return true;
                };
            }));
        });
    }
}
