package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;

public class WorldGenDecoratorFrequencyConfiguration implements WorldGenFeatureDecoratorConfiguration, WorldGenFeatureConfiguration {

    public static final Codec<WorldGenDecoratorFrequencyConfiguration> CODEC = IntProvider.b(0, 256).fieldOf("count").xmap(WorldGenDecoratorFrequencyConfiguration::new, WorldGenDecoratorFrequencyConfiguration::a).codec();
    private final IntProvider count;

    public WorldGenDecoratorFrequencyConfiguration(int i) {
        this.count = ConstantInt.a(i);
    }

    public WorldGenDecoratorFrequencyConfiguration(IntProvider intprovider) {
        this.count = intprovider;
    }

    public IntProvider a() {
        return this.count;
    }
}
