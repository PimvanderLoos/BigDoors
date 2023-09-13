package net.minecraft.world.entity.monster.piglin;

import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.item.Items;

public class BehaviorStopAdmiring {

    public BehaviorStopAdmiring() {}

    public static BehaviorControl<EntityPiglin> create() {
        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.absent(MemoryModuleType.ADMIRING_ITEM)).apply(behaviorbuilder_b, (memoryaccessor) -> {
                return (worldserver, entitypiglin, i) -> {
                    if (!entitypiglin.getOffhandItem().isEmpty() && !entitypiglin.getOffhandItem().is(Items.SHIELD)) {
                        PiglinAI.stopHoldingOffHandItem(entitypiglin, true);
                        return true;
                    } else {
                        return false;
                    }
                };
            });
        });
    }
}
