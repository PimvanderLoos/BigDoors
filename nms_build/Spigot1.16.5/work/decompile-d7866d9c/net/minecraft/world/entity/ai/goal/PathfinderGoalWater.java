package net.minecraft.world.entity.ai.goal;

import java.util.Iterator;
import net.minecraft.core.BlockPosition;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsFluid;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.EntityCreature;

public class PathfinderGoalWater extends PathfinderGoal {

    private final EntityCreature a;

    public PathfinderGoalWater(EntityCreature entitycreature) {
        this.a = entitycreature;
    }

    @Override
    public boolean a() {
        return this.a.isOnGround() && !this.a.world.getFluid(this.a.getChunkCoordinates()).a((Tag) TagsFluid.WATER);
    }

    @Override
    public void c() {
        BlockPosition blockposition = null;
        Iterable<BlockPosition> iterable = BlockPosition.b(MathHelper.floor(this.a.locX() - 2.0D), MathHelper.floor(this.a.locY() - 2.0D), MathHelper.floor(this.a.locZ() - 2.0D), MathHelper.floor(this.a.locX() + 2.0D), MathHelper.floor(this.a.locY()), MathHelper.floor(this.a.locZ() + 2.0D));
        Iterator iterator = iterable.iterator();

        while (iterator.hasNext()) {
            BlockPosition blockposition1 = (BlockPosition) iterator.next();

            if (this.a.world.getFluid(blockposition1).a((Tag) TagsFluid.WATER)) {
                blockposition = blockposition1;
                break;
            }
        }

        if (blockposition != null) {
            this.a.getControllerMove().a((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), 1.0D);
        }

    }
}
