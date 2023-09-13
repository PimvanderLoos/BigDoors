package net.minecraft.server;

public class ControllerMoveFlying extends ControllerMove {

    public ControllerMoveFlying(EntityInsentient entityinsentient) {
        super(entityinsentient);
    }

    public void a() {
        if (this.h == ControllerMove.Operation.MOVE_TO) {
            this.h = ControllerMove.Operation.WAIT;
            this.a.setNoGravity(true);
            double d0 = this.b - this.a.locX;
            double d1 = this.c - this.a.locY;
            double d2 = this.d - this.a.locZ;
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;

            if (d3 < 2.500000277905201E-7D) {
                this.a.o(0.0F);
                this.a.n(0.0F);
                return;
            }

            float f = (float) (MathHelper.c(d2, d0) * 57.2957763671875D) - 90.0F;

            this.a.yaw = this.a(this.a.yaw, f, 10.0F);
            float f1;

            if (this.a.onGround) {
                f1 = (float) (this.e * this.a.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue());
            } else {
                f1 = (float) (this.e * this.a.getAttributeInstance(GenericAttributes.e).getValue());
            }

            this.a.k(f1);
            double d4 = (double) MathHelper.sqrt(d0 * d0 + d2 * d2);
            float f2 = (float) (-(MathHelper.c(d1, d4) * 57.2957763671875D));

            this.a.pitch = this.a(this.a.pitch, f2, 10.0F);
            this.a.o(d1 > 0.0D ? f1 : -f1);
        } else {
            this.a.setNoGravity(false);
            this.a.o(0.0F);
            this.a.n(0.0F);
        }

    }
}
