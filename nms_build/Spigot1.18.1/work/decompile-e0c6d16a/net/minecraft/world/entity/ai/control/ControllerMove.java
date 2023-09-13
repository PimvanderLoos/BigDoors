package net.minecraft.world.entity.ai.control;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfinderAbstract;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ControllerMove implements Control {

    public static final float MIN_SPEED = 5.0E-4F;
    public static final float MIN_SPEED_SQR = 2.5000003E-7F;
    protected static final int MAX_TURN = 90;
    protected final EntityInsentient mob;
    protected double wantedX;
    protected double wantedY;
    protected double wantedZ;
    protected double speedModifier;
    protected float strafeForwards;
    protected float strafeRight;
    protected ControllerMove.Operation operation;

    public ControllerMove(EntityInsentient entityinsentient) {
        this.operation = ControllerMove.Operation.WAIT;
        this.mob = entityinsentient;
    }

    public boolean hasWanted() {
        return this.operation == ControllerMove.Operation.MOVE_TO;
    }

    public double getSpeedModifier() {
        return this.speedModifier;
    }

    public void setWantedPosition(double d0, double d1, double d2, double d3) {
        this.wantedX = d0;
        this.wantedY = d1;
        this.wantedZ = d2;
        this.speedModifier = d3;
        if (this.operation != ControllerMove.Operation.JUMPING) {
            this.operation = ControllerMove.Operation.MOVE_TO;
        }

    }

    public void strafe(float f, float f1) {
        this.operation = ControllerMove.Operation.STRAFE;
        this.strafeForwards = f;
        this.strafeRight = f1;
        this.speedModifier = 0.25D;
    }

    public void tick() {
        float f;

        if (this.operation == ControllerMove.Operation.STRAFE) {
            float f1 = (float) this.mob.getAttributeValue(GenericAttributes.MOVEMENT_SPEED);
            float f2 = (float) this.speedModifier * f1;
            float f3 = this.strafeForwards;
            float f4 = this.strafeRight;
            float f5 = MathHelper.sqrt(f3 * f3 + f4 * f4);

            if (f5 < 1.0F) {
                f5 = 1.0F;
            }

            f5 = f2 / f5;
            f3 *= f5;
            f4 *= f5;
            float f6 = MathHelper.sin(this.mob.getYRot() * 0.017453292F);
            float f7 = MathHelper.cos(this.mob.getYRot() * 0.017453292F);
            float f8 = f3 * f7 - f4 * f6;

            f = f4 * f7 + f3 * f6;
            if (!this.isWalkable(f8, f)) {
                this.strafeForwards = 1.0F;
                this.strafeRight = 0.0F;
            }

            this.mob.setSpeed(f2);
            this.mob.setZza(this.strafeForwards);
            this.mob.setXxa(this.strafeRight);
            this.operation = ControllerMove.Operation.WAIT;
        } else if (this.operation == ControllerMove.Operation.MOVE_TO) {
            this.operation = ControllerMove.Operation.WAIT;
            double d0 = this.wantedX - this.mob.getX();
            double d1 = this.wantedZ - this.mob.getZ();
            double d2 = this.wantedY - this.mob.getY();
            double d3 = d0 * d0 + d2 * d2 + d1 * d1;

            if (d3 < 2.500000277905201E-7D) {
                this.mob.setZza(0.0F);
                return;
            }

            f = (float) (MathHelper.atan2(d1, d0) * 57.2957763671875D) - 90.0F;
            this.mob.setYRot(this.rotlerp(this.mob.getYRot(), f, 90.0F));
            this.mob.setSpeed((float) (this.speedModifier * this.mob.getAttributeValue(GenericAttributes.MOVEMENT_SPEED)));
            BlockPosition blockposition = this.mob.blockPosition();
            IBlockData iblockdata = this.mob.level.getBlockState(blockposition);
            VoxelShape voxelshape = iblockdata.getCollisionShape(this.mob.level, blockposition);

            if (d2 > (double) this.mob.maxUpStep && d0 * d0 + d1 * d1 < (double) Math.max(1.0F, this.mob.getBbWidth()) || !voxelshape.isEmpty() && this.mob.getY() < voxelshape.max(EnumDirection.EnumAxis.Y) + (double) blockposition.getY() && !iblockdata.is((Tag) TagsBlock.DOORS) && !iblockdata.is((Tag) TagsBlock.FENCES)) {
                this.mob.getJumpControl().jump();
                this.operation = ControllerMove.Operation.JUMPING;
            }
        } else if (this.operation == ControllerMove.Operation.JUMPING) {
            this.mob.setSpeed((float) (this.speedModifier * this.mob.getAttributeValue(GenericAttributes.MOVEMENT_SPEED)));
            if (this.mob.isOnGround()) {
                this.operation = ControllerMove.Operation.WAIT;
            }
        } else {
            this.mob.setZza(0.0F);
        }

    }

    private boolean isWalkable(float f, float f1) {
        NavigationAbstract navigationabstract = this.mob.getNavigation();

        if (navigationabstract != null) {
            PathfinderAbstract pathfinderabstract = navigationabstract.getNodeEvaluator();

            if (pathfinderabstract != null && pathfinderabstract.getBlockPathType(this.mob.level, MathHelper.floor(this.mob.getX() + (double) f), this.mob.getBlockY(), MathHelper.floor(this.mob.getZ() + (double) f1)) != PathType.WALKABLE) {
                return false;
            }
        }

        return true;
    }

    protected float rotlerp(float f, float f1, float f2) {
        float f3 = MathHelper.wrapDegrees(f1 - f);

        if (f3 > f2) {
            f3 = f2;
        }

        if (f3 < -f2) {
            f3 = -f2;
        }

        float f4 = f + f3;

        if (f4 < 0.0F) {
            f4 += 360.0F;
        } else if (f4 > 360.0F) {
            f4 -= 360.0F;
        }

        return f4;
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

    protected static enum Operation {

        WAIT, MOVE_TO, STRAFE, JUMPING;

        private Operation() {}
    }
}
