package net.minecraft.world.entity.ai.behavior;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.SectionPosition;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryTarget;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.ai.village.poi.VillagePlace;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.phys.Vec3D;

public class BehaviorNearestVillage {

    public BehaviorNearestVillage() {}

    public static BehaviorControl<EntityVillager> create(float f, int i) {
        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.absent(MemoryModuleType.WALK_TARGET)).apply(behaviorbuilder_b, (memoryaccessor) -> {
                return (worldserver, entityvillager, j) -> {
                    if (worldserver.isVillage(entityvillager.blockPosition())) {
                        return false;
                    } else {
                        VillagePlace villageplace = worldserver.getPoiManager();
                        int k = villageplace.sectionsToVillage(SectionPosition.of(entityvillager.blockPosition()));
                        Vec3D vec3d = null;

                        for (int l = 0; l < 5; ++l) {
                            Vec3D vec3d1 = LandRandomPos.getPos(entityvillager, 15, 7, (blockposition) -> {
                                return (double) (-villageplace.sectionsToVillage(SectionPosition.of(blockposition)));
                            });

                            if (vec3d1 != null) {
                                int i1 = villageplace.sectionsToVillage(SectionPosition.of(BlockPosition.containing(vec3d1)));

                                if (i1 < k) {
                                    vec3d = vec3d1;
                                    break;
                                }

                                if (i1 == k) {
                                    vec3d = vec3d1;
                                }
                            }
                        }

                        if (vec3d != null) {
                            memoryaccessor.set(new MemoryTarget(vec3d, f, i));
                        }

                        return true;
                    }
                };
            });
        });
    }
}
