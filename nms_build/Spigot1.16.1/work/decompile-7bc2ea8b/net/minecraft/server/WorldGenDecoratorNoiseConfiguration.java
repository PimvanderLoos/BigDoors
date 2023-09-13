package net.minecraft.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldGenDecoratorNoiseConfiguration implements WorldGenFeatureDecoratorConfiguration {

    public static final Codec<WorldGenDecoratorNoiseConfiguration> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.INT.fieldOf("noise_to_count_ratio").forGetter((worldgendecoratornoiseconfiguration) -> {
            return worldgendecoratornoiseconfiguration.b;
        }), Codec.DOUBLE.fieldOf("noise_factor").forGetter((worldgendecoratornoiseconfiguration) -> {
            return worldgendecoratornoiseconfiguration.c;
        }), Codec.DOUBLE.fieldOf("noise_offset").withDefault(0.0D).forGetter((worldgendecoratornoiseconfiguration) -> {
            return worldgendecoratornoiseconfiguration.d;
        }), HeightMap.Type.g.fieldOf("heightmap").forGetter((worldgendecoratornoiseconfiguration) -> {
            return worldgendecoratornoiseconfiguration.e;
        })).apply(instance, WorldGenDecoratorNoiseConfiguration::new);
    });
    private static final Logger LOGGER = LogManager.getLogger();
    public final int b;
    public final double c;
    public final double d;
    public final HeightMap.Type e;

    public WorldGenDecoratorNoiseConfiguration(int i, double d0, double d1, HeightMap.Type heightmap_type) {
        this.b = i;
        this.c = d0;
        this.d = d1;
        this.e = heightmap_type;
    }
}
