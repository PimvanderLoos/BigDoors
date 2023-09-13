package net.minecraft.world.level.block;

import java.util.Map;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.block.state.properties.IBlockState;

public class BlockHugeMushroom extends Block {

    public static final BlockStateBoolean NORTH = BlockSprawling.NORTH;
    public static final BlockStateBoolean EAST = BlockSprawling.EAST;
    public static final BlockStateBoolean SOUTH = BlockSprawling.SOUTH;
    public static final BlockStateBoolean WEST = BlockSprawling.WEST;
    public static final BlockStateBoolean UP = BlockSprawling.UP;
    public static final BlockStateBoolean DOWN = BlockSprawling.DOWN;
    private static final Map<EnumDirection, BlockStateBoolean> PROPERTY_BY_DIRECTION = BlockSprawling.PROPERTY_BY_DIRECTION;

    public BlockHugeMushroom(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BlockHugeMushroom.NORTH, true)).setValue(BlockHugeMushroom.EAST, true)).setValue(BlockHugeMushroom.SOUTH, true)).setValue(BlockHugeMushroom.WEST, true)).setValue(BlockHugeMushroom.UP, true)).setValue(BlockHugeMushroom.DOWN, true));
    }

    @Override
    public IBlockData getStateForPlacement(BlockActionContext blockactioncontext) {
        World world = blockactioncontext.getLevel();
        BlockPosition blockposition = blockactioncontext.getClickedPos();

        return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.defaultBlockState().setValue(BlockHugeMushroom.DOWN, !world.getBlockState(blockposition.below()).is((Block) this))).setValue(BlockHugeMushroom.UP, !world.getBlockState(blockposition.above()).is((Block) this))).setValue(BlockHugeMushroom.NORTH, !world.getBlockState(blockposition.north()).is((Block) this))).setValue(BlockHugeMushroom.EAST, !world.getBlockState(blockposition.east()).is((Block) this))).setValue(BlockHugeMushroom.SOUTH, !world.getBlockState(blockposition.south()).is((Block) this))).setValue(BlockHugeMushroom.WEST, !world.getBlockState(blockposition.west()).is((Block) this));
    }

    @Override
    public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return iblockdata1.is((Block) this) ? (IBlockData) iblockdata.setValue((IBlockState) BlockHugeMushroom.PROPERTY_BY_DIRECTION.get(enumdirection), false) : super.updateShape(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public IBlockData rotate(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) iblockdata.setValue((IBlockState) BlockHugeMushroom.PROPERTY_BY_DIRECTION.get(enumblockrotation.rotate(EnumDirection.NORTH)), (Boolean) iblockdata.getValue(BlockHugeMushroom.NORTH))).setValue((IBlockState) BlockHugeMushroom.PROPERTY_BY_DIRECTION.get(enumblockrotation.rotate(EnumDirection.SOUTH)), (Boolean) iblockdata.getValue(BlockHugeMushroom.SOUTH))).setValue((IBlockState) BlockHugeMushroom.PROPERTY_BY_DIRECTION.get(enumblockrotation.rotate(EnumDirection.EAST)), (Boolean) iblockdata.getValue(BlockHugeMushroom.EAST))).setValue((IBlockState) BlockHugeMushroom.PROPERTY_BY_DIRECTION.get(enumblockrotation.rotate(EnumDirection.WEST)), (Boolean) iblockdata.getValue(BlockHugeMushroom.WEST))).setValue((IBlockState) BlockHugeMushroom.PROPERTY_BY_DIRECTION.get(enumblockrotation.rotate(EnumDirection.UP)), (Boolean) iblockdata.getValue(BlockHugeMushroom.UP))).setValue((IBlockState) BlockHugeMushroom.PROPERTY_BY_DIRECTION.get(enumblockrotation.rotate(EnumDirection.DOWN)), (Boolean) iblockdata.getValue(BlockHugeMushroom.DOWN));
    }

    @Override
    public IBlockData mirror(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) iblockdata.setValue((IBlockState) BlockHugeMushroom.PROPERTY_BY_DIRECTION.get(enumblockmirror.mirror(EnumDirection.NORTH)), (Boolean) iblockdata.getValue(BlockHugeMushroom.NORTH))).setValue((IBlockState) BlockHugeMushroom.PROPERTY_BY_DIRECTION.get(enumblockmirror.mirror(EnumDirection.SOUTH)), (Boolean) iblockdata.getValue(BlockHugeMushroom.SOUTH))).setValue((IBlockState) BlockHugeMushroom.PROPERTY_BY_DIRECTION.get(enumblockmirror.mirror(EnumDirection.EAST)), (Boolean) iblockdata.getValue(BlockHugeMushroom.EAST))).setValue((IBlockState) BlockHugeMushroom.PROPERTY_BY_DIRECTION.get(enumblockmirror.mirror(EnumDirection.WEST)), (Boolean) iblockdata.getValue(BlockHugeMushroom.WEST))).setValue((IBlockState) BlockHugeMushroom.PROPERTY_BY_DIRECTION.get(enumblockmirror.mirror(EnumDirection.UP)), (Boolean) iblockdata.getValue(BlockHugeMushroom.UP))).setValue((IBlockState) BlockHugeMushroom.PROPERTY_BY_DIRECTION.get(enumblockmirror.mirror(EnumDirection.DOWN)), (Boolean) iblockdata.getValue(BlockHugeMushroom.DOWN));
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockHugeMushroom.UP, BlockHugeMushroom.DOWN, BlockHugeMushroom.NORTH, BlockHugeMushroom.EAST, BlockHugeMushroom.SOUTH, BlockHugeMushroom.WEST);
    }
}
