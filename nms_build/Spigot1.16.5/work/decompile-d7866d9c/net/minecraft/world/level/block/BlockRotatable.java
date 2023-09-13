package net.minecraft.world.level.block;

import net.minecraft.core.EnumDirection;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateEnum;

public class BlockRotatable extends Block {

    public static final BlockStateEnum<EnumDirection.EnumAxis> AXIS = BlockProperties.F;

    public BlockRotatable(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.j((IBlockData) this.getBlockData().set(BlockRotatable.AXIS, EnumDirection.EnumAxis.Y));
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        switch (enumblockrotation) {
            case COUNTERCLOCKWISE_90:
            case CLOCKWISE_90:
                switch ((EnumDirection.EnumAxis) iblockdata.get(BlockRotatable.AXIS)) {
                    case X:
                        return (IBlockData) iblockdata.set(BlockRotatable.AXIS, EnumDirection.EnumAxis.Z);
                    case Z:
                        return (IBlockData) iblockdata.set(BlockRotatable.AXIS, EnumDirection.EnumAxis.X);
                    default:
                        return iblockdata;
                }
            default:
                return iblockdata;
        }
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockRotatable.AXIS);
    }

    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        return (IBlockData) this.getBlockData().set(BlockRotatable.AXIS, blockactioncontext.getClickedFace().n());
    }
}
