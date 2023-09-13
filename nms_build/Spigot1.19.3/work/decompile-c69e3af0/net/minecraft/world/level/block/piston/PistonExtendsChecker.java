package net.minecraft.world.level.block.piston;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.material.EnumPistonReaction;

public class PistonExtendsChecker {

    public static final int MAX_PUSH_DEPTH = 12;
    private final World level;
    private final BlockPosition pistonPos;
    private final boolean extending;
    private final BlockPosition startPos;
    private final EnumDirection pushDirection;
    private final List<BlockPosition> toPush = Lists.newArrayList();
    private final List<BlockPosition> toDestroy = Lists.newArrayList();
    private final EnumDirection pistonDirection;

    public PistonExtendsChecker(World world, BlockPosition blockposition, EnumDirection enumdirection, boolean flag) {
        this.level = world;
        this.pistonPos = blockposition;
        this.pistonDirection = enumdirection;
        this.extending = flag;
        if (flag) {
            this.pushDirection = enumdirection;
            this.startPos = blockposition.relative(enumdirection);
        } else {
            this.pushDirection = enumdirection.getOpposite();
            this.startPos = blockposition.relative(enumdirection, 2);
        }

    }

    public boolean resolve() {
        this.toPush.clear();
        this.toDestroy.clear();
        IBlockData iblockdata = this.level.getBlockState(this.startPos);

        if (!BlockPiston.isPushable(iblockdata, this.level, this.startPos, this.pushDirection, false, this.pistonDirection)) {
            if (this.extending && iblockdata.getPistonPushReaction() == EnumPistonReaction.DESTROY) {
                this.toDestroy.add(this.startPos);
                return true;
            } else {
                return false;
            }
        } else if (!this.addBlockLine(this.startPos, this.pushDirection)) {
            return false;
        } else {
            for (int i = 0; i < this.toPush.size(); ++i) {
                BlockPosition blockposition = (BlockPosition) this.toPush.get(i);

                if (isSticky(this.level.getBlockState(blockposition)) && !this.addBranchingBlocks(blockposition)) {
                    return false;
                }
            }

            return true;
        }
    }

    private static boolean isSticky(IBlockData iblockdata) {
        return iblockdata.is(Blocks.SLIME_BLOCK) || iblockdata.is(Blocks.HONEY_BLOCK);
    }

    private static boolean canStickToEachOther(IBlockData iblockdata, IBlockData iblockdata1) {
        return iblockdata.is(Blocks.HONEY_BLOCK) && iblockdata1.is(Blocks.SLIME_BLOCK) ? false : (iblockdata.is(Blocks.SLIME_BLOCK) && iblockdata1.is(Blocks.HONEY_BLOCK) ? false : isSticky(iblockdata) || isSticky(iblockdata1));
    }

    private boolean addBlockLine(BlockPosition blockposition, EnumDirection enumdirection) {
        IBlockData iblockdata = this.level.getBlockState(blockposition);

        if (iblockdata.isAir()) {
            return true;
        } else if (!BlockPiston.isPushable(iblockdata, this.level, blockposition, this.pushDirection, false, enumdirection)) {
            return true;
        } else if (blockposition.equals(this.pistonPos)) {
            return true;
        } else if (this.toPush.contains(blockposition)) {
            return true;
        } else {
            int i = 1;

            if (i + this.toPush.size() > 12) {
                return false;
            } else {
                while (isSticky(iblockdata)) {
                    BlockPosition blockposition1 = blockposition.relative(this.pushDirection.getOpposite(), i);
                    IBlockData iblockdata1 = iblockdata;

                    iblockdata = this.level.getBlockState(blockposition1);
                    if (iblockdata.isAir() || !canStickToEachOther(iblockdata1, iblockdata) || !BlockPiston.isPushable(iblockdata, this.level, blockposition1, this.pushDirection, false, this.pushDirection.getOpposite()) || blockposition1.equals(this.pistonPos)) {
                        break;
                    }

                    ++i;
                    if (i + this.toPush.size() > 12) {
                        return false;
                    }
                }

                int j = 0;

                int k;

                for (k = i - 1; k >= 0; --k) {
                    this.toPush.add(blockposition.relative(this.pushDirection.getOpposite(), k));
                    ++j;
                }

                k = 1;

                while (true) {
                    BlockPosition blockposition2 = blockposition.relative(this.pushDirection, k);
                    int l = this.toPush.indexOf(blockposition2);

                    if (l > -1) {
                        this.reorderListAtCollision(j, l);

                        for (int i1 = 0; i1 <= l + j; ++i1) {
                            BlockPosition blockposition3 = (BlockPosition) this.toPush.get(i1);

                            if (isSticky(this.level.getBlockState(blockposition3)) && !this.addBranchingBlocks(blockposition3)) {
                                return false;
                            }
                        }

                        return true;
                    }

                    iblockdata = this.level.getBlockState(blockposition2);
                    if (iblockdata.isAir()) {
                        return true;
                    }

                    if (!BlockPiston.isPushable(iblockdata, this.level, blockposition2, this.pushDirection, true, this.pushDirection) || blockposition2.equals(this.pistonPos)) {
                        return false;
                    }

                    if (iblockdata.getPistonPushReaction() == EnumPistonReaction.DESTROY) {
                        this.toDestroy.add(blockposition2);
                        return true;
                    }

                    if (this.toPush.size() >= 12) {
                        return false;
                    }

                    this.toPush.add(blockposition2);
                    ++j;
                    ++k;
                }
            }
        }
    }

    private void reorderListAtCollision(int i, int j) {
        List<BlockPosition> list = Lists.newArrayList();
        List<BlockPosition> list1 = Lists.newArrayList();
        List<BlockPosition> list2 = Lists.newArrayList();

        list.addAll(this.toPush.subList(0, j));
        list1.addAll(this.toPush.subList(this.toPush.size() - i, this.toPush.size()));
        list2.addAll(this.toPush.subList(j, this.toPush.size() - i));
        this.toPush.clear();
        this.toPush.addAll(list);
        this.toPush.addAll(list1);
        this.toPush.addAll(list2);
    }

    private boolean addBranchingBlocks(BlockPosition blockposition) {
        IBlockData iblockdata = this.level.getBlockState(blockposition);
        EnumDirection[] aenumdirection = EnumDirection.values();
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection[j];

            if (enumdirection.getAxis() != this.pushDirection.getAxis()) {
                BlockPosition blockposition1 = blockposition.relative(enumdirection);
                IBlockData iblockdata1 = this.level.getBlockState(blockposition1);

                if (canStickToEachOther(iblockdata1, iblockdata) && !this.addBlockLine(blockposition1, enumdirection)) {
                    return false;
                }
            }
        }

        return true;
    }

    public EnumDirection getPushDirection() {
        return this.pushDirection;
    }

    public List<BlockPosition> getToPush() {
        return this.toPush;
    }

    public List<BlockPosition> getToDestroy() {
        return this.toDestroy;
    }
}
