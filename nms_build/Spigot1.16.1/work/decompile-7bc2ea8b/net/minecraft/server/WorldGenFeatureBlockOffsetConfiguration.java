package net.minecraft.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class WorldGenFeatureBlockOffsetConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureBlockOffsetConfiguration> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(IBlockData.b.fieldOf("state").forGetter((worldgenfeatureblockoffsetconfiguration) -> {
            return worldgenfeatureblockoffsetconfiguration.b;
        }), Codec.INT.fieldOf("start_radius").withDefault(0).forGetter((worldgenfeatureblockoffsetconfiguration) -> {
            return worldgenfeatureblockoffsetconfiguration.c;
        })).apply(instance, WorldGenFeatureBlockOffsetConfiguration::new);
    });
    public final IBlockData b;
    public final int c;

    public WorldGenFeatureBlockOffsetConfiguration(IBlockData iblockdata, int i) {
        this.b = iblockdata;
        this.c = i;
    }
}
