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
import net.minecraft.world.entity.ai.util.RandomPositionGenerator;
import net.minecraft.world.entity.ai.village.poi.VillagePlace;
import net.minecraft.world.entity.ai.village.poi.VillagePlaceRecord;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.phys.Vec3D;

public class PathfinderGoalStrollVillageGolem extends PathfinderGoalRandomStroll {

    public PathfinderGoalStrollVillageGolem(EntityCreature entitycreature, double d0) {
        super(entitycreature, d0, 240, false);
    }

    @Nullable
    @Override
    protected Vec3D g() {
        float f = this.a.world.random.nextFloat();

        if (this.a.world.random.nextFloat() < 0.3F) {
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
        return RandomPositionGenerator.b(this.a, 10, 7);
    }

    @Nullable
    private Vec3D k() {
        WorldServer worldserver = (WorldServer) this.a.world;
        List<EntityVillager> list = worldserver.a(EntityTypes.VILLAGER, this.a.getBoundingBox().g(32.0D), this::a);

        if (list.isEmpty()) {
            return null;
        } else {
            EntityVillager entityvillager = (EntityVillager) list.get(this.a.world.random.nextInt(list.size()));
            Vec3D vec3d = entityvillager.getPositionVector();

            return RandomPositionGenerator.a(this.a, 10, 7, vec3d);
        }
    }

    @Nullable
    private Vec3D l() {
        SectionPosition sectionposition = this.m();

        if (sectionposition == null) {
            return null;
        } else {
            BlockPosition blockposition = this.a(sectionposition);

            return blockposition == null ? null : RandomPositionGenerator.a(this.a, 10, 7, Vec3D.c((BaseBlockPosition) blockposition));
        }
    }

    @Nullable
    private SectionPosition m() {
        WorldServer worldserver = (WorldServer) this.a.world;
        List<SectionPosition> list = (List) SectionPosition.a(SectionPosition.a((Entity) this.a), 2).filter((sectionposition) -> {
            return worldserver.b(sectionposition) == 0;
        }).collect(Collectors.toList());

        return list.isEmpty() ? null : (SectionPosition) list.get(worldserver.random.nextInt(list.size()));
    }

    @Nullable
    private BlockPosition a(SectionPosition sectionposition) {
        WorldServer worldserver = (WorldServer) this.a.world;
        VillagePlace villageplace = worldserver.y();
        List<BlockPosition> list = (List) villageplace.c((villageplacetype) -> {
            return true;
        }, sectionposition.q(), 8, VillagePlace.Occupancy.IS_OCCUPIED).map(VillagePlaceRecord::f).collect(Collectors.toList());

        return list.isEmpty() ? null : (BlockPosition) list.get(worldserver.random.nextInt(list.size()));
    }

    private boolean a(EntityVillager entityvillager) {
        return entityvillager.a(this.a.world.getTime());
    }
}
