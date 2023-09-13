package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;

public class PathfinderGoalFleeSun extends PathfinderGoal {

    protected final EntityCreature mob;
    private double wantedX;
    private double wantedY;
    private double wantedZ;
    private final double speedModifier;
    private final World level;

    public PathfinderGoalFleeSun(EntityCreature entitycreature, double d0) {
        this.mob = entitycreature;
        this.speedModifier = d0;
        this.level = entitycreature.level;
        this.setFlags(EnumSet.of(PathfinderGoal.Type.MOVE));
    }

    @Override
    public boolean canUse() {
        return this.mob.getTarget() != null ? false : (!this.level.isDay() ? false : (!this.mob.isOnFire() ? false : (!this.level.canSeeSky(this.mob.blockPosition()) ? false : (!this.mob.getItemBySlot(EnumItemSlot.HEAD).isEmpty() ? false : this.setWantedPos()))));
    }

    protected boolean setWantedPos() {
        Vec3D vec3d = this.getHidePos();

        if (vec3d == null) {
            return false;
        } else {
            this.wantedX = vec3d.x;
            this.wantedY = vec3d.y;
            this.wantedZ = vec3d.z;
            return true;
        }
    }

    @Override
    public boolean canContinueToUse() {
        return !this.mob.getNavigation().isDone();
    }

    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
    }

    @Nullable
    protected Vec3D getHidePos() {
        Random random = this.mob.getRandom();
        BlockPosition blockposition = this.mob.blockPosition();

        for (int i = 0; i < 10; ++i) {
            BlockPosition blockposition1 = blockposition.offset(random.nextInt(20) - 10, random.nextInt(6) - 3, random.nextInt(20) - 10);

            if (!this.level.canSeeSky(blockposition1) && this.mob.getWalkTargetValue(blockposition1) < 0.0F) {
                return Vec3D.atBottomCenterOf(blockposition1);
            }
        }

        return null;
    }
}
