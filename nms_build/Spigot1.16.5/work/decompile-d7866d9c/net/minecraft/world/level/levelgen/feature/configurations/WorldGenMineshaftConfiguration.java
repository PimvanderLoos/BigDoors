package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.WorldGenMineshaft;

public class WorldGenMineshaftConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenMineshaftConfiguration> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.floatRange(0.0F, 1.0F).fieldOf("probability").forGetter((worldgenmineshaftconfiguration) -> {
            return worldgenmineshaftconfiguration.b;
        }), WorldGenMineshaft.Type.c.fieldOf("type").forGetter((worldgenmineshaftconfiguration) -> {
            return worldgenmineshaftconfiguration.c;
        })).apply(instance, WorldGenMineshaftConfiguration::new);
    });
    public final float b;
    public final WorldGenMineshaft.Type c;

    public WorldGenMineshaftConfiguration(float f, WorldGenMineshaft.Type worldgenmineshaft_type) {
        this.b = f;
        this.c = worldgenmineshaft_type;
    }
}
