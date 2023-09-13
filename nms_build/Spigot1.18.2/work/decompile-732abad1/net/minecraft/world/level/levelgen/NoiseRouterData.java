package net.minecraft.world.level.levelgen;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.data.RegistryGeneration;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import net.minecraft.world.level.biome.TerrainShaper;
import net.minecraft.world.level.dimension.DimensionManager;
import net.minecraft.world.level.levelgen.synth.BlendedNoise;
import net.minecraft.world.level.levelgen.synth.NoiseGeneratorNormal;

public class NoiseRouterData {

    private static final float ORE_THICKNESS = 0.08F;
    private static final double VEININESS_FREQUENCY = 1.5D;
    private static final double NOODLE_SPACING_AND_STRAIGHTNESS = 1.5D;
    private static final double SURFACE_DENSITY_THRESHOLD = 1.5625D;
    private static final DensityFunction BLENDING_FACTOR = DensityFunctions.constant(10.0D);
    private static final DensityFunction BLENDING_JAGGEDNESS = DensityFunctions.zero();
    private static final ResourceKey<DensityFunction> ZERO = createKey("zero");
    private static final ResourceKey<DensityFunction> Y = createKey("y");
    private static final ResourceKey<DensityFunction> SHIFT_X = createKey("shift_x");
    private static final ResourceKey<DensityFunction> SHIFT_Z = createKey("shift_z");
    private static final ResourceKey<DensityFunction> BASE_3D_NOISE = createKey("overworld/base_3d_noise");
    private static final ResourceKey<DensityFunction> CONTINENTS = createKey("overworld/continents");
    private static final ResourceKey<DensityFunction> EROSION = createKey("overworld/erosion");
    private static final ResourceKey<DensityFunction> RIDGES = createKey("overworld/ridges");
    private static final ResourceKey<DensityFunction> FACTOR = createKey("overworld/factor");
    private static final ResourceKey<DensityFunction> DEPTH = createKey("overworld/depth");
    private static final ResourceKey<DensityFunction> SLOPED_CHEESE = createKey("overworld/sloped_cheese");
    private static final ResourceKey<DensityFunction> CONTINENTS_LARGE = createKey("overworld_large_biomes/continents");
    private static final ResourceKey<DensityFunction> EROSION_LARGE = createKey("overworld_large_biomes/erosion");
    private static final ResourceKey<DensityFunction> FACTOR_LARGE = createKey("overworld_large_biomes/factor");
    private static final ResourceKey<DensityFunction> DEPTH_LARGE = createKey("overworld_large_biomes/depth");
    private static final ResourceKey<DensityFunction> SLOPED_CHEESE_LARGE = createKey("overworld_large_biomes/sloped_cheese");
    private static final ResourceKey<DensityFunction> SLOPED_CHEESE_END = createKey("end/sloped_cheese");
    private static final ResourceKey<DensityFunction> SPAGHETTI_ROUGHNESS_FUNCTION = createKey("overworld/caves/spaghetti_roughness_function");
    private static final ResourceKey<DensityFunction> ENTRANCES = createKey("overworld/caves/entrances");
    private static final ResourceKey<DensityFunction> NOODLE = createKey("overworld/caves/noodle");
    private static final ResourceKey<DensityFunction> PILLARS = createKey("overworld/caves/pillars");
    private static final ResourceKey<DensityFunction> SPAGHETTI_2D_THICKNESS_MODULATOR = createKey("overworld/caves/spaghetti_2d_thickness_modulator");
    private static final ResourceKey<DensityFunction> SPAGHETTI_2D = createKey("overworld/caves/spaghetti_2d");

    public NoiseRouterData() {}

    protected static NoiseRouterWithOnlyNoises overworld(NoiseSettings noisesettings, boolean flag) {
        return overworldWithNewCaves(noisesettings, flag);
    }

    private static ResourceKey<DensityFunction> createKey(String s) {
        return ResourceKey.create(IRegistry.DENSITY_FUNCTION_REGISTRY, new MinecraftKey(s));
    }

