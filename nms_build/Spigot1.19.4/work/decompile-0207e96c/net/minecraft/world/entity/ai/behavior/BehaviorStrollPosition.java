package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryTarget;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3D;
import org.apache.commons.lang3.mutable.MutableLong;

public class BehaviorStrollPosition {

    private static final int MIN_TIME_BETWEEN_STROLLS = 180;
    private static final int STROLL_MAX_XZ_DIST = 8;
    private static final int STROLL_MAX_Y_DIST = 6;

    public BehaviorStrollPosition() {}

    public static OneShot<EntityCreature> create(MemoryModuleType<GlobalPos> memorymoduletype, float f, int i) {
        MutableLong mutablelong = new MutableLong(0L);

        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.registered(MemoryModuleType.WALK_TARGET), behaviorbuilder_b.present(memorymoduletype)).apply(behaviorbuilder_b, (memoryaccessor, memoryaccessor1) -> {
                return (worldserver, entitycreature, j) -> {
                    GlobalPos globalpos = (GlobalPos) behaviorbuilder_b.get(memoryaccessor1);

                    if (worldserver.dimension() == globalpos.dimension() && globalpos.pos().closerToCenterThan(entitycreature.position(), (double) i)) {
                        if (j <= mutablelong.getValue()) {
                            return true;
                        } else {
                            Optional<Vec3D> optional = Optional.ofNullable(LandRandomPos.getPos(entitycreature, 8, 6));

                            memoryaccessor.setOrErase(optional.map((vec3d) -> {
                                return new MemoryTarget(vec3d, f, 1);
                            }));
                            mutablelong.setValue(j + 180L);
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
