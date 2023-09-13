package net.minecraft.world.entity.monster.piglin;

import java.util.Optional;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class BehaviorAdmireTimeout {

    public BehaviorAdmireTimeout() {}

    public static BehaviorControl<EntityLiving> create(int i, int j) {
        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.present(MemoryModuleType.ADMIRING_ITEM), behaviorbuilder_b.present(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM), behaviorbuilder_b.registered(MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM), behaviorbuilder_b.registered(MemoryModuleType.DISABLE_WALK_TO_ADMIRE_ITEM)).apply(behaviorbuilder_b, (memoryaccessor, memoryaccessor1, memoryaccessor2, memoryaccessor3) -> {
                return (worldserver, entityliving, k) -> {
                    if (!entityliving.getOffhandItem().isEmpty()) {
                        return false;
                    } else {
                        Optional<Integer> optional = behaviorbuilder_b.tryGet(memoryaccessor2);

                        if (optional.isEmpty()) {
                            memoryaccessor2.set(0);
                        } else {
                            int l = (Integer) optional.get();

                            if (l > i) {
                                memoryaccessor.erase();
                                memoryaccessor2.erase();
                                memoryaccessor3.setWithExpiry(true, (long) j);
                            } else {
                                memoryaccessor2.set(l + 1);
                            }
                        }

                        return true;
                    }
                };
            });
        });
    }
}
