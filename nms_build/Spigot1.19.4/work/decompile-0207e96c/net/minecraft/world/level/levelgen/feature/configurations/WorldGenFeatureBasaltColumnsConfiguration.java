package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.IntProvider;

public class WorldGenFeatureBasaltColumnsConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureBasaltColumnsConfiguration> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(IntProvider.codec(0, 3).fieldOf("reach").forGetter((worldgenfeaturebasaltcolumnsconfiguration) -> {
            return worldgenfeaturebasaltcolumnsconfiguration.reach;
        }), IntProvider.codec(1, 10).fieldOf("height").forGetter((worldgenfeaturebasaltcolumnsconfiguration) -> {
            return worldgenfeaturebasaltcolumnsconfiguration.height;
        })).apply(instance, WorldGenFeatureBasaltColumnsConfiguration::new);
    });
    private final IntProvider reach;
    private final IntProvider height;

    public WorldGenFeatureBasaltColumnsConfiguration(IntProvider intprovider, IntProvider intprovider1) {
        this.reach = intprovider;
        this.height = intprovider1;
    }

    public IntProvider reach() {
        return this.reach;
    }

    public IntProvider height() {
        return this.height;
    }
}
