package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import java.util.function.Predicate;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.gameevent.GameEvent;

public class PathfinderGoalEatTile extends PathfinderGoal {

    private static final int EAT_ANIMATION_TICKS = 40;
    private static final Predicate<IBlockData> IS_TALL_GRASS = BlockStatePredicate.forBlock(Blocks.GRASS);
    private final EntityInsentient mob;
    private final World level;
    private int eatAnimationTick;

    public PathfinderGoalEatTile(EntityInsentient entityinsentient) {
        this.mob = entityinsentient;
        this.level = entityinsentient.level;
        this.setFlags(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK, PathfinderGoal.Type.JUMP));
    }

    @Override
    public boolean canUse() {
        if (this.mob.getRandom().nextInt(this.mob.isBaby() ? 50 : 1000) != 0) {
            return false;
        } else {
            BlockPosition blockposition = this.mob.blockPosition();

            return PathfinderGoalEatTile.IS_TALL_GRASS.test(this.level.getBlockState(blockposition)) ? true : this.level.getBlockState(blockposition.below()).is(Blocks.GRASS_BLOCK);
        }
    }

    @Override
    public void start() {
        this.eatAnimationTick = this.adjustedTickDelay(40);
        this.level.broadcastEntityEvent(this.mob, (byte) 10);
        this.mob.getNavigation().stop();
    }

    @Override
    public void stop() {
        this.eatAnimationTick = 0;
    }

    @Override
    public boolean canContinueToUse() {
        return this.eatAnimationTick > 0;
    }

    public int getEatAnimationTick() {
        return this.eatAnimationTick;
    }

    @Override
    public void tick() {
        this.eatAnimationTick = Math.max(0, this.eatAnimationTick - 1);
        if (this.eatAnimationTick == this.adjustedTickDelay(4)) {
            BlockPosition blockposition = this.mob.blockPosition();

            if (PathfinderGoalEatTile.IS_TALL_GRASS.test(this.level.getBlockState(blockposition))) {
                if (this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                    this.level.destroyBlock(blockposition, false);
                }

                this.mob.ate();
                this.mob.gameEvent(GameEvent.EAT, this.mob.eyeBlockPosition());
            } else {
                BlockPosition blockposition1 = blockposition.below();

                if (this.level.getBlockState(blockposition1).is(Blocks.GRASS_BLOCK)) {
                    if (this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                        this.level.levelEvent(2001, blockposition1, Block.getId(Blocks.GRASS_BLOCK.defaultBlockState()));
                        this.level.setBlock(blockposition1, Blocks.DIRT.defaultBlockState(), 2);
                    }

                    this.mob.ate();
                    this.mob.gameEvent(GameEvent.EAT, this.mob.eyeBlockPosition());
                }
            }

        }
    }
}
