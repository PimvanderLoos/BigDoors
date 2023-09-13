package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.SectionPosition;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryTarget;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3D;

public class BehaviorStrollRandom {

    private static final int MAX_XZ_DIST = 10;
    private static final int MAX_Y_DIST = 7;

    public BehaviorStrollRandom() {}

    public static OneShot<EntityCreature> create(float f) {
        return create(f, 10, 7);
    }

    public static OneShot<EntityCreature> create(float f, int i, int j) {
        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.absent(MemoryModuleType.WALK_TARGET)).apply(behaviorbuilder_b, (memoryaccessor) -> {
                return (worldserver, entitycreature, k) -> {
                    BlockPosition blockposition = entitycreature.blockPosition();
                    Vec3D vec3d;

                    if (worldserver.isVillage(blockposition)) {
                        vec3d = LandRandomPos.getPos(entitycreature, i, j);
                    } else {
                        SectionPosition sectionposition = SectionPosition.of(blockposition);
                        SectionPosition sectionposition1 = BehaviorUtil.findSectionClosestToVillage(worldserver, sectionposition, 2);

                        if (sectionposition1 != sectionposition) {
                            vec3d = DefaultRandomPos.getPosTowards(entitycreature, i, j, Vec3D.atBottomCenterOf(sectionposition1.center()), 1.5707963705062866D);
                        } else {
                            vec3d = LandRandomPos.getPos(entitycreature, i, j);
                        }
                    }

                    memoryaccessor.setOrErase(Optional.ofNullable(vec3d).map((vec3d1) -> {
                        return new MemoryTarget(vec3d1, f, 0);
                    }));
                    return true;
                };
            });
        });
    }
}
