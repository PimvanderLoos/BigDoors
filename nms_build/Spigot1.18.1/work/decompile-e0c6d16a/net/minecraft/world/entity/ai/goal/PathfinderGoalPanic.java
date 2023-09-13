package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsFluid;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.phys.Vec3D;

public class PathfinderGoalPanic extends PathfinderGoal {

    public static final int WATER_CHECK_DISTANCE_VERTICAL = 1;
    protected final EntityCreature mob;
    protected final double speedModifier;
    protected double posX;
    protected double posY;
    protected double posZ;
    protected boolean isRunning;

    public PathfinderGoalPanic(EntityCreature entitycreature, double d0) {
        this.mob = entitycreature;
        this.speedModifier = d0;
        this.setFlags(EnumSet.of(PathfinderGoal.Type.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.mob.getLastHurtByMob() == null && !this.mob.isOnFire()) {
            return false;
        } else {
            if (this.mob.isOnFire()) {
                BlockPosition blockposition = this.lookForWater(this.mob.level, this.mob, 5);

                if (blockposition != null) {
                    this.posX = (double) blockposition.getX();
                    this.posY = (double) blockposition.getY();
                    this.posZ = (double) blockposition.getZ();
                    return true;
                }
            }

            return this.findRandomPosition();
        }
    }

    protected boolean findRandomPosition() {
        Vec3D vec3d = DefaultRandomPos.getPos(this.mob, 5, 4);

        if (vec3d == null) {
            return false;
        } else {
            this.posX = vec3d.x;
            this.posY = vec3d.y;
            this.posZ = vec3d.z;
            return true;
        }
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.posX, this.posY, this.posZ, this.speedModifier);
        this.isRunning = true;
    }

    @Override
    public void stop() {
        this.isRunning = false;
    }

    @Override
    public boolean canContinueToUse() {
        return !this.mob.getNavigation().isDone();
    }

    @Nullable
    protected BlockPosition lookForWater(IBlockAccess iblockaccess, Entity entity, int i) {
        BlockPosition blockposition = entity.blockPosition();

        return !iblockaccess.getBlockState(blockposition).getCollisionShape(iblockaccess, blockposition).isEmpty() ? null : (BlockPosition) BlockPosition.findClosestMatch(entity.blockPosition(), i, 1, (blockposition1) -> {
            return iblockaccess.getFluidState(blockposition1).is((Tag) TagsFluid.WATER);
        }).orElse((Object) null);
    }
}
