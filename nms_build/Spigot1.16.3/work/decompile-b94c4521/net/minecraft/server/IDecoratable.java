package net.minecraft.server;

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
