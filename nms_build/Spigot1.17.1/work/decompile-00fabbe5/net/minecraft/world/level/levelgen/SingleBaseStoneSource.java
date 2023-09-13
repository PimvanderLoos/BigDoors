package net.minecraft.world.level.levelgen;

import net.minecraft.world.level.block.state.IBlockData;

public class SingleBaseStoneSource implements BaseStoneSource {

    private final IBlockData state;

    public SingleBaseStoneSource(IBlockData iblockdata) {
        this.state = iblockdata;
    }

    @Override
    public IBlockData getBaseBlock(int i, int j, int k) {
        return this.state;
    }
}
