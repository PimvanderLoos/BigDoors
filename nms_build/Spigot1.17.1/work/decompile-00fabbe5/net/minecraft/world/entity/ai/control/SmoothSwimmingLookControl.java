package net.minecraft.world.entity.ai.control;

import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.EntityInsentient;

public class SmoothSwimmingLookControl extends ControllerLook {

    private final int maxYRotFromCenter;
    private static final int HEAD_TILT_X = 10;
    private static final int HEAD_TILT_Y = 20;

    public SmoothSwimmingLookControl(EntityInsentient entityinsentient, int i) {
        super(entityinsentient);
        this.maxYRotFromCenter = i;
    }

    @Override
    public void a() {
        if (this.hasWanted) {
            this.hasWanted = false;
            this.i().ifPresent((ofloat) -> {
                this.mob.yHeadRot = this.a(this.mob.yHeadRot, ofloat + 20.0F, this.yMaxRotSpeed);
            });
            this.h().ifPresent((ofloat) -> {
                this.mob.setXRot(this.a(this.mob.getXRot(), ofloat + 10.0F, this.xMaxRotAngle));
            });
        } else {
            if (this.mob.getNavigation().m()) {
                this.mob.setXRot(this.a(this.mob.getXRot(), 0.0F, 5.0F));
            }

            this.mob.yHeadRot = this.a(this.mob.yHeadRot, this.mob.yBodyRot, this.yMaxRotSpeed);
        }

        float f = MathHelper.g(this.mob.yHeadRot - this.mob.yBodyRot);

        if (f < (float) (-this.maxYRotFromCenter)) {
            this.mob.yBodyRot -= 4.0F;
        } else if (f > (float) this.maxYRotFromCenter) {
            this.mob.yBodyRot += 4.0F;
        }

    }
}
