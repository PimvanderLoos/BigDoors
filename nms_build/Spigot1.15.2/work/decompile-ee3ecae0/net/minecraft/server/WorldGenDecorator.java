package net.minecraft.server;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Stream;

public abstract class WorldGenDecorator<DC extends WorldGenFeatureDecoratorConfiguration> {

    public static final WorldGenDecorator<WorldGenFeatureEmptyConfiguration2> a = a("nope", new WorldGenDecoratorEmpty(WorldGenFeatureEmptyConfiguration2::a));
    public static final WorldGenDecorator<WorldGenDecoratorFrequencyConfiguration> b = a("count_heightmap", new WorldGenDecoratorHeight(WorldGenDecoratorFrequencyConfiguration::a));
    public static final WorldGenDecorator<WorldGenDecoratorFrequencyConfiguration> c = a("count_top_solid", new WorldGenDecoratorSkyVisible(WorldGenDecoratorFrequencyConfiguration::a));
    public static final WorldGenDecorator<WorldGenDecoratorFrequencyConfiguration> d = a("count_heightmap_32", new WorldGenDecoratorHeight32(WorldGenDecoratorFrequencyConfiguration::a));
    public static final WorldGenDecorator<WorldGenDecoratorFrequencyConfiguration> e = a("count_heightmap_double", new WorldGenDecoratorHeightDouble(WorldGenDecoratorFrequencyConfiguration::a));
    public static final WorldGenDecorator<WorldGenDecoratorFrequencyConfiguration> f = a("count_height_64", new WorldGenDecoratorHeight64(WorldGenDecoratorFrequencyConfiguration::a));
    public static final WorldGenDecorator<WorldGenFeatureDecoratorNoiseConfiguration> g = a("noise_heightmap_32", new WorldGenDecoratorNoiseHeight32(WorldGenFeatureDecoratorNoiseConfiguration::a));
    public static final WorldGenDecorator<WorldGenFeatureDecoratorNoiseConfiguration> h = a("noise_heightmap_double", new WorldGenDecoratorNoiseHeightDouble(WorldGenFeatureDecoratorNoiseConfiguration::a));
    public static final WorldGenDecorator<WorldGenDecoratorDungeonConfiguration> i = a("chance_heightmap", new WorldGenDecoratorChance(WorldGenDecoratorDungeonConfiguration::a));
    public static final WorldGenDecorator<WorldGenDecoratorDungeonConfiguration> j = a("chance_heightmap_double", new WorldGenDecoratorChanceHeight(WorldGenDecoratorDungeonConfiguration::a));
    public static final WorldGenDecorator<WorldGenDecoratorDungeonConfiguration> k = a("chance_passthrough", new WorldGenDecoratorChancePass(WorldGenDecoratorDungeonConfiguration::a));
    public static final WorldGenDecorator<WorldGenDecoratorDungeonConfiguration> l = a("chance_top_solid_heightmap", new WorldGenDecoratorSkyVisibleBiased(WorldGenDecoratorDungeonConfiguration::a));
    public static final WorldGenDecorator<WorldGenDecoratorFrequencyExtraChanceConfiguration> m = a("count_extra_heightmap", new WorldGenDecoratorHeightExtraChance(WorldGenDecoratorFrequencyExtraChanceConfiguration::a));
    public static final WorldGenDecorator<WorldGenFeatureChanceDecoratorCountConfiguration> n = a("count_range", new WorldGenDecoratorNetherHeight(WorldGenFeatureChanceDecoratorCountConfiguration::a));
    public static final WorldGenDecorator<WorldGenFeatureChanceDecoratorCountConfiguration> o = a("count_biased_range", new WorldGenDecoratorHeightBiased(WorldGenFeatureChanceDecoratorCountConfiguration::a));
    public static final WorldGenDecorator<WorldGenFeatureChanceDecoratorCountConfiguration> p = a("count_very_biased_range", new WorldGenDecoratorHeightBiased2(WorldGenFeatureChanceDecoratorCountConfiguration::a));
    public static final WorldGenDecorator<WorldGenFeatureChanceDecoratorCountConfiguration> q = a("random_count_range", new WorldGenDecoratorNetherRandomCount(WorldGenFeatureChanceDecoratorCountConfiguration::a));
    public static final WorldGenDecorator<WorldGenFeatureChanceDecoratorRangeConfiguration> r = a("chance_range", new WorldGenDecoratorNetherChance(WorldGenFeatureChanceDecoratorRangeConfiguration::a));
    public static final WorldGenDecorator<WorldGenDecoratorFrequencyChanceConfiguration> s = a("count_chance_heightmap", new WorldGenFeatureChanceDecorator(WorldGenDecoratorFrequencyChanceConfiguration::a));
    public static final WorldGenDecorator<WorldGenDecoratorFrequencyChanceConfiguration> t = a("count_chance_heightmap_double", new WorldGenFeatureChanceDecoratorHeight(WorldGenDecoratorFrequencyChanceConfiguration::a));
    public static final WorldGenDecorator<WorldGenDecoratorHeightAverageConfiguration> u = a("count_depth_average", new WorldGenDecoratorHeightAverage(WorldGenDecoratorHeightAverageConfiguration::a));
    public static final WorldGenDecorator<WorldGenFeatureEmptyConfiguration2> v = a("top_solid_heightmap", new WorldGenDecoratorSolidTop(WorldGenFeatureEmptyConfiguration2::a));
    public static final WorldGenDecorator<WorldGenDecoratorRangeConfiguration> w = a("top_solid_heightmap_range", new WorldGenDecoratorSolidTopHeight(WorldGenDecoratorRangeConfiguration::a));
    public static final WorldGenDecorator<WorldGenDecoratorNoiseConfiguration> x = a("top_solid_heightmap_noise_biased", new WorldGenDecoratorSolidTopNoise(WorldGenDecoratorNoiseConfiguration::a));
    public static final WorldGenDecorator<WorldGenDecoratorCarveMaskConfiguration> y = a("carving_mask", new WorldGenDecoratorCarveMask(WorldGenDecoratorCarveMaskConfiguration::a));
    public static final WorldGenDecorator<WorldGenDecoratorFrequencyConfiguration> z = a("forest_rock", new WorldGenDecoratorForestRock(WorldGenDecoratorFrequencyConfiguration::a));
    public static final WorldGenDecorator<WorldGenDecoratorFrequencyConfiguration> A = a("hell_fire", new WorldGenDecoratorNetherFire(WorldGenDecoratorFrequencyConfiguration::a));
    public static final WorldGenDecorator<WorldGenDecoratorFrequencyConfiguration> B = a("magma", new WorldGenDecoratorNetherMagma(WorldGenDecoratorFrequencyConfiguration::a));
    public static final WorldGenDecorator<WorldGenFeatureEmptyConfiguration2> C = a("emerald_ore", new WorldGenDecoratorEmerald(WorldGenFeatureEmptyConfiguration2::a));
    public static final WorldGenDecorator<WorldGenDecoratorDungeonConfiguration> D = a("lava_lake", new WorldGenDecoratorLakeLava(WorldGenDecoratorDungeonConfiguration::a));
    public static final WorldGenDecorator<WorldGenDecoratorDungeonConfiguration> E = a("water_lake", new WorldGenDecoratorLakeWater(WorldGenDecoratorDungeonConfiguration::a));
    public static final WorldGenDecorator<WorldGenDecoratorDungeonConfiguration> F = a("dungeons", new WorldGenDecoratorDungeon(WorldGenDecoratorDungeonConfiguration::a));
    public static final WorldGenDecorator<WorldGenFeatureEmptyConfiguration2> G = a("dark_oak_tree", new WorldGenDecoratorRoofedTree(WorldGenFeatureEmptyConfiguration2::a));
    public static final WorldGenDecorator<WorldGenDecoratorDungeonConfiguration> H = a("iceberg", new WorldGenDecoratorIceburg(WorldGenDecoratorDungeonConfiguration::a));
    public static final WorldGenDecorator<WorldGenDecoratorFrequencyConfiguration> I = a("light_gem_chance", new WorldGenDecoratorNetherGlowstone(WorldGenDecoratorFrequencyConfiguration::a));
    public static final WorldGenDecorator<WorldGenFeatureEmptyConfiguration2> J = a("end_island", new WorldGenDecoratorEndIsland(WorldGenFeatureEmptyConfiguration2::a));
    public static final WorldGenDecorator<WorldGenFeatureEmptyConfiguration2> K = a("chorus_plant", new WorldGenDecoratorChorusPlant(WorldGenFeatureEmptyConfiguration2::a));
    public static final WorldGenDecorator<WorldGenFeatureEmptyConfiguration2> L = a("end_gateway", new WorldGenDecoratorEndGateway(WorldGenFeatureEmptyConfiguration2::a));
    private final Function<Dynamic<?>, ? extends DC> M;

