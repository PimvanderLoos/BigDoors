package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryTarget;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3D;

public class BehaviorWalkAway {

    public BehaviorWalkAway() {}

    public static BehaviorControl<EntityCreature> pos(MemoryModuleType<BlockPosition> memorymoduletype, float f, int i, boolean flag) {
        return create(memorymoduletype, f, i, flag, Vec3D::atBottomCenterOf);
    }

    public static OneShot<EntityCreature> entity(MemoryModuleType<? extends Entity> memorymoduletype, float f, int i, boolean flag) {
        return create(memorymoduletype, f, i, flag, Entity::position);
    }

    private static <T> OneShot<EntityCreature> create(MemoryModuleType<T> memorymoduletype, float f, int i, boolean flag, Function<T, Vec3D> function) {
        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.registered(MemoryModuleType.WALK_TARGET), behaviorbuilder_b.present(memorymoduletype)).apply(behaviorbuilder_b, (memoryaccessor, memoryaccessor1) -> {
                return (worldserver, entitycreature, j) -> {
                    Optional<MemoryTarget> optional = behaviorbuilder_b.tryGet(memoryaccessor);

                    if (optional.isPresent() && !flag) {
                        return false;
                    } else {
                        Vec3D vec3d = entitycreature.position();
                        Vec3D vec3d1 = (Vec3D) function.apply(behaviorbuilder_b.get(memoryaccessor1));

                        if (!vec3d.closerThan(vec3d1, (double) i)) {
                            return false;
                        } else {
                            Vec3D vec3d2;

                            if (optional.isPresent() && ((MemoryTarget) optional.get()).getSpeedModifier() == f) {
                                Vec3D vec3d3 = ((MemoryTarget) optional.get()).getTarget().currentPosition().subtract(vec3d);

                                vec3d2 = vec3d1.subtract(vec3d);
                                if (vec3d3.dot(vec3d2) < 0.0D) {
                                    return false;
                                }
                            }

                            for (int k = 0; k < 10; ++k) {
                                vec3d2 = LandRandomPos.getPosAway(entitycreature, 16, 7, vec3d1);
                                if (vec3d2 != null) {
                                    memoryaccessor.set(new MemoryTarget(vec3d2, f, 0));
                                    break;
                                }
                            }

                            return true;
                        }
                    }
                };
            });
        });
    }
}
