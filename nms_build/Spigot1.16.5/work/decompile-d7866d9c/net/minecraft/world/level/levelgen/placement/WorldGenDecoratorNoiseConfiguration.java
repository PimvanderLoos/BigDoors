package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureDecoratorConfiguration;

public class WorldGenDecoratorNoiseConfiguration implements WorldGenFeatureDecoratorConfiguration {

    public static final Codec<WorldGenDecoratorNoiseConfiguration> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.INT.fieldOf("noise_to_count_ratio").forGetter((worldgendecoratornoiseconfiguration) -> {
            return worldgendecoratornoiseconfiguration.c;
        }), Codec.DOUBLE.fieldOf("noise_factor").forGetter((worldgendecoratornoiseconfiguration) -> {
            return worldgendecoratornoiseconfiguration.d;
        }), Codec.DOUBLE.fieldOf("noise_offset").orElse(0.0D).forGetter((worldgendecoratornoiseconfiguration) -> {
            return worldgendecoratornoiseconfiguration.e;
        })).apply(instance, WorldGenDecoratorNoiseConfiguration::new);
    });
    public final int c;
    public final double d;
    public final double e;

    public WorldGenDecoratorNoiseConfiguration(int i, double d0, double d1) {
        this.c = i;
        this.d = d0;
        this.e = d1;
    }
}
