package net.minecraft.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class WorldGenDecoratorCarveMaskConfiguration implements WorldGenFeatureDecoratorConfiguration {

    public static final Codec<WorldGenDecoratorCarveMaskConfiguration> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(WorldGenStage.Features.c.fieldOf("step").forGetter((worldgendecoratorcarvemaskconfiguration) -> {
            return worldgendecoratorcarvemaskconfiguration.b;
        }), Codec.FLOAT.fieldOf("probability").forGetter((worldgendecoratorcarvemaskconfiguration) -> {
            return worldgendecoratorcarvemaskconfiguration.c;
        })).apply(instance, WorldGenDecoratorCarveMaskConfiguration::new);
    });
    protected final WorldGenStage.Features b;
    protected final float c;

    public WorldGenDecoratorCarveMaskConfiguration(WorldGenStage.Features worldgenstage_features, float f) {
        this.b = worldgenstage_features;
        this.c = f;
    }
}
