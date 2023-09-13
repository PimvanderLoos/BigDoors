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

    private boolean b;
    private PathEntity c;
    private Vec3D d;

    public DragonControllerFly(EntityEnderDragon entityenderdragon) {
        super(entityenderdragon);
    }

    @Override
    public void c() {
        if (!this.b && this.c != null) {
            BlockPosition blockposition = this.a.world.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, WorldGenEndTrophy.a);

            if (!blockposition.a((IPosition) this.a.getPositionVector(), 10.0D)) {
                this.a.getDragonControllerManager().setControllerPhase(DragonControllerPhase.HOLDING_PATTERN);
            }
        } else {
            this.b = false;
            this.j();
        }

    }

    @Override
    public void d() {
        this.b = true;
        this.c = null;
        this.d = null;
    }

    private void j() {
        int i = this.a.eI();
        Vec3D vec3d = this.a.x(1.0F);
        int j = this.a.p(-vec3d.x * 40.0D, 105.0D, -vec3d.z * 40.0D);

        if (this.a.getEnderDragonBattle() != null && this.a.getEnderDragonBattle().c() > 0) {
            j %= 12;
            if (j < 0) {
                j += 12;
            }
        } else {
            j -= 12;
            j &= 7;
            j += 12;
        }

        this.c = this.a.a(i, j, (PathPoint) null);
        this.k();
    }

    private void k() {
        if (this.c != null) {
            this.c.a();
            if (!this.c.c()) {
                BlockPosition blockposition = this.c.g();

                this.c.a();

                double d0;

                do {
                    d0 = (double) ((float) blockposition.getY() + this.a.getRandom().nextFloat() * 20.0F);
                } while (d0 < (double) blockposition.getY());

                this.d = new Vec3D((double) blockposition.getX(), d0, (double) blockposition.getZ());
            }
        }

    }

    @Nullable
    @Override
    public Vec3D g() {
        return this.d;
    }

    @Override
    public DragonControllerPhase<DragonControllerFly> getControllerPhase() {
        return DragonControllerPhase.TAKEOFF;
    }
}
