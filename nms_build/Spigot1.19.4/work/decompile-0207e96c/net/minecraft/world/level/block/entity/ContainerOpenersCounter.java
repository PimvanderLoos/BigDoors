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

    protected abstract void onOpen(World world, BlockPosition blockposition, IBlockData iblockdata);

    protected abstract void onClose(World world, BlockPosition blockposition, IBlockData iblockdata);

    protected abstract void openerCountChanged(World world, BlockPosition blockposition, IBlockData iblockdata, int i, int j);

    protected abstract boolean isOwnContainer(EntityHuman entityhuman);

    public void incrementOpeners(EntityHuman entityhuman, World world, BlockPosition blockposition, IBlockData iblockdata) {
        int i = this.openCount++;

        if (i == 0) {
            this.onOpen(world, blockposition, iblockdata);
            world.gameEvent((Entity) entityhuman, GameEvent.CONTAINER_OPEN, blockposition);
            scheduleRecheck(world, blockposition, iblockdata);
        }

        this.openerCountChanged(world, blockposition, iblockdata, i, this.openCount);
    }

    public void decrementOpeners(EntityHuman entityhuman, World world, BlockPosition blockposition, IBlockData iblockdata) {
        int i = this.openCount--;

        if (this.openCount == 0) {
            this.onClose(world, blockposition, iblockdata);
            world.gameEvent((Entity) entityhuman, GameEvent.CONTAINER_CLOSE, blockposition);
        }

        this.openerCountChanged(world, blockposition, iblockdata, i, this.openCount);
    }

    private int getOpenCount(World world, BlockPosition blockposition) {
        int i = blockposition.getX();
        int j = blockposition.getY();
        int k = blockposition.getZ();
        float f = 5.0F;
        AxisAlignedBB axisalignedbb = new AxisAlignedBB((double) ((float) i - 5.0F), (double) ((float) j - 5.0F), (double) ((float) k - 5.0F), (double) ((float) (i + 1) + 5.0F), (double) ((float) (j + 1) + 5.0F), (double) ((float) (k + 1) + 5.0F));

        return world.getEntities(EntityTypeTest.forClass(EntityHuman.class), axisalignedbb, this::isOwnContainer).size();
    }

    public void recheckOpeners(World world, BlockPosition blockposition, IBlockData iblockdata) {
        int i = this.getOpenCount(world, blockposition);
        int j = this.openCount;

        if (j != i) {
            boolean flag = i != 0;
            boolean flag1 = j != 0;

            if (flag && !flag1) {
                this.onOpen(world, blockposition, iblockdata);
                world.gameEvent((Entity) null, GameEvent.CONTAINER_OPEN, blockposition);
            } else if (!flag) {
                this.onClose(world, blockposition, iblockdata);
                world.gameEvent((Entity) null, GameEvent.CONTAINER_CLOSE, blockposition);
            }

            this.openCount = i;
        }

        this.openerCountChanged(world, blockposition, iblockdata, j, i);
        if (i > 0) {
            scheduleRecheck(world, blockposition, iblockdata);
        }

    }

    public int getOpenerCount() {
        return this.openCount;
    }

    private static void scheduleRecheck(World world, BlockPosition blockposition, IBlockData iblockdata) {
        world.scheduleTick(blockposition, iblockdata.getBlock(), 5);
    }
}
