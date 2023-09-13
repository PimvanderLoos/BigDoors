package net.minecraft.world.entity.ai.behavior;

import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryTarget;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.ai.village.poi.VillagePlace;

public class BehaviorHome {

    public BehaviorHome() {}

    public static OneShot<EntityLiving> create(int i, float f, int j) {
        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.absent(MemoryModuleType.WALK_TARGET), behaviorbuilder_b.registered(MemoryModuleType.HOME), behaviorbuilder_b.registered(MemoryModuleType.HIDING_PLACE), behaviorbuilder_b.registered(MemoryModuleType.PATH), behaviorbuilder_b.registered(MemoryModuleType.LOOK_TARGET), behaviorbuilder_b.registered(MemoryModuleType.BREED_TARGET), behaviorbuilder_b.registered(MemoryModuleType.INTERACTION_TARGET)).apply(behaviorbuilder_b, (memoryaccessor, memoryaccessor1, memoryaccessor2, memoryaccessor3, memoryaccessor4, memoryaccessor5, memoryaccessor6) -> {
                return (worldserver, entityliving, k) -> {
                    worldserver.getPoiManager().find((holder) -> {
                        return holder.is(PoiTypes.HOME);
                    }, (blockposition) -> {
                        return true;
                    }, entityliving.blockPosition(), j + 1, VillagePlace.Occupancy.ANY).filter((blockposition) -> {
                        return blockposition.closerToCenterThan(entityliving.position(), (double) j);
                    }).or(() -> {
                        return worldserver.getPoiManager().getRandom((holder) -> {
                            return holder.is(PoiTypes.HOME);
                        }, (blockposition) -> {
                            return true;
                        }, VillagePlace.Occupancy.ANY, entityliving.blockPosition(), i, entityliving.getRandom());
                    }).or(() -> {
                        return behaviorbuilder_b.tryGet(memoryaccessor1).map(GlobalPos::pos);
                    }).ifPresent((blockposition) -> {
                        memoryaccessor3.erase();
                        memoryaccessor4.erase();
                        memoryaccessor5.erase();
                        memoryaccessor6.erase();
                        memoryaccessor2.set(GlobalPos.of(worldserver.dimension(), blockposition));
                        if (!blockposition.closerToCenterThan(entityliving.position(), (double) j)) {
                            memoryaccessor.set(new MemoryTarget(blockposition, f, j));
                        }

                    });
                    return true;
                };
            });
        });
    }
}
