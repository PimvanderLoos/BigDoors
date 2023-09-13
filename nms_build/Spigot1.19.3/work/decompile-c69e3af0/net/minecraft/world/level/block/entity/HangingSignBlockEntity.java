package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.block.state.IBlockData;

public class HangingSignBlockEntity extends TileEntitySign {

    private static final int MAX_TEXT_LINE_WIDTH = 50;
    private static final int TEXT_LINE_HEIGHT = 9;

    public HangingSignBlockEntity(BlockPosition blockposition, IBlockData iblockdata) {
        super(TileEntityTypes.HANGING_SIGN, blockposition, iblockdata);
    }

    @Override
    public int getTextLineHeight() {
        return 9;
    }

    @Override
    public int getMaxTextLineWidth() {
        return 50;
    }
}
