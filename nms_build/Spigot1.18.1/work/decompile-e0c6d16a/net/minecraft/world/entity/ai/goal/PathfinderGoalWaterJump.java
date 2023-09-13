package net.minecraft.world.entity.ai.goal;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsFluid;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.animal.EntityDolphin;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3D;

public class PathfinderGoalWaterJump extends PathfinderGoalWaterJumpAbstract {

    private static final int[] STEPS_TO_CHECK = new int[]{0, 1, 4, 5, 6, 7};
    private final EntityDolphin dolphin;
    private final int interval;
    private boolean breached;

    public PathfinderGoalWaterJump(EntityDolphin entitydolphin, int i) {
        this.dolphin = entitydolphin;
        this.interval = reducedTickDelay(i);
    }

    @Override
    public boolean canUse() {
        if (this.dolphin.getRandom().nextInt(this.interval) != 0) {
            return false;
        } else {
            EnumDirection enumdirection = this.dolphin.getMotionDirection();
            int i = enumdirection.getStepX();
            int j = enumdirection.getStepZ();
            BlockPosition blockposition = this.dolphin.blockPosition();
            int[] aint = PathfinderGoalWaterJump.STEPS_TO_CHECK;
            int k = aint.length;

            for (int l = 0; l < k; ++l) {
                int i1 = aint[l];

                if (!this.waterIsClear(blockposition, i, j, i1) || !this.surfaceIsClear(blockposition, i, j, i1)) {
                    return false;
                }
            }

            return true;
        }
    }

    private boolean waterIsClear(BlockPosition blockposition, int i, int j, int k) {
        BlockPosition blockposition1 = blockposition.offset(i * k, 0, j * k);

        return this.dolphin.level.getFluidState(blockposition1).is((Tag) TagsFluid.WATER) && !this.dolphin.level.getBlockState(blockposition1).getMaterial().blocksMotion();
    }

    private boolean surfaceIsClear(BlockPosition blockposition, int i, int j, int k) {
        return this.dolphin.level.getBlockState(blockposition.offset(i * k, 1, j * k)).isAir() && this.dolphin.level.getBlockState(blockposition.offset(i * k, 2, j * k)).isAir();
    }

    @Override
    public boolean canContinueToUse() {
        double d0 = this.dolphin.getDeltaMovement().y;

        return (d0 * d0 >= 0.029999999329447746D || this.dolphin.getXRot() == 0.0F || Math.abs(this.dolphin.getXRot()) >= 10.0F || !this.dolphin.isInWater()) && !this.dolphin.isOnGround();
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public void start() {
        EnumDirection enumdirection = this.dolphin.getMotionDirection();

        this.dolphin.setDeltaMovement(this.dolphin.getDeltaMovement().add((double) enumdirection.getStepX() * 0.6D, 0.7D, (double) enumdirection.getStepZ() * 0.6D));
        this.dolphin.getNavigation().stop();
    }

    @Override
    public void stop() {
        this.dolphin.setXRot(0.0F);
    }

    @Override
    public void tick() {
        boolean flag = this.breached;

        if (!flag) {
            Fluid fluid = this.dolphin.level.getFluidState(this.dolphin.blockPosition());

            this.breached = fluid.is((Tag) TagsFluid.WATER);
        }

        if (this.breached && !flag) {
            this.dolphin.playSound(SoundEffects.DOLPHIN_JUMP, 1.0F, 1.0F);
        }

        Vec3D vec3d = this.dolphin.getDeltaMovement();

        if (vec3d.y * vec3d.y < 0.029999999329447746D && this.dolphin.getXRot() != 0.0F) {
            this.dolphin.setXRot(MathHelper.rotlerp(this.dolphin.getXRot(), 0.0F, 0.2F));
        } else if (vec3d.length() > 9.999999747378752E-6D) {
            double d0 = vec3d.horizontalDistance();
            double d1 = Math.atan2(-vec3d.y, d0) * 57.2957763671875D;

            this.dolphin.setXRot((float) d1);
        }

    }
}
