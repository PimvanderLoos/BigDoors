package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IPosition;
import net.minecraft.core.SectionPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.phys.Vec3D;

public class PathfinderGoalNearestVillage extends PathfinderGoal {

    private static final int DISTANCE_THRESHOLD = 10;
    private final EntityCreature mob;
    private final int interval;
    @Nullable
    private BlockPosition wantedPos;

    public PathfinderGoalNearestVillage(EntityCreature entitycreature, int i) {
        this.mob = entitycreature;
        this.interval = i;
        this.a(EnumSet.of(PathfinderGoal.Type.MOVE));
    }

    @Override
    public boolean a() {
        if (this.mob.isVehicle()) {
            return false;
        } else if (this.mob.level.isDay()) {
            return false;
        } else if (this.mob.getRandom().nextInt(this.interval) != 0) {
            return false;
        } else {
            WorldServer worldserver = (WorldServer) this.mob.level;
            BlockPosition blockposition = this.mob.getChunkCoordinates();

            if (!worldserver.a(blockposition, 6)) {
                return false;
            } else {
                Vec3D vec3d = LandRandomPos.a(this.mob, 15, 7, (blockposition1) -> {
                    return (double) (-worldserver.b(SectionPosition.a(blockposition1)));
                });

                this.wantedPos = vec3d == null ? null : new BlockPosition(vec3d);
                return this.wantedPos != null;
            }
        }
    }

    @Override
    public boolean b() {
        return this.wantedPos != null && !this.mob.getNavigation().m() && this.mob.getNavigation().h().equals(this.wantedPos);
    }

    @Override
    public void e() {
        if (this.wantedPos != null) {
            NavigationAbstract navigationabstract = this.mob.getNavigation();

            if (navigationabstract.m() && !this.wantedPos.a((IPosition) this.mob.getPositionVector(), 10.0D)) {
                Vec3D vec3d = Vec3D.c((BaseBlockPosition) this.wantedPos);
                Vec3D vec3d1 = this.mob.getPositionVector();
                Vec3D vec3d2 = vec3d1.d(vec3d);

                vec3d = vec3d2.a(0.4D).e(vec3d);
                Vec3D vec3d3 = vec3d.d(vec3d1).d().a(10.0D).e(vec3d1);
                BlockPosition blockposition = new BlockPosition(vec3d3);

                blockposition = this.mob.level.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, blockposition);
                if (!navigationabstract.a((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), 1.0D)) {
                    this.g();
                }
            }

        }
    }

    private void g() {
        Random random = this.mob.getRandom();
        BlockPosition blockposition = this.mob.level.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, this.mob.getChunkCoordinates().c(-8 + random.nextInt(16), 0, -8 + random.nextInt(16)));

        this.mob.getNavigation().a((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), 1.0D);
    }
}
