package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.stateproviders.WorldGenFeatureStateProvider;

public class WorldGenFeatureMushroomConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureMushroomConfiguration> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(WorldGenFeatureStateProvider.a.fieldOf("cap_provider").forGetter((worldgenfeaturemushroomconfiguration) -> {
            return worldgenfeaturemushroomconfiguration.b;
        }), WorldGenFeatureStateProvider.a.fieldOf("stem_provider").forGetter((worldgenfeaturemushroomconfiguration) -> {
            return worldgenfeaturemushroomconfiguration.c;
        }), Codec.INT.fieldOf("foliage_radius").orElse(2).forGetter((worldgenfeaturemushroomconfiguration) -> {
            return worldgenfeaturemushroomconfiguration.d;
        })).apply(instance, WorldGenFeatureMushroomConfiguration::new);
    });
    public final WorldGenFeatureStateProvider b;
    public final WorldGenFeatureStateProvider c;
    public final int d;

    public WorldGenFeatureMushroomConfiguration(WorldGenFeatureStateProvider worldgenfeaturestateprovider, WorldGenFeatureStateProvider worldgenfeaturestateprovider1, int i) {
        this.b = worldgenfeaturestateprovider;
        this.c = worldgenfeaturestateprovider1;
        this.d = i;
    }
}
