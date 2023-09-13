package net.minecraft.world.entity.ai.behavior;

import java.util.function.BiPredicate;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.GameRules;

public class BehaviorCelebrateDeath {

    public BehaviorCelebrateDeath() {}

    public static BehaviorControl<EntityLiving> create(int i, BiPredicate<EntityLiving, EntityLiving> bipredicate) {
        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.present(MemoryModuleType.ATTACK_TARGET), behaviorbuilder_b.registered(MemoryModuleType.ANGRY_AT), behaviorbuilder_b.absent(MemoryModuleType.CELEBRATE_LOCATION), behaviorbuilder_b.registered(MemoryModuleType.DANCING)).apply(behaviorbuilder_b, (memoryaccessor, memoryaccessor1, memoryaccessor2, memoryaccessor3) -> {
                return (worldserver, entityliving, j) -> {
                    EntityLiving entityliving1 = (EntityLiving) behaviorbuilder_b.get(memoryaccessor);

                    if (!entityliving1.isDeadOrDying()) {
                        return false;
                    } else {
                        if (bipredicate.test(entityliving, entityliving1)) {
                            memoryaccessor3.setWithExpiry(true, (long) i);
                        }

                        memoryaccessor2.setWithExpiry(entityliving1.blockPosition(), (long) i);
                        if (entityliving1.getType() != EntityTypes.PLAYER || worldserver.getGameRules().getBoolean(GameRules.RULE_FORGIVE_DEAD_PLAYERS)) {
                            memoryaccessor.erase();
                            memoryaccessor1.erase();
                        }

                        return true;
                    }
                };
            });
        });
    }
}
