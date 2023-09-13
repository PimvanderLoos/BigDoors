package net.minecraft.world.level.block;

import java.util.Iterator;
import java.util.Optional;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.RandomSource;
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
    private final ResourceKey<WorldGenFeatureConfigured<?, ?>> feature;

    public BlockMushroom(BlockBase.Info blockbase_info, ResourceKey<WorldGenFeatureConfigured<?, ?>> resourcekey) {
        super(blockbase_info);
        this.feature = resourcekey;
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockMushroom.SHAPE;
    }

    @Override
    public void randomTick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, RandomSource randomsource) {
        if (randomsource.nextInt(25) == 0) {
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

            BlockPosition blockposition2 = blockposition.offset(randomsource.nextInt(3) - 1, randomsource.nextInt(2) - randomsource.nextInt(2), randomsource.nextInt(3) - 1);

            for (int j = 0; j < 4; ++j) {
                if (worldserver.isEmptyBlock(blockposition2) && iblockdata.canSurvive(worldserver, blockposition2)) {
                    blockposition = blockposition2;
                }

                blockposition2 = blockposition.offset(randomsource.nextInt(3) - 1, randomsource.nextInt(2) - randomsource.nextInt(2), randomsource.nextInt(3) - 1);
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

        return iblockdata1.is(TagsBlock.MUSHROOM_GROW_BLOCK) ? true : iworldreader.getRawBrightness(blockposition, 0) < 13 && this.mayPlaceOn(iblockdata1, iworldreader, blockposition1);
    }

    public boolean growMushroom(WorldServer worldserver, BlockPosition blockposition, IBlockData iblockdata, RandomSource randomsource) {
        Optional<? extends Holder<WorldGenFeatureConfigured<?, ?>>> optional = worldserver.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE).getHolder(this.feature);

        if (optional.isEmpty()) {
            return false;
        } else {
            worldserver.removeBlock(blockposition, false);
            if (((WorldGenFeatureConfigured) ((Holder) optional.get()).value()).place(worldserver, worldserver.getChunkSource().getGenerator(), randomsource, blockposition)) {
                return true;
            } else {
                worldserver.setBlock(blockposition, iblockdata, 3);
                return false;
            }
        }
    }

    @Override
    public boolean isValidBonemealTarget(IWorldReader iworldreader, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(World world, RandomSource randomsource, BlockPosition blockposition, IBlockData iblockdata) {
        return (double) randomsource.nextFloat() < 0.4D;
    }

    @Override
    public void performBonemeal(WorldServer worldserver, RandomSource randomsource, BlockPosition blockposition, IBlockData iblockdata) {
        this.growMushroom(worldserver, blockposition, iblockdata, randomsource);
    }
}
