package net.minecraft.world.entity.ai.control;

import java.util.Optional;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.phys.Vec3D;

public class ControllerLook implements Control {

    protected final EntityInsentient mob;
    protected float yMaxRotSpeed;
    protected float xMaxRotAngle;
    protected boolean hasWanted;
    protected double wantedX;
    protected double wantedY;
    protected double wantedZ;

    public ControllerLook(EntityInsentient entityinsentient) {
        this.mob = entityinsentient;
    }

    public void a(Vec3D vec3d) {
        this.a(vec3d.x, vec3d.y, vec3d.z);
    }

    public void a(Entity entity) {
        this.a(entity.locX(), b(entity), entity.locZ());
    }

    public void a(Entity entity, float f, float f1) {
        this.a(entity.locX(), b(entity), entity.locZ(), f, f1);
    }

    public void a(double d0, double d1, double d2) {
        this.a(d0, d1, d2, (float) this.mob.fb(), (float) this.mob.eZ());
    }

    public void a(double d0, double d1, double d2, float f, float f1) {
        this.wantedX = d0;
        this.wantedY = d1;
        this.wantedZ = d2;
        this.yMaxRotSpeed = f;
        this.xMaxRotAngle = f1;
        this.hasWanted = true;
    }

    public void a() {
        if (this.c()) {
            this.mob.setXRot(0.0F);
        }

        if (this.hasWanted) {
            this.hasWanted = false;
            this.i().ifPresent((ofloat) -> {
                this.mob.yHeadRot = this.a(this.mob.yHeadRot, ofloat, this.yMaxRotSpeed);
            });
            this.h().ifPresent((ofloat) -> {
                this.mob.setXRot(this.a(this.mob.getXRot(), ofloat, this.xMaxRotAngle));
            });
        } else {
            this.mob.yHeadRot = this.a(this.mob.yHeadRot, this.mob.yBodyRot, 10.0F);
        }

        this.b();
    }

    protected void b() {
        if (!this.mob.getNavigation().m()) {
            this.mob.yHeadRot = MathHelper.c(this.mob.yHeadRot, this.mob.yBodyRot, (float) this.mob.fa());
        }

    }

    protected boolean c() {
        return true;
    }

    public boolean d() {
        return this.hasWanted;
    }

    public double e() {
        return this.wantedX;
    }

    public double f() {
        return this.wantedY;
    }

    public double g() {
        return this.wantedZ;
    }

    protected Optional<Float> h() {
        double d0 = this.wantedX - this.mob.locX();
        double d1 = this.wantedY - this.mob.getHeadY();
        double d2 = this.wantedZ - this.mob.locZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);

        return Math.abs(d1) <= 9.999999747378752E-6D && Math.abs(d3) <= 9.999999747378752E-6D ? Optional.empty() : Optional.of((float) (-(MathHelper.d(d1, d3) * 57.2957763671875D)));
    }

    protected Optional<Float> i() {
        double d0 = this.wantedX - this.mob.locX();
        double d1 = this.wantedZ - this.mob.locZ();

        return Math.abs(d1) <= 9.999999747378752E-6D && Math.abs(d0) <= 9.999999747378752E-6D ? Optional.empty() : Optional.of((float) (MathHelper.d(d1, d0) * 57.2957763671875D) - 90.0F);
    }

    protected float a(float f, float f1, float f2) {
        float f3 = MathHelper.c(f, f1);
        float f4 = MathHelper.a(f3, -f2, f2);

        return f + f4;
    }

    private static double b(Entity entity) {
        return entity instanceof EntityLiving ? entity.getHeadY() : (entity.getBoundingBox().minY + entity.getBoundingBox().maxY) / 2.0D;
    }
}
