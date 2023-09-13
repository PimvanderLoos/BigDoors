package net.minecraft.world.entity.monster.piglin;

import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.item.EntityItem;

public class BehaviorStartAdmiringItem {

    public BehaviorStartAdmiringItem() {}

    public static BehaviorControl<EntityLiving> create(int i) {
        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.present(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM), behaviorbuilder_b.absent(MemoryModuleType.ADMIRING_ITEM), behaviorbuilder_b.absent(MemoryModuleType.ADMIRING_DISABLED), behaviorbuilder_b.absent(MemoryModuleType.DISABLE_WALK_TO_ADMIRE_ITEM)).apply(behaviorbuilder_b, (memoryaccessor, memoryaccessor1, memoryaccessor2, memoryaccessor3) -> {
                return (worldserver, entityliving, j) -> {
                    EntityItem entityitem = (EntityItem) behaviorbuilder_b.get(memoryaccessor);

                    if (!PiglinAI.isLovedItem(entityitem.getItem())) {
                        return false;
                    } else {
                        memoryaccessor1.setWithExpiry(true, (long) i);
                        return true;
                    }
                };
            });
        });
    }
}
