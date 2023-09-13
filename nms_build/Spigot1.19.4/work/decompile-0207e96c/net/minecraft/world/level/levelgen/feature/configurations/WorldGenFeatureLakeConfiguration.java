package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.block.state.IBlockData;

public class WorldGenFeatureLakeConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureLakeConfiguration> CODEC = IBlockData.CODEC.fieldOf("state").xmap(WorldGenFeatureLakeConfiguration::new, (worldgenfeaturelakeconfiguration) -> {
        return worldgenfeaturelakeconfiguration.state;
    }).codec();
    public final IBlockData state;

    public WorldGenFeatureLakeConfiguration(IBlockData iblockdata) {
        this.state = iblockdata;
    }
}
