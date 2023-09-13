package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;

public class WorldGenDecoratorFrequencyConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenDecoratorFrequencyConfiguration> CODEC = IntProvider.codec(0, 256).fieldOf("count").xmap(WorldGenDecoratorFrequencyConfiguration::new, WorldGenDecoratorFrequencyConfiguration::count).codec();
    private final IntProvider count;

    public WorldGenDecoratorFrequencyConfiguration(int i) {
        this.count = ConstantInt.of(i);
    }

    public WorldGenDecoratorFrequencyConfiguration(IntProvider intprovider) {
        this.count = intprovider;
    }

    public IntProvider count() {
        return this.count;
    }
}
