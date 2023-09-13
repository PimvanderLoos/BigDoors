package net.minecraft.world.entity.ai.navigation;

import net.minecraft.core.BlockPosition;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.pathfinder.PathEntity;
import net.minecraft.world.level.pathfinder.PathPoint;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.Pathfinder;
import net.minecraft.world.level.pathfinder.PathfinderNormal;
import net.minecraft.world.phys.Vec3D;

public class Navigation extends NavigationAbstract {

    private boolean avoidSun;

    public Navigation(EntityInsentient entityinsentient, World world) {
        super(entityinsentient, world);
    }

    @Override
    protected Pathfinder createPathFinder(int i) {
        this.nodeEvaluator = new PathfinderNormal();
        this.nodeEvaluator.setCanPassDoors(true);
        return new Pathfinder(this.nodeEvaluator, i);
    }

    @Override
    protected boolean canUpdatePath() {
        return this.mob.isOnGround() || this.isInLiquid() || this.mob.isPassenger();
    }

    @Override
    protected Vec3D getTempMobPos() {
        return new Vec3D(this.mob.getX(), (double) this.getSurfaceY(), this.mob.getZ());
    }

    @Override
    public PathEntity createPath(BlockPosition blockposition, int i) {
        BlockPosition blockposition1;

        if (this.level.getBlockState(blockposition).isAir()) {
            for (blockposition1 = blockposition.below(); blockposition1.getY() > this.level.getMinBuildHeight() && this.level.getBlockState(blockposition1).isAir(); blockposition1 = blockposition1.below()) {
                ;
            }

            if (blockposition1.getY() > this.level.getMinBuildHeight()) {
                return super.createPath(blockposition1.above(), i);
            }

            while (blockposition1.getY() < this.level.getMaxBuildHeight() && this.level.getBlockState(blockposition1).isAir()) {
                blockposition1 = blockposition1.above();
            }

            blockposition = blockposition1;
        }

        if (!this.level.getBlockState(blockposition).getMaterial().isSolid()) {
            return super.createPath(blockposition, i);
        } else {
            for (blockposition1 = blockposition.above(); blockposition1.getY() < this.level.getMaxBuildHeight() && this.level.getBlockState(blockposition1).getMaterial().isSolid(); blockposition1 = blockposition1.above()) {
                ;
            }

            return super.createPath(blockposition1, i);
        }
    }

    @Override
    public PathEntity createPath(Entity entity, int i) {
        return this.createPath(entity.blockPosition(), i);
    }

    private int getSurfaceY() {
        if (this.mob.isInWater() && this.canFloat()) {
            int i = this.mob.getBlockY();
            IBlockData iblockdata = this.level.getBlockState(new BlockPosition(this.mob.getX(), (double) i, this.mob.getZ()));
            int j = 0;

            do {
                if (!iblockdata.is(Blocks.WATER)) {
                    return i;
                }

                ++i;
                iblockdata = this.level.getBlockState(new BlockPosition(this.mob.getX(), (double) i, this.mob.getZ()));
                ++j;
            } while (j <= 16);

            return this.mob.getBlockY();
        } else {
            return MathHelper.floor(this.mob.getY() + 0.5D);
        }
    }

    @Override
    protected void trimPath() {
        super.trimPath();
        if (this.avoidSun) {
            if (this.level.canSeeSky(new BlockPosition(this.mob.getX(), this.mob.getY() + 0.5D, this.mob.getZ()))) {
                return;
            }

            for (int i = 0; i < this.path.getNodeCount(); ++i) {
                PathPoint pathpoint = this.path.getNode(i);

                if (this.level.canSeeSky(new BlockPosition(pathpoint.x, pathpoint.y, pathpoint.z))) {
                    this.path.truncateNodes(i);
                    return;
                }
            }
        }

    }

    protected boolean hasValidPathType(PathType pathtype) {
        return pathtype == PathType.WATER ? false : (pathtype == PathType.LAVA ? false : pathtype != PathType.OPEN);
    }

    public void setCanOpenDoors(boolean flag) {
        this.nodeEvaluator.setCanOpenDoors(flag);
    }

    public boolean canPassDoors() {
        return this.nodeEvaluator.canPassDoors();
    }

    public void setCanPassDoors(boolean flag) {
        this.nodeEvaluator.setCanPassDoors(flag);
    }

    public boolean canOpenDoors() {
        return this.nodeEvaluator.canPassDoors();
    }

    public void setAvoidSun(boolean flag) {
        this.avoidSun = flag;
    }
}
