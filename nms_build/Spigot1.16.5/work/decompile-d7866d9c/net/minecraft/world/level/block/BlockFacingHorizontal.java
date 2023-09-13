package net.minecraft.world.level.block;

import net.minecraft.core.EnumDirection;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateDirection;

public abstract class BlockFacingHorizontal extends Block {

    public static final BlockStateDirection FACING = BlockProperties.O;

    protected BlockFacingHorizontal(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.set(BlockFacingHorizontal.FACING, enumblockrotation.a((EnumDirection) iblockdata.get(BlockFacingHorizontal.FACING)));
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.a(enumblockmirror.a((EnumDirection) iblockdata.get(BlockFacingHorizontal.FACING)));
    }
}
