package net.minecraft.world.entity.monster.piglin;

import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class BehaviorRememberHuntedHoglin {

    public BehaviorRememberHuntedHoglin() {}

    public static BehaviorControl<EntityLiving> create() {
        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.present(MemoryModuleType.ATTACK_TARGET), behaviorbuilder_b.registered(MemoryModuleType.HUNTED_RECENTLY)).apply(behaviorbuilder_b, (memoryaccessor, memoryaccessor1) -> {
                return (worldserver, entityliving, i) -> {
                    EntityLiving entityliving1 = (EntityLiving) behaviorbuilder_b.get(memoryaccessor);

                    if (entityliving1.getType() == EntityTypes.HOGLIN && entityliving1.isDeadOrDying()) {
                        memoryaccessor1.setWithExpiry(true, (long) PiglinAI.TIME_BETWEEN_HUNTS.sample(entityliving.level.random));
                    }

                    return true;
                };
            });
        });
    }
}
