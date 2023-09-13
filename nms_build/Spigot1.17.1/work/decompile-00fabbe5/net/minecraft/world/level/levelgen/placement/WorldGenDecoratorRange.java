package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureChanceDecoratorRangeConfiguration;

public class WorldGenDecoratorRange extends VerticalDecorator<WorldGenFeatureChanceDecoratorRangeConfiguration> {

    public WorldGenDecoratorRange(Codec<WorldGenFeatureChanceDecoratorRangeConfiguration> codec) {
        super(codec);
    }

    protected int a(WorldGenDecoratorContext worldgendecoratorcontext, Random random, WorldGenFeatureChanceDecoratorRangeConfiguration worldgenfeaturechancedecoratorrangeconfiguration, int i) {
        return worldgenfeaturechancedecoratorrangeconfiguration.height.a(random, worldgendecoratorcontext);
    }
}
