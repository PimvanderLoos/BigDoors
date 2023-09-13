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
    private static final Predicate<IBlockData> IS_TALL_GRASS = BlockStatePredicate.a(Blocks.GRASS);
    private final EntityInsentient mob;
    private final World level;
    private int eatAnimationTick;

    public PathfinderGoalEatTile(EntityInsentient entityinsentient) {
        this.mob = entityinsentient;
        this.level = entityinsentient.level;
        this.a(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK, PathfinderGoal.Type.JUMP));
    }

    @Override
    public boolean a() {
        if (this.mob.getRandom().nextInt(this.mob.isBaby() ? 50 : 1000) != 0) {
            return false;
        } else {
            BlockPosition blockposition = this.mob.getChunkCoordinates();

            return PathfinderGoalEatTile.IS_TALL_GRASS.test(this.level.getType(blockposition)) ? true : this.level.getType(blockposition.down()).a(Blocks.GRASS_BLOCK);
        }
    }

    @Override
    public void c() {
        this.eatAnimationTick = 40;
        this.level.broadcastEntityEffect(this.mob, (byte) 10);
        this.mob.getNavigation().o();
    }

    @Override
    public void d() {
        this.eatAnimationTick = 0;
    }

    @Override
    public boolean b() {
        return this.eatAnimationTick > 0;
    }

    public int g() {
        return this.eatAnimationTick;
    }

    @Override
    public void e() {
        this.eatAnimationTick = Math.max(0, this.eatAnimationTick - 1);
        if (this.eatAnimationTick == 4) {
            BlockPosition blockposition = this.mob.getChunkCoordinates();

            if (PathfinderGoalEatTile.IS_TALL_GRASS.test(this.level.getType(blockposition))) {
                if (this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                    this.level.b(blockposition, false);
                }

                this.mob.blockEaten();
                this.mob.a(GameEvent.EAT, this.mob.cT());
            } else {
                BlockPosition blockposition1 = blockposition.down();

                if (this.level.getType(blockposition1).a(Blocks.GRASS_BLOCK)) {
                    if (this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                        this.level.triggerEffect(2001, blockposition1, Block.getCombinedId(Blocks.GRASS_BLOCK.getBlockData()));
                        this.level.setTypeAndData(blockposition1, Blocks.DIRT.getBlockData(), 2);
                    }

                    this.mob.blockEaten();
                    this.mob.a(GameEvent.EAT, this.mob.cT());
                }
            }

        }
    }
}
