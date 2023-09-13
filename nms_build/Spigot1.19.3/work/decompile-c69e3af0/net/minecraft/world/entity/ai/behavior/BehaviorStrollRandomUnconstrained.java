package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryTarget;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3D;

public class BehaviorStrollRandomUnconstrained {

    private static final int MAX_XZ_DIST = 10;
    private static final int MAX_Y_DIST = 7;
    private static final int[][] SWIM_XY_DISTANCE_TIERS = new int[][]{{1, 1}, {3, 3}, {5, 5}, {6, 5}, {7, 7}, {10, 7}};

    public BehaviorStrollRandomUnconstrained() {}

    public static OneShot<EntityCreature> stroll(float f) {
        return stroll(f, true);
    }

    public static OneShot<EntityCreature> stroll(float f, boolean flag) {
        return strollFlyOrSwim(f, (entitycreature) -> {
            return LandRandomPos.getPos(entitycreature, 10, 7);
        }, flag ? (entitycreature) -> {
            return true;
        } : (entitycreature) -> {
            return !entitycreature.isInWaterOrBubble();
        });
    }

    public static BehaviorControl<EntityCreature> stroll(float f, int i, int j) {
        return strollFlyOrSwim(f, (entitycreature) -> {
            return LandRandomPos.getPos(entitycreature, i, j);
        }, (entitycreature) -> {
            return true;
        });
    }

    public static BehaviorControl<EntityCreature> fly(float f) {
        return strollFlyOrSwim(f, (entitycreature) -> {
            return getTargetFlyPos(entitycreature, 10, 7);
        }, (entitycreature) -> {
            return true;
        });
    }

    public static BehaviorControl<EntityCreature> swim(float f) {
        return strollFlyOrSwim(f, BehaviorStrollRandomUnconstrained::getTargetSwimPos, Entity::isInWaterOrBubble);
    }

    private static OneShot<EntityCreature> strollFlyOrSwim(float f, Function<EntityCreature, Vec3D> function, Predicate<EntityCreature> predicate) {
        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.absent(MemoryModuleType.WALK_TARGET)).apply(behaviorbuilder_b, (memoryaccessor) -> {
                return (worldserver, entitycreature, i) -> {
                    if (!predicate.test(entitycreature)) {
                        return false;
                    } else {
                        Optional<Vec3D> optional = Optional.ofNullable((Vec3D) function.apply(entitycreature));

                        memoryaccessor.setOrErase(optional.map((vec3d) -> {
                            return new MemoryTarget(vec3d, f, 0);
                        }));
                        return true;
                    }
                };
            });
        });
    }

    @Nullable
    private static Vec3D getTargetSwimPos(EntityCreature entitycreature) {
        Vec3D vec3d = null;
        Vec3D vec3d1 = null;
        int[][] aint = BehaviorStrollRandomUnconstrained.SWIM_XY_DISTANCE_TIERS;
        int i = aint.length;

        for (int j = 0; j < i; ++j) {
            int[] aint1 = aint[j];

            if (vec3d == null) {
                vec3d1 = BehaviorUtil.getRandomSwimmablePos(entitycreature, aint1[0], aint1[1]);
            } else {
                vec3d1 = entitycreature.position().add(entitycreature.position().vectorTo(vec3d).normalize().multiply((double) aint1[0], (double) aint1[1], (double) aint1[0]));
            }

            if (vec3d1 == null || entitycreature.level.getFluidState(new BlockPosition(vec3d1)).isEmpty()) {
                return vec3d;
            }

            vec3d = vec3d1;
        }

        return vec3d1;
    }

    @Nullable
    private static Vec3D getTargetFlyPos(EntityCreature entitycreature, int i, int j) {
        Vec3D vec3d = entitycreature.getViewVector(0.0F);

        return AirAndWaterRandomPos.getPos(entitycreature, i, j, -2, vec3d.x, vec3d.z, 1.5707963705062866D);
    }
}
