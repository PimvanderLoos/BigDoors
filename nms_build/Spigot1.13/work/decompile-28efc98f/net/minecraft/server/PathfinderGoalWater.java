package net.minecraft.server;

import java.util.Iterator;

public class PathfinderGoalWater extends PathfinderGoal {

    private final EntityCreature a;

    public PathfinderGoalWater(EntityCreature entitycreature) {
        this.a = entitycreature;
    }

    public boolean a() {
        return this.a.onGround && !this.a.world.b(new BlockPosition(this.a)).a(TagsFluid.a);
    }

    public void c() {
        BlockPosition blockposition = null;
        Iterable iterable = BlockPosition.MutableBlockPosition.b(MathHelper.floor(this.a.locX - 2.0D), MathHelper.floor(this.a.locY - 2.0D), MathHelper.floor(this.a.locZ - 2.0D), MathHelper.floor(this.a.locX + 2.0D), MathHelper.floor(this.a.locY), MathHelper.floor(this.a.locZ + 2.0D));
        Iterator iterator = iterable.iterator();

        while (iterator.hasNext()) {
            BlockPosition blockposition1 = (BlockPosition) iterator.next();

            if (this.a.world.b(blockposition1).a(TagsFluid.a)) {
                blockposition = blockposition1;
                break;
            }
        }

        if (blockposition != null) {
            this.a.getControllerMove().a((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), 1.0D);
        }

    }
}
