package net.minecraft.world.level.block;

import net.minecraft.core.EnumDirection;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateEnum;

public class BlockRotatable extends Block {

    public static final BlockStateEnum<EnumDirection.EnumAxis> AXIS = BlockProperties.AXIS;

    public BlockRotatable(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.registerDefaultState((IBlockData) this.defaultBlockState().setValue(BlockRotatable.AXIS, EnumDirection.EnumAxis.Y));
    }

    @Override
    public IBlockData rotate(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return rotatePillar(iblockdata, enumblockrotation);
    }

    public static IBlockData rotatePillar(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        switch (enumblockrotation) {
            case COUNTERCLOCKWISE_90:
            case CLOCKWISE_90:
                switch ((EnumDirection.EnumAxis) iblockdata.getValue(BlockRotatable.AXIS)) {
                    case X:
                        return (IBlockData) iblockdata.setValue(BlockRotatable.AXIS, EnumDirection.EnumAxis.Z);
                    case Z:
                        return (IBlockData) iblockdata.setValue(BlockRotatable.AXIS, EnumDirection.EnumAxis.X);
                    default:
                        return iblockdata;
                }
            default:
                return iblockdata;
        }
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockRotatable.AXIS);
    }

    @Override
    public IBlockData getStateForPlacement(BlockActionContext blockactioncontext) {
        return (IBlockData) this.defaultBlockState().setValue(BlockRotatable.AXIS, blockactioncontext.getClickedFace().getAxis());
    }
}
