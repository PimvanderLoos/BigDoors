package net.minecraft.world.level.levelgen;

import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.data.RegistryGeneration;
import net.minecraft.data.worldgen.TerrainProvider;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.DimensionManager;
import net.minecraft.world.level.levelgen.synth.BlendedNoise;
import net.minecraft.world.level.levelgen.synth.NoiseGeneratorNormal;

public class NoiseRouterData {

    public static final float GLOBAL_OFFSET = -0.50375F;
    private static final float ORE_THICKNESS = 0.08F;
    private static final double VEININESS_FREQUENCY = 1.5D;
    private static final double NOODLE_SPACING_AND_STRAIGHTNESS = 1.5D;
    private static final double SURFACE_DENSITY_THRESHOLD = 1.5625D;
    private static final double CHEESE_NOISE_TARGET = -0.703125D;
    public static final int ISLAND_CHUNK_DISTANCE = 64;
    public static final long ISLAND_CHUNK_DISTANCE_SQR = 4096L;
    private static final DensityFunction BLENDING_FACTOR = DensityFunctions.constant(10.0D);
    private static final DensityFunction BLENDING_JAGGEDNESS = DensityFunctions.zero();
    private static final ResourceKey<DensityFunction> ZERO = createKey("zero");
    private static final ResourceKey<DensityFunction> Y = createKey("y");
    private static final ResourceKey<DensityFunction> SHIFT_X = createKey("shift_x");
    private static final ResourceKey<DensityFunction> SHIFT_Z = createKey("shift_z");
    private static final ResourceKey<DensityFunction> BASE_3D_NOISE_OVERWORLD = createKey("overworld/base_3d_noise");
    private static final ResourceKey<DensityFunction> BASE_3D_NOISE_NETHER = createKey("nether/base_3d_noise");
    private static final ResourceKey<DensityFunction> BASE_3D_NOISE_END = createKey("end/base_3d_noise");
    public static final ResourceKey<DensityFunction> CONTINENTS = createKey("overworld/continents");
    public static final ResourceKey<DensityFunction> EROSION = createKey("overworld/erosion");
    public static final ResourceKey<DensityFunction> RIDGES = createKey("overworld/ridges");
    public static final ResourceKey<DensityFunction> RIDGES_FOLDED = createKey("overworld/ridges_folded");
    public static final ResourceKey<DensityFunction> OFFSET = createKey("overworld/offset");
    public static final ResourceKey<DensityFunction> FACTOR = createKey("overworld/factor");
    public static final ResourceKey<DensityFunction> JAGGEDNESS = createKey("overworld/jaggedness");
    public static final ResourceKey<DensityFunction> DEPTH = createKey("overworld/depth");
    private static final ResourceKey<DensityFunction> SLOPED_CHEESE = createKey("overworld/sloped_cheese");
    public static final ResourceKey<DensityFunction> CONTINENTS_LARGE = createKey("overworld_large_biomes/continents");
    public static final ResourceKey<DensityFunction> EROSION_LARGE = createKey("overworld_large_biomes/erosion");
    private static final ResourceKey<DensityFunction> OFFSET_LARGE = createKey("overworld_large_biomes/offset");
    private static final ResourceKey<DensityFunction> FACTOR_LARGE = createKey("overworld_large_biomes/factor");
    private static final ResourceKey<DensityFunction> JAGGEDNESS_LARGE = createKey("overworld_large_biomes/jaggedness");
    private static final ResourceKey<DensityFunction> DEPTH_LARGE = createKey("overworld_large_biomes/depth");
    private static final ResourceKey<DensityFunction> SLOPED_CHEESE_LARGE = createKey("overworld_large_biomes/sloped_cheese");
    private static final ResourceKey<DensityFunction> OFFSET_AMPLIFIED = createKey("overworld_amplified/offset");
    private static final ResourceKey<DensityFunction> FACTOR_AMPLIFIED = createKey("overworld_amplified/factor");
    private static final ResourceKey<DensityFunction> JAGGEDNESS_AMPLIFIED = createKey("overworld_amplified/jaggedness");
    private static final ResourceKey<DensityFunction> DEPTH_AMPLIFIED = createKey("overworld_amplified/depth");
    private static final ResourceKey<DensityFunction> SLOPED_CHEESE_AMPLIFIED = createKey("overworld_amplified/sloped_cheese");
    private static final ResourceKey<DensityFunction> SLOPED_CHEESE_END = createKey("end/sloped_cheese");
    private static final ResourceKey<DensityFunction> SPAGHETTI_ROUGHNESS_FUNCTION = createKey("overworld/caves/spaghetti_roughness_function");
    private static final ResourceKey<DensityFunction> ENTRANCES = createKey("overworld/caves/entrances");
    private static final ResourceKey<DensityFunction> NOODLE = createKey("overworld/caves/noodle");
    private static final ResourceKey<DensityFunction> PILLARS = createKey("overworld/caves/pillars");
    private static final ResourceKey<DensityFunction> SPAGHETTI_2D_THICKNESS_MODULATOR = createKey("overworld/caves/spaghetti_2d_thickness_modulator");
    private static final ResourceKey<DensityFunction> SPAGHETTI_2D = createKey("overworld/caves/spaghetti_2d");

