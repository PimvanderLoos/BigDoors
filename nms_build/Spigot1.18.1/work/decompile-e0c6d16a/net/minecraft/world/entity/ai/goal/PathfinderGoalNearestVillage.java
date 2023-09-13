package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import java.util.Random;
import javax.annotation.Nullable;
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
        this.interval = reducedTickDelay(i);
        this.setFlags(EnumSet.of(PathfinderGoal.Type.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.mob.isVehicle()) {
            return false;
        } else if (this.mob.level.isDay()) {
            return false;
        } else if (this.mob.getRandom().nextInt(this.interval) != 0) {
            return false;
        } else {
            WorldServer worldserver = (WorldServer) this.mob.level;
            BlockPosition blockposition = this.mob.blockPosition();

            if (!worldserver.isCloseToVillage(blockposition, 6)) {
                return false;
            } else {
                Vec3D vec3d = LandRandomPos.getPos(this.mob, 15, 7, (blockposition1) -> {
                    return (double) (-worldserver.sectionsToVillage(SectionPosition.of(blockposition1)));
                });

                this.wantedPos = vec3d == null ? null : new BlockPosition(vec3d);
                return this.wantedPos != null;
            }
        }
    }

    @Override
    public boolean canContinueToUse() {
        return this.wantedPos != null && !this.mob.getNavigation().isDone() && this.mob.getNavigation().getTargetPos().equals(this.wantedPos);
    }

    @Override
    public void tick() {
        if (this.wantedPos != null) {
            NavigationAbstract navigationabstract = this.mob.getNavigation();

            if (navigationabstract.isDone() && !this.wantedPos.closerThan((IPosition) this.mob.position(), 10.0D)) {
                Vec3D vec3d = Vec3D.atBottomCenterOf(this.wantedPos);
                Vec3D vec3d1 = this.mob.position();
                Vec3D vec3d2 = vec3d1.subtract(vec3d);

                vec3d = vec3d2.scale(0.4D).add(vec3d);
                Vec3D vec3d3 = vec3d.subtract(vec3d1).normalize().scale(10.0D).add(vec3d1);
                BlockPosition blockposition = new BlockPosition(vec3d3);

                blockposition = this.mob.level.getHeightmapPos(HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, blockposition);
                if (!navigationabstract.moveTo((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), 1.0D)) {
                    this.moveRandomly();
                }
            }

        }
    }

    private void moveRandomly() {
        Random random = this.mob.getRandom();
        BlockPosition blockposition = this.mob.level.getHeightmapPos(HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, this.mob.blockPosition().offset(-8 + random.nextInt(16), 0, -8 + random.nextInt(16)));

        this.mob.getNavigation().moveTo((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), 1.0D);
    }
}
