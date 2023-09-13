package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import java.util.Iterator;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.phys.Vec3D;

public class PathfinderGoalBreath extends PathfinderGoal {

    private final EntityCreature mob;

    public PathfinderGoalBreath(EntityCreature entitycreature) {
        this.mob = entitycreature;
        this.setFlags(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
    }

    @Override
    public boolean canUse() {
        return this.mob.getAirSupply() < 140;
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public void start() {
        this.findAirPosition();
    }

    private void findAirPosition() {
        Iterable<BlockPosition> iterable = BlockPosition.betweenClosed(MathHelper.floor(this.mob.getX() - 1.0D), this.mob.getBlockY(), MathHelper.floor(this.mob.getZ() - 1.0D), MathHelper.floor(this.mob.getX() + 1.0D), MathHelper.floor(this.mob.getY() + 8.0D), MathHelper.floor(this.mob.getZ() + 1.0D));
        BlockPosition blockposition = null;
        Iterator iterator = iterable.iterator();

        while (iterator.hasNext()) {
            BlockPosition blockposition1 = (BlockPosition) iterator.next();

            if (this.givesAir(this.mob.level, blockposition1)) {
                blockposition = blockposition1;
                break;
            }
        }

        if (blockposition == null) {
            blockposition = new BlockPosition(this.mob.getX(), this.mob.getY() + 8.0D, this.mob.getZ());
        }

        this.mob.getNavigation().moveTo((double) blockposition.getX(), (double) (blockposition.getY() + 1), (double) blockposition.getZ(), 1.0D);
    }

    @Override
    public void tick() {
        this.findAirPosition();
        this.mob.moveRelative(0.02F, new Vec3D((double) this.mob.xxa, (double) this.mob.yya, (double) this.mob.zza));
        this.mob.move(EnumMoveType.SELF, this.mob.getDeltaMovement());
    }

    private boolean givesAir(IWorldReader iworldreader, BlockPosition blockposition) {
        IBlockData iblockdata = iworldreader.getBlockState(blockposition);

        return (iworldreader.getFluidState(blockposition).isEmpty() || iblockdata.is(Blocks.BUBBLE_COLUMN)) && iblockdata.isPathfindable(iworldreader, blockposition, PathMode.LAND);
    }
}
