package net.minecraft.world.entity.ai.goal;

import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.SectionPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.ai.village.poi.VillagePlace;
import net.minecraft.world.entity.ai.village.poi.VillagePlaceRecord;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.Vec3D;

public class PathfinderGoalStrollVillageGolem extends PathfinderGoalRandomStroll {

    private static final int POI_SECTION_SCAN_RADIUS = 2;
    private static final int VILLAGER_SCAN_RADIUS = 32;
    private static final int RANDOM_POS_XY_DISTANCE = 10;
    private static final int RANDOM_POS_Y_DISTANCE = 7;

    public PathfinderGoalStrollVillageGolem(EntityCreature entitycreature, double d0) {
        super(entitycreature, d0, 240, false);
    }

    @Nullable
    @Override
    protected Vec3D getPosition() {
        float f = this.mob.level.random.nextFloat();

        if (this.mob.level.random.nextFloat() < 0.3F) {
            return this.getPositionTowardsAnywhere();
        } else {
            Vec3D vec3d;

            if (f < 0.7F) {
                vec3d = this.getPositionTowardsVillagerWhoWantsGolem();
                if (vec3d == null) {
                    vec3d = this.getPositionTowardsPoi();
                }
            } else {
                vec3d = this.getPositionTowardsPoi();
                if (vec3d == null) {
                    vec3d = this.getPositionTowardsVillagerWhoWantsGolem();
                }
            }

            return vec3d == null ? this.getPositionTowardsAnywhere() : vec3d;
        }
    }

    @Nullable
    private Vec3D getPositionTowardsAnywhere() {
        return LandRandomPos.getPos(this.mob, 10, 7);
    }

    @Nullable
    private Vec3D getPositionTowardsVillagerWhoWantsGolem() {
        WorldServer worldserver = (WorldServer) this.mob.level;
        List<EntityVillager> list = worldserver.getEntities((EntityTypeTest) EntityTypes.VILLAGER, this.mob.getBoundingBox().inflate(32.0D), this::doesVillagerWantGolem);

        if (list.isEmpty()) {
            return null;
        } else {
            EntityVillager entityvillager = (EntityVillager) list.get(this.mob.level.random.nextInt(list.size()));
            Vec3D vec3d = entityvillager.position();

            return LandRandomPos.getPosTowards(this.mob, 10, 7, vec3d);
        }
    }

    @Nullable
    private Vec3D getPositionTowardsPoi() {
        SectionPosition sectionposition = this.getRandomVillageSection();

        if (sectionposition == null) {
            return null;
        } else {
            BlockPosition blockposition = this.getRandomPoiWithinSection(sectionposition);

            return blockposition == null ? null : LandRandomPos.getPosTowards(this.mob, 10, 7, Vec3D.atBottomCenterOf(blockposition));
        }
    }

    @Nullable
    private SectionPosition getRandomVillageSection() {
        WorldServer worldserver = (WorldServer) this.mob.level;
        List<SectionPosition> list = (List) SectionPosition.cube(SectionPosition.of((Entity) this.mob), 2).filter((sectionposition) -> {
            return worldserver.sectionsToVillage(sectionposition) == 0;
        }).collect(Collectors.toList());

        return list.isEmpty() ? null : (SectionPosition) list.get(worldserver.random.nextInt(list.size()));
    }

    @Nullable
    private BlockPosition getRandomPoiWithinSection(SectionPosition sectionposition) {
        WorldServer worldserver = (WorldServer) this.mob.level;
        VillagePlace villageplace = worldserver.getPoiManager();
        List<BlockPosition> list = (List) villageplace.getInRange((villageplacetype) -> {
            return true;
        }, sectionposition.center(), 8, VillagePlace.Occupancy.IS_OCCUPIED).map(VillagePlaceRecord::getPos).collect(Collectors.toList());

        return list.isEmpty() ? null : (BlockPosition) list.get(worldserver.random.nextInt(list.size()));
    }

    private boolean doesVillagerWantGolem(EntityVillager entityvillager) {
        return entityvillager.wantsToSpawnGolem(this.mob.level.getGameTime());
    }
}