    public NoiseRouterData() {}

    private static ResourceKey<DensityFunction> createKey(String s) {
        return ResourceKey.create(IRegistry.DENSITY_FUNCTION_REGISTRY, new MinecraftKey(s));
    }

    public static Holder<? extends DensityFunction> bootstrap(IRegistry<DensityFunction> iregistry) {
        register(iregistry, NoiseRouterData.ZERO, DensityFunctions.zero());
        int i = DimensionManager.MIN_Y * 2;
        int j = DimensionManager.MAX_Y * 2;

        register(iregistry, NoiseRouterData.Y, DensityFunctions.yClampedGradient(i, j, (double) i, (double) j));
        DensityFunction densityfunction = registerAndWrap(iregistry, NoiseRouterData.SHIFT_X, DensityFunctions.flatCache(DensityFunctions.cache2d(DensityFunctions.shiftA(getNoise(Noises.SHIFT)))));
        DensityFunction densityfunction1 = registerAndWrap(iregistry, NoiseRouterData.SHIFT_Z, DensityFunctions.flatCache(DensityFunctions.cache2d(DensityFunctions.shiftB(getNoise(Noises.SHIFT)))));

        register(iregistry, NoiseRouterData.BASE_3D_NOISE_OVERWORLD, BlendedNoise.createUnseeded(0.25D, 0.125D, 80.0D, 160.0D, 8.0D));
        register(iregistry, NoiseRouterData.BASE_3D_NOISE_NETHER, BlendedNoise.createUnseeded(0.25D, 0.375D, 80.0D, 60.0D, 8.0D));
        register(iregistry, NoiseRouterData.BASE_3D_NOISE_END, BlendedNoise.createUnseeded(0.25D, 0.25D, 80.0D, 160.0D, 4.0D));
        Holder<DensityFunction> holder = register(iregistry, NoiseRouterData.CONTINENTS, DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d(densityfunction, densityfunction1, 0.25D, getNoise(Noises.CONTINENTALNESS))));
        Holder<DensityFunction> holder1 = register(iregistry, NoiseRouterData.EROSION, DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d(densityfunction, densityfunction1, 0.25D, getNoise(Noises.EROSION))));
        DensityFunction densityfunction2 = registerAndWrap(iregistry, NoiseRouterData.RIDGES, DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d(densityfunction, densityfunction1, 0.25D, getNoise(Noises.RIDGE))));

        register(iregistry, NoiseRouterData.RIDGES_FOLDED, peaksAndValleys(densityfunction2));
        DensityFunction densityfunction3 = DensityFunctions.noise(getNoise(Noises.JAGGED), 1500.0D, 0.0D);

        registerTerrainNoises(iregistry, densityfunction3, holder, holder1, NoiseRouterData.OFFSET, NoiseRouterData.FACTOR, NoiseRouterData.JAGGEDNESS, NoiseRouterData.DEPTH, NoiseRouterData.SLOPED_CHEESE, false);
        Holder<DensityFunction> holder2 = register(iregistry, NoiseRouterData.CONTINENTS_LARGE, DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d(densityfunction, densityfunction1, 0.25D, getNoise(Noises.CONTINENTALNESS_LARGE))));
        Holder<DensityFunction> holder3 = register(iregistry, NoiseRouterData.EROSION_LARGE, DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d(densityfunction, densityfunction1, 0.25D, getNoise(Noises.EROSION_LARGE))));

        registerTerrainNoises(iregistry, densityfunction3, holder2, holder3, NoiseRouterData.OFFSET_LARGE, NoiseRouterData.FACTOR_LARGE, NoiseRouterData.JAGGEDNESS_LARGE, NoiseRouterData.DEPTH_LARGE, NoiseRouterData.SLOPED_CHEESE_LARGE, false);
        registerTerrainNoises(iregistry, densityfunction3, holder, holder1, NoiseRouterData.OFFSET_AMPLIFIED, NoiseRouterData.FACTOR_AMPLIFIED, NoiseRouterData.JAGGEDNESS_AMPLIFIED, NoiseRouterData.DEPTH_AMPLIFIED, NoiseRouterData.SLOPED_CHEESE_AMPLIFIED, true);
        register(iregistry, NoiseRouterData.SLOPED_CHEESE_END, DensityFunctions.add(DensityFunctions.endIslands(0L), getFunction(iregistry, NoiseRouterData.BASE_3D_NOISE_END)));
        register(iregistry, NoiseRouterData.SPAGHETTI_ROUGHNESS_FUNCTION, spaghettiRoughnessFunction());
        register(iregistry, NoiseRouterData.SPAGHETTI_2D_THICKNESS_MODULATOR, DensityFunctions.cacheOnce(DensityFunctions.mappedNoise(getNoise(Noises.SPAGHETTI_2D_THICKNESS), 2.0D, 1.0D, -0.6D, -1.3D)));
        register(iregistry, NoiseRouterData.SPAGHETTI_2D, spaghetti2D(iregistry));
        register(iregistry, NoiseRouterData.ENTRANCES, entrances(iregistry));
        register(iregistry, NoiseRouterData.NOODLE, noodle(iregistry));
        return register(iregistry, NoiseRouterData.PILLARS, pillars());
    }

    private static void registerTerrainNoises(IRegistry<DensityFunction> iregistry, DensityFunction densityfunction, Holder<DensityFunction> holder, Holder<DensityFunction> holder1, ResourceKey<DensityFunction> resourcekey, ResourceKey<DensityFunction> resourcekey1, ResourceKey<DensityFunction> resourcekey2, ResourceKey<DensityFunction> resourcekey3, ResourceKey<DensityFunction> resourcekey4, boolean flag) {
        DensityFunctions.w.a densityfunctions_w_a = new DensityFunctions.w.a(holder);
        DensityFunctions.w.a densityfunctions_w_a1 = new DensityFunctions.w.a(holder1);
        DensityFunctions.w.a densityfunctions_w_a2 = new DensityFunctions.w.a(iregistry.getHolderOrThrow(NoiseRouterData.RIDGES));
        DensityFunctions.w.a densityfunctions_w_a3 = new DensityFunctions.w.a(iregistry.getHolderOrThrow(NoiseRouterData.RIDGES_FOLDED));
        DensityFunction densityfunction1 = registerAndWrap(iregistry, resourcekey, splineWithBlending(DensityFunctions.add(DensityFunctions.constant(-0.5037500262260437D), DensityFunctions.spline(TerrainProvider.overworldOffset(densityfunctions_w_a, densityfunctions_w_a1, densityfunctions_w_a3, flag))), DensityFunctions.blendOffset()));
        DensityFunction densityfunction2 = registerAndWrap(iregistry, resourcekey1, splineWithBlending(DensityFunctions.spline(TerrainProvider.overworldFactor(densityfunctions_w_a, densityfunctions_w_a1, densityfunctions_w_a2, densityfunctions_w_a3, flag)), NoiseRouterData.BLENDING_FACTOR));
        DensityFunction densityfunction3 = registerAndWrap(iregistry, resourcekey3, DensityFunctions.add(DensityFunctions.yClampedGradient(-64, 320, 1.5D, -1.5D), densityfunction1));
        DensityFunction densityfunction4 = registerAndWrap(iregistry, resourcekey2, splineWithBlending(DensityFunctions.spline(TerrainProvider.overworldJaggedness(densityfunctions_w_a, densityfunctions_w_a1, densityfunctions_w_a2, densityfunctions_w_a3, flag)), NoiseRouterData.BLENDING_JAGGEDNESS));
        DensityFunction densityfunction5 = DensityFunctions.mul(densityfunction4, densityfunction.halfNegative());
        DensityFunction densityfunction6 = noiseGradientDensity(densityfunction2, DensityFunctions.add(densityfunction3, densityfunction5));

        register(iregistry, resourcekey4, DensityFunctions.add(densityfunction6, getFunction(iregistry, NoiseRouterData.BASE_3D_NOISE_OVERWORLD)));
    }

    private static DensityFunction registerAndWrap(IRegistry<DensityFunction> iregistry, ResourceKey<DensityFunction> resourcekey, DensityFunction densityfunction) {
        return new DensityFunctions.j(RegistryGeneration.register(iregistry, resourcekey, densityfunction));
    }

    private static Holder<DensityFunction> register(IRegistry<DensityFunction> iregistry, ResourceKey<DensityFunction> resourcekey, DensityFunction densityfunction) {
        return RegistryGeneration.register(iregistry, resourcekey, densityfunction);
    }

    private static Holder<NoiseGeneratorNormal.a> getNoise(ResourceKey<NoiseGeneratorNormal.a> resourcekey) {
        return RegistryGeneration.NOISE.getHolderOrThrow(resourcekey);
    }

    private static DensityFunction getFunction(IRegistry<DensityFunction> iregistry, ResourceKey<DensityFunction> resourcekey) {
        return new DensityFunctions.j(iregistry.getHolderOrThrow(resourcekey));
    }

    private static DensityFunction peaksAndValleys(DensityFunction densityfunction) {
        return DensityFunctions.mul(DensityFunctions.add(DensityFunctions.add(densityfunction.abs(), DensityFunctions.constant(-0.6666666666666666D)).abs(), DensityFunctions.constant(-0.3333333333333333D)), DensityFunctions.constant(-3.0D));
    }

    public static float peaksAndValleys(float f) {
        return -(Math.abs(Math.abs(f) - 0.6666667F) - 0.33333334F) * 3.0F;
    }

    private static DensityFunction spaghettiRoughnessFunction() {
        DensityFunction densityfunction = DensityFunctions.noise(getNoise(Noises.SPAGHETTI_ROUGHNESS));
        DensityFunction densityfunction1 = DensityFunctions.mappedNoise(getNoise(Noises.SPAGHETTI_ROUGHNESS_MODULATOR), 0.0D, -0.1D);

        return DensityFunctions.cacheOnce(DensityFunctions.mul(densityfunction1, DensityFunctions.add(densityfunction.abs(), DensityFunctions.constant(-0.4D))));
    }

    private static DensityFunction entrances(IRegistry<DensityFunction> iregistry) {
        DensityFunction densityfunction = DensityFunctions.cacheOnce(DensityFunctions.noise(getNoise(Noises.SPAGHETTI_3D_RARITY), 2.0D, 1.0D));
        DensityFunction densityfunction1 = DensityFunctions.mappedNoise(getNoise(Noises.SPAGHETTI_3D_THICKNESS), -0.065D, -0.088D);
        DensityFunction densityfunction2 = DensityFunctions.weirdScaledSampler(densityfunction, getNoise(Noises.SPAGHETTI_3D_1), DensityFunctions.z.a.TYPE1);
        DensityFunction densityfunction3 = DensityFunctions.weirdScaledSampler(densityfunction, getNoise(Noises.SPAGHETTI_3D_2), DensityFunctions.z.a.TYPE1);
        DensityFunction densityfunction4 = DensityFunctions.add(DensityFunctions.max(densityfunction2, densityfunction3), densityfunction1).clamp(-1.0D, 1.0D);
        DensityFunction densityfunction5 = getFunction(iregistry, NoiseRouterData.SPAGHETTI_ROUGHNESS_FUNCTION);
        DensityFunction densityfunction6 = DensityFunctions.noise(getNoise(Noises.CAVE_ENTRANCE), 0.75D, 0.5D);
        DensityFunction densityfunction7 = DensityFunctions.add(DensityFunctions.add(densityfunction6, DensityFunctions.constant(0.37D)), DensityFunctions.yClampedGradient(-10, 30, 0.3D, 0.0D));

        return DensityFunctions.cacheOnce(DensityFunctions.min(densityfunction7, DensityFunctions.add(densityfunction5, densityfunction4)));
    }

    private static DensityFunction noodle(IRegistry<DensityFunction> iregistry) {
        DensityFunction densityfunction = getFunction(iregistry, NoiseRouterData.Y);
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

    private static DensityFunction spaghetti2D(IRegistry<DensityFunction> iregistry) {
        DensityFunction densityfunction = DensityFunctions.noise(getNoise(Noises.SPAGHETTI_2D_MODULATOR), 2.0D, 1.0D);
        DensityFunction densityfunction1 = DensityFunctions.weirdScaledSampler(densityfunction, getNoise(Noises.SPAGHETTI_2D), DensityFunctions.z.a.TYPE2);
        DensityFunction densityfunction2 = DensityFunctions.mappedNoise(getNoise(Noises.SPAGHETTI_2D_ELEVATION), 0.0D, (double) Math.floorDiv(-64, 8), 8.0D);
        DensityFunction densityfunction3 = getFunction(iregistry, NoiseRouterData.SPAGHETTI_2D_THICKNESS_MODULATOR);
        DensityFunction densityfunction4 = DensityFunctions.add(densityfunction2, DensityFunctions.yClampedGradient(-64, 320, 8.0D, -40.0D)).abs();
        DensityFunction densityfunction5 = DensityFunctions.add(densityfunction4, densityfunction3).cube();
        double d0 = 0.083D;
        DensityFunction densityfunction6 = DensityFunctions.add(densityfunction1, DensityFunctions.mul(DensityFunctions.constant(0.083D), densityfunction3));

        return DensityFunctions.max(densityfunction6, densityfunction5).clamp(-1.0D, 1.0D);
    }

    private static DensityFunction underground(IRegistry<DensityFunction> iregistry, DensityFunction densityfunction) {
        DensityFunction densityfunction1 = getFunction(iregistry, NoiseRouterData.SPAGHETTI_2D);
        DensityFunction densityfunction2 = getFunction(iregistry, NoiseRouterData.SPAGHETTI_ROUGHNESS_FUNCTION);
        DensityFunction densityfunction3 = DensityFunctions.noise(getNoise(Noises.CAVE_LAYER), 8.0D);
        DensityFunction densityfunction4 = DensityFunctions.mul(DensityFunctions.constant(4.0D), densityfunction3.square());
        DensityFunction densityfunction5 = DensityFunctions.noise(getNoise(Noises.CAVE_CHEESE), 0.6666666666666666D);
        DensityFunction densityfunction6 = DensityFunctions.add(DensityFunctions.add(DensityFunctions.constant(0.27D), densityfunction5).clamp(-1.0D, 1.0D), DensityFunctions.add(DensityFunctions.constant(1.5D), DensityFunctions.mul(DensityFunctions.constant(-0.64D), densityfunction)).clamp(0.0D, 0.5D));
        DensityFunction densityfunction7 = DensityFunctions.add(densityfunction4, densityfunction6);
        DensityFunction densityfunction8 = DensityFunctions.min(DensityFunctions.min(densityfunction7, getFunction(iregistry, NoiseRouterData.ENTRANCES)), DensityFunctions.add(densityfunction1, densityfunction2));
        DensityFunction densityfunction9 = getFunction(iregistry, NoiseRouterData.PILLARS);
        DensityFunction densityfunction10 = DensityFunctions.rangeChoice(densityfunction9, -1000000.0D, 0.03D, DensityFunctions.constant(-1000000.0D), densityfunction9);

        return DensityFunctions.max(densityfunction8, densityfunction10);
    }

    private static DensityFunction postProcess(DensityFunction densityfunction) {
        DensityFunction densityfunction1 = DensityFunctions.blendDensity(densityfunction);

        return DensityFunctions.mul(DensityFunctions.interpolated(densityfunction1), DensityFunctions.constant(0.64D)).squeeze();
    }

    protected static NoiseRouter overworld(IRegistry<DensityFunction> iregistry, boolean flag, boolean flag1) {
        DensityFunction densityfunction = DensityFunctions.noise(getNoise(Noises.AQUIFER_BARRIER), 0.5D);
        DensityFunction densityfunction1 = DensityFunctions.noise(getNoise(Noises.AQUIFER_FLUID_LEVEL_FLOODEDNESS), 0.67D);
        DensityFunction densityfunction2 = DensityFunctions.noise(getNoise(Noises.AQUIFER_FLUID_LEVEL_SPREAD), 0.7142857142857143D);
        DensityFunction densityfunction3 = DensityFunctions.noise(getNoise(Noises.AQUIFER_LAVA));
        DensityFunction densityfunction4 = getFunction(iregistry, NoiseRouterData.SHIFT_X);
        DensityFunction densityfunction5 = getFunction(iregistry, NoiseRouterData.SHIFT_Z);
        DensityFunction densityfunction6 = DensityFunctions.shiftedNoise2d(densityfunction4, densityfunction5, 0.25D, getNoise(flag ? Noises.TEMPERATURE_LARGE : Noises.TEMPERATURE));
        DensityFunction densityfunction7 = DensityFunctions.shiftedNoise2d(densityfunction4, densityfunction5, 0.25D, getNoise(flag ? Noises.VEGETATION_LARGE : Noises.VEGETATION));
        DensityFunction densityfunction8 = getFunction(iregistry, flag ? NoiseRouterData.FACTOR_LARGE : (flag1 ? NoiseRouterData.FACTOR_AMPLIFIED : NoiseRouterData.FACTOR));
        DensityFunction densityfunction9 = getFunction(iregistry, flag ? NoiseRouterData.DEPTH_LARGE : (flag1 ? NoiseRouterData.DEPTH_AMPLIFIED : NoiseRouterData.DEPTH));
        DensityFunction densityfunction10 = noiseGradientDensity(DensityFunctions.cache2d(densityfunction8), densityfunction9);
        DensityFunction densityfunction11 = getFunction(iregistry, flag ? NoiseRouterData.SLOPED_CHEESE_LARGE : (flag1 ? NoiseRouterData.SLOPED_CHEESE_AMPLIFIED : NoiseRouterData.SLOPED_CHEESE));
        DensityFunction densityfunction12 = DensityFunctions.min(densityfunction11, DensityFunctions.mul(DensityFunctions.constant(5.0D), getFunction(iregistry, NoiseRouterData.ENTRANCES)));
        DensityFunction densityfunction13 = DensityFunctions.rangeChoice(densityfunction11, -1000000.0D, 1.5625D, densityfunction12, underground(iregistry, densityfunction11));
        DensityFunction densityfunction14 = DensityFunctions.min(postProcess(slideOverworld(flag1, densityfunction13)), getFunction(iregistry, NoiseRouterData.NOODLE));
        DensityFunction densityfunction15 = getFunction(iregistry, NoiseRouterData.Y);
        int i = Stream.of(OreVeinifier.a.values()).mapToInt((oreveinifier_a) -> {
            return oreveinifier_a.minY;
        }).min().orElse(-DimensionManager.MIN_Y * 2);
        int j = Stream.of(OreVeinifier.a.values()).mapToInt((oreveinifier_a) -> {
            return oreveinifier_a.maxY;
        }).max().orElse(-DimensionManager.MIN_Y * 2);
        DensityFunction densityfunction16 = yLimitedInterpolatable(densityfunction15, DensityFunctions.noise(getNoise(Noises.ORE_VEININESS), 1.5D, 1.5D), i, j, 0);
        float f = 4.0F;
        DensityFunction densityfunction17 = yLimitedInterpolatable(densityfunction15, DensityFunctions.noise(getNoise(Noises.ORE_VEIN_A), 4.0D, 4.0D), i, j, 0).abs();
        DensityFunction densityfunction18 = yLimitedInterpolatable(densityfunction15, DensityFunctions.noise(getNoise(Noises.ORE_VEIN_B), 4.0D, 4.0D), i, j, 0).abs();
        DensityFunction densityfunction19 = DensityFunctions.add(DensityFunctions.constant(-0.07999999821186066D), DensityFunctions.max(densityfunction17, densityfunction18));
        DensityFunction densityfunction20 = DensityFunctions.noise(getNoise(Noises.ORE_GAP));

        return new NoiseRouter(densityfunction, densityfunction1, densityfunction2, densityfunction3, densityfunction6, densityfunction7, getFunction(iregistry, flag ? NoiseRouterData.CONTINENTS_LARGE : NoiseRouterData.CONTINENTS), getFunction(iregistry, flag ? NoiseRouterData.EROSION_LARGE : NoiseRouterData.EROSION), densityfunction9, getFunction(iregistry, NoiseRouterData.RIDGES), slideOverworld(flag1, DensityFunctions.add(densityfunction10, DensityFunctions.constant(-0.703125D)).clamp(-64.0D, 64.0D)), densityfunction14, densityfunction16, densityfunction19, densityfunction20);
    }

    private static NoiseRouter noNewCaves(IRegistry<DensityFunction> iregistry, DensityFunction densityfunction) {
        DensityFunction densityfunction1 = getFunction(iregistry, NoiseRouterData.SHIFT_X);
        DensityFunction densityfunction2 = getFunction(iregistry, NoiseRouterData.SHIFT_Z);
        DensityFunction densityfunction3 = DensityFunctions.shiftedNoise2d(densityfunction1, densityfunction2, 0.25D, getNoise(Noises.TEMPERATURE));
        DensityFunction densityfunction4 = DensityFunctions.shiftedNoise2d(densityfunction1, densityfunction2, 0.25D, getNoise(Noises.VEGETATION));
        DensityFunction densityfunction5 = postProcess(densityfunction);

        return new NoiseRouter(DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), densityfunction3, densityfunction4, DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), densityfunction5, DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero());
    }

    private static DensityFunction slideOverworld(boolean flag, DensityFunction densityfunction) {
        return slide(densityfunction, -64, 384, flag ? 16 : 80, flag ? 0 : 64, -0.078125D, 0, 24, flag ? 0.4D : 0.1171875D);
    }

    private static DensityFunction slideNetherLike(IRegistry<DensityFunction> iregistry, int i, int j) {
        return slide(getFunction(iregistry, NoiseRouterData.BASE_3D_NOISE_NETHER), i, j, 24, 0, 0.9375D, -8, 24, 2.5D);
    }

    private static DensityFunction slideEndLike(DensityFunction densityfunction, int i, int j) {
        return slide(densityfunction, i, j, 72, -184, -23.4375D, 4, 32, -0.234375D);
    }

    protected static NoiseRouter nether(IRegistry<DensityFunction> iregistry) {
        return noNewCaves(iregistry, slideNetherLike(iregistry, 0, 128));
    }

    protected static NoiseRouter caves(IRegistry<DensityFunction> iregistry) {
        return noNewCaves(iregistry, slideNetherLike(iregistry, -64, 192));
    }

    protected static NoiseRouter floatingIslands(IRegistry<DensityFunction> iregistry) {
        return noNewCaves(iregistry, slideEndLike(getFunction(iregistry, NoiseRouterData.BASE_3D_NOISE_END), 0, 256));
    }

    private static DensityFunction slideEnd(DensityFunction densityfunction) {
        return slideEndLike(densityfunction, 0, 128);
    }

    protected static NoiseRouter end(IRegistry<DensityFunction> iregistry) {
        DensityFunction densityfunction = DensityFunctions.cache2d(DensityFunctions.endIslands(0L));
        DensityFunction densityfunction1 = postProcess(slideEnd(getFunction(iregistry, NoiseRouterData.SLOPED_CHEESE_END)));

        return new NoiseRouter(DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), densityfunction, DensityFunctions.zero(), DensityFunctions.zero(), slideEnd(DensityFunctions.add(densityfunction, DensityFunctions.constant(-0.703125D))), densityfunction1, DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero());
    }

    protected static NoiseRouter none() {
        return new NoiseRouter(DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero());
    }

    private static DensityFunction splineWithBlending(DensityFunction densityfunction, DensityFunction densityfunction1) {
        DensityFunction densityfunction2 = DensityFunctions.lerp(DensityFunctions.blendAlpha(), densityfunction1, densityfunction);

        return DensityFunctions.flatCache(DensityFunctions.cache2d(densityfunction2));
    }

    private static DensityFunction noiseGradientDensity(DensityFunction densityfunction, DensityFunction densityfunction1) {
        DensityFunction densityfunction2 = DensityFunctions.mul(densityfunction1, densityfunction);

        return DensityFunctions.mul(DensityFunctions.constant(4.0D), densityfunction2.quarterNegative());
    }

    private static DensityFunction yLimitedInterpolatable(DensityFunction densityfunction, DensityFunction densityfunction1, int i, int j, int k) {
        return DensityFunctions.interpolated(DensityFunctions.rangeChoice(densityfunction, (double) i, (double) (j + 1), densityfunction1, DensityFunctions.constant((double) k)));
    }

    private static DensityFunction slide(DensityFunction densityfunction, int i, int j, int k, int l, double d0, int i1, int j1, double d1) {
        DensityFunction densityfunction1 = DensityFunctions.yClampedGradient(i + j - k, i + j - l, 1.0D, 0.0D);
        DensityFunction densityfunction2 = DensityFunctions.lerp(densityfunction1, d0, densityfunction);
        DensityFunction densityfunction3 = DensityFunctions.yClampedGradient(i + i1, i + j1, 0.0D, 1.0D);

        densityfunction2 = DensityFunctions.lerp(densityfunction3, d1, densityfunction2);
        return densityfunction2;
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
