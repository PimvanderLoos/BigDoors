package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockPropertyDoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.block.state.properties.BlockStateDirection;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class SmallDripleafBlock extends BlockTallPlant implements IBlockFragilePlantElement, IBlockWaterlogged {

    private static final BlockStateBoolean WATERLOGGED = BlockProperties.WATERLOGGED;
    public static final BlockStateDirection FACING = BlockProperties.HORIZONTAL_FACING;
    protected static final float AABB_OFFSET = 6.0F;
    protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 13.0D, 14.0D);

    public SmallDripleafBlock(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(SmallDripleafBlock.HALF, BlockPropertyDoubleBlockHalf.LOWER)).setValue(SmallDripleafBlock.WATERLOGGED, false)).setValue(SmallDripleafBlock.FACING, EnumDirection.NORTH));
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return SmallDripleafBlock.SHAPE;
    }

    @Override
    protected boolean mayPlaceOn(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockdata.is(TagsBlock.SMALL_DRIPLEAF_PLACEABLE) || iblockaccess.getFluidState(blockposition.above()).isSourceOfType(FluidTypes.WATER) && super.mayPlaceOn(iblockdata, iblockaccess, blockposition);
    }

    @Nullable
    @Override
    public IBlockData getStateForPlacement(BlockActionContext blockactioncontext) {
        IBlockData iblockdata = super.getStateForPlacement(blockactioncontext);

        return iblockdata != null ? copyWaterloggedFrom(blockactioncontext.getLevel(), blockactioncontext.getClickedPos(), (IBlockData) iblockdata.setValue(SmallDripleafBlock.FACING, blockactioncontext.getHorizontalDirection().getOpposite())) : null;
    }

    @Override
    public void setPlacedBy(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
        if (!world.isClientSide()) {
            BlockPosition blockposition1 = blockposition.above();
            IBlockData iblockdata1 = BlockTallPlant.copyWaterloggedFrom(world, blockposition1, (IBlockData) ((IBlockData) this.defaultBlockState().setValue(SmallDripleafBlock.HALF, BlockPropertyDoubleBlockHalf.UPPER)).setValue(SmallDripleafBlock.FACING, (EnumDirection) iblockdata.getValue(SmallDripleafBlock.FACING)));

            world.setBlock(blockposition1, iblockdata1, 3);
        }

    }

    @Override
    public Fluid getFluidState(IBlockData iblockdata) {
        return (Boolean) iblockdata.getValue(SmallDripleafBlock.WATERLOGGED) ? FluidTypes.WATER.getSource(false) : super.getFluidState(iblockdata);
    }

    @Override
    public boolean canSurvive(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        if (iblockdata.getValue(SmallDripleafBlock.HALF) == BlockPropertyDoubleBlockHalf.UPPER) {
            return super.canSurvive(iblockdata, iworldreader, blockposition);
        } else {
            BlockPosition blockposition1 = blockposition.below();
            IBlockData iblockdata1 = iworldreader.getBlockState(blockposition1);

            return this.mayPlaceOn(iblockdata1, iworldreader, blockposition1);
        }
    }

    @Override
    public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if ((Boolean) iblockdata.getValue(SmallDripleafBlock.WATERLOGGED)) {
            generatoraccess.scheduleTick(blockposition, (FluidType) FluidTypes.WATER, FluidTypes.WATER.getTickDelay(generatoraccess));
        }

        return super.updateShape(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(SmallDripleafBlock.HALF, SmallDripleafBlock.WATERLOGGED, SmallDripleafBlock.FACING);
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
        BlockPosition blockposition1;

        if (iblockdata.getValue(BlockTallPlant.HALF) == BlockPropertyDoubleBlockHalf.LOWER) {
            blockposition1 = blockposition.above();
            worldserver.setBlock(blockposition1, worldserver.getFluidState(blockposition1).createLegacyBlock(), 18);
            BigDripleafBlock.placeWithRandomHeight(worldserver, randomsource, blockposition, (EnumDirection) iblockdata.getValue(SmallDripleafBlock.FACING));
        } else {
            blockposition1 = blockposition.below();
            this.performBonemeal(worldserver, randomsource, blockposition1, worldserver.getBlockState(blockposition1));
        }

    }

    @Override
    public IBlockData rotate(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.setValue(SmallDripleafBlock.FACING, enumblockrotation.rotate((EnumDirection) iblockdata.getValue(SmallDripleafBlock.FACING)));
    }

    @Override
    public IBlockData mirror(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.rotate(enumblockmirror.getRotation((EnumDirection) iblockdata.getValue(SmallDripleafBlock.FACING)));
    }

    @Override
    public float getMaxVerticalOffset() {
        return 0.1F;
    }
}
