package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.SectionPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryTarget;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.ai.village.poi.VillagePlace;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.phys.Vec3D;

public class BehaviorNearestVillage extends Behavior<EntityVillager> {

    private final float speedModifier;
    private final int closeEnoughDistance;

    public BehaviorNearestVillage(float f, int i) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));
        this.speedModifier = f;
        this.closeEnoughDistance = i;
    }

    protected boolean checkExtraStartConditions(WorldServer worldserver, EntityVillager entityvillager) {
        return !worldserver.isVillage(entityvillager.blockPosition());
    }

    protected void start(WorldServer worldserver, EntityVillager entityvillager, long i) {
        VillagePlace villageplace = worldserver.getPoiManager();
        int j = villageplace.sectionsToVillage(SectionPosition.of(entityvillager.blockPosition()));
        Vec3D vec3d = null;

        for (int k = 0; k < 5; ++k) {
            Vec3D vec3d1 = LandRandomPos.getPos(entityvillager, 15, 7, (blockposition) -> {
                return (double) (-villageplace.sectionsToVillage(SectionPosition.of(blockposition)));
            });

            if (vec3d1 != null) {
                int l = villageplace.sectionsToVillage(SectionPosition.of(new BlockPosition(vec3d1)));

                if (l < j) {
                    vec3d = vec3d1;
                    break;
                }

                if (l == j) {
                    vec3d = vec3d1;
                }
            }
        }

        if (vec3d != null) {
            entityvillager.getBrain().setMemory(MemoryModuleType.WALK_TARGET, (Object) (new MemoryTarget(vec3d, this.speedModifier, this.closeEnoughDistance)));
        }

    }
}
