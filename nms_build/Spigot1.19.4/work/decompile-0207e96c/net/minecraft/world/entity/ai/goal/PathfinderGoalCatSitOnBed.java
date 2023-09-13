package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.core.BlockPosition;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.animal.EntityCat;
import net.minecraft.world.level.IWorldReader;

public class PathfinderGoalCatSitOnBed extends PathfinderGoalGotoTarget {

    private final EntityCat cat;

    public PathfinderGoalCatSitOnBed(EntityCat entitycat, double d0, int i) {
        super(entitycat, d0, i, 6);
        this.cat = entitycat;
        this.verticalSearchStart = -2;
        this.setFlags(EnumSet.of(PathfinderGoal.Type.JUMP, PathfinderGoal.Type.MOVE));
    }

    @Override
    public boolean canUse() {
        return this.cat.isTame() && !this.cat.isOrderedToSit() && !this.cat.isLying() && super.canUse();
    }

    @Override
    public void start() {
        super.start();
        this.cat.setInSittingPose(false);
    }

    @Override
    protected int nextStartTick(EntityCreature entitycreature) {
        return 40;
    }

    @Override
    public void stop() {
        super.stop();
        this.cat.setLying(false);
    }

    @Override
    public void tick() {
        super.tick();
        this.cat.setInSittingPose(false);
        if (!this.isReachedTarget()) {
            this.cat.setLying(false);
        } else if (!this.cat.isLying()) {
            this.cat.setLying(true);
        }

    }

    @Override
    protected boolean isValidTarget(IWorldReader iworldreader, BlockPosition blockposition) {
        return iworldreader.isEmptyBlock(blockposition.above()) && iworldreader.getBlockState(blockposition).is(TagsBlock.BEDS);
    }
}
