package net.minecraft.world.entity.monster.piglin;

import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.hoglin.EntityHoglin;

public class BehaviorHuntHoglin {

    public BehaviorHuntHoglin() {}

    public static OneShot<EntityPiglin> create() {
        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.present(MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN), behaviorbuilder_b.absent(MemoryModuleType.ANGRY_AT), behaviorbuilder_b.absent(MemoryModuleType.HUNTED_RECENTLY), behaviorbuilder_b.registered(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS)).apply(behaviorbuilder_b, (memoryaccessor, memoryaccessor1, memoryaccessor2, memoryaccessor3) -> {
                return (worldserver, entitypiglin, i) -> {
                    if (!entitypiglin.isBaby() && !behaviorbuilder_b.tryGet(memoryaccessor3).map((list) -> {
                        return list.stream().anyMatch(BehaviorHuntHoglin::hasHuntedRecently);
                    }).isPresent()) {
                        EntityHoglin entityhoglin = (EntityHoglin) behaviorbuilder_b.get(memoryaccessor);

                        PiglinAI.setAngerTarget(entitypiglin, entityhoglin);
                        PiglinAI.dontKillAnyMoreHoglinsForAWhile(entitypiglin);
                        PiglinAI.broadcastAngerTarget(entitypiglin, entityhoglin);
                        behaviorbuilder_b.tryGet(memoryaccessor3).ifPresent((list) -> {
                            list.forEach(PiglinAI::dontKillAnyMoreHoglinsForAWhile);
                        });
                        return true;
                    } else {
                        return false;
                    }
                };
            });
        });
    }

    private static boolean hasHuntedRecently(EntityPiglinAbstract entitypiglinabstract) {
        return entitypiglinabstract.getBrain().hasMemoryValue(MemoryModuleType.HUNTED_RECENTLY);
    }
}
