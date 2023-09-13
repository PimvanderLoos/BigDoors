package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IPosition;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.level.IWorldReader;

public abstract class PathfinderGoalGotoTarget extends PathfinderGoal {

    private static final int GIVE_UP_TICKS = 1200;
    private static final int STAY_TICKS = 1200;
    private static final int INTERVAL_TICKS = 200;
    protected final EntityCreature mob;
    public final double speedModifier;
    protected int nextStartTick;
    protected int tryTicks;
    private int maxStayTicks;
    protected BlockPosition blockPos;
    private boolean reachedTarget;
    private final int searchRange;
    private final int verticalSearchRange;
    protected int verticalSearchStart;

    public PathfinderGoalGotoTarget(EntityCreature entitycreature, double d0, int i) {
        this(entitycreature, d0, i, 1);
    }

    public PathfinderGoalGotoTarget(EntityCreature entitycreature, double d0, int i, int j) {
        this.blockPos = BlockPosition.ZERO;
        this.mob = entitycreature;
        this.speedModifier = d0;
        this.searchRange = i;
        this.verticalSearchStart = 0;
        this.verticalSearchRange = j;
        this.setFlags(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.JUMP));
    }

    @Override
    public boolean canUse() {
        if (this.nextStartTick > 0) {
            --this.nextStartTick;
            return false;
        } else {
            this.nextStartTick = this.nextStartTick(this.mob);
            return this.findNearestBlock();
        }
    }

    protected int nextStartTick(EntityCreature entitycreature) {
        return reducedTickDelay(200 + entitycreature.getRandom().nextInt(200));
    }

    @Override
    public boolean canContinueToUse() {
        return this.tryTicks >= -this.maxStayTicks && this.tryTicks <= 1200 && this.isValidTarget(this.mob.level, this.blockPos);
    }

    @Override
    public void start() {
        this.moveMobToBlock();
        this.tryTicks = 0;
        this.maxStayTicks = this.mob.getRandom().nextInt(this.mob.getRandom().nextInt(1200) + 1200) + 1200;
    }

    protected void moveMobToBlock() {
        this.mob.getNavigation().moveTo((double) ((float) this.blockPos.getX()) + 0.5D, (double) (this.blockPos.getY() + 1), (double) ((float) this.blockPos.getZ()) + 0.5D, this.speedModifier);
    }

    public double acceptedDistance() {
        return 1.0D;
    }

    protected BlockPosition getMoveToTarget() {
        return this.blockPos.above();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        BlockPosition blockposition = this.getMoveToTarget();

        if (!blockposition.closerThan((IPosition) this.mob.position(), this.acceptedDistance())) {
            this.reachedTarget = false;
            ++this.tryTicks;
            if (this.shouldRecalculatePath()) {
                this.mob.getNavigation().moveTo((double) ((float) blockposition.getX()) + 0.5D, (double) blockposition.getY(), (double) ((float) blockposition.getZ()) + 0.5D, this.speedModifier);
            }
        } else {
            this.reachedTarget = true;
            --this.tryTicks;
        }

    }

    public boolean shouldRecalculatePath() {
        return this.tryTicks % 40 == 0;
    }

    protected boolean isReachedTarget() {
        return this.reachedTarget;
    }

    protected boolean findNearestBlock() {
        int i = this.searchRange;
        int j = this.verticalSearchRange;
        BlockPosition blockposition = this.mob.blockPosition();
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (int k = this.verticalSearchStart; k <= j; k = k > 0 ? -k : 1 - k) {
            for (int l = 0; l < i; ++l) {
                for (int i1 = 0; i1 <= l; i1 = i1 > 0 ? -i1 : 1 - i1) {
                    for (int j1 = i1 < l && i1 > -l ? l : 0; j1 <= l; j1 = j1 > 0 ? -j1 : 1 - j1) {
                        blockposition_mutableblockposition.setWithOffset(blockposition, i1, k - 1, j1);
                        if (this.mob.isWithinRestriction(blockposition_mutableblockposition) && this.isValidTarget(this.mob.level, blockposition_mutableblockposition)) {
                            this.blockPos = blockposition_mutableblockposition;
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    protected abstract boolean isValidTarget(IWorldReader iworldreader, BlockPosition blockposition);
}
