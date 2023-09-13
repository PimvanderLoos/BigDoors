package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;

public class WorldGenFeatureStateProviderRotatedBlock extends WorldGenFeatureStateProvider {

    public static final Codec<WorldGenFeatureStateProviderRotatedBlock> b = IBlockData.b.fieldOf("state").xmap(BlockBase.BlockData::getBlock, Block::getBlockData).xmap(WorldGenFeatureStateProviderRotatedBlock::new, (worldgenfeaturestateproviderrotatedblock) -> {
        return worldgenfeaturestateproviderrotatedblock.c;
    }).codec();
    private final Block c;

    public WorldGenFeatureStateProviderRotatedBlock(Block block) {
        this.c = block;
    }

    @Override
    protected WorldGenFeatureStateProviders<?> a() {
        return WorldGenFeatureStateProviders.e;
    }

    @Override
    public IBlockData a(Random random, BlockPosition blockposition) {
        EnumDirection.EnumAxis enumdirection_enumaxis = EnumDirection.EnumAxis.a(random);

        return (IBlockData) this.c.getBlockData().set(BlockRotatable.AXIS, enumdirection_enumaxis);
    }
}
