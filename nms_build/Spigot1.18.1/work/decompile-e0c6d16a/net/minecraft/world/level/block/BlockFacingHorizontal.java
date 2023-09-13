package net.minecraft.world.level.block;

import net.minecraft.core.EnumDirection;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateDirection;

public abstract class BlockFacingHorizontal extends Block {

    public static final BlockStateDirection FACING = BlockProperties.HORIZONTAL_FACING;

    protected BlockFacingHorizontal(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public IBlockData rotate(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.setValue(BlockFacingHorizontal.FACING, enumblockrotation.rotate((EnumDirection) iblockdata.getValue(BlockFacingHorizontal.FACING)));
    }

    @Override
    public IBlockData mirror(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.rotate(enumblockmirror.getRotation((EnumDirection) iblockdata.getValue(BlockFacingHorizontal.FACING)));
    }
}
