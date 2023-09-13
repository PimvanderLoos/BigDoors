package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.state.IBlockData;

public class WorldGenFeatureFillConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureFillConfiguration> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.intRange(0, 255).fieldOf("height").forGetter((worldgenfeaturefillconfiguration) -> {
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
