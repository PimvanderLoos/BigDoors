package net.minecraft.world.level.block;

import java.util.Iterator;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class BlockMushroom extends BlockPlant implements IBlockFragilePlantElement {

    protected static final float AABB_OFFSET = 3.0F;
    protected static final VoxelShape SHAPE = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 6.0D, 11.0D);
    private final Supplier<WorldGenFeatureConfigured<?, ?>> featureSupplier;

    public BlockMushroom(BlockBase.Info blockbase_info, Supplier<WorldGenFeatureConfigured<?, ?>> supplier) {
        super(blockbase_info);
        this.featureSupplier = supplier;
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockMushroom.SHAPE;
    }

    @Override
    public void randomTick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        if (random.nextInt(25) == 0) {
            int i = 5;
            boolean flag = true;
            Iterator iterator = BlockPosition.betweenClosed(blockposition.offset(-4, -1, -4), blockposition.offset(4, 1, 4)).iterator();

            while (iterator.hasNext()) {
                BlockPosition blockposition1 = (BlockPosition) iterator.next();

                if (worldserver.getBlockState(blockposition1).is((Block) this)) {
                    --i;
                    if (i <= 0) {
                        return;
                    }
                }
            }

            BlockPosition blockposition2 = blockposition.offset(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);

            for (int j = 0; j < 4; ++j) {
                if (worldserver.isEmptyBlock(blockposition2) && iblockdata.canSurvive(worldserver, blockposition2)) {
                    blockposition = blockposition2;
                }

                blockposition2 = blockposition.offset(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);
            }

            if (worldserver.isEmptyBlock(blockposition2) && iblockdata.canSurvive(worldserver, blockposition2)) {
                worldserver.setBlock(blockposition2, iblockdata, 2);
            }
        }

    }

    @Override
    protected boolean mayPlaceOn(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockdata.isSolidRender(iblockaccess, blockposition);
    }

    @Override
    public boolean canSurvive(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        BlockPosition blockposition1 = blockposition.below();
        IBlockData iblockdata1 = iworldreader.getBlockState(blockposition1);

        return iblockdata1.is((Tag) TagsBlock.MUSHROOM_GROW_BLOCK) ? true : iworldreader.getRawBrightness(blockposition, 0) < 13 && this.mayPlaceOn(iblockdata1, iworldreader, blockposition1);
    }

    public boolean growMushroom(WorldServer worldserver, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        worldserver.removeBlock(blockposition, false);
        if (((WorldGenFeatureConfigured) this.featureSupplier.get()).place(worldserver, worldserver.getChunkSource().getGenerator(), random, blockposition)) {
            return true;
        } else {
            worldserver.setBlock(blockposition, iblockdata, 3);
            return false;
        }
    }

    @Override
    public boolean isValidBonemealTarget(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        return (double) random.nextFloat() < 0.4D;
    }

    @Override
    public void performBonemeal(WorldServer worldserver, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        this.growMushroom(worldserver, blockposition, iblockdata, random);
    }
}
