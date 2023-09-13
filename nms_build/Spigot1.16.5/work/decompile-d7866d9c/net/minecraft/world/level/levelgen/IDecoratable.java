package net.minecraft.world.level.levelgen;

import net.minecraft.util.IntSpread;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenDecoratorFrequencyConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureChanceDecoratorRangeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration2;
import net.minecraft.world.level.levelgen.placement.WorldGenDecorator;
import net.minecraft.world.level.levelgen.placement.WorldGenDecoratorConfigured;
import net.minecraft.world.level.levelgen.placement.WorldGenDecoratorDungeonConfiguration;

public interface IDecoratable<R> {

    R a(WorldGenDecoratorConfigured<?> worldgendecoratorconfigured);

    default R a(int i) {
        return this.a(WorldGenDecorator.b.b(new WorldGenDecoratorDungeonConfiguration(i)));
    }

    default R a(IntSpread intspread) {
        return this.a(WorldGenDecorator.c.b(new WorldGenDecoratorFrequencyConfiguration(intspread)));
    }

    default R b(int i) {
        return this.a(IntSpread.a(i));
    }

    default R c(int i) {
        return this.a(IntSpread.a(0, i));
    }

    default R d(int i) {
        return this.a(WorldGenDecorator.l.b(new WorldGenFeatureChanceDecoratorRangeConfiguration(0, 0, i)));
    }

    default R a() {
        return this.a(WorldGenDecorator.g.b(WorldGenFeatureEmptyConfiguration2.c));
    }
}
