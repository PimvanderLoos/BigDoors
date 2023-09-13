package net.minecraft.server;

import java.util.Random;

public abstract class WorldGenFeatureStateProvider implements MinecraftSerializable {

    protected final WorldGenFeatureStateProviders<?> a;

    protected WorldGenFeatureStateProvider(WorldGenFeatureStateProviders<?> worldgenfeaturestateproviders) {
        this.a = worldgenfeaturestateproviders;
    }

    public abstract IBlockData a(Random random, BlockPosition blockposition);
}
