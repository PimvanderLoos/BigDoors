package net.minecraft.world.entity.ai.goal;

import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BaseBlockPosition;
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
    protected Vec3D g() {
        float f = this.mob.level.random.nextFloat();

        if (this.mob.level.random.nextFloat() < 0.3F) {
            return this.j();
        } else {
            Vec3D vec3d;

            if (f < 0.7F) {
                vec3d = this.k();
                if (vec3d == null) {
                    vec3d = this.l();
                }
            } else {
                vec3d = this.l();
                if (vec3d == null) {
                    vec3d = this.k();
                }
            }

            return vec3d == null ? this.j() : vec3d;
        }
    }

    @Nullable
    private Vec3D j() {
        return LandRandomPos.a(this.mob, 10, 7);
    }

    @Nullable
    private Vec3D k() {
        WorldServer worldserver = (WorldServer) this.mob.level;
        List<EntityVillager> list = worldserver.a((EntityTypeTest) EntityTypes.VILLAGER, this.mob.getBoundingBox().g(32.0D), this::a);

        if (list.isEmpty()) {
            return null;
        } else {
            EntityVillager entityvillager = (EntityVillager) list.get(this.mob.level.random.nextInt(list.size()));
            Vec3D vec3d = entityvillager.getPositionVector();

            return LandRandomPos.a(this.mob, 10, 7, vec3d);
        }
    }

    @Nullable
    private Vec3D l() {
        SectionPosition sectionposition = this.m();

        if (sectionposition == null) {
            return null;
        } else {
            BlockPosition blockposition = this.a(sectionposition);

            return blockposition == null ? null : LandRandomPos.a(this.mob, 10, 7, Vec3D.c((BaseBlockPosition) blockposition));
        }
    }

    @Nullable
    private SectionPosition m() {
        WorldServer worldserver = (WorldServer) this.mob.level;
        List<SectionPosition> list = (List) SectionPosition.a(SectionPosition.a((Entity) this.mob), 2).filter((sectionposition) -> {
            return worldserver.b(sectionposition) == 0;
        }).collect(Collectors.toList());

        return list.isEmpty() ? null : (SectionPosition) list.get(worldserver.random.nextInt(list.size()));
    }

    @Nullable
    private BlockPosition a(SectionPosition sectionposition) {
        WorldServer worldserver = (WorldServer) this.mob.level;
        VillagePlace villageplace = worldserver.A();
        List<BlockPosition> list = (List) villageplace.c((villageplacetype) -> {
            return true;
        }, sectionposition.q(), 8, VillagePlace.Occupancy.IS_OCCUPIED).map(VillagePlaceRecord::f).collect(Collectors.toList());

        return list.isEmpty() ? null : (BlockPosition) list.get(worldserver.random.nextInt(list.size()));
    }

    private boolean a(EntityVillager entityvillager) {
        return entityvillager.a(this.mob.level.getTime());
    }
}
