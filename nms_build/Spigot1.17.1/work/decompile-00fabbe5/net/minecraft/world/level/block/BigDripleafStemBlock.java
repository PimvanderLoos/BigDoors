package net.minecraft.world.level.block;

import java.util.Optional;
import java.util.Random;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.LevelHeightAccessor;
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
    protected static final VoxelShape NORTH_SHAPE = Block.a(5.0D, 0.0D, 9.0D, 11.0D, 16.0D, 15.0D);
    protected static final VoxelShape SOUTH_SHAPE = Block.a(5.0D, 0.0D, 1.0D, 11.0D, 16.0D, 7.0D);
    protected static final VoxelShape EAST_SHAPE = Block.a(1.0D, 0.0D, 5.0D, 7.0D, 16.0D, 11.0D);
    protected static final VoxelShape WEST_SHAPE = Block.a(9.0D, 0.0D, 5.0D, 15.0D, 16.0D, 11.0D);

    protected BigDripleafStemBlock(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.k((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.getBlockData()).set(BigDripleafStemBlock.WATERLOGGED, false)).set(BigDripleafStemBlock.FACING, EnumDirection.NORTH));
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        switch ((EnumDirection) iblockdata.get(BigDripleafStemBlock.FACING)) {
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
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BigDripleafStemBlock.WATERLOGGED, BigDripleafStemBlock.FACING);
    }

    @Override
    public Fluid c_(IBlockData iblockdata) {
        return (Boolean) iblockdata.get(BigDripleafStemBlock.WATERLOGGED) ? FluidTypes.WATER.a(false) : super.c_(iblockdata);
    }

    @Override
    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        BlockPosition blockposition1 = blockposition.down();
        IBlockData iblockdata1 = iworldreader.getType(blockposition1);
        IBlockData iblockdata2 = iworldreader.getType(blockposition.up());

        return (iblockdata1.a((Block) this) || iblockdata1.d(iworldreader, blockposition1, EnumDirection.UP)) && (iblockdata2.a((Block) this) || iblockdata2.a(Blocks.BIG_DRIPLEAF));
    }

    protected static boolean a(GeneratorAccess generatoraccess, BlockPosition blockposition, Fluid fluid, EnumDirection enumdirection) {
        IBlockData iblockdata = (IBlockData) ((IBlockData) Blocks.BIG_DRIPLEAF_STEM.getBlockData().set(BigDripleafStemBlock.WATERLOGGED, fluid.a((FluidType) FluidTypes.WATER))).set(BigDripleafStemBlock.FACING, enumdirection);

        return generatoraccess.setTypeAndData(blockposition, iblockdata, 3);
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if ((enumdirection == EnumDirection.DOWN || enumdirection == EnumDirection.UP) && !iblockdata.canPlace(generatoraccess, blockposition)) {
            generatoraccess.getBlockTickList().a(blockposition, this, 1);
        }

        if ((Boolean) iblockdata.get(BigDripleafStemBlock.WATERLOGGED)) {
            generatoraccess.getFluidTickList().a(blockposition, FluidTypes.WATER, FluidTypes.WATER.a((IWorldReader) generatoraccess));
        }

        return super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public void tickAlways(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        if (!iblockdata.canPlace(worldserver, blockposition)) {
            worldserver.b(blockposition, true);
        }

    }

    @Override
    public boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        Optional<BlockPosition> optional = BlockUtil.a(iblockaccess, blockposition, iblockdata.getBlock(), EnumDirection.UP, Blocks.BIG_DRIPLEAF);

        if (!optional.isPresent()) {
            return false;
        } else {
            BlockPosition blockposition1 = ((BlockPosition) optional.get()).up();
            IBlockData iblockdata1 = iblockaccess.getType(blockposition1);

            return BigDripleafBlock.a((LevelHeightAccessor) iblockaccess, blockposition1, iblockdata1);
        }
    }

    @Override
    public boolean a(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        return true;
    }

    @Override
    public void a(WorldServer worldserver, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        Optional<BlockPosition> optional = BlockUtil.a(worldserver, blockposition, iblockdata.getBlock(), EnumDirection.UP, Blocks.BIG_DRIPLEAF);

        if (optional.isPresent()) {
            BlockPosition blockposition1 = (BlockPosition) optional.get();
            BlockPosition blockposition2 = blockposition1.up();
            EnumDirection enumdirection = (EnumDirection) iblockdata.get(BigDripleafStemBlock.FACING);

            a((GeneratorAccess) worldserver, blockposition1, worldserver.getFluid(blockposition1), enumdirection);
            BigDripleafBlock.a((GeneratorAccess) worldserver, blockposition2, worldserver.getFluid(blockposition2), enumdirection);
        }
    }

    @Override
    public ItemStack a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        return new ItemStack(Blocks.BIG_DRIPLEAF);
    }
}
