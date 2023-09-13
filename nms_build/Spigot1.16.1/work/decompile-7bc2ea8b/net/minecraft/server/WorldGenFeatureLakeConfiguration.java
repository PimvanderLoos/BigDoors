package net.minecraft.server;

import com.mojang.serialization.Codec;

public class WorldGenFeatureLakeConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureLakeConfiguration> a = IBlockData.b.fieldOf("state").xmap(WorldGenFeatureLakeConfiguration::new, (worldgenfeaturelakeconfiguration) -> {
        return worldgenfeaturelakeconfiguration.b;
    }).codec();
    public final IBlockData b;

    public WorldGenFeatureLakeConfiguration(IBlockData iblockdata) {
        this.b = iblockdata;
    }
}
