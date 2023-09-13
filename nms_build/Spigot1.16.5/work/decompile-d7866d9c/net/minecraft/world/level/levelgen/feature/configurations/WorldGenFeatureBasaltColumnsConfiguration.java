package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.IntSpread;

public class WorldGenFeatureBasaltColumnsConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureBasaltColumnsConfiguration> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(IntSpread.a(0, 2, 1).fieldOf("reach").forGetter((worldgenfeaturebasaltcolumnsconfiguration) -> {
            return worldgenfeaturebasaltcolumnsconfiguration.b;
        }), IntSpread.a(1, 5, 5).fieldOf("height").forGetter((worldgenfeaturebasaltcolumnsconfiguration) -> {
            return worldgenfeaturebasaltcolumnsconfiguration.c;
        })).apply(instance, WorldGenFeatureBasaltColumnsConfiguration::new);
    });
    private final IntSpread b;
    private final IntSpread c;

    public WorldGenFeatureBasaltColumnsConfiguration(IntSpread intspread, IntSpread intspread1) {
        this.b = intspread;
        this.c = intspread1;
    }

    public IntSpread am_() {
        return this.b;
    }

    public IntSpread b() {
        return this.c;
    }
}