    public static Holder<? extends DensityFunction> bootstrap() {
        register(NoiseRouterData.ZERO, DensityFunctions.zero());
        int i = DimensionManager.MIN_Y * 2;
        int j = DimensionManager.MAX_Y * 2;

        register(NoiseRouterData.Y, DensityFunctions.yClampedGradient(i, j, (double) i, (double) j));
        DensityFunction densityfunction = register(NoiseRouterData.SHIFT_X, DensityFunctions.flatCache(DensityFunctions.cache2d(DensityFunctions.shiftA(getNoise(Noises.SHIFT)))));
        DensityFunction densityfunction1 = register(NoiseRouterData.SHIFT_Z, DensityFunctions.flatCache(DensityFunctions.cache2d(DensityFunctions.shiftB(getNoise(Noises.SHIFT)))));

        register(NoiseRouterData.BASE_3D_NOISE, BlendedNoise.UNSEEDED);
        DensityFunction densityfunction2 = register(NoiseRouterData.CONTINENTS, DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d(densityfunction, densityfunction1, 0.25D, getNoise(Noises.CONTINENTALNESS))));
        DensityFunction densityfunction3 = register(NoiseRouterData.EROSION, DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d(densityfunction, densityfunction1, 0.25D, getNoise(Noises.EROSION))));
        DensityFunction densityfunction4 = register(NoiseRouterData.RIDGES, DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d(densityfunction, densityfunction1, 0.25D, getNoise(Noises.RIDGE))));
        DensityFunction densityfunction5 = DensityFunctions.noise(getNoise(Noises.JAGGED), 1500.0D, 0.0D);
        DensityFunction densityfunction6 = splineWithBlending(densityfunction2, densityfunction3, densityfunction4, DensityFunctions.y.b.OFFSET, -0.81D, 2.5D, DensityFunctions.blendOffset());
        DensityFunction densityfunction7 = register(NoiseRouterData.FACTOR, splineWithBlending(densityfunction2, densityfunction3, densityfunction4, DensityFunctions.y.b.FACTOR, 0.0D, 8.0D, NoiseRouterData.BLENDING_FACTOR));
        DensityFunction densityfunction8 = register(NoiseRouterData.DEPTH, DensityFunctions.add(DensityFunctions.yClampedGradient(-64, 320, 1.5D, -1.5D), densityfunction6));

        register(NoiseRouterData.SLOPED_CHEESE, slopedCheese(densityfunction2, densityfunction3, densityfunction4, densityfunction7, densityfunction8, densityfunction5));
        DensityFunction densityfunction9 = register(NoiseRouterData.CONTINENTS_LARGE, DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d(densityfunction, densityfunction1, 0.25D, getNoise(Noises.CONTINENTALNESS_LARGE))));
        DensityFunction densityfunction10 = register(NoiseRouterData.EROSION_LARGE, DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d(densityfunction, densityfunction1, 0.25D, getNoise(Noises.EROSION_LARGE))));
        DensityFunction densityfunction11 = splineWithBlending(densityfunction9, densityfunction10, densityfunction4, DensityFunctions.y.b.OFFSET, -0.81D, 2.5D, DensityFunctions.blendOffset());
        DensityFunction densityfunction12 = register(NoiseRouterData.FACTOR_LARGE, splineWithBlending(densityfunction9, densityfunction10, densityfunction4, DensityFunctions.y.b.FACTOR, 0.0D, 8.0D, NoiseRouterData.BLENDING_FACTOR));
        DensityFunction densityfunction13 = register(NoiseRouterData.DEPTH_LARGE, DensityFunctions.add(DensityFunctions.yClampedGradient(-64, 320, 1.5D, -1.5D), densityfunction11));

        register(NoiseRouterData.SLOPED_CHEESE_LARGE, slopedCheese(densityfunction9, densityfunction10, densityfunction4, densityfunction12, densityfunction13, densityfunction5));
        register(NoiseRouterData.SLOPED_CHEESE_END, DensityFunctions.add(DensityFunctions.endIslands(0L), getFunction(NoiseRouterData.BASE_3D_NOISE)));
        register(NoiseRouterData.SPAGHETTI_ROUGHNESS_FUNCTION, spaghettiRoughnessFunction());
        register(NoiseRouterData.SPAGHETTI_2D_THICKNESS_MODULATOR, DensityFunctions.cacheOnce(DensityFunctions.mappedNoise(getNoise(Noises.SPAGHETTI_2D_THICKNESS), 2.0D, 1.0D, -0.6D, -1.3D)));
        register(NoiseRouterData.SPAGHETTI_2D, spaghetti2D());
        register(NoiseRouterData.ENTRANCES, entrances());
        register(NoiseRouterData.NOODLE, noodle());
        register(NoiseRouterData.PILLARS, pillars());
        return (Holder) RegistryGeneration.DENSITY_FUNCTION.holders().iterator().next();
    }

    private static DensityFunction register(ResourceKey<DensityFunction> resourcekey, DensityFunction densityfunction) {
        return new DensityFunctions.j(RegistryGeneration.register(RegistryGeneration.DENSITY_FUNCTION, resourcekey, densityfunction));
    }

    private static Holder<NoiseGeneratorNormal.a> getNoise(ResourceKey<NoiseGeneratorNormal.a> resourcekey) {
        return RegistryGeneration.NOISE.getHolderOrThrow(resourcekey);
    }

    private static DensityFunction getFunction(ResourceKey<DensityFunction> resourcekey) {
        return new DensityFunctions.j(RegistryGeneration.DENSITY_FUNCTION.getHolderOrThrow(resourcekey));
    }

    private static DensityFunction slopedCheese(DensityFunction densityfunction, DensityFunction densityfunction1, DensityFunction densityfunction2, DensityFunction densityfunction3, DensityFunction densityfunction4, DensityFunction densityfunction5) {
        DensityFunction densityfunction6 = splineWithBlending(densityfunction, densityfunction1, densityfunction2, DensityFunctions.y.b.JAGGEDNESS, 0.0D, 1.28D, NoiseRouterData.BLENDING_JAGGEDNESS);
        DensityFunction densityfunction7 = DensityFunctions.mul(densityfunction6, densityfunction5.halfNegative());
        DensityFunction densityfunction8 = noiseGradientDensity(densityfunction3, DensityFunctions.add(densityfunction4, densityfunction7));

        return DensityFunctions.add(densityfunction8, getFunction(NoiseRouterData.BASE_3D_NOISE));
    }

    private static DensityFunction spaghettiRoughnessFunction() {
        DensityFunction densityfunction = DensityFunctions.noise(getNoise(Noises.SPAGHETTI_ROUGHNESS));
        DensityFunction densityfunction1 = DensityFunctions.mappedNoise(getNoise(Noises.SPAGHETTI_ROUGHNESS_MODULATOR), 0.0D, -0.1D);

        return DensityFunctions.cacheOnce(DensityFunctions.mul(densityfunction1, DensityFunctions.add(densityfunction.abs(), DensityFunctions.constant(-0.4D))));
    }

    private static DensityFunction entrances() {
        DensityFunction densityfunction = DensityFunctions.cacheOnce(DensityFunctions.noise(getNoise(Noises.SPAGHETTI_3D_RARITY), 2.0D, 1.0D));
        DensityFunction densityfunction1 = DensityFunctions.mappedNoise(getNoise(Noises.SPAGHETTI_3D_THICKNESS), -0.065D, -0.088D);
        DensityFunction densityfunction2 = DensityFunctions.weirdScaledSampler(densityfunction, getNoise(Noises.SPAGHETTI_3D_1), DensityFunctions.ab.a.TYPE1);
        DensityFunction densityfunction3 = DensityFunctions.weirdScaledSampler(densityfunction, getNoise(Noises.SPAGHETTI_3D_2), DensityFunctions.ab.a.TYPE1);
        DensityFunction densityfunction4 = DensityFunctions.add(DensityFunctions.max(densityfunction2, densityfunction3), densityfunction1).clamp(-1.0D, 1.0D);
        DensityFunction densityfunction5 = getFunction(NoiseRouterData.SPAGHETTI_ROUGHNESS_FUNCTION);
        DensityFunction densityfunction6 = DensityFunctions.noise(getNoise(Noises.CAVE_ENTRANCE), 0.75D, 0.5D);
        DensityFunction densityfunction7 = DensityFunctions.add(DensityFunctions.add(densityfunction6, DensityFunctions.constant(0.37D)), DensityFunctions.yClampedGradient(-10, 30, 0.3D, 0.0D));

        return DensityFunctions.cacheOnce(DensityFunctions.min(densityfunction7, DensityFunctions.add(densityfunction5, densityfunction4)));
    }

    private static DensityFunction noodle() {
        DensityFunction densityfunction = getFunction(NoiseRouterData.Y);
        boolean flag = true;
        boolean flag1 = true;
        boolean flag2 = true;
        DensityFunction densityfunction1 = yLimitedInterpolatable(densityfunction, DensityFunctions.noise(getNoise(Noises.NOODLE), 1.0D, 1.0D), -60, 320, -1);
        DensityFunction densityfunction2 = yLimitedInterpolatable(densityfunction, DensityFunctions.mappedNoise(getNoise(Noises.NOODLE_THICKNESS), 1.0D, 1.0D, -0.05D, -0.1D), -60, 320, 0);
        double d0 = 2.6666666666666665D;
        DensityFunction densityfunction3 = yLimitedInterpolatable(densityfunction, DensityFunctions.noise(getNoise(Noises.NOODLE_RIDGE_A), 2.6666666666666665D, 2.6666666666666665D), -60, 320, 0);
        DensityFunction densityfunction4 = yLimitedInterpolatable(densityfunction, DensityFunctions.noise(getNoise(Noises.NOODLE_RIDGE_B), 2.6666666666666665D, 2.6666666666666665D), -60, 320, 0);
        DensityFunction densityfunction5 = DensityFunctions.mul(DensityFunctions.constant(1.5D), DensityFunctions.max(densityfunction3.abs(), densityfunction4.abs()));

        return DensityFunctions.rangeChoice(densityfunction1, -1000000.0D, 0.0D, DensityFunctions.constant(64.0D), DensityFunctions.add(densityfunction2, densityfunction5));
    }

    private static DensityFunction pillars() {
        double d0 = 25.0D;
        double d1 = 0.3D;
        DensityFunction densityfunction = DensityFunctions.noise(getNoise(Noises.PILLAR), 25.0D, 0.3D);
        DensityFunction densityfunction1 = DensityFunctions.mappedNoise(getNoise(Noises.PILLAR_RARENESS), 0.0D, -2.0D);
        DensityFunction densityfunction2 = DensityFunctions.mappedNoise(getNoise(Noises.PILLAR_THICKNESS), 0.0D, 1.1D);
        DensityFunction densityfunction3 = DensityFunctions.add(DensityFunctions.mul(densityfunction, DensityFunctions.constant(2.0D)), densityfunction1);

        return DensityFunctions.cacheOnce(DensityFunctions.mul(densityfunction3, densityfunction2.cube()));
    }

    private static DensityFunction spaghetti2D() {
        DensityFunction densityfunction = DensityFunctions.noise(getNoise(Noises.SPAGHETTI_2D_MODULATOR), 2.0D, 1.0D);
        DensityFunction densityfunction1 = DensityFunctions.weirdScaledSampler(densityfunction, getNoise(Noises.SPAGHETTI_2D), DensityFunctions.ab.a.TYPE2);
        DensityFunction densityfunction2 = DensityFunctions.mappedNoise(getNoise(Noises.SPAGHETTI_2D_ELEVATION), 0.0D, (double) Math.floorDiv(-64, 8), 8.0D);
        DensityFunction densityfunction3 = getFunction(NoiseRouterData.SPAGHETTI_2D_THICKNESS_MODULATOR);
        DensityFunction densityfunction4 = DensityFunctions.add(densityfunction2, DensityFunctions.yClampedGradient(-64, 320, 8.0D, -40.0D)).abs();
        DensityFunction densityfunction5 = DensityFunctions.add(densityfunction4, densityfunction3).cube();
        double d0 = 0.083D;
        DensityFunction densityfunction6 = DensityFunctions.add(densityfunction1, DensityFunctions.mul(DensityFunctions.constant(0.083D), densityfunction3));

        return DensityFunctions.max(densityfunction6, densityfunction5).clamp(-1.0D, 1.0D);
    }

    private static DensityFunction underground(DensityFunction densityfunction) {
        DensityFunction densityfunction1 = getFunction(NoiseRouterData.SPAGHETTI_2D);
        DensityFunction densityfunction2 = getFunction(NoiseRouterData.SPAGHETTI_ROUGHNESS_FUNCTION);
        DensityFunction densityfunction3 = DensityFunctions.noise(getNoise(Noises.CAVE_LAYER), 8.0D);
        DensityFunction densityfunction4 = DensityFunctions.mul(DensityFunctions.constant(4.0D), densityfunction3.square());
        DensityFunction densityfunction5 = DensityFunctions.noise(getNoise(Noises.CAVE_CHEESE), 0.6666666666666666D);
        DensityFunction densityfunction6 = DensityFunctions.add(DensityFunctions.add(DensityFunctions.constant(0.27D), densityfunction5).clamp(-1.0D, 1.0D), DensityFunctions.add(DensityFunctions.constant(1.5D), DensityFunctions.mul(DensityFunctions.constant(-0.64D), densityfunction)).clamp(0.0D, 0.5D));
        DensityFunction densityfunction7 = DensityFunctions.add(densityfunction4, densityfunction6);
        DensityFunction densityfunction8 = DensityFunctions.min(DensityFunctions.min(densityfunction7, getFunction(NoiseRouterData.ENTRANCES)), DensityFunctions.add(densityfunction1, densityfunction2));
        DensityFunction densityfunction9 = getFunction(NoiseRouterData.PILLARS);
        DensityFunction densityfunction10 = DensityFunctions.rangeChoice(densityfunction9, -1000000.0D, 0.03D, DensityFunctions.constant(-1000000.0D), densityfunction9);

        return DensityFunctions.max(densityfunction8, densityfunction10);
    }

    private static DensityFunction postProcess(NoiseSettings noisesettings, DensityFunction densityfunction) {
        DensityFunction densityfunction1 = DensityFunctions.slide(noisesettings, densityfunction);
        DensityFunction densityfunction2 = DensityFunctions.blendDensity(densityfunction1);

        return DensityFunctions.mul(DensityFunctions.interpolated(densityfunction2), DensityFunctions.constant(0.64D)).squeeze();
    }

    private static NoiseRouterWithOnlyNoises overworldWithNewCaves(NoiseSettings noisesettings, boolean flag) {
        DensityFunction densityfunction = DensityFunctions.noise(getNoise(Noises.AQUIFER_BARRIER), 0.5D);
        DensityFunction densityfunction1 = DensityFunctions.noise(getNoise(Noises.AQUIFER_FLUID_LEVEL_FLOODEDNESS), 0.67D);
        DensityFunction densityfunction2 = DensityFunctions.noise(getNoise(Noises.AQUIFER_FLUID_LEVEL_SPREAD), 0.7142857142857143D);
        DensityFunction densityfunction3 = DensityFunctions.noise(getNoise(Noises.AQUIFER_LAVA));
        DensityFunction densityfunction4 = getFunction(NoiseRouterData.SHIFT_X);
        DensityFunction densityfunction5 = getFunction(NoiseRouterData.SHIFT_Z);
        DensityFunction densityfunction6 = DensityFunctions.shiftedNoise2d(densityfunction4, densityfunction5, 0.25D, getNoise(flag ? Noises.TEMPERATURE_LARGE : Noises.TEMPERATURE));
        DensityFunction densityfunction7 = DensityFunctions.shiftedNoise2d(densityfunction4, densityfunction5, 0.25D, getNoise(flag ? Noises.VEGETATION_LARGE : Noises.VEGETATION));
        DensityFunction densityfunction8 = getFunction(flag ? NoiseRouterData.FACTOR_LARGE : NoiseRouterData.FACTOR);
        DensityFunction densityfunction9 = getFunction(flag ? NoiseRouterData.DEPTH_LARGE : NoiseRouterData.DEPTH);
        DensityFunction densityfunction10 = noiseGradientDensity(DensityFunctions.cache2d(densityfunction8), densityfunction9);
        DensityFunction densityfunction11 = getFunction(flag ? NoiseRouterData.SLOPED_CHEESE_LARGE : NoiseRouterData.SLOPED_CHEESE);
        DensityFunction densityfunction12 = DensityFunctions.min(densityfunction11, DensityFunctions.mul(DensityFunctions.constant(5.0D), getFunction(NoiseRouterData.ENTRANCES)));
        DensityFunction densityfunction13 = DensityFunctions.rangeChoice(densityfunction11, -1000000.0D, 1.5625D, densityfunction12, underground(densityfunction11));
        DensityFunction densityfunction14 = DensityFunctions.min(postProcess(noisesettings, densityfunction13), getFunction(NoiseRouterData.NOODLE));
        DensityFunction densityfunction15 = getFunction(NoiseRouterData.Y);
        int i = noisesettings.minY();
        int j = Stream.of(OreVeinifier.a.values()).mapToInt((oreveinifier_a) -> {
            return oreveinifier_a.minY;
        }).min().orElse(i);
        int k = Stream.of(OreVeinifier.a.values()).mapToInt((oreveinifier_a) -> {
            return oreveinifier_a.maxY;
        }).max().orElse(i);
        DensityFunction densityfunction16 = yLimitedInterpolatable(densityfunction15, DensityFunctions.noise(getNoise(Noises.ORE_VEININESS), 1.5D, 1.5D), j, k, 0);
        float f = 4.0F;
        DensityFunction densityfunction17 = yLimitedInterpolatable(densityfunction15, DensityFunctions.noise(getNoise(Noises.ORE_VEIN_A), 4.0D, 4.0D), j, k, 0).abs();
        DensityFunction densityfunction18 = yLimitedInterpolatable(densityfunction15, DensityFunctions.noise(getNoise(Noises.ORE_VEIN_B), 4.0D, 4.0D), j, k, 0).abs();
        DensityFunction densityfunction19 = DensityFunctions.add(DensityFunctions.constant(-0.07999999821186066D), DensityFunctions.max(densityfunction17, densityfunction18));
        DensityFunction densityfunction20 = DensityFunctions.noise(getNoise(Noises.ORE_GAP));

        return new NoiseRouterWithOnlyNoises(densityfunction, densityfunction1, densityfunction2, densityfunction3, densityfunction6, densityfunction7, getFunction(flag ? NoiseRouterData.CONTINENTS_LARGE : NoiseRouterData.CONTINENTS), getFunction(flag ? NoiseRouterData.EROSION_LARGE : NoiseRouterData.EROSION), getFunction(flag ? NoiseRouterData.DEPTH_LARGE : NoiseRouterData.DEPTH), getFunction(NoiseRouterData.RIDGES), densityfunction10, densityfunction14, densityfunction16, densityfunction19, densityfunction20);
    }

    private static NoiseRouterWithOnlyNoises noNewCaves(NoiseSettings noisesettings) {
        DensityFunction densityfunction = getFunction(NoiseRouterData.SHIFT_X);
        DensityFunction densityfunction1 = getFunction(NoiseRouterData.SHIFT_Z);
        DensityFunction densityfunction2 = DensityFunctions.shiftedNoise2d(densityfunction, densityfunction1, 0.25D, getNoise(Noises.TEMPERATURE));
        DensityFunction densityfunction3 = DensityFunctions.shiftedNoise2d(densityfunction, densityfunction1, 0.25D, getNoise(Noises.VEGETATION));
        DensityFunction densityfunction4 = noiseGradientDensity(DensityFunctions.cache2d(getFunction(NoiseRouterData.FACTOR)), getFunction(NoiseRouterData.DEPTH));
        DensityFunction densityfunction5 = postProcess(noisesettings, getFunction(NoiseRouterData.SLOPED_CHEESE));

        return new NoiseRouterWithOnlyNoises(DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), densityfunction2, densityfunction3, getFunction(NoiseRouterData.CONTINENTS), getFunction(NoiseRouterData.EROSION), getFunction(NoiseRouterData.DEPTH), getFunction(NoiseRouterData.RIDGES), densityfunction4, densityfunction5, DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero());
    }

    protected static NoiseRouterWithOnlyNoises overworldWithoutCaves(NoiseSettings noisesettings) {
        return noNewCaves(noisesettings);
    }

    protected static NoiseRouterWithOnlyNoises nether(NoiseSettings noisesettings) {
        return noNewCaves(noisesettings);
    }

    protected static NoiseRouterWithOnlyNoises end(NoiseSettings noisesettings) {
        DensityFunction densityfunction = DensityFunctions.cache2d(DensityFunctions.endIslands(0L));
        DensityFunction densityfunction1 = postProcess(noisesettings, getFunction(NoiseRouterData.SLOPED_CHEESE_END));

        return new NoiseRouterWithOnlyNoises(DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), densityfunction, densityfunction1, DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero());
    }

    private static NoiseGeneratorNormal seedNoise(PositionalRandomFactory positionalrandomfactory, IRegistry<NoiseGeneratorNormal.a> iregistry, Holder<NoiseGeneratorNormal.a> holder) {
        Optional optional = holder.unwrapKey();

        Objects.requireNonNull(iregistry);
        return Noises.instantiate(positionalrandomfactory, (Holder) optional.flatMap(iregistry::getHolder).orElse(holder));
    }

    public static NoiseRouter createNoiseRouter(NoiseSettings noisesettings, long i, IRegistry<NoiseGeneratorNormal.a> iregistry, SeededRandom.a seededrandom_a, NoiseRouterWithOnlyNoises noiserouterwithonlynoises) {
        boolean flag = seededrandom_a == SeededRandom.a.LEGACY;
        PositionalRandomFactory positionalrandomfactory = seededrandom_a.newInstance(i).forkPositional();
        Map<DensityFunction, DensityFunction> map = new HashMap();
        DensityFunction.e densityfunction_e = (densityfunction) -> {
            Holder holder;

            if (densityfunction instanceof DensityFunctions.o) {
                DensityFunctions.o densityfunctions_o = (DensityFunctions.o) densityfunction;

                holder = densityfunctions_o.noiseData();
                return new DensityFunctions.o(holder, seedNoise(positionalrandomfactory, iregistry, holder), densityfunctions_o.xzScale(), densityfunctions_o.yScale());
            } else if (densityfunction instanceof DensityFunctions.u) {
                DensityFunctions.u densityfunctions_u = (DensityFunctions.u) densityfunction;
                Holder<NoiseGeneratorNormal.a> holder1 = densityfunctions_u.noiseData();
                NoiseGeneratorNormal noisegeneratornormal;

                if (flag) {
                    noisegeneratornormal = NoiseGeneratorNormal.create(positionalrandomfactory.fromHashOf(Noises.SHIFT.location()), new NoiseGeneratorNormal.a(0, 0.0D, new double[0]));
                } else {
                    noisegeneratornormal = seedNoise(positionalrandomfactory, iregistry, holder1);
                }

                return densityfunctions_u.withNewNoise(noisegeneratornormal);
            } else if (densityfunction instanceof DensityFunctions.v) {
                DensityFunctions.v densityfunctions_v = (DensityFunctions.v) densityfunction;

                if (flag) {
                    holder = densityfunctions_v.noiseData();
                    NoiseGeneratorNormal noisegeneratornormal1;

                    if (Objects.equals(holder.unwrapKey(), Optional.of(Noises.TEMPERATURE))) {
                        noisegeneratornormal1 = NoiseGeneratorNormal.createLegacyNetherBiome(seededrandom_a.newInstance(i), new NoiseGeneratorNormal.a(-7, 1.0D, new double[]{1.0D}));
                        return new DensityFunctions.v(densityfunctions_v.shiftX(), densityfunctions_v.shiftY(), densityfunctions_v.shiftZ(), densityfunctions_v.xzScale(), densityfunctions_v.yScale(), holder, noisegeneratornormal1);
                    }

                    if (Objects.equals(holder.unwrapKey(), Optional.of(Noises.VEGETATION))) {
                        noisegeneratornormal1 = NoiseGeneratorNormal.createLegacyNetherBiome(seededrandom_a.newInstance(i + 1L), new NoiseGeneratorNormal.a(-7, 1.0D, new double[]{1.0D}));
                        return new DensityFunctions.v(densityfunctions_v.shiftX(), densityfunctions_v.shiftY(), densityfunctions_v.shiftZ(), densityfunctions_v.xzScale(), densityfunctions_v.yScale(), holder, noisegeneratornormal1);
                    }
                }

                holder = densityfunctions_v.noiseData();
                return new DensityFunctions.v(densityfunctions_v.shiftX(), densityfunctions_v.shiftY(), densityfunctions_v.shiftZ(), densityfunctions_v.xzScale(), densityfunctions_v.yScale(), holder, seedNoise(positionalrandomfactory, iregistry, holder));
            } else if (densityfunction instanceof DensityFunctions.ab) {
                DensityFunctions.ab densityfunctions_ab = (DensityFunctions.ab) densityfunction;

                return new DensityFunctions.ab(densityfunctions_ab.input(), densityfunctions_ab.noiseData(), seedNoise(positionalrandomfactory, iregistry, densityfunctions_ab.noiseData()), densityfunctions_ab.rarityValueMapper());
            } else if (densityfunction instanceof BlendedNoise) {
                return flag ? new BlendedNoise(seededrandom_a.newInstance(i), noisesettings.noiseSamplingSettings(), noisesettings.getCellWidth(), noisesettings.getCellHeight()) : new BlendedNoise(positionalrandomfactory.fromHashOf(new MinecraftKey("terrain")), noisesettings.noiseSamplingSettings(), noisesettings.getCellWidth(), noisesettings.getCellHeight());
            } else if (densityfunction instanceof DensityFunctions.i) {
                return new DensityFunctions.i(i);
            } else if (densityfunction instanceof DensityFunctions.y) {
                DensityFunctions.y densityfunctions_y = (DensityFunctions.y) densityfunction;
                TerrainShaper terrainshaper = noisesettings.terrainShaper();

                return new DensityFunctions.y(densityfunctions_y.continentalness(), densityfunctions_y.erosion(), densityfunctions_y.weirdness(), terrainshaper, densityfunctions_y.spline(), densityfunctions_y.minValue(), densityfunctions_y.maxValue());
            } else if (densityfunction instanceof DensityFunctions.w) {
                DensityFunctions.w densityfunctions_w = (DensityFunctions.w) densityfunction;

                return new DensityFunctions.w(noisesettings, densityfunctions_w.input());
            } else {
                return densityfunction;
            }
        };
        DensityFunction.e densityfunction_e1 = (densityfunction) -> {
            return (DensityFunction) map.computeIfAbsent(densityfunction, densityfunction_e);
        };
        NoiseRouterWithOnlyNoises noiserouterwithonlynoises1 = noiserouterwithonlynoises.mapAll(densityfunction_e1);
        PositionalRandomFactory positionalrandomfactory1 = positionalrandomfactory.fromHashOf(new MinecraftKey("aquifer")).forkPositional();
        PositionalRandomFactory positionalrandomfactory2 = positionalrandomfactory.fromHashOf(new MinecraftKey("ore")).forkPositional();

        return new NoiseRouter(noiserouterwithonlynoises1.barrierNoise(), noiserouterwithonlynoises1.fluidLevelFloodednessNoise(), noiserouterwithonlynoises1.fluidLevelSpreadNoise(), noiserouterwithonlynoises1.lavaNoise(), positionalrandomfactory1, positionalrandomfactory2, noiserouterwithonlynoises1.temperature(), noiserouterwithonlynoises1.vegetation(), noiserouterwithonlynoises1.continents(), noiserouterwithonlynoises1.erosion(), noiserouterwithonlynoises1.depth(), noiserouterwithonlynoises1.ridges(), noiserouterwithonlynoises1.initialDensityWithoutJaggedness(), noiserouterwithonlynoises1.finalDensity(), noiserouterwithonlynoises1.veinToggle(), noiserouterwithonlynoises1.veinRidged(), noiserouterwithonlynoises1.veinGap(), (new OverworldBiomeBuilder()).spawnTarget());
    }

    private static DensityFunction splineWithBlending(DensityFunction densityfunction, DensityFunction densityfunction1, DensityFunction densityfunction2, DensityFunctions.y.b densityfunctions_y_b, double d0, double d1, DensityFunction densityfunction3) {
        DensityFunction densityfunction4 = DensityFunctions.terrainShaperSpline(densityfunction, densityfunction1, densityfunction2, densityfunctions_y_b, d0, d1);
        DensityFunction densityfunction5 = DensityFunctions.lerp(DensityFunctions.blendAlpha(), densityfunction3, densityfunction4);

        return DensityFunctions.flatCache(DensityFunctions.cache2d(densityfunction5));
    }

    private static DensityFunction noiseGradientDensity(DensityFunction densityfunction, DensityFunction densityfunction1) {
        DensityFunction densityfunction2 = DensityFunctions.mul(densityfunction1, densityfunction);

        return DensityFunctions.mul(DensityFunctions.constant(4.0D), densityfunction2.quarterNegative());
    }

    private static DensityFunction yLimitedInterpolatable(DensityFunction densityfunction, DensityFunction densityfunction1, int i, int j, int k) {
        return DensityFunctions.interpolated(DensityFunctions.rangeChoice(densityfunction, (double) i, (double) (j + 1), densityfunction1, DensityFunctions.constant((double) k)));
    }

    protected static double applySlide(NoiseSettings noisesettings, double d0, double d1) {
        double d2 = (double) ((int) d1 / noisesettings.getCellHeight() - noisesettings.getMinCellY());

        d0 = noisesettings.topSlideSettings().applySlide(d0, (double) noisesettings.getCellCountY() - d2);
        d0 = noisesettings.bottomSlideSettings().applySlide(d0, d2);
        return d0;
    }

    protected static double computePreliminarySurfaceLevelScanning(NoiseSettings noisesettings, DensityFunction densityfunction, int i, int j) {
        for (int k = noisesettings.getMinCellY() + noisesettings.getCellCountY(); k >= noisesettings.getMinCellY(); --k) {
            int l = k * noisesettings.getCellHeight();
            double d0 = -0.703125D;
            double d1 = densityfunction.compute(new DensityFunction.d(i, l, j)) + -0.703125D;
            double d2 = MathHelper.clamp(d1, -64.0D, 64.0D);

            d2 = applySlide(noisesettings, d2, (double) l);
            if (d2 > 0.390625D) {
                return (double) l;
            }
        }

        return 2.147483647E9D;
    }

    protected static final class a {

        protected a() {}

        protected static double getSphaghettiRarity2D(double d0) {
            return d0 < -0.75D ? 0.5D : (d0 < -0.5D ? 0.75D : (d0 < 0.5D ? 1.0D : (d0 < 0.75D ? 2.0D : 3.0D)));
        }

        protected static double getSpaghettiRarity3D(double d0) {
            return d0 < -0.5D ? 0.75D : (d0 < 0.0D ? 1.0D : (d0 < 0.5D ? 1.5D : 2.0D));
        }
    }
}
