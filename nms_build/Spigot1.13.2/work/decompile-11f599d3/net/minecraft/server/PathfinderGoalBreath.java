package net.minecraft.server;

import java.util.Iterator;

public class PathfinderGoalBreath extends PathfinderGoal {

    private final EntityCreature a;

    public PathfinderGoalBreath(EntityCreature entitycreature) {
        this.a = entitycreature;
        this.a(3);
    }

    public boolean a() {
        return this.a.getAirTicks() < 140;
    }

    public boolean b() {
        return this.a();
    }

    public boolean f() {
        return false;
    }

    public void c() {
        this.g();
    }

    private void g() {
        Iterable<BlockPosition.MutableBlockPosition> iterable = BlockPosition.MutableBlockPosition.b(MathHelper.floor(this.a.locX - 1.0D), MathHelper.floor(this.a.locY), MathHelper.floor(this.a.locZ - 1.0D), MathHelper.floor(this.a.locX + 1.0D), MathHelper.floor(this.a.locY + 8.0D), MathHelper.floor(this.a.locZ + 1.0D));
        BlockPosition blockposition = null;
        Iterator iterator = iterable.iterator();

        while (iterator.hasNext()) {
            BlockPosition blockposition1 = (BlockPosition) iterator.next();

            if (this.a(this.a.world, blockposition1)) {
                blockposition = blockposition1;
                break;
            }
        }

        if (blockposition == null) {
            blockposition = new BlockPosition(this.a.locX, this.a.locY + 8.0D, this.a.locZ);
        }

        this.a.getNavigation().a((double) blockposition.getX(), (double) (blockposition.getY() + 1), (double) blockposition.getZ(), 1.0D);
    }

    public void e() {
        this.g();
        this.a.a(this.a.bh, this.a.bi, this.a.bj, 0.02F);
        this.a.move(EnumMoveType.SELF, this.a.motX, this.a.motY, this.a.motZ);
    }

    private boolean a(IWorldReader iworldreader, BlockPosition blockposition) {
        IBlockData iblockdata = iworldreader.getType(blockposition);

        return (iworldreader.getFluid(blockposition).e() || iblockdata.getBlock() == Blocks.BUBBLE_COLUMN) && iblockdata.a((IBlockAccess) iworldreader, blockposition, PathMode.LAND);
    }
}
