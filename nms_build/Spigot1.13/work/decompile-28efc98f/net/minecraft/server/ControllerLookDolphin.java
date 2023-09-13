package net.minecraft.server;

public class ControllerLookDolphin extends ControllerLook {

    private final int h;

    public ControllerLookDolphin(EntityInsentient entityinsentient, int i) {
        super(entityinsentient);
        this.h = i;
    }

    public void a() {
        if (this.d) {
            this.d = false;
            double d0 = this.e - this.a.locX;
            double d1 = this.f - (this.a.locY + (double) this.a.getHeadHeight());
            double d2 = this.g - this.a.locZ;
            double d3 = (double) MathHelper.sqrt(d0 * d0 + d2 * d2);
            float f = (float) (MathHelper.c(d2, d0) * 57.2957763671875D) - 90.0F + 20.0F;
            float f1 = (float) (-(MathHelper.c(d1, d3) * 57.2957763671875D)) + 10.0F;

            this.a.pitch = this.a(this.a.pitch, f1, this.c);
            this.a.aS = this.a(this.a.aS, f, this.b);
        } else {
            if (this.a.getNavigation().p()) {
                this.a.pitch = this.a(this.a.pitch, 0.0F, 5.0F);
            }

            this.a.aS = this.a(this.a.aS, this.a.aQ, this.b);
        }

        float f2 = MathHelper.g(this.a.aS - this.a.aQ);

        if (f2 < (float) (-this.h)) {
            this.a.aQ -= 4.0F;
        } else if (f2 > (float) this.h) {
            this.a.aQ += 4.0F;
        }

    }
}
