package net.minecraft.world.entity.ai.control;

import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;

public class ControllerMoveFlying extends ControllerMove {

    private final int maxTurn;
    private final boolean hoversInPlace;

    public ControllerMoveFlying(EntityInsentient entityinsentient, int i, boolean flag) {
        super(entityinsentient);
        this.maxTurn = i;
        this.hoversInPlace = flag;
    }

    @Override
    public void tick() {
        if (this.operation == ControllerMove.Operation.MOVE_TO) {
            this.operation = ControllerMove.Operation.WAIT;
            this.mob.setNoGravity(true);
            double d0 = this.wantedX - this.mob.getX();
            double d1 = this.wantedY - this.mob.getY();
            double d2 = this.wantedZ - this.mob.getZ();
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;

            if (d3 < 2.500000277905201E-7D) {
                this.mob.setYya(0.0F);
                this.mob.setZza(0.0F);
                return;
            }

            float f = (float) (MathHelper.atan2(d2, d0) * 57.2957763671875D) - 90.0F;

            this.mob.setYRot(this.rotlerp(this.mob.getYRot(), f, 90.0F));
            float f1;

            if (this.mob.isOnGround()) {
                f1 = (float) (this.speedModifier * this.mob.getAttributeValue(GenericAttributes.MOVEMENT_SPEED));
            } else {
                f1 = (float) (this.speedModifier * this.mob.getAttributeValue(GenericAttributes.FLYING_SPEED));
            }

            this.mob.setSpeed(f1);
            double d4 = Math.sqrt(d0 * d0 + d2 * d2);

            if (Math.abs(d1) > 9.999999747378752E-6D || Math.abs(d4) > 9.999999747378752E-6D) {
                float f2 = (float) (-(MathHelper.atan2(d1, d4) * 57.2957763671875D));

                this.mob.setXRot(this.rotlerp(this.mob.getXRot(), f2, (float) this.maxTurn));
                this.mob.setYya(d1 > 0.0D ? f1 : -f1);
            }
        } else {
            if (!this.hoversInPlace) {
                this.mob.setNoGravity(false);
            }

            this.mob.setYya(0.0F);
            this.mob.setZza(0.0F);
        }

    }
}
