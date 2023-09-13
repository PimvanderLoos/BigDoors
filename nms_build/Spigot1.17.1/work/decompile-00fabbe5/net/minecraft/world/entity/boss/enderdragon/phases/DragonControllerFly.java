package net.minecraft.world.entity.boss.enderdragon.phases;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IPosition;
import net.minecraft.world.entity.boss.enderdragon.EntityEnderDragon;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.WorldGenEndTrophy;
import net.minecraft.world.level.pathfinder.PathEntity;
import net.minecraft.world.level.pathfinder.PathPoint;
import net.minecraft.world.phys.Vec3D;

public class DragonControllerFly extends AbstractDragonController {

    private boolean firstTick;
    private PathEntity currentPath;
    private Vec3D targetLocation;

    public DragonControllerFly(EntityEnderDragon entityenderdragon) {
        super(entityenderdragon);
    }

    @Override
    public void c() {
        if (!this.firstTick && this.currentPath != null) {
            BlockPosition blockposition = this.dragon.level.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, WorldGenEndTrophy.END_PODIUM_LOCATION);

            if (!blockposition.a((IPosition) this.dragon.getPositionVector(), 10.0D)) {
                this.dragon.getDragonControllerManager().setControllerPhase(DragonControllerPhase.HOLDING_PATTERN);
            }
        } else {
            this.firstTick = false;
            this.j();
        }

    }

    @Override
    public void d() {
        this.firstTick = true;
        this.currentPath = null;
        this.targetLocation = null;
    }

    private void j() {
        int i = this.dragon.p();
        Vec3D vec3d = this.dragon.y(1.0F);
        int j = this.dragon.q(-vec3d.x * 40.0D, 105.0D, -vec3d.z * 40.0D);

        if (this.dragon.getEnderDragonBattle() != null && this.dragon.getEnderDragonBattle().c() > 0) {
            j %= 12;
            if (j < 0) {
                j += 12;
            }
        } else {
            j -= 12;
            j &= 7;
            j += 12;
        }

        this.currentPath = this.dragon.a(i, j, (PathPoint) null);
        this.k();
    }

    private void k() {
        if (this.currentPath != null) {
            this.currentPath.a();
            if (!this.currentPath.c()) {
                BlockPosition blockposition = this.currentPath.g();

                this.currentPath.a();

                double d0;

                do {
                    d0 = (double) ((float) blockposition.getY() + this.dragon.getRandom().nextFloat() * 20.0F);
                } while (d0 < (double) blockposition.getY());

                this.targetLocation = new Vec3D((double) blockposition.getX(), d0, (double) blockposition.getZ());
            }
        }

    }

    @Nullable
    @Override
    public Vec3D g() {
        return this.targetLocation;
    }

    @Override
    public DragonControllerPhase<DragonControllerFly> getControllerPhase() {
        return DragonControllerPhase.TAKEOFF;
    }
}
