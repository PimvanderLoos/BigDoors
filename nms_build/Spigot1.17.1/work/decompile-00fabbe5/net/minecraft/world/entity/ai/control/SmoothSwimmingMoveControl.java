package net.minecraft.world.entity.ai.control;

import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;

public class SmoothSwimmingMoveControl extends ControllerMove {

    private final int maxTurnX;
    private final int maxTurnY;
    private final float inWaterSpeedModifier;
    private final float outsideWaterSpeedModifier;
    private final boolean applyGravity;

    public SmoothSwimmingMoveControl(EntityInsentient entityinsentient, int i, int j, float f, float f1, boolean flag) {
        super(entityinsentient);
        this.maxTurnX = i;
        this.maxTurnY = j;
        this.inWaterSpeedModifier = f;
        this.outsideWaterSpeedModifier = f1;
        this.applyGravity = flag;
    }

    @Override
    public void a() {
        if (this.applyGravity && this.mob.isInWater()) {
            this.mob.setMot(this.mob.getMot().add(0.0D, 0.005D, 0.0D));
        }

        if (this.operation == ControllerMove.Operation.MOVE_TO && !this.mob.getNavigation().m()) {
            double d0 = this.wantedX - this.mob.locX();
            double d1 = this.wantedY - this.mob.locY();
            double d2 = this.wantedZ - this.mob.locZ();
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;

            if (d3 < 2.500000277905201E-7D) {
                this.mob.u(0.0F);
            } else {
                float f = (float) (MathHelper.d(d2, d0) * 57.2957763671875D) - 90.0F;

                this.mob.setYRot(this.a(this.mob.getYRot(), f, (float) this.maxTurnY));
                this.mob.yBodyRot = this.mob.getYRot();
                this.mob.yHeadRot = this.mob.getYRot();
                float f1 = (float) (this.speedModifier * this.mob.b(GenericAttributes.MOVEMENT_SPEED));

                if (this.mob.isInWater()) {
                    this.mob.r(f1 * this.inWaterSpeedModifier);
                    double d4 = Math.sqrt(d0 * d0 + d2 * d2);
                    float f2;

                    if (Math.abs(d1) > 9.999999747378752E-6D || Math.abs(d4) > 9.999999747378752E-6D) {
                        f2 = -((float) (MathHelper.d(d1, d4) * 57.2957763671875D));
                        f2 = MathHelper.a(MathHelper.g(f2), (float) (-this.maxTurnX), (float) this.maxTurnX);
                        this.mob.setXRot(this.a(this.mob.getXRot(), f2, 5.0F));
                    }

                    f2 = MathHelper.cos(this.mob.getXRot() * 0.017453292F);
                    float f3 = MathHelper.sin(this.mob.getXRot() * 0.017453292F);

                    this.mob.zza = f2 * f1;
                    this.mob.yya = -f3 * f1;
                } else {
                    this.mob.r(f1 * this.outsideWaterSpeedModifier);
                }

            }
        } else {
            this.mob.r(0.0F);
            this.mob.w(0.0F);
            this.mob.v(0.0F);
            this.mob.u(0.0F);
        }
    }
}
