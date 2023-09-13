package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.IBlockData;

public class WorldGenFeatureStateProviderSimpl extends WorldGenFeatureStateProvider {

    public static final Codec<WorldGenFeatureStateProviderSimpl> CODEC = IBlockData.CODEC.fieldOf("state").xmap(WorldGenFeatureStateProviderSimpl::new, (worldgenfeaturestateprovidersimpl) -> {
        return worldgenfeaturestateprovidersimpl.state;
    }).codec();
    private final IBlockData state;

    protected WorldGenFeatureStateProviderSimpl(IBlockData iblockdata) {
        this.state = iblockdata;
    }

    @Override
    protected WorldGenFeatureStateProviders<?> type() {
        return WorldGenFeatureStateProviders.SIMPLE_STATE_PROVIDER;
    }

    @Override
    public IBlockData getState(RandomSource randomsource, BlockPosition blockposition) {
        return this.state;
    }
}
