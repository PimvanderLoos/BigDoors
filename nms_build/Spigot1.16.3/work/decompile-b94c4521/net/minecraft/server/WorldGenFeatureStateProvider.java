package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;

public abstract class WorldGenFeatureStateProvider {

    public static final Codec<WorldGenFeatureStateProvider> a = IRegistry.BLOCK_STATE_PROVIDER_TYPE.dispatch(WorldGenFeatureStateProvider::a, WorldGenFeatureStateProviders::a);

    public WorldGenFeatureStateProvider() {}

    protected abstract WorldGenFeatureStateProviders<?> a();

    public abstract IBlockData a(Random random, BlockPosition blockposition);
}