    private static <T extends WorldGenFeatureDecoratorConfiguration, G extends WorldGenDecorator<T>> G a(String s, G g0) {
        return (WorldGenDecorator) IRegistry.a(IRegistry.DECORATOR, s, (Object) g0);
    }

    public WorldGenDecorator(Function<Dynamic<?>, ? extends DC> function) {
        this.M = function;
    }

    public DC a(Dynamic<?> dynamic) {
        return (WorldGenFeatureDecoratorConfiguration) this.M.apply(dynamic);
    }

    public WorldGenDecoratorConfigured<DC> a(DC dc) {
        return new WorldGenDecoratorConfigured<>(this, dc);
    }

    protected <FC extends WorldGenFeatureConfiguration, F extends WorldGenerator<FC>> boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettingsDefault> chunkgenerator, Random random, BlockPosition blockposition, DC dc, WorldGenFeatureConfigured<FC, F> worldgenfeatureconfigured) {
        AtomicBoolean atomicboolean = new AtomicBoolean(false);

        this.a(generatoraccess, chunkgenerator, random, dc, blockposition).forEach((blockposition1) -> {
            boolean flag = worldgenfeatureconfigured.a(generatoraccess, chunkgenerator, random, blockposition1);

            atomicboolean.set(atomicboolean.get() || flag);
        });
        return atomicboolean.get();
    }

    public abstract Stream<BlockPosition> a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettingsDefault> chunkgenerator, Random random, DC dc, BlockPosition blockposition);

    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(this.hashCode());
    }
}
