package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.block.state.IBlockData;

public class WorldGenFeatureStateProviderSimpl extends WorldGenFeatureStateProvider {

    public static final Codec<WorldGenFeatureStateProviderSimpl> CODEC = IBlockData.CODEC.fieldOf("state").xmap(WorldGenFeatureStateProviderSimpl::new, (worldgenfeaturestateprovidersimpl) -> {
        return worldgenfeaturestateprovidersimpl.state;
    }).codec();
    private final IBlockData state;

    public WorldGenFeatureStateProviderSimpl(IBlockData iblockdata) {
        this.state = iblockdata;
    }

    @Override
    protected WorldGenFeatureStateProviders<?> a() {
        return WorldGenFeatureStateProviders.SIMPLE_STATE_PROVIDER;
    }

    @Override
    public IBlockData a(Random random, BlockPosition blockposition) {
        return this.state;
    }
}
