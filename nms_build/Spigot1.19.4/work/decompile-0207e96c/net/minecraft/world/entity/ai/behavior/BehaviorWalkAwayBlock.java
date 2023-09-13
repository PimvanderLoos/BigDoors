package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryTarget;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.phys.Vec3D;

public class BehaviorWalkAwayBlock {

    public BehaviorWalkAwayBlock() {}

    public static OneShot<EntityVillager> create(MemoryModuleType<GlobalPos> memorymoduletype, float f, int i, int j, int k) {
        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.registered(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE), behaviorbuilder_b.absent(MemoryModuleType.WALK_TARGET), behaviorbuilder_b.present(memorymoduletype)).apply(behaviorbuilder_b, (memoryaccessor, memoryaccessor1, memoryaccessor2) -> {
                return (worldserver, entityvillager, l) -> {
                    GlobalPos globalpos = (GlobalPos) behaviorbuilder_b.get(memoryaccessor2);
                    Optional<Long> optional = behaviorbuilder_b.tryGet(memoryaccessor);

                    if (globalpos.dimension() == worldserver.dimension() && (!optional.isPresent() || worldserver.getGameTime() - (Long) optional.get() <= (long) k)) {
                        if (globalpos.pos().distManhattan(entityvillager.blockPosition()) > j) {
                            Vec3D vec3d = null;
                            int i1 = 0;
                            boolean flag = true;

                            while (vec3d == null || BlockPosition.containing(vec3d).distManhattan(entityvillager.blockPosition()) > j) {
                                vec3d = DefaultRandomPos.getPosTowards(entityvillager, 15, 7, Vec3D.atBottomCenterOf(globalpos.pos()), 1.5707963705062866D);
                                ++i1;
                                if (i1 == 1000) {
                                    entityvillager.releasePoi(memorymoduletype);
                                    memoryaccessor2.erase();
                                    memoryaccessor.set(l);
                                    return true;
                                }
                            }

                            memoryaccessor1.set(new MemoryTarget(vec3d, f, i));
                        } else if (globalpos.pos().distManhattan(entityvillager.blockPosition()) > i) {
                            memoryaccessor1.set(new MemoryTarget(globalpos.pos(), f, i));
                        }
                    } else {
                        entityvillager.releasePoi(memorymoduletype);
                        memoryaccessor2.erase();
                        memoryaccessor.set(l);
                    }

                    return true;
                };
            });
        });
    }
}
