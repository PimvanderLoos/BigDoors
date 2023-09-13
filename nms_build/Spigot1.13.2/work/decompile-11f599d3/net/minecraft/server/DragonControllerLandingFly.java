package net.minecraft.server;

import javax.annotation.Nullable;

public class DragonControllerLandingFly extends AbstractDragonController {

    private PathEntity b;
    private Vec3D c;

    public DragonControllerLandingFly(EntityEnderDragon entityenderdragon) {
        super(entityenderdragon);
    }

    public DragonControllerPhase<DragonControllerLandingFly> getControllerPhase() {
        return DragonControllerPhase.LANDING_APPROACH;
    }

    public void d() {
        this.b = null;
        this.c = null;
    }

    public void c() {
        double d0 = this.c == null ? 0.0D : this.c.c(this.a.locX, this.a.locY, this.a.locZ);

        if (d0 < 100.0D || d0 > 22500.0D || this.a.positionChanged || this.a.C) {
            this.j();
        }

    }

    @Nullable
    public Vec3D g() {
        return this.c;
    }

    private void j() {
        if (this.b == null || this.b.b()) {
            int i = this.a.l();
            BlockPosition blockposition = this.a.world.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, WorldGenEndTrophy.a);
            EntityHuman entityhuman = this.a.world.a(blockposition, 128.0D, 128.0D);
            int j;

            if (entityhuman != null) {
                Vec3D vec3d = (new Vec3D(entityhuman.locX, 0.0D, entityhuman.locZ)).a();

                j = this.a.k(-vec3d.x * 40.0D, 105.0D, -vec3d.z * 40.0D);
            } else {
                j = this.a.k(40.0D, (double) blockposition.getY(), 0.0D);
            }

            PathPoint pathpoint = new PathPoint(blockposition.getX(), blockposition.getY(), blockposition.getZ());

            this.b = this.a.a(i, j, pathpoint);
            if (this.b != null) {
                this.b.a();
            }
        }

        this.k();
        if (this.b != null && this.b.b()) {
            this.a.getDragonControllerManager().setControllerPhase(DragonControllerPhase.LANDING);
        }

    }

    private void k() {
        if (this.b != null && !this.b.b()) {
            Vec3D vec3d = this.b.f();

            this.b.a();
            double d0 = vec3d.x;
            double d1 = vec3d.z;

            double d2;

            do {
                d2 = vec3d.y + (double) (this.a.getRandom().nextFloat() * 20.0F);
            } while (d2 < vec3d.y);

            this.c = new Vec3D(d0, d2, d1);
        }

    }
}
