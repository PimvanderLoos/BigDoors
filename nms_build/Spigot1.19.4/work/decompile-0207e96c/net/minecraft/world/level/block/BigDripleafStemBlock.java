package net.minecraft.world.level.block;

import java.util.Optional;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class BigDripleafStemBlock extends BlockFacingHorizontal implements IBlockFragilePlantElement, IBlockWaterlogged {

    private static final BlockStateBoolean WATERLOGGED = BlockProperties.WATERLOGGED;
    private static final int STEM_WIDTH = 6;
    protected static final VoxelShape NORTH_SHAPE = Block.box(5.0D, 0.0D, 9.0D, 11.0D, 16.0D, 15.0D);
    protected static final VoxelShape SOUTH_SHAPE = Block.box(5.0D, 0.0D, 1.0D, 11.0D, 16.0D, 7.0D);
    protected static final VoxelShape EAST_SHAPE = Block.box(1.0D, 0.0D, 5.0D, 7.0D, 16.0D, 11.0D);
    protected static final VoxelShape WEST_SHAPE = Block.box(9.0D, 0.0D, 5.0D, 15.0D, 16.0D, 11.0D);

    protected BigDripleafStemBlock(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BigDripleafStemBlock.WATERLOGGED, false)).setValue(BigDripleafStemBlock.FACING, EnumDirection.NORTH));
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        switch ((EnumDirection) iblockdata.getValue(BigDripleafStemBlock.FACING)) {
            case SOUTH:
                return BigDripleafStemBlock.SOUTH_SHAPE;
            case NORTH:
            default:
                return BigDripleafStemBlock.NORTH_SHAPE;
            case WEST:
                return BigDripleafStemBlock.WEST_SHAPE;
            case EAST:
                return BigDripleafStemBlock.EAST_SHAPE;
        }
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BigDripleafStemBlock.WATERLOGGED, BigDripleafStemBlock.FACING);
    }

    @Override
    public Fluid getFluidState(IBlockData iblockdata) {
        return (Boolean) iblockdata.getValue(BigDripleafStemBlock.WATERLOGGED) ? FluidTypes.WATER.getSource(false) : super.getFluidState(iblockdata);
    }

    @Override
    public boolean canSurvive(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        BlockPosition blockposition1 = blockposition.below();
        IBlockData iblockdata1 = iworldreader.getBlockState(blockposition1);
        IBlockData iblockdata2 = iworldreader.getBlockState(blockposition.above());

        return (iblockdata1.is((Block) this) || iblockdata1.is(TagsBlock.BIG_DRIPLEAF_PLACEABLE)) && (iblockdata2.is((Block) this) || iblockdata2.is(Blocks.BIG_DRIPLEAF));
    }

    protected static boolean place(GeneratorAccess generatoraccess, BlockPosition blockposition, Fluid fluid, EnumDirection enumdirection) {
        IBlockData iblockdata = (IBlockData) ((IBlockData) Blocks.BIG_DRIPLEAF_STEM.defaultBlockState().setValue(BigDripleafStemBlock.WATERLOGGED, fluid.isSourceOfType(FluidTypes.WATER))).setValue(BigDripleafStemBlock.FACING, enumdirection);

        return generatoraccess.setBlock(blockposition, iblockdata, 3);
    }

    @Override
    public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if ((enumdirection == EnumDirection.DOWN || enumdirection == EnumDirection.UP) && !iblockdata.canSurvive(generatoraccess, blockposition)) {
            generatoraccess.scheduleTick(blockposition, (Block) this, 1);
        }

        if ((Boolean) iblockdata.getValue(BigDripleafStemBlock.WATERLOGGED)) {
            generatoraccess.scheduleTick(blockposition, (FluidType) FluidTypes.WATER, FluidTypes.WATER.getTickDelay(generatoraccess));
        }

        return super.updateShape(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public void tick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, RandomSource randomsource) {
        if (!iblockdata.canSurvive(worldserver, blockposition)) {
            worldserver.destroyBlock(blockposition, true);
        }

    }

    @Override
    public boolean isValidBonemealTarget(IWorldReader iworldreader, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        Optional<BlockPosition> optional = BlockUtil.getTopConnectedBlock(iworldreader, blockposition, iblockdata.getBlock(), EnumDirection.UP, Blocks.BIG_DRIPLEAF);

        if (!optional.isPresent()) {
            return false;
        } else {
            BlockPosition blockposition1 = ((BlockPosition) optional.get()).above();
            IBlockData iblockdata1 = iworldreader.getBlockState(blockposition1);

            return BigDripleafBlock.canPlaceAt(iworldreader, blockposition1, iblockdata1);
        }
    }

    @Override
    public boolean isBonemealSuccess(World world, RandomSource randomsource, BlockPosition blockposition, IBlockData iblockdata) {
        return true;
    }

    @Override
    public void performBonemeal(WorldServer worldserver, RandomSource randomsource, BlockPosition blockposition, IBlockData iblockdata) {
        Optional<BlockPosition> optional = BlockUtil.getTopConnectedBlock(worldserver, blockposition, iblockdata.getBlock(), EnumDirection.UP, Blocks.BIG_DRIPLEAF);

        if (optional.isPresent()) {
            BlockPosition blockposition1 = (BlockPosition) optional.get();
            BlockPosition blockposition2 = blockposition1.above();
            EnumDirection enumdirection = (EnumDirection) iblockdata.getValue(BigDripleafStemBlock.FACING);

            place(worldserver, blockposition1, worldserver.getFluidState(blockposition1), enumdirection);
            BigDripleafBlock.place(worldserver, blockposition2, worldserver.getFluidState(blockposition2), enumdirection);
        }
    }

    @Override
    public ItemStack getCloneItemStack(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        return new ItemStack(Blocks.BIG_DRIPLEAF);
    }
}
