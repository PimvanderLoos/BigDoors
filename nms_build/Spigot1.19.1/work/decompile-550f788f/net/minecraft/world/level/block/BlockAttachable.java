package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockPropertyAttachPosition;
import net.minecraft.world.level.block.state.properties.BlockStateEnum;

public class BlockAttachable extends BlockFacingHorizontal {

    public static final BlockStateEnum<BlockPropertyAttachPosition> FACE = BlockProperties.ATTACH_FACE;

    protected BlockAttachable(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public boolean canSurvive(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        return canAttach(iworldreader, blockposition, getConnectedDirection(iblockdata).getOpposite());
    }

    public static boolean canAttach(IWorldReader iworldreader, BlockPosition blockposition, EnumDirection enumdirection) {
        BlockPosition blockposition1 = blockposition.relative(enumdirection);

        return iworldreader.getBlockState(blockposition1).isFaceSturdy(iworldreader, blockposition1, enumdirection.getOpposite());
    }

    @Nullable
    @Override
    public IBlockData getStateForPlacement(BlockActionContext blockactioncontext) {
        EnumDirection[] aenumdirection = blockactioncontext.getNearestLookingDirections();
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection[j];
            IBlockData iblockdata;

            if (enumdirection.getAxis() == EnumDirection.EnumAxis.Y) {
                iblockdata = (IBlockData) ((IBlockData) this.defaultBlockState().setValue(BlockAttachable.FACE, enumdirection == EnumDirection.UP ? BlockPropertyAttachPosition.CEILING : BlockPropertyAttachPosition.FLOOR)).setValue(BlockAttachable.FACING, blockactioncontext.getHorizontalDirection());
            } else {
                iblockdata = (IBlockData) ((IBlockData) this.defaultBlockState().setValue(BlockAttachable.FACE, BlockPropertyAttachPosition.WALL)).setValue(BlockAttachable.FACING, enumdirection.getOpposite());
            }

            if (iblockdata.canSurvive(blockactioncontext.getLevel(), blockactioncontext.getClickedPos())) {
                return iblockdata;
            }
        }

        return null;
    }

    @Override
    public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return getConnectedDirection(iblockdata).getOpposite() == enumdirection && !iblockdata.canSurvive(generatoraccess, blockposition) ? Blocks.AIR.defaultBlockState() : super.updateShape(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    protected static EnumDirection getConnectedDirection(IBlockData iblockdata) {
        switch ((BlockPropertyAttachPosition) iblockdata.getValue(BlockAttachable.FACE)) {
            case CEILING:
                return EnumDirection.DOWN;
            case FLOOR:
                return EnumDirection.UP;
            default:
                return (EnumDirection) iblockdata.getValue(BlockAttachable.FACING);
        }
    }
}
