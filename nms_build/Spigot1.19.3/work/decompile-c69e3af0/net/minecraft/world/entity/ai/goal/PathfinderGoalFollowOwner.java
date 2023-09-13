package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTameableAnimal;
import net.minecraft.world.entity.ai.navigation.Navigation;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.entity.ai.navigation.NavigationFlying;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.block.BlockLeaves;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfinderNormal;

public class PathfinderGoalFollowOwner extends PathfinderGoal {

    public static final int TELEPORT_WHEN_DISTANCE_IS = 12;
    private static final int MIN_HORIZONTAL_DISTANCE_FROM_PLAYER_WHEN_TELEPORTING = 2;
    private static final int MAX_HORIZONTAL_DISTANCE_FROM_PLAYER_WHEN_TELEPORTING = 3;
    private static final int MAX_VERTICAL_DISTANCE_FROM_PLAYER_WHEN_TELEPORTING = 1;
    private final EntityTameableAnimal tamable;
    private EntityLiving owner;
    private final IWorldReader level;
    private final double speedModifier;
    private final NavigationAbstract navigation;
    private int timeToRecalcPath;
    private final float stopDistance;
    private final float startDistance;
    private float oldWaterCost;
    private final boolean canFly;

    public PathfinderGoalFollowOwner(EntityTameableAnimal entitytameableanimal, double d0, float f, float f1, boolean flag) {
        this.tamable = entitytameableanimal;
        this.level = entitytameableanimal.level;
        this.speedModifier = d0;
        this.navigation = entitytameableanimal.getNavigation();
        this.startDistance = f;
        this.stopDistance = f1;
        this.canFly = flag;
        this.setFlags(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
        if (!(entitytameableanimal.getNavigation() instanceof Navigation) && !(entitytameableanimal.getNavigation() instanceof NavigationFlying)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
        }
    }

    @Override
    public boolean canUse() {
        EntityLiving entityliving = this.tamable.getOwner();

        if (entityliving == null) {
            return false;
        } else if (entityliving.isSpectator()) {
            return false;
        } else if (this.tamable.isOrderedToSit()) {
            return false;
        } else if (this.tamable.distanceToSqr((Entity) entityliving) < (double) (this.startDistance * this.startDistance)) {
            return false;
        } else {
            this.owner = entityliving;
            return true;
        }
    }

    @Override
    public boolean canContinueToUse() {
        return this.navigation.isDone() ? false : (this.tamable.isOrderedToSit() ? false : this.tamable.distanceToSqr((Entity) this.owner) > (double) (this.stopDistance * this.stopDistance));
    }

    @Override
    public void start() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.tamable.getPathfindingMalus(PathType.WATER);
        this.tamable.setPathfindingMalus(PathType.WATER, 0.0F);
    }

    @Override
    public void stop() {
        this.owner = null;
        this.navigation.stop();
        this.tamable.setPathfindingMalus(PathType.WATER, this.oldWaterCost);
    }

    @Override
    public void tick() {
        this.tamable.getLookControl().setLookAt(this.owner, 10.0F, (float) this.tamable.getMaxHeadXRot());
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(10);
            if (!this.tamable.isLeashed() && !this.tamable.isPassenger()) {
                if (this.tamable.distanceToSqr((Entity) this.owner) >= 144.0D) {
                    this.teleportToOwner();
                } else {
                    this.navigation.moveTo((Entity) this.owner, this.speedModifier);
                }

            }
        }
    }

    private void teleportToOwner() {
        BlockPosition blockposition = this.owner.blockPosition();

        for (int i = 0; i < 10; ++i) {
            int j = this.randomIntInclusive(-3, 3);
            int k = this.randomIntInclusive(-1, 1);
            int l = this.randomIntInclusive(-3, 3);
            boolean flag = this.maybeTeleportTo(blockposition.getX() + j, blockposition.getY() + k, blockposition.getZ() + l);

            if (flag) {
                return;
            }
        }

    }

    private boolean maybeTeleportTo(int i, int j, int k) {
        if (Math.abs((double) i - this.owner.getX()) < 2.0D && Math.abs((double) k - this.owner.getZ()) < 2.0D) {
            return false;
        } else if (!this.canTeleportTo(new BlockPosition(i, j, k))) {
            return false;
        } else {
            this.tamable.moveTo((double) i + 0.5D, (double) j, (double) k + 0.5D, this.tamable.getYRot(), this.tamable.getXRot());
            this.navigation.stop();
            return true;
        }
    }

    private boolean canTeleportTo(BlockPosition blockposition) {
        PathType pathtype = PathfinderNormal.getBlockPathTypeStatic(this.level, blockposition.mutable());

        if (pathtype != PathType.WALKABLE) {
            return false;
        } else {
            IBlockData iblockdata = this.level.getBlockState(blockposition.below());

            if (!this.canFly && iblockdata.getBlock() instanceof BlockLeaves) {
                return false;
            } else {
                BlockPosition blockposition1 = blockposition.subtract(this.tamable.blockPosition());

                return this.level.noCollision(this.tamable, this.tamable.getBoundingBox().move(blockposition1));
            }
        }
    }

    private int randomIntInclusive(int i, int j) {
        return this.tamable.getRandom().nextInt(j - i + 1) + i;
    }
}
