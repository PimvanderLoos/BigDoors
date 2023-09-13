package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.world.level.block.state.IBlockData;

public abstract class WorldGenFeatureStateProvider {

    public static final Codec<WorldGenFeatureStateProvider> CODEC = IRegistry.BLOCKSTATE_PROVIDER_TYPES.dispatch(WorldGenFeatureStateProvider::a, WorldGenFeatureStateProviders::a);

    public WorldGenFeatureStateProvider() {}

    protected abstract WorldGenFeatureStateProviders<?> a();

    public abstract IBlockData a(Random random, BlockPosition blockposition);
}
