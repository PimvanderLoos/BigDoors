package net.minecraft.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class WorldGenMineshaftConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenMineshaftConfiguration> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.DOUBLE.fieldOf("probability").forGetter((worldgenmineshaftconfiguration) -> {
            return worldgenmineshaftconfiguration.b;
        }), WorldGenMineshaft.Type.c.fieldOf("type").forGetter((worldgenmineshaftconfiguration) -> {
            return worldgenmineshaftconfiguration.c;
        })).apply(instance, WorldGenMineshaftConfiguration::new);
    });
    public final double b;
    public final WorldGenMineshaft.Type c;

    public WorldGenMineshaftConfiguration(double d0, WorldGenMineshaft.Type worldgenmineshaft_type) {
        this.b = d0;
        this.c = worldgenmineshaft_type;
    }
}
