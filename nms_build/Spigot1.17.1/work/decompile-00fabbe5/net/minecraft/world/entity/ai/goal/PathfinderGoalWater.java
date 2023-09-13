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
    public boolean a() {
        return this.mob.isOnGround() && !this.mob.level.getFluid(this.mob.getChunkCoordinates()).a((Tag) TagsFluid.WATER);
    }

    @Override
    public void c() {
        BlockPosition blockposition = null;
        Iterable<BlockPosition> iterable = BlockPosition.b(MathHelper.floor(this.mob.locX() - 2.0D), MathHelper.floor(this.mob.locY() - 2.0D), MathHelper.floor(this.mob.locZ() - 2.0D), MathHelper.floor(this.mob.locX() + 2.0D), this.mob.cY(), MathHelper.floor(this.mob.locZ() + 2.0D));
        Iterator iterator = iterable.iterator();

        while (iterator.hasNext()) {
            BlockPosition blockposition1 = (BlockPosition) iterator.next();

            if (this.mob.level.getFluid(blockposition1).a((Tag) TagsFluid.WATER)) {
                blockposition = blockposition1;
                break;
            }
        }

        if (blockposition != null) {
            this.mob.getControllerMove().a((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), 1.0D);
        }

    }
}
