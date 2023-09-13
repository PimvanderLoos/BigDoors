package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.HeightMap;

public class HeightmapConfiguration implements WorldGenFeatureDecoratorConfiguration {

    public static final Codec<HeightmapConfiguration> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(HeightMap.Type.CODEC.fieldOf("heightmap").forGetter((heightmapconfiguration) -> {
            return heightmapconfiguration.heightmap;
        })).apply(instance, HeightmapConfiguration::new);
    });
    public final HeightMap.Type heightmap;

    public HeightmapConfiguration(HeightMap.Type heightmap_type) {
        this.heightmap = heightmap_type;
    }
}
