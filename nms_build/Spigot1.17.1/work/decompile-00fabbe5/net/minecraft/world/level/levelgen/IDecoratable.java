package net.minecraft.world.level.levelgen;

import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenDecoratorFrequencyConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureChanceDecoratorRangeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration2;
import net.minecraft.world.level.levelgen.heightproviders.TrapezoidHeight;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.placement.WorldGenDecorator;
import net.minecraft.world.level.levelgen.placement.WorldGenDecoratorConfigured;
import net.minecraft.world.level.levelgen.placement.WorldGenDecoratorDungeonConfiguration;

public interface IDecoratable<R> {

    R a(WorldGenDecoratorConfigured<?> worldgendecoratorconfigured);

    default R a(int i) {
        return this.a(WorldGenDecorator.CHANCE.a(new WorldGenDecoratorDungeonConfiguration(i)));
    }

    default R a(IntProvider intprovider) {
        return this.a(WorldGenDecorator.COUNT.a(new WorldGenDecoratorFrequencyConfiguration(intprovider)));
    }

    default R b(int i) {
        return this.a((IntProvider) ConstantInt.a(i));
    }

    default R c(int i) {
        return this.a((IntProvider) UniformInt.a(0, i));
    }

    default R a(VerticalAnchor verticalanchor, VerticalAnchor verticalanchor1) {
        return this.a(new WorldGenFeatureChanceDecoratorRangeConfiguration(UniformHeight.a(verticalanchor, verticalanchor1)));
    }

    default R b(VerticalAnchor verticalanchor, VerticalAnchor verticalanchor1) {
        return this.a(new WorldGenFeatureChanceDecoratorRangeConfiguration(TrapezoidHeight.a(verticalanchor, verticalanchor1)));
    }

    default R a(WorldGenFeatureChanceDecoratorRangeConfiguration worldgenfeaturechancedecoratorrangeconfiguration) {
        return this.a(WorldGenDecorator.RANGE.a(worldgenfeaturechancedecoratorrangeconfiguration));
    }

    default R a() {
        return this.a(WorldGenDecorator.SQUARE.a(WorldGenFeatureEmptyConfiguration2.INSTANCE));
    }
}
