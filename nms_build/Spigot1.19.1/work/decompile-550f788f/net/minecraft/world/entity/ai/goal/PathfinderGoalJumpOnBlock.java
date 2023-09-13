package net.minecraft.world.entity.ai.goal;

import net.minecraft.core.BlockPosition;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.entity.animal.EntityCat;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.block.BlockBed;
import net.minecraft.world.level.block.BlockFurnaceFurace;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntityChest;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockPropertyBedPart;

public class PathfinderGoalJumpOnBlock extends PathfinderGoalGotoTarget {

    private final EntityCat cat;

    public PathfinderGoalJumpOnBlock(EntityCat entitycat, double d0) {
        super(entitycat, d0, 8);
        this.cat = entitycat;
    }

    @Override
    public boolean canUse() {
        return this.cat.isTame() && !this.cat.isOrderedToSit() && super.canUse();
    }

    @Override
    public void start() {
        super.start();
        this.cat.setInSittingPose(false);
    }

    @Override
    public void stop() {
        super.stop();
        this.cat.setInSittingPose(false);
    }

    @Override
    public void tick() {
        super.tick();
        this.cat.setInSittingPose(this.isReachedTarget());
    }

    @Override
    protected boolean isValidTarget(IWorldReader iworldreader, BlockPosition blockposition) {
        if (!iworldreader.isEmptyBlock(blockposition.above())) {
            return false;
        } else {
            IBlockData iblockdata = iworldreader.getBlockState(blockposition);

            return iblockdata.is(Blocks.CHEST) ? TileEntityChest.getOpenCount(iworldreader, blockposition) < 1 : (iblockdata.is(Blocks.FURNACE) && (Boolean) iblockdata.getValue(BlockFurnaceFurace.LIT) ? true : iblockdata.is(TagsBlock.BEDS, (blockbase_blockdata) -> {
                return (Boolean) blockbase_blockdata.getOptionalValue(BlockBed.PART).map((blockpropertybedpart) -> {
                    return blockpropertybedpart != BlockPropertyBedPart.HEAD;
                }).orElse(true);
            }));
        }
    }
}
