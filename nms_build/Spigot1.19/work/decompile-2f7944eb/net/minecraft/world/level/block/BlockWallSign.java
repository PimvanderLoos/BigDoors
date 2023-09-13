package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockPropertyWood;
import net.minecraft.world.level.block.state.properties.BlockStateDirection;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class BlockWallSign extends BlockSign {

    public static final BlockStateDirection FACING = BlockFacingHorizontal.FACING;
    protected static final float AABB_THICKNESS = 2.0F;
    protected static final float AABB_BOTTOM = 4.5F;
    protected static final float AABB_TOP = 12.5F;
    private static final Map<EnumDirection, VoxelShape> AABBS = Maps.newEnumMap(ImmutableMap.of(EnumDirection.NORTH, Block.box(0.0D, 4.5D, 14.0D, 16.0D, 12.5D, 16.0D), EnumDirection.SOUTH, Block.box(0.0D, 4.5D, 0.0D, 16.0D, 12.5D, 2.0D), EnumDirection.EAST, Block.box(0.0D, 4.5D, 0.0D, 2.0D, 12.5D, 16.0D), EnumDirection.WEST, Block.box(14.0D, 4.5D, 0.0D, 16.0D, 12.5D, 16.0D)));

    public BlockWallSign(BlockBase.Info blockbase_info, BlockPropertyWood blockpropertywood) {
        super(blockbase_info, blockpropertywood);
        this.registerDefaultState((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BlockWallSign.FACING, EnumDirection.NORTH)).setValue(BlockWallSign.WATERLOGGED, false));
    }

    @Override
    public String getDescriptionId() {
        return this.asItem().getDescriptionId();
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return (VoxelShape) BlockWallSign.AABBS.get(iblockdata.getValue(BlockWallSign.FACING));
    }

    @Override
    public boolean canSurvive(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        return iworldreader.getBlockState(blockposition.relative(((EnumDirection) iblockdata.getValue(BlockWallSign.FACING)).getOpposite())).getMaterial().isSolid();
    }

    @Nullable
    @Override
    public IBlockData getStateForPlacement(BlockActionContext blockactioncontext) {
        IBlockData iblockdata = this.defaultBlockState();
        Fluid fluid = blockactioncontext.getLevel().getFluidState(blockactioncontext.getClickedPos());
        World world = blockactioncontext.getLevel();
        BlockPosition blockposition = blockactioncontext.getClickedPos();
        EnumDirection[] aenumdirection = blockactioncontext.getNearestLookingDirections();
        EnumDirection[] aenumdirection1 = aenumdirection;
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection1[j];

            if (enumdirection.getAxis().isHorizontal()) {
                EnumDirection enumdirection1 = enumdirection.getOpposite();

                iblockdata = (IBlockData) iblockdata.setValue(BlockWallSign.FACING, enumdirection1);
                if (iblockdata.canSurvive(world, blockposition)) {
                    return (IBlockData) iblockdata.setValue(BlockWallSign.WATERLOGGED, fluid.getType() == FluidTypes.WATER);
                }
            }
        }

        return null;
    }

    @Override
    public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return enumdirection.getOpposite() == iblockdata.getValue(BlockWallSign.FACING) && !iblockdata.canSurvive(generatoraccess, blockposition) ? Blocks.AIR.defaultBlockState() : super.updateShape(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public IBlockData rotate(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.setValue(BlockWallSign.FACING, enumblockrotation.rotate((EnumDirection) iblockdata.getValue(BlockWallSign.FACING)));
    }

    @Override
    public IBlockData mirror(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.rotate(enumblockmirror.getRotation((EnumDirection) iblockdata.getValue(BlockWallSign.FACING)));
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockWallSign.FACING, BlockWallSign.WATERLOGGED);
    }
}
