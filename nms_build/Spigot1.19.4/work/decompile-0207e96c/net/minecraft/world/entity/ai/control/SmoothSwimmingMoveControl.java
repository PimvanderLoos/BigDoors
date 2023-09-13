package net.minecraft.world.entity.ai.control;

import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;

public class SmoothSwimmingMoveControl extends ControllerMove {

    private static final float FULL_SPEED_TURN_THRESHOLD = 10.0F;
    private static final float STOP_TURN_THRESHOLD = 60.0F;
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
    public void tick() {
        if (this.applyGravity && this.mob.isInWater()) {
            this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(0.0D, 0.005D, 0.0D));
        }

        if (this.operation == ControllerMove.Operation.MOVE_TO && !this.mob.getNavigation().isDone()) {
            double d0 = this.wantedX - this.mob.getX();
            double d1 = this.wantedY - this.mob.getY();
            double d2 = this.wantedZ - this.mob.getZ();
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;

            if (d3 < 2.500000277905201E-7D) {
                this.mob.setZza(0.0F);
            } else {
                float f = (float) (MathHelper.atan2(d2, d0) * 57.2957763671875D) - 90.0F;

                this.mob.setYRot(this.rotlerp(this.mob.getYRot(), f, (float) this.maxTurnY));
                this.mob.yBodyRot = this.mob.getYRot();
                this.mob.yHeadRot = this.mob.getYRot();
                float f1 = (float) (this.speedModifier * this.mob.getAttributeValue(GenericAttributes.MOVEMENT_SPEED));

                if (this.mob.isInWater()) {
                    this.mob.setSpeed(f1 * this.inWaterSpeedModifier);
                    double d4 = Math.sqrt(d0 * d0 + d2 * d2);
                    float f2;

                    if (Math.abs(d1) > 9.999999747378752E-6D || Math.abs(d4) > 9.999999747378752E-6D) {
                        f2 = -((float) (MathHelper.atan2(d1, d4) * 57.2957763671875D));
                        f2 = MathHelper.clamp(MathHelper.wrapDegrees(f2), (float) (-this.maxTurnX), (float) this.maxTurnX);
                        this.mob.setXRot(this.rotlerp(this.mob.getXRot(), f2, 5.0F));
                    }

                    f2 = MathHelper.cos(this.mob.getXRot() * 0.017453292F);
                    float f3 = MathHelper.sin(this.mob.getXRot() * 0.017453292F);

                    this.mob.zza = f2 * f1;
                    this.mob.yya = -f3 * f1;
                } else {
                    float f4 = Math.abs(MathHelper.wrapDegrees(this.mob.getYRot() - f));
                    float f5 = getTurningSpeedFactor(f4);

                    this.mob.setSpeed(f1 * this.outsideWaterSpeedModifier * f5);
                }

            }
        } else {
            this.mob.setSpeed(0.0F);
            this.mob.setXxa(0.0F);
            this.mob.setYya(0.0F);
            this.mob.setZza(0.0F);
        }
    }

    private static float getTurningSpeedFactor(float f) {
        return 1.0F - MathHelper.clamp((f - 10.0F) / 50.0F, 0.0F, 1.0F);
    }
}
