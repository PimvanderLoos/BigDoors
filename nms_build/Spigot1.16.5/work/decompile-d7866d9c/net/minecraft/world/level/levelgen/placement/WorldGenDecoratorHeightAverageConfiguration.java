package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureDecoratorConfiguration;

public class WorldGenDecoratorHeightAverageConfiguration implements WorldGenFeatureDecoratorConfiguration {

    public static final Codec<WorldGenDecoratorHeightAverageConfiguration> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.INT.fieldOf("baseline").forGetter((worldgendecoratorheightaverageconfiguration) -> {
            return worldgendecoratorheightaverageconfiguration.c;
        }), Codec.INT.fieldOf("spread").forGetter((worldgendecoratorheightaverageconfiguration) -> {
            return worldgendecoratorheightaverageconfiguration.d;
        })).apply(instance, WorldGenDecoratorHeightAverageConfiguration::new);
    });
    public final int c;
    public final int d;

    public WorldGenDecoratorHeightAverageConfiguration(int i, int j) {
        this.c = i;
        this.d = j;
    }
}
