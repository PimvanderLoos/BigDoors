package net.minecraft.world.entity.ai.goal;

import java.util.Iterator;
import net.minecraft.core.BlockPosition;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsFluid;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.EntityCreature;

public class PathfinderGoalWater extends PathfinderGoal {

    private final EntityCreature mob;

    public PathfinderGoalWater(EntityCreature entitycreature) {
        this.mob = entitycreature;
    }

    @Override
    public boolean canUse() {
        return this.mob.isOnGround() && !this.mob.level.getFluidState(this.mob.blockPosition()).is((Tag) TagsFluid.WATER);
    }

    @Override
    public void start() {
        BlockPosition blockposition = null;
        Iterable<BlockPosition> iterable = BlockPosition.betweenClosed(MathHelper.floor(this.mob.getX() - 2.0D), MathHelper.floor(this.mob.getY() - 2.0D), MathHelper.floor(this.mob.getZ() - 2.0D), MathHelper.floor(this.mob.getX() + 2.0D), this.mob.getBlockY(), MathHelper.floor(this.mob.getZ() + 2.0D));
        Iterator iterator = iterable.iterator();

        while (iterator.hasNext()) {
            BlockPosition blockposition1 = (BlockPosition) iterator.next();

            if (this.mob.level.getFluidState(blockposition1).is((Tag) TagsFluid.WATER)) {
                blockposition = blockposition1;
                break;
            }
        }

        if (blockposition != null) {
            this.mob.getMoveControl().setWantedPosition((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), 1.0D);
        }

    }
}
