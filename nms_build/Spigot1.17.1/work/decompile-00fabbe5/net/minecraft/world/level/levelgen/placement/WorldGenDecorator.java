package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.world.level.levelgen.feature.configurations.HeightmapConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenDecoratorFrequencyConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureChanceDecoratorRangeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureDecoratorConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureDecoratorNoiseConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration2;
import net.minecraft.world.level.levelgen.placement.nether.WorldGenDecoratorCountMultilayer;

public abstract class WorldGenDecorator<DC extends WorldGenFeatureDecoratorConfiguration> {

    public static final WorldGenDecorator<WorldGenFeatureEmptyConfiguration2> NOPE = a("nope", new WorldGenDecoratorEmpty(WorldGenFeatureEmptyConfiguration2.CODEC));
    public static final WorldGenDecorator<WorldGenDecoratorDecpratedConfiguration> DECORATED = a("decorated", new WorldGenDecoratorDecorated(WorldGenDecoratorDecpratedConfiguration.CODEC));
    public static final WorldGenDecorator<WorldGenDecoratorCarveMaskConfiguration> CARVING_MASK = a("carving_mask", new WorldGenDecoratorCarveMask(WorldGenDecoratorCarveMaskConfiguration.CODEC));
    public static final WorldGenDecorator<WorldGenDecoratorFrequencyConfiguration> COUNT_MULTILAYER = a("count_multilayer", new WorldGenDecoratorCountMultilayer(WorldGenDecoratorFrequencyConfiguration.CODEC));
    public static final WorldGenDecorator<WorldGenFeatureEmptyConfiguration2> SQUARE = a("square", new WorldGenDecoratorSquare(WorldGenFeatureEmptyConfiguration2.CODEC));
    public static final WorldGenDecorator<WorldGenFeatureEmptyConfiguration2> DARK_OAK_TREE = a("dark_oak_tree", new WorldGenDecoratorRoofedTree(WorldGenFeatureEmptyConfiguration2.CODEC));
    public static final WorldGenDecorator<WorldGenFeatureEmptyConfiguration2> ICEBERG = a("iceberg", new WorldGenDecoratorIceburg(WorldGenFeatureEmptyConfiguration2.CODEC));
    public static final WorldGenDecorator<WorldGenDecoratorDungeonConfiguration> CHANCE = a("chance", new WorldGenDecoratorChance(WorldGenDecoratorDungeonConfiguration.CODEC));
    public static final WorldGenDecorator<WorldGenDecoratorFrequencyConfiguration> COUNT = a("count", new WorldGenDecoratorCount(WorldGenDecoratorFrequencyConfiguration.CODEC));
    public static final WorldGenDecorator<WorldGenFeatureDecoratorNoiseConfiguration> COUNT_NOISE = a("count_noise", new WorldGenDecoratorCountNoise(WorldGenFeatureDecoratorNoiseConfiguration.CODEC));
    public static final WorldGenDecorator<WorldGenDecoratorNoiseConfiguration> COUNT_NOISE_BIASED = a("count_noise_biased", new WorldGenDecoratorCountNoiseBiased(WorldGenDecoratorNoiseConfiguration.CODEC));
    public static final WorldGenDecorator<WorldGenDecoratorFrequencyExtraChanceConfiguration> COUNT_EXTRA = a("count_extra", new WorldGenDecoratorCountExtra(WorldGenDecoratorFrequencyExtraChanceConfiguration.CODEC));
    public static final WorldGenDecorator<WorldGenDecoratorDungeonConfiguration> LAVA_LAKE = a("lava_lake", new WorldGenDecoratorLakeLava(WorldGenDecoratorDungeonConfiguration.CODEC));
    public static final WorldGenDecorator<HeightmapConfiguration> HEIGHTMAP = a("heightmap", new WorldGenDecoratorHeightmap(HeightmapConfiguration.CODEC));
    public static final WorldGenDecorator<HeightmapConfiguration> HEIGHTMAP_SPREAD_DOUBLE = a("heightmap_spread_double", new WorldGenDecoratorHeightmapSpreadDouble(HeightmapConfiguration.CODEC));
    public static final WorldGenDecorator<WaterDepthThresholdConfiguration> WATER_DEPTH_THRESHOLD = a("water_depth_threshold", new WaterDepthThresholdDecorator(WaterDepthThresholdConfiguration.CODEC));
    public static final WorldGenDecorator<CaveDecoratorConfiguration> CAVE_SURFACE = a("cave_surface", new CaveSurfaceDecorator(CaveDecoratorConfiguration.CODEC));
    public static final WorldGenDecorator<WorldGenFeatureChanceDecoratorRangeConfiguration> RANGE = a("range", new WorldGenDecoratorRange(WorldGenFeatureChanceDecoratorRangeConfiguration.CODEC));
    public static final WorldGenDecorator<WorldGenFeatureEmptyConfiguration2> SPREAD_32_ABOVE = a("spread_32_above", new WorldGenDecoratorSpread32Above(WorldGenFeatureEmptyConfiguration2.CODEC));
    public static final WorldGenDecorator<WorldGenFeatureEmptyConfiguration2> END_GATEWAY = a("end_gateway", new WorldGenDecoratorEndGateway(WorldGenFeatureEmptyConfiguration2.CODEC));
    private final Codec<WorldGenDecoratorConfigured<DC>> configuredCodec;

    private static <T extends WorldGenFeatureDecoratorConfiguration, G extends WorldGenDecorator<T>> G a(String s, G g0) {
        return (WorldGenDecorator) IRegistry.a(IRegistry.DECORATOR, s, (Object) g0);
    }

    public WorldGenDecorator(Codec<DC> codec) {
        this.configuredCodec = codec.fieldOf("config").xmap((worldgenfeaturedecoratorconfiguration) -> {
            return new WorldGenDecoratorConfigured<>(this, worldgenfeaturedecoratorconfiguration);
        }, WorldGenDecoratorConfigured::b).codec();
    }

    public WorldGenDecoratorConfigured<DC> a(DC dc) {
        return new WorldGenDecoratorConfigured<>(this, dc);
    }

    public Codec<WorldGenDecoratorConfigured<DC>> a() {
        return this.configuredCodec;
    }

    public abstract Stream<BlockPosition> a(WorldGenDecoratorContext worldgendecoratorcontext, Random random, DC dc, BlockPosition blockposition);

    public String toString() {
        String s = this.getClass().getSimpleName();

        return s + "@" + Integer.toHexString(this.hashCode());
    }
}
