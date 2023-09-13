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
        this.k((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.getBlockData()).set(BlockHugeMushroom.NORTH, true)).set(BlockHugeMushroom.EAST, true)).set(BlockHugeMushroom.SOUTH, true)).set(BlockHugeMushroom.WEST, true)).set(BlockHugeMushroom.UP, true)).set(BlockHugeMushroom.DOWN, true));
    }

    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        World world = blockactioncontext.getWorld();
        BlockPosition blockposition = blockactioncontext.getClickPosition();

        return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.getBlockData().set(BlockHugeMushroom.DOWN, !world.getType(blockposition.down()).a((Block) this))).set(BlockHugeMushroom.UP, !world.getType(blockposition.up()).a((Block) this))).set(BlockHugeMushroom.NORTH, !world.getType(blockposition.north()).a((Block) this))).set(BlockHugeMushroom.EAST, !world.getType(blockposition.east()).a((Block) this))).set(BlockHugeMushroom.SOUTH, !world.getType(blockposition.south()).a((Block) this))).set(BlockHugeMushroom.WEST, !world.getType(blockposition.west()).a((Block) this));
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return iblockdata1.a((Block) this) ? (IBlockData) iblockdata.set((IBlockState) BlockHugeMushroom.PROPERTY_BY_DIRECTION.get(enumdirection), false) : super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) iblockdata.set((IBlockState) BlockHugeMushroom.PROPERTY_BY_DIRECTION.get(enumblockrotation.a(EnumDirection.NORTH)), (Boolean) iblockdata.get(BlockHugeMushroom.NORTH))).set((IBlockState) BlockHugeMushroom.PROPERTY_BY_DIRECTION.get(enumblockrotation.a(EnumDirection.SOUTH)), (Boolean) iblockdata.get(BlockHugeMushroom.SOUTH))).set((IBlockState) BlockHugeMushroom.PROPERTY_BY_DIRECTION.get(enumblockrotation.a(EnumDirection.EAST)), (Boolean) iblockdata.get(BlockHugeMushroom.EAST))).set((IBlockState) BlockHugeMushroom.PROPERTY_BY_DIRECTION.get(enumblockrotation.a(EnumDirection.WEST)), (Boolean) iblockdata.get(BlockHugeMushroom.WEST))).set((IBlockState) BlockHugeMushroom.PROPERTY_BY_DIRECTION.get(enumblockrotation.a(EnumDirection.UP)), (Boolean) iblockdata.get(BlockHugeMushroom.UP))).set((IBlockState) BlockHugeMushroom.PROPERTY_BY_DIRECTION.get(enumblockrotation.a(EnumDirection.DOWN)), (Boolean) iblockdata.get(BlockHugeMushroom.DOWN));
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) iblockdata.set((IBlockState) BlockHugeMushroom.PROPERTY_BY_DIRECTION.get(enumblockmirror.b(EnumDirection.NORTH)), (Boolean) iblockdata.get(BlockHugeMushroom.NORTH))).set((IBlockState) BlockHugeMushroom.PROPERTY_BY_DIRECTION.get(enumblockmirror.b(EnumDirection.SOUTH)), (Boolean) iblockdata.get(BlockHugeMushroom.SOUTH))).set((IBlockState) BlockHugeMushroom.PROPERTY_BY_DIRECTION.get(enumblockmirror.b(EnumDirection.EAST)), (Boolean) iblockdata.get(BlockHugeMushroom.EAST))).set((IBlockState) BlockHugeMushroom.PROPERTY_BY_DIRECTION.get(enumblockmirror.b(EnumDirection.WEST)), (Boolean) iblockdata.get(BlockHugeMushroom.WEST))).set((IBlockState) BlockHugeMushroom.PROPERTY_BY_DIRECTION.get(enumblockmirror.b(EnumDirection.UP)), (Boolean) iblockdata.get(BlockHugeMushroom.UP))).set((IBlockState) BlockHugeMushroom.PROPERTY_BY_DIRECTION.get(enumblockmirror.b(EnumDirection.DOWN)), (Boolean) iblockdata.get(BlockHugeMushroom.DOWN));
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockHugeMushroom.UP, BlockHugeMushroom.DOWN, BlockHugeMushroom.NORTH, BlockHugeMushroom.EAST, BlockHugeMushroom.SOUTH, BlockHugeMushroom.WEST);
    }
}
