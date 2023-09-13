package net.minecraft.world.entity.boss.enderdragon.phases;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition;
import net.minecraft.world.entity.boss.enderdragon.EntityEnderDragon;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.WorldGenEndTrophy;
import net.minecraft.world.level.pathfinder.PathEntity;
import net.minecraft.world.level.pathfinder.PathPoint;
import net.minecraft.world.phys.Vec3D;

public class DragonControllerLandingFly extends AbstractDragonController {

    private static final PathfinderTargetCondition NEAR_EGG_TARGETING = PathfinderTargetCondition.a().d();
    private PathEntity currentPath;
    private Vec3D targetLocation;

    public DragonControllerLandingFly(EntityEnderDragon entityenderdragon) {
        super(entityenderdragon);
    }

    @Override
    public DragonControllerPhase<DragonControllerLandingFly> getControllerPhase() {
        return DragonControllerPhase.LANDING_APPROACH;
    }

    @Override
    public void d() {
        this.currentPath = null;
        this.targetLocation = null;
    }

    @Override
    public void c() {
        double d0 = this.targetLocation == null ? 0.0D : this.targetLocation.c(this.dragon.locX(), this.dragon.locY(), this.dragon.locZ());

        if (d0 < 100.0D || d0 > 22500.0D || this.dragon.horizontalCollision || this.dragon.verticalCollision) {
            this.j();
        }

    }

    @Nullable
    @Override
    public Vec3D g() {
        return this.targetLocation;
    }

    private void j() {
        if (this.currentPath == null || this.currentPath.c()) {
            int i = this.dragon.p();
            BlockPosition blockposition = this.dragon.level.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, WorldGenEndTrophy.END_PODIUM_LOCATION);
            EntityHuman entityhuman = this.dragon.level.a(DragonControllerLandingFly.NEAR_EGG_TARGETING, this.dragon, (double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ());
            int j;

            if (entityhuman != null) {
                Vec3D vec3d = (new Vec3D(entityhuman.locX(), 0.0D, entityhuman.locZ())).d();

                j = this.dragon.q(-vec3d.x * 40.0D, 105.0D, -vec3d.z * 40.0D);
            } else {
                j = this.dragon.q(40.0D, (double) blockposition.getY(), 0.0D);
            }

            PathPoint pathpoint = new PathPoint(blockposition.getX(), blockposition.getY(), blockposition.getZ());

            this.currentPath = this.dragon.a(i, j, pathpoint);
            if (this.currentPath != null) {
                this.currentPath.a();
            }
        }

        this.k();
        if (this.currentPath != null && this.currentPath.c()) {
            this.dragon.getDragonControllerManager().setControllerPhase(DragonControllerPhase.LANDING);
        }

    }

    private void k() {
        if (this.currentPath != null && !this.currentPath.c()) {
            BlockPosition blockposition = this.currentPath.g();

            this.currentPath.a();
            double d0 = (double) blockposition.getX();
            double d1 = (double) blockposition.getZ();

            double d2;

            do {
                d2 = (double) ((float) blockposition.getY() + this.dragon.getRandom().nextFloat() * 20.0F);
            } while (d2 < (double) blockposition.getY());

            this.targetLocation = new Vec3D(d0, d2, d1);
        }

    }
}
