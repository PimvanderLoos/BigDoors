package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.WorldGenStage;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureDecoratorConfiguration;

public class WorldGenDecoratorCarveMaskConfiguration implements WorldGenFeatureDecoratorConfiguration {

    public static final Codec<WorldGenDecoratorCarveMaskConfiguration> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(WorldGenStage.Features.c.fieldOf("step").forGetter((worldgendecoratorcarvemaskconfiguration) -> {
            return worldgendecoratorcarvemaskconfiguration.c;
        }), Codec.FLOAT.fieldOf("probability").forGetter((worldgendecoratorcarvemaskconfiguration) -> {
            return worldgendecoratorcarvemaskconfiguration.d;
        })).apply(instance, WorldGenDecoratorCarveMaskConfiguration::new);
    });
    protected final WorldGenStage.Features c;
    protected final float d;

    public WorldGenDecoratorCarveMaskConfiguration(WorldGenStage.Features worldgenstage_features, float f) {
        this.c = worldgenstage_features;
        this.d = f;
    }
}
