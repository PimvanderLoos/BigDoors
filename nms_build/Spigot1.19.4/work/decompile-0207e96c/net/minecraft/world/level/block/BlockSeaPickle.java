package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.block.state.properties.BlockStateInteger;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class BlockSeaPickle extends BlockPlant implements IBlockFragilePlantElement, IBlockWaterlogged {

    public static final int MAX_PICKLES = 4;
    public static final BlockStateInteger PICKLES = BlockProperties.PICKLES;
    public static final BlockStateBoolean WATERLOGGED = BlockProperties.WATERLOGGED;
    protected static final VoxelShape ONE_AABB = Block.box(6.0D, 0.0D, 6.0D, 10.0D, 6.0D, 10.0D);
    protected static final VoxelShape TWO_AABB = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 6.0D, 13.0D);
    protected static final VoxelShape THREE_AABB = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 6.0D, 14.0D);
    protected static final VoxelShape FOUR_AABB = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 7.0D, 14.0D);

    protected BlockSeaPickle(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BlockSeaPickle.PICKLES, 1)).setValue(BlockSeaPickle.WATERLOGGED, true));
    }

    @Nullable
    @Override
    public IBlockData getStateForPlacement(BlockActionContext blockactioncontext) {
        IBlockData iblockdata = blockactioncontext.getLevel().getBlockState(blockactioncontext.getClickedPos());

        if (iblockdata.is((Block) this)) {
            return (IBlockData) iblockdata.setValue(BlockSeaPickle.PICKLES, Math.min(4, (Integer) iblockdata.getValue(BlockSeaPickle.PICKLES) + 1));
        } else {
            Fluid fluid = blockactioncontext.getLevel().getFluidState(blockactioncontext.getClickedPos());
            boolean flag = fluid.getType() == FluidTypes.WATER;

            return (IBlockData) super.getStateForPlacement(blockactioncontext).setValue(BlockSeaPickle.WATERLOGGED, flag);
        }
    }

    public static boolean isDead(IBlockData iblockdata) {
        return !(Boolean) iblockdata.getValue(BlockSeaPickle.WATERLOGGED);
    }

    @Override
    protected boolean mayPlaceOn(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return !iblockdata.getCollisionShape(iblockaccess, blockposition).getFaceShape(EnumDirection.UP).isEmpty() || iblockdata.isFaceSturdy(iblockaccess, blockposition, EnumDirection.UP);
    }

    @Override
    public boolean canSurvive(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        BlockPosition blockposition1 = blockposition.below();

        return this.mayPlaceOn(iworldreader.getBlockState(blockposition1), iworldreader, blockposition1);
    }

    @Override
    public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if (!iblockdata.canSurvive(generatoraccess, blockposition)) {
            return Blocks.AIR.defaultBlockState();
        } else {
            if ((Boolean) iblockdata.getValue(BlockSeaPickle.WATERLOGGED)) {
                generatoraccess.scheduleTick(blockposition, (FluidType) FluidTypes.WATER, FluidTypes.WATER.getTickDelay(generatoraccess));
            }

            return super.updateShape(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
        }
    }

    @Override
    public boolean canBeReplaced(IBlockData iblockdata, BlockActionContext blockactioncontext) {
        return !blockactioncontext.isSecondaryUseActive() && blockactioncontext.getItemInHand().is(this.asItem()) && (Integer) iblockdata.getValue(BlockSeaPickle.PICKLES) < 4 ? true : super.canBeReplaced(iblockdata, blockactioncontext);
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        switch ((Integer) iblockdata.getValue(BlockSeaPickle.PICKLES)) {
            case 1:
            default:
                return BlockSeaPickle.ONE_AABB;
            case 2:
                return BlockSeaPickle.TWO_AABB;
            case 3:
                return BlockSeaPickle.THREE_AABB;
            case 4:
                return BlockSeaPickle.FOUR_AABB;
        }
    }

    @Override
    public Fluid getFluidState(IBlockData iblockdata) {
        return (Boolean) iblockdata.getValue(BlockSeaPickle.WATERLOGGED) ? FluidTypes.WATER.getSource(false) : super.getFluidState(iblockdata);
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockSeaPickle.PICKLES, BlockSeaPickle.WATERLOGGED);
    }

    @Override
    public boolean isValidBonemealTarget(IWorldReader iworldreader, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(World world, RandomSource randomsource, BlockPosition blockposition, IBlockData iblockdata) {
        return true;
    }

    @Override
    public void performBonemeal(WorldServer worldserver, RandomSource randomsource, BlockPosition blockposition, IBlockData iblockdata) {
        if (!isDead(iblockdata) && worldserver.getBlockState(blockposition.below()).is(TagsBlock.CORAL_BLOCKS)) {
            boolean flag = true;
            int i = 1;
            boolean flag1 = true;
            int j = 0;
            int k = blockposition.getX() - 2;
            int l = 0;

            for (int i1 = 0; i1 < 5; ++i1) {
                for (int j1 = 0; j1 < i; ++j1) {
                    int k1 = 2 + blockposition.getY() - 1;

                    for (int l1 = k1 - 2; l1 < k1; ++l1) {
                        BlockPosition blockposition1 = new BlockPosition(k + i1, l1, blockposition.getZ() - l + j1);

                        if (blockposition1 != blockposition && randomsource.nextInt(6) == 0 && worldserver.getBlockState(blockposition1).is(Blocks.WATER)) {
                            IBlockData iblockdata1 = worldserver.getBlockState(blockposition1.below());

                            if (iblockdata1.is(TagsBlock.CORAL_BLOCKS)) {
                                worldserver.setBlock(blockposition1, (IBlockData) Blocks.SEA_PICKLE.defaultBlockState().setValue(BlockSeaPickle.PICKLES, randomsource.nextInt(4) + 1), 3);
                            }
                        }
                    }
                }

                if (j < 2) {
                    i += 2;
                    ++l;
                } else {
                    i -= 2;
                    --l;
                }

                ++j;
            }

            worldserver.setBlock(blockposition, (IBlockData) iblockdata.setValue(BlockSeaPickle.PICKLES, 4), 2);
        }

    }

    @Override
    public boolean isPathfindable(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}
