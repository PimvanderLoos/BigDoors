package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenDecoratorFrequencyConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureChanceDecoratorRangeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureDecoratorConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureDecoratorNoiseConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration2;
import net.minecraft.world.level.levelgen.placement.nether.WorldGenDecoratorCountMultilayer;
import net.minecraft.world.level.levelgen.placement.nether.WorldGenDecoratorNetherFire;
import net.minecraft.world.level.levelgen.placement.nether.WorldGenDecoratorNetherGlowstone;
import net.minecraft.world.level.levelgen.placement.nether.WorldGenDecoratorNetherMagma;

public abstract class WorldGenDecorator<DC extends WorldGenFeatureDecoratorConfiguration> {

    public static final WorldGenDecorator<WorldGenFeatureEmptyConfiguration2> a = a("nope", new WorldGenDecoratorEmpty(WorldGenFeatureEmptyConfiguration2.a));
    public static final WorldGenDecorator<WorldGenDecoratorDungeonConfiguration> b = a("chance", new WorldGenDecoratorChance(WorldGenDecoratorDungeonConfiguration.a));
    public static final WorldGenDecorator<WorldGenDecoratorFrequencyConfiguration> c = a("count", new WorldGenDecoratorCount(WorldGenDecoratorFrequencyConfiguration.a));
    public static final WorldGenDecorator<WorldGenFeatureDecoratorNoiseConfiguration> d = a("count_noise", new WorldGenDecoratorCountNoise(WorldGenFeatureDecoratorNoiseConfiguration.a));
    public static final WorldGenDecorator<WorldGenDecoratorNoiseConfiguration> e = a("count_noise_biased", new WorldGenDecoratorCountNoiseBiased(WorldGenDecoratorNoiseConfiguration.a));
    public static final WorldGenDecorator<WorldGenDecoratorFrequencyExtraChanceConfiguration> f = a("count_extra", new WorldGenDecoratorCountExtra(WorldGenDecoratorFrequencyExtraChanceConfiguration.a));
    public static final WorldGenDecorator<WorldGenFeatureEmptyConfiguration2> g = a("square", new WorldGenDecoratorSquare(WorldGenFeatureEmptyConfiguration2.a));
    public static final WorldGenDecorator<WorldGenFeatureEmptyConfiguration2> h = a("heightmap", new WorldGenDecoratorHeightmap<>(WorldGenFeatureEmptyConfiguration2.a));
    public static final WorldGenDecorator<WorldGenFeatureEmptyConfiguration2> i = a("heightmap_spread_double", new WorldGenDecoratorHeightmapSpreadDouble<>(WorldGenFeatureEmptyConfiguration2.a));
    public static final WorldGenDecorator<WorldGenFeatureEmptyConfiguration2> j = a("top_solid_heightmap", new WorldGenDecoratorSolidTop(WorldGenFeatureEmptyConfiguration2.a));
    public static final WorldGenDecorator<WorldGenFeatureEmptyConfiguration2> k = a("heightmap_world_surface", new WorldGenDecoratorHeightmapWorldSurface(WorldGenFeatureEmptyConfiguration2.a));
    public static final WorldGenDecorator<WorldGenFeatureChanceDecoratorRangeConfiguration> l = a("range", new WorldGenDecoratorRange(WorldGenFeatureChanceDecoratorRangeConfiguration.a));
    public static final WorldGenDecorator<WorldGenFeatureChanceDecoratorRangeConfiguration> m = a("range_biased", new WorldGenDecoratorRangeBiased(WorldGenFeatureChanceDecoratorRangeConfiguration.a));
    public static final WorldGenDecorator<WorldGenFeatureChanceDecoratorRangeConfiguration> n = a("range_very_biased", new WorldGenDecoratorRangeVeryBiased(WorldGenFeatureChanceDecoratorRangeConfiguration.a));
    public static final WorldGenDecorator<WorldGenDecoratorHeightAverageConfiguration> o = a("depth_average", new WorldGenDecoratorDepthAverage(WorldGenDecoratorHeightAverageConfiguration.a));
    public static final WorldGenDecorator<WorldGenFeatureEmptyConfiguration2> p = a("spread_32_above", new WorldGenDecoratorSpread32Above(WorldGenFeatureEmptyConfiguration2.a));
    public static final WorldGenDecorator<WorldGenDecoratorCarveMaskConfiguration> q = a("carving_mask", new WorldGenDecoratorCarveMask(WorldGenDecoratorCarveMaskConfiguration.a));
    public static final WorldGenDecorator<WorldGenDecoratorFrequencyConfiguration> r = a("fire", new WorldGenDecoratorNetherFire(WorldGenDecoratorFrequencyConfiguration.a));
    public static final WorldGenDecorator<WorldGenFeatureEmptyConfiguration2> s = a("magma", new WorldGenDecoratorNetherMagma(WorldGenFeatureEmptyConfiguration2.a));
    public static final WorldGenDecorator<WorldGenFeatureEmptyConfiguration2> t = a("emerald_ore", new WorldGenDecoratorEmerald(WorldGenFeatureEmptyConfiguration2.a));
    public static final WorldGenDecorator<WorldGenDecoratorDungeonConfiguration> u = a("lava_lake", new WorldGenDecoratorLakeLava(WorldGenDecoratorDungeonConfiguration.a));
    public static final WorldGenDecorator<WorldGenDecoratorDungeonConfiguration> v = a("water_lake", new WorldGenDecoratorLakeWater(WorldGenDecoratorDungeonConfiguration.a));
    public static final WorldGenDecorator<WorldGenDecoratorFrequencyConfiguration> w = a("glowstone", new WorldGenDecoratorNetherGlowstone(WorldGenDecoratorFrequencyConfiguration.a));
    public static final WorldGenDecorator<WorldGenFeatureEmptyConfiguration2> x = a("end_gateway", new WorldGenDecoratorEndGateway(WorldGenFeatureEmptyConfiguration2.a));
    public static final WorldGenDecorator<WorldGenFeatureEmptyConfiguration2> y = a("dark_oak_tree", new WorldGenDecoratorRoofedTree(WorldGenFeatureEmptyConfiguration2.a));
    public static final WorldGenDecorator<WorldGenFeatureEmptyConfiguration2> z = a("iceberg", new WorldGenDecoratorIceburg(WorldGenFeatureEmptyConfiguration2.a));
    public static final WorldGenDecorator<WorldGenFeatureEmptyConfiguration2> A = a("end_island", new WorldGenDecoratorEndIsland(WorldGenFeatureEmptyConfiguration2.a));
    public static final WorldGenDecorator<WorldGenDecoratorDecpratedConfiguration> B = a("decorated", new WorldGenDecoratorDecorated(WorldGenDecoratorDecpratedConfiguration.a));
    public static final WorldGenDecorator<WorldGenDecoratorFrequencyConfiguration> C = a("count_multilayer", new WorldGenDecoratorCountMultilayer(WorldGenDecoratorFrequencyConfiguration.a));
    private final Codec<WorldGenDecoratorConfigured<DC>> D;

    private static <T extends WorldGenFeatureDecoratorConfiguration, G extends WorldGenDecorator<T>> G a(String s, G g0) {
        return (WorldGenDecorator) IRegistry.a(IRegistry.DECORATOR, s, (Object) g0);
    }

    public WorldGenDecorator(Codec<DC> codec) {
        this.D = codec.fieldOf("config").xmap((worldgenfeaturedecoratorconfiguration) -> {
            return new WorldGenDecoratorConfigured<>(this, worldgenfeaturedecoratorconfiguration);
        }, WorldGenDecoratorConfigured::b).codec();
    }

    public WorldGenDecoratorConfigured<DC> b(DC dc) {
        return new WorldGenDecoratorConfigured<>(this, dc);
    }

    public Codec<WorldGenDecoratorConfigured<DC>> a() {
        return this.D;
    }

    public abstract Stream<BlockPosition> a(WorldGenDecoratorContext worldgendecoratorcontext, Random random, DC dc, BlockPosition blockposition);

    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(this.hashCode());
    }
}
