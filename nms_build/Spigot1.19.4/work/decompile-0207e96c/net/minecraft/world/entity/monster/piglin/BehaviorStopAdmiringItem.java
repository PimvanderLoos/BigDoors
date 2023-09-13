package net.minecraft.world.entity.monster.piglin;

import java.util.Optional;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.item.EntityItem;

public class BehaviorStopAdmiringItem<E extends EntityPiglin> {

    public BehaviorStopAdmiringItem() {}

    public static BehaviorControl<EntityLiving> create(int i) {
        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.present(MemoryModuleType.ADMIRING_ITEM), behaviorbuilder_b.registered(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM)).apply(behaviorbuilder_b, (memoryaccessor, memoryaccessor1) -> {
                return (worldserver, entityliving, j) -> {
                    if (!entityliving.getOffhandItem().isEmpty()) {
                        return false;
                    } else {
                        Optional<EntityItem> optional = behaviorbuilder_b.tryGet(memoryaccessor1);

                        if (optional.isPresent() && ((EntityItem) optional.get()).closerThan(entityliving, (double) i)) {
                            return false;
                        } else {
                            memoryaccessor.erase();
                            return true;
                        }
                    }
                };
            });
        });
    }
}
