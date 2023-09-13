package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.WorldGenMineshaft;

public class WorldGenMineshaftConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenMineshaftConfiguration> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.floatRange(0.0F, 1.0F).fieldOf("probability").forGetter((worldgenmineshaftconfiguration) -> {
            return worldgenmineshaftconfiguration.probability;
        }), WorldGenMineshaft.Type.CODEC.fieldOf("type").forGetter((worldgenmineshaftconfiguration) -> {
            return worldgenmineshaftconfiguration.type;
        })).apply(instance, WorldGenMineshaftConfiguration::new);
    });
    public final float probability;
    public final WorldGenMineshaft.Type type;

    public WorldGenMineshaftConfiguration(float f, WorldGenMineshaft.Type worldgenmineshaft_type) {
        this.probability = f;
        this.type = worldgenmineshaft_type;
    }
}
