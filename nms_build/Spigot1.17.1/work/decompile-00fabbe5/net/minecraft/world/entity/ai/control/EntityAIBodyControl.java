package net.minecraft.world.entity.ai.control;

import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.EntityInsentient;

public class EntityAIBodyControl implements Control {

    private final EntityInsentient mob;
    private static final int HEAD_STABLE_ANGLE = 15;
    private static final int DELAY_UNTIL_STARTING_TO_FACE_FORWARD = 10;
    private static final int HOW_LONG_IT_TAKES_TO_FACE_FORWARD = 10;
    private int headStableTime;
    private float lastStableYHeadRot;

    public EntityAIBodyControl(EntityInsentient entityinsentient) {
        this.mob = entityinsentient;
    }

    public void a() {
        if (this.f()) {
            this.mob.yBodyRot = this.mob.getYRot();
            this.c();
            this.lastStableYHeadRot = this.mob.yHeadRot;
            this.headStableTime = 0;
        } else {
            if (this.e()) {
                if (Math.abs(this.mob.yHeadRot - this.lastStableYHeadRot) > 15.0F) {
                    this.headStableTime = 0;
                    this.lastStableYHeadRot = this.mob.yHeadRot;
                    this.b();
                } else {
                    ++this.headStableTime;
                    if (this.headStableTime > 10) {
                        this.d();
                    }
                }
            }

        }
    }

    private void b() {
        this.mob.yBodyRot = MathHelper.c(this.mob.yBodyRot, this.mob.yHeadRot, (float) this.mob.fa());
    }

    private void c() {
        this.mob.yHeadRot = MathHelper.c(this.mob.yHeadRot, this.mob.yBodyRot, (float) this.mob.fa());
    }

    private void d() {
        int i = this.headStableTime - 10;
        float f = MathHelper.a((float) i / 10.0F, 0.0F, 1.0F);
        float f1 = (float) this.mob.fa() * (1.0F - f);

        this.mob.yBodyRot = MathHelper.c(this.mob.yBodyRot, this.mob.yHeadRot, f1);
    }

    private boolean e() {
        return !(this.mob.cB() instanceof EntityInsentient);
    }

    private boolean f() {
        double d0 = this.mob.locX() - this.mob.xo;
        double d1 = this.mob.locZ() - this.mob.zo;

        return d0 * d0 + d1 * d1 > 2.500000277905201E-7D;
    }
}
