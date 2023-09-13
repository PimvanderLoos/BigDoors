package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureDecoratorConfiguration;

public class WorldGenDecoratorFrequencyExtraChanceConfiguration implements WorldGenFeatureDecoratorConfiguration {

    public static final Codec<WorldGenDecoratorFrequencyExtraChanceConfiguration> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.INT.fieldOf("count").forGetter((worldgendecoratorfrequencyextrachanceconfiguration) -> {
            return worldgendecoratorfrequencyextrachanceconfiguration.count;
        }), Codec.FLOAT.fieldOf("extra_chance").forGetter((worldgendecoratorfrequencyextrachanceconfiguration) -> {
            return worldgendecoratorfrequencyextrachanceconfiguration.extraChance;
        }), Codec.INT.fieldOf("extra_count").forGetter((worldgendecoratorfrequencyextrachanceconfiguration) -> {
            return worldgendecoratorfrequencyextrachanceconfiguration.extraCount;
        })).apply(instance, WorldGenDecoratorFrequencyExtraChanceConfiguration::new);
    });
    public final int count;
    public final float extraChance;
    public final int extraCount;

    public WorldGenDecoratorFrequencyExtraChanceConfiguration(int i, float f, int j) {
        this.count = i;
        this.extraChance = f;
        this.extraCount = j;
    }
}
