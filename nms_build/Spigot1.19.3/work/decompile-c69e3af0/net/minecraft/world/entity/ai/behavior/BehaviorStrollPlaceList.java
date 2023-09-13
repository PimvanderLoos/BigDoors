package net.minecraft.world.entity.ai.behavior;

import java.util.List;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryTarget;
import net.minecraft.world.entity.npc.EntityVillager;
import org.apache.commons.lang3.mutable.MutableLong;

public class BehaviorStrollPlaceList {

    public BehaviorStrollPlaceList() {}

    public static BehaviorControl<EntityVillager> create(MemoryModuleType<List<GlobalPos>> memorymoduletype, float f, int i, int j, MemoryModuleType<GlobalPos> memorymoduletype1) {
        MutableLong mutablelong = new MutableLong(0L);

        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.registered(MemoryModuleType.WALK_TARGET), behaviorbuilder_b.present(memorymoduletype), behaviorbuilder_b.present(memorymoduletype1)).apply(behaviorbuilder_b, (memoryaccessor, memoryaccessor1, memoryaccessor2) -> {
                return (worldserver, entityvillager, k) -> {
                    List<GlobalPos> list = (List) behaviorbuilder_b.get(memoryaccessor1);
                    GlobalPos globalpos = (GlobalPos) behaviorbuilder_b.get(memoryaccessor2);

                    if (list.isEmpty()) {
                        return false;
                    } else {
                        GlobalPos globalpos1 = (GlobalPos) list.get(worldserver.getRandom().nextInt(list.size()));

                        if (globalpos1 != null && worldserver.dimension() == globalpos1.dimension() && globalpos.pos().closerToCenterThan(entityvillager.position(), (double) j)) {
                            if (k > mutablelong.getValue()) {
                                memoryaccessor.set(new MemoryTarget(globalpos1.pos(), f, i));
                                mutablelong.setValue(k + 100L);
                            }

                            return true;
                        } else {
                            return false;
                        }
                    }
                };
            });
        });
    }
}
