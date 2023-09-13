package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;

public class WorldGenFeatureStateProviderSimpl extends WorldGenFeatureStateProvider {

    public static final Codec<WorldGenFeatureStateProviderSimpl> b = IBlockData.b.fieldOf("state").xmap(WorldGenFeatureStateProviderSimpl::new, (worldgenfeaturestateprovidersimpl) -> {
        return worldgenfeaturestateprovidersimpl.c;
    }).codec();
    private final IBlockData c;

    public WorldGenFeatureStateProviderSimpl(IBlockData iblockdata) {
        this.c = iblockdata;
    }

    @Override
    protected WorldGenFeatureStateProviders<?> a() {
        return WorldGenFeatureStateProviders.a;
    }

    @Override
    public IBlockData a(Random random, BlockPosition blockposition) {
        return this.c;
    }
}
