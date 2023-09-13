package net.minecraft.world.entity.ai.behavior;

import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryTarget;
import org.apache.commons.lang3.mutable.MutableLong;

public class BehaviorStrollPlace {

    public BehaviorStrollPlace() {}

    public static BehaviorControl<EntityCreature> create(MemoryModuleType<GlobalPos> memorymoduletype, float f, int i, int j) {
        MutableLong mutablelong = new MutableLong(0L);

        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.registered(MemoryModuleType.WALK_TARGET), behaviorbuilder_b.present(memorymoduletype)).apply(behaviorbuilder_b, (memoryaccessor, memoryaccessor1) -> {
                return (worldserver, entitycreature, k) -> {
                    GlobalPos globalpos = (GlobalPos) behaviorbuilder_b.get(memoryaccessor1);

                    if (worldserver.dimension() == globalpos.dimension() && globalpos.pos().closerToCenterThan(entitycreature.position(), (double) j)) {
                        if (k <= mutablelong.getValue()) {
                            return true;
                        } else {
                            memoryaccessor.set(new MemoryTarget(globalpos.pos(), f, i));
                            mutablelong.setValue(k + 80L);
                            return true;
                        }
                    } else {
                        return false;
                    }
                };
            });
        });
    }
}
