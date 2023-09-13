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
    protected int lookAtCooldown;
    protected double wantedX;
    protected double wantedY;
    protected double wantedZ;

    public ControllerLook(EntityInsentient entityinsentient) {
        this.mob = entityinsentient;
    }

    public void setLookAt(Vec3D vec3d) {
        this.setLookAt(vec3d.x, vec3d.y, vec3d.z);
    }

    public void setLookAt(Entity entity) {
        this.setLookAt(entity.getX(), getWantedY(entity), entity.getZ());
    }

    public void setLookAt(Entity entity, float f, float f1) {
        this.setLookAt(entity.getX(), getWantedY(entity), entity.getZ(), f, f1);
    }

    public void setLookAt(double d0, double d1, double d2) {
        this.setLookAt(d0, d1, d2, (float) this.mob.getHeadRotSpeed(), (float) this.mob.getMaxHeadXRot());
    }

    public void setLookAt(double d0, double d1, double d2, float f, float f1) {
        this.wantedX = d0;
        this.wantedY = d1;
        this.wantedZ = d2;
        this.yMaxRotSpeed = f;
        this.xMaxRotAngle = f1;
        this.lookAtCooldown = 2;
    }

    public void tick() {
        if (this.resetXRotOnTick()) {
            this.mob.setXRot(0.0F);
        }

        if (this.lookAtCooldown > 0) {
            --this.lookAtCooldown;
            this.getYRotD().ifPresent((ofloat) -> {
                this.mob.yHeadRot = this.rotateTowards(this.mob.yHeadRot, ofloat, this.yMaxRotSpeed);
            });
            this.getXRotD().ifPresent((ofloat) -> {
                this.mob.setXRot(this.rotateTowards(this.mob.getXRot(), ofloat, this.xMaxRotAngle));
            });
        } else {
            this.mob.yHeadRot = this.rotateTowards(this.mob.yHeadRot, this.mob.yBodyRot, 10.0F);
        }

        this.clampHeadRotationToBody();
    }

    protected void clampHeadRotationToBody() {
        if (!this.mob.getNavigation().isDone()) {
            this.mob.yHeadRot = MathHelper.rotateIfNecessary(this.mob.yHeadRot, this.mob.yBodyRot, (float) this.mob.getMaxHeadYRot());
        }

    }

    protected boolean resetXRotOnTick() {
        return true;
    }

    public boolean isLookingAtTarget() {
        return this.lookAtCooldown > 0;
    }

    public double getWantedX() {
        return this.wantedX;
    }

    public double getWantedY() {
        return this.wantedY;
    }

    public double getWantedZ() {
        return this.wantedZ;
    }

    protected Optional<Float> getXRotD() {
        double d0 = this.wantedX - this.mob.getX();
        double d1 = this.wantedY - this.mob.getEyeY();
        double d2 = this.wantedZ - this.mob.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);

        return Math.abs(d1) <= 9.999999747378752E-6D && Math.abs(d3) <= 9.999999747378752E-6D ? Optional.empty() : Optional.of((float) (-(MathHelper.atan2(d1, d3) * 57.2957763671875D)));
    }

    protected Optional<Float> getYRotD() {
        double d0 = this.wantedX - this.mob.getX();
        double d1 = this.wantedZ - this.mob.getZ();

        return Math.abs(d1) <= 9.999999747378752E-6D && Math.abs(d0) <= 9.999999747378752E-6D ? Optional.empty() : Optional.of((float) (MathHelper.atan2(d1, d0) * 57.2957763671875D) - 90.0F);
    }

    protected float rotateTowards(float f, float f1, float f2) {
        float f3 = MathHelper.degreesDifference(f, f1);
        float f4 = MathHelper.clamp(f3, -f2, f2);

        return f + f4;
    }

    private static double getWantedY(Entity entity) {
        return entity instanceof EntityLiving ? entity.getEyeY() : (entity.getBoundingBox().minY + entity.getBoundingBox().maxY) / 2.0D;
    }
}
