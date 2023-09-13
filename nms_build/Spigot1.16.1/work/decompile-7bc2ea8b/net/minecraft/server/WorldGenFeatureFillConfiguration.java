package net.minecraft.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class WorldGenFeatureFillConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureFillConfiguration> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.INT.fieldOf("height").forGetter((worldgenfeaturefillconfiguration) -> {
            return worldgenfeaturefillconfiguration.b;
        }), IBlockData.b.fieldOf("state").forGetter((worldgenfeaturefillconfiguration) -> {
            return worldgenfeaturefillconfiguration.c;
        })).apply(instance, WorldGenFeatureFillConfiguration::new);
    });
    public final int b;
    public final IBlockData c;

    public WorldGenFeatureFillConfiguration(int i, IBlockData iblockdata) {
        this.b = i;
        this.c = iblockdata;
    }
}
