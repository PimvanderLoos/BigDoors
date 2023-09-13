package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;

public abstract class WorldGenFeatureStateProvider {

    public static final Codec<WorldGenFeatureStateProvider> CODEC = BuiltInRegistries.BLOCKSTATE_PROVIDER_TYPE.byNameCodec().dispatch(WorldGenFeatureStateProvider::type, WorldGenFeatureStateProviders::codec);

    public WorldGenFeatureStateProvider() {}

    public static WorldGenFeatureStateProviderSimpl simple(IBlockData iblockdata) {
        return new WorldGenFeatureStateProviderSimpl(iblockdata);
    }

    public static WorldGenFeatureStateProviderSimpl simple(Block block) {
        return new WorldGenFeatureStateProviderSimpl(block.defaultBlockState());
    }

    protected abstract WorldGenFeatureStateProviders<?> type();

    public abstract IBlockData getState(RandomSource randomsource, BlockPosition blockposition);
}
