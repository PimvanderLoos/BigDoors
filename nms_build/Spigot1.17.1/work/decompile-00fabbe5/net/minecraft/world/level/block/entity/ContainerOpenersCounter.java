package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AxisAlignedBB;

public abstract class ContainerOpenersCounter {

    private static final int CHECK_TICK_DELAY = 5;
    private int openCount;

    public ContainerOpenersCounter() {}

    protected abstract void a(World world, BlockPosition blockposition, IBlockData iblockdata);

    protected abstract void b(World world, BlockPosition blockposition, IBlockData iblockdata);

    protected abstract void a(World world, BlockPosition blockposition, IBlockData iblockdata, int i, int j);

    protected abstract boolean a(EntityHuman entityhuman);

    public void a(EntityHuman entityhuman, World world, BlockPosition blockposition, IBlockData iblockdata) {
        int i = this.openCount++;

        if (i == 0) {
            this.a(world, blockposition, iblockdata);
            world.a((Entity) entityhuman, GameEvent.CONTAINER_OPEN, blockposition);
            d(world, blockposition, iblockdata);
        }

        this.a(world, blockposition, iblockdata, i, this.openCount);
    }

    public void b(EntityHuman entityhuman, World world, BlockPosition blockposition, IBlockData iblockdata) {
        int i = this.openCount--;

        if (this.openCount == 0) {
            this.b(world, blockposition, iblockdata);
            world.a((Entity) entityhuman, GameEvent.CONTAINER_CLOSE, blockposition);
        }

        this.a(world, blockposition, iblockdata, i, this.openCount);
    }

    private int a(World world, BlockPosition blockposition) {
        int i = blockposition.getX();
        int j = blockposition.getY();
        int k = blockposition.getZ();
        float f = 5.0F;
        AxisAlignedBB axisalignedbb = new AxisAlignedBB((double) ((float) i - 5.0F), (double) ((float) j - 5.0F), (double) ((float) k - 5.0F), (double) ((float) (i + 1) + 5.0F), (double) ((float) (j + 1) + 5.0F), (double) ((float) (k + 1) + 5.0F));

        return world.a(EntityTypeTest.a(EntityHuman.class), axisalignedbb, this::a).size();
    }

    public void c(World world, BlockPosition blockposition, IBlockData iblockdata) {
        int i = this.a(world, blockposition);
        int j = this.openCount;

        if (j != i) {
            boolean flag = i != 0;
            boolean flag1 = j != 0;

            if (flag && !flag1) {
                this.a(world, blockposition, iblockdata);
                world.a((Entity) null, GameEvent.CONTAINER_OPEN, blockposition);
            } else if (!flag) {
                this.b(world, blockposition, iblockdata);
                world.a((Entity) null, GameEvent.CONTAINER_CLOSE, blockposition);
            }

            this.openCount = i;
        }

        this.a(world, blockposition, iblockdata, j, i);
        if (i > 0) {
            d(world, blockposition, iblockdata);
        }

    }

    public int getOpenerCount() {
        return this.openCount;
    }

    private static void d(World world, BlockPosition blockposition, IBlockData iblockdata) {
        world.getBlockTickList().a(blockposition, iblockdata.getBlock(), 5);
    }
}
