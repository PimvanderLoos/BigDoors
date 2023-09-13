package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import net.minecraft.util.IntSpread;

public class WorldGenDecoratorFrequencyConfiguration implements WorldGenFeatureDecoratorConfiguration, WorldGenFeatureConfiguration {

    public static final Codec<WorldGenDecoratorFrequencyConfiguration> a = IntSpread.a(-10, 128, 128).fieldOf("count").xmap(WorldGenDecoratorFrequencyConfiguration::new, WorldGenDecoratorFrequencyConfiguration::a).codec();
    private final IntSpread c;

    public WorldGenDecoratorFrequencyConfiguration(int i) {
        this.c = IntSpread.a(i);
    }

    public WorldGenDecoratorFrequencyConfiguration(IntSpread intspread) {
        this.c = intspread;
    }

    public IntSpread a() {
        return this.c;
    }
}
