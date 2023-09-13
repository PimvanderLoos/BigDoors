package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.block.state.IBlockData;

public class TileEntityLightDetector extends TileEntity {

    public TileEntityLightDetector(BlockPosition blockposition, IBlockData iblockdata) {
        super(TileEntityTypes.DAYLIGHT_DETECTOR, blockposition, iblockdata);
    }
}
